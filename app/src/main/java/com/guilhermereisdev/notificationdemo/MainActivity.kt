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
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput

class MainActivity : AppCompatActivity() {

    private val channelId = "com.guilhermereisdev.notificationdemo.channel1"
    private var notificationManager: NotificationManager? = null
    private val KEY_REPLY = "key_reply"
    private val notificationId = 45

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(channelId, "DemoChannel", "This is a demo channel")

        val button: Button = findViewById(R.id.button)
        button.setOnClickListener {
            displayNotification()
        }
        receiveInput()
    }

    private fun displayNotification() {
        // Ao clicar na notificação leva para a MainActivity
        val tapResultIntent = Intent(this, MainActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            tapResultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
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

        // Exibe um textInput na notificação para o usuário inserir alguma informação
        val remoteInput: RemoteInput = RemoteInput.Builder(KEY_REPLY).run {
            setLabel("Insira seu nome")
            build()
        }
        val replyAction: NotificationCompat.Action = NotificationCompat.Action.Builder(
            0, "Responder", pendingIntent
        ).addRemoteInput(remoteInput)
            .build()

        // Cria a notificação
        val notification = NotificationCompat.Builder(this@MainActivity, channelId)
            .setContentTitle("Demo title")
            .setContentText("This is a demo notification")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setContentIntent(pendingIntent)
            .addAction(actionDetails)
            .addAction(actionSettings)
            .addAction(replyAction)
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

    private fun receiveInput() {
        val intent = this.intent
        val remoteInput = RemoteInput.getResultsFromIntent(intent)
        if (remoteInput != null) {
            val inputString = remoteInput.getCharSequence(KEY_REPLY).toString()
            val tvInputFromNotification: TextView = findViewById(R.id.tvInputStringFromNotification)
            tvInputFromNotification.apply {
                text = inputString
            }
            // Cria a resposta para aparecer na própria notificação após ser inserida uma informação
            val repliedNotification = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentText("Resposta enviada com sucesso")
                .build()
            notificationManager?.notify(notificationId, repliedNotification)
        }
    }
}
