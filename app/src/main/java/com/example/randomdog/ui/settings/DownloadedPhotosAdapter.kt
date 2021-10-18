package com.example.randomdog.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.randomdog.data.DownloadedTextData
import com.example.randomdog.databinding.ItemDownloadedTextBinding

class DownloadedTextAdapter(private val clickListener: DownloadedTextListener) :
    ListAdapter<DownloadedTextData, DownloadedTextAdapter.ViewHolder>(DownloadedTextItemDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ItemDownloadedTextBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: DownloadedTextData,
            clickListener: DownloadedTextListener,
        ) {
            binding.downloadedTextItem = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDownloadedTextBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class DownloadedTextItemDiffCallback : DiffUtil.ItemCallback<DownloadedTextData>() {
    override fun areItemsTheSame(oldItem: DownloadedTextData, newItem: DownloadedTextData): Boolean {
        return oldItem.text == newItem.text
    }

    override fun areContentsTheSame(oldItem: DownloadedTextData, newItem: DownloadedTextData): Boolean {
        return oldItem == newItem
    }
}

class DownloadedTextListener(val listener: (textText: String) -> Unit) {
    fun onClick(item: DownloadedTextData) = listener(item.text)
}