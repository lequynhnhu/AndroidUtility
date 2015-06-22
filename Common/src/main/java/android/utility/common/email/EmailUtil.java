package android.utility.common.email;

import android.content.Context;
import android.content.Intent;

/**
 * Created by lqnhu on 6/21/15.
 */
public class EmailUtil {
    public static void startEmailActivity(Context context, String to, String subject, String body, String pickerTitle){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL , new String[]{to});
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT , body);
        try {
            context.startActivity(Intent.createChooser(i, pickerTitle).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } catch (android.content.ActivityNotFoundException ex) {
        }
    }
}
