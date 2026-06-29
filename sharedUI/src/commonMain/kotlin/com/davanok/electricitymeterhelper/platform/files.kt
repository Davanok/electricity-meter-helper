package com.davanok.electricitymeterhelper.platform

expect suspend fun saveFile(filename: String, bytes: ByteArray)