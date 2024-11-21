package com.dear.littledoll

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.dear.littledoll.ad.AdDataUtils
import com.dear.littledoll.ad.up.UpDataMix
import com.dear.littledoll.adapter.ResultAdapter
import com.dear.littledoll.bean.CountryBean
import com.dear.littledoll.databinding.ActivitySpeedBinding
import com.dear.littledoll.utils.DataManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.random.Random

class SpeedActivity : AppCompatActivity() {

    val binding by lazy { ActivitySpeedBinding.inflate(layoutInflater) }
    private val resultList = mutableListOf<CountryBean>()
    var jobSpeedAd: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        showSpeedAd()
        binding.back.setOnClickListener { finish() }
        binding.download.text = intent.getStringExtra("download")
        binding.upload.text = intent.getStringExtra("upload")
        val list = DataManager.getGrouping().apply { removeAt(0) }.shuffled().take(4)
        list.forEach { resultList.add(it.shuffled().random(Random(System.currentTimeMillis()))) }
        if (resultList.size != 3) {
            val hosts = resultList.map { it.ldHost }.joinToString(",")
            resultList.addAll(
                DataManager.getOnlineVpnData(false).filter { hosts.contains(it.ldHost).not() }
                    .shuffled().take(3 - list.size)
            )
        }
        binding.rvLayout.adapter = ResultAdapter(resultList) {
            setResult(998, Intent().apply { putExtra("data", it) })
            finish()
        }
        binding.ip.text = DataManager.ip
        ping()
        UpDataMix.postPointData("p_test_view")
    }

    private fun ping() {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                val pro = Runtime.getRuntime().exec("ping -c 1 -w 1 google.com")
                val contentRead = BufferedReader(InputStreamReader(pro.inputStream))
                val cn = StringBuffer()
                while (true) {
                    val data = contentRead.readLine() ?: break
                    cn.append(data)
                }
                val ms = cn.toString().split("time=")[1].split("ms")[0].replace(" ", "")
                binding.ping.text = "${ms}ms"
            }
        }
    }

    private fun showSpeedAd() {
        AdDataUtils.log("showResultAd")
        jobSpeedAd?.cancel()
        jobSpeedAd = null
        if (AdDataUtils.getAdBlackData() || DataManager.black_admin == "2") {
            binding.adLayout.isVisible = false
            return
        }
        binding.adLayout.isVisible = true
        binding.imgOcAd.isVisible = true
        if (AdDataUtils.getEndNativeAdData()
                .canShowAd(AdDataUtils.result_type) == AdDataUtils.ad_wait
        ) {
            binding.adLayoutAdmob.isVisible = false
            AdDataUtils.getEndNativeAdData().loadAd(AdDataUtils.result_type)
        }
        jobSpeedAd = lifecycleScope.launch {
            delay(300)
            while (isActive) {
                if (AdDataUtils.getEndNativeAdData()
                        .canShowAd(AdDataUtils.result_type) == AdDataUtils.ad_show
                ) {
                    AdDataUtils.getEndNativeAdData()
                        .showAd(AdDataUtils.result_type, this@SpeedActivity) {
                        }
                    jobSpeedAd?.cancel()
                    jobSpeedAd = null
                    break
                }
                delay(500L)
            }
        }
    }

}