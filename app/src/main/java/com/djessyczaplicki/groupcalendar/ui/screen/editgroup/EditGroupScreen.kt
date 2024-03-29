package com.djessyczaplicki.groupcalendar.ui.screen.editgroup

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.djessyczaplicki.groupcalendar.R
import com.djessyczaplicki.groupcalendar.data.remote.model.Group
import com.djessyczaplicki.groupcalendar.ui.item.TopBar
import com.djessyczaplicki.groupcalendar.ui.screen.AppScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGroupScreen(
    navController: NavController,
    editGroupViewModel: EditGroupViewModel
) {
    val context = LocalContext.current
    val isEditing = editGroupViewModel.isEditing.value
    Scaffold(
        topBar = {
            TopBar(
                title = stringResource(if (isEditing) R.string.edit_group_screen else R.string.create_group),
                onLeftButtonClicked = {
                    navController.popBackStack()
                },
                onRightButtonClicked = {
                    if (isEditing) {
                        editGroupViewModel.sendInviteLink(context)
                    }
                },
                rightButtonIcon = if (isEditing) Icons.Filled.GroupAdd else null
            )
        },
        content = { padding ->
            EditGroupScreenContent(
                navController,
                editGroupViewModel,
                Modifier.padding(top = padding.calculateTopPadding()),
            )
        },
        floatingActionButton = {
            EditGroupScreenFAB(
                navController,
                editGroupViewModel
            )
        },
        floatingActionButtonPosition = FabPosition.End
    )

}

@Composable
fun EditGroupScreenContent(
    navController: NavController,
    editGroupViewModel: EditGroupViewModel,
    modifier: Modifier = Modifier
) {
    val isEditing = editGroupViewModel.isEditing.value
    val group = editGroupViewModel.group.value

    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var image by rememberSaveable { mutableStateOf("") }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->
            if (imageUri != null) {
                image = imageUri.toString()
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

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(start = 10.dp, end = 10.dp, top = 10.dp)
        ) {
            Spacer(modifier = Modifier.height(5.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(image.ifEmpty { stringResource(id = R.string.image_placeholder_url) })
                            .crossfade(true)
                            .transformations(CircleCropTransformation())
                            .build(),
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
                    if (editGroupViewModel.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .width(80.dp)
                                .padding(top = 30.dp, start = 10.dp),
                            strokeWidth = 5.dp
                        )
                    }
                }

            }
            Spacer(Modifier.height(20.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(id = R.string.name)) },
                placeholder = { Text(stringResource(id = R.string.group_name)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(4.dp)
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(id = R.string.description)) },
                placeholder = { Text(stringResource(id = R.string.group_description)) },
                singleLine = false,
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
                    .padding(4.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                Button(
                    enabled = name.isNotBlank(),
                    onClick = {
                        if (editGroupViewModel.isLoading) return@Button
                        if (isEditing) {
                            group.name = name
                            group.description = description
                            editGroupViewModel.editGroup(group, image) {
                                navController.popBackStack()
                            }
                        } else {
                            val newGroup = Group(
                                name = name,
                                description = description
                            )
                            editGroupViewModel.editGroup(newGroup, image) {
                                navController.navigate(AppScreens.TimetableScreen.route + "/${group.id}") {
                                    popUpTo(0)
                                }
                            }
                        }
                    }) {
                    Text(
                        stringResource(id = if (isEditing) R.string.save_changes else R.string.create_group),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            if (editGroupViewModel.users.value.isNotEmpty()) {
                Text(
                    text = stringResource(id = R.string.members),
                    modifier = Modifier.padding(10.dp)
                )
            }

            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp)
                    .requiredHeightIn(max = 300.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shape = RectangleShape
                    )
            ) {
                items(editGroupViewModel.users.value) { user ->
                    Box {
                        GroupUserRow(user, group, editGroupViewModel)
                    }

                }

            }
            Spacer(modifier = modifier.height(1.dp))
            if (isEditing) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            navController.navigate(AppScreens.SendNotificationScreen.route + "/${group.id}");
                        }
                    ) {
                        Text("Notificar usuarios")
                    }
                }
            }
        }
    }
}

@Composable
fun EditGroupScreenFAB(
    navController: NavController,
    editGroupViewModel: EditGroupViewModel
) {
    val isEditing = editGroupViewModel.isEditing.value
    if (isEditing) {
        val group = editGroupViewModel.group.value
        var isDialogShown by remember { mutableStateOf(false) }
        FloatingActionButton(
            onClick = {
                isDialogShown = true
            },
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        ) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Delete group",
            )
        }
        if (isDialogShown) {
            AlertDialog(
                onDismissRequest = {
                    isDialogShown = false
                },
                title = {
                    Text(stringResource(id = R.string.delete_group))
                },
                text = {
                    Text(getDeleteGroupText(group))
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            editGroupViewModel.deleteGroup {
                                navController.popBackStack()
                            }
                        }
                    ) {
                        Text(stringResource(id = R.string.delete))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            isDialogShown = false
                        }
                    ) {
                        Text(stringResource(id = R.string.cancel))
                    }
                }
            )
        }
    }
}

@Composable
fun getDeleteGroupText(group: Group): AnnotatedString {
    val text = stringResource(R.string.delete_group_text).split("{0}")
    val annotatedString = buildAnnotatedString {
        append(text[0])
        withStyle(
            style = SpanStyle(fontWeight = FontWeight.Bold)
        ) {
            if (group.description.isNullOrBlank()) {
                append(group.name)
            } else {
                append("${group.name}: ${group.description}")
            }
        }
        append(text[1])
    }
    return annotatedString
}

@Preview(showBackground = true)
@Composable
fun EditGroupScreenPreview() {
    EditGroupScreen(
        rememberNavController(),
        viewModel()
    )
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
