package com.example.serviceandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.os.bundleOf
import com.example.serviceandroid.databinding.ActivityMainBinding
import com.example.serviceandroid.forgroundservice.ForeGroundServiceApp
import com.example.serviceandroid.model.SONG

class MainActivity : AppCompatActivity() {

    companion object {
        const val SONG_KEY = "SONG_KEY"
        const val SEND_SONG_ACTION_KEY = "SEND_SONG_ACTION_KEY"
        const val RECEIVER_SONG_ACTION_KEY = "RECEIVER_SONG_ACTION_KEY"
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnMainForeGround.setOnClickListener {
            startWithForeGroundService()
        }
    }

    private fun startWithForeGroundService() {
        val intent = Intent(this, ForeGroundServiceApp::class.java)
        startService(intent)
    }
}
