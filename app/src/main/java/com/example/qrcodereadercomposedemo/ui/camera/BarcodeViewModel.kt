package com.example.qrcodereadercomposedemo.ui.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.google.android.gms.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import org.koin.core.KoinComponent

class BarcodeViewModel : ViewModel(), KoinComponent {

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
                    Log.e(BarcodeViewModel::class.java.simpleName, "Photo capture failed: ${exception.message}", exception)
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
                        com.google.mlkit.vision.barcode.Barcode.TYPE_WIFI -> {
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
                        com.google.mlkit.vision.barcode.Barcode.TYPE_URL -> {
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
                        com.google.mlkit.vision.barcode.Barcode.TYPE_CONTACT_INFO -> {
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
                        com.google.mlkit.vision.barcode.Barcode.TYPE_DRIVER_LICENSE -> {
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
                Toast.makeText(context, "Sorry, something went wrong!", Toast.LENGTH_SHORT)
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
