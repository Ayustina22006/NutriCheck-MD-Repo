package com.example.nutricheck.ui.scan

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.nutricheck.R
import com.example.nutricheck.ViewModelFactory
import com.example.nutricheck.data.Injection
import com.example.nutricheck.databinding.FragmentCameraBinding
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private var cameraControl: CameraControl? = null // Untuk mengontrol flashlight
    private var isFlashlightOn = false // State flashlight
    private lateinit var cameraViewModel: CameraViewModel
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            showToast("Izin kamera diperlukan untuk menggunakan fitur ini.")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userRepository = Injection.provideRepository(requireContext())
        val resourceProvider = Injection.provideResourceProvider(requireContext())
        val factory = ViewModelFactory(userRepository, resourceProvider, requireActivity().application)
        cameraViewModel = ViewModelProvider(this, factory)[CameraViewModel::class.java]

        // Initialize Executor
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Periksa izin kamera sebelum memulai kamera
        if (hasCameraPermission()) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        // Initialize Gallery Launcher
        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK && result.data != null) {
                val selectedImageUri = result.data?.data
                if (selectedImageUri != null) {
                    val bitmap = uriToBitmap(selectedImageUri)
                    val compressedBitmap = compressBitmap(bitmap)
                    uploadFrameToServer(selectedImageUri, compressedBitmap)
                }
            } else {
                showToast("Tidak ada gambar yang dipilih.")
            }
        }

        binding.backButton.setOnClickListener{
            findNavController().navigate(R.id.navigation_home)
        }

        // Event Listener untuk tombol
        binding.Capture.setOnClickListener { takePhoto() }
        binding.btnGallery.setOnClickListener { openGallery() }
        binding.btnInfo.setOnClickListener {
            findNavController().navigate(R.id.action_cameraFragment_to_addImageFragment)
        }
        binding.btnRightTop.setOnClickListener { toggleFlashlight() } // Listener Flashlight
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

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

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                val camera = cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
                cameraControl = camera.cameraControl // Inisialisasi CameraControl
            } catch (exc: Exception) {
                Log.e("CameraFragment", "Use case binding failed", exc)
                showToast("Gagal memulai kamera: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun toggleFlashlight() {
        if (cameraControl == null) {
            showToast("Kamera belum siap.")
            return
        }
        isFlashlightOn = !isFlashlightOn
        cameraControl?.enableTorch(isFlashlightOn)
        binding.btnRightTop.setImageResource(if (isFlashlightOn) R.drawable.ic_flash else R.drawable.ic_flash_off)
        showToast(if (isFlashlightOn) "Flashlight ON" else "Flashlight OFF")
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = createCustomTempFile(requireContext())
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    Log.d("CameraFragment", "Image saved at: $savedUri")
                    val bitmap = BitmapFactory.decodeFile(savedUri.path)
                    val croppedBitmap = cropToScanBox(bitmap)
                    uploadFrameToServer(savedUri, croppedBitmap)
                }

                override fun onError(exception: ImageCaptureException) {
                    showToast("Gagal mengambil gambar: ${exception.message}")
                }
            }
        )
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun uriToBitmap(uri: Uri): Bitmap {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        return BitmapFactory.decodeStream(inputStream)
    }

    private fun compressBitmap(bitmap: Bitmap): Bitmap {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return bitmap
    }

    private fun createCustomTempFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, ".jpg", storageDir)
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
    private fun uploadFrameToServer(imageUri: Uri, bitmap: Bitmap) {
        Log.d("CameraFragment", "uploadFrameToServer - Image URI: $imageUri")
        binding.progressBar.visibility = View.VISIBLE
        binding.predictionTextView.text = "Processing prediction..."

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()
        val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray)
        val imagePart = MultipartBody.Part.createFormData("image", "capture.jpg", requestBody)

        lifecycleScope.launch {
            cameraViewModel.predictFood(
                imagePart,
                onSuccess = { foodName ->
                    binding.predictionTextView.text = "Prediction: $foodName"
                    cameraViewModel.fetchNutritionData(
                        foodName,
                        onSuccess = { foodResponse ->
                            val food = foodResponse.data.firstOrNull()
                            if (food != null) {
                                cameraViewModel.addCapturedFoodItem(
                                    imagePath = imageUri.toString(),
                                    mealid = food.id,
                                    foodName = food.foodName
                                )
                                Log.d("CameraFragment", "Captured food item added to ViewModel: ${imageUri},$foodName")
                                findNavController().navigate(R.id.action_cameraFragment_to_addImageFragment)
                            } else {
                                binding.nutritionTextView.text = "Data nutrisi tidak ditemukan."
                            }
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
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        cameraExecutor.shutdown()
    }
}
