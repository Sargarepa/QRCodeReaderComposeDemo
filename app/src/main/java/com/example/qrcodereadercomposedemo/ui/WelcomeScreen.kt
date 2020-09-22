package com.example.qrcodereadercomposedemo.ui

import android.util.Log
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.ContextAmbient
import com.firebase.ui.auth.AuthUI

@Composable
fun WelcomeScreen(
    loginViewModel: LoginViewModel,
    launchSignInFlow: () -> Unit,
    navigateToBarCodeScanner: () -> Unit
) {
    val context = ContextAmbient.current
    val authenticationState by loginViewModel.authenticationState.observeAsState(initial = LoginViewModel.AuthenticationState.UNAUTHENTICATED)
    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to QR scanner app",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(top = 24.dp)
        )
        when (authenticationState) {
            LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                LoginButton(onClick = {
                    AuthUI.getInstance().signOut(context)
                }) {
                    Text("Logout")
                }
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
        Button(
            onClick = { navigateToBarCodeScanner() },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Open Barcode Scanner")
        }
    }
}

