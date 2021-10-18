package com.example.randomdog.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.randomdog.data.DownloadedTextData
import com.example.randomdog.network.RandomTextApi
import io.ktor.util.*
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi

class SettingsViewModel : ViewModel() {

    private var viewModelJob = SupervisorJob()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _downloadedTexts = MutableLiveData<List<DownloadedTextData>>(emptyList())
    val downloadedTexts: LiveData<List<DownloadedTextData>>
        get() = _downloadedTexts

    @KtorExperimentalAPI
    @ExperimentalSerializationApi
    fun downloadNewText() {
        coroutineScope.launch {
            val newDownloadedText = RandomTextApi.getNewText()
            newDownloadedText?.let { _downloadedTexts.postValue(_downloadedTexts.value!!.plus(it)) }
        }
    }
}