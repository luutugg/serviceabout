package com.example.serviceandroid.unboundservice

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.example.serviceandroid.BroadcastReceiverApp
import com.example.serviceandroid.MainActivity.Companion.RECEIVER_SONG_ACTION_KEY
import com.example.serviceandroid.MainActivity.Companion.SEND_SONG_ACTION_KEY
import com.example.serviceandroid.R
import com.example.serviceandroid.ServiceApplication
import com.example.serviceandroid.eventbus.EventBusManager
import com.example.serviceandroid.eventbus.event.PostData
import com.example.serviceandroid.model.ACTION_SONG
import com.example.serviceandroid.model.SONG

class UnBoundServiceApp : Service() {

    private var mediaPlayer: MediaPlayer? = null

    private var song: SONG = SONG.SONG_1

    private var currentSong: SONG? = null

    private var actionSong: ACTION_SONG? = null

    private var handler: Handler? = null

    private var runnale: Runnable? = null

    override fun onCreate() {
        super.onCreate()
        Log.d("tunglvvv", "onCreate: ")
        actionSong = ACTION_SONG.PAUSE
        handler = Handler(Looper.myLooper()!!)
        mediaPlayer = MediaPlayer.create(this, R.raw.file1)
        startMedia(song)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("tunglvvv", "onDestroy: ")
        mediaPlayer?.release()
        mediaPlayer = null
        if (runnale != null) {
            handler?.removeCallbacks(runnale!!)
            runnale = null
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sendNotification(song)
        actionSong = (intent?.getSerializableExtra(RECEIVER_SONG_ACTION_KEY) as? ACTION_SONG)
        handleMusic(actionSong)
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startMedia(song: SONG?) {
        if (currentSong != song) {
            when (song) {
                SONG.SONG_1 -> {
                    mediaPlayer?.reset()
                    mediaPlayer?.setDataSource(
                        applicationContext,
                        Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.file1)
                    );
                    mediaPlayer?.prepare();
                }

                SONG.SONG_2 -> {
                    mediaPlayer?.reset()
                    mediaPlayer?.setDataSource(
                        applicationContext,
                        Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.file2)
                    );
                    mediaPlayer?.prepare();
                }

                SONG.SONG_3 -> {
                    mediaPlayer?.reset()
                    mediaPlayer?.setDataSource(
                        applicationContext,
                        Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.file3)
                    );
                    mediaPlayer?.prepare();
                }

                else -> {}
            }
            currentSong = song
        }
        mediaPlayer?.setOnPreparedListener {
            runnale = object : Runnable {
                override fun run() {
                    EventBusManager.instance?.postPending(
                        PostData(
                            currentPosition = mediaPlayer!!.currentPosition,
                            maxPosition = mediaPlayer!!.duration
                        )
                    );
                    handler?.postDelayed(this, 1000)
                }
            }
            handler?.postDelayed(runnale!!, 1000)
        }
        mediaPlayer?.start()
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
                        R.drawable.ic_pause
                    )
                    when {
                        song?.value!! >= 3 -> {
                            setViewVisibility(R.id.ivNotificationNext, View.INVISIBLE)
                            setViewVisibility(R.id.ivNotificationPrev, View.VISIBLE)
                        }

                        song.value <= 1 -> {
                            setViewVisibility(R.id.ivNotificationPrev, View.INVISIBLE)
                            setViewVisibility(R.id.ivNotificationNext, View.VISIBLE)
                        }

                        else -> {
                            setViewVisibility(R.id.ivNotificationPrev, View.VISIBLE)
                            setViewVisibility(R.id.ivNotificationNext, View.VISIBLE)
                        }
                    }
                }

                ACTION_SONG.PLAY -> {
                    setImageViewResource(
                        R.id.ivNotificationPlay,
                        R.drawable.ic_play
                    )
                    when {
                        song?.value!! >= 3 -> {
                            setViewVisibility(R.id.ivNotificationNext, View.INVISIBLE)
                            setViewVisibility(R.id.ivNotificationPrev, View.VISIBLE)
                        }

                        song.value <= 1 -> {
                            setViewVisibility(R.id.ivNotificationPrev, View.INVISIBLE)
                            setViewVisibility(R.id.ivNotificationNext, View.VISIBLE)
                        }

                        else -> {
                            setViewVisibility(R.id.ivNotificationPrev, View.VISIBLE)
                            setViewVisibility(R.id.ivNotificationNext, View.VISIBLE)
                        }
                    }
                }

                ACTION_SONG.NEXT -> {
                    if (song?.value!! >= 3) {
                        setViewVisibility(R.id.ivNotificationNext, View.INVISIBLE)
                    } else {
                        setViewVisibility(R.id.ivNotificationNext, View.VISIBLE)
                    }
                    setViewVisibility(R.id.ivNotificationPrev, View.VISIBLE)
                }

                ACTION_SONG.PREV -> {
                    if (song?.value!! <= 1) {
                        setViewVisibility(R.id.ivNotificationPrev, View.INVISIBLE)
                    } else {
                        setViewVisibility(R.id.ivNotificationPrev, View.VISIBLE)
                    }
                    setViewVisibility(R.id.ivNotificationNext, View.VISIBLE)
                }

                else -> {
                    when (song) {
                        SONG.SONG_1 -> {
                            setViewVisibility(R.id.ivNotificationPrev, View.INVISIBLE)
                            setViewVisibility(R.id.ivNotificationNext, View.VISIBLE)
                        }

                        SONG.SONG_2 -> {
                            setViewVisibility(R.id.ivNotificationPrev, View.VISIBLE)
                            setViewVisibility(R.id.ivNotificationNext, View.VISIBLE)
                        }

                        SONG.SONG_3 -> {
                            setViewVisibility(R.id.ivNotificationPrev, View.VISIBLE)
                            setViewVisibility(R.id.ivNotificationNext, View.INVISIBLE)
                        }

                        else -> {
                        }
                    }
                }
            }
            val action = if (mediaPlayer?.isPlaying == true) {
                ACTION_SONG.PAUSE
            } else {
                ACTION_SONG.PLAY
            }
            setOnClickPendingIntent(R.id.ivNotificationPlay, getPendingIntent(action))
            setOnClickPendingIntent(R.id.ivNotificationPrev, getPendingIntent(ACTION_SONG.PREV))
            setOnClickPendingIntent(R.id.ivNotificationNext, getPendingIntent(ACTION_SONG.NEXT))
        }
        val notification = NotificationCompat.Builder(this, ServiceApplication.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setCustomContentView(remoteView)
            .build()

        val notificationManager: NotificationManager =
            getSystemService(NotificationManager::class.java)
        notificationManager.notify(1, notification)

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
        intent.action = actionSong.toString()
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
        when (actionSong) {
            ACTION_SONG.PLAY -> {
                resumeMusic()
                this.actionSong = ACTION_SONG.PAUSE
            }

            ACTION_SONG.PAUSE -> {
                pauseMusic()
                this.actionSong = ACTION_SONG.PLAY
            }

            ACTION_SONG.PREV -> {
                this.actionSong = ACTION_SONG.PREV
                prevMucic()
            }

            ACTION_SONG.NEXT -> {
                nextMucis()
                this.actionSong = ACTION_SONG.NEXT
            }

            else -> {
                Log.d("TAG", "handleMusic: ")
            }
        }
        sendNotification(song)
    }
}
