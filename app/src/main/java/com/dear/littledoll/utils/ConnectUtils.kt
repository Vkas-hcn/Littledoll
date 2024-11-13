package com.dear.littledoll.utils

import android.net.VpnService
import android.util.Log
import com.dear.littledoll.BuildConfig
import com.dear.littledoll.LDApplication
import com.dear.littledoll.ad.AdDataUtils
import de.blinkt.openvpn.api.IOpenVPNAPIService
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.random.Random

object ConnectUtils {

    var oState = ""
    fun open(server: IOpenVPNAPIService) {
        var data = DataManager.selectItem
        if (data.ldHost.isEmpty()) {
            DataManager.isSmart = true
            data = DataManager.getOnlineVpnData(true).shuffled()
                .random(Random(System.currentTimeMillis()))
        } else {
            DataManager.isSmart = false
        }
        AdDataUtils.nowVpnBean = data
        DataManager.ip = data.ldHost
        runCatching {
            val conf = LDApplication.app.assets.open("test.ovpn")
            val bufferedReader = BufferedReader(InputStreamReader(conf))
            val config = StringBuilder()
            var line: String?
            while (true) {
                line = bufferedReader.readLine()
                if (line == null) break
                if (line.contains("hostIp", true)) line =
                    line.replace("hostIp", data.ldHost + " " + data.ldPort)
                if (line.contains("userPassword", true)) line =
                    line.replace("userPassword", data.ldPassword)
                config.append(line).append("\n")
                AdDataUtils.log("pei zhi nei rong =${line}")
            }
            bufferedReader.close()
            conf.close()
            server.startVPN(config.toString())
        }
    }


    fun isVpnPermission(): Boolean {
        return VpnService.prepare(LDApplication.app) == null
    }

    fun isVpnConnect(): Boolean {
        return oState == "CONNECTED"
    }

}