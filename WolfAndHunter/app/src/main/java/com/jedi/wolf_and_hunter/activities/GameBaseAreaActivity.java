package com.jedi.wolf_and_hunter.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.jedi.wolf_and_hunter.MyViews.JRocker;
import com.jedi.wolf_and_hunter.MyViews.LeftRocker;
import com.jedi.wolf_and_hunter.MyViews.MapBaseFrame;
import com.jedi.wolf_and_hunter.MyViews.RightRocker;
import com.jedi.wolf_and_hunter.MyViews.SightView;
import com.jedi.wolf_and_hunter.MyViews.characters.BaseCharacterView;
import com.jedi.wolf_and_hunter.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class GameBaseAreaActivity extends AppCompatActivity {
    boolean isStop=false;

    LeftRocker leftRocker;
    RightRocker rightRocker;
    Thread gameThread;
    ArrayList<BaseCharacterView> characters=new ArrayList<BaseCharacterView>();
    BaseCharacterView myCharacter;
    SightView mySight;
    GameHandler gameHandler;
    Timer timer=new Timer();

    private class MyTimerTask extends TimerTask
    {
        @Override
        public void run() {
            gameHandler.sendEmptyMessage(0);
        }
    };

             public class GameHandler extends Handler{
                 public static final int FROM_ROCKER_LEFT=1;
                 @Override
                 public void handleMessage(Message msg) {
                     reflashCharacterState();

                 }

//                 private void reactLeftRocker(Message msg){
//                     Bundle b = msg.getData();
//                     int relateX=b.getInt("relateX");
//                     int relateY=b.getInt("relateY");
//                     myCharacter.offsetLeftAndRight(relateX/10);
//                     myCharacter.offsetTopAndBottom(relateY/10);
//                     myCharacter.getmLayoutParams().setMargins(myCharacter.getNowLeft(),myCharacter.getNowTop(),myCharacter.getNowRight(),myCharacter.getNowBotton());
//
//
//                 }
             }

             private void reflashCharacterState(){
//                 if(myCharacter.hasChanged==false){
//                    return;
//                 }
                 if(myCharacter==null||leftRocker==null)
                     return;
                 myCharacter.offsetLRTB();
                 myCharacter.getmLayoutParams().setMargins(myCharacter.nowLeft,myCharacter.nowTop,myCharacter.nowRight,myCharacter.nowBotton);

             }

             private class ReflashCharacterState implements Runnable{

                 @Override
                 public void run() {
                     int i=0;
                     while (!isStop) {

//                         if(i++%2==0)
//                             leftRocker.setBackgroundColor(Color.RED);
//                         else
//                             leftRocker.setBackgroundColor(Color.GREEN);
                         if(myCharacter.hasChanged==false){
                             try {
                                 Thread.sleep(30);
                                 continue;
                             } catch (InterruptedException e) {
                                 e.printStackTrace();
                             }
                         }
                         myCharacter.offsetLRTB();
                         myCharacter.getmLayoutParams().setMargins(myCharacter.nowLeft,myCharacter.nowTop,myCharacter.nowRight,myCharacter.nowBotton);
                         myCharacter.hasChanged=false;


                         try {
                             Thread.sleep(30);
                         } catch (InterruptedException e) {
                             e.printStackTrace();
                         }
                     }
                 }
             }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_base_area);

        MapBaseFrame mapBaseFrame=(MapBaseFrame)findViewById(R.id.map_base_frame);
        FrameLayout.LayoutParams paramsForMapBase = (FrameLayout.LayoutParams)mapBaseFrame.getLayoutParams();
        paramsForMapBase.width=3500;
        paramsForMapBase.height=3500;
        myCharacter = new BaseCharacterView(this);
        mapBaseFrame.addView(myCharacter);

        mySight = new SightView(this);
        FrameLayout.LayoutParams paramsForMySight = new FrameLayout.LayoutParams(0,0);
        paramsForMySight.leftMargin=myCharacter.characterBodySize*2;
        paramsForMySight.topMargin=myCharacter.characterBodySize*2;
        mapBaseFrame.addView(mySight);
        leftRocker=(LeftRocker) this.findViewById(R.id.rocker_left);
        leftRocker.setBindingCharacter(myCharacter);
        this.gameHandler=new GameHandler();
        leftRocker.gameHandler=gameHandler;


        timer.schedule(new MyTimerTask(),20,20);



        rightRocker=(RightRocker) this.findViewById(R.id.rocker_right);
        rightRocker.setBindingCharacter(myCharacter);


//        leftRocker.gameHandler=this.gameHandler;
//        rightRocker.gameHandler=this.gameHandler;

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

    @Override
    protected void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }
}
