package com.example.xiaomingassistant.ui.fragment.skill_study_activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.xiaomingassistant.PlanEditingActivity
import com.example.xiaomingassistant.R
import com.example.xiaomingassistant.ui.view.TopBarWithScrollView
import com.google.android.material.button.MaterialButton

class MainPlaningFragment : Fragment() {
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
        val deletePlanButton = view.findViewById<MaterialButton>(R.id.skillstudy_edit_btn_delete_plan)
        val topBarWithScrollView = view.findViewById<TopBarWithScrollView>(R.id.skillstudy_edit_intersection_topbar)

        topBarWithScrollView.clearTopBarLeftIcons()
        topBarWithScrollView.clearTopBarRightIcons()

        topBarWithScrollView.addTopBarLeftIcon(R.drawable.back_arrow) { activity?.finish() }

        addPlanButton.setOnClickListener {
            (requireActivity() as PlanEditingActivity).showAddFragment()
        }

        deletePlanButton.setOnClickListener {
            (requireActivity() as PlanEditingActivity).showDeleteFragment()
        }
    }


}