package com.mjzuo.location.helper;

import android.content.Context;

import com.mjzuo.location.constant.Constant;
import com.mjzuo.location.location.BsGeocoding;
import com.mjzuo.location.location.GoogleGeocoding;
import com.mjzuo.location.location.IGeocoding;
import com.mjzuo.location.location.OpenCellidGeocoding;
import com.mjzuo.location.regelocation.BaiduGeRe;
import com.mjzuo.location.regelocation.GaodeGeRe;
import com.mjzuo.location.regelocation.GoogleRege;
import com.mjzuo.location.regelocation.IReGe;
import com.mjzuo.location.regelocation.TencentGeRe;

public class GeReFactory {

    public static IReGe getReGeByType(int reGeType){
        IReGe iReGe;
        switch (reGeType) {
            case Constant.GOOGLE_API:
                iReGe = new GoogleRege();
                break;
            case Constant.BAIDU_API:
                iReGe = new BaiduGeRe();
                break;
            case Constant.TENCENT_API:
                iReGe = new TencentGeRe();
                break;
            case Constant.GAODE_API:
                iReGe = new GaodeGeRe();
                break;
            default:
                iReGe = new GoogleRege();
        }
        return iReGe;
    }

    public static IGeocoding getGeocodingType(Context context, int geocodingType){
        IGeocoding iGeocoding;
        switch (geocodingType) {
            case Constant.LM_API:
                iGeocoding = new GoogleGeocoding(context);
                break;
            case Constant.BS_UNWIRED_API:
                iGeocoding = new BsGeocoding(context);
                break;
            case Constant.BS_OPENCELLID_API:
                iGeocoding = new OpenCellidGeocoding(context);
                break;
            default:
                iGeocoding =  new GoogleGeocoding(context);
        }
        return iGeocoding;
    }
}
