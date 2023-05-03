package com.guilhermereisdev.notificationdemo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.app.NotificationCompat

class MainActivity : AppCompatActivity() {

    private val channelId = "com.guilhermereisdev.notificationdemo.channel1"
    private var notificationManager: NotificationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(channelId, "DemoChannel", "This is a demo channel")

        val button: Button = findViewById(R.id.button)
        button.setOnClickListener {
            displayNotification()
        }
    }

    private fun displayNotification() {
        val notificationId = 45

        // Ao clicar na notificação leva para a MainActivity
        val tapResultIntent = Intent(this, MainActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            tapResultIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Botão "Details" na notificação que leva para a DetailsActivity
        val tapResultIntentDetails = Intent(this, DetailsActivity::class.java)
        val pendingIntentDetails: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            tapResultIntentDetails,
            PendingIntent.FLAG_IMMUTABLE
        )
        val actionDetails: NotificationCompat.Action =
            NotificationCompat.Action.Builder(0, "Details", pendingIntentDetails).build()

        // Botão "Settings" na notificação que leva para a SettingsActivity
        val tapResultIntentSettings = Intent(this, SettingsActivity::class.java)
        val pendingIntentSettings: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            tapResultIntentSettings,
            PendingIntent.FLAG_IMMUTABLE
        )
        val actionSettings: NotificationCompat.Action =
            NotificationCompat.Action.Builder(0, "Settings", pendingIntentSettings).build()

        // Cria a notificação
        val notification = NotificationCompat.Builder(this@MainActivity, channelId)
            .setContentTitle("Demo title")
            .setContentText("This is a demo notification")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .addAction(actionDetails)
            .addAction(actionSettings)
            .build()
        notificationManager?.notify(notificationId, notification)
    }

    private fun createNotificationChannel(
        id: String,
        name: String,
        channelDescription: String,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(id, name, importance).apply {
                description = channelDescription
            }
            notificationManager?.createNotificationChannel(channel)
        }
    }
}
