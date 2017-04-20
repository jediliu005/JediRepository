package com.jedi.wolf_and_hunter.MyViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.support.annotation.Px;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import com.jedi.wolf_and_hunter.MyViews.characters.BaseCharacterView;
import com.jedi.wolf_and_hunter.R;
import com.jedi.wolf_and_hunter.utils.ViewUtils;

/**
 * Created by Administrator on 2017/3/13.
 */

public class SightView extends SurfaceView implements SurfaceHolder.Callback {
    public static final String TAG = "SurfaceView";
    //以下为移动相关
    int windowWidth;
    int windowHeight;
    //这四个是可视界面看做相对this.parent的View而的虚拟出来的LRTB
    public boolean hasUpdatedWindowPosition = false;

    public int nowWindowLeft;
    public int nowWindowRight;
    public int nowWindowTop;
    public int nowWindowBottom;


    public int offX;
    public int offY;
    public boolean needMove = false;
    //以下为视点基本共有属性
    public boolean hasUpdatedPosition = false;
    public int centerX, centerY;
    public int nowLeft;
    public int nowTop;
    public int nowRight;
    public int nowBottom;
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
    public BaseCharacterView bindingCharacter;
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

    private void init() {
        ViewUtils.initWindowParams(getContext());
        DisplayMetrics dm = ViewUtils.windowsDisplayMetrics;
        windowHeight = dm.heightPixels;
        windowWidth = dm.widthPixels;
        if (sightSize == 0)
            sightSize = 50;
        getHolder().addCallback(this);
        mHolder = getHolder();
        mHolder.addCallback(this);
        //以下两句必须在构造方法里做，否则各种奇妙poorguy
        mHolder.setFormat(PixelFormat.TRANSLUCENT);

        setZOrderOnTop(true);
    }

    public FrameLayout.LayoutParams getmLayoutParams() {
        return mLayoutParams;
    }

    public void setmLayoutParams(FrameLayout.LayoutParams mLayoutParams) {
        this.mLayoutParams = mLayoutParams;
    }

    public void goWatchingCharacter() {
        updateNowPosition();
        updateNowWindowPosition();
        int newWindowLeft = nowWindowLeft;
        int newWindowTop = nowWindowTop;
        if (bindingCharacter.nowLeft < nowWindowLeft)
            newWindowLeft = bindingCharacter.nowLeft;
        if (bindingCharacter.nowRight > nowWindowRight)
            newWindowLeft = bindingCharacter.nowRight - windowWidth;
        if (bindingCharacter.nowBottom > nowWindowBottom)
            newWindowTop = bindingCharacter.nowBottom - windowHeight;
        if (bindingCharacter.nowTop < nowWindowTop)
            newWindowTop = bindingCharacter.nowTop;
        nowWindowLeft = newWindowLeft;
        nowWindowTop = newWindowTop;
        nowWindowRight = newWindowLeft + windowWidth;
        nowWindowBottom = newWindowTop + windowHeight;

//        offsetWindow(nowWindowLeft, nowWindowTop);


//        if (nowWindowLeft != -parent.getLeft() || nowWindowTop != -parent.getTop())
//            parent.layout(-nowWindowLeft, -nowWindowTop, -nowWindowLeft + parent.getWidth(), -nowWindowTop + parent.getHeight());

    }

    public void keepDirectionAndMove(int limitLeft, int limitTop, int limitRight, int limitBottom) {
        updateNowPosition();
        updateNowWindowPosition();
        //注意添加sight本身宽度修正
        int relateLimitLeft = limitLeft + getWidth() / 2 - bindingCharacter.centerX;
        int relateLimitTop = limitTop + getHeight() / 2 - bindingCharacter.centerY;
        int relateLimitRight = limitRight - getWidth() / 2 - bindingCharacter.centerX;
        int relateLimitBottom = limitBottom - getHeight() / 2 - bindingCharacter.centerY;

        int resultRelateX = 0;
        int resultRelateY = 0;
        int relateX = this.centerX - bindingCharacter.centerX;
        int relateY = this.centerY - bindingCharacter.centerY;
        if (relateX == 0 && relateY == 0)
            return;

        if (relateX == 0) {
            resultRelateX = 0;
            if (relateY > 0)
                resultRelateY = relateLimitBottom;
            else {
                resultRelateY = relateLimitTop;
            }
        } else if (relateY == 0) {
            resultRelateY = 0;
            if (relateX > 0)
                resultRelateX = relateLimitRight;
            else {
                resultRelateX = relateLimitLeft;
            }
        } else {

            double tanAlpha = 0;
            try {

                tanAlpha = (double) relateY / relateX;
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (tanAlpha > 0) {
                if (relateX > 0) {
                    //BR
                    resultRelateY = (int) (tanAlpha * relateLimitRight);

                    if (resultRelateY > relateLimitBottom) {
                        resultRelateY = relateLimitBottom;
                        resultRelateX = (int) (relateLimitBottom / tanAlpha);
                    } else {
                        resultRelateX = relateLimitRight;
                    }

                } else if (relateX < 0) {
                    //TL
                    resultRelateY = (int) (tanAlpha * relateLimitLeft);

                    if (resultRelateY < relateLimitTop) {
                        resultRelateY = relateLimitTop;
                        resultRelateX = (int) (relateLimitTop / tanAlpha);
                    } else {
                        resultRelateX = relateLimitLeft;
                    }
                }

            } else if (tanAlpha < 0) {
                if (relateX > 0) {
                    //TR
                    resultRelateY = (int) (tanAlpha * relateLimitRight);

                    if (resultRelateY < relateLimitTop) {
                        resultRelateY = relateLimitTop;
                        resultRelateX = (int) (relateLimitTop / tanAlpha);
                    } else {
                        resultRelateX = relateLimitRight;
                    }
                } else if (relateX < 0) {
                    //BL
                    resultRelateY = (int) (tanAlpha * relateLimitLeft);

                    if (resultRelateY > relateLimitBottom) {
                        resultRelateY = relateLimitBottom;
                        resultRelateX = (int) (relateLimitBottom / tanAlpha);
                    } else {
                        resultRelateX = relateLimitLeft;
                    }
                }

            }
//            else {
//                Log.i("", "");
//            }
//            if (resultRelateX == 0 || resultRelateY == 0) {
//                Log.i("", "");
//            }
        }
        int newCenterX = bindingCharacter.centerX + resultRelateX;
        int newCenterY = bindingCharacter.centerY + resultRelateY;
//        mLayoutParams.leftMargin=bindingCharacter.centerX+resultRelateX-this.getWidth()/2;
//        mLayoutParams.topMargin=bindingCharacter.centerY+resultRelateY-this.getHeight()/2;

        nowLeft = newCenterX - getWidth() / 2;
        nowTop = newCenterY - getHeight() / 2;
        nowRight = nowLeft + getWidth();
        nowBottom = nowTop + getHeight();


    }

    public void updateNowPosition() {
        if (hasUpdatedPosition == true)
            return;

        nowLeft = getLeft();
        nowTop = getTop();
        nowRight = getRight();
        nowBottom = getBottom();
        hasUpdatedPosition = true;
    }

    public void updateNowWindowPosition() {
        if (hasUpdatedWindowPosition == true)
            return;
        FrameLayout parent = (FrameLayout) this.getParent();
        nowWindowLeft = -parent.getLeft();
        nowWindowTop = -parent.getTop();
        nowWindowRight = nowWindowLeft + windowWidth;
        nowWindowBottom = nowWindowTop + windowHeight;
        hasUpdatedWindowPosition = true;
    }

    public boolean isCharacterInWindow() {
        if (bindingCharacter.nowLeft < nowWindowLeft
                || bindingCharacter.nowRight > nowWindowRight
                || bindingCharacter.nowBottom > nowWindowBottom
                || bindingCharacter.nowTop < nowWindowTop) {
            return false;
        }
        return true;
    }

    public boolean isSightInWindow() {
        updateNowPosition();
        updateNowWindowPosition();
        if (this.nowLeft < nowWindowLeft
                || this.nowRight > nowWindowRight
                || this.nowBottom > nowWindowBottom
                || this.nowTop < nowWindowTop) {
            return false;
        }
        return true;
    }

    public void followCharacter(int CharacterOffX, int CharacterOffY) {
        updateNowPosition();
        updateNowWindowPosition();
        int followX = CharacterOffX;
        int followY = CharacterOffY;

        //控制位置不超出父View
        try {
            followX = ViewUtils.reviseOffX(this, (View) this.getParent(), followX);
            followY = ViewUtils.reviseOffY(this, (View) this.getParent(), followY);
            nowLeft = nowLeft + followX;
            nowRight=nowLeft+getWidth();
            nowTop = nowTop + followY;
            nowBottom=nowTop+getHeight();
            if (nowLeft < nowWindowLeft) {
                nowWindowLeft = nowLeft;
                nowWindowRight = nowWindowLeft + windowWidth;
            } else if (nowRight > nowWindowRight) {
                nowWindowRight = nowRight;
                nowWindowLeft = nowWindowRight - windowWidth;
            }
            if (nowTop < nowWindowTop) {
                nowWindowTop = nowTop;
                nowWindowBottom = nowWindowTop + windowHeight;
            } else if (nowBottom > nowWindowBottom) {
                nowWindowBottom = nowBottom;
                nowWindowTop = nowWindowBottom - windowHeight;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void offsetLRTBParams(boolean isMyCharacterMoving) {
        updateNowPosition();
        updateNowWindowPosition();
        int oldWindowLeft = nowWindowLeft;
        int oldWindowTop = nowWindowTop;

        double offDistance = Math.sqrt(offX * offX + offY * offY);
        offX = (int) (speed * offX / offDistance);
        offY = (int) (speed * offY / offDistance);

        try {

            //控制位置不超出父View
            offX = ViewUtils.reviseOffX(this, (View) this.getParent(), offX);
            offY = ViewUtils.reviseOffY(this, (View) this.getParent(), offY);

//            centerX = getLeft() + getWidth() / 2 + offX;
//            centerY = getTop() + getHeight() / 2 + offX;
            //控制位置不超出可视范围

            nowLeft = nowLeft + offX;
            nowTop = nowTop + offY;
            nowRight = nowLeft + getWidth();
            nowBottom = nowTop + getHeight();
            if (isMyCharacterMoving) {
                movingNearCharacter();
            } else {

                if (this.nowLeft  < nowWindowLeft)
                    nowWindowLeft = this.nowLeft;
                if (this.nowRight> nowWindowRight)
                    nowWindowLeft = this.nowRight - windowWidth;
                if (this.nowTop  < nowWindowTop)
                    nowWindowTop = this.nowTop;
                if (this.nowBottom  > nowWindowBottom)
                    nowWindowTop = this.nowBottom - windowHeight;
                nowWindowRight = nowWindowLeft + windowWidth;
                nowWindowBottom = nowWindowTop + windowHeight;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void movingNearCharacter() {
        int newLRRelateX = Math.abs((nowLeft + nowRight) / 2 - (bindingCharacter.nowLeft + bindingCharacter.nowRight) / 2) + (getWidth() + bindingCharacter.getWidth()) / 2 ;
        int newTBRelateY = Math.abs((nowTop + nowBottom) / 2 - (bindingCharacter.nowTop + bindingCharacter.nowBottom) / 2) + (getHeight() + bindingCharacter.getHeight()) / 2 ;
        if (newLRRelateX > windowWidth) {
            if (nowRight > bindingCharacter.nowLeft) {
                nowRight = bindingCharacter.nowLeft + windowWidth;
                nowLeft = nowRight - getWidth();
            } else if (nowLeft < bindingCharacter.nowRight) {
                nowLeft = bindingCharacter.nowRight - windowWidth;
                nowRight = nowLeft + getWidth();
            }
        } else {

            if (nowLeft < nowWindowLeft) {
                nowWindowLeft = nowLeft;
                nowWindowRight = nowWindowLeft + windowWidth;
            } else if (nowRight > nowWindowRight) {
                nowWindowRight = nowRight;
                nowWindowLeft = nowWindowRight - windowWidth;
            }
        }


        if (newTBRelateY > windowHeight) {
            if (nowBottom > bindingCharacter.nowTop) {
                nowBottom = bindingCharacter.nowTop + windowHeight;
                nowTop = nowBottom - getHeight();
            } else if (nowTop < bindingCharacter.nowBottom) {
                nowTop = bindingCharacter.nowBottom - windowHeight;
                nowBottom = nowTop + getHeight();
            }
        } else {

            if (nowTop < nowWindowTop) {
                nowWindowTop = nowTop;
                nowWindowBottom = nowWindowTop + windowHeight;
            } else if (nowBottom > nowWindowBottom) {
                nowWindowBottom = nowBottom;
                nowWindowTop = nowWindowBottom - windowHeight;
            }
        }


    }


    //此方法已废弃
    @Deprecated
    public void offsetLRTB(boolean isMyCharacterMoving) {
        updateNowWindowPosition();
        int oldWindowLeft = nowWindowLeft;
        int oldWindowTop = nowWindowTop;
        boolean canSeeCharacter = true;
        if (bindingCharacter.getRight() < nowWindowLeft
                || bindingCharacter.getLeft() > nowWindowRight
                || bindingCharacter.getTop() > nowWindowBottom
                || bindingCharacter.getBottom() < nowWindowTop)
            canSeeCharacter = false;
        if (isMyCharacterMoving && !canSeeCharacter) {
            goWatchingCharacter();
        }
        double offDistance = Math.sqrt(offX * offX + offY * offY);
        offX = (int) (speed * offX / offDistance);
        offY = (int) (speed * offY / offDistance);

        try {

            //控制位置不超出父View
//            offX = ViewUtils.reviseOffX(this, (View) this.getParent(), offX);
//            offY = ViewUtils.reviseOffY(this, (View) this.getParent(), offY);


            //控制位置不超出可视范围

            if (this.getLeft() + offX < nowWindowLeft)
                nowWindowLeft = this.getLeft() + offX;
            if (this.getRight() + offX > nowWindowRight)
                nowWindowLeft = this.getRight() + offX - windowWidth;
            if (this.getTop() + offY < nowWindowTop)
                nowWindowTop = this.getTop() + offY;
            if (this.getBottom() + offY > nowWindowBottom)
                nowWindowTop = this.getBottom() + offY - windowHeight;

            nowWindowRight = nowWindowLeft + windowWidth;
            nowWindowBottom = nowWindowTop + windowHeight;
            if (nowWindowLeft != oldWindowLeft || nowWindowTop != oldWindowTop) {
                offsetWindow(nowWindowLeft, nowWindowTop);
            }
//             transition(offX,offY);
            offsetLeftAndRight(offX);
            offsetTopAndBottom(offY);
            getmLayoutParams().leftMargin = getLeft();
            getmLayoutParams().topMargin = getTop();


            if (nowWindowBottom - nowWindowTop != windowHeight || nowWindowRight - nowWindowLeft != windowWidth) {
                Log.i("", "");
            }
            FrameLayout parent = (FrameLayout) getParent();
            if (parent.getWidth() != 2000 || parent.getHeight() != 1500) {
                Log.i("", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Deprecated
    public void transition(@Px int offsetX, @Px int offsetY) {
        if (needMove == false)
            return;
        int offSetSightX = offsetX;
        int offSetSightY = offsetY;
        try {
            offSetSightX = ViewUtils.reviseOffX(this, (View) this.getParent(), offsetX);

            offSetSightY = ViewUtils.reviseOffY(this, (View) this.getParent(), offsetY);
        } catch (Exception e) {
            e.printStackTrace();
        }


        super.layout(getLeft() + offSetSightX, getTop() + offSetSightY, getRight() + offSetSightX, getBottom() + offSetSightY);
        getmLayoutParams().leftMargin = getLeft();
        getmLayoutParams().topMargin = getTop();
        centerY = (getTop() + getBottom()) / 2;
        centerX = (getLeft() + getRight()) / 2;


    }
    @Deprecated
    @Override
    public void offsetTopAndBottom(@Px int offset) {
        if (needMove == false)
            return;
        //控制位置不超出父View
        try {
            offY = ViewUtils.reviseOffY(this, (View) this.getParent(), offset);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.offsetTopAndBottom(offY);

        centerY = (getTop() + getBottom()) / 2;
    }
    @Deprecated
    @Override
    public void offsetLeftAndRight(@Px int offset) {
        if (needMove == false)
            return;
        try {
            offX = ViewUtils.reviseOffX(this, (View) this.getParent(), offset);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.offsetLeftAndRight(offX);

        centerX = (getLeft() + getRight()) / 2;


    }

    public void offsetWindow(int windowLeft, int windowTop) {
        FrameLayout parent = (FrameLayout) this.getParent();
        FrameLayout.LayoutParams parentParams = (FrameLayout.LayoutParams) parent.getLayoutParams();

        int parentNewLeft = -windowLeft;
        int parentNewTop = -windowTop;
        parentParams.leftMargin = parentNewLeft;
        parentParams.topMargin = parentNewTop;
        parent.setLayoutParams(parentParams);

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
//                nowBottom=movementArr[3];
//
//                mLayoutParams = (FrameLayout.LayoutParams)this.getLayoutParams();
//                mLayoutParams.setMargins(movementArr[0],movementArr[1], movementArr[2], movementArr[3]);
//                layout(nowLeft,nowTop, nowRight, nowBottom);
//        }
//
//        return true;
//    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setAntiAlias(true);
        sightBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.aim64);
        matrix = new Matrix();
//         缩放原图
        matrix.postScale((float) sightSize / sightBitmap.getWidth(), (float) sightSize / sightBitmap.getHeight());
        sightBitmap = Bitmap.createBitmap(sightBitmap, 0, 0, sightBitmap.getWidth(), sightBitmap.getHeight(),
                matrix, true);
        sightBitmapHeight = sightBitmap.getHeight();
        sightBitmapWidth = sightBitmap.getHeight();
        mLayoutParams = (FrameLayout.LayoutParams) this.getLayoutParams();


        mLayoutParams.height = sightBitmapHeight;
        mLayoutParams.width = sightBitmapWidth;
        centerX = sightBitmapHeight / 2;
        centerY = sightBitmapWidth / 2;
        this.setLayoutParams(mLayoutParams);

//        FrameLayout parent = (FrameLayout) this.getParent();
//        nowWindowLeft = -parent.getLeft();
//        nowWindowTop = -parent.getTop();
//        nowWindowRight =  windowWidth- parent.getLeft();
//        nowWindowBottom =  windowHeight- parent.getTop();
        Thread drawThread = new Thread(new SightDraw());
        drawThread.start();
    }

    class SightDraw implements Runnable {


        @Override
        public void run() {


            while (!isStop) {

                Canvas canvas = getHolder().lockCanvas();
                try {
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//清除屏幕
                    canvas.drawBitmap(sightBitmap, 0, 0, null);

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

