package com.dear.littledoll.ad

import android.app.Activity
import android.app.Application
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.dear.littledoll.LDApplication
import com.dear.littledoll.MainActivity
import com.dear.littledoll.R
import com.dear.littledoll.ResultActivity
import com.dear.littledoll.SelectServerActivity
import com.dear.littledoll.SpeedActivity
import com.dear.littledoll.ad.AdDataUtils.log
import com.dear.littledoll.ad.up.UpDataMix
import com.dear.littledoll.utils.ConnectUtils
import com.dear.littledoll.utils.DataManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Arrays

class AdManager(private val application: Application) {
    private val adCache = mutableMapOf<String, Any>()
    private val adLoadInProgress = mutableMapOf<String, Boolean>()
    private val adTimestamps = mutableMapOf<String, Long>()
    private var adAllData: VpnAdBean = AdDataUtils.getAdListData()
    private var adDataOpen: AdEasy = AdEasy()
    private var adDataHome: AdEasy = AdEasy()
    private var adDataResult: AdEasy = AdEasy()
    private var adDataCont: AdEasy = AdEasy()
    private var adDataList: AdEasy = AdEasy()
    private var adDataEnd: AdEasy = AdEasy()

    private var adDataOpenDis: AdEasy = AdEasy()
    private var adDataHomeDis: AdEasy = AdEasy()
    private var adDataResultDis: AdEasy = AdEasy()
    private var adDataListDis: AdEasy = AdEasy()
    private var adDataEndDis: AdEasy = AdEasy()
    private var isFirstLoadingOpenAd = true

    init {
        MobileAds.initialize(application) {
            Log.d("AdManager", "AdMob initialized")
        }
        isAppOpenSameDayBa()
    }

    private fun canRequestAd(adType: String): Boolean {
        val currentTime = System.currentTimeMillis()
        val lastLoadTime = adTimestamps[adType] ?: 0L
        return currentTime - lastLoadTime > 3600 * 1000 // 1 hour
    }

    fun loadAd(adType: String) {
        adAllData = AdDataUtils.getAdListData()
        if (adLoadInProgress[adType] == true) return
        adLoadInProgress[adType] = true
        val adAllDataDetail = if (ConnectUtils.isVpnConnect()) {
            adAllData.cloud
        } else {
            adAllData.local
        }
        val adList = when (adType) {
            AdDataUtils.open_type -> adAllDataDetail.open
            AdDataUtils.home_type -> adAllDataDetail.mainad
            AdDataUtils.result_type -> adAllDataDetail.resultnv
            AdDataUtils.cont_type -> adAllDataDetail.vncad
            AdDataUtils.list_type -> adAllDataDetail.backivsv
            AdDataUtils.end_type -> adAllDataDetail.backivconnect
            else -> emptyList()
        }.sortedByDescending { it.doll_we }

        loadAdFromList(adType, adList, 0)
    }

    private fun loadAdFromList(adType: String, adList: List<AdEasy>, index: Int) {
        if (index >= adList.size) {
            adLoadInProgress[adType] = false
            if (isFirstLoadingOpenAd && adType == AdDataUtils.open_type) {
                loadAdFromList(adType, adList, 0)
                isFirstLoadingOpenAd = false
            }
            log("广告数据列表已用完，无法加载更多广告")
            return
        }
        if (ConnectUtils.isVpnConnect() && (getLoadIp(adType).isNotEmpty()) && getLoadIp(adType) != AdDataUtils.nowVpnBean.ldHost) {
            log(adType + "-ip不一致-重新加载-load_ip=" + getLoadIp(adType) + "-now-ip=" + AdDataUtils.nowVpnBean.ldHost)
            qcAd(adType)
            clearLoadIp(adType)
            loadAdFromList(adType, adList, index)
            return
        }
        if (adCache.containsKey(adType) && !canRequestAd(adType)) {
            log("已有$adType 广告，不在加载: ")
            return
        }
        if (!canLoadAd()) {
            log("广告超限，不在加载")
            return
        }
        val blackData = AdDataUtils.getAdBlackData()

        if (blackData && (adType == AdDataUtils.home_type || adType == AdDataUtils.cont_type || adType == AdDataUtils.list_type || adType == AdDataUtils.end_type)) {
            log("黑名单屏蔽$adType 广告，不在加载: ")
            return
        }
        if (DataManager.black_admin == "2" && (adType == AdDataUtils.end_type || adType == AdDataUtils.cont_type || adType == AdDataUtils.home_type)) {
            Log.e("TAG", "admin屏蔽${adType}广告")
            return
        }
        val adEasy = adList[index]
        log("$adType 广告，开始加载: id=${adEasy.doll_id};we=${adEasy.doll_we}")
        when (adEasy.doll_where) {
            "open" -> loadOpenAd(adType, adEasy, adList, index)
            "native" -> loadNativeAd(adType, adEasy, adList, index)
            "interstitial" -> loadInterstitialAd(adType, adEasy, adList, index)
        }
    }

    private fun loadOpenAd(adType: String, adEasy: AdEasy, adList: List<AdEasy>, index: Int) {
        if (ConnectUtils.isVpnConnect()) {
            AdDataUtils.openTypeIp = AdDataUtils.nowVpnBean.ldHost
            adDataOpen = adEasy
            adDataOpen = calledBeforeLoading(adEasy)
            UpDataMix.startLoadPointData(adDataOpen, adType)
        } else {
            adDataOpenDis = adEasy
            adDataOpenDis = calledBeforeLoading(adEasy)
            UpDataMix.startLoadPointData(adDataOpenDis, adType)
        }

        AppOpenAd.load(application, adEasy.doll_id, AdRequest.Builder().build(),
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    log("$adType+广告加载成功")
                    adCache[adType] = ad
                    adTimestamps[adType] = System.currentTimeMillis()
                    adLoadInProgress[adType] = false
                    ad.setOnPaidEventListener { adValue ->
                        adValue.let {
                            val adBean = if (ConnectUtils.isVpnConnect()) {
                                adDataOpen
                            } else {
                                adDataOpenDis
                            }
                            UpDataMix.postAdmobData(
                                application,
                                it,
                                ad.responseInfo,
                                adBean,
                                adType
                            )
                        }
                    }
                    val adBean = if (ConnectUtils.isVpnConnect()) {
                        adDataOpen
                    } else {
                        adDataOpenDis
                    }
                    UpDataMix.getLoadPointData(adBean, adType)

                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    log("${adType}广告加载失败=${loadAdError}----${getLoadIp(adType)}-----$adType")
                    adLoadInProgress[adType] = false
                    UpDataMix.getFailedPointData(
                        adEasy.doll_id,
                        getTbaLoadIp(adType),
                        adType,
                        loadAdError.message
                    )
                    loadAdFromList(adType, adList, index + 1)
                }
            })
    }

    private fun loadNativeAd(adType: String, adEasy: AdEasy, adList: List<AdEasy>, index: Int) {
        if (ConnectUtils.isVpnConnect()) {
            when (adType) {
                AdDataUtils.home_type -> {
                    AdDataUtils.homeTypeIp = AdDataUtils.nowVpnBean.ldHost
                    adDataHome = adEasy
                    adDataHome = calledBeforeLoading(adEasy)
                    UpDataMix.startLoadPointData(adDataHome, adType)
                }

                AdDataUtils.result_type -> {
                    AdDataUtils.resultTypeIp = AdDataUtils.nowVpnBean.ldHost
                    adDataResult = adEasy
                    adDataResult = calledBeforeLoading(adEasy)
                    UpDataMix.startLoadPointData(adDataResult, adType)
                }
            }
        } else {
            when (adType) {
                AdDataUtils.home_type -> {
                    adDataHomeDis = adEasy
                    adDataHomeDis = calledBeforeLoading(adEasy)
                    UpDataMix.startLoadPointData(adDataHomeDis, adType)
                }

                AdDataUtils.result_type -> {
                    adDataResultDis = adEasy
                    adDataResultDis = calledBeforeLoading(adEasy)
                    UpDataMix.startLoadPointData(adDataResultDis, adType)
                }
            }
        }

        val builder = NativeAdOptions.Builder()
            .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_LEFT)
        val adLoader = com.google.android.gms.ads.AdLoader.Builder(application, adEasy.doll_id)
            .forNativeAd { ad: NativeAd ->
                log("${adType}广告加载成功")
                adCache[adType] = ad
                adTimestamps[adType] = System.currentTimeMillis()
                adLoadInProgress[adType] = false
                getNatData(ad, adType)
            }
            .withNativeAdOptions(builder.build())
            .withAdListener(object : com.google.android.gms.ads.AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    log("${adType}广告加载失败=${loadAdError}")
                    adLoadInProgress[adType] = false
                    UpDataMix.getFailedPointData(
                        adEasy.doll_id,
                        getTbaLoadIp(adType),
                        adType,
                        loadAdError.message
                    )
                    loadAdFromList(adType, adList, index + 1)
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    log("点击原生广告")
                    setCLickNumFun()
                }
            })
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun loadInterstitialAd(
        adType: String,
        adEasy: AdEasy,
        adList: List<AdEasy>,
        index: Int
    ) {
        if (ConnectUtils.isVpnConnect()) {
            when (adType) {
                AdDataUtils.cont_type -> {
                    AdDataUtils.contTypeIp = AdDataUtils.nowVpnBean.ldHost
                    adDataCont = adEasy
                    adDataCont = calledBeforeLoading(adEasy)
                    UpDataMix.startLoadPointData(adDataCont, adType)

                }

                AdDataUtils.list_type -> {
                    AdDataUtils.listTypeIp = AdDataUtils.nowVpnBean.ldHost
                    adDataList = adEasy
                    adDataList = calledBeforeLoading(adEasy)
                    UpDataMix.startLoadPointData(adDataList, adType)

                }

                else -> {
                    AdDataUtils.endTypeIp = AdDataUtils.nowVpnBean.ldHost
                    adDataEnd = adEasy
                    adDataEnd = calledBeforeLoading(adEasy)
                    UpDataMix.startLoadPointData(adDataEnd, adType)
                }
            }
        } else {
            when (adType) {
                AdDataUtils.end_type -> {
                    adDataEndDis = adEasy
                    adDataEndDis = calledBeforeLoading(adEasy)
                }

                AdDataUtils.list_type -> {
                    adDataListDis = adEasy
                    adDataListDis = calledBeforeLoading(adEasy)
                }
            }
        }

        InterstitialAd.load(application, adEasy.doll_id, AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    log("${adType}广告加载成功")

                    adCache[adType] = ad
                    adTimestamps[adType] = System.currentTimeMillis()
                    adLoadInProgress[adType] = false
                    val adBean = if (ConnectUtils.isVpnConnect()) {
                        when (adType) {
                            AdDataUtils.cont_type -> {
                                adDataCont
                            }

                            AdDataUtils.list_type -> {
                                adDataList
                            }

                            else -> {
                                adDataEnd
                            }
                        }
                    } else {
                        when (adType) {
                            AdDataUtils.end_type -> {
                                adDataEndDis
                            }

                            AdDataUtils.list_type -> {
                                adDataListDis
                            }

                            else -> {
                                adDataEndDis
                            }
                        }
                    }
                    ad.setOnPaidEventListener { adValue ->
                        log("插屏广告 -${adType}，开始上报: ")
                        UpDataMix.postAdmobData(
                            application,
                            adValue,
                            ad.responseInfo,
                            adBean,
                            adType
                        )
                    }
                    UpDataMix.getLoadPointData(adBean, adType)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    log("${adType}广告加载失败=${loadAdError}")
                    adLoadInProgress[adType] = false
                    UpDataMix.getFailedPointData(
                        adEasy.doll_id,
                        getTbaLoadIp(adType),
                        adType,
                        loadAdError.message
                    )
                    loadAdFromList(adType, adList, index + 1)
                }
            })
    }

    fun qcAd(adType: String) {
        adLoadInProgress.remove(adType)
        adCache.remove(adType)
    }

    fun showAd(
        adType: String, activity: AppCompatActivity, nextFun: () -> Unit
    ) {
        if (ConnectUtils.isVpnConnect() && (getLoadIp(adType).isNotEmpty()) && getLoadIp(adType) != AdDataUtils.nowVpnBean.ldHost) {
            log(adType + "-ip不一致-禁止显示-load_ip=" + getLoadIp(adType) + "-now-ip=" + AdDataUtils.nowVpnBean.ldHost)
            nextFun()
            return
        }
        if (adCache.containsKey(adType) && activity.lifecycle.currentState == Lifecycle.State.RESUMED) {
            when (val ad = adCache[adType]) {
                is AppOpenAd -> {
                    ad.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                if (isAppInForeground(activity)) {
                                    nextFun()
                                }
                                qcAd(adType)
                            }

                            override fun onAdShowedFullScreenContent() {
                            }

                            override fun onAdClicked() {
                                setCLickNumFun()
                            }
                        }
                    ad.show(activity)
                    setShowNumFun()
                    if (ConnectUtils.isVpnConnect()) {
                        log(adType + "ip一致-显示-${adType}广告-load_ip=" + getLoadIp(adType) + "-now-ip=" + AdDataUtils.nowVpnBean.ldHost)
                    } else {
                        log(adType + "显示-${adType}广告")
                    }
                    adCache.remove(adType)
                    clearLoadIp(adType)
                    if (ConnectUtils.isVpnConnect()) {
                        adDataOpen = calledAfterPresentation(adDataOpen)
                        UpDataMix.showAdPointData(adDataOpen, adType)

                    } else {
                        adDataOpenDis = calledAfterPresentation(adDataOpenDis)
                        UpDataMix.showAdPointData(adDataOpenDis, adType)
                    }
                }

                is NativeAd -> {
                    setDisplayNativeAdLittle(adType, ad, activity, nextFun)
                }

                is InterstitialAd -> {
                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            log("关闭-${adType}广告: ")
                            qcAd(adType)
                            if (adType == AdDataUtils.home_type) {
                                loadAd(adType)
                            }
                            if (isAppInForeground(activity)) {
                                nextFun()
                            }
                        }

                        override fun onAdClicked() {
                            super.onAdClicked()
                            setCLickNumFun()
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                            adCache.remove(adType)
                        }

                        override fun onAdShowedFullScreenContent() {
                        }
                    }
                    ad.show(activity)
                    setShowNumFun()
                    if (ConnectUtils.isVpnConnect()) {
                        log(adType + "ip一致-显示-${adType}广告-load_ip=" + getLoadIp(adType) + "-now-ip=" + AdDataUtils.nowVpnBean.ldHost)
                    } else {
                        log(adType + "显示-${adType}广告")
                    }
                    adCache.remove(adType)
                    clearLoadIp(adType)
                    if (ConnectUtils.isVpnConnect()) {
                        when (adType) {
                            AdDataUtils.cont_type -> {
                                adDataCont = calledAfterPresentation(adDataCont)
                                UpDataMix.showAdPointData(adDataCont, adType)
                            }

                            AdDataUtils.list_type -> {
                                adDataList = calledAfterPresentation(adDataList)
                                UpDataMix.showAdPointData(adDataList, adType)
                            }

                            else -> {
                                adDataEnd = calledAfterPresentation(adDataEnd)
                                UpDataMix.showAdPointData(adDataEnd, adType)
                            }
                        }
                    } else {
                        when (adType) {
                            AdDataUtils.end_type -> {
                                adDataEndDis = calledAfterPresentation(adDataEndDis)
                                UpDataMix.showAdPointData(adDataEndDis, adType)
                            }

                            AdDataUtils.list_type -> {
                                adDataListDis = calledAfterPresentation(adDataListDis)
                                UpDataMix.showAdPointData(adDataListDis, adType)
                            }
                        }
                    }
                }
            }
        }
    }

    fun canShowAd(adType: String): String {
        val ad = adCache[adType]
        val blackData = AdDataUtils.getAdBlackData()

        if (blackData && (adType == AdDataUtils.home_type || adType == AdDataUtils.cont_type || adType == AdDataUtils.list_type || adType == AdDataUtils.end_type)) {
            return AdDataUtils.ad_jump_over
        }
        if (DataManager.black_admin == "2" && (adType == AdDataUtils.end_type || adType == AdDataUtils.cont_type || adType == AdDataUtils.home_type)) {
            return AdDataUtils.ad_jump_over
        }
        if (ad == null && !canLoadAd()) {
            return AdDataUtils.ad_jump_over
        }
        if (ad == null && canLoadAd()) {
            return AdDataUtils.ad_wait
        }
        if (ad != null && canLoadAd()) {
            return AdDataUtils.ad_show
        }
        return AdDataUtils.ad_show
    }

    fun getAdDataState(adType: String): Boolean {
        return adCache.containsKey(adType)
    }

    private fun canLoadAd(): Boolean {
        isAppOpenSameDayBa()
        val adOpenNum = adAllData.doll_sss
        val adClickNum = adAllData.doll_ccc
        val currentOpenCount = DataManager.ad_s_num
        val currentClickCount = DataManager.ad_c_num
        if (currentOpenCount >= adOpenNum && DataManager.point_num_state) {
            UpDataMix.adUpperLimitAdPointData("show")
        }
        if (currentClickCount >= adClickNum && DataManager.point_num_state) {
            UpDataMix.adUpperLimitAdPointData("click")
        }
        return currentOpenCount < adOpenNum && currentClickCount < adClickNum
    }


    private fun isAppOpenSameDayBa() {
        if (DataManager.ad_load_date.isBlank()) {
            DataManager.ad_load_date = formatDateNow()
        } else {
            if (dateAfterDate(DataManager.ad_load_date, formatDateNow())) {
                DataManager.ad_load_date = formatDateNow()
                log("超限-qingling:")
                DataManager.ad_s_num = 0
                DataManager.ad_c_num = 0
                DataManager.point_num_state = true
            }
        }
    }

    fun dateAfterDate(startTime: String?, endTime: String?): Boolean {
        val format = SimpleDateFormat("yyyy-MM-dd")
        try {
            val startDate: Date = format.parse(startTime)
            val endDate: Date = format.parse(endTime)
            val start: Long = startDate.getTime()
            val end: Long = endDate.getTime()
            if (end > start) {
                return true
            }
        } catch (e: Exception) {
            return false
        }
        return false
    }

    fun formatDateNow(): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val date = Date()
        return simpleDateFormat.format(date)
    }

    fun setCLickNumFun() {
        DataManager.ad_c_num += 1
    }

    private fun setShowNumFun() {
        DataManager.ad_s_num += 1
    }

    fun isAppInForeground(activity: FragmentActivity): Boolean {
        val activityManager =
            activity.getSystemService(Activity.ACTIVITY_SERVICE) as android.app.ActivityManager
        val runningAppProcesses = activityManager.runningAppProcesses ?: return false
        for (processInfo in runningAppProcesses) {
            if (processInfo.importance == android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && processInfo.processName == activity.packageName) {
                return true
            }
        }
        return false
    }

    private fun setDisplayNativeAdLittle(
        adType: String,
        ad: NativeAd,
        activity: AppCompatActivity,
        nextFun: () -> Unit
    ) {
        val adLayoutAdmob: ViewGroup
        val imgOcAd: View
        when (activity) {
            is MainActivity -> {
                adLayoutAdmob = activity.binding.adLayoutAdmob
                imgOcAd = activity.binding.imgOcAd
            }

            is ResultActivity -> {
                adLayoutAdmob = activity.binding.adLayoutAdmob
                imgOcAd = activity.binding.imgOcAd
            }

            is SelectServerActivity -> {
                adLayoutAdmob = activity.binding.adLayoutAdmob
                imgOcAd = activity.binding.imgOcAd
            }

            is SpeedActivity -> {
                adLayoutAdmob = activity.binding.adLayoutAdmob
                imgOcAd = activity.binding.imgOcAd
            }

            else -> {
                throw IllegalArgumentException("Unsupported activity type: ${activity::class.java.simpleName}")
            }
        }

        activity.lifecycleScope.launch(Dispatchers.Main) {
            ad.let { adData ->
                val state = activity.lifecycle.currentState == Lifecycle.State.RESUMED
                if (state) {
                    val adView = activity.layoutInflater.inflate(
                        R.layout.view_y_s,
                        null
                    ) as NativeAdView
                    populateNativeAdView(adData, adView)
                    adLayoutAdmob.apply {
                        removeAllViews()
                        addView(adView)
                    }
                    imgOcAd.isVisible = false
                    adLayoutAdmob.isVisible = true

                    setShowNumFun()
                    log("${adType} ip一致-显示广告-load_ip=" + getLoadIp(adType) + "-now-ip=" + AdDataUtils.nowVpnBean.ldHost)
                    adCache.remove(adType)
                    adLoadInProgress[adType] = false
                    if (ConnectUtils.isVpnConnect()) {
                        when (adType) {
                            AdDataUtils.home_type -> {
                                adDataHome = calledAfterPresentation(adDataHome)
                                UpDataMix.showAdPointData(adDataHome, adType)
                            }

                            AdDataUtils.result_type -> {
                                adDataResult = calledAfterPresentation(adDataResult)
                                UpDataMix.showAdPointData(adDataResult, adType)
                            }

                            else -> {
                            }
                        }
                    } else {
                        when (adType) {
                            AdDataUtils.home_type -> {
                                adDataHomeDis = calledAfterPresentation(adDataHomeDis)
                                UpDataMix.showAdPointData(adDataHomeDis, adType)
                            }

                            AdDataUtils.result_type -> {
                                adDataResultDis = calledAfterPresentation(adDataResultDis)
                                UpDataMix.showAdPointData(adDataResultDis, adType)
                            }

                            else -> {
                            }
                        }
                    }
                    nextFun()
                }
            }
        }
    }

    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.mediaView = adView.findViewById(R.id.ad_media)

        nativeAd.mediaContent?.let {
            adView.mediaView?.apply { setImageScaleType(ImageView.ScaleType.CENTER_CROP) }?.mediaContent =
                it
        }
        adView.mediaView?.clipToOutline = true
        if (nativeAd.body == null) {
            adView.bodyView?.visibility = View.INVISIBLE
        } else {
            adView.bodyView?.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }
        if (nativeAd.callToAction == null) {
            adView.callToActionView?.visibility = View.INVISIBLE
        } else {
            adView.callToActionView?.visibility = View.VISIBLE
            (adView.callToActionView as TextView).text = nativeAd.callToAction
        }
        if (nativeAd.headline == null) {
            adView.headlineView?.visibility = View.INVISIBLE
        } else {
            adView.headlineView?.visibility = View.VISIBLE
            (adView.headlineView as TextView).text = nativeAd.headline
        }

        if (nativeAd.icon == null) {
            adView.iconView?.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon?.drawable
            )
            adView.iconView?.visibility = View.VISIBLE
        }
        adView.setNativeAd(nativeAd)
    }

    private fun getNatData(ad: NativeAd, adType: String) {
        val adBean = if (ConnectUtils.isVpnConnect()) {
            when (adType) {
                AdDataUtils.home_type -> {
                    adDataHome
                }

                AdDataUtils.result_type -> {
                    adDataResult
                }

                else -> {
                    adDataHome
                }
            }
        } else {
            when (adType) {
                AdDataUtils.home_type -> {
                    adDataHomeDis
                }

                AdDataUtils.result_type -> {
                    adDataResultDis
                }

                else -> {
                    adDataHomeDis
                }
            }
        }
        ad.setOnPaidEventListener { adValue ->
            log("原生广告 -${adType}，开始上报: ")
            ad.responseInfo?.let {
                UpDataMix.postAdmobData(
                    application,
                    adValue,
                    it,
                    adBean,
                    adType
                )
            }
        }
        UpDataMix.getLoadPointData(adBean, adType)
    }


    private fun getLoadIp(adType: String): String {
        return when (adType) {
            AdDataUtils.open_type -> {
                AdDataUtils.openTypeIp
            }

            AdDataUtils.cont_type -> {
                AdDataUtils.contTypeIp
            }

            AdDataUtils.home_type -> {
                AdDataUtils.homeTypeIp
            }

            AdDataUtils.result_type -> {
                AdDataUtils.resultTypeIp
            }

            AdDataUtils.list_type -> {
                AdDataUtils.listTypeIp
            }

            AdDataUtils.end_type -> {
                AdDataUtils.endTypeIp
            }

            else -> {
                DataManager.localIp
            }
        }
    }

    private fun clearLoadIp(adType: String) {
        when (adType) {
            AdDataUtils.open_type -> {
                AdDataUtils.openTypeIp = ""
            }

            AdDataUtils.cont_type -> {
                AdDataUtils.contTypeIp = ""
            }

            AdDataUtils.home_type -> {
                AdDataUtils.homeTypeIp = ""
            }

            AdDataUtils.result_type -> {
                AdDataUtils.resultTypeIp = ""
            }

            AdDataUtils.list_type -> {
                AdDataUtils.listTypeIp = ""
            }

            AdDataUtils.end_type -> {
                AdDataUtils.endTypeIp = ""
            }
        }
    }

    private fun calledBeforeLoading(adInformation: AdEasy): AdEasy {
        if (ConnectUtils.isVpnConnect()) {
            adInformation.llllpp = AdDataUtils.nowVpnBean.ldHost
            adInformation.llllcc = AdDataUtils.nowVpnBean.nayan4
            return adInformation
        }
        adInformation.llllpp = DataManager.localIp
        adInformation.llllcc = "no connect"
        return adInformation
    }

    private fun calledAfterPresentation(adInformation: AdEasy): AdEasy {
        if (ConnectUtils.isVpnConnect()) {
            adInformation.sssspp = AdDataUtils.nowVpnBean.ldHost
            adInformation.sssscc = AdDataUtils.nowVpnBean.nayan4
            return adInformation
        }
        adInformation.sssspp = DataManager.localIp
        adInformation.sssscc = "no connect"
        return adInformation
    }

    fun getTbaLoadIp(adType: String): String {
        return if (ConnectUtils.isVpnConnect()) {
            getLoadIp(adType)
        } else {
            DataManager.localIp
        }
    }
}
