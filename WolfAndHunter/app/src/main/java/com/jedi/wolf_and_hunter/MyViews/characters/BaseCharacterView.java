package com.jedi.wolf_and_hunter.MyViews.characters;

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
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.jedi.wolf_and_hunter.MyViews.AttackRange;
import com.jedi.wolf_and_hunter.MyViews.GameMap;
import com.jedi.wolf_and_hunter.MyViews.JRocker;
import com.jedi.wolf_and_hunter.MyViews.SightView;
import com.jedi.wolf_and_hunter.MyViews.ViewRange;
import com.jedi.wolf_and_hunter.MyViews.landform.Landform;
import com.jedi.wolf_and_hunter.R;
import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;
import com.jedi.wolf_and_hunter.utils.MyMathsUtils;
import com.jedi.wolf_and_hunter.utils.ViewUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/3/13.
 */

public class BaseCharacterView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "BaseCharacterView";
    public static final int HIDDEN_LEVEL_NO_HIDDEN = 0;
    public static final int HIDDEN_LEVEL_LOW_HIDDEN = 1;
    public static final int HIDDEN_LEVEL_HIGHT_HIDDEN = 2;
    public static final int HIDDEN_LEVEL_ABSOLUTE_HIDDEN = 3;
    //以下为移动相关
    public int lastX;
    public int lastY;
    public int offX;
    public int offY;
    public boolean needMove = false;
    public boolean needTurned = false;
    //以下为角色基本共有属性
    public boolean hasUpdatedPosition = false;
    public int centerX = -1, centerY = -1;
    public int nowLeft = -1;
    public int nowTop = -1;
    public int nowRight = -1;
    public int nowBottom = -1;
    public float nowFacingAngle;
    public float nowViewAngle = 90;
    public int characterBodySize;
    public boolean isMyCharacter = false;
    public int nowHiddenLevel = 0;

    public final int defaultHiddenLevel = HIDDEN_LEVEL_NO_HIDDEN;
    public int nowAttackRadius = 600;
    public int nowViewRadius = 460;
    public int nowForceViewRadius = 200;
    public int speed = 10;
    public SightView sight;
    public AttackRange attackRange;
    public ViewRange viewRange;
    public int lastEffectX = -1;
    public int lastEffectY = -1;
    public Landform lastLandform;
    public int teamID;
    //以下为绘图杂项
    public Bitmap characterPic;
    public Matrix matrixForCP;
    int windowWidth;
    int windowHeight;
    public boolean isStop;
    public Bitmap arrowBitMap;
    public Matrix matrixForArrow;
    public SurfaceHolder mHolder;
    public int arrowBitmapWidth;
    public int arrowBitmapHeight;
    public FrameLayout.LayoutParams mLayoutParams;
    int borderWidth;
    Paint normalPaint;
    Paint alphaPaint;
    Paint transparentPaint;
    Paint textNormalPaint;
    Paint textAlphaPaint;
    public boolean isDead = false;
    public boolean isForceToBeSaw = false;//注意！这属性只针对玩家视觉，对AI判行为无效
    public HashSet<Integer> seeMeTeamIDs;
    public GameBaseAreaActivity.GameHandler gameHandler;
    public HashSet<BaseCharacterView> theyDiscoverMe;



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

    //sight仅对玩家操控角色有意义，不在这里统一创建
    private void init() {
        theyDiscoverMe = new HashSet<BaseCharacterView>();
        seeMeTeamIDs = new HashSet<Integer>();
        ViewUtils.initWindowParams(getContext());
        DisplayMetrics dm = ViewUtils.windowsDisplayMetrics;
        windowHeight = dm.heightPixels;
        windowWidth = dm.widthPixels;
        characterBodySize = 60;
        mHolder = getHolder();
        mHolder.addCallback(this);
        //以下两句必须在构造方法里做，否则各种奇妙poorguy
        mHolder.setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);

        borderWidth = 5;
        normalPaint = new Paint();
        normalPaint.setColor(Color.BLACK);
        normalPaint.setStyle(Paint.Style.STROKE);
        normalPaint.setStrokeWidth(borderWidth);
        normalPaint.setTextAlign(Paint.Align.CENTER);
        normalPaint.setAntiAlias(true);

        textNormalPaint = new Paint();
        textNormalPaint.setColor(Color.BLACK);
        textNormalPaint.setFakeBoldText(false);
        textNormalPaint.setTextAlign(Paint.Align.CENTER);
        textNormalPaint.setTextSize(characterBodySize * 2 / 3);
        textNormalPaint.setAntiAlias(true);


        transparentPaint = new Paint();
        transparentPaint.setColor(Color.BLACK);
        transparentPaint.setStyle(Paint.Style.STROKE);
        transparentPaint.setTextAlign(Paint.Align.CENTER);
        transparentPaint.setTextSize(characterBodySize);
        transparentPaint.setStrokeWidth(borderWidth);
        transparentPaint.setAlpha(0);
        transparentPaint.setAntiAlias(true);


        alphaPaint = new Paint();
        alphaPaint.setColor(Color.BLACK);
        alphaPaint.setStyle(Paint.Style.STROKE);
        alphaPaint.setTextAlign(Paint.Align.CENTER);
        alphaPaint.setTextSize(characterBodySize);
        alphaPaint.setStrokeWidth(borderWidth);
        alphaPaint.setAlpha(50);
        alphaPaint.setAntiAlias(true);

        textAlphaPaint = new Paint();
        textAlphaPaint.setColor(Color.BLACK);
        textNormalPaint.setFakeBoldText(false);
        textAlphaPaint.setTextAlign(Paint.Align.CENTER);
        int textSize = characterBodySize * 2 / 3;
        textAlphaPaint.setTextSize(textSize);
        textAlphaPaint.setAlpha(50);
        textAlphaPaint.setAntiAlias(true);

        matrixForCP = new Matrix();
        matrixForCP.postScale((float) (0.7 * characterBodySize / 76), (float) (0.7 * characterBodySize / 76));

        arrowBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);
        matrixForArrow = new Matrix();

        matrixForArrow.postScale((float) (0.5 * characterBodySize / arrowBitMap.getWidth()), (float) (0.5 * characterBodySize / arrowBitMap.getHeight()));
        arrowBitMap = Bitmap.createBitmap(arrowBitMap, 0, 0, arrowBitMap.getWidth(), arrowBitMap.getHeight(),
                matrixForArrow, true);
        arrowBitmapHeight = arrowBitMap.getHeight();
        arrowBitmapWidth = arrowBitMap.getHeight();
//        aroundSize = 2 * arrowBitmapWidth;

        mLayoutParams = (FrameLayout.LayoutParams) this.getLayoutParams();
        if (mLayoutParams == null)
            mLayoutParams = new FrameLayout.LayoutParams(characterBodySize, characterBodySize);
//        mLayoutParams.height = characterBodySize + aroundSize;
//        mLayoutParams.width = characterBodySize + aroundSize;
//        centerX = getLeft() + (characterBodySize + aroundSize) / 2;
//        centerY = getTop() + (characterBodySize + aroundSize) / 2;
        else {
            mLayoutParams.height = characterBodySize;
            mLayoutParams.width = characterBodySize;

        }

        if (nowLeft < 0 || nowTop < 0) {
            nowLeft = 100;
            nowTop = 100;
            nowRight = nowLeft + characterBodySize;
            nowBottom = nowTop + characterBodySize;
        }
        mLayoutParams.leftMargin = nowLeft;
        mLayoutParams.topMargin = nowTop;
        this.setLayoutParams(mLayoutParams);
        centerX = nowLeft + (characterBodySize) / 2;
        centerY = nowTop + (characterBodySize) / 2;
        new AttackRange(getContext(), this);
        new ViewRange(getContext(), this);


    }

    public SightView getSight() {
        return sight;
    }

    public void setSight(SightView sight) {
        this.sight = sight;
        sight.bindingCharacter = this;
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

    public void masterModeOffsetLRTBParams() {
        int nowOffX = offX;
        int nowOffY = offY;


        //根据设定速度修正位移量
        double offDistance = Math.sqrt(nowOffX * nowOffX + nowOffY * nowOffY);
        int nowSpeed = speed;
        if (offDistance < JRocker.padRadius * 3 / 4)
            nowSpeed = speed / 2;

        nowOffX = (int) (nowSpeed * nowOffX / offDistance);
        nowOffY = (int) (nowSpeed * nowOffY / offDistance);
        //保证不超出父View边界
        try {
            nowOffX = ViewUtils.reviseOffX(this, (View) this.getParent(), nowOffX);

            nowOffY = ViewUtils.reviseOffY(this, (View) this.getParent(), nowOffY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        nowLeft = nowLeft + nowOffX;
        nowTop = nowTop + nowOffY;
        nowRight = nowLeft + getWidth();
        nowBottom = nowTop + getHeight();
        //判定character位置修正是否在当前视窗内，若不在，根据sight和character位置修正视窗位置
        if (sight.isCharacterInWindow() == false) {

            sight.goWatchingCharacter();

        }

        //两种跟随修正
        if (sight.isSightInWindow() == false) {//当视点不在屏幕内，将保持视点角度平移至屏幕边缘

            sight.keepDirectionAndMove(sight.nowWindowLeft, sight.nowWindowTop, sight.nowWindowRight, sight.nowWindowBottom);

        } else if (sight.needMove == false)//当视点在屏幕内且右摇杆不在操作的时候，视点需要伴随角色平移
        {
            sight.followCharacter(nowOffX, nowOffY);
        }

//        changeRotate();


    }

    public void reactAIMove() {
        int nowOffX = offX;
        int nowOffY = offY;


        //根据设定速度修正位移量
        double offDistance = Math.sqrt(nowOffX * nowOffX + nowOffY * nowOffY);
        int nowSpeed = speed;
        if (offDistance > nowSpeed) {
            nowOffX = (int) (nowSpeed * nowOffX / offDistance);
            nowOffY = (int) (nowSpeed * nowOffY / offDistance);
        }
        //保证不超出父View边界
        try {
            nowOffX = ViewUtils.reviseOffX(this, (View) this.getParent(), nowOffX);

            nowOffY = ViewUtils.reviseOffY(this, (View) this.getParent(), nowOffY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        nowLeft = nowLeft + nowOffX;
        nowTop = nowTop + nowOffY;
        nowRight = nowLeft + getWidth();
        nowBottom = nowTop + getHeight();

    }


    //因更新不同步产生震动，懒得调了，废弃，留着玩儿
    @Deprecated
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
    @Deprecated
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
    @Deprecated
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
    @Deprecated
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

    public void changeThisCharacterOnLandformses() {

        int x = (centerX - 1) / 100;
        int y = (centerY - 1) / 100;
        if (x != lastEffectX || y != lastEffectY) {
            if (lastLandform != null)
                lastLandform.removeEffect(this);
            lastEffectX = x;
            lastEffectY = y;
        }
        if (GameMap.landformses != null) {
            if (GameMap.landformses.length <= y || GameMap.landformses[y].length <= x)
                return;
            Landform landform = GameMap.landformses[y][x];
            if (landform != null) {
                landform.effect(this);
                lastLandform = landform;
            } else {

            }
        } else {
            lastLandform = null;
        }
    }

    public void changeOtherCharacterState(BaseCharacterView otherCharacter) {
        //处理隐身

        boolean isInViewRange = isInViewRange(otherCharacter, nowViewRadius);
        boolean isDiscoverByMe=false;
        //在基本可视范围内
        if (isInViewRange) {
            //对方没有隐藏，直接可见
            if (otherCharacter.nowHiddenLevel == HIDDEN_LEVEL_NO_HIDDEN) {
                isDiscoverByMe = true;

            }
            //有隐身，判是否在强制可视范围内
            else if (otherCharacter.nowHiddenLevel > HIDDEN_LEVEL_NO_HIDDEN) {
                boolean isInForceViewRange = isInViewRange(otherCharacter, nowForceViewRadius);
                if (isInForceViewRange) {
                    isDiscoverByMe = true;
                } else {
                    isDiscoverByMe = false;
                }
            }
        }
        //不在基本可视范围内
        else{
            isDiscoverByMe = false;
        }


        if(isDiscoverByMe==true){//处理闯入我视觉范围的情况
            if(otherCharacter.seeMeTeamIDs.contains(this.teamID)) {//已经被本队发现
                if (otherCharacter.theyDiscoverMe.contains(this)==false) {//第一发现人不是自己
                    otherCharacter.theyDiscoverMe.add(this);
                }
            }else{//自己是第一发现人
                otherCharacter.seeMeTeamIDs.add(this.teamID);
                otherCharacter.theyDiscoverMe.add(this);

                    otherCharacter.isForceToBeSaw = true;
            }
        }else{//处理不在我视觉范围内的情况
            if(otherCharacter.seeMeTeamIDs.contains(this.teamID)){//已经被我队发现
                if(otherCharacter.theyDiscoverMe.contains(this)){
                    otherCharacter.theyDiscoverMe.remove(this);
                }
                boolean hasMyTeammate=false;
                for(BaseCharacterView c:otherCharacter.theyDiscoverMe){
                    if(c.teamID==this.teamID) {
                        hasMyTeammate = true;
                        break;
                    }
                }
                if(hasMyTeammate==false) {
                    otherCharacter.seeMeTeamIDs.remove(this.teamID);
                    otherCharacter.isForceToBeSaw = false;

                }
            }


        }


    }

    public void changeRotate() {
        int relateX = sight.centerX - this.centerX;
        int relateY = sight.centerY - this.centerY;

        double cos = relateX / Math.sqrt(relateX * relateX + relateY * relateY);
        double radian = Math.acos(cos);

        nowFacingAngle = MyMathsUtils.getAngleBetweenXAxus(relateX, relateY);
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


    public boolean isInViewRange(BaseCharacterView otherCharacter, int viewRadius) {
        boolean returnResult = false;

        boolean isInCircle = MyMathsUtils.isInCircle(new Point(this.centerX
                        , this.centerY)
                , viewRadius
                , new Point(otherCharacter.centerX
                        , otherCharacter.centerY)
        );
        if (isInCircle) {
            int relateX = otherCharacter.centerX - this.centerX;
            int relateY = otherCharacter.centerY - this.centerY;
            double cosAlpha = relateX / Math.sqrt(relateX * relateX + relateY * relateY);
            double alphaDegrees = Math.toDegrees(Math.acos(cosAlpha));
            if (relateY < 0)
                alphaDegrees = 360 - alphaDegrees;
            float startAngle = this.nowFacingAngle - this.nowViewAngle / 2;
            float endAngle = this.nowFacingAngle + this.nowViewAngle / 2;
            if (alphaDegrees > startAngle && alphaDegrees < endAngle)
                returnResult = true;
            if (startAngle < 0 && alphaDegrees > 360 + startAngle)
                returnResult = true;
            if (endAngle > 360 && alphaDegrees < endAngle - 360)
                returnResult = true;

        } else
            returnResult = false;

        return returnResult;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

//        setFocusable(true);
//        setFocusableInTouchMode(true);
//        setZOrderOnTop(true);
//        holder.setFormat(PixelFormat.TRANSLUCENT);


//        this.setLayoutParams(mLayoutParams);
//        gameHandler.sendEmptyMessage(GameBaseAreaActivity.GameHandler.ADD_ATTACT_RANGE);
//        gameHandler.sendEmptyMessage(GameBaseAreaActivity.GameHandler.ADD_VIEW_RANGE);
        Thread drawThread = new Thread(new CharacterDraw());
        drawThread.start();
    }

    class CharacterDraw implements Runnable {


        @Override
        public void run() {
            isStop = false;
            int i = 0;

            while (!isStop) {

                Canvas canvas = getHolder().lockCanvas();
                try {

                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//清除屏幕
//                    canvas.drawText(Float.toString(nowFacingAngle), (characterBodySize + aroundSize) / 2, (characterBodySize + aroundSize) / 2, paint);
//                    canvas.rotate(nowFacingAngle, (characterBodySize + aroundSize) / 2, (characterBodySize + aroundSize) / 2);
//                    canvas.drawRect(aroundSize / 2, aroundSize / 2, aroundSize / 2 + characterBodySize, aroundSize / 2 + characterBodySize, paint);
//                    canvas.drawCircle((characterBodySize + aroundSize) / 2, (characterBodySize + aroundSize) / 2, characterBodySize / 2, paint);
//                    canvas.drawBitmap(arrowBitMap, characterBodySize + aroundSize / 2, (characterBodySize + aroundSize - arrowBitmapHeight) / 2, null);
                    i++;
                    if (i % 90 == 89) {
                        isDead = false;

                    }
                    if (isDead) {
                        canvas.drawColor(Color.RED);

                        continue;
                    }



                    //这对象是myCharacter或队友情况下
                    if (isMyCharacter || teamID == GameBaseAreaActivity.myCharacter.teamID) {
                        if (nowHiddenLevel == HIDDEN_LEVEL_NO_HIDDEN) {
                            canvas.drawBitmap(characterPic, (int) (characterBodySize * 0.15), (int) (characterBodySize * 0.15), normalPaint);
                            canvas.rotate(nowFacingAngle, characterBodySize / 2, characterBodySize / 2);
//                        canvas.drawRect(0, 0, characterBodySize, characterBodySize, normalPaint);
                            canvas.drawCircle(characterBodySize / 2, characterBodySize / 2, characterBodySize / 2 - borderWidth, normalPaint);
                            canvas.drawBitmap(arrowBitMap, characterBodySize - arrowBitmapWidth, (characterBodySize - arrowBitmapHeight) / 2, normalPaint);

                        } else if (nowHiddenLevel > HIDDEN_LEVEL_NO_HIDDEN) {
                            canvas.drawBitmap(characterPic, (int) (characterBodySize * 0.15), (int) (characterBodySize * 0.15), alphaPaint);
                            canvas.rotate(nowFacingAngle, characterBodySize / 2, characterBodySize / 2);
//                        canvas.drawRect(0, 0, characterBodySize, characterBodySize, alphaPaint);
                            canvas.drawCircle(characterBodySize / 2, characterBodySize / 2, characterBodySize / 2 - borderWidth, alphaPaint);
                            canvas.drawBitmap(arrowBitMap, characterBodySize - arrowBitmapWidth, (characterBodySize - arrowBitmapHeight) / 2, alphaPaint);

                        }
                    } else {//不是队友
                        if (isForceToBeSaw) {
                            canvas.drawBitmap(characterPic, (int) (characterBodySize * 0.15), (int) (characterBodySize * 0.15), normalPaint);
                            canvas.rotate(nowFacingAngle, characterBodySize / 2, characterBodySize / 2);
//                        canvas.drawRect(0, 0, characterBodySize, characterBodySize, normalPaint);
                            canvas.drawCircle(characterBodySize / 2, characterBodySize / 2, characterBodySize / 2 - borderWidth, normalPaint);
                            canvas.drawBitmap(arrowBitMap, characterBodySize - arrowBitmapWidth, (characterBodySize - arrowBitmapHeight) / 2, normalPaint);
                            viewRange.isHidden = false;
                            attackRange.isHidden = true;

                        } else {
                            canvas.drawBitmap(characterPic, (int) (characterBodySize * 0.15), (int) (characterBodySize * 0.15), transparentPaint);
                            canvas.rotate(nowFacingAngle, characterBodySize / 2, characterBodySize / 2);
                            canvas.drawCircle(characterBodySize / 2, characterBodySize / 2, characterBodySize / 2 - borderWidth, transparentPaint);
                            canvas.drawBitmap(arrowBitMap, characterBodySize - arrowBitmapWidth, (characterBodySize - arrowBitmapHeight) / 2, transparentPaint);
                            viewRange.isHidden = true;
                            attackRange.isHidden = true;
                        }
                    }


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

    public void judgeFire() {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}

