package com.ufo.ccphotopaint.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.ufo.ccphotopaint.R;

import java.util.HashMap;

/**
 * Created by tjpld on 16/8/5.
 */
public class PaintImageView extends TouchImageView {

    public static final int DEFAULT_PAINT_COLOR = Color.RED;
    public static final int DEFAULT_PAINT_WIDTH = 8;


    private int paintColor;
    private int paintWidth;


    private Bitmap mBitmap;
    private Bitmap mRealBitmap;
    private Canvas mCanvas;
    private Paint mPaint;
    private Paint mBitmapPaint;

    private String mImagePath;

    public String getmImagePath() {
        return mImagePath;
    }

    public void setmImagePath(String imagePath) {
        mImagePath = imagePath;
        mRealBitmap = BitmapFactory.decodeFile(mImagePath);
        this.setImageBitmap(mRealBitmap);
    }


    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {

        mBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            System.gc();
            bitmap = null;
        }
    }

    public Bitmap combineBitmap() {
        if (mRealBitmap == null) {
            return null;
        }
        int bgWidth = mRealBitmap.getWidth();
        int bgHeight = mRealBitmap.getHeight();
        int fgWidth = mRealBitmap.getWidth();
        int fgHeight = mRealBitmap.getHeight();

        Bitmap newBitmap = Bitmap.createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(mRealBitmap, 0, 0, null);
        canvas.drawBitmap(mBitmap, (bgWidth - fgWidth) / 2,
                (bgHeight - fgHeight) / 2, null);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return newBitmap;
    }

    public void clearDraw() {
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mCanvas.drawPaint(paint);
    }


    public int getPaintColor() {
        return paintColor;
    }

    public void setPaintColor(int color) {
        paintColor = color;
        mPaint.setColor(paintColor);
    }

    public int getPaintWidth() {
        return paintWidth;
    }

    public void setPaintWidth(int width) {
        paintWidth = width;
        mPaint.setStrokeWidth(paintWidth);
    }

    public PaintImageView(Context context) {
        super(context);
        initAttr(context, null);
    }

    public PaintImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
    }

    public PaintImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttr(context, attrs);
    }


    private void initAttr(Context context, AttributeSet attrs) {

        paintColor = DEFAULT_PAINT_COLOR;
        paintWidth = DEFAULT_PAINT_WIDTH;

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes
                    (attrs, R.styleable.PaintImageView);
            paintColor = typedArray.getInteger(R.styleable.PaintImageView_paintColor, DEFAULT_PAINT_COLOR);
            paintWidth = typedArray.getInteger(R.styleable.PaintImageView_paintWidth, DEFAULT_PAINT_WIDTH);

            if (typedArray != null) {
                typedArray.recycle();
            }
        }


        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(paintColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(paintWidth);

        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        this.setOnTouchListener(new touchListener());

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        }
        mCanvas = new Canvas(mBitmap);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, this.getImageMatrix(), mBitmapPaint);
    }


    private HashMap getImageViewIneerSize() {
        HashMap size = new HashMap();
        //获得ImageView中Image的变换矩阵
        Matrix m = this.getImageMatrix();
        float[] values = new float[10];
        m.getValues(values);

        //Image在绘制过程中的变换矩阵，从中获得x和y方向的缩放系数
        float sx = values[0];
        float sy = values[4];

        //计算Image在屏幕上实际绘制的宽高
        size.put("scaleX", 1 / sx);
        size.put("scaleY", 1 / sy);
        size.put("offsetX", values[2]); //X轴的translate的值
        size.put("offsetY", values[5]);

        return size;
    }


    private class touchListener implements OnTouchListener {

        float downx, downy, upx, upy;

        @Override
        public boolean onTouch(View v, MotionEvent event) {


            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    downx = event.getX();
                    downy = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:

                    upx = event.getX();
                    upy = event.getY();

                    HashMap hashMap = getImageViewIneerSize();
                    float scaleX = (float) hashMap.get("scaleX");
                    float scaleY = (float) hashMap.get("scaleY");
                    float offsetX = (float) hashMap.get("offsetX");
                    float offsetY = (float) hashMap.get("offsetY");

                    if (getCanDraw()) {
                        mCanvas.drawLine((downx - offsetX) * scaleX, (downy - offsetY) * scaleY, (upx - offsetX) * scaleX, (upy - offsetY) * scaleY, mPaint);
                        invalidate();
                    }

                    downx = upx;
                    downy = upy;


                    break;
                case MotionEvent.ACTION_UP:
                    if (getCanDraw()) {
                        if (mPaintImageListener != null) {
                            mPaintImageListener.OnDrawing();
                        }
                    }
                    break;

                default:
                    break;
            }


            return true;
        }


    }

}
