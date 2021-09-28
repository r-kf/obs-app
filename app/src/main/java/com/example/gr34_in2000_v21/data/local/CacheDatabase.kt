package com.example.gr34_in2000_v21.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.gr34_in2000_v21.data.models.MetAlertsModel
import com.example.gr34_in2000_v21.utils.Helpers

/**
 *  APP DATABASE
 *  entities - Data classes of API responses
 *  version - DO NOT TOUCH
 *  exportSchema - DO NOT TOUCH
 *
 *  For every new Dao-object, place it in as:
 *      abstract fun <name-of-data-class>Dao(): <dao-object>.<name-of-dao-interface>
 */
@Database(
    entities = [
        MetAlertsModel.Item::class,
        MetAlertsModel.CAPAlert::class,
    ],
    version = 12,
    exportSchema = false
)
@TypeConverters(
    Helpers.MetAlerts.CAPInfoTypeConverter::class,
)
abstract class CacheDatabase : RoomDatabase() {
    abstract fun metalertsDao(): MetAlertsDao
    abstract fun generalDao(): GeneralDao

    companion object {
        fun create(context: Context): CacheDatabase {

            val databaseBuilder =
                Room.inMemoryDatabaseBuilder(context, CacheDatabase::class.java)
                    .fallbackToDestructiveMigration()
            return databaseBuilder.build()
        }
    }
}