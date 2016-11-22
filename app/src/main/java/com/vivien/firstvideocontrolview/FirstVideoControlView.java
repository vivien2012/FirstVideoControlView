package com.vivien.firstvideocontrolview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by vivien on 16/11/21.
 */

public class FirstVideoControlView extends View {

    /**
     * 圆环背景色值
     */
    private int mBgColor;

    /**
     * 圆环前景色值
     */
    private int mFgColor;

    /**
     * 圆环的宽度
     */
    private int mCircleWidth;

    /**
     * 画笔
     */
    private Paint mPaint;

    /**
     * 当前进度
     */
    private int mCurrent = 3;

    /**
     * 中间的图片
     */
    private Bitmap mBitmap;

    /**
     * 每个块块间的间隙
     */
    private int mSpliteSize;

    /**
     * 个数
     */
    private int mCount;

    private Rect mRect;


    public FirstVideoControlView(Context context) {
        this(context, null);
    }

    public FirstVideoControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 必要的初始化，获得一些自定义的值
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public FirstVideoControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FirstVideoControlView, defStyleAttr, 0);
        mBgColor = a.getColor(R.styleable.FirstVideoControlView_dotBg, Color.GRAY);
        mFgColor = a.getColor(R.styleable.FirstVideoControlView_dotFg, Color.RED);
        mBitmap = BitmapFactory.decodeResource(getResources(), a.getResourceId(R.styleable.FirstVideoControlView_centerImg, R.mipmap.ic_launcher));
        mCircleWidth = a.getDimensionPixelSize(R.styleable.FirstVideoControlView_circleWidth, 30);
        mCount = a.getInt(R.styleable.FirstVideoControlView_dotCount, 15);
        mSpliteSize = a.getInt(R.styleable.FirstVideoControlView_splitSize, 20);

        a.recycle();

        mPaint = new Paint();
        mRect = new Rect();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setAntiAlias(true); //消除锯齿
        mPaint.setStrokeWidth(mCircleWidth); //设置圆环的宽度
        mPaint.setStrokeCap(Paint.Cap.ROUND); //定义线段断电形状为圆头
        mPaint.setAntiAlias(true); //消除锯齿
        mPaint.setStyle(Paint.Style.STROKE); //设置空心
        int center = getWidth() / 2; //设置圆心的x坐标
        int radius = center - mCircleWidth / 2; //半径
        /**
         * 画块块去
         */
        drawOval(canvas, center, radius);

        /**
         * 计算内切正方形的位置
         */
        int relRadius = radius - mCircleWidth / 2;// 获得内圆的半径
        /**
         * 内切正方形的距离顶部 = mCircleWidth + relRadius - √2 / 2
         */
        mRect.left = (int) (relRadius - Math.sqrt(2) * 1.0f / 2 * relRadius) + mCircleWidth;
        /**
         * 内切正方形的距离左边 = mCircleWidth + relRadius - √2 / 2
         */
        mRect.top = (int) (relRadius - Math.sqrt(2) * 1.0f / 2 * relRadius) + mCircleWidth;
        mRect.bottom = (int) (mRect.left + Math.sqrt(2) * relRadius);
        mRect.right = (int) (mRect.left + Math.sqrt(2) * relRadius);

        int maxSize = (int) (Math.sqrt(2) * relRadius);
        if (mBitmap.getWidth() > maxSize || mBitmap.getHeight() > maxSize) {
            if (mBitmap.getWidth() > mBitmap.getHeight()) {
                //mRect.left  mRect.right 不变，高度比例缩小
                int tempHeight = (int) (mBitmap.getHeight() * 1.0f * maxSize / mBitmap.getWidth());
                mRect.top = mRect.top + maxSize / 2 - tempHeight / 2;
                mRect.bottom = mRect.top + tempHeight;
            } else {
                //mRect.top mRect.bottom 不变，宽度比例缩小
                int tempWidth = (int) (mBitmap.getWidth() * 1.0f / mBitmap.getHeight() * maxSize);
                mRect.left = mRect.left + maxSize / 2 - tempWidth / 2;
                mRect.right = mRect.left + tempWidth;
            }
        } else {
            mRect.left = (int) (mRect.left + Math.sqrt(2) * relRadius * 1.0f / 2 - mBitmap.getWidth() * 1.0f / 2);
            mRect.top = (int) (mRect.top + Math.sqrt(2) * relRadius * 1.0f / 2 - mBitmap.getHeight() * 1.0f / 2);
            mRect.right = (int) (mRect.left + mBitmap.getWidth());
            mRect.bottom = (int) (mRect.top + mBitmap.getHeight());
        }

        canvas.drawBitmap(mBitmap, null, mRect, mPaint);
    }

    /**
     * 根据参数画出小块块
     *
     * @param canvas
     * @param center
     * @param radius
     */
    private void drawOval(Canvas canvas, int center, int radius) {
        /**
         * 根据需要画的个数以及间隙计算每个块块所占的比例＊360
         */
        float itemSize = (360 * 1.0f - mCount * mSpliteSize) / mCount;

        RectF oval = new RectF(center - radius, center - radius, center + radius, center + radius); //用于定义圆弧的形状和大小的界限

        //第一圈
        mPaint.setColor(mBgColor); //设置圆环颜色
        for (int i = 0; i < mCount; i++) {
            canvas.drawArc(oval, i * (itemSize + mSpliteSize), itemSize, false, mPaint);
        }

        //第二圈
        mPaint.setColor(mFgColor); //设置圆环颜色
        for (int i = 0; i < mCurrent; i++) {
            canvas.drawArc(oval, i * (itemSize + mSpliteSize), itemSize, false, mPaint);
        }
    }


    private int xDown, xUp;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDown = (int) event.getY();
                break;
            case MotionEvent.ACTION_UP:
                xUp = (int) event.getY();
                if (xUp > xDown) {
                    down();
                } else {
                    up();
                }
        }
        return true;
    }

    /**
     * 当前数据减1
     */
    private void down() {
        if (mCurrent > 0) mCurrent--;
        postInvalidate();
    }

    /**
     * 当前数据加1
     */
    private void up() {
        if (mCurrent < mCount) mCurrent++;
        postInvalidate();
    }

}
