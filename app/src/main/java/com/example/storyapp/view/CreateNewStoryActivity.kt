package com.example.storyapp.view


import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils.isEmpty
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.storyapp.R
import com.example.storyapp.viewmodel.ViewModelFactory
import com.example.storyapp.custom_view.CustomDialog
import com.example.storyapp.data.Result
import com.example.storyapp.databinding.ActivityCreateNewStoryBinding
import com.example.storyapp.viewmodel.CreateNewStoryViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class CreateNewStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateNewStoryBinding
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private lateinit var factory: ViewModelFactory
    private val createStoryViewModel : CreateNewStoryViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateNewStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        factory = ViewModelFactory.getInstance(binding.root.context)

        handlerGallery()
        handlerCamera()
        handlerSubmitStory()
    }

    private val timeStamp: String = SimpleDateFormat(
        "dd-MMM-yyyy",
        Locale.US
    ).format(System.currentTimeMillis())


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    private fun handlerGallery() {
        binding.buttonGaleri.apply {
            setOnClickListener {
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "image/*"
                val pilih = Intent.createChooser(intent, "Pilih Gambar")
                galleryIntent.launch(pilih)
            }
        }
    }

    private val cameraIntent = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val file = File(currentPhotoPath)
            getFile = file
            val resultBitmap = BitmapFactory.decodeFile(file.path)

            binding.image.setImageBitmap(resultBitmap)
        }
    }

    private fun handlerCamera() {
        binding.buttonKamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.resolveActivity(packageManager)

            createCustomTempFile(applicationContext).also { tempFile ->
                val uri: Uri = FileProvider.getUriForFile(
                    this@CreateNewStoryActivity,
                    "com.example.storyapp.mycamera",
                    tempFile
                )
                currentPhotoPath = tempFile.absolutePath
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                cameraIntent.launch(intent)
            }
        }
    }

    private val galleryIntent = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@CreateNewStoryActivity)
            getFile = myFile
            binding.image.setImageURI(selectedImg)
        }
    }

    private fun handlerSubmitStory() {
        binding.buttonUploadStory.setOnClickListener {
            val description = binding.deskripsiTextField.text.toString()
            if (!isEmpty(description) && getFile != null) {
                createStory(description, getFile)
            } else {
                CustomDialog(
                    this,
                    R.string.validation_error,
                ).show()
            }
        }
    }

    private fun createCustomTempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, ".jpg", storageDir)
    }

    private fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = this.createCustomTempFile(context)

        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int

        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return myFile
    }

    private fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int

        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000 )

        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    private fun createStory(description: String, getFile: File?) {
        val convertFile = getFile?.let { file ->
            val reducedFile = reduceFileImage(file)
            val requestImageFile = reducedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("photo", reducedFile.name, requestImageFile)
        }

        val desc = description.toRequestBody("text/plain".toMediaType())

        if (convertFile != null) {
            createStoryViewModel.postCreateStory(convertFile, desc).observe(this@CreateNewStoryActivity) { result ->
                when (result) {
                    is Result.Loading -> {
                        loadingHandler(true)
                    }
                    is Result.Eror -> {
                        loadingHandler(false)
                        errorHandler()
                    }
                    is Result.Berhasil -> {
                        successHandler()
                    }
                }
            }
        }
    }

    private fun loadingHandler(isLoading: Boolean) {
        if (isLoading) {
            binding.root.visibility = View.VISIBLE
            binding.root.visibility = View.GONE
        } else {
            binding.root.visibility = View.GONE
            binding.root.visibility = View.VISIBLE
        }
    }

    private fun errorHandler() {
        CustomDialog(this, R.string.pesan_error).show()
    }

    private fun successHandler() {
        CustomDialog(
            this,
            R.string.success_create_story,
            fun() {
                val moveActivity = Intent(this@CreateNewStoryActivity, MainActivity::class.java)
                moveActivity.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(moveActivity)
                finish()
            }
        ).show()
        binding.image.setImageResource(R.drawable.picture)
        binding.deskripsiTextField.text?.clear()
    }
}