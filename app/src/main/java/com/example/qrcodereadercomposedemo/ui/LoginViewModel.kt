package com.example.qrcodereadercomposedemo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.qrcodereadercomposedemo.livedata.FirebaseUserLiveData
import org.koin.core.KoinComponent

class LoginViewModel : ViewModel(), KoinComponent {

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
    }

    val authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }

}