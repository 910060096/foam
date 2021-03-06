package com.percolate.foam;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Foam utility methods.
 *
 * Some methods were taken from Apache Commons Lang (http://commons.apache.org/proper/commons-lang/)
 * Copied over to avoid requiring additional dependencies.
 *
 */
class Utils {

    protected FoamLogger foamLogger = new FoamLogger();

    /**
     * Checks if a CharSequence is whitespace, empty ("") or null.
     *
     * @param cs the CharSequence to check, may be null
     * @return true if the CharSequence is null, empty or whitespace
     */
    public boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a CharSequence is not empty (""), not null and not whitespace only.
     *
     * @param cs the CharSequence to check, may be null
     * @return true if the CharSequence is not empty and not null and not whitespace
     */
    public boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    /**
     * Trim a given string to maxStringLength if it is over maxStringLength.
     *
     * @param str String to trim
     * @param maxStringLength Max length of string to return
     * @return str, trimmed to maxStringLength.
     */
    public String trimToSize(String str, int maxStringLength) {
        if(str == null)
            return null;

        if(str.length() > maxStringLength) {
            StringBuilder sb = new StringBuilder(str);
            sb.setLength(maxStringLength);
            str = sb.toString();
        }
        return str;
    }

    /**
     * Perform system sleep for the given number of milliseconds
     * @param ms Milliseconds to sleep for
     */
    public void sleep(int ms) {
        SystemClock.sleep(ms);
    }

    /**
     * Foam error logging.  Log a warning message using the TAG "Foam".  Include stacktrace of
     * a Throwable, if provided.
     *
     * @param message Message to log
     * @param ex Optional Throwable.  Stacktrace will be printed if provided.
     */
    public void logIssue(String message, Throwable ex){
        foamLogger.w(message, ex);
    }

    /**
     * Foam logger class used to log warning message
     */
    protected class FoamLogger {
        protected void w(String message, Throwable ex){
            if(ex != null) {
                Log.w("Foam", "Foam library: problem detected: " + message, ex);
            } else {
                Log.w("Foam", "Foam library: problem detected: " + message);
            }
        }
    }

    /**
     * Get application "label" value from Manifest.
     *
     * @param context Context
     * @return Application label from Manifest
     */
    public String getApplicationName(Context context) {
        int stringId = context.getApplicationInfo().labelRes;
        return context.getString(stringId);
    }

    /**
     * Get versionName value from Manifest.
     *
     * @param context Context
     * @return Application versionName from Manifest
     */
    public String getVersionName(Context context) {
        String versionName;
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "";
        }
        return versionName;
    }

    /**
     * Get versionCode value from Manifest.
     *
     * @param context Context
     * @return Application versionCode from Manifest
     */
    public int getVersionCode(Context context) {
        int versionCode;
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            versionCode = -1;
        }
        return versionCode;
    }

    /**
     * Get package name from Manifest.
     *
     * @param context Context
     * @return Application package from Manifest
     */
    public String getApplicationPackageName(Context context) {
        String packageName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            packageName = packageInfo.packageName;
        } catch (PackageManager.NameNotFoundException ex){
            logIssue("Could not find package name.", ex);
        }
        return packageName;
    }

    /**
     * Get ANDROID_ID, which is unique to the device + user combo.  Can sometimes be null in theory.
     *
     * @param context Context
     * @return Unique User+Device identifier.  Will never be null.  Can be blank.
     */
    public @NonNull String getAndroidId(Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if(androidId == null){
            return "";
        } else {
            return androidId;
        }
    }

    /**
     * Check to see if the device is currently connected to a WiFi network.  Used along with
     * {@link FoamApiKeys#wifiOnly()} to only send data over WiFi.
     *
     * @param context Context
     * @return true if the device is currently connected to a WiFi network.
     */
    public boolean isOnWifi(Context context) {
        try {
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWifi.isConnected()) {
                return true;
            }
        } catch (Exception ex){
            logIssue("Error checking wifi state", ex);
        }
        return false;
    }
}
