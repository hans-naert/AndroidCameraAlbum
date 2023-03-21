package com.example.androidcameraalbum

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.example.androidcameraalbum.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val takePhoto = 1
    lateinit var imageUri: Uri
    lateinit var outputImage: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val launcher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                // Handle the success
                val bitmap = BitmapFactory.decodeStream(contentResolver.
                openInputStream(imageUri))
                binding.imageView.setImageBitmap(rotateIfRequired(bitmap))

            } else {
                // Handle failure
                Toast.makeText(this, "Failed to take photo", Toast.LENGTH_SHORT).show()
            }
        }

        binding.takePhoteButton.setOnClickListener {
            // Create File object to store the image
            outputImage = File(externalCacheDir, "output_image.jpg")
            if (outputImage.exists()) {
                outputImage.delete()
            }
            outputImage.createNewFile()
            imageUri = FileProvider.getUriForFile(this, "com.example.androidcameraalbum.fileprovider", outputImage)
            // Start the camera activity
            launcher.launch(imageUri)
        }
    }

    private fun rotateIfRequired(bitmap: Bitmap): Bitmap {
        val exif = ExifInterface(outputImage.path)
        val orientation = exif.getAttributeInt(ExifInterface.
        TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap,
                90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap,
                180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap,
                270)
            else -> bitmap
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degree: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.
        width, bitmap.height,
            matrix, true)
        bitmap.recycle() // Recycle the bitmap object
        return rotatedBitmap
    }
}
