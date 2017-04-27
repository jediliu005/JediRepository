package com.jedi.wolf_and_hunter.MyViews.landform;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.jedi.wolf_and_hunter.MyViews.characters.BaseCharacterView;

/**
 * Created by Administrator on 2017/4/20.
 */

public interface  Landform {

    public Bitmap getBitmap(Context context);
    public  void effect(BaseCharacterView character);
    public  void removeEffect(BaseCharacterView character);
}
