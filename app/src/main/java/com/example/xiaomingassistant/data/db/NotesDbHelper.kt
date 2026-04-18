package com.example.xiaomingassistant.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class NotesDbHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_CATEGORY (
                $CATEGORY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $CATEGORY_NAME TEXT NOT NULL UNIQUE,
                $CATEGORY_CREATED_AT INTEGER NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_NOTE (
                $NOTE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $NOTE_CATEGORY_ID INTEGER NOT NULL,
                $NOTE_TITLE TEXT NOT NULL,
                $NOTE_CONTENT TEXT NOT NULL,
                $NOTE_CREATED_AT INTEGER NOT NULL,
                $NOTE_UPDATED_AT INTEGER NOT NULL,
                FOREIGN KEY($NOTE_CATEGORY_ID) REFERENCES $TABLE_CATEGORY($CATEGORY_ID) ON DELETE RESTRICT
            )
            """.trimIndent()
        )

        insertDefaultCategory(db, "未分类")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS $TABLE_CATEGORY (
                    $CATEGORY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $CATEGORY_NAME TEXT NOT NULL UNIQUE,
                    $CATEGORY_CREATED_AT INTEGER NOT NULL
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS $TABLE_NOTE (
                    $NOTE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $NOTE_CATEGORY_ID INTEGER NOT NULL,
                    $NOTE_TITLE TEXT NOT NULL,
                    $NOTE_CONTENT TEXT NOT NULL,
                    $NOTE_CREATED_AT INTEGER NOT NULL,
                    $NOTE_UPDATED_AT INTEGER NOT NULL,
                    FOREIGN KEY($NOTE_CATEGORY_ID) REFERENCES $TABLE_CATEGORY($CATEGORY_ID) ON DELETE RESTRICT
                )
                """.trimIndent()
            )

            insertDefaultCategory(db, "未分类")
        }
    }

    private fun insertDefaultCategory(db: SQLiteDatabase, name: String) {
        val values = ContentValues().apply {
            put(CATEGORY_NAME, name)
            put(CATEGORY_CREATED_AT, System.currentTimeMillis())
        }
        db.insertWithOnConflict(
            TABLE_CATEGORY,
            null,
            values,
            SQLiteDatabase.CONFLICT_IGNORE
        )
    }

    companion object {
        const val DATABASE_NAME = "notes.db"
        const val DATABASE_VERSION = 2

        const val TABLE_CATEGORY = "note_categories"
        const val TABLE_NOTE = "notes"

        const val CATEGORY_ID = "id"
        const val CATEGORY_NAME = "name"
        const val CATEGORY_CREATED_AT = "created_at"

        const val NOTE_ID = "id"
        const val NOTE_CATEGORY_ID = "category_id"
        const val NOTE_TITLE = "title"
        const val NOTE_CONTENT = "content"
        const val NOTE_CREATED_AT = "created_at"
        const val NOTE_UPDATED_AT = "updated_at"
    }
}