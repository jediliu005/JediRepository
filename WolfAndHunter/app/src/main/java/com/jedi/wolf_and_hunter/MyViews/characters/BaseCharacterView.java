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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.jedi.wolf_and_hunter.MyViews.SightView;
import com.jedi.wolf_and_hunter.R;
import com.jedi.wolf_and_hunter.utils.ViewUtils;

import java.io.File;

/**
 * Created by Administrator on 2017/3/13.
 */

public class BaseCharacterView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "BaseCharacterView";

    //以下为移动相关
    public int lastX;
    public int lastY;
    public int offX;
    public int offY;
    public boolean needMove = false;
    public boolean needTurned = false;
    //以下为角色基本共有属性
    public int centerX, centerY;
    public int nowLeft;
    public int nowTop;
    public int nowRight;
    public int nowBottom;
    public float nowDegree;
    public double directionAngle;
    public int characterBodySize;
    //    public int aroundSize;
    public int speed = 10;
    private SightView sight;
    //以下为绘图杂项
    int windowWidth;
    int windowHeight;
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

    private void init() {
        ViewUtils.initWindowParams(getContext());
        DisplayMetrics dm = ViewUtils.windowsDisplayMetrics;
        windowHeight = dm.heightPixels;
        windowWidth = dm.widthPixels;
        characterBodySize = 50;
        getHolder().addCallback(this);
        mHolder = getHolder();
        mHolder.addCallback(this);
        //以下两句必须在构造方法里做，否则各种奇妙poorguy
        mHolder.setFormat(PixelFormat.TRANSLUCENT);

        setZOrderOnTop(true);
    }

    public SightView getSight() {
        return sight;
    }

    public void setSight(SightView sight) {
        this.sight = sight;
        sight.bindingCharacter = this;
    }

    public void offsetLRTBParams() {
        if (needMove == false)
            return;
        //获得视窗虚拟位置
        sight.updateCurrentWindowPosition();
        //根据设定速度修正位移量
        double offDistance = Math.sqrt(offX * offX + offY * offY);
        offX = (int) (speed * offX / offDistance);
        offY = (int) (speed * offY / offDistance);
        //保证不超出父View边界
        try {
            offX = ViewUtils.reviseOffX(this, (View) this.getParent(), offX);

            offY = ViewUtils.reviseOffY(this, (View) this.getParent(), offY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //判定character位置修正是否在当前视窗内，若不在，根据sight和character位置修正视窗位置
        if (sight.isCharacterInWindow() == false) {

            sight.goWatchingCharacter();
//            ((FrameLayout) getParent()).invalidate();

        }
        if (sight.isSightInWindow() == false) {

            sight.keepDirectionAndMove(sight.nowWindowLeft, sight.nowWindowTop, sight.nowWindowRight, sight.nowWindowBottom);


        }

//            centerX = getLeft() + getWidth() / 2 + offX;
//            centerY = getTop() + getHeight() / 2 + offX;
        mLayoutParams.leftMargin = getLeft() + offX;
        mLayoutParams.topMargin = getTop() + offY;
        this.setLayoutParams(mLayoutParams);

        if (sight.needMove == false)//当右摇杆不在操作的时候，视点需要伴随角色平移{
        {
            sight.needMove = true;

//            sight.followCharacter(offX, offY);
            sight.needMove = false;
        }


//        changeRotate();


    }

    //因更新不同步产生震动，废弃，留着玩儿
    public void offsetLRTB() {
        boolean isRightRockerWorking = false;


        double offDistance = Math.sqrt(offX * offX + offY * offY);
        offX = (int) (speed * offX / offDistance);
        offY = (int) (speed * offY / offDistance);
        if (sight.needMove)//当右摇杆不在操作的时候，视点需要伴随角色平移
            isRightRockerWorking = true;
        else
            sight.needMove = true;

        transition(offX, offY);
//        offsetLeftAndRight(offX);
//        offsetTopAndBottom(offY);

        if (isRightRockerWorking == false)
            sight.needMove = false;
        changeRotate();


    }

    //因更新不同步产生震动，废弃，留着玩儿
    public void transition(@Px int offsetX, @Px int offsetY) {
        if (needMove == false)
            return;
        int offSetCharacterX = offsetX;
        int offSetSightX = 0;
        int offSetCharacterY = offsetY;
        int offSetSightY = 0;
        try {
            offSetCharacterX = ViewUtils.reviseOffX(this, (View) this.getParent(), offsetX);
            if (offSetCharacterX != 0)
                offSetSightX = ViewUtils.reviseOffX(this.sight, (View) this.sight.getParent(), offsetX);

            offSetCharacterY = ViewUtils.reviseOffY(this, (View) this.getParent(), offsetY);
            if (offSetCharacterY != 0)
                offSetSightY = ViewUtils.reviseOffY(this.sight, (View) this.sight.getParent(), offsetY);
        } catch (Exception e) {
            e.printStackTrace();
        }


        super.layout(getLeft() + offSetCharacterX, getTop() + offSetCharacterY, getRight() + offSetCharacterX, getBottom() + offSetCharacterY);
        centerY = (getTop() + getBottom()) / 2;
        centerX = (getLeft() + getRight()) / 2;

        sight.transition(offSetSightX, offSetSightY);//视点可能需要跟随平移，视右摇杆而定

    }

    @Override
    public void offsetTopAndBottom(@Px int offset) {
        int offSetCharacter = offset;
        int offSetSight = 0;
        try {
            offSetCharacter = ViewUtils.reviseOffY(this, (View) this.getParent(), offset);
            if (offSetCharacter != 0)
                offSetSight = ViewUtils.reviseOffY(this.sight, (View) this.sight.getParent(), offset);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.offsetTopAndBottom(offSetCharacter);
        centerY = (getTop() + getBottom()) / 2;


        sight.offsetTopAndBottom(offSetSight);//视点可能需要跟随平移，视右摇杆而定

    }


    @Override
    public void offsetLeftAndRight(@Px int offset) {
        int offSetCharacter = offset;
        int offSetSight = 0;
        try {
            offSetCharacter = ViewUtils.reviseOffX(this, (View) this.getParent(), offset);
            if (offSetCharacter != 0)
                offSetSight = ViewUtils.reviseOffX(this.sight, (View) this.sight.getParent(), offset);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.offsetLeftAndRight(offSetCharacter);
        centerX = (getLeft() + getRight()) / 2;

        sight.offsetLeftAndRight(offSetSight);//视点可能需要跟随平移，视右摇杆而定


    }

    public void changeRotate() {
        int relateX = sight.centerX - this.centerX;
        int relateY = sight.centerY - this.centerY;

        double cos = relateX / Math.sqrt(relateX * relateX + relateY * relateY);
        double radian = Math.acos(cos);
        nowDegree = (float) (180 * radian / Math.PI);
        if (relateY < 0)
            nowDegree = 360 - nowDegree;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(wSize, hSize);


        Log.i(TAG, "onMeasure Run");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


//    public boolean onTouchEvent(MotionEvent event) {
//        //获取到手指处的横坐标和纵坐标
//        int x = (int) event.getX();
//        int y = (int) event.getY();
//
//        switch(event.getAction())
//        {
//            case MotionEvent.ACTION_DOWN:
//
//                lastX = x;
//                lastY = y;
//
//                break;
//
//            case MotionEvent.ACTION_MOVE:
//                int offX ;
//                int offY ;
//                int[] movementArr=new int[4];
//                //计算移动的距离
//                offX = x - lastX;
//                offY = y - lastY;
//                movementArr= new ViewUtils().reviseTwoRectViewMovement(this,(View)this.getParent(),offX,offY);
//                nowLeft=movementArr[0];
//                nowTop=movementArr[1];
//                nowRight=movementArr[2];
//                nowBotton=movementArr[3];
//
//                mLayoutParams = (FrameLayout.LayoutParams)this.getLayoutParams();
//                mLayoutParams.setMargins(movementArr[0],movementArr[1], movementArr[2], movementArr[3]);
//                layout(nowLeft,nowTop, nowRight, nowBotton);
//        }
//
//        return true;
//    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

//        setFocusable(true);
//        setFocusableInTouchMode(true);
//        setZOrderOnTop(true);
//        holder.setFormat(PixelFormat.TRANSLUCENT);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setAntiAlias(true);
        paint.setTextSize(30);

        arrowBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);
        matrix = new Matrix();
//         缩放原图
        matrix.postScale((float) (0.5 * characterBodySize / arrowBitMap.getWidth()), (float) (0.5 * characterBodySize / arrowBitMap.getHeight()));
        arrowBitMap = Bitmap.createBitmap(arrowBitMap, 0, 0, arrowBitMap.getWidth(), arrowBitMap.getHeight(),
                matrix, true);
        arrowBitmapHeight = arrowBitMap.getHeight();
        arrowBitmapWidth = arrowBitMap.getHeight();
//        aroundSize = 2 * arrowBitmapWidth;

        mLayoutParams = (FrameLayout.LayoutParams) this.getLayoutParams();
//        mLayoutParams.height = characterBodySize + aroundSize;
//        mLayoutParams.width = characterBodySize + aroundSize;
//        centerX = getLeft() + (characterBodySize + aroundSize) / 2;
//        centerY = getTop() + (characterBodySize + aroundSize) / 2;
        mLayoutParams.height = characterBodySize;
        mLayoutParams.width = characterBodySize;
        centerX = getLeft() + (characterBodySize) / 2;
        centerY = getTop() + (characterBodySize) / 2;
        this.setLayoutParams(mLayoutParams);
        Thread drawThread = new Thread(new CharacterDraw());
        drawThread.start();
    }

    class CharacterDraw implements Runnable {


        @Override
        public void run() {


            while (!isStop) {

                Canvas canvas = getHolder().lockCanvas();
                try {
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//清除屏幕
//                    canvas.drawText(Float.toString(nowDegree), (characterBodySize + aroundSize) / 2, (characterBodySize + aroundSize) / 2, paint);
//                    canvas.rotate(nowDegree, (characterBodySize + aroundSize) / 2, (characterBodySize + aroundSize) / 2);
//                    canvas.drawRect(aroundSize / 2, aroundSize / 2, aroundSize / 2 + characterBodySize, aroundSize / 2 + characterBodySize, paint);
//                    canvas.drawCircle((characterBodySize + aroundSize) / 2, (characterBodySize + aroundSize) / 2, characterBodySize / 2, paint);
//                    canvas.drawBitmap(arrowBitMap, characterBodySize + aroundSize / 2, (characterBodySize + aroundSize - arrowBitmapHeight) / 2, null);
                    canvas.rotate(nowDegree, characterBodySize / 2, characterBodySize / 2);
                    canvas.drawRect(0, 0, characterBodySize, characterBodySize, paint);
                    canvas.drawCircle(characterBodySize / 2, characterBodySize / 2, characterBodySize / 2, paint);
                    canvas.drawBitmap(arrowBitMap, characterBodySize - arrowBitmapWidth, (characterBodySize - arrowBitmapHeight) / 2, null);
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

