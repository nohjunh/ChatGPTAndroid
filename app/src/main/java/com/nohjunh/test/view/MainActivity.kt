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
import com.nohjunh.test.BuildConfig
import com.nohjunh.test.R
import com.nohjunh.test.adapter.ContentAdapter
import com.nohjunh.test.database.entity.ContentEntity
import com.nohjunh.test.databinding.ActivityMainBinding
import com.nohjunh.test.model.event.SteamDataEvent
import com.nohjunh.test.view.settings.SettingsActivity
import com.nohjunh.test.viewModel.MainViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private var branch: Int = 1 // # 1 -> First time loading
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private val layoutManager = LinearLayoutManager(this).apply { stackFromEnd = true }
    private val adapter = ContentAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.RVContainer.layoutManager = layoutManager
        binding.RVContainer.adapter = adapter
        viewModel.checkToken()

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
            adapter.submitList(it)
            layoutManager.scrollToPosition(adapter.itemCount - 1)
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
        viewModel.chatGptNewMsg.observe(this) {
            if (it != null) {
                adapter.addMsg(it)
                layoutManager.scrollToPosition(adapter.itemCount - 1)
            }
        }

        binding.sendBtn.setOnClickListener {
            binding.loading.visibility = View.VISIBLE
            val msg = binding.EDView.text.toString().trim()
            if (msg.isEmpty()) {
                viewModel.insertContent(getString(R.string.chat_tips), ContentEntity.Gpt, ContentEntity.TYPE_SYSTEM) // 1: Gpt, 2: User
                return@setOnClickListener
            }
            if (!BuildConfig.DEBUG) {
                binding.EDView.setText("")
            }
            branch = 2
            viewModel.insertContent(msg, ContentEntity.User, ContentEntity.TYPE_CONVERSATION, false) // 1: Gpt, 2: User
            adapter.addMsg(ContentEntity(0, msg, ContentEntity.User))

            if (viewModel.sendBySteam) {
                adapter.addChatGpt("waiting for response...")
            }
            layoutManager.scrollToPosition(adapter.itemCount - 1)
            viewModel.postResponse(msg)
        }

        binding.ivSetting.setOnClickListener {
//            showInputDialog()
            SettingsActivity.start(this)
        }
        viewModel.getContentData()
        if (BuildConfig.DEBUG) {
            binding.EDView.setText("介绍一下你自己")
        }

        EventBus.getDefault().register(this)
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
            viewModel.setupApiKey(text)
            viewModel.insertContent(getString(R.string.api_key_inserted), ContentEntity.Gpt, ContentEntity.TYPE_SYSTEM)
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun receiveSteamData(data: SteamDataEvent) {
        Timber.d("receiveSteamData: ${data.data} type ${data.type}")
        if (data.isSteam()) {
            adapter.appendLast(data.data)
            layoutManager.scrollToPosition(adapter.itemCount - 1)
        } else if (data.isSteamStart()) {
//            adapter.addLast("waiting for response...")
        } else if (data.isSteamEnd()) {
            viewModel.insertContent(adapter.getLastContent(), ContentEntity.Gpt, ContentEntity.TYPE_CONVERSATION, false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}