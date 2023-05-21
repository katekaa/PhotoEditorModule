package com.example.resizemodule

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ImageAdapter: ListAdapter<Bitmap, ImageAdapter.ImageViewHolder>(BitmapDiffCallback()) {
//    private var data = listOf<Bitmap>()
//    fun setData(list: List<Bitmap>) {
//        data = list
//    }
    class ImageViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val imageView = view.findViewById<ImageView>(R.id.changed_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.image,
            parent,
            false
        )
        return ImageViewHolder(view)
    }


    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val img = getItem(position)
        println("img $img")
        holder.imageView.setImageBitmap(img)
    }
}

class BitmapDiffCallback : DiffUtil.ItemCallback<Bitmap>() {
    override fun areItemsTheSame(oldItem: Bitmap, newItem: Bitmap): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Bitmap, newItem: Bitmap): Boolean {
        return oldItem.sameAs(newItem)
    }

}