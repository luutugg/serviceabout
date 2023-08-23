package com.example.serviceandroid

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.core.os.bundleOf
import com.example.serviceandroid.MainActivity.Companion.RECEIVER_SONG_ACTION_KEY
import com.example.serviceandroid.MainActivity.Companion.SEND_SONG_ACTION_KEY
import com.example.serviceandroid.boundservice.BoundServiceApp
import com.example.serviceandroid.boundservice.SubBoundService
import com.example.serviceandroid.eventbus.EventBusManager
import com.example.serviceandroid.eventbus.event.SendToService
import com.example.serviceandroid.unboundservice.UnBoundServiceApp
import com.example.serviceandroid.model.ACTION_SONG

class BroadcastReceiverApp : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val actionSong = intent?.extras?.getSerializable(SEND_SONG_ACTION_KEY) as ACTION_SONG
        startBound(context, actionSong)
//        startUnBound(context, actionSong)
    }

    private fun startUnBound(context: Context?, actionSong: ACTION_SONG?) {
        val intentNew = Intent(context, UnBoundServiceApp::class.java)
        val bundle = bundleOf(RECEIVER_SONG_ACTION_KEY to actionSong)
        intentNew.putExtras(bundle)
        context?.startService(intentNew)
    }

    private fun startBound(context: Context?, actionSong: ACTION_SONG?) {
        EventBusManager.instance?.postPending(SendToService(actionSong))
    }
}
