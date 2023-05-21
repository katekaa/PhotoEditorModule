package com.example.resizemodule.resizeUtils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

class Scaler(private val context: Context) {
    fun scaleCommon(inputBitmap: Bitmap, scaleFactor: Double): Bitmap {
        // Calculate the desired width and height of the scaled image
        val scaleWidth = inputBitmap.width * scaleFactor
        val scaleHeight = inputBitmap.height * scaleFactor

        // Create the scaled bitmap
        // Use the scaled bitmap as needed (e.g. display in an ImageView)
        return Bitmap.createScaledBitmap(
            inputBitmap,
            scaleWidth.toInt(), scaleHeight.toInt(), true
        )
    }

    fun compressCommon(inputBitmap: Bitmap, quality: Int): Bitmap {
        val outputStream = ByteArrayOutputStream()
        // Set the compression quality to 50 (medium quality)
        inputBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val compressedImageByteArray = outputStream.toByteArray()
        return BitmapFactory.decodeByteArray(compressedImageByteArray, 0, compressedImageByteArray.size)
    }
}