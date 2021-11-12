package com.vildanov.randomdog.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.vildanov.randomdog.MainActivity
import timber.log.Timber

open class LocalizedTitleFragment(private val titleStringId: Int) : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolBar = (requireActivity() as MainActivity).supportActionBar
        if (toolBar == null) {
            Timber.e("Null toolBar onViewCreated")
        } else {
            toolBar.title = getString(titleStringId)
        }
    }
}