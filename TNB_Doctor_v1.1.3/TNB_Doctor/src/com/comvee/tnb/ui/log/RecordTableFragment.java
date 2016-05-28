package com.comvee.tnb.ui.log;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.comvee.FinalDb;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.RecordTableInfo;
import com.comvee.tnb.ui.assess.AssessFragment;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UserMrg;
import com.comvee.util.Util;

/**
 * 健康日志 列表页面
 * 
 * @author friendlove
 * 
 */
@SuppressLint("ValidFragment")
public class RecordTableFragment extends BaseFragment implements OnHttpListener, OnClickListener {
	private TendencyListAdapter mAdapter;
	private List<RecordTableInfo> mListItems;

	private FinalDb db = null;
	private int maxCount, minCount, normolCount;
	private TextView tvMax, tvMin, tvNormol, tvMaxPercent, tvMinPercent, tvNomorlPercent, tvLabel;
	private View layoutCalendarNodata;
	private ListView listView;
	private int mType;//
	private View btnRecord;
	private TitleBarView mBarView;

	public RecordTableFragment() {
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			mRoot.setVisibility(View.VISIBLE);
			onUpdateView();
		};
	};

	private RecordTableFragment(int type) {
		this.mType = type;
	}

	@SuppressLint("ValidFragment")
	public static RecordTableFragment newInstance(int type) {
		RecordTableFragment fragment = new RecordTableFragment(type);
		return fragment;
	}

	private void onUpdateView() {

		// mListItems = db.findAllByWhere(RecordTableInfo.class, "");
		mListItems = db.findAll(RecordTableInfo.class);
		mAdapter.notifyDataSetChanged();
		refreshHeader();
	}

	private void refreshHeader() {
		int total = maxCount + minCount + normolCount;
		tvMax.setText(maxCount + "");
		tvMin.setText(minCount + "");
		tvNormol.setText(normolCount + "");
		layoutCalendarNodata.setVisibility(total == 0 ? View.VISIBLE : View.GONE);
		// btnRecord.setVisibility(total == 0 ? View.VISIBLE : View.GONE);
		btnRecord.setVisibility(View.GONE);
		if (total == 0) {
			tvMaxPercent.setText("0%");
			tvMinPercent.setText("0%");
			tvNomorlPercent.setText("0%");
			drawCircle(0, 0, 0);

		} else {
			int maxPer = maxCount * 100 / total;
			int minPer = minCount * 100 / total;
			int normolPer = (100 - maxPer - minPer);
			tvMaxPercent.setText(maxPer + "%");
			tvMinPercent.setText(minPer + "%");
			tvNomorlPercent.setText(normolPer + "%");
			drawCircle(maxPer, minPer, normolPer);

		}

	}

	private void drawCircle(int maxPercent, int minPercent, int normolPercent) {
		ImageView iv = (ImageView) findViewById(R.id.img_photo);
		int width = Util.dip2px(getContext(), 120);
		float radius = width / 6f;
		Bitmap newb = Bitmap.createBitmap(width, width, Config.ARGB_8888);
		Canvas cv = new Canvas(newb);
		cv.save(Canvas.ALL_SAVE_FLAG);
		Paint paint = new Paint(Color.BLACK);
		paint.setAntiAlias(true);

		if (maxPercent == 0 && minPercent == 0 && normolPercent == 0) {
			paint.setColor(getResources().getColor(R.color.text_defualt));
			RectF rect = new RectF(0, 0, width, width);
			cv.drawArc(rect, 0, 360, true, paint);
		} else {
			paint.setColor(getResources().getColor(R.color.blue));
			RectF rect = new RectF(0, 0, width, width);
			cv.drawArc(rect, 0, maxPercent / 100f * 360, true, paint);
			paint.setColor(getResources().getColor(R.color.green));
			cv.drawArc(rect, maxPercent / 100f * 360, normolPercent / 100f * 360, true, paint);
			paint.setColor(getResources().getColor(R.color.red));
			cv.drawArc(rect, maxPercent / 100f * 360 + normolPercent / 100f * 360, minPercent / 100f * 360, true, paint);
		}

		paint.setColor(Color.WHITE);
		cv.drawCircle(width / 2, width / 2, radius, paint);
		paint.setColor(getResources().getColor(R.color.text_defualt));
		paint.setTextSize(40);
		paint.setStrokeWidth(4);
		cv.drawText("%", width / 2 - 15, width / 2 + 15, paint);
		iv.setImageBitmap(newb);
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.fragment_record_table;
	}

	@Override
	public void onLaunch(Bundle bundle) {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		mBarView.setTitle(getString(R.string.title_log));
		db = FinalDb.create(getApplicationContext(), ConfigParams.DB_NAME);
		init();
		requestDataList();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	public void requestDataList() {
		mRoot.setVisibility(View.GONE);
		showProgressDialog(getString(R.string.msg_loading));
		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.RECORD_DATA_TABLE);
		http.setOnHttpListener(1, this);
		http.startAsynchronous();
	}

	@Override
	public void onSussece(int what, byte[] b, boolean fromCache) {
		cancelProgressDialog();
		paseDataList(what, b, fromCache);
	}

	public void paseDataList(int index, byte[] b, boolean fromCache) {

		try {
			final ComveePacket packet = ComveePacket.fromJsonString(b);
			if (packet.getResultCode() == 0) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							ArrayList<RecordTableInfo> list = new ArrayList<RecordTableInfo>();

							JSONObject body = packet.getJSONObject("body");
							final int monitor_cfg = body.optInt("monitor_cfg");
							final int mood_cfg = body.optInt("mood_cfg");
							final int food_cfg = body.optInt("food_cfg");
							final int pill_cfg = body.optInt("pill_cfg");
							final int habit_cfg = body.optInt("habit_cfg");
							final int sport_cfg = body.optInt("sport_cfg");

							JSONArray array = body.getJSONArray("rows");
							RecordTableInfo info = null;

							int len = array.length();
							for (int i = 0; i < len; i++) {
								JSONObject obj = array.getJSONObject(i);
								info = new RecordTableInfo();
								info.assistFood = obj.optInt("assistFood");
								info.dinner = obj.optInt("dinner");
								info.drink = obj.optInt("drink");
								info.fat = obj.optInt("fat");
								info.mood = obj.optInt("mood");
								info.pill = obj.optInt("pill");
								info.salt = obj.optInt("salt");
								info.sport = obj.optInt("sport");
								info.smoke = obj.optInt("smoke");
								info.sugarMonitor = obj.optInt("sugarMonitor");
								info.vagetable = obj.optInt("vagetable");
								info.water = obj.optInt("water");
								info.insertDt = obj.optString("insertDt");
								info.goal = obj.optInt("goal");
								info.score = obj.optInt("score");

								if (UserMrg.isTnb()) {
									info.totalFood = info.assistFood + info.dinner + info.vagetable + info.fat + info.salt;
									info.totalLive = info.smoke + info.drink;
								} else {
									info.totalFood = info.assistFood + info.dinner + info.vagetable + info.fat + info.salt + info.water;
									info.totalLive = info.smoke + info.drink;
								}

								if (mType == 0) {
									getStatus(info, info.score, info.goal);
								} else if (mType == 1) {
									getStatus(info, info.totalFood, food_cfg);
								} else if (mType == 2) {
									getStatus(info, info.sport, sport_cfg);
								} else if (mType == 3) {
									getStatus(info, info.totalLive, habit_cfg);
								} else if (mType == 4) {
									getStatus(info, info.mood, mood_cfg);
								} else if (mType == 5) {
									getStatus(info, info.pill, pill_cfg);
								} else if (mType == 6) {
									getStatus(info, info.sugarMonitor, monitor_cfg);
								}

								list.add(info);
							}

							try {
								db.deleteByWhere(RecordTableInfo.class, "");
							} catch (Exception e) {
								e.printStackTrace();
							}
							db.saveList(list);
							mHandler.sendEmptyMessage(1);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();

			} else {
				ComveeHttpErrorControl.parseError(getActivity(), packet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void getStatus(RecordTableInfo info, int cur, int target) {
		if (cur > target * 0.8f) {
			info.status = 1;
			maxCount++;
		} else if (cur < target * 0.4f) {
			info.status = -1;
			minCount++;
		} else {
			info.status = 0;
			normolCount++;
		}
	}

	private void init() {
		layoutCalendarNodata = findViewById(R.id.layout_calendar_nodata);

		listView = (ListView) findViewById(R.id.list_view);
		listView.setPadding(0, 0, 0, 0);
		listView.setDivider(null);
		mAdapter = new TendencyListAdapter();
		listView.addHeaderView(View.inflate(getContext(), R.layout.item_record_table_head, null));
		btnRecord = findViewById(R.id.btn_record);
		btnRecord.setOnClickListener(this);
		listView.setAdapter(mAdapter);
		tvMax = (TextView) findViewById(R.id.tv_max_count);
		tvMin = (TextView) findViewById(R.id.tv_min_count);
		tvNormol = (TextView) findViewById(R.id.tv_normol_count);
		tvMaxPercent = (TextView) findViewById(R.id.tv_max_percent);
		tvMinPercent = (TextView) findViewById(R.id.tv_min_percent);
		tvNomorlPercent = (TextView) findViewById(R.id.tv_normol_percent);
		tvLabel = (TextView) findViewById(R.id.tv_label);

		if (mType == 0) {
			findViewById(R.id.layout_record0).setVisibility(View.VISIBLE);
			tvLabel.setText("总体详情");

			if (!UserMrg.isTnb()) {
				findViewById(R.id.tv_pill).setVisibility(View.GONE);
				findViewById(R.id.tv_sugar).setVisibility(View.GONE);
			}

		} else if (mType == 1) {
			findViewById(R.id.layout_record1).setVisibility(View.VISIBLE);
			tvLabel.setText("饮食详情");
			if (UserMrg.isTnb()) {
				findViewById(R.id.tv_water).setVisibility(View.GONE);
			}
		} else if (mType == 2) {
			findViewById(R.id.layout_record2).setVisibility(View.VISIBLE);
			tvLabel.setText("运动详情");
		} else if (mType == 3) {
			findViewById(R.id.layout_record3).setVisibility(View.VISIBLE);
			tvLabel.setText("生活习惯详情");
		} else if (mType == 4) {
			findViewById(R.id.layout_record4).setVisibility(View.VISIBLE);
			tvLabel.setText("情绪详情");
		} else if (mType == 5) {
			findViewById(R.id.layout_record5).setVisibility(View.VISIBLE);
			tvLabel.setText("用药详情");
		} else if (mType == 6) {
			findViewById(R.id.layout_record6).setVisibility(View.VISIBLE);
			tvLabel.setText("血糖监测详情");
		}

	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
		case R.id.btn_record:
			toFragment(RecordCalendarFragment.newInstance(), true, true);
			break;
		case TitleBarView.ID_RIGHT_BUTTON:
			toFragment(RecordCalendarFragment.newInstance(), true, true);
			break;
		case R.id.btn_non_jump:
			toFragment(new AssessFragment(), true);
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
	public boolean onBackPress() {
		FragmentMrg.toBack(getActivity());
		return true;
	}

	class TendencyListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mListItems == null ? 0 : mListItems.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mListItems.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (null == convertView) {
				if (mType == 0 || mType == 1) {
					convertView = View.inflate(getApplicationContext(), R.layout.item_record_table1, null);
				} else if (mType == 2 || mType == 4 || mType == 5 || mType == 6) {
					convertView = View.inflate(getApplicationContext(), R.layout.item_record_table0, null);
				} else if (mType == 3) {
					convertView = View.inflate(getApplicationContext(), R.layout.item_record_table2, null);
				}
			}

			RecordTableInfo info = mListItems.get(position);
			TextView tv = null;
			tv = (TextView) convertView.findViewById(R.id.tv_date);
			tv.setText(info.insertDt.substring(5));
			if (mType == 0) {
				tv = (TextView) convertView.findViewById(R.id.tv_value0);
				tv.setText(info.totalFood + "");
				tv = (TextView) convertView.findViewById(R.id.tv_value1);
				tv.setText(info.sport + "");
				tv = (TextView) convertView.findViewById(R.id.tv_value2);
				tv.setText(info.totalLive + "");
				tv = (TextView) convertView.findViewById(R.id.tv_value3);
				tv.setText(info.mood + "");
				tv = (TextView) convertView.findViewById(R.id.tv_value4);
				tv.setText(info.pill + "");
				tv = (TextView) convertView.findViewById(R.id.tv_value5);
				tv.setText(info.sugarMonitor + "");
				tv = (TextView) convertView.findViewById(R.id.tv_value6);
				tv.setText(info.score + "");

				if (!UserMrg.isTnb()) {
					convertView.findViewById(R.id.tv_value4).setVisibility(View.GONE);
					convertView.findViewById(R.id.tv_value5).setVisibility(View.GONE);
				}

			} else if (mType == 1) {
				tv = (TextView) convertView.findViewById(R.id.tv_value0);
				tv.setText(info.assistFood + "");
				tv = (TextView) convertView.findViewById(R.id.tv_value1);
				tv.setText(info.dinner + "");
				tv = (TextView) convertView.findViewById(R.id.tv_value2);
				tv.setText(info.vagetable + "");
				tv = (TextView) convertView.findViewById(R.id.tv_value3);
				tv.setText(info.water + "");
				tv = (TextView) convertView.findViewById(R.id.tv_value4);
				tv.setText(info.fat + "");
				tv = (TextView) convertView.findViewById(R.id.tv_value5);
				tv.setText(info.salt + "");
				tv = (TextView) convertView.findViewById(R.id.tv_value6);
				tv.setText(info.totalFood + "");

				if (UserMrg.isTnb()) {
					convertView.findViewById(R.id.tv_value3).setVisibility(View.GONE);
				}
			} else if (mType == 2) {
				tv = (TextView) convertView.findViewById(R.id.tv_value0);
				tv.setText(info.sport + "");
				tv = (TextView) convertView.findViewById(R.id.tv_value1);
				tv.setText(info.sport + "");
			} else if (mType == 3) {
				tv = (TextView) convertView.findViewById(R.id.tv_value0);
				tv.setText(info.drink + "");
				tv = (TextView) convertView.findViewById(R.id.tv_value1);
				tv.setText(info.smoke + "");
				tv = (TextView) convertView.findViewById(R.id.tv_value2);
				tv.setText(info.totalLive + "");
			} else if (mType == 4) {
				tv = (TextView) convertView.findViewById(R.id.tv_value0);
				tv.setText(info.mood + "");
				tv = (TextView) convertView.findViewById(R.id.tv_value1);
				tv.setText(info.mood + "");
			} else if (mType == 5) {
				tv = (TextView) convertView.findViewById(R.id.tv_value0);
				tv.setText(info.pill + "");
				tv = (TextView) convertView.findViewById(R.id.tv_value1);
				tv.setText(info.pill + "");
			} else if (mType == 6) {
				tv = (TextView) convertView.findViewById(R.id.tv_value0);
				tv.setText(info.sugarMonitor + "");
				tv = (TextView) convertView.findViewById(R.id.tv_value1);
				tv.setText(info.sugarMonitor + "");
			}

			if (info.status == 0) {
				tv.setTextColor(getResources().getColor(R.color.green));
			} else if (info.status == 1) {
				tv.setTextColor(getResources().getColor(R.color.blue));
			} else {
				tv.setTextColor(getResources().getColor(R.color.red));
			}

			return convertView;
		}
	}
}
