package com.djessyczaplicki.groupcalendar.ui.screen.login

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.djessyczaplicki.groupcalendar.R
import com.djessyczaplicki.groupcalendar.ui.screen.AppScreens

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    intent: Intent
) {
    val TAG = "LoginScreen"
    val context = LocalContext.current


    LaunchedEffect("login_test") {
        loginViewModel.testAuth(context) {
            loginViewModel.success(intent, navController)
        }
    }

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisibility by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current

    fun login() {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(
                context,
                context.getString(R.string.error_email_password),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        loginViewModel.login(
            email,
            password,
            context,
            onSuccessCallback = {
                loginViewModel.success(intent, navController)
            },
            onFailureCallback = {
                Toast.makeText(
                    context,
                    it ?: context.getText(R.string.wrong_credentials),
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    Surface(
        color = MaterialTheme.colorScheme.background,
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
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .align(CenterHorizontally)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_calendar),
                    contentDescription = stringResource(id = R.string.app_name),
                    modifier = Modifier.fillMaxSize()
                )
                if (loginViewModel.isLoading().value) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .width(50.dp)
                            .align(Alignment.TopCenter)
                            .padding(top = 30.dp),
                        strokeWidth = 5.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(60.dp))

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
                onDone = {
                    keyboardController?.hide()
                    login()
                },
                onVisibilityChanged = { passwordVisibility = !passwordVisibility }
            )
            Spacer(modifier = Modifier.height(40.dp))
            ElevatedButton(
                modifier = Modifier
                    .height(50.dp)
                    .width(240.dp),
                onClick = {
                    login()
                }
            ) {
                Text(text = stringResource(id = R.string.login))
            }

            Spacer(modifier = Modifier.height(20.dp))
            ElevatedButton(
                modifier = Modifier
                    .height(50.dp)
                    .width(240.dp),
                onClick = {
                    loginViewModel.loginWithGoogle(context)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.google_g_logo),
                    contentDescription = stringResource(id = R.string.login_with_google),
                    modifier = Modifier.height(25.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(id = R.string.login_with_google),
                    color = MaterialTheme.colorScheme.onError
                )
            }
            Spacer(modifier = Modifier.height(30.dp))

            Text(stringResource(R.string.or))
            Spacer(modifier = Modifier.height(30.dp))

            ElevatedButton(
                modifier = Modifier
                    .height(50.dp)
                    .width(240.dp),
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
    )
}

@Composable
fun LoginPasswordTextField(
    value: String,
    label: String,
    required: Boolean = false,
    onValueChange: (value: String) -> Unit,
    passwordVisibility: Boolean,
    onDone: () -> Unit = {},
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
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = {
            onDone()
        }),
        trailingIcon = {
            val image = if (passwordVisibility)
                Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff

            IconButton(onClick = { onVisibilityChanged() }) {
                Icon(imageVector = image, "")
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    MaterialTheme {
        LoginScreen(
            navController = rememberNavController(),
            loginViewModel = viewModel(),
            intent = Intent()
        )
    }
}
