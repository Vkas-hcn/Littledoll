package com.dear.littledoll.utils

import android.content.Context
import com.dear.littledoll.BuildConfig
import com.dear.littledoll.LDApplication
import com.dear.littledoll.R
import com.dear.littledoll.ad.AdDataUtils.base64Decode
import com.dear.littledoll.bean.CountryBean
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object DataManager {

    private val sharedPreferences by lazy { LDApplication.app.getSharedPreferences("data_ld", Context.MODE_PRIVATE) }
    //hmd
    var black_value:String
        get() = sharedPreferences.getString("black_value", "").toString()
        set(value) {
            sharedPreferences.edit().putString("black_value", value).apply()
        }
    var online_ad_value:String
        get() = sharedPreferences.getString("online_ad_value", "").toString()
        set(value) {
            sharedPreferences.edit().putString("online_ad_value", value).apply()
        }
    var online_pz_value:String
        get() = sharedPreferences.getString("online_pz_value", "").toString()
        set(value) {
            sharedPreferences.edit().putString("online_pz_value", value).apply()
        }

    var online_smart_value:String
        get() = sharedPreferences.getString("online_smart_value", "").toString()
        set(value) {
            sharedPreferences.edit().putString("online_smart_value", value).apply()
        }
    var online_list_value:String
        get() = sharedPreferences.getString("online_list_value", "").toString()
        set(value) {
            sharedPreferences.edit().putString("online_list_value", value).apply()
        }

    var htp_country: String
        get() = sharedPreferences.getString("htp_country", "").toString()
        set(value) {
            sharedPreferences.edit().putString("htp_country", value).apply()
        }

    var isSmart: Boolean
        get() = sharedPreferences.getBoolean("isSmart", true)
        set(value) {
            sharedPreferences.edit().putBoolean("isSmart", value).apply()
        }

    var selectItem = CountryBean()
    var selectTime = System.currentTimeMillis()
    var ip: String
        get() = sharedPreferences.getString("select_ip", "").toString()
        set(value) {
            sharedPreferences.edit().putString("select_ip", value).apply()
        }

    var ad_c_num: Int
        get() = sharedPreferences.getInt("ad_c_num", 0)
        set(value) {
            sharedPreferences.edit().putInt("ad_c_num", value).apply()
        }
    var ad_s_num: Int
        get() = sharedPreferences.getInt("ad_s_num", 0)
        set(value) {
            sharedPreferences.edit().putInt("ad_s_num", value).apply()
        }

    var ad_load_date: String
        get() = sharedPreferences.getString("ad_load_date", "").toString()
        set(value) {
            sharedPreferences.edit().putString("ad_load_date", value).apply()
        }

    var uid_value: String
        get() = sharedPreferences.getString("uid_value", "").toString()
        set(value) {
            sharedPreferences.edit().putString("uid_value", value).apply()
        }

    private fun getList(): MutableList<CountryBean> {
        val list = mutableListOf<CountryBean>()
        if (BuildConfig.DEBUG) {
            list.add(CountryBean("96.30.196.60", 443, "O6UcTwyLMDfAJqvdC5.l2hb", "AES-256-GCM", "Tokyo", "Japan"))

            list.add(CountryBean("195.234.82.34", 443, "O6UcTwyLMDfAJqvdC5.l2hb", "AES-256-GCM", "Miami", "United States"))

        } else {
            list.add(CountryBean("45.76.62.223", 443, "yTXIqjEZgtM9DQx-", "AES-256-GCM", "Atlanta","United States"))
            list.add(CountryBean("149.28.177.45", 443, "yTXIqjEZgtM9DQx-", "AES-256-GCM", "Sydney", "Australia"))
            list.add(CountryBean("216.128.185.73", 443, "yTXIqjEZgtM9DQx-", "AES-256-GCM", "Toronto", "Canada"))
            list.add(CountryBean("45.32.40.143", 443, "yTXIqjEZgtM9DQx-", "AES-256-GCM", "Tokyo", "Japan"))
        }
        return list
    }

    private fun getSmart(): MutableList<CountryBean> {
        val list = mutableListOf<CountryBean>()
        if (BuildConfig.DEBUG) {
            list.add(CountryBean("96.30.196.60", 443, "O6UcTwyLMDfAJqvdC5.l2hb", "AES-256-GCM", "Tokyo", "Japan"))
            list.add(CountryBean("195.234.82.34", 443, "O6UcTwyLMDfAJqvdC5.l2hb", "AES-256-GCM", "Miami", "United States"))
        } else {
            list.add(CountryBean("45.76.62.223", 443, "yTXIqjEZgtM9DQx-", "AES-256-GCM", "Atlanta","United States"))
            list.add(CountryBean("149.28.177.45", 443, "yTXIqjEZgtM9DQx-", "AES-256-GCM", "Sydney", "Australia"))
            list.add(CountryBean("216.128.185.73", 443, "yTXIqjEZgtM9DQx-", "AES-256-GCM", "Toronto", "Canada"))
            list.add(CountryBean("45.32.40.143", 443, "yTXIqjEZgtM9DQx-", "AES-256-GCM", "Tokyo", "Japan"))
        }
        return list
    }

    fun getOnlineVpnData(isSmart: Boolean): MutableList<CountryBean> {
        val onlineValue = if (isSmart) online_smart_value else online_list_value
        val localAdBean = if (isSmart) getSmart() else  getList()
        return runCatching {
            if (onlineValue.isNotEmpty()) {
                val type = object : TypeToken<MutableList<CountryBean>>() {}.type
                Gson().fromJson(base64Decode(onlineValue), type)
            } else {
                localAdBean
            }
        }.getOrDefault(localAdBean)
    }


    fun getSelectCountry(): CountryBean {
        return getList().find { it.ldHost == ip }!!
    }

    fun getGrouping(ls: MutableList<CountryBean> = getOnlineVpnData(false)): MutableList<MutableList<CountryBean>> {
        val list = mutableListOf<MutableList<CountryBean>>()
        list.add(mutableListOf())
        var count = 0
        val hasMap = hashMapOf<String, Int>()
        ArrayList(ls).forEach {
            val cc = it.nayan2.replace(" ","").lowercase()
            if (hasMap.containsKey(cc).not()) {
                count++
                hasMap[cc] = count
                list.add(mutableListOf())
            }
            list[hasMap[cc] as Int].add(it)
        }
        return list
    }


    fun getCountryIcon(name: String): Int {
        return when (name.replace(" ", "").lowercase()) {
            "australia" -> R.mipmap.australia
            "belgium" -> R.mipmap.belgium
            "brazil" -> R.mipmap.brazil
            "canada" -> R.mipmap.canada
            "eng" -> R.mipmap.eng
            "france" -> R.mipmap.france
            "germany" -> R.mipmap.germany
            "hongkong" -> R.mipmap.hongkong
            "india" -> R.mipmap.india
            "ireland" -> R.mipmap.ireland
            "italy" -> R.mipmap.italy
            "japan" -> R.mipmap.japan
            "korea" -> R.mipmap.korea
            "netherlands" -> R.mipmap.netherlands
            "newzealand" -> R.mipmap.newzealand
            "norway" -> R.mipmap.norway
            "russianfederation" -> R.mipmap.russianfederation
            "singapore" -> R.mipmap.singapore
            "sweden" -> R.mipmap.sweden
            "switzerland" -> R.mipmap.switzerland
            "unitedarabemirates" -> R.mipmap.unitedarabemirates
            "unitedstates" -> R.mipmap.unitedstates
            else -> R.mipmap.default_icon
        }
    }


}