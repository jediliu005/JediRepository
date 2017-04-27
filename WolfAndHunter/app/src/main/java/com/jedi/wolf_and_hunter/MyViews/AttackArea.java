package com.jedi.wolf_and_hunter.MyViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
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

public class AttackArea extends View {
    public int radius;
    public int centerX, centerY;
    public int nowLeft;
    public int nowTop;
    public int nowRight;
    public int nowBottom;
    public FrameLayout.LayoutParams layoutParams;
    BaseCharacterView bindingCharacter;
    Paint borderPaint;
    public AttackArea(Context context) {
        super(context);
        if(GameBaseAreaActivity.myCharacter!=null)
            bindingCharacter=GameBaseAreaActivity.myCharacter;
        init();
    }
    public AttackArea(Context context, BaseCharacterView character) {
        super(context);
        bindingCharacter=character;
        init();
    }

    public AttackArea(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if(GameBaseAreaActivity.myCharacter!=null)
            bindingCharacter=GameBaseAreaActivity.myCharacter;
        init();
    }

    public AttackArea(Context context, @Nullable AttributeSet attrs,BaseCharacterView character) {
        super(context, attrs);
        bindingCharacter=character;
        init();
    }

    private void init(){

        if(bindingCharacter!=null) {
            radius = bindingCharacter.defaultAttackRadius;
            centerX=bindingCharacter.centerX;
            centerY=bindingCharacter.centerY;
            nowLeft=centerX-radius;
            nowRight=centerX+radius;
            nowTop=centerY-radius;
            nowBottom=centerY+radius;
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
            layoutParams.leftMargin=bindingCharacter.centerX-radius;
            layoutParams.topMargin=bindingCharacter.centerY-radius;
            this.setLayoutParams(layoutParams);
            this.layoutParams=layoutParams;
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(radius,radius,radius,borderPaint);
    }
}
