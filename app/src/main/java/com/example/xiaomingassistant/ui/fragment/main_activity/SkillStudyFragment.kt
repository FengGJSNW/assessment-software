package com.example.xiaomingassistant.ui.fragment.main_activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import com.example.xiaomingassistant.PlanEditingActivity
import com.example.xiaomingassistant.R
import com.example.xiaomingassistant.data.PlanRepository
import com.example.xiaomingassistant.data.model.Plan
import com.example.xiaomingassistant.data.session.SessionManager
import com.example.xiaomingassistant.ui.component.TaskDisplayCardView
import com.example.xiaomingassistant.util.calc.DEFAULT_PLAN_END_TIME
import com.example.xiaomingassistant.util.calc.DEFAULT_PLAN_START_TIME
import com.example.xiaomingassistant.util.calc.PlanPointState
import com.example.xiaomingassistant.util.calc.dp
import com.example.xiaomingassistant.util.calc.formatPlanTimeLabel
import com.example.xiaomingassistant.util.calc.isDateInRange
import com.example.xiaomingassistant.util.calc.normalizePlanTime
import com.example.xiaomingassistant.util.calc.resolvePlanPointState
import com.example.xiaomingassistant.util.dialog.style.applyRoundedStyle
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.LocalDate
import java.time.LocalTime

class SkillStudyFragment : Fragment(R.layout.main_interface_skillstudy) {

    private lateinit var cardLeft: MaterialCardView
    private lateinit var cardRightTop: MaterialCardView
    private lateinit var cardRightBottom: MaterialCardView
    private lateinit var cardDisplayTask: LinearLayout

    private lateinit var keepDaysText: TextView
    private lateinit var finishedCountText: TextView

    private lateinit var repository: PlanRepository

    private lateinit var sessionManager: SessionManager
    private lateinit var emptyHintTv: TextView

    private var userId: Long = -1L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = PlanRepository(requireContext())
        sessionManager = SessionManager(requireContext())
        userId = sessionManager.getUserId()

        bindViews(view)
        setupCardLayout()
        setupClickEvents()
        renderPlanList()
    }

    override fun onResume() {
        super.onResume()
        renderPlanList()
    }

    // 绑定学习计划首页组件
    private fun bindViews(view: View) {
        cardLeft = view.findViewById(R.id.skillstudy_card_top_left)
        cardRightTop = view.findViewById(R.id.skillstudy_card_top_right_1)
        cardRightBottom = view.findViewById(R.id.skillstudy_card_top_right_2)
        cardDisplayTask = view.findViewById(R.id.skillstudy_task_list_container)

        keepDaysText = view.findViewById(R.id.skillstudy_edit_right_top_card_tv_day_count)
        finishedCountText = view.findViewById(R.id.skillstudy_edit_right_top_card_tv_task_count)
        emptyHintTv = view.findViewById(R.id.skillstudy_tv_empty_hint)
    }

    // 让左侧方卡和右侧两张小卡保持成套比例
    private fun setupCardLayout() {
        cardLeft.post {
            val width = cardLeft.width
            val margin = 12.dp

            cardLeft.updateLayoutParams {
                height = width
            }

            val smallHeight = (width - margin) / 2
            cardRightTop.updateLayoutParams {
                height = smallHeight
            }
            cardRightBottom.updateLayoutParams {
                height = smallHeight
            }
        }
    }

    // 点击左上角卡片进入计划编辑页
    private fun setupClickEvents() {
        cardLeft.setOnClickListener {
            val intent = Intent(requireContext(), PlanEditingActivity::class.java)
            startActivity(intent)
        }
    }

    // 刷新计划列表，并根据状态设置卡片颜色
    private fun renderPlanList() {
        val today = LocalDate.now().toString()
        val list = repository.getAll(userId).filter { plan ->
            isDateInRange(today, plan.startDate, plan.endDate)
        }

        updateStatistics(today, list)

        cardDisplayTask.removeAllViews()

        if (list.isEmpty()) {
            emptyHintTv.visibility = View.VISIBLE        // 显示“暂无计划”提示
        } else {
            emptyHintTv.visibility = View.GONE           // 隐藏提示文字
        }

        for (plan in list) {
            val taskCard = TaskDisplayCardView(requireContext())

            val pointColorRes = getPointColorRes(plan, today)

            taskCard.setTaskData(
                pointColor = ContextCompat.getColor(requireContext(), pointColorRes),
                title = plan.title,
                dateRange = "${plan.startDate} ~ ${plan.endDate}",
                startTime = formatPlanTimeLabel("开始", normalizePlanTime(plan.startTime, DEFAULT_PLAN_START_TIME)),
                endTime = formatPlanTimeLabel("结束", normalizePlanTime(plan.endTime, DEFAULT_PLAN_END_TIME)),
                content = if (plan.note.isBlank()) "暂无备注" else plan.note
            )

            taskCard.setOnClickListener {
                showPlanActionDialog(plan, today)
            }

            cardDisplayTask.addView(taskCard)
        }
    }

    // 更新坚持天数与已完成任务数
    private fun updateStatistics(today: String, plans: List<Plan>) {
        val finishedTotal = repository.getFinishedTotalCount(userId)
        val allFinishedToday = plans.isNotEmpty() && plans.all { plan ->
            isPlanCompletedToday(plan, today)
        }

        val keepDays = repository.syncKeepDays(userId, today, allFinishedToday)

        keepDaysText.text = keepDays.toString()
        finishedCountText.text = finishedTotal.toString()
    }

    // 判断某条计划今天是否已经完成
    private fun isPlanCompletedToday(plan: Plan, today: String): Boolean {
        return plan.isFinished == 1 || repository.isFinishedToday(userId, plan.id, today)
    }

    // 根据计划当天状态返回不同的提示颜色
    private fun getPointColorRes(plan: Plan, today: String): Int {
        val state = resolvePlanPointState(
            nowTime = LocalTime.now(),
            startTimeText = normalizePlanTime(plan.startTime, DEFAULT_PLAN_START_TIME),
            endTimeText = normalizePlanTime(plan.endTime, DEFAULT_PLAN_END_TIME),
            isFinished = isPlanCompletedToday(plan, today)
        )

        return when (state) {
            PlanPointState.YELLOW -> R.color.point_yellow
            PlanPointState.RED -> R.color.point_red
            PlanPointState.GREEN -> R.color.point_green
        }
    }

    // 点击任务卡片后弹出操作菜单
    private fun showPlanActionDialog(plan: Plan, today: String) {
        val finishedToday = repository.isFinishedToday(userId, plan.id, today)

        val builder = MaterialAlertDialogBuilder(requireContext())
            .setTitle("任务：${plan.title}")
            .setMessage(
                if (finishedToday) {
                    "今天这个任务已经完成，你可以撤销今日完成，或将它标记为全部完成。"
                } else {
                    "你可以将这个任务标记为单日完成，或直接全部完成。"
                }
            )
            .setNeutralButton("取消", null)

        if (finishedToday) {
            builder.setNegativeButton("撤销单日完成") { _, _ ->
                repository.unmarkFinishedToday(userId, plan.id, today)
                renderPlanList()
            }
        } else {
            builder.setNegativeButton("单日完成") { _, _ ->
                repository.markFinishedToday(userId, plan.id, today)
                renderPlanList()
            }
        }

        builder.setPositiveButton("全部完成") { _, _ ->
            showDeleteConfirmDialog(plan.id, plan.title)
        }

        val dialog = builder.create()
        styleDialog(dialog)
    }

    // 将任务标记为全部完成前再次确认
    private fun showDeleteConfirmDialog(planId: Long, title: String) {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("确认全部完成")
            .setMessage("任务「$title」全部完成后会被删除，且无法恢复。")
            .setPositiveButton("确认完成") { _, _ ->
                repository.deleteAsFinished(userId, planId)
                renderPlanList()
            }
            .setNegativeButton("再想想", null)
            .create()

        styleDialog(dialog)
    }

    // 统一应用项目里的圆角弹窗样式
    private fun styleDialog(dialog: androidx.appcompat.app.AlertDialog) {
        dialog.applyRoundedStyle()
    }

    // 没有今日计划时给出轻量提示
    private fun createHintCard(message: String): View {
        return TextView(requireContext()).apply {
            text = message
            setTextColor(0xFF666666.toInt())
            setPadding(16.dp, 16.dp, 16.dp, 16.dp)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
    }
}
