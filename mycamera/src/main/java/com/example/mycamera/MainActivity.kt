package com.example.mycamera

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import androidx.core.content.FileProvider
import com.example.mycamera.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    private val takePhoto: Int = 1
    private val fromAlbum: Int = 2
    private lateinit var outputImage: File
    private lateinit var binding: ActivityMainBinding
    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.takePhoto.setOnClickListener {
            //创建File对象，用于存储拍照后的照片
            outputImage = File(externalCacheDir, "output_image.jpg")
            if (outputImage.exists()) {
                outputImage.delete()
            }
            outputImage.createNewFile()
            imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(this, "com.example.mycamera.fileprovider", outputImage)
            } else {
                Uri.fromFile(outputImage)
            }

            //启动相机
            val intent = Intent("android.media.action.IMAGE_CAPTURE")
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(intent, takePhoto)
        }

        binding.choose.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, fromAlbum)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            takePhoto -> {
                if (resultCode == Activity.RESULT_OK) {
                    //将拍摄的照片显示出来
                    val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
                    binding.imageView.setImageBitmap(bitmap)
                }
            }
            fromAlbum -> {
                if (resultCode == Activity.RESULT_OK && data != null){
                    data.data?.let {
                        val bitmap = getBitmapFromUri(it)
                        binding.imageView.setImageBitmap(bitmap)
                    }
                }
            }
        }
    }

    private fun getBitmapFromUri(it: Uri) = contentResolver.openFileDescriptor(it,"r")?.use {
        BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
    }
}