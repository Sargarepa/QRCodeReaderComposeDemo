package com.example.qrcodereadercomposedemo.ui.camera

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.platform.LifecycleOwnerAmbient
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat

private const val TAG = "CameraPreview"

@Composable
fun CameraPreview(barcodeViewModel: BarcodeViewModel) {
    val lifecycleOwner = LifecycleOwnerAmbient.current
    val context = ContextAmbient.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val imageCapture = ImageCapture.Builder()
        .build()
    Stack {
        AndroidView(viewBlock = ::PreviewView, modifier = Modifier.fillMaxWidth().fillMaxHeight()) { previewView ->
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                try {
                    // Unbind use cases before rebinding
                    cameraProvider.unbindAll()

                    // Bind use cases to camera
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, preview, imageCapture
                    )

                } catch (exc: Exception) {
                    Log.e(TAG, "Use case binding failed", exc)
                }
            }, ContextCompat.getMainExecutor(context))
        }
        Button(onClick = { barcodeViewModel.takePictureAndRunBarcodeScanner(imageCapture, context) }, Modifier.align(Alignment.BottomCenter)) {
            Text("Scan code")
        }
    }

}
