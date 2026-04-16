package com.example.xiaomingassistant

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.xiaomingassistant.ui.fragment.skill_study_activity.EditPlanFragment
import com.example.xiaomingassistant.ui.fragment.skill_study_activity.MainPlaningFragment

class PlanEditingActivity : BaseActivity() {

    private val mainFragment = MainPlaningFragment()
    private val editFragment = EditPlanFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_skillstudy_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.plan_edit_fragment_container, mainFragment, "main")
                .commit()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isShowingEditFragment()) {
                    showMainFragment()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    fun showMainFragment() {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fast_fade_in,
                R.anim.fast_fade_out
            )
            .apply {
                if (editFragment.isAdded) hide(editFragment)
                if (mainFragment.isAdded) show(mainFragment)
            }
            .commit()

        mainFragment.refreshPlansIfVisible()
    }

    fun showAddFragment() {
        editFragment.openForCreate()

        if (!editFragment.isAdded) {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.fast_fade_in,
                    R.anim.fast_fade_out
                )
                .hide(mainFragment)
                .add(R.id.plan_edit_fragment_container, editFragment, "edit")
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.fast_fade_in,
                    R.anim.fast_fade_out
                )
                .hide(mainFragment)
                .show(editFragment)
                .commit()
        }
    }

    fun showEditFragment(planId: Long) {
        editFragment.openForEdit(planId)

        if (!editFragment.isAdded) {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.fast_fade_in,
                    R.anim.fast_fade_out
                )
                .hide(mainFragment)
                .add(R.id.plan_edit_fragment_container, editFragment, "edit")
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.fast_fade_in,
                    R.anim.fast_fade_out
                )
                .hide(mainFragment)
                .show(editFragment)
                .commit()
        }
    }

    private fun isShowingEditFragment(): Boolean {
        return editFragment.isAdded && editFragment.isVisible
    }
}