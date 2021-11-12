package com.vildanov.randomdog.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DatabaseDogImage constructor(
    @PrimaryKey val url: String,
    val name: String,
    val description: String,
    val breed: String = ""
)
