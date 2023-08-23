package com.example.serviceandroid.boundservice

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.core.os.bundleOf
import com.example.serviceandroid.MainActivity
import com.example.serviceandroid.model.ACTION_SONG

class SubBoundService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            (service as? BoundServiceApp.BoundBinder)?.getBoundService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("onServiceDisconnected", "onServiceDisconnected: ${name}")
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "startBoundService") {
            val actionSong =
                intent.extras?.getSerializable(MainActivity.RECEIVER_SONG_ACTION_KEY) as ACTION_SONG
            val intentNew = Intent(this, BoundServiceApp::class.java)
            unbindService(serviceConnection)
            val bundle = bundleOf(MainActivity.RECEIVER_SONG_ACTION_KEY to actionSong)
            intentNew.putExtras(bundle)
            bindService(intentNew, serviceConnection, BIND_AUTO_CREATE)
        }
        return super.onStartCommand(intent, flags, startId)
    }
}
