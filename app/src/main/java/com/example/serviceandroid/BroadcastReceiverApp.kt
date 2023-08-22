package com.example.serviceandroid

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.os.bundleOf
import com.example.serviceandroid.MainActivity.Companion.RECEIVER_SONG_ACTION_KEY
import com.example.serviceandroid.MainActivity.Companion.SEND_SONG_ACTION_KEY
import com.example.serviceandroid.forgroundservice.ForeGroundServiceApp
import com.example.serviceandroid.model.ACTION_SONG

class BroadcastReceiverApp : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        var actionSong = intent?.extras?.getSerializable(SEND_SONG_ACTION_KEY)
        if (actionSong == ACTION_SONG.PAUSE) {
            actionSong = ACTION_SONG.PLAY
        } else if (actionSong == ACTION_SONG.PLAY) {
            actionSong = ACTION_SONG.PAUSE
        }
        val intentNew = Intent(context, ForeGroundServiceApp::class.java)
        val bundle = bundleOf(RECEIVER_SONG_ACTION_KEY to actionSong)
        intentNew.putExtras(bundle)
        context?.startService(intentNew)
    }
}
