package com.moxun.tagcloudlib.view

import android.graphics.Color
import android.graphics.PointF
import android.view.View
import com.moxun.tagcloudlib.view.graphics.Point3DF

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
class Tag @JvmOverloads constructor(
    x: Float = 0f, y: Float = 0f, z: Float = 0f, var scale: Float = 1.0f, val popularity: Int = DEFAULT_POPULARITY
) : Comparable<Tag> {
    private val mColor: FloatArray = floatArrayOf(1.0f, 0.5f, 0.5f, 0.5f)
    var view: View? = null
        private set
    private val mFlatCenter: PointF = PointF(0f, 0f)
    private val mSpatialCenter: Point3DF = Point3DF(x, y, z)

    constructor(popularity: Int) : this(0f, 0f, 0f, 1.0f, popularity) {}

    var spatialX: Float
        get() = mSpatialCenter.x
        set(x) {
            mSpatialCenter.x = x
        }
    var spatialY: Float
        get() = mSpatialCenter.y
        set(y) {
            mSpatialCenter.y = y
        }
    var spatialZ: Float
        get() = mSpatialCenter.z
        set(z) {
            mSpatialCenter.z = z
        }

    fun bindingView(view: View?) {
        this.view = view
    }

    var flatX: Float
        get() = mFlatCenter.x
        set(x) {
            mFlatCenter.x = x
        }

    var flatY: Float
        get() = mFlatCenter.y
        set(y) {
            mFlatCenter.y = y
        }

    fun setColorComponent(rgb: FloatArray) {
        System.arraycopy(rgb, 0, mColor, mColor.size - rgb.size, rgb.size)
    }

    var alpha: Float
        get() = mColor[0]
        set(alpha) {
            mColor[0] = alpha
        }

    val color: Int
        get() {
            val result = IntArray(4)
            for (i in 0..3) {
                result[i] = (mColor[i] * 0xff).toInt()
            }
            return Color.argb(result[0], result[1], result[2], result[3])
        }

    override fun compareTo(another: Tag): Int {
        return if (scale > another.scale) 1 else -1
    }

    companion object {
        private const val DEFAULT_POPULARITY = 5
    }
}