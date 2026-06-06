package com.safecoin.safecoin.locale

import android.content.Context
import android.content.res.Configuration
import com.safecoin.safecoin.data.local.LocalePreferences
import java.util.Locale

object LocaleManager {
    fun wrap(context: Context): Context {
        val languageCode = readLanguageCode(context)
        return applyLocale(context, languageCode)
    }

    fun readLanguageCode(context: Context): String = LocalePreferences.readLanguageCode(context)

    fun applyLocale(context: Context, languageCode: String): Context {
        val locale = Locale.forLanguageTag(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}
