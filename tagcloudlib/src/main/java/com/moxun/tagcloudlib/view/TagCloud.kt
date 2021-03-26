package com.moxun.tagcloudlib.view

import java.lang.Math.random
import java.util.*
import kotlin.math.*

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
class TagCloud @JvmOverloads constructor(
    val tagList: MutableList<Tag>,
    var radius: Int = DEFAULT_RADIUS,
    var tagColorLight: FloatArray = DEFAULT_COLOR_DARK,
    var tagColorDark: FloatArray = DEFAULT_COLOR_LIGHT
) {
    private var mSinX = 0f
    private var mCosX = 0f
    private var mSinY = 0f
    private var mCosY = 0f
    private var mSinZ = 0f
    private var mCosZ = 0f
    private val mInertiaZ = 0f
    private var mInertiaX = 0f
    private var mInertiaY = 0f
    private var mMinPopularity = 0
    private var mMaxPopularity = 0
    private var mRebuildOnUpdate = true
    private var maxDelta = 0f
    private var minDelta = 0f

    @JvmOverloads
    constructor(radius: Int = DEFAULT_RADIUS) : this(ArrayList<Tag>(), radius) {
    }

    fun create(rebuild: Boolean) {
        mRebuildOnUpdate = rebuild
        positionAll(mRebuildOnUpdate)
        calculatePopularity()
        recalculateAngle()
        updateAll()
    }

    fun clear() {
        tagList.clear()
    }

    operator fun get(position: Int): Tag {
        return tagList[position]
    }

    fun reset() {
        create(mRebuildOnUpdate)
    }

    fun update() {
        if (abs(mInertiaX) > 0.1f || abs(mInertiaY) > 0.1f) {
            recalculateAngle()
            updateAll()
        }
    }

    private fun initTag(tag: Tag) {
        val percentage = getPercentage(tag)
        val argb = getColorFromGradient(percentage)
        tag.setColorComponent(argb)
    }

    private fun getPercentage(tag: Tag): Float {
        val p = tag.popularity
        return if (mMinPopularity == mMaxPopularity) 1.0f else (p.toFloat() - mMinPopularity) / (mMaxPopularity.toFloat() - mMinPopularity)
    }

    fun add(newTag: Tag) {
        initTag(newTag)
        position(newTag)
        tagList.add(newTag)
        updateAll()
    }

    private fun position(newTag: Tag) {
        val phi = random() * PI
        val theta = random() * (2 * PI)
        newTag.spatialX = (radius * cos(theta) * sin(phi)).toFloat()
        newTag.spatialY = (radius * sin(theta) * sin(phi)).toFloat()
        newTag.spatialZ = (radius * cos(phi)).toFloat()
    }

    private fun positionAll(rebuild: Boolean) {
        var phi = 0.0
        var theta = 0.0
        val max = tagList.size //distribute: (disrtEven is used to specify whether distribute random or even
        for (i in 1 until max + 1) {
            if (rebuild) {
                phi = acos(-1.0 + (2.0 * i - 1.0) / max)
                theta = sqrt(max * PI) * phi
            } else {
                phi = random() * PI
                theta = random() * (2 * PI)
            }

            //coordinate conversion:
            tagList[i - 1].spatialX = (radius * cos(theta) * sin(phi)).toFloat()
            tagList[i - 1].spatialY = (radius * sin(theta) * sin(phi)).toFloat()
            tagList[i - 1].spatialZ = (radius * cos(phi)).toFloat()
        }
    }

    private fun updateAll() {
        //update transparency/scale for all tags:
        for (j in tagList.indices) {
            val tag = tagList[j]
            val x = tag.spatialX
            val y = tag.spatialY
            val z = tag.spatialZ

            //There exists two options for this part:
            // multiply positions by a x-rotation matrix
            val ry1 = y * mCosX + z * -mSinX
            val rz1 = y * mSinX + z * mCosX // multiply new positions by a y-rotation matrix
            val rx2 = x * mCosY + rz1 * mSinY
            val rz2 = x * -mSinY + rz1 * mCosY // multiply new positions by a z-rotation matrix
            val rx3 = rx2 * mCosZ + ry1 * -mSinZ
            val ry3 = rx2 * mSinZ + ry1 * mCosZ // set arrays to new positions
            tag.spatialX = rx3
            tag.spatialY = ry3
            tag.spatialZ = rz2

            // add perspective
            val diameter = 2 * radius
            val per = diameter / 1.0f / (diameter + rz2) // let's set position, scale, alpha for the tag;
            tag.flatX = rx3 * per
            tag.flatY = ry3 * per
            tag.scale = per

            // calculate alpha value
            val delta = diameter + rz2
            maxDelta = max(maxDelta, delta)
            minDelta = min(minDelta, delta)
            val alpha = (delta - minDelta) / (maxDelta - minDelta)
            tag.alpha = 1 - alpha
        }
        sortTagByScale()
    }

    private fun getColorFromGradient(percentage: Float): FloatArray {
        val rgba = FloatArray(4)
        rgba[0] = 1f
        rgba[1] = percentage * tagColorDark[0] + (1f - percentage) * tagColorLight[0]
        rgba[2] = percentage * tagColorDark[1] + (1f - percentage) * tagColorLight[1]
        rgba[3] = percentage * tagColorDark[2] + (1f - percentage) * tagColorLight[2]
        return rgba
    }

    private fun recalculateAngle() {
        val degToRad = PI / 180
        mSinX = sin(mInertiaX * degToRad).toFloat()
        mCosX = cos(mInertiaX * degToRad).toFloat()
        mSinY = sin(mInertiaY * degToRad).toFloat()
        mCosY = cos(mInertiaY * degToRad).toFloat()
        mSinZ = sin(mInertiaZ * degToRad).toFloat()
        mCosZ = cos(mInertiaZ * degToRad).toFloat()
    }

    fun setInertia(x: Float, y: Float) {
        mInertiaX = x
        mInertiaY = y
    }

    fun sortTagByScale() {
        tagList.sort()
    }

    private fun calculatePopularity() {
        for (i in tagList.indices) {
            val tag = tagList[i]
            val popularity = tag.popularity
            mMaxPopularity = max(mMaxPopularity, popularity)
            mMinPopularity = min(mMinPopularity, popularity)
        }
        for (tag in tagList) {
            initTag(tag)
        }
    }

    companion object {
        private const val DEFAULT_RADIUS = 3
        private val DEFAULT_COLOR_DARK = floatArrayOf(0.886f, 0.725f, 0.188f, 1f)
        private val DEFAULT_COLOR_LIGHT = floatArrayOf(0.3f, 0.3f, 0.3f, 1f)
    }
}