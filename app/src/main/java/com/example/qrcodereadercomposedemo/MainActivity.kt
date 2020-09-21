package com.example.qrcodereadercomposedemo


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.compose.ui.platform.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.qrcodereadercomposedemo.ui.QRCodeReaderComposeDemoTheme
import com.example.qrcodereadercomposedemo.ui.camera.CameraPreview
import com.google.android.gms.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.Barcode.*
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        } else {
            setContent {
                QRCodeReaderComposeDemoTheme {
                    CameraPreview(::takePictureAndRunBarcodeScanner)
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

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                setContent {
                    QRCodeReaderComposeDemoTheme {
                        CameraPreview(::takePictureAndRunBarcodeScanner)
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

    companion object {
        private const val TAG = "MainActivity"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    fun takePictureAndRunBarcodeScanner(imageCapture: ImageCapture, context: Context) {
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    runBarcodeScanner(image.toBitmap(), context)
                    image.close()
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
                }
            })
    }

    private fun runBarcodeScanner(bitmap: Bitmap, context: Context) {
        //Create a FirebaseVisionImage
        val image = InputImage.fromBitmap(bitmap, 0)

        //Optional : Define what kind of barcodes you want to scan
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.ALL_FORMATS
            )
            .build()

        //Get access to an instance of FirebaseBarcodeDetector
        val scanner = BarcodeScanning.getClient(options)

        //Use the detector to detect the labels inside the image
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    // See API reference for complete list of supported types
                    when (barcode.valueType) {
                        TYPE_WIFI -> {
                            val ssid = barcode.wifi?.ssid
                            val password = barcode.wifi?.password
                            val type = barcode.wifi?.encryptionType
                            Toast.makeText(
                                context,
                                "WIFI: ssid = $ssid, " +
                                        "password = $password, " +
                                        "type = $type",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                        TYPE_URL -> {
                            val title = barcode.url?.title
                            val url = barcode.url?.url
                            Toast.makeText(
                                context,
                                "URL: title = $title, " +
                                        "url = $url",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                        TYPE_CONTACT_INFO -> {
                            val addresses = barcode.contactInfo?.addresses
                            val emails = barcode.contactInfo?.emails
                            val phones = barcode.contactInfo?.phones
                            val names = barcode.contactInfo?.name
                            val organization = barcode.contactInfo?.organization
                            val title = barcode.contactInfo?.title
                            val urls = barcode.contactInfo?.urls
                            Toast.makeText(
                                context,
                                "CONTACT INFO: names = $names, " +
                                        "organization = $organization, " +
                                        "addresses = $addresses, " +
                                        "emails = $emails, " +
                                        "phones = $phones, " +
                                        "title = $title, " +
                                        "urls = $urls",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                        TYPE_DRIVER_LICENSE -> {
                            val licenseNumber = barcode.driverLicense?.licenseNumber
                            Toast.makeText(
                                context,
                                "DRIVER_LICENCE: $licenseNumber",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                        else -> {
                            val displayValue = barcode.displayValue
                            Toast.makeText(
                                context,
                                "GENERIC: $displayValue",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
            }
            .addOnFailureListener {
                // Task failed with an exception
                Toast.makeText(baseContext, "Sorry, something went wrong!", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}

fun ImageProxy.toBitmap(): Bitmap {
    val buffer = planes[0].buffer
    buffer.rewind()
    val bytes = ByteArray(buffer.capacity())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}



