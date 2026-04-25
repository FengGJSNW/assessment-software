package com.example.xiaomingassistant

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.example.xiaomingassistant.data.model.NoteCategory
import com.example.xiaomingassistant.data.repository.NotesRepository
import com.example.xiaomingassistant.data.session.SessionManager
import com.example.xiaomingassistant.ui.view.TopBarWithScrollView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.example.xiaomingassistant.util.calc.dp
import com.example.xiaomingassistant.util.dialog.DialogBuilder
import com.example.xiaomingassistant.util.dialog.setInputView
import com.example.xiaomingassistant.util.dialog.showConfirmDialog
import com.example.xiaomingassistant.util.toast.showShortToast
import com.example.xiaomingassistant.util.dialog.style.applyRoundedStyle

class NotesCategoryManageActivity : BaseActivity() {

    private lateinit var repository: NotesRepository

    private lateinit var topBar: TopBarWithScrollView
    private lateinit var inputEdit: TextInputEditText
    private lateinit var addButtonContainer: FrameLayout
    private lateinit var listContainer: LinearLayout

    private lateinit var localSessionManager: SessionManager
    private var userId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes_manage_classify)

        repository = NotesRepository(this)
        localSessionManager = SessionManager(this)
        userId = localSessionManager.getUserId()

        bindViews()
        setupTopBar()
        setupAction()
        renderCategoryList()
    }

    override fun onResume() {
        super.onResume()
        renderCategoryList()
    }

    // 绑定分类管理页组件
    private fun bindViews() {
        topBar = findViewById(R.id.activity_notes_classify_topbar)
        inputEdit = findViewById(R.id.activity_notes_classify_input_edit)
        addButtonContainer = findViewById(R.id.activity_notes_classify_add_btn_container)
        listContainer = findViewById(R.id.activity_notes_classify_list_container)
    }

    // 配置顶部返回和新增分类入口
    private fun setupTopBar() {
        topBar.clearTopBarLeftIcons()
        topBar.clearTopBarRightIcons()

        topBar.addTopBarLeftIcon(R.drawable.exit) {
            finish()
        }

        topBar.addTopBarRightIcon(R.drawable.plus) {
            addCategoryFromInput()
        }
    }

    // 页面中的添加按钮与顶部按钮共用同一逻辑
    private fun setupAction() {
        addButtonContainer.setOnClickListener {
            addCategoryFromInput()
        }
    }

    // 从输入框读取分类名并执行新增
    private fun addCategoryFromInput() {
        val name = inputEdit.text?.toString()?.trim().orEmpty()
        if (name.isBlank()) {
            showShortToast("分类名称不能为空")
            return
        }

        val success = repository.addCategory(userId, name)
        if (success) {
            inputEdit.setText("")
            renderCategoryList()
            showShortToast("分类添加成功")
        } else {
            showShortToast("添加失败，可能是分类重复")
        }
    }

    // 重新绘制当前用户的分类列表
    private fun renderCategoryList() {
        listContainer.removeAllViews()
        val categories = repository.getAllCategories(userId)

        if (categories.isEmpty()) {
            listContainer.addView(createHintText("暂无分类"))
            return
        }

        categories.forEach { category ->
            listContainer.addView(createCategoryItemView(category))
        }
    }

    // 生成单条分类卡片，附带重命名和删除操作
    private fun createCategoryItemView(category: NoteCategory): View {
        val card = MaterialCardView(this).apply {
            radius = 16.dp.toFloat()
            strokeWidth = 2.dp
            strokeColor = 0xFFD8EBDD.toInt()
            cardElevation = 0f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 12.dp
            }
        }

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(16.dp, 16.dp, 16.dp, 16.dp)
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
            setPadding(10.dp, 6.dp, 10.dp, 6.dp)
            setOnClickListener {
                showRenameDialog(category)
            }
        }

        val delete = TextView(this).apply {
            text = "删除"
            setTextColor(0xFF4F7F62.toInt())
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setPadding(10.dp, 6.dp, 10.dp, 6.dp)
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

    // 弹出重命名输入框并提交修改
    private fun showRenameDialog(category: NoteCategory) {
        lateinit var editView: TextInputEditText

        val dialog = DialogBuilder {
            setTitle("重命名分类")
            editView = setInputView(category.name)
            setPositiveButton("确定") { _, _ ->
                val newName = editView.text?.toString()?.trim().orEmpty()
                // 重命名分类
                val success = repository.renameCategory(userId, category.id, newName)
                if (success) {
                    renderCategoryList()
                    showShortToast("修改成功")
                } else {
                    showShortToast("修改失败，名称可能重复或为空")
                }
            }
            setNegativeButton("取消", null)
            create()
        }

        dialog.applyRoundedStyle()
    }

    // 删除分类前先提示分类下仍有笔记时无法删除
    private fun showDeleteDialog(category: NoteCategory) {
        showConfirmDialog(
            title = "删除分类",
            message = "确定删除“${category.name}”吗？\n若该分类下仍有笔记，将无法删除。",
            positiveText = "删除"
        ) {
                val success = repository.deleteCategory(userId, category.id)
                if (success) {
                    renderCategoryList()
                    showShortToast("删除成功")
                } else {
                    showShortToast("删除失败：该分类下还有笔记")
                }
            }
    }

    // 分类为空时显示一条轻提示文案
    private fun createHintText(message: String): View {
        return TextView(this).apply {
            text = message
            setTextColor(0xFF666666.toInt())
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
            setPadding(0, 8.dp, 0, 8.dp)
        }
    }
}
