package com.dear.littledoll.ad


import androidx.annotation.Keep


@Keep
data class VpnAdBean(
    var cloud: VpnAdBeanDetail = VpnAdBeanDetail(),
    var doll_ccc: Int = 0,
    var doll_sss: Int = 0,
    var local: VpnAdBeanDetail = VpnAdBeanDetail()
)

@Keep
data class VpnAdBeanDetail(
    var open: List<AdEasy> = emptyList(),
    var mainad: List<AdEasy> = emptyList(), // VPN首页底部原生
    var resultnv: List<AdEasy> = emptyList(), // VPN连接/断开结果页/服务器列表页/测速结果页底部原生广告
    var vncad: List<AdEasy> = emptyList(), // 连接/断开/切换VPN插屏广告
    var backivsv: List<AdEasy> = emptyList(), // 服务器页面返回插屏广告
    var backivconnect: List<AdEasy> = emptyList() // VPN连接/断开结果页返回插屏广告
)

@Keep
data class AdEasy(
    var doll_id: String = "",
    var doll_we: Int = 0,
    var doll_admob: String = "",
    var doll_where: String = "",
    var loadCity: String? = null,
    var showTheCity: String? = null,
    var loadIp: String? = null,
    var showIp: String? = null
)

@Keep
data class AdLjBean(
    val nayan1: String,
    val nayan2: String,
    val nayan3: String
)


