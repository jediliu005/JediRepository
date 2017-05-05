package com.jedi.wolf_and_hunter.MyViews.characters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.jedi.wolf_and_hunter.R;
import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;
import com.jedi.wolf_and_hunter.utils.MyMathsUtils;

/**
 * Created by Administrator on 2017/4/21.
 */

public class NormalHunter extends BaseCharacterView {
    private static final String TAG = "NormalHunter";
    private final static String characterName="普通猎人";
    private int bolletWidth=1;
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

    @Override
    public void judgeFire() {
        super.judgeFire();
        for(BaseCharacterView targetCharacter: GameBaseAreaActivity.allCharacters){
            if(this==targetCharacter)
                continue;
            double distance=MyMathsUtils.getDistance(new Point(centerX,centerY),new Point(targetCharacter.centerX,targetCharacter.centerY));
            if(distance>attackRange.nowAttackRadius)
                continue;


            int targetCharacterSize=targetCharacter.characterBodySize;
            int relateX=targetCharacter.centerX-centerX;
            int relateY=targetCharacter.centerY-centerY;

            float angleBetweenXAxus = MyMathsUtils.getAngleBetweenXAxus(relateX,relateY);
            float relateAngle=Math.abs(angleBetweenXAxus-nowFacingAngle);
            boolean isInFrontOfCharacter=true;
            if(relateAngle>90&&relateAngle<270)
                isInFrontOfCharacter=false;
            if(isInFrontOfCharacter==false)
                continue;
            double pointToLineDistance=0;
            if(nowFacingAngle==0||nowFacingAngle==180)
                pointToLineDistance=relateY;
            else if(nowFacingAngle==90||nowFacingAngle==270)
                pointToLineDistance=relateX;
            else {
                double k=Math.tan(Math.toRadians(nowFacingAngle));
                pointToLineDistance=MyMathsUtils.getPointToLineDistance(new Point(relateX,relateY),k,0);
                if(pointToLineDistance<=bolletWidth/2+targetCharacterSize)
                    targetCharacter.isDead=true;
            }
        }

    }
}
