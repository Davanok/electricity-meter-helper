package com.davanok.electricitymeterhelper.platform

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.download

actual suspend fun saveFile(filename: String, bytes: ByteArray) {
    FileKit.download(bytes, filename)
}