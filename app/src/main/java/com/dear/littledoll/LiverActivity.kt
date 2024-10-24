package com.dear.littledoll

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.dear.littledoll.ad.AdDataUtils
import com.dear.littledoll.ad.AdDataUtils.log
import com.dear.littledoll.ad.AdDataUtils.onlien_ad_key
import com.dear.littledoll.ad.AdDataUtils.onlien_list_key
import com.dear.littledoll.ad.AdDataUtils.onlien_pz_key
import com.dear.littledoll.ad.AdDataUtils.onlien_smart_key
import com.dear.littledoll.databinding.ActivityLiverBinding
import com.dear.littledoll.utils.DataManager
import com.dear.littledoll.utils.InspectUtils
import com.facebook.FacebookSdk
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        getFileBaseData {
            AdDataUtils.getStartOpenAdData().loadAd(AdDataUtils.open_type)
            showOpenAd()
        }
        CoroutineScope(Dispatchers.IO).launch {
            InspectUtils.obtainTheDataOfBlacklistedUsers(this@LiverActivity)
        }

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        })
    }

    var isCa = false

    private fun getFileBaseData(loadAdFun: () -> Unit) {
        fileBaseJob = lifecycleScope.launch {
            val auth = Firebase.remoteConfig
            auth.fetchAndActivate().addOnSuccessListener {
                DataManager.online_ad_value = auth.getString(onlien_ad_key)
                DataManager.online_pz_value = auth.getString(onlien_pz_key)
                DataManager.online_smart_value = auth.getString(onlien_smart_key)
                DataManager.online_list_value = auth.getString(onlien_list_key)
                log("nayan=${auth.getString(onlien_pz_key)}")
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


    private fun initFaceBook() {
        val bean = AdDataUtils.getLjData()
        // TODO fb id 1
        if (bean.nayan2.isBlank()) {
            return
        }
        log("initFaceBook: ${bean.nayan2}")
        FacebookSdk.setApplicationId(bean.nayan2)
        FacebookSdk.sdkInitialize(LDApplication.app)
//        AppEventsLogger.activateApp(AAApp.thisApplication)
    }

    private fun showOpenAd() {
        GlobalScope.launch {
            while (true) {
                delay(10000)
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

}