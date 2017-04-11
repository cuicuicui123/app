package com.goodo.app;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
 
/**
 * @author wangcccong
 * @version 1.140122
 * create at��2014-02-26
 */
public class ViewUtil {
 
    /**
     * ��ȡ��Ļ�Ŀ��
     * @param context
     * @return
     */
    public int getScreenWidth(Context context) {
        Resources res = context.getResources();
        return res.getDisplayMetrics().widthPixels;
    }
     
    /**
     * ��ȡ��Ļ�߶�
     * @param context
     * @return
     */
    public int getScreenHeight(Context context) {
        Resources res = context.getResources();
        return res.getDisplayMetrics().heightPixels;
    }
     
    /**
     * ���������ݷֱ��ʻ�������С.
     *
     * @param screenWidth the screen width
     * @param screenHeight the screen height
     * @param textSize the text size
     * @return the int
     */
    public static int resizeTextSize(int screenWidth,int screenHeight,int textSize){
        float ratio =  1;
        try {
            float ratioWidth = (float)screenWidth / 480; 
            float ratioHeight = (float)screenHeight / 800; 
            ratio = Math.min(ratioWidth, ratioHeight); 
        } catch (Exception e) {
        }
        return Math.round(textSize * ratio);
    }
     
    /**
     * 
     * ������dipת��Ϊpx
     * @param context
     * @param dipValue
     * @return
     * @throws 
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return Math.round(dipValue * scale);
    }
 
    /**
     * 
     * ������pxת��Ϊdip
     * @param context
     * @param pxValue
     * @return
     * @throws 
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return Math.round(pxValue / scale);
    }
     
    /**
     * 
     * ������pxת��Ϊsp
     * @param context
     * @param pxValue
     * @return
     * @throws 
     */
    public static int px2sp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return Math.round(pxValue / scale);
    }
     
    /**
     * 
     * ������spת��Ϊpx
     * @param context
     * @param spValue
     * @return
     * @throws 
     */
    public static int sp2px(Context context, float spValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return Math.round(spValue * scale);
    }
}
 
