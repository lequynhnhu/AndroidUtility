package android.utility.common.intent;
import android.content.Intent;
/**
 * Created by lqnhu on 6/21/15.
 */
public class ShareIntents {
    /**
     * Creates a chooser to share some data.
     *
     * @param subject The subject to share (might be discarded, for instance if the user picks an SMS app)
     * @param message The message to share
     * @param chooserDialogTitle The title for the chooser dialog
     * @return the intent
     */
    public static Intent newShareTextIntent(String subject, String message, String chooserDialogTitle) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.setType(MIME_TYPE_TEXT);
        return Intent.createChooser(shareIntent, chooserDialogTitle);
    }
    private static final String MIME_TYPE_TEXT = "text/*";
}
