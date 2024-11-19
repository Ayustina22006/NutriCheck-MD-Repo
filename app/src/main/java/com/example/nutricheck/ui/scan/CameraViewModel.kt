package com.example.nutricheck.ui.scan

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class CameraViewModel : ViewModel() {

    // LiveData untuk teks hasil OCR
    private val _ocrResult = MutableLiveData<String>()
    val ocrResult: LiveData<String> get() = _ocrResult

    // LiveData untuk error atau status
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun processImage(imageProxy: ImageProxy) {
        try {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

                // OCR menggunakan ML Kit
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        Log.d("CameraViewModel", "Recognized text: ${visionText.text}")
                        _ocrResult.postValue(visionText.text) // Kirim teks ke UI
                    }
                    .addOnFailureListener { e ->
                        Log.e("CameraViewModel", "Text recognition failed", e)
                        _errorMessage.postValue("Failed to recognize text")
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            } else {
                imageProxy.close()
            }
        } catch (e: Exception) {
            Log.e("CameraViewModel", "Error processing image", e)
            _errorMessage.postValue("An error occurred while processing the image")
            imageProxy.close()
        }
    }
}
