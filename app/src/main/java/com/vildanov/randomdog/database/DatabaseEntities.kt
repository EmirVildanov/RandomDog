package com.vildanov.randomdog.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vildanov.randomdog.domain.DogImage

@Entity
data class DatabaseDogImage constructor(
    @PrimaryKey
    val url: String,
    val description: String,
    val thumbnail: String,
    val dateOfAdding: String
)

fun List<DatabaseDogImage>.asDomainModel(): List<DogImage> {
    return map {
        DogImage(
            url = it.url,
            description = it.description,
            thumbnail = it.thumbnail,
            dateOfAdding = it.dateOfAdding
        )
    }
}