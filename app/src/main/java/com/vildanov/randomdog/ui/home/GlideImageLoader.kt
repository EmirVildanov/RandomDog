package com.vildanov.randomdog.ui.home

import android.app.Activity
import android.graphics.drawable.Drawable
import android.widget.ImageView

import com.bumptech.glide.load.engine.GlideException

import com.bumptech.glide.request.RequestListener

import com.bumptech.glide.Glide

import com.bumptech.glide.request.RequestOptions

import android.widget.ProgressBar
import com.bumptech.glide.load.DataSource
import android.graphics.Bitmap
import com.bumptech.glide.request.target.CustomTarget

import com.bumptech.glide.request.transition.Transition

import android.os.Environment
import com.vildanov.randomdog.constants.DOG_IMAGES_FOLDER_NAME
import com.vildanov.randomdog.utils.combine
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.lang.IllegalStateException


class GlideImageLoader(
    private val imageView: ImageView,
    private val progressBar: ProgressBar,
    private val onConnecting: () -> Unit,
    private val onFinished: () -> Unit,
    private val onResourceReady: (Bitmap, String) -> Unit
) {
    fun load(url: String?, options: RequestOptions?) {
        if (url == null || options == null) return
        onConnecting()

        CustomGlideModule.expect(url, object : UIonProgressListener {
            override fun onProgress(bytesRead: Long, expectedLength: Long) {
                progressBar.progress = (100 * bytesRead / expectedLength).toInt()
            }

            override val granualityPercentage: Float
                get() = 1.0f
        })
        Glide.with(imageView.context)
            .asBitmap()
            .load(url)
            .listener(object : RequestListener<Bitmap?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Bitmap?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    CustomGlideModule.forget(url)
                    onFinished()
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Bitmap?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    CustomGlideModule.forget(url)
                    onFinished()
                    return false
                }
            })
            .into(
                object : CustomTarget<Bitmap?>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap?>?
                    ) {
                        imageView.setImageBitmap(resource)
                        onResourceReady(resource, url)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                }
            )
    }
}