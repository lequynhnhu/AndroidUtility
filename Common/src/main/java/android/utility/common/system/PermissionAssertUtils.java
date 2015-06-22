package android.utility.common.system;
import android.content.Context;
import android.content.pm.PackageManager;
/**
 * Created by lqnhu on 6/20/15.
 */
public final class PermissionAssertUtils {
    private PermissionAssertUtils() {}
    public static void assertPermission(Context ctx, String perm) {
        final int checkPermission = ctx.getPackageManager().checkPermission(
                perm, ctx.getPackageName());
        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
            throw new SecurityException("Permission " + perm + " is required");
        }
    }
}