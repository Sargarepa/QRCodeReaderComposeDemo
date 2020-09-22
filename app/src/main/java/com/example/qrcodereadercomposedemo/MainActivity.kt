package com.example.qrcodereadercomposedemo


import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.remember
import androidx.compose.runtime.savedinstancestate.rememberSavedInstanceState
import androidx.compose.ui.platform.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.qrcodereadercomposedemo.navigation.Actions
import com.example.qrcodereadercomposedemo.navigation.BackDispatcherAmbient
import com.example.qrcodereadercomposedemo.navigation.Destination
import com.example.qrcodereadercomposedemo.navigation.Navigator
import com.example.qrcodereadercomposedemo.ui.LoginScreen
import com.example.qrcodereadercomposedemo.ui.LoginViewModel
import com.example.qrcodereadercomposedemo.ui.QRCodeReaderComposeDemoTheme
import com.example.qrcodereadercomposedemo.ui.WelcomeScreen
import com.example.qrcodereadercomposedemo.ui.camera.BarcodeScannerScreen
import com.example.qrcodereadercomposedemo.ui.camera.BarcodeViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val barcodeViewModel: BarcodeViewModel by inject()
    private val loginViewModel: LoginViewModel by inject()

    private val loginResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val response = IdpResponse.fromResultIntent(result.data)
            if (result.resultCode == Activity.RESULT_OK) {
                Log.i(
                    TAG,
                    "Successfully signed in user ${FirebaseAuth.getInstance().currentUser?.displayName}!"
                )
            } else {
                Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
            }
        }

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        } else {
            setContent {
                QRCodeReaderComposeDemoTheme {
                    QRApp(
                        backDispatcher = onBackPressedDispatcher,
                        barcodeViewModel = barcodeViewModel,
                        loginViewModel = loginViewModel,
                        launchSignInFlow = launchSignInFlow
                    )
                }
            }
        }
    }

    private fun allPermissionsGranted(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    baseContext, permission
                ) != PackageManager.PERMISSION_GRANTED
            ) return false
        }
        return true
    }

    @ExperimentalCoroutinesApi
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                setContent {
                    QRCodeReaderComposeDemoTheme {
                        QRApp(
                            backDispatcher = onBackPressedDispatcher,
                            barcodeViewModel = barcodeViewModel,
                            loginViewModel = loginViewModel,
                            launchSignInFlow = launchSignInFlow
                        )
                    }
                }
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private val launchSignInFlow: () -> Unit = {
        // Give users the option to sign in / register with their Google account.
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
            // This is where you can provide more ways for users to register and
            // sign in.
        )
        // Create and launch the sign-in intent.
        loginResultLauncher.launch(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build()
        )
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val TAG = "MainActivity"
    }
}

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


