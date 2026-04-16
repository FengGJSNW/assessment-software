package com.example.xiaomingassistant.data

import android.content.ContentValues
import android.content.Context
import com.example.xiaomingassistant.data.db.PlanDatabaseHelper
import com.example.xiaomingassistant.data.model.Plan
import android.database.sqlite.SQLiteDatabase

class PlanRepository(context: Context) {

    private val dbHelper = PlanDatabaseHelper(context)

    fun insert(plan: Plan): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
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
            "id=?",
            arrayOf(plan.id.toString())
        )
    }

    fun getAll(): List<Plan> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM study_plan ORDER BY id DESC", null)

        val list = mutableListOf<Plan>()

        while (cursor.moveToNext()) {
            list.add(
                Plan(
                    id = cursor.getLong(0),
                    title = cursor.getString(1),
                    startDate = cursor.getString(2),
                    endDate = cursor.getString(3),
                    note = cursor.getString(4),
                    isFinished = cursor.getInt(5)
                )
            )
        }

        cursor.close()
        return list
    }

    fun getById(id: Long): Plan? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM study_plan WHERE id=? LIMIT 1",
            arrayOf(id.toString())
        )

        val result = if (cursor.moveToFirst()) {
            Plan(
                id = cursor.getLong(0),
                title = cursor.getString(1),
                startDate = cursor.getString(2),
                endDate = cursor.getString(3),
                note = cursor.getString(4),
                isFinished = cursor.getInt(5)
            )
        } else {
            null
        }

        cursor.close()
        return result
    }

    fun delete(id: Long): Int {
        val db = dbHelper.writableDatabase
        db.delete("plan_daily_record", "planId=?", arrayOf(id.toString()))
        return db.delete("study_plan", "id=?", arrayOf(id.toString()))
    }

    fun deleteAsFinished(id: Long): Int {
        val db = dbHelper.writableDatabase
        db.execSQL(
            """
            UPDATE plan_stat
            SET statValue = statValue + 1
            WHERE statKey = 'finished_total_count'
            """.trimIndent()
        )
        return delete(id)
    }

    fun markFinished(id: Long): Int {
        val values = ContentValues().apply {
            put("isFinished", 1)
        }
        return dbHelper.writableDatabase.update(
            "study_plan",
            values,
            "id=?",
            arrayOf(id.toString())
        )
    }

    fun isFinishedToday(planId: Long, date: String): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT 1 FROM plan_daily_record WHERE planId=? AND recordDate=? LIMIT 1",
            arrayOf(planId.toString(), date)
        )
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    fun markFinishedToday(planId: Long, date: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
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

    fun unmarkFinishedToday(planId: Long, date: String): Int {
        return dbHelper.writableDatabase.delete(
            "plan_daily_record",
            "planId=? AND recordDate=?",
            arrayOf(planId.toString(), date)
        )
    }

    fun getFinishedTotalCount(): Int {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT statValue FROM plan_stat WHERE statKey='finished_total_count' LIMIT 1",
            null
        )

        val result = if (cursor.moveToFirst()) cursor.getInt(0) else 0
        cursor.close()
        return result
    }

    fun getEarliestStartDate(): String? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT MIN(startDate) FROM study_plan",
            null
        )

        val result = if (cursor.moveToFirst()) cursor.getString(0) else null
        cursor.close()
        return result
    }
}