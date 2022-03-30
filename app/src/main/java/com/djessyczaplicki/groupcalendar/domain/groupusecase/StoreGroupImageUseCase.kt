package com.djessyczaplicki.groupcalendar.domain.groupusecase

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class StoreGroupImageUseCase {
    operator fun invoke(groupId: String, groupImage: Bitmap, onSuccessCallback: (url: Uri) -> Unit) = storeGroupImage(groupId, groupImage, onSuccessCallback)
}

fun storeGroupImage(groupId: String, groupImage: Bitmap, onSuccessCallback: (url: Uri) -> Unit) {
    val TAG = "StoreG.@storeGroupImage"
    val storage = Firebase.storage
    val storageRef = storage.reference
    val groupImageRef = storageRef.child("groups/$groupId")

    val baos = ByteArrayOutputStream()
    groupImage.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val data = baos.toByteArray()

    Log.i(TAG, "Sending image")
    val uploadTask = groupImageRef.putBytes(data)

    uploadTask.continueWithTask { task ->
        if (!task.isSuccessful) {
            Log.i(TAG, "image sent!")
            task.exception?.let {
                throw it
            }
        } else {
            Log.e(TAG, task.exception.toString())
        }
        groupImageRef.downloadUrl
    }.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val downloadUri = task.result!!
            Log.i(TAG, "image url generated: $downloadUri")
            onSuccessCallback(downloadUri)
        }
    }
}