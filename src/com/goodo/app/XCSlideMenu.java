package com.goodo.app;

import com.nineoldandroids.view.ViewHelper;

import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
/**
 * 鑷畾涔変晶婊戣彍鍗昖iew
 * @author caizhiming
 *
 */
public class XCSlideMenu extends HorizontalScrollView{

    private LinearLayout mWapper;
    private ViewGroup mMenu;
    private ViewGroup mContent;
    private int mScreenWidth;
    private int mMenuRightPadding;
    private int mMenuWidth = 0;
    
    private boolean once = false;
    //Menu鏄惁澶勪簬鏄剧ず鐘舵?
    private boolean isSlideOut;

    public static final int RIGHT_PADDING = 100;
    
    public XCSlideMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
        //灏哾p杞寲涓簆x
        mMenuRightPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, XCSlideMenu.RIGHT_PADDING, context.getResources().getDisplayMetrics());
    }

    /**
     * 璁剧疆瀛怴iew鐨勫鍜岄珮
     * 璁剧疆鑷繁鐨勫鍜岄珮
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        if(!once){
            mWapper = (LinearLayout) getChildAt(0);
            mMenu = (ViewGroup) mWapper.getChildAt(0);
            mContent = (ViewGroup) mWapper.getChildAt(1);
            
            mMenuWidth  = mMenu.getLayoutParams().width = mScreenWidth - mMenuRightPadding;
            mContent.getLayoutParams().width = mScreenWidth;
            once = true;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    /**
     * 閫氳繃璁剧疆鍋忕Щ閲忓皢Menu闅愯棌
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO Auto-generated method stub
        super.onLayout(changed, l, t, r, b);
        if(changed){
            this.scrollTo(mMenuWidth, 0);
        }
    }
    float startX = 0;
    float endX = 0;
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        int action = ev.getAction();
        switch (action) {
        	case MotionEvent.ACTION_DOWN:
        		startX = ev.getX();
            case MotionEvent.ACTION_UP:
            	endX = ev.getX();
            	if(Math.abs(endX - startX)>10){
            		//闅愯棌鍦ㄥ乏杈瑰搴?
                    int scrollX = getScrollX();
                    if(scrollX >= mMenuWidth /2){
                        //Menu 宸︽粦闅愯棌璧锋潵
                        this.smoothScrollTo(mMenuWidth, 0);
                        isSlideOut = false;
                    }else{
                        //Menu 鍙虫粦 鏄剧ず鍑烘潵
                        this.smoothScrollTo(0, 0);
                        isSlideOut = true;
                    }
                    return true;
            	}else{
            		return false;
            	}
            
        }
        return super.onTouchEvent(ev);
    }
    /**
     * 鍚戝彸婊戝嚭鑿滃崟鏄剧ず鍑烘潵
     */
    public void slideOutMenu(){
        if(!isSlideOut){
            this.smoothScrollTo(0, 0);
            isSlideOut = true;
        }else{
            return;
        }
     }  
    /**
     * 鍚戝乏婊戝嚭鑿滃崟闅愯棌璧锋潵 
     */
    public void slideInMenu(){
        if(isSlideOut){
            this.smoothScrollTo(mMenuWidth, 0);
            isSlideOut = false;
        }else{
            return;
        }
    }
    /**
     * 鍒囨崲鑿滃崟鍚戝彸婊戝嚭鏄剧ず鎴栧悜宸︽粦鍑洪殣钘忕殑鐘舵?
     */
    public void switchMenu(){
        if(isSlideOut){
            slideInMenu();
        }else{
            slideOutMenu();
        }
    }
    /**
     * 婊氬姩鍙戠敓鏃?
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        // TODO Auto-generated method stub
        super.onScrollChanged(l, t, oldl, oldt);
        //瀹炵幇鎶藉眽寮忔粦鍔?
        float scale = l * 1.0f /mMenuWidth ;//1 ~ 0
        float menuScale = 1.0f - scale * 0.3f;
        float menuAlpha = 0.0f + 1.0f * (1 - scale);
        float contentScale = 0.8f + 0.2f * scale;
        //璋冪敤灞炴?鍔ㄧ敾锛岃缃甌ranslationX
        ViewHelper.setTranslationX(mMenu, mMenuWidth*scale*0.8f);
        
        //宸︿晶鑿滃崟鐨勭缉鏀?
        ViewHelper.setScaleX(mMenu, menuScale);
        ViewHelper.setScaleY(mMenu, menuScale);
        //宸︿晶鑿滃崟鐨勯?鏄庡害缂╂斁
        ViewHelper.setAlpha(mMenu, menuAlpha);
        
        //鍙充晶鍐呭鐨勭缉鏀?
        ViewHelper.setPivotX(mContent, 0);
        ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
        ViewHelper.setScaleY(mContent, contentScale);
        ViewHelper.setScaleX(mContent, contentScale);
    }
    
}
