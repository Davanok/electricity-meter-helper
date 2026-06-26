package com.davanok.electricitymeterhelper.data

import com.davanok.electricitymeterhelper.domain.ReadingObject
import com.davanok.electricitymeterhelper.domain.ReadingObjectMin
import com.davanok.electricitymeterhelper.domain.ReadingObjectsRepository
import kotlin.uuid.Uuid

class ReadingObjectsRepositoryImpl(
    private val dataStorage: DataStorage
): ReadingObjectsRepository {
    override suspend fun getObjectsMin(): Result<List<ReadingObjectMin>> = runCatching {
        dataStorage.get<List<ReadingObjectMin>>(OBJECTS_MIN_KEY) ?: emptyList()
    }

    override suspend fun getObject(objId: Uuid): Result<ReadingObject?> = runCatching {
        dataStorage.get<ReadingObject>(buildObjectKey(objId))
    }

    override suspend fun setObject(obj: ReadingObject): Result<Unit> = runCatching {
        dataStorage.set(buildObjectKey(obj.id), obj)

        val list = getObjectsMin().getOrThrow().toMutableList()
        val index = list.indexOfFirst { it.id == obj.id }

        val objMin = ReadingObjectMin(id = obj.id, date = obj.date)
        if (index < 0) list.add(objMin)
        else list[index] = objMin

        dataStorage.set(OBJECTS_MIN_KEY, list)
    }

    override suspend fun deleteObject(objId: Uuid): Result<Unit> = runCatching {
        dataStorage.delete(buildObjectKey(objId))

        val list = getObjectsMin().getOrThrow()
        if (list.none { it.id == objId }) return@runCatching

        val newList = list.filter { it.id != objId }

        dataStorage.set(OBJECTS_MIN_KEY, newList)
    }

    companion object {
        private const val OBJECTS_MIN_KEY = "objects"
        private fun buildObjectKey(id: Uuid) = id.toString()
    }
}