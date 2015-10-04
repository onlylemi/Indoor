package com.onlylemi.dr.listViewAnimation;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * Created by jct on 2015/9/24.
 */
public class ScaleInAnimationAdapter extends AnimationAdapter {

    private static final float DEFAULTSCALEFROM = 0.8f;

    private float mScaleFrom;
    private long mAnimationDelayMillis;
    private long mAnimationDurationMillis;

    public ScaleInAnimationAdapter(BaseAdapter baseAdapter) {
        this(baseAdapter, DEFAULTSCALEFROM);
    }

    public ScaleInAnimationAdapter(BaseAdapter baseAdapter, float scaleFrom) {
        this(baseAdapter, scaleFrom, DEFAULTANIMATIONDELAYMILLIS, DEFAULTANIMATIONDURATIONMILLIS);
    }

    public ScaleInAnimationAdapter(BaseAdapter baseAdapter, float scaleFrom, long animationDelayMillis, long animationDurationMillis) {
        super(baseAdapter);
        mScaleFrom = scaleFrom;
        mAnimationDelayMillis = animationDelayMillis;
        mAnimationDurationMillis = animationDurationMillis;
    }

    @Override
    protected long getAnimationDelayMillis() {
        return mAnimationDelayMillis;
    }

    @Override
    protected long getAnimationDurationMillis() {
        return mAnimationDurationMillis;
    }

    @Override
    public Animator[] getAnimators(ViewGroup parent, View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", mScaleFrom, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", mScaleFrom, 1f);
        return new ObjectAnimator[] { scaleX, scaleY };
    }

}
