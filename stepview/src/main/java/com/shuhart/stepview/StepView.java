package com.shuhart.stepview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StepView extends View {
    private static final int START_STEP = 0;
    private final List<String> steps = new ArrayList<>();
    private int currentStep = START_STEP;

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

    private Paint paint;

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

    public void setSteps(List<String> steps) {
        this.steps.clear();
        if (steps != null) {
            this.steps.addAll(steps);
        }
        requestLayout();
        go(START_STEP);
    }

    public void go(int step) {
        if (step >= START_STEP && step < steps.size()) {
            currentStep = step;
            invalidate();
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
    }

    private int measureWidth(int widthMeasureSpec) {
        return MeasureSpec.getSize(widthMeasureSpec);
    }

    private int measureHeight(int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (heightMode == MeasureSpec.AT_MOST) {
            final int fontHeight = textHeight();
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

    private int textHeight() {
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

    @Override
    protected void onDraw(Canvas canvas) {
        final int stepSize = steps.size();
        if (stepSize == 0) {
            return;
        }

        final float ascent = paint.ascent();
        final float descent = paint.descent();
        final int fontHeight = (int) Math.ceil(descent - ascent);
        final int halfFontHeightOffset = -(int) (ascent + descent) / 2;
        final int y = getPaddingTop() + selectedCircleRadius;
        int[] stepsTextWidth = measureSteps();
        int[] circlePositions = getCirclePositions(stepsTextWidth);

        for (int i = 0; i < circlePositions.length; i++) {
            drawStep(canvas, i, halfFontHeightOffset, fontHeight,
                    circlePositions[i], y);
        }
        for (int i = 1; i < stepSize; i++) {
            drawLine(canvas, i, circlePositions[i - 1] + stepPadding + selectedCircleRadius,
                    circlePositions[i] - stepPadding - selectedCircleRadius, y);
        }
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
        result[textWidth.length - 1] = getWidth() - getPaddingRight() -
                Math.max(textWidth[textWidth.length - 1] / 2, selectedCircleRadius);
        if (result.length < 3) {
            return result;
        }
        int spaceLeft = result[textWidth.length - 1] - result[0];
        int margin = spaceLeft / (textWidth.length - 1);
        for (int i = 1; i < textWidth.length - 1; i++) {
            result[i] = result[i - 1] + i * margin;
        }
        return result;
    }

    private void drawStep(Canvas canvas, int step, int halfFontHeightOffset, int fontHeight,
                          int circleCenterX, int circleCenterY) {
        final String text = steps.get(step);
        final boolean isSelected = step == currentStep;
        final boolean isDone = step < currentStep;
        final String number = String.valueOf(step + 1);

        int textY = circleCenterY + selectedCircleRadius + textPadding + fontHeight / 2;

        if (isSelected) {
            paint.setColor(selectedCircleColor);
            canvas.drawCircle(circleCenterX, circleCenterY, selectedCircleRadius, paint);

            paint.setColor(selectedStepNumberColor);
            paint.setTextSize(stepNumberTextSize);
            canvas.drawText(number, circleCenterX, getStepNumberY(circleCenterY), paint);

            paint.setColor(selectedTextColor);
            paint.setTextSize(textSize);
            drawText(canvas, text, circleCenterX, textY, paint);
        } else if (isDone) {
            paint.setColor(doneCircleColor);
            canvas.drawCircle(circleCenterX, circleCenterY, doneCircleRadius, paint);

            drawCheckMark(canvas, circleCenterX, circleCenterY);

            paint.setColor(doneTextColor);
            paint.setTextSize(textSize);
            drawText(canvas, text, circleCenterX, textY, paint);
        } else {
            paint.setColor(nextTextColor);
            paint.setTextSize(stepNumberTextSize);
            canvas.drawText(number, circleCenterX, circleCenterY + halfFontHeightOffset, paint);

            paint.setTextSize(textSize);
            drawText(canvas, text, circleCenterX, textY, paint);
        }
    }

    private float getStepNumberY(int circleCenterY) {
        int fontSize = textHeight();
        return circleCenterY + fontSize / 2 - paint.descent();
    }

    private void drawText(Canvas canvas, String text, int x, int y, Paint paint) {
        String[] split = text.split("\\n");
        if (split.length == 1) {
            canvas.drawText(text, x, y, paint);
        } else {
            for (int i = 0; i < split.length; i++) {
                canvas.drawText(split[i], x, y + i * textHeight(), paint);
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

    private void drawLine(Canvas canvas, int step, int startX, int endX, int centerY) {
        final boolean isSelected = step == currentStep;
        final boolean isDone = step < currentStep;

        if (isSelected || isDone) {
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