package com.example.xiaomingassistant.ui.fragment.main_activity

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.xiaomingassistant.R
import com.example.xiaomingassistant.ui.adapter.AiMessageAdapter
import com.example.xiaomingassistant.ui.view.TopBarWithScrollView
import kotlinx.coroutines.launch

class AiFragment : Fragment(R.layout.main_interface_ai) {

    private val viewModel: AiChatViewModel by viewModels()

    private lateinit var topBar: TopBarWithScrollView
    private lateinit var recyclerView: RecyclerView
    private lateinit var inputEdit: EditText
    private lateinit var sendButton: ImageView

    private lateinit var adapter: AiMessageAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        topBar = view.findViewById(R.id.ai_content_container)
        recyclerView = view.findViewById(R.id.ai_message_list)
        inputEdit = view.findViewById(R.id.ai_input)
        sendButton = view.findViewById(R.id.ai_send_btn)

        setupTopBar()
        setupRecyclerView()
        setupInput()
        observeUi()
    }

    private fun setupTopBar() {
        topBar.clearTopBarRightIcons()
        topBar.addTopBarRightIcon(R.drawable.icon_for_none) {
            viewModel.clearConversation()
        }
    }

    private fun setupRecyclerView() {
        adapter = AiMessageAdapter()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

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

    private fun sendCurrentInput() {
        val text = inputEdit.text?.toString().orEmpty()
        if (text.isBlank()) return
        viewModel.sendMessage(text)
        inputEdit.setText("")
    }

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