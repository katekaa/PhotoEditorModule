package com.example.resizemodule.filterUtils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import androidx.renderscript.Allocation
import androidx.renderscript.Element
import androidx.renderscript.Matrix3f
import androidx.renderscript.Matrix4f
import androidx.renderscript.RenderScript
import androidx.renderscript.ScriptIntrinsicBlur
import androidx.renderscript.ScriptIntrinsicColorMatrix
import com.example.resizemodule.ScriptC_black_and_white
import com.example.resizemodule.ScriptC_contrast
import com.example.resizemodule.ScriptC_gray
import com.example.resizemodule.ScriptC_mono
import com.example.resizemodule.ScriptC_convolve


class FilterManager(private val context: Context) {

    fun filterBlackWhiteRs(inputBitmap: Bitmap): Bitmap{
            val rs = RenderScript.create(context)
            val inputAllocation = Allocation.createFromBitmap(rs, inputBitmap)
            val outputAllocation = Allocation.createTyped(rs, inputAllocation.type)
            val script = ScriptIntrinsicColorMatrix.create(rs, Element.U8_4(rs))
            val matrix = Matrix4f(floatArrayOf(
                0.33f, 0.33f, 0.33f, 0f,
                0.33f, 0.33f, 0.33f, 0f,
                0.33f, 0.33f, 0.33f, 0f,
                0f, 0f, 0f, 1f
            ))
            script.setColorMatrix(matrix)
            script.forEach(inputAllocation, outputAllocation)

            val outputBitmap = Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, inputBitmap.config)
            outputAllocation.copyTo(outputBitmap)
            rs.destroy()
            script.destroy()
            inputAllocation.destroy()
            outputAllocation.destroy()
            return outputBitmap

    }

    fun filterBlackWhiteRsKernel(inputBitmap: Bitmap): Bitmap {
        val rs = RenderScript.create(context)
        val script = ScriptC_black_and_white(rs)
        val outputBitmap =
                Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, Bitmap.Config.ARGB_8888)
        val input = Allocation.createFromBitmap(rs, inputBitmap)
        val output = Allocation.createTyped(rs, input.type)
        script._gIn = input
        script._gOut = output
        script.forEach_root(input, output)
        output.copyTo(outputBitmap)

        rs.destroy()
        script.destroy()
        output.destroy()
        input.destroy()
        return outputBitmap
    }

    fun filterBlackWhiteCommon(inputBitmap: Bitmap): Bitmap {

        val bmpGrayscale =
            Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bmpGrayscale)
        val paint = Paint()

        val cm = ColorMatrix()
        cm.setSaturation(0f)
        paint.colorFilter = ColorMatrixColorFilter(cm)
        canvas.drawBitmap(inputBitmap, 0f, 0f, paint)

        return bmpGrayscale
    }
    fun filterBlackWhiteAlgorithm(inputBitmap: Bitmap): Bitmap {
        val width: Int = inputBitmap.width
        val height: Int = inputBitmap.height
        // create output bitmap
        val bmOut = Bitmap.createBitmap(width, height, inputBitmap.config)
        // color information
        var A: Int
        var R: Int
        var G: Int
        var B: Int
        var pixel: Int

        // scan through all pixels
        for (x in 0 until width) {
            for (y in 0 until height) {
                // get pixel color
                pixel = inputBitmap.getPixel(x, y)
                A = Color.alpha(pixel)
                R = Color.red(pixel)
                G = Color.green(pixel)
                B = Color.blue(pixel)
                var gray = (0.2989 * R + 0.5870 * G + 0.1140 * B).toInt()

                // use 128 as threshold, above -> white, below -> black
                gray = if (gray > 128) 255 else 0
                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, gray, gray, gray))
            }
        }
        return bmOut
    }

    fun filterBlackWhitePixel(inputBitmap: Bitmap): Bitmap {
        val width = inputBitmap.width
        val height = inputBitmap.height

        // Create a new Bitmap with the same dimensions as the input Bitmap

        // Create a new Bitmap with the same dimensions as the input Bitmap
        val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        // Iterate through each pixel in the input Bitmap

        // Iterate through each pixel in the input Bitmap
        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = inputBitmap.getPixel(x, y)

                // Extract the RGB channels from the pixel
                val r = Color.red(pixel)
                val g = Color.green(pixel)
                val b = Color.blue(pixel)

                // Calculate the average value for the RGB channels
                val average = (r + g + b) / 3

                // Set the output pixel to grayscale
                val grayPixel = Color.rgb(average, average, average)
                outputBitmap.setPixel(x, y, grayPixel)
            }
        }

        return outputBitmap

    }

    fun filterMonoRs(inputBitmap: Bitmap): Bitmap {
        val rs = RenderScript.create(context)
        val input = Allocation.createFromBitmap(rs, inputBitmap)
        val output = Allocation.createTyped(rs, input.type)
        val script = ScriptC_mono(rs)
        val outputBitmap =
            Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, Bitmap.Config.ARGB_8888)
        script.forEach_root(input, output)
        output.copyTo(outputBitmap)

        rs.destroy()
        script.destroy()
        output.destroy()
        input.destroy()
        return outputBitmap

    }

    fun filterBlackWhiteGrayRsKernel(inputBitmap: Bitmap): Bitmap {
        val rs = RenderScript.create(context)
        val input = Allocation.createFromBitmap(rs, inputBitmap)
        val output = Allocation.createTyped(rs, input.type)
        val script = ScriptC_gray(rs)
        val outputBitmap =
            Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, Bitmap.Config.ARGB_8888)
        script.forEach_gray(input, output)
        output.copyTo(outputBitmap)

        rs.destroy()
        script.destroy()
        output.destroy()
        input.destroy()
        return outputBitmap
    }

    fun filterBlackWhiteColorMatrix(inputBitmap: Bitmap): Bitmap {
        return useColorMatrix(inputBitmap, context, BLACK_AND_WHITE_MATRIX)
    }

    fun filterBrightnessColorMatrix(inputBitmap: Bitmap, v: Float): Bitmap {
        return useColorMatrix(inputBitmap, context, Matrix3f(floatArrayOf(v, 0f, 0f, 0f, v, 0f, 0f, 0f, v)))
    }


    fun filterContrastRsKernel(inputBitmap: Bitmap, brightness: Float = 0f): Bitmap {
        val rs = RenderScript.create(context)
        val input = Allocation.createFromBitmap(rs, inputBitmap)
        val output = Allocation.createTyped(rs, input.type)
        val script = ScriptC_contrast(rs)
        val outputBitmap =
            Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, Bitmap.Config.ARGB_8888)
        if (brightness != 0f)  script.invoke_setBright(brightness)
        script.forEach_contrast(input, output)
        output.copyTo(outputBitmap)

        rs.destroy()
        script.destroy()
        output.destroy()
        input.destroy()
        return outputBitmap
    }
    /**
     *
     * @param bmp input bitmap
     * @param contrast 0..10 1 is default
     * @param brightness -255..255 0 is default
     * @return new bitmap
     */
    fun filterContrastCommon(inputBitmap: Bitmap, contrast: Float, brightness: Float): Bitmap {
        val cm = ColorMatrix(
            floatArrayOf(
                contrast, 0f, 0f, 0f, brightness,
                0f, contrast, 0f, 0f, brightness,
                0f, 0f, contrast, 0f, brightness,
                0f, 0f, 0f, 1f, 0f
            )
        )
        val ret = Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, inputBitmap.config)
        val canvas = Canvas(ret)
        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(cm)
        canvas.drawBitmap(inputBitmap, 0f, 0f, paint)
        return ret
    }

    private fun filterBlurColorMatrix(inputBitmap: Bitmap, radius: Float = DEFAULT_BLUR_RADIUS): Bitmap {
        val rs = RenderScript.create(context)
        val input = Allocation.createFromBitmap(rs, inputBitmap)
        val output = Allocation.createTyped(rs, input.type)
        val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        script.setRadius(radius)
        script.setInput(input)
        script.forEach(output)
        val outputBitmap =
            Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, inputBitmap.config)

        output.copyTo(outputBitmap)

        rs.destroy()
        script.destroy()
        output.destroy()
        input.destroy()
        return outputBitmap
    }

    private fun filterSharpenColorMatrix(inputBitmap: Bitmap): Bitmap {
        val rs = RenderScript.create(context)
        val input = Allocation.createFromBitmap(rs, inputBitmap)
        val output = Allocation.createTyped(rs, input.type)

        val allocKernel = Allocation.createSized(rs, Element.F32(rs), MATRIX_SIZE, Allocation.USAGE_SCRIPT)
        allocKernel.copyFrom(SHARPEN_MATRIX)
        val script = ScriptC_convolve(rs)
        script._width = inputBitmap.width.toLong()
        script._height = inputBitmap.height.toLong()
        script._in = input
        script.bind_kernel(allocKernel)
        script.forEach_convolve(output)

        val outputBitmap =
            Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, inputBitmap.config)

        output.copyTo(outputBitmap)

        rs.destroy()
        script.destroy()
        output.destroy()
        input.destroy()
        return outputBitmap
    }

    private fun useColorMatrix(inputBitmap: Bitmap, context: Context, matrix: Matrix4f): Bitmap {
        val rs = RenderScript.create(context)
        val input = Allocation.createFromBitmap(rs, inputBitmap)
        val output = Allocation.createTyped(rs, input.type)
        val element = Element.RGBA_8888(rs)
        val script = ScriptIntrinsicColorMatrix.create(rs, element)
        script.setColorMatrix(matrix)
        script.setGreyscale()
        script.forEach(input, output)
        val outputBitmap =
            Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, Bitmap.Config.ARGB_8888)

        output.copyTo(outputBitmap)

        rs.destroy()
        script.destroy()
        output.destroy()
        input.destroy()
        return outputBitmap
    }

    private fun useColorMatrix(inputBitmap: Bitmap, context: Context, matrix: Matrix3f): Bitmap {
        val rs = RenderScript.create(context)
        val input = Allocation.createFromBitmap(rs, inputBitmap)
        val output = Allocation.createTyped(rs, input.type)
        val element = Element.RGBA_8888(rs)
        val script = ScriptIntrinsicColorMatrix.create(rs, element)
        script.setColorMatrix(matrix)
        script.setGreyscale()
        script.forEach(input, output)
        val outputBitmap =
            Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, Bitmap.Config.ARGB_8888)

        output.copyTo(outputBitmap)

        rs.destroy()
        script.destroy()
        output.destroy()
        input.destroy()
        return outputBitmap
    }

    companion object ColorConstants {
        val SEPIA_MATRIX = Matrix3f(floatArrayOf(0.393f, 0.349f, 0.272f, 0.769f, 0.686f, 0.534f, 0.189f, 0.168f, 0.131f))
        val INVERT_MATRIX = Matrix4f(floatArrayOf(-1f, 0f, 0f, 0f, 0f, -1f, 0f, 0f, 0f, 0f, -1f, 0f, 1f, 1f, 1f, 0f))
        val BLACK_AND_WHITE_MATRIX = Matrix4f(floatArrayOf(1.5f, 1.5f, 1.5f, 0f, 1.5f, 1.5f, 1.5f, 0f, 1.5f, 1.5f, 1.5f, 0f, -1f, -1f, -1f, 0f))
        const val DEFAULT_BLUR_RADIUS = 5.0f
        const val BLUR = 1.0f / 9.0f
        const val MATRIX_SIZE = 9
        val SHARPEN_MATRIX = floatArrayOf(0.0f, -1.0f, 0.0f, -1.0f, 5.0f, -1.0f, 0.0f, -1.0f, 0.0f)
        val EDGE_MATRIX = floatArrayOf(-1.0f, -1.0f, -1.0f, -1.0f, 8.0f, -1.0f, -1.0f, -1.0f, -1.0f)
        val EMBOSS_MATRIX = floatArrayOf(-2.0f, -1.0f, 0.0f, -1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 2.0f)
        val BLOOM_MATRIX = floatArrayOf(0.0f, 20.0f / 7.0f, 0.0f, 20.0f / 7.0f, -59.0f / 7.0f, 20.0f / 7.0f, 1.0f / 7.0f, 13.0f / 7.0f, 0.0f)
        val BLUR_MATRIX = floatArrayOf(
            BLUR,
            BLUR,
            BLUR,
            BLUR,
            BLUR,
            BLUR,
            BLUR,
            BLUR,
            BLUR
        )
    }

}