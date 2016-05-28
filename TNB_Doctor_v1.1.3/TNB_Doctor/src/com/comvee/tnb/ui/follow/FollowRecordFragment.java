package com.comvee.tnb.ui.follow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.FollowAction;
import com.comvee.tnb.model.FollowRecord;
import com.comvee.tnb.model.TaskItem;
import com.comvee.tnb.ui.task.TaskIntroduceFragment;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.ImageLoaderUtil;

/**
 * 随访记录详细页
 * 
 * @author Administrator
 * 
 */
public class FollowRecordFragment extends BaseFragment implements OnHttpListener, OnClickListener {
	private LinearLayout group;
	private int type;
	private long followupId;
	private TextView left, right, followtv;
	private View view;
	private ArrayList<FollowRecord> followList;
	private ArrayList<FollowAction> actions;
	private ArrayList<Integer> actionOther;// 推荐行动最外层类型
	private HashMap<String, FollowRecord> fathList;
	private List<KindView> recordKingView;// 最外层类别：基本信息，家族史，生活方式等等
	private LayoutInflater inflater;
	private TitleBarView mBarView;

	@Override
	public int getViewLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.fragment_follow_record;
	}

	public FollowRecordFragment() {

	}

	public FollowRecordFragment(int type, long followupId) {
		this.type = type;
		this.followupId = followupId;
	}

	public static FollowRecordFragment newInstance(int type, long followupId) {
		return new FollowRecordFragment(type, followupId);
	}

	@Override
	public void onLaunch(Bundle bundle) {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		initView();

	}

	public void initView() {
		this.inflater = LayoutInflater.from(getActivity());
		group = (LinearLayout) findViewById(R.id.follwo_lin);
		followtv = (TextView) findViewById(R.id.follow_text);
		view = View.inflate(getApplicationContext(), R.layout.titlebar_follow_record, null);
		left = (TextView) view.findViewById(R.id.tab_left);
		right = (TextView) view.findViewById(R.id.tab_right);
		left.setText("问卷记录");
		right.setText("医生建议");
		left.setOnClickListener(this);
		right.setOnClickListener(this);
		view.setLayoutParams(new LayoutParams(200, 3));
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		mBarView.addView(view, params);
		mBarView.setTitle("");
		choiceTabUI(type);
	}

	private void choiceTabUI(int type) {
		group.removeAllViews();
		left.setBackgroundResource(type == 1 ? R.drawable.jiankangzixun_03 : R.drawable.jiankangzixun_07);
		right.setBackgroundResource(type != 1 ? R.drawable.jiankangzixun_08 : R.drawable.jiankangzixun_04);
		int green = getResources().getColor(R.color.theme_color_green);
		right.setTextColor(type != 1 ? Color.WHITE : green);
		left.setTextColor(type == 1 ? Color.WHITE : green);
		this.type = type;
		initData();

	}

	private void initData() {
		if (type == 1) {
			recordFollowUpMemberLog();
		} else {
			recordActionPlanDetai();
		}
	}

	private void recordFollowUpMemberLog() {
		showProgressDialog(getString(R.string.msg_loading));
		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.FOLLOW_LOG);
		http.setPostValueForKey("followupId", followupId + "");
		http.setNeedGetCache(true, ConfigUrlMrg.FOLLOW_LOG + followupId);
		http.setOnHttpListener(1, this);
		http.startAsynchronous();
	}

	private void recordActionPlanDetai() {
		showProgressDialog(getString(R.string.msg_loading));
		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.FOLLOW_ACTION);
		http.setPostValueForKey("followupId", followupId + "");
		http.setNeedGetCache(true, ConfigUrlMrg.FOLLOW_ACTION + followupId);
		http.setOnHttpListener(2, this);
		http.startAsynchronous();

	}

	@Override
	public void onSussece(int what, byte[] b, boolean fromCache) {

		cancelProgressDialog();
		switch (what) {
		case 1:
			parseFollowUpMemberLog(b, fromCache);

			break;
		case 2:
			parserActionPlanDetai(b, fromCache);
			break;
		default:
			break;
		}
	}

	@Override
	public void onFialed(int what, int errorCode) {
		cancelProgressDialog();
		ComveeHttpErrorControl.parseError(getActivity(), errorCode);
	}

	@Override
	public void onDestroyView() {

		super.onDestroyView();

		mBarView.removeView(view);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.tab_left:
			choiceTabUI(1);
			break;
		case R.id.tab_right:
			choiceTabUI(0);
			break;
		case 123456:
			FollowAction action = (FollowAction) arg0.getTag();
			TaskItem item = new TaskItem();
			item.setJobCfgId(action.getJobCfgId() + "");
			TaskIntroduceFragment frag = TaskIntroduceFragment.newInstance();
			frag.setTaskInfo(item);
			frag.setDoctorId(action.getDoctroId());
			toFragment(frag, true, true);
			break;
		default:
			break;
		}
	}

	private void parserActionPlanDetai(byte b[], boolean fromCache) {
		actions = new ArrayList<FollowAction>();
		actionOther = new ArrayList<Integer>();
		try {
			final ComveePacket packet = ComveePacket.fromJsonString(b);
			if (packet.getResultCode() == 0) {
				JSONObject body = packet.optJSONObject("body");
				JSONArray list = body.optJSONArray("list");
				if (list == null) {
					followtv.setText(getString(R.string.first3_noresponse));
					followtv.setVisibility(View.VISIBLE);
					return;
				}
				for (int i = 0; i < list.length(); i++) {
					JSONObject info = list.optJSONObject(i);
					FollowAction action = new FollowAction();

					String title = info.optString("title");
					int type = info.optInt("type");
					String detailText = info.optString("detailText");
					String doctroName = info.optString("doctroName");
					long doctroId = info.optLong("doctroId");
					action.setTitle(title);
					action.setDetailText(detailText);
					action.setType(type);
					action.setDoctroName(doctroName);
					action.setDoctroId(doctroId);
					if (type == 2) {
						JSONObject info2 = info.optJSONObject("jobCfg");
						action.setIsNew(info2.optInt("isNew"));
						action.setCommentNum(info2.optInt("commentNum"));
						action.setDoSuggest(info2.optString("doSuggest"));
						action.setGainNum(info2.optInt("gainNum"));
						action.setImgUrl(info2.optString("imgUrl"));
						action.setJobInfo(info2.optString("jobInfo"));
						action.setJobTitle(info2.optString("jobTitle"));
						action.setJobCfgId(info2.optLong("jobCfgId"));

					}
					actions.add(action);
					if (!actionOther.contains(action.getType())) {
						if (action.getType() == 3) {
							actionOther.add(0, action.getType());
						} else {
							actionOther.add(action.getType());
						}
					}
				}
				if (!fromCache) {
					ComveeHttp.setCache(getApplicationContext(), ConfigUrlMrg.FOLLOW_ACTION + followupId, ConfigParams.CHACHE_TIME_SHORT, b);
				}
			}

			if (actions.size() > 0) {
				followtv.setVisibility(View.GONE);
			} else {
				followtv.setText(getString(R.string.first3_noresponse));
				followtv.setVisibility(View.VISIBLE);
				return;
			}
			createActionGroupView();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseFollowUpMemberLog(byte[] b, boolean fromCache) {
		followList = new ArrayList<FollowRecord>();
		fathList = new HashMap<String, FollowRecord>();
		try {
			final ComveePacket packet = ComveePacket.fromJsonString(b);
			if (packet.getResultCode() == 0) {
				JSONObject body = packet.optJSONObject("body");
				JSONArray followlist = body.optJSONArray("followUpLog");
				for (int i = 0; i < followlist.length(); i++) {
					JSONObject info = followlist.optJSONObject(i);
					FollowRecord record = new FollowRecord();
					record.setDictName(info.optString("dictName"));
					record.setDictValue(info.optString("dictValue"));
					record.setPath(info.optString("path"));
					record.setPcode(info.optString("PCode"));
					followList.add(record);
					fathList.put(record.getPath(), record);
				}

				if (followList.size() > 0) {
					followtv.setVisibility(View.GONE);
				} else {
					//followtv.setText(getString(R.string.first3_noresponse_1));
					followtv.setVisibility(View.VISIBLE);
					return;
				}
				getKindLayout();
				bindKindView();
				bindindChildren();
				if (!fromCache) {
					ComveeHttp.setCache(getApplicationContext(), ConfigUrlMrg.FOLLOW_LOG + followupId, ConfigParams.CHACHE_TIME_SHORT, b);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据外层控件获取子项 生成子控件
	 */
	private void bindindChildren() {
		if (followList == null)
			return;
		for (int i = 0; i < recordKingView.size(); i++) {
			int num = 0;
			for (int j = 0; j < followList.size(); j++) {
				FollowRecord model = followList.get(j);
				if (model.getPcode().equals(recordKingView.get(i).kindKey)) {
					addChildView(recordKingView.get(i).kindLayout, j, num);
					num++;
				}
			}
		}
	}

	/**
	 * 生成随访记录子控件
	 * 
	 * @param parent
	 * @param index
	 */
	private void addChildView(LinearLayout parent, int index, int num) {
		FollowRecord archive = followList.get(index);
		View child = inflater.inflate(R.layout.follow_record_item, null);
		TextView basicfile_key = (TextView) child.findViewById(R.id.basicfile_key);
		TextView basicfile_value = (TextView) child.findViewById(R.id.basicfile_value);
		View line = child.findViewById(R.id.sprite_line);
		if (num == 0) {
			line.setVisibility(View.GONE);

		} else {
			line.setVisibility(View.VISIBLE);
		}
		basicfile_key.setText(archive.getDictName());
		basicfile_value.setText(archive.getDictValue());
		parent.addView(child, parent.getChildCount());

	}

	/**
	 * 生成随访记录外层控件
	 */
	private void bindKindView() {
		KindView kindView = null;
		for (int i = 0; i < recordKingView.size(); i++) {
			kindView = recordKingView.get(i);
			LinearLayout layout = null;
			layout = (LinearLayout) inflater.inflate(R.layout.basicfile_linerlayout_item, null);
			TextView tv_pcode = (TextView) layout.findViewById(R.id.basicfile_tv_pcode);
			tv_pcode.setText(kindView.kindName);
			kindView.kindLayout = layout;
			group.addView(layout);
		}
	}

	private void createActionGroupView() {
		if (actionOther == null || actionOther.size() == 0) {
			return;
		}
		for (int i = 0; i < actionOther.size(); i++) {
			LinearLayout layout = null;
			layout = (LinearLayout) inflater.inflate(R.layout.follow_item_group, null);
			TextView tv_pcode = (TextView) layout.findViewById(R.id.basicfile_tv_pcode);
			switch (actionOther.get(i)) {
			case 1:
				tv_pcode.setText("请坚持完成以下行动方案");
				break;
			case 2:
				tv_pcode.setText("请添加以下任务");
				break;
			case 3:
				tv_pcode.setText("医生建议");
				break;
			default:
				break;
			}
			group.addView(layout);
			createChildView(layout, actionOther.get(i));
		}
	}

	/**
	 * 获取随访记录的外层控件数
	 */
	@SuppressWarnings("unchecked")
	private void getKindLayout() {
		if (type == 1) {
			recordKingView = new ArrayList<KindView>();
			for (int i = 0; i < followList.size(); i++) {
				KindView kindView = null;
				FollowRecord model = followList.get(i);
				String str[] = model.getPath().split("_");
				if (str.length == 2 || str.length == 3) {
					model = fathList.get(model.getPcode());
					kindView = new KindView(model.getPath(), model.getDictName(), null);
				}

				if (!recordKingView.contains(kindView) && kindView != null) {
					recordKingView.add(kindView);
					followList.remove(model);
				}
			}
			// 按照kindKey从小到大排序
			Collections.sort(recordKingView, new MyComparator());
		}
	}

	private void createChildView(LinearLayout layout, int type) {

		for (int j = 0; j < actions.size(); j++) {
			FollowAction model = actions.get(j);
			if (model.getType() == type) {
				View v = View.inflate(getContext(), R.layout.item_follow_record, null);
				ImageView dictview = (ImageView) v.findViewById(R.id.follow_img);
				ImageView view2 = (ImageView) v.findViewById(R.id.follow_img1);
				TextView dictname = (TextView) v.findViewById(R.id.follw_tv1);
				TextView value = (TextView) v.findViewById(R.id.follw_tv2);
				layout.addView(v);
				switch (model.getType()) {
				case 1:
					dictview.setVisibility(View.GONE);
					view2.setVisibility(View.GONE);
					value.setVisibility(View.GONE);
					dictname.setText(model.getTitle());
					break;
				case 2:
					ImageLoaderUtil.getInstance(mContext).displayImage(model.getImgUrl(), dictview, ImageLoaderUtil.default_options);
					dictname.setText(model.getJobTitle());
					value.setText(model.getJobInfo());
					v.setTag(model);
					v.setId(123456);
					v.setOnClickListener(this);
					break;
				case 3:
					dictview.setVisibility(View.GONE);
					view2.setVisibility(View.GONE);
					dictname.setText(model.getTitle());
					value.setText(model.getDetailText());
					break;
				default:
					break;
				}
			}
		}

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		ComveeHttp.clearCache(getApplicationContext(), ConfigUrlMrg.FOLLOW_ACTION + followupId);
		ComveeHttp.clearCache(getApplicationContext(), ConfigUrlMrg.FOLLOW_LOG + followupId);
		super.onDestroy();
	}

	class MyComparator implements Comparator {
		@Override
		public int compare(Object obj1, Object obj2) {
			KindView k1 = (KindView) obj1;
			KindView k2 = (KindView) obj2;
			return k1.kindKey.compareTo(k2.kindKey);
		}

	}

	class KindView {
		String kindKey;
		String kindName;
		LinearLayout kindLayout;

		public KindView(String kindKey, String kindName, LinearLayout kindLayout) {
			this.kindKey = kindKey;
			this.kindName = kindName;
			this.kindLayout = kindLayout;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			KindView other = (KindView) obj;
			if (other.kindKey.equals(this.kindKey) && other.kindName.equals(this.kindName))
				return true;
			return false;
		}
	}
}
