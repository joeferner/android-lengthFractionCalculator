package com.fernsroth.lengthfractioncalculator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.View;

public class FractionView extends View {
	private Paint wholeNumberPaint;
	private float value;
	private int denominator = 128;
	private Paint fractionNumDenPaint;

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
		wholeNumberPaint.setColor(Color.argb(150, 0, 0, 0));

		fractionNumDenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		fractionNumDenPaint.setStrokeWidth(1.0f);
		fractionNumDenPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.fractionNumDenFontSize));
		fractionNumDenPaint.setColor(Color.argb(255, 0, 0, 0));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int wholeX, wholeY, numX, numY, denX, denY;
		String wholeNumberStr = String.format("%d", (int) FloatMath.floor(this.value));
		float rest = this.value - FloatMath.floor(this.value);
		String numeratorStr = String.format("%d", (int) (rest * this.denominator));
		String denominatorStr = String.format("%d", this.denominator);

		Rect wholeNumberBounds = new Rect();
		wholeNumberPaint.getTextBounds(wholeNumberStr, 0, wholeNumberStr.length(), wholeNumberBounds);

		Rect numBounds = new Rect();
		fractionNumDenPaint.getTextBounds(numeratorStr, 0, numeratorStr.length(), numBounds);

		Rect denBounds = new Rect();
		fractionNumDenPaint.getTextBounds(denominatorStr, 0, denominatorStr.length(), denBounds);

		int centerX = wholeNumberBounds.;
		
		wholeX = 0;
		wholeY = 0;
		numX = 0;
		numY = 0;
		denX = 0;
		denY = 0;
		
		CanvasUtils.drawHvAlignedText(canvas, wholeX, wholeY, wholeNumberStr, wholeNumberPaint, Paint.Align.RIGHT, CanvasUtils.TextVertAlign.Middle);
		CanvasUtils.drawHvAlignedText(canvas, numX, numY, numeratorStr, wholeNumberPaint, Paint.Align.CENTER, CanvasUtils.TextVertAlign.Bottom);
		CanvasUtils.drawHvAlignedText(canvas, denX, denY, denominatorStr, wholeNumberPaint, Paint.Align.CENTER, CanvasUtils.TextVertAlign.Top);
	}

	public void setValue(float value) {
		this.value = value;
		postInvalidate();
	}
}
