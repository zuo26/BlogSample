package com.blog.location;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.blog.BaseActivity;
import com.blog.R;
import com.mjzuo.location.GeocodingManager;
import com.mjzuo.location.ReverseGeocodingManager;
import com.mjzuo.location.bean.Latlng;
import com.mjzuo.location.constant.Constant;
import com.mjzuo.location.location.GoogleGeocoding;
import com.mjzuo.location.location.IGeocoding;
import com.mjzuo.location.regelocation.IReGe;

public class LocationActivity extends BaseActivity {

    private static final String LOG_TAG = "tag_sl";

    /** 定位获取经纬度，包括LocationManager、基站地位*/
    GeocodingManager siLoManager;
    /** 反地理编码的manager，包括google反地理、高德反地理、百度反地理、腾讯反地理*/
    ReverseGeocodingManager reGeManager;

    TextView tvSimpleLo;
    TextView tvSimpleAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_activity);

        tvSimpleLo = findViewById(R.id.tv_simple_location_txt);
        tvSimpleAd = findViewById(R.id.tv_simple_address_txt);

        ReverseGeocodingManager.ReGeOption reGeOption = new ReverseGeocodingManager.ReGeOption()
                .setReGeType(Constant.TENCENT_API)// 腾讯api返地理编码
                .setSn(true)// sn 签名校验方式
                .setIslog(true);// 打印log
        reGeManager = new ReverseGeocodingManager(this, reGeOption);
        reGeManager.addReGeListener(new IReGe.IReGeListener() {
            @Override
            public void onSuccess(int state, Latlng latlng) {
                Log.e(LOG_TAG,"reGeManager onSuccess:" + latlng);
                tvSimpleAd.setText(String.format(getResources().getString(R.string.regeoString)
                        , latlng.getCountry(), latlng.getCityCode(), latlng.getSublocality()
                        , latlng.getAddress(), latlng.getName()));
            }

            @Override
            public void onFail(int errorCode, String error) {
                Log.e(LOG_TAG,"error:" + error);
                tvSimpleAd.setText(String.format(getResources().getString(R.string.errMsg)
                        , errorCode, error));
            }
        });

        GeocodingManager.GeoOption option = new GeocodingManager.GeoOption()
                .setGeoType(Constant.LM_API) // 使用openCellid服务器的基站地位
                .setOption(new GoogleGeocoding.SiLoOption()
                        .setGpsFirst(false));// locationManager定位方式时，gps优先
        siLoManager = new GeocodingManager(this, option);
        siLoManager.start(new IGeocoding.ISiLoResponseListener() {
            @Override
            public void onSuccess(Latlng latlng) {
                Log.e(LOG_TAG,"siLoManager onSuccess:" + latlng);
                tvSimpleLo.setText(String.format(getResources().getString(R.string.geoString)
                        , latlng.getLatitude(), latlng.getLongitude(), latlng.getProvider()));
                reGeManager.reGeToAddress(latlng);
            }

            @Override
            public void onFail(String msg) {
                Log.e(LOG_TAG,"error:" + msg);
                tvSimpleAd.setText(String.format(getResources().getString(R.string.errMsg)
                        , -1, msg));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(siLoManager != null)
            siLoManager.stop();
        if(reGeManager != null){
            reGeManager.stop();
        }
    }
}
