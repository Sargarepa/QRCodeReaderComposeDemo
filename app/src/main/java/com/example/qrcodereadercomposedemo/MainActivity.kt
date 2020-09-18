package com.example.qrcodereadercomposedemo


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
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
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import java.nio.Buffer

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
            object: ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    runBarcodeScanner(image.toBitmap(), context)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
                }
            })
    }

    private fun runBarcodeScanner(bitmap: Bitmap, context: Context) {
        //Create a FirebaseVisionImage
        val image = FirebaseVisionImage.fromBitmap(bitmap)

        //Optional : Define what kind of barcodes you want to scan
        val options = FirebaseVisionBarcodeDetectorOptions.Builder()
            .setBarcodeFormats(
                //Detect all kind of barcodes
                FirebaseVisionBarcode.FORMAT_ALL_FORMATS
                //Or specify which kind of barcode you want to detect
                /*
                    FirebaseVisionBarcode.FORMAT_QR_CODE,
                FirebaseVisionBarcode.FORMAT_AZTEC
                 */
            )
            .build()

        //Get access to an instance of FirebaseBarcodeDetector
        val detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options)

        //Use the detector to detect the labels inside the image
        detector.detectInImage(image)
            .addOnSuccessListener {
                // Task completed successfully
                for (firebaseBarcode in it) {
                    when (firebaseBarcode.valueType) {
                        //Handle the URL here
                        FirebaseVisionBarcode.TYPE_URL ->
                            Toast.makeText(context, "URL: ${firebaseBarcode.displayValue}", Toast.LENGTH_SHORT)
                                .show()
                        // Handle the contact info here, i.e. address, name, phone, etc.
                        FirebaseVisionBarcode.TYPE_CONTACT_INFO ->
                            Toast.makeText(context, "CONTACT_INFO: ${firebaseBarcode.contactInfo?.title}", Toast.LENGTH_SHORT)
                                .show()
                        // Handle the wifi here, i.e. firebaseBarcode.wifi.ssid, etc.
                        FirebaseVisionBarcode.TYPE_WIFI ->
                            Toast.makeText(context, "WIFI: ${firebaseBarcode.wifi?.ssid}", Toast.LENGTH_SHORT)
                                .show()
                        // Handle the driver license barcode here, i.e. City, Name, Expiry, etc.
                        FirebaseVisionBarcode.TYPE_DRIVER_LICENSE ->
                            Toast.makeText(context, "DRIVER_LICENCE: ${firebaseBarcode.driverLicense?.licenseNumber}", Toast.LENGTH_SHORT)
                                .show()
                        //Handle more types
                        else ->
                            Toast.makeText(context, "GENERIC: ${firebaseBarcode.displayValue}", Toast.LENGTH_SHORT)
                                .show()
                        //None of the above type was detected, so extract the value from the barcode
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



