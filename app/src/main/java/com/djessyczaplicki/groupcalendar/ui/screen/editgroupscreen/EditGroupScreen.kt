package com.djessyczaplicki.groupcalendar.ui.screen.editgroupscreen

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.djessyczaplicki.groupcalendar.data.remote.model.Group
import com.djessyczaplicki.groupcalendar.R

@Composable
fun EditGroupScreen(
    navController: NavController,
    editGroupViewModel: EditGroupViewModel
) {
    Column(
        Modifier.verticalScroll(rememberScrollState())
    ) {
        val group = editGroupViewModel.group.value

        var name by remember { mutableStateOf(group.name) }
        var description by remember { mutableStateOf(group.description) }
        var image by remember { mutableStateOf(group.image) }

        Text(
            text = stringResource(id = if (!isEditing) R.string.add_event_screen else R.string.edit_event),
            modifier = Modifier
                .height(30.dp)
                .padding(4.dp),
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
        Image(

        )
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(id = R.string.name)) },
            placeholder = { Text(stringResource(id = R.string.group_name))},
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(4.dp)
        )
        OutlinedTextField(
            value = description ?: "",
            onValueChange = { description = it },
            label = { Text(stringResource(id = R.string.description)) },
            placeholder = { Text(stringResource(id = R.string.group_description))},
            singleLine = false,
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth()
                .padding(4.dp)
        )
    }
}

private fun openGallery() {
    val intent = Intent(Intent.ACTION_GET_CONTENT)
    intent.type = "image/*"
    startForActivityGallery.launch(intent)
}

private val startForActivityGallery = rememberLauncherForActivityResult(
    ActivityResultContracts.StartActivityForResult()
){ result ->

    if (result.resultCode == Activity.RESULT_OK) {
        val data = result.data?.data
        binding.ImageViewObject.setImageURI(data)
        UserCollections.ImageDataFromCollection = data
    }

}