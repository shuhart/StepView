package com.shuhart.stepview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.shuhart.stepview.animation.AnimatorListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StepView extends View {
    public static final int ANIMATION_LINE = 0;
    public static final int ANIMATION_CIRCLE = 1;
    public static final int ANIMATION_ALL = 2;
    public static final int ANIMATION_NONE = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ANIMATION_LINE, ANIMATION_CIRCLE, ANIMATION_ALL, ANIMATION_NONE})
    public @interface AnimationType {
    }

    private static final int ANIMATE_STEP_TRANSITION = 0;
    private static final int IDLE = 1;

    private static final int START_STEP = 0;
    private @AnimationType
    int animationType;
    private final List<String> steps = new ArrayList<>();
    private int currentStep = START_STEP;
    private int nextAnimatedStep;
    private int state = IDLE;

    private int selectedCircleColor;
    private int selectedCircleRadius;
    private int selectedTextColor;
    private int doneCircleColor;
    private int doneCircleRadius;
    private int doneTextColor;
    private int nextTextColor;
    private int stepPadding;
    private int nextStepLineColor;
    private int doneStepLineColor;
    private int stepLineWidth;
    private float textSize;
    private int textPadding;
    private int selectedStepNumberColor;
    private float stepNumberTextSize;
    private int doneStepMarkColor;
    private int animationDuration;

    private Paint paint;
    private ValueAnimator animator;

    private int[] circlesX;
    private int stepNumbersY;
    private int[] startLinesX;
    private int[] endLinesX;
    private int circlesY;
    private int textY;
    //    private int animatedX;
    private float animatedFraction;

    public StepView(Context context) {
        this(context, null);
    }

    public StepView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.stepViewStyle);
    }

    public StepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyStyles(context, attrs, defStyleAttr);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextAlign(Paint.Align.CENTER);
        drawEditMode();
    }

    private void applyStyles(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.StepView, defStyleAttr, R.style.StepView);
        selectedCircleColor = ta.getColor(R.styleable.StepView_selectedCircleColor, 0);
        selectedCircleRadius = ta.getDimensionPixelSize(R.styleable.StepView_selectedCircleRadius, 0);
        selectedTextColor = ta.getColor(R.styleable.StepView_selectedTextColor, 0);
        selectedStepNumberColor = ta.getColor(R.styleable.StepView_selectedStepNumberColor, 0);
        doneStepMarkColor = ta.getColor(R.styleable.StepView_doneStepMarkColor, 0);
        doneCircleColor = ta.getColor(R.styleable.StepView_doneCircleColor, 0);
        doneCircleRadius = ta.getDimensionPixelSize(R.styleable.StepView_doneCircleRadius, 0);
        doneTextColor = ta.getColor(R.styleable.StepView_doneTextColor, 0);
        nextTextColor = ta.getColor(R.styleable.StepView_nextTextColor, 0);
        stepPadding = ta.getDimensionPixelSize(R.styleable.StepView_stepPadding, 0);
        nextStepLineColor = ta.getColor(R.styleable.StepView_nextStepLineColor, 0);
        doneStepLineColor = ta.getColor(R.styleable.StepView_doneStepLineColor, 0);
        stepLineWidth = ta.getDimensionPixelSize(R.styleable.StepView_stepLineWidth, 0);
        textPadding = ta.getDimensionPixelSize(R.styleable.StepView_textPadding, 0);
        stepNumberTextSize = ta.getDimension(R.styleable.StepView_stepNumberTextSize, 0);
        textSize = ta.getDimension(R.styleable.StepView_android_textSize, 0);
        animationDuration = ta.getInteger(R.styleable.StepView_android_animationDuration, 0);
        animationType = ta.getInteger(R.styleable.StepView_animationType, 0);
        Drawable background = ta.getDrawable(R.styleable.StepView_android_background);
        if (background != null) {
            setBackgroundDrawable(background);
        }
        ta.recycle();
    }

    private void drawEditMode() {
        if (isInEditMode()) {
            String[] steps = {"Step 1", "Step 2", "Step 3"};
            setSteps(Arrays.asList(steps));
        }
    }

    public void setAnimationType(@AnimationType int animationType) {
        this.animationType = animationType;
        invalidate();
    }

    public void setSteps(List<String> steps) {
        this.steps.clear();
        if (steps != null) {
            this.steps.addAll(steps);
        }
        requestLayout();
        go(START_STEP, false);
    }

    public void go(int step, boolean animate) {
        if (step >= START_STEP && step < steps.size()) {
            if (animate && animationType != ANIMATION_NONE) {
                if (Math.abs(step - currentStep) > 1) {
                    currentStep = step;
                    invalidate();
                } else {
                    nextAnimatedStep = step;
                    state = ANIMATE_STEP_TRANSITION;
                    animate(step);
                    invalidate();
                }
            } else {
                currentStep = step;
                invalidate();
            }
        }
    }

    private void animate(final int step) {
        if (animator != null && animator.isRunning()) {
            animator.end();
        }
        animator = getAnimator(step);
        if (animator == null) {
            return;
        }
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                animatedFraction = valueAnimator.getAnimatedFraction();
                invalidate();
            }
        });
        animator.addListener(new AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                state = IDLE;
                currentStep = step;
                invalidate();
            }
        });
        animator.setDuration(animationDuration);
        animator.start();
    }

    @Nullable
    private ValueAnimator getAnimator(int step) {
        ValueAnimator animator = null;
        final int i;
        if (step > currentStep) {
            if (animationType == ANIMATION_LINE) {
                i = step - 1;
                animator = ValueAnimator.ofInt(startLinesX[i], endLinesX[i]);
            } else if (animationType == ANIMATION_CIRCLE) {
                animator = ValueAnimator.ofInt(0, selectedCircleRadius);
            } else if (animationType == ANIMATION_ALL) {
                i = step - 1;
                animator = ValueAnimator.ofInt(0, (endLinesX[i] - startLinesX[i] + selectedCircleRadius) / 2);
            }
        } else if (step < currentStep) {
            if (animationType == ANIMATION_LINE) {
                i = step;
                animator = ValueAnimator.ofInt(endLinesX[i], startLinesX[i]);
            } else if (animationType == ANIMATION_CIRCLE) {
                animator = ValueAnimator.ofInt(0, selectedCircleRadius);
            } else if (animationType == ANIMATION_ALL) {
                i = step;
                animator = ValueAnimator.ofInt(0, (endLinesX[i] - startLinesX[i] + selectedCircleRadius) / 2);
            }
        }
        return animator;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public int getStepCount() {
        return steps.size();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
        measureAttributes();
    }

    private int measureWidth(int widthMeasureSpec) {
        return MeasureSpec.getSize(widthMeasureSpec);
    }

    private int measureHeight(int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (heightMode == MeasureSpec.AT_MOST) {
            final int fontHeight = fontHeight();
            height = getPaddingTop()
                    + getPaddingBottom()
                    + (Math.max(selectedCircleRadius, doneCircleRadius)) * 2
                    + textPadding;
            if (steps.isEmpty()) {
                height += fontHeight;
            } else {
                height += measureStepsHeight(fontHeight);
            }
        }

        return height;
    }

    private int fontHeight() {
        return (int) Math.ceil(paint.descent() - paint.ascent());
    }

    private int measureStepsHeight(int fontHeight) {
        paint.setTextSize(textSize);
        int max = 0;
        for (int i = 0; i < steps.size(); i++) {
            String text = steps.get(i);
            String[] split = text.split("\\n");
            if (split.length == 1) {
                max = Math.max(fontHeight, max);
            } else {
                max = Math.max(fontHeight * split.length, max);
            }
        }
        return max;
    }

    private void measureAttributes() {
        int fontHeight = fontHeight();
        int[] stepsTextWidth = measureSteps();

        circlesY = getPaddingTop() + selectedCircleRadius;
        circlesX = getCirclePositions(stepsTextWidth);
        stepNumbersY = (int) (circlesY + fontHeight / 2 - paint.descent());
        textY = circlesY + selectedCircleRadius + textPadding + fontHeight / 2;
        measureLines();
    }

    private int[] measureSteps() {
        int[] result = new int[steps.size()];
        for (int i = 0; i < steps.size(); i++) {
            result[i] = (int) paint.measureText(steps.get(i)) + /* correct possible conversion error */ 1;
        }
        return result;
    }

    private int[] getCirclePositions(int[] textWidth) {
        int[] result = new int[textWidth.length];
        result[0] = getPaddingLeft() + Math.max(textWidth[0] / 2, selectedCircleRadius);
        if (result.length == 1) {
            return result;
        }
        result[textWidth.length - 1] = getMeasuredWidth() - getPaddingRight() -
                Math.max(textWidth[textWidth.length - 1] / 2, selectedCircleRadius);
        if (result.length < 3) {
            return result;
        }
        float spaceLeft = result[textWidth.length - 1] - result[0];
        int margin = (int) (spaceLeft / (textWidth.length - 1));
        for (int i = 1; i < textWidth.length - 1; i++) {
            result[i] = result[i - 1] + margin;
        }
        return result;
    }

    private void measureLines() {
        startLinesX = new int[steps.size() - 1];
        endLinesX = new int[steps.size() - 1];
        int padding = stepPadding + selectedCircleRadius;

        for (int i = 1; i < steps.size(); i++) {
            startLinesX[i - 1] = circlesX[i - 1] + padding;
            endLinesX[i - 1] = circlesX[i] - padding;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int stepSize = steps.size();

        if (stepSize == 0) {
            return;
        }

        for (int i = 0; i < stepSize; i++) {
            drawStep(canvas, i, circlesX[i], circlesY);
        }

        for (int i = 0; i < startLinesX.length; i++) {
            if (state == ANIMATE_STEP_TRANSITION && i == nextAnimatedStep - 1
                    && nextAnimatedStep > currentStep && (animationType == ANIMATION_LINE || animationType == ANIMATION_ALL)) {
                int animatedX = (int) (startLinesX[i] + animatedFraction * (endLinesX[i] - startLinesX[i]));
                drawLine(canvas, startLinesX[i], animatedX, circlesY, true);
                drawLine(canvas, animatedX, endLinesX[i], circlesY, false);
            } else if (state == ANIMATE_STEP_TRANSITION && i == nextAnimatedStep
                    && nextAnimatedStep < currentStep && (animationType == ANIMATION_LINE || animationType == ANIMATION_ALL)) {
                int animatedX = (int) (endLinesX[i] - animatedFraction * (endLinesX[i] - startLinesX[i]));
                drawLine(canvas, startLinesX[i], animatedX, circlesY, true);
                drawLine(canvas, animatedX, endLinesX[i], circlesY, false);
            } else if (i < currentStep) {
                drawLine(canvas, startLinesX[i], endLinesX[i], circlesY, true);
            } else {
                drawLine(canvas, startLinesX[i], endLinesX[i], circlesY, false);
            }
        }
    }

    private void drawStep(Canvas canvas, int step, int circleCenterX, int circleCenterY) {
        final String text = steps.get(step);
        final boolean isSelected = step == currentStep;
        final boolean isDone = step < currentStep;
        final String number = String.valueOf(step + 1);

        if (isSelected) {
            paint.setColor(selectedCircleColor);
            int radius;
            if (state == ANIMATE_STEP_TRANSITION && (animationType == ANIMATION_CIRCLE || animationType == ANIMATION_ALL)
                    && nextAnimatedStep < currentStep) {
                radius = (int) (selectedCircleRadius - selectedCircleRadius * animatedFraction);
            } else {
                radius = selectedCircleRadius;
            }
            canvas.drawCircle(circleCenterX, circleCenterY, radius, paint);

            paint.setColor(selectedStepNumberColor);
            paint.setTextSize(stepNumberTextSize);
            canvas.drawText(number, circleCenterX, stepNumbersY, paint);

            paint.setColor(selectedTextColor);
            paint.setTextSize(textSize);
            drawText(canvas, text, circleCenterX, textY, paint);
        } else if (isDone) {
            paint.setColor(doneCircleColor);
            canvas.drawCircle(circleCenterX, circleCenterY, doneCircleRadius, paint);

            drawCheckMark(canvas, circleCenterX, circleCenterY);

            if (state == ANIMATE_STEP_TRANSITION && step == nextAnimatedStep && nextAnimatedStep < currentStep) {
                paint.setColor(selectedTextColor);
                int alpha = Math.max(Color.alpha(doneTextColor), (int) (animatedFraction * 255));
                paint.setAlpha(alpha);
            } else {
                paint.setColor(doneTextColor);
            }
            paint.setTextSize(textSize);
            drawText(canvas, text, circleCenterX, textY, paint);
        } else {
            if (state == ANIMATE_STEP_TRANSITION && step == nextAnimatedStep && nextAnimatedStep > currentStep) {
                if (animationType == ANIMATION_CIRCLE || animationType == ANIMATION_ALL) {
                    int animatedRadius = (int) (selectedCircleRadius * animatedFraction);
                    paint.setColor(selectedCircleColor);
                    canvas.drawCircle(circleCenterX, circleCenterY, animatedRadius, paint);
                }
                if (animationType != ANIMATION_NONE) {
                    paint.setTextSize(stepNumberTextSize);

                    if (animationType == ANIMATION_CIRCLE || animationType == ANIMATION_ALL) {
                        paint.setColor(selectedStepNumberColor);
                        int alpha = (int) (animatedFraction * 255);
                        paint.setAlpha(alpha);
                        paint.setTextSize(stepNumberTextSize * animatedFraction);
                        canvas.drawText(number, circleCenterX, stepNumbersY, paint);
                    } else {
                        paint.setColor(nextTextColor);
                        canvas.drawText(number, circleCenterX, stepNumbersY, paint);
                    }
                } else {
                    paint.setColor(nextTextColor);
                    canvas.drawText(number, circleCenterX, stepNumbersY, paint);
                }

                paint.setTextSize(textSize);
                paint.setColor(nextTextColor);
                int alpha = (int) Math.max(Color.alpha(nextTextColor), animatedFraction * 255);
                paint.setAlpha(alpha);
                drawText(canvas, text, circleCenterX, textY, paint);
            } else {
                paint.setColor(nextTextColor);

                paint.setTextSize(stepNumberTextSize);
                canvas.drawText(number, circleCenterX, stepNumbersY, paint);

                paint.setTextSize(textSize);
                drawText(canvas, text, circleCenterX, textY, paint);
            }
        }
    }

    private void drawText(Canvas canvas, String text, int x, int y, Paint paint) {
        String[] split = text.split("\\n");
        if (split.length == 1) {
            canvas.drawText(text, x, y, paint);
        } else {
            for (int i = 0; i < split.length; i++) {
                canvas.drawText(split[i], x, y + i * fontHeight(), paint);
            }
        }
    }

    private void drawCheckMark(Canvas canvas, int circleCenterX, int circleCenterY) {
        paint.setColor(doneStepMarkColor);
        float width = stepNumberTextSize * 0.1f;
        paint.setStrokeWidth(width);
        Rect bounds = new Rect(
                (int) (circleCenterX - width * 4.5),
                (int) (circleCenterY - width * 3.5),
                (int) (circleCenterX + width * 4.5),
                (int) (circleCenterY + width * 3.5));
        canvas.drawLine(
                bounds.left + 0.5f * width,
                bounds.bottom - 3.25f * width,
                bounds.left + 3.25f * width,
                bounds.bottom - 0.75f * width, paint);
        canvas.drawLine(
                bounds.left + 2.75f * width,
                bounds.bottom - 0.75f * width,
                bounds.right - 0.375f * width,
                bounds.top + 0.75f * width, paint);
    }

    private void drawLine(Canvas canvas, int startX, int endX, int centerY, boolean highlight) {
        if (highlight) {
            paint.setColor(doneStepLineColor);
            paint.setStrokeWidth(stepLineWidth);
            canvas.drawLine(startX, centerY, endX, centerY, paint);
        } else {
            paint.setColor(nextStepLineColor);
            paint.setStrokeWidth(stepLineWidth);
            canvas.drawLine(startX, centerY, endX, centerY, paint);
        }
    }
}