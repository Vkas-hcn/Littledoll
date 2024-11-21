package com.dear.littledoll

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.dear.littledoll.ad.AdDataUtils
import com.dear.littledoll.ad.up.UpDataMix
import com.dear.littledoll.adapter.ServiceAdapter
import com.dear.littledoll.databinding.ActivitySelectServerBinding
import com.dear.littledoll.utils.ConnectUtils
import com.dear.littledoll.utils.DataManager
import com.facebook.ads.Ad
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.TimeZone

class SelectServerActivity : AppCompatActivity() {
    private var timeJob: Job? = null
    val binding by lazy { ActivitySelectServerBinding.inflate(layoutInflater) }
    var jobListAd: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        showListAd()
        binding.back.setOnClickListener { returnToHomePage() }
        onBackPressedDispatcher.addCallback(this) {
            returnToHomePage()
        }
        binding.rvLayout.adapter = ServiceAdapter {
            if (ConnectUtils.isVpnConnect()) {
                if (DataManager.selectItem.ldHost == it.ldHost) return@ServiceAdapter
                AlertDialog.Builder(this).create().apply {
                    setCancelable(false)
                    setOnKeyListener { dialog, keyCode, event -> true }
                    setMessage("Are you sure you want to switch servers? Switching will require disconnecting the current connection")
                    setButton(AlertDialog.BUTTON_NEUTRAL, "No") { d, _ -> dismiss() }
                    setButton(AlertDialog.BUTTON_NEGATIVE, "Yes") { d, _ ->
                        dismiss()
                        setResult(999, Intent().apply { putExtra("data", it) })
                        finish()
                        UpDataMix.postPointData("p_switch_server")
                    }
                    show()
                }
            } else {
                setResult(999, Intent().apply { putExtra("data", it) })
                finish()
            }
        }
        if (DataManager.selectItem.ldHost.isNotEmpty()) {
            binding.icon.setImageResource(DataManager.selectItem.getIcon())
            val name =
                DataManager.selectItem.getFistName() + "-" + DataManager.selectItem.getLastName()
            binding.name.text = name
            binding.tag.text = DataManager.selectItem.getTag()
        }
        if (ConnectUtils.isVpnConnect()) startTime()
        AdDataUtils.getInterListAdData().loadAd(AdDataUtils.list_type)
        val state = if (ConnectUtils.isVpnConnect()) {
            "connect"
        } else {
            "disconnect"
        }
        UpDataMix.postPointData("p_list_view", "state", state)
    }

    private fun startTime() {
        timeJob = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                delay(1000)
                val t = SimpleDateFormat("HH:mm:ss").apply {
                    timeZone = TimeZone.getTimeZone("+8")
                }.format(System.currentTimeMillis() - DataManager.selectTime)
                binding.time.text = t
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timeJob?.cancel()
    }

    private fun returnToHomePage() {
        UpDataMix.postPointData("p_list_back")
        if (AdDataUtils.getInterListAdData()
                .canShowAd(AdDataUtils.list_type) == AdDataUtils.ad_jump_over
        ) {
            finish()
            return
        }
        binding.conLoadAd.isVisible = true
        AdDataUtils.getInterListAdData().loadAd(AdDataUtils.list_type)
        lifecycleScope.launch {
            val startTime = System.currentTimeMillis()
            var elapsedTime: Long
            try {
                while (isActive) {
                    elapsedTime = System.currentTimeMillis() - startTime
                    if (elapsedTime >= 5000L) {
                        Log.e("TAG", "连接超时")
                        finish()
                        binding.conLoadAd.isVisible = false
                        break
                    }

                    if (AdDataUtils.getInterListAdData()
                            .canShowAd(AdDataUtils.list_type) == AdDataUtils.ad_show
                    ) {
                        AdDataUtils.getInterListAdData()
                            .showAd(AdDataUtils.list_type, this@SelectServerActivity) {
                                finish()
                                binding.conLoadAd.isVisible = false
                            }
                        break
                    }
                    delay(500L)
                }
            } catch (e: Exception) {
                finish()
                binding.conLoadAd.isVisible = false
            }
        }
    }

    private fun showListAd() {
        AdDataUtils.log("showResultAd")
        jobListAd?.cancel()
        jobListAd = null
        binding.adLayout.isVisible = true
        binding.imgOcAd.isVisible = true
        if (AdDataUtils.getEndNativeAdData()
                .canShowAd(AdDataUtils.result_type) == AdDataUtils.ad_wait
        ) {
            binding.adLayoutAdmob.isVisible = false
            AdDataUtils.getEndNativeAdData().loadAd(AdDataUtils.result_type)
        }
        jobListAd = lifecycleScope.launch {
            delay(300)
            while (isActive) {
                if (AdDataUtils.getEndNativeAdData()
                        .canShowAd(AdDataUtils.result_type) == AdDataUtils.ad_show
                ) {
                    AdDataUtils.getEndNativeAdData()
                        .showAd(AdDataUtils.result_type, this@SelectServerActivity) {
                        }
                    jobListAd?.cancel()
                    jobListAd = null
                    break
                }
                delay(500L)
            }
        }
    }

}