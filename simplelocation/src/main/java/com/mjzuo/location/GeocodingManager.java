package com.mjzuo.location;

import android.content.Context;

import androidx.annotation.Nullable;

import com.mjzuo.location.constant.Constant;
import com.mjzuo.location.helper.GeReFactory;
import com.mjzuo.location.location.GoogleGeocoding;
import com.mjzuo.location.location.IGeocoding;
import com.mjzuo.location.util.LogUtil;

/**
 *  这是定位的类
 *
 * @author mingjiezuo
 * @since 19/09/05
 */
public class GeocodingManager {

    private IGeocoding mGeocoding;

    private IGeocoding.ISiLoResponseListener mListener;

    private Context mContext;

    public GeocodingManager(Context context) {
        mContext = context;
        mGeocoding = GeReFactory.getGeocodingType(mContext, Constant.LM_API);
    }

    public GeocodingManager(Context context, @Nullable GeoOption option) {
        mContext = context;
        mGeocoding = GeReFactory.getGeocodingType(mContext, option.getGeoType());
        if(mGeocoding instanceof GoogleGeocoding)
            ((GoogleGeocoding) mGeocoding).setSimpleLocationOption(option.getOption());
    }

    public void start(IGeocoding.ISiLoResponseListener listener) {
        if(listener != null)
            mListener = listener;
        if(mListener == null){
            LogUtil.e("simple location response listener null");
        }
        mGeocoding.start(mListener);
    }

    public void reStart(){
        start(null);
    }

    public void stop(){
        mGeocoding = null;
        mListener = null;
    }

    public static class GeoOption {
        /**
         *  定位的类型
         *
         *  LocationManager或基站定位
         */
        private int GeoType = Constant.LM_API;

        /**
         *  这是locationManager的配置类
         */
        private GoogleGeocoding.SiLoOption option;

        public int getGeoType() {
            return GeoType;
        }

        public GeoOption setGeoType(int geoType) {
            GeoType = geoType;
            return this;
        }

        public GoogleGeocoding.SiLoOption getOption() {
            return option;
        }

        public GeoOption setOption(GoogleGeocoding.SiLoOption option) {
            this.option = option;
            return this;
        }
    }

}
