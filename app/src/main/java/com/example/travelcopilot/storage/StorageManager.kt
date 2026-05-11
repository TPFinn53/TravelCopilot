package com.example.travelcopilot.storage

import android.content.Context
import android.content.Intent
import android.net.Uri

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.documentfile.provider.DocumentFile
import com.example.travelcopilot.domain.model.ChatMessage
import com.example.travelcopilot.domain.model.Trip

import dagger.hilt.android.qualifiers.ApplicationContext

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

import java.io.OutputStreamWriter

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageManager @Inject constructor(
    @ApplicationContext
    private val context: Context
) {

    companion object {

        private val ROOT_URI_KEY =
            stringPreferencesKey("root_uri")
    }

    // =========================================================
    // Root URI flow
    // =========================================================

    val rootUriFlow: Flow<Uri?> =
        context.dataStore.data.map { prefs ->

            prefs[ROOT_URI_KEY]
                ?.let { Uri.parse(it) }
        }

    // =========================================================
    // Save SAF root URI
    // =========================================================

    suspend fun saveRootUri(
        uri: Uri
    ) {

        context.dataStore.edit { prefs ->

            prefs[ROOT_URI_KEY] =
                uri.toString()
        }
    }

    // =========================================================
    // Get root URI
    // =========================================================

    suspend fun getRootUri(): Uri? {

        return rootUriFlow.first()
    }

    // =========================================================
    // Persist SAF permissions
    // =========================================================

    fun persistUriPermission(
        uri: Uri
    ) {

        val flags =
            Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION

        context.contentResolver
            .takePersistableUriPermission(
                uri,
                flags
            )
    }

    // =========================================================
    // Validate storage access
    // =========================================================

    suspend fun isExternalStorageAvailable(): Boolean {

        val uri = getRootUri()
            ?: return false

        return context
            .contentResolver
            .persistedUriPermissions
            .any {

                it.uri == uri &&
                        it.isWritePermission
            }
    }

    // =========================================================
    // Resolve SAF root folder
    // =========================================================

    private suspend fun getRootDirectory(): DocumentFile? {

        val uri = getRootUri()
            ?: return null

        return DocumentFile.fromTreeUri(
            context,
            uri
        )
    }

    // =========================================================
    // Save text file
    // =========================================================

    suspend fun saveTextFile(
        fileName: String,
        content: String,
        mimeType: String = "text/plain"
    ): Boolean = withContext(Dispatchers.IO) {

        try {

            val root =
                getRootDirectory()
                    ?: return@withContext false

            val file =
                root.createFile(
                    mimeType,
                    fileName
                ) ?: return@withContext false

            context.contentResolver
                .openOutputStream(file.uri)
                ?.use { stream ->

                    OutputStreamWriter(stream)
                        .use { writer ->

                            writer.write(content)
                        }
                }

            true

        } catch (e: Exception) {

            e.printStackTrace()

            false
        }
    }

    // =========================================================
    // Save JSON file
    // =========================================================

    suspend fun saveJsonFile(
        fileName: String,
        json: String
    ): Boolean {

        return saveTextFile(
            fileName = fileName,
            content = json,
            mimeType = "application/json"
        )
    }

    // =========================================================
    // Save markdown file
    // =========================================================

    suspend fun saveMarkdownFile(
        fileName: String,
        markdown: String
    ): Boolean {

        return saveTextFile(
            fileName = fileName,
            content = markdown,
            mimeType = "text/markdown"
        )
    }

    // =========================================================
    // Delete file
    // =========================================================

    suspend fun deleteFile(
        fileName: String
    ): Boolean = withContext(Dispatchers.IO) {

        try {

            val root =
                getRootDirectory()
                    ?: return@withContext false

            val file =
                root.findFile(fileName)
                    ?: return@withContext false

            file.delete()

        } catch (e: Exception) {

            e.printStackTrace()

            false
        }
    }

    // =========================================================
    // Read text file
    // =========================================================

    suspend fun readTextFile(
        fileName: String
    ): String? = withContext(Dispatchers.IO) {

        try {

            val root =
                getRootDirectory()
                    ?: return@withContext null

            val file =
                root.findFile(fileName)
                    ?: return@withContext null

            context.contentResolver
                .openInputStream(file.uri)
                ?.bufferedReader()
                ?.use {

                    it.readText()
                }

        } catch (e: Exception) {

            e.printStackTrace()

            null
        }
    }

    // =========================================================
    // List files
    // =========================================================

    suspend fun listFiles(): List<String> =
        withContext(Dispatchers.IO) {

            try {

                val root =
                    getRootDirectory()
                        ?: return@withContext emptyList()

                root.listFiles()
                    .mapNotNull { it.name }

            } catch (e: Exception) {

                e.printStackTrace()

                emptyList()
            }
        }

    fun exportTrip(trip: Trip, messages: List<ChatMessage>): Boolean {return true}
}