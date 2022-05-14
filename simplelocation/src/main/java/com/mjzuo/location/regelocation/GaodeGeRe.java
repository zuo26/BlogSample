package com.mjzuo.location.regelocation;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.Nullable;

import com.mjzuo.location.ReverseGeocodingManager;
import com.mjzuo.location.bean.Latlng;
import com.mjzuo.location.constant.UrlConstant;
import com.mjzuo.location.helper.Helper;
import com.mjzuo.location.net.NetUtil;
import com.mjzuo.location.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class GaodeGeRe implements IReGe {

    private String ak = "74fb103d602ca3fd94be9b25ac9cacb2";
    private String host = "/v3/geocode/regeo";

    private HashMap<String, String> hashMap;
    private MyAsyncTask task;
    private IReGeListener mListener;

    @Override
    public void init(@Nullable Context context) {
        hashMap = new HashMap<>();
        hashMap.put("key",ak);
    }

    @Override
    public void setOptions(@Nullable ReverseGeocodingManager.ReGeOption options) {
        if(options.getKey() != null && !options.getKey().isEmpty())
            this.ak = options.getKey();
    }

    @Override
    public void reGeToAddress(Latlng latlng) {
        hashMap.put("location", latlng.getLongitude()+","+latlng.getLatitude());

        task = new MyAsyncTask(Helper.toAppendUrl(hashMap, UrlConstant.GAODE_URL, host));
        task.execute();
    }

    @Override
    public void stop() {
        if(hashMap != null)
            hashMap.clear();
        hashMap = null;
        mListener = null;
        task = null;
    }

    @Override
    public void addReGeListener(IReGeListener listener) {
        mListener = listener;
    }

    class MyAsyncTask extends AsyncTask<Void, Void, String> {

        String mUrl;
        public MyAsyncTask(String url) {
            mUrl = url;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return NetUtil.doHttpGet(mUrl);
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            fromJson(json);
            task = null;
        }
    }

    /**
     *  解析json
     */
    private void fromJson(String json) {
        LogUtil.e("json:"+json);
        if(json == null || json.isEmpty())
            return;
        if(mListener == null)
            return;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
        }catch (Exception e){
            LogUtil.e("error:" + e.getMessage());
            return;
        }
        if(jsonObject.has("status")){
            String state;
            try {
                state = jsonObject.getString("status");
                // 1请求成功标志码
                if(!"1".equals(state)){
                    if(jsonObject.has("info"))
                        mListener.onFail(Integer.parseInt(state), jsonObject.getString("info"));
                    return;
                }
                if(jsonObject.has("regeocode")){
                    JSONObject obj;
                    JSONObject objAddress;
                    try{
                        obj = jsonObject.getJSONObject("regeocode");
                        objAddress = obj.getJSONObject("addressComponent");
                        Latlng latlng = new Latlng();
                        latlng.setCountry(objAddress.getString("country"));
                        latlng.setCity(objAddress.getString("province"));
                        latlng.setSublocality(objAddress.getString("district"));
                        latlng.setCityCode(objAddress.getString("citycode"));
                        latlng.setAddress(objAddress.getString("township"));
                        JSONArray nameArray;
                        JSONObject nameObj;
                        if(objAddress.has("businessAreas")){
                            try{
                                nameArray = objAddress.getJSONArray("businessAreas");
                                if(nameArray != null && nameArray.length() > 0){
                                    nameObj = nameArray.getJSONObject(0);
                                    latlng.setName(nameObj.getString("name"));
                                }
                            }catch (Exception e){
                                LogUtil.e("error:" + e.getMessage());
                            }
                        }
                        mListener.onSuccess(Integer.parseInt(state), latlng);
                    }catch (Exception e){
                        LogUtil.e("error:"+e.getMessage());
                        return;
                    }
                }
            }catch (Exception e){
                LogUtil.e("error:" + e.getMessage());
                return;
            }
        }
    }
}
