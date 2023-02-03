package com.nohjunh.test.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nohjunh.test.R
import com.nohjunh.test.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
    
}