package com.mjzuo.location.util;

import android.util.Log;

public class LogUtil {

    public static boolean DEBUG= false;

    public static final String TAG = "tag_sl";

    public static void d(String text){
        if (DEBUG){
            Log.d(TAG,text);
        }
    }

    public static void i(String text){
        if(DEBUG){
            Log.i(TAG,text);
        }
    }

    public static void w(String text){
        if(DEBUG){
            Log.w(TAG,text);
        }
    }

    public static void e(String text){
        if(DEBUG){
            Log.e(TAG,text);
        }
    }
}
