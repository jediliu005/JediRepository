package com.jedi.wolf_and_hunter.activities;

import android.app.Activity;
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
import com.jedi.wolf_and_hunter.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class GameBaseAreaActivity extends Activity {
    boolean isStop=false;

    LeftRocker leftRocker;
    RightRocker rightRocker;
    Thread gameThread;
    ArrayList<BaseCharacterView> characters=new ArrayList<BaseCharacterView>();
    MapBaseFrame mapBaseFrame;
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
                 if(myCharacter==null||leftRocker==null||rightRocker==null)
                     return;
                 boolean isMyCharacterMoving=myCharacter.needMove;
                 boolean needChange=false;
                 if(myCharacter.needMove==true) {
                     synchronized (myCharacter) {
                         myCharacter.offsetLRTBParams();
                         needChange=true;
//                         myCharacter.getmLayoutParams().setMargins(myCharacter.getLeft(), myCharacter.getTop(), myCharacter.getRight(), myCharacter.getBottom());
//                         mySight.getmLayoutParams().setMargins(mySight.getLeft(), mySight.getTop(), mySight.getRight(), mySight.getBottom());

                     }
                 }
                 if(mySight.needMove==true){
                     synchronized (mySight) {
                         mySight.offsetLRTBParams(isMyCharacterMoving);
//                         mySight.getmLayoutParams().setMargins(mySight.getLeft(), mySight.getTop(), mySight.getRight(), mySight.getBottom());

                         needChange=true;
                     }
                 }
                 if(needChange){


                             mapBaseFrame.invalidate();
                             myCharacter.centerX = myCharacter.getLeft() + myCharacter.getWidth() / 2;
                             myCharacter.centerY = myCharacter.getTop() + myCharacter.getHeight() / 2;
                             mySight.centerX = mySight.getLeft() + mySight.getWidth() / 2;
                             mySight.centerY = mySight.getTop() + mySight.getHeight() / 2;
                             myCharacter.changeRotate();


//                     mapBaseFrame.setLayoutParams(mapBaseFrame.getLayoutParams());
                 }


             }

//             private class ReflashCharacterState implements Runnable{
//
//                 @Override
//                 public void run() {
//                     int i=0;
//                     while (!isStop) {
//
////                         if(i++%2==0)
////                             leftRocker.setBackgroundColor(Color.RED);
////                         else
////                             leftRocker.setBackgroundColor(Color.GREEN);
//                         if(myCharacter.hasMoved==true){
//                             myCharacter.offsetLRTB();
//                             myCharacter.getmLayoutParams().setMargins(myCharacter.nowLeft,myCharacter.nowTop,myCharacter.nowRight,myCharacter.nowBotton);
//                             myCharacter.hasMoved=false;
//                         }
//                         if(mySight.hasMoved==true){
//                             mySight.offsetLRTB();
//                             mySight.getmLayoutParams().setMargins(mySight.nowLeft,mySight.nowTop,mySight.nowRight,mySight.nowBotton);
//                             mySight.hasMoved=false;
//
//                         }
//                         if(myCharacter.hasTurned==true){
//
//
//                         }
//
//
//
//                         try {
//                             Thread.sleep(30);
//                         } catch (InterruptedException e) {
//                             e.printStackTrace();
//                         }
//                     }
//                 }
//             }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_base_area);
        ViewUtils.initWindowParams(this);

        mapBaseFrame=(MapBaseFrame)findViewById(R.id.map_base_frame);
        FrameLayout.LayoutParams paramsForMapBase = (FrameLayout.LayoutParams)mapBaseFrame.getLayoutParams();
        paramsForMapBase.width=2000;
        paramsForMapBase.height=1500;
        myCharacter = new BaseCharacterView(this);
        mapBaseFrame.addView(myCharacter);

        mySight = new SightView(this);
        mySight. sightSize=myCharacter.characterBodySize;
        mapBaseFrame.addView(mySight);
        FrameLayout.LayoutParams paramsForMySight = (FrameLayout.LayoutParams)mySight.getLayoutParams();
        paramsForMySight.leftMargin=myCharacter.characterBodySize*2;
        paramsForMySight.topMargin=myCharacter.characterBodySize*2;
        mySight.setLayoutParams(paramsForMySight);
        mapBaseFrame.mySight=mySight;
        leftRocker=(LeftRocker) this.findViewById(R.id.rocker_left);
        leftRocker.setBindingCharacter(myCharacter);
        this.gameHandler=new GameHandler();
        leftRocker.gameHandler=gameHandler;


        timer.schedule(new MyTimerTask(),20,20);



        rightRocker=(RightRocker) this.findViewById(R.id.rocker_right);
        rightRocker.setBindingCharacter(myCharacter);
        myCharacter.setSight(mySight);


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
