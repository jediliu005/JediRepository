package com.jedi.wolf_and_hunter.MyViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Bundle;
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

public class JRocker extends SurfaceView implements SurfaceHolder.Callback {
    static final int ROCKER_TYPE_LEFT=1;
    static final int ROCKER_TYPE_RIGHT=2;
    int rockerType;
    public GameBaseAreaActivity.GameHandler gameHandler;
    JRocker jRocker;
    Point padCircleCenter=new Point();
    Point rockerCircleCenter=new Point();
    int windowWidth;
    int windowHeight;
    int padRadius;
    int rockerRadius;
    float radian=-100;
    double distance=0;
    Paint paintForPad;
    Paint paintForRocker;
    SurfaceHolder mHolder;
    boolean isHoldRocker=false;
    boolean isStop=false;
    public void init(){
        mHolder=getHolder();
        mHolder.addCallback(this);
        ViewUtils.initWindowParams(getContext());
        DisplayMetrics dm=ViewUtils.windowsDisplayMetrics;

//        setZOrderMediaOverlay(true);
        //以下这两个因为横屏关系，必须宽高调换，
        windowWidth=dm.heightPixels;
        windowHeight=dm.widthPixels;
        paintForPad = new Paint();
        paintForPad.setColor(Color.RED);
        paintForPad.setStyle(Paint.Style.STROKE);
        paintForPad.setAntiAlias(true);
        paintForPad.setStrokeWidth(10);

        paintForRocker = new Paint();
        paintForRocker.setColor(Color.RED);
        paintForRocker.setStyle(Paint.Style.FILL);
        paintForRocker.setAntiAlias(true);
        mHolder.setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);
    }
    public JRocker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

        TypedArray ta=context.obtainStyledAttributes(attrs,R.styleable.JRocker);
        padRadius = (int)(ta.getDimension(R.styleable.JRocker_pad_radius,windowWidth/6));
        rockerRadius =(int)(ta.getDimension(R.styleable.JRocker_rocker_radius,padRadius/3)) ;
        padCircleCenter.set(padRadius+rockerRadius,padRadius+rockerRadius);
        rockerCircleCenter.set(padRadius+rockerRadius,padRadius+rockerRadius);




    }

    public JRocker(Context context) {
        super(context);
    }

    /**
     * 等重构再说。。。
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mode=MeasureSpec.getMode(widthMeasureSpec);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        jRocker=this;
        isStop=false;

        setFocusable(true);
        setFocusableInTouchMode(true);

        FrameLayout.LayoutParams paramsForJRocker = ( FrameLayout.LayoutParams)jRocker.getLayoutParams();
        paramsForJRocker.height=(int)(2*padRadius+2*rockerRadius);
        paramsForJRocker.width=(int)(2*padRadius+2*rockerRadius);
        //在这里，新版系统必须重新setLayoutParams才能成功调整,19不用，还是要加。。。
        this.setLayoutParams(paramsForJRocker);
        DrawRocker drawLeftRocker=new DrawRocker();
        Thread drawThread=new Thread(drawLeftRocker);
        drawThread.start();
        ReactToCharacter reactToCharacter=new ReactToCharacter();
        Thread reactThread=new Thread(reactToCharacter);
        reactThread.start();

    }




    class DrawRocker implements Runnable{

        @Override
        public void run() {
            Canvas canvas = null;
            jRocker.getX();
            jRocker.getY();

            while (!isStop) {
                try {

                    canvas = mHolder.lockCanvas();
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//清除屏幕

//                    canvas.drawColor(Color.GREEN);
                    canvas.drawCircle( padCircleCenter.x, padCircleCenter.y,padRadius, paintForPad);
                    canvas.drawCircle( rockerCircleCenter.x, rockerCircleCenter.y,rockerRadius, paintForRocker);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null) {
                        mHolder.unlockCanvasAndPost(canvas);
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

    class ReactToCharacter implements Runnable{



        @Override
        public void run() {


            while (!isStop) {
                if(distance==0&&radian==-100){
                    try {
                        Thread.sleep(30);
                        continue;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Message msg=gameHandler.obtainMessage();
                msg.what= GameBaseAreaActivity.GameHandler.FROM_ROCKER_LEFT;
                msg.getData().putDouble("moveDis",distance);
                msg.getData().putInt("relateX",rockerCircleCenter.x-padCircleCenter.x);
                msg.getData().putInt("relateY",rockerCircleCenter.y-padCircleCenter.y);
                msg.sendToTarget();
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
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
                        isHoldRocker=true;

                    break;
                case MotionEvent.ACTION_UP:
                    isHoldRocker=false;
                    distance=0;
                    rockerCircleCenter.set(padCircleCenter.x,padCircleCenter.y);

                    break;

                case MotionEvent.ACTION_MOVE:
                    if(isHoldRocker==false) {
                        break;
                    }
//                    int offX = x - lastX;
//                    int offY = y - lastY;
//                    int newRockerX=rockerCircleCenter.x+offX;
//                    int newRockerY=rockerCircleCenter.y+offY;
//                    Point toPoint=new Point(newRockerX,newRockerY);
                    rockerCircleCenter= new ViewUtils().revisePointInCircleViewMovement(padCircleCenter,padRadius,new Point(x,y));
                    distance= MyMathsUtils.getDistance(rockerCircleCenter,padCircleCenter);

            }
            return true;
        }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isStop=true;
    }
}
