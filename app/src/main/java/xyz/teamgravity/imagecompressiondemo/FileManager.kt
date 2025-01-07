package xyz.teamgravity.imagecompressiondemo

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FileManager(
    private val context: Context
) {

    ///////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////

    suspend fun saveImage(
        contentUri: Uri,
        fileName: String
    ) {
        withContext(Dispatchers.IO) {
            context
                .contentResolver
                .openInputStream(contentUri)
                ?.use { input ->
                    context
                        .openFileOutput(fileName, Context.MODE_PRIVATE)
                        .use { output ->
                            input.copyTo(output)
                        }
                }
        }
    }

    suspend fun saveImage(
        bytes: ByteArray,
        fileName: String
    ) {
        withContext(Dispatchers.IO) {
            context
                .openFileOutput(fileName, Context.MODE_PRIVATE)
                .use { it.write(bytes) }
        }
    }
}