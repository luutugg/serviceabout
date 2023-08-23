package com.example.serviceandroid

import android.app.NotificationManager
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.example.serviceandroid.boundservice.BoundServiceApp
import com.example.serviceandroid.databinding.ActivityMainBinding
import com.example.serviceandroid.eventbus.EventBusManager
import com.example.serviceandroid.eventbus.IEvent
import com.example.serviceandroid.eventbus.IEventHandler
import com.example.serviceandroid.eventbus.event.PostData
import com.example.serviceandroid.eventbus.event.SendToService
import com.example.serviceandroid.unboundservice.UnBoundServiceApp
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainActivity : AppCompatActivity(), IEventHandler {

    companion object {
        const val SONG_KEY = "SONG_KEY"
        const val SEND_SONG_ACTION_KEY = "SEND_SONG_ACTION_KEY"
        const val RECEIVER_SONG_ACTION_KEY = "RECEIVER_SONG_ACTION_KEY"
    }

    private lateinit var binding: ActivityMainBinding

    private var startFore: Boolean = false

    private var isConnectedBound = false

    private var startBound = false

    private var serviceBound: BoundServiceApp? = null

    private var serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            serviceBound = (service as? BoundServiceApp.BoundBinder)?.getBoundService()
            serviceBound?.listener = object : BoundServiceApp.IBoundServiceListener {
                override fun onProgress(current: Int, max: Int) {
                    if (current != 0) {
                        binding.sbMain.progress = current
                    }
                    binding.sbMain.max = max
                    isConnectedBound = true
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isConnectedBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnMainForeGround.setOnClickListener {
            startWithForeGroundService()
        }

        binding.btnMainBound.setOnClickListener {
            startBoundService()
        }
    }

    override fun onStart() {
        super.onStart()
        EventBusManager.instance?.register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBusManager.instance?.unregister(this)
    }


    private fun startWithForeGroundService() {
        startFore = !startFore
        val intent = Intent(this, UnBoundServiceApp::class.java)
        if (startFore) {
            startService(intent)
            binding.btnMainForeGround.text = "stop foreground"
        } else {
            binding.btnMainForeGround.text = "foreground"
            stopService(intent)
            binding.sbMain.progress = 0
            removeNotify()
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    override fun onEvent(event: IEvent) {
        when (event) {
            is PostData -> {
                if (event.currentPosition != 0) {
                    binding.sbMain.progress = event.currentPosition
                }
                binding.sbMain.max = event.maxPosition
                EventBusManager.instance?.removeSticky(event)
            }

            is SendToService -> {
                serviceBound?.handleMusic(event.actionSong)
                EventBusManager.instance?.removeSticky(event)
            }
        }
    }

    private fun removeNotify() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    private fun startBoundService() {
        startBound = !startBound
        val intent = Intent(this, BoundServiceApp::class.java)
        if (startBound) {
            bindService(intent, serviceConnection, BIND_AUTO_CREATE)
            binding.btnMainBound.text = "stop bound"
        } else {
            unbindService(serviceConnection)
            isConnectedBound = false
            binding.btnMainBound.text = "bound service"
            binding.sbMain.progress = 0
            removeNotify()
        }
    }
}
