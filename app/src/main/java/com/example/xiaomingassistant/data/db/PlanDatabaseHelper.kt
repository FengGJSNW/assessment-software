package com.example.xiaomingassistant.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class PlanDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "plan.db", null, 4) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE study_plan (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                userId INTEGER NOT NULL,
                title TEXT NOT NULL,
                startDate TEXT NOT NULL,
                endDate TEXT NOT NULL,
                note TEXT,
                isFinished INTEGER NOT NULL DEFAULT 0
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE plan_daily_record (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                userId INTEGER NOT NULL,
                planId INTEGER NOT NULL,
                recordDate TEXT NOT NULL,
                UNIQUE(userId, planId, recordDate)
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE plan_stat (
                userId INTEGER NOT NULL,
                statKey TEXT NOT NULL,
                statValue INTEGER NOT NULL,
                PRIMARY KEY(userId, statKey)
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS plan_daily_record")
        db.execSQL("DROP TABLE IF EXISTS plan_stat")
        db.execSQL("DROP TABLE IF EXISTS study_plan")
        onCreate(db)
    }
}