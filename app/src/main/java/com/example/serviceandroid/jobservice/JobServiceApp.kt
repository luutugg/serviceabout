package com.example.serviceandroid.jobservice

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.Operation
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkContinuation
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkQuery
import androidx.work.WorkRequest
import com.example.serviceandroid.R
import com.example.serviceandroid.ServiceApplication
import com.google.common.util.concurrent.ListenableFuture
import java.util.UUID

@SuppressLint("SpecifyJobSchedulerIdRange")
class JobServiceApp : JobService() {

    companion object {
        private var id = 0
    }

    private val TAG = "JobServiceApp"


    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        id++
        doWork(params)
//        jobFinished(params, true)
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }

    private fun doWork(params: JobParameters?) {
        val notification = NotificationCompat.Builder(this, ServiceApplication.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentText("jobservice")
            .setContentTitle("job")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .build()

        val notificationManager: NotificationManager =
            getSystemService(NotificationManager::class.java)
        notificationManager.notify(id, notification)

        Thread {
            for (i in 0 until 20){
                Log.d(TAG, "doWork: $i")
                Thread.sleep(1000)
            }
            jobFinished(params,true)
        }.start()
    }
}
