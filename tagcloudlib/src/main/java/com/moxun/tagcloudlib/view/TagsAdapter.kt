package com.moxun.tagcloudlib.view

import android.content.Context
import android.view.View
import android.view.ViewGroup

/**
 * Copyright © 2016 moxun
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the “Software”),
 * to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */
abstract class TagsAdapter {
    private var onDataSetChangeListener: OnDataSetChangeListener? = null

    abstract val count: Int
    abstract fun getView(context: Context, position: Int, parent: ViewGroup): View?
    abstract fun getItem(position: Int): Any?
    abstract fun getPopularity(position: Int): Int
    abstract fun onThemeColorChanged(view: View, themeColor: Int, alpha: Float)

    fun notifyDataSetChanged() {
        onDataSetChangeListener?.onChange()
    }

    fun interface OnDataSetChangeListener {
        fun onChange()
    }

    fun setOnDataSetChangeListener(listener: OnDataSetChangeListener?) {
        onDataSetChangeListener = listener
    }
}