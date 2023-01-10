package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;


public class PaintView extends View {

    public static int BRUSH_SIZE = 20;
    public static final int DEFAULT_COLOR = Color.RED;
    public static final int DEFAULT_BG_COLOR = Color.WHITE;
    private static final float TOUCH_TOLERANCE = 4;
    private float mX, mY;
    private Path mPath;
    private Paint mPaint;
    private ArrayList<FingerPath> paths = new ArrayList<>();
    private int currentColor;
    private int backgroundColor = DEFAULT_BG_COLOR;
    private int strokeWidth;
    private boolean emboss;
    private boolean blur;
    private boolean figure;
    //private MaskFilter mEmboss;
    //private MaskFilter mBlur;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);

    public PaintView(Context context) {
        this(context, null);
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(DEFAULT_COLOR);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xff);

        //mEmboss = new EmbossMaskFilter(new float[] {1, 1, 1}, 0.4f, 6, 3.5f);
        //mBlur = new BlurMaskFilter(5, BlurMaskFilter.Blur.NORMAL);
    }

    public void init(DisplayMetrics metrics) {
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        currentColor = DEFAULT_COLOR;
        strokeWidth = BRUSH_SIZE;
    }

    public void normal() {
        emboss = false;
        blur = false;
        figure = false;    }
    public void size_normal() {
        strokeWidth = 10;
    }
    public void size_big() {
        strokeWidth = 15;
    }
    public void size_small() {
        strokeWidth = 5;
    }
    public void color_green() {
        currentColor = Color.GREEN;
    }
    public void color_red() {
        currentColor = Color.RED;
    }
    public void color_black() {
        currentColor = Color.BLACK;
    }

    public void emboss() {
        emboss = true;
        blur = false;
        figure = false;    }

    public void blur() {
        emboss = false;
        blur = true;
        figure = false;
    }
    public void figure(){
        emboss =false;
        blur = false;
        figure = true;
    }
    public void clear() {
        backgroundColor = DEFAULT_BG_COLOR;
        paths.clear();
        normal();
        invalidate();
    }
    public void setPolygon(float x, float y, float radius, int numOfPt) {

        double section = 2.0 * Math.PI / numOfPt;

        mPath.reset();
        mPath.moveTo((float) (x + radius * Math.cos(0)), (float) (y + radius
                * Math.sin(0)));

        for (int i = 1; i < numOfPt; i++) {
            mPath.lineTo((float) (x + radius * Math.cos(section * i)),
                    (float) (y + radius * Math.sin(section * i)));
        }

        mPath.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        mCanvas.drawColor(backgroundColor);

        for (FingerPath fp : paths) {
            mPaint.setColor(fp.color);
            mPaint.setStrokeWidth(fp.strokeWidth);
            mPaint.setMaskFilter(null);

            //if (fp.emboss)
                //mPaint.setMaskFilter(mEmboss);
            //else if (fp.blur)
                //mPaint.setMaskFilter(mBlur);

            mCanvas.drawPath(fp.path, mPaint);

        }

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.restore();
    }

    private void touchStart(float x, float y) {
        mPath = new Path();
        FingerPath fp = new FingerPath(currentColor,figure, emboss, blur, strokeWidth, mPath);
        paths.add(fp);

        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            if(!emboss && !blur && !figure) {

                mX = x;//для линии
                mY = y;//для линии
            }
        }
    }

    private void touchUp(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        double pdx = dx*dx;
        double pdy = dy*dy;
        double sdxy = Math.sqrt(pdx+pdy);
        double section = 2.0 * Math.PI / 3;

        float f = (float)sdxy;


        if(figure){
            mPath.moveTo((float) (x + f * Math.cos(0)), (float) (y + f
                    * Math.sin(0)));

            for (int i = 1; i < 4; i++) {
                mPath.lineTo((float) (x + f * Math.cos(section * i)),
                        (float) (y + f * Math.sin(section * i)));
            }
        }
        if(emboss){// тут что то про квадрат
        mPath.addRect(mX, mY, x  , y ,Path.Direction.CW);}
        if(blur){
            mPath.addCircle(x,y,f,Path.Direction.CW);
        }
        if(!emboss && !blur && !figure){
        mPath.lineTo(mX, mY);}

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN :

                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE :
                touchMove(x, y);

                invalidate();
                break;
            case MotionEvent.ACTION_UP :
                touchUp(x,y);

                invalidate();
                break;
        }

        return true;
    }
}
