package com.moxun.tagcloudlib.view

import android.content.Context
import android.view.View
import android.view.ViewGroup

/**
 * Default adapter and do nothing
 * Created by moxun on 16/3/25.
 *//*package*/
internal class NOPTagsAdapter : TagsAdapter() {
    override val count: Int = 0

    override fun getView(context: Context, position: Int, parent: ViewGroup): View? {
        return null
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getPopularity(position: Int): Int {
        return 0
    }

    override fun onThemeColorChanged(view: View, themeColor: Int, alpha: Float) {}
}