package com.dear.littledoll.utils

import android.animation.ObjectAnimator
import android.app.Activity
import android.app.AlertDialog
import android.text.format.Formatter
import android.util.Log
import android.view.LayoutInflater
import com.dear.littledoll.LDApplication
import com.dear.littledoll.R
import com.dear.littledoll.databinding.LoadingLayoutBinding
import fr.bmartel.speedtest.SpeedTestReport
import fr.bmartel.speedtest.SpeedTestSocket
import fr.bmartel.speedtest.inter.ISpeedTestListener
import fr.bmartel.speedtest.model.SpeedTestError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.random.Random


class SpeedUtils {

    fun loading(activity: Activity, block: (String, String) -> Unit, error: () -> Unit) {
        val binding = LoadingLayoutBinding.inflate(LayoutInflater.from(activity))
        val rotationAnimator = ObjectAnimator.ofFloat(binding.ivIcon, "rotation", 0f, 360f)
        rotationAnimator.duration = 2000
        rotationAnimator.repeatCount = ObjectAnimator.INFINITE
        rotationAnimator.start()
        AlertDialog.Builder(activity).setView(binding.root).create().apply {
            setCancelable(false)
            window?.setBackgroundDrawableResource(R.drawable.laoding_drawable)
            setOnKeyListener { _, _, _ -> true }
            speed({ a, b ->
                block(a, b)
                dismiss()
            }, {
                dismiss()
                error()
            })
            show()
            window?.apply {
                attributes = attributes.apply {
                    width = activity.resources.displayMetrics.widthPixels
                    height = activity.resources.displayMetrics.heightPixels
                }
            }
        }
    }


    private fun speed(block: (String, String) -> Unit, error: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val downloadSpeed = SpeedTestSocket()
            val uploadSpeed = SpeedTestSocket()
            val time = (30000L..105000L).random(Random(System.currentTimeMillis()))
            var download = ""
            var upload = ""
            var downloadError = -1
            var uploadError = -1
            var downloadImp: DownloadSpeedListener? = null
            var uploadImp: UploadSpeedListener? = null
            launch {
                runCatching {
                    downloadImp = DownloadSpeedListener(block = { download = it }, result = { downloadError = it })
                    downloadSpeed.addSpeedTestListener(downloadImp)
//                    downloadSpeed.startDownload("https://ipv4.aix-marseille.testdebit.info/5M.iso")
                    downloadSpeed.startDownload("https://hil.proof.ovh.us/files/1Mb.dat")
                }
            }
            launch {
                runCatching {
                    uploadImp = UploadSpeedListener(block = { upload = it }, result = { uploadError = it })
                    uploadSpeed.addSpeedTestListener(uploadImp)
//                    uploadSpeed.startUpload("https://k-net.testdebit.info/", 5 * 1024)
//                    uploadSpeed.startUpload("https://ipv4.aix-marseille.testdebit.info/", 5 * 1024)
                    uploadSpeed.startUpload("https://hil.proof.ovh.us/files/", 5 * 1024)
                }
            }
            withTimeoutOrNull(time) {
                while (true) {
                    if (downloadError == 0 && uploadError == 0) break
                    if (downloadError == 1 && uploadError == 1) break
                    delay(200)
                }
            }
            downloadSpeed.removeSpeedTestListener(downloadImp)
            uploadSpeed.removeSpeedTestListener(uploadImp)

            if (download.isNotEmpty() && upload.isNotEmpty()) {
                withContext(Dispatchers.Main) { block(download, upload) }
            } else {
                withContext(Dispatchers.Main) { error() }
            }
        }
    }


    class DownloadSpeedListener(val block: (String) -> Unit = {}, val result: (Int) -> Unit) : ISpeedTestListener {
        override fun onCompletion(report: SpeedTestReport?) {
            result(1)
        }

        override fun onProgress(percent: Float, report: SpeedTestReport?) {
            block(Formatter.formatFileSize(LDApplication.app, (report?.transferRateBit ?: 0.0).toLong()))
        }

        override fun onError(speedTestError: SpeedTestError?, errorMessage: String?) {
            result(0)
        }

    }


    class UploadSpeedListener(val block: (String) -> Unit = {}, val result: (Int) -> Unit) : ISpeedTestListener {
        override fun onCompletion(report: SpeedTestReport?) {
            result(1)
        }

        override fun onProgress(percent: Float, report: SpeedTestReport?) {
            block(Formatter.formatFileSize(LDApplication.app, (report?.transferRateBit ?: 0.0).toLong()))
        }

        override fun onError(speedTestError: SpeedTestError?, errorMessage: String?) {
            result(0)
        }

    }


}