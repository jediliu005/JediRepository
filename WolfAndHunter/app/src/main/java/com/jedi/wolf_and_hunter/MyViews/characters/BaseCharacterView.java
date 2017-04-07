package com.jedi.wolf_and_hunter.MyViews.characters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.support.annotation.Px;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.jedi.wolf_and_hunter.R;
import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;
import com.jedi.wolf_and_hunter.utils.ViewUtils;

/**
 * Created by Administrator on 2017/3/13.
 */

public class BaseCharacterView extends SurfaceView implements SurfaceHolder.Callback{
    private static final String TAG = "BaseCharacterView";
    //以下为移动相关
    private int lastX;
    private int lastY;
    //以下为角色基本共有属性
    private int centerX, centerY;
    private int nowLeft;
    private int nowTop;
    private int nowRight;
    private int nowBotton;
    private int characterBodySize;
    private int aroundSize;
    private int speed = 10;
    //以下为绘图杂项
    private boolean isStop;
    private Bitmap arrowBitMap;
    private Matrix matrix;
    private SurfaceHolder mHolder;
    private int arrowBitmapWidth;
    private int arrowBitmapHeight;
    private FrameLayout.LayoutParams mLayoutParams;
    Paint paint;
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

    public FrameLayout.LayoutParams getmLayoutParams() {
        return mLayoutParams;
    }

    public void setmLayoutParams(FrameLayout.LayoutParams mLayoutParams) {
        this.mLayoutParams = mLayoutParams;
    }

    public int getNowLeft() {
        return nowLeft;
    }

    public void setNowLeft(int nowLeft) {
        this.nowLeft = nowLeft;
    }

    public int getNowTop() {
        return nowTop;
    }

    public void setNowTop(int nowTop) {
        this.nowTop = nowTop;
    }

    public int getNowRight() {
        return nowRight;
    }

    public void setNowRight(int nowRight) {
        this.nowRight = nowRight;
    }

    public int getNowBotton() {
        return nowBotton;
    }

    public void setNowBotton(int nowBotton) {
        this.nowBotton = nowBotton;
    }

    public int getCharacterBodySize() {
        return characterBodySize;
    }

    public void setCharacterBodySize(int characterBodySize) {
        this.characterBodySize = characterBodySize;
    }

    public int getAroundSize() {
        return aroundSize;
    }

    public void setAroundSize(int aroundSize) {
        this.aroundSize = aroundSize;
    }

    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public BaseCharacterView(Context context) {
        super(context);
        init();
    }
    public BaseCharacterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseCharacterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    private void init(){
        getHolder().addCallback(this);
        mHolder=getHolder();
        mHolder.addCallback(this);
        //以下两句必须在构造方法里做，否则各种奇妙poorguy
        mHolder.setFormat(PixelFormat.TRANSLUCENT);

        setZOrderOnTop(true);
    }

    @Override
    public void offsetTopAndBottom(@Px int offset) {
        try {
            offset=ViewUtils.reviseOffY(this,(View)this.getParent(),offset);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.offsetTopAndBottom(offset);
        nowTop=+this.getTop();
        nowBotton=this.getBottom();
    }

    @Override
    public void offsetLeftAndRight(@Px int offset) {
        try {
            offset=ViewUtils.reviseOffX(this,(View)this.getParent(),offset);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.offsetLeftAndRight(offset);
        nowLeft=this.getLeft();
        nowRight=+this.getRight();

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
                movementArr= new ViewUtils().reviseTwoRectViewMovement(this,(View)this.getParent(),offX,offY);
                nowLeft=movementArr[0];
                nowTop=movementArr[1];
                nowRight=movementArr[2];
                nowBotton=movementArr[3];

                mLayoutParams = (FrameLayout.LayoutParams)this.getLayoutParams();
                mLayoutParams.setMargins(movementArr[0],movementArr[1], movementArr[2], movementArr[3]);
                layout(nowLeft,nowTop, nowRight, nowBotton);
        }

        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

//        setFocusable(true);
//        setFocusableInTouchMode(true);
//        setZOrderOnTop(true);
//        holder.setFormat(PixelFormat.TRANSLUCENT);
        paint=new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setAntiAlias(true);
        arrowBitMap= BitmapFactory.decodeResource(getResources(), R.drawable.arrow);
        matrix = new Matrix();
//         缩放原图
        matrix.postScale(0.3f, 0.3f);
        arrowBitMap=Bitmap.createBitmap(arrowBitMap, 0, 0, arrowBitMap.getWidth(), arrowBitMap.getHeight(),
                matrix, true);
        arrowBitmapHeight=arrowBitMap.getHeight();
        arrowBitmapWidth= arrowBitMap.getHeight();
        mLayoutParams = ( FrameLayout.LayoutParams)this.getLayoutParams();
        characterBodySize=200;
        aroundSize=4*arrowBitmapWidth;

        mLayoutParams.height=characterBodySize+aroundSize;
        mLayoutParams.width=characterBodySize+aroundSize;
        this.setLayoutParams(mLayoutParams);
        Thread drawThread=new Thread(new CharacterDraw());
        drawThread.start();
    }

    class CharacterDraw implements Runnable{



        @Override
        public void run() {


            while (!isStop) {

                Canvas canvas=getHolder().lockCanvas();
                try {
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//清除屏幕
                    canvas.drawCircle((characterBodySize+aroundSize)/2,(characterBodySize+aroundSize)/2,characterBodySize/2,paint);
                    canvas.drawBitmap(arrowBitMap,characterBodySize+aroundSize/2,(characterBodySize+aroundSize-arrowBitmapHeight)/2,null);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null) {
                        getHolder().unlockCanvasAndPost(canvas);
                    }
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}

