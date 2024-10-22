package com.dear.littledoll.utils

import android.content.Context
import com.dear.littledoll.BuildConfig
import com.dear.littledoll.LDApplication
import com.dear.littledoll.R
import com.dear.littledoll.bean.CountryBean

object DataManager {

    private val sharedPreferences by lazy { LDApplication.app.getSharedPreferences("data_ld", Context.MODE_PRIVATE) }

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


    fun getList(): MutableList<CountryBean> {
        val list = mutableListOf<CountryBean>()
        if (BuildConfig.DEBUG) {
//            list.add(CountryBean("195.234.82.34", 443, "O6UcTwyLMDfAJqvdC5.l2hb", "AES-256-GCM", "Japan-Tokyo", "JA"))
//            list.add(CountryBean("155.118.15.96", 13, "1zE9koY3-ZwlBfAX", "AES-256-GCM", "Japan-Tokyo", "JA"))
            list.add(CountryBean("155.138.175.96", 443, "1zE9koY3-ZwlBfAX", "AES-256-GCM", "Japan-Tokyo", "JA"))
        } else {
            list.add(CountryBean("45.76.62.223", 443, "yTXIqjEZgtM9DQx-", "AES-256-GCM", "United States-Atlanta", "US"))
            list.add(CountryBean("149.28.177.45", 443, "yTXIqjEZgtM9DQx-", "AES-256-GCM", "Australia-Sydney", "AU"))
            list.add(CountryBean("216.128.185.73", 443, "yTXIqjEZgtM9DQx-", "AES-256-GCM", "Canada-Toronto", "FR"))
            list.add(CountryBean("45.32.40.143", 443, "yTXIqjEZgtM9DQx-", "AES-256-GCM", "Japan-Tokyo", "JA"))
        }
        return list
    }

    fun getSelectCountry(): CountryBean {
        return getList().find { it.ldHost == ip }!!
    }

    fun getGrouping(ls: MutableList<CountryBean> = getList()): MutableList<MutableList<CountryBean>> {
        val list = mutableListOf<MutableList<CountryBean>>()
        list.add(mutableListOf())
        var count = 0
        val hasMap = hashMapOf<String, Int>()
        ArrayList(ls).forEach {
            if (hasMap.containsKey(it.country).not()) {
                count++
                hasMap[it.country] = count
                list.add(mutableListOf())
            }
            list[hasMap[it.country] as Int].add(it)
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