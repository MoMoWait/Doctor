/**
 * 
 */
package com.comvee.tnb.ui.follow;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.comvee.FinalDb;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.adapter.FollowQDetailedAdapter;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.CustomDatePickDialog;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.FollowQuestionDetailed;
import com.comvee.tnb.model.FollowQuestionOption;
import com.comvee.tnb.network.FollowQuestinLoader;
import com.comvee.tnb.network.NetworkCallBack;
import com.comvee.tnb.widget.ComveeAlertDialog;
import com.comvee.tnb.widget.MySeekBar;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UserMrg;

/**
 * 随访问答页面
 * 
 * @author SZM
 * 
 */
public class FollowQuestionFragment extends BaseFragment implements OnClickListener, OnHttpListener, OnItemClickListener {

	private FinalDb db;

	private boolean isRequest = false;

	private Button bt_next;
	private ListView listView;
	private TextView tv_categoryName;
	private FollowQDetailedAdapter adapter;
	private MySeekBar progressBar;

	private String followupId;
	private String title;
	private int position = 0;// 当前位置
	private String parentPath;
	private FollowQuestionDetailed mDetailed;
	private FollowQuestionDetailed mDetailed2;
	private String newValue;
	private int newValuePotion;
	private int oldValuePotion;
	private boolean[] oldSelected;
	private List<FollowQuestionOption> optionItems = null;
	private List<FollowQuestionDetailed> parent = new ArrayList<FollowQuestionDetailed>();
	private HashMap<String, List<FollowQuestionDetailed>> childs = new HashMap<String, List<FollowQuestionDetailed>>();
	private TitleBarView mBarView;

	@Override
	public int getViewLayoutId() {
		return R.layout.follow_question_fragment;
	}

	public FollowQuestionFragment(String followupId, String title) {
		this.followupId = followupId;
		this.title = title;
	}

	public FollowQuestionFragment() {
	}

	public static FollowQuestionFragment newInstance(String followupId) {
		return new FollowQuestionFragment(followupId, "");
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onLaunch(Bundle bundle) {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		ConfigParams.setAnswerNew(getApplicationContext(), false);
		mRoot.setVisibility(View.GONE);
		mBarView.setTitle(title + "");
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		initView();
		parent.clear();
		mHandler.sendEmptyMessageDelayed(1, 500);

	}

	// 初始化控件
	private void initView() {
		if (TextUtils.isEmpty(followupId)) {
			showToast("异常");
			FragmentMrg.toBack(getActivity());
		} else {
			db = FinalDb.create(getActivity(), ConfigParams.DB_NAME, true);
			tv_categoryName = (TextView) findViewById(R.id.follow_question_categoryName);
			progressBar = (MySeekBar) findViewById(R.id.follow_progressbar);
			listView = (ListView) findViewById(R.id.question_list);
			adapter = new FollowQDetailedAdapter(getActivity());
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(this);
			findViewById(R.id.follow_question_before).setOnClickListener(this);
			bt_next = (Button) findViewById(R.id.follow_question_next);
			bt_next.setOnClickListener(this);

			// Drawable indicator =
			// getResources().getDrawable(R.drawable.progress_indicator);
			// Rect bounds = new Rect(0, 0, indicator.getIntrinsicWidth(),
			// indicator.getIntrinsicHeight());
			// indicator.setBounds(bounds);
			//
			// progressBar.setProgressIndicator(indicator);
			progressBar.setProgress(0);
		}
	}

	// 获取数据
	private void judgeSqliteData() {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final List<FollowQuestionDetailed> temp = db.findAllByWhere(FollowQuestionDetailed.class,
						String.format("followUpId='%s' AND level=%d", followupId, 1));
				// 如果本地没有 发送请求从服务器取
				if (temp == null || temp.size() == 0) {
					if (isRequest) {
						cancelProgressDialog();
						showToast("随访题目加载失败！");
						FragmentMrg.toBack(getActivity());
					}
					if (!isRequest) {
						requestQuestionInfo();
						isRequest = true;
					}

				} else {
					mRoot.setVisibility(View.VISIBLE);
					parent.clear();
					// 获取本地随访题目

					// TODO Auto-generated method stub
					sqliteChild(temp);
					mBarView.setTitle(UserMrg.getTitle(getApplicationContext()));
					Collections.sort(parent, new FollowQuestionDetailed2Comparator());
					getPosition(parentPath);
					changeData(position);
					mHandler.sendEmptyMessage(0);
				}

			}
		});
	}

	// 子控件获取
	private void sqliteChild(List<FollowQuestionDetailed> temp) {
		for (int i = 0; i < temp.size(); i++) {
			List<FollowQuestionDetailed> temp02 = db.findAllByWhere(FollowQuestionDetailed.class, String.format(
					"followUpId='%s' AND level=%d AND parentPath='%s'", temp.get(i).getFollowUpId(), (temp.get(i).getLevel() + 1), temp.get(i)
							.getPath()));
			if (temp02 != null) {
				if (temp02.size() > 0) {
					List<FollowQuestionDetailed> temp03 = db.findAllByWhere(FollowQuestionDetailed.class, String.format(
							"followUpId='%s' AND level=%d AND parentPath='%s'", temp02.get(0).getFollowUpId(), (temp02.get(0).getLevel() + 1), temp02
									.get(0).getPath()));
					if (temp03 == null || temp03.size() == 0) {
						parent.add(temp.get(i));
						childs.put(temp.get(i).getPath(), temp02);
					} else {
						sqliteChild(temp02);
					}
				}
			}
		}
	}

	// 根据父节点Path获取position
	private void getPosition(String parentPath) {
		if (!TextUtils.isEmpty(parentPath)) {
			for (int i = 0; i < parent.size(); i++) {
				if (parentPath.equals(parent.get(i).getPath())) {
					position = i;
					break;
				}
			}
		}
	}

	// 请求随访信息
	private void requestQuestionInfo() {
		showProgressDialog(getString(R.string.msg_loading));
		// ComveeHttp http = new ComveeHttp(getApplicationContext(),
		// ConfigUrlMrg.FOLLOW_GETQUESTION);
		// http.setOnHttpListener(1, this);
		// http.setPostValueForKey("followupId", followupId);
		// http.setNeedGetCache(true,
		// UserMrg.getCacheKey(ConfigUrlMrg.FOLLOW_GETQUESTION));
		// http.startAsynchronous();
		FollowQuestinLoader loader = new FollowQuestinLoader();
		loader.loadQuestion(new NetworkCallBack() {

			@Override
			public void callBack(int what, int status, Object obj) {

				if (status == 0) {
					String title = (String) obj;
					mBarView.setTitle(title);
					UserMrg.setTitle(getApplicationContext(), title);
					parent.clear();
					judgeSqliteData();
				} else {
					cancelProgressDialog();
					showToast(obj.toString());
				}

			}
		}, followupId, db);
	}

	@Override
	public void onSussece(int what, byte[] b, boolean fromCache) {
		cancelProgressDialog();
		switch (what) {
		case 1:
			// parseFollowQuestion(b, fromCache);
			break;
		case 2:
			try {
				ComveePacket packet = ComveePacket.fromJsonString(b);
				if (packet.getResultCode() == 0) {
					showToast("提交随访成功！");

					toFragment(FollowFinishFragment.newInstance(), false, true);
				} else {
					if ("该随访已填写".equals(packet.optJSONObject("res_msg").optString("res_desc"))) {
						toFragment(FollowFinishFragment.newInstance(), false, true);
						return;
					}
					showToast("随访问卷提交失败，请重试！" + packet.optJSONObject("res_msg").optString("res_desc"));
				}
			} catch (Exception e) {
				e.printStackTrace();
				showToast("随访问卷提交失败，请重试！");
			}
			// toFragment(FollowFinishFragment.newInstance(), true);
			break;
		default:
			break;
		}
	}

	// // 回调解析
	// private void parseFollowQuestion(byte[] arg1, boolean fromCache) {
	// try {
	// mRoot.setVisibility(View.VISIBLE);
	// ComveePacket packet = ComveePacket.fromJsonString(arg1);
	// if (packet.getResultCode() == 0) {
	// List<FollowQuestionDetailed> detailedlist = null;
	// List<FollowQuestionOption> optionlist = null;
	// JSONArray ques = packet.optJSONObject("body").optJSONArray("ques");
	// String title = packet.optJSONObject("body").optString("title");
	// mBarView.setTitle(title);
	// UserMrg.setTitle(getApplicationContext(), title);
	// if (ques != null) {
	// int len = ques.length();
	// if (len > 0) {
	// detailedlist = new ArrayList<FollowQuestionDetailed>();
	// optionlist = new ArrayList<FollowQuestionOption>();
	// JSONObject JsonObject = null;
	// JSONArray JSONArrayoptions = null;
	// JSONObject JSONoption = null;
	// FollowQuestionDetailed detailed = null;
	// FollowQuestionOption option = null;
	// for (int i = 0; i < len; i++) {
	// JsonObject = ques.optJSONObject(i);
	// // 题目解析
	// detailed = new FollowQuestionDetailed();
	// detailed.setFollowUpId(followupId);
	// detailed.setCategory(JsonObject.optInt("category"));
	// detailed.setCategoryName(JsonObject.optString("categoryName"));
	// detailed.setDefualtCheck(JsonObject.optInt("defualtCheck"));
	// detailed.setDictName(JsonObject.optString("dictName"));
	// detailed.setHelp(JsonObject.optString("help"));
	// detailed.setIsNeed(JsonObject.optInt("isNeed"));
	// detailed.setIsShow(JsonObject.optInt("isShow"));
	// detailed.setItemCode(JsonObject.optString("itemCode"));
	// detailed.setItemType(JsonObject.optInt("itemType"));
	// detailed.setPath(JsonObject.optString("path"));
	// detailed.setParent(JsonObject.optString("pCode", ""));
	// detailed.setRules(JsonObject.optString("rules"));
	// detailed.setSeq(JsonObject.optString("seq"));
	// detailed.setTie(JsonObject.optInt("tie"));
	// String[] paths = detailed.getPath().split("_");
	// detailed.setLevel(paths.length);
	// int isParent = 0;
	// String parentPath;
	// if (detailed.getLevel() == 1) {
	// isParent = 0;
	// parentPath = "top";
	// } else {
	// isParent = 1;
	// parentPath = "";
	// for (int j = 0; j < paths.length - 1; j++) {
	// if (parentPath.length() > 0) {
	// parentPath += "_";
	// }
	// parentPath += paths[j];
	// }
	// }
	// detailed.setParentPath(parentPath);
	// detailed.setMhasParent(isParent);
	// String value = "";
	// // 选项解析
	// JSONArrayoptions = JsonObject.optJSONArray("itemList");
	// if (JSONArrayoptions != null) {
	// for (int j = 0; j < JSONArrayoptions.length(); j++) {
	// JSONoption = JSONArrayoptions.optJSONObject(j);
	// option = new FollowQuestionOption();
	// option.setFollowId(followupId);
	// option.setId(JSONoption.optString("id"));
	// option.setItemCode(detailed.getItemCode());
	// option.setIsRestrain(JSONoption.optInt("isRestrain"));
	// option.setIsValue(JSONoption.optInt("isValue"));
	// option.setValueCode(JSONoption.optString("valueCode"));
	// option.setValueName(JSONoption.optString("valueName"));
	// option.setShow_seq(JSONoption.optInt("show_seq", j));
	// option.setPosition(optionlist.size());
	// if (detailed.getItemType() == 1 || detailed.getItemType() == 2) {
	// if (option.getIsValue() == 1) {
	// if (value.length() > 0) {
	// value += ",";
	// }
	// value += option.getValueName();
	// }
	// }
	// if (option.getValueCode().equals("unit")) {
	// detailed.setUnit(option.getValueName());
	// }
	// optionlist.add(option);
	// }
	// }
	// detailed.setValue(value);
	// detailedlist.add(detailed);
	// }
	// }
	// }
	// if (detailedlist != null && detailedlist.size() > 0 && optionlist != null
	// && optionlist.size() > 0) {
	// saveSqliteQDetailed(detailedlist, optionlist);
	// }
	// } else {
	// showToast(packet.getResultMsg());
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	// // 保存数据
	// private void saveSqliteQDetailed(List<FollowQuestionDetailed> dList,
	// List<FollowQuestionOption> oList) {
	// db.deleteByWhere(FollowQuestionDetailed.class, "");
	// db.deleteByWhere(FollowQuestionOption.class, "");
	// db.saveList(dList);
	// db.saveList(oList);
	//
	// parent.clear();
	// judgeSqliteData();
	// }

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case TitleBarView.ID_LEFT_BUTTON:
			FragmentMrg.toBack(getActivity());
			break;
		case R.id.follow_question_before:
			saveList();
			if (position > 0) {
				position--;
				changeData(position);
			} else if (position == 0) {
				FragmentMrg.toBack(getActivity());
			}
			break;
		case R.id.follow_question_next:
			saveList();
			if (position < (parent.size() - 1)) {
				position++;
				changeData(position);
			}
			// 到最后一步完成提交
			else if (position == (parent.size() - 1)) {
				requestSumbit();
			}
			break;
		default:
			break;
		}
	}

	// 请求提交随访信息
	private void requestSumbit() {
		showProgressDialog("提交随访内容中……");
		String followUpMemberLog = getFollowUpMemberLog();
		if (TextUtils.isEmpty(followUpMemberLog)) {
			cancelProgressDialog();
			showToast("随访内容错误，请重新提交！");
		} else {
			ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.FOLLOW_SUBMIT);
			http.setOnHttpListener(2, this);
			http.setPostValueForKey("followupId", followupId);
			http.setPostValueForKey("followUpMemberLog", followUpMemberLog);
			http.setNeedGetCache(true, UserMrg.getCacheKey(ConfigUrlMrg.FOLLOW_SUBMIT));
			http.startAsynchronous();
		}
	}

	// 改变数据
	private void changeData(int index) {
		if (index < parent.size()) {
			position = index;
			parentPath = parent.get(position).getPath();
			adapter.update(childs.get(parentPath));
		}
		// 设置父名
		tv_categoryName.setText(parent.get(position).getDictName());
		// 判断下一页按钮文本
		if (position == (parent.size() - 1)) {
			bt_next.setText("完成");
		} else {
			bt_next.setText("下一步");
		}
		// 设置进度条
		mHandler.sendEmptyMessageDelayed(5, 30);
	}

	private void saveList() {
		if (adapter.save) {
			showProgressDialog("正在保存数据！");
			for (int i = 0; i < adapter.getList().size(); i++) {
				mDetailed = adapter.getItem(i);
				mDetailed2 = null;
				// Log.e("", "value>>" + mDetailed.getValue());
				setItemValue(mDetailed.getValue());
			}
			adapter.save = false;
			cancelProgressDialog();
		}
	}

	@Override
	public void onFialed(int what, int errorCode) {
		cancelProgressDialog();
		ComveeHttpErrorControl.parseError(getActivity(), errorCode);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		mDetailed = adapter.getItem(arg2);
		createDialogByItemType(mDetailed);
	}

	// 点击子控件后创建对话框
	private void createDialogByItemType(FollowQuestionDetailed detailed) {
		int itemtype = detailed.getItemType();
		String[] itemNames = null;
		if (itemtype == 1 || itemtype == 2) {
			// 初始化各个选项
			optionItems = db.findAllByWhere(FollowQuestionOption.class,
					String.format("followId='%s' AND itemCode='%s'", followupId, detailed.getItemCode()));
			// 排序
			Collections.sort(optionItems, new FollowQuestionOptionComparator());
			itemNames = new String[optionItems.size()];
			for (int j = 0; j < optionItems.size(); j++) {
				itemNames[j] = optionItems.get(j).getValueName();
			}
		}
		if (detailed.getTie() == 0) {
			switch (itemtype) {// 1 单选题 2多选题 3日期 4 文本填空 5 数值填空
			case 1:
				oldValuePotion = getItemListChooseId(optionItems);
				createSingleDialog(detailed.getDictName(), itemNames);
				break;
			case 2:
				oldSelected = new boolean[optionItems.size()];
				for (int i = 0; i < optionItems.size(); i++) {
					if (optionItems.get(i).getIsValue() == 1)
						oldSelected[i] = true;
				}
				createMultiDialog(detailed.getDictName(), itemNames);
				break;
			case 3:
				Calendar calendar = null;
				try {
					calendar = stringToCalendar(detailed.getValue(), "yyyy-MM-dd");
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (calendar == null) {
					calendar = Calendar.getInstance();
				}
				showTimeDialogDate(calendar);
				break;
			case 4:
			case 5:
				// String title = detailed.getDictName();
				// if (!TextUtils.isEmpty(detailed.getUnit())) {
				// title += "(" + detailed.getUnit() + ")";
				// }
				// toFragment(PatientArchiveInputFragment.newInstance(position,
				// title, detailed.getValue(), this), true);
				break;

			default:
				break;
			}
		} else {
			showToast("该项不可修改！");
		}
	}

	// 拼接上传数据
	private String getFollowUpMemberLog() {
		StringBuffer followlog = new StringBuffer();
		followlog.append("[");
		List<FollowQuestionDetailed> child = null;
		FollowQuestionDetailed detailed = null;
		List<FollowQuestionOption> options = null;
		for (int i = 0; i < parent.size(); i++) {
			child = childs.get(parent.get(i).getPath());
			if (child == null || child.isEmpty()) {
				continue;
			}
			for (int j = 0; j < child.size(); j++) {
				detailed = child.get(j);
				if (detailed.getMhasChild() == 0 && !TextUtils.isEmpty(detailed.getValue())) {
					if (followlog.toString().length() > 1) {
						followlog.append(",");
					}
					followlog.append("{");
					followlog.append("\"code\":\"");
					followlog.append(detailed.getItemCode());
					followlog.append("\",\"pcode\":\"");
					followlog.append(detailed.getParent());
					followlog.append("\",\"value\":\"");
					String value = "";
					switch (detailed.getItemType()) {
					case 1:
					case 2:
						options = db.findAllByWhere(FollowQuestionOption.class,
								String.format("followId='%s' AND itemCode='%s'", followupId, detailed.getItemCode()));
						if (options != null) {
							if (options.size() > 0) {
								for (int k = 0; k < options.size(); k++) {
									if (options.get(k).getIsValue() == 1) {
										if (!TextUtils.isEmpty(value)) {
											value += "|";
										}
										value += options.get(k).getValueCode();
									}
								}
							}
						}
						break;
					case 3:
					case 4:
					case 5:
						value = detailed.getValue();
						break;
					default:
						break;
					}
					followlog.append(value);
					followlog.append("\"}");
				}
			}
		}
		followlog.append("]");
		return followlog.toString();
	}

	// 将String转Calendar
	private Calendar stringToCalendar(String time, String fomate) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(fomate);
		Date date = (Date) sdf.parse(time);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	// // 数字对话框
	// private void showNumDialog(FollowQuestionDetailed detailed, final
	// String[] items) {
	// CustomSingleNumPickDialog.Builder buidler = new
	// CustomSingleNumPickDialog.Builder(getActivity());
	//
	// buidler.setLimit(Integer.valueOf(items[0]), Integer.valueOf(items[1]));
	// buidler.setFloat(false);
	// if (detailed.getValue().equals(""))
	// detailed.setValue(0 + "");
	// buidler.setDefualtNum(Integer.valueOf(detailed.getValue()));
	// buidler.setUnit(items[2]);
	// buidler.setOnChangeNumListener(new OnChangeNumListener() {
	// @Override
	// public void onChange(Dialog dialog, float num) {
	// String n = String.valueOf((int) num);
	// setItemValue(n);
	// saveOptions(optionItems);
	// }
	// });
	// buidler.create().show();
	// }

	// 创建时间对话框
	private void showTimeDialogDate(Calendar calendar) {
		CustomDatePickDialog dialog = new CustomDatePickDialog();
		dialog.setOnTimeChangeListener(new CustomDatePickDialog.OnTimeChangeListener() {

			@Override
			public void onChange(DialogFragment dialog, int year, int month, int day) {
				// TODO Auto-generated method stub
				String time = String.format("%d-%02d-%02d", year, month, day);

				setItemValue(time);
			}
		});
		dialog.setLimitTime(1890, 2190);
		Calendar cal = Calendar.getInstance();
		dialog.setLimitCanSelectOfEndTime(cal);

		try {
			dialog.setDefaultTime(calendar);
		} catch (Exception e) {
			e.printStackTrace();
		}
		dialog.show(getActivity().getSupportFragmentManager(), "dialog");
	}

	AlertDialog dialog;

	// 创建单选对话框
	private void createSingleDialog(String title, String[] itemNames) {
		dialog = new ComveeAlertDialog.Builder(getActivity()).setTitle(title)
				.setSingleChoiceItems(itemNames, oldValuePotion, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						if (arg1 < optionItems.size()) {
							newValuePotion = arg1;
							newValue = optionItems.get(arg1).getValueName();
						}
						if (newValue != null) {
							// 将旧的值设置未选中
							if (oldValuePotion != -1)
								optionItems.get(oldValuePotion).setIsValue(0);
							// 将新值设置为选中
							optionItems.get(newValuePotion).setIsValue(1);
							setItemValue(optionItems.get(newValuePotion).getValueName());
							saveOptions(optionItems);

						}
						dialog.cancel();
					}
				}).create();
		dialog.show();
	}

	// 创建多选对话框
	private void createMultiDialog(String title, String[] itemNames) {
		new ComveeAlertDialog.Builder(getActivity()).setTitle(title)
				.setMultiChoiceItems(itemNames, oldSelected, new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1, boolean arg2) {
						oldSelected[arg1] = arg2;
					}
				}).setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						StringBuffer value = new StringBuffer();
						for (int i = 0; i < oldSelected.length; i++) {
							if (oldSelected[i]) {
								optionItems.get(i).setIsValue(1);
								if (value.length() > 0) {
									value.append(",");
								}
								value.append(optionItems.get(i).getValueName());
							} else {
								optionItems.get(i).setIsValue(0);
							}
						}
						setItemValue(value.toString());
						saveOptions(optionItems);

					}
				}).show();
	}

	// 更新选项修改
	private void saveOptions(List<FollowQuestionOption> list) {
		for (int i = 0; i < list.size(); i++) {
			db.update(list.get(i));
		}
		// 执行规则
		dealIsRestrain();
	}

	// 处理约束
	private void dealIsRestrain() {
		int which = getItemListChooseId(optionItems);
		if (which == -1)
			return;
		// 获取当前题目的所有规则
		String[] seqs = getRuleByItemListId(mDetailed);
		if (seqs == null) {
			return;
		}
		//
		FollowQuestionDetailed temp = getArchiveBySeq(seqs[0]);
		if (temp != null) {

			// 开始实行规则
			switch (optionItems.get(which).getIsRestrain()) {
			case 0:// 0 没有规则
				break;
			case 1:// 1 显示隐藏项目
				updateShow(seqs, 1, 0);
				break;
			case 2:// 2 嵌入弹开
				mDetailed2 = temp;
				createDialogByItemType(temp);
				break;
			case 3:// 3 显示隐藏项目同时触发题目约束
				updateShow(seqs, 1, 1);
				break;
			case 4:// 4 隐藏项目
				updateShow(seqs, 0, 0);
				break;
			default:
				break;
			}
		}
	}

	// 根据规则修改选项
	private void updateShow(String[] seqs, int isShow, int isTie) {
		FollowQuestionDetailed temp = null;
		for (int i = 0; i < seqs.length; i++) {
			temp = getArchiveBySeq(seqs[i]);
			if (temp != null) {
				temp.setIsShow(isShow);
				temp.setTie(isTie);
				db.update(temp);
			}
		}

		judgeSqliteData();
	}

	// 根据题号获取档案题目下标
	private FollowQuestionDetailed getArchiveBySeq(String seq) {
		List<FollowQuestionDetailed> temp = db.findAllByWhere(FollowQuestionDetailed.class, String.format("seq='%s'", seq));
		FollowQuestionDetailed ret = null;
		if (temp == null || temp.size() == 0) {
			ret = null;
		} else {
			ret = temp.get(0);
		}
		return ret;
	}

	// 根据档案题目选项自身id去取规则
	private String[] getRuleByItemListId(FollowQuestionDetailed detailed) {
		if (TextUtils.isEmpty(detailed.getRules()))
			return null;
		int which = getItemListChooseId(optionItems);
		if (which == -1)
			return null;
		String id = optionItems.get(which).getId();
		String[] seqs = null;
		try {
			JSONObject object = new JSONObject(detailed.getRules());
			seqs = object.optString(id).split(",");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return seqs;
	}

	/**
	 * 设置选项值
	 * */
	private void setItemValue(String value) {
		if (mDetailed2 != null) {
			mDetailed2.setValue(value);
			db.update(mDetailed2);
			value = String.format("%s(%s)", mDetailed.getValue(), value);
			mDetailed2 = null;
		}
		mDetailed.setValue(value);
		db.update(mDetailed);
		// 更新数据
		List<FollowQuestionDetailed> temp = childs.get(parentPath);
		for (int i = 0; i < temp.size(); i++) {
			if (mDetailed.getPath().equals(temp.get(i).getPath())) {
				temp.get(i).setValue(value);
				break;
			}
		}
		childs.remove(parentPath);
		childs.put(parentPath, temp);
		// 更新视图
		changeData(position);
	}

	// 判断子项里面哪一个被选中,-1表示全部没有选
	private int getItemListChooseId(List<FollowQuestionOption> optionItems) {
		int which = -1;
		// 判断被选中的是哪一项
		if (optionItems != null) {
			for (int j = 0; j < optionItems.size(); j++) {
				if (optionItems.get(j).getIsValue() == 1) {
					which = j;
				}
			}
		}
		return which;
	}

	// 选项比较器
	class FollowQuestionOptionComparator implements Comparator<FollowQuestionOption> {
		@Override
		public int compare(FollowQuestionOption object1, FollowQuestionOption object2) {

			long m1 = object1.getShow_seq();
			long m2 = object2.getShow_seq();
			int result = 0;
			if (m1 > m2) {
				result = 1;
			}
			if (m1 < m2) {
				result = -1;
			}
			return result;
		}
	}

	// 问题比较器
	class FollowQuestionDetailed2Comparator implements Comparator<FollowQuestionDetailed> {
		@Override
		public int compare(FollowQuestionDetailed object1, FollowQuestionDetailed object2) {
			String ms1 = object1.getPath();
			String ms2 = object2.getPath();
			int m1 = Integer.parseInt(ms1.replace("_", ""));
			int m2 = Integer.parseInt(ms2.replace("_", ""));
			int result = 0;
			if (m1 > m2) {
				result = 1;
			}
			if (m1 < m2) {
				result = -1;
			}
			return result;
		}
	}

	private int mTempProgress = 0;
	private int mCurProgress = 0;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				cancelProgressDialog();
				break;
			case 1:
				judgeSqliteData();
				break;

			default:
				try {
					if (position == (parent.size() - 1)) {
						mCurProgress = 100;
					} else {
						mCurProgress = (100 / parent.size()) * (position + 1);
					}
					if (mTempProgress > mCurProgress) {
						mHandler.sendEmptyMessageDelayed(5, 30);
						progressBar.setProgress(--mTempProgress);
					} else if (mTempProgress < mCurProgress) {
						mHandler.sendEmptyMessageDelayed(5, 30);
						progressBar.setProgress(++mTempProgress);
					} else {
						progressBar.setProgress(mTempProgress);
						mHandler.removeMessages(5);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}

		}
	};
}
