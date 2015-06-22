package android.utility.ui.textview;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
/**
 * Created by lqnhu on 6/20/15.
 */

public class RobotoTextView extends TextView {
    public RobotoTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    public RobotoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public RobotoTextView(Context context) {
        super(context);
        init();
    }
    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto.ttf");
        setTypeface(tf, 1);
    }
}
