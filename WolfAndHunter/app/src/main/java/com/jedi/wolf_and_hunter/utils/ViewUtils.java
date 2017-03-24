package com.jedi.wolf_and_hunter.utils;

import android.view.View;

/**
 * Created by Administrator on 2017/3/18.
 */

public class ViewUtils {
    /**
     *
     * @param targetView
     * @param containerView
     * @param offX
     * @param offY
     * @return
     */
    public static int[] reviseViewMovement(View targetView, View containerView, int offX, int offY) {
        int nowTargetLeft = targetView.getLeft();
        int nowTargetRight = targetView.getRight();
        int nowTargetBottom = targetView.getBottom();
        int nowTargetTop = targetView.getTop();
        int targetHeight = targetView.getHeight();
        int targetWidth = targetView.getWidth();
        int containerHeight = containerView.getHeight();
        int containerWidth = containerView.getWidth();
        int retTargetX1 = 0;
        int retTargetY1 = 0;
        int retTargetX2 = 0;
        int retTargetY2 = 0;
//        boolean isVerticalCenter=false;
//        boolean isHorizontalCenter=false;
        int[] retArr = new int[4];
        retTargetY1 = nowTargetTop + offY;
        retTargetY2 = nowTargetBottom + offY;
        retTargetX1 = nowTargetLeft + offX;
        retTargetX2 = nowTargetRight + offX;
        //targetView宽高都比containerView小的情况下
        if (containerHeight > targetHeight && containerWidth > targetHeight) {
            if (nowTargetTop + offY < 0) {
                retTargetY1 = 0;
                retTargetY2 = targetHeight;
            } else if (nowTargetBottom + offY > containerHeight) {
                retTargetY1 = containerHeight - targetHeight;
                retTargetY2 = containerHeight;
            }
            if (nowTargetLeft + offX < 0) {
                retTargetX1 = 0;
                retTargetX2 = targetWidth;
            } else if (nowTargetRight + offX > containerWidth) {
                retTargetX1 = containerWidth - targetWidth;
                retTargetX2 = containerWidth;
            }
        } else {//targetView宽高至少有一个比containerVIew大
            if (targetHeight >= containerHeight) {
                if (nowTargetTop + offY > 0.5 * containerHeight) {
                    retTargetY1 = (int) (0.5 * containerHeight);
                    retTargetY2 = (int) (0.5 * containerHeight + targetHeight);
                } else if (nowTargetBottom + offY < 0.5 * containerHeight) {
                    retTargetY1 = (int) (0.5 * containerHeight - targetHeight);
                    retTargetY2 = (int) (0.5 * containerHeight);
                }
            }
            if (targetWidth >= containerWidth) {
                if (nowTargetLeft + offX > 0.5 * containerWidth) {
                    retTargetX1 = (int) (0.5 * containerWidth);
                    retTargetX2 = (int) (0.5 * containerWidth + targetWidth);
                } else if (nowTargetRight + offX < 0.5 * containerWidth) {
                    retTargetX1 = (int) (0.5 * containerWidth - targetWidth);
                    retTargetX2 = (int) (0.5 * containerWidth);
                }
            }
        }

        retArr[0]=retTargetX1;
        retArr[1]=retTargetY1;
        retArr[2]=retTargetX2;
        retArr[3]=retTargetY2;
        return retArr;
    }

}
