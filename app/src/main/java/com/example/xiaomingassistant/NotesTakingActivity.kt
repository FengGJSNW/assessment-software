package com.example.xiaomingassistant

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.example.xiaomingassistant.data.repository.NotesRepository
import com.example.xiaomingassistant.data.session.SessionManager
import com.example.xiaomingassistant.ui.view.TopBarWithScrollView
import com.example.xiaomingassistant.util.dialog.showConfirmDialog
import com.example.xiaomingassistant.util.toast.showShortToast

class NotesTakingActivity : BaseActivity() {

    private lateinit var repository: NotesRepository
    private lateinit var topBar: TopBarWithScrollView

    private lateinit var titleText: TextView
    private lateinit var categoryText: TextView
    private lateinit var contentText: TextView

    private lateinit var localSessionManager: SessionManager
    private var userId: Long = -1L
    private var noteId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes_taking)

        repository = NotesRepository(this)
        localSessionManager = SessionManager(this)
        userId = localSessionManager.getUserId()

        bindViews()
        noteId = intent.getLongExtra(EXTRA_NOTE_ID, -1L)
        if (noteId <= 0) {
            showShortToast("笔记不存在")
            finish()
            return
        }

        setupTopBar()
        loadNote()
    }

    // 绑定笔记详情页组件
    private fun bindViews() {
        topBar = findViewById(R.id.activity_notes_taking_topbar)
        titleText = findViewById(R.id.activity_notes_taking_title)
        categoryText = findViewById(R.id.activity_notes_taking_category)
        contentText = findViewById(R.id.activity_notes_taking_content)
    }

    override fun onResume() {
        super.onResume()
        loadNote()
    }

    // 顶栏提供返回、编辑和删除操作
    private fun setupTopBar() {
        topBar.clearTopBarLeftIcons()
        topBar.clearTopBarRightIcons()

        topBar.addTopBarLeftIcon(R.drawable.exit) {
            finish()
        }

        topBar.addTopBarRightIcon(R.drawable.edit) {
            val intent = Intent(this, NotesEditActivity::class.java)
            intent.putExtra(NotesEditActivity.EXTRA_NOTE_ID, noteId)
            startActivity(intent)
        }

        topBar.addTopBarRightIcon(R.drawable.delete) {
            showDeleteDialog()
        }
    }

    // 根据当前 noteId 重新加载最新内容
    private fun loadNote() {
        val note = repository.getNoteById(userId, noteId)
        if (note == null) {
            showShortToast("笔记不存在")
            finish()
            return
        }

        topBar.setTitle(note.title)
        titleText.text = note.title
        categoryText.text = "分类：${note.categoryName}"
        contentText.text = note.content
    }

    // 删除前弹出统一样式确认框
    private fun showDeleteDialog() {
        showConfirmDialog(
            title = "删除笔记",
            message = "确定删除这条笔记吗？删除后无法恢复。",
            positiveText = "删除"
        ) {
                val success = repository.deleteNote(userId, noteId)
                if (success) {
                    showShortToast("删除成功")
                    finish()
                } else {
                    showShortToast("删除失败")
                }
            }
    }

    companion object {
        const val EXTRA_NOTE_ID = "extra_note_id"
    }
}
