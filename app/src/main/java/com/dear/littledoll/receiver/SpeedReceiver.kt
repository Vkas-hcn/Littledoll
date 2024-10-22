package com.dear.littledoll.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dear.littledoll.LDApplication
import com.dear.littledoll.imp.SpeedImp

class SpeedReceiver(val imp: SpeedImp) : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        imp.speedLong(p1?.getLongExtra("download", 0) ?: 0, p1?.getLongExtra("upload", 0) ?: 0)
    }
}