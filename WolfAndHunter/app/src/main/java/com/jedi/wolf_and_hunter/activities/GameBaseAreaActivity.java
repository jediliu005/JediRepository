package com.jedi.wolf_and_hunter.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.FrameLayout;

import com.jedi.wolf_and_hunter.MyViews.AttackRange;
import com.jedi.wolf_and_hunter.MyViews.GameMap;
import com.jedi.wolf_and_hunter.MyViews.LeftRocker;
import com.jedi.wolf_and_hunter.MyViews.MapBaseFrame;
import com.jedi.wolf_and_hunter.MyViews.RightRocker;
import com.jedi.wolf_and_hunter.MyViews.SFViewRange;
import com.jedi.wolf_and_hunter.MyViews.SightView;
import com.jedi.wolf_and_hunter.MyViews.ViewRange;
import com.jedi.wolf_and_hunter.MyViews.characters.BaseCharacterView;
import com.jedi.wolf_and_hunter.MyViews.characters.NormalHunter;
import com.jedi.wolf_and_hunter.MyViews.landform.Landform;
import com.jedi.wolf_and_hunter.MyViews.landform.TallGrassland;
import com.jedi.wolf_and_hunter.R;
import com.jedi.wolf_and_hunter.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class GameBaseAreaActivity extends Activity {
    boolean isStop = false;
    Context context = this;
    LeftRocker leftRocker;
    RightRocker rightRocker;
    Thread gameThread;
    ArrayList<BaseCharacterView> characters = new ArrayList<BaseCharacterView>();
    MapBaseFrame mapBaseFrame;
    public static BaseCharacterView myCharacter;
    SightView mySight;
    public  GameHandler gameHandler=new GameHandler();
    Timer timer = new Timer();
    Landform[][] landformses;

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            gameHandler.sendEmptyMessage(0);
        }
    }

    ;

    public class GameHandler extends Handler {
        public static final int ADD_ATTACT_RANGE = 1;
        public static final int ADD_VIEW_RANGE = 2;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ADD_ATTACT_RANGE:
                    AttackRange attackRange = new AttackRange(context, myCharacter);
                    myCharacter.attackRange = attackRange;
                    mapBaseFrame.addView(attackRange);
                    break;
                case ADD_VIEW_RANGE:
                    ViewRange viewRange = new ViewRange(context, myCharacter);
                    myCharacter.viewRange = viewRange;
                    mapBaseFrame.addView(viewRange);
//                    SFViewRange viewRange = new SFViewRange(context, myCharacter);
//                    myCharacter.viewRange = viewRange;
//                    mapBaseFrame.addView(viewRange);
                    break;
                default:

                    reflashCharacterState();
            }


        }

    }

    private synchronized void reflashCharacterState() {

        if (myCharacter == null || leftRocker == null || rightRocker == null)
            return;
        boolean isMyCharacterMoving = myCharacter.needMove;
        boolean needChange = false;
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
                    myCharacter.masterModeOffsetLRTBParams();
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

                    myCharacter.centerX = myCharacter.nowLeft + myCharacter.getWidth() / 2;
                    myCharacter.centerY = myCharacter.nowTop + myCharacter.getHeight() / 2;
                    mySight.centerX = mySight.nowLeft + mySight.getWidth() / 2;
                    mySight.centerY = mySight.nowTop + mySight.getHeight() / 2;
                    myCharacter.changeState();
                    myCharacter.changeRotate();

                    myCharacter.attackRange.centerX=myCharacter.centerX;
                    myCharacter.attackRange.centerY=myCharacter.centerY;
                    myCharacter.attackRange.layoutParams.leftMargin=myCharacter.attackRange.centerX-myCharacter.attackRange.nowAttackRadius;
                    myCharacter.attackRange.layoutParams.topMargin=myCharacter.attackRange.centerY-myCharacter.attackRange.nowAttackRadius;
                    myCharacter.attackRange.setLayoutParams(myCharacter.attackRange.layoutParams);

                    myCharacter.viewRange.centerX=myCharacter.centerX;
                    myCharacter.viewRange.centerY=myCharacter.centerY;
                    myCharacter.viewRange.layoutParams.leftMargin=myCharacter.viewRange.centerX-myCharacter.viewRange.nowViewRadius;
                    myCharacter.viewRange.layoutParams.topMargin=myCharacter.viewRange.centerY-myCharacter.viewRange.nowViewRadius;
                    myCharacter.viewRange.setLayoutParams(myCharacter.viewRange.layoutParams);

                    mapBaseFrame.invalidate();


                    Log.i("GBA", "Change  ended");
                }

            }

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_base_area);
        ViewUtils.initWindowParams(this);

        mapBaseFrame = (MapBaseFrame) findViewById(R.id.map_base_frame);
//        mapBaseFrame.setWillNotDraw(false);
        FrameLayout.LayoutParams paramsForMapBase = (FrameLayout.LayoutParams) mapBaseFrame.getLayoutParams();
        paramsForMapBase.width = 2000;
        paramsForMapBase.height = 1500;
        mapBaseFrame.setLayoutParams(paramsForMapBase);


//        map.getLayoutParams().width= FrameLayout.LayoutParams.MATCH_PARENT;
//        map.getLayoutParams().height= FrameLayout.LayoutParams.MATCH_PARENT;


        landformses = new Landform[15][20];
        for (int i = 0; i < landformses.length; i++) {
            for (int j = 0; j < landformses[i].length; j++) {
                if (Math.abs(i - j) % 3 == 0)
                    landformses[i][j] = new TallGrassland(this);
            }
        }
        GameMap map = new GameMap(this);
        mapBaseFrame.addView(map);
        map.landformses = landformses;
        map.addLandforms();


        myCharacter = new NormalHunter(this);
        myCharacter.gameHandler=gameHandler;

        mapBaseFrame.addView(myCharacter);
        mapBaseFrame.myCharacter = myCharacter;

        mySight = new SightView(this);
        mySight.sightSize = myCharacter.characterBodySize;
        mapBaseFrame.addView(mySight);
        mapBaseFrame.mySight = mySight;
        FrameLayout.LayoutParams paramsForMySight = (FrameLayout.LayoutParams) mySight.getLayoutParams();
        paramsForMySight.leftMargin = myCharacter.characterBodySize * 2;
        paramsForMySight.topMargin = myCharacter.characterBodySize * 2;
        mySight.setLayoutParams(paramsForMySight);
        mapBaseFrame.mySight = mySight;
        leftRocker = (LeftRocker) this.findViewById(R.id.rocker_left);
        leftRocker.getLayoutParams().width=300;
        leftRocker.getLayoutParams().width=300;
        leftRocker.setBindingCharacter(myCharacter);

        leftRocker.gameHandler = gameHandler;
        mapBaseFrame.leftRocker = leftRocker;


        rightRocker = (RightRocker) this.findViewById(R.id.rocker_right);
        rightRocker.setBindingCharacter(myCharacter);
        mapBaseFrame.rightRocker = rightRocker;
        myCharacter.setSight(mySight);
        timer.schedule(new MyTimerTask(), 20, 20);

    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }
}
