package com.example.xiaomingassistant.ui.fragment.skill_study_activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import com.example.xiaomingassistant.PlanEditingActivity
import com.example.xiaomingassistant.R
import com.example.xiaomingassistant.data.PlanRepository
import com.example.xiaomingassistant.data.session.SessionManager
import com.example.xiaomingassistant.ui.component.TaskDisplayCardView
import com.example.xiaomingassistant.util.calc.DEFAULT_PLAN_END_TIME
import com.example.xiaomingassistant.util.calc.DEFAULT_PLAN_START_TIME
import com.example.xiaomingassistant.util.calc.formatPlanTimeLabel
import com.example.xiaomingassistant.util.calc.normalizePlanTime
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class MainPlaningFragment : Fragment() {

    private lateinit var taskContainer: LinearLayout
    private lateinit var planCountText: TextView
    private lateinit var addPlanButton: MaterialButton

    private lateinit var cardLeft: MaterialCardView
    private lateinit var cardRight: MaterialCardView

    private lateinit var sessionManager: SessionManager
    private var userId: Long = -1L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.activity_edit_skillstudy_intersection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        userId = sessionManager.getUserId()

        bindViews(view)
        setupTopCards()
        setupListeners()
        renderPlans()
    }

    // 绑定计划列表页组件
    private fun bindViews(view: View) {
        addPlanButton = view.findViewById(R.id.skillstudy_edit_btn_add_plan)
        taskContainer = view.findViewById(R.id.skillstudy_edit_task_list_container)
        planCountText = view.findViewById(R.id.skillstudy_edit_plan_count)
        cardLeft = view.findViewById(R.id.skillstudy_edit_card_left)
        cardRight = view.findViewById(R.id.skillstudy_edit_card_right)
    }

    // 绑定新增计划入口
    private fun setupListeners() {
        addPlanButton.setOnClickListener {
            (requireActivity() as PlanEditingActivity).showAddFragment()
        }
    }

    private fun setupTopCards() {
        cardLeft.post {
            val width = cardLeft.width

            cardLeft.updateLayoutParams<LinearLayout.LayoutParams> {
                height = width
            }

            cardRight.updateLayoutParams<LinearLayout.LayoutParams> {
                height = width
            }
        }
    }

    fun refreshPlansIfVisible() {
        if (isAdded && view != null) {
            renderPlans()
        }
    }

    private fun renderPlans() {
        val repo = PlanRepository(requireContext())
        val list = repo.getAll(userId)

        planCountText.text = list.size.toString()

        taskContainer.removeAllViews()

        for (plan in list) {
            val card = TaskDisplayCardView(requireContext())

            card.setTaskData(
                pointColor = requireContext().getColor(R.color.sky_blue),
                title = plan.title,
                dateRange = "${plan.startDate} ~ ${plan.endDate}",
                startTime = formatPlanTimeLabel("开始", normalizePlanTime(plan.startTime, DEFAULT_PLAN_START_TIME)),
                endTime = formatPlanTimeLabel("结束", normalizePlanTime(plan.endTime, DEFAULT_PLAN_END_TIME)),
                content = if (plan.note.isBlank()) "暂无备注" else plan.note
            )

            card.setOnClickListener {
                (requireActivity() as PlanEditingActivity).showEditFragment(plan.id)
            }

            taskContainer.addView(card)
        }
    }
}
