package com.jedi.wolf_and_hunter.utils;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by Administrator on 2017/3/18.
 */

public class ViewUtils {
    public static DisplayMetrics windowsDisplayMetrics;


//    private final int SHAPE_TYPE_UNSET = 0;
//    private final int SHAPE_TYPE_RECT = 1;
//    private final int SHAPE_TYPE_CIRCLE = 2;

    public ViewUtils() {

    }

    /**
     * @param targetView
     * @param containerView
     * @param offX
     * @param offY
     * @return
     */
    public int[] reviseTwoRectViewMovement(View targetView, View containerView, int offX, int offY) {
        int nowTargetLeft;
        int nowTargetRight;
        int nowTargetBottom;
        int nowTargetTop;
        int targetHeight;
        int targetWidth;
        int containerHeight;
        int containerWidth;

        int retTargetX1;
        int retTargetY1;
        int retTargetX2;
        int retTargetY2;
        int[] retArr = new int[4];

        nowTargetLeft = targetView.getLeft();
        nowTargetRight = targetView.getRight();
        nowTargetBottom = targetView.getBottom();
        nowTargetTop = targetView.getTop();
        targetHeight = targetView.getHeight();
        targetWidth = targetView.getWidth();
        containerHeight = containerView.getHeight();
        containerWidth = containerView.getWidth();


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

        retArr[0] = retTargetX1;
        retArr[1] = retTargetY1;
        retArr[2] = retTargetX2;
        retArr[3] = retTargetY2;
        return retArr;
    }

    public Point revisePointInCircleViewMovement(Point containerCircleCentre, int containerCircleRadius, Point pointTo) {
        Point retPoint = new Point();
        int centreX = containerCircleCentre.x;
        int centreY = containerCircleCentre.y;
        int pointToX=pointTo.x;
        int pointToY=pointTo.y;
        int relateX=pointToX-centreX;
        int relateY=pointToY-centreY;
        int retPointX=pointToX;
        int retPointY=pointToY;

        boolean isOut = relateX * relateX + relateY * relateY >containerCircleRadius*containerCircleRadius;
        if(isOut){
            double pointCos=relateX/Math.sqrt(relateX*relateX+relateY*relateY);
            double pointSin=relateY/Math.sqrt(relateX*relateX+relateY*relateY);
            retPointX=(int)(centreX+containerCircleRadius*pointCos);
            retPointY=(int)(centreY+containerCircleRadius*pointSin);
        }
        retPoint.set(retPointX,retPointY);
        return retPoint;
    }


    public static void initWindowParams(Context context) {
        if (windowsDisplayMetrics == null) {
            windowsDisplayMetrics = new DisplayMetrics();
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(windowsDisplayMetrics);
        }

    }
}
