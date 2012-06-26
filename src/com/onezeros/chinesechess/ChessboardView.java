package com.onezeros.chinesechess;

import com.android.chinesechess.R;
import com.onezeros.chinesechess.AI.Move;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

public class ChessboardView extends ImageView{
	static final int USER_COLOR = AI.LIGHT;
	static final int MSG_USER_MOVE_DONE = 0;
	static final int MSG_COMPUTER_MOVE_DONE = 1;
	
	Bitmap[][] mChessBitmaps = new Bitmap[2][7];
	Bitmap mSelectBitmap = null;
	float mLaticeLen = -1 ;
	float mLaticeLen2;
	float mChesslen ;
	float mChessLen2;
	int mChessFrom = -1;
	int mChessTo = -1;
	
	AI mAi = new AI();
	MessageHandler mMessageHandler = new MessageHandler();

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
	private int canvasCoord2ChessIndex(PointF point) {
		Point logicPoint = new Point((int)((point.x - mLaticeLen2)/mLaticeLen), (int)((point.y - mLaticeLen2)/mLaticeLen));
		int index =logicPoint.x + logicPoint.y * 9;
		if (index >= AI.BOARD_SIZE || index < 0) {
			return -1;
		}
		return index;
	}
	private PointF chessIndex2CanvasCoord(int i) {
		PointF point = new PointF(chessIndex2LogicPoint(i));
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
				mChessBitmaps[AI.LIGHT][i] = Bitmap.createBitmap(chessBitmap, i * stepW, 0, stepW, stepH);
				mChessBitmaps[AI.DARK][i] = Bitmap.createBitmap(chessBitmap, (i+7) * stepW, 0, stepW, stepH);
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
	        
	        mSelectBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sel);
		}

		// draw each chess
		for(int i =0 ; i < AI.BOARD_SIZE ; i++) {
			if (mAi.piece[i] != AI.EMPTY) {
				PointF point = chessIndex2CanvasCoord(i);
				Bitmap bmp = mChessBitmaps[mAi.color[i]][mAi.piece[i]];
				canvas.drawBitmap(bmp, null,new RectF(point.x - mChessLen2, point.y - mChessLen2, point.x + mChessLen2, point.y + mChessLen2), null);
			}
		}

		// draw selected positions
		if (mChessFrom >=0 ) {
			PointF point = chessIndex2CanvasCoord(mChessFrom);
			canvas.drawBitmap(mSelectBitmap, null,new RectF(point.x - mChessLen2, point.y - mChessLen2, point.x + mChessLen2, point.y + mChessLen2), null);
		}
		if (mChessTo >=0 ) {
			PointF point = chessIndex2CanvasCoord(mChessTo);
			canvas.drawBitmap(mSelectBitmap, null,new RectF(point.x - mChessLen2, point.y - mChessLen2, point.x + mChessLen2, point.y + mChessLen2), null);
		}
		super.onDraw(canvas);
	}

	void newGame() {
		mAi.init();
		mChessFrom = -1;
		mChessTo = -1;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			final int chess = canvasCoord2ChessIndex(new PointF(event.getX(),event.getY()));
			Log.d("lzj", "chess index clicked : "+ chess);
			if (chess >0 ) {
				if (mChessFrom >= 0 && mChessTo >= 0 || mChessFrom < 0) {
					if (mAi.color[chess] == USER_COLOR) {
						mChessFrom = chess;
						mChessTo = -1;
					}
				}else if( mChessFrom >= 0 && mChessTo < 0 && mAi.color[chess] == USER_COLOR){
					mChessFrom = chess;
				}else if (mChessTo < 0) {
					Log.d("lzj", "second click, from: " + mChessFrom + ", to: "+chess);
					//human move
//					new Thread(new Runnable() {
//						
//						public void run() {
							int ret = mAi.takeAMove(mChessFrom, chess);
							if (ret == AI.MOVE_OK) {
								Log.d("lzj", "human move ok");
								mChessTo = chess;
//								Message msg = mMessageHandler.obtainMessage(MSG_USER_MOVE_DONE);
//								mMessageHandler.sendMessage(msg);
								invalidate();
								
								// computer move 
								ret = mAi.computerMove();
								Move move = mAi.getComputerMove();
								mChessFrom = move.from;
								mChessTo = move.dest;
								invalidate();
//								msg = mMessageHandler.obtainMessage(MSG_COMPUTER_MOVE_DONE);
//								mMessageHandler.sendMessage(msg);
							}else if (ret == AI.MOVE_WIN) {
								// show dialog
								
							}
							Log.d("lzj","takeAMove ret : " + ret);
							
//						}
//					}).start();
				}
				postInvalidate();
			}
		}
		return super.onTouchEvent(event);
	}
	class MessageHandler extends Handler {
		public void handleMessage(Message msg){
    		Log.d("lzj", "message hander : msg.what = " + msg.what);
    		switch (msg.what) {
    		case MSG_USER_MOVE_DONE:
				postInvalidate();
    			break;
    		case MSG_COMPUTER_MOVE_DONE:
    			postInvalidate();
    			break;    			
			default:
				break;
			}
    	}
	}
}
