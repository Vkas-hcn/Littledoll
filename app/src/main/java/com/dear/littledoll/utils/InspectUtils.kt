package com.dear.littledoll.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.webkit.WebSettings
import com.dear.littledoll.LDApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
        if (arrayOf("CN", "HK", "MO", "IR").any { country.contains(it, true) }) {
            AlertDialog.Builder(activity).create().apply {
                setCancelable(false)
                setOnKeyListener { dialog, keyCode, event -> true }
                setMessage("This service is restricted in your region")
                setButton(AlertDialog.BUTTON_NEGATIVE, "Confirm") { d, _ -> dismiss() }
                setOnDismissListener {
                    activity.finish()
                    exitProcess(0)
                }
                show()
            }
            return true
        }
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


}