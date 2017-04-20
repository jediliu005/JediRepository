package com.jedi.wolf_and_hunter.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

             private synchronized void reflashCharacterState(){

                 if(myCharacter==null||leftRocker==null||rightRocker==null)
                     return;
                 boolean isMyCharacterMoving=myCharacter.needMove;
                 boolean needChange=false;
                 synchronized (myCharacter) {
                     synchronized (mySight) {
                         myCharacter.hasUpdatedPosition = false;
                         mySight.hasUpdatedWindowPosition = false;
                         mySight.hasUpdatedPosition = false;
                         //获得当前位置
                         myCharacter.updateNowPosition();
                         mySight.updateNowPosition();
                         //获得视窗虚拟位置
                         mySight.updateNowWindowPosition();
                         if (myCharacter.needMove == true) {
                             Log.i("GBA", "Moving Character Started");
                             myCharacter.offsetLRTBParams();
                             needChange = true;
                             Log.i("GBA", "Moving Character ended");
                         }
                         if (mySight.needMove == true) {
                             Log.i("GBA", "Moving Sight Started");
                             mySight.offsetLRTBParams(isMyCharacterMoving);
                             Log.i("GBA", "Moving Sight ended");
                             needChange = true;
                         }
                         if (needChange) {
                             myCharacter.mLayoutParams.leftMargin = myCharacter.nowLeft;
                             myCharacter.mLayoutParams.topMargin = myCharacter.nowTop;
                             myCharacter.setLayoutParams(myCharacter.mLayoutParams);
                             mySight.mLayoutParams.leftMargin = mySight.nowLeft;
                             mySight.mLayoutParams.topMargin = mySight.nowTop;
                             mySight.setLayoutParams(mySight.mLayoutParams);
                             mySight.offsetWindow(mySight.nowWindowLeft, mySight.nowWindowTop);

                             Log.i("GBA", "Change  Started");
                             mapBaseFrame.invalidate();
                             myCharacter.centerX = myCharacter.nowLeft + myCharacter.getWidth() / 2;
                             myCharacter.centerY = myCharacter.nowTop + myCharacter.getHeight() / 2;
                             mySight.centerX = mySight.nowLeft + mySight.getWidth() / 2;
                             mySight.centerY = mySight.nowTop + mySight.getHeight() / 2;
                             myCharacter.changeRotate();

                             Log.i("GBA", "Change  ended");
                         }

                     }

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
        mapBaseFrame.myCharacter=myCharacter;
        mySight = new SightView(this);
        mySight. sightSize=myCharacter.characterBodySize;
        mapBaseFrame.addView(mySight);
        mapBaseFrame.mySight=mySight;
        FrameLayout.LayoutParams paramsForMySight = (FrameLayout.LayoutParams)mySight.getLayoutParams();
        paramsForMySight.leftMargin=myCharacter.characterBodySize*2;
        paramsForMySight.topMargin=myCharacter.characterBodySize*2;
        mySight.setLayoutParams(paramsForMySight);
        mapBaseFrame.mySight=mySight;
        leftRocker=(LeftRocker) this.findViewById(R.id.rocker_left);
        leftRocker.setBindingCharacter(myCharacter);
        this.gameHandler=new GameHandler();
        leftRocker.gameHandler=gameHandler;
        mapBaseFrame.leftRocker=leftRocker;






        rightRocker=(RightRocker) this.findViewById(R.id.rocker_right);
        rightRocker.setBindingCharacter(myCharacter);
        mapBaseFrame.rightRocker=rightRocker;
        myCharacter.setSight(mySight);
        timer.schedule(new MyTimerTask(),20,20);

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
