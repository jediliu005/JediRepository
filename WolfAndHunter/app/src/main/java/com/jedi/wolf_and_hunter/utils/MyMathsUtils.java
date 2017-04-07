package com.jedi.wolf_and_hunter.utils;

import android.graphics.Point;

/**
 * Created by Administrator on 2017/4/2.
 */

public class MyMathsUtils {
    public static int POSITION_IN=0;
    public static int POSITION_ON=1;
    public static int POSITION_OUT=2;

    public static boolean isInCircle(Point circleCenter, int radius, Point targetPoint){
        return positionRelativeToCircle(circleCenter,radius,targetPoint)==POSITION_IN?true:false;
    }

    public static int positionRelativeToCircle(Point circleCenter, int radius, Point targetPoint){
        int centreX = circleCenter.x;
        int centreY = circleCenter.y;
        int targetPointX=targetPoint.x;
        int targetPointY=targetPoint.y;
        int relateX=targetPointX-centreX;
        int relateY=targetPointY-centreY;
        if(relateX * relateX + relateY * relateY >radius*radius)
            return POSITION_OUT;
        if(relateX * relateX + relateY * relateY ==radius*radius)
            return POSITION_ON;
        if(relateX * relateX + relateY * relateY <radius*radius)
            return POSITION_IN;


        return 1;
    }

    public static double getDistance(Point a,Point b){
        if(a!=null&&b!=null ){
            return Math.sqrt((a.x-b.x)^2+(a.y-b.y)^2);
        }
        return 0;
    }
}
