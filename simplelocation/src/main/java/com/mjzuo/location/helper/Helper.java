package com.mjzuo.location.helper;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class Helper {

    /**
     *  gps 提供器
     * @param locationManager
     * @return
     */
    public static String getGPSProvider(LocationManager locationManager) {
        List<String> prodiverlist = locationManager.getProviders(true);
        // gps定位，建议优先，因为精度较高
        if(prodiverlist.contains(LocationManager.GPS_PROVIDER))
            return LocationManager.GPS_PROVIDER;
        return null;
    }

    /**
     *  network 提供器
     * @param locationManager
     * @return
     */
    public static String getNetWorkProvider(LocationManager locationManager) {
        List<String> prodiverlist = locationManager.getProviders(true);
        // 网络定位
        if(prodiverlist.contains(LocationManager.NETWORK_PROVIDER))
            return LocationManager.NETWORK_PROVIDER;
        return null;
    }

    /**
     *  检查定位权限
     */
    public static boolean checkPermission(Context mContext) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.INTERNET)
                == PackageManager.PERMISSION_GRANTED)
            return true;
        return false;
    }

    /**
     *  检查手机权限
     */
    public static boolean checkPhonePermission(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED)
            return true;
        return false;
    }

    /**
     *  拼接Url
     */
    public static String toAppendUrl(Map<?, ?> data, String url, String host) {
        StringBuffer queryString = new StringBuffer();
        queryString.append(url);
        queryString.append(host);
        int index = 0;
        for (Map.Entry<?, ?> pair : data.entrySet()) {
            if(index == 0)
                queryString.append("?");
            else
                queryString.append("&");
            queryString.append(pair.getKey() + "=");
            String str = pair.getValue().toString();
            try {
                str = URLEncoder.encode(str, "utf-8");
            }catch (Exception e){
                Log.e("log_sl", "error:" + e.getMessage());
            }
            queryString.append(str);

            index ++;
        }
        return queryString.toString();
    }

    /**
     *  拼接url, 不进行编码
     */
    public static String toAppendUrlWithoutEncode(Map<?, ?> data, String url, String host) {
        StringBuffer queryString = new StringBuffer();
        queryString.append(url);
        queryString.append(host);
        int index = 0;
        for (Map.Entry<?, ?> pair : data.entrySet()) {
            if(index == 0)
                queryString.append("?");
            else
                queryString.append("&");
            queryString.append(pair.getKey() + "=" + pair.getValue());

            index ++;
        }
        return queryString.toString();
    }
}
