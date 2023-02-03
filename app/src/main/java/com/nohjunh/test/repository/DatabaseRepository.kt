package com.nohjunh.test.repository

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nohjunh.test.App
import com.nohjunh.test.database.ChatDatabase
import com.nohjunh.test.database.entity.ContentEntity

class DatabaseRepository {

    private val context = App.context()
    private val database = ChatDatabase.getDatabase(context)

    fun getContentData() = database.contentDAO().getContentData()

    fun insertContent(content : String) = database.contentDAO().insertContent(ContentEntity(0, content))

    fun deleteSelectedContent(id : Int) = database.contentDAO().deleteSelectedContent(id)

}