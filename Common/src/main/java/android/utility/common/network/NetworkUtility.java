package android.utility.common.network;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
/**
 * Created by lqnhu on 6/19/15.
 */
public final class NetworkUtility {
    private Context context = null;

    private static NetworkUtility instance = null;

    /**
     * Constructor
     *
     * @param context
     */
    private NetworkUtility(Context context) {
        this.context = context;
    }

    /**
     * Get class instance
     *
     * @param context
     * @return
     */
    public static NetworkUtility getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkUtility(context);
        }
        return instance;
    }

    /**
     * Check network connection
     *
     * @return
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager conMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        if (i == null) {
            return false;
        }
        if (!i.isConnected()) {
            return false;
        }
        if (!i.isAvailable()) {
            return false;
        }
        return true;
    }
}

