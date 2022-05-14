package com.mjzuo.location.location;

import com.mjzuo.location.bean.Latlng;

public interface IGeocoding {

    /**
     * 开始获取位置
     */
    void start(ISiLoResponseListener listener);

    /**
     * 重新请求
     */
    void reStart();

    /**
     * 结束
     */
    void stop();

    /**
     * 结果监听
     */
    interface ISiLoResponseListener {
        void onSuccess(Latlng latlng);
        void onFail(String msg);
    }
}
