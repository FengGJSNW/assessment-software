package com.example.xiaomingassistant.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class NotesDbHelper(context: Context) : SQLiteOpenHelper(
    context,
    "notes.db",
    null,
    3
) {

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        // 开启外键约束支持
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        /**
         * @param 笔记分类表
         * user_id: 区分不同用户的分类。
         * name: 分类名称
         * created_at: 存储的是 Unix 时间戳（毫秒），记录分类创建时间。
         */
        db.execSQL("""
            CREATE TABLE note_categories (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                name TEXT NOT NULL,
                created_at INTEGER NOT NULL,
                UNIQUE(user_id, name)
            )
        """.trimIndent())

        /**
         * @param 笔记正文表
         * user_id: 归属用户 ID。
         * category_id: 将笔记关联到特定的分类上。
         * title & content: 笔记的标题和详细内容。
         * created_at & updated_at: 分别记录笔记的初次创建时间和最后一次修改时间
         * FOREIGN KEY(...) ON DELETE RESTRICT: 如果某个分类下还有笔记，那么阻止删除
         */
        db.execSQL("""
            CREATE TABLE notes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                category_id INTEGER NOT NULL,
                title TEXT NOT NULL,
                content TEXT NOT NULL,
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL,
                FOREIGN KEY(category_id) REFERENCES note_categories(id) ON DELETE RESTRICT
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS notes")
        db.execSQL("DROP TABLE IF EXISTS note_categories")
        onCreate(db)
    }

    // 确保每个用户都有一个默认分类
    fun ensureDefaultCategory(userId: Long) {
        val values = ContentValues().apply {
            put("user_id", userId)
            put("name", "未分类")
            put("created_at", System.currentTimeMillis())
        }
        writableDatabase.insertWithOnConflict(
            "note_categories",
            null,
            values,
            SQLiteDatabase.CONFLICT_IGNORE
        )
    }
}