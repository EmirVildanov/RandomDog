package com.vildanov.randomdog.ui.home

import android.content.Context;
import android.os.Handler


import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import java.io.InputStream;
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

import android.os.Looper
import okhttp3.*
import okio.*
import java.util.*

import okhttp3.OkHttpClient
import timber.log.Timber


/*
    Custom Glide module with
    1) Downloading progress handling
    2) Dealing with Glide SSLHandshakeException
 */
@GlideModule
final class CustomGlideModule : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val okHttpClient = OkHttpClient()
        registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            OkHttpUrlLoader.Factory(okHttpClient)
        )
    }

    companion object {
        fun forget(url: String?) {
            DispatchingProgressListener.forget(url)
        }

        fun expect(url: String?, listener: UIonProgressListener?) {
            DispatchingProgressListener.expect(url, listener)
        }
    }
}

private interface ResponseProgressListener {
    fun update(url: HttpUrl?, bytesRead: Long, contentLength: Long)
}

interface UIonProgressListener {
    fun onProgress(bytesRead: Long, expectedLength: Long)

    /**
     * Control how often the listener needs an update. 0% and 100% will always be dispatched.
     * @return in percentage (0.2 = call [.onProgress] around every 0.2 percent of progress)
     */
    val granualityPercentage: Float
}

private class DispatchingProgressListener() : ResponseProgressListener {
    private val handler: Handler = Handler(Looper.getMainLooper())
    override fun update(url: HttpUrl?, bytesRead: Long, contentLength: Long) {
        //System.out.printf("%s: %d/%d = %.2f%%%n", url, bytesRead, contentLength, (100f * bytesRead) / contentLength);
        val key = url.toString()
        val listener: UIonProgressListener = LISTENERS[key]
            ?: return
        if (contentLength <= bytesRead) {
            forget(key)
        }
        if (needsDispatch(key, bytesRead, contentLength, listener.granualityPercentage)) {
            handler.post { listener.onProgress(bytesRead, contentLength) }
        }
    }

    private fun needsDispatch(
        key: String,
        current: Long,
        total: Long,
        granularity: Float
    ): Boolean {
        if (granularity == 0f || current == 0L || total == current) {
            return true
        }
        val percent = 100f * current / total
        val currentProgress = (percent / granularity).toLong()
        val lastProgress = PROGRESSES[key]
        return if (lastProgress == null || currentProgress != lastProgress) {
            PROGRESSES.put(key, currentProgress)
            true
        } else {
            false
        }
    }

    companion object {
        private val LISTENERS: WeakHashMap<String, UIonProgressListener> = WeakHashMap()
        private val PROGRESSES: WeakHashMap<String, Long> = WeakHashMap()
        fun forget(url: String?) {
            LISTENERS.remove(url)
            PROGRESSES.remove(url)
        }

        fun expect(url: String?, listener: UIonProgressListener?) {
            LISTENERS.put(url, listener)
        }
    }
}

private class OkHttpProgressResponseBody(private val url: HttpUrl, private val responseBody: ResponseBody, private val progressListener: ResponseProgressListener) : ResponseBody() {
    private var bufferedSource: BufferedSource? = null
    override fun contentType(): MediaType? {
        return responseBody.contentType()
    }

    override fun contentLength(): Long {
        return responseBody.contentLength()
    }

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()))
        }
        return bufferedSource!!
    }

    private fun source(source: Source): Source {
        return object : ForwardingSource(source) {
            var totalBytesRead = 0L

            override fun read(sink: Buffer?, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                val fullLength = responseBody.contentLength()
                if (bytesRead == -1L) { // this source is exhausted
                    totalBytesRead = fullLength
                } else {
                    totalBytesRead += bytesRead
                }
                progressListener.update(url, totalBytesRead, fullLength)
                return bytesRead
            }
        }
    }
}