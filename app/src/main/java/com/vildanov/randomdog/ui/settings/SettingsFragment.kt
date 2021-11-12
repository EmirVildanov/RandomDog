package com.vildanov.randomdog.ui.settings

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.vildanov.randomdog.MainActivity
import com.vildanov.randomdog.R
import com.vildanov.randomdog.RandomDogApplication
import com.vildanov.randomdog.data.Language
import com.vildanov.randomdog.databinding.FragmentSettingsBinding
import com.vildanov.randomdog.utils.LocalizedTitleFragment
import timber.log.Timber


class SettingsFragment : LocalizedTitleFragment(R.string.settings_fragment_label) {

    private lateinit var viewModel: SettingsViewModel

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

        binding.siteLinkTextView.movementMethod = LinkMovementMethod.getInstance()

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