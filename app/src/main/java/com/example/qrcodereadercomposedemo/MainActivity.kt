package com.example.qrcodereadercomposedemo


import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.qrcodereadercomposedemo.ui.*
import com.example.qrcodereadercomposedemo.ui.camera.BarcodeViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
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
        createChannel(
            getString(R.string.test_notification_channel_id),
            getString(R.string.test_notification_channel_name)
        )

        subscribeTopic()
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

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.test_notification_channel_description)

            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun subscribeTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
            .addOnCompleteListener { task ->
                var msg = getString(R.string.message_subscribed)
                if (!task.isSuccessful) {
                    msg = getString(R.string.message_subscribe_failed)
                }
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val TOPIC = "test"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val TAG = "MainActivity"
    }
}


