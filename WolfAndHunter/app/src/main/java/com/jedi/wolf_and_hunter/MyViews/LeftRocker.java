package com.jedi.wolf_and_hunter.MyViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.jedi.wolf_and_hunter.R;
import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;
import com.jedi.wolf_and_hunter.utils.MyMathsUtils;
import com.jedi.wolf_and_hunter.utils.ViewUtils;

/**
 * Created by Administrator on 2017/3/29.
 */

public class LeftRocker extends JRocker  {

    public GameBaseAreaActivity.GameHandler gameHandler;
    boolean isStop=false;


    public LeftRocker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
        ReactToCharacter reactToCharacter=new ReactToCharacter();
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

                bindingCharacter.offX=rockerCircleCenter.x-padCircleCenter.x;
                bindingCharacter.offY=rockerCircleCenter.y-padCircleCenter.y;
                distance=MyMathsUtils.getDistance(rockerCircleCenter,padCircleCenter);
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
