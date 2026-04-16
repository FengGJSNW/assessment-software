package com.example.xiaomingassistant

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.xiaomingassistant.ui.fragment.skill_study_activity.AddPlanFragment
import com.example.xiaomingassistant.ui.fragment.skill_study_activity.DeletePlanFragment
import com.example.xiaomingassistant.ui.fragment.skill_study_activity.MainPlaningFragment

class PlanEditingActivity : BaseActivity() {

    private val mainFragment = MainPlaningFragment()
    private val addFragment = AddPlanFragment()
    private val deleteFragment = DeletePlanFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_skillstudy_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.plan_edit_fragment_container, mainFragment, "main")
                .add(R.id.plan_edit_fragment_container, addFragment, "add")
                .hide(addFragment)
                .add(R.id.plan_edit_fragment_container, deleteFragment, "delete")
                .hide(deleteFragment)
                .commit()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isShowingAddOrDelete()) {
                    showMainFragment()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    fun showMainFragment() {
        switchTo(mainFragment)
    }

    fun showAddFragment() {
        switchTo(addFragment)
    }

    fun showDeleteFragment() {
        switchTo(deleteFragment)
    }

    private fun switchTo(target: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fast_fade_in,
                R.anim.fast_fade_out
            )
            .apply {
                listOf(mainFragment, addFragment, deleteFragment).forEach { fragment ->
                    if (fragment == target) show(fragment) else hide(fragment)
                }
            }
            .commit()
    }

    private fun isShowingAddOrDelete(): Boolean {
        return addFragment.isVisible || deleteFragment.isVisible
    }
}