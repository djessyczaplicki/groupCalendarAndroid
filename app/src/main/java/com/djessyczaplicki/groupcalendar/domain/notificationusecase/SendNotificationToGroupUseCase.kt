package com.djessyczaplicki.groupcalendar.domain.notificationusecase

import com.djessyczaplicki.groupcalendar.data.network.Repository
import com.djessyczaplicki.groupcalendar.data.remote.model.FCMTopicBody
import com.djessyczaplicki.groupcalendar.data.remote.model.Group
import com.djessyczaplicki.groupcalendar.data.remote.model.NotificationData
import com.djessyczaplicki.groupcalendar.data.remote.model.PushNotification
import javax.inject.Inject

class SendNotificationToGroupUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(notificationData: NotificationData, group: Group): FCMTopicBody? {
        val topic = "/topics/${group.id}"
        notificationData.title = "${group.name}: ${notificationData.title}"
        val pushNotification = PushNotification(notificationData, topic)
        return repository.sendNotification(pushNotification)
    }
}
