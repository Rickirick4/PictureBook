package com.rickirick.fragmentartbook.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class Model(
            @ColumnInfo(name = "name")
            var pictureName : String,

            @ColumnInfo(name = "comment")
            var comment : String,

            @ColumnInfo(name = "image")
            var image: ByteArray

            ) : Serializable {

        @PrimaryKey(autoGenerate = true)
        var id : Int = 0

}