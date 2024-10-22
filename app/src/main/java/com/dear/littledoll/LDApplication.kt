package com.dear.littledoll

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.util.Log
import com.dear.littledoll.utils.InspectUtils


class LDApplication : Application() {

    companion object {
        lateinit var app: Application
    }

    override fun onCreate() {
        super.onCreate()
        if (isMain()) {
            app = this
            InspectUtils.inspectCountry()
        }
    }

    private fun isMain(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val processName = activityManager.runningAppProcesses.find { it.pid == android.os.Process.myPid() }?.processName ?: ""
        return processName == packageName
    }

}