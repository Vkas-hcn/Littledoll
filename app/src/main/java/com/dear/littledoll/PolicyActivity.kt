package com.dear.littledoll

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.dear.littledoll.databinding.ActivityPolicyBinding

class PolicyActivity : AppCompatActivity() {
    private val binding by lazy { ActivityPolicyBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.back.setOnClickListener { finish() }
        binding.web.webViewClient = WebViewClient()
        binding.web.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                binding.progress.progress = newProgress
                if (newProgress >= 100) binding.progress.visibility = View.GONE
            }
        }
        binding.web.loadUrl("https://www.baidu.com")
    }
}