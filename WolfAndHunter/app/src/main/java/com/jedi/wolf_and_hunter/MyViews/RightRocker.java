package com.jedi.wolf_and_hunter.MyViews;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.jedi.wolf_and_hunter.MyViews.characters.BaseCharacterView;
import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;
import com.jedi.wolf_and_hunter.utils.MyMathsUtils;
import com.jedi.wolf_and_hunter.utils.ViewUtils;

/**
 * Created by Administrator on 2017/3/29.
 */

public class RightRocker extends JRocker  {


    public GameBaseAreaActivity.GameHandler gameHandler;
    boolean isStop=false;



    public RightRocker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
        RightRocker.ReactToCharacter reactToCharacter=new RightRocker.ReactToCharacter();
        Thread reactThread=new Thread(reactToCharacter);
        reactThread.start();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获取到手指处的横坐标和纵坐标
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:

                if(MyMathsUtils.isInCircle(rockerCircleCenter,rockerRadius,new Point(x,y)))
                    isHoldingRocker=true;

                break;
            case MotionEvent.ACTION_UP:
                isHoldingRocker=false;
                distance=0;
                rockerCircleCenter.set(padCircleCenter.x,padCircleCenter.y);
                bindingCharacter.getSight().needMove=false;
                bindingCharacter.getSight().offX=0;
                bindingCharacter.getSight().offY=0;

                break;

            case MotionEvent.ACTION_MOVE:
                if(isHoldingRocker==false) {
                    break;
                }
                rockerCircleCenter= new ViewUtils().revisePointInCircleViewMovement(padCircleCenter,padRadius,new Point(x,y));
                distance= MyMathsUtils.getDistance(rockerCircleCenter,padCircleCenter);

        }

        return true;
    }


    class ReactToCharacter implements Runnable{



        @Override
        public void run() {




            while (!isStop) {
                if(distance==0){
                    try {
                        Thread.sleep(20);
                        continue;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                synchronized (bindingCharacter.getSight()) {
                    if (Math.abs(rockerCircleCenter.x - padCircleCenter.x) > padRadius * 0.1) {
                        bindingCharacter.getSight().offX = rockerCircleCenter.x - padCircleCenter.x;
                        bindingCharacter.getSight().needMove = true;
                    }
                    if (Math.abs(rockerCircleCenter.y - padCircleCenter.y) > padRadius * 0.1) {
                        bindingCharacter.getSight().offY = rockerCircleCenter.y - padCircleCenter.y;
                        bindingCharacter.getSight().needMove = true;
                    }
                }



                distance= MyMathsUtils.getDistance(rockerCircleCenter,padCircleCenter);
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
