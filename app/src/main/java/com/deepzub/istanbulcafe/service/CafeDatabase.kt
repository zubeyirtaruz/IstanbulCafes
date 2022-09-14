package com.deepzub.istanbulcafe.service

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.deepzub.istanbulcafe.model.Cafe
import com.deepzub.istanbulcafe.model.MyFavorite

@Database(entities = [Cafe::class, MyFavorite::class], version = 1)
abstract class CafeDatabase : RoomDatabase() {

    abstract fun cafeDao(): CafeDao

    companion object{
        @Volatile private var instance : CafeDatabase? = null
        private val lock = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(lock) {
            instance ?: makeDatabase(context).also {
                instance = it
            }
        }

        private fun makeDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,CafeDatabase::class.java,"IstanbulCafes"
        ).build()

    }

}