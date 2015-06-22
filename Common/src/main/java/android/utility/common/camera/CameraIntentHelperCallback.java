package android.utility.common.camera;

import android.net.Uri;

import java.util.Date;

/**
 * Created by lqnhu on 6/21/15.
 */
public interface CameraIntentHelperCallback {
    void onPhotoUriFound(Date dateCameraIntentStarted, Uri photoUri, int rotateXDegrees);
    void deletePhotoWithUri(Uri photoUri);
    void onSdCardNotMounted();
    void onCanceled();
    void onCouldNotTakePhoto();
    void onPhotoUriNotFound();
    void logException(Exception e);
}
