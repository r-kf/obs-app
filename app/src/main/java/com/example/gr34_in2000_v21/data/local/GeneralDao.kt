package com.example.gr34_in2000_v21.data.local

import androidx.room.Dao
import androidx.room.Query

@Dao
interface GeneralDao {
    @Query("DELETE from items")
    fun nukeItems()

    @Query("DELETE from cap")
    fun nukeCaps()
}