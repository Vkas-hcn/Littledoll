package com.dear.littledoll

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.dear.littledoll.adapter.ResultAdapter
import com.dear.littledoll.bean.CountryBean
import com.dear.littledoll.databinding.ActivityResultBinding
import com.dear.littledoll.utils.ConnectUtils
import com.dear.littledoll.utils.DataManager
import com.dear.littledoll.utils.SpeedUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.random.Random


class ResultActivity : AppCompatActivity() {
    private val binding by lazy { ActivityResultBinding.inflate(layoutInflater) }
    private val resultList = mutableListOf<CountryBean>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.back.setOnClickListener { finish() }
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
                SpeedUtils().loading(this, { a, b ->
                    CoroutineScope(Dispatchers.Main).launch {
                        withTimeoutOrNull(500){
                            while (lifecycle.currentState != Lifecycle.State.RESUMED) delay(50)
                        }
                        if (lifecycle.currentState == Lifecycle.State.RESUMED){
                            serviceResult.launch(Intent(this@ResultActivity, SpeedActivity::class.java).putExtra("download", a).putExtra("upload", b))
                        }
                    }
                }) {
                    Toast.makeText(this, "Speed measurement timeout", Toast.LENGTH_SHORT).show()
                }
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
            list.forEach { resultList.add(it.shuffled().random(Random(System.currentTimeMillis()))) }
            if (resultList.size != 4) {
                val hosts = resultList.map { it.ldHost }.joinToString(",")
                resultList.addAll(DataManager.getList().filter { hosts.contains(it.ldHost).not() }.shuffled().take(4 - list.size))
            }
            binding.rvLayout.adapter = ResultAdapter(resultList) {
                setResult(999, Intent().apply { putExtra("data", it) })
                finish()
            }
            binding.btn.setOnClickListener {
                setResult(999, Intent().putExtra("data", DataManager.selectItem))
                finish()
            }
        }
        binding.icon.setImageResource(data.getIcon())
        binding.name.text = data.getName()
        binding.tag.text = data.getTag()
        ping()
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

            }
        }
    }


    private val serviceResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == 998) {
            val data = it.data!!.getSerializableExtra("data") as CountryBean
            setResult(998, Intent().putExtra("data", data))
            finish()
        }
    }


}