package com.watertracker.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

public class CircularProgressView extends View {

    private Paint backgroundPaint;
    private Paint progressPaint;
    private RectF rect;
    private float progress = 0f;
    private int strokeWidth = 20;

    public CircularProgressView(Context context) {
        super(context);
        init();
    }

    public CircularProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(strokeWidth);
        backgroundPaint.setColor(ContextCompat.getColor(getContext(), R.color.progress_background));
        backgroundPaint.setStrokeCap(Paint.Cap.ROUND);

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidth);
        progressPaint.setColor(ContextCompat.getColor(getContext(), R.color.progress_color));
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        rect = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int size = Math.min(w, h);
        int padding = strokeWidth / 2;

        rect.set(padding, padding, size - padding, size - padding);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制背景圆环
        canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 2, backgroundPaint);

        // 绘制进度圆弧
        if (progress > 0) {
            float sweepAngle = 360 * progress;
            canvas.drawArc(rect, -90, sweepAngle, false, progressPaint);
        }
    }

    public void setProgress(float progress) {
        this.progress = Math.max(0, Math.min(1, progress));
        invalidate();
    }

    public float getProgress() {
        return progress;
    }
}