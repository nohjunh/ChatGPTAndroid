package com.nohjunh.test.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.nohjunh.test.adapter.ContentAdapter
import com.nohjunh.test.database.entity.ContentEntity
import com.nohjunh.test.databinding.ActivityMainBinding
import com.nohjunh.test.viewModel.MainViewModel
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private val viewModel : MainViewModel by viewModels()
    private var contentDataList = ArrayList<ContentEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 로딩 되었을 때 바로 content가 보이도록
        viewModel.getContentData()

        viewModel.contentList.observe(this, Observer {
            contentDataList.clear()
            for (entity in it) {
                contentDataList.add(entity)
            }
            setContentListRV()
        })

        viewModel.deleteCheck.observe(this, Observer {
            if (it == true) {
                viewModel.getContentData()
            }
        })

        binding.sendBtn.setOnClickListener {
            viewModel.insertContent(binding.EDView.text.toString())
            binding.EDView.setText("")
            viewModel.getContentData()
        }

    }

    private fun setContentListRV() {
        val contentAdapter = ContentAdapter(this, contentDataList)
        binding.RVContainer.adapter = contentAdapter
        binding.RVContainer.layoutManager = LinearLayoutManager(this)
        // 맨 밑부터 보이게
        binding.RVContainer.scrollToPosition(contentDataList.size-1)
        // onClick 구현
        contentAdapter.delBtnClick = object : ContentAdapter.DelBtnClick {
            override fun onClick(view : View, position: Int) {
                Timber.tag("삭제버튼클릭").e("${contentDataList[position].id}")
                viewModel.deleteSelectedContent(contentDataList[position].id)
            }
        }

    }
}