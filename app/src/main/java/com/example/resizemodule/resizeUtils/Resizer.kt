package com.example.resizemodule.resizeUtils

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicResize

object Resizer {
    fun resizeCommon(inBmp: Bitmap, width: Int, height: Int, filter: Boolean): Bitmap {
        return Bitmap.createScaledBitmap(inBmp, width, height, true)
    }

    fun resizeRs (inBmp: Bitmap, width: Int, height: Int, appContext: Context): Bitmap {
        val outBmp = Bitmap.createBitmap(
            width,
            height,
            inBmp.config
        )
        val rs = RenderScript.create(appContext)
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

    fun resizeRsKernel (inBmp: Bitmap, width: Int, height: Int, appContext: Context): Bitmap {
        val outBmp = Bitmap.createBitmap(
            width,
            height,
            inBmp.config
        )
        val rs = RenderScript.create(appContext)
        val input = Allocation.createFromBitmap(rs, inBmp)
        val output = Allocation.createFromBitmap(rs, outBmp)
        val resizeScript = ScriptC_resize(rs)
        resizeScript.set_input(input)
        resizeScript.set_output(output)
        resizeScript.set_inputWidth(inBmp.width)
        resizeScript.set_inputHeight(inBmp.height)
        resizeScript.set_outputWidth(width)
        resizeScript.set_outputHeight(height)
        resizeScript.invoke_resize()
        resizeScript.setInput(input)
        resizeScript.forEach_bicubic(output)
        output.copyTo(outBmp)

        output.destroy()
        input.destroy()
        rs.destroy()
        return outBmp
    }
}