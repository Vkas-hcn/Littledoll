package com.dear.littledoll

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.dear.littledoll.ad.AdDataUtils
import com.dear.littledoll.ad.up.UpDataMix
import com.dear.littledoll.adapter.ResultAdapter
import com.dear.littledoll.bean.CountryBean
import com.dear.littledoll.databinding.ActivityResultBinding
import com.dear.littledoll.utils.ConnectUtils
import com.dear.littledoll.utils.DataManager
import com.dear.littledoll.utils.SpeedUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.log
import kotlin.random.Random


class ResultActivity : AppCompatActivity() {
    val binding by lazy { ActivityResultBinding.inflate(layoutInflater) }
    private val resultList = mutableListOf<CountryBean>()
    var jobResultLittle: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.back.setOnClickListener { returnToHomePage() }
        onBackPressedDispatcher.addCallback(this) {
            returnToHomePage()
        }
        showResultAd()
        val data: CountryBean
        if (ConnectUtils.isVpnConnect()) {
            binding.btn.setBackgroundResource(R.drawable.test_btn_drawable)
            binding.bg.setBackgroundColor(Color.parseColor("#EDFFA1"))
            data = DataManager.selectItem
            binding.btn.text = "Test Speed Now"
            binding.resultIcon.setImageResource(R.mipmap.connect_success)
            binding.content.text = "Connection Successful"
            binding.resultContent.text = "You’re Quickly Connected and Protected"
            binding.btn.setOnClickListener {
                AdDataUtils.getEndNativeAdData().loadAd(AdDataUtils.result_type)
                testVpnSpAd {
                    SpeedUtils().loading(this, { a, b ->
                        CoroutineScope(Dispatchers.Main).launch {
                            withTimeoutOrNull(500) {
                                while (lifecycle.currentState != Lifecycle.State.RESUMED) delay(50)
                            }
                            if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                                serviceResult.launch(
                                    Intent(
                                        this@ResultActivity,
                                        SpeedActivity::class.java
                                    ).putExtra("download", a).putExtra("upload", b)
                                )
                            }
                        }
                    }) {
                        Toast.makeText(this, "Speed measurement timeout", Toast.LENGTH_SHORT).show()
                    }
                }
                UpDataMix.postPointData("p_result_test")
            }
        } else {
            binding.btn.setBackgroundResource(R.drawable.re_btn_drawable)
            binding.bg.setBackgroundColor(Color.WHITE)
            data = intent.getSerializableExtra("data") as CountryBean
            binding.btn.text = "Reconnect"
            binding.resultIcon.setImageResource(R.mipmap.dis_connect)
            binding.content.text = "Disconnection Successful"
            binding.resultContent.text = "Secure Connection Ended"
            binding.resultList.visibility = View.VISIBLE
            val list = DataManager.getGrouping().apply { removeAt(0) }.shuffled().take(4)
            list.forEach {
                resultList.add(
                    it.shuffled().random(Random(System.currentTimeMillis()))
                )
            }
            if (resultList.size != 4) {
                val hosts = resultList.map { it.ldHost }.joinToString(",")
                resultList.addAll(
                    DataManager.getOnlineVpnData(false).filter { hosts.contains(it.ldHost).not() }
                        .shuffled().take(4 - list.size)
                )
            }
            binding.rvLayout.adapter = ResultAdapter(resultList) {
                setResult(999, Intent().apply { putExtra("data", it) })
                finish()
                UpDataMix.postPointData("p_result_use")

            }
            binding.btn.setOnClickListener {
                setResult(999, Intent().putExtra("data", DataManager.selectItem))
                finish()
                UpDataMix.postPointData("p_result_reconnect")
            }
        }
        binding.icon.setImageResource(data.getIcon())
        binding.name.text = data.getName()
        binding.tag.text = data.getTag()
        ping()
        if (ConnectUtils.isVpnConnect()) {
            UpDataMix.postPointData("p_result_view", "type", "connect", "IP", DataManager.ip)
        } else {
            UpDataMix.postPointData("p_result_view", "type", "disconnect", "IP", "1")
        }
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
                withContext(Dispatchers.Main) {
                    binding.ping.text = "${ms}ms"
                    binding.xhBg.visibility = View.VISIBLE
                    val s = ms.toLongOrNull() ?: 9999L
                    Log.e("TAG", "ping: $s", )
                    UpDataMix.postPointData("c_ping", "ping", s, "pp", DataManager.ip)
                    if (s <= 400) {
                        binding.view1.setBackgroundResource(R.drawable.xh_drawable)
                        binding.view2.setBackgroundResource(R.drawable.xh_drawable)
                        binding.view3.setBackgroundResource(R.drawable.xh_drawable)
                    } else if (s <= 800) {
                        binding.view1.setBackgroundResource(R.drawable.xh_drawable)
                        binding.view2.setBackgroundResource(R.drawable.xh_drawable)
                        binding.view3.setBackgroundResource(R.drawable.xh_h_drawable)
                    } else {
                        binding.view1.setBackgroundResource(R.drawable.xh_red_drawable)
                        binding.view2.setBackgroundResource(R.drawable.xh_h_drawable)
                        binding.view3.setBackgroundResource(R.drawable.xh_h_drawable)
                    }
                }
            }.onFailure {
                UpDataMix.postPointData("c_ping", "ping", "timeout", "pp", DataManager.ip)
            }
        }
    }


    private val serviceResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == 998) {
                val data = it.data!!.getSerializableExtra("data") as CountryBean
                setResult(998, Intent().putExtra("data", data))
                finish()
            }
        }

    private fun returnToHomePage() {
        val type = if (ConnectUtils.isVpnConnect()) {
            "connect"
        } else {
            "disconnect"
        }
        UpDataMix.postPointData("p_result_back", "type", type)
        if (AdDataUtils.getEndIntAdData()
                .canShowAd(AdDataUtils.end_type) == AdDataUtils.ad_jump_over
        ) {
            finish()
            return
        }
        binding.conLoadAd.isVisible = true
        AdDataUtils.getEndIntAdData().loadAd(AdDataUtils.end_type)
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

                    if (AdDataUtils.getEndIntAdData()
                            .canShowAd(AdDataUtils.end_type) == AdDataUtils.ad_show
                    ) {
                        AdDataUtils.getEndIntAdData()
                            .showAd(AdDataUtils.end_type, this@ResultActivity) {
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

    private fun testVpnSpAd(nextFun: () -> Unit) {
        if (AdDataUtils.getInterListAdData()
                .canShowAd(AdDataUtils.list_type) == AdDataUtils.ad_jump_over
        ) {
            nextFun()
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
                        binding.conLoadAd.isVisible = false
                        nextFun()
                        break
                    }

                    if (elapsedTime >= 1000L && AdDataUtils.getInterListAdData()
                            .canShowAd(AdDataUtils.list_type) == AdDataUtils.ad_show
                    ) {
                        AdDataUtils.getInterListAdData()
                            .showAd(AdDataUtils.list_type, this@ResultActivity) {
                                binding.conLoadAd.isVisible = false
                                nextFun()
                            }
                        break
                    }
                    delay(500L)
                }
            } catch (e: Exception) {
                binding.conLoadAd.isVisible = false
                nextFun()
            }
        }
    }

    private fun showResultAd() {
        AdDataUtils.log("showResultAd")
        jobResultLittle?.cancel()
        jobResultLittle = null
        binding.adLayout.isVisible = true
        binding.imgOcAd.isVisible = true
        if (AdDataUtils.getEndNativeAdData()
                .canShowAd(AdDataUtils.result_type) == AdDataUtils.ad_wait
        ) {
            binding.adLayoutAdmob.isVisible = false
            AdDataUtils.getEndNativeAdData().loadAd(AdDataUtils.result_type)
        }
        jobResultLittle = lifecycleScope.launch {
            delay(300)
            while (isActive) {
                if (AdDataUtils.getEndNativeAdData()
                        .canShowAd(AdDataUtils.result_type) == AdDataUtils.ad_show
                ) {
                    AdDataUtils.getEndNativeAdData()
                        .showAd(AdDataUtils.result_type, this@ResultActivity) {
                        }
                    jobResultLittle?.cancel()
                    jobResultLittle = null
                    break
                }
                delay(500L)
            }
        }
    }

}