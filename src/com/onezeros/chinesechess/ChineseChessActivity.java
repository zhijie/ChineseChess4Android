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

import cn.domob.android.ads.DomobAdView;
import cn.domob.android.ads.DomobUpdater;

import com.android.chinesechess.R;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChineseChessActivity extends Activity {
	public static final String DOMOB_PUBLISHER_ID_STRING = "56OJyOeouMzH2P6sIM";
	
	ChessboardView mChessboardView;
	RelativeLayout mMainLayout;
	LinearLayout mMenuLayout;
	Button mNewGameButton;
	Button mContinueButton;
	TextView mInfoTextView;
	boolean mIsUIStart = true;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mMainLayout = (RelativeLayout)findViewById(R.id.mainview);
        mMenuLayout = (LinearLayout)findViewById(R.id.menu_view);
        mNewGameButton = (Button)findViewById(R.id.new_game_btn);
        mContinueButton = (Button)findViewById(R.id.restore_game_btn);
        
        mChessboardView = (ChessboardView)findViewById(R.id.chessboard);
        
        mInfoTextView = (TextView)findViewById(R.id.info_tv);
        mChessboardView.setInfoTextview(mInfoTextView);
        
        mNewGameButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				mChessboardView.newGame();
				switchViewTo(mMainLayout);
				mIsUIStart = false;
			}
		});
        mContinueButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if (mIsUIStart) {
					mChessboardView.restoreGameStatus();
				}
				switchViewTo(mMainLayout);
				mIsUIStart = false;
			}
		});
        
        // domob ad
        LinearLayout layout = (LinearLayout)findViewById(R.id.AdLinearLayout);
        DomobAdView adView = new DomobAdView(this,DOMOB_PUBLISHER_ID_STRING,DomobAdView.INLINE_SIZE_320X50);
        layout.addView(adView);
        
        DomobUpdater.checkUpdate(this, DOMOB_PUBLISHER_ID_STRING);
    }

    void switchViewTo(View v) {
    	if (v == mMainLayout) {
			mMenuLayout.setVisibility(View.INVISIBLE);
			mMainLayout.setVisibility(View.VISIBLE);
		}else if (v == mMenuLayout) {
			mMenuLayout.setVisibility(View.VISIBLE);
			mMainLayout.setVisibility(View.INVISIBLE);
		}
    }
	@Override
	public void onBackPressed() {
		if (mMainLayout.getVisibility() == View.VISIBLE) {
			switchViewTo(mMenuLayout);
		}else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		mChessboardView.saveGameStatus();
	}

	@Override
	protected void onResume() {		
		super.onResume();
		MobclickAgent.onResume(this);
	}
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = new MenuInflater(this);
		inflater.inflate(R.menu.option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.level1_menu:
			mChessboardView.setAILevel(3);
			mChessboardView.newGame();
			break;
		case R.id.level2_menu:
			mChessboardView.setAILevel(4);
			mChessboardView.newGame();
			break;
		case R.id.level3_menu:
			mChessboardView.setAILevel(5);
			mChessboardView.newGame();
			break;
		case R.id.about_menu:
			Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.dialog);
			dialog.setTitle(R.string.about_title);
			dialog.show();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}