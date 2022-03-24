package com.djessyczaplicki.groupcalendar.ui.screen.editgroupscreen

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.djessyczaplicki.groupcalendar.R
import com.djessyczaplicki.groupcalendar.data.remote.model.Group

@Composable
fun EditGroupScreen(
    navController: NavController,
    editGroupViewModel: EditGroupViewModel
) {
    val context = LocalContext.current
    val isEditing = editGroupViewModel.isEditing.value

    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var image by rememberSaveable { mutableStateOf(context.resources.getString(R.string.image_placeholder_url)) }
    var imageBitmap by rememberSaveable { mutableStateOf<Bitmap?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->
        imageBitmap = if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(
                context.contentResolver,
                imageUri
            )
        } else {
            val source = ImageDecoder.createSource(context.contentResolver, imageUri!!)
            ImageDecoder.decodeBitmap(source)
        }
    }


    // load group if groupId is set
    LaunchedEffect(key1 = editGroupViewModel.groupId) {
        if (editGroupViewModel.groupId != null) {
            editGroupViewModel.loadGroup { group ->
                name = group.name
                description = group.description ?: ""
                image = group.image
            }
        }
    }

    Column(
        Modifier.verticalScroll(rememberScrollState())
    ) {

        Text(
            text = stringResource(id = if (isEditing) R.string.edit_group_screen else R.string.create_group),
            modifier = Modifier
                .height(30.dp)
                .padding(4.dp),
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ){
            if (imageBitmap != null) {
                Image(
                    imageBitmap!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clickable {
                            launcher.launch("image/*")
                        }
                )
                if (editGroupViewModel.isLoading)
                    CircularProgressIndicator(
                        modifier = Modifier
                            .width(50.dp)
                            .padding(top = 30.dp),
                        strokeWidth = 5.dp
                    )
            } else {
                SubcomposeAsyncImage(
                    model = image,
                    loading = {
                        CircularProgressIndicator()
                    },
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clickable {
                            launcher.launch("image/*")
                        }
                )
            }
        }
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
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Button(
                onClick = {
                    if (!editGroupViewModel.isLoading) {
                        if (isEditing) {
                            val group = editGroupViewModel.group.value
                            group.name = name
                            group.description = description
                            editGroupViewModel.editGroup(group, imageBitmap) {
                                navController.popBackStack()
                            }
                        } else {
                            val newGroup = Group(
                                name = name,
                                description = description
                            )
                            editGroupViewModel.editGroup(newGroup, imageBitmap) {
                    //                             Doesn't work because the group isn't created yet when the app tries to load the view
                    //                                navController.navigate(AppScreens.TimetableScreen.route + "/${group.id}") {
                    //                                    popUpTo(0)
                    //                                }
                                navController.popBackStack()
                            }
                        }
                    }
                }) {
                Text(stringResource(id = if (isEditing) R.string.edit_group_screen else R.string.create_group))
            }
        }
    }
}

//private val startForActivityGallery = rememberLauncherForActivityResult(
//    ActivityResultContracts.StartActivityForResult()
//){ result ->
//
//    if (result.resultCode == Activity.RESULT_OK) {
//        val data = result.data?.data
//        binding.ImageViewObject.setImageURI(data)
//        UserCollections.ImageDataFromCollection = data
//    }
//
//}