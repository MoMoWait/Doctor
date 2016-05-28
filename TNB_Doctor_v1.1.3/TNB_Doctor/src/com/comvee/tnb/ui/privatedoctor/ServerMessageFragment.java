package com.comvee.tnb.ui.privatedoctor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.DoctorServerItemMsg;
import com.comvee.tnb.model.DoctotServerMessage;
import com.comvee.tnb.ui.more.WebFragment;
import com.comvee.tnb.ui.pay.PayMrg;
import com.comvee.tnb.ui.user.LoginFragment;
import com.comvee.tnb.ui.voucher.SelectUseVoucherFrag;
import com.comvee.tnb.ui.voucher.SelectUseVoucherFrag.OnSelectVoucherListener;
import com.comvee.tnb.ui.voucher.VoucherModel;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.tool.UserMrg;

/**
 * 医生服务详细页
 * 
 * @author Administrator
 * 
 */
public class ServerMessageFragment extends BaseFragment implements OnItemClickListener, OnHttpListener, OnClickListener, OnSelectVoucherListener {
	private String packageCode;//
	private int packageType;// 套餐类型
	private List<DoctotServerMessage> list;
	public static int SERVER_OF_PUB = 0;
	public static int SERVER_OF_PRI = 1;
	private String doctorName, doctorId;
	private int selectServer = 0;// 选中的套餐
	private ImageView photo;
	private TextView tv_Name;
	private String packageid;
	private ListView mlistView;
	private ServerMsgAdapter adapter;
	private TitleBarView mBarView;
	private TextView server_price, offset_price, amount_price;
	private VoucherModel voucherModel;
	private List<VoucherModel> voucherLists;
	private DoctotServerMessage message;
	private int selectVoucher;// 选中的优惠券
	private View group_offset;

	@Override
	public int getViewLayoutId() {
		return R.layout.fragment_buy_server_new;
	}

	public void setSelectVoucher(int selectVoucher) {
		this.selectVoucher = selectVoucher;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onLaunch(Bundle bundle) {
		super.onLaunch(bundle);
		if (bundle != null) {
			doctorId = bundle.getString("doctorId");
			packageCode = bundle.getString("packageCode");
			packageid = bundle.getString("packageid");
			voucherLists = (List<VoucherModel>) bundle.getSerializable("modelse");
			if (voucherLists != null && voucherLists.size() > 0) {
				voucherModel = voucherLists.get(0);
				selectVoucher = 0;
			}
			packageType = bundle.getInt("packageType");
		}
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		init();
		if (list.size() > 0) {
			initDocMsg();
			initAmountView();
		} else {
			requestDocServerMessage();
		}
	}

	public ServerMessageFragment() {
	}

	public static void toFragment(FragmentActivity activity, int packageType, String packageCode, String doctorId, String packageid,
			List<VoucherModel> models) {
		Bundle bundle = new Bundle();
		bundle.putString("doctorId", doctorId);
		bundle.putString("packageCode", packageCode);
		bundle.putString("packageid", packageid);
		bundle.putSerializable("modelse", (Serializable) models);
		bundle.putInt("packageType", packageType);
		FragmentMrg.toFragment(activity, ServerMessageFragment.class, bundle, true);
	}

	private void init() {
		if (list == null) {
			list = new ArrayList<DoctotServerMessage>();
		}
		adapter = new ServerMsgAdapter(getActivity());
		adapter.setList(list);
		mBarView.setTitle(getString(R.string.server_message_title));
		amount_price = (TextView) findViewById(R.id.amount_price);
		findViewById(R.id.buy_server).setOnClickListener(this);
		mlistView = (ListView) findViewById(R.id.buy_server_lin);
		mlistView.setOnItemClickListener(this);

		View head = View.inflate(getApplicationContext(), R.layout.buy_server_head, null);
		photo = (ImageView) head.findViewById(R.id.doc_server_img);
		tv_Name = (TextView) head.findViewById(R.id.package_name);
		View footer = View.inflate(getApplicationContext(), R.layout.buy_server_btn, null);
		server_price = (TextView) footer.findViewById(R.id.server_price);
		offset_price = (TextView) footer.findViewById(R.id.offset_price);
		group_offset = footer.findViewById(R.id.group_offset);
		mlistView.addHeaderView(head);
		mlistView.addFooterView(footer);
		if (packageCode.equals("KWYSTC_XTY")) {
			footer.findViewById(R.id.sugar_message).setVisibility(View.VISIBLE);
			footer.findViewById(R.id.sugar_message).setOnClickListener(this);
		}
		mlistView.setAdapter(adapter);
	}

	@Override
	public void onSussece(int what, byte[] b, boolean fromCache) {

		switch (what) {
		case 1:
			cancelProgressDialog();
			parseDocSerMesssage(b);
			// createServerPerionView((LinearLayout)
			// findViewById(R.id.buy_server_lin), list);
			adapter.setSelect(selectServer);
			adapter.notifyDataSetChanged();
			initDocMsg();
			initAmountView();
			break;
		default:
			break;
		}
	}

	private void initDocMsg() {
		if (list.size() > 0) {
			ImageLoaderUtil.getInstance(getApplicationContext()).displayImage(list.get(0).getPackageImg(), photo);
			if (packageType == SERVER_OF_PUB) {
				tv_Name.setText(list.get(0).getPackageName());
			} else {
				tv_Name.setText(doctorName + "-" + list.get(0).getPackageName());
			}
		}
	}

	@Override
	public void onFialed(int what, int errorCode) {
		cancelProgressDialog();
		ComveeHttpErrorControl.parseError(getActivity(), errorCode);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.sugar_message:
			String url = ConfigParams.getConfig(getApplicationContext(), ConfigParams.TEXT_MECHINE_INFO) + "?origin=android&sessionID="
					+ UserMrg.getSessionId(getApplicationContext()) + "&sessionMemberID=" + UserMrg.getMemberSessionId(getApplicationContext());
			WebFragment web = WebFragment.newInstance("购买设备", url);
			toFragment(web, true, true);
			break;
		case R.id.buy_server:
			if (ConfigParams.IS_TEST_DATA) {
				toFragment(LoginFragment.class, null, true);
			} else {
				if (message.getPackageUrl() != null && !"".equals(message.getPackageUrl())) {
					String urls = message.getPackageUrl() + "?r=0.7025720614474267&origin=android&sessionID="
							+ UserMrg.getSessionId(getApplicationContext()) + "&sessionMemberID="
							+ UserMrg.getMemberSessionId(getApplicationContext());
					WebFragment webs = WebFragment.newInstance("购买设备", urls);
					toFragment(webs, true, true);

				} else {
					String couponId = voucherModel == null ? "" : voucherModel.sid;
					int couponAmount = voucherModel == null ? 0 : voucherModel.price;
					int buyPric = message.getFeeNumSale() - couponAmount * 100 > 0 ? message.getFeeNumSale() - couponAmount * 100 : 0;
					PayMrg.getIntance(this).requeseBuyDoctorServer(buyPric + "", doctorName, message.getPackageName(), 1 + "",
							message.getUseNum() + "", message.getUseUnit() + "", message.getPackageOwnerId(), message.getPackageCode(),
							list.get(0).getPackageImg(), couponId, couponAmount + "");
					// requeseBuyDoctorServer();
				}
			}
			break;
		case R.id.group_offset:
			SelectUseVoucherFrag frag = SelectUseVoucherFrag.newInstance(voucherLists);
			frag.setSelectPostion(selectVoucher);
			frag.setOnSelectVoucherListener(this);
			toFragment(frag, true, true);
			break;
		default:
			break;
		}
	}

	/**
	 * 设置商品价格、总价、优惠券界面
	 */
	@SuppressWarnings("static-access")
	private void initAmountView() {
		group_offset.setOnClickListener(this);
		double price = message == null ? 0 : message.getFeeNumSale() / (double) 100;
		server_price.setText(String.format("￥%s", String.format("%.2f", price)));
		if (voucherModel != null) {
			offset_price.setText(String.format(getString(R.string.private_server_voucher), voucherModel.price));
			amount_price.setText(String.format("￥%s", String.format("%.2f", price - voucherModel.price > 0 ? price - voucherModel.price : 0.00)));
		} else {
			amount_price.setText(String.format("￥%s", String.format("%.2f", price)));
			offset_price.setText(getText(R.string.private_server_no_use_voucher));
		}
		if (voucherLists == null || voucherLists.size() == 0) {
			offset_price.setText(getText(R.string.no_voucher));
			group_offset.setOnClickListener(null);
		}
	}

	/**
	 * 获取医生服务的详细信息
	 */
	private void requestDocServerMessage() {
		showProgressDialog(getString(R.string.msg_loading));
		ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.GET_DOC_SERVER_MESSAGE));
		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.GET_DOC_SERVER_MESSAGE);
		if (packageType == SERVER_OF_PUB) {
			http.setPostValueForKey("packageId", packageid);
		} else {
			http.setPostValueForKey("doctorId", doctorId);
			http.setPostValueForKey("packageCode", packageCode);
		}
		http.setOnHttpListener(1, this);
		http.startAsynchronous();
	}

	/**
	 * 解析获取到的服务详细信息
	 * 
	 * @param b
	 */
	@SuppressWarnings("unchecked")
	private void parseDocSerMesssage(byte[] b) {
		try {
			ComveePacket packet = ComveePacket.fromJsonString(b);

			if (packet.getResultCode() == 0) {
				JSONObject body = packet.optJSONObject("body");
				doctorName = body.optString("doctorName");
				doctorId = body.optString("doctorId");
				JSONArray packages = body.optJSONArray("package");
				list.clear();
				for (int i = 0; i < packages.length(); i++) {
					JSONObject obj = packages.getJSONObject(i);
					DoctotServerMessage info = new DoctotServerMessage();
					info.setFeeNum(obj.optInt("feeNum"));
					info.setFeeNumSale(obj.optInt("feeNumSale"));
					info.setOwnerId(obj.optString("ownerId"));
					info.setPackageCode(obj.optString("packageCode"));
					info.setPackageId(obj.optString("packageId"));
					info.setPackageImg(obj.optString("packageImg"));
					info.setPackageName(obj.optString("packageName"));
					info.setPackageOwnerId(obj.optString("packageOwnerId"));
					info.setUseNum(obj.optInt("useNum"));
					info.setUseUnit(obj.optString("useUnit"));
					info.setPackageUrl(obj.optString("packageUrl"));
					List<DoctorServerItemMsg> itemList = new ArrayList<DoctorServerItemMsg>();
					JSONArray array = obj.optJSONArray("list");
					for (int j = 0; j < array.length(); j++) {
						JSONObject itemObj = array.getJSONObject(j);
						DoctorServerItemMsg item = new DoctorServerItemMsg();
						item.setDictName(itemObj.optString("dictName"));
						item.setMemo(itemObj.optString("memo"));
						itemList.add(item);
					}
					info.setItemList(itemList);
					list.add(info);
					message = list.get(selectServer);
				}
			} else {
				ComveeHttpErrorControl.parseError(getActivity(), packet);
			}
		} catch (Exception e) {
			e.printStackTrace();
			showToast(R.string.error);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterview, View view, int i, long l) {
		if (i == 0 || i > list.size()) {
			return;
		}
		selectServer = i - 1;
		adapter.setSelect(selectServer);
		message = list.get(selectServer);
		adapter.notifyDataSetChanged();
		initAmountView();
	}

	@Override
	public void onSelectVoucher(int selectPostion) {
		this.selectVoucher = selectPostion;
		if (selectPostion >= 0 && voucherLists != null && voucherLists.size() > 0) {
			this.voucherModel = voucherLists.get(selectPostion);
		} else {
			this.voucherModel = null;
		}
		initAmountView();
	}
}
