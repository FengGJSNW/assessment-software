package com.example.xiaomingassistant.ui.fragment.main_activity

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.xiaomingassistant.R
import com.example.xiaomingassistant.ui.fragment.main_activity.AiMessageAdapter
import com.example.xiaomingassistant.ui.view.TopBarWithScrollView
import com.example.xiaomingassistant.util.calc.dp
import com.example.xiaomingassistant.util.dialog.showConfirmDialog
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch

class AiFragment : Fragment(R.layout.main_interface_ai) {

    private val viewModel: AiChatViewModel by viewModels()

    private lateinit var topBar: TopBarWithScrollView
    private lateinit var recyclerView: RecyclerView
    private lateinit var inputEdit: EditText
    private lateinit var sendButton: ImageView
    private lateinit var inputBar: MaterialCardView

    private lateinit var adapter: AiMessageAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        setupTopBar()
        setupRecyclerView()
        setupInput()
        setupWindowInsets(view)
        observeUi()
    }

    // 绑定 AI 对话页组件
    private fun bindViews(view: View) {
        topBar = view.findViewById(R.id.ai_content_container)
        recyclerView = view.findViewById(R.id.ai_message_list)
        inputEdit = view.findViewById(R.id.ai_input)
        sendButton = view.findViewById(R.id.ai_send_btn)
        inputBar = view.findViewById(R.id.input_bar)
    }

    // 顶栏提供清空当前会话入口
    private fun setupTopBar() {
        topBar.clearTopBarRightIcons()
        topBar.addTopBarRightIcon(R.drawable.garbage) {
            showClearConversationDialog()
        }
    }

    // 清空对话前弹出统一确认框
    private fun showClearConversationDialog() {
        requireContext().showConfirmDialog(
            title = "清空对话",
            message = "确定清空当前会话吗？清空后将删除当前上下文，且无法恢复。",
            positiveText = "清空"
        ) {
                viewModel.clearConversation()
            }
    }

    // 配置聊天消息列表
    private fun setupRecyclerView() {
        adapter = AiMessageAdapter(requireContext())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    // 发送按钮和回车键都复用同一套发送逻辑
    private fun setupInput() {
        sendButton.setOnClickListener {
            sendCurrentInput()
        }

        inputEdit.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                sendCurrentInput()
                true
            } else {
                false
            }
        }
    }

    // 根据键盘显隐动态调整输入栏底部间距
    private fun setupWindowInsets(rootView: View) {
        // 你手写底栏的固定高度：60dp
        val bottomBarHeight = 60.dp
        val spaceBetween = 16.dp

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { _, insets ->
            val imeBottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            val systemBottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())

            val lp = inputBar.layoutParams as ViewGroup.MarginLayoutParams

            lp.bottomMargin = if (imeVisible) {
                // 键盘弹出时，底栏那 60dp 不再保留
                (imeBottom - systemBottom).coerceAtLeast(0) + spaceBetween
            } else {
                // 键盘没弹出时，正常垫高 60dp，避开你手写的底栏
                bottomBarHeight + spaceBetween
            }

            inputBar.layoutParams = lp
            insets
        }
    }

    // 发送前先过滤空白输入
    private fun sendCurrentInput() {
        val text = inputEdit.text?.toString().orEmpty()
        if (text.isBlank()) return
        viewModel.sendMessage(text)
        inputEdit.setText("")
    }

    // 观察 ViewModel 消息流并自动滚动到底部
    private fun observeUi() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiMessages.collect { list ->
                adapter.submitList(list)
                recyclerView.post {
                    if (list.isNotEmpty()) {
                        recyclerView.scrollToPosition(list.lastIndex)
                    }
                }
            }
        }
    }
}
