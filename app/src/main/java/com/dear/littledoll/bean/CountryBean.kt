package com.dear.littledoll.bean

import com.dear.littledoll.utils.DataManager
import java.io.Serializable

data class CountryBean(
    var ldHost: String = "",
    var ldPort: Int = 0,
    var ldPassword: String = "",
    var ldMethode: String = "",
    var nameStr: String = "Smart Server",
    var country: String = "",
):Serializable {
    fun getName(): String {
        return nameStr
    }

    fun getFistName(): String {
        return nameStr.split("-")[0]
    }

    fun getLastName(): String {
        return nameStr.split("-")[1]
    }

    fun getIcon(): Int {
        return DataManager.getCountryIcon(getFistName())
    }

    fun getTag(): String {
        return "01"
    }

}