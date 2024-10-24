package com.dear.littledoll

import android.Manifest
import android.animation.ValueAnimator
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.graphics.Color
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.text.format.Formatter
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.dear.littledoll.ad.AdDataUtils
import com.dear.littledoll.bean.CountryBean
import com.dear.littledoll.databinding.ActivityMainBinding
import com.dear.littledoll.imp.SpeedImp
import com.dear.littledoll.receiver.SpeedReceiver
import com.dear.littledoll.utils.ConnectUtils
import com.dear.littledoll.utils.DataManager
import com.dear.littledoll.utils.InspectUtils
import com.dear.littledoll.utils.SpeedUtils
import de.blinkt.openvpn.api.ExternalOpenVPNService
import de.blinkt.openvpn.api.IOpenVPNAPIService
import de.blinkt.openvpn.api.IOpenVPNStatusCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.text.SimpleDateFormat
import java.util.TimeZone


class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var oldSelect = CountryBean()
    private var isLoading = false
    private var isOptimize = false
    private var isData = false
    var nowClickState: String = "1"
    private var isClick = true
    private var jobConnect: Job? = null
    var iOpenVPNAPIService: IOpenVPNAPIService? = null
    private val imp by lazy {
        object : SpeedImp {
            override fun speedLong(download: Long, upload: Long) {
                binding.download.text = "${Formatter.formatFileSize(this@MainActivity, download)}/s"
                binding.upload.text = "${Formatter.formatFileSize(this@MainActivity, upload)}/s"
            }
        }
    }

    private val receiver by lazy { SpeedReceiver(imp) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
        click()
    }

    private fun init() {
        binding.timeTitle.text = "Blazing Speed, Worry-Free Privacy"
        setInfoData()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(
                receiver,
                IntentFilter().apply { addAction("com.speed.b") },
                Context.RECEIVER_EXPORTED
            )
        } else {
            registerReceiver(receiver, IntentFilter().apply { addAction("com.speed.b") })
        }
        bindService(Intent(this, ExternalOpenVPNService::class.java), mConnection, BIND_AUTO_CREATE)
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                clickBlock {
                    val intent = Intent(Intent.ACTION_MAIN)
                    intent.addCategory(Intent.CATEGORY_HOME)
                    startActivity(intent)
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }


    private fun click() {
        binding.serviceLayout.setOnClickListener {
            clickBlock { serviceResult.launch(Intent(this, SelectServerActivity::class.java)) }
        }
        binding.btn.setOnClickListener(clickUp)
        binding.qd.setOnClickListener(clickUp)
        binding.set.setOnClickListener {
            clickBlock { startActivity(Intent(this, SettingActivity::class.java)) }
        }

        binding.disConnect.setOnClickListener {
            oldSelect = DataManager.selectItem
            startUp()
        }
        binding.optimize.setOnClickListener {
            isOptimize = true
            iOpenVPNAPIService?.disconnect()
            Log.e("TAG", ": disconnect()-3")

        }
        binding.test.setOnClickListener {
            clickBlock {
                testVpnSpAd {
                    SpeedUtils().loading(this, { a, b ->
                        CoroutineScope(Dispatchers.IO).launch {
                            withTimeoutOrNull(500) {
                                while (lifecycle.currentState != Lifecycle.State.RESUMED) delay(50)
                            }
                            if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                                serviceResult.launch(
                                    Intent(
                                        this@MainActivity,
                                        SpeedActivity::class.java
                                    ).putExtra("download", a).putExtra("upload", b)
                                )
                            }
                        }
                    }) {
                        Toast.makeText(this, "Speed measurement timeout", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun tipsView(b: Boolean = false) {
        binding.btn.visibility = if (b.not()) View.VISIBLE else View.GONE
    }


    private val clickUp = View.OnClickListener {
        clickBlock { sendPostResult() }
    }


    private fun clickBlock(block: () -> Unit) {
        if (isClick) {
            block()
        } else {
            Toast.makeText(
                this,
                if (ConnectUtils.isVpnConnect()) "Disconnecting... Please wait" else "Connecting... Please wait",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }


    private fun prohibit(isBoolean: Boolean = false) {
        isClick = isBoolean
    }


    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            iOpenVPNAPIService = IOpenVPNAPIService.Stub.asInterface(service)
            runCatching {
                iOpenVPNAPIService?.registerStatusCallback(callback)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            iOpenVPNAPIService = null
        }
    }

    private val callback = object : IOpenVPNStatusCallback.Stub() {
        override fun newStatus(uuid: String?, state: String?, message: String?, level: String?) {
            // NOPROCESS 未连接  CONNECTED 已连接  RECONNECTING 尝试重新链接  EXITING 连接中主动掉用断开
            ConnectUtils.oState = state ?: ""
            AdDataUtils.log("newStatus-=${ConnectUtils.oState}")
            CoroutineScope(Dispatchers.Main).launch {
                when (ConnectUtils.oState) {
                    "NOPROCESS" -> {
                        defaultStatus()
                        binding.viewBackgroundView.setBackgroundColor(Color.WHITE)
                        if (isLoading) {
                            isLoading = false
                            AdDataUtils.getEndIntAdData().loadAd(AdDataUtils.end_type)
                            stopTime()
                        }
                        if (isOptimize) {
                            isOptimize = false
                            binding.btn.visibility = View.VISIBLE
                            binding.connectBtn.visibility = View.GONE
                            stopTime(false)
                        }

                    }

                    "..." -> {
                        ConnectUtils.oState = "CONNECTED"
                        tipsView(false)
                        defaultStatus()
                        val t =
                            if (DataManager.selectTime > 0L) DataManager.selectTime else System.currentTimeMillis()
                        startTime(t)
                        binding.viewBackgroundView.setBackgroundColor(Color.parseColor("#EDFFA1"))
                    }

                    "CONNECTED" -> {
                        showConnectAd()
                        AdDataUtils.getEndIntAdData().loadAd(AdDataUtils.end_type)
                    }

                    "RECONNECTING" -> {
                        Log.e("TAG", ": disconnect()-2")
                        iOpenVPNAPIService?.disconnect()
                        timeJob?.cancel()
                        defaultStatus()
                        setInfoData()
                        prohibit(true)
                        binding.time.text = "00:00:00"
                        binding.time.visibility = View.GONE
                        binding.timeTitle.text = "Blazing Speed, Worry-Free Privacy"
                        binding.qd.visibility = View.VISIBLE
                        binding.layoutConnectInfo.visibility = View.GONE
                    }
                }
            }

        }
    }


    private val serviceResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == 999) {
                val data = it.data!!.getSerializableExtra("data") as CountryBean
                oldSelect = DataManager.selectItem
                DataManager.selectItem = data
                if (ConnectUtils.isVpnConnect().not()) {
                    setInfoData()
                }
                sendPostResult()
            } else if (it.resultCode == 998) {
                val data = it.data!!.getSerializableExtra("data") as CountryBean
                oldSelect = DataManager.selectItem
                DataManager.selectItem = data
                setInfoData()
                isOptimize = true
                isData = true
                Log.e("TAG", ": disconnect()-1", )
                iOpenVPNAPIService?.disconnect()
            }
        }

    private val vpnResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) startUp()
        }


    private fun setInfoData(data: CountryBean = DataManager.selectItem) {
        binding.icon.setImageResource(data.getIcon())
        if (data.ldHost.isEmpty()) {
            binding.name.text = "Smart Server"
            binding.tag.text = "01"
        } else {
            binding.name.text = data.getName()
            binding.tag.text = data.getTag()
        }
    }


    private val loadingIng by lazy {
        ValueAnimator.ofInt(0, 100).apply {
            duration = 1000
            repeatCount = -1
            addUpdateListener {
                val progress = it.animatedValue as Int
                if (progress <= 30) {
                    binding.btn.text = if (isLoading) "Disconnecting." else "Connecting."
                } else if (progress <= 60) {
                    binding.btn.text = if (isLoading) "Disconnecting.." else "Connecting.."
                } else if (progress <= 100) {
                    binding.btn.text = if (isLoading) "Disconnecting..." else "Connecting..."
                }
            }
        }
    }

    private fun startUp() {
        nowClickState = if (ConnectUtils.isVpnConnect()) {
            "2"
        } else {
            "0"
        }
        if (ConnectUtils.isVpnConnect().not()) {
            loadingIng.start()
            prohibit(false)
            iOpenVPNAPIService?.let { ConnectUtils.open(it) }
        } else {
            disconnect()
        }
    }

    private suspend fun defaultStatus() {
        loadingIng.pause()
        delay(100)
        prohibit(true)
        binding.btn.text = if (ConnectUtils.isVpnConnect()) "Disconnected" else "Connect Now"
    }

    private var timeJob: Job? = null

    private fun startTime(t: Long = System.currentTimeMillis()) {
        timeJob = CoroutineScope(Dispatchers.Main).launch {
            DataManager.selectTime = t
            binding.time.visibility = View.VISIBLE
            binding.timeTitle.text = "Connection Duration:"
            binding.qd.visibility = View.GONE
            binding.layoutConnectInfo.visibility = View.VISIBLE
            binding.btn.visibility = View.GONE
            binding.connectBtn.visibility = View.VISIBLE
            while (true) {
                delay(1000)
                val t = SimpleDateFormat("HH:mm:ss").apply {
                    timeZone = TimeZone.getTimeZone("+8")
                }.format(System.currentTimeMillis() - DataManager.selectTime)
                binding.time.text = t
            }
        }
    }

    private suspend fun stopTime(isResult: Boolean = true) {
        timeJob?.cancel()
        defaultStatus()
        setInfoData()
        prohibit(true)
        binding.time.text = "00:00:00"
        binding.time.visibility = View.GONE
        binding.timeTitle.text = "Blazing Speed, Worry-Free Privacy"
        binding.qd.visibility = View.VISIBLE
        binding.layoutConnectInfo.visibility = View.GONE
        if (isResult) {
            withTimeoutOrNull(500) {
                while (lifecycle.currentState != Lifecycle.State.RESUMED) delay(50)
            }
            if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                serviceResult.launch(
                    Intent(this@MainActivity, ResultActivity::class.java).putExtra(
                        "data",
                        oldSelect
                    )
                )
            }
        } else {
            if (isData) {
                isData = false
            } else {
                DataManager.selectItem = CountryBean()
            }
            setInfoData()
            sendPostResult()
        }
    }

    private fun disconnect() {
        lifecycleScope.launch {
            isLoading = true
            binding.btn.visibility = View.VISIBLE
            binding.connectBtn.visibility = View.GONE
            loadingIng.start()
            prohibit()
            delay(1000)
            showConnectAd()
        }

    }


    private fun sendPostResult() {
        startVpn()
    }

    private fun startVpn() {
        //TODO
        if (InspectUtils.inspectConnect(this)) return
        if (ConnectUtils.isVpnPermission()) {
            startUp()
        } else {
            vpnResult.launch(VpnService.prepare(this))
        }
    }

    private val singlePermissionRequestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted.not()) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                ) {
                    Toast.makeText(
                        this,
                        "Please go to the application settings page to enable notification permissions",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                startVpn()
            }
        }

    fun showConnectAd() {
        jobConnect?.cancel()
        jobConnect = null
        jobConnect = lifecycleScope.launch {
            if (AdDataUtils.adManagerConnect.canShowAd(AdDataUtils.cont_type) == AdDataUtils.ad_jump_over) {
                showFinishAd()
                return@launch
            }
            AdDataUtils.adManagerConnect.loadAd(AdDataUtils.cont_type)
            val startTime = System.currentTimeMillis()
            var elapsedTime: Long
            try {
                while (isActive) {
                    elapsedTime = System.currentTimeMillis() - startTime
                    if (elapsedTime >= (10 * 1000)) {
                        Log.e("TAG", "连接超时")
                        showFinishAd()
                        break
                    }
                    if (AdDataUtils.adManagerConnect.canShowAd(AdDataUtils.cont_type) == AdDataUtils.ad_show) {
                        AdDataUtils.adManagerConnect.showAd(
                            AdDataUtils.cont_type,
                            this@MainActivity
                        ) {
                            showFinishAd()
                        }
                        break
                    }
                    delay(500L)
                }
            } catch (e: Exception) {
                showFinishAd()
            }
        }
    }

    private fun showFinishAd() {
        lifecycleScope.launch {
            AdDataUtils.log("showFinishAd-nowClickState=${nowClickState}")
            if (nowClickState == "0") {
                connectFinish()
                AdDataUtils.adManagerConnect.loadAd(AdDataUtils.cont_type)
            }
            if (nowClickState == "2") {
                disConnectFinish()
            }
        }

    }

    private suspend fun connectFinish() {
        defaultStatus()
        startTime()
        binding.viewBackgroundView.setBackgroundColor(Color.parseColor("#EDFFA1"))
        withTimeoutOrNull(500) {
            while (lifecycle.currentState != Lifecycle.State.RESUMED) delay(100)
        }
        if (lifecycle.currentState == Lifecycle.State.RESUMED) {
            serviceResult.launch(
                Intent(
                    this@MainActivity,
                    ResultActivity::class.java
                ).putExtra("data", oldSelect)
            )
        }
    }

    private fun disConnectFinish() {
        iOpenVPNAPIService?.disconnect()
        Log.e("TAG", ": disconnect()-4")

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
                        nextFun()
                        binding.conLoadAd.isVisible = false
                        break
                    }

                    if (elapsedTime >= 1000L && AdDataUtils.getInterListAdData()
                            .canShowAd(AdDataUtils.list_type) == AdDataUtils.ad_show
                    ) {
                        AdDataUtils.getInterListAdData()
                            .showAd(AdDataUtils.list_type, this@MainActivity) {
                                nextFun()
                                binding.conLoadAd.isVisible = false
                            }
                        break
                    }
                    delay(500L)
                }
            } catch (e: Exception) {
                nextFun()
                binding.conLoadAd.isVisible = false
            }
        }
    }

}