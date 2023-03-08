package com.nohjunh.test.view

import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import coil.Coil
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.google.android.material.textfield.TextInputLayout
import com.nohjunh.test.R
import com.nohjunh.test.adapter.ContentAdapter
import com.nohjunh.test.database.entity.ContentEntity
import com.nohjunh.test.databinding.ActivityMainBinding
import com.nohjunh.test.viewModel.MainViewModel

class MainActivity : AppCompatActivity() {

    private var branch : Int = 1 // # 1 -> First time loading
    private lateinit var binding : ActivityMainBinding
    private val viewModel : MainViewModel by viewModels()
    private var contentDataList = ArrayList<ContentEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        // 로딩 되었을 때 바로 content가 보이도록
        viewModel.getContentData()

        viewModel.contentList.observe(this, Observer {
            contentDataList.clear()
            for (entity in it) {
                contentDataList.add(entity)
            }
            setContentListRV(branch)
        })

        viewModel.deleteCheck.observe(this, Observer {
            if (it == true) {
                viewModel.getContentData()
                branch = 1
            }
        })

        viewModel.gptInsertCheck.observe(this, Observer {
            if (it == true) {
                viewModel.getContentData()
                binding.loading.visibility = View.INVISIBLE
            }
            branch = 2
        })

        viewModel.showLoading.observe(this) {
            if (it == true) {
                binding.loading.visibility = View.VISIBLE
            } else {
                binding.loading.visibility = View.INVISIBLE
            }
        }

        viewModel.showDeleteGuide.observe(this) {
            if (it == true) {
                showAsk(getString(R.string.ask_title_notice), getString(R.string.ask_msg_delete_guide), {
                    viewModel.resetFirstOpen()
                })
            }
        }

        binding.sendBtn.setOnClickListener {
            binding.loading.visibility = View.VISIBLE
            val msg = binding.EDView.text.toString().trim()
            if (msg.isEmpty()) {
                viewModel.insertContent("你可以跟我对话，历史、人文、科技、甚至是段子、笑话都可以。", 1) // 1: Gpt, 2: User
                return@setOnClickListener
            }
            viewModel.postResponse(msg)
            viewModel.insertContent(msg, 2) // 1: Gpt, 2: User
            binding.EDView.setText("")
            branch = 2
            viewModel.getContentData()
        }

        binding.ivSetting.setOnClickListener {
            showInputDialog()
        }
    }

    private fun setContentListRV(branch : Int) {
        val contentAdapter = ContentAdapter(this, contentDataList)
        binding.RVContainer.adapter = contentAdapter
        binding.RVContainer.layoutManager = LinearLayoutManager(this).apply { stackFromEnd = true }
        contentAdapter.delChatLayoutClick = object : ContentAdapter.DelChatLayoutClick {
            override fun onLongClick(view: View, position: Int) {
                showAsk(getString(R.string.ask_title_delete_cov), getString(R.string.ask_msg_delete_cov), {
                    viewModel.deleteSelectedContent(contentDataList[position].id)
                })
            }
        }
    }

    private fun showInputDialog() {
        val inputView = layoutInflater.inflate(R.layout.input_layout, null)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.ask_title_setting))
        builder.setMessage(getString(R.string.ask_msg_setting))
            .setCancelable(false)
            .setView(inputView)
        builder.setPositiveButton(getString(R.string.ask_ok)) { dialog, which ->
            val etInput = inputView.findViewById<TextInputLayout>(R.id.etInput)
            val text = etInput.editText?.text.toString().trim()
            if (text.isEmpty()) {
                Toast.makeText(this, getString(R.string.api_key_empty_error), Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            viewModel.insertContent(getString(R.string.api_key_inserted), 1)
            viewModel.setupApiKey(text)
        }
        builder.setNegativeButton(getString(R.string.ask_cancel)) { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }

    fun showAsk(title: String, message: String, positiveAction: () -> Unit, negativeAction: (() -> Unit)? = null) {
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setCancelable(false)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.ask_ok) { _, _ ->
                positiveAction()
            }
            .setNegativeButton(R.string.ask_cancel) { _, _ ->
                negativeAction?.invoke()
            }
        builder.show()
    }
}