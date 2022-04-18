package com.djessyczaplicki.groupcalendar.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.djessyczaplicki.groupcalendar.GroupCalendarApp
import com.djessyczaplicki.groupcalendar.R
import com.djessyczaplicki.groupcalendar.ui.view.MainActivity

fun createNotificationChannel() {
    val context = GroupCalendarApp.applicationContext()

    val notificationChannelId = context.getString(R.string.default_notification_channel_id)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            notificationChannelId,
            context.getString(R.string.default_notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        )

        notificationChannel.description =
            context.getString(R.string.default_notification_channel_description)
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.vibrationPattern = longArrayOf(0, 100, 200, 100)
        notificationChannel.enableVibration(true)
        notificationChannel.canBypassDnd()

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }
}

fun simpleNotificationWithTapAction(
    context: Context,
    channelId: String,
    notificationId: Int,
    textTitle: String,
    textContent: String,
    priority: Int = NotificationCompat.PRIORITY_DEFAULT
) {
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
//    intent.putExtra("screen", AppScreens.OutputTrayScreen.route)
    val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

    val builder = NotificationCompat.Builder(context, channelId)
//        .setSmallIcon(R.drawable.logo_min)
        .setContentTitle(textTitle)
        .setContentText(textContent)
        .setPriority(priority)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
    with(NotificationManagerCompat.from(context)) {
        notify(notificationId, builder.build())
    }
}
