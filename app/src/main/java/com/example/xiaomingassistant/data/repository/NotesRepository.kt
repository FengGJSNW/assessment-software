package com.example.xiaomingassistant.data.repository

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import com.example.xiaomingassistant.data.db.NotesDbHelper
import com.example.xiaomingassistant.data.model.NoteCategory
import com.example.xiaomingassistant.data.model.NoteItem

class NotesRepository(context: Context) {

    private val dbHelper = NotesDbHelper(context.applicationContext)

    fun getAllCategories(): List<NoteCategory> {
        val db = dbHelper.readableDatabase
        val result = mutableListOf<NoteCategory>()

        db.query(
            NotesDbHelper.TABLE_CATEGORY,
            null,
            null,
            null,
            null,
            null,
            "${NotesDbHelper.CATEGORY_CREATED_AT} ASC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                result.add(
                    NoteCategory(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow(NotesDbHelper.CATEGORY_ID)),
                        name = cursor.getString(cursor.getColumnIndexOrThrow(NotesDbHelper.CATEGORY_NAME)),
                        createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(NotesDbHelper.CATEGORY_CREATED_AT))
                    )
                )
            }
        }
        return result
    }

    fun addCategory(name: String): Boolean {
        val realName = name.trim()
        if (realName.isBlank()) return false

        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(NotesDbHelper.CATEGORY_NAME, realName)
            put(NotesDbHelper.CATEGORY_CREATED_AT, System.currentTimeMillis())
        }

        return try {
            db.insertOrThrow(NotesDbHelper.TABLE_CATEGORY, null, values) > 0
        } catch (_: SQLiteConstraintException) {
            false
        }
    }

    fun renameCategory(categoryId: Long, newName: String): Boolean {
        val realName = newName.trim()
        if (realName.isBlank()) return false

        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(NotesDbHelper.CATEGORY_NAME, realName)
        }

        return try {
            db.update(
                NotesDbHelper.TABLE_CATEGORY,
                values,
                "${NotesDbHelper.CATEGORY_ID} = ?",
                arrayOf(categoryId.toString())
            ) > 0
        } catch (_: SQLiteConstraintException) {
            false
        }
    }

    fun deleteCategory(categoryId: Long): Boolean {
        val db = dbHelper.writableDatabase

        db.rawQuery(
            """
            SELECT COUNT(*) FROM ${NotesDbHelper.TABLE_NOTE}
            WHERE ${NotesDbHelper.NOTE_CATEGORY_ID} = ?
            """.trimIndent(),
            arrayOf(categoryId.toString())
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                val count = cursor.getInt(0)
                if (count > 0) return false
            }
        }

        return db.delete(
            NotesDbHelper.TABLE_CATEGORY,
            "${NotesDbHelper.CATEGORY_ID} = ?",
            arrayOf(categoryId.toString())
        ) > 0
    }

    fun getAllNotes(): List<NoteItem> {
        val db = dbHelper.readableDatabase
        val result = mutableListOf<NoteItem>()

        val sql = """
            SELECT n.${NotesDbHelper.NOTE_ID},
                   n.${NotesDbHelper.NOTE_CATEGORY_ID},
                   c.${NotesDbHelper.CATEGORY_NAME},
                   n.${NotesDbHelper.NOTE_TITLE},
                   n.${NotesDbHelper.NOTE_CONTENT},
                   n.${NotesDbHelper.NOTE_CREATED_AT},
                   n.${NotesDbHelper.NOTE_UPDATED_AT}
            FROM ${NotesDbHelper.TABLE_NOTE} n
            INNER JOIN ${NotesDbHelper.TABLE_CATEGORY} c
            ON n.${NotesDbHelper.NOTE_CATEGORY_ID} = c.${NotesDbHelper.CATEGORY_ID}
            ORDER BY c.${NotesDbHelper.CATEGORY_CREATED_AT} ASC,
                     n.${NotesDbHelper.NOTE_UPDATED_AT} DESC
        """.trimIndent()

        db.rawQuery(sql, null).use { cursor ->
            while (cursor.moveToNext()) {
                result.add(
                    NoteItem(
                        id = cursor.getLong(0),
                        categoryId = cursor.getLong(1),
                        categoryName = cursor.getString(2),
                        title = cursor.getString(3),
                        content = cursor.getString(4),
                        createdAt = cursor.getLong(5),
                        updatedAt = cursor.getLong(6)
                    )
                )
            }
        }

        return result
    }

    fun getNoteById(noteId: Long): NoteItem? {
        val db = dbHelper.readableDatabase

        val sql = """
            SELECT n.${NotesDbHelper.NOTE_ID},
                   n.${NotesDbHelper.NOTE_CATEGORY_ID},
                   c.${NotesDbHelper.CATEGORY_NAME},
                   n.${NotesDbHelper.NOTE_TITLE},
                   n.${NotesDbHelper.NOTE_CONTENT},
                   n.${NotesDbHelper.NOTE_CREATED_AT},
                   n.${NotesDbHelper.NOTE_UPDATED_AT}
            FROM ${NotesDbHelper.TABLE_NOTE} n
            INNER JOIN ${NotesDbHelper.TABLE_CATEGORY} c
            ON n.${NotesDbHelper.NOTE_CATEGORY_ID} = c.${NotesDbHelper.CATEGORY_ID}
            WHERE n.${NotesDbHelper.NOTE_ID} = ?
        """.trimIndent()

        db.rawQuery(sql, arrayOf(noteId.toString())).use { cursor ->
            if (cursor.moveToFirst()) {
                return NoteItem(
                    id = cursor.getLong(0),
                    categoryId = cursor.getLong(1),
                    categoryName = cursor.getString(2),
                    title = cursor.getString(3),
                    content = cursor.getString(4),
                    createdAt = cursor.getLong(5),
                    updatedAt = cursor.getLong(6)
                )
            }
        }
        return null
    }

    fun insertNote(categoryId: Long, title: String, content: String): Long {
        val now = System.currentTimeMillis()
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(NotesDbHelper.NOTE_CATEGORY_ID, categoryId)
            put(NotesDbHelper.NOTE_TITLE, title.trim())
            put(NotesDbHelper.NOTE_CONTENT, content.trim())
            put(NotesDbHelper.NOTE_CREATED_AT, now)
            put(NotesDbHelper.NOTE_UPDATED_AT, now)
        }
        return db.insert(NotesDbHelper.TABLE_NOTE, null, values)
    }

    fun updateNote(noteId: Long, categoryId: Long, title: String, content: String): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(NotesDbHelper.NOTE_CATEGORY_ID, categoryId)
            put(NotesDbHelper.NOTE_TITLE, title.trim())
            put(NotesDbHelper.NOTE_CONTENT, content.trim())
            put(NotesDbHelper.NOTE_UPDATED_AT, System.currentTimeMillis())
        }

        return db.update(
            NotesDbHelper.TABLE_NOTE,
            values,
            "${NotesDbHelper.NOTE_ID} = ?",
            arrayOf(noteId.toString())
        ) > 0
    }

    fun deleteNote(noteId: Long): Boolean {
        val db = dbHelper.writableDatabase
        return db.delete(
            NotesDbHelper.TABLE_NOTE,
            "${NotesDbHelper.NOTE_ID} = ?",
            arrayOf(noteId.toString())
        ) > 0
    }

    fun getNotesGroupedByCategory(): List<Pair<NoteCategory, List<NoteItem>>> {
        val categories = getAllCategories()
        val allNotes = getAllNotes()
        return categories.map { category ->
            category to allNotes.filter { it.categoryId == category.id }
        }
    }
}