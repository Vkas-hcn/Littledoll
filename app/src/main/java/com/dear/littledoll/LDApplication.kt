package com.dear.littledoll

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.dear.littledoll.ad.AdDataUtils
import com.dear.littledoll.ad.up.UpDataMix
import com.dear.littledoll.utils.ConnectUtils
import com.dear.littledoll.utils.DataManager
import com.dear.littledoll.utils.InspectUtils
import com.google.android.gms.ads.AdActivity
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import de.blinkt.openvpn.api.ExternalOpenVPNService
import de.blinkt.openvpn.api.IOpenVPNAPIService
import de.blinkt.openvpn.api.IOpenVPNStatusCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.log


class LDApplication : Application(), Application.ActivityLifecycleCallbacks, LifecycleObserver {
    var adActivity: Activity? = null
    var lastBackgroundTime: Long = 0

    companion object {
        lateinit var app: Application
        var isInBackground = false
        var nowAN: String? = null
    }

    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this)
        FirebaseApp.initializeApp(this)
        registerActivityLifecycleCallbacks(this)
        this.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        Log.e("TAG", "onCreate: APP-${isMain()}")
        if (isMain()) {
            app = this
            getGid(this)
            InspectUtils.inspectCountry()
            if (DataManager.uid_value.isBlank()) {
                DataManager.uid_value = UUID.randomUUID().toString()
            }
            refInformation(this)
            initAdJust(this)
        }
    }

    private fun isMain(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val processName =
            activityManager.runningAppProcesses.find { it.pid == android.os.Process.myPid() }?.processName
                ?: ""
        return processName == packageName
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
        if (activity is AdActivity) {
            adActivity = activity
        } else {
            nowAN = activity.javaClass.simpleName
        }
    }

    override fun onActivityResumed(activity: Activity) {
        Adjust.onResume()
        if (isInBackground) {
            isInBackground = false
            val currentTime = System.currentTimeMillis()
            val backgroundDuration = currentTime - lastBackgroundTime
            if (backgroundDuration > 3000) {
                restartApp(activity)
            }
        }
    }

    override fun onActivityPaused(activity: Activity) {
        Adjust.onPause()
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onEnterForeground() {
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onEnterBackground() {
        lastBackgroundTime = System.currentTimeMillis()
        isInBackground = true
    }


    private fun restartApp(activity: Activity) {
        if (adActivity != null) {
            adActivity?.finish()
        }
        val intent = Intent(activity, LiverActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        activity.finish()
    }

    private fun refInformation(context: Context) {
        if (DataManager.install_value == "OK") {
            return
        }
        runCatching {
            val referrerClient = InstallReferrerClient.newBuilder(context).build()
            referrerClient.startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(p0: Int) {
                    when (p0) {
                        InstallReferrerClient.InstallReferrerResponse.OK -> {
                            referrerClient.installReferrer?.run {
                                UpDataMix.postInstallData(
                                    context,
                                    referrerClient.installReferrer
                                )
                            }
                        }
                    }
                    referrerClient.endConnection()
                }

                override fun onInstallReferrerServiceDisconnected() {
                }
            })
        }.onFailure { e ->
        }
    }

    private fun initAdJust(application: Application) {
        Adjust.addSessionCallbackParameter(
            "customer_user_id",
            DataManager.uid_value
        )
        val appToken = if (BuildConfig.DEBUG) {
            "ih2pm2dr3k74"
        } else {
            "6jppx2zv5slc"
        }
        val environment: String = if (BuildConfig.DEBUG) {
            AdjustConfig.ENVIRONMENT_SANDBOX
        } else {
            AdjustConfig.ENVIRONMENT_PRODUCTION
        }
        val config = AdjustConfig(application, appToken, environment)
        config.needsCost = true
        config.setOnAttributionChangedListener { attribution ->
            Log.e("TAG", "adjust=${attribution}")
            val data = DataManager.adjust_value == "adjust"
            if (!data && attribution.network.isNotEmpty() && attribution.network.contains(
                    "organic",
                    true
                ).not()
            ) {
                DataManager.adjust_value = "adjust"
            }
        }
        Adjust.onCreate(config)
    }

    private fun getGid(context: Context) {
        if (DataManager.gid_value.isNotBlank()) {
            return
        }
        DataManager.gid_value =
            (runCatching { AdvertisingIdClient.getAdvertisingIdInfo(context).id }.getOrNull() ?: "")
        AdDataUtils.log("gid = ${ DataManager.gid_value}")
    }
}