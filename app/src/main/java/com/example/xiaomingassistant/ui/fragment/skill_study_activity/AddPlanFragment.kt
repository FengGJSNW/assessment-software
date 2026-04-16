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

class AddPlanFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.activity_edit_skillstudy_add_plan, container, false)
    }

    private var topBarWithScrollView : TopBarWithScrollView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        topBarWithScrollView = view.findViewById<TopBarWithScrollView>(R.id.skillstudy_edit_add_plan_topbar)

        topBarWithScrollView?.clearTopBarLeftIcons()
        topBarWithScrollView?.clearTopBarRightIcons()

        topBarWithScrollView?.addTopBarLeftIcon(R.drawable.back_arrow) {
            (requireActivity() as PlanEditingActivity).showMainFragment()
        }
    }
}