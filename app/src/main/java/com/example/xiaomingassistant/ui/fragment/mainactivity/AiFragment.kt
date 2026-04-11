package com.example.xiaomingassistant.ui.fragment.mainactivity

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.xiaomingassistant.R
import com.example.xiaomingassistant.ui.utils.TopBarManager

class AiFragment : Fragment(R.layout.main_interface_ai) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 调用 setup。注意参数名是 rootView，如果你之前写的是 view = view，请改为 rootView = view
        TopBarManager.setup(
            fragment = this,
            rootView = view,
            title = "AI 助手"
        ) { resId ->
            when (resId) {
                R.drawable.exit -> parentFragmentManager.popBackStack()
            }
        }
    }
}