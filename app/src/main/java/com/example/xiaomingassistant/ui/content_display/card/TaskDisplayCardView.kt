package com.example.xiaomingassistant.ui.content_display.card

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.xiaomingassistant.R

class TaskDisplayCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val pointView: ImageView
    private val startTimeView: TextView
    private val endTimeView: TextView
    private val summaryView: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.myview_inner_display_card_for_task, this, true)

        pointView = findViewById(R.id.internal_icon)
        startTimeView = findViewById(R.id.internal_start_time)
        endTimeView = findViewById(R.id.internal_end_time)
        summaryView = findViewById(R.id.internal_summary)
    }

    /** 设置 point 颜色 */
    fun setPointColor(@ColorInt color: Int) {
        pointView.imageTintList = ColorStateList.valueOf(color)
    }

    /** 设置开始时间 */
    fun setStartTime(startTime: String) {
        startTimeView.text = startTime
    }

    /** 设置结束时间 */
    fun setEndTime(endTime: String) {
        endTimeView.text = endTime
    }

    /** 同时设置时间 */
    fun setTime(startTime: String, endTime: String) {
        startTimeView.text = startTime
        endTimeView.text = endTime
    }

    /** 设置任务内容 */
    fun setTaskContent(content: String) {
        summaryView.text = content
    }

    /** 一次性设置全部数据 */
    fun setTaskData(
        @ColorInt pointColor: Int,
        startTime: String,
        endTime: String,
        content: String
    ) {
        setPointColor(pointColor)
        setTime(startTime, endTime)
        setTaskContent(content)
    }

    /** 获取开始时间 */
    fun getStartTime(): String {
        return startTimeView.text.toString()
    }

    /** 获取结束时间 */
    fun getEndTime(): String {
        return endTimeView.text.toString()
    }

    /** 获取任务内容 */
    fun getTaskContent(): String {
        return summaryView.text.toString()
    }
}