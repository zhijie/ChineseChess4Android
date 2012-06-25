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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class ChessboardView extends View{
	Bitmap[][] mChessBitmaps = new Bitmap[2][7];
	int mLaticeLen = -1 ;
	int mLaticeLen2;
	int mChesslen ;
	int mChessLen2;
	AI mAi = new AI();


	public ChessboardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ChessboardView(Context context) {
		super(context);
		init(context);
	}

	void init(Context context){
		mAi.init();
	}
	
	private Point chessIndex2CanvasCoord(int i) {
		Point point = chessIndex2LogicPoint(i);
		point.x *= mLaticeLen ;
		point.x += mLaticeLen2;
		point.y *= mLaticeLen ;
		point.y += mLaticeLen2;
		return point;
	}
	private Point chessIndex2LogicPoint(int i) {
		return new Point(i%9,i/9);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if (mLaticeLen <0) {

			mLaticeLen = getWidth()/9;
			mChesslen = mLaticeLen * 19  / 20;
			mLaticeLen2 = mLaticeLen/2;
			mChessLen2 = mChesslen /2;
			
	        // load chess images
	        Bitmap chessBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.qz);
	        int stepH = chessBitmap.getHeight() / 3;
	        int stepW = chessBitmap.getWidth() / 14;
	        for (int i = 0; i < 7; i++) {
				mChessBitmaps[0][i] = Bitmap.createBitmap(chessBitmap, i * stepW, 0, stepW, stepH);
				mChessBitmaps[1][i] = Bitmap.createBitmap(chessBitmap, (i+7) * stepW, 0, stepW, stepH);
			}
	        chessBitmap = mChessBitmaps[0][0];
	        mChessBitmaps[0][0] = mChessBitmaps[0][6];
	        mChessBitmaps[0][6] = chessBitmap;
	        chessBitmap = mChessBitmaps[1][0];
	        mChessBitmaps[1][0] = mChessBitmaps[1][6];
	        mChessBitmaps[1][6] = chessBitmap;
	        chessBitmap = mChessBitmaps[0][4];
	        mChessBitmaps[0][4] = mChessBitmaps[0][5];
	        mChessBitmaps[0][5] = chessBitmap;
	        chessBitmap = mChessBitmaps[1][4];
	        mChessBitmaps[1][4] = mChessBitmaps[1][5];
	        mChessBitmaps[1][5] = chessBitmap;
		}
		// draw each chess
		for(int i =0 ; i < AI.BOARD_SIZE ; i++) {
			if (mAi.piece[i] != AI.EMPTY) {
				Point point = chessIndex2CanvasCoord(i);
				Bitmap bmp = mChessBitmaps[mAi.color[i]][mAi.piece[i]];
				canvas.drawBitmap(bmp, null,new Rect(point.x - mChessLen2, point.y - mChessLen2, point.x + mChessLen2, point.y + mChessLen2), null);
			}
		}
		super.onDraw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(event);
	}
}
