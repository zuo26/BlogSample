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

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class BaiduGeRe implements IReGe {

    private String ak = "KHVTQZiP2UGuv7SkNbqYPKu4co7kbkS4";
    private String sk = "cTqIacm4uvDnQWpWWCZGElhbIx4Nxv3q";
    private String host = "/reverse_geocoding/v3/";

    private LinkedHashMap<String, String> paramsMap;
    // 默认使用sn校验方式
    private boolean isSn = true;

    private MyAsyncTask task;

    private IReGeListener mListener;

    @Override
    public void init(@Nullable Context context) {
        paramsMap = new LinkedHashMap<>();
        paramsMap.put("ak", ak);
        paramsMap.put("coordtype", "wgs84ll");
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
        paramsMap.put("location", latlng.getLatitude() + "," + latlng.getLongitude());
        paramsMap.put("output", "json");
        if(isSn) {
            String paramsStr;
            try {
                paramsStr = toQueryString(paramsMap);
            }catch (UnsupportedEncodingException e){
                LogUtil.e("error:"+e.getMessage());
                return;
            }
            String wholeStr = new String(host + "?" + paramsStr + sk);
            try {
                wholeStr = URLEncoder.encode(wholeStr, "UTF-8");
            }catch (Exception e){
                LogUtil.e("error:"+e.getMessage());
                return;
            }
            String sn = CommonUtil.MD5(wholeStr);
            paramsMap.put("sn", sn);
        }
        task = new MyAsyncTask(Helper.toAppendUrl(paramsMap, UrlConstant.BAIDU_URL, host));
        task.execute();
    }

    @Override
    public void stop() {
        if(paramsMap != null)
            paramsMap.clear();
        paramsMap = null;
        task = null;
    }

    @Override
    public void addReGeListener(IReGeListener listener) {
        mListener = listener;
    }

    class MyAsyncTask extends AsyncTask<Void, Void, String>{

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
     *  对Map内所有value作utf8编码，拼接返回结果
     * @param data
     * @return
     * @throws UnsupportedEncodingException
     */
    private String toQueryString(Map<?, ?> data)
            throws UnsupportedEncodingException {
        StringBuffer queryString = new StringBuffer();
        for (Map.Entry<?, ?> pair : data.entrySet()) {
            queryString.append(pair.getKey() + "=");
            queryString.append(URLEncoder.encode((String) pair.getValue(),
                    "UTF-8") + "&");
        }
        if (queryString.length() > 0) {
            queryString.deleteCharAt(queryString.length() - 1);
        }
        return queryString.toString();
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
                    try{
                        obj = jsonObject.getJSONObject("result");
                        objAddress = obj.getJSONObject("addressComponent");
                        Latlng latlng = new Latlng();
                        latlng.setCountry(objAddress.getString("country"));
                        latlng.setCountryCode(objAddress.getString("country_code"));
                        latlng.setCity(objAddress.getString("city"));
                        latlng.setSublocality(objAddress.getString("district"));
                        latlng.setCityCode(obj.getInt("cityCode")+"");
                        latlng.setAddress(objAddress.getString("street"));
                        latlng.setName(obj.getString("business"));
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
