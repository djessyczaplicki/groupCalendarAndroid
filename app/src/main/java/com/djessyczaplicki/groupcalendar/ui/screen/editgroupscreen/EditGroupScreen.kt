package com.djessyczaplicki.groupcalendar.ui.screen.editgroupscreen

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.djessyczaplicki.groupcalendar.R
import com.djessyczaplicki.groupcalendar.data.remote.model.Group
import com.djessyczaplicki.groupcalendar.ui.item.GroupUserRow

@Composable
fun EditGroupScreen(
    navController: NavController,
    editGroupViewModel: EditGroupViewModel
) {
    val context = LocalContext.current
    val isEditing = editGroupViewModel.isEditing.value
    val group = editGroupViewModel.group.value

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
            editGroupViewModel.group.value = Group()
            editGroupViewModel.loadGroup { group ->
                name = group.name
                description = group.description ?: ""
                image = group.image
                editGroupViewModel.loadUsers()
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

        Text(
            text = stringResource(id = R.string.members),
            modifier = Modifier.padding(10.dp)
        )

        LazyColumn(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp)
                .requiredHeightIn(max = 300.dp)
                .border(
                    BorderStroke(1.dp, colorResource(id = R.color.lighter_grey)),
                    shape = RectangleShape
                )
        ) {
            items(editGroupViewModel.users.value) { user ->

                Box {
                    GroupUserRow(user, group, editGroupViewModel)

                }
            }
        }



        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            if (name.isNotBlank()) {
                Button(
                    onClick = {
                        if (editGroupViewModel.isLoading) return@Button
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
                    }) {
                    Text(stringResource(id = if (isEditing) R.string.edit_group_screen else R.string.create_group))
                }
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