package com.example.xiaomingassistant.ui.fragment.main_activity

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.xiaomingassistant.R
import com.example.xiaomingassistant.data.ai.AiUiMessage
import com.example.xiaomingassistant.ui.util.MarkdownHelper
import com.google.android.material.card.MaterialCardView

class AiMessageAdapter(
    private val context: Context
) : RecyclerView.Adapter<AiMessageAdapter.MessageViewHolder>() {

    private val messages = mutableListOf<AiUiMessage>()

    fun submitList(newList: List<AiUiMessage>) {
        messages.clear()
        messages.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val container = LinearLayout(context).apply {
            layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(12)
            }
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(4), dp(2), dp(4), dp(2))
        }
        return MessageViewHolder(container)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size

    inner class MessageViewHolder(
        private val container: LinearLayout
    ) : RecyclerView.ViewHolder(container) {

        fun bind(item: AiUiMessage) {
            container.removeAllViews()

            if (item.role == "user") {
                bindUserMessage(item)
            } else {
                bindAiMessage(item)
            }
        }

        private fun bindUserMessage(item: AiUiMessage) {
            container.gravity = Gravity.END

            val card = MaterialCardView(context).apply {
                radius = dp(20).toFloat()
                strokeWidth = dp(1)
                strokeColor = Color.parseColor("#CFE7D7")
                setCardBackgroundColor(Color.parseColor("#EAF7F1"))
                cardElevation = 0f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginStart = dp(56)
                }
            }

            val textView = TextView(context).apply {
                text = item.content
                setTextColor(Color.parseColor("#1F1F1F"))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                setLineSpacing(dp(3).toFloat(), 1f)
                setPadding(dp(16), dp(14), dp(16), dp(14))
                maxWidth = (context.resources.displayMetrics.widthPixels * 0.68f).toInt()
            }

            card.addView(textView)
            container.addView(card)
        }

        private fun bindAiMessage(item: AiUiMessage) {
            container.gravity = Gravity.START

            val card = MaterialCardView(context).apply {
                radius = dp(22).toFloat()
                strokeWidth = dp(1)
                strokeColor = Color.parseColor("#D9E6DD")
                setCardBackgroundColor(Color.WHITE)
                cardElevation = 0f
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply {
                    marginEnd = dp(28)
                }
            }

            val textView = TextView(context).apply {
                setTextColor(Color.parseColor("#222222"))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                setLineSpacing(dp(4).toFloat(), 1.08f)
                setPadding(dp(18), dp(16), dp(18), dp(16))

                if (item.isLoading) {
                    setTypeface(typeface, Typeface.ITALIC)
                }

                isClickable = true
                isLongClickable = true
                linksClickable = true
            }

            MarkdownHelper.get(context).setMarkdown(textView, item.content)

            card.addView(textView)
            container.addView(card)
        }
    }

    private fun dp(value: Int): Int {
        return (value * context.resources.displayMetrics.density + 0.5f).toInt()
    }
}