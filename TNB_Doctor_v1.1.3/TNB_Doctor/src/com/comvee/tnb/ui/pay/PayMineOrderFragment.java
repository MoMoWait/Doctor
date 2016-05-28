package com.comvee.tnb.ui.pay;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comvee.tnb.DrawerMrg;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.adapter.OrderListAdapter;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.CustomDialog;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.model.OrderInfo;
import com.comvee.tnb.model.OrderItemInfo;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.ui.more.WebFragment;
import com.comvee.tnb.ui.privatedoctor.MemberDoctorListFrag;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UserMrg;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 订单列表页
 *
 * @author Administrator
 */
public class PayMineOrderFragment extends BaseFragment implements OnItemClickListener, com.comvee.tnb.http.OnHttpListener, OnClickListener {

    private static final int REQUEST_ORDER_LIST = 1;
    private static final int REQUEST_ORDER_REMOVE = 2;
    private static final int SDK_PAY_FLAG = 1;
    private int mOrderType = 2;// 1、医生2、订单
    private ListView mListView;
    private OrderListAdapter mAdapter;
    private View layoutNoData;
    private ArrayList<OrderInfo> allInfoList;// 所有数据
    private TextView left, right;
    private View view;
    private String OrderId = "";// 订单号
    private boolean isSliding;
    private TitleBarView mBarView;
    private CustomDialog cus;

    public static void toFragment(FragmentActivity fragment, int type, boolean isSliding) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isSliding", isSliding);
        bundle.putInt("type", type);
        boolean isAnima = true;
        if (isSliding) {
            isAnima = false;
        }
        FragmentMrg.toFragment(fragment, PayMineOrderFragment.class, bundle, isAnima);
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.fragment_order_list;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onLaunch(Bundle bundle) {
        super.onLaunch(bundle);
        if (bundle != null) {
            isSliding = bundle.getBoolean("isSliding");
            mOrderType = bundle.getInt("type", 3);
        }
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        if (isSliding) {
            DrawerMrg.getInstance().close();
        }
        initTable();
        init();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (view != null) {
            mBarView.removeView(view);
        }
    }

    private void initTable() {
        if (mOrderType == 3) {
            mBarView.setTitle(getString(R.string.left_fragment_order_manager));
        } else {
            view = View.inflate(getApplicationContext(), R.layout.titlebar_follow_record, null);
            left = (TextView) view.findViewById(R.id.tab_left);
            right = (TextView) view.findViewById(R.id.tab_right);

            left.setText(R.string.left_fragment_my_doctor);
            right.setText(R.string.title_mine_order);
            left.setOnClickListener(this);
            right.setOnClickListener(this);
            view.setLayoutParams(new LayoutParams(200, 3));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            mBarView.addView(view, params);
            mBarView.setTitle("");
        }
    }

    private void init() {
        if (mOrderType == 3) {
            allInfoList = new ArrayList<OrderInfo>();
            findViewById(R.id.btn_machine_info).setOnClickListener(this);
            layoutNoData = findViewById(R.id.layout_no_data);
            mAdapter = new OrderListAdapter(getApplicationContext());
            mAdapter.setOnClickListener(this);
            mListView = (ListView) findViewById(R.id.list_order);
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(this);
            requestOrderList(-1);
            mAdapter.setListItems(allInfoList);
            mAdapter.setOrderType(2);
            mAdapter.notifyDataSetChanged();

        } else {
            allInfoList = new ArrayList<OrderInfo>();
            findViewById(R.id.btn_machine_info).setOnClickListener(this);
            layoutNoData = findViewById(R.id.layout_no_data);
            mAdapter = new OrderListAdapter(getApplicationContext());
            mAdapter.setOnClickListener(this);
            mListView = (ListView) findViewById(R.id.list_order);
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(this);
            choiceTabUI(mOrderType);
        }

    }

    private void choiceTabUI(int type) {
        left.setBackgroundResource(type == 1 ? R.drawable.jiankangzixun_03 : R.drawable.jiankangzixun_07);
        right.setBackgroundResource(type != 1 ? R.drawable.jiankangzixun_08 : R.drawable.jiankangzixun_04);
        int green = getResources().getColor(R.color.title);
        right.setTextColor(type != 1 ? Color.WHITE : green);
        left.setTextColor(type == 1 ? Color.WHITE : green);

        mAdapter.setListItems(allInfoList);
        mAdapter.setOrderType(type);
        mAdapter.notifyDataSetChanged();
        mOrderType = type;
        if (mOrderType == 1) {
            toFragment(MemberDoctorListFrag.class, null, true);
        }
        if (mOrderType == 2) {
            requestOrderList(-1);
        }
    }

    // 0未支付,1已支付,-1取全部
    private void requestOrderList(int mOrderType) {
        showProgressDialog(getString(R.string.msg_loading));
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.ORDER_LIST);
        http.setPostValueForKey("type", mOrderType + "");
        http.setOnHttpListener(REQUEST_ORDER_LIST, this);
        http.startAsynchronous();
    }

    private void requestOrderRemove(String id) {
        showProgressDialog(getString(R.string.msg_loading));
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.ORDER_REMOVE);
        http.setPostValueForKey("orderId", id);

        http.setOnHttpListener(REQUEST_ORDER_REMOVE, this);
        http.startAsynchronous();
    }

    @Override
    public void onFialed(int arg0, int errorCode) {
        cancelProgressDialog();
        ComveeHttpErrorControl.parseError(getActivity(), errorCode);
    }

    private void parseOrderList(byte[] b, boolean fromCache) throws Exception {
        ComveePacket packet = ComveePacket.fromJsonString(b);
        if (packet.getResultCode() == 0) {
            allInfoList.clear();
            JSONArray array = packet.getJSONArray("body");
            int len = array.length();
            for (int i = 0; i < len; i++) {
                JSONObject obj = array.getJSONObject(i);
                String orderType = obj.getString("orderType");
                ArrayList<OrderItemInfo> items = new ArrayList<OrderItemInfo>();
                OrderInfo info = new OrderInfo();
                // 实体产品
                if (orderType.equals("1")) {
                    JSONObject order = obj.getJSONObject("order");
                    JSONArray list = obj.getJSONArray("list");
                    info.type = 1;
                    info.address = order.optString("address");
                    info.insertDt = order.optString("insertDt");
                    info.mobile = order.optString("mobile");
                    info.orderId = order.optString("orderId");
                    info.orderType = order.optString("orderType");
                    info.userName = order.optString("userName");
                    info.orderNum = order.optString("orderNum");
                    info.packageRemark = order.optString("packageRemark");
                    info.payStatus = order.optInt("payStatus");
                    double tempMoneys = order.optDouble("payMoney") / 100;
                    info.payMoney = String.format("%1$.2f", tempMoneys);
                    info.list = items;
                    for (int j = 0; j < list.length(); j++) {

                        JSONObject itemObj = list.getJSONObject(j);
                        OrderItemInfo item = new OrderItemInfo();
                        tempMoneys = itemObj.optDouble("money") / 100;
                        item.money = String.format("%1$.2f", tempMoneys);
                        item.num = itemObj.optInt("num");
                        item.packageName = itemObj.optString("packageName");
                        if (TextUtils.isEmpty(itemObj.optString("packageUrl"))) {
                            item.packageUrl = itemObj.optString("packageImg");
                        } else {
                            item.packageUrl = itemObj.optString("packageUrl");
                        }
                        info.totalNum += item.num;
                        items.add(item);

                    }
                }
                // 虚拟产品
                if (orderType.equals("2")) {
                    JSONObject serverOrder = obj.getJSONObject("serverOrder");
                    JSONArray serverList = obj.getJSONArray("serverList");
                    info.type = 2;
                    info.insertDt = serverOrder.optString("insertDt");
                    info.mobile = serverOrder.optString("regAccount");
                    info.orderId = serverOrder.optString("orderId");

                    // serverInfo.orderType =
                    // serverOrder.optString("orderType");
                    info.userName = serverOrder.optString("userName");
                    info.orderNum = serverOrder.optString("orderId");
                    // serverInfo.packageRemark =
                    // serverOrder.optString("packageRemark");
                    info.payStatus = serverOrder.optInt("orderStatus");
                    double tempMoney = serverOrder.optDouble("orderMoney") / 100;
                    info.payMoney = String.format("%1$.2f", tempMoney);
                    info.list = items;
                    for (int j = 0; j < serverList.length(); j++) {

                        JSONObject itemObj = serverList.getJSONObject(j);
                        OrderItemInfo item = new OrderItemInfo();
                        item.packagecode = itemObj.optString("packageCode");
                        tempMoney = itemObj.optDouble("packagePrice") / 100;
                        item.money = String.format("%1$.2f", tempMoney);
                        item.num = itemObj.optInt("packageNum");
                        item.packageName = itemObj.optString("packageName");
                        item.packageUrl = itemObj.optString("packageImg");
                        info.totalNum += item.num;
                        items.add(item);
                    }
                }
                allInfoList.add(info);
            }

            if (mOrderType == 2 || mOrderType == 3) {
                if (allInfoList != null && allInfoList.size() != 0) {
                    layoutNoData.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                    // layoutTabBar.setVisibility(View.VISIBLE);
                } else {// 未支付、已支付列表都为空
                    layoutNoData.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                    // layoutTabBar.setVisibility(View.GONE);
                }
            }
            mAdapter.notifyDataSetChanged();

        } else {
            ComveeHttpErrorControl.parseError(getActivity(), packet);
        }
    }

    private void parseOrderRemove(byte[] b) throws Exception {
        ComveePacket packet = ComveePacket.fromJsonString(b);
        if (packet.getResultCode() == 0) {
            requestOrderList(-1);
        } else {
            ComveeHttpErrorControl.parseError(getActivity(), packet);
        }
    }

    @Override
    public void onSussece(int what, byte[] b, boolean fromCache) {
        cancelProgressDialog();

        try {
            switch (what) {
                case REQUEST_ORDER_LIST:
                    parseOrderList(b, fromCache);
                    break;
                case REQUEST_ORDER_REMOVE:
                    parseOrderRemove(b);
                    break;
                case 3:
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case TitleBarView.ID_LEFT_BUTTON:
                getActivity().onBackPressed();
                break;
            case R.id.btn_machine_info:
                // toFragment(WebFragment.newInstance("了解设备",
                // ParamsConfig.getConfig(getApplicationContext(),
                // ParamsConfig.TEXT_MECHINE_INFO)), true, true);

                String url = ConfigParams.getConfig(getApplicationContext(), ConfigParams.TEXT_MECHINE_INFO) + "?origin=android&sessionID="
                        + UserMrg.getSessionId(getApplicationContext()) + "&sessionMemberID=" + UserMrg.getMemberSessionId(getApplicationContext());
                WebFragment web = WebFragment.newInstance("购买设备", url);
                web.setSliding(false);
                toFragment(web, true, true);
                break;
            case R.id.tab_left:
                choiceTabUI(1);
                break;
            case R.id.tab_right:
                choiceTabUI(2);
                break;
            case R.id.btn_remove:
                showOrderRemoveDialog((OrderInfo) arg0.getTag());
                break;
            case R.id.btn_oder_pay:
                OrderInfo info = (OrderInfo) arg0.getTag();
                if (info.payStatus == 0) {
                    if (info.type == 1) {
                        String payUrl = ConfigParams.getConfig(getApplicationContext(), ConfigParams.TEXT_PAY_URL) + "?orderNum=" + info.orderNum
                                + "&nump=" + info.mobile;
                        toFragment(WebFragment.newInstance("支付", payUrl), true);
                    }
                    if (info.type == 2) {
                        PayMrg payMrg = PayMrg.getIntance(this);
                        payMrg.setOrderMoney(Double.parseDouble(info.payMoney) * 100);
                        payMrg.setOrderName(info.list.get(0).packageName);
                        payMrg.setOrderNum(info.orderId);
                        payMrg.setOrderTime(info.insertDt);
                        payMrg.requestSignMsg();
                    }
                } else {
                    toFragment(PayOrderDetailFragment.newInstance(info), true, true);
                }
                break;
            default:
                break;
        }
    }

    private void showOrderRemoveDialog(final OrderInfo info) {
        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setMessage("是否确定要删除当前订单？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                requestOrderRemove(info.orderId);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                cus.dismiss();
            }
        });
        cus = builder.create();
        cus.show();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
        // if (mAdapter.getItem(position).type == 1) {
        // toFragment(PayOrderDetailFragment.newInstance(mAdapter.getItem(position)),
        // true);
        // } else {
        // showToast("该物品是虚拟物品，暂无物流信息！");
        // }
    }

    @Override
    public boolean onBackPress() {
        if (isSliding) {
            IndexFrag.toFragment(getActivity(),true); 
            return true;
        }
        return false;
    }

}
