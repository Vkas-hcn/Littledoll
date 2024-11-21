package com.dear.littledoll.ad.up

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.webkit.WebSettings
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.android.installreferrer.api.ReferrerDetails
import com.dear.littledoll.BuildConfig
import com.dear.littledoll.LDApplication
import com.dear.littledoll.ad.AdDataUtils
import com.dear.littledoll.ad.AdDataUtils.log
import com.dear.littledoll.ad.AdEasy
import com.dear.littledoll.utils.ConnectUtils
import com.dear.littledoll.utils.DataManager
import com.dear.littledoll.utils.InspectUtils
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.ResponseInfo
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.math.BigDecimal
import java.util.Currency
import java.util.Locale
import java.util.UUID

object UpDataMix {
    var upUrl = if (BuildConfig.DEBUG) {
        "https://test-delia.safeproxyxxx.com/warranty/bindery/rotate"
    } else {
        "https://delia.safeproxyxxx.com/migrant/shipload"
    }

    private fun topJsonData(context: Context): JSONObject {
        val lewd = JSONObject().apply {
            put("larval", InspectUtils.getAppVersion(context))
            put("beatnik", "")
            put("wastrel", "2222")
            put("boyar", DataManager.uid_value)
            put("rate", "1222")
            put("redbud", "1314")
            put("wayside", "offhand")
        }

        val nun = JSONObject().apply {
            put("hampton", "12")
            put("dahomey", "122")
        }

        val cicada = JSONObject().apply {
            put("eventual", System.currentTimeMillis())
        }

        val currant = JSONObject().apply {
            put("pugh", context.packageName)
            put("flagrant", DataManager.gid_value)
            put("fixture", Locale.getDefault().language + '_' + Locale.getDefault().country)
            put("ribbon", UUID.randomUUID().toString())
        }

        val json = JSONObject().apply {
            put("lewd", lewd)
            put("nun", nun)
            put("cicada", cicada)
            put("currant", currant)
        }

        return json

    }

    private fun upSessionJson(context: Context): String {
        return topJsonData(context).apply {
            put("plenary", JSONObject())
        }.toString()
    }

    private fun upInstallJson(context: Context, referrerDetails: ReferrerDetails): String {
        return topJsonData(context).apply {
            //build
            put("descant", "build/${Build.ID}")

            //referrer_url
            put("inspire", referrerDetails.installReferrer)

            //install_version
            put("coronet", referrerDetails.installVersion)

            //user_agent
            put("amanita", getWebDefaultUserAgent(context))

            //lat
            put("excision", getLimitTracking(context))

            //referrer_click_timestamp_seconds
            put("elector", referrerDetails.referrerClickTimestampSeconds)

            //install_begin_timestamp_seconds
            put("hand", referrerDetails.installBeginTimestampSeconds)

            //referrer_click_timestamp_server_seconds
            put("kim", referrerDetails.referrerClickTimestampServerSeconds)

            //install_begin_timestamp_server_seconds
            put("pose", referrerDetails.installBeginTimestampServerSeconds)

            //install_first_seconds
            put("well", getFirstInstallTime(context))

            //last_update_seconds
            put("bench", getLastUpdateTime(context))
            put("jerry", "carlton")
        }.toString()
    }


    private fun upAdJson(
        context: Context, adValue: AdValue,
        responseInfo: ResponseInfo,
        adEasy: AdEasy,
        ad_pos_id: String
    ): String {
        val faustian = JSONObject().apply {
            //ad_pre_ecpm
            put("astigmat", adValue.valueMicros)
            //currency
            put("morpheme", adValue.currencyCode)
            //ad_network
            put(
                "oman",
                responseInfo.mediationAdapterClassName
            )
            //ad_source
            put("pastor", "admob")
            //ad_code_id
            put("siege", adEasy.doll_id)
            //ad_pos_id
            put("gases", ad_pos_id)
            //ad_rit_id
            put("mercedes", "")
            //ad_sense
            put("horrible", "")
            //ad_format
            put("guru", adEasy.doll_where)
            //precision_type
            put("burly", getPrecisionType(adValue.precisionType))
            //ad_load_ip
            put("sargent", adEasy.llllpp ?: "")
            //ad_impression_ip
            put("handgun", adEasy.sssspp ?: "")
            //ad_sdk_ver
            put("ipecac", responseInfo.responseId)
        }
        return topJsonData(context).apply {
            put("faustian", faustian)
            put("p_theorist", adEasy.llllcc)
            put("s_theorist", adEasy.sssscc)
        }.toString()
    }

    private fun upPointJson(name: String): String {
        return topJsonData(LDApplication.app).apply {
            put("jerry", name)
        }.toString()
    }

    private fun upPointJson(
        name: String,
        key1: String? = null,
        keyValue1: Any? = null,
        key2: String? = null,
        keyValue2: Any? = null,
        key3: String? = null,
        keyValue3: Any? = null,
        key4: String? = null,
        keyValue4: Any? = null
    ): String {

        return topJsonData(LDApplication.app).apply {
            put("jerry", name)
            if (key1 != null) {
                put("thwart$$key1", keyValue1)
            }
            if (key2 != null) {
                put("thwart$$key2", keyValue2)
            }
            if (key3 != null) {
                put("thwart$$key3", keyValue3)
            }
            if (key4 != null) {
                put("thwart$$key4", keyValue4)
            }
        }.toString()
    }

    suspend fun postSessionData(context: Context) {
        val data = upSessionJson(context)
        log("Session: data=${data}")
        try {
            val result = InspectUtils.postNetwork(data)
            result.onSuccess { responseBody ->
                log("Session-请求成功: $responseBody")
            }

            result.onFailure { error ->
                log("Session-请求失败: ${error.message}")
            }
        } catch (e: Exception) {
            log("Session-发生错误: ${e.message}")
        }
    }

    fun postInstallData(context: Context, referrerDetails: ReferrerDetails) {
        if (DataManager.install_value == "OK") {
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            val data = upInstallJson(context, referrerDetails)
            log("Install: data=${data}")
            try {
                val result = InspectUtils.postNetwork(data)
                result.onSuccess { responseBody ->
                    log("Install-请求成功: $responseBody")
                    DataManager.install_value = "OK"
                }

                result.onFailure { error ->
                    log("Install-请求失败: ${error.message}")
                }
            } catch (e: Exception) {
                log("Install-发生错误: ${e.message}")
            }
        }
    }

    fun postAdmobData(
        context: Context, adValue: AdValue,
        responseInfo: ResponseInfo,
        adEasy: AdEasy,
        ad_pos_id: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val data = upAdJson(context, adValue, responseInfo, adEasy, ad_pos_id)
            log("Admob: ${ad_pos_id}-data=${data}")
            try {
                val result = InspectUtils.postNetwork(data)
                result.onSuccess { responseBody ->
                    log("Admob-${ad_pos_id}-请求成功: $responseBody")
                }

                result.onFailure { error ->
                    log("Admob-${ad_pos_id}-请求失败: ${error.message}")
                }
            } catch (e: Exception) {
                log("Admob-${ad_pos_id}-发生错误: ${e.message}")
            }
            postAdValue(context, adValue, responseInfo)
        }
    }

    fun postPointData(
        name: String,
        key1: String? = null,
        keyValue1: Any? = null,
        key2: String? = null,
        keyValue2: Any? = null,
        key3: String? = null,
        keyValue3: Any? = null,
        key4: String? = null,
        keyValue4: Any? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val data = if (key1 != null) {
                upPointJson(
                    name,
                    key1,
                    keyValue1,
                    key2,
                    keyValue2,
                    key3,
                    keyValue3,
                    key4,
                    keyValue4
                )
            } else {
                upPointJson(name)
            }
            log("Point-${name}-开始打点--${data}")
            try {
                val result = InspectUtils.postNetwork(data)
                result.onSuccess { responseBody ->
                    log("Point-${name}-请求成功: $responseBody")
                }

                result.onFailure { error ->
                    log("Point-${name}-请求失败: ${error.message}")
                }
            } catch (e: Exception) {
                log("Point-${name}-发生错误: ${e.message}")
            }
            val bundleEvent = Bundle()
            if (keyValue1 != null) {
                bundleEvent.putString(key1, keyValue1.toString())
            }
            if (keyValue2 != null) {
                bundleEvent.putString(key2, keyValue2.toString())
            }
            if (keyValue3 != null) {
                bundleEvent.putString(key3, keyValue3.toString())
            }
            if (keyValue4 != null) {
                bundleEvent.putString(key4, keyValue4.toString())
            }
            FirebaseAnalytics.getInstance(LDApplication.app).logEvent(name, bundleEvent)
        }
    }

    private fun getFirstInstallTime(context: Context): Long {
        try {
            val packageInfo =
                context.packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.firstInstallTime / 1000
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return 0
    }

    private fun getLimitTracking(context: Context): String {
        return try {
            if (AdvertisingIdClient.getAdvertisingIdInfo(context).isLimitAdTrackingEnabled) {
                "ephraim"
            } else {
                "lutz"
            }
        } catch (e: Exception) {
            "lutz"
        }
    }

    private fun getLastUpdateTime(context: Context): Long {
        try {
            val packageInfo =
                context.packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.lastUpdateTime / 1000
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return 0
    }

    private fun getPrecisionType(precisionType: Int): String {
        return when (precisionType) {
            0 -> {
                "UNKNOWN"
            }

            1 -> {
                "ESTIMATED"
            }

            2 -> {
                "PUBLISHER_PROVIDED"
            }

            3 -> {
                "PRECISE"
            }

            else -> {
                "UNKNOWN"
            }
        }
    }

    private fun getWebDefaultUserAgent(context: Context): String {
        return try {
            WebSettings.getDefaultUserAgent(context)
        } catch (e: Exception) {
            ""
        }
    }

    // point--------

    fun startLoadPointData(adBean: AdEasy, adType: String) {
        postPointData(
            "abc_ask",
            "inform",
            adType,
            "page",
            LDApplication.nowAN,
            "ID",
            "${adBean.doll_id}+${ConnectUtils.isVpnConnect()}",
            "IP",
            adBean.llllpp
        )

        if (ConnectUtils.isVpnConnect()) {
            postPointData(
                "abc_connect_ask",
                "inform",
                adType,
                "page",
                LDApplication.nowAN,
                "ID",
                "${adBean.doll_id}+${ConnectUtils.isVpnConnect()}",
                "IP",
                adBean.llllpp
            )
        }
    }

    fun getLoadPointData(adBean: AdEasy, adType: String) {
        postPointData(
            "abc_gett",
            "inform",
            adType,
            "page",
            LDApplication.nowAN,
            "ID",
            "${adBean.doll_id}+${ConnectUtils.isVpnConnect()}",
            "IP",
            adBean.llllpp
        )
    }

    fun getFailedPointData(doll_id:String,llllpp: String, adType: String, error: String) {
        postPointData(
            "abc_askdis",
            "inform",
            adType,
            "ID",
            "$doll_id}+${ConnectUtils.isVpnConnect()}",
            "IP",
            llllpp,
            "reason",
            error
        )
    }

    fun showAdPointData(adBean: AdEasy, adType: String) {
        postPointData(
            "abc_view",
            "inform",
            adType,
            "page",
            LDApplication.nowAN,
            "ID",
            "${adBean.doll_id}+${ConnectUtils.isVpnConnect()}",
            "pp",
            adBean.llllpp,
        )
    }

    fun adUpperLimitAdPointData(showType: String) {
        DataManager.point_num_state = false
        postPointData("abc_limit", "type", showType)
    }

    private fun postAdValue(context: Context, adValue: AdValue, responseInfo: ResponseInfo?) {
        val adRevenue = AdjustAdRevenue(AdjustConfig.AD_REVENUE_ADMOB)
        adRevenue.setRevenue(adValue.valueMicros / 1000000.0, adValue.currencyCode)
        adRevenue.setAdRevenueNetwork(responseInfo?.mediationAdapterClassName)
        Adjust.trackAdRevenue(adRevenue)
        if (AdDataUtils.getLjData().nayan2.isNotBlank()) {
            AppEventsLogger.newLogger(context).logPurchase(
                BigDecimal((adValue.valueMicros / 1000000.0).toString()),
                Currency.getInstance("USD")
            )
            return
        }
    }
}