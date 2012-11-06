package com.fernsroth.lengthfractioncalculator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class CanvasUtils {
	public enum TextVertAlign {
		Top, Middle, Baseline, Bottom
	};

	public static void drawHvAlignedText(Canvas canvas, float x, float y, String s, Paint p, Paint.Align horizAlign, TextVertAlign vertAlign) {

		// Set horizontal alignment
		p.setTextAlign(horizAlign);

		// Get bounding rectangle - we need its attribute and method values
		Rect r = new Rect();
		p.getTextBounds(s, 0, s.length(), r); // Note: r.top will be negative

		// Compute y-coordinate we'll need for drawing text for specified
		// vertical alignment
		float textX = x;
		float textY = y;
		switch (vertAlign) {
		case Top:
			textY = y - r.top;
			break;
		case Middle:
			textY = y - r.top - r.height() / 2;
			break;
		case Baseline: // Default behavior - do nothing
			break;
		case Bottom:
			textY = y - (r.height() + r.top);
			break;
		}
		canvas.drawText(s, textX, textY, p);
	}
}
