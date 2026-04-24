package com.example.xiaomingassistant.data.repository

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import com.example.xiaomingassistant.data.db.NotesDbHelper
import com.example.xiaomingassistant.data.model.NoteCategory
import com.example.xiaomingassistant.data.model.NoteItem

class NotesRepository(context: Context) {

    private val dbHelper = NotesDbHelper(context.applicationContext)

    /* 获取所有分类信息 */
    fun getAllCategories(userId: Long): List<NoteCategory> {
        // 确保有“未分类”
        dbHelper.ensureDefaultCategory(userId)

        val db = dbHelper.readableDatabase
        val result = mutableListOf<NoteCategory>()

        db.query(
            "note_categories",
            null,
            "user_id = ?",
            arrayOf(userId.toString()),
            null,
            null,
            "created_at ASC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                result.add(
                    NoteCategory(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        createdAt = cursor.getLong(cursor.getColumnIndexOrThrow("created_at"))
                    )
                )
            }
        }
        return result
    }

    /* 添加分类 */
    fun addCategory(userId: Long, name: String): Boolean {
        val realName = name.trim()
        if (realName.isBlank()) return false

        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("user_id", userId)
            put("name", realName)
            put("created_at", System.currentTimeMillis())
        }

        return try {
            db.insertOrThrow("note_categories", null, values) > 0
        } catch (_: SQLiteConstraintException) {
            false
        }
    }

    /* 重命名分类 */
    fun renameCategory(userId: Long, categoryId: Long, newName: String): Boolean {
        val realName = newName.trim()
        if (realName.isBlank()) return false

        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", realName)
        }

        return try {
            db.update(
                "note_categories",
                values,
                "id = ? AND user_id = ?",
                arrayOf(categoryId.toString(), userId.toString())
            ) > 0
        } catch (_: SQLiteConstraintException) {
            false
        }
    }

    /* 删除分类 */
    fun deleteCategory(userId: Long, categoryId: Long): Boolean {
        val db = dbHelper.writableDatabase

        // 检查分类下是否有笔记
        db.rawQuery(
            """
            SELECT COUNT(*) FROM notes
            WHERE category_id = ?
              AND user_id = ?
            """.trimIndent(),
            arrayOf(categoryId.toString(), userId.toString())
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                val count = cursor.getInt(0)
                if (count > 0) return false
            }
        }

        return db.delete(
            "note_categories",
            "id = ? AND user_id = ?",
            arrayOf(categoryId.toString(), userId.toString())
        ) > 0
    }

    /* 获取所有笔记 */
    fun getAllNotes(userId: Long): List<NoteItem> {
        val db = dbHelper.readableDatabase
        val result = mutableListOf<NoteItem>()

        val sql = """
            SELECT n.id,
                   n.category_id,
                   c.name,
                   n.title,
                   n.content,
                   n.created_at,
                   n.updated_at
            FROM notes n
            INNER JOIN note_categories c ON n.category_id = c.id
            WHERE n.user_id = ? AND c.user_id = ?
            ORDER BY c.created_at ASC, n.updated_at DESC
        """.trimIndent()

        db.rawQuery(sql, arrayOf(userId.toString(), userId.toString())).use { cursor ->
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

    /* 根据用户 id 获取笔记 */
    fun getNoteById(userId: Long, noteId: Long): NoteItem? {
        val db = dbHelper.readableDatabase

        val sql = """
            SELECT n.id,
                   n.category_id,
                   c.name,
                   n.title,
                   n.content,
                   n.created_at,
                   n.updated_at
            FROM notes n
            INNER JOIN note_categories c ON n.category_id = c.id
            WHERE n.id = ? AND n.user_id = ? AND c.user_id = ?
        """.trimIndent()

        db.rawQuery(sql, arrayOf(noteId.toString(), userId.toString(), userId.toString())).use { cursor ->
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

    /* 插入笔记 */
    fun insertNote(userId: Long, categoryId: Long, title: String, content: String): Long {
        val now = System.currentTimeMillis()
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("user_id", userId)
            put("category_id", categoryId)
            put("title", title.trim())
            put("content", content.trim())
            put("created_at", now)
            put("updated_at", now)
        }
        return db.insert("notes", null, values)
    }

    /* 修改笔记 */
    fun updateNote(userId: Long, noteId: Long, categoryId: Long, title: String, content: String): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("category_id", categoryId)
            put("title", title.trim())
            put("content", content.trim())
            put("updated_at", System.currentTimeMillis())
        }

        return db.update(
            "notes",
            values,
            "id = ? AND user_id = ?",
            arrayOf(noteId.toString(), userId.toString())
        ) > 0
    }

    /* 删除笔记 */
    fun deleteNote(userId: Long, noteId: Long): Boolean {
        val db = dbHelper.writableDatabase
        return db.delete(
            "notes",
            "id = ? AND user_id = ?",
            arrayOf(noteId.toString(), userId.toString())
        ) > 0
    }

    /* 根据分类查找笔记，并分好类 */
    fun getNotesGroupedByCategory(userId: Long): List<Pair<NoteCategory, List<NoteItem>>> {
        val categories = getAllCategories(userId)
        val allNotes = getAllNotes(userId)
        return categories.map { category ->
            category to allNotes.filter { it.categoryId == category.id }
        }
    }
}