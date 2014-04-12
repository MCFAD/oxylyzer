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

	protected Paint barPaint = null;
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
		barPaint = new Paint();
		barPaint.setAntiAlias(true);
		barPaint.setDither(true);
		barPaint.setStyle(Style.STROKE);
		barPaint.setStrokeWidth(30);
		barPaint.setColor(Color.RED);
	}

	public void setMaxVal(float max){
		maxVal = max;
		invalidate();
	}
	public void setCurrentVal(float val){
		currentVal = val;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if(rect==null){
			rect = new Rect();
			canvas.getClipBounds(rect);
		}
		float ratio = Math.min(1.0f, currentVal/maxVal); 
		int x = (rect.left+rect.right)/2;
		int y = rect.bottom;
		int height = (int) ((rect.top-rect.bottom)*ratio);
		canvas.drawLine(x,y,x,y+height*ratio,barPaint);
	}
}
