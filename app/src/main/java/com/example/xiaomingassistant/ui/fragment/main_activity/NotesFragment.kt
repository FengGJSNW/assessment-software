package com.example.xiaomingassistant.ui.fragment.main_activity

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.xiaomingassistant.NotesCategoryManageActivity
import com.example.xiaomingassistant.NotesEditActivity
import com.example.xiaomingassistant.NotesTakingActivity
import com.example.xiaomingassistant.R
import com.example.xiaomingassistant.data.model.NoteCategory
import com.example.xiaomingassistant.data.model.NoteItem
import com.example.xiaomingassistant.data.repository.NotesRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class NotesFragment : Fragment(R.layout.main_interface_notes) {

    private lateinit var repository: NotesRepository
    private lateinit var notesDisplay: LinearLayout
    private lateinit var addNoteButton: MaterialButton
    private lateinit var manageCategoryButton: MaterialButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = NotesRepository(requireContext())

        notesDisplay = view.findViewById(R.id.notes_display)
        addNoteButton = view.findViewById(R.id.notes_btn_add_note)
        manageCategoryButton = view.findViewById(R.id.notes_manage_category)

        addNoteButton.setOnClickListener {
            startActivity(Intent(requireContext(), NotesEditActivity::class.java))
        }

        manageCategoryButton.setOnClickListener {
            startActivity(Intent(requireContext(), NotesCategoryManageActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        renderNotes()
    }

    private fun renderNotes() {
        notesDisplay.removeAllViews()

        val grouped = repository.getNotesGroupedByCategory()
        val hasAnyNote = grouped.any { it.second.isNotEmpty() }

        if (!hasAnyNote) {
            notesDisplay.addView(createEmptyCard("还没有笔记", "点击上方“添加笔记”开始记录"))
            return
        }

        grouped.forEach { (category, notes) ->
            if (notes.isNotEmpty()) {
                notesDisplay.addView(createCategoryCard(category, notes))
            }
        }
    }

    private fun createCategoryCard(category: NoteCategory, notes: List<NoteItem>): View {
        val context = requireContext()

        val outerCard = MaterialCardView(context).apply {
            radius = dp(24).toFloat()
            strokeWidth = dp(2)
            strokeColor = ContextCompat.getColor(context, R.color.card_darkgreen)
            cardElevation = 0f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(16)
            }
        }

        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(18))
        }

        val title = TextView(context).apply {
            text = category.name
            setTextColor(0xFF222222.toInt())
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setTypeface(typeface, Typeface.BOLD)
        }

        val subTitle = TextView(context).apply {
            text = "共 ${notes.size} 条笔记"
            setTextColor(0xFF666666.toInt())
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            setPadding(0, dp(4), 0, 0)
        }

        root.addView(title)
        root.addView(subTitle)

        notes.forEach { note ->
            root.addView(createNoteTitleCard(note))
        }

        outerCard.addView(root)
        return outerCard
    }

    private fun createNoteTitleCard(note: NoteItem): View {
        val context = requireContext()

        val card = MaterialCardView(context).apply {
            radius = dp(18).toFloat()
            strokeWidth = dp(2)
            strokeColor = 0xFFD8EBDD.toInt()
            cardElevation = 0f
            isClickable = true
            isFocusable = true
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(12)
            }
            setOnClickListener {
                val intent = Intent(requireContext(), NotesTakingActivity::class.java)
                intent.putExtra(NotesTakingActivity.EXTRA_NOTE_ID, note.id)
                startActivity(intent)
            }
        }

        val title = TextView(context).apply {
            text = note.title
            setTextColor(0xFF222222.toInt())
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            setTypeface(typeface, Typeface.BOLD)
            setPadding(dp(18), dp(18), dp(18), dp(18))
        }

        card.addView(title)
        return card
    }

    private fun createEmptyCard(titleText: String, message: String): View {
        val context = requireContext()

        val card = MaterialCardView(context).apply {
            radius = dp(24).toFloat()
            strokeWidth = dp(2)
            strokeColor = ContextCompat.getColor(context, R.color.card_darkgreen)
            cardElevation = 0f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(20), dp(20), dp(20))
        }

        val title = TextView(context).apply {
            text = titleText
            setTextColor(0xFF222222.toInt())
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setTypeface(typeface, Typeface.BOLD)
        }

        val sub = TextView(context).apply {
            text = message
            setTextColor(0xFF666666.toInt())
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setPadding(0, dp(8), 0, 0)
        }

        root.addView(title)
        root.addView(sub)
        card.addView(root)
        return card
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density + 0.5f).toInt()
    }
}