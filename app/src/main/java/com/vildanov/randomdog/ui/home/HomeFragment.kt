package com.vildanov.randomdog.ui.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.vildanov.randomdog.MainActivity
import com.vildanov.randomdog.R
import com.vildanov.randomdog.RandomDogApplication
import com.vildanov.randomdog.databinding.FragmentHomeBinding
import io.ktor.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import timber.log.Timber
import java.util.*
import android.content.Intent


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
            binding.loadNewImageButton.isEnabled = false
            binding.copyToClipboardButton.isEnabled = false
            binding.shareButton.isEnabled = false
            playBarkSound()
            val activity = requireActivity()
            val application = activity.application as RandomDogApplication
            if (!application.isInternetAvailable(activity)) {
                application.showToast(activity, getString(R.string.internet_is_not_available))
            } else {
                val currentTime = Calendar.getInstance().time
                Timber.i("Date: $currentTime")
                viewModel.getNewImageUrl()
            }
        }

        binding.copyToClipboardButton.setOnClickListener {
            val clipboard =
                requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip =
                ClipData.newPlainText("Copied Text", viewModel.currentDogPictureData.value!!.url)
            clipboard.setPrimaryClip(clip)
            val application = requireActivity().application as RandomDogApplication
            application.showToast(requireActivity(), getString(R.string.link_copied_to_clipboard))
        }

        binding.shareButton.setOnClickListener {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            val shareBody = viewModel.currentDogPictureData.value!!.url
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Cute dog url")
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(sharingIntent, "Share via"))
        }

        binding.nicknameTextView.movementMethod = LinkMovementMethod.getInstance()

        viewModel.currentDogPictureData.observe(viewLifecycleOwner, { pictureData ->
            val options: RequestOptions = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.dog_image)
                .error(R.drawable.ic_baseline_error_24)
                .priority(Priority.HIGH)

            GlideImageLoader(
                binding.imageViewOfADogInside,
                binding.imageLoadingProgressBar,
                binding.loadNewImageButton,
                binding.copyToClipboardButton,
                binding.shareButton,
                binding.interactionButtonsLayout
            ).load(pictureData.url, options)
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
            } else {
                Timber.e("Afd is null")
            }
        }
    }
}

class TrackPlayer {
    private val trackIds = listOf(
        R.raw.bark1,
        R.raw.bark2,
        R.raw.bark3,
        R.raw.bark4,
        R.raw.bark5,
        R.raw.bark6,
        R.raw.bark7,
        R.raw.bark8,
        R.raw.bark9,
    )
    private var currentTrackIndex = 0

    fun getNextTrackId(): Int {
        val currentTrackId = trackIds[currentTrackIndex]
        currentTrackIndex = (currentTrackIndex + 1) % trackIds.size
        return currentTrackId
    }
}