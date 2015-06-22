package android.utility.ui.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
/**
 * Created by lqnhu on 6/20/15.
 */
public class AnimateOpenAndClosed extends Animation
{
    private final int mHeightOpen;
    private final int mHeightClose;
    private final View mView;
    private final boolean mIsOpening;
    public AnimateOpenAndClosed(final View view, final int openHeight, final int closedHeight, final boolean isOpening)
    {
        super();
        this.mView = view;
        this.mHeightOpen = openHeight;
        this.mHeightClose = closedHeight;
        this.mIsOpening = isOpening;
    }
    @Override
    protected void applyTransformation(final float interpolatedTime, final Transformation transformation)
    {
        int newHeight;
        final int diff = this.mHeightOpen - this.mHeightClose;
        if (this.mIsOpening)
        {
            newHeight = this.mHeightClose + (int) (diff * interpolatedTime);
        }
        else
        {
            newHeight = this.mHeightClose + (int) (diff * (1 - interpolatedTime));
        }
        this.mView.getLayoutParams().height = newHeight;
        this.mView.requestLayout();
    }
    @Override
    public boolean willChangeBounds()
    {
        return true;
    }
}
