package com.mjzuo.location.location;

import android.content.Context;
import android.os.AsyncTask;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

import androidx.annotation.Nullable;

import com.mjzuo.location.bean.Latlng;
import com.mjzuo.location.constant.UrlConstant;
import com.mjzuo.location.helper.Helper;
import com.mjzuo.location.net.NetUtil;
import com.mjzuo.location.util.LogUtil;

import org.json.JSONObject;

/**
 *  这是基站定位的类
 *
 *  http://www.cellocation.com/interfac/#cell
 * @author mingjiezuo
 * @since 19/09/04
 */
public class BsGeocoding implements IGeocoding {

    /** 手机信息的manager*/
    private TelephonyManager telephonyManager;

    /** 定位成功失败的监听*/
    private ISiLoResponseListener mListener;

    private MyAsyncTask task;

    private Context mContext;

    public BsGeocoding(Context context) {
        mContext = context;
        telephonyManager = (TelephonyManager) mContext.getApplicationContext()
                .getSystemService(mContext.getApplicationContext().TELEPHONY_SERVICE);
    }

    @Override
    public void start(@Nullable ISiLoResponseListener listener) {
        mListener = listener;
        String operator = telephonyManager.getNetworkOperator();
        int mcc = Integer.parseInt(operator.substring(0, 3));
        int mnc = Integer.parseInt(operator.substring(3));
        CellLocation celo = null;
        if(Helper.checkPermission(mContext.getApplicationContext())
                && Helper.checkPhonePermission(mContext.getApplicationContext())){
            celo = telephonyManager.getCellLocation();
        }
        if(celo == null){
            mListener.onFail("phone manager no permission");
            return;
        }
        int cid = 0;
        int lac = 0;
        if(celo instanceof GsmCellLocation){
            cid = ((GsmCellLocation) celo).getCid();
            lac = ((GsmCellLocation) celo).getLac();
        } else if(celo instanceof CdmaCellLocation) {//03 05 11 为电信CDMA
            cid = ((CdmaCellLocation) celo).getBaseStationId();
            lac = ((CdmaCellLocation) celo).getNetworkId();
            mnc = ((CdmaCellLocation) celo).getSystemId();
        }
        StringBuffer sbUrl = new StringBuffer();
        sbUrl.append(UrlConstant.BS_URL)
                .append(":81")
                .append("/cell/?")
                .append("mcc=")
                .append(mcc)
                .append("&mnc=")
                .append(mnc)
                .append("&lac=")
                .append(lac)
                .append("&ci=")
                .append(cid)
                .append("&output=json");
        task = new MyAsyncTask(sbUrl.toString());
        task.execute();
    }

    @Override
    public void reStart() {
        if(mListener != null)
            start(mListener);
    }

    @Override
    public void stop() {
        if(task != null)
            task = null;
        if(telephonyManager != null)
            telephonyManager = null;
        if(mListener != null){
            mListener = null;
        }
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
        if(mListener == null)
            return;
        if(json == null || json.isEmpty()){
            mListener.onFail("json null");
            return;
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
        }catch (Exception e){
            LogUtil.e("error:" + e.getMessage());
            return;
        }
        if(jsonObject.has("errcode")){
            int errorCode;
            try {
                errorCode = jsonObject.getInt("errcode");
            }catch (Exception e){
                LogUtil.e("error:" + e.getMessage());
                return;
            }
            if(errorCode != 0){
                mListener.onFail("phone manager bs on fail");
                return;
            }
            Latlng latlng = new Latlng();
            try{
                latlng.setLatitude(Double.parseDouble(jsonObject.getString("lat")));
                latlng.setLongitude(Double.parseDouble(jsonObject.getString("lon")));
                latlng.setAddress(jsonObject.getString("address"));
                latlng.setProvider("cellocation基站定位");
                mListener.onSuccess(latlng);
            }catch (Exception e){
                LogUtil.e("error:" + e.getMessage());
                return;
            }
        }
    }
}
