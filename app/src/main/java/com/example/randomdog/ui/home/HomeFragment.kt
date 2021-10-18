package com.example.randomdog.ui.home

import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.example.randomdog.MainActivity
import com.example.randomdog.R
import com.example.randomdog.databinding.FragmentHomeBinding
import io.ktor.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import timber.log.Timber
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private val trackPlayer = TrackPlayer()

    private var viewModelJob = SupervisorJob()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    @ExperimentalSerializationApi
    @KtorExperimentalAPI
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentHomeBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_home,
            container,
            false
        )

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = activity

        binding.loadNewImageButton.setOnClickListener {
            playBarkSound()
            val currentTime = Calendar.getInstance().time
            Timber.i("Date: $currentTime")
            viewModel.getNewImageUrl()
        }

        binding.copyToClipboardButton.setOnClickListener {
            val clipboard =
                requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied Text", viewModel.currentDogPictureData.value!!.url)
            clipboard.setPrimaryClip(clip)
        }

        binding.nicknameTextView.movementMethod = LinkMovementMethod.getInstance()

        viewModel.currentDogPictureData.observe(viewLifecycleOwner, { pictureData ->

            val options: RequestOptions = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.dog_image)
                .error(R.drawable.ic_baseline_error_24)
                .priority(Priority.HIGH)

            GlideImageLoader(binding.imageViewOfADog, binding.imageLoadingProgressBar, binding.loadNewImageButton, binding.copyToClipboardButton).load(pictureData.url, options)
//            GlideApp.with(this)
//                .load(pictureData.url)
//                .into(binding.imageViewOfADog);
        })

        return binding.root
    }

    private fun playBarkSound() {
        coroutineScope.launch {
            val activity = activity as MainActivity
            val mediaPlayer = activity.mediaPlayer
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.reset()
            val afd = requireActivity().resources.openRawResourceFd(trackPlayer.getNextTrackId())
            if (afd != null) {
                mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                mediaPlayer.prepare()
                mediaPlayer.start()
//                afd.close()
            } else {
                Timber.e("Afd is null")
            }
        }
    }
}

class TrackPlayer {
    private val trackIds = listOf(R.raw.bark1, R.raw.bark2, R.raw.bark3)
    private var currentTrackIndex = 0

    fun getNextTrackId(): Int {
        val currentTrackId = trackIds[currentTrackIndex]
        currentTrackIndex = (currentTrackIndex + 1) % trackIds.size
        return currentTrackId
    }
}