package com.example.oyunmerkezi3.shared_preferences

import android.content.SharedPreferences
import androidx.lifecycle.LiveData

abstract class SharedPreferenceLiveData<T>(prefs: SharedPreferences, private var key: String,
                                           private var defValue: T
) :
    LiveData<T>() {
    var sharedPrefs: SharedPreferences = prefs
    private val preferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (this@SharedPreferenceLiveData.key == key) {
                value = getValueFromPreferences(key, defValue)
            }
        }

    abstract fun getValueFromPreferences(key: String?, defValue: T): T

    override fun onActive() {
        super.onActive()
        this.value = getValueFromPreferences(key, defValue)
        sharedPrefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onInactive() {
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
        super.onInactive()
    }

    fun getBooleanLiveData(
        key: String?,
        defaultValue: Boolean
    ): SharedPreferenceBooleanLiveData {
        return SharedPreferenceBooleanLiveData(sharedPrefs, key, defaultValue)
    }

    fun getStringLiveData(
        key: String?,
        defaultValue: String
    ): SharedPreferenceStringLiveData {
        return SharedPreferenceStringLiveData(sharedPrefs, key, defaultValue)
    }
}