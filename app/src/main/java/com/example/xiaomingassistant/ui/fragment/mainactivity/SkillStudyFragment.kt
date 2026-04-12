package com.example.xiaomingassistant.ui.fragment.mainactivity

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import com.example.xiaomingassistant.R
import com.example.xiaomingassistant.ui.view.TaskDisplayCardView
import com.example.xiaomingassistant.ui.view.TopBarWithScrollView
import com.google.android.material.card.MaterialCardView

class SkillStudyFragment : Fragment(R.layout.main_interface_skillstudy) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val topBarWithScrollView =
            view.findViewById<TopBarWithScrollView>(R.id.skillstudy_topbar_container)

        topBarWithScrollView.setTitle("技能规划")

        val cardLeft = view.findViewById<MaterialCardView>(R.id.skillstudy_card_top_left)
        val cardRightTop = view.findViewById<MaterialCardView>(R.id.skillstudy_card_top_right_1)
        val cardRightBottom = view.findViewById<MaterialCardView>(R.id.skillstudy_card_top_right_2)

        val cardDisplayTask = view.findViewById<LinearLayout>(R.id.task_list_container)

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

        val taskTestList = arrayOf(
            TaskTestData(R.color.black, "09:00", "10:30", "这里是任务内容1"),
            TaskTestData(R.color.card_darkgreen, "10:40", "11:20", "这里是任务内容2"),
            TaskTestData(R.color.purple_500, "14:00", "15:30", "这里是任务内容3"),
            TaskTestData(R.color.black, "15:40", "16:20", "这里是任务内容4"),
            TaskTestData(R.color.card_darkgreen, "16:30", "17:10", "这里是任务内容5"),
            TaskTestData(R.color.purple_500, "17:20", "18:00", "这里是任务内容6"),
            TaskTestData(R.color.black, "18:10", "18:50", "这里是任务内容7"),
            TaskTestData(R.color.card_darkgreen, "19:00", "19:40", "这里是任务内容8"),
            TaskTestData(R.color.purple_500, "20:00", "21:00", "这里是任务内容9"),
            TaskTestData(R.color.black, "21:10", "22:00", "这里是任务内容10"),
            TaskTestData(R.color.black, "21:10", "22:00", "这里是任务内容11"),
            TaskTestData(R.color.purple_500, "20:00", "21:00", "这里是任务内容12"),
            TaskTestData(R.color.black, "21:10", "22:00", "这里是任务内容13"),
            TaskTestData(R.color.black, "21:10", "22:00", "这里是任务内容14")
        )

        cardDisplayTask.removeAllViews()

        for (task in taskTestList) {
            val taskCard = TaskDisplayCardView(requireContext())
            taskCard.setTaskData(
                pointColor = ContextCompat.getColor(requireContext(), task.colorRes),
                startTime = task.startTime,
                endTime = task.endTime,
                content = task.content
            )
            cardDisplayTask.addView(taskCard)
        }


    }
}

data class TaskTestData(
    val colorRes: Int,
    val startTime: String,
    val endTime: String,
    val content: String
)