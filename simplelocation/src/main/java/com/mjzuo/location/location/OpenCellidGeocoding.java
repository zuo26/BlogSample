package com.mjzuo.location.location;

import android.content.Context;
import android.os.AsyncTask;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

import com.mjzuo.location.bean.Latlng;
import com.mjzuo.location.constant.UrlConstant;
import com.mjzuo.location.helper.Helper;
import com.mjzuo.location.net.NetUtil;
import com.mjzuo.location.util.CommonUtil;
import com.mjzuo.location.util.LogUtil;

import org.json.JSONObject;

/**
 *  这是基站定位的类
 *   https://unwiredlabs.com/api#documentation
 * @author mingjiezuo
 * @since 19/09/05
 */
public class OpenCellidGeocoding implements IGeocoding {

    /** 手机信息的manager*/
    private TelephonyManager telephonyManager;

    private ISiLoResponseListener mListener;

    private MyAsyncTask task;

    private Context mContext;

    public OpenCellidGeocoding(Context context) {
        mContext = context;
        telephonyManager = (TelephonyManager) mContext.getApplicationContext()
                .getSystemService(mContext.getApplicationContext().TELEPHONY_SERVICE);
    }

    @Override
    public void start(ISiLoResponseListener listener) {
        if(listener != null)
            mListener = listener;
        if(mListener == null)
            LogUtil.e("open cellid grocoding listener nuol");

        String operator = telephonyManager.getNetworkOperator();
        if(operator == null || operator.isEmpty()){
            mListener.onFail("phone manager getNetworkOperator null");
            return;
        }
        int mcc = Integer.parseInt(operator.substring(0, 3));
        int mnc = Integer.parseInt(operator.substring(3));
        CellLocation celo = null;
        if(Helper.checkPermission(mContext.getApplicationContext())
                && Helper.checkPhonePermission(mContext.getApplicationContext())){
            celo = telephonyManager.getCellLocation();
        }else{
            mListener.onFail("phone manager no permission");
            return;
        }
        if(celo == null){
            mListener.onFail("phone manager getCellLocation null");
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
        sbUrl.append(UrlConstant.OPEN_CELLID_URL)
                .append("/v2/")
                .append("process.php");
        StringBuffer jsonPost = new StringBuffer();
        jsonPost.append("{"+"\"token\": \"09a4f0709c487e\"");
        jsonPost.append(","+"\"radio\": \"gsm\"");
        jsonPost.append(","+"\"mcc\":"+mcc);
        jsonPost.append(","+"\"mnc\":"+mnc);
        jsonPost.append(","+"\"cells\": [{"+"\"lac\":"+lac+","+"\"cid\":"+cid+"}]");
        jsonPost.append(","+"\"address\":"+2);
        jsonPost.append("}");

        task = new MyAsyncTask(sbUrl.toString(), jsonPost.toString());
        task.execute();
    }

    @Override
    public void reStart() {
        start(null);
    }

    @Override
    public void stop() {
        if(task != null)
            task = null;
        mListener = null;
        telephonyManager = null;
    }

    class MyAsyncTask extends AsyncTask<Void, Void, String> {

        String mUrl;
        byte[] mParams;
        public MyAsyncTask(String url, String param) {
            mUrl = url;
            mParams = CommonUtil.stringToBytes(param);
        }

        @Override
        protected String doInBackground(Void... voids) {
            return NetUtil.doHttpPost(mUrl, mParams);
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
        if(jsonObject.has("status")){
            String errorCode;
            try {
                errorCode = jsonObject.getString("status");
            }catch (Exception e){
                LogUtil.e("error:" + e.getMessage());
                return;
            }
            if(!"ok".equals(errorCode)){
                mListener.onFail("phone manager bs on fail error");
                return;
            }
            Latlng latlng = new Latlng();
            try{
                latlng.setLatitude(jsonObject.getDouble("lat"));
                latlng.setLongitude(jsonObject.getDouble("lon"));
                latlng.setProvider("openCellid基站定位");
                mListener.onSuccess(latlng);
            }catch (Exception e){
                LogUtil.e("error:" + e.getMessage());
                return;
            }
        }
    }
}
