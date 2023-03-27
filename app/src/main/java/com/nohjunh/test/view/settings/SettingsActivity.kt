package com.nohjunh.test.view.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nohjunh.test.R

class SettingsActivity : AppCompatActivity() {

  companion object {
    @JvmStatic
    fun start(context: Context) {
      val starter = Intent(context, SettingsFragment::class.java)
      context.startActivity(starter)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.settings_activity)
    if (savedInstanceState == null) {
      supportFragmentManager
        .beginTransaction()
        .replace(R.id.settings, SettingsFragment.newInstance())
        .commit()
    }
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
  }
}