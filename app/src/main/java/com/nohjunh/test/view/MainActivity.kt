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
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private var branch: Int = 1 // # 1 -> First time loading
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private var contentDataList = ArrayList<ContentEntity>()
    private val layoutManager = LinearLayoutManager(this).apply { stackFromEnd = true }
    private val adapter = ContentAdapter(this, contentDataList)

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.RVContainer.layoutManager = layoutManager
        binding.RVContainer.adapter = adapter


        val imageLoader = this.let {
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
        Coil.setImageLoader(imageLoader)
        binding.loading.visibility = View.INVISIBLE

        viewModel.contentList.observe(this) {
            Timber.d("contentList: ${it.size}")
            contentDataList.clear()
            contentDataList.addAll(it)
            adapter.notifyDataSetChanged()
            layoutManager.scrollToPosition(contentDataList.size - 1)
        }

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
//            if (it == true) {
//                showAsk(getString(R.string.ask_title_notice), getString(R.string.ask_msg_delete_guide), {
//                    viewModel.resetFirstOpen()
//                })
//            }
        }

        binding.sendBtn.setOnClickListener {
            binding.loading.visibility = View.VISIBLE
            val msg = binding.EDView.text.toString().trim()
            if (msg.isEmpty()) {
                viewModel.insertContent(getString(R.string.chat_tips), 1) // 1: Gpt, 2: User
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
        viewModel.getContentData()
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