package com.example.xiaomingassistant

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.xiaomingassistant.ui.view.TopBarWithScrollView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.example.xiaomingassistant.data.repository.NotesRepository
import com.example.xiaomingassistant.data.model.NoteCategory

class NotesCategoryManageActivity : BaseActivity() {

    private lateinit var repository: NotesRepository

    private lateinit var topBar: TopBarWithScrollView
    private lateinit var inputEdit: TextInputEditText
    private lateinit var addButtonContainer: FrameLayout
    private lateinit var listContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes_manage_classify)

        repository = NotesRepository(this)

        topBar = findViewById(R.id.activity_notes_classify_topbar)
        inputEdit = findViewById(R.id.activity_notes_classify_input_edit)
        addButtonContainer = findViewById(R.id.activity_notes_classify_add_btn_container)
        listContainer = findViewById(R.id.activity_notes_classify_list_container)

        setupTopBar()
        setupAction()
        renderCategoryList()
    }

    override fun onResume() {
        super.onResume()
        renderCategoryList()
    }

    private fun setupTopBar() {
        topBar.clearTopBarLeftIcons()
        topBar.clearTopBarRightIcons()

        topBar.addTopBarLeftIcon(R.drawable.exit) {
            finish()
        }
    }

    private fun setupAction() {
        addButtonContainer.setOnClickListener {
            addCategoryFromInput()
        }
    }

    private fun addCategoryFromInput() {
        val name = inputEdit.text?.toString()?.trim().orEmpty()
        if (name.isBlank()) {
            Toast.makeText(this, "分类名称不能为空", Toast.LENGTH_SHORT).show()
            return
        }

        val success = repository.addCategory(name)
        if (success) {
            inputEdit.setText("")
            renderCategoryList()
            Toast.makeText(this, "分类添加成功", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "添加失败，可能是分类重复", Toast.LENGTH_SHORT).show()
        }
    }

    private fun renderCategoryList() {
        listContainer.removeAllViews()
        val categories = repository.getAllCategories()

        if (categories.isEmpty()) {
            listContainer.addView(createHintText("暂无分类"))
            return
        }

        categories.forEach { category ->
            listContainer.addView(createCategoryItemView(category))
        }
    }

    private fun createCategoryItemView(category: NoteCategory): View {
        val card = MaterialCardView(this).apply {
            radius = dp(16).toFloat()
            strokeWidth = dp(2)
            strokeColor = 0xFFD8EBDD.toInt()
            cardElevation = 0f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(12)
            }
        }

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }

        val title = TextView(this).apply {
            text = category.name
            setTextColor(0xFF222222.toInt())
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val rename = TextView(this).apply {
            text = "重命名"
            setTextColor(0xFF4F7F62.toInt())
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setPadding(dp(10), dp(6), dp(10), dp(6))
            setOnClickListener {
                showRenameDialog(category)
            }
        }

        val delete = TextView(this).apply {
            text = "删除"
            setTextColor(0xFF4F7F62.toInt())
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setPadding(dp(10), dp(6), dp(10), dp(6))
            setOnClickListener {
                showDeleteDialog(category)
            }
        }

        row.addView(title)
        row.addView(rename)
        row.addView(delete)
        card.addView(row)
        return card
    }

    private fun showRenameDialog(category: NoteCategory) {
        val edit = TextInputEditText(this).apply {
            setText(category.name)
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }

        AlertDialog.Builder(this)
            .setTitle("重命名分类")
            .setView(edit)
            .setPositiveButton("确定") { _, _ ->
                val newName = edit.text?.toString()?.trim().orEmpty()
                val success = repository.renameCategory(category.id, newName)
                if (success) {
                    renderCategoryList()
                    Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "修改失败，名称可能重复或为空", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showDeleteDialog(category: NoteCategory) {
        AlertDialog.Builder(this)
            .setTitle("删除分类")
            .setMessage("确定删除“${category.name}”吗？\n若该分类下仍有笔记，将无法删除。")
            .setPositiveButton("删除") { _, _ ->
                val success = repository.deleteCategory(category.id)
                if (success) {
                    renderCategoryList()
                    Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "删除失败：该分类下还有笔记", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun createHintText(message: String): View {
        return TextView(this).apply {
            text = message
            setTextColor(0xFF666666.toInt())
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
            setPadding(0, dp(8), 0, dp(8))
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density + 0.5f).toInt()
    }
}