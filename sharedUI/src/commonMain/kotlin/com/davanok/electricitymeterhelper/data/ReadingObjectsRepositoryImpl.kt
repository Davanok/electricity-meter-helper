package com.davanok.electricitymeterhelper.data

import com.davanok.electricitymeterhelper.domain.ReadingObject
import com.davanok.electricitymeterhelper.domain.ReadingObjectsRepository
import kotlin.uuid.Uuid

class ReadingObjectsRepositoryImpl(
    private val dataStorage: DataStorage
): ReadingObjectsRepository {
    override suspend fun getObjectsMin(): Result<List<ReadingObject>> {
        TODO("Not yet implemented")
    }

    override suspend fun getObject(objId: Uuid): Result<ReadingObject> {
        TODO("Not yet implemented")
    }

    override suspend fun setObject(obj: ReadingObject): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteObject(objId: Uuid): Result<Unit> {
        TODO("Not yet implemented")
    }
}