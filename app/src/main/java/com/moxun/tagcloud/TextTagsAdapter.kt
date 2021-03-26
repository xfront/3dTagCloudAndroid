package com.moxun.tagcloud

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.moxun.tagcloudlib.view.TagsAdapter

/**
 * Created by moxun on 16/1/19.
 */
class TextTagsAdapter(vararg data: String?) : TagsAdapter() {
    private val dataSet = data.toList()

    override val count = dataSet.size

    override fun getView(context: Context, position: Int, parent: ViewGroup): View? {
        val tv = TextView(context)
        tv.text = "No.$position"
        tv.gravity = Gravity.CENTER
        tv.setOnClickListener {
            Log.e("Click", "Tag $position clicked.")
            Toast.makeText(context, "Tag $position clicked", Toast.LENGTH_SHORT)
                    .show()
        }
        tv.setTextColor(Color.WHITE)
        return tv
    }

    override fun getItem(position: Int): Any? {
        return dataSet[position]
    }

    override fun getPopularity(position: Int): Int {
        return position % 7
    }

    override fun onThemeColorChanged(view: View, themeColor: Int, alpha: Float) {
        view.setBackgroundColor(themeColor)
    }

}