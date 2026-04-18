package com.example.xiaomingassistant

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.xiaomingassistant.data.model.NoteCategory
import com.example.xiaomingassistant.data.repository.NotesRepository
import com.example.xiaomingassistant.data.session.SessionManager
import com.example.xiaomingassistant.ui.view.TopBarWithScrollView
import com.google.android.material.textfield.TextInputEditText

class NotesEditActivity : BaseActivity() {

    private lateinit var repository: NotesRepository
    private lateinit var topBar: TopBarWithScrollView

    private lateinit var categoryAutoComplete: AutoCompleteTextView
    private lateinit var titleEdit: TextInputEditText
    private lateinit var contentEdit: TextInputEditText

    private lateinit var localSessionManager: SessionManager
    private var userId: Long = -1L

    private var categories: List<NoteCategory> = emptyList()
    private var selectedCategoryId: Long = -1L
    private var editingNoteId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes_add_notes)

        repository = NotesRepository(this)
        localSessionManager = SessionManager(this)
        userId = localSessionManager.getUserId()

        topBar = findViewById(R.id.activity_notes_adding_topbar)
        categoryAutoComplete = findViewById(R.id.activity_notes_adding_category_auto_complete)
        titleEdit = findViewById(R.id.activity_notes_adding_title_edit)
        contentEdit = findViewById(R.id.activity_notes_adding_note_edit)

        editingNoteId = intent.getLongExtra(EXTRA_NOTE_ID, -1L)

        loadCategories()
        loadEditDataIfNeeded()
        setupTopBar()
    }

    private fun setupTopBar() {
        topBar.clearTopBarLeftIcons()
        topBar.clearTopBarRightIcons()

        topBar.setTitle(if (editingNoteId > 0) "编辑笔记" else "添加笔记")

        topBar.addTopBarLeftIcon(R.drawable.exit) {
            finish()
        }

        topBar.addTopBarRightIcon(R.drawable.save) {
            saveNote()
        }

        if (editingNoteId > 0) {
            topBar.addTopBarRightIcon(R.drawable.delete) {
                showDeleteDialog()
            }
        }
    }

    private fun loadCategories() {
        categories = repository.getAllCategories(userId)

        if (categories.isEmpty()) {
            repository.addCategory(userId, "未分类")
            categories = repository.getAllCategories(userId)
        }

        val names = categories.map { it.name }
        categoryAutoComplete.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_list_item_1, names)
        )

        if (categories.isNotEmpty()) {
            selectedCategoryId = categories.first().id
            categoryAutoComplete.setText(categories.first().name, false)
        }

        categoryAutoComplete.setOnItemClickListener { _, _, position, _ ->
            selectedCategoryId = categories[position].id
        }
    }

    private fun loadEditDataIfNeeded() {
        if (editingNoteId <= 0) return

        val note = repository.getNoteById(userId, editingNoteId) ?: return
        titleEdit.setText(note.title)
        contentEdit.setText(note.content)
        selectedCategoryId = note.categoryId
        categoryAutoComplete.setText(note.categoryName, false)
    }

    private fun saveNote() {
        val title = titleEdit.text?.toString()?.trim().orEmpty()
        val content = contentEdit.text?.toString()?.trim().orEmpty()
        val categoryName = categoryAutoComplete.text?.toString()?.trim().orEmpty()

        if (title.isBlank()) {
            Toast.makeText(this, "标题不能为空", Toast.LENGTH_SHORT).show()
            return
        }

        if (content.isBlank()) {
            Toast.makeText(this, "正文不能为空", Toast.LENGTH_SHORT).show()
            return
        }

        val matchedCategory = categories.find { it.name == categoryName }
        if (matchedCategory == null) {
            Toast.makeText(this, "请先选择有效分类", Toast.LENGTH_SHORT).show()
            return
        }
        selectedCategoryId = matchedCategory.id

        val success = if (editingNoteId > 0) {
            repository.updateNote(userId, editingNoteId, selectedCategoryId, title, content)
        } else {
            repository.insertNote(userId, selectedCategoryId, title, content) > 0
        }

        if (success) {
            Toast.makeText(this, if (editingNoteId > 0) "修改成功" else "添加成功", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDeleteDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("删除笔记")
            .setMessage("确定删除这条笔记吗？删除后无法恢复。")
            .setPositiveButton("删除") { _, _ ->
                val success = repository.deleteNote(userId, editingNoteId)
                if (success) {
                    Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .create()

        styleDialog(dialog)
    }

    companion object {
        const val EXTRA_NOTE_ID = "extra_note_id"
    }
}