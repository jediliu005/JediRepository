package com.jedi.wolf_and_hunter.MyViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.jedi.wolf_and_hunter.MyViews.landform.Landform;
import com.jedi.wolf_and_hunter.MyViews.landform.TallGrassland;
import com.jedi.wolf_and_hunter.R;

/**
 * 为什么要加这个View且这个View与角色等其他组件并列呢？
 * 其实是因为Landform可能是View也可能是普通图片
 * 虽然作为ViewGroup的MapBaseFrame也可以强制ondraw
 * 但经过测试十分耗资源，大概是因为经常被强制触发吧
 * Created by Administrator on 2017/4/21.
 */

public class GameMap extends View {
    public static Landform[][] landformses;
    MapBaseFrame mapBaseFrame;
    public GameMap(Context context) {
        super(context);
    }

    public GameMap(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void addLandforms() {
        FrameLayout parent = (FrameLayout) getParent();
        for (int y = 0; y < landformses.length; y++) {
            for (int x = 0; x < landformses[y].length; x++) {
                if (landformses[y][x] != null) {
                    Landform l = landformses[y][x];
                    if (l instanceof TallGrassland) {
                        TallGrassland tgl=(TallGrassland)l;
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(100, 100);
                        params.leftMargin = x * 100;
                        params.topMargin = y * 100;

                        tgl.setLayoutParams(params);
                        parent.addView(tgl);

                    }

                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint=new Paint();
        Bitmap backgroung= BitmapFactory.decodeResource(getResources(), R.drawable.map_background_normal);
        //创建一个在水平和垂直方向都重复的BitmapShader对象
        BitmapShader bitmapshader = new BitmapShader(backgroung, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        paint.setShader(bitmapshader);//设置渲染对象
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);//绘制一个使用BitmapShader渲染的矩形

//
        int width=getWidth();
        int height=getHeight();
        if(landformses==null)
            return;
        int lengthY=landformses.length;
        int lengthX=landformses[0].length;
        if (lengthX*100>width||lengthY*100>height)
            return;



    }

}
