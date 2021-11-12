package com.vildanov.randomdog.ui.library

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.vildanov.randomdog.R
import com.vildanov.randomdog.constants.DOG_IMAGES_FOLDER_NAME
import com.vildanov.randomdog.data.DisplayingDogImageInfo
import com.vildanov.randomdog.databinding.FragmentLibraryBinding
import com.vildanov.randomdog.ui.settings.DownloadedDogImageAdapter
import com.vildanov.randomdog.ui.settings.DownloadedDogImageListener
import com.vildanov.randomdog.utils.combine
import timber.log.Timber
import java.io.File
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.text.SimpleDateFormat
import java.util.*
import androidx.recyclerview.widget.DividerItemDecoration
import com.vildanov.randomdog.ui.LocalizedTitleFragment
import com.vildanov.randomdog.utils.extractExtension


class LibraryFragment : LocalizedTitleFragment(R.string.library_fragment_label) {

    private val viewModel: LibraryViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        ViewModelProvider(this, LibraryViewModel.Factory(activity.application)).get(
            LibraryViewModel::class.java
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentLibraryBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_library,
            container,
            false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val adapter = DownloadedDogImageAdapter(DownloadedDogImageListener { file: File ->
            val onConfirm = {

            }

            val onShare = {
                val filePath = file.absolutePath
                Timber.i("FILE PATH: $filePath")
                Timber.i("FILE EXTENSION: ${extractExtension(filePath)}")
                val shareIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, Uri.parse(filePath))
                    type = "image/${extractExtension(filePath)}"
                }
                startActivity(Intent.createChooser(shareIntent, resources.getText(R.string.send_to)))
            }
            activity?.supportFragmentManager?.let {
                DogImagePopupDialogFragment.newInstance(file, onShare, onConfirm)
                    .show(it, DogImagePopupDialogFragment.TAG)
            }
        }, ::getDogImageInfoByName)

        viewModel.library.observe(viewLifecycleOwner, { dogImages ->
            Timber.i("Observing dog images: $dogImages")
            dogImages?.let {
                adapter.submitList(dogImages)
            }
        })

        binding.downloadedPhotosRecyclerView.adapter = adapter
        binding.downloadedPhotosRecyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        return binding.root
    }

    private fun getDogImageInfoByName(fileName: String): DisplayingDogImageInfo {
        val pathToDogImagesFolder = combine(
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString(),
            DOG_IMAGES_FOLDER_NAME
        )
        val file = File(pathToDogImagesFolder)
        val listOfFiles = file.listFiles()?.toList()
            ?: throw IllegalStateException("Images directory wasn't found")
        Timber.i("Downloaded images: $listOfFiles")

        val currentDbDogImages =
            viewModel.library.value ?: throw IllegalStateException("No images list provided")
        assert(listOfFiles.size == currentDbDogImages.size) { "Number of values in db: ${currentDbDogImages.size}. In fact: ${listOfFiles.size}" }

        if (fileName in listOfFiles.map { it.name }) {
            val currentFile = listOfFiles.find { it.name == fileName }!!
            val lastModified = currentFile.lastModified()
            val date = Date(lastModified)
            val format = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.US)
            return DisplayingDogImageInfo(
                fileName = fileName,
                description = fileName,
                lastModified = format.format(date),
                file = currentFile
            )
        }
        throw IllegalArgumentException("There is no saved file with name \"$fileName\"")
    }
}