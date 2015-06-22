package android.utility.common.system;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.Provider;
import java.util.ArrayList;
import java.util.UUID;

import android.app.KeyguardManager;
import android.content.Context;
import java.util.List;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.utility.common.common.ListUtils;
import android.utility.common.common.ObjectUtils;
import android.utility.common.constant.AppInfo;
import android.net.Uri;
import android.os.PowerManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by lqnhu on 6/20/15.
 */
public final class AppInstallUtil {
    private static String sID = null;
    private static final String INSTALLATION = "app_installation_identifier";
    public synchronized static String id(Context context) {
        if (sID == null) {
            File installation = new File(context.getFilesDir(), INSTALLATION);
            try {
                if (!installation.exists()) {
                    writeInstallationFile(installation);
                }
                sID = readInstallationFile(installation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sID;
    }
    private static String readInstallationFile(File installation)
            throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }
    private static void writeInstallationFile(File installation)
            throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        out.close();
    }

    public static boolean isNamedProcess(Context context, String processName) {
        if (context == null) {
            return false;
        }
        int pid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> processInfoList = manager.getRunningAppProcesses();
        if (ListUtils.isEmpty(processInfoList)) {
            return false;
        }
        for (RunningAppProcessInfo processInfo : processInfoList) {
            if (processInfo != null && processInfo.pid == pid
                    && ObjectUtils.isEquals(processName, processInfo.processName)) {
                return true;
            }
        }
        return false;
    }
    /**
     * whether application is in background
     * <ul>
     * <li>need use permission android.permission.GET_TASKS in Manifest.xml</li>
     * </ul>
     *
     * @param context
     * @return if application is in background return true, otherwise return false
     */
    public static boolean isApplicationInBackground(Context context) {
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> taskList = am.getRunningTasks(1);
        if (taskList != null && !taskList.isEmpty()) {
            ComponentName topActivity = taskList.get(0).topActivity;
            if (topActivity != null && !topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the application versionname.
     * @param context
     * @return
     */
    public static String getAppVersion(Context context) {
        String version = "";
        PackageInfo pInfo;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return version;
    }
    /**
     * Starts Google Play application for this application.
     * @param context
     */
    public static void startGooglePlay(Context context) {
        final String appPackageName = context.getPackageName();// context.getPackageName();
// //
// getPackageName()
// from Context
// or Activity
// object
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }
    /**
     * Starts Google Play Services in Play application.
     * @param context
     */
    public static void startGooglePlayServicesRefresh(Context context){
        try{
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.gms")));
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /**
     * Wake ups the phone.
     * @param context
     */
    public static void partialWakeUpLock(Context context){
        PowerManager pm = (PowerManager)context.getApplicationContext().getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
        wakeLock.acquire();
    }
    /**
     * Release the devices screen lock.
     * @param context
     */
    public static void releaseScreenLock(Context context){
        KeyguardManager keyguardManager = (KeyguardManager) context.getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
        keyguardLock.disableKeyguard();
    }
    /**
     * Wake ups and release the screen lock.
     * @param context
     */
    public static void wakeLockWithScreenUnLock(Context context){
        partialWakeUpLock(context);
        releaseScreenLock(context);
    }

    public static boolean isServiceRunning(Class<? extends Provider.Service> service, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.getName().equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //---------------------------------------------------------------------------------

    public static int ALL_APPS = 0;
    public static int SYSTEM_APPS = 1;
    public static int NONSYSTEM_APPS = 2;
    public static int ABOUNT = 3;
    public static final int MSG_PROCESSING = 0;
    public static final int MSG_DONE = 1;
    public static final int MSG_ERROR = 2;
    public static final int ACTIVITIES = 0;
    public static final int RECEIVERS = 1;
    public static final int SERVICES = 2;
    public static final String PKGINFO_KEY = "pkginfo";
    public static final String APPTYPE_KEY = "apptype";
    public static List<AppInfo> getPackageInfo(Context context, int type){
        List<AppInfo> pkgInfoList = new ArrayList<AppInfo>();
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(
                PackageManager.GET_DISABLED_COMPONENTS
                        | PackageManager.GET_ACTIVITIES
                        | PackageManager.GET_RECEIVERS
                        | PackageManager.GET_INSTRUMENTATION
                        | PackageManager.GET_SERVICES);
        for(int i=0;i<packages.size();i++) {
            PackageInfo packageInfo = packages.get(i);
            if (type == SYSTEM_APPS)
            {
                if((packageInfo.applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM) == 1)
                {
                    pkgInfoList.add(fillAppInfo(packageInfo, context));
                }
            }else if(type == NONSYSTEM_APPS)
            {
                if((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
                {
                    pkgInfoList.add(fillAppInfo(packageInfo, context));
                }
            }else
            {
                pkgInfoList.add(fillAppInfo(packageInfo, context));
            }
        }
        return pkgInfoList;
    }
    private static AppInfo fillAppInfo(PackageInfo packageInfo, Context context){
        AppInfo appInfo = new AppInfo();
        appInfo.setPackageInfo(packageInfo);
        appInfo.setAppName(packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString());
        appInfo.setPackageName(packageInfo.packageName);
        appInfo.setAppIcon( packageInfo.applicationInfo.loadIcon(context.getPackageManager()));
        return appInfo;
    }
}
