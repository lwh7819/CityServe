package com.example.lvweihao.cityserve;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by lv.weihao on 2017/7/4.
 * 滑动定位导航栏
 */
public class MyAnimaNavigatorBar extends FrameLayout{
    private static Integer[] colors = {R.color.blue, R.color.orange, R.color.purple, R.color.green, R.color.red};

    private ObservableScrollView mScrollView;
    private HorizontalScrollView mHorizontalScrollView;

    /**
     * 存放子布局的map
     */
    private Map<Integer, Object> mItemViewMap;

    /**
     * 标记当前位置
     */
    private int tag = -1;
    private int horizontalTag = 0;

    private LinearLayout llTitleBar;
    private LinearLayout llContentView;

    private Context mContext;
    private List mTitleList;
    private List mTitleBackgroundColorList;

    private int screenWidth;

    private int height;
    private List viewHeightList = new ArrayList();

    private Map<Integer, Object> mTitleTextViewMap;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            height = msg.arg1;
            viewHeightList.add(height);
        }
    };

    public MyAnimaNavigatorBar(Context context, List mTitleList) {
        super(context);
        this.mContext = context;
        this.mTitleList = mTitleList;
        init(context);
    }

    public MyAnimaNavigatorBar(Context context, List mTitleList, List mTitleBackgroundColorList) {
        super(context);
        this.mContext = context;
        this.mTitleList = mTitleList;
        this.mTitleBackgroundColorList = mTitleBackgroundColorList;
        init(context);
    }

    public MyAnimaNavigatorBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (this.mTitleList != null && this.mTitleBackgroundColorList != null) {
            init(context);
        }
    }

    public MyAnimaNavigatorBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setAdaper(Context context,List mTitleList) {
        this.mContext = context;
        this.mTitleList = mTitleList;
        init(context);
    }
    public void setAdaper(Context context,List mTitleList, List mTitleBackgroundColorList) {
        this.mContext = context;
        this.mTitleList = mTitleList;
        this.mTitleBackgroundColorList = mTitleBackgroundColorList;
        init(context);
    }

    private void init(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        inflate(context, R.layout.activity_main2, this);
        mScrollView = (ObservableScrollView) findViewById(R.id.vertical_scrollview);
        mHorizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontal_scrollview);

        llTitleBar = (LinearLayout) findViewById(R.id.ll_titlebar);
        llContentView = (LinearLayout) findViewById(R.id.ll_content);

        //默认颜色池
        if (mTitleBackgroundColorList == null) {
            mTitleBackgroundColorList = new ArrayList();
            for (int i = 0; i < mTitleList.size(); i++) {
                int j = i % colors.length;
                mTitleBackgroundColorList.add(getResources().getColor(colors[j]));
            }
        }

        /**
         * 创建自定义子布局
         */
        createContentView();

        /**
         * 创建titleBar
         */
        layoutTitleBar();

        /**
         * 为titleBar添加点击滑动监听事件
         */
        titleBarListener();

        //子布局
        mItemViewMap = new HashMap<>();
        for (int i = 0; i < mTitleList.size(); i++) {
            mItemViewMap.put(i, llContentView.getChildAt(i));
        }

        //titleBar布局
        mTitleTextViewMap = new HashMap<>();
        for (int i = 0; i < mTitleTextViewMap.size(); i++) {
            mTitleTextViewMap.put(i, llTitleBar.getChildAt(i));
        }

        //当触摸时注册监听事件，点击上方button时移除监听（避免触发多次动画）
        mScrollView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setScrollViewListener();
                return false;
            }
        });

        /**
         * 获取所有子布局高度并存到本地
         */
        getAllViewHeight();
    }

    /**
     * 测量view的高度
     * @param view
     * @return height
     */
    private void getViewHeight(final View view) {
        final ViewTreeObserver vto = view.getViewTreeObserver();
                vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { 
                        public boolean onPreDraw() {
//                            vto.removeOnPreDrawListener(this);
                            int height = view.getMeasuredHeight();
                            Message message = new Message();
                            message.arg1 = height;
                            handler.sendMessage(message);
                            return true;
                        } 
                });
    }

    /**
     * 获取所有子View高度并保存（当子布局高度有改变时要调用一下）
     */
    public void getAllViewHeight() {
        for (int i = 0; i < mItemViewMap.size(); i++) {
            getViewHeight((View) mItemViewMap.get(i));
        }
    }

    /**
     * 纵向scrollView的监听事件
     */
    private void setScrollViewListener() {
        mScrollView.setScrollViewListener(new ObservableScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
                float lastY = scrollView.getScrollY();
                if (lastY == 0) {
                    //titleBar第一个位置的动画处理
                    final MyAnimTextView mView = (MyAnimTextView) llTitleBar.getChildAt(0);
                    if (tag != 0) {
                        mView.doDownAnima();
                    }
                    for (int j = 0; j < mTitleList.size(); j++) {
                        if (tag == j && tag != 0) {
                            ((MyAnimTextView) llTitleBar.getChildAt(j)).doUpAnima();
                        }
                    }
                    tag = 0;
                } else {
                    //titleBar非第一个位置的动画处理
                    for (int i = 1; i < mItemViewMap.size(); i++) {
                        if (lastY >= getScrollToPosition(i) && lastY < getScrollToPosition(i + 1)) {
                            final MyAnimTextView mView = (MyAnimTextView) llTitleBar.getChildAt(i);
                            if (tag != i) {
                                mView.doDownAnima();
                            }
                            for (int j = 0; j < mTitleList.size(); j++) {
                                if (tag == j && tag != i) {
                                    ((MyAnimTextView) llTitleBar.getChildAt(j)).doUpAnima();
                                }
                            }
                            tag = i;

                            //横向scrollView滚动到的位置
                            horizontalScrollToPostion(lastY);
                        }
                    }
                }
            }
        });
    }

    /**
     * titleBar的点击事件
     */
    private void titleBarListener() {
        for (int i = 0; i < mTitleList.size(); i++) {
            final int finalI = i;
            final MyAnimTextView mView = (MyAnimTextView) llTitleBar.getChildAt(i);
            mView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mScrollView.removeScrollViewlistener(); // 移除scrollView的滑动监听事件（避免触发两次动画）
                    if (tag != finalI) {
                        mView.doDownAnima();
                        slowlyVerticalScrollTo(getScrollToPosition(finalI));
                    }

                    for (int j = 0; j < mTitleList.size(); j++) {
                        if (tag == j && tag != finalI) {
                            ((MyAnimTextView)llTitleBar.getChildAt(j)).doUpAnima();
                        }
                    }

                    tag = finalI;
                }
            });
        }
    }

    /**
     * 获取纵向scrollView应该滚动到的位置
     * @param position
     * @return
     */
    private int getScrollToPosition(int position) {
        int scrollToHeight = 0;
        if (position == 0) {
            scrollToHeight = 0;
        } else {
            for (int i = 0; i < position; i++) {
                scrollToHeight += ((int)viewHeightList.get(i));
            }
        }
        return scrollToHeight;
    }

    /**
     * 横向scrollView的滚动动画
     * @param dx
     */
    private void slowlyHorizontalScrollTo(int dx) {
        ObjectAnimator animator= ObjectAnimator.ofInt(mHorizontalScrollView, "scrollX", dx);
        animator.setDuration(800);
        animator.start();
    }

    /**
     * 纵向scrollView的滚动动画
     * @param dy
     */
    private void slowlyVerticalScrollTo(int dy) {
        ObjectAnimator animator= ObjectAnimator.ofInt(mScrollView, "scrollY", dy);
        animator.setDuration(500);
        animator.start();
    }

    /**
     * 通过纵向scrollView的位置判断横向scrollView应该滚动到的位置
     * @param lastY 纵向scrollView的位置
     */
    private void horizontalScrollToPostion(float lastY) {
        int sizeInt = mItemViewMap.size() / 5;
        if (sizeInt > 0 && mItemViewMap.size() % 5 != 0) {
            sizeInt = sizeInt + 1;
        }
        for (int i = 0; i < sizeInt; i++) {
            if (i != sizeInt - 1) {
                if (lastY >= getScrollToPosition(i * 5) && lastY < getScrollToPosition((i + 1) * 5)) {
                    if (horizontalTag != i) {
                        slowlyHorizontalScrollTo(i * screenWidth);
                        horizontalTag = i;
                    }
                }
            } else {
                if (lastY >= getScrollToPosition(i * 5)) {
                    if (horizontalTag != i) {
                        slowlyHorizontalScrollTo(i * screenWidth);
                        horizontalTag = i;
                    }
                }
            }
        }
    }

    /**
     * 根据titleBar的子项个数绘制纵向scrollView里的子布局
     */
    private void createContentView() {
        FrameLayout frameLayout = null;
        for (int i = 0; i < mTitleList.size(); i++) {
            frameLayout = new FrameLayout(mContext);
            frameLayout.setBackgroundColor((int) mTitleBackgroundColorList.get(i));
             int mH = 1080;
            if (i == 0) {
                mH = 2002;
            } else if (i == 1) {
                mH = 1964;
            } else if (i == 2) {
                mH = 800;
            } else if (i == 3) {
                mH = 3000;
            } else if (i == 4) {
                mH = 2500;
            } else if (i == 5) {
                mH = 1820;
            } else if (i == 6) {
                mH = 1080;
            } else if (i == 7) {
                mH = 500;
            } else if (i == 8) {
                mH = 3600;
            } else if (i == 9) {
                mH = 400;
            } else if (i == 10) {
                mH = 3800;
            } else if (i == mTitleList.size() - 1) {
                mH = 3000;
            }
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mH);
            llContentView.addView(frameLayout, i, layoutParams);
        }
    }

    /**
     * 绘制横向scrollView里的控件
     */
    private void layoutTitleBar() {
        int marginLeft = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        //如果大于5项，每页显示5项且平分手机屏幕宽度
        int mWidth = (screenWidth - 4 * marginLeft) / 5;
        if (mTitleList.size() <= 5) {
            //如果小于5项，每页显示当前项个数且平分手机屏幕宽度
            if (mTitleList.size() == 1) {
                mWidth = screenWidth;
            } else {
                mWidth = (screenWidth - (mTitleList.size() - 1) * marginLeft) / mTitleList.size();
            }
            mHorizontalScrollView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View arg0, MotionEvent arg1) {
                    //不能滑动
                    return true;
                }
            });
        }
        for (int i = 0; i < mTitleList.size(); i++) {
            MyAnimTextView mView = (MyAnimTextView) createTitleTextView(i);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(mWidth, ViewGroup.LayoutParams.MATCH_PARENT);
            int left = 0;
            if (i == 0) {
                left = 0;
            } else {
                left = marginLeft;
            }
            layoutParams.setMargins(left, 0, 0, 0);
            llTitleBar.addView(mView, layoutParams);
        }
    }

    /**
     * 创建自定义带动画的textView
     * @param indx
     * @return
     */
    private View createTitleTextView(int indx) {
        int mTextSize = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics());
        MyAnimTextView myAnimTextView = new MyAnimTextView(mContext, (String) mTitleList.get(indx),
                mTextSize, (int) mTitleBackgroundColorList.get(indx));
        return myAnimTextView;
    }

    public void initPostion(final int postion) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tag = postion;
                ((MyAnimTextView)llTitleBar.getChildAt(postion)).doDownAnima();
                slowlyVerticalScrollTo(getScrollToPosition(postion));
            }
        }, 100);
    }
}
