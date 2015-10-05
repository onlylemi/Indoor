package com.onlylemi.dr.custom_view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by 董神 on 2015/9/5.
 * I love programming
 */
public class MyGridView extends GridView {
    public MyGridView(Context context) {
        super(context);
    }



    public MyGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
