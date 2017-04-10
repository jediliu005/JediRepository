package com.jedi.wolf_and_hunter.MyViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;
import com.jedi.wolf_and_hunter.utils.MyMathsUtils;

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

//                bindingCharacter.offX=rockerCircleCenter.x-padCircleCenter.x;
//                bindingCharacter.offY=rockerCircleCenter.y-padCircleCenter.y;
//                distance= MyMathsUtils.getDistance(rockerCircleCenter,padCircleCenter);
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
