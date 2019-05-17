package com.shuhart.stepview

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.Dimension
import android.support.annotation.IntDef
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.graphics.ColorUtils
import android.support.v4.view.ViewCompat
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.shuhart.stepview.animation.AnimatorListener
import java.util.*

@Suppress("unused")
class StepView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = R.attr.sv_stepViewStyle) : View(context, attrs, defStyleAttr) {

    private var onStepClickListener: OnStepClickListener? = null
    @DisplayMode
    private var displayMode = DISPLAY_MODE_WITH_TEXT
    private val steps = ArrayList<String>()
    // for display mode DISPLAY_MODE_NO_TEXT
    private var stepsNumber = 0
    var currentStep = START_STEP
        private set
    private var nextAnimatedStep: Int = 0
    private var state = IDLE

    @AnimationType
    private var animationType: Int = 0
    @ColorInt
    private var selectedCircleColor: Int = 0
    @Dimension
    private var selectedCircleRadius: Int = 0
    @ColorInt
    private var selectedTextColor: Int = 0
    @ColorInt
    private var doneCircleColor: Int = 0
    @Dimension
    private var doneCircleRadius: Int = 0
    @ColorInt
    private var doneTextColor: Int = 0
    @ColorInt
    private var nextTextColor: Int = 0
    @Dimension
    private var stepPadding: Int = 0
    @ColorInt
    private var nextStepLineColor: Int = 0
    @ColorInt
    private var doneStepLineColor: Int = 0
    @Dimension
    private var stepLineWidth: Int = 0
    @Dimension(unit = Dimension.SP)
    private var textSize: Float = 0.toFloat()
    @Dimension
    private var textPadding: Int = 0
    private var selectedStepNumberColor: Int = 0
    @Dimension(unit = Dimension.SP)
    private var stepNumberTextSize: Float = 0.toFloat()
    @ColorInt
    private var doneStepMarkColor: Int = 0
    private var animationDuration: Int = 0
    private var nextStepCircleEnabled: Boolean = false
    @ColorInt
    private var nextStepCircleColor: Int = 0

    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint: TextPaint
    private var animator: ValueAnimator? = null

    private lateinit var circlesX: IntArray
    private lateinit var startLinesX: IntArray
    private lateinit var endLinesX: IntArray
    private lateinit var constraints: FloatArray
    private var circlesY: Int = 0
    private var textY: Int = 0
    private var animatedFraction: Float = 0.toFloat()
    private var done: Boolean = false
    private lateinit var textLayouts: Array<StaticLayout>

    private val bounds = Rect()

    val stepCount: Int
        get() = if (displayMode == DISPLAY_MODE_WITH_TEXT) steps.size else stepsNumber

    private val isRtl: Boolean
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        get() = ViewCompat.getLayoutDirection(this) == View.LAYOUT_DIRECTION_RTL

    private val circleY: Int
        get() {
            val availableHeight = measuredHeight - paddingTop - paddingBottom
            if (displayMode == DISPLAY_MODE_NO_TEXT) {
                return availableHeight / 2
            }
            val maxItemHeight = maxTextHeight + Math.max(selectedCircleRadius, doneCircleRadius) + textPadding
            val additionalPadding = (availableHeight - maxItemHeight) / 2
            return paddingTop + additionalPadding + selectedCircleRadius
        }

    private val maxTextHeight: Int
        get() {
            var max = 0
            if (textLayouts.isEmpty()) {
                return max
            }
            for (tl in textLayouts) {
                max = Math.max(tl.height, max)
            }
            return max
        }

    private val circlePositions: IntArray
        get() {
            val stepsCount = stepCount
            val result = IntArray(stepsCount)

            if (result.isEmpty()) {
                return result
            }

            result[0] = startCirclePosition

            if (result.size == 1) {
                return result
            }

            result[stepsCount - 1] = endCirclePosition

            if (result.size < 3) {
                return result
            }

            val spaceLeft = (if (isRtl) result[0] - result[stepsCount - 1] else result[stepsCount - 1] - result[0]).toFloat()
            val margin = (spaceLeft / (stepsCount - 1)).toInt()

            if (isRtl) {
                for (i in 1 until stepsCount - 1) {
                    result[i] = result[i - 1] - margin
                }
            } else {
                for (i in 1 until stepsCount - 1) {
                    result[i] = result[i - 1] + margin
                }
            }

            return result
        }

    private val startCirclePosition: Int
        get() {
            return if (displayMode == DISPLAY_MODE_WITH_TEXT) {
                if (isRtl) {
                    measuredWidth - paddingRight -
                            Math.max(getMaxLineWidth(textLayouts[0]) / 2, selectedCircleRadius)
                } else {
                    paddingLeft + Math.max(getMaxLineWidth(textLayouts[0]) / 2, selectedCircleRadius)
                }
            } else {
                if (isRtl) {
                    measuredWidth - paddingRight - selectedCircleRadius
                } else {
                    paddingLeft + selectedCircleRadius
                }
            }
        }

    private val endCirclePosition: Int
        get() {
            return if (displayMode == DISPLAY_MODE_WITH_TEXT) {
                if (isRtl) {
                    paddingLeft + Math.max(getMaxLineWidth(last(textLayouts)) / 2, selectedCircleRadius)
                } else {
                    measuredWidth - paddingRight -
                            Math.max(getMaxLineWidth(last(textLayouts)) / 2, selectedCircleRadius)
                }
            } else {
                if (isRtl) {
                    paddingLeft + selectedCircleRadius
                } else {
                    measuredWidth - paddingRight - selectedCircleRadius
                }
            }
        }

    interface OnStepClickListener {
        /**
         * Index of the first step is 0.
         *
         * @param step index of the step clicked.
         */
        fun onStepClick(step: Int)
    }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(ANIMATION_LINE, ANIMATION_CIRCLE, ANIMATION_ALL, ANIMATION_NONE)
    annotation class AnimationType

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(DISPLAY_MODE_WITH_TEXT, DISPLAY_MODE_NO_TEXT)
    annotation class DisplayMode

    init {
        paint.textAlign = Paint.Align.CENTER
        textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        textPaint.textAlign = Paint.Align.CENTER
        applyStyles(context, attrs, defStyleAttr)
        drawEditMode()
    }

    @Suppress("DEPRECATION")
    private fun applyStyles(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.StepView, defStyleAttr, R.style.StepView)
        selectedCircleColor = ta.getColor(R.styleable.StepView_sv_selectedCircleColor, 0)
        selectedCircleRadius = ta.getDimensionPixelSize(R.styleable.StepView_sv_selectedCircleRadius, 0)
        selectedTextColor = ta.getColor(R.styleable.StepView_sv_selectedTextColor, 0)
        selectedStepNumberColor = ta.getColor(R.styleable.StepView_sv_selectedStepNumberColor, 0)
        doneStepMarkColor = ta.getColor(R.styleable.StepView_sv_doneStepMarkColor, 0)
        doneCircleColor = ta.getColor(R.styleable.StepView_sv_doneCircleColor, 0)
        doneCircleRadius = ta.getDimensionPixelSize(R.styleable.StepView_sv_doneCircleRadius, 0)
        doneTextColor = ta.getColor(R.styleable.StepView_sv_doneTextColor, 0)
        nextTextColor = ta.getColor(R.styleable.StepView_sv_nextTextColor, 0)
        stepPadding = ta.getDimensionPixelSize(R.styleable.StepView_sv_stepPadding, 0)
        nextStepLineColor = ta.getColor(R.styleable.StepView_sv_nextStepLineColor, 0)
        doneStepLineColor = ta.getColor(R.styleable.StepView_sv_doneStepLineColor, 0)
        stepLineWidth = ta.getDimensionPixelSize(R.styleable.StepView_sv_stepLineWidth, 0)
        textPadding = ta.getDimensionPixelSize(R.styleable.StepView_sv_textPadding, 0)
        stepNumberTextSize = ta.getDimension(R.styleable.StepView_sv_stepNumberTextSize, 0f)
        textSize = ta.getDimension(R.styleable.StepView_sv_textSize, 0f)
        animationDuration = ta.getInteger(R.styleable.StepView_sv_animationDuration, 0)
        animationType = ta.getInteger(R.styleable.StepView_sv_animationType, 0)
        stepsNumber = ta.getInteger(R.styleable.StepView_sv_stepsNumber, 0)
        nextStepCircleEnabled = ta.getBoolean(R.styleable.StepView_sv_nextStepCircleEnabled, false)
        nextStepCircleColor = ta.getColor(R.styleable.StepView_sv_nextStepCircleColor, 0)
        val descriptions = ta.getTextArray(R.styleable.StepView_sv_steps)
        displayMode = if (descriptions != null) {
            for (description in descriptions) {
                steps.add(description.toString())
            }
            DISPLAY_MODE_WITH_TEXT
        } else {
            DISPLAY_MODE_NO_TEXT
        }
        val background = ta.getDrawable(R.styleable.StepView_sv_background)
        if (background != null) {
            setBackgroundDrawable(background)
        }
        val fontId = ta.getResourceId(R.styleable.StepView_sv_typeface, 0)
        if (fontId != 0) {
            val typeface = ResourcesCompat.getFont(context, fontId)
            setTypeface(typeface)
        }
        textPaint.textSize = textSize
        ta.recycle()
    }

    private fun setTypeface(typeface: Typeface?) {
        if (typeface != null) {
            textPaint.typeface = typeface
            paint.typeface = typeface
        }
    }

    private fun drawEditMode() {
        if (isInEditMode) {
            if (displayMode == DISPLAY_MODE_WITH_TEXT) {
                if (steps.isEmpty()) {
                    steps.add("Step 1")
                    steps.add("Step 2")
                    steps.add("Step 3")
                }
                setSteps(steps)
            } else {
                if (stepsNumber == 0) {
                    stepsNumber = 4
                }
                setStepsNumber(stepsNumber)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val superResult = super.onTouchEvent(event)
        if (onStepClickListener != null && isEnabled) {
            val action = event.actionMasked
            if (action == MotionEvent.ACTION_UP) {
                val x = event.x
                val y = event.y
                val step = getStepByPointer(x, y)
                onStepClickListener!!.onStepClick(step)
            }
        }
        return superResult
    }

    @Suppress("ProtectedInFinal")
    protected fun getStepByPointer(x: Float, @Suppress("UNUSED_PARAMETER") y: Float): Int {
        val count = stepCount
        for (i in constraints.indices) {
            val constraint = constraints[i]
            if (x <= constraint) {
                return i
            }
        }
        return count - 1
    }

    fun setOnStepClickListener(listener: OnStepClickListener?) {
        isClickable = listener != null
        onStepClickListener = listener
    }

    fun setSteps(steps: List<String>) {
        stepsNumber = 0
        displayMode = DISPLAY_MODE_WITH_TEXT
        this.steps.clear()
        this.steps.addAll(steps)
        requestLayout()
        go(START_STEP, false)
    }

    fun setStepsNumber(number: Int) {
        steps.clear()
        displayMode = DISPLAY_MODE_NO_TEXT
        stepsNumber = number
        requestLayout()
        go(START_STEP, false)
    }

    fun getState(): State {
        return State()
    }

    fun go(step: Int, animate: Boolean) {
        if (step in START_STEP..(stepCount - 1)) {
            if (animate && animationType != ANIMATION_NONE) {
                if (Math.abs(step - currentStep) > 1) {
                    endAnimation()
                    currentStep = step
                    invalidate()
                } else {
                    nextAnimatedStep = step
                    state = ANIMATE_STEP_TRANSITION
                    animate(step)
                    invalidate()
                }
            } else {
                currentStep = step
                invalidate()
            }
        }
    }

    fun done(isDone: Boolean) {
        done = isDone
        invalidate()
    }

    private fun endAnimation() {
        if (animator != null && animator!!.isRunning) {
            animator!!.end()
        }
    }

    private fun animate(step: Int) {
        endAnimation()
        animator = getAnimator(step)
        animator?.addUpdateListener { valueAnimator ->
            animatedFraction = valueAnimator.animatedFraction
            invalidate()
        }
        animator?.addListener(object : AnimatorListener() {
            override fun onAnimationEnd(animator: Animator) {
                state = IDLE
                currentStep = step
                invalidate()
            }
        })
        animator?.duration = animationDuration.toLong()
        animator?.start()
    }

    private fun getAnimator(step: Int): ValueAnimator? {
        var animator: ValueAnimator? = null
        val i: Int
        if (step > currentStep) {
            when (animationType) {
                ANIMATION_LINE -> {
                    i = step - 1
                    animator = ValueAnimator.ofInt(startLinesX[i], endLinesX[i])
                }
                ANIMATION_CIRCLE -> animator = ValueAnimator.ofInt(0, selectedCircleRadius)
                ANIMATION_ALL -> {
                    i = step - 1
                    animator = ValueAnimator.ofInt(0, (endLinesX[i] - startLinesX[i] + selectedCircleRadius) / 2)
                }
            }
        } else if (step < currentStep) {
            when (animationType) {
                ANIMATION_LINE -> {
                    i = step
                    animator = ValueAnimator.ofInt(endLinesX[i], startLinesX[i])
                }
                ANIMATION_CIRCLE -> animator = ValueAnimator.ofInt(0, selectedCircleRadius)
                ANIMATION_ALL -> {
                    i = step
                    animator = ValueAnimator.ofInt(0, (endLinesX[i] - startLinesX[i] + selectedCircleRadius) / 2)
                }
            }
        }
        return animator
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (animator != null && animator!!.isRunning) {
            animator?.cancel()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = measureWidth(widthMeasureSpec)
        if (stepCount == 0) {
            setMeasuredDimension(width, 0)
            return
        }
        if (width == 0) {
            setMeasuredDimension(width, 0)
            return
        }
        measureConstraints(width)
        val height = measureHeight(heightMeasureSpec)
        setMeasuredDimension(width, height)
        measureAttributes()
    }

    private fun measureWidth(widthMeasureSpec: Int): Int {
        return View.MeasureSpec.getSize(widthMeasureSpec)
    }

    private fun measureConstraints(width: Int) {
        constraints = FloatArray(stepCount)
        constraints[0] = (width / stepCount).toFloat()
        for (i in 1 until constraints.size) {
            constraints[i] = constraints[0] * (i + 1)
        }
    }

    private fun measureHeight(heightMeasureSpec: Int): Int {
        val specSize = View.MeasureSpec.getSize(heightMeasureSpec)
        val specMode = View.MeasureSpec.getMode(heightMeasureSpec)
        var desiredSize = (paddingTop
                + paddingBottom
                + Math.max(selectedCircleRadius, doneCircleRadius) * 2
                + if (displayMode == DISPLAY_MODE_WITH_TEXT) textPadding else 0)
        if (!steps.isEmpty()) {
            desiredSize += measureStepsHeight()
        }
        var result = 0

        when (specMode) {
            View.MeasureSpec.UNSPECIFIED ->
                // Parent says we can be as big as we want.
                result = desiredSize
            View.MeasureSpec.AT_MOST ->
                // Parent says we can be as big as we want, up to specSize.
                // Don't be larger than specSize
                result = Math.min(desiredSize, specSize)
            View.MeasureSpec.EXACTLY ->
                // No choice. Do what we are told.
                result = specSize
        }

        return result
    }

    @Suppress("DEPRECATION")
    private fun measureStepsHeight(): Int {
        textPaint.textSize = textSize
        textLayouts = Array(steps.size) {
            val text = steps[it]
            val alignment = if (isRtl) Layout.Alignment.ALIGN_OPPOSITE else Layout.Alignment.ALIGN_NORMAL
            StaticLayout(
                    text,
                    textPaint,
                    measuredWidth / steps.size,
                    alignment,
                    1f,
                    0f,
                    true
            )
        }
        return textLayouts.maxBy { it.height }!!.height
    }

    private fun measureAttributes() {
        circlesY = circleY
        if (displayMode == DISPLAY_MODE_NO_TEXT) {
            circlesY += paddingTop
        }
        circlesX = circlePositions
        if (displayMode == DISPLAY_MODE_NO_TEXT) {
            paint.textSize = stepNumberTextSize
        } else {
            paint.textSize = stepNumberTextSize
            paint.textSize = textSize
            textY = circlesY + selectedCircleRadius + textPadding
        }
        measureLines()
    }

    private fun getMaxLineWidth(layout: StaticLayout): Int {
        val lineCount = layout.lineCount
        var max = 0
        for (i in 0 until lineCount) {
            max = Math.max(layout.getLineWidth(i), max.toFloat()).toInt()
        }
        return max
    }

    private fun <T> last(array: Array<T>): T {
        return array[array.size - 1]
    }

    private fun measureLines() {
        startLinesX = IntArray(stepCount - 1)
        endLinesX = IntArray(stepCount - 1)
        val padding = stepPadding + selectedCircleRadius

        for (i in 1 until stepCount) {
            if (isRtl) {
                startLinesX[i - 1] = circlesX[i - 1] - padding
                endLinesX[i - 1] = circlesX[i] + padding
            } else {
                startLinesX[i - 1] = circlesX[i - 1] + padding
                endLinesX[i - 1] = circlesX[i] - padding
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (height == 0) return

        val stepSize = stepCount

        if (stepSize == 0) {
            return
        }

        for (i in 0 until stepSize) {
            drawStep(canvas, i, circlesX[i], circlesY)
        }

        for (i in startLinesX.indices) {
            if (state == ANIMATE_STEP_TRANSITION && i == nextAnimatedStep - 1
                    && nextAnimatedStep > currentStep && (animationType == ANIMATION_LINE || animationType == ANIMATION_ALL)) {
                val animatedX = (startLinesX[i] + animatedFraction * (endLinesX[i] - startLinesX[i])).toInt()
                drawLine(canvas, startLinesX[i], animatedX, circlesY, true)
                drawLine(canvas, animatedX, endLinesX[i], circlesY, false)
            } else if (state == ANIMATE_STEP_TRANSITION && i == nextAnimatedStep
                    && nextAnimatedStep < currentStep && (animationType == ANIMATION_LINE || animationType == ANIMATION_ALL)) {
                val animatedX = (endLinesX[i] - animatedFraction * (endLinesX[i] - startLinesX[i])).toInt()
                drawLine(canvas, startLinesX[i], animatedX, circlesY, true)
                drawLine(canvas, animatedX, endLinesX[i], circlesY, false)
            } else if (i < currentStep) {
                drawLine(canvas, startLinesX[i], endLinesX[i], circlesY, true)
            } else {
                drawLine(canvas, startLinesX[i], endLinesX[i], circlesY, false)
            }
        }
    }

    private fun drawStep(canvas: Canvas, step: Int, circleCenterX: Int, circleCenterY: Int) {
        // todo: fix alpha for text when going back/forward
        // todo: don't scale up/down numbers if circles are not scaled
        val text = if (displayMode == DISPLAY_MODE_WITH_TEXT) steps[step] else ""
        val isSelected = step == currentStep
        val isDone = if (done) step <= currentStep else step < currentStep
        val number = (step + 1).toString()

        if (isSelected && !isDone) {
            paint.color = selectedCircleColor
            val radius: Int
            if (state == ANIMATE_STEP_TRANSITION && (animationType == ANIMATION_CIRCLE || animationType == ANIMATION_ALL)
                    && nextAnimatedStep < currentStep) {
                radius = if (!nextStepCircleEnabled || nextStepCircleColor == 0) {
                    (selectedCircleRadius - selectedCircleRadius * animatedFraction).toInt()
                } else {
                    selectedCircleRadius
                }
                if (nextStepCircleEnabled && nextStepCircleColor != 0) {
                    paint.color = ColorUtils.blendARGB(
                            selectedCircleColor,
                            nextStepCircleColor,
                            animatedFraction)
                }
            } else {
                radius = selectedCircleRadius
            }
            canvas.drawCircle(circleCenterX.toFloat(), circleCenterY.toFloat(), radius.toFloat(), paint)

            paint.color = selectedStepNumberColor
            paint.textSize = stepNumberTextSize
            drawNumber(canvas, number, circleCenterX, paint)

            textPaint.textSize = textSize
            textPaint.color = selectedTextColor
            drawText(canvas, text, textY, step)
        } else if (isDone) {
            paint.color = doneCircleColor
            canvas.drawCircle(circleCenterX.toFloat(), circleCenterY.toFloat(), doneCircleRadius.toFloat(), paint)

            drawCheckMark(canvas, circleCenterX, circleCenterY)

            if (state == ANIMATE_STEP_TRANSITION && step == nextAnimatedStep && nextAnimatedStep < currentStep) {
                paint.color = selectedTextColor
                val alpha = Math.max(Color.alpha(doneTextColor), (animatedFraction * 255).toInt())
                paint.alpha = alpha
            } else {
                paint.color = doneTextColor
            }
            textPaint.textSize = textSize
            textPaint.color = doneTextColor
            drawText(canvas, text, textY, step)
        } else {
            if (state == ANIMATE_STEP_TRANSITION && step == nextAnimatedStep && nextAnimatedStep > currentStep) {
                if (animationType == ANIMATION_CIRCLE || animationType == ANIMATION_ALL) {
                    if (nextStepCircleEnabled && nextStepCircleColor != 0) {
                        paint.color = ColorUtils.blendARGB(
                                nextStepCircleColor,
                                selectedCircleColor,
                                animatedFraction)
                        canvas.drawCircle(circleCenterX.toFloat(), circleCenterY.toFloat(), selectedCircleRadius.toFloat(), paint)
                    } else {
                        val animatedRadius = (selectedCircleRadius * animatedFraction).toInt()
                        paint.color = selectedCircleColor
                        canvas.drawCircle(circleCenterX.toFloat(), circleCenterY.toFloat(), animatedRadius.toFloat(), paint)
                    }
                }
                if (animationType != ANIMATION_NONE) {
                    if (animationType == ANIMATION_CIRCLE || animationType == ANIMATION_ALL) {
                        paint.color = selectedStepNumberColor
                        val alpha = (animatedFraction * 255).toInt()
                        paint.alpha = alpha
                        paint.textSize = stepNumberTextSize * animatedFraction
                        drawNumber(canvas, number, circleCenterX, paint)
                    } else {
                        paint.textSize = stepNumberTextSize
                        paint.color = nextTextColor
                        drawNumber(canvas, number, circleCenterX, paint)
                    }
                } else {
                    paint.textSize = stepNumberTextSize
                    paint.color = nextTextColor
                    drawNumber(canvas, number, circleCenterX, paint)
                }

                textPaint.textSize = textSize
                textPaint.color = nextTextColor
                val alpha = Math.max(Color.alpha(nextTextColor).toFloat(), animatedFraction * 255).toInt()
                textPaint.alpha = alpha
                drawText(canvas, text, textY, step)
            } else {
                if (nextStepCircleEnabled && nextStepCircleColor != 0) {
                    paint.color = nextStepCircleColor
                    canvas.drawCircle(circleCenterX.toFloat(), circleCenterY.toFloat(), selectedCircleRadius.toFloat(), paint)
                }

                paint.color = nextTextColor

                paint.textSize = stepNumberTextSize
                drawNumber(canvas, number, circleCenterX, paint)

                textPaint.textSize = textSize
                textPaint.color = nextTextColor
                drawText(canvas, text, textY, step)
            }
        }
    }

    private fun drawNumber(canvas: Canvas, number: String, circleCenterX: Int, paint: Paint) {
        paint.getTextBounds(number, 0, number.length, bounds)
        val y = circlesY + bounds.height() / 2f - bounds.bottom
        canvas.drawText(number, circleCenterX.toFloat(), y, paint)
    }

    private fun drawText(canvas: Canvas, text: String, y: Int, step: Int) {
        if (text.isEmpty()) {
            return
        }
        val layout = textLayouts[step]
        canvas.save()
        canvas.translate(circlesX[step].toFloat(), y.toFloat())
        layout.draw(canvas)
        canvas.restore()
    }

    private fun drawCheckMark(canvas: Canvas, circleCenterX: Int, circleCenterY: Int) {
        paint.color = doneStepMarkColor
        val width = stepNumberTextSize * 0.1f
        paint.strokeWidth = width
        val bounds = Rect(
                (circleCenterX - width * 4.5).toInt(),
                (circleCenterY - width * 3.5).toInt(),
                (circleCenterX + width * 4.5).toInt(),
                (circleCenterY + width * 3.5).toInt())
        canvas.drawLine(
                bounds.left + 0.5f * width,
                bounds.bottom - 3.25f * width,
                bounds.left + 3.25f * width,
                bounds.bottom - 0.75f * width, paint)
        canvas.drawLine(
                bounds.left + 2.75f * width,
                bounds.bottom - 0.75f * width,
                bounds.right - 0.375f * width,
                bounds.top + 0.75f * width, paint)
    }

    private fun drawLine(canvas: Canvas, startX: Int, endX: Int, centerY: Int, highlight: Boolean) {
        if (highlight) {
            paint.color = doneStepLineColor
            paint.strokeWidth = stepLineWidth.toFloat()
            canvas.drawLine(startX.toFloat(), centerY.toFloat(), endX.toFloat(), centerY.toFloat(), paint)
        } else {
            paint.color = nextStepLineColor
            paint.strokeWidth = stepLineWidth.toFloat()
            canvas.drawLine(startX.toFloat(), centerY.toFloat(), endX.toFloat(), centerY.toFloat(), paint)
        }
    }

    inner class State {
        private var steps: List<String>? = null
        private var stepsNumber: Int = 0
        @AnimationType
        private var animationType = this@StepView.animationType
        @ColorInt
        private var selectedCircleColor = this@StepView.selectedCircleColor
        @Dimension
        private var selectedCircleRadius = this@StepView.selectedCircleRadius
        @ColorInt
        private var selectedTextColor = this@StepView.selectedTextColor
        @ColorInt
        private var doneCircleColor = this@StepView.doneCircleColor
        @Dimension
        private var doneCircleRadius = this@StepView.doneCircleRadius
        @ColorInt
        private var doneTextColor = this@StepView.doneTextColor
        @ColorInt
        private var nextTextColor = this@StepView.nextTextColor
        @Dimension
        private var stepPadding = this@StepView.stepPadding
        @ColorInt
        private var nextStepLineColor = this@StepView.nextStepLineColor
        @ColorInt
        private var doneStepLineColor = this@StepView.doneStepLineColor
        @Dimension
        private var stepLineWidth = this@StepView.stepLineWidth
        @Dimension(unit = Dimension.SP)
        private var textSize = this@StepView.textSize
        @Dimension
        private var textPadding = this@StepView.textPadding
        @ColorInt
        private var selectedStepNumberColor = this@StepView.selectedStepNumberColor
        @Dimension(unit = Dimension.SP)
        private var stepNumberTextSize = this@StepView.stepNumberTextSize
        @ColorInt
        private var doneStepMarkColor = this@StepView.doneStepMarkColor
        private var animationDuration = this@StepView.animationDuration
        private var nextStepCircleEnabled = this@StepView.nextStepCircleEnabled
        @ColorInt
        private var nextStepCircleColor = this@StepView.nextStepCircleColor
        private var typeface = paint.typeface

        fun animationType(@AnimationType animationType: Int): State {
            this.animationType = animationType
            return this
        }

        fun selectedCircleColor(@ColorInt selectedCircleColor: Int): State {
            this.selectedCircleColor = selectedCircleColor
            return this
        }

        fun selectedCircleRadius(@Dimension selectedCircleRadius: Int): State {
            this.selectedCircleRadius = selectedCircleRadius
            return this
        }

        fun selectedTextColor(@ColorInt selectedTextColor: Int): State {
            this.selectedTextColor = selectedTextColor
            return this
        }

        fun doneCircleColor(@ColorInt doneCircleColor: Int): State {
            this.doneCircleColor = doneCircleColor
            return this
        }

        fun doneCircleRadius(@Dimension doneCircleRadius: Int): State {
            this.doneCircleRadius = doneCircleRadius
            return this
        }

        fun doneTextColor(@ColorInt doneTextColor: Int): State {
            this.doneTextColor = doneTextColor
            return this
        }

        fun nextTextColor(@ColorInt nextTextColor: Int): State {
            this.nextTextColor = nextTextColor
            return this
        }

        fun stepPadding(@Dimension stepPadding: Int): State {
            this.stepPadding = stepPadding
            return this
        }

        fun nextStepLineColor(@ColorInt nextStepLineColor: Int): State {
            this.nextStepLineColor = nextStepLineColor
            return this
        }

        fun doneStepLineColor(@ColorInt doneStepLineColor: Int): State {
            this.doneStepLineColor = doneStepLineColor
            return this
        }

        fun stepLineWidth(@Dimension stepLineWidth: Int): State {
            this.stepLineWidth = stepLineWidth
            return this
        }

        fun textSize(@Dimension(unit = Dimension.SP) textSize: Int): State {
            this.textSize = textSize.toFloat()
            return this
        }

        fun textPadding(@Dimension textPadding: Int): State {
            this.textPadding = textPadding
            return this
        }

        fun selectedStepNumberColor(@ColorInt selectedStepNumberColor: Int): State {
            this.selectedStepNumberColor = selectedStepNumberColor
            return this
        }

        fun stepNumberTextSize(@Dimension(unit = Dimension.SP) stepNumberTextSize: Int): State {
            this.stepNumberTextSize = stepNumberTextSize.toFloat()
            return this
        }

        fun doneStepMarkColor(@ColorInt doneStepMarkColor: Int): State {
            this.doneStepMarkColor = doneStepMarkColor
            return this
        }

        fun animationDuration(animationDuration: Int): State {
            this.animationDuration = animationDuration
            return this
        }

        fun steps(steps: List<String>): State {
            this.steps = steps
            return this
        }

        fun stepsNumber(stepsNumber: Int): State {
            this.stepsNumber = stepsNumber
            return this
        }

        fun typeface(typeface: Typeface): State {
            this.typeface = typeface
            return this
        }

        fun nextStepCircleEnabled(enabled: Boolean): State {
            this.nextStepCircleEnabled = enabled
            return this
        }

        fun nextStepCircleColor(@ColorInt color: Int): State {
            this.nextStepCircleColor = color
            return this
        }

        fun commit() {
            this@StepView.animationType = animationType
            this@StepView.selectedTextColor = selectedTextColor
            this@StepView.selectedCircleRadius = selectedCircleRadius
            this@StepView.selectedCircleColor = selectedCircleColor
            this@StepView.doneCircleColor = doneCircleColor
            this@StepView.doneCircleRadius = doneCircleRadius
            this@StepView.doneTextColor = doneTextColor
            this@StepView.nextTextColor = nextTextColor
            this@StepView.stepPadding = stepPadding
            this@StepView.nextStepLineColor = nextStepLineColor
            this@StepView.doneStepLineColor = doneStepLineColor
            this@StepView.stepLineWidth = stepLineWidth
            this@StepView.textSize = textSize
            this@StepView.textPadding = textPadding
            this@StepView.selectedStepNumberColor = selectedStepNumberColor
            this@StepView.stepNumberTextSize = stepNumberTextSize
            this@StepView.doneStepMarkColor = doneStepMarkColor
            this@StepView.animationDuration = animationDuration
            setTypeface(typeface)
            this@StepView.nextStepCircleEnabled = nextStepCircleEnabled
            this@StepView.nextStepCircleColor = nextStepCircleColor
            if (steps != null && this@StepView.steps != steps) {
                this@StepView.setSteps(steps!!)
            } else if (stepsNumber != 0 && stepsNumber != this@StepView.stepsNumber) {
                this@StepView.setStepsNumber(stepsNumber)
            } else {
                this@StepView.invalidate()
            }
        }
    }

    companion object {

        const val ANIMATION_LINE = 0
        const val ANIMATION_CIRCLE = 1
        const val ANIMATION_ALL = 2
        const val ANIMATION_NONE = 3

        const val DISPLAY_MODE_WITH_TEXT = 0
        const val DISPLAY_MODE_NO_TEXT = 1
        private const val ANIMATE_STEP_TRANSITION = 0
        private const val IDLE = 1

        private const val START_STEP = 0
    }
}
