package com.o_bdreldin.slider

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getIntOrThrow

class Slider : ConstraintLayout {

    val ANCHOR_UP = 0
    val ANCHOR_BOTTOM = 1

    val DEFAULT_SENSITIVITY = 3

    var sensitivity = DEFAULT_SENSITIVITY

    private var foreground: ConstraintLayout? = null

    var onSlideListener: OnSlideListener? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initialize(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize(attrs)
    }

    private fun initialize(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.Slider)
        val foregroundColor = a.getColorOrThrow(R.styleable.Slider_foregroundColor)
        val anchor = a.getIntOrThrow(R.styleable.Slider_anchor)
        a.recycle()
        createForeground(foregroundColor, anchor)
        // TODO: prevent negative and above 0 & 100 values, draw borders & perform same action on "event"
    }

    var startPoint = -1f

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                startPoint = event.y
                return true
            }
            MotionEvent.ACTION_UP -> performClick()
            MotionEvent.ACTION_MOVE -> {
                var foregroundHeight = foreground?.height ?: 0
                if (event.y < startPoint) {
                    // this is up
                    if (foregroundHeight != height) {
                        foregroundHeight += (sensitivity * (resources.displayMetrics.densityDpi / 160f)).toInt()
                        if (foregroundHeight > height)
                            foregroundHeight = height
                    }
                } else {
                    // this is down
                    if (foregroundHeight != 0) {
                        foregroundHeight -= (sensitivity * (resources.displayMetrics.densityDpi / 160f)).toInt()
                        if (foregroundHeight < 0)
                            foregroundHeight = 0
                    }
                }
                foreground?.let {
                    val params = it.layoutParams as LayoutParams
//                    params.height = height - event.y.toInt()
                    params.height = foregroundHeight
                    it.layoutParams = params
                }
//                if (foregroundHeight != height && foregroundHeight != 0)
                onSlideListener?.onSlide(foregroundHeight / height.toFloat())
            }
        }
        return super.onTouchEvent(event)
    }

    private fun createForeground(foregroundColor: Int, anchor: Int) {
        val set = ConstraintSet()
        foreground = ConstraintLayout(context)
        foreground?.let {
            it.id = View.generateViewId()
            it.setBackgroundColor(foregroundColor)
            it.isClickable = false
            addView(foreground, 0)
            set.clone(this)
            when (anchor) {
                ANCHOR_BOTTOM -> set.connect(it.id, ConstraintSet.BOTTOM, id, ConstraintSet.BOTTOM)
            }
            set.connect(it.id, ConstraintSet.START, id, ConstraintSet.START)
            set.connect(it.id, ConstraintSet.END, id, ConstraintSet.END)
            set.constrainHeight(it.id, ConstraintSet.MATCH_CONSTRAINT)
            set.constrainWidth(it.id, ConstraintSet.MATCH_CONSTRAINT)
            set.applyTo(this)
        }
    }

    interface OnSlideListener {
        fun onSlide(progress: Float)
    }
}