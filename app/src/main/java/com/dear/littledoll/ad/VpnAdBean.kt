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
    var mainad: List<AdEasy> = emptyList(), 
    var resultnv: List<AdEasy> = emptyList(),
    var vncad: List<AdEasy> = emptyList(), 
    var backivsv: List<AdEasy> = emptyList(), 
    var backivconnect: List<AdEasy> = emptyList() 
)

@Keep
data class AdEasy(
    var doll_id: String = "",
    var doll_we: Int = 0,
    var doll_admob: String = "",
    var doll_where: String = "",
    var llllcc: String? = null,
    var sssscc: String? = null,
    var llllpp: String? = null,
    var sssspp: String? = null
)

@Keep
data class AdLjBean(
    val nayan1: String,
    val nayan2: String,
    val nayan3: String
)


