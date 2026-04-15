package com.example.xiaomingassistant

import android.os.Bundle
import com.google.android.material.button.MaterialButton
import com.example.xiaomingassistant.ui.fragment.skill_study_activity.AddPlanFragment
import com.example.xiaomingassistant.ui.fragment.skill_study_activity.DeletePlanFragment


class PlanEditingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_skillstudy_intersection)

        val addPlanButton = findViewById<MaterialButton>(R.id.skillstudy_edit_btn_add_plan)
        val deletePlanButton = findViewById<MaterialButton>(R.id.skillstudy_edit_btn_delete_plan)

        // 默认先显示添加计划页
        if (savedInstanceState == null) {
            switchFragment(AddPlanFragment())
        }

        addPlanButton.setOnClickListener {
            switchFragment(AddPlanFragment())
        }

        deletePlanButton.setOnClickListener {
            switchFragment(DeletePlanFragment())
        }
    }

    private fun switchFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            .replace(R.id.plan_edit_fragment_container, fragment)
            .commit()
    }
}