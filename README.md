# CityServe仿支付宝城市服务模块
### 效果图
![效果图1](https://raw.githubusercontent.com/lwh7819/source/master/image/cityserve.gif)
### 使用
1.在布局中引用
```
<com.example.lvweihao.cityserve.MyAnimaNavigatorBar
            android:id="@+id/mybar"
            android:layout_width="match_parent"
            android:layout_height="match_parent"/>
```
2.在java代码中为控件设置list数据源
```
MyAnimaNavigatorBar myAnimaNavigatorBar = (MyAnimaNavigatorBar) findViewById(R.id.mybar);
        myAnimaNavigatorBar.setAdaper(this, mTitleList);
        myAnimaNavigatorBar.initPostion(0);
```
3.添加自定义的每个列表中的页面,在下边方法中添加自己的业务界面
```
/**
     * 根据titleBar的子项个数绘制纵向scrollView里的子布局
     */
    public void createContentView() {
        FrameLayout frameLayout = null;
        for (int i = 0; i < mTitleList.size(); i++) {
            frameLayout = new FrameLayout(mContext);
            TextView textView = new TextView(mContext);
            textView.setText("this is page " + i);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
            frameLayout.addView(textView, params);
            frameLayout.setBackgroundColor((int) mTitleBackgroundColorList.get(i));
             int mH = 1080;
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mH);
            llContentView.addView(frameLayout, i, layoutParams);
        }
    }
```
