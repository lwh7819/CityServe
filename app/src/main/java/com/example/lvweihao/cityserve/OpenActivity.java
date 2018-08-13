package com.example.lvweihao.cityserve;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lv.weihao on 2017/7/4.
 */
public class OpenActivity extends AppCompatActivity {

    private List mTitleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_activity);

        int mNumber = getIntent().getIntExtra("number", 0);
        mTitleList = new ArrayList();
        for (int j = 0; j <= mNumber; j++) {
            mTitleList.add("标题" + j);
        }

//        View view = new MyAnimaNavigatorBar2(this, mTitleList, mTitleBackgroundColorList);
//        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
//        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        frameLayout.addView(view, layoutParams);

        MyAnimaNavigatorBar myAnimaNavigatorBar = (MyAnimaNavigatorBar) findViewById(R.id.mybar);
        myAnimaNavigatorBar.setAdaper(this, mTitleList);
        myAnimaNavigatorBar.initPostion(0);

    }
}
