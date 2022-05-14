package com.mjzuo.location.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.mjzuo.location.bean.Latlng;
import com.mjzuo.location.helper.ConverHelper;
import com.mjzuo.location.helper.Helper;
import com.mjzuo.location.util.LogUtil;

/**
 *  LocationManager获取经纬度。
 *
 * @author mingjiezuo
 * @since 19/08/28
 */
public class GoogleGeocoding implements IGeocoding {

    /** 当前系统定位manager*/
    private LocationManager lm;
    /** 位置提供器*/
    private String mProvider;

    /** 定位change监听*/
    private MyLocationListener mChangeListener;
    /** 响应用户的回调*/
    private ISiLoResponseListener mListener;
    /** 服务的参数配置类*/
    private SiLoOption mSiLoOption;

    /** 当前经纬度*/
    private Latlng latlng;

    /** 计数器*/
    private int mCounter;
    /** gps信号消失，尝试network定位*/
    private static final int GPS_TIME_SPACE = 6;
    /** 尝试network之后，同样获取不到定位，则错误回调，时间间隔 = NET_TIME_SPACE - GPS_TIME_SPACE*/
    private static final int NET_TIME_SPACE = 12;

    private Context mContext;

    public GoogleGeocoding(Context context) {
        this.mContext = context;
        this.mChangeListener = new MyLocationListener();
    }

    public void setSimpleLocationOption(@Nullable SiLoOption siLoOption) {
        this.mSiLoOption = siLoOption;
    }

    @Override
    public void start(@Nullable ISiLoResponseListener listener) {
        if(listener != null)
            this.mListener = listener;
        if(mListener == null)
            return;
        lm = (LocationManager) mContext.getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);
        // 检查是否有相关定位权限
        if(!Helper.checkPermission(mContext.getApplicationContext())){
            mListener.onFail("location no permission");
            return;
        }
        // 如果不传配置类，则按默认配置
        if(mSiLoOption == null)
            mSiLoOption = new SiLoOption();
        mProvider = mSiLoOption.isGpsFirst ? Helper.getGPSProvider(lm) : Helper.getNetWorkProvider(lm);
        if(mProvider == null)
            mListener.onFail("location provider no exist");
        else
            getLocation(mProvider);
    }

    @Override
    public void reStart() {
        start(null);
    }

    @Override
    public void stop() {
        mProvider = null;
        if(lm != null)
            lm.removeUpdates(mChangeListener);
        lm = null;
        mListener = null;
        mChangeListener = null;
    }

    @SuppressWarnings("all")
    private String getLocation(String provider) {
        if(provider == null)
            return null;
        lm.requestLocationUpdates(provider, mSiLoOption.time, mSiLoOption.distance, mChangeListener);
        return provider;
    }

    class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if(mProvider == null)
                return;
            latlng = location != null ? ConverHelper.loConverToLatlng(location) : latlng;
            mListener.onSuccess(latlng);
            if(mSiLoOption.isGpsFirst){
                if(location != null){
                    if(mProvider == LocationManager.GPS_PROVIDER)
                        mCounter = 0;
                    else{
                        mCounter += mSiLoOption.time/1000;
                        if(mCounter > NET_TIME_SPACE){
                            mProvider = getLocation(Helper.getGPSProvider(lm));
                            mCounter = 0;
                        }
                    }
                }else{
                    // gps无信号时，尝试network获取
                    mCounter += mSiLoOption.time/1000;
                    if(mCounter >= GPS_TIME_SPACE && mCounter < NET_TIME_SPACE){
                        mProvider = getLocation(Helper.getNetWorkProvider(lm));
                    }else if(mCounter > NET_TIME_SPACE){
                        // 只要最后一个net点失败，就抛出回调；前两个点，由于开始，可能为空，不做强制判断处理
                        mListener.onFail("location latlng = null , provider = "+ mProvider);
                    }
                }
            }else{
                // 当network获取不到定位
                if(location == null)
                    mListener.onFail("location latlng = null , provider = "+ mProvider);
            }
        }

        @Override
        public void onStatusChanged(String s, int state, Bundle bundle) {
            if(mListener == null)
                return;
            if(Build.VERSION.SDK_INT > 28){
                LogUtil.e("after api 29 always AVAILABLE");
            }else{
                switch (state){
                    case LocationProvider.OUT_OF_SERVICE:
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        LogUtil.e("current provider out of condition");
                        break;
                }
            }
        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }

    public static class SiLoOption {
        /**
         * gps 优先否
         */
        private boolean isGpsFirst = false;
        /**
         * 监听定位变化最短时间间隔，默认time s
         */
        private int time = 1;
        /**
         * 监听变化的最小距离
         */
        private int distance = 10;

        public boolean isGpsFirst() {
            return isGpsFirst;
        }

        public SiLoOption setGpsFirst(boolean gpsFirst) {
            isGpsFirst = gpsFirst;
            return this;
        }

        public int getTime() {
            return time;
        }

        public SiLoOption setTime(int time) {
            this.time = time;
            return this;
        }

        public int getRequestTepe() {
            return distance;
        }

        public SiLoOption setRequestTepe(int requestTepe) {
            this.distance = requestTepe;
            return this;
        }
    }
}
