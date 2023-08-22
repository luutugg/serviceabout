package com.example.serviceandroid.forgroundservice

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.example.serviceandroid.BroadcastReceiverApp
import com.example.serviceandroid.MainActivity.Companion.RECEIVER_SONG_ACTION_KEY
import com.example.serviceandroid.MainActivity.Companion.SEND_SONG_ACTION_KEY
import com.example.serviceandroid.MainActivity.Companion.SONG_KEY
import com.example.serviceandroid.R
import com.example.serviceandroid.ServiceApplication
import com.example.serviceandroid.model.ACTION_SONG
import com.example.serviceandroid.model.SONG

class ForeGroundServiceApp : Service() {

    private var mediaPlayer: MediaPlayer? = null

    private var song: SONG = SONG.SONG_1

    private var actionSong: ACTION_SONG? = ACTION_SONG.PAUSE

    override fun onCreate() {
        super.onCreate()
//        mediaPlayer = MediaPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        Log.d("tunglvvvv", "onDestroy: ")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sendNotification(song)
        startMedia(song)

        actionSong = (intent?.getSerializableExtra(RECEIVER_SONG_ACTION_KEY) as? ACTION_SONG)
        handleMusic(actionSong)
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startMedia(song: SONG?) {
        mediaPlayer?.release()
        mediaPlayer = null

        when (song) {
            SONG.SONG_1 -> mediaPlayer = MediaPlayer.create(this, R.raw.file1)
            SONG.SONG_2 -> mediaPlayer = MediaPlayer.create(this, R.raw.file2)
            SONG.SONG_3 -> mediaPlayer = MediaPlayer.create(this, R.raw.file3)
            else -> {}
        }
        mediaPlayer?.start()

        mediaPlayer?.setOnCompletionListener {
            actionSong = ACTION_SONG.PAUSE
            sendNotification(song)
        }
    }

    private fun sendNotification(song: SONG?) {
        val titleSong = when (song) {
            SONG.SONG_1 -> "bài 1"
            SONG.SONG_2 -> "bài 2"
            SONG.SONG_3 -> "bài 3"
            else -> "ko co"
        }
        val remoteView = RemoteViews(this.packageName, R.layout.notification_item)
        remoteView.apply {
            setTextViewText(R.id.tvNotificationName, titleSong)
            when (actionSong) {
                ACTION_SONG.PAUSE -> {
                    setImageViewResource(
                        R.id.ivNotificationPlay,
                        R.drawable.ic_play
                    )
                }

                ACTION_SONG.PLAY -> setImageViewResource(
                    R.id.ivNotificationPlay,
                    R.drawable.ic_pause
                )

                ACTION_SONG.NEXT -> {
                    if (song?.value!! >= 3) {
                        setViewVisibility(R.id.ivNotificationNext, View.INVISIBLE)
                        setViewVisibility(R.id.ivNotificationPrev, View.VISIBLE)
                    }
                }

                ACTION_SONG.PREV -> {
                    if (song?.value!! <= 1) {
                        setViewVisibility(R.id.ivNotificationNext, View.VISIBLE)
                        setViewVisibility(R.id.ivNotificationPrev, View.INVISIBLE)
                    }
                }

                else -> {}
            }
            setOnClickPendingIntent(R.id.ivNotificationPlay, getPendingIntent(actionSong))
            setOnClickPendingIntent(R.id.ivNotificationPrev, getPendingIntent(ACTION_SONG.PREV))
            setOnClickPendingIntent(R.id.ivNotificationNext, getPendingIntent(ACTION_SONG.NEXT))
        }
        val notification = NotificationCompat.Builder(this, ServiceApplication.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setCustomContentView(remoteView)
            .build()

        val notificationManager : NotificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(1,notification)

  //      startForeground(1, notification)
    }

    private fun pauseMusic() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }

    private fun resumeMusic() {
        if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }

    private fun getPendingIntent(actionSong: ACTION_SONG?): PendingIntent {
        val intent = Intent(this, BroadcastReceiverApp::class.java)
        val bundle = bundleOf(SEND_SONG_ACTION_KEY to actionSong)
        intent.putExtras(bundle)
        return PendingIntent.getBroadcast(
            this,
            actionSong.hashCode(),
            intent,
            PendingIntent.FLAG_MUTABLE
        )
    }

    private fun prevMucic() {
        this.song = SONG.findSong(song.value - 1)
        startMedia(song)
    }

    private fun nextMucis() {
        this.song = SONG.findSong(song.value + 1)
        startMedia(song)
    }

    private fun handleMusic(actionSong: ACTION_SONG?) {
        this.actionSong = actionSong
        when (this.actionSong) {
            ACTION_SONG.PLAY -> resumeMusic()
            ACTION_SONG.PAUSE -> pauseMusic()
            ACTION_SONG.PREV -> prevMucic()
            ACTION_SONG.NEXT -> nextMucis()
            else -> {
                Log.d("TAG", "handleMusic: ")
            }
        }
        sendNotification(song)
    }
}
