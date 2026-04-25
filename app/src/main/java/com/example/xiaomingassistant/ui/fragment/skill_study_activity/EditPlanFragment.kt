package com.example.xiaomingassistant.ui.fragment.skill_study_activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import com.example.xiaomingassistant.PlanEditingActivity
import com.example.xiaomingassistant.R
import com.example.xiaomingassistant.data.PlanRepository
import com.example.xiaomingassistant.data.model.Plan
import com.example.xiaomingassistant.data.session.SessionManager
import com.example.xiaomingassistant.ui.view.TopBarWithScrollView
import com.example.xiaomingassistant.util.calc.DEFAULT_PLAN_END_TIME
import com.example.xiaomingassistant.util.calc.DEFAULT_PLAN_START_TIME
import com.example.xiaomingassistant.util.calc.normalizePlanTime
import com.example.xiaomingassistant.util.calc.parsePlanTime
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar
import com.example.xiaomingassistant.util.dialog.showConfirmDialog
import com.example.xiaomingassistant.util.toast.showShortToast


class EditPlanFragment : Fragment() {

    private lateinit var topBarWithScrollView: TopBarWithScrollView

    private lateinit var startYearView: AutoCompleteTextView
    private lateinit var startMonthView: AutoCompleteTextView
    private lateinit var startDayView: AutoCompleteTextView

    private lateinit var endYearView: AutoCompleteTextView
    private lateinit var endMonthView: AutoCompleteTextView
    private lateinit var endDayView: AutoCompleteTextView

    private lateinit var startTimeEditText: TextInputEditText
    private lateinit var endTimeEditText: TextInputEditText
    private lateinit var titleEditText: TextInputEditText
    private lateinit var noteEditText: TextInputEditText
    private var pendingPlanId: Long? = null
    private var editingPlanId: Long? = null

    private lateinit var sessionManager: SessionManager
    private var userId: Long = -1L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.activity_edit_skillstudy_edit_plan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        userId = sessionManager.getUserId()

        bindViews(view)
        setupDateDropdowns()
        loadPendingPlanIfNeeded()
        refreshTopBar()
    }

    fun openForCreate() {
        editingPlanId = null
        pendingPlanId = null

        if (view != null) {
            clearForm()
            refreshTopBar()
        }
    }

    fun openForEdit(planId: Long) {
        editingPlanId = planId
        pendingPlanId = planId

        if (view != null) {
            loadPendingPlanIfNeeded()
            refreshTopBar()
        }
    }

    override fun onResume() {
        super.onResume()
        topBarWithScrollView.post {
            topBarWithScrollView.refreshLayoutState()
        }
    }

    private fun loadPendingPlanIfNeeded() {
        val planId = pendingPlanId ?: return
        loadPlan(planId)
        pendingPlanId = null
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            topBarWithScrollView.post {
                topBarWithScrollView.refreshLayoutState()
            }
        }
    }

    // 绑定编辑计划页的所有输入组件
    private fun bindViews(view: View) {
        topBarWithScrollView = view.findViewById(R.id.skillstudy_edit_add_plan_topbar)

        startYearView = view.findViewById(R.id.skillstudy_edit_add_start_year)
        startMonthView = view.findViewById(R.id.skillstudy_edit_add_start_month)
        startDayView = view.findViewById(R.id.skillstudy_edit_add_start_day)

        endYearView = view.findViewById(R.id.skillstudy_edit_add_end_year)
        endMonthView = view.findViewById(R.id.skillstudy_edit_add_end_month)
        endDayView = view.findViewById(R.id.skillstudy_edit_add_end_day)

        startTimeEditText = view.findViewById(R.id.skillstudy_edit_add_start_time)
        endTimeEditText = view.findViewById(R.id.skillstudy_edit_add_end_time)
        titleEditText = view.findViewById(R.id.skillstudy_edit_add_title)
        noteEditText = view.findViewById(R.id.skillstudy_edit_add_note)

        startTimeEditText.setText(DEFAULT_PLAN_START_TIME)
        endTimeEditText.setText(DEFAULT_PLAN_END_TIME)
    }

    // 根据当前模式刷新顶部按钮
    private fun refreshTopBar() {
        topBarWithScrollView.clearTopBarLeftIcons()
        topBarWithScrollView.clearTopBarRightIcons()

        topBarWithScrollView.addTopBarLeftIcon(R.drawable.back_arrow) {
            (requireActivity() as PlanEditingActivity).showMainFragment()
        }

        if (editingPlanId == null) {
            topBarWithScrollView.setTitle("添加计划")
            topBarWithScrollView.addTopBarRightIcon(R.drawable.plus) {
                submitPlan()
            }
        } else {
            topBarWithScrollView.setTitle("编辑计划")

            topBarWithScrollView.addTopBarRightIcon(R.drawable.save) {
                submitPlan()
            }

            topBarWithScrollView.addTopBarRightIcon(R.drawable.garbage) {
                deletePlan()
            }
        }
    }

    // 初始化起止日期的下拉选项
    private fun setupDateDropdowns() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        val years = (currentYear..(currentYear + 100)).map { it.toString() }
        val months = (1..12).map { it.toString() }

        startYearView.setAdapter(simpleAdapter(years))
        endYearView.setAdapter(simpleAdapter(years))

        startMonthView.setAdapter(simpleAdapter(months))
        endMonthView.setAdapter(simpleAdapter(months))

        startYearView.setOnItemClickListener { _, _, _, _ -> updateStartDayOptions() }
        startMonthView.setOnItemClickListener { _, _, _, _ -> updateStartDayOptions() }

        endYearView.setOnItemClickListener { _, _, _, _ -> updateEndDayOptions() }
        endMonthView.setOnItemClickListener { _, _, _, _ -> updateEndDayOptions() }

        startYearView.setText(currentYear.toString(), false)
        endYearView.setText(currentYear.toString(), false)
        startMonthView.setText("1", false)
        endMonthView.setText("1", false)

        updateStartDayOptions()
        updateEndDayOptions()
    }

    private fun updateStartDayOptions() {
        val year = startYearView.text?.toString()?.toIntOrNull()
        val month = startMonthView.text?.toString()?.toIntOrNull()
        if (year == null || month == null) return

        val days = (1..getDaysInMonth(year, month)).map { it.toString() }
        val currentDay = startDayView.text?.toString()

        startDayView.setAdapter(simpleAdapter(days))
        if (currentDay.isNullOrBlank() || currentDay.toIntOrNull()
                ?.let { it > days.size } == true
        ) {
            startDayView.setText(days.first(), false)
        }
    }

    private fun updateEndDayOptions() {
        val year = endYearView.text?.toString()?.toIntOrNull()
        val month = endMonthView.text?.toString()?.toIntOrNull()
        if (year == null || month == null) return

        val days = (1..getDaysInMonth(year, month)).map { it.toString() }
        val currentDay = endDayView.text?.toString()

        endDayView.setAdapter(simpleAdapter(days))
        if (currentDay.isNullOrBlank() || currentDay.toIntOrNull()
                ?.let { it > days.size } == true
        ) {
            endDayView.setText(days.first(), false)
        }
    }

    // 提交前先完成日期和标题校验
    private fun submitPlan() {
        val startDate = buildDateText(
            startYearView.text?.toString(),
            startMonthView.text?.toString(),
            startDayView.text?.toString()
        )

        val endDate = buildDateText(
            endYearView.text?.toString(),
            endMonthView.text?.toString(),
            endDayView.text?.toString()
        )

        val title = titleEditText.text?.toString()?.trim().orEmpty()
        val note = noteEditText.text?.toString()?.trim().orEmpty()
        val startTime = normalizePlanTime(
            startTimeEditText.text?.toString().orEmpty(),
            DEFAULT_PLAN_START_TIME
        )
        val endTime = normalizePlanTime(
            endTimeEditText.text?.toString().orEmpty(),
            DEFAULT_PLAN_END_TIME
        )

        when {
            startDate == null -> showShortToast("请完整选择开始日期")
            endDate == null -> showShortToast("请完整选择结束日期")
            parsePlanTime(startTime) == null -> showShortToast("请输入正确的开始时间，例如 07:30")
            parsePlanTime(endTime) == null -> showShortToast("请输入正确的结束时间，例如 10:30")
            title.isBlank() -> showShortToast("请输入计划标题")
            else -> {
                val repo = PlanRepository(requireContext())

                if (editingPlanId == null) {
                    repo.insert(
                        Plan(
                            userId = userId,
                            title = title,
                            startDate = startDate,
                            endDate = endDate,
                            startTime = startTime,
                            endTime = endTime,
                            note = note
                        )
                    )
                    showShortToast("添加成功")
                } else {
                    repo.update(
                        Plan(
                            id = editingPlanId!!,
                            userId = userId,
                            title = title,
                            startDate = startDate,
                            endDate = endDate,
                            startTime = startTime,
                            endTime = endTime,
                            note = note
                        )
                    )
                    showShortToast("保存成功")
                }

                (requireActivity() as PlanEditingActivity).showMainFragment()
            }
        }
    }

    // 删除计划前弹出确认框
    private fun deletePlan() {
        val planId = editingPlanId ?: return

        requireContext().showConfirmDialog(
            title = "确认删除",
            message = "确定要删除这个计划吗？删除后无法恢复。",
            positiveText = "确认删除"
        ) {
                val repo = PlanRepository(requireContext())
                repo.delete(userId, planId)
                showShortToast("删除成功")
                editingPlanId = null
                (activity as? PlanEditingActivity)?.showMainFragment()
            }
    }

    // 编辑模式下回填计划数据
    private fun loadPlan(planId: Long) {
        val repo = PlanRepository(requireContext())
        val plan = repo.getById(userId, planId) ?: return

        titleEditText.setText(plan.title)
        noteEditText.setText(plan.note)
        startTimeEditText.setText(normalizePlanTime(plan.startTime, DEFAULT_PLAN_START_TIME))
        endTimeEditText.setText(normalizePlanTime(plan.endTime, DEFAULT_PLAN_END_TIME))

        applyDateToViews(plan.startDate, startYearView, startMonthView, startDayView)
        applyDateToViews(plan.endDate, endYearView, endMonthView, endDayView)
    }

    // 将数据库中的日期拆开后写回到输入框
    private fun applyDateToViews(
        dateText: String?,
        yearView: AutoCompleteTextView,
        monthView: AutoCompleteTextView,
        dayView: AutoCompleteTextView
    ) {
        val parts = dateText?.split("-") ?: return
        if (parts.size != 3) return

        yearView.setText(parts[0], false)
        monthView.setText(parts[1].toIntOrNull()?.toString().orEmpty(), false)

        val year = parts[0].toIntOrNull()
        val month = parts[1].toIntOrNull()
        if (year != null && month != null) {
            val days = (1..getDaysInMonth(year, month)).map { it.toString() }
            dayView.setAdapter(simpleAdapter(days))
        }

        dayView.setText(parts[2].toIntOrNull()?.toString().orEmpty(), false)
    }

    // 切回新增模式时清空表单
    private fun clearForm() {
        titleEditText.setText("")
        noteEditText.setText("")
        startTimeEditText.setText(DEFAULT_PLAN_START_TIME)
        endTimeEditText.setText(DEFAULT_PLAN_END_TIME)

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        startYearView.setText(currentYear.toString(), false)
        endYearView.setText(currentYear.toString(), false)
        startMonthView.setText("1", false)
        endMonthView.setText("1", false)

        updateStartDayOptions()
        updateEndDayOptions()
    }


    /**
     * @param 下方为工具区域
     */

    private fun simpleAdapter(items: List<String>): ArrayAdapter<String> {
        return ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            items
        )
    }

    // 获取月中的天数
    private fun getDaysInMonth(year: Int, month: Int): Int {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> {
                if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) 29 else 28
            }

            else -> 30
        }
    }

    // 生成日期文本
    private fun buildDateText(year: String?, month: String?, day: String?): String? {
        val y = year?.toIntOrNull() ?: return null
        val m = month?.toIntOrNull() ?: return null
        val d = day?.toIntOrNull() ?: return null
        return String.format("%04d-%02d-%02d", y, m, d)
    }

}
