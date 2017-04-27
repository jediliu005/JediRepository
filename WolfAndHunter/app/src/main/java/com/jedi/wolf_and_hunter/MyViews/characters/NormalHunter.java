package com.jedi.wolf_and_hunter.MyViews.characters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

/**
 * Created by Administrator on 2017/4/21.
 */

public class NormalHunter extends BaseCharacterView {
    private static final String TAG = "NormalHunter";

    public static final int defaultHiddenLevel=BaseCharacterView.HIDDEN_LEVEL_NO_HIDDEN;
    public NormalHunter(Context context) {
        super(context);
    }

    public NormalHunter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
