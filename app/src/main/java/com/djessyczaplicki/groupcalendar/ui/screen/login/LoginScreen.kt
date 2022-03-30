package com.djessyczaplicki.groupcalendar.ui.screen.login

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.djessyczaplicki.groupcalendar.R
import com.djessyczaplicki.groupcalendar.ui.screen.AppScreens
import com.orhanobut.logger.Logger

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen (
    navController: NavController,
    loginViewModel: LoginViewModel,
    intent: Intent
) {
    val context = LocalContext.current

    fun success() {
        if (!intent.getStringExtra("invite").isNullOrBlank()) {
            val groupId = intent.getStringExtra("invite")!!
            Logger.i("invite: $groupId")
            navController.navigate(AppScreens.InviteScreen.route + "/$groupId"){
                popUpTo(0)
            }
        } else if (!intent.getStringExtra("group_id").isNullOrBlank()){
            val groupId = intent.getStringExtra("group_id")!!
            Logger.i(groupId)
            navController.navigate(AppScreens.TimetableScreen.route + "/$groupId"){
                popUpTo(0)
            }
        } else {
            loginViewModel.loadUserGroups{ groupId ->
                navController.navigate(AppScreens.TimetableScreen.route + "/$groupId}"){
                    popUpTo(0)
                }
            }

        }

    }
    LaunchedEffect("login_test") {
        loginViewModel.testAuth(context){
            success()
        }
    }

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisibility by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .align(CenterHorizontally)
                    .background(Color.White)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = stringResource(id = R.string.app_name),
                    modifier = Modifier.fillMaxSize()
                )
                if (loginViewModel.isLoading().value)
                    CircularProgressIndicator(
                        modifier = Modifier
                            .width(50.dp)
                            .align(Alignment.TopCenter)
                            .padding(top = 30.dp),
                        strokeWidth = 5.dp
                    )
            }

            Spacer(modifier = Modifier.height(20.dp))

            LoginTextField(
                value = email,
                onValueChange = { email = it },
                label = stringResource(R.string.email)
            )
            Spacer(modifier = Modifier.height(20.dp))
            LoginPasswordTextField(
                value = password,
                onValueChange = { password = it },
                label = stringResource(R.string.password),
                passwordVisibility = passwordVisibility,
                onVisibilityChanged = { passwordVisibility = !passwordVisibility }
            )
            Spacer(modifier = Modifier.height(40.dp))
            Button(
                modifier = Modifier
                    .height(50.dp)
                    .width(200.dp),
                onClick = {
                    loginViewModel.login(
                        email,
                        password,
                        context,
                        onSuccessCallback = {
                            success()
                        },
                        onFailureCallback = {
                            Toast.makeText(context, it ?: context.getText(R.string.wrong_credentials), Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            ) {
                Text(text = stringResource(id = R.string.login))
            }
            Spacer(modifier = Modifier.height(30.dp))

            Text(stringResource(R.string.or))
            Spacer(modifier = Modifier.height(30.dp))

            Button(
                modifier = Modifier
                    .height(50.dp)
                    .width(200.dp),
                onClick = {
                    navController.navigate(AppScreens.RegisterScreen.route)
                }
            ) {
                Text(text = stringResource(id = R.string.register))
            }
            Spacer(modifier = Modifier.height(40.dp))

        }


    }

}

@Composable
fun LoginTextField(
    value: String,
    label: String,
    onValueChange: (value: String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Email
) {
    TextField(
        shape = AbsoluteRoundedCornerShape(10.dp, 10.dp),
        singleLine = true,
        value = value,
        onValueChange = { onValueChange(it) },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Next
        ),
        label = { Text(label) },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = colorResource(id = R.color.text_field_bg)
        )
    )
}

@Composable
fun LoginPasswordTextField(
    value: String,
    label: String,
    required: Boolean = false,
    onValueChange: (value: String) -> Unit,
    passwordVisibility: Boolean,
    onVisibilityChanged: () -> Unit
) {
    var text = label
    if (required) {
        text += "*"
    }
    TextField(
        shape = AbsoluteRoundedCornerShape(10.dp, 10.dp),
        value = value,
        onValueChange = { onValueChange(it) },
        label = { Text(text) },
        singleLine = true,
        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Go
        ),
        trailingIcon = {
            val image = if (passwordVisibility)
                Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff

            IconButton(onClick = { onVisibilityChanged() }) {
                Icon(imageVector = image, "")
            }
        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = colorResource(id = R.color.text_field_bg)
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    MaterialTheme {
        LoginScreen(
            navController = rememberNavController(),
            loginViewModel = LoginViewModel(),
            intent = Intent()
        )
    }
}