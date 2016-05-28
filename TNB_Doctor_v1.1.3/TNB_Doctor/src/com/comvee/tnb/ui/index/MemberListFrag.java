package com.comvee.tnb.ui.index;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.http.KWHttpRequest.KwHttpRequestListener;
import com.comvee.tnb.DrawerMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.activity.MainActivity;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.db.DBManager;
import com.comvee.tnb.dialog.CustomDialog;
import com.comvee.tnb.dialog.CustomProgressDialog;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.model.MemberInfo;
import com.comvee.tnb.ui.member.MemberChooseRelativeFragment;
import com.comvee.tnb.ui.user.QQLoginManager;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.tool.UmenPushUtil;
import com.comvee.tool.UserMrg;
import com.comvee.ui.remind.TimeRemindUtil;
import com.comvee.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 成员列表
 *
 * @author friendlove
 */
public class MemberListFrag extends BaseFragment implements OnClickListener, KwHttpRequestListener, OnItemClickListener {

	CustomProgressDialog mProDialog;
	private ListView mListView;
	private MemberAdapter mAdapter;
	private boolean isEdit;
	private String mDefaultMemberID;
	private int dirType;// 1、切换成员2、进入个人中心
	private int curPosition;
	// private TextView btnEdit;
	private boolean isSliding;
	private TitleBarView mBarView;
	private Handler mHandler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			try {
				switch (msg.what) {
				case 1:
					int len = msg.arg1;
					if (len < 2 || (len == 2 && curPosition == 1)) {
						// btnEdit.setVisibility(View.GONE);
						setEditStatus(false);
					} else {
						// btnEdit.setVisibility(View.VISIBLE);
					}

					mAdapter.setList((ArrayList<MemberInfo>) msg.obj);
					mAdapter.notifyDataSetChanged();
					break;
				case 2:
					showToast("切换成功");
					// ((MainActivity) getActivity()).updateLiftView();
					// ((MainActivity) getActivity()).updateMsg();
					// ((MainActivity) getActivity()).updateMsgView(0);
					if (dirType == 1) {
						IndexFrag.toFragment(getActivity(), false); //// ((MainActivity)getActivity()).refreshLeftFrag();//刷新侧边栏
					}
					// hideMemberProgress();
					cancelProDialog();
					break;

				case 3:
					CustomDialog.Builder dialog = new CustomDialog.Builder(getActivity());
					dialog.setMessage(msg.obj.toString());
					dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							QQLoginManager.getIntence((FragmentActivity) getActivity()).tryLoginQQ();
						}
					});
					dialog.setNegativeButton("取消", null);
					dialog.create().show();
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		;
	};

	public static void toFragment(FragmentActivity fragment, boolean isSliding) {
		Bundle bundle = new Bundle();
		bundle.putBoolean("isSliding", isSliding);
		boolean isAnima = true;
		if (isSliding) {
			isAnima = false;
		}
		FragmentMrg.toFragment(fragment, MemberListFrag.class, bundle, isAnima);
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.fragment_right;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	public void setEditStatus(boolean b) {
		this.isEdit = b;
		mAdapter.notifyDataSetChanged();

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	private void init() {

	}

	@Override
	public void onLaunch(Bundle bundle) {
		super.onLaunch(bundle);
		if (bundle != null) {
			isSliding = bundle.getBoolean("isSliding");
		}
		if (isSliding) {
		}
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		ArrayList<MemberInfo> mInfos = new ArrayList<MemberInfo>();
		mAdapter = new MemberAdapter(mInfos);
		mListView = (ListView) findViewById(R.id.list_view);
		final View layoutAddMember = View.inflate(getApplicationContext(), R.layout.layout_add_member, null);
		layoutAddMember.setId(R.id.btn_add_member);
		layoutAddMember.setOnClickListener(this);
		mListView.addFooterView(layoutAddMember);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		init();
		mBarView.setTitle("家人");
		mBarView.setRightButton("编辑", this);
		requestMembersList();
	}

	private void toEdit() {
		isEdit = !isEdit;
		mAdapter.notifyDataSetChanged();
		if (isEdit) {
			// btnEdit.setText("完成");
			mBarView.setRightButton("完成", this);
		} else {
			// btnEdit.setText("删除");
			mBarView.setRightButton("编辑", this);
		}
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
		case R.id.btn_add_member:
			if (!isEdit) {
				// if (mAdapter.getCount() >= 5) {
				// showToast("目前只能添加五个成员");
				// return;
				// }
				// toCreateMember();
				checkCreateMemberMax();
				mBarView.hideRightButton();
			} else {
				showToast("您正处于编辑状态");
			}
			break;
		// case R.id.btn_edt:
		// toEdit();
		// break;
		case TitleBarView.ID_LEFT_BUTTON:
			IndexFrag.toFragment(getActivity(), true);
			break;
		case TitleBarView.ID_RIGHT_BUTTON:
			toEdit();
			break;
		default:
			break;
		}

	}

	public void requestMembersList(boolean hasPro, KwHttpRequestListener listener) {

		if (TextUtils.isEmpty(UserMrg.getMemberSessionId(mContext))) {
			return;
		}

		if (hasPro) {
			showProgressDialog(getString(R.string.msg_loading));
		}

		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.GET_MEMBER_LIST);
		http.setCallBackAsyn(true);
		http.setListener(1, listener);
		http.startAsynchronous();
	}

	public void requestMembersList() {
		requestMembersList(true, this);
	}

	/**
	 * 检测新建成员 是否达到上限
	 */
	private void checkCreateMemberMax() {

		showProDialog(getString(R.string.msg_loading));
		String url = ConfigUrlMrg.MEMBER_CHECK_ADD_MEMBER_MAX;
		ComveeHttp http = new ComveeHttp(getApplicationContext(), url);
		http.setListener(4, this);
		http.startAsynchronous();
	}

	// private void toMemberCenter(int position) {
	// // showToast(String.format("进入第%d个的成员中心", position + 1));
	// if
	// (!mDefaultMemberID.equalsIgnoreCase(mAdapter.getListItem(position).mId))
	// {
	// curPosition = position;
	// dirType = 2;
	// changeDefaultMember(mAdapter.getListItem(position).mId);
	// } else {
	// // toMemberInfo(mAdapter.getListItem(position).mId);
	// }
	//
	// }

	private void toCreateMember() {
		if (ConfigParams.IS_TEST_DATA) {
			showToast("游客状态无法添加成员，请注册！");
		} else {

			MemberChooseRelativeFragment frag = MemberChooseRelativeFragment.newInstance(new MemberInfo());
			if (mAdapter.getListItems() != null) {
				for (MemberInfo info : mAdapter.getListItems()) {
					if ("CBYBRGX001".equalsIgnoreCase(info.relative)) {
						frag.setHasSelf(true);
						break;
					}
				}
			}
			toFragment(frag, true, false);
		}

		// toFragment(MemberCreateFragment.newInstance(), true, false);
	}

	private void toDel(final int position) {

		CustomDialog.Builder buidler = new CustomDialog.Builder(getActivity());
		buidler.setTitle("提示").setMessage("确定要删除该成员么？一旦删除成功，则与该成员相关的全部数据将被清除。").setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				showProDialog(getString(R.string.msg_loading));
				final String url = ConfigUrlMrg.DEL_MEMBER;
				ComveeHttp http = new ComveeHttp(getApplicationContext(), url);
				http.setPostValueForKey("memberId", mAdapter.getListItem(position).mId);
				http.setListener(2, MemberListFrag.this);
				http.startAsynchronous();
			}
		}).setNegativeButton("取消", null).create().show();

	}

	protected void onDeleteSussec() {
		showToast("移除成功");
		requestMembersList();
		// mAdapter.notifyDataSetChanged();
	}

	@Override
	public void loadFailed(int arg0, int arg1) {
		cancelProDialog();
		super.cancelProgressDialog();
		// showToast(R.string.time_out);
		ComveeHttpErrorControl.parseError(getActivity(), arg1);
	}

	@Override
	public void loadFinished(int arg0, byte[] arg1) {

		switch (arg0) {
		case 1:
			cancelProgressDialog();
			parseMemberList(arg1, true);
			break;
		case 2:
			cancelProDialog();
			parseDel(arg1);
			break;
		case 3:
			parseChangeMember(arg1);
			break;
		case 4:
			cancelProDialog();
			paresCheckMemberMax(arg1);
			break;
		case 5:
			cancelProDialog();

			try {
				ComveePacket packet1 = ComveePacket.fromJsonString(arg1);

				if (packet1.getResultCode() == 0) {
					showToast(packet1.getResultMsg());
					// UITool.setTextView(getView(), R.id.tv_qq, "已绑定");
				} else {
					ComveeHttpErrorControl.parseError(getActivity(), packet1);
				}
			} catch (Exception e) {
				e.printStackTrace();
				showToast(R.string.error);
			}
			break;
		default:
			break;
		}
	}

	private void paresCheckMemberMax(byte[] arg1) {
		try {
			ComveePacket packet = ComveePacket.fromJsonString(arg1);
			if (packet.getResultCode() == 0) {
				toCreateMember();
			} else {
				ComveeHttpErrorControl.parseError(getActivity(), packet);
				// showToast(packet.getResultMsg());
			}
		} catch (Exception e) {
			e.printStackTrace();
			showToast(R.string.error);
		}
	}

	private void parseDel(byte[] arg1) {
		try {
			ComveePacket packet = ComveePacket.fromJsonString(arg1);
			if (packet.getResultCode() == 0) {
				onDeleteSussec();
			} else {
				ComveeHttpErrorControl.parseError(getActivity(), packet);
				// showToast(packet.getResultMsg());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseMemberList(JSONArray array) throws Exception {

		ArrayList<MemberInfo> list = new ArrayList<MemberInfo>();
		int len = array.length();
		for (int i = 0; i < len; i++) {
			JSONObject obj = array.getJSONObject(i);
			MemberInfo info = new MemberInfo();
			info.name = obj.optString("memberName");
			info.mId = obj.optString("memberId");
			info.coordinator = obj.optInt("coordinator");
			info.photo = obj.optString("picUrl") + obj.optString("picPath");
			info.callreason = obj.optInt("callreason");
			info.birthday = obj.optString("birthday");
			info.relative = obj.optString("relation");
			info.hasMachine = obj.optInt("hasMachine");
			list.add(info);
			if (mDefaultMemberID.equals(info.mId)) {
				curPosition = i;
			}

		}

		Message msg = mHandler.obtainMessage(1, len, 0, list);
		mHandler.sendMessage(msg);

	}

	private void parseMemberList(byte[] arg1, boolean isShowQQDialog) {
		try {
			ComveePacket packet = ComveePacket.fromJsonString(arg1);

			if (packet.getResultCode() == 0) {
				JSONObject body = packet.getJSONObject("body");
				JSONObject obj = body.getJSONObject("member");
				MemberInfo info = new MemberInfo();
				info.name = obj.optString("memberName");

				if (StringUtil.checkChinese(info.name)) {
					info.name = info.name.length() > 8 ? info.name.substring(0, 6) + "..." : info.name;
				} else {
					info.name = info.name.length() > 8 ? info.name.substring(0, 6) + "..." : info.name;
				}
				info.mId = obj.optString("memberId");
				info.coordinator = obj.optInt("coordinator");
				info.photo = obj.optString("picUrl") + obj.optString("picPath");
				info.mId = obj.optString("memberId");
				info.callreason = obj.optInt("callreason");
				info.birthday = obj.optString("birthday");
				info.memberHeight = obj.optString("memberHeight");
				info.diabetes_plan = body.optString("diabetes_plan");
				info.score_describe = body.optString("score_describe");
				info.hasMachine = obj.optInt("hasMachine");
				info.memberWeight = obj.optString("memberWeight");
				info.relative = obj.optString("relation");
				UserMrg.setDefaultMember(info);
				mDefaultMemberID = info.mId;
				if (isShowQQDialog) {
					try {
						parseMemberList(body.getJSONArray("memberList"));
					} catch (Exception e) {
						// TODO: handle exception
					}

				}
				if (body.optInt("qqDatedFlag") == 1) {
					if (isShowQQDialog) {
						mHandler.sendMessage(mHandler.obtainMessage(3, body.optString("datedMsg")));
					}
				}

			} else {
				ComveeHttpErrorControl.parseError(getActivity(), packet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// toMemberInfo(mAdapter.getListItem(arg2).mId);

		if (isEdit) {

		} else {
			curPosition = arg2;
			if (!mDefaultMemberID.equalsIgnoreCase(mAdapter.getListItem(arg2).mId)) {
				dirType = 1;
				changeDefaultMember(mAdapter.getListItem(arg2).mId);
				DrawerMrg.getInstance().updateLefFtagment();
			} else {
				// ((MainActivity) getActivity()).closeRight();
				// toFragment(IndexFrag.newInstance(0), false)
			}
		}

	}

	private void parseChangeMember(byte[] arg1) {
		try {
			ComveePacket packet = ComveePacket.fromJsonString(arg1);

			if (packet.getResultCode() == 0) {
				// 清除前一个成员的闹钟
				TimeRemindUtil.getInstance(getApplicationContext()).cancleDisposableAlarm(IndexFrag.PENDING_CODE);
				// 清理数据库，，，，临时这样做
				DBManager.cleanDatabases(getApplicationContext());
				ComveeHttp.clearAllCache(getApplicationContext());
				UserMrg.setMemberSessionId(getApplicationContext(), packet.optString("sessionMemberID"));
				// ((MainActivity) getActivity()).requestMsgUnReadCount();
				try {
					FragmentMrg.getSingleFragment(IndexFrag.class).requestMemUnReadMsgLoader();
				}catch (Exception e){
					e.printStackTrace();
				}
				parseMemberList(arg1, false);

				IndexModel.notifyMemberClock(getApplicationContext());
				mHandler.sendEmptyMessage(2);

				DrawerMrg.getInstance().updateLefFtagment();
				// requestMembersList(false, new KwHttpRequestListener() {
				// @Override
				// public void loadFinished(int arg0, byte[] arg1) {
				// parseMemberList(arg1, false);
				// cancelProDialog();
				// mHandler.sendEmptyMessage(2);
				//
				// }
				//
				// @Override
				// public void loadFailed(int arg0, int arg1) {
				// cancelProDialog();
				// }
				// });

			} else {
				ComveeHttpErrorControl.parseError(getActivity(), packet);
				// showToast(packet.getResultMsg());
			}
		} catch (Exception e) {
			e.printStackTrace();
			showToast(R.string.error);
		}
	}

	private void changeDefaultMember(String mId) {
		showProDialog(getString(R.string.msg_loading));
		String url = ConfigUrlMrg.MEMBER_CHANGE;
		ComveeHttp http = new ComveeHttp(getApplicationContext(), url);
		http.setPostValueForKey("memberId", mId);
		http.setListener(3, this);
		http.startAsynchronous();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// if (curPosition != 0) {// 不是户主，就清楚缓存（因为每次启动都是户主状态）
		// ComveeHttp.clearCache(getApplicationContext(),
		// UrlMrg.TENDENCY_POINT_LIST);
		// ComveeHttp.clearCache(getApplicationContext(), UrlMrg.REMIND_LIST);
		// }
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public boolean onBackPress() {
		return false;
	}

	public void showProDialog(String str) {
		if (mProDialog == null) {
			mProDialog = CustomProgressDialog.createDialog(getActivity());
			mProDialog.setCanceledOnTouchOutside(false);
		}
		mProDialog.setMessage(str);
		if (!mProDialog.isShowing()) {
			mProDialog.show();
		}
	}

	public void cancelProDialog() {
		try {
			if (mProDialog != null) {
				mProDialog.dismiss();
				mProDialog = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class MemberAdapter extends BaseAdapter implements View.OnClickListener {

		private ArrayList<MemberInfo> mInfos;

		private MemberAdapter(ArrayList<MemberInfo> infos) {
			this.mInfos = infos;
		}

		public ArrayList<MemberInfo> getListItems() {
			return mInfos;
		}

		public MemberInfo getListItem(int position) {
			return mInfos.get(position);
		}

		private void setList(ArrayList<MemberInfo> infos) {
			this.mInfos = infos;
		}

		@Override
		public int getCount() {
			return mInfos.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mInfos.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {

			if (arg1 == null) {
				arg1 = View.inflate(getApplicationContext(), R.layout.item_member, null);
			}
			if (arg0 == mInfos.size() - 1) {
				arg1.findViewById(R.id.line_short).setVisibility(View.GONE);
				arg1.findViewById(R.id.line_long).setVisibility(View.VISIBLE);
			}
			final TextView tvName = (TextView) arg1.findViewById(R.id.tv_name);
			final ImageView ivPhoto = (ImageView) arg1.findViewById(R.id.img_photo);
			final ImageView btnDel = (ImageView) arg1.findViewById(R.id.btn_del);
			// final View btnToCenter = arg1.findViewById(R.id.btn_tocenter);
			final ImageView btnChoose = (ImageView) arg1.findViewById(R.id.btn_choose);

			MemberInfo info = mInfos.get(arg0);

			tvName.setText(info.name);

			if (isEdit && info.coordinator == 0) {
				btnDel.setVisibility(View.VISIBLE);
				btnDel.setEnabled(true);
				btnDel.setImageResource(R.drawable.btn_del);
				if (info.mId.equals(mDefaultMemberID)) {
					btnDel.setEnabled(false);
					btnChoose.setVisibility(View.VISIBLE);
					btnDel.setVisibility(View.INVISIBLE);
				}

			} else {
				btnDel.setEnabled(false);
				btnDel.setVisibility(View.GONE);
				btnChoose.setVisibility(View.INVISIBLE);
			}
			if (isEdit && info.coordinator == 1) {
				btnDel.setEnabled(false);
				btnDel.setImageResource(R.drawable.btn_del);
				btnDel.setVisibility(View.INVISIBLE);
			}
			try {
				if (info.mId.equals(mDefaultMemberID)) {
					btnDel.setEnabled(false);
					// btnDel.setVisibility(View.VISIBLE);
					// btnDel.setImageResource(R.drawable.member_defualt);
					btnChoose.setVisibility(View.VISIBLE);

				} else {
					btnChoose.setVisibility(View.GONE);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// if (isEdit) {
			// btnToCenter.setEnabled(false);
			// } else {
			// btnToCenter.setEnabled(true);
			// }

			btnDel.setTag(arg0);
			btnDel.setOnClickListener(this);

			// btnToCenter.setTag(arg0);
			// btnToCenter.setOnClickListener(this);
			ivPhoto.setImageResource(R.drawable.icon_head);
			if (!TextUtils.isEmpty(info.photo) && !info.photo.equalsIgnoreCase("null")) {
				ImageLoaderUtil.getInstance(mContext).displayImage(info.photo, ivPhoto, ImageLoaderUtil.user_options);
			}

			return arg1;
		}

		@Override
		public void onClick(View v) {
			final int id = v.getId();
			final int position = (Integer) v.getTag();
			switch (id) {
			case R.id.btn_del:
				toDel(position);
				break;
			// case R.id.btn_tocenter:
			// toMemberCenter(position);
			// break;
			default:
				break;
			}
		}

	}
}
