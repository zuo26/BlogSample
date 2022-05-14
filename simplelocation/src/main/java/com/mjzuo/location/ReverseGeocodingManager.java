package com.mjzuo.location;

import android.content.Context;

import androidx.annotation.Nullable;

import com.mjzuo.location.bean.Latlng;
import com.mjzuo.location.constant.Constant;
import com.mjzuo.location.helper.GeReFactory;
import com.mjzuo.location.regelocation.IReGe;
import com.mjzuo.location.util.LogUtil;

/**
 *  特定经纬度的反向地理编码
 * @author mingjiezuo
 * @since 19/08/28
 */
public class ReverseGeocodingManager {

    IReGe mGeRe;

    Context mContext;

    public ReverseGeocodingManager(Context context) {
        mContext = context;
        mGeRe = GeReFactory.getReGeByType(Constant.GOOGLE_API);
        mGeRe.init(mContext);
    }

    public ReverseGeocodingManager(Context context, @Nullable ReGeOption option) {
        mContext = context;
        mGeRe = GeReFactory.getReGeByType(option.getReGeType());
        mGeRe.setOptions(option);
        mGeRe.init(mContext);
    }

    public void reGeToAddress(@Nullable Latlng latlng) {
        mGeRe.reGeToAddress(latlng);
    }

    public void stop() {
        mGeRe.stop();
    }

    public void addReGeListener(@Nullable IReGe.IReGeListener listener) {
        mGeRe.addReGeListener(listener);
    }

    public static class ReGeOption {
        /**
         *  api类型，默认谷歌服务
         *
         *  注意：个别机型内部移除了谷歌服务，如果不能请求，请切换别的api接口
         */
        private int reGeType = Constant.GOOGLE_API;
        /**
         *  是否sn校验，默认签名校验方式，而非白名单方式
         */
        private boolean isSn = true;
        /**
         *  key
         */
        private String key = "";
        /**
         *  sk
         */
        private String sk = "";
        /**
         *  是否打开log
         */
        private boolean islog = true;

        public int getReGeType() {
            return reGeType;
        }

        public ReGeOption setReGeType(int reGeType) {
            this.reGeType = reGeType;
            return this;
        }

        public boolean isSn() {
            return isSn;
        }

        public ReGeOption setSn(boolean sn) {
            isSn = sn;
            return this;
        }

        public boolean isIslog() {
            return LogUtil.DEBUG;
        }

        public ReGeOption setIslog(boolean islog) {
            LogUtil.DEBUG = islog;
            return this;
        }

        public String getKey() {
            return key;
        }

        public String getSk() {
            return sk;
        }

        public ReGeOption setKey(String key) {
            this.key = key;
            return this;
        }

        public ReGeOption setSk(String sk) {
            this.sk = sk;
            return this;
        }
    }
}
