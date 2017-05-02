package com.jedi.wolf_and_hunter.MyViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.jedi.wolf_and_hunter.MyViews.characters.BaseCharacterView;
import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;

/**
 * Created by Administrator on 2017/4/26.
 */

public class AttackRange extends View {
    public int nowAttackRadius;
    public int centerX, centerY;
    public int nowLeft;
    public int nowTop;
    public int nowRight;
    public int nowBottom;
    public FrameLayout.LayoutParams layoutParams;
    BaseCharacterView bindingCharacter;
    Paint borderPaint;
    public AttackRange(Context context) {
        super(context);
        if(GameBaseAreaActivity.myCharacter!=null)
            bindingCharacter=GameBaseAreaActivity.myCharacter;
        GameBaseAreaActivity.myCharacter.attackRange=this;
        init();
    }
    public AttackRange(Context context, BaseCharacterView character) {
        super(context);
        bindingCharacter=character;
        character.attackRange=this;
        init();
    }

    public AttackRange(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if(GameBaseAreaActivity.myCharacter!=null)
            bindingCharacter=GameBaseAreaActivity.myCharacter;
        init();
    }

    public AttackRange(Context context, @Nullable AttributeSet attrs,BaseCharacterView character) {
        super(context, attrs);
        bindingCharacter=character;
        init();
    }

    private void init(){

        if(bindingCharacter!=null) {
            nowAttackRadius = bindingCharacter.nowAttackRadius;
            centerX=bindingCharacter.centerX;
            centerY=bindingCharacter.centerY;
            nowLeft=centerX-nowAttackRadius;
            nowRight=centerX+nowAttackRadius;
            nowTop=centerY-nowAttackRadius;
            nowBottom=centerY+nowAttackRadius;
            DashPathEffect pathEffect=new DashPathEffect(new float[]{10,10},0);
            borderPaint = new Paint();
            borderPaint.setPathEffect(pathEffect);
            borderPaint.setColor(Color.RED);
            borderPaint.setAlpha(70);
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setStrokeWidth(5);
            borderPaint.setAntiAlias(true);
        }
        if(this.getLayoutParams()==null){
            FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin=bindingCharacter.centerX-nowAttackRadius;
            layoutParams.topMargin=bindingCharacter.centerY-nowAttackRadius;
            this.setLayoutParams(layoutParams);
            this.layoutParams=layoutParams;
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(nowAttackRadius,nowAttackRadius,nowAttackRadius,borderPaint);

        double cosAlpha=Math.cos(Math.toRadians(bindingCharacter.nowFacingAngle));
        double endX=cosAlpha*nowAttackRadius;

        double endY=Math.sqrt(nowAttackRadius*nowAttackRadius-endX*endX);
        if(bindingCharacter.nowFacingAngle>=180)
            endY=-endY;
        endX=endX+nowAttackRadius;
        endY=endY+nowAttackRadius;
        canvas.drawLine(nowAttackRadius,nowAttackRadius,(int)endX,(int)endY,borderPaint);
    }
}
