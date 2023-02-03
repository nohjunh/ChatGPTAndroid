package com.nohjunh.test.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nohjunh.test.repository.DatabaseRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val databaseRepository = DatabaseRepository()

    fun getContentData() = viewModelScope.launch {

    }

    fun insertContent(content : String) = viewModelScope.launch {

    }

    fun deleteSelectedContent(id : Int) = viewModelScope.launch {

    }

}