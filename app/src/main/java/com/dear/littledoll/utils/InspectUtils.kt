package com.dear.littledoll.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.webkit.WebSettings
import com.dear.littledoll.LDApplication
import com.dear.littledoll.ad.AdDataUtils.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.Locale
import kotlin.system.exitProcess
import java.net.URLEncoder

object InspectUtils {

    private var getUrlCount = 0

    fun inspectCountry() {
        getUrlCount++
        val pair = getUrl()
        val build = Request.Builder().url(pair.first).header("User-Agent", WebSettings.getDefaultUserAgent(LDApplication.app)).build()
        OkHttpClient().newBuilder().build().newCall(build).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                CoroutineScope(Dispatchers.IO).launch {
                    delay(3000)
                    inspectCountry()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code == 200) {
                    runCatching {
                        val json = JSONObject(response.body?.string() ?: "")
                        DataManager.htp_country = json.getString(pair.second)
                    }

                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        delay(3000)
                        inspectCountry()
                    }
                }
            }
        })
    }


    fun getMapData(
        url: String,
        map: Map<String, Any>,
        onNext: (response: String) -> Unit,
        onError: (error: String) -> Unit
    ) {
        val queryParameters = StringBuilder()
        for ((key, value) in map) {
            if (queryParameters.isNotEmpty()) {
                queryParameters.append("&")
            }
            queryParameters.append(URLEncoder.encode(key, "UTF-8"))
            queryParameters.append("=")
            queryParameters.append(URLEncoder.encode(value.toString(), "UTF-8"))
        }

        val urlString = if (url.contains("?")) {
            "$url&$queryParameters"
        } else {
            "$url?$queryParameters"
        }

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(urlString)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        onNext(it.body?.string() ?: "No response body")
                    } else {
                        onError("HTTP error: ${it.code}")
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                onError("Network error: ${e.message}")
            }
        })
    }
    fun obtainTheDataOfBlacklistedUsers(context: Context) {
        if (DataManager.black_value.isNotEmpty()) {
            return
        }
        getMapData(
            "https://sling.safeproxyxxx.com/vodka/lenore",
            mapOf(
                "pugh" to "com.safeproxy.xxx.speedy",
                "wayside" to "offhand",
                "larval" to getAppVersion(context).orEmpty(),
                "boyar" to DataManager.uid_value,
            ),
            onNext = {
                log( "The blacklist request is successful：$it")
                DataManager.black_value = it
            },
            onError = {
                GlobalScope.launch(Dispatchers.IO) {
                    delay(10000)
                    log( "The blacklist request failed：$it")
                    obtainTheDataOfBlacklistedUsers(context)
                }
            })
    }

    private fun getUrl(): Pair<String, String> {
        if (getUrlCount <= 3) {
            return Pair("https://api.infoip.io/", "country_short")
        } else if (getUrlCount <= 6) {
            return Pair("https://ipapi.co/json", "country_code")
        }
        getUrlCount = 0
        return Pair("https://api.infoip.io/", "country_short")
    }


    fun inspectConnect(activity: Activity): Boolean {
        if (inspectNetwork().not()) {
            AlertDialog.Builder(activity).create().apply {
                setCancelable(false)
                setOnKeyListener { dialog, keyCode, event -> true }
                setMessage("No network available. Please check your connection")
                setButton(AlertDialog.BUTTON_NEGATIVE, "OK") { d, _ -> dismiss() }
                show()
            }
            return true
        }
        val country = DataManager.htp_country.ifEmpty { Locale.getDefault().country }
//        if (arrayOf("CN", "HK", "MO", "IR").any { country.contains(it, true) }) {
//            AlertDialog.Builder(activity).create().apply {
//                setCancelable(false)
//                setOnKeyListener { dialog, keyCode, event -> true }
//                setMessage("This service is restricted in your region")
//                setButton(AlertDialog.BUTTON_NEGATIVE, "Confirm") { d, _ -> dismiss() }
//                setOnDismissListener {
//                    activity.finish()
//                    exitProcess(0)
//                }
//                show()
//            }
//            return true
//        }
        return false
    }

    private fun inspectNetwork(): Boolean {
        (LDApplication.app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            getNetworkCapabilities(activeNetwork)?.apply {
                return (hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || hasTransport(
                    NetworkCapabilities.TRANSPORT_CELLULAR
                ) || hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
            }
        }
        return false
    }

    private fun getAppVersion(context: Context): String? {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }
}