package com.vildanov.randomdog.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface VideoDao {
    @Query("SELECT * FROM databasedogimage")
    fun getDogImages(): List<DatabaseDogImage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg dogImages: DatabaseDogImage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(dogImage: DatabaseDogImage)
}

@Database(
    version = 1,
    entities = [DatabaseDogImage::class],
)
abstract class DogImagesDatabase : RoomDatabase() {
    abstract val dogImageDao: VideoDao
}

private lateinit var INSTANCE: DogImagesDatabase

fun getDatabase(context: Context): DogImagesDatabase {
    synchronized(DogImagesDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext, DogImagesDatabase::class.java, "dog-images").build()
        }
    }
    return INSTANCE
}