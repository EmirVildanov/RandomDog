package com.example.randomdog.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.randomdog.R
import com.example.randomdog.databinding.FragmentSettingsBinding
import io.ktor.util.*
import kotlinx.serialization.ExperimentalSerializationApi
import timber.log.Timber

class SettingsFragment : Fragment() {

    private lateinit var viewModel: SettingsViewModel

    @ExperimentalSerializationApi
    @KtorExperimentalAPI
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentSettingsBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_settings,
            container,
            false
        )

        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this


//        binding.nextFragmentButton.setOnClickListener {
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
//        }

//        binding.downloadNewTextButton.setOnClickListener {
//            viewModel.downloadNewText()
//        }


        val adapter = DownloadedTextAdapter(DownloadedTextListener {
                textId -> Timber.i("Inventory item with $textId was clicked")
        })
        binding.downloadedTextRecyclerView.adapter = adapter

        viewModel.downloadedTexts.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                binding.textViewEmptyDownloadedRecyclerView.visibility = View.GONE
                binding.downloadedTextRecyclerView.visibility = View.VISIBLE
            }
            it?.let {
                adapter.submitList(it)
            }
        })

        return binding.root
    }
}