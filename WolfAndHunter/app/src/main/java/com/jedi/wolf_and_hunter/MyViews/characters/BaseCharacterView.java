package com.jedi.wolf_and_hunter.MyViews.characters;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.jedi.wolf_and_hunter.utils.ViewUtils;

/**
 * Created by Administrator on 2017/3/13.
 */

public class BaseCharacterView extends SurfaceView {
    private static final String TAG = "BaseCharacterView";
    private int centerX, centerY;
    private int lastX;
    private int lastY;
    private int parentHeight;
    private int parentWidth;
    private int nowLeft;
    private int nowTop;
    private int nowRight;
    private int nowBotton;
    public int getCenterX() {
        return centerX;
    }

    public void setCenterX(int centerX) {
        this.centerX = centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public void setCenterY(int centerY) {
        this.centerY = centerY;
    }

    public int getLastX() {
        return lastX;
    }

    public void setLastX(int lastX) {
        this.lastX = lastX;
    }

    public int getLastY() {
        return lastY;
    }

    public void setLastY(int lastY) {
        this.lastY = lastY;
    }

    public int getParentHeight() {
        return parentHeight;
    }

    public void setParentHeight(int parentHeight) {
        this.parentHeight = parentHeight;
    }

    public int getParentWidth() {
        return parentWidth;
    }

    public void setParentWidth(int parentWidth) {
        this.parentWidth = parentWidth;
    }

    public int getCharacterSize() {
        return characterSize;
    }

    public void setCharacterSize(int characterSize) {
        this.characterSize = characterSize;
    }

    private int characterSize;

    public BaseCharacterView(Context context) {
        super(context);
        Log.i(TAG, "SlgBaseView()");
    }

    public BaseCharacterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.i(TAG, "SlgBaseView( , , )");
    }

    public BaseCharacterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i(TAG, "SlgBaseView( , )");
    }

    public void layout(int l, int t, int r, int b) {
        super.layout(l,t,r,b);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(wSize, hSize);
        centerX = wSize / 2;
        centerY = hSize / 2;

        Log.i(TAG, "onMeasure Run");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }





    public void getParentSize(){
        View parentView=(View)this.getParent();
        if(parentView==null)
            return;
        if(parentHeight==0||parentWidth==0) {
            parentHeight = parentView.getHeight();
            parentWidth = parentView.getWidth();
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        //获取到手指处的横坐标和纵坐标
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:

                lastX = x;
                lastY = y;

                break;

            case MotionEvent.ACTION_MOVE:
                int offX ;
                int offY ;
                int[] movementArr=new int[4];
                //计算移动的距离
                offX = x - lastX;
                offY = y - lastY;
                movementArr= ViewUtils.reviseViewMovement(this,(View)this.getParent(),offX,offY);
                nowLeft=movementArr[0];
                nowTop=movementArr[1];
                nowRight=movementArr[2];
                nowBotton=movementArr[3];
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)this.getLayoutParams();
                params.setMargins(movementArr[0],movementArr[1], movementArr[2], movementArr[3]);
                layout(nowLeft,nowTop, nowRight, nowBotton);
        }

        return true;
    }
}
