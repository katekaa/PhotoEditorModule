//package com.example.resizemodule
//
//import android.content.Context
//import android.content.Intent
//import androidx.lifecycle.lifecycleScope
//import android.content.pm.ResolveInfo
//import android.provider.MediaStore
//import java.util.Collections
//
//class ImagePicker(val context: Context) {
//
//    fun pickFromCamera(): Intent {
//        lifecycleScope.launchWhenStarted {
//            getTmpFileUri().let { uri ->
//                latestTmpUri = uri
//                takeImageResult.launch(uri)
//            }
//        }
//    }
//
//    fun pickFromGallery(): Intent {
//
//    }
//}