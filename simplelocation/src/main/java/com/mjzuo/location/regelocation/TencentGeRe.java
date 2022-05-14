package com.mjzuo.location.regelocation;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.Nullable;

import com.mjzuo.location.ReverseGeocodingManager;
import com.mjzuo.location.bean.Latlng;
import com.mjzuo.location.constant.UrlConstant;
import com.mjzuo.location.helper.Helper;
import com.mjzuo.location.net.NetUtil;
import com.mjzuo.location.util.CommonUtil;
import com.mjzuo.location.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TencentGeRe implements IReGe {

    private String ak = "TF7BZ-6KYC4-2KBUK-D63XI-WJLBO-TKBGJ";
    private String sk = "KUmq7ZNVgispZZoRcQxULuJK2KXxngs0";
    private String host = "/ws/geocoder/v1/";

    private LinkedHashMap<String, String> hashMap;
    // 默认使用sn校验方式
    private boolean isSn = true;

    private MyAsyncTask task;

    private IReGeListener mListener;

    @Override
    public void init(@Nullable Context context) {
        hashMap = new LinkedHashMap<>();
    }
    
    @Override
    public void setOptions(@Nullable ReverseGeocodingManager.ReGeOption options) {
        this.isSn = options.isSn();
        if(options.getKey() != null && !options.getKey().isEmpty())
            this.ak = options.getKey();
        if(options.getSk() != null && !options.getSk().isEmpty())
            this.sk = options.getSk();
    }

    @Override
    public void reGeToAddress(Latlng latlng) {
        if (null == hashMap) { return; }

        hashMap.put("location", latlng.getLatitude()+","+latlng.getLongitude());
        hashMap.put("key", ak);
        hashMap.put("get_poi", "1");
        if(isSn){
            LinkedHashMap<String, String> linkedHashMap = hashMapBySort(hashMap);
            String sn = CommonUtil.MD5(Helper.toAppendUrlWithoutEncode(linkedHashMap, "", host) + sk);
            hashMap.put("sig", sn);
        }
        task = new MyAsyncTask(Helper.toAppendUrl(hashMap, UrlConstant.TENCENT_URL, host));
        task.execute();
    }

    @Override
    public void stop() {
        if(hashMap != null)
            hashMap.clear();
        hashMap = null;
        task = null;
        mListener = null;
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

    private LinkedHashMap<String, String> hashMapBySort(HashMap<String, String> hashMap) {
        List<Map.Entry<String,String>> list = new ArrayList<>(hashMap.entrySet());
        Collections.sort(list,new Comparator<Map.Entry<String, String>>() {
            // 升序排序
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
        for(Map.Entry<String,String> map : list){
            linkedHashMap.put(map.getKey(), map.getValue());
        }
        return linkedHashMap;
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
            Integer state;
            try {
                state = (Integer) jsonObject.get("status");
                // 0请求成功标志码
                if(state != 0){
                    if(jsonObject.has("message"))
                        mListener.onFail(state, jsonObject.getString("message"));
                    return;
                }
                if(jsonObject.has("result")){
                    JSONObject obj;
                    JSONObject objAddress;
                    JSONObject objAdInfo;
                    JSONArray objArray;
                    try{
                        obj = jsonObject.getJSONObject("result");
                        objAddress = obj.getJSONObject("address_component");
                        Latlng latlng = new Latlng();
                        latlng.setCountry(objAddress.getString("nation"));
                        latlng.setCity(objAddress.getString("city"));
                        latlng.setSublocality(objAddress.getString("district"));
                        latlng.setAddress(objAddress.getString("street"));

                        objAdInfo = obj.getJSONObject("ad_info");
                        latlng.setCityCode(objAdInfo.getString("city_code"));

                        objArray = obj.getJSONArray("pois");
                        if(objArray != null && objArray.length() != 0){
                            String name = objArray.getJSONObject(0).getString("title");
                            latlng.setName(name);
                        }
                        mListener.onSuccess(state, latlng);
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
