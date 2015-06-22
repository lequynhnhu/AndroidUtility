package android.utility.ui.gui;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
/**
 * Created by lqnhu on 6/22/15.
 */
public class HelpUtils {
    public static boolean hasSeenTutorial(final Context context, final String id) {
        final SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sp.getBoolean("seen_tutorial_" + id, false);
    }
    private static void setSeenTutorial(final Context context, final String id) {
// noinspection unchecked
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... voids) {
                final SharedPreferences sp = PreferenceManager
                        .getDefaultSharedPreferences(context);
                sp.edit().putBoolean("seen_tutorial_" + id, true).commit();
                return null;
            }
        }.execute();
    }
    public static boolean needShowHelp(final Context context, final String id) {
        final boolean need = !HelpUtils.hasSeenTutorial(context, id);
        if (need) {
            HelpUtils.setSeenTutorial(context, id);
        }
        return need;
    }
}
