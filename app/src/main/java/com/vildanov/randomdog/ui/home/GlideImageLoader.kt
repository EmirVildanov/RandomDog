package com.vildanov.randomdog.ui.home

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.Button
import android.widget.ImageView

import com.bumptech.glide.load.engine.GlideException

import com.bumptech.glide.request.RequestListener

import com.bumptech.glide.Glide

import com.bumptech.glide.request.RequestOptions

import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade


class GlideImageLoader(
    private val imageView: ImageView,
    private val progressBar: ProgressBar,
    private val downloadButton: Button,
    private val copyToClipboardButton: Button,
    private val shareButton: Button,
    private val buttonsInteractionLayout: ConstraintLayout,
    ) {
    fun load(url: String?, options: RequestOptions?) {
        if (url == null || options == null) return
        onConnecting()

        //set Listener & start
        CustomGlideModule.expect(url, object : UIonProgressListener {
            override fun onProgress(bytesRead: Long, expectedLength: Long) {
                progressBar.progress = (100 * bytesRead / expectedLength).toInt()
            }

            override val granualityPercentage: Float
                get() = 1.0f
        })
        //Get Image
        Glide.with(imageView.context)
            .load(url)
            .transition(withCrossFade())
            .apply(options.skipMemoryCache(true))
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    CustomGlideModule.forget(url)
                    onFinished()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    CustomGlideModule.forget(url)
                    onFinished()
                    return false
                }
            })
            .into(imageView)
    }

    private fun onConnecting() {
        progressBar.visibility = View.VISIBLE
        buttonsInteractionLayout.visibility = View.GONE
    }

    private fun onFinished() {
        progressBar.progress = 0
        progressBar.visibility = View.GONE
        buttonsInteractionLayout.visibility = View.VISIBLE
        copyToClipboardButton.isEnabled = true
        shareButton.isEnabled = true
        imageView.visibility = View.VISIBLE
        downloadButton.isEnabled = true
    }

}