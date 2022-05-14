package com.mjzuo.location.constant;

public class Constant {

    /** 反地理google服务*/
    public static final int GOOGLE_API        = 0; // 国内设备不提供谷歌服务

    /** 反地理腾讯服务*/
    public static final int TENCENT_API       = 1;

    /** 反地理高德服务*/
    public static final int GAODE_API         = 2;

    /** 反地理百度服务*/
    public static final int BAIDU_API         = 3;

    /** 谷歌LocationManager定位*/
    public static final int LM_API            = 4; // 部分设备不提供谷歌服务

    /** unwiredlabs.com 基站定位*/
    public static final int BS_UNWIRED_API    = 5; // 不稳定

    /** cellocation 基站地位*/
    public static final int BS_OPENCELLID_API = 6;
}
