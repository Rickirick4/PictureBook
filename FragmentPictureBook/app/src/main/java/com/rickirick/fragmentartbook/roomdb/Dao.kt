package com.rickirick.fragmentartbook.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.rickirick.fragmentartbook.model.Model
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface Dao {

    @Query("SELECT * FROM Model")
    fun getAll(): Flowable<List<Model>>

    @Query("SELECT * FROM Model WHERE id = :id")
    fun getArtById(id: Int): Flowable<Model>

    @Insert
    fun insert(model : Model) : Completable

    @Delete
    fun delete(model: Model) : Completable
}