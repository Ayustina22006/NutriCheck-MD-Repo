package com.example.nutricheck.ui.profil

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.nutricheck.R
import com.example.nutricheck.ViewModelFactory
import com.example.nutricheck.data.Result
import com.example.nutricheck.data.response.UserResponse
import com.example.nutricheck.data.response.updateUserResponse
import kotlinx.coroutines.launch

class EditUserProfileFragment : Fragment() {
    // UI Components
    private lateinit var tvUsername: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvGender: TextView
    private lateinit var tvAge: TextView
    private lateinit var ivProfileUser: ImageView
    private lateinit var tvPassword: TextView

    // ViewModel
    private val viewModel: EditUserProfileViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_user_profile, container, false)

        // Initialize UI components
        tvUsername = view.findViewById(R.id.tvUsername)
        tvEmail = view.findViewById(R.id.tvEmail)
        tvGender = view.findViewById(R.id.tvGender)
        tvAge = view.findViewById(R.id.tvAge)
        tvPassword = view.findViewById(R.id.tvPassword)
        ivProfileUser = view.findViewById(R.id.profileUser)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fetch user data
        fetchUserData()

        setupInput(view)

        // Observe update results
        observeUpdateResult()

        // Back button
        val btnBack: ImageView = view.findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupInput(view: View) {
        val ivpanah1: ImageView = view.findViewById(R.id.ivpanah1)
        val ivpanah2: ImageView = view.findViewById(R.id.ivzpanah2)
        val ivpanah3: ImageView = view.findViewById(R.id.ivpanah3)
        val ivpanah4: ImageView = view.findViewById(R.id.ivpanah4)
        val ivpanah5: ImageView = view.findViewById(R.id.ivpanah5)

        ivpanah1.setOnClickListener {
            showEditDialog("Username", tvUsername)
        }
        ivpanah2.setOnClickListener {
            showEditDialog("Email", tvEmail)
        }
        ivpanah3.setOnClickListener {
            showEditDialog("Password", null)  // No existing text for password
        }
        ivpanah4.setOnClickListener {
            showGenderDialog()
        }
        ivpanah5.setOnClickListener {
            showEditDialog("Age", tvAge)
        }
    }

    private fun showGenderDialog() {
        val genders = arrayOf("male", "female")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select Gender")
        builder.setItems(genders) { _, which ->
            val selectedGender = genders[which]
            lifecycleScope.launch {
                viewModel.updateGenderBMI(gender = selectedGender)
            }
        }
        builder.show()
    }


    private fun showEditDialog(title: String, textView: TextView?) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.popup_form, null)
        val editText: EditText = dialogView.findViewById(R.id.tvInputEdit)
        val tvDialogTitle: TextView = dialogView.findViewById(R.id.titleText)
        val tvDialogSubtitle: TextView = dialogView.findViewById(R.id.subtitleText)

        if (title == "Age") {
            editText.inputType = InputType.TYPE_CLASS_NUMBER
        }

        // Customize dialog based on the title
        when (title) {
            "Username" -> {
                tvDialogTitle.text = "Want to change your username?"
                tvDialogSubtitle.text = "Enter your new username below."
                editText.hint = "Enter your new username"
                editText.setText(textView?.text)
                editText.inputType = InputType.TYPE_CLASS_TEXT
            }
            "Email" -> {
                tvDialogTitle.text = "Want to change your email?"
                tvDialogSubtitle.text = "Enter your new email address below."
                editText.hint = "Enter your new email"
                editText.setText(textView?.text)
                editText.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            }
            "Password" -> {
                tvDialogTitle.text = "Want to change your password?"
                tvDialogSubtitle.text = "Enter your new password below."
                editText.hint = "Enter your new password"
                editText.inputType = InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            "Age" -> {
                tvDialogTitle.text = "Want to update your age?"
                tvDialogSubtitle.text = "Enter your new age below."
                editText.hint = "Enter your new age"
                editText.setText(textView?.text?.toString()?.replace(" y.o", ""))
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // Set up custom submit button
        val submitButton = dialogView.findViewById<ImageButton>(R.id.tvButton)
        submitButton.setOnClickListener {
            val newValue = editText.text.toString()
            when (title) {
                "Username" -> {
                    if (newValue.isNotBlank()) {
                        viewModel.updateUsername(newValue)
                        dialog.dismiss()
                    } else {
                        Toast.makeText(requireContext(), "Username cannot be empty", Toast.LENGTH_SHORT).show()
                    }
                }
                "Email" -> {
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(newValue).matches()) {
                        viewModel.updateEmail(newValue)
                        dialog.dismiss()
                    } else {
                        Toast.makeText(requireContext(), "Invalid email format", Toast.LENGTH_SHORT).show()
                    }
                }
                "Password" -> {
                    if (newValue.length >= 6) {
                        viewModel.updatePassword(newValue)
                        dialog.dismiss()
                    } else {
                        Toast.makeText(requireContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                    }
                }
                "Age" -> {
                    val age = newValue.toIntOrNull()
                    if (age != null && age in 12..120) {
                        viewModel.updateAgeBMI(age = age)
                        dialog.dismiss()
                    } else {
                        Toast.makeText(requireContext(), "Invalid age", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        dialog.show()
}

    private fun observeUpdateResult() {
        viewModel.updateResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success<*> -> {
                    if (result.data is UserResponse) {
                        val userResponse = result.data
                        userResponse.data?.let { userData ->
                            userData.username?.let { username -> tvUsername.text = username }
                            userData.email?.let { email -> tvEmail.text = email }
                            userData.bmi?.let { bmi ->
                                bmi.gender?.let { gender -> tvGender.text = gender }
                                bmi.age?.let { age -> tvAge.text = "$age y.o" }
                            }
                        }
                        Toast.makeText(requireContext(), userResponse.message ?: "Update successful", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Unexpected response type", Toast.LENGTH_SHORT).show()
                    }
                }
                is Result.Error -> {
                    Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                }
                is Result.Loading -> {
                    // Tampilkan indikator loading jika diperlukan
                }
            }
        }

        viewModel.updateResultBmi.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    Toast.makeText(requireContext(), "Update successful", Toast.LENGTH_SHORT).show()
                    fetchUserData()
                }
                is Result.Error -> {
                    Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                }
                is Result.Loading -> {
                    // Tampilkan loading indikator jika perlu
                }
            }
        }
    }

    private fun fetchUserData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.fetchUserBMI().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        // Show loading indicator if needed
                    }
                    is Result.Success -> {
                        val userData = result.data.data
                        val bmiData = userData?.bmi

                        // Update UI with fetched data
                        tvUsername.text = userData?.username ?: "N/A"
                        tvEmail.text = userData?.email ?: "N/A"
                        tvGender.text = bmiData?.gender ?: "N/A"
                        tvAge.text = "${bmiData?.age ?: "N/A"} y.o"
                        tvPassword.text = "********"  // Masking password

                        userData?.username?.let {
                            val initials = getInitials(it)
                            val initialsDrawable = createProfileInitialsDrawable(initials)
                            ivProfileUser.setImageBitmap(initialsDrawable)
                        }
                    }
                    is Result.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "Failed to load user data: ${result.error}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    // Existing helper methods for initials and profile image remain the same
    private fun getInitials(username: String): String {
        val parts = username.trim().split(" ")
        return when {
            parts.size >= 2 -> parts[0].first().uppercase() + parts[1].first().uppercase()
            parts.isNotEmpty() -> parts[0].first().uppercase()
            else -> ""
        }
    }

    private fun createProfileInitialsDrawable(initials: String): Bitmap {
        val size = 100
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint().apply {
            color = Color.LTGRAY
            style = Paint.Style.FILL
        }
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)

        paint.apply {
            color = Color.BLACK
            textSize = 40f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val textY = (canvas.height / 2 - (paint.descent() + paint.ascent()) / 2)
        canvas.drawText(initials, (canvas.width / 2).toFloat(), textY, paint)

        return bitmap
    }
}