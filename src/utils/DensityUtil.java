package utils;

import android.content.Context;  
import android.util.DisplayMetrics;  
import android.util.Log;
  
/** 
 * ���㹫ʽ pixels = dips * (density / 160) 
 *  
 * @version 1.0.1 2010-12-11 
 *  
 * @author 
 */  
public class DensityUtil {  
      
    private static final String TAG = DensityUtil.class.getSimpleName();  
      
    // ��ǰ��Ļ��densityDpi  
    private static float dmDensityDpi = 0.0f;  
    private static DisplayMetrics dm;  
    private static float scale = 0.0f;  
  
    /** 
     *  
     * ���ݹ��캯����õ�ǰ�ֻ�����Ļϵ�� 
     *  
     * */  
    public DensityUtil(Context context) {  
        // ��ȡ��ǰ��Ļ  
        dm = new DisplayMetrics();  
        dm = context.getApplicationContext().getResources().getDisplayMetrics();  
        // ����DensityDpi  
        setDmDensityDpi(dm.densityDpi);  
        // �ܶ�����  
        scale = getDmDensityDpi() / 160;  
        Log.i(TAG, toString());  
    }  
  
    /** 
     * ��ǰ��Ļ��density���� 
     *  
     * @param DmDensity 
     * @retrun DmDensity Getter 
     * */  
    public static float getDmDensityDpi() {  
        return dmDensityDpi;  
    }  
  
    /** 
     * ��ǰ��Ļ��density���� 
     *  
     * @param DmDensity 
     * @retrun DmDensity Setter 
     * */  
    public static void setDmDensityDpi(float dmDensityDpi) {  
        DensityUtil.dmDensityDpi = dmDensityDpi;  
    }  
  
    /** 
     * �ܶ�ת������ 
     * */  
    public int dip2px(float dipValue) {  
  
        return (int) (dipValue * scale + 0.5f);  
  
    }  
  
    /** 
     * ����ת���ܶ� 
     * */  
    public int px2dip(float pxValue) {  
        return (int) (pxValue / scale + 0.5f);  
    }  
  
    @Override  
    public String toString() {  
        return " dmDensityDpi:" + dmDensityDpi;  
    }  
}  