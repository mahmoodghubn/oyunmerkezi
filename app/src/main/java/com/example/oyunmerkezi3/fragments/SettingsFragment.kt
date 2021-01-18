package com.example.oyunmerkezi3.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.oyunmerkezi3.R

class SettingsFragment: PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        addPreferencesFromResource(R.xml.setting)

    }
}