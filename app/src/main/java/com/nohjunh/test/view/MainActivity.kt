package com.nohjunh.test.view

import android.content.DialogInterface
import android.os.Build.VERSION.SDK_INT
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import coil.Coil
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.load
import com.nohjunh.test.R
import com.nohjunh.test.adapter.ContentAdapter
import com.nohjunh.test.database.entity.ContentEntity
import com.nohjunh.test.databinding.ActivityMainBinding
import com.nohjunh.test.viewModel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private val viewModel : MainViewModel by viewModels()
    private var contentDataList = ArrayList<ContentEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* coil GIF 확장 라이브러리 */
        val imageLoader = this?.let {
            ImageLoader.Builder(it)
                .components {
                    if (SDK_INT >= 28) {
                        add(ImageDecoderDecoder.Factory())
                    } else {
                        add(GifDecoder.Factory())
                    }
                }
                .build()
        }
        if (imageLoader != null) {
            Coil.setImageLoader(imageLoader)
        }
        binding.loading.visibility = View.INVISIBLE
        binding.loading.load(R.drawable.loading3)

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

        viewModel.gptInsertCheck.observe(this, Observer {
            if (it == true) {
                viewModel.getContentData()
                binding.loading.visibility = View.INVISIBLE
            }
        })

        binding.sendBtn.setOnClickListener {
            binding.loading.visibility = View.VISIBLE

            viewModel.postResponse(binding.EDView.text.toString())
            viewModel.insertContent(binding.EDView.text.toString(), 2) // 1: Gpt, 2: User
            binding.EDView.setText("")
            viewModel.getContentData()
        }

    }

    private fun setContentListRV() {
        val contentAdapter = ContentAdapter(this, contentDataList)
        binding.RVContainer.adapter = contentAdapter
        binding.RVContainer.layoutManager = LinearLayoutManager(this).apply {
            // 맨 밑부터 보이게
            stackFromEnd = true
        }
        // 맨 밑부터 보이게
        //binding.RVContainer.scrollToPosition(contentDataList.size-1)
        CoroutineScope(Dispatchers.Main).launch {
            delay(100)
            binding.SVContainer.fullScroll(ScrollView.FOCUS_DOWN);
        }
        // onClick 구현
        contentAdapter.delChatLayoutClick = object : ContentAdapter.DelChatLayoutClick {
            override fun onLongClick(view : View, position: Int) {
                Timber.tag("삭제버튼클릭").e("${contentDataList[position].id}")
                // alertDialog
                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setTitle("대화컨텐츠 삭제")
                    .setMessage("한번 삭제된 대화는 복구할 수 없습니다.")
                    .setPositiveButton("확인",
                        DialogInterface.OnClickListener { dialog, id ->
                            viewModel.deleteSelectedContent(contentDataList[position].id)
                        })
                    .setNegativeButton("취소",
                        DialogInterface.OnClickListener { dialog, id ->
                        })
                // 다이얼로그를 띄워주기
                builder.show()
            }
        }
    }
}