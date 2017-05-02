package com.jedi.wolf_and_hunter.MyViews.characters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.jedi.wolf_and_hunter.R;

/**
 * Created by Administrator on 2017/4/21.
 */

public class NormalHunter extends BaseCharacterView {
    private static final String TAG = "NormalHunter";
    private final static String characterName="猎";
    BitmapFactory.Options option=new BitmapFactory.Options();
    {option.inScaled=false;}
    public static final int defaultHiddenLevel=BaseCharacterView.HIDDEN_LEVEL_NO_HIDDEN;
    public NormalHunter(Context context) {
        super(context);
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.word,option);
        characterPic=Bitmap.createBitmap(bitmap,24,5,76,76,matrixForCP,true);
    }

    public NormalHunter(Context context, AttributeSet attrs) {
        super(context, attrs);
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.word,option);
        characterPic=Bitmap.createBitmap(bitmap,24,5,76,76,matrixForCP,true);
    }
}
