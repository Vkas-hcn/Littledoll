package com.dear.littledoll

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import com.dear.littledoll.bean.CountryBean
import com.dear.littledoll.databinding.ActivityLiverBinding
import com.dear.littledoll.utils.DataManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class LiverActivity : AppCompatActivity() {

    private val binding by lazy { ActivityLiverBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        CoroutineScope(Dispatchers.IO).launch {
            delay(1000)
            if (lifecycle.currentState == Lifecycle.State.RESUMED) startActivity(Intent(this@LiverActivity, MainActivity::class.java))
            finish()
        }

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        })
    }

}