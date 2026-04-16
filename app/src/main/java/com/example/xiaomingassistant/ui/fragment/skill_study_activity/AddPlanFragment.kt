package com.example.xiaomingassistant.ui.fragment.skill_study_activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.xiaomingassistant.PlanEditingActivity
import com.example.xiaomingassistant.R
import com.example.xiaomingassistant.ui.view.TopBarWithScrollView
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class AddPlanFragment : Fragment() {

    private var topBarWithScrollView: TopBarWithScrollView? = null

    private var startYearView: AutoCompleteTextView? = null
    private var startMonthView: AutoCompleteTextView? = null
    private var startDayView: AutoCompleteTextView? = null

    private var endYearView: AutoCompleteTextView? = null
    private var endMonthView: AutoCompleteTextView? = null
    private var endDayView: AutoCompleteTextView? = null

    private var titleEditText: TextInputEditText? = null
    private var noteEditText: TextInputEditText? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.activity_edit_skillstudy_add_plan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        setupTopBar()
        setupDateDropdowns()
    }

    private fun bindViews(view: View) {
        topBarWithScrollView = view.findViewById(R.id.skillstudy_edit_add_plan_topbar)

        startYearView = view.findViewById(R.id.skillstudy_edit_add_start_year)
        startMonthView = view.findViewById(R.id.skillstudy_edit_add_start_month)
        startDayView = view.findViewById(R.id.skillstudy_edit_add_start_day)

        endYearView = view.findViewById(R.id.skillstudy_edit_add_end_year)
        endMonthView = view.findViewById(R.id.skillstudy_edit_add_end_month)
        endDayView = view.findViewById(R.id.skillstudy_edit_add_end_day)

        titleEditText = view.findViewById(R.id.skillstudy_edit_add_title)
        noteEditText = view.findViewById(R.id.skillstudy_edit_add_note)
    }

    private fun setupTopBar() {
        topBarWithScrollView?.clearTopBarLeftIcons()
        topBarWithScrollView?.clearTopBarRightIcons()

        topBarWithScrollView?.addTopBarLeftIcon(R.drawable.back_arrow) {
            (requireActivity() as PlanEditingActivity).showMainFragment()
        }

        topBarWithScrollView?.addTopBarRightIcon(R.drawable.plus) {
            submitPlan()
        }
    }

    private fun setupDateDropdowns() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        val years = (currentYear..(currentYear + 5)).map { it.toString() }
        val months = (1..12).map { it.toString() }

        startYearView?.setAdapter(simpleAdapter(years))
        endYearView?.setAdapter(simpleAdapter(years))

        startMonthView?.setAdapter(simpleAdapter(months))
        endMonthView?.setAdapter(simpleAdapter(months))

        startYearView?.setOnItemClickListener { _, _, _, _ -> updateStartDayOptions() }
        startMonthView?.setOnItemClickListener { _, _, _, _ -> updateStartDayOptions() }

        endYearView?.setOnItemClickListener { _, _, _, _ -> updateEndDayOptions() }
        endMonthView?.setOnItemClickListener { _, _, _, _ -> updateEndDayOptions() }

        // 给一个默认值，避免一开始全空
        startYearView?.setText(currentYear.toString(), false)
        endYearView?.setText(currentYear.toString(), false)
        startMonthView?.setText("1", false)
        endMonthView?.setText("1", false)

        updateStartDayOptions()
        updateEndDayOptions()
    }

    private fun updateStartDayOptions() {
        val year = startYearView?.text?.toString()?.toIntOrNull()
        val month = startMonthView?.text?.toString()?.toIntOrNull()

        if (year == null || month == null) return

        val days = (1..getDaysInMonth(year, month)).map { it.toString() }
        val currentDay = startDayView?.text?.toString()

        startDayView?.setAdapter(simpleAdapter(days))

        if (currentDay.isNullOrBlank() || currentDay.toIntOrNull()?.let { it > days.size } == true) {
            startDayView?.setText(days.first(), false)
        }
    }

    private fun updateEndDayOptions() {
        val year = endYearView?.text?.toString()?.toIntOrNull()
        val month = endMonthView?.text?.toString()?.toIntOrNull()

        if (year == null || month == null) return

        val days = (1..getDaysInMonth(year, month)).map { it.toString() }
        val currentDay = endDayView?.text?.toString()

        endDayView?.setAdapter(simpleAdapter(days))

        if (currentDay.isNullOrBlank() || currentDay.toIntOrNull()?.let { it > days.size } == true) {
            endDayView?.setText(days.first(), false)
        }
    }

    private fun submitPlan() {
        val startDate = buildDateText(
            startYearView?.text?.toString(),
            startMonthView?.text?.toString(),
            startDayView?.text?.toString()
        )

        val endDate = buildDateText(
            endYearView?.text?.toString(),
            endMonthView?.text?.toString(),
            endDayView?.text?.toString()
        )

        val title = titleEditText?.text?.toString()?.trim().orEmpty()
        val note = noteEditText?.text?.toString()?.trim().orEmpty()

        when {
            startDate == null -> {
                toast("请完整选择开始日期")
            }

            endDate == null -> {
                toast("请完整选择结束日期")
            }

            title.isBlank() -> {
                toast("请输入计划标题")
            }

            else -> {
                // 这里后面接你的本地存储或数据库逻辑
                toast("添加成功\n$title\n$startDate ~ $endDate")
                (requireActivity() as PlanEditingActivity).showMainFragment()
            }
        }
    }

    private fun buildDateText(year: String?, month: String?, day: String?): String? {
        val y = year?.toIntOrNull() ?: return null
        val m = month?.toIntOrNull() ?: return null
        val d = day?.toIntOrNull() ?: return null
        return "%04d-%02d-%02d".format(y, m, d)
    }

    private fun getDaysInMonth(year: Int, month: Int): Int {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (isLeapYear(year)) 29 else 28
            else -> 30
        }
    }

    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

    private fun simpleAdapter(items: List<String>): ArrayAdapter<String> {
        return ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            items
        )
    }

    private fun toast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        topBarWithScrollView = null
        startYearView = null
        startMonthView = null
        startDayView = null
        endYearView = null
        endMonthView = null
        endDayView = null
        titleEditText = null
        noteEditText = null
    }
}