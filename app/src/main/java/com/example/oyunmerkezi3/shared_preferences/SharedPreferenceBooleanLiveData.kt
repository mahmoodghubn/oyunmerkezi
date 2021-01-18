package com.example.oyunmerkezi3.shared_preferences

import android.content.SharedPreferences


class SharedPreferenceBooleanLiveData(prefs: SharedPreferences?, key: String?, defValue: Boolean?) :
    SharedPreferenceLiveData<Boolean?>(prefs!!, key!!, defValue) {

    override fun getValueFromPreferences(key: String?, defValue: Boolean?): Boolean {
        return sharedPrefs.getBoolean(key, defValue!! )
    }
}