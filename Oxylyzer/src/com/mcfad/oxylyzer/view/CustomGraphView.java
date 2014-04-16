package com.mcfad.oxylyzer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Align;
import android.util.Log;
import android.view.View;

import com.jjoe64.graphview.LineGraphView;


public class CustomGraphView extends LineGraphView {
	static final private class GraphViewConfig {
		static final float BORDER = 20;
	}
	public CustomGraphView(Context context, String title) {
		super(context, title);

		//DualVerLabelsView labels = new DualVerLabelsView(context, this);
		//addView(labels);
	}

	public class DualVerLabelsView extends View {

		protected final Paint paint;
		private CustomGraphView graphView;
		private String[] verlabels;
		private Integer labelTextHeight;
		private Integer verLabelTextWidth;
		private final Rect textBounds = new Rect();
		/**
		 * @param context
		 */
		public DualVerLabelsView(Context context,CustomGraphView graphView) {
			super(context);
			paint = new Paint();
			this.graphView = graphView;
			setLayoutParams(new LayoutParams(
					graphView.getGraphViewStyle().getVerticalLabelsWidth()==0?100:graphView.getGraphViewStyle().getVerticalLabelsWidth()
							, LayoutParams.FILL_PARENT));
		}

		/**
		 * @param canvas
		 */
		@Override
		protected void onDraw(Canvas canvas) {
			// normal
			paint.setStrokeWidth(0);

			// measure bottom text
			if (labelTextHeight == null || verLabelTextWidth == null) {
				paint.setTextSize(graphView.getGraphViewStyle().getTextSize());
				double testY = ((graphView.getMaxY()-graphView.getMinY())*0.783)+graphView.getMinY();
				String testLabel = graphView.formatLabel(testY, false);
				paint.getTextBounds(testLabel, 0, testLabel.length(), textBounds);
				labelTextHeight = (textBounds.height());
				verLabelTextWidth = (textBounds.width());
			}
			if (graphView.getGraphViewStyle().getVerticalLabelsWidth()==0 && getLayoutParams().width != verLabelTextWidth+GraphViewConfig.BORDER) {
				setLayoutParams(new LayoutParams(
						(int) (verLabelTextWidth+GraphViewConfig.BORDER), LayoutParams.FILL_PARENT));
			} else if (graphView.getGraphViewStyle().getVerticalLabelsWidth()!=0 && graphView.getGraphViewStyle().getVerticalLabelsWidth() != getLayoutParams().width) {
				setLayoutParams(new LayoutParams(
						graphView.getGraphViewStyle().getVerticalLabelsWidth(), LayoutParams.FILL_PARENT));
			}

			float border = GraphViewConfig.BORDER;
			border += labelTextHeight;
			float height = getHeight();
			float graphheight = height - (2 * border);

			if (verlabels == null) {
				verlabels = generateVerlabels(graphheight);
			}

			// vertical labels
			paint.setTextAlign(graphView.getGraphViewStyle().getVerticalLabelsAlign());
			int labelsWidth = getWidth();
			int labelsOffset = 0;
			if (graphView.getGraphViewStyle().getVerticalLabelsAlign() == Align.RIGHT) {
				labelsOffset = labelsWidth;
			} else if (graphView.getGraphViewStyle().getVerticalLabelsAlign() == Align.CENTER) {
				labelsOffset = labelsWidth / 2;
			}
			int vers = verlabels.length - 1;
			for (int i = 0; i < verlabels.length; i++) {
				float y = ((graphheight / vers) * i) + border;
				paint.setColor(graphView.graphViewStyle.getVerticalLabelsColor());
				//canvas.drawText(verlabels[i], labelsOffset, y, paint);
				canvas.drawText("test", labelsOffset, y, paint);
			}

			// reset
			paint.setTextAlign(Align.LEFT);
		}

		synchronized private String[] generateVerlabels(float graphheight) {
			int numLabels = getGraphViewStyle().getNumVerticalLabels()-1;
			if (numLabels < 0) {
				numLabels = (int) (graphheight/(labelTextHeight*3));
				if (numLabels == 0) {
					Log.w("GraphView", "Height of Graph is smaller than the label text height, so no vertical labels were shown!");
				}
			}
			String[] labels = new String[numLabels+1];
			double min = getMinY();
			double max = getMaxY();
			if (max == min) {
				// if min/max is the same, fake it so that we can render a line
				if(max == 0) {
					// if both are zero, change the values to prevent division by zero
					max = 1.0d;
					min = 0.0d;
				} else {
					max = max*1.05d;
					min = min*0.95d;
				}
			}

			for (int i=0; i<=numLabels; i++) {
				labels[numLabels-i] = formatLabel(min + ((max-min)*i/numLabels), false);
			}
			return labels;
		}
	}
}
