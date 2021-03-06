package org.tokend.template.features.localaccount.storage

import android.content.SharedPreferences
import com.google.gson.annotations.SerializedName
import org.tokend.sdk.utils.extentions.decodeHex
import org.tokend.sdk.utils.extentions.encodeHexString
import org.tokend.template.data.repository.base.ObjectPersistenceOnPrefs
import org.tokend.template.features.localaccount.model.LocalAccount

class LocalAccountPersistenceOnPrefs(
        preferences: SharedPreferences
) : ObjectPersistenceOnPrefs<LocalAccount>(LocalAccount::class.java, preferences, KEY) {
    private class LocalAccountData(
            @SerializedName("account_id")
            val accountId: String,
            @SerializedName("serialized_encrypted_source_hex")
            val serializedEncryptedSourceHex: String
    ) {
        fun toLocalAccount(): LocalAccount {
            return LocalAccount.fromSerializedEncryptedSource(
                    accountId = accountId,
                    serializedEncryptedSource = serializedEncryptedSourceHex.decodeHex()
            )
        }

        companion object {
            fun fromLocalAccount(localAccount: LocalAccount): LocalAccountData {
                return LocalAccountData(
                        accountId = localAccount.accountId,
                        serializedEncryptedSourceHex = localAccount
                                .serializeEncryptedSource()
                                ?.encodeHexString()
                                ?: throw IllegalArgumentException("Cannot save unencrypted local account")
                )
            }
        }
    }

    override fun serializeItem(item: LocalAccount): String {
        return gson.toJson(LocalAccountData.fromLocalAccount(item))
    }

    override fun deserializeItem(serialized: String): LocalAccount? {
        return try {
            gson.fromJson(serialized, LocalAccountData::class.java).toLocalAccount()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private companion object {
        const val KEY = "local_account"
    }
}