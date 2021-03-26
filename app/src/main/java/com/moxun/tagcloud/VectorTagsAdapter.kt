package com.moxun.tagcloud

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.moxun.tagcloudlib.view.TagsAdapter

/**
 * Created by moxun on 16/3/11.
 */
class VectorTagsAdapter : TagsAdapter() {
    override val count: Int = 20

    override fun getView(context: Context, position: Int, parent: ViewGroup): View {
        return LayoutInflater.from(context)
                .inflate(R.layout.tag_item_vector, parent, false)
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getPopularity(position: Int): Int {
        Log.e("", "Popularity" + position % 5)
        return position % 5
    }

    override fun onThemeColorChanged(view: View, themeColor: Int, alpha: Float) {
        val imageView = view.findViewById<View>(R.id.vector_img) as ImageView
        val porterDuffColorFilter = PorterDuffColorFilter(themeColor, PorterDuff.Mode.SRC_ATOP)
        if (imageView == null) {
            return
        }
        imageView.drawable.colorFilter = porterDuffColorFilter
    }
}