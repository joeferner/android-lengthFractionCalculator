package com.fernsroth.lengthfractioncalculator;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

public class RulerView extends View {

  private static final float PIXELS_PER_INCH = 1000.0f;
  private static final float PIXELS_PER_MILLIMETER = PIXELS_PER_INCH / 25.4f;
  private static final float MILLIMETERS_PER_INCH = 25.4f;
  private ArrayList<OnRulerScrollListener> onRulerScrollListeners = new ArrayList<OnRulerScrollListener>();
  private Paint centerLinePaint;
  private GestureDetector gestureDetector;
  private Scroller scroller;
  private float nextX;
  private int maxX;
  private float currentX;
  private Paint tickPaint;
  private int width;
  private int height;
  private Paint tickTextPaint;
  private Runnable requestLayoutRunnable = new Runnable() {
    public void run() {
      requestLayout();
    }
  };

  public RulerView(Context context) {
    super(context);
    initView();
  }

  public RulerView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initView();
  }

  public RulerView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    initView();
  }

  private void initView() {
    if (centerLinePaint != null) {
      return;
    }

    centerLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    centerLinePaint.setStrokeWidth(1.0f);
    centerLinePaint.setColor(Color.argb(150, 255, 0, 0));

    tickPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    tickPaint.setStrokeWidth(1.0f);
    tickPaint.setColor(Color.argb(255, 0, 0, 0));

    tickTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    tickTextPaint.setStrokeWidth(1.0f);
    tickTextPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.tickFontSize));
    tickTextPaint.setColor(Color.argb(255, 0, 0, 0));

    nextX = 0;
    currentX = 0;
    maxX = Integer.MAX_VALUE;

    scroller = new Scroller(getContext());
    gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
      @Override
      public boolean onDown(MotionEvent e) {
        return RulerView.this.onDown(e);
      }

      @Override
      public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return RulerView.this.onFling(e1, e2, velocityX, velocityY);
      }

      @Override
      public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        synchronized (RulerView.this) {
          nextX += (int) distanceX;
        }
        requestLayout();

        return true;
      }
    });
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent event) {
    boolean handled = super.dispatchTouchEvent(event);
    handled |= gestureDetector.onTouchEvent(event);
    return handled;
  }

  protected boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    synchronized (this) {
      scroller.fling((int) nextX, 0, (int) -velocityX, 0, 0, maxX, 0, 0);
    }
    requestLayout();

    return true;
  }

  protected boolean onDown(MotionEvent e) {
    scroller.forceFinished(true);
    return true;
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);

    if (scroller.computeScrollOffset()) {
      int scrollx = scroller.getCurrX();
      nextX = scrollx;
    }

    if (nextX <= 0) {
      nextX = 0;
      scroller.forceFinished(true);
    }
    if (nextX >= maxX) {
      nextX = maxX;
      scroller.forceFinished(true);
    }

    currentX = nextX;
    notifyOnRulerScrollListeners();

    if (!scroller.isFinished()) {
      post(requestLayoutRunnable);
    } else {
      postInvalidate();
    }
  }

  private void notifyOnRulerScrollListeners() {
    for (OnRulerScrollListener listener : onRulerScrollListeners) {
      listener.onRulerScroll();
    }
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    width = getWidth();
    height = getHeight();
    int centerX = width / 2;
    int centerY = height / 2;
    int minor1TickHeight = height / 5;
    int minor2TickHeight = height / 7;
    int minor4TickHeight = height / 9;
    int minor8TickHeight = height / 12;
    int minor16TickHeight = height / 16;
    int minor32TickHeight = height / 22;
    int minor64TickHeight = height / 30;
    int minor128TickHeight = height / 40;
    int minorMillimeterTickHeight = height / 22;
    int minorCentimeterTickHeight = height / 7;
    int scrollLeftX = (int) (currentX - centerX);
    float leftInchesX = FloatMath.floor(getInches(scrollLeftX) * 128.0f) / 128.0f;
    float leftMillimetersX = FloatMath.floor(getMillimeters(scrollLeftX));

    for (float in = leftInchesX;; in += 1.0f / 128.0f) {
      if (in < 0.0f) {
        continue;
      }
      int x = getXFromInches(in);
      if (x > width) {
        break;
      }

      if (isRound(in)) {
        canvas.drawLine(x, centerY, x, centerY - minor1TickHeight, tickPaint);
        CanvasUtils.drawHvAlignedText(canvas, x + 4, centerY - minor1TickHeight, String.format("%d", (int) in), tickTextPaint, Paint.Align.LEFT, CanvasUtils.TextVertAlign.Top);
      } else if (isRound(in / (1.0f / 2.0f))) {
        canvas.drawLine(x, centerY, x, centerY - minor2TickHeight, tickPaint);
      } else if (isRound(in / (1.0f / 4.0f))) {
        canvas.drawLine(x, centerY, x, centerY - minor4TickHeight, tickPaint);
      } else if (isRound(in / (1.0f / 8.0f))) {
        canvas.drawLine(x, centerY, x, centerY - minor8TickHeight, tickPaint);
      } else if (isRound(in / (1.0f / 16.0f))) {
        canvas.drawLine(x, centerY, x, centerY - minor16TickHeight, tickPaint);
      } else if (isRound(in / (1.0f / 32.0f))) {
        canvas.drawLine(x, centerY, x, centerY - minor32TickHeight, tickPaint);
      } else if (isRound(in / (1.0f / 64.0f))) {
        canvas.drawLine(x, centerY, x, centerY - minor64TickHeight, tickPaint);
      } else {
        canvas.drawLine(x, centerY, x, centerY - minor128TickHeight, tickPaint);
      }
    }

    for (float mm = leftMillimetersX;; mm += 1.0f) {
      if (mm < 0.0f) {
        continue;
      }
      int x = getXFromMillimeters(mm);
      if (x > width) {
        break;
      }

      if (isRound(mm / 10.0f)) {
        canvas.drawLine(x, centerY + 2, x, centerY + minorCentimeterTickHeight, tickPaint);
        CanvasUtils.drawHvAlignedText(canvas, x + 4, centerY + minorCentimeterTickHeight, String.format("%dmm", (int) mm), tickTextPaint, Paint.Align.LEFT, CanvasUtils.TextVertAlign.Baseline);
      } else {
        canvas.drawLine(x, centerY + 2, x, centerY + minorMillimeterTickHeight, tickPaint);
      }
    }

    canvas.drawLine(centerX, 0, centerX, height, centerLinePaint);
  }

  private boolean isRound(float f) {
    return Math.abs(FloatMath.floor(f) - f) < 0.001f;
  }

  private int getXFromInches(float inches) {
    int centerX = width / 2;
    int scrollLeftX = (int) (currentX - centerX);
    return (int) (inches * PIXELS_PER_INCH) - scrollLeftX;
  }

  public float getInches() {
    return getInches(currentX);
  }

  public float getInches(float x) {
    return x / PIXELS_PER_INCH;
  }

  public void setInches(float inches) {
    nextX = currentX = inches * PIXELS_PER_INCH;
    notifyOnRulerScrollListeners();
    postInvalidate();
  }

  private int getXFromMillimeters(float mm) {
    int centerX = width / 2;
    int scrollLeftX = (int) (currentX - centerX);
    return (int) (mm * PIXELS_PER_MILLIMETER) - scrollLeftX;
  }

  public float getMillimeters() {
    return getInches() * MILLIMETERS_PER_INCH;
  }

  public float getMillimeters(float x) {
    return getInches(x) * MILLIMETERS_PER_INCH;
  }

  public void setMillimeters(float mm) {
    setInches(mm / MILLIMETERS_PER_INCH);
  }

  public void addOnRulerScrollListener(OnRulerScrollListener listener) {
    onRulerScrollListeners.add(listener);
  }
}
