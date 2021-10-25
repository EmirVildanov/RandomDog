package com.vildanov.randomdog.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface VideoDao {
    @Query("select * from databasedogimage")
    fun getDogImages(): LiveData<List<DatabaseDogImage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg dogImages: DatabaseDogImage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(dogImage: DatabaseDogImage)
}

@Database(entities = [DatabaseDogImage::class], version = 1)
abstract class DogImagesDatabase : RoomDatabase() {
    abstract val dogImageDao: VideoDao
}

private lateinit var INSTANCE: DogImagesDatabase

fun getDatabase(context: Context): DogImagesDatabase {
    synchronized(DogImagesDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext, DogImagesDatabase::class.java, "dogImages").build()
        }
    }
    return INSTANCE
}