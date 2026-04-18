package com.example.xiaomingassistant.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.xiaomingassistant.data.db.PlanDatabaseHelper
import com.example.xiaomingassistant.data.model.Plan

class PlanRepository(context: Context) {

    private val dbHelper = PlanDatabaseHelper(context.applicationContext)

    fun insert(plan: Plan): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("userId", plan.userId)
            put("title", plan.title)
            put("startDate", plan.startDate)
            put("endDate", plan.endDate)
            put("note", plan.note)
            put("isFinished", plan.isFinished)
        }
        return db.insert("study_plan", null, values)
    }

    fun update(plan: Plan): Int {
        val values = ContentValues().apply {
            put("title", plan.title)
            put("startDate", plan.startDate)
            put("endDate", plan.endDate)
            put("note", plan.note)
            put("isFinished", plan.isFinished)
        }
        return dbHelper.writableDatabase.update(
            "study_plan",
            values,
            "id=? AND userId=?",
            arrayOf(plan.id.toString(), plan.userId.toString())
        )
    }

    fun getAll(userId: Long): List<Plan> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM study_plan WHERE userId=? ORDER BY id DESC",
            arrayOf(userId.toString())
        )

        val list = mutableListOf<Plan>()
        while (cursor.moveToNext()) {
            list.add(
                Plan(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    userId = cursor.getLong(cursor.getColumnIndexOrThrow("userId")),
                    title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    startDate = cursor.getString(cursor.getColumnIndexOrThrow("startDate")),
                    endDate = cursor.getString(cursor.getColumnIndexOrThrow("endDate")),
                    note = cursor.getString(cursor.getColumnIndexOrThrow("note")),
                    isFinished = cursor.getInt(cursor.getColumnIndexOrThrow("isFinished"))
                )
            )
        }

        cursor.close()
        return list
    }

    fun getById(userId: Long, id: Long): Plan? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM study_plan WHERE id=? AND userId=? LIMIT 1",
            arrayOf(id.toString(), userId.toString())
        )

        val result = if (cursor.moveToFirst()) {
            Plan(
                id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                userId = cursor.getLong(cursor.getColumnIndexOrThrow("userId")),
                title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                startDate = cursor.getString(cursor.getColumnIndexOrThrow("startDate")),
                endDate = cursor.getString(cursor.getColumnIndexOrThrow("endDate")),
                note = cursor.getString(cursor.getColumnIndexOrThrow("note")),
                isFinished = cursor.getInt(cursor.getColumnIndexOrThrow("isFinished"))
            )
        } else {
            null
        }

        cursor.close()
        return result
    }

    fun delete(userId: Long, id: Long): Int {
        val db = dbHelper.writableDatabase
        db.delete(
            "plan_daily_record",
            "planId=? AND userId=?",
            arrayOf(id.toString(), userId.toString())
        )
        return db.delete(
            "study_plan",
            "id=? AND userId=?",
            arrayOf(id.toString(), userId.toString())
        )
    }

    fun deleteAsFinished(userId: Long, id: Long): Int {
        val db = dbHelper.writableDatabase

        db.execSQL(
            """
            INSERT OR IGNORE INTO plan_stat(userId, statKey, statValue)
            VALUES (?, 'finished_total_count', 0)
            """.trimIndent(),
            arrayOf(userId)
        )

        db.execSQL(
            """
            UPDATE plan_stat
            SET statValue = statValue + 1
            WHERE userId = ? AND statKey = 'finished_total_count'
            """.trimIndent(),
            arrayOf(userId)
        )

        return delete(userId, id)
    }

    fun markFinished(userId: Long, id: Long): Int {
        val values = ContentValues().apply {
            put("isFinished", 1)
        }
        return dbHelper.writableDatabase.update(
            "study_plan",
            values,
            "id=? AND userId=?",
            arrayOf(id.toString(), userId.toString())
        )
    }

    fun isFinishedToday(userId: Long, planId: Long, date: String): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT 1 FROM plan_daily_record WHERE userId=? AND planId=? AND recordDate=? LIMIT 1",
            arrayOf(userId.toString(), planId.toString(), date)
        )
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    fun markFinishedToday(userId: Long, planId: Long, date: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("userId", userId)
            put("planId", planId)
            put("recordDate", date)
        }
        return db.insertWithOnConflict(
            "plan_daily_record",
            null,
            values,
            SQLiteDatabase.CONFLICT_IGNORE
        )
    }

    fun unmarkFinishedToday(userId: Long, planId: Long, date: String): Int {
        return dbHelper.writableDatabase.delete(
            "plan_daily_record",
            "userId=? AND planId=? AND recordDate=?",
            arrayOf(userId.toString(), planId.toString(), date)
        )
    }

    fun getFinishedTotalCount(userId: Long): Int {
        val db = dbHelper.readableDatabase

        db.execSQL(
            """
            INSERT OR IGNORE INTO plan_stat(userId, statKey, statValue)
            VALUES (?, 'finished_total_count', 0)
            """.trimIndent(),
            arrayOf(userId)
        )

        val cursor = db.rawQuery(
            "SELECT statValue FROM plan_stat WHERE userId=? AND statKey='finished_total_count' LIMIT 1",
            arrayOf(userId.toString())
        )

        val result = if (cursor.moveToFirst()) cursor.getInt(0) else 0
        cursor.close()
        return result
    }

    fun getEarliestStartDate(userId: Long): String? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT MIN(startDate) FROM study_plan WHERE userId=?",
            arrayOf(userId.toString())
        )

        val result = if (cursor.moveToFirst()) cursor.getString(0) else null
        cursor.close()
        return result
    }
}