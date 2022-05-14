package com.mjzuo.location.regelocation;

import android.content.Context;

import androidx.annotation.Nullable;

import com.mjzuo.location.ReverseGeocodingManager;
import com.mjzuo.location.bean.Latlng;

public interface IReGe {

    /**
     * 初始化工作
     */
    void init(@Nullable Context context);

    /**
     *  配置参数
     */
    void setOptions(@Nullable ReverseGeocodingManager.ReGeOption options);

    /**
     * 反向编码
     */
    void reGeToAddress(Latlng latlng);

    /**
     * 结束
     */
    void stop();

    /**
     * 注册监听
     */
    void addReGeListener(IReGeListener listener);

    /**
     * 反编码的回调
     */
    interface IReGeListener {
        void onSuccess(int state, Latlng latlng);
        void onFail(int errorCode, String error);
    }
}
