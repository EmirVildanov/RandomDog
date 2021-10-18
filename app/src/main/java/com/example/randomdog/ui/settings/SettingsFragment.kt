package com.example.randomdog.ui.settings

import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.randomdog.MainActivity
import com.example.randomdog.R
import com.example.randomdog.data.Language
import com.example.randomdog.RandomDogApplication
import com.example.randomdog.databinding.FragmentSettingsBinding
import io.ktor.util.*
import kotlinx.serialization.ExperimentalSerializationApi
import timber.log.Timber
import java.lang.NullPointerException


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

        val languageOptionDialogBuilder = activity?.let {
            androidx.appcompat.app.AlertDialog.Builder(
                ContextThemeWrapper(this.requireActivity(), R.style.Theme_RandomDog_AlertDialog)
            )
        }
        val languages = Language.values()
        val languagesOptionDialog = languageOptionDialogBuilder?.setTitle(R.string.choose_language)
            ?.setItems(languages.map { it.languageName }.toTypedArray()) { dialog, which ->
                RandomDogApplication().changeLanguage(
                    activity,
                    languages[which]
                )
                restartFragment()
                dialog.cancel()
            }

        binding.changeLanguageButton.setOnClickListener {
            languagesOptionDialog?.show()
        }

        binding.githubLinkTextView.movementMethod = LinkMovementMethod.getInstance()
        binding.siteLinkTextView.movementMethod = LinkMovementMethod.getInstance()

        val adapter = DownloadedTextAdapter(DownloadedTextListener { textId ->
            Timber.i("Text with $textId id was clicked")
        })

        viewModel.downloadedTexts.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        return binding.root
    }

    private fun restartFragment() {
        try {
            (requireActivity() as MainActivity).restartFragment(id)
        } catch (e: NullPointerException) {
            Timber.i("Can not restart SettingsFragment")
        }
    }
}