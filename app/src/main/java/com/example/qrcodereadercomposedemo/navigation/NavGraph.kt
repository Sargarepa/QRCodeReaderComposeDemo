package com.example.qrcodereadercomposedemo.navigation

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class Destination : Parcelable {
    @Parcelize
    object Welcome : Destination()

    @Parcelize
    object Login : Destination()

    @Parcelize
    object BarcodeScanner : Destination()
}

/**
 * Models the navigation actions in the app.
 */
class Actions(navigator: Navigator<Destination>) {
    val popBackToDestination: (destination: Destination) -> Unit = { destination ->
        navigator.popBackStack(destination)
    }

    val navigateToLogin: () -> Unit = {
        navigator.navigate(Destination.Login)
    }
    val navigateToBarcodeScanner: () -> Unit = {
        navigator.navigate(Destination.BarcodeScanner)
    }
    val upPress: () -> Unit = {
        navigator.back()
    }
}