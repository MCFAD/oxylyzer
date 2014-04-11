package com.mcfad.oxylyzer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class VerticalBar extends View {
	protected float currentVal;
	protected float maxVal;

	protected Paint shapePaint = null;
	protected Rect rect = null;

	public VerticalBar(Context context) {
		super(context);
		initShape();
	}
	public VerticalBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initShape();
	}
	public VerticalBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		initShape();
	}
	protected void initShape(){
		shapePaint = new Paint();
		shapePaint.setAntiAlias(true);
		shapePaint.setDither(true);
		shapePaint.setStyle(Style.STROKE);
		shapePaint.setStrokeWidth(30);
		shapePaint.setColor(Color.RED);
		rect = new Rect();
	}

	public void setMax(float max){
		maxVal = max;
		invalidate();
	}
	public void setCurrentVal(float val){
		currentVal = val;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.getClipBounds(rect);
		float ratio = Math.min(1.0f, currentVal/maxVal); 
		int x = (rect.left+rect.right)/2;
		int y = rect.bottom;
		int height = (int) ((rect.top-rect.bottom)*ratio);
		canvas.drawLine(x,y,x,y+height*ratio,shapePaint);
	}
}
