package com.jedi.wolf_and_hunter.MyViews.characters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.support.annotation.Px;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.jedi.wolf_and_hunter.MyViews.SightView;
import com.jedi.wolf_and_hunter.R;
import com.jedi.wolf_and_hunter.utils.ViewUtils;

/**
 * Created by Administrator on 2017/3/13.
 */

public class BaseCharacterView extends SurfaceView implements SurfaceHolder.Callback{
    private static final String TAG = "BaseCharacterView";
    //以下为移动相关
    public int lastX;
    public int lastY;
    public int offX;



    public int offY;
    public boolean hasChanged=false;
    //以下为角色基本共有属性
    public int centerX, centerY;
    public int nowLeft;
    public int nowTop;
    public int nowRight;
    public int nowBotton;
    public double directionAngle;
    public int characterBodySize;
    public int aroundSize;
    public int speed = 10;
    public SightView sight;
    //以下为绘图杂项
    public boolean isStop;
    public Bitmap arrowBitMap;
    public Matrix matrix;
    public SurfaceHolder mHolder;
    public int arrowBitmapWidth;
    public int arrowBitmapHeight;
    public FrameLayout.LayoutParams mLayoutParams;
    Paint paint;

    public FrameLayout.LayoutParams getmLayoutParams() {
        return mLayoutParams;
    }

    public void setmLayoutParams(FrameLayout.LayoutParams mLayoutParams) {
        this.mLayoutParams = mLayoutParams;
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
        characterBodySize=200;
        getHolder().addCallback(this);
        mHolder=getHolder();
        mHolder.addCallback(this);
        //以下两句必须在构造方法里做，否则各种奇妙poorguy
        mHolder.setFormat(PixelFormat.TRANSLUCENT);

        setZOrderOnTop(true);
    }

    public  void offsetLRTB(){

        offsetLeftAndRight(offX);
        offsetTopAndBottom(offY);


    }

    @Override
    public void offsetTopAndBottom(@Px int offset) {
        if(hasChanged==false)
            return;
        try {
            offset=ViewUtils.reviseOffY(this,(View)this.getParent(),Math.abs(offset)/offset*speed);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.offsetTopAndBottom(offset);
        nowTop=+this.getTop();
        nowBotton=this.getBottom();

    }

    @Override
    public void offsetLeftAndRight(@Px int offset) {
        if(hasChanged==false)
            return;
        try {
            offset=ViewUtils.reviseOffX(this,(View)this.getParent(),Math.abs(offset)/offset*speed);
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

        aroundSize=2*arrowBitmapWidth;

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

