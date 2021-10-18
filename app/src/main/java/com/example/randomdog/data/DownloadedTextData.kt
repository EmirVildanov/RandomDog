package com.example.randomdog.data

import kotlinx.serialization.Serializable

@Serializable
data class DownloadedTextData(val status: String, val text: String)
