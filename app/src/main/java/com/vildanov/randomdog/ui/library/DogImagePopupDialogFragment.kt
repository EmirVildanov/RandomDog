package com.vildanov.randomdog.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.vildanov.randomdog.R
import com.vildanov.randomdog.databinding.FragmentImagePopupDialogBinding
import java.io.File

class DogImagePopupDialogFragment(private val imageFile: File, val onShare: () -> Unit, val onClose: () -> Unit): DialogFragment() {
    companion object {
        const val TAG = "DogImagePopupDialogFragment"

        fun newInstance(imageFile: File, onShare: () -> Unit, onConfirm: () -> Unit): DogImagePopupDialogFragment {
            val args = Bundle()
            val fragment = DogImagePopupDialogFragment(imageFile, onShare, onConfirm)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding: FragmentImagePopupDialogBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_image_popup_dialog,
            container,
            false
        )

        Glide.with(binding.openedDogImageView.context)
            .load(imageFile)
            .placeholder(R.drawable.ic_baseline_photo_library_24)
            .error(R.drawable.ic_baseline_error_24)
            .into(binding.openedDogImageView)

        binding.shareButton.setOnClickListener {
            onShare()
        }

        binding.closeButton.setOnClickListener {
            onClose()
            dismiss()
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }
}