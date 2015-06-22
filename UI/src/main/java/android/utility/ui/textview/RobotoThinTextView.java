package android.utility.ui.textview;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
/**
 * Created by lqnhu on 6/20/15.
 */
public class RobotoThinTextView extends TextView {
    public RobotoThinTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    public RobotoThinTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public RobotoThinTextView(Context context) {
        super(context);
        init();
    }
    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Thin.ttf");
        setTypeface(tf, 1);
    }
}
