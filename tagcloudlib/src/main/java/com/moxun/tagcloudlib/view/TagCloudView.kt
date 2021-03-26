package com.moxun.tagcloudlib.view

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.*
import androidx.annotation.IntDef
import com.moxun.tagcloudlib.R
import com.moxun.tagcloudlib.view.TagsAdapter.OnDataSetChangeListener
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import kotlin.math.min

/**
 * Copyright © 2016 moxun
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the “Software”),
 * to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */
class TagCloudView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ViewGroup(context, attrs, defStyleAttr), Runnable, OnDataSetChangeListener {
    private var mSpeed = 2f
    private var mTagCloud: TagCloud
    private var mInertiaX = 0.5f
    private var mInertiaY = 0.5f
    private var mCenterX = 0f
    private var mCenterY = 0f
    private var mRadius = 0f
    private var mRadiusPercent = 0.9f
    private var mDarkColor = floatArrayOf(1f, 0f, 0f, 1f) //rgba
    private var mLightColor = floatArrayOf(0.9412f, 0.7686f, 0.2f, 1f) //rgba

    @IntDef(MODE_DISABLE, MODE_DECELERATE, MODE_UNIFORM)
    @Retention(RetentionPolicy.SOURCE)
    annotation class Mode

    private var manualScroll = false

    @get:Mode
    var autoScrollMode = 0
    private var mLayoutParams: MarginLayoutParams? = null
    private var mMinSize = 0
    private var mIsOnTouch = false
    private val mHandler = Handler(Looper.getMainLooper())
    private var mAdapter: TagsAdapter = NOPTagsAdapter()
    private var mOnTagClickListener: OnTagClickListener? = null

    init {
        isFocusableInTouchMode = true
        mTagCloud = TagCloud()
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TagCloudView)
            val m = typedArray.getString(R.styleable.TagCloudView_autoScrollMode)
            autoScrollMode = Integer.valueOf(m!!)
            setManualScroll(typedArray.getBoolean(R.styleable.TagCloudView_manualScroll, true))
            mInertiaX = typedArray.getFloat(R.styleable.TagCloudView_startAngleX, 0.5f)
            mInertiaY = typedArray.getFloat(R.styleable.TagCloudView_startAngleY, 0.5f)
            val light = typedArray.getColor(R.styleable.TagCloudView_lightColor, Color.WHITE)
            setLightColor(light)
            val dark = typedArray.getColor(R.styleable.TagCloudView_darkColor, Color.BLACK)
            setDarkColor(dark)
            val p = typedArray.getFloat(R.styleable.TagCloudView_radiusPercent, mRadiusPercent)
            setRadiusPercent(p)
            val s = typedArray.getFloat(R.styleable.TagCloudView_scrollSpeed, 2f)
            setScrollSpeed(s)
            typedArray.recycle()
        }
        val wm = getContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            wm.defaultDisplay.getSize(point)
        } else {
            point.x = wm.defaultDisplay.width
            point.y = wm.defaultDisplay.height
        }
        val screenWidth = point.x
        val screenHeight = point.y
        mMinSize = if (screenHeight < screenWidth) screenHeight else screenWidth
    }

    fun setAdapter(adapter: TagsAdapter) {
        mAdapter = adapter
        mAdapter.setOnDataSetChangeListener(this)
        onChange()
    }

    fun setManualScroll(manualScroll: Boolean) {
        this.manualScroll = manualScroll
    }

    fun setLightColor(color: Int) {
        val argb = FloatArray(4)
        argb[3] = Color.alpha(color) / 1.0f / 0xff
        argb[0] = Color.red(color) / 1.0f / 0xff
        argb[1] = Color.green(color) / 1.0f / 0xff
        argb[2] = Color.blue(color) / 1.0f / 0xff
        mLightColor = argb.clone()
        onChange()
    }

    fun setDarkColor(color: Int) {
        val argb = FloatArray(4)
        argb[3] = Color.alpha(color) / 1.0f / 0xff
        argb[0] = Color.red(color) / 1.0f / 0xff
        argb[1] = Color.green(color) / 1.0f / 0xff
        argb[2] = Color.blue(color) / 1.0f / 0xff
        mDarkColor = argb.clone()
        onChange()
    }

    fun setRadiusPercent(percent: Float) {
        require(!(percent > 1f || percent < 0f)) { "percent value not in range 0 to 1" }
        mRadiusPercent = percent
        onChange()
    }

    private fun initFromAdapter() {
        postDelayed({
            mCenterX = ((right - left) / 2).toFloat()
            mCenterY = ((bottom - top) / 2).toFloat()
            mRadius = min(mCenterX * mRadiusPercent, mCenterY * mRadiusPercent)
            mTagCloud.radius = mRadius.toInt()
            mTagCloud.tagColorLight = mLightColor //higher color
            mTagCloud.tagColorDark = mDarkColor //lower color
            mTagCloud.clear()
            removeAllViews()
            for (i in 0 until mAdapter.count) { //binding view to each tag
                val tag = Tag(mAdapter.getPopularity(i))
                val view = mAdapter.getView(context, i, this@TagCloudView) ?: continue
                tag.bindingView(view)
                mTagCloud.add(tag)
                addListener(view, i)
            }
            mTagCloud.setInertia(mInertiaX, mInertiaY)
            mTagCloud.create(true)
            resetChildren()
        }, 0)
    }

    private fun addListener(view: View, position: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            if (!view.hasOnClickListeners() && mOnTagClickListener != null) {
                view.setOnClickListener { v -> mOnTagClickListener!!.onItemClick(this@TagCloudView, v, position) }
            }
        } else {
            if (mOnTagClickListener != null) {
                view.setOnClickListener { v -> mOnTagClickListener!!.onItemClick(this@TagCloudView, v, position) }
                Log.e("TagCloudView", "Build version is less than 15, the OnClickListener may be overwritten.")
            }
        }
    }

    fun setScrollSpeed(scrollSpeed: Float) {
        mSpeed = scrollSpeed
    }

    private fun resetChildren() {
        removeAllViews() //必须保证getChildAt(i) == mTagCloud.getTagList().get(i)
        for (tag in mTagCloud.tagList) {
            if (tag.view != null) addView(tag.view)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val contentWidth = MeasureSpec.getSize(widthMeasureSpec)
        val contentHeight = MeasureSpec.getSize(heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        if (mLayoutParams == null) {
            mLayoutParams = layoutParams as MarginLayoutParams
        }
        val dimensionX =
            if (widthMode == MeasureSpec.EXACTLY) contentWidth else mMinSize - mLayoutParams!!.leftMargin - mLayoutParams!!.rightMargin
        val dimensionY =
            if (heightMode == MeasureSpec.EXACTLY) contentHeight else mMinSize - mLayoutParams!!.leftMargin - mLayoutParams!!.rightMargin
        setMeasuredDimension(dimensionX, dimensionY)
        measureChildren(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mHandler.post(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mHandler.removeCallbacksAndMessages(null)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val tag = mTagCloud[i]
            if (child != null && child.visibility != GONE) {
                mAdapter.onThemeColorChanged(child, tag.color, tag.alpha)
                child.scaleX = tag.scale
                child.scaleY = tag.scale
                val left = (mCenterX + tag.flatX).toInt() - child.measuredWidth / 2
                val top = (mCenterY + tag.flatY).toInt() - child.measuredHeight / 2
                child.layout(left, top, left + child.measuredWidth, top + child.measuredHeight)
            }
        }
    }

    fun reset() {
        mTagCloud.reset()
        resetChildren()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (manualScroll) {
            handleTouchEvent(ev)
        }
        return false
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (manualScroll) {
            handleTouchEvent(e)
        }
        return true
    }

    private var downX = 0f
    private var downY = 0f
    private fun handleTouchEvent(e: MotionEvent) {
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = e.x
                downY = e.y
                mIsOnTouch = true //rotate elements depending on how far the selection point is from center of cloud
                val dx = e.x - downX
                val dy = e.y - downY
                if (isValidMove(dx, dy)) {
                    mInertiaX = dy / mRadius * mSpeed * TOUCH_SCALE_FACTOR
                    mInertiaY = -dx / mRadius * mSpeed * TOUCH_SCALE_FACTOR
                    processTouch()
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = e.x - downX
                val dy = e.y - downY
                if (isValidMove(dx, dy)) {
                    mInertiaX = dy / mRadius * mSpeed * TOUCH_SCALE_FACTOR
                    mInertiaY = -dx / mRadius * mSpeed * TOUCH_SCALE_FACTOR
                    processTouch()
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> mIsOnTouch = false
        }
    }

    private fun isValidMove(dx: Float, dy: Float): Boolean {
        val minDistance = ViewConfiguration.get(context).scaledTouchSlop
        return Math.abs(dx) > minDistance || Math.abs(dy) > minDistance
    }

    private fun processTouch() {
        mTagCloud.setInertia(mInertiaX, mInertiaY)
        mTagCloud.update()
        resetChildren()
    }

    override fun onChange() {
        initFromAdapter()
    }

    override fun run() {
        if (!mIsOnTouch && autoScrollMode != MODE_DISABLE) {
            if (autoScrollMode == MODE_DECELERATE) {
                if (mInertiaX > 0.04f) {
                    mInertiaX -= 0.02f
                }
                if (mInertiaY > 0.04f) {
                    mInertiaY -= 0.02f
                }
                if (mInertiaX < -0.04f) {
                    mInertiaX += 0.02f
                }
                if (mInertiaY < -0.04f) {
                    mInertiaY += 0.02f
                }
            }
            processTouch()
        }
        mHandler.postDelayed(this, 16)
    }

    fun setOnTagClickListener(listener: OnTagClickListener?) {
        mOnTagClickListener = listener
    }

    interface OnTagClickListener {
        fun onItemClick(parent: ViewGroup?, view: View?, position: Int)
    }

    companion object {
        private const val TOUCH_SCALE_FACTOR = 0.8f
        const val MODE_DISABLE = 0
        const val MODE_DECELERATE = 1
        const val MODE_UNIFORM = 2
    }
}