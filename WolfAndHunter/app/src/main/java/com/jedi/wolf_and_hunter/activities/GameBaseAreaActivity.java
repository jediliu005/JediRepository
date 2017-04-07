package com.jedi.wolf_and_hunter.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.jedi.wolf_and_hunter.MyViews.JRocker;
import com.jedi.wolf_and_hunter.MyViews.MapBaseFrame;
import com.jedi.wolf_and_hunter.MyViews.characters.BaseCharacterView;
import com.jedi.wolf_and_hunter.R;

import java.util.ArrayList;
import java.util.List;

public class GameBaseAreaActivity extends AppCompatActivity {
    JRocker leftRocker;
    JRocker rightRocker;
    ArrayList<BaseCharacterView> characters=new ArrayList<BaseCharacterView>();
    BaseCharacterView myCharacter;
    GameHandler gameHandler;


             public class GameHandler extends Handler{
                 public static final int FROM_ROCKER_LEFT=1;
                 @Override
                 public void handleMessage(Message msg) {
                     super.handleMessage(msg);
                     switch ( msg.what)
                     {case FROM_ROCKER_LEFT:
                         reactLeftRocker(msg);





                     }

                 }

                 private void reactLeftRocker(Message msg){
                     Bundle b = msg.getData();
                     int relateX=b.getInt("relateX");
                     int relateY=b.getInt("relateY");
                     myCharacter.getTop();
                     myCharacter.offsetLeftAndRight(relateX/10);
                     myCharacter.offsetTopAndBottom(relateY/10);
                     myCharacter.getmLayoutParams().setMargins(myCharacter.getNowLeft(),myCharacter.getNowTop(),myCharacter.getNowRight(),myCharacter.getNowBotton());


                 }
             }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_base_area);

        MapBaseFrame mapBaseFrame=(MapBaseFrame)findViewById(R.id.map_base_frame);
        FrameLayout.LayoutParams paramsForMapBase = (FrameLayout.LayoutParams)mapBaseFrame.getLayoutParams();
        paramsForMapBase.width=2000;
        paramsForMapBase.height=3500;
        leftRocker=(JRocker) this.findViewById(R.id.rocker_left);
        rightRocker=(JRocker) this.findViewById(R.id.rocker_right);
        this.gameHandler=new GameHandler();
        leftRocker.gameHandler=this.gameHandler;
        rightRocker.gameHandler=this.gameHandler;
        myCharacter = new BaseCharacterView(this);
        mapBaseFrame.addView(myCharacter);

//        FrameLayout.LayoutParams params = ( FrameLayout.LayoutParams)character.getLayoutParams();
//        if(params!=null) {
//            params.height = 300;
//            params.width = 300;
//            params.height = FrameLayout.LayoutParams.WRAP_CONTENT;
//            params.width = FrameLayout.LayoutParams.WRAP_CONTENT;
//            character.setLayoutParams(params);
//
//        }
//        character.setBackgroundColor(Color.parseColor("#FF0000"));

    }
}
