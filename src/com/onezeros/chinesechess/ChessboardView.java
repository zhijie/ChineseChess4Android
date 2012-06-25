package com.onezeros.chinesechess;

import com.android.chinesechess.R;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class ChessboardView extends ImageView{
	Bitmap[][] mChessBitmaps = new Bitmap[2][7];
	int mLaticeLen ;
	int mLaticeLen2;
	int mChesslen ;
	int mChessLen2;
	AI mAi = new AI();
	
	public ChessboardView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public ChessboardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ChessboardView(Context context) {
		super(context);
		init(context);
	}

	void init(Context context){
		mLaticeLen = getWidth()/9;
		mChesslen = mLaticeLen * 19  / 20;
		mLaticeLen2 = mLaticeLen/2;
		mChessLen2 = mChesslen /2;
		
        // load chess images
        Bitmap chessBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.qp1);
        int stepH = chessBitmap.getHeight() / 3;
        int stepW = chessBitmap.getWidth() / 14;
        for (int i = 0; i < 7; i++) {
			mChessBitmaps[0][i] = Bitmap.createBitmap(chessBitmap, i * stepW, 0, stepW, stepH);
			mChessBitmaps[1][i] = Bitmap.createBitmap(chessBitmap, i * stepW, 0, stepW, stepH);
		}
	}
	private Point chessIndex2CanvasCoord(int i) {
		Point point = chessIndex2LogicPoint(i);
		point.x *= mLaticeLen + mLaticeLen2;
		point.y *= mLaticeLen + mLaticeLen2;
		return point;
	}
	private Point chessIndex2LogicPoint(int i) {
		return new Point(i%9,i/9);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// draw each chess
//		for(int i =0 ; i < 90 ; i++) {
//			if (mAi.piece[i] != mAi.EMPTY) {
//				Point point = chessIndex2CanvasCoord(i);
//				Bitmap bmp = mChessBitmaps[mAi.color[i]][mAi.piece[i]];
//				canvas.drawBitmap(bmp, new Rect(0,0,bmp.getWidth(),bmp.getHeight()), 
//						new Rect(point.x - mChessLen2, point.y - mChessLen2, mChesslen,mChesslen), new Paint());
//			}
//		}
		super.onDraw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(event);
	}
}
