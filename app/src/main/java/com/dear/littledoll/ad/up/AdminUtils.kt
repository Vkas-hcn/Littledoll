package com.dear.littledoll.ad.up

import android.annotation.SuppressLint
import android.content.Context
import android.util.Base64
import com.dear.littledoll.BuildConfig
import com.dear.littledoll.LDApplication
import com.dear.littledoll.ad.AdDataUtils
import com.dear.littledoll.ad.AdDataUtils.log
import com.dear.littledoll.utils.DataManager
import com.dear.littledoll.utils.InspectUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

object AdminUtils {

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder().build()
    }

    private val BASE_URL = if (BuildConfig.DEBUG) {
        "https://ffat.safeproxyxxx.com/apitest/sdwsc/"
    } else {
        "https://ffat.safeproxyxxx.com/api/sdwsc/"
    }

    @SuppressLint("HardwareIds")
    private fun createAdminPayload(): String {
        return JSONObject().apply {
            put("DYsJlSz", "com.safeproxy.xxx.speedy")
            put("WxYrhjS", DataManager.uid_value)
            put("dAZMZDJTBL", DataManager.ref_value)
            put("mXZBIQNDRx", InspectUtils.getAppVersion(LDApplication.app))
        }.toString()
    }

    suspend fun getAdminData() = withContext(Dispatchers.IO) {
        val loadingTime =
            ((System.currentTimeMillis() - LDApplication.startAppTime) / 1000).toInt()
        val payload = createAdminPayload()
        log("getAdminData: -params-$payload")
        val startTime = System.currentTimeMillis()
        val maxRetries = 3
        UpDataMix.postPointData("u_admin_ask","time",loadingTime)
        repeat(maxRetries) { attempt ->
            try {
                val response = postWithRetry(BASE_URL, payload)
                DataManager.black_admin = parseAdminBlackData(response)
                DataManager.ref_admin = parseAdminRefData(response)
                postV24proxyData()
                val timeElapsed =
                    ((System.currentTimeMillis() - LDApplication.startAppTime) / 1000).toInt()
                UpDataMix.postPointData("u_admin_result", "time", timeElapsed)
                log("getAdminData-Success: $response --- ${DataManager.black_admin} --- ${DataManager.ref_admin}")
                return@withContext
            } catch (e: Exception) {
                log("getAdminData-Exception: Attempt $attempt failed - $e")
                if (attempt == maxRetries - 1) {
                    log("getAdminData-FinalFailure: Max retries reached.")
                }
            }
        }
    }

    private suspend fun postWithRetry(url: String, payload: String): String {
        return withContext(Dispatchers.IO) {
            val encryptedPayload = preparePayload(payload)
            repeat(3) { attempt ->
                try {
                    return@withContext executePostRequest(url, encryptedPayload)
                } catch (e: Exception) {
                    if (attempt == 2) throw e
                }
            }
            throw IllegalStateException("Retry attempts exhausted")
        }
    }

    private fun executePostRequest(url: String, encryptedPayload: Pair<String, String>): String {
        val (encodedPayload, timestamp) = encryptedPayload
        val request = Request.Builder()
            .url(url)
            .post(RequestBody.create("application/json".toMediaTypeOrNull(), encodedPayload))
            .addHeader("ts", timestamp)
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val errorBody = response.body?.string()
                throw Exception("HTTP error ${response.code}: $errorBody")
            }

            val responseTimestamp =
                response.header("ts") ?: throw IllegalStateException("Missing ts header")
            val encryptedResponse =
                response.body?.string() ?: throw IllegalStateException("Empty response body")
            val decodedBytes = Base64.decode(encryptedResponse, Base64.DEFAULT)
            val decodedString = String(decodedBytes, Charsets.UTF_8)

            return xorWithTimestamp(decodedString, responseTimestamp)
        }
    }

    private fun preparePayload(payload: String): Pair<String, String> {
        val timestamp = System.currentTimeMillis().toString()
        val encryptedPayload = xorWithTimestamp(payload, timestamp)
        val encodedPayload = Base64.encodeToString(
            encryptedPayload.toByteArray(StandardCharsets.UTF_8),
            Base64.NO_WRAP
        )
        return encodedPayload to timestamp
    }

    private fun xorWithTimestamp(text: String, ts: String): String {
        val cycleKey = ts.toCharArray()
        return text.mapIndexed { index, char ->
            char.toInt().xor(cycleKey[index % cycleKey.size].toInt()).toChar()
        }.joinToString("")
    }

    private fun parseAdminBlackData(jsonString: String): String {
        val confString = JSONObject(jsonString).getJSONObject("pLpoN").getString("conf")
        return JSONObject(confString).getString("munt")
    }

    private fun parseAdminRefData(jsonString: String): String {
        val confString = JSONObject(jsonString).getJSONObject("pLpoN").getString("conf")
        return JSONObject(confString).getString("humt")
    }

    private fun postV24proxyData() {
        if (DataManager.ref_admin == "1") {
            UpDataMix.postPointData("u_buying")
        }
    }
}
