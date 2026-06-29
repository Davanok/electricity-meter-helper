package com.davanok.electricitymeterhelper.domain

interface SaveReadingObjectRepository {
    suspend fun saveReadingObject(filename: String, obj: ReadingObject): Result<Unit>
}