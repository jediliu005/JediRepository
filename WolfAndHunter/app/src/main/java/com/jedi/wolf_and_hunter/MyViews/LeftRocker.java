package com.jedi.wolf_and_hunter.MyViews;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.FrameLayout;

import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;
import com.jedi.wolf_and_hunter.utils.MyMathsUtils;
import com.jedi.wolf_and_hunter.utils.ViewUtils;

/**
 * Created by Administrator on 2017/3/29.
 */

public class LeftRocker extends JRocker  {

    public GameBaseAreaActivity.GameHandler gameHandler;


    public LeftRocker(Context context, AttributeSet attrs) {

        super(context, attrs);
        FrameLayout.LayoutParams params=( FrameLayout.LayoutParams)getLayoutParams();
        params.gravity= Gravity.TOP | Gravity.LEFT;
        setLayoutParams(params);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获取到手指处的横坐标和纵坐标
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch(event.getAction())
        {

            case MotionEvent.ACTION_DOWN:

                if(MyMathsUtils.isInCircle(rockerCircleCenter,rockerRadius,new Point(x,y))) {
                    isHoldingRocker = true;
                    startCenterX=x;
                    startCenterY=y;
                }
                break;
            case MotionEvent.ACTION_UP:
                isHoldingRocker=false;
                distance=0;
                rockerCircleCenter.set(padCircleCenter.x,padCircleCenter.y);
                synchronized (bindingCharacter) {
                    bindingCharacter.needMove = false;
                    bindingCharacter.offX = 0;
                    bindingCharacter.offY = 0;
                    startCenterX = padCircleCenter.x;
                    startCenterY = padCircleCenter.y;
                    invalidate();
                    break;
                }
            case MotionEvent.ACTION_MOVE:
                if(isHoldingRocker==false) {
                    break;
                }
                int relateX=x-startCenterX;
                int relateY=y-startCenterY;
                Point newPosition=new Point(padCircleCenter.x+relateX,padCircleCenter.y+relateY);
                rockerCircleCenter= new ViewUtils().revisePointInCircleViewMovement(padCircleCenter,padRadius,newPosition);
                distance= MyMathsUtils.getDistance(rockerCircleCenter,padCircleCenter);
                synchronized (bindingCharacter) {
                    bindingCharacter.offX = rockerCircleCenter.x - padCircleCenter.x;
                    bindingCharacter.needMove = true;
                    bindingCharacter.offY = rockerCircleCenter.y - padCircleCenter.y;
                    bindingCharacter.needMove = true;
                }
                invalidate();

        }

        return true;
    }


}
