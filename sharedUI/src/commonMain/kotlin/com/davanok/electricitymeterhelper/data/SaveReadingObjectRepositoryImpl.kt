package com.davanok.electricitymeterhelper.data

import app.softwork.serialization.csv.CSVFormat
import com.davanok.electricitymeterhelper.domain.ReadingObject
import com.davanok.electricitymeterhelper.domain.SaveReadingObjectRepository
import com.davanok.electricitymeterhelper.platform.saveFile
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString

@OptIn(ExperimentalSerializationApi::class)
class SaveReadingObjectRepositoryImpl : SaveReadingObjectRepository {
    private val format = CSVFormat

    override suspend fun saveReadingObject(
        filename: String,
        obj: ReadingObject
    ): Result<Unit> = runCatching {
        val csvData = format.encodeToString(obj.entries)
        val data = csvData.encodeToByteArray()

        saveFile(filename, data)
    }
}