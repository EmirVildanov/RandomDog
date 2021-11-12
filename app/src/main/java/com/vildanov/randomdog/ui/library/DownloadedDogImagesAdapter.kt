package com.vildanov.randomdog.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vildanov.randomdog.R
import com.vildanov.randomdog.data.DisplayingDogImageInfo
import com.vildanov.randomdog.database.DatabaseDogImage
import com.vildanov.randomdog.databinding.ItemDownloadedDogImageBinding
import java.io.File

class DownloadedDogImageAdapter(private val clickListener: DownloadedDogImageListener, private val getDogImageInfoByName: (String) -> DisplayingDogImageInfo) :
    ListAdapter<DatabaseDogImage, DownloadedDogImageAdapter.ViewHolder>(DownloadedDogImageItemDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, getDogImageInfoByName)
    }

    class ViewHolder (val binding: ItemDownloadedDogImageBinding, private val getDogImageInfoByName: (String) -> DisplayingDogImageInfo ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: DatabaseDogImage,
            clickListener: DownloadedDogImageListener,
        ) {
            val dogInfo = getDogImageInfoByName(item.name)
            Glide.with(binding.downloadedDogImageView.context)
                .load(dogInfo.file)
                .placeholder(R.drawable.ic_baseline_photo_library_24)
                .error(R.drawable.ic_baseline_error_24)
                .into(binding.downloadedDogImageView)
            binding.displayingDogImageInfo = dogInfo
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup, getDogImageInfoByName: (String) -> DisplayingDogImageInfo): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDownloadedDogImageBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, getDogImageInfoByName)
            }
        }
    }
}

class DownloadedDogImageItemDiffCallback : DiffUtil.ItemCallback<DatabaseDogImage>() {
    override fun areItemsTheSame(oldItem: DatabaseDogImage, newItem: DatabaseDogImage): Boolean {
        return oldItem.url == newItem.url
    }

    override fun areContentsTheSame(oldItem: DatabaseDogImage, newItem: DatabaseDogImage): Boolean {
        return oldItem == newItem
    }
}

class DownloadedDogImageListener(val listener: (file: File) -> Unit) {
    fun onClick(item: DisplayingDogImageInfo) = listener(item.file)
}