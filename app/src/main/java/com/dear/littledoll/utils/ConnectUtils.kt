package com.dear.littledoll.utils

import android.net.VpnService
import android.util.Log
import com.dear.littledoll.BuildConfig
import com.dear.littledoll.LDApplication
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
            data = DataManager.getList().shuffled().random(Random(System.currentTimeMillis()))
        }else{
            DataManager.isSmart = false
        }

        DataManager.ip = data.ldHost
        runCatching {
            //这个是春哥给你的配置文件，然后你放到项目的assets目录下即可，填上对应的名字
//            val conf = LDApplication.app.assets.open(if (BuildConfig.DEBUG) "test2.ovpn" else "lain.ovpn")
            val conf = LDApplication.app.assets.open("test2.ovpn" )
            val bufferedReader = BufferedReader(InputStreamReader(conf))
            val config = StringBuilder()
            var line: String?
            while (true) {
                line = bufferedReader.readLine()
                if (line == null) break
                if (line.contains("hostIp", true)) line = line.replace("hostIp", data.ldHost + " " + data.ldPort)
                if (line.contains("userPassword", true)) line = line.replace("userPassword", data.ldPassword)
                config.append(line).append("\n")
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