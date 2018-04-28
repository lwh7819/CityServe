package com.example.lvweihao.cityserve;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by lv.weihao on 2017/7/3.
 * 自定义带动画改变背景色的TextView
 */
public class MyAnimTextView extends View {

    private int mTextColor; //文字颜色
    private int mTextColorBlack; //黑色
    private int mTextColorWhite; //白色

    private String mText; //文字内容
    private int mBackgroudColor; //背景色
    private int mTextSize; //文字大小

    private float mHeight; //textView高度
    private float mHeightCurrently; //动画当前到位的高度

    private Rect mBound;
    private Paint mPaint;

    private int mColorBarHeight;

    public MyAnimTextView(Context context, String mText, int mTextSize, int mBackgroudColor) {
        super(context);
        this.mText = mText;
        this.mTextSize = mTextSize;
        this.mBackgroudColor = mBackgroudColor;
        init(context);
    }

    public MyAnimTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyAnimTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MyAnimTextView);
        mText = array.getString(R.styleable.MyAnimTextView_mText);
        mBackgroudColor = array.getColor(R.styleable.MyAnimTextView_mBackGroundColor, context.getResources().getColor(R.color.white));
        mTextSize = array.getDimensionPixelSize(R.styleable.MyAnimTextView_mTextSize, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
        init(context);
    }

    private void init(Context context) {
        mColorBarHeight = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        mTextColorBlack = context.getResources().getColor(R.color.black);
        mTextColorWhite = context.getResources().getColor(R.color.white);
        mTextColor = mTextColorBlack;

        mPaint = new Paint();
        mBound = new Rect();
        mPaint.setTextSize(mTextSize);
        mPaint.getTextBounds(mText, 0, mText.length(), mBound);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mHeight = getHeight();
        mPaint.setColor(mBackgroudColor);
        //绘制backGroundColor
        canvas.drawRect(0, 0, getWidth(), mColorBarHeight, mPaint);
        canvas.drawRect(0, mColorBarHeight, getWidth(), mHeightCurrently, mPaint);
        mPaint.setColor(mTextColor);
        //绘制文字
        canvas.drawText(mText, getWidth() / 2 - mBound.width() / 2, getHeight() / 2 + mBound.height() / 2, mPaint);
    }

    /**
     * 背景色下移动画
     */
    public void doDownAnima() {
        ObjectAnimator anim = ObjectAnimator.ofFloat(this, "mHeight", mHeight);
        anim.setDuration(200);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                setHeightNotInUiThread(value);
                if (value > 3 * mHeight / 4) {
                    //改变文字颜色
                    mTextColor = mTextColorWhite;
                }
            }
        });
        anim.start();
    }

    /**
     * 背景色上移动画
     */
    public void doUpAnima() {
        ObjectAnimator anim = ObjectAnimator.ofFloat(this, "mHeightCurrently", mHeightCurrently, 0);
        anim.setDuration(200);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                setHeightNotInUiThread(value);
                if (value < 1 * mHeight / 4) {
                    //改变文字颜色
                    mTextColor = mTextColorBlack;
                }
            }
        });
        anim.start();
    }

    public void setHeightNotInUiThread(float height) {
        this.mHeightCurrently = height;
        this.postInvalidate();
    }

    public void setmBackgroudColor(int color) {
        this.mBackgroudColor = color;
    }

    public void setmTextSize(int size) {
        this.mTextSize = size;
    }

    public void setmText(String mText) {
        this.mText = mText;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //适配wrap_content
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);   //获取宽的模式
        int heightMode = MeasureSpec.getMode(heightMeasureSpec); //获取高的模式
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);   //获取宽的尺寸
        int heightSize = MeasureSpec.getSize(heightMeasureSpec); //获取高的尺寸
        int width;
        int height ;
        if (widthMode == MeasureSpec.EXACTLY) {
            //如果match_parent或者具体的值，直接赋值
            width = widthSize;
        } else {
            //如果是wrap_content，我们要得到控件需要多大的尺寸
            float textWidth = mBound.width();   //文本的宽度
            //控件的宽度就是文本的宽度加上两边的内边距。内边距就是padding值，在构造方法执行完就被赋值
            width = (int) (getPaddingLeft() + textWidth + getPaddingRight());
        }
        //高度跟宽度处理方式一样
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            float textHeight = mBound.height();
            height = (int) (getPaddingTop() + textHeight + getPaddingBottom());
        }
        //保存测量宽度和测量高度
        setMeasuredDimension(width, height);
    }
}
