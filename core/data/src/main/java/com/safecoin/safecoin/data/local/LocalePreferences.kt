package com.safecoin.safecoin.data.local

import android.content.Context

object LocalePreferences {
    private const val PREFS_NAME = "safecoin_locale"
    private const val KEY_LANGUAGE = "language"
    private const val DEFAULT_LANGUAGE = "en"

    fun readLanguageCode(context: Context): String {
        return context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LANGUAGE, DEFAULT_LANGUAGE)
            ?: DEFAULT_LANGUAGE
    }

    fun saveLanguageCode(context: Context, languageCode: String) {
        context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANGUAGE, languageCode)
            .apply()
    }
}
