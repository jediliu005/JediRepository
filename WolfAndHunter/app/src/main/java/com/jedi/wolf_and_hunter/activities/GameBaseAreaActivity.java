package com.jedi.wolf_and_hunter.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.FrameLayout;

import com.jedi.wolf_and_hunter.AI.BaseAI;
import com.jedi.wolf_and_hunter.MyViews.AttackRange;
import com.jedi.wolf_and_hunter.MyViews.GameMap;
import com.jedi.wolf_and_hunter.MyViews.LeftRocker;
import com.jedi.wolf_and_hunter.MyViews.MapBaseFrame;
import com.jedi.wolf_and_hunter.MyViews.RightRocker;
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
    public static ArrayList<BaseCharacterView> allCharacters ;
    public static MapBaseFrame mapBaseFrame;
    public static BaseCharacterView myCharacter;
    SightView mySight;
    public  GameHandler gameHandler=new GameHandler();
    Timer timerForMyMoving = new Timer();
    ArrayList<Timer>  timerForAIList = new ArrayList<Timer>();
    Timer timerForOthersMoving = new Timer();
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
//                    if(myCharacter.attackRange==null) {
//                        AttackRange attackRange = new AttackRange(context, myCharacter);
//                        myCharacter.attackRange = attackRange;
//                    }
//                    mapBaseFrame.addView(myCharacter.attackRange);
                    break;
                case ADD_VIEW_RANGE:
//                    if(myCharacter.viewRange==null) {
//                        ViewRange viewRange = new ViewRange(context, myCharacter);
//                        myCharacter.viewRange = viewRange;
//                    }
//                    mapBaseFrame.addView(myCharacter.viewRange);
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
                    myCharacter.changeThisCharacterState();
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




                    Log.i("GBA", "Change  ended");
                }
                for(BaseCharacterView c:allCharacters){
                    if(c==myCharacter)
                        continue;
                    c.reactAIMove();
                    c.offX=0;
                    c.offY=0;
                    c.mLayoutParams.leftMargin = c.nowLeft;
                    c.mLayoutParams.topMargin = c.nowTop;
                    c.centerX=c.nowLeft+c.getWidth()/2;
                    c.centerY=c.nowTop+c.getHeight()/2;
                    c.changeThisCharacterState();
                    myCharacter.changeOtherCharacterState(c);
                    c.setLayoutParams(c.mLayoutParams);


                    c.attackRange.centerX=c.centerX;
                    c.attackRange.centerY=c.centerY;
                    c.attackRange.layoutParams.leftMargin=c.attackRange.centerX-c.attackRange.nowAttackRadius;
                    c.attackRange.layoutParams.topMargin=c.attackRange.centerY-c.attackRange.nowAttackRadius;
                    c.attackRange.setLayoutParams(c.attackRange.layoutParams);

                    c.viewRange.centerX=c.centerX;
                    c.viewRange.centerY=c.centerY;
                    c.viewRange.layoutParams.leftMargin=c.viewRange.centerX-c.viewRange.nowViewRadius;
                    c.viewRange.layoutParams.topMargin=c.viewRange.centerY-c.viewRange.nowViewRadius;
                    c.viewRange.setLayoutParams(c.viewRange.layoutParams);
                }
                mapBaseFrame.invalidate();

            }

        }
    }

    private void startAI(){
        NormalHunter aiCharacter = new NormalHunter(this);
        ViewRange viewRange= new ViewRange(this,aiCharacter);
        AttackRange attackRange=new AttackRange(this,aiCharacter);
        mapBaseFrame.addView(viewRange);
        mapBaseFrame.addView(attackRange);
        BaseAI ai1=new BaseAI(aiCharacter);

        mapBaseFrame.addView(aiCharacter);
        allCharacters.add(aiCharacter);
        Timer timerForAI=new Timer("AIPlayer1",true);
        timerForAI.scheduleAtFixedRate(ai1,0,50);
        timerForAIList.add(timerForAI);



    }

    private  void addElementToMap() throws Exception{


        int widthCount=mapBaseFrame.mapWidth/100;
        int heightCount=mapBaseFrame.mapHeight/100;
        landformses = new Landform[heightCount][widthCount];
        for (int i = 0; i < landformses.length; i++) {
            for (int j = 0; j < landformses[i].length; j++) {
                if (Math.abs(i - j) % 3 == 0)
                    landformses[i][j] = new TallGrassland(this);
            }
        }


        //添加地形
        GameMap map = new GameMap(this);
        mapBaseFrame.addView(map);
        map.landformses = landformses;
        map.addLandforms();

        //添加我的角色
        allCharacters= new ArrayList<BaseCharacterView>();
        myCharacter = new NormalHunter(this);
        allCharacters.add(myCharacter);
        myCharacter.isMyCharacter=true;
        myCharacter.gameHandler=gameHandler;
//        mapBaseFrame.addView(myCharacter);
        mapBaseFrame.myCharacter = myCharacter;
        mapBaseFrame.addView(myCharacter);
        mapBaseFrame.addView(myCharacter.attackRange);
        mapBaseFrame.addView(myCharacter.viewRange);

        //添加视点
        mySight = new SightView(this);
        mySight.sightSize = myCharacter.characterBodySize;
        myCharacter.setSight(mySight);
        myCharacter.changeRotate();
        mapBaseFrame.addView(mySight);
        mapBaseFrame.mySight = mySight;


        //添加摇杆
        leftRocker = (LeftRocker) this.findViewById(R.id.rocker_left);
        leftRocker.setBindingCharacter(myCharacter);
        mapBaseFrame.leftRocker = leftRocker;
        rightRocker = (RightRocker) this.findViewById(R.id.rocker_right);
        rightRocker.setBindingCharacter(myCharacter);
        mapBaseFrame.rightRocker = rightRocker;
        leftRocker.bringToFront();
        rightRocker.bringToFront();



        startAI();







    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_base_area);
        ViewUtils.initWindowParams(this);
        FrameLayout baseFrame=(FrameLayout) findViewById(R.id.baseFrame);
        mapBaseFrame = new MapBaseFrame(this,1000,750);
        baseFrame.addView(mapBaseFrame);
        mapBaseFrame.post(new Runnable() {
            @Override
            public void run() {
                try {
                    addElementToMap();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        allCharacters= new ArrayList<BaseCharacterView>();
//        FrameLayout.LayoutParams paramsForMapBase = (FrameLayout.LayoutParams) mapBaseFrame.getLayoutParams();
//        paramsForMapBase.width = 2000;
//        paramsForMapBase.height = 1500;
//        mapBaseFrame.setLayoutParams(paramsForMapBase);



        //scheduleAtFixedRate后一次Task不以前一个Task执行完毕的时间为起点延时执行
        timerForMyMoving.scheduleAtFixedRate(new MyTimerTask(), 20, 20);

    }



    @Override
    protected void onDestroy() {
        timerForMyMoving.cancel();
        timerForOthersMoving.cancel();
        for(Timer timer:timerForAIList) {
            timer.cancel();
        }
        super.onDestroy();
    }
}
