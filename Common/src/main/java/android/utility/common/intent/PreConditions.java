package android.utility.common.intent;
import android.content.Context;
import android.os.Parcelable;
import java.util.List;
/**
 * Created by lqnhu on 6/21/15.
 */
public class PreConditions {
    private PreConditions() {
    }
    public static void validateContext(Context context) {
        if (context == null) {
            throw new IllegalStateException("Call IntentBuilder.context() first");
        }
    }
    public static void validateNotEmpty(List param, String message) {
        validateNotNull(param, message);
        validateNotZero(param.size(), message);
    }
    public static void validateNotEmpty(CharSequence[] param, String message) {
        validateNotNull(param, message);
        validateNotZero(param.length, message);
    }
    public static void validateNotEmpty(boolean[] param, String message) {
        validateNotNull(param, message);
        validateNotZero(param.length, message);
    }
    public static void validateNotEmpty(byte[] param, String message) {
        validateNotNull(param, message);
        validateNotZero(param.length, message);
    }
    public static void validateNotEmpty(char[] param, String message) {
        validateNotNull(param, message);
        validateNotZero(param.length, message);
    }
    public static void validateNotEmpty(double[] param, String message) {
        validateNotNull(param, message);
        validateNotZero(param.length, message);
    }
    public static void validateNotEmpty(float[] param, String message) {
        validateNotNull(param, message);
        validateNotZero(param.length, message);
    }
    public static void validateNotEmpty(int[] param, String message) {
        validateNotNull(param, message);
        validateNotZero(param.length, message);
    }
    public static void validateNotEmpty(long[] param, String message) {
        validateNotNull(param, message);
        validateNotZero(param.length, message);
    }
    public static void validateNotEmpty(short[] param, String message) {
        validateNotNull(param, message);
        validateNotZero(param.length, message);
    }
    public static void validateNotEmpty(Parcelable[] param, String message) {
        validateNotNull(param, message);
        validateNotZero(param.length, message);
    }
    public static void validateNotNull(Object param, String message) {
        if (param == null) {
            throw new IllegalArgumentException(message + " must not be null");
        }
    }
    public static void validateNotZero(int length, String message) {
        if (length == 0) {
            throw new IllegalArgumentException(message + " must not be 0 length");
        }
    }
    public static void validateNotBlank(CharSequence param, String message) {
        validateNotNull(param, message);
        if (param.length() < 1) {
            throw new IllegalArgumentException(message + " must not be empty");
        }
    }
}
