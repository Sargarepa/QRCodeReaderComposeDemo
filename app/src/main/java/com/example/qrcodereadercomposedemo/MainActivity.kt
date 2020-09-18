package com.example.qrcodereadercomposedemo


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.qrcodereadercomposedemo.ui.QRCodeReaderComposeDemoTheme
import com.example.qrcodereadercomposedemo.ui.camera.CameraPreview

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        } else {
            setContent {
                QRCodeReaderComposeDemoTheme {
                    CameraPreview()
                }
            }
        }
    }

    private fun allPermissionsGranted(): Boolean {
        for(permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                baseContext, permission
            ) != PackageManager.PERMISSION_GRANTED) return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                setContent {
                    QRCodeReaderComposeDemoTheme {
                        CameraPreview()
                    }
                }
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}



