package com.example.gr34_in2000_v21.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.gr34_in2000_v21.data.models.GeoNorgeModel
import com.example.gr34_in2000_v21.utils.Helpers

@Database(
    entities = [GeoNorgeModel.CountyCoordinates::class],
    version = 4
)
@TypeConverters(
    Helpers.GeoNorge.CountyCoordinatesTypeConverter::class
)
abstract class PersistentDatabase : RoomDatabase() {
    abstract fun geonorgeDao(): GeoNorgeDao

    companion object {
        fun create(context: Context): PersistentDatabase {

            val databaseBuilder =
                Room.databaseBuilder(context, PersistentDatabase::class.java, "persistent-database")
                    .fallbackToDestructiveMigration()
            return databaseBuilder.build()
        }
    }
}