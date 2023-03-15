package com.nohjunh.test.view.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.nohjunh.test.R

class SettingsFragment : PreferenceFragmentCompat() {

  companion object {
    fun newInstance() = SettingsFragment()
  }

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    setPreferencesFromResource(R.xml.root_preferences, rootKey)
  }

}