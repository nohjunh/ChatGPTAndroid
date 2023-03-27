package com.nohjunh.test.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ContentTable")
data class ContentEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id : Int,
    @ColumnInfo(name = "Content")
    var content : String,
    @ColumnInfo(name = "gptOrUser")
    var gptOrUser : Int

) {
    @ColumnInfo(name = "type")
    var type: Int = 0
    @ColumnInfo(name = "time")
    var time: Long = 0

    companion object {
        const val Gpt = 1
        const val User = 2

        const val TYPE_CONVERSATION = 0
        const val TYPE_SYSTEM = 1
    }
}