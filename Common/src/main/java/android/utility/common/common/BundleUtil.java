package android.utility.common.common;

import android.os.Bundle;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
/**
 * Created by lqnhu on 6/19/15.
 */
public class BundleUtil {
    public static Map<String, Serializable> bundleToMap(Bundle bundle) {
        Map<String, Serializable> result = new HashMap<String, Serializable>();
        if (bundle == null) return result;
        for (String key : bundle.keySet()) {
            result.put(key, bundle.getSerializable(key));
        }
        return result;
    }
    public static Bundle mapToBundle(Map<String, Serializable> map) {
        Bundle result = new Bundle();
        if (map == null) return result;
        for (String key : map.keySet()) {
            result.putSerializable(key, map.get(key));
        }
        return result;
    }
}
