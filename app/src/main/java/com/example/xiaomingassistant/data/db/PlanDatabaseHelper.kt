package com.example.xiaomingassistant.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class PlanDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    "plan.db",
    null,
    6
) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            /**
             * @param 学习计划主表
             * id 自增主键
             * userId 用户ID
             * title 计划标题
             * startDate / endDate 起止日期
             * startTime / endTime 当日时间点
             * isFinished 完成状态
             */
            """
            CREATE TABLE study_plan (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                userId INTEGER NOT NULL,
                title TEXT NOT NULL,
                startDate TEXT NOT NULL,
                endDate TEXT NOT NULL,
                startTime TEXT NOT NULL DEFAULT '',
                endTime TEXT NOT NULL DEFAULT '',
                note TEXT,
                isFinished INTEGER NOT NULL DEFAULT 0
            )
            """.trimIndent()
        )

        /**
         * @param 每日打卡记录表
         * id 自增主键
         * userId 用户ID
         * planId 关联 study_plan 表的外键ID
         * recordDate 打卡日期
         */
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

        /**
         * @param 统计数据表
         * userId 用户ID
         * statKey 统计项的键
         * statValue 对应统计数值
         */
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

        /**
         * @param 坚持天数记录表
         * userId 用户ID
         * recordDate 当天日期
         */
        db.execSQL(
            """
            CREATE TABLE plan_keep_day_record (
                userId INTEGER NOT NULL,
                recordDate TEXT NOT NULL,
                PRIMARY KEY(userId, recordDate)
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 5) {
            db.execSQL("ALTER TABLE study_plan ADD COLUMN startTime TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE study_plan ADD COLUMN endTime TEXT NOT NULL DEFAULT ''")
        }
        if (oldVersion < 6) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS plan_keep_day_record (
                    userId INTEGER NOT NULL,
                    recordDate TEXT NOT NULL,
                    PRIMARY KEY(userId, recordDate)
                )
                """.trimIndent()
            )
        }
    }
}
