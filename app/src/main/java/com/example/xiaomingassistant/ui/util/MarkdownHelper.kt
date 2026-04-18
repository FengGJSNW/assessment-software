package com.example.xiaomingassistant.ui.util

import android.content.Context
import io.noties.markwon.Markwon
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin

object MarkdownHelper {

    @Volatile
    private var instance: Markwon? = null

    fun get(context: Context): Markwon {
        return instance ?: synchronized(this) {
            instance ?: Markwon.builder(context.applicationContext)
                .usePlugin(StrikethroughPlugin.create())
                .usePlugin(TablePlugin.create(context))
                .usePlugin(TaskListPlugin.create(context))
                .build()
                .also { instance = it }
        }
    }
}