package com.davanok.electricitymeterhelper.data

import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy

class FilesStorage(
    private val dataDir: Path,
    private val format: BinaryFormat,
    private val filesExtension: String
) : DataStorage {
    init {
        if (!SystemFileSystem.exists(dataDir)) {
            SystemFileSystem.createDirectories(dataDir)
        }
    }

    override fun <T : @Serializable Any> set(
        key: String,
        value: T,
        serializer: SerializationStrategy<T>
    ) {
        val filename = getFilename(key)

        val tempFilename = Path("$filename.tmp")
        val encodedValue = format.encodeToByteArray(
            serializer = serializer,
            value = value
        )
        SystemFileSystem.sink(tempFilename).buffered().use {
            it.write(source = encodedValue)
        }
        SystemFileSystem.atomicMove(tempFilename, filename)
    }

    override fun <T : @Serializable Any> get(
        key: String,
        serializer: DeserializationStrategy<T>
    ): T? {
        val filename = getFilename(key)

        if (!SystemFileSystem.exists(filename)) return null

        return try {
            SystemFileSystem.source(filename).buffered().use {
                val bytes = it.readByteArray()
                format.decodeFromByteArray(serializer, bytes)
            }
        } catch (e: Exception) {
            null
        }
    }

    override fun delete(key: String) {
        val filename = getFilename(key)
        SystemFileSystem.delete(filename, mustExist = false)
    }

    private fun getFilename(key: String) = Path(dataDir, "$key.$filesExtension")
}