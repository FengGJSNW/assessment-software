package com.example.xiaomingassistant.util.calc

import java.time.LocalTime
import java.time.format.DateTimeFormatter

enum class PlanPointState {
    YELLOW,
    RED,
    GREEN
}

const val DEFAULT_PLAN_START_TIME = "07:30"
const val DEFAULT_PLAN_END_TIME = "10:30"

// 判断当前日期是否落在计划日期区间内
fun isDateInRange(today: String, startDate: String, endDate: String): Boolean {
    return today >= startDate && today <= endDate
}

// 将时间文本解析成可比较的时间对象
fun parsePlanTime(timeText: String): LocalTime? {
    if (timeText.isBlank()) return null

    val formatter = DateTimeFormatter.ofPattern("H:mm")
    return try {
        LocalTime.parse(timeText, formatter)
    } catch (_: Exception) {
        null
    }
}

// 给任务卡片的时间点文本统一补上前缀
fun formatPlanTimeLabel(prefix: String, timeText: String): String {
    return if (timeText.isBlank()) {
        "$prefix --"
    } else {
        "$prefix $timeText"
    }
}

// 没有填写时，给计划补上默认时间点
fun normalizePlanTime(timeText: String, fallback: String): String {
    return timeText.trim().ifBlank { fallback }
}

// 根据当前时间和计划时间点决定卡片颜色
fun resolvePlanPointState(
    nowTime: LocalTime,
    startTimeText: String,
    endTimeText: String,
    isFinished: Boolean
): PlanPointState {
    if (isFinished) return PlanPointState.GREEN

    val startTime = parsePlanTime(startTimeText) ?: LocalTime.MIN
    val endTime = parsePlanTime(endTimeText) ?: LocalTime.MAX

    return when {
        nowTime.isBefore(startTime) -> PlanPointState.YELLOW
        nowTime.isAfter(endTime) -> PlanPointState.RED
        else -> PlanPointState.RED
    }
}
