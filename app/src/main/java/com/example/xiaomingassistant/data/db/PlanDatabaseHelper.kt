package com.example.xiaomingassistant.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class PlanDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "plan.db", null, 3) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE study_plan (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
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
                planId INTEGER NOT NULL,
                recordDate TEXT NOT NULL,
                UNIQUE(planId, recordDate)
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE plan_stat (
                statKey TEXT PRIMARY KEY,
                statValue INTEGER NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT OR IGNORE INTO plan_stat(statKey, statValue)
            VALUES ('finished_total_count', 0)
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS plan_daily_record (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    planId INTEGER NOT NULL,
                    recordDate TEXT NOT NULL,
                    UNIQUE(planId, recordDate)
                )
                """.trimIndent()
            )
        }

        if (oldVersion < 3) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS plan_stat (
                    statKey TEXT PRIMARY KEY,
                    statValue INTEGER NOT NULL
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                INSERT OR IGNORE INTO plan_stat(statKey, statValue)
                VALUES ('finished_total_count', 0)
                """.trimIndent()
            )
        }
    }
}