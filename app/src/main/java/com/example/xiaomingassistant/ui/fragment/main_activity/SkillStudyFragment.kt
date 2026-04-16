package com.example.xiaomingassistant.ui.fragment.main_activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
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
import com.example.xiaomingassistant.ui.content_display.card.TaskDisplayCardView
import com.google.android.material.card.MaterialCardView
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class SkillStudyFragment : Fragment(R.layout.main_interface_skillstudy) {

    private var cardLeft: MaterialCardView? = null
    private var cardRightTop: MaterialCardView? = null
    private var cardRightBottom: MaterialCardView? = null
    private var cardDisplayTask: LinearLayout? = null

    private var keepDaysText: TextView? = null
    private var finishedCountText: TextView? = null

    private var repository: PlanRepository? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = PlanRepository(requireContext())

        bindViews(view)
        setupCardLayout()
        setupClickEvents()
        renderPlanList()
    }

    override fun onResume() {
        super.onResume()
        renderPlanList()
    }

    private fun bindViews(view: View) {
        cardLeft = view.findViewById(R.id.skillstudy_card_top_left)
        cardRightTop = view.findViewById(R.id.skillstudy_card_top_right_1)
        cardRightBottom = view.findViewById(R.id.skillstudy_card_top_right_2)
        cardDisplayTask = view.findViewById(R.id.skillstudy_task_list_container)

        keepDaysText = view.findViewById(R.id.skillstudy_edit_right_top_card_tv_day_count)
        finishedCountText = view.findViewById(R.id.skillstudy_edit_right_top_card_tv_task_count)
    }

    private fun setupCardLayout() {
        val left = cardLeft ?: return
        val rightTop = cardRightTop ?: return
        val rightBottom = cardRightBottom ?: return

        left.post {
            val width = left.width
            val margin = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                12f,
                resources.displayMetrics
            ).toInt()

            left.updateLayoutParams {
                height = width
            }

            val smallHeight = (width - margin) / 2
            rightTop.updateLayoutParams {
                height = smallHeight
            }
            rightBottom.updateLayoutParams {
                height = smallHeight
            }
        }
    }

    private fun setupClickEvents() {
        cardLeft?.setOnClickListener {
            val intent = Intent(requireContext(), PlanEditingActivity::class.java)
            startActivity(intent)
        }
    }

    private fun renderPlanList() {
        val container = cardDisplayTask ?: return
        val repo = repository ?: return

        val list = repo.getAll()
        val today = LocalDate.now().toString()

        updateStatistics()

        container.removeAllViews()

        for (plan in list) {
            val taskCard = TaskDisplayCardView(requireContext())

            val pointColorRes = getPointColorRes(plan, today, repo)

            taskCard.setTaskData(
                pointColor = ContextCompat.getColor(requireContext(), pointColorRes),
                title = plan.title,
                dateRange = "${plan.startDate} ~ ${plan.endDate}",
                startTime = "开始 ${plan.startDate}",
                endTime = "结束 ${plan.endDate}",
                content = if (plan.note.isBlank()) "暂无备注" else plan.note
            )

            taskCard.setOnClickListener {
                showPlanActionDialog(plan, today)
            }

            container.addView(taskCard)
        }
    }

    private fun updateStatistics() {
        val repo = repository ?: return

        val earliestDateText = repo.getEarliestStartDate()
        val finishedTotal = repo.getFinishedTotalCount()

        val keepDays = if (earliestDateText.isNullOrBlank()) {
            0
        } else {
            try {
                val startDate = LocalDate.parse(earliestDateText)
                (ChronoUnit.DAYS.between(startDate, LocalDate.now()) + 1)
                    .toInt()
                    .coerceAtLeast(0)
            } catch (e: Exception) {
                0
            }
        }

        keepDaysText?.text = keepDays.toString()
        finishedCountText?.text = finishedTotal.toString()
    }

    private fun getPointColorRes(plan: Plan, today: String, repo: PlanRepository): Int {
        return when {
            repo.isFinishedToday(plan.id, today) -> R.color.point_green
            today < plan.startDate -> R.color.point_yellow
            else -> R.color.point_red
        }
    }

    private fun showPlanActionDialog(plan: Plan, today: String) {
        val repo = repository ?: return
        val finishedToday = repo.isFinishedToday(plan.id, today)

        val items = if (finishedToday) {
            arrayOf("撤销单日完成", "全部完成")
        } else {
            arrayOf("单日完成", "全部完成")
        }

        AlertDialog.Builder(requireContext())
            .setTitle(plan.title)
            .setItems(items) { _, which ->
                when (items[which]) {
                    "单日完成" -> {
                        repo.markFinishedToday(plan.id, today)
                        renderPlanList()
                    }

                    "撤销单日完成" -> {
                        repo.unmarkFinishedToday(plan.id, today)
                        renderPlanList()
                    }

                    "全部完成" -> {
                        showDeleteConfirmDialog(plan.id, plan.title)
                    }
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showDeleteConfirmDialog(planId: Long, title: String) {
        val repo = repository ?: return

        AlertDialog.Builder(requireContext())
            .setTitle("确认是否完成")
            .setMessage("全部完成后任务会被删除，且无法恢复。\n\n$title")
            .setPositiveButton("确认") { _, _ ->
                repo.deleteAsFinished(planId)
                renderPlanList()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cardLeft = null
        cardRightTop = null
        cardRightBottom = null
        cardDisplayTask = null
        keepDaysText = null
        finishedCountText = null
    }
}