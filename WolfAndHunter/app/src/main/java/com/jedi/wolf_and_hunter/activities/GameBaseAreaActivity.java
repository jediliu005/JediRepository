package com.jedi.wolf_and_hunter.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.jedi.wolf_and_hunter.MyViews.JRocker;
import com.jedi.wolf_and_hunter.MyViews.MapBaseFrame;
import com.jedi.wolf_and_hunter.MyViews.characters.BaseCharacterView;
import com.jedi.wolf_and_hunter.R;

public class GameBaseAreaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_base_area);

        MapBaseFrame mapBaseFrame=(MapBaseFrame)findViewById(R.id.map_base_frame);
        FrameLayout.LayoutParams paramsForMapBase = (FrameLayout.LayoutParams)mapBaseFrame.getLayoutParams();
        paramsForMapBase.width=2000;
        paramsForMapBase.height=3500;
        BaseCharacterView character = new BaseCharacterView(this);
        mapBaseFrame.addView(character);
        FrameLayout.LayoutParams params = ( FrameLayout.LayoutParams)character.getLayoutParams();
        if(params!=null) {
            params.height = 200;
            params.width = 100;
        }
        character.setBackgroundColor(Color.parseColor("#FF0000"));

    }
}
