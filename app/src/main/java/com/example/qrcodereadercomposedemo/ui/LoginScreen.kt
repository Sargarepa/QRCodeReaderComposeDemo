package com.example.qrcodereadercomposedemo.ui

import android.util.Log
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.qrcodereadercomposedemo.navigation.backHandler

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    launchSignInFlow: () -> Unit,
    popBackToWelcome: (() -> Unit)? = null,
    popBackToBarcodeScanner: (() -> Unit)? = null
) {
    val authenticationState by loginViewModel.authenticationState.observeAsState(initial = LoginViewModel.AuthenticationState.UNAUTHENTICATED)
    backHandler(onBack = { popBackToWelcome?.invoke() })
    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "You need to login first \nyou dumb bitch",
            style = MaterialTheme.typography.subtitle2,
            textAlign = TextAlign.Center
        )
        when (authenticationState) {
            LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                popBackToBarcodeScanner?.invoke()
            }
            LoginViewModel.AuthenticationState.UNAUTHENTICATED -> {
                LoginButton(onClick = {
                    launchSignInFlow()
                }) {
                    Text("Login")
                }
            }
            else -> {
                Log.d("Login: ", "Something went wrong")
            }
        }
    }
}

@Composable
fun LoginButton(
    modifier: Modifier = Modifier.padding(8.dp),
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    Button(modifier = modifier, onClick = onClick) {
        content()
    }
}