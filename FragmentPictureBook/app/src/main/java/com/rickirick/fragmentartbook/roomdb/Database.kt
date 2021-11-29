package com.rickirick.fragmentartbook.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rickirick.fragmentartbook.model.Model

@Database(entities = [Model::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun Dao(): Dao
}