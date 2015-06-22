package android.utility.common.phone;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.inputmethod.InputMethodManager;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by lqnhu on 6/20/15.
 */
public class PhoneUtils {

    public static String getDeviceId(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    public static void hideSoftInput(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
    }

    public static DisplayMetrics getDisplayMetrics(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        Display display = activity.getWindowManager().getDefaultDisplay();
        display.getMetrics(dm);
        return dm;
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f) - 15;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int getDefaultStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Created by lqnhu on 6/19/15.
     */
    public static class VolumeControl {
        //private static final String TAG = Constants.TAG + ".VOLUME_CONTROL";

        private static VolumeControl sharedVolumeControl = null;

        /**
         * Obtain the singleton {@link VolumeControl} object.
         * @return the singleton {@link VolumeControl} object
         */
        public static synchronized VolumeControl sharedVolumeControl() {
            if (sharedVolumeControl == null) {
                sharedVolumeControl = new VolumeControl();
            }

            return sharedVolumeControl;
        }

        /**
         * The {@code VolumeChangeIndicator} enumerates the volume level change indicators that can be used
         * when programmatically changing the volume level (using {@link VolumeControl#setVolume(float)}).
         *
         * @author Ephraim A. Tekle
         *
         */
        public static enum VolumeChangeIndicator {
            /**
             * Play a sound when changing the volume
             * @see #SHOW_DIALOG
             */
            PLAY_SOUND,
            /**
             * Show a (progress bar) dialog when changing the volume
             * @see #PLAY_SOUND
             */
            SHOW_DIALOG,
            /**
             * Play a sound and show a dialog when changing the volume
             * @see #PLAY_SOUND
             * @see #SHOW_DIALOG
             */
            PLAY_SOUND_AND_SHOW_DIALOG,
            /**
             * Do not show any volume level change indicator
             */
            NONE;

            int getFlag() {
                switch(this) {
                    case PLAY_SOUND:
                        return AudioManager.FLAG_PLAY_SOUND;
                    case SHOW_DIALOG:
                        return AudioManager.FLAG_SHOW_UI;
                    case PLAY_SOUND_AND_SHOW_DIALOG:
                        return PLAY_SOUND.getFlag() | SHOW_DIALOG.getFlag();
                    default:
                        return AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE;
                }
            }
        }

        private VolumeChangeIndicator volumeChangeIndicator = VolumeChangeIndicator.SHOW_DIALOG;
        private final static float GRANULARITY = 100;
        static final int VOLUME_MONITOR_RATE_MS = 1000;
        //static final int VOLUME_MONITOR_RATE_HIGH_MS = 100; // sampling rate when volume change is detected

        private static float SYSTEM_MAX_VOLUME;

        private float playerVolume = 1;

        private AudioManager audioManager = null;
        private MediaPlayer mediaPlayer = null;
        private Activity activity = null;

        private boolean inLowVolumeMode = false;


        private final Handler handler = new Handler();
        private final List<VolumeChangeListener> volumeChangeListeners = new ArrayList<VolumeChangeListener>();
        private volatile float monitoredVolume;
        private volatile boolean stopVolumeMonitor;

        private VolumeControl() {
        }

        /**
         * Configures the {@link VolumeControl} object with the Audio Service system service and {@link AudioManager}.
         *
         * @param activity the Activity that will be used to retrieve the {@link AudioManager} and execute listener call backs on the main thread
         * @param mediaPlayer the {@link MediaPlayer} being used to play audio/video. While the {@code VolumeControl} will adjust system volumes, it's excepted that this class is being used within the context of a MediaPlayer.
         * @return returns {@code true} if configuration is successful. Returns {@code false} otherwise.
         */
        public boolean configure(Activity activity, MediaPlayer mediaPlayer) {

            if (activity == null || mediaPlayer == null) {
                return false;
            }

            this.audioManager = (AudioManager) activity.getSystemService(Activity.AUDIO_SERVICE);
            this.mediaPlayer = mediaPlayer;
            this.activity = activity;

            SYSTEM_MAX_VOLUME = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

            return true;
        }

        /**
         * Returns {@code true} if the {@code VolumeControl} is configured properly. Otherwise, {@code false} is returned.
         * @return {@code true} if this {@code VolumeControl} is configured properly and can be used.
         */
        public boolean isConfigured() {
            return (this.audioManager != null && this.mediaPlayer != null && this.activity != null);
        }

        /**
         * Sets the volume using {@code AudioManager} and the {@code MediaPlayer} (use {@link #setVolumeChangeIndicator(VolumeChangeIndicator)} to change the volume change indicator).
         *
         * @param volume the volume level between 0 (mute) and 1 (maximum volume).
         * @see #setVolumeChangeIndicator(VolumeChangeIndicator)
         */
        public void setVolume(float volume) {

            this.audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (Math.ceil(SYSTEM_MAX_VOLUME*volume)), volumeChangeIndicator.getFlag());

            float systemVolume = this.getSystemVolume();

            if (Math.abs(systemVolume-volume)*GRANULARITY >= 1) {

                this.playerVolume = volume/systemVolume;

                this.mediaPlayer.setVolume(this.playerVolume,this.playerVolume);
            }
        }

        /**
         * Get the current volume level (using {@code AudioManager} and the {@code MediaPlayer})
         * @return the volume level
         */
        public float getVolume() {
            return this.getSystemVolume()*this.playerVolume;
        }

        /**
         * Use this method to enter a low-volume mode. This is intended to be used when volume {@link AudioManager#AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK} is detected.
         */
        public synchronized void enterLowVolumeMode() {
            if (this.playerVolume > 0.1f) {
                this.mediaPlayer.setVolume(0.1f,0.1f);
                this.inLowVolumeMode = true;
            }
        }

        /**
         * Use this method to exit a low-volume mode and set volume to pre audio-focus loss. This is intended to be used when volume {@link AudioManager#AUDIOFOCUS_GAIN} is detected.
         */
        public synchronized void exitLowVolumeMode() {
            if (this.inLowVolumeMode) {
                this.mediaPlayer.setVolume(this.playerVolume,this.playerVolume);
                this.inLowVolumeMode = false;
            }
        }

        private float getSystemVolume() {
            return this.audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)/SYSTEM_MAX_VOLUME;
        }

        /**
         * Adds a volume change listener. The listener's {@code VolumeChanged} method is called immediately on the UI thread.
         * @param l the {@link VolumeChangeListener} to be added
         */
        public synchronized void addVolumeChangeListener(final VolumeChangeListener l) {
            this.volumeChangeListeners.add(l);

            this.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    l.volumeChanged(getVolume());
                }
            });
        }

        /**
         * Removes a volume change listener
         * @param l the volume change listener to remove
         */
        public synchronized void removeVolumeChangeListener(VolumeChangeListener l) {
            this.volumeChangeListeners.remove(l);
        }

        /**
         * Removes all volume change listeners. This method can be used as a cleanup when the main Activity exits.
         */
        public void removeAllVolumeChangeListeners() {
            this.volumeChangeListeners.clear();
        }

        /**
         * Starts the volume monitor so that {@link VolumeChangeListener}s will get notification if the volume is changed (for example, by the user using the volume up/down buttons).
         */
        public void startVolumeMonitor() {
            stopVolumeMonitor = false;
            this.monitoredVolume = this.getVolume();
            this.primaryVolumeUpdater() ;
        }

        /**
         * Stops volume monitoring so that no volume change updates are sent to listeners.
         */
        public void stopVolumeMonitor() {
            stopVolumeMonitor = true;
        }

        private void notifyVolumeListenersOnMainThread(final float volume) {
            this.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (VolumeChangeListener l : VolumeControl.this.volumeChangeListeners) {
                        l.volumeChanged(volume);
                    }
                }
            });
        }

        private void primaryVolumeUpdater() {
            if (this.stopVolumeMonitor) {
                return;
            }

            float volumeNow = this.getVolume();
            int samplingRate = VOLUME_MONITOR_RATE_MS;

            if (Math.abs(volumeNow-this.monitoredVolume)*GRANULARITY >= 1) {
                this.notifyVolumeListenersOnMainThread(volumeNow);
                //samplingRate = VOLUME_MONITOR_RATE_HIGH_MS;
                // sampling rate made no difference since we are bound by the UI Thread
            }

            this.monitoredVolume = volumeNow;

            handler.postDelayed(new Runnable() {
                public void run() {
                    primaryVolumeUpdater();
                }
            }, samplingRate);
        }

        /**
         * Set the volume change indicator used when volume is changed using  {@link #setVolume(float)}.
         * @param indicator the desired volume change indicator
         * @see #getVolumeChangeIndicator()
         */
        public void setVolumeChangeIndicator(VolumeChangeIndicator indicator) {
            this.volumeChangeIndicator = indicator;
        }

        /**
         * Returns the volume change indicator used when volume is changed using  {@link #setVolume(float)}.
         * @return the volume change indicator
         * @see #setVolumeChangeIndicator(VolumeChangeIndicator)
         */
        public VolumeChangeIndicator getVolumeChangeIndicator() {
            return this.volumeChangeIndicator;
        }

        /**
         * Interface for receiving notification when the system volume has changed (eg when user changes volume using the device volume buttons). Update calls are done on the UI (i.e. main) thread and therefore are safe to update UI elements within the interface method implementation.
         *
         * @author Ephraim A. Tekle
         *
         */
        public static interface VolumeChangeListener {
            public void volumeChanged(float volume);
        }
    }
}
