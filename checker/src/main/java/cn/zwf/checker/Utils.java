package cn.zwf.checker;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;

/**
 * 工具类
 * Created by ZhangWF(zhangwf0929@gmail.com) on 16/9/6.
 */
public class Utils {

    public static String getModel(Context context) {
        return context.getString(R.string.model) + Build.MANUFACTURER + " " + Build.MODEL;
    }

    public static String getAndroidInfo(Context context) {
        return context.getString(R.string.android_version) + Build.VERSION.RELEASE + " API " + Build.VERSION.SDK_INT;
    }

    public static String getRomInfo(Context context) {
        return context.getString(R.string.rom_version) + Build.DISPLAY + " " + Build.VERSION.INCREMENTAL;
    }

    public static String getNetworkType(Context context) {
        String result = null;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) {
            result = context.getString(R.string.connectivity_type_none);
        } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
            result = context.getString(R.string.connectivity_type_wifi);
        } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            result = context.getString(R.string.connectivity_type_mobile);
        }
        return context.getString(R.string.connectivity_type) + result;
    }

    public static String getAppInfo(Context context) {
        String result = null;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packInfo = pm.getPackageInfo(context.getPackageName(), 0);
            if (packInfo != null) {
                result = pm.getApplicationLabel(packInfo.applicationInfo) + " " + packInfo.versionName + "_" + packInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return context.getString(R.string.app_info) + result;
    }

    public static boolean isEmpty(Collection c) {
        return c == null || c.size() == 0;
    }

    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    public String getLocalDNS() {
        /*
        net.dns1、net.dns2以及wifi中的net.tiwlan0.dns1以及 net.tiwlan0.dns2，移动网络中的net.rmnet0.dns1以及net.rmnet0.dns2
         */
//        String s = System.getProperty("os.name") + ";" +
//                System.getProperty("net.dns1") + ";" +
//                System.getProperty("net.dns2") + ";" +
//                System.getProperty("net.tiwlan0.dns1") + ";" +
//                System.getProperty("net.tiwlan0.dns2") + ";" +
//                System.getProperty("net.rmnet0.dns1") + ";" +
//                System.getProperty("net.rmnet0.dns2");
//        Log.d(TAG, s);

        Process cmdProcess = null;
        BufferedReader reader = null;
        String dnsIP = null;
        try {
            cmdProcess = Runtime.getRuntime().exec("getprop net.dns1");
            reader = new BufferedReader(new InputStreamReader(cmdProcess.getInputStream()));
            dnsIP = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (cmdProcess != null) {
                    cmdProcess.destroy();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return dnsIP;
    }
}
