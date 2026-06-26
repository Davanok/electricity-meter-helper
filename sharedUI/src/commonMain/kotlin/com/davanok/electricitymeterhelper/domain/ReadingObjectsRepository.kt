package com.davanok.electricitymeterhelper.domain

import kotlin.uuid.Uuid

interface ReadingObjectsRepository {
    suspend fun getObjectsMin(): Result<List<ReadingObject>>

    suspend fun getObject(objId: Uuid): Result<ReadingObject>
    suspend fun setObject(obj: ReadingObject): Result<Unit>
    suspend fun deleteObject(objId: Uuid): Result<Unit>
}