package android.utility.ui.activity;

import java.lang.reflect.Method;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.TextView;
import android.util.DisplayMetrics;
/**
 * Created by lqnhu on 6/20/15.
 */
public class ViewUtil {
    public static final String TAG = "ViewUtil";
    public final static int measureTextWidth(TextView view, String text) {
        if (TextUtils.isEmpty(text)) {
            return 0;
        }
        TextPaint paint = view.getPaint();
        int width = (int) paint.measureText(text);
        return width;
    }
    public static boolean eventInView(MotionEvent event, View view) {
        if (event == null || view == null) {
            return false;
        }
        int eventX = (int) event.getRawX();
        int eventY = (int) event.getRawY();
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int width = view.getWidth();
        int height = view.getHeight();
        int left = location[0];
        int top = location[1];
        int right = left + width;
        int bottom = top + height;
        Rect rect = new Rect(left, top, right, bottom);
        boolean contains = rect.contains(eventX, eventY);
        return contains;
    }
    public static Point getViewCenter(View view) {
        if (view == null) {
            return new Point();
        }
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0] + view.getWidth() / 2;
        int y = location[1] + view.getHeight() / 2;
        return new Point(x, y);
    }
    @SuppressLint("NewApi")
    public static void setAlpha(View view, float alpha) {
        if (Build.VERSION.SDK_INT < 11) {
            final AlphaAnimation animation = new AlphaAnimation(alpha, alpha);
            animation.setDuration(0);
            animation.setFillAfter(true);
            view.startAnimation(animation);
        } else {
            view.setAlpha(alpha);
        }
    }
    public static void removeFromParent(View view) {
        if (view == null) {
            return;
        }
        ViewParent parent = view.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(view);
        }
    }
    public static Bitmap capture(View view) {
        if (view == null) {
            return null;
        }
        boolean oldEnabled = view.isDrawingCacheEnabled();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        if (!oldEnabled) {
            view.setDrawingCacheEnabled(false);
        }
        return bitmap;
    }
    public static void toggleSoftInput(TextView tv, boolean enable) {
        Method setShowSoftInputOnFocus = null;
        try {
            Method[] methods = tv.getClass().getMethods();
            for (Method m : methods) {
                String name = m.getName();
                if (name.equals("setSoftInputShownOnFocus")) {
                    setShowSoftInputOnFocus = tv.getClass().getMethod(
                            "setSoftInputShownOnFocus", boolean.class);
                    break;
                } else if (name.equals("setShowSoftInputOnFocus")) {
                    setShowSoftInputOnFocus = tv.getClass().getMethod(
                            "setShowSoftInputOnFocus", boolean.class);
                    break;
                }
            }
            if (null != setShowSoftInputOnFocus) {
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(tv, enable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static View findActionBarContainer(Activity activity) {
        int id = activity.getResources().getIdentifier("action_bar_container", "id", "android");
        return activity.findViewById(id);
    }
    public static View findSplitActionBar(Activity activity) {
        int id = activity.getResources().getIdentifier("split_action_bar", "id", "android");
        return activity.findViewById(id);
    }

    /**
     * Converts the number in pixels to the number in dips
     */
    public static int convertToDip(DisplayMetrics displayMetrics, int sizeInPixels) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, sizeInPixels, displayMetrics);
    }
    /**
     * Converts the number in dips to the number in pixels
     */
    public static int convertToPix(float density, int sizeInDips) {
        float size = sizeInDips * density;
        return (int) size;
    }
    /**
     * Utility method to make getting a View via findViewById() more safe & simple.
     * <p/>
     * - Casts view to appropriate type based on expected return value
     * - Handles & logs invalid casts
     *
     * @param context The current Context or Activity that this method is called from
     * @param id R.id value for view
     * @return View object, cast to appropriate type based on expected return value.
     * @throws ClassCastException if cast to the expected type breaks.
     */
    @SuppressWarnings("unchecked")
    public static <T extends View> T findViewById(Activity context, int id) {
        T view = null;
        View genericView = context.findViewById(id);
        try {
            view = (T) (genericView);
        } catch (Exception ex) {
            String message = "Can't cast view (" + id + ") to a " + view.getClass() + ". Is actually a " + genericView.getClass() + ".";
            Log.e("PercolateAndroidUtils", message);
            throw new ClassCastException(message);
        }
        return view;
    }
    /**
     * Utility method to make getting a View via findViewById() more safe & simple.
     * <p/>
     * - Casts view to appropriate type based on expected return value
     * - Handles & logs invalid casts
     *
     * @param parentView Parent View containing the view we are trying to get
     * @param id R.id value for view
     * @return View object, cast to appropriate type based on expected return value.
     * @throws ClassCastException if cast to the expected type breaks.
     */
    @SuppressWarnings("unchecked")
    public static <T extends View> T findViewById(View parentView, int id) {
        T view = null;
        View genericView = parentView.findViewById(id);
        try {
            view = (T) (genericView);
        } catch (Exception ex) {
            String message = "Can't cast view (" + id + ") to a " + view.getClass() + ". Is actually a " + genericView.getClass() + ".";
            Log.e("PercolateAndroidUtils", message);
            throw new ClassCastException(message);
        }
        return view;
    }
    /**
     * Get text as String from EditView.
     * <b>Note:</b> returns "" for null EditText, not a NullPointerException
     *
     * @param view EditView to get text from
     * @return the text
     */
    public static String getText(TextView view) {
        String text = "";
        if (view != null) {
            text = view.getText().toString();
        } else {
            Log.e("PercolateAndroidUtils", "Null view given to getText(). \"\" will be returned.");
        }
        return text;
    }
    /**
     * Get text as String from EditView.
     * <b>Note:</b> returns "" for null EditText, not a NullPointerException
     *
     * @param context The current Context or Activity that this method is called from
     * @param id Id for the TextView/EditView to get text from
     * @return the text
     */
    public static String getText(Activity context, int id) {
        TextView view = findViewById(context, id);
        String text = "";
        if (view != null) {
            text = view.getText().toString();
        } else {
            Log.e("PercolateAndroidUtils", "Null view given to getText(). \"\" will be returned.");
        }
        return text;
    }
    /**
     * Append given text String to the provided view (one of TextView or EditText).
     *
     * @param view View to update
     * @param toAppend String text
     */
    public static void appendText(TextView view, String toAppend) {
        String currentText = getText(view);
        view.setText(currentText + toAppend);
    }
    /**
     * Go away keyboard, nobody likes you.
     *
     * @param context The current Context or Activity that this method is called from
     * @param field field that holds the keyboard focus
     */
    public static void closeKeyboard(Context context, View field) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(field.getWindowToken(), 0);
        } catch (Exception ex) {
            Log.e("PercolateAndroidUtils", "Error occurred trying to hide the keyboard. Exception=" + ex);
        }
    }
    /**
     * Show the pop-up keyboard
     *
     * @param context Activity/Context
     * @param field field that requests focus
     */
    public static void showKeyboard(Context context, View field){
        try {
            field.requestFocus();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(field, InputMethodManager.SHOW_IMPLICIT);
        } catch (Exception ex) {
            Log.e("Caffeine", "Error occurred trying to show the keyboard. Exception=" + ex);
        }
    }
    /**
     * Convert view to an image. Can be used to make animations smoother.
     *
     * @param context The current Context or Activity that this method is called from
     * @param viewToBeConverted View to convert to a Bitmap
     * @return Bitmap object that can be put in an ImageView. Will look like the converted viewToBeConverted.
     */
    public static Bitmap viewToImage(Context context, WebView viewToBeConverted) {
        int extraSpace = 2000; //because getContentHeight doesn't always return the full screen height.
        int height = viewToBeConverted.getContentHeight() + extraSpace;
        Bitmap viewBitmap = Bitmap.createBitmap(viewToBeConverted.getWidth(), height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(viewBitmap);
        viewToBeConverted.draw(canvas);
//If the view is scrolled, cut off the top part that is off the screen.
        try {
            int scrollY = viewToBeConverted.getScrollY();
            if (scrollY > 0) {
                viewBitmap = Bitmap.createBitmap(viewBitmap, 0, scrollY, viewToBeConverted.getWidth(), height - scrollY);
            }
        } catch (Exception ex) {
            Log.e("PercolateAndroidUtils", "Could not remove top part of the webview image. ex=" + ex);
        }
        return viewBitmap;
    }
    /**
     * Method used to set text for a TextView
     *
     * @param context The current Context or Activity that this method is called from
     * @param field R.id.xxxx value for the text field.
     * @param text Text to place in the text field.
     */
    public static void setText(Activity context, int field, String text) {
        View view = context.findViewById(field);
        if (view instanceof TextView) {
            ((TextView) view).setText(text);
        } else {
            Log.e("PercolateAndroidUtils", "ViewUtils.setText() given a field that is not a TextView");
        }
    }
    /**
     * Method used to set text for a TextView
     *
     * @param parentView The View used to call findViewId() on
     * @param field R.id.xxxx value for the text field.
     * @param text Text to place in the text field.
     */
    public static void setText(View parentView, int field, String text) {
        View view = parentView.findViewById(field);
        if (view instanceof TextView) {
            ((TextView) view).setText(text);
        } else {
            Log.e("PercolateAndroidUtils", "ViewUtils.setText() given a field that is not a TextView");
        }
    }
    /**
     * Sets visibility of the given view to <code>View.GONE</code>.
     *
     * @param context The current Context or Activity that this method is called from
     * @param id R.id.xxxx value for the view to hide"expected textView to throw a ClassCastException" + textView
     */
    public static void hideView(Activity context, int id) {
        if (context != null) {
            View view = context.findViewById(id);
            if (view != null) {
                view.setVisibility(View.GONE);
            } else {
                Log.e("PercolateAndroidUtils", "View does not exist. Could not hide it.");
            }
        }
    }
    /**
     * Sets visibility of the given view to <code>View.VISIBLE</code>.
     *
     * @param context The current Context or Activity that this method is called from
     * @param id R.id.xxxx value for the view to show
     */
    public static void showView(Activity context, int id) {
        if (context != null) {
            View view = context.findViewById(id);
            if (view != null) {
                view.setVisibility(View.VISIBLE);
            } else {
                Log.e("PercolateAndroidUtils", "View does not exist. Could not hide it.");
            }
        }
    }
}
