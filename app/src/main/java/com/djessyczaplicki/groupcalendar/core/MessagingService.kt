package com.djessyczaplicki.groupcalendar.core

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.djessyczaplicki.groupcalendar.GroupCalendarApp
import com.djessyczaplicki.groupcalendar.R
import com.djessyczaplicki.groupcalendar.ui.view.MainActivity
import com.djessyczaplicki.groupcalendar.util.UserPreferences
import com.djessyczaplicki.groupcalendar.util.createNotificationChannel
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


open class MessagingService : FirebaseMessagingService() {
    val NOTIFICATION_CHANNEL_ID =
        GroupCalendarApp.applicationContext().getString(R.string.default_notification_channel_id)

    override fun onNewToken(token: String) {
        Log.e("NEW_TOKEN", token);

        registerNewToken(token)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun registerNewToken(token: String) {
        val context: Context = this
        GlobalScope.launch(Dispatchers.IO) {
            UserPreferences(context).saveFCMToken(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.notification?.let {
            sendNotification(it)
        }
    }

    private fun sendNotification(notification: RemoteMessage.Notification) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_calendar)
            .setContentTitle(notification.title)
            .setContentText(notification.body)
//            .setDefaults(Notification.DEFAULT_ALL)
            .setSound(defaultSound)
//            .setWhen(System.currentTimeMillis())
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel()

        notificationManager.notify(0, notificationBuilder.build())
    }


}
