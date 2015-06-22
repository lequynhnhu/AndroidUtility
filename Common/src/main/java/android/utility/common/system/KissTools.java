package android.utility.common.system;

import java.lang.ref.WeakReference;
import java.security.InvalidParameterException;
import android.content.Context;
/**
 * Created by lqnhu on 6/20/15.
 */
public class KissTools {
    public static final String TAG = "KissTools";
    private static WeakReference<Context> contextRef;
    public static void setContext(Context context) {
        if (context == null) {
            throw new InvalidParameterException("Invalid context parameter!");
        }
        Context appContext = context.getApplicationContext();
        contextRef = new WeakReference<Context>(appContext);
    }
    public static Context getApplicationContext() {
        Context context = contextRef.get();
        if (context == null) {
            throw new InvalidParameterException("Context parameter not set!");
        } else {
            return context;
        }
    }
}