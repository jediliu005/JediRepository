package com.jedi.wolf_and_hunter.MyViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.jedi.wolf_and_hunter.MyViews.characters.BaseCharacterView;
import com.jedi.wolf_and_hunter.MyViews.landform.Landform;
import com.jedi.wolf_and_hunter.R;
import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;
import com.jedi.wolf_and_hunter.utils.ViewUtils;

/**
 * Created by Administrator on 2017/3/15.
 */

public class MapBaseFrame extends FrameLayout {
    private int lastX;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    private int lastY;
    public SightView mySight;
    public BaseCharacterView myCharacter;
    public LeftRocker leftRocker;
    public RightRocker rightRocker;
    public  Landform[][] landformses;

    public MapBaseFrame(@NonNull Context context) {
        super(context);

    }

    public MapBaseFrame(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta=null;


    }

    public MapBaseFrame(@NonNull Context context, @Nullable AttributeSet attrs, GameBaseAreaActivity.GameHandler gameHandler) {
        super(context, attrs);
        TypedArray ta=null;


    }

    public MapBaseFrame(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    public boolean onTouchEvent(MotionEvent event) {


        //获取到手指处的横坐标和纵坐标
        int x = (int) event.getX();
        int y = (int) event.getY();
        int offX ;
        int offY ;

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                lastX = x;
                lastY = y;

                break;

            case MotionEvent.ACTION_UP:
                lastX = 0;
                lastY = 0;
                offX = 0;
                offY = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                if (leftRocker.isHoldingRocker == false && rightRocker.isHoldingRocker == false) {
                    int[] movementArr = new int[4];
                    //计算移动的距离
                    offX = x - lastX;
                    offY = y - lastY;

                    movementArr = new ViewUtils().reviseTwoRectViewMovement(this, (View) getParent(), offX, offY);
                    layout(movementArr[0], movementArr[1], movementArr[2], movementArr[3]);
                }
        }
        return true;
    }


}
