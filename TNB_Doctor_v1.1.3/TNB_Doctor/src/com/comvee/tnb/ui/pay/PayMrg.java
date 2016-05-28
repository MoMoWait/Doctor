package com.comvee.tnb.ui.pay;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.comvee.frame.BaseFragmentActivity;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.model.ServerDetailsInfo;
import com.comvee.tnb.ui.more.Result;
import com.comvee.tool.UserMrg;

import org.json.JSONObject;

public class PayMrg {

    private final int SDK_PAY_FLAG = 1;
    private Activity activity;
    private BaseFragment fragment;
    private String orderNum;
    private String orderTime;
    private String orderName;
    private double orderMoney;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    Result resultObj = new Result((String) msg.obj);
                    String resultStatus = resultObj.resultStatus;

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        sendEmptyMessageDelayed(3, 500);
                        // Toast.makeText(activity, "支付成功", Toast.LENGTH_SHORT);

                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”
                        // 代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            sendEmptyMessageDelayed(4, 500);
                        } else {
                            // Toast.makeText(activity, "支付失败", Toast.LENGTH_SHORT);
                            // FragmentMrg.toFragment(activity,new
                            // PayResultFragment(orderNum, orderName, orderMoney,
                            // orderTime, 0), false, true);
                            sendEmptyMessageDelayed(2, 500);
                        }
                    }

                }
                break;
                case 2:
                    FragmentMrg.toFragment((BaseFragmentActivity) activity, new PayResultFragment(orderNum, orderName, orderMoney, orderTime, 0), true, true);
                    break;
                case 3:
                    ((BaseFragmentActivity) activity).toFragment(new PayResultFragment(orderNum, orderName, orderMoney, orderTime, 1), true, true);
                    ComveeHttp.clearCache(activity, UserMrg.getCacheKey(ConfigUrlMrg.MEMBER_SERVER));
                    break;
                case 4:
                    Toast.makeText(activity, "支付结果确认中", Toast.LENGTH_SHORT);
                    break;
                default:
                    break;
            }
        }

        ;
    };

    private PayMrg(BaseFragment fragment) {
        this.activity = fragment.getActivity();
        this.fragment = fragment;
    }

    public static PayMrg getIntance(BaseFragment fragment) {

        return new PayMrg(fragment);
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public double getOrderMoney() {
        return orderMoney;
    }

    public void setOrderMoney(double orderMoney) {
        this.orderMoney = orderMoney;
    }

    /**
     * 调用插件进行支付
     *
     * @param orderInfo
     */
    private void Pay(final String orderInfo) {
        try {
            new Thread() {
                public void run() {
                    PayTask alipay = new PayTask(activity);
                    // 调用支付接口
                    String result = alipay.pay(orderInfo);
                    Message msg = new Message();
                    msg.what = SDK_PAY_FLAG;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }
            }.start();

        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    /**
     * 解析获取到的签名字符串
     *
     * @param b
     * @param fromCache
     */
    public void parseSignMsgInfo(String b, boolean fromCache) {
        if (TextUtils.isEmpty(b)) {
            return;
        }
        try {
            ComveePacket packet = ComveePacket.fromJsonString(b);
            if (packet.getResultCode() == 1) {
                JSONObject jbody = packet.getJSONObject("body");
                String orderInfo = jbody.optString("result");
                // OrderId = jbody.optString("temp");
                Pay(orderInfo);
            } else {
                ComveeHttpErrorControl.parseError(activity, packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下单
     *
     * @param Packageprice   套餐价格
     * @param docName        医生名称
     * @param packageName    套餐名称
     * @param packageNum     套餐数量
     * @param useNum         套餐次数
     * @param useUnit        价格单位
     * @param packageOwnerId
     * @param packageCode
     * @param couponId       代金券id
     * @param couponAmount   代金券金额
     */
    public void requeseBuyDoctorServer(final String Packageprice, final String docName, final String packageName, final String packageNum,
                                       final String useNum, final String useUnit, final String packageOwnerId, final String packageCode, final String packageImg,
                                       final String couponId, final String couponAmount) {
        fragment.showProgressDialog(activity.getResources().getString(R.string.msg_loading));
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... arg0) {
                ComveeHttp http = new ComveeHttp(activity, ConfigUrlMrg.BUY_DOCTOR_SERVER);
                http.setPostValueForKey("payStatus", "1");
                http.setPostValueForKey("orderMoney", Packageprice);
                http.setPostValueForKey("packageName", docName + " - " + packageName);
                http.setPostValueForKey("packagePrice", Packageprice);
                http.setPostValueForKey("packageNum", packageNum);
                http.setPostValueForKey("useNum", useNum);
                http.setPostValueForKey("useUnit", useUnit);
                http.setPostValueForKey("packageOwnerId", packageOwnerId);
                // http.setPostValueForKey("",
                // info.getDoctorName()+"-"+getMenuName(packageCode));
                http.setPostValueForKey("packageCode", packageCode);
                http.setPostValueForKey("packageImg", packageImg);
                http.setPostValueForKey("couponId", couponId);
                http.setPostValueForKey("couponAmount", couponAmount);

                String result = http.startSyncRequestString();
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                // TODO Auto-generated method stub
                super.onPostExecute(result);
                parseServerDetails(result, packageName);
            }
        }.execute();

    }

    /**
     * 解析订单数据
     *
     * @param b
     */
    private void parseServerDetails(String b, String packageName) {
        try {
            ComveePacket packet = ComveePacket.fromJsonString(b);

            if (packet.getResultCode() == 0) {
                ServerDetailsInfo detailsInfo = null;
                JSONObject body = packet.optJSONObject("body");

                detailsInfo = new ServerDetailsInfo();
                detailsInfo.setOrderId(body.optString("orderId"));
                detailsInfo.setOrderMoney(body.optString("orderMoney"));
                detailsInfo.setOrderStatus(body.optString("orderStatus"));
                detailsInfo.setOrigin(body.optString("origin"));
                detailsInfo.setPayAccount(body.optString("payAccount"));
                detailsInfo.setPayStatus(body.optString("payStatus"));
                detailsInfo.setRegAccount(body.optString("regAccount"));
                detailsInfo.setUserName(body.optString("userName"));
                orderMoney = Double.parseDouble(detailsInfo.getOrderMoney());
                orderName = packageName;
                orderNum = detailsInfo.getOrderId();
                orderTime = body.optString("insertDt");

                if (orderMoney == 0) {
                    fragment.cancelProgressDialog();
                    FragmentMrg.toFragment((FragmentActivity) activity, new PayResultFragment(orderNum, orderName, orderMoney, orderTime, 1), true, true);
                    return;
                }

                requestSignMsg();
            } else {
                fragment.cancelProgressDialog();
                ComveeHttpErrorControl.parseError(activity, packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
            fragment.showToast(R.string.error);
        }
    }

    /**
     * 通过服务器生成支付宝签名字符串
     */

    public void requestSignMsg() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... arg0) {
                // TODO Auto-generated method stub
                ComveeHttp http = new ComveeHttp(activity, ConfigUrlMrg.PAY_SIGN_TRADE);
                http.setPostValueForKey("subject", orderName);
                http.setPostValueForKey("totalFee", orderMoney + "");
                http.setPostValueForKey("body", orderName);
                http.setPostValueForKey("orderId", orderNum);

                String result = http.startSyncRequestString();
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                fragment.cancelProgressDialog();
                parseSignMsgInfo(result, false);
                super.onPostExecute(result);
            }
        }.execute();

    }
}
