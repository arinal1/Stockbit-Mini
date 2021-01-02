package com.arinal.common.preferences

import android.content.Context
import android.content.SharedPreferences
import com.arinal.common.R

class PreferencesHelper(context: Context) {

    private val appName = context.getString(R.string.app_name)
    private val editor: SharedPreferences.Editor
        get() = preferences.edit()
    private val preferences = context.getSharedPreferences(appName, Context.MODE_PRIVATE)

    fun setString(key: PreferencesKey, value: String) = editor.putString(key.toString(), value).apply()
    fun getString(key: PreferencesKey): String = preferences.getString(key.toString(), "") ?: ""
    fun setInt(key: PreferencesKey, value: Int) = editor.putInt(key.toString(), value).apply()
    fun getInt(key: PreferencesKey): Int = preferences.getInt(key.toString(), 0)
    fun setLong(key: PreferencesKey, value: Long) = editor.putLong(key.toString(), value).apply()
    fun getLong(key: PreferencesKey): Long = preferences.getLong(key.toString(), 0L)
    fun setBoolean(key: PreferencesKey, value: Boolean) = editor.putBoolean(key.toString(), value).apply()
    fun getBoolean(key: PreferencesKey): Boolean = preferences.getBoolean(key.toString(), false)
    fun clearPreference(key: PreferencesKey) = editor.remove(key.toString()).apply()
    fun clearAllPreferences() = editor.clear().apply()

}
