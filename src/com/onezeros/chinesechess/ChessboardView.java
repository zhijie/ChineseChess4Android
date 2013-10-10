/*
 * ChineseChess4Android
 *
 * Copyright (c) 2012 Zhijie Lee
 *
 * The MIT License (MIT)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.onezeros.chinesechess;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.android.chinesechess.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TextView;

public class ChessboardView extends ImageView{
	static final int USER_COLOR = AI.LIGHT;
	static final int MSG_USER_MOVE_DONE = 0;
	static final int MSG_COMPUTER_MOVE_DONE = 1;
	static final int MSG_COMPUTER_WIN = 2;
	static final int MSG_USER_WIN = 3;
	static final String SAVE_STATE_FILE_NAME = "bundledata.txt";
	
	Bitmap[][] mChessBitmaps = new Bitmap[2][7];
	Bitmap mSelectBitmap = null;
	float mLaticeLen = -1 ;
	float mLaticeLen2;
	float mChesslen ;
	float mChessLen2;
	int mChessFrom = -1;
	int mChessTo = -1;
	float mStartBoardX;
	float mStartBoardY;
	boolean mIsComputerThinking =false;
	// back up of ai status for drawing
	int[] mPieces = new int[AI.BOARD_SIZE];
	int[] mColors = new int[AI.BOARD_SIZE];
	
	AI mAi = new AI();
	MessageHandler mMessageHandler = new MessageHandler();
	Context mContext;
	AlertDialog.Builder mAlertDialogBuilder;
	TextView mInfoTextView;
	
	
	public ChessboardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ChessboardView(Context context) {
		super(context);
		init(context);
	}

	public void setInfoTextview(TextView tv) {
		mInfoTextView = tv;
	}
	
	void init(Context context){
		mContext = context;
		
		
		mAlertDialogBuilder = new AlertDialog.Builder(mContext);
		mAlertDialogBuilder.setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                newGame();
		           }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
	}
	
	private int canvasCoord2ChessIndex(PointF point) {
		Point logicPoint = new Point((int)((point.x - mStartBoardX + mLaticeLen2)/mLaticeLen), (int)((point.y - mStartBoardY +mLaticeLen2)/mLaticeLen));
		int index =logicPoint.x + logicPoint.y * 9;
		if (index >= AI.BOARD_SIZE || index < 0) {
			return -1;
		}
		return index;
	}
	private PointF chessIndex2CanvasCoord(int i) {
		PointF point = new PointF(chessIndex2LogicPoint(i));
		point.x *= mLaticeLen ;
		point.x += mStartBoardX;
		point.y *= mLaticeLen ;
		point.y += mStartBoardY;
		return point;
	}
	private Point chessIndex2LogicPoint(int i) {
		return new Point(i%9,i/9);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if (mLaticeLen <0) {
			// load chess images
			Bitmap chessBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.qz);

			mLaticeLen = getWidth() * 35 /320.0f;
			mChesslen = mLaticeLen * 19.0f  / 20;
			mLaticeLen2 = mLaticeLen/2.0f;
			mChessLen2 = mChesslen /2.0f;
			
			mStartBoardX = getWidth() * 20.0f / 320;
			mStartBoardY = getHeight() * 20.0f /354;
			
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
			if (mPieces[i] != AI.EMPTY) {
				PointF point = chessIndex2CanvasCoord(i);
				Bitmap bmp = mChessBitmaps[mColors[i]][mPieces[i]];
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

	public void setAILevel(int depth) {
		mAi.setSearchDepth(depth);
	}
	void newGame() {
		mAi.init();
		mChessFrom = -1;
		mChessTo = -1;
		mIsComputerThinking = false;

		System.arraycopy(mAi.piece, 0, mPieces, 0, mPieces.length);
		System.arraycopy(mAi.color, 0, mColors, 0, mColors.length);
		mInfoTextView.setText(getResources().getString(R.string.welcome));
		postInvalidate();
	}
	
	void saveGameStatus() {
		try {

//			File file = new File(SAVE_STATE_FILE_NAME);
//			FileOutputStream fos = new FileOutputStream(file);
			FileOutputStream fos = mContext.openFileOutput(SAVE_STATE_FILE_NAME, Context.MODE_PRIVATE);
			DataOutputStream dos = new DataOutputStream(fos);
			
			dos.writeInt(mChessFrom);
			dos.writeInt(mChessTo);

			mAi.saveStatus(dos);
			
			dos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	void restoreGameStatus() {
		try {
//			File file = new File(SAVE_STATE_FILE_NAME);
//			FileInputStream fis = new FileInputStream(file);			
			FileInputStream fis = mContext.openFileInput(SAVE_STATE_FILE_NAME);
			DataInputStream dis = new DataInputStream(fis);

			mChessFrom = dis.readInt();
			mChessTo = dis.readInt();

			mAi.restoreStatus(dis);

			System.arraycopy(mAi.piece, 0, mPieces, 0, mPieces.length);
			System.arraycopy(mAi.color, 0, mColors, 0, mColors.length);
			mIsComputerThinking = false;
		} catch (FileNotFoundException e) {
			newGame();
			e.printStackTrace();
		} catch (IOException e) {
			newGame();
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!mIsComputerThinking && event.getAction() == MotionEvent.ACTION_DOWN) {
			final int chess = canvasCoord2ChessIndex(new PointF(event.getX(),event.getY()));
			Log.d("lzj", "chess index clicked : "+ chess);
			if (chess >0 ) {
				if (mChessFrom >= 0 && mChessTo >= 0 || mChessFrom < 0) {
					if (mAi.color[chess] == USER_COLOR) {
						mChessFrom = chess;
						mChessTo = -1;
						invalidate();
					}
				}else if( mChessFrom >= 0 && mChessTo < 0 && mAi.color[chess] == USER_COLOR){
					mChessFrom = chess;
					invalidate();
				}else if (mChessTo < 0) {
					Log.d("lzj", "second click, from: " + mChessFrom + ", to: "+chess);
					//human move
					int ret = mAi.takeAMove(mChessFrom, chess);
					if (ret == AI.MOVE_OK) {
						Log.d("lzj", "human move ok");
						mChessTo = chess;
						System.arraycopy(mAi.piece, 0, mPieces, 0, mPieces.length);
						System.arraycopy(mAi.color, 0, mColors, 0, mColors.length);
						invalidate();

						mIsComputerThinking = true;
						mInfoTextView.setText(getResources().getString(R.string.computer_move));
						new Thread(new Runnable() {
						
							public void run() {
								// computer move
								int ret = mAi.computerMove();
								Move move = mAi.getComputerMove();
								mChessFrom = move.from;
								mChessTo = move.dest;
								System.arraycopy(mAi.piece, 0, mPieces, 0, mPieces.length);
								System.arraycopy(mAi.color, 0, mColors, 0, mColors.length);
								postInvalidate();								
								mIsComputerThinking = false;
								
								Message msg = mMessageHandler.obtainMessage(MSG_COMPUTER_MOVE_DONE);
								mMessageHandler.sendMessage(msg);
								
								if (ret == AI.MOVE_WIN) {
									msg = mMessageHandler.obtainMessage(MSG_COMPUTER_WIN);
									mMessageHandler.sendMessage(msg);
								}
							}
						}).start();
					} else if (ret == AI.MOVE_WIN) {
						Message msg = mMessageHandler.obtainMessage(MSG_USER_WIN);
						mMessageHandler.sendMessage(msg);
					}
					Log.d("lzj", "takeAMove ret : " + ret);
				}
			}
		}
		return super.onTouchEvent(event);
	}
	class MessageHandler extends Handler {
		public void handleMessage(Message msg){
    		Log.d("lzj", "message hander : msg.what = " + msg.what);
    		switch (msg.what) {
    		case MSG_USER_MOVE_DONE:
    			break;
    		case MSG_COMPUTER_MOVE_DONE:
    			invalidate();
    			mInfoTextView.setText(getResources().getString(R.string.user_move));
    			break;
    		case MSG_COMPUTER_WIN:
				mAlertDialogBuilder.setMessage(getResources().getString(R.string.computer_win));
				mAlertDialogBuilder.create().show();
    			break;
    		case MSG_USER_WIN:
				mAlertDialogBuilder.setMessage(getResources()
						.getString(R.string.user_win));
				mAlertDialogBuilder.create().show();
    			break;
			default:
				break;
			}
    	}
	}
}
