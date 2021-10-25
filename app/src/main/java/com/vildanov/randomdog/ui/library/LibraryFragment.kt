package com.vildanov.randomdog.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.vildanov.randomdog.R
import com.vildanov.randomdog.databinding.FragmentHomeBinding
import com.vildanov.randomdog.databinding.FragmentLibraryBinding
import com.vildanov.randomdog.ui.home.HomeViewModel
import com.vildanov.randomdog.ui.home.TrackPlayer
import com.vildanov.randomdog.ui.settings.DownloadedTextAdapter
import com.vildanov.randomdog.ui.settings.DownloadedTextListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber

class LibraryFragment : Fragment() {

    private lateinit var viewModel: LibraryViewModel

    private var viewModelJob = SupervisorJob()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

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

        viewModel = ViewModelProvider(this).get(LibraryViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val adapter = DownloadedTextAdapter(DownloadedTextListener { textId ->
            Timber.i("Text with $textId id was clicked")
        })

        viewModel.downloadedImages.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        return binding.root
    }
}