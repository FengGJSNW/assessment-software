package com.example.xiaomingassistant

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.xiaomingassistant.data.repository.NotesRepository
import com.example.xiaomingassistant.ui.view.TopBarWithScrollView

class NotesTakingActivity : BaseActivity() {

    private lateinit var repository: NotesRepository
    private lateinit var topBar: TopBarWithScrollView

    private lateinit var titleText: TextView
    private lateinit var categoryText: TextView
    private lateinit var contentText: TextView

    private var noteId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes_taking)

        repository = NotesRepository(this)

        topBar = findViewById(R.id.activity_notes_taking_topbar)
        titleText = findViewById(R.id.activity_notes_taking_title)
        categoryText = findViewById(R.id.activity_notes_taking_category)
        contentText = findViewById(R.id.activity_notes_taking_content)

        noteId = intent.getLongExtra(EXTRA_NOTE_ID, -1L)
        if (noteId <= 0) {
            Toast.makeText(this, "笔记不存在", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupTopBar()
        loadNote()
    }

    override fun onResume() {
        super.onResume()
        loadNote()
    }

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

    private fun loadNote() {
        val note = repository.getNoteById(noteId)
        if (note == null) {
            Toast.makeText(this, "笔记不存在", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        topBar.setTitle(note.title)
        titleText.text = note.title
        categoryText.text = "分类：${note.categoryName}"
        contentText.text = note.content
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(this)
            .setTitle("删除笔记")
            .setMessage("确定删除这条笔记吗？删除后无法恢复。")
            .setPositiveButton("删除") { _, _ ->
                val success = repository.deleteNote(noteId)
                if (success) {
                    Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    companion object {
        const val EXTRA_NOTE_ID = "extra_note_id"
    }
}