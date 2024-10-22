package com.dear.littledoll

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dear.littledoll.databinding.ActivitySettingBinding


class SettingActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySettingBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.back.setOnClickListener { finish() }
        binding.share.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=${packageName}")
            startActivity(Intent.createChooser(shareIntent, "Share"))
        }
        binding.policy.setOnClickListener { startActivity(Intent(this, PolicyActivity::class.java)) }
    }
}