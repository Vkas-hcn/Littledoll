package com.dear.littledoll.bean

import androidx.annotation.Keep
import com.dear.littledoll.utils.DataManager
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class CountryBean(
    @SerializedName("nayan1")
    var ldHost: String = "",
    @SerializedName("nayan6")
    var ldPort: Int = 0,
    @SerializedName("nayan3")
    var ldPassword: String = "",
    @SerializedName("nayan5")
    var ldMethode: String = "",
    var nayan4:String ="",
    var nayan2: String = "Smart Server",
):Serializable {
    fun getName(): String {
        return if (ldPort==0) "Smart Server" else "$nayan2 - $nayan4"
    }

    fun getFistName(): String {
        return nayan2
    }

    fun getLastName(): String {
        return nayan4
    }

    fun getIcon(): Int {
        return DataManager.getCountryIcon(getFistName())
    }

    fun getTag(): String {
        return "01"
    }

}