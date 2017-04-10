package com.jedi.wolf_and_hunter.MyViews;

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

import com.jedi.wolf_and_hunter.R;
import com.jedi.wolf_and_hunter.utils.ViewUtils;

/**
 * Created by Administrator on 2017/3/13.
 */

public class SightView extends SurfaceView implements SurfaceHolder.Callback{
    public static final String TAG = "SurfaceView";
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
    public int speed = 10;
    public int sightSize;
    //以下为绘图杂项
    public boolean isStop;
    public Bitmap sightBitmap;
    public Matrix matrix;
    public SurfaceHolder mHolder;
    public int sightBitmapWidth;
    public int sightBitmapHeight;
    public FrameLayout.LayoutParams mLayoutParams;
    Paint paint;


    public SightView(Context context) {
        super(context);
        init();
    }
    public SightView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SightView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    private void init(){
        sightSize=100;
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

        paint=new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setAntiAlias(true);
        sightBitmap= BitmapFactory.decodeResource(getResources(), R.drawable.aim64);
        matrix = new Matrix();
//         缩放原图
        matrix.postScale((float)sightSize/sightBitmap.getWidth(), (float)sightSize/sightBitmap.getHeight());
        sightBitmap=Bitmap.createBitmap(sightBitmap, 0, 0, sightBitmap.getWidth(), sightBitmap.getHeight(),
                matrix, true);
        sightBitmapHeight=sightBitmap.getHeight();
        sightBitmapWidth= sightBitmap.getHeight();
        mLayoutParams = ( FrameLayout.LayoutParams)this.getLayoutParams();


        mLayoutParams.height=sightBitmapHeight;
        mLayoutParams.width=sightBitmapWidth;
        this.setLayoutParams(mLayoutParams);
        Thread drawThread=new Thread(new SightDraw());
        drawThread.start();
    }

    class SightDraw implements Runnable{



        @Override
        public void run() {


            while (!isStop) {

                Canvas canvas=getHolder().lockCanvas();
                try {
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//清除屏幕
                    canvas.drawBitmap(sightBitmap,0,0,null);

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

