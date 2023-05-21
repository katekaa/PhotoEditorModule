package com.example.resizemodule.resizeUtils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.renderscript.*
import com.example.resizemodule.ScriptC_crop
import com.example.resizemodule.ScriptC_resize
import java.io.ByteArrayOutputStream

class Resizer(private val context: Context) {
    fun resizeCommon(inBmp: Bitmap, width: Int, height: Int, filter: Boolean): Bitmap {
        return Bitmap.createScaledBitmap(inBmp, width, height, filter)
    }

    fun resizeRs (inBmp: Bitmap, width: Int, height: Int): Bitmap {
        val outBmp = Bitmap.createBitmap(
            width,
            height,
            inBmp.config
        )
        val rs = RenderScript.create(context)
        val input = Allocation.createFromBitmap(rs, inBmp)
        val output = Allocation.createFromBitmap(rs, outBmp)
        val resizeScript = ScriptIntrinsicResize.create(rs)
        resizeScript.setInput(input)
        resizeScript.forEach_bicubic(output)
        output.copyTo(outBmp)
        output.destroy()
        input.destroy()
        rs.destroy()
        return outBmp
    }

    fun resizeRsKernel (inBmp: Bitmap, width: Int, height: Int): Bitmap {
        val outBmp = Bitmap.createBitmap(
            width,
            height,
            inBmp.config
        )
        val rs = RenderScript.create(context)
        val input = Allocation.createFromBitmap(rs, inBmp)
        val output = Allocation.createFromBitmap(rs, outBmp)
        val resizeScript = ScriptC_resize(rs)
        resizeScript._input = input
        resizeScript._output = output
        resizeScript._inputWidth = inBmp.width
        resizeScript._inputHeight = inBmp.height
        resizeScript._outputWidth = width
        resizeScript._outputHeight = height
        resizeScript.invoke_resize()
////        resizeScript.setInput(input)
//        resizeScript.forEach_bicubic(output)
        output.copyTo(outBmp)
        output.destroy()
        input.destroy()
        rs.destroy()
        return outBmp
    }

    fun cropRsKernel (inBmp: Bitmap, width: Int, height: Int, xStart: Int, yStart: Int) : Bitmap {
        val baos = ByteArrayOutputStream()
//        inBmp.compress(Bitmap.CompressFormat.JPEG, 50, baos)
//        baos.toByteArray()
        val rs = RenderScript.create(context)

        val inputType = Type.createXY(rs, Element.RGBA_8888(rs), inBmp.width, inBmp.height)
        val outputType = Type.createXY(rs, Element.RGBA_8888(rs), width, height)
        val inputAllocation = Allocation.createFromBitmap(rs, inBmp,
            Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT)
        val outputAllocation = Allocation.createTyped(rs, inputAllocation.type)

        inputAllocation.copyFrom(baos.toByteArray())
        val script = ScriptC_crop(rs)
        script._xStart = xStart.toLong()
        script._yStart = yStart.toLong()
        script._input = inputAllocation
        script.forEach_crop(outputAllocation)
        val outputArray = ByteArray(outputAllocation.bytesSize)
        outputAllocation.copyTo(outputArray)
        return BitmapFactory.decodeByteArray(outputArray, 0, outputArray.size)
    }
}