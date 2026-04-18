package com.example.xiaomingassistant.ui.adapter

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
import com.google.android.material.card.MaterialCardView

class AiMessageAdapter : RecyclerView.Adapter<AiMessageAdapter.MessageViewHolder>() {

    private val items = mutableListOf<AiUiMessage>()

    fun submitList(newList: List<AiUiMessage>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val context = parent.context

        val root = LinearLayout(context).apply {
            layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(context, 12)
            }
            orientation = LinearLayout.HORIZONTAL
        }

        return MessageViewHolder(root)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class MessageViewHolder(
        private val root: LinearLayout
    ) : RecyclerView.ViewHolder(root) {

        fun bind(item: AiUiMessage) {
            val context = root.context
            root.removeAllViews()

            if (item.role == "user") {
                root.gravity = Gravity.END

                val card = MaterialCardView(context).apply {
                    radius = dp(context, 20).toFloat()
                    strokeWidth = dp(context, 2)
                    strokeColor = ContextCompat.getColor(context, R.color.card_darkgreen)
                    setCardBackgroundColor(Color.parseColor("#EAF7F1"))
                }

                val textView = TextView(context).apply {
                    text = item.content
                    setTextColor(Color.parseColor("#222222"))
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                    setPadding(
                        dp(context, 16),
                        dp(context, 14),
                        dp(context, 16),
                        dp(context, 14)
                    )
                    maxWidth = dp(context, 260)
                }

                card.addView(textView)
                root.addView(card)

            } else {
                root.gravity = Gravity.START

                val icon = ImageView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        dp(context, 34),
                        dp(context, 34)
                    ).apply {
                        topMargin = dp(context, 4)
                    }
                    setImageResource(R.drawable.icon_for_none)
                    imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(context, R.color.card_darkgreen)
                    )
                }

                val card = MaterialCardView(context).apply {
                    radius = dp(context, 20).toFloat()
                    strokeWidth = dp(context, 2)
                    strokeColor = Color.parseColor("#D8EBDD")
                    setCardBackgroundColor(Color.WHITE)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        leftMargin = dp(context, 10)
                    }
                }

                val textView = TextView(context).apply {
                    text = item.content
                    setTextColor(Color.parseColor("#222222"))
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                    setPadding(
                        dp(context, 16),
                        dp(context, 14),
                        dp(context, 16),
                        dp(context, 14)
                    )
                    maxWidth = dp(context, 260)

                    if (item.isLoading) {
                        setTypeface(typeface, Typeface.ITALIC)
                    }
                }

                card.addView(textView)
                root.addView(icon)
                root.addView(card)
            }
        }
    }

    private fun dp(context: Context, value: Int): Int {
        return (value * context.resources.displayMetrics.density + 0.5f).toInt()
    }
}