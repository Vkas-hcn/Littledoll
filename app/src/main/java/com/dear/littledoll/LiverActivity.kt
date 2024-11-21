package com.dear.littledoll

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.dear.littledoll.ad.AdDataUtils
import com.dear.littledoll.ad.AdDataUtils.log
import com.dear.littledoll.ad.AdDataUtils.onlien_ad_key
import com.dear.littledoll.ad.AdDataUtils.onlien_list_key
import com.dear.littledoll.ad.AdDataUtils.onlien_pz_key
import com.dear.littledoll.ad.AdDataUtils.onlien_smart_key
import com.dear.littledoll.ad.up.AdminUtils
import com.dear.littledoll.ad.up.UpDataMix
import com.dear.littledoll.databinding.ActivityLiverBinding
import com.dear.littledoll.utils.ConnectUtils
import com.dear.littledoll.utils.DataManager
import com.dear.littledoll.utils.InspectUtils
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class LiverActivity : AppCompatActivity() {

    private val binding by lazy { ActivityLiverBinding.inflate(layoutInflater) }
    private var jobOpenTdo: Job? = null
    private var jobJumpJob: Job? = null
    private var fileBaseJob: Job? = null
    var isCa = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        refInformation(this)
        cmpUtils()
        getFileBaseData {
            waitForCmpInALoop()
        }
        CoroutineScope(Dispatchers.IO).launch {
            UpDataMix.postSessionData(this@LiverActivity)
        }
        clickFUn()
    }

    private fun clickFUn() {
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        })
    }

    private fun getFileBaseData(loadAdFun: () -> Unit) {
        fileBaseJob = lifecycleScope.launch {
            val auth = Firebase.remoteConfig
            auth.fetchAndActivate().addOnSuccessListener {
                DataManager.online_ad_value = auth.getString(onlien_ad_key)
                DataManager.online_pz_value = auth.getString(onlien_pz_key)
                DataManager.online_smart_value = auth.getString(onlien_smart_key)
                DataManager.online_list_value = auth.getString(onlien_list_key)
                log("onlien_list_key=${auth.getString(onlien_list_key)}")
                isCa = true
                initFaceBook()
            }
            try {
                withTimeout(4000L) {
                    while (true) {
                        if (!isActive) {
                            break
                        }
                        if (isCa) {
                            loadAdFun()
                            cancel()
                            fileBaseJob = null
                        }
                        log("Detect firebase data...")
                        delay(500)
                    }
                }
            } catch (e: TimeoutCancellationException) {
                log("Detect firebase data timeouts")
                cancel()
                fileBaseJob = null
                loadAdFun()
                isCa = true

            }
        }
    }
    //循环等待cmp
    private fun waitForCmpInALoop() {
        CoroutineScope(Dispatchers.Main).launch {
            while (isActive){
                if (DataManager.cmp_state == "OK") {
                    log("循环等待cmp=${ConnectUtils.isVpnConnect()}")
                    AdDataUtils.getStartOpenAdData().loadAd(AdDataUtils.open_type)
                    AdDataUtils.getHomeNativeAdData().loadAd(AdDataUtils.home_type)
                    showOpenAd()
                    cancel()
                    break
                }
                delay(500)
            }
        }
    }

    private fun initFaceBook() {
        val bean = AdDataUtils.getLjData()
        // TODO fb id 1
        if (bean.nayan2.isBlank()) {
            return
        }
        log("initFaceBook: ${bean.nayan2}")
        FacebookSdk.setApplicationId(bean.nayan2)
        FacebookSdk.sdkInitialize(LDApplication.app)
        AppEventsLogger.activateApp(LDApplication.app)
    }

    private fun showOpenAd() {
        GlobalScope.launch {
            while (true) {
                delay(12000)
                onCountdownFinished()
                return@launch
            }
        }
    }

    override fun onResume() {
        super.onResume()
        jobOpenTdo?.cancel()
        jobOpenTdo = lifecycleScope.launch {
            while (isActive) {
                val adData = AdDataUtils.getStartOpenAdData()
                if (isCa) {
                    when (adData.canShowAd(AdDataUtils.open_type)) {
                        AdDataUtils.ad_jump_over -> {
                            onCountdownFinished()
                            return@launch
                        }

                        AdDataUtils.ad_show -> {
                            delay(1000)
                            adData.showAd(AdDataUtils.open_type, this@LiverActivity) {
                                onCountdownFinished()
                            }
                            return@launch
                        }
                    }
                }
                delay(500L)
            }
        }
    }

    private fun onCountdownFinished() {
        jobOpenTdo?.cancel()
        jobOpenTdo = null
        jobJumpJob?.cancel()
        jobJumpJob = lifecycleScope.launch {
            delay(300)
            if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                startActivity(Intent(this@LiverActivity, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun refInformation(context: Context) {
        runCatching {
            val referrerClient = InstallReferrerClient.newBuilder(context).build()
            referrerClient.startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(p0: Int) {
                    when (p0) {
                        InstallReferrerClient.InstallReferrerResponse.OK -> {
                            DataManager.ref_value =
                                referrerClient.installReferrer.installReferrer ?: ""
                            Log.e(
                                "TAG",
                                "onInstallReferrerSetupFinished: ${referrerClient.installReferrer}",
                            )
                            val timeElapsed =
                                ((System.currentTimeMillis() - LDApplication.startAppTime) / 1000).toInt()
                            UpDataMix.postPointData("u_rf","time",timeElapsed)
                            CoroutineScope(Dispatchers.IO).launch {
                                AdminUtils.getAdminData()
                            }
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

    private fun cmpUtils() {
        if (DataManager.cmp_state == "OK") {
            return
        }
        val debugSettings =
            ConsentDebugSettings.Builder(this)
                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                .addTestDeviceHashedId("76A730E9AE68BD60E99DF7B83D65C4B4")
                .build()
        val params = ConsentRequestParameters
            .Builder()
            .setConsentDebugSettings(debugSettings)
            .build()
        val consentInformation: ConsentInformation =
            UserMessagingPlatform.getConsentInformation(this)
        consentInformation.requestConsentInfoUpdate(
            this,
            params, {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(this) {
                    if (consentInformation.canRequestAds()) {
                        log("cmp1111")
                        DataManager.cmp_state = "OK"
                    }
                }
            },
            {
                log("cpm222")
                DataManager.cmp_state = "OK"
            }
        )
    }
}