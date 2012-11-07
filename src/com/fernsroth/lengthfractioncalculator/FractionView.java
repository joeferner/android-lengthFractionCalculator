package com.fernsroth.lengthfractioncalculator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.fernsroth.lengthfractioncalculator.Fraction.RoundingDirection;

public class FractionView extends View {
  private Paint wholeNumberPaint;
  private Paint fractionNumDenPaint;
  private int margin;
  private Fraction value = new Fraction();
  private Rect wholeNumberBounds = new Rect();
  private Rect numBounds = new Rect();
  private Rect denBounds = new Rect();
  private Rect errorBounds = new Rect();
  private Paint errorNegPaint;
  private Paint errorPosPaint;

  public FractionView(Context context) {
    super(context);
    initView();
  }

  public FractionView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initView();
  }

  public FractionView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    initView();
  }

  private void initView() {
    if (wholeNumberPaint != null) {
      return;
    }

    wholeNumberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    wholeNumberPaint.setStrokeWidth(1.0f);
    wholeNumberPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.fractionWholeFontSize));
    wholeNumberPaint.setColor(Color.argb(255, 0, 0, 0));

    fractionNumDenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    fractionNumDenPaint.setStrokeWidth(1.0f);
    fractionNumDenPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.fractionNumDenFontSize));
    fractionNumDenPaint.setColor(Color.argb(255, 0, 0, 0));

    errorNegPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    errorNegPaint.setStrokeWidth(1.0f);
    errorNegPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.fractionErrorFontSize));
    errorNegPaint.setColor(Color.argb(255, 175, 0, 0));

    errorPosPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    errorPosPaint.setStrokeWidth(1.0f);
    errorPosPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.fractionErrorFontSize));
    errorPosPaint.setColor(Color.argb(255, 0, 175, 0));

    margin = getResources().getDimensionPixelSize(R.dimen.fractionMargin);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    int wholeX, wholeY;
    int marginX = margin;
    int marginY = margin;

    String wholeNumberStr;
    String numeratorStr;
    String denominatorStr;
    String errorStr;

    wholeNumberStr = String.format("%d", this.value.getWholeNumber());
    numeratorStr = String.format("%d", this.value.getNumerator());
    denominatorStr = String.format("%d", this.value.getDenominator());

    errorStr = String.format("%.3f\"", this.value.getError());

    if (this.value.getNumerator() == 0) {
      numeratorStr = "";
      denominatorStr = "";
    } else if (this.value.getWholeNumber() == 0) {
      wholeNumberStr = "";
    }

    wholeNumberBounds.setEmpty();
    if (wholeNumberStr.length() > 0) {
      wholeNumberPaint.getTextBounds(wholeNumberStr, 0, wholeNumberStr.length(), wholeNumberBounds);
      wholeNumberBounds.inset(-marginX, -marginY);
      wholeNumberBounds.offsetTo(0, 0);
    }

    errorBounds.setEmpty();
    if (errorStr.length() > 0) {
      errorNegPaint.getTextBounds(errorStr, 0, errorStr.length(), errorBounds);
      errorBounds.inset(-marginX, -marginY);
      errorBounds.offsetTo(0, 0);
    }

    numBounds.setEmpty();
    denBounds.setEmpty();
    if (numeratorStr.length() > 0) {
      fractionNumDenPaint.getTextBounds(numeratorStr, 0, numeratorStr.length(), numBounds);
      numBounds.inset(-marginX, -marginY);
      numBounds.offsetTo(0, 0);

      fractionNumDenPaint.getTextBounds(denominatorStr, 0, denominatorStr.length(), denBounds);
      denBounds.inset(-marginX, -marginY);
      denBounds.offsetTo(0, 0);
    }

    int totalWidth = wholeNumberBounds.width() + Math.max(numBounds.width(), denBounds.width());

    wholeX = (getWidth() - totalWidth) / 2;
    wholeY = marginY;

    numBounds.offset(wholeX + wholeNumberBounds.width(), wholeY);
    denBounds.offset(wholeX + wholeNumberBounds.width(), wholeY + numBounds.height());
    wholeNumberBounds.offset(wholeX, numBounds.top);
    wholeNumberBounds.bottom = denBounds.bottom;

    errorBounds.offset(0, Math.max(denBounds.bottom, wholeNumberBounds.bottom));

    numBounds.right = Math.max(numBounds.right, denBounds.right);
    denBounds.right = Math.max(numBounds.right, denBounds.right);

    CanvasUtils.drawHvAlignedText(canvas, wholeNumberBounds.left, wholeNumberBounds.centerY(), wholeNumberStr, wholeNumberPaint, Paint.Align.LEFT, CanvasUtils.TextVertAlign.Middle);
    if (numeratorStr.length() > 0) {
      CanvasUtils.drawHvAlignedText(canvas, numBounds.centerX(), numBounds.top, numeratorStr, fractionNumDenPaint, Paint.Align.CENTER, CanvasUtils.TextVertAlign.Top);
      CanvasUtils.drawHvAlignedText(canvas, denBounds.centerX(), denBounds.top, denominatorStr, fractionNumDenPaint, Paint.Align.CENTER, CanvasUtils.TextVertAlign.Top);
      canvas.drawLine(numBounds.left, numBounds.bottom - marginY, numBounds.right, numBounds.bottom - marginY, fractionNumDenPaint);
    }
    if (Math.abs(this.value.getError()) >= 0.001) {
      Paint paint = this.value.getError() > 0 ? errorPosPaint : errorNegPaint;
      CanvasUtils.drawHvAlignedText(canvas, getWidth() / 2, errorBounds.top, errorStr, paint, Paint.Align.CENTER, CanvasUtils.TextVertAlign.Top);
    }
  }

  public void setValue(float value) {
    this.value.setValue(value);
    postInvalidate();
  }

  public void setRoundingDirection(RoundingDirection roundingDirection) {
    this.value.setRoundingDirection(roundingDirection);
  }

  public void setMaximumDenominator(int maxDenominator) {
    this.value.setMaximumDenominator(maxDenominator);
    postInvalidate();
  }

  public int getMaximumDenominator() {
    return this.value.getMaximumDenominator();
  }
}
