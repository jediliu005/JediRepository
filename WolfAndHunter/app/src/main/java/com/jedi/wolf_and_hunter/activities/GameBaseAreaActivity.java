package com.jedi.wolf_and_hunter.activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jedi.wolf_and_hunter.MyViews.MapBaseFrame;
import com.jedi.wolf_and_hunter.MyViews.characters.BaseCharacterView;
import com.jedi.wolf_and_hunter.R;

public class GameBaseAreaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_base_area);
        //        TextView t = new TextView(this);
//        t.setText("测试");
//        t.setTextColor(20);
//        t.setTextSize(500);
        LinearLayout mapMainLayout = (LinearLayout) findViewById(R.id.linearLayout_map_main);
        MapBaseFrame mapBaseFrame=(MapBaseFrame)findViewById(R.id.map_base_frame);
        LinearLayout.LayoutParams paramsForMapBase = (LinearLayout.LayoutParams)mapBaseFrame.getLayoutParams();


        paramsForMapBase.width=2000;
        paramsForMapBase.height=3500;
        BaseCharacterView character = new BaseCharacterView(this);
        mapBaseFrame.addView(character);
        ViewGroup.LayoutParams params = character.getLayoutParams();
        if(params!=null) {
            params.height = 100;
            params.width = 100;
        }
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100,LinearLayout.LayoutParams.WRAP_CONTENT);
//        character.setLayoutParams(params);
//        Button b =new Button(this);
//
//        b.setText("测试");
//        b.setOnClickListener(new View.OnClickListener() {
//                                 int i=0;
//            @Override
//                                 public void onClick(View v) {
//                ((Button)v).setText(i++);
//                                 }
//                             }
//
//
//        );
//        mapMainLayout.addView(b);



        character.setBackgroundColor(Color.parseColor("#FF0000"));

    }
}
