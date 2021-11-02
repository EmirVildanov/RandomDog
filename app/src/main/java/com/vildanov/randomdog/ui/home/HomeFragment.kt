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
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import androidx.preference.PreferenceManager
import com.vildanov.randomdog.constants.DOG_IMAGES_FOLDER_NAME
import com.vildanov.randomdog.database.getDatabase
import com.vildanov.randomdog.repository.DogImagesRepository
import com.vildanov.randomdog.utils.combine
import com.vildanov.randomdog.utils.extractExtension
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.lang.IllegalStateException
import android.provider.MediaStore
import java.io.ByteArrayOutputStream


class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private val trackPlayer = TrackPlayer()

    private var fragmentJob = SupervisorJob()
    private val coroutineScope = CoroutineScope(fragmentJob + Dispatchers.Main)

    private lateinit var glideImageLoader: GlideImageLoader
    private var currentDownloadedImageInfo: Pair<Bitmap, String>? = null

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

        val onConnecting = {
            binding.imageLoadingProgressBar.visibility = View.VISIBLE
            binding.interactionButtonsLayout.visibility = View.GONE
        }

        val onFinished = {
            binding.imageLoadingProgressBar.progress = 0
            binding.imageLoadingProgressBar.visibility = View.GONE
            binding.interactionButtonsLayout.visibility = View.VISIBLE
            binding.copyToClipboardButton.isEnabled = true
            binding.shareButton.isEnabled = true
            binding.downloadToLibraryButton.isEnabled = true
            binding.loadNewImageButton.isEnabled = true
            binding.imageViewOfADogInside.visibility = View.VISIBLE
        }

        val onResourceReady = { resource: Bitmap, url: String ->
            currentDownloadedImageInfo = resource to url
        }

        glideImageLoader = GlideImageLoader(
            binding.imageViewOfADogInside,
            binding.imageLoadingProgressBar,
            onConnecting,
            onFinished,
            onResourceReady
        )

        binding.loadNewImageButton.setOnClickListener {
            binding.loadNewImageButton.isEnabled = false
            binding.copyToClipboardButton.isEnabled = false
            binding.shareButton.isEnabled = false
            binding.downloadToLibraryButton.isEnabled = false
            playBarkSound()
            val activity = requireActivity()
            val application = activity.application as RandomDogApplication
            if (!application.isInternetAvailable(activity)) {
                application.showToast(activity, getString(R.string.internet_is_not_available))
                binding.loadNewImageButton.isEnabled = true
                binding.copyToClipboardButton.isEnabled = true
                binding.shareButton.isEnabled = true
                binding.downloadToLibraryButton.isEnabled = true
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
//            val sharingIntent = Intent(Intent.ACTION_SEND)
//            sharingIntent.type = "text/plain"
//            val shareBody = viewModel.currentDogPictureData.value!!.url
//            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Cute dog url")
//            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
//            startActivity(Intent.createChooser(sharingIntent, "Share via"))
//
            val bytes = ByteArrayOutputStream()
            val bitmap = currentDownloadedImageInfo!!.first
            val currentItemExtension = extractExtension(currentDownloadedImageInfo!!.second)

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path: String = MediaStore.Images.Media.insertImage(
                requireActivity().getContentResolver(),
                bitmap,
                "DownloadedDogImage",
                null
            )
            val uriToImage = Uri.parse(path)
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uriToImage)
                type = "image/$currentItemExtension"
            }
            startActivity(Intent.createChooser(shareIntent, resources.getText(R.string.send_to)))
        }

        binding.downloadToLibraryButton.setOnClickListener {
            binding.downloadToLibraryButton.isEnabled = false
            val currentDownloadedImageSnapshot = viewModel.currentDogPictureData.value
                ?: throw IllegalStateException("Null dog image data")
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity())

            val savedImagesNumber =
                (sharedPreferences.getString(getString(R.string.saved_dog_images_number_key), null)
                    ?: "0").toInt()
            val nextFileIndex = (savedImagesNumber + 1).toString()

            with(sharedPreferences.edit()) {
                putString(activity?.getString(R.string.saved_dog_images_number_key), nextFileIndex)
                apply()
            }

            val currentItemExtension = extractExtension(currentDownloadedImageSnapshot.url)
            val nextFileName = "DogItem$nextFileIndex.$currentItemExtension"

            coroutineScope.launch {
                val database = getDatabase(requireActivity())
                val dogImagesRepository = DogImagesRepository(database)
                dogImagesRepository.addImage(currentDownloadedImageSnapshot, nextFileName, "TEST_DESCRIPTION")
            }
            saveImage(nextFileName)

            val application = requireActivity().application as RandomDogApplication
            application.showToast(requireActivity(), getString(R.string.image_saved))
        }

        binding.nicknameTextView.movementMethod = LinkMovementMethod.getInstance()

        viewModel.currentDogPictureData.observe(viewLifecycleOwner, { pictureData ->
            val options: RequestOptions = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.dog_image)
                .error(R.drawable.ic_baseline_error_24)
                .priority(Priority.HIGH)

            glideImageLoader.load(pictureData.url, options)
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

    private fun saveImage(fileName: String): String? {
        val currentDownloadedImageInfoSnapshot = currentDownloadedImageInfo
            ?: throw IllegalStateException("There is no image info to save")
        var savedImagePath: String? = null
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val storageDir = File(
                combine(
                    requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                        .toString(), DOG_IMAGES_FOLDER_NAME
                )
            )
            var success = true
            if (!storageDir.exists()) {
                success = storageDir.mkdirs()
            }
            if (success) {
                val imageFile = File(storageDir, fileName)
                savedImagePath = imageFile.absolutePath
                try {
                    val fOut = FileOutputStream(imageFile)
                    currentDownloadedImageInfoSnapshot.first.compress(
                        Bitmap.CompressFormat.JPEG,
                        100,
                        fOut
                    )
                    fOut.close()
                    Timber.i("DOG IMAGE SAVED")
                } catch (e: Exception) {
                    e.printStackTrace()
                    Timber.i("DOG IMAGE WAS NOT SAVED")
                }
            }
        }
        return savedImagePath
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