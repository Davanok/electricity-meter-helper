package com.davanok.electricitymeterhelper.platform

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.openFileSaver
import io.github.vinceglb.filekit.withScopedAccess
import io.github.vinceglb.filekit.write

actual suspend fun saveFile(filename: String, bytes: ByteArray) {
    val file = FileKit.openFileSaver(suggestedName = filename, defaultExtension = null) ?: return
    file.withScopedAccess {
        it.write(bytes)
    }
}