package com.djessyczaplicki.groupcalendar.core

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.djessyczaplicki.groupcalendar.R
import com.djessyczaplicki.groupcalendar.ui.view.MainActivity

class AlarmReceiver : BroadcastReceiver() {
    @Override
    override fun onReceive(context: Context, intent: Intent) {
        val notificationChannelId = context.getString(R.string.default_notification_channel_id)

        val newIntent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        addExtras(intent, newIntent)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val groupName = intent.getStringExtra("group_name")
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")

        val builder = NotificationCompat.Builder(context, notificationChannelId)
            .setSmallIcon(R.drawable.ic_calendar)
            .setContentTitle("$title ($groupName)")
            .setContentText(description)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)

        val notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat.notify(123, builder.build())
    }

    private fun addExtras(intent: Intent, newIntent: Intent) {
        if (!intent.getStringExtra("group_id").isNullOrBlank()) {
            val groupId = intent.getStringExtra("group_id")
            newIntent.putExtra("group_id", groupId)
        }
        if (!intent.getStringExtra("event_id").isNullOrBlank()) {
            val eventId = intent.getStringExtra("event_id")
            newIntent.putExtra("event_id", eventId)
        }
    }
}
