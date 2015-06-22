package android.utility.ui.textview;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
/**
 * Created by lqnhu on 6/20/15.
 */
public class RobotoCondensedTextView extends TextView {
    public RobotoCondensedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    public RobotoCondensedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public RobotoCondensedTextView(Context context) {
        super(context);
        init();
    }
    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Condensed.ttf");
        setTypeface(tf, 1);
    }
}



