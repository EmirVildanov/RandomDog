package com.vildanov.randomdog.ui.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

class SettingsViewModel : ViewModel() {

    private var viewModelJob = SupervisorJob()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)
}