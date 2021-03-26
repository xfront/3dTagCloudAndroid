package com.moxun.tagcloud

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.moxun.tagcloudlib.view.TagsAdapter

/**
 * Created by moxun on 16/3/4.
 */
class ViewTagsAdapter : TagsAdapter() {
    override val count: Int = 20

    override fun getView(context: Context, position: Int, parent: ViewGroup): View? {
        return LayoutInflater.from(context)
                .inflate(R.layout.tag_item_view, parent, false)
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getPopularity(position: Int): Int {
        return position % 5
    }

    override fun onThemeColorChanged(view: View, themeColor: Int, alpha: Float) {
        view.findViewById<View>(R.id.android_eye)
                .setBackgroundColor(themeColor)
        val color = Color.argb(((1 - alpha) * 255).toInt(), 255, 255, 255)
        (view.findViewById<View>(R.id.iv) as ImageView).setColorFilter(color)
    }
}