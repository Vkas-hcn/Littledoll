package com.dear.littledoll.ad

import android.util.Base64
import android.util.Log
import com.dear.littledoll.BuildConfig
import com.dear.littledoll.LDApplication
import com.dear.littledoll.ad.up.UpDataMix
import com.dear.littledoll.bean.CountryBean
import com.dear.littledoll.utils.ConnectUtils
import com.dear.littledoll.utils.DataManager
import com.google.gson.Gson

object AdDataUtils {
    var adManagerOpen: AdManager = AdManager(LDApplication.app)
    var adManagerHomeNative: AdManager = AdManager(LDApplication.app)
    var adManagerEndNative: AdManager = AdManager(LDApplication.app)
    var adManagerConnect: AdManager = AdManager(LDApplication.app)
    var adManagerEndInt: AdManager = AdManager(LDApplication.app)
    var adManagerListInt: AdManager = AdManager(LDApplication.app)

    var adManagerOpenDis: AdManager = AdManager(LDApplication.app)
    var adManagerHomeDisNative: AdManager = AdManager(LDApplication.app)
    var adManagerEndDisNative: AdManager = AdManager(LDApplication.app)
    var adManagerEndIntDis: AdManager = AdManager(LDApplication.app)
    var adManagerListIntDis: AdManager = AdManager(LDApplication.app)

    const val open_type = "open"
    const val home_type = "mainad"
    const val result_type = "resultnv"
    const val cont_type = "vncad"
    const val list_type = "backivsv"
    const val end_type = "backivconnect"

    const val ad_wait = "ad_wait"
    const val ad_jump_over = "ad_jump_over"
    const val ad_show = "ad_show"

    // TODO firebase key
    const val onlien_ad_key = "nexus_va"
    const val onlien_pz_key = "nayan"
    const val onlien_smart_key = "cctea"
    const val onlien_list_key = "nows"

    var openTypeIp = ""
    var homeTypeIp = ""
    var resultTypeIp = ""
    var contTypeIp = ""
    var listTypeIp = ""
    var endTypeIp = ""
    var nowVpnBean: CountryBean = CountryBean()
    fun log(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.e("TAG", msg)
        }
    }

    fun getStartOpenAdData(): AdManager {
        return if (ConnectUtils.isVpnConnect()) {
            adManagerOpen
        } else {
            adManagerOpenDis
        }
    }

    fun getHomeNativeAdData(): AdManager {
        return if (ConnectUtils.isVpnConnect()) {
            adManagerHomeNative
        } else {
            adManagerHomeDisNative
        }
    }

    fun getEndNativeAdData(): AdManager {
        return if (ConnectUtils.isVpnConnect()) {
            adManagerEndNative
        } else {
            adManagerEndDisNative
        }
    }

    fun getInterListAdData(): AdManager {
        return if (ConnectUtils.isVpnConnect()) {
            adManagerListInt
        } else {
            adManagerListIntDis
        }
    }

    fun getEndIntAdData(): AdManager {
        return if (ConnectUtils.isVpnConnect()) {
            adManagerEndInt
        } else {
            adManagerEndIntDis
        }
    }


    fun getAdListData(): VpnAdBean {
        val onlineAdBean = DataManager.online_ad_value
        runCatching {
            if (onlineAdBean.isNotEmpty()) {
                return Gson().fromJson(base64Decode(onlineAdBean), VpnAdBean::class.java)
            } else {
                return Gson().fromJson(adJson, VpnAdBean::class.java)
            }
        }.getOrNull()
            ?: return Gson().fromJson(adJson, VpnAdBean::class.java)
    }

    fun base64Decode(base64Str: String): String {
        return String(Base64.decode(base64Str, Base64.DEFAULT))
    }

    fun getLjData(): AdLjBean {
        val adRefBean = DataManager.online_pz_value
        return runCatching {
            if (adRefBean.isNotEmpty()) {
                return Gson().fromJson(base64Decode(adRefBean), AdLjBean::class.java)
            } else {
                Gson().fromJson(dataPingJson, AdLjBean::class.java)
            }
        }.onFailure { exception ->
        }.getOrNull() ?: Gson().fromJson(dataPingJson, AdLjBean::class.java)
    }

    fun getAdBlackData(): Boolean {
        val adBlack = when (getLjData().nayan1) {
            "1" -> {
                DataManager.black_value != "tibet"
            }

            "2" -> {
                false
            }

            else -> {
                DataManager.black_value != "tibet"
            }
        }
        if (DataManager.black_up_state != "OK" && !adBlack) {
            UpDataMix.postPointData("u_whitelist")
            DataManager.black_up_state = "OK"
        }
        return adBlack
    }

    fun getConnectNum(): Pair<Long, Long> {
        val input = getLjData().nayan3 ?: "10&10"
        val regex = "(\\d+)&(\\d+)".toRegex() // 匹配任意长度的数字分隔符&数字
        val matchResult = regex.matchEntire(input)
        if (matchResult != null) {
            val leftNumber = matchResult.groupValues[1].toLong() // 转为长整型以处理大数字
            val rightNumber = matchResult.groupValues[2].toLong()
            log("Left Number: $leftNumber")
            log("Right Number: $rightNumber")
            return Pair(leftNumber, rightNumber)

        } else {
            log("Invalid format")
            return Pair(10, 10)
        }

    }

    // TODO ad json
    const val adJson = """
{
    "doll_sss": 40,
    "doll_ccc": 2,
    "cloud": {
        "open": [
            {
                "doll_where": "open",
                "doll_admob": "admob",
                "doll_id": "ca-app-pub-3940256099942544/9257395921",
                "doll_we": 2
            }
        ],
        "mainad": [
            {
                "doll_where": "native",
                "doll_admob": "admob",
                "doll_id": "ca-app-pub-3940256099942544/2247696110",
                "doll_we": 2
            }
        ],
        "resultnv": [
            {
                "doll_where": "native",
                "doll_admob": "admob",
                "doll_id": "ca-app-pub-3940256099942544/2247696110",
                "doll_we": 2
            }
        ],
        "vncad": [
            {
                "doll_where": "interstitial",
                "doll_admob": "admob",
                "doll_id": "ca-app-pub-3940256099942544/1033173712",
                "doll_we": 2
            }
        ],
        "backivsv": [
            {
                "doll_where": "interstitial",
                "doll_admob": "admob",
                "doll_id": "ca-app-pub-3940256099942544/1033173712",
                "doll_we": 2
            }
        ],
        "backivconnect": [
            {
                "doll_where": "interstitial",
                "doll_admob": "admob",
                "doll_id": "ca-app-pub-3940256099942544/1033173712",
                "doll_we": 2
            }
        ]
    },
    "local": {
        "open": [
            {
                "doll_where": "open",
                "doll_admob": "admob",
                "doll_id": "ca-app-pub-3940256099942544/9257395921",
                "doll_we": 1
            }
        ],
        "mainad": [
            {
                "doll_where": "native",
                "doll_admob": "admob",
                "doll_id": "ca-app-pub-3940256099942544/2247696110",
                "doll_we": 1
            }
        ],
        "resultnv": [
            {
                "doll_where": "native",
                "doll_admob": "admob",
                "doll_id": "ca-app-pub-3940256099942544/2247696110",
                "doll_we": 1
            }
        ],
        "backivsv": [
            {
                "doll_where": "interstitial",
                "doll_admob": "admob",
                "doll_id": "ca-app-pub-3940256099942544/1033173712",
                "doll_we": 1
            }
        ],
        "backivconnect": [
            {
                "doll_where": "interstitial",
                "doll_admob": "admob",
                "doll_id": "ca-app-pub-3940256099942544/1033173712",
                "doll_we": 1
            }
        ]
    }
}
    """

    // TODO ping json
    const val dataPingJson = """
        {
  "nayan1": 1,
  "nayan2":"",
  "nayan3":"10&10"
}
    """
}