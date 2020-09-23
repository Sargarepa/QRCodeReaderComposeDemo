package com.example.qrcodereadercomposedemo.ui

import androidx.activity.OnBackPressedDispatcher
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.remember
import androidx.compose.runtime.savedinstancestate.rememberSavedInstanceState
import com.example.qrcodereadercomposedemo.navigation.Actions
import com.example.qrcodereadercomposedemo.navigation.BackDispatcherAmbient
import com.example.qrcodereadercomposedemo.navigation.Destination
import com.example.qrcodereadercomposedemo.navigation.Navigator
import com.example.qrcodereadercomposedemo.ui.camera.BarcodeScannerScreen
import com.example.qrcodereadercomposedemo.ui.camera.BarcodeViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Composable
fun QRApp(
    backDispatcher: OnBackPressedDispatcher,
    barcodeViewModel: BarcodeViewModel,
    loginViewModel: LoginViewModel,
    launchSignInFlow: () -> Unit
) {
    val navigator: Navigator<Destination> = rememberSavedInstanceState(
        saver = Navigator.saver(backDispatcher)
    ) {
        Navigator(Destination.Welcome, backDispatcher)
    }
    val actions = remember(navigator) { Actions(navigator) }

    Providers(BackDispatcherAmbient provides backDispatcher) {
        Crossfade(navigator.current) { destination ->
            when (destination) {
                Destination.Welcome -> WelcomeScreen(
                    loginViewModel,
                    launchSignInFlow,
                    actions.navigateToBarcodeScanner
                )
                Destination.Login -> LoginScreen(
                    loginViewModel,
                    launchSignInFlow,
                    { actions.popBackToDestination(Destination.Welcome) },
                    { actions.popBackToDestination(Destination.BarcodeScanner)})
                Destination.BarcodeScanner -> BarcodeScannerScreen(
                    barcodeViewModel,
                    loginViewModel,
                    actions.navigateToLogin
                )
            }
        }
    }
}