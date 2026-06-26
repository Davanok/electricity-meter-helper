package com.davanok.electricitymeterhelper.data

import kotlinx.browser.localStorage
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.StringFormat
import org.w3c.dom.Storage

class WebStorage(
    private val prefix: String,
    private val format: StringFormat,
    private val storage: Storage = localStorage
) : DataStorage {
    override fun <T : @Serializable Any> set(
        key: String,
        value: T,
        serializer: SerializationStrategy<T>
    ) {
        val fullKey = buildKey(key)

        val encodedValue = format.encodeToString(serializer, value)
        storage.setItem(
            key = fullKey,
            value = encodedValue
        )
    }

    override fun <T : @Serializable Any> get(
        key: String,
        serializer: DeserializationStrategy<T>
    ): T? {
        val fullKey = buildKey(key)
        val raw = storage.getItem(fullKey) ?: return null

        return runCatching {
            format.decodeFromString(serializer, raw)
        }.getOrNull()
    }

    override fun delete(key: String) {
        storage.removeItem(buildKey(key))
    }

    private fun buildKey(key: String) = "$prefix:$key"
}