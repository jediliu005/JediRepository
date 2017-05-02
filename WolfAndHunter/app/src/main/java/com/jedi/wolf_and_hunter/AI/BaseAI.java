package com.jedi.wolf_and_hunter.AI;

import android.util.Log;
import android.widget.FrameLayout;

import com.jedi.wolf_and_hunter.MyViews.characters.BaseCharacterView;

import java.util.TimerTask;

/**
 * Created by Administrator on 2017/4/29.
 */

public class BaseAI  extends TimerTask {
    private int intent=1;
    public static final int INTENT_DAZE=0;
    public static final int INTENT_MOVE=1;
    private int targetX=500;
    private int targetY=0;
    public BaseCharacterView bindingCharacter;
    public FrameLayout.LayoutParams characterLayoutParams;
    public BaseAI(BaseCharacterView character){
        super();
        this.bindingCharacter=character;
    }

    @Override
    public void run() {
        if(characterLayoutParams==null){
            characterLayoutParams=new FrameLayout.LayoutParams(0,0);
        }
        decideWhatToDo();
        if(intent==0){
            return;
        }else if(intent==1){
            move();
        }

    }
    public void decideWhatToDo(){

    }
    public void move() {
//        synchronized (bindingCharacter) {
            Log.i("AI",bindingCharacter.centerX+":"+bindingCharacter.nowLeft);
            if (bindingCharacter.centerX <= 100) {
                targetX=500;


            } else if (bindingCharacter.centerX >= 500) {
                targetX=100;
            }
            if(bindingCharacter.centerX<targetX){
                if(bindingCharacter.centerX+bindingCharacter.speed>targetX)
                    bindingCharacter.nowLeft=targetX-bindingCharacter.getWidth()/2;
                else
                    bindingCharacter.nowLeft=bindingCharacter.nowLeft+bindingCharacter.speed;
            }else{
                if(bindingCharacter.centerX-bindingCharacter.speed<targetX)
                    bindingCharacter.nowLeft=targetX-bindingCharacter.getWidth()/2;
                else
                    bindingCharacter.nowLeft=bindingCharacter.nowLeft-bindingCharacter.speed;
            }
        bindingCharacter.centerX = bindingCharacter.nowLeft + bindingCharacter.getWidth() / 2;
        bindingCharacter.centerY = bindingCharacter.nowTop + bindingCharacter.getHeight() / 2;

//        characterLayoutParams.leftMargin=bindingCharacter.nowLeft;
//        characterLayoutParams.topMargin=bindingCharacter.nowTop;
//        bindingCharacter.changeState();
//        bindingCharacter.setLayoutParams(characterLayoutParams);

//        bindingCharacter.attackRange.centerX=bindingCharacter.centerX;
//        bindingCharacter.attackRange.centerY=bindingCharacter.centerY;
//        bindingCharacter.attackRange.layoutParams.leftMargin=bindingCharacter.attackRange.centerX-bindingCharacter.attackRange.nowAttackRadius;
//        bindingCharacter.attackRange.layoutParams.topMargin=bindingCharacter.attackRange.centerY-bindingCharacter.attackRange.nowAttackRadius;
//        bindingCharacter.attackRange.setLayoutParams(bindingCharacter.attackRange.layoutParams);

//        bindingCharacter.viewRange.centerX=bindingCharacter.centerX;
//        bindingCharacter.viewRange.centerY=bindingCharacter.centerY;
//        bindingCharacter.viewRange.layoutParams.leftMargin=bindingCharacter.viewRange.centerX-bindingCharacter.viewRange.nowViewRadius;
//        bindingCharacter.viewRange.layoutParams.topMargin=bindingCharacter.viewRange.centerY-bindingCharacter.viewRange.nowViewRadius;
//        bindingCharacter.viewRange.setLayoutParams(bindingCharacter.viewRange.layoutParams);
//        }
    }
}