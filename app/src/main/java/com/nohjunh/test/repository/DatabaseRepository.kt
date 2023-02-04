package com.nohjunh.test.repository

import com.nohjunh.test.App
import com.nohjunh.test.database.ChatDatabase
import com.nohjunh.test.database.entity.ContentEntity

class DatabaseRepository {

    private val context = App.context()
    private val database = ChatDatabase.getDatabase(context)

    fun getContentData() = database.contentDAO().getContentData()

    fun insertContent(content : String, gptOrUser : Int) = database.contentDAO().insertContent(ContentEntity(0, content, gptOrUser))

    fun deleteSelectedContent(id : Int) = database.contentDAO().deleteSelectedContent(id)

}