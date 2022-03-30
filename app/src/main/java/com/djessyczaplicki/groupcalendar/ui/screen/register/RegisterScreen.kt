package com.djessyczaplicki.groupcalendar.ui.screen.login

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.djessyczaplicki.groupcalendar.data.remote.model.User
import com.djessyczaplicki.groupcalendar.ui.screen.AppScreens

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    registerViewModel: RegisterViewModel
) {
    val context = LocalContext.current

    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var surname by rememberSaveable { mutableStateOf("") }
    var age by rememberSaveable { mutableStateOf("") }

    var passwordVisibility by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisibility by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .align(CenterHorizontally)
                    .background(Color.White)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = stringResource(id = R.string.app_name),
                    modifier = Modifier.fillMaxSize()
                )
                if (registerViewModel.isLoading().value)
                    CircularProgressIndicator(
                        modifier = Modifier
                            .width(50.dp)
                            .align(Alignment.TopCenter)
                            .padding(top = 30.dp),
                        strokeWidth = 5.dp
                    )
            }

            Spacer(modifier = Modifier.height(20.dp))

            RegisterTextField(
                value = email,
                label = stringResource(R.string.email),
                required = true,
                onValueChange = { email = it },
                keyboardType = KeyboardType.Email
            )
            Spacer(modifier = Modifier.height(20.dp))
            PasswordTextField(
                value = password,
                onValueChange = { password = it },
                label = stringResource(R.string.password),
                passwordVisibility = passwordVisibility,
                onVisibilityChanged = { passwordVisibility = !passwordVisibility }
            )
            Spacer(modifier = Modifier.height(20.dp))
            PasswordTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = stringResource(R.string.confirm_password),
                passwordVisibility = confirmPasswordVisibility,
                onVisibilityChanged = { confirmPasswordVisibility = !confirmPasswordVisibility }
            )
            Spacer(modifier = Modifier.height(20.dp))
            RegisterTextField(
                value = username,
                label = stringResource(R.string.username),
                required = true,
                onValueChange = { username = it }
            )
            Spacer(modifier = Modifier.height(20.dp))
            RegisterTextField(
                value = name,
                label = stringResource(R.string.name),
                onValueChange = { name = it }
            )
            Spacer(modifier = Modifier.height(20.dp))
            RegisterTextField(
                value = surname,
                label = stringResource(R.string.surname),
                onValueChange = { surname = it }
            )
            Spacer(modifier = Modifier.height(20.dp))
            RegisterTextField(
                value = age,
                label = stringResource(R.string.age),
                onValueChange = { age = it },
                keyboardType = KeyboardType.Number
            )
            Spacer(modifier = Modifier.height(40.dp))
            Button(
                modifier = Modifier
                    .height(50.dp)
                    .width(200.dp),
                onClick = {
                    registerViewModel.register(
                        user = User(name = name, surname = surname, age = age.toIntOrNull(), email = email, username = username),
                        password = password,
                        confirmPassword = confirmPassword,
                        context = context,
                        onSuccessCallback = {
                            navController.navigate(AppScreens.TimetableScreen.route)
                        },
                        onFailureCallback = {
                            Toast.makeText(context, it ?: context.getText(R.string.wrong_credentials), Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            ) {
                Text(text = stringResource(id = R.string.register))
            }
            Spacer(modifier = Modifier.height(100.dp))
        }


    }

}

@Composable
fun RegisterTextField(
    value: String,
    label: String,
    required: Boolean = false,
    onValueChange: (value: String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    var text = label
    if (required) {
        text += "*"
    }
    TextField(
        shape = AbsoluteRoundedCornerShape(10.dp, 10.dp),
        singleLine = true,
        value = value,
        onValueChange = { onValueChange(it) },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Next
        ),
        label = { Text(text) },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = colorResource(id = R.color.text_field_bg)
        )
    )
}


@Composable
fun PasswordTextField(
    value: String,
    label: String,
    required: Boolean = true,
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
            imeAction = ImeAction.Next
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
fun PreviewRegisterScreen() {
    MaterialTheme {
        RegisterScreen(
            navController = rememberNavController(),
            registerViewModel = RegisterViewModel()
        )
    }
}