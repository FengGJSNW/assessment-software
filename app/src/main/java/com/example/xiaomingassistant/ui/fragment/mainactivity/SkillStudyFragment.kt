package com.example.xiaomingassistant.ui.fragment.mainactivity

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import com.example.xiaomingassistant.R
import com.example.xiaomingassistant.ui.view.TopBarWithScrollView
import com.google.android.material.card.MaterialCardView

class SkillStudyFragment : Fragment(R.layout.main_interface_skillstudy) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val topBarWithScrollView =
            view.findViewById<TopBarWithScrollView>(R.id.skillstudy_topbar_container)

        // 如果你以后想动态改标题，可以保留
        topBarWithScrollView.setTitle("技能规划")

        // 三个卡片
        val cardLeft = view.findViewById<MaterialCardView>(R.id.skillstudy_card_top_left)
        val cardRightTop = view.findViewById<MaterialCardView>(R.id.skillstudy_card_top_right_1)
        val cardRightBottom = view.findViewById<MaterialCardView>(R.id.skillstudy_card_top_right_2)

        // 卡片比例动态适配逻辑
        cardLeft.post {
            val width = cardLeft.width
            val margin = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                12f,
                resources.displayMetrics
            ).toInt()

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
}