package com.onezeros.chinesechess;

import cn.domob.android.ads.DomobAdView;

import com.android.chinesechess.R;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class ChineseChessActivity extends Activity {
	ChessboardView mChessboardView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mChessboardView = (ChessboardView)findViewById(R.id.chessboard);
        
        // domob ad
        LinearLayout layout = (LinearLayout)findViewById(R.id.AdLinearLayout);
        DomobAdView adView = new DomobAdView(this,"56OJyOeouMzH2P6sIM",DomobAdView.INLINE_SIZE_320X50);
        layout.addView(adView);
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
		mChessboardView.restoreGameStatus();
	}
}