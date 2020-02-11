package com.example.stepprogressbar;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;


import java.util.ArrayList;
import java.util.List;

public class StepView extends View {
    private static final int DEFAULT_STEP_CIRCLE_RADIUS_IN_DP = 15;
    private static final int START_STEP = 0;
    private static  final int DEFAULT_LINE_STROKE_WIDTH_IN_PX = 5;
    private float halfRectLengthInPx;
    private int totalSteps;
    private ArrayList<Drawable> inCompleteStateDrawables;
    private ArrayList<Drawable> inProgressStateDrawables;
    private float[] centersX;
    private float centersY;
    private float[] startLinesX;
    private float[] endLinesX;
    private Drawable completedIcon;
    private int currentStep = START_STEP;
    private Paint dashedLinePaint;
    private Paint solidLinePaint;

    public StepView(Context context) {
        this(context,null,0);
    }

    public StepView(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public StepView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context,attrs,defStyleAttr);
    }

    public StepView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeParameters(context, attrs);
        initializePaint();
    }

    private static float dpToPx(final Context context, final float value) {
        return value * context.getResources().getDisplayMetrics().density;
    }

    public void initializeParameters(Context context, AttributeSet attributeSet) {
        inCompleteStateDrawables = new ArrayList<>();
        inProgressStateDrawables = new ArrayList<>();
        TypedArray attributeArray = context.obtainStyledAttributes(attributeSet, R.styleable.StepView);
        halfRectLengthInPx = dpToPx(context, DEFAULT_STEP_CIRCLE_RADIUS_IN_DP);
        completedIcon = attributeArray.getDrawable(R.styleable.StepView_completed_state_icon);
        int inCompleteStateDrawablesResId = attributeArray.getResourceId(R.styleable.StepView_in_complete_state_icons, 0);

        if (inCompleteStateDrawablesResId != 0) {
            TypedArray drawableArray = getResources().obtainTypedArray(inCompleteStateDrawablesResId);
            for (int i = 0; i < drawableArray.length(); i++) {
                inCompleteStateDrawables.add(ContextCompat.getDrawable(context, drawableArray.getResourceId(i, 0)));
            }
            drawableArray.recycle();
        }

        int inProgressStateDrawablesResId = attributeArray.getResourceId(R.styleable.StepView_in_progress_state_icons, 0);

        if (inProgressStateDrawablesResId != 0) {
            TypedArray drawableArray = getResources().obtainTypedArray(inProgressStateDrawablesResId);
            for (int i = 0; i < drawableArray.length(); i++) {
                inProgressStateDrawables.add(ContextCompat.getDrawable(context, drawableArray.getResourceId(i, 0)));
            }
            drawableArray.recycle();
        }

        attributeArray.recycle();
    }


    public void initializePaint(){
        //Paint object for dashed line
        dashedLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dashedLinePaint.setStyle(Paint.Style.STROKE);
        dashedLinePaint.setColor(Color.WHITE);
        dashedLinePaint.setStrokeWidth(DEFAULT_LINE_STROKE_WIDTH_IN_PX);
        float[] intervals = new float[]{10,5};
        float phase = 0;
        dashedLinePaint.setPathEffect(new DashPathEffect(intervals,phase));

        //Paint object for solid line
        solidLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        solidLinePaint.setColor(Color.WHITE);
        solidLinePaint.setStyle(Paint.Style.STROKE);
        solidLinePaint.setStrokeWidth(DEFAULT_LINE_STROKE_WIDTH_IN_PX);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if((inCompleteStateDrawables == null) || (inProgressStateDrawables == null)){
            return;
        }
        if(inCompleteStateDrawables.size() == inProgressStateDrawables.size()){
            totalSteps = inCompleteStateDrawables.size();
        }else{
            throw new IllegalArgumentException("InComplete state drawable array and InProgress state drawable array must have same size");
        }

        int measuredWidth = measureWidth(widthMeasureSpec);
        int measuredHeight = measureHeight(heightMeasureSpec);
        setMeasuredDimension(measuredWidth, measuredHeight);
        measureAttributes();
    }


    private int measureHeight(int heightMeasureSpec) {
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        int result;
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = (int) ((halfRectLengthInPx * 2) + getPaddingBottom() + getPaddingTop());
        }
        return result;
    }

    private int measureWidth(int widthMeasureSpec) {
        return MeasureSpec.getSize(widthMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getHeight() == 0) {
            return;
        }
        if (getStepCount() == 0) {
            return;
        }

        for (int i = 0; i < getStepCount(); i++) {
            drawStep(canvas, i, centersX[i], centersY);
        }

        for (int i = 0; i < startLinesX.length; i++){
            if(i < currentStep) {
                drawLine(canvas, startLinesX[i], endLinesX[i], centersY,false);
            }else{
                drawLine(canvas, startLinesX[i], endLinesX[i], centersY,true);

            }
        }
    }

    private void measureAttributes() {
        centersX = getCenterPositionsInX();
        centersY = getCenterPositionInY();
        startLinesX = getStartLinesPositionsInX();
        endLinesX = getEndLinesPositionsInX();
    }

    private void drawStep(Canvas canvas, int step, float centerX, float centerY) {
        int left = (int) (centerX - halfRectLengthInPx);
        int top = (int) (centerY - halfRectLengthInPx);
        int right = (int) (centerX + halfRectLengthInPx);
        int bottom = (int) (centerY + halfRectLengthInPx);
        Rect rect = new Rect(left, top, right, bottom);
        if(step < currentStep){
           completedIcon.setBounds(rect);
           completedIcon.draw(canvas);
        }else if(step == currentStep){
            Drawable stepDrawable = DrawableCompat.wrap(inProgressStateDrawables.get(step));
            stepDrawable.setBounds(rect);
            stepDrawable.draw(canvas);
        }else if(step > currentStep){
            Drawable stepDrawable = DrawableCompat.wrap(inCompleteStateDrawables.get(step));
            stepDrawable.setBounds(rect);
            stepDrawable.draw(canvas);
        }
    }



    private void drawLine(Canvas canvas, float startX,float endX, float centerY, boolean isDashed){
        if(isDashed) {
            canvas.drawLine(startX, centerY, endX, centerY, dashedLinePaint);
        }else{
            canvas.drawLine(startX, centerY, endX, centerY, solidLinePaint);

        }
    }

    public int getStepCount() {
        return totalSteps;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    //The center positions are stored in array for x axis
    private float[] getCenterPositionsInX() {
        int stepCount = getStepCount();
        float[] result = new float[stepCount];

        if (result.length == 0) {
            return result;
        }

        result[0] = getStartCenterPosition();

        if (result.length == 1) {
            return result;
        }

        result[stepCount - 1] = getEndCenterPosition();

        if (result.length < 3) {
            return result;
        }

        float spaceLeft = result[stepCount - 1] - result[0];
        int margin = (int) (spaceLeft / (stepCount - 1));

        for (int i = 1; i < stepCount - 1; i++) {
            result[i] = result[i - 1] + margin;
        }

        return result;
    }

    private float getStartCenterPosition() {
        return getPaddingStart() + halfRectLengthInPx;
    }

    private float getEndCenterPosition() {
        return getMeasuredWidth() - getPaddingRight() - halfRectLengthInPx;
    }

    //The position of center points in Y axis
    private float getCenterPositionInY() {
        float availableHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        return availableHeight / 2;
    }

    private float[] getStartLinesPositionsInX() {
        float[] result = new float[getStepCount() - 1];
        for (int i = 1; i < getStepCount(); i++) {
            result[i - 1] = centersX[i - 1] + halfRectLengthInPx;
        }
        return result;
    }


    private float[] getEndLinesPositionsInX() {
        float[] result = new float[getStepCount() - 1];
        for (int i = 1; i < getStepCount(); i++) {
            result[i - 1] = centersX[i] - halfRectLengthInPx;
        }
        return result;
    }

    public void setInProgressStateDrawables(List<Drawable> drawables){
            inProgressStateDrawables.clear();
            inProgressStateDrawables.addAll(drawables);
            invalidate();
            requestLayout();
    }


    public void setCompleteStateIcon(Drawable drawable){
        completedIcon = drawable;
        invalidate();
        requestLayout();
    }

    public void setInCompleteStateDrawables(List<Drawable> drawables){
            inCompleteStateDrawables.clear();
            inCompleteStateDrawables.addAll(drawables);
            invalidate();
            requestLayout();
    }


    public void setCurrentStep(int currentStep){
        int step = currentStep - 1;
        if(currentStep < 0 || currentStep > getStepCount()){
            throw new IllegalArgumentException("Invalid Step value "+ currentStep);
        }
        this.currentStep = step;
        invalidate();
    }

    public void reset(){
        this.currentStep = START_STEP;
        invalidate();
    }

    public void done(){
        this.currentStep = getStepCount();
        invalidate();
    }

}



