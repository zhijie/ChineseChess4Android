package com.onezeros.chinesechess;

import com.android.chinesechess.R;

import android.app.Activity;
import android.os.Bundle;

public class ChineseChessActivity extends Activity {
    
    ChessboardView mChessboardImageView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mChessboardImageView = (ChessboardView ) findViewById(R.id.chessboard);
        

    }
}