package com.hope.verticalviewpager.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import com.hope.verticalviewpager.R;

import java.util.ArrayList;
import java.util.List;


public class FullscreenActivity extends Activity {


    private static final int[] COLORS = new int[] { Color.BLACK, Color.BLUE, Color.CYAN, Color.DKGRAY};

    private static final String TAG = FullscreenActivity.class.getSimpleName();

    private VerticalViewPager mVerticalViewPager;

    private List<String> mDataSource = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        for (int i = 0; i < 4; i++) {
            mDataSource.add("btn_" + i);
        }

        mVerticalViewPager = (VerticalViewPager) findViewById(R.id.vertical_viewpager);
        mVerticalViewPager.setDataSource(mDataSource);
        mVerticalViewPager.setListener(new VerticalViewPager.OnVerticalPagerListener() {

            @Override
            public void onPageSelected(android.view.View childView, int position) {
                ViewHolder holder = null;
                if(childView.getTag() == null) {
                    holder = new ViewHolder();
                    holder.btn = (Button)childView.findViewById(R.id.btn);

                    childView.setTag(holder);
                } else {
                    holder = (ViewHolder) childView.getTag();
                }
                childView.setBackgroundColor(COLORS[position]);

                holder.btn.setText(mDataSource.get(position));
            }

        });


    }

    private class ViewHolder {
        private Button btn;
    }

}
