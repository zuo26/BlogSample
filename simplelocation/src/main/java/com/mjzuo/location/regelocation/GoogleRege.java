package com.mjzuo.location.regelocation;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;

import androidx.annotation.Nullable;

import com.mjzuo.location.ReverseGeocodingManager;
import com.mjzuo.location.bean.Latlng;
import com.mjzuo.location.constant.ResponseConstant;
import com.mjzuo.location.helper.ConverHelper;
import com.mjzuo.location.util.LogUtil;

import java.io.IOException;
import java.util.List;

public class GoogleRege implements IReGe {
    private static int MAX_RESULTS = 1;

    private Criteria mCriteria;
    /** 编码监听*/
    private IReGeListener mListener;
    /** 反编码类*/
    private Geocoder geocoder;

    private List<Address> mAddresses;

    private Context mContext;

    @Override
    public void init(Context context) {
        if(!Geocoder.isPresent()){
            if(mListener != null)
                mListener.onFail(ResponseConstant.GOOGLE_API_OUT_OF_CONDITON, "geocoder is out of condition");
            return;
        }
        mContext = context;
        if(mCriteria == null){
            mCriteria = new Criteria();
            mCriteria.setAccuracy(Criteria.ACCURACY_FINE);
            mCriteria.setAltitudeRequired(false);
            mCriteria.setBearingRequired(false);
            mCriteria.setCostAllowed(true);
            mCriteria.setPowerRequirement(Criteria.POWER_LOW);
        }
    }

    @Override
    public void setOptions(@Nullable ReverseGeocodingManager.ReGeOption options) {

    }

    @Override
    public void reGeToAddress(Latlng latlng) {
        geocoder = new Geocoder(mContext.getApplicationContext());
        try {
            mAddresses = geocoder.getFromLocation(latlng.getLatitude(), latlng.getLongitude(), MAX_RESULTS);
        }catch (IOException e){
            if(mListener != null)
                mListener.onFail(ResponseConstant.GOOGLE_API_ON_FALI, "geocoder get from location error:"+e.getMessage());
            else
                LogUtil.e("geocoder get from location error:"+e.getMessage());
        }
        if(mAddresses != null && mAddresses.size() != 0){
            latlng = ConverHelper.asConverToLatlng(latlng, mAddresses.get(0));
            if(mListener == null)
                LogUtil.e("geocoder listener null");
            else
                mListener.onSuccess(ResponseConstant.GOOGLE_API_SUCCESS, latlng);
        }else{
            LogUtil.e("geocoder get from location address size null or 0");
        }
    }

    @Override
    public void stop() {
        mCriteria = null;
        mAddresses = null;
    }

    @Override
    public void addReGeListener(IReGeListener listener) {
        this.mListener = listener;
    }
}
