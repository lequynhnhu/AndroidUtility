package android.utility.ui.gui;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lqnhu on 6/20/15.
 */
public class FontUtils {
    public static boolean checkMMFont(String s) {
        Pattern p = Pattern.compile("[^\\u0000-\\u0080]+");
        Matcher matcher = p.matcher(s);
        return matcher.find();
    }
    public static void setRobotoLight(Context mContext, TextView tv) {
        Typeface font = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Roboto-Light.ttf");
        tv.setTypeface(font);
    }
    public static void setRobotoCondense(Context mContext, TextView tv) {
        Typeface font = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/RobotoCondensed-Regular.ttf");
        tv.setTypeface(font);
    }
    public static void setRoboto(Context mContext, TextView tv) {
        Typeface font = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Roboto-Regular.ttf");
        tv.setTypeface(font);
    }
    public static void setRobotoMedium(Context mContext, TextView tv) {
        Typeface font = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Roboto-Medium.ttf");
        tv.setTypeface(font);
    }
    public static void setRobotoCondenseBold(Context mContext, TextView tv) {
        Typeface font = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/RobotoCondensed-Bold.ttf");
        tv.setTypeface(font);
    }
}
