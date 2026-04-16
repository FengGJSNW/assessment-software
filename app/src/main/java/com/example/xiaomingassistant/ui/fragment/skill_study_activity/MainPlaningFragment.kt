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
import com.example.xiaomingassistant.ui.content_display.card.TaskDisplayCardView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class MainPlaningFragment : Fragment() {

    private var taskContainer: LinearLayout? = null
    private var planCountText: TextView? = null

    private var cardLeft: MaterialCardView? = null
    private var cardRight: MaterialCardView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.activity_edit_skillstudy_intersection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addPlanButton = view.findViewById<MaterialButton>(R.id.skillstudy_edit_btn_add_plan)

        taskContainer = view.findViewById(R.id.skillstudy_edit_task_list_container)
        planCountText = view.findViewById(R.id.skillstudy_edit_plan_count)

        cardLeft = view.findViewById(R.id.skillstudy_edit_card_left)
        cardRight = view.findViewById(R.id.skillstudy_edit_card_right)

        setupTopCards()

        addPlanButton.setOnClickListener {
            (requireActivity() as PlanEditingActivity).showAddFragment()
        }

        renderPlans()
    }

    private fun setupTopCards() {
        val left = cardLeft ?: return
        val right = cardRight ?: return

        left.post {
            val width = left.width

            left.updateLayoutParams<LinearLayout.LayoutParams> {
                height = width
            }

            right.updateLayoutParams<LinearLayout.LayoutParams> {
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
        val container = taskContainer ?: return
        val repo = PlanRepository(requireContext())
        val list = repo.getAll()

        planCountText?.text = list.size.toString()

        container.removeAllViews()

        for (plan in list) {
            val card = TaskDisplayCardView(requireContext())

            card.setTaskData(
                pointColor = requireContext().getColor(R.color.sky_blue),
                title = plan.title,
                dateRange = "${plan.startDate} ~ ${plan.endDate}",
                startTime = "开始 ${plan.startDate}",
                endTime = "结束 ${plan.endDate}",
                content = if (plan.note.isBlank()) "暂无备注" else plan.note
            )

            card.setOnClickListener {
                (requireActivity() as PlanEditingActivity).showEditFragment(plan.id)
            }

            container.addView(card)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        taskContainer = null
        planCountText = null
        cardLeft = null
        cardRight = null
    }
}