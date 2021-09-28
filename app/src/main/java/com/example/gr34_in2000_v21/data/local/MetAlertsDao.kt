package com.example.gr34_in2000_v21.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.gr34_in2000_v21.data.models.MetAlertsModel


@Dao
interface MetAlertsDao {
    @Query("SELECT * from items, cap")
    fun getAllItems(): LiveData<List<MetAlertsModel.ItemCapJoin>>

    @Query("SELECT * from items, cap WHERE identifier = :guid")
    fun getItem(guid: String): LiveData<MetAlertsModel.ItemCapJoin>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveItems(items: List<MetAlertsModel.Item>, caps: List<MetAlertsModel.CAPAlert>)

    @Delete
    suspend fun deleteItem(item: MetAlertsModel.Item, cap: MetAlertsModel.CAPAlert)

}