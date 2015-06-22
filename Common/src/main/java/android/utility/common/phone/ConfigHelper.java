package android.utility.common.phone;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.Window;
import android.view.WindowManager;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
/**
 * Created by lqnhu on 6/22/15.
 */
public class ConfigHelper {

    public static boolean getWifiStatu(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    public static void setWifiStatu(Context context, boolean isopen) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(isopen);
    }

    public static boolean getGprsStatu(Context context) {
        boolean isopen = false;
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Class<?> conMgrClass = null;
        Field iConMgrField = null;
        Object iConMgr = null;
        Class<?> iConMgrClass = null;
        Method getMobileDataEnabledMethod = null;
        try {
            conMgrClass = Class.forName(conMgr.getClass().getName());
            iConMgrField = conMgrClass.getDeclaredField("mService");
            iConMgrField.setAccessible(true);
            iConMgr = iConMgrField.get(conMgr);
            iConMgrClass = Class.forName(iConMgr.getClass().getName());
            getMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("getMobileDataEnabled");
            getMobileDataEnabledMethod.setAccessible(true);
            isopen = (Boolean) getMobileDataEnabledMethod.invoke(iConMgr);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return isopen;
    }

    public static void setGprsStatu(Context context, boolean isopen) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Class<?> conMgrClass = null;
        Field iConMgrField = null;
        Object iConMgr = null;
        Class<?> iConMgrClass = null;
        Method setMobileDataEnabledMethod = null;
        try {
            conMgrClass = Class.forName(conMgr.getClass().getName());
            iConMgrField = conMgrClass.getDeclaredField("mService");
            iConMgrField.setAccessible(true);
            iConMgr = iConMgrField.get(conMgr);
            iConMgrClass = Class.forName(iConMgr.getClass().getName());
            setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);
            setMobileDataEnabledMethod.invoke(iConMgr, isopen);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static boolean getGpsStatu(Context context) {
        LocationManager alm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }

    public static void setGpsStatu(Activity caller) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        caller.startActivityForResult(intent, 0);
    }

    public static boolean getBluetoothStatu() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter.isEnabled();
    }

    public static void setBluetoothStatu(boolean isOpen) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (isOpen) {
            bluetoothAdapter.enable();
        } else {
            bluetoothAdapter.disable();
        }
    }

    public static boolean getFlightMode(Context context) {
        boolean isOpen = false;
        ContentResolver resolver = context.getContentResolver();
        if (Settings.System.getString(resolver, Settings.System.AIRPLANE_MODE_ON).equals("0")) {
            isOpen = false;
        } else {
            isOpen = true;
        }
        return isOpen;
    }

    public static void setFiightMode(Context context, boolean isOpen) {
        ContentResolver resolver = context.getContentResolver();
        if (isOpen) {
            Settings.System.putString(resolver, Settings.System.AIRPLANE_MODE_ON, "1");
            Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            context.sendBroadcast(intent);
        } else {
            Settings.System.putString(resolver, Settings.System.AIRPLANE_MODE_ON, "0");
            Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            context.sendBroadcast(intent);
        }
    }

    public static boolean getScreenRotation(Context context) {
        boolean isOpen = false;
        try {
            int status = Settings.System.getInt(context.getContentResolver(), "accelerometer_rotation");
            if (status == 1) {
                isOpen = true;
            } else {
                isOpen = false;
            }
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        return isOpen;
    }

    public static void setScreenRotation(Context context, boolean isOpen) {
        if (isOpen) {
            Settings.System.putInt(context.getContentResolver(), "accelerometer_rotation", 1);
        } else {
            Settings.System.putInt(context.getContentResolver(), "accelerometer_rotation", 0);
        }
    }

    public static void Shutdown(Context context, long time) {
    }

    public static int getScreenMode(Context context) {
        int screenMode = 0;
        try {
            screenMode = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return screenMode;
    }

    public static int getScreenBrightness(Context context) {
        int screenBrightness = 255;
        try {
            screenBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return screenBrightness;
    }

    public static void setScreenMode(int paramInt, Context context) {
        try {
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, paramInt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveScreenBrightness(int paramInt, Context context) {
        try {
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, paramInt);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    public static void setScreenBrightness(int paramInt, Activity caller) {
        Window window = caller.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        float f = paramInt / 255.0F;
        params.screenBrightness = f;
        window.setAttributes(params);
    }

    public static int getCallSound(Context context) {
        int current = 0;
        try {
            AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            current = manager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        } catch (Exception e) {
        }
        return current;
    }

    public static int getCallMaxSound(Context context) {
        int max = 0;
        try {
            AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            max = manager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
        } catch (Exception e) {
        }
        return max;
    }

    public static int getSystemSound(Context context) {
        int current = 0;
        try {
            AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            current = manager.getStreamVolume(AudioManager.STREAM_SYSTEM);
        } catch (Exception e) {
        }
        return current;
    }

    public static int getSystemMaxSound(Context context) {
        int max = 0;
        try {
            AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            max = manager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
        } catch (Exception e) {
        }
        return max;
    }

    public static int getRingtoneSound(Context context) {
        int current = 0;
        try {
            AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            current = manager.getStreamVolume(AudioManager.STREAM_RING);
        } catch (Exception e) {
        }
        return current;
    }

    public static int getRingtoneMaxSound(Context context) {
        int max = 0;
        try {
            AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            max = manager.getStreamMaxVolume(AudioManager.STREAM_RING);
        } catch (Exception e) {
        }
        return max;
    }

    public static int getMusicSound(Context context) {
        int current = 0;
        try {
            AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            current = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
        } catch (Exception e) {
        }
        return current;
    }

    public static int getMusicMaxSound(Context context) {
        int max = 0;
        try {
            AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            max = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        } catch (Exception e) {
        }
        return max;
    }

    public static int getAlarmSound(Context context) {
        int current = 0;
        try {
            AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            current = manager.getStreamVolume(AudioManager.STREAM_ALARM);
        } catch (Exception e) {
        }
        return current;
    }

    public static int getAlarmMaxSound(Context context) {
        int max = 0;
        try {
            AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            max = manager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        } catch (Exception e) {
        }
        return max;
    }

    public static void setAllSound(Context context, int systemsound, int callsound, int ringtonesound, int musicsound, int alarmsound) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        try {
            manager.setStreamVolume(AudioManager.STREAM_SYSTEM, systemsound, 0);
            manager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, callsound, 0);
            manager.setStreamVolume(AudioManager.STREAM_RING, ringtonesound, 0);
            manager.setStreamVolume(AudioManager.STREAM_MUSIC, musicsound, 0);
            manager.setStreamVolume(AudioManager.STREAM_ALARM, alarmsound, 0);
        } catch (Exception e) {
        }
    }

    public static int getSceneMode(Context context) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return manager.getRingerMode();
    }

    public static void setSceneMode(Context context, int mode) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        switch (mode) {
            case 0:
                manager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                break;
            case 1:
                manager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                break;
            case 2:
                manager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                break;
            default:
                break;
        }
    }
}
