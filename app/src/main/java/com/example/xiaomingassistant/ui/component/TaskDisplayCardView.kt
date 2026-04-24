package com.example.xiaomingassistant.ui.component

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
    private val titleView: TextView
    private val dateRangeView: TextView
    private val startTimeView: TextView
    private val endTimeView: TextView
    private val summaryView: TextView

    init {
        LayoutInflater.from(context).inflate(
            R.layout.myview_inner_display_card_for_task,
            this,
            true
        )

        pointView = findViewById(R.id.internal_icon)
        titleView = findViewById(R.id.internal_title)
        dateRangeView = findViewById(R.id.internal_date_range)
        startTimeView = findViewById(R.id.internal_start_time)
        endTimeView = findViewById(R.id.internal_end_time)
        summaryView = findViewById(R.id.internal_summary)
    }

    fun setPointColor(@ColorInt color: Int) {
        pointView.imageTintList = ColorStateList.valueOf(color)
    }

    fun setTitle(title: String) {
        titleView.text = title
    }

    fun setDateRange(dateRange: String) {
        dateRangeView.text = dateRange
    }

    fun setStartTime(startTime: String) {
        startTimeView.text = startTime
    }

    fun setEndTime(endTime: String) {
        endTimeView.text = endTime
    }

    fun setTime(startTime: String, endTime: String) {
        startTimeView.text = startTime
        endTimeView.text = endTime
    }

    fun setTaskContent(content: String) {
        summaryView.text = content
    }

    fun setTaskData(
        @ColorInt pointColor: Int,
        title: String,
        dateRange: String,
        startTime: String,
        endTime: String,
        content: String
    ) {
        setPointColor(pointColor)
        setTitle(title)
        setDateRange(dateRange)
        setTime(startTime, endTime)
        setTaskContent(content)
    }

    fun getTitle(): String {
        return titleView.text.toString()
    }

    fun getDateRange(): String {
        return dateRangeView.text.toString()
    }

    fun getStartTime(): String {
        return startTimeView.text.toString()
    }

    fun getEndTime(): String {
        return endTimeView.text.toString()
    }

    fun getTaskContent(): String {
        return summaryView.text.toString()
    }
}