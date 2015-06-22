package android.utility.ui.textview;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
/**
 * Created by lqnhu on 6/20/15.
 */
public class TypeWriterTextView extends RobotoTextView {
    private CharSequence mText;
    private int mIndex;
    private long mDelay = 500; //Default 500ms delay
    // usage:
// Typewriter textView = (Typewriter) rowView.findViewById(R.id.gridItemTextView);
// textView.setCharacterDelay(60);
// textView.animateText(mCountries.get(position).name);
    public TypeWriterTextView(Context context) {
        super(context);
    }
    public TypeWriterTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    private Handler mHandler = new Handler();
    private Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            setText(mText.subSequence(0, mIndex++));
            if (mIndex <= mText.length()) {
                mHandler.postDelayed(characterAdder, mDelay);
            }
        }
    };
    public void animateText(CharSequence text) {
        mText = text;
        mIndex = 0;
        setText("");
        mHandler.removeCallbacks(characterAdder);
        mHandler.postDelayed(characterAdder, mDelay);
    }
    public void setCharacterDelay(long millis) {
        mDelay = millis;
    }
}
