package org.tokend.template.base.logic.persistance

import android.content.SharedPreferences
import org.tokend.sdk.factory.GsonFactory
import org.tokend.template.base.logic.model.UrlConfig

class UrlConfigPersistor(
        private val preferences: SharedPreferences
) {
    fun saveConfig(config: UrlConfig) {
        preferences
                .edit()
                .putString(
                        CONFIG_KEY,
                        GsonFactory().getBaseGson().toJson(config)
                )
                .apply()
    }

    fun loadConfig(): UrlConfig? {
        return preferences
                .getString(CONFIG_KEY, null)
                ?.let {
                    try {
                        GsonFactory().getBaseGson().fromJson(it, UrlConfig::class.java)
                    } catch (e: Exception) {
                        null
                    }
                }

    }

    companion object {
        private const val CONFIG_KEY = "url_config"
    }
}