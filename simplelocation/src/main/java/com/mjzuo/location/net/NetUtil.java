package com.mjzuo.location.net;

import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetUtil {

    /**
     *  HttpURLConnection get 请求
     * @param url
     * @return
     */
    public static String doHttpGet(String url){
        try{
            URL myUrl = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) myUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(60000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setUseCaches(true);
            urlConnection.setRequestProperty("Content-Type","application/json");
            urlConnection.setDoInput(true);
            // 注意：android4.0后，doOutPut=true后，会自动将请求转换成post请求
            // 腾讯白名单和sn校验方式都不支持post;百度sn校验不支持post请求
            urlConnection.setDoOutput(false);

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedInputStream responseStream = new BufferedInputStream(urlConnection.getInputStream());
                byte[] result = StreamUtil.toBytes(responseStream);
                return result == null ? "" : new String(result) ;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     *  httpUrlConnection post 请求
     * @param url
     * @param params
     * @return
     */
    public static String doHttpPost(String url, byte[] params){
        if (params == null){
            return null;
        }
        try{
            URL myUrl = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) myUrl.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setRequestProperty("Content-Type","application/json");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(params);

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedInputStream responseStream = new BufferedInputStream(urlConnection.getInputStream());
                byte[] result = StreamUtil.toBytes(responseStream);
                return result == null ? "" : new String(result) ;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
