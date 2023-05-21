package com.example.resizemodule

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resizemodule.filterUtils.FilterManager
import com.example.resizemodule.resizeUtils.Resizer
import com.example.resizemodule.resizeUtils.Scaler
import kotlinx.coroutines.launch
import java.io.File
import kotlin.system.measureTimeMillis
import androidx.lifecycle.MutableLiveData


class MainActivity : AppCompatActivity() {
    val bitmapList10 = mutableListOf<Bitmap>()
    val bitmapList50 = mutableListOf<Bitmap>()
    val adapterData = MutableLiveData<List<Bitmap>>()
    val changedList = mutableListOf<Bitmap>()
    private val adapter = ImageAdapter()
    private lateinit var originalBitMap: Bitmap
    private lateinit var fiterManager: FilterManager
    private lateinit var resizer: Resizer
    private lateinit var scaler: Scaler
    private lateinit var recycler: RecyclerView

    private val takeImageResult = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            latestTmpUri?.let { uri ->
                previewImage.setImageURI(uri)
            }
        }
    }

    private val selectImageFromGalleryResult = registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { list ->
        for (i in list) {
            println(i)
        }
        list.first()?.let { previewImage.setImageURI(it) }
    }

    private var latestTmpUri: Uri? = null

    private val previewImage by lazy { findViewById<ImageView>(R.id.img_preview1) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fiterManager = FilterManager(this)
        resizer = Resizer(this)
        scaler = Scaler(this)
        recycler = findViewById<RecyclerView>(R.id.recycler)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this)
        adapterData.observe(this){
            println("list: ${it.size}")
            adapter.submitList(it)
        }

        findViewById<Button>(R.id.resize).setOnClickListener {
           startResize()
        }

        findViewById<Button>(R.id.scale).setOnClickListener {
            startScale()
        }
        findViewById<Button>(R.id.compress).setOnClickListener {
            startCompress()
        }
        findViewById<Button>(R.id.bw).setOnClickListener {
            startFilters()
        }

        findViewById<Button>(R.id.contrast).setOnClickListener {
            startContrast()
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            originalBitMap = BitmapFactory.decodeResource(resources, R.drawable.good_dog)
            for (i in 0..10) {
                bitmapList10.add(originalBitMap)
            }
            for (i in 0..50) {
                bitmapList50.add(originalBitMap)
            }
        }
    }

    private fun startFilters() {
        val elapsedTimeBwGrayRsKernel = measureTimeMillis {
//            for (bm in bitmapList) {
//                fiterManager.filterBlackWhiteRs(bm)
//            }
            fiterManager.filterBlackWhiteGrayRsKernel(originalBitMap)
        }
        println("filterBlackWhiteGrayRsKernel $elapsedTimeBwGrayRsKernel")
        changedList.add(fiterManager.filterBlackWhiteGrayRsKernel(originalBitMap))

        val elapsedTimeBwRs = measureTimeMillis {
//            for (bm in bitmapList) {
//                fiterManager.filterBlackWhiteRs(bm)
//            }
            fiterManager.filterBlackWhiteRs(originalBitMap)
        }
        println("filterBlackWhiteRs $elapsedTimeBwRs")
       changedList.add(fiterManager.filterBlackWhiteRs(originalBitMap))

//        val elapsedTimeBwRsKernel = measureTimeMillis {
////            for (bm in bitmapList) {
////                fiterManager.filterBlackWhiteRsKernel(bm)
////            }
//            fiterManager.filterBlackWhiteRsKernel(originalBitMap)
//        }
//        println("filterBlackWhiteRsKernel $elapsedTimeBwRsKernel")
//        val res = fiterManager.filterBlackWhiteRsKernel(originalBitMap)
//        changedList.add(res)
//
//        val emptyBitmap = Bitmap.createBitmap(res.width, res.height, res.config)
//
//        if (res.sameAs(emptyBitmap)) {
//            println("IT IS EMPTY")
//        }

        val elapsedTimeBwCommon = measureTimeMillis {
//            for (bm in bitmapList) {
//                fiterManager.filterBlackWhiteBuiltIn(bm)
//            }
            fiterManager.filterBlackWhiteCommon(originalBitMap)
        }
        println("filterBlackWhiteBuiltIn $elapsedTimeBwCommon")
        changedList.add(fiterManager.filterBlackWhiteCommon(originalBitMap))


//        val elapsedTimeBwPixel = measureTimeMillis {
////            for (bm in bitmapList) {
////                fiterManager.filterBlackWhitePixel(bm)
////            }
//            fiterManager.filterBlackWhitePixel(originalBitMap)
//        }
//        println("filterBlackWhitePixel $elapsedTimeBwPixel")
//        changedList.add(fiterManager.filterBlackWhitePixel(originalBitMap))

        val elapsedTimeBwMonoRs = measureTimeMillis {
            fiterManager.filterMonoRs(originalBitMap)
        }
        println("filterBlackWhiteMonoRs $elapsedTimeBwMonoRs")
        changedList.add(fiterManager.filterMonoRs(originalBitMap))

        val elapsedTimeBwColorMatrix = measureTimeMillis {
            fiterManager.filterBlackWhiteColorMatrix(originalBitMap)
        }
        println("filterBlackWhiteColorMatrix $elapsedTimeBwColorMatrix")
        changedList.add(fiterManager.filterBlackWhiteColorMatrix(originalBitMap))

        adapterData.value = changedList

    }

    private fun startResize() {
        val elapsedTimeResizeCommon = measureTimeMillis {
            resizer.resizeCommon(originalBitMap, OUTPUT_IMAGE_WIDTH, OUTPUT_IMAGE_HEIGHT, true)
        }
        println("resizeCommon $elapsedTimeResizeCommon")
        changedList.add(resizer.resizeCommon(originalBitMap, OUTPUT_IMAGE_WIDTH, OUTPUT_IMAGE_HEIGHT, true))

        val elapsedTimeResizeRs = measureTimeMillis {
            resizer.resizeRs(originalBitMap, OUTPUT_IMAGE_WIDTH, OUTPUT_IMAGE_HEIGHT)
        }
        println("resizeRs $elapsedTimeResizeRs")
        changedList.add(resizer.resizeRs(originalBitMap, OUTPUT_IMAGE_WIDTH, OUTPUT_IMAGE_HEIGHT))

        val elapsedTimeResizeRsKernel = measureTimeMillis {
            resizer.resizeRsKernel(originalBitMap, OUTPUT_IMAGE_WIDTH, OUTPUT_IMAGE_HEIGHT)
        }
        println("resizeRsKernel $elapsedTimeResizeRsKernel")
        changedList.add(resizer.resizeRsKernel(originalBitMap, OUTPUT_IMAGE_WIDTH, OUTPUT_IMAGE_HEIGHT))

//        val elapsedTimeCropRsKernel = measureTimeMillis {
//            resizer.cropRsKernel(originalBitMap, OUTPUT_IMAGE_WIDTH, OUTPUT_IMAGE_HEIGHT, 0, 0)
//        }
//        println("cropRsKernel $elapsedTimeCropRsKernel")
//        changedList.add(resizer.cropRsKernel(originalBitMap, OUTPUT_IMAGE_WIDTH, OUTPUT_IMAGE_HEIGHT, 0, 0))
        adapterData.value = changedList

    }

    private fun startScale() {
        val elapsedTimeScaleCommon = measureTimeMillis {
            scaler.scaleCommon(originalBitMap, SCALE_FACTOR)
        }
        println("scaleCommon $elapsedTimeScaleCommon")
        changedList.add(scaler.scaleCommon(originalBitMap, SCALE_FACTOR))
        adapterData.value = changedList
    }

    private fun startCompress() {
        val elapsedTimeCompressCommon = measureTimeMillis {
            scaler.compressCommon(originalBitMap, QUALITY)
        }
        println("compressCommon $elapsedTimeCompressCommon")
        changedList.add(scaler.compressCommon(originalBitMap, QUALITY))
        adapterData.value = changedList
    }

    private fun startContrast() {
        val elapsedTimeContrastCommon = measureTimeMillis {
            fiterManager.filterContrastCommon(originalBitMap, CONTRAST, BRIGHTNESS)
        }
        println("filterContrastCommon $elapsedTimeContrastCommon")
        changedList.add(fiterManager.filterContrastCommon(originalBitMap, CONTRAST, BRIGHTNESS))

        val elapsedTimeBrightnessColorMatrix = measureTimeMillis {
            fiterManager.filterBrightnessColorMatrix(originalBitMap, BRIGHTNESS_RS)
        }
        println("filterBrightnessColorMatrix $elapsedTimeBrightnessColorMatrix")
        changedList.add(fiterManager.filterBrightnessColorMatrix(originalBitMap, BRIGHTNESS_RS))
        adapterData.value = changedList
    }

    private fun takeImage() {
        lifecycleScope.launchWhenStarted {
            getTmpFileUri().let { uri ->
                latestTmpUri = uri
                takeImageResult.launch(uri)
            }
        }
    }

    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch(arrayOf("image/*"))

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".png", cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(applicationContext, "com.example.resizemodule.provider", tmpFile)
    }

    companion object {
        val OUTPUT_IMAGE_WIDTH = 240
        val OUTPUT_IMAGE_HEIGHT = 180
        val SCALE_FACTOR = 0.5
        val QUALITY = 50
        val BRIGHTNESS = 10f
        val BRIGHTNESS_RS = 50f
        val CONTRAST = 5f
    }
}