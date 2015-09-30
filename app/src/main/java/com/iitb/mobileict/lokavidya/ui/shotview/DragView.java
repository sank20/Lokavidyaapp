

package com.iitb.mobileict.lokavidya.ui.shotview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;



public class DragView extends View 
{

    private static final int DRAG_SCALE = 50;

    private Bitmap mBitmap;
    
    private int mRegistrationX;
    private int mRegistrationY;

    private float mAnimationScale = 0.9f;

    private WindowManager.LayoutParams mLayoutParams;
    private WindowManager mWindowManager;


    public DragView(Context context, Bitmap bitmap, int registrationX, int registrationY,
            int left, int top, int width, int height) {
        super(context);


        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);        
  
        Matrix scale = new Matrix();
        float scaleFactor = width;
        scaleFactor =  (scaleFactor + DRAG_SCALE) / scaleFactor;
        scale.setScale(scaleFactor, scaleFactor);
        mBitmap = Bitmap.createBitmap(bitmap, left, top, width, height, scale, true);


        mRegistrationX = registrationX + (DRAG_SCALE / 2);
        mRegistrationY = registrationY + (DRAG_SCALE / 2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mBitmap.getWidth(), mBitmap.getHeight());
    }

    @SuppressLint({ "DrawAllocation", "DrawAllocation" })
	@Override
    protected void onDraw(Canvas canvas) {
        if (true) {

            Paint p = new Paint();
            p.setStyle (Paint.Style.FILL);

            p.setColor (Color.YELLOW);
            p.setAlpha (80);
            canvas.drawRect(0, 0, getWidth(), getHeight(), p);
        }
        float scale = mAnimationScale;
        if (scale < 0.999f) {
            float height = mBitmap.getHeight();
            float width = mBitmap.getWidth();
            float offset1 = (width-(width*scale))/2;
            float offset2 = (height-(height*scale))/2;
            canvas.translate(offset1, offset2);
            canvas.scale(scale, scale);
        }
        Paint p2 = new Paint();
        p2.setAlpha (100);
        canvas.drawBitmap(mBitmap, 0.0f, 0.0f, p2);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mBitmap.recycle();
    }

    public void setPaint(Paint paint) {
        invalidate();
    }

    public void setScale (float scale) {
        if (scale > 1.0f) mAnimationScale = 1.0f;
        else mAnimationScale = scale;
        invalidate();
    }


    public void show(IBinder windowToken, int touchX, int touchY) {
        WindowManager.LayoutParams lp;
        int pixelFormat;

        pixelFormat = PixelFormat.TRANSLUCENT;

        lp = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                touchX-mRegistrationX, touchY-mRegistrationY,
                WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                   ,
                pixelFormat);

        lp.gravity = Gravity.LEFT | Gravity.TOP;
        lp.token = windowToken;
        lp.setTitle("DragView");
        mLayoutParams = lp;

        mWindowManager.addView(this, lp);

    }
    

    void move(int touchX, int touchY) {

        WindowManager.LayoutParams lp = mLayoutParams;
        lp.x = touchX - mRegistrationX;
        lp.y = touchY - mRegistrationY;
        mWindowManager.updateViewLayout(this, lp);
    }

    void remove() {
        mWindowManager.removeView(this);
    }
}

