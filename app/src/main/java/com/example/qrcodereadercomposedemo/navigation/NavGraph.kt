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
    val login: () -> Unit = {
        navigator.navigate(Destination.BarcodeScanner)
    }
    val upPress: () -> Unit = {
        navigator.back()
    }
}