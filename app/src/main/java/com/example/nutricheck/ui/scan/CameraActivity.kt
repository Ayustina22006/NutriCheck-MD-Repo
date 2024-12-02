package com.example.nutricheck.ui.scan

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.nutricheck.ViewModelFactory
import com.example.nutricheck.data.Injection
import com.example.nutricheck.databinding.ActivityCameraBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraViewModel: CameraViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi CameraViewModel
        val userRepository = Injection.provideRepository(this)
        val factory = ViewModelFactory(userRepository)
        cameraViewModel = ViewModelProvider(this, factory)[CameraViewModel::class.java]

        cameraExecutor = Executors.newSingleThreadExecutor()
        startCamera()

        binding.Capture.setOnClickListener {
            takePhoto()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.surfaceProvider = binding.previewView.surfaceProvider
            }

            @Suppress("DEPRECATION")
            imageCapture = ImageCapture.Builder()
                .setTargetResolution(Size(640, 480))
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                Log.e("CameraActivity", "Use case binding failed", exc)
                showToast("Gagal memulai kamera: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = File(cacheDir, "capture-${System.currentTimeMillis()}.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri
                    if (savedUri != null) {
                        val bitmap = BitmapFactory.decodeFile(savedUri.path)
                        val croppedBitmap = cropToScanBox(bitmap)
                        uploadFrameToServer(croppedBitmap)
                    } else {
                        showToast("Gagal menyimpan gambar.")
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraActivity", "Photo capture failed: ${exception.message}")
                    showToast("Gagal mengambil gambar: ${exception.message}")
                }
            }
        )
    }

    private fun cropToScanBox(bitmap: Bitmap): Bitmap {
        val scanBoxSize = 300
        val centerX = bitmap.width / 2
        val centerY = bitmap.height / 2

        val left = (centerX - scanBoxSize / 2).coerceAtLeast(0)
        val top = (centerY - scanBoxSize / 2).coerceAtLeast(0)
        val right = (centerX + scanBoxSize / 2).coerceAtMost(bitmap.width)
        val bottom = (centerY + scanBoxSize / 2).coerceAtMost(bitmap.height)

        return Bitmap.createBitmap(bitmap, left, top, right - left, bottom - top)
    }

    @SuppressLint("SetTextI18n")
    private fun uploadFrameToServer(bitmap: Bitmap) {
        binding.progressBar.visibility = View.VISIBLE
        binding.predictionTextView.text = "Processing prediction..."

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()

        val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray)
        val imagePart = MultipartBody.Part.createFormData("image", "capture.jpg", requestBody)

        cameraViewModel.predictFood(
            imagePart,
            onSuccess = { foodName ->
                binding.predictionTextView.text = "Prediction: $foodName"
                cameraViewModel.fetchNutritionData(
                    foodName,
                    onSuccess = { nutritionData ->
                        val nutritions = nutritionData.nutritions
                        val nutritionInfo = """
                            Kalori: ${nutritionData.calories}
                            Protein: ${nutritions?.protein} g
                            Karbohidrat: ${nutritions?.totalCarbohydrate} g
                            Zat Besi: ${nutritions?.iron} mg
                        """.trimIndent()
                        binding.nutritionTextView.text = nutritionInfo
                        binding.progressBar.visibility = View.GONE
                    },
                    onError = { errorMessage ->
                        showToast(errorMessage)
                        binding.progressBar.visibility = View.GONE
                    }
                )
            },
            onError = { errorMessage ->
                showToast(errorMessage)
                binding.progressBar.visibility = View.GONE
            }
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
