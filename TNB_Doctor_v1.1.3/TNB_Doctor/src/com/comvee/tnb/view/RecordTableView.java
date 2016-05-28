package com.comvee.tnb.view;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comvee.FinalDb;
import com.comvee.tnb.R;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.model.TendencyInputModel;
import com.comvee.tnb.model.TendencyInputModelItem;
import com.comvee.tnb.model.TendencyPointInfo;
import com.comvee.tnb.ui.record.RecordBmiInputFragment;
import com.comvee.tnb.ui.record.RecordHemoglobinFragment;
import com.comvee.tnb.ui.record.RecordPressBloodInputFragment;
import com.comvee.tnb.ui.record.RecordSugarInputFragment;
import com.comvee.tnb.ui.record.RecordSugarInputNewFrag;
import com.comvee.tnb.ui.record.RecordTableFragment;
import com.comvee.tool.Log;
import com.comvee.tool.ResUtil;
import com.comvee.util.TimeUtil;
import com.comvee.util.Util;

public class RecordTableView extends RelativeLayout {
	private View layoutCalendarNodata;
	private ListView listView;
	private TendencyInputModel mInfo;
	private TendencyListAdapter mAdapter;
	private HashMap<String, TendencyInputModelItem> items;
	private HashMap<String, HashMap<String, TendencyItem>> listItems = new HashMap<String, HashMap<String, TendencyItem>>();
	private ArrayList<String> mDates = new ArrayList<String>();
	private int maxCount, minCount, normolCount;
	private TextView tvMax, tvMin, tvNormol, tvMaxPercent, tvMinPercent, tvNomorlPercent, tvLabel;
	private int dateType;
	private RecordTableFragment mainFragment;

	final String strSugar = ResUtil.getString(R.string.record_sugar);
	final String strHemoglobin = ResUtil.getString(R.string.tendencyadd1_glycosylated_hemoglobin);
	final String strBlood = ResUtil.getString(R.string.record_bloodpress);

	// private LinearLayout linearlayout1;

	public RecordTableView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	public RecordTableView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RecordTableView(Context context) {
		super(context);
	}

	public void setMainFragment(RecordTableFragment main) {
		mainFragment = main;
	}

	private void refreshHeader() {
		int total = maxCount + minCount + normolCount;
		tvMax.setText(maxCount + "次");
		tvMin.setText(minCount + "次");
		tvNormol.setText(normolCount + "次");
		layoutCalendarNodata.setVisibility(total == 0 ? View.VISIBLE : View.GONE);
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

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		init();
	};

	public final void setTendencyTitle(String title) {
		// tvTendencyTitle.setText("记录项目："+title);
	}

	public void setTendencyInputModel(TendencyInputModel info) {
		this.mInfo = info;
	}

	public void setTendencyInputModelItem(ArrayList<TendencyInputModelItem> items) {
		this.items = new HashMap<String, TendencyInputModelItem>();
		if (mInfo.label.equals("BMI")) {
			TendencyInputModelItem item = new TendencyInputModelItem();
			item.code = "bmi";
			item.maxValue = 24f;
			item.minValue = 18.5f;
			this.items.put(item.code, item);
		}
		for (TendencyInputModelItem item : items) {
			this.items.put(item.code, item);
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
			paint.setColor(getResources().getColor(R.color.red));
			RectF rect = new RectF(0, 0, width, width);
			cv.drawArc(rect, 0, maxPercent / 100f * 360, true, paint);
			paint.setColor(getResources().getColor(R.color.green));
			cv.drawArc(rect, maxPercent / 100f * 360, normolPercent / 100f * 360, true, paint);
			paint.setColor(getResources().getColor(R.color.blue));
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

	public void init() {

		LayoutInflater.from(getContext()).inflate(R.layout.fragment_tendency_list, this, true);
		layoutCalendarNodata = findViewById(R.id.layout_calendar_nodata);

		listView = (ListView) findViewById(R.id.list_view);
		listView.setPadding(0, 0, 0, 0);
		listView.setDivider(null);
		mAdapter = new TendencyListAdapter();
		listView.addHeaderView(View.inflate(getContext(), R.layout.tendency_table_header, null));
		listView.setAdapter(mAdapter);
		tvMax = (TextView) findViewById(R.id.tv_max_count);
		tvMin = (TextView) findViewById(R.id.tv_min_count);
		tvNormol = (TextView) findViewById(R.id.tv_normol_count);
		tvMaxPercent = (TextView) findViewById(R.id.tv_max_percent);
		tvMinPercent = (TextView) findViewById(R.id.tv_min_percent);
		tvNomorlPercent = (TextView) findViewById(R.id.tv_normol_percent);
		tvLabel = (TextView) findViewById(R.id.tv_label);
		// linearlayout1 = (LinearLayout) findViewById(R.id.record_modify);
		// linearlayout1.setOnClickListener(this);

		// ===============================先写死

		if (strSugar.equalsIgnoreCase(mInfo.label)) {
			tvLabel.setText("血糖详情（mmol/L）");
			findViewById(R.id.layout_xuetang).setVisibility(View.VISIBLE);
		} else if (strBlood.equalsIgnoreCase(mInfo.label)) {
			tvLabel.setText("收缩压/舒张压（mmHg）");
			findViewById(R.id.layout_xueya).setVisibility(View.VISIBLE);
		} else if ("BMI".equalsIgnoreCase(mInfo.label)) {
			findViewById(R.id.layout_bmi).setVisibility(View.VISIBLE);
			tvLabel.setText("身高（cm）/体重（kg）/BMI");
		} else {
			findViewById(R.id.layout_hemoglobin).setVisibility(View.VISIBLE);
			tvLabel.setText(strHemoglobin);
		}

	}

	public void updateData(String label, int date) {
		dateType = date;
		maxCount = 0;
		minCount = 0;
		normolCount = 0;
		mDates.clear();
		listItems.clear();
		loadData(date);
		mAdapter.notifyDataSetChanged();
	}

	public void loadData(int dateType) {
		FinalDb db = FinalDb.create(getContext(), ConfigParams.DB_NAME);

		String day = null;

		if (dateType == 0) {// 天（精确到小时）
			day = "date(time)=date('now')";
		} else if (dateType == 1) {// 周（精确到天）
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -7);
			String time = TimeUtil.fomateTime(cal.getTimeInMillis(), "yyyy-MM-dd HH:mm:ss");
			day = String.format("date(time)>date('%s')", time);
		} else if (dateType == 2) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -31);
			String time = TimeUtil.fomateTime(cal.getTimeInMillis(), "yyyy-MM-dd HH:mm:ss");
			day = String.format("date(time)>date('%s')", time);
		} else if (dateType == 3) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -91);
			String time = TimeUtil.fomateTime(cal.getTimeInMillis(), "yyyy-MM-dd HH:mm:ss");
			day = String.format("date(time)>date('%s')", time);
		}

		String sql = null;
		StringBuffer sb = new StringBuffer();
		for (TendencyInputModelItem item : items.values()) {
			sb.append("code='").append(item.code).append("'").append(" or ");
		}
		int end = sb.lastIndexOf(" or ");
		if (end == -1) {
			return;
		}

		String code = sb.toString().substring(0, end);
		if (dateType == 0) {
			sql = String.format("(%s) and %s order by time desc,insertDt desc", code, day);
		} else {
			sql = String.format("(%s) and %s order by date(time) desc,insertDt desc", code, day);
		}
		List<TendencyPointInfo> infos1 = db.findAllByWhere(TendencyPointInfo.class, sql);
		for (TendencyPointInfo info : infos1) {
			if (dateType != 0) {
				try {
					info.time = TimeUtil.fomateTime("yyyy-MM-dd HH:mm", ConfigParams.DATE_FORMAT, info.time);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}

			HashMap<String, TendencyItem> list = null;
			if (listItems.containsKey(info.time)) {
				list = listItems.get(info.time);
			} else {
				list = new HashMap<String, TendencyItem>();
				listItems.put(info.time, list);
				mDates.add(info.time);
			}
			TendencyItem item = new TendencyItem();
			TendencyInputModelItem model = items.get(info.code);
			item.value = info.value + "";
			item.id = info.id;
			if (info.code.equalsIgnoreCase("bmi") || mInfo.label.equalsIgnoreCase(strSugar)) {
				item.value = String.format("%.1f", Float.valueOf(item.value));
			}
			item.time = info.time;
			item.type = info.type;
			item.codeNmae = model.code;
			if (!list.containsValue(item)) {

				// boolean isTnb = UserMrg.isTnb();
				if (strSugar.equals(mInfo.label)) {

					if (info.bloodGlucoseStatus == 1 || info.bloodGlucoseStatus == 2) {
						item.beLimit = 1;
					} else if (info.bloodGlucoseStatus == 4 || info.bloodGlucoseStatus == 5) {
						item.beLimit = 2;
					} else {
						item.beLimit = 0;
					}

					// System.out.println(item.codeNmae + "    值：" + item.value
					// + "   status:" + info.bloodGlucoseStatus + "   limit:" +
					// item.beLimit);

				} else if (!info.code.equals("height") && !info.code.equals("weight")) {
					float max = model.maxValue;
					float min = model.minValue;

					if (info.value < min) {
						item.beLimit = 1;
					} else if (info.value > max) {
						item.beLimit = 2;
					} else {
						item.beLimit = 0;
					}
				}
				list.put(info.code, item);
			}

		}

		/******** 求偏低偏高值 ********/
		for (HashMap<String, TendencyItem> map : listItems.values()) {
			if ("bmi".equalsIgnoreCase(mInfo.label)) {
				if(map.get("bmi")==null){
					return;
				}
				checklimit(map.get("bmi"));
			} else if (strHemoglobin.equalsIgnoreCase(mInfo.label)) {
				if(map.get("hemoglobin")==null){
					return;
				}
				checklimit(map.get("hemoglobin"));
			} else if (strSugar.equals(mInfo.label)) {
				for (TendencyItem item : map.values()) {
					checklimit(item);
				}
			} else if (strBlood.equals(mInfo.label)) {
				checkBloodLimit(map);
				// for (TendencyItem item : map.values()) {
				// checklimit(item);
				// }
			}

		}

		refreshHeader();
	}

	private void checkBloodLimit(HashMap<String, TendencyItem> map) {
		if(map.get("bloodpressuresystolic")==null||map.get("bloodpressurediastolic")==null){
			return;
		}
		TendencyItem infoMax = map.get("bloodpressuresystolic");
		TendencyItem infoMin = map.get("bloodpressurediastolic");

		if (infoMax.beLimit == 2 || infoMin.beLimit == 2) {
			maxCount++;
		} else if (infoMax.beLimit == 1 || infoMin.beLimit == 1) {
			minCount++;
		} else {
			normolCount++;
		}
	}

	private void checklimit(TendencyItem item) {
		if (item.beLimit == 1) {
			minCount++;
		} else if (item.beLimit == 2) {
			maxCount++;
		} else {
			normolCount++;
		}
	}

	class TendencyItem {
		String id;
		String value = "";
		String time;
		int type;
		String codeNmae;
		int beLimit;// 0、正常1、偏低2、偏高

		@Override
		public boolean equals(Object o) {
			return ((TendencyItem) o).codeNmae.equals(codeNmae) && ((TendencyItem) o).time.equals(time);
		}
	}

	class TendencyListAdapter extends BaseAdapter implements OnClickListener {

		@Override
		public int getCount() {
			return mDates.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mDates.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		private void setTextView(TextView tv, String code, HashMap<String, TendencyItem> map) {
			setTextView(tv, code, map, 0);
		}

		private void setTextView(TextView tv, String code, HashMap<String, TendencyItem> map, int color) {
			if (map == null) {
				tv.setText("--");
				return;
			}

			TendencyItem info = map.get(code);
			if ("hemoglobin".equals(code)) {
				info.value = info.value + "%";
			}
			if (info == null) {
				tv.setText("");
				tv.setOnClickListener(null);
			} else {
				tv.setText(info.value + "");
				tv.setTag(info.time + " " + info.codeNmae);
				if (strSugar.equalsIgnoreCase(mInfo.label)) {
					tv.setOnClickListener(this);
				} else {
					tv.setTag(info.time);
					tv.setOnClickListener(this);
				}
				if (color != 0) {
					tv.setTextColor(color);
				} else {// 如果没有设置颜色，就根据最高最低值范围来设置颜色
					if (info.beLimit == 0) {
						tv.setTextColor(getResources().getColor(R.color.new_green));
					} else if (info.beLimit == 1) {
						tv.setTextColor(getResources().getColor(R.color.new_blue));
					} else {
						tv.setTextColor(getResources().getColor(R.color.new_red));
					}
				}
			}

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (null == convertView) {
				Log.e(mInfo.label);
				if (strSugar.equalsIgnoreCase(mInfo.label)) {
					convertView = View.inflate(getContext(), R.layout.item_tendency_table, null);
				} else if (strBlood.equalsIgnoreCase(mInfo.label)) {
					convertView = View.inflate(getContext(), R.layout.item_tendency_table_xueya, null);
				} else if ("BMI".equalsIgnoreCase(mInfo.label)) {
					convertView = View.inflate(getContext(), R.layout.item_tendency_table_bmi, null);
				} else {
					convertView = View.inflate(getContext(), R.layout.item_tendency_table_hemoglobin, null);
				}
			}
			View short_line = convertView.findViewById(R.id.record_line_short);
			View long_line = convertView.findViewById(R.id.record_line_long);
			if (position == mDates.size() - 1) {
				short_line.setVisibility(View.GONE);
				long_line.setVisibility(View.VISIBLE);
			} else {
				short_line.setVisibility(View.VISIBLE);
				long_line.setVisibility(View.GONE);
			}
			TextView tv = null;
			String date = mDates.get(position);
			HashMap<String, TendencyItem> map = listItems.get(date);
			tv = (TextView) convertView.findViewById(R.id.tv_date);
			try {
				if (dateType != 0) {
					tv.setText(TimeUtil.fomateTime(ConfigParams.DATE_FORMAT, "MM-dd", date));
				} else {
					// tv.setText(date);
					tv.setText(TimeUtil.fomateTime(ConfigParams.TIME_FORMAT1, "dd日HH:mm", date));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (strSugar.equals(mInfo.label)) {
				tv = (TextView) convertView.findViewById(R.id.tv_value);
				setTextView(tv, "beforedawn", map);
				tv = (TextView) convertView.findViewById(R.id.tv_value_0);
				setTextView(tv, "beforeBreakfast", map);
				// tv.setOnClickListener(this);
				tv = (TextView) convertView.findViewById(R.id.tv_value_1);
				setTextView(tv, "afterBreakfast", map);
				// tv.setOnClickListener(this);
				tv = (TextView) convertView.findViewById(R.id.tv_value_2);
				setTextView(tv, "beforeLunch", map);
				// tv.setOnClickListener(this);
				tv = (TextView) convertView.findViewById(R.id.tv_value_3);
				setTextView(tv, "afterLunch", map);
				// tv.setOnClickListener(this);
				tv = (TextView) convertView.findViewById(R.id.tv_value_4);
				setTextView(tv, "beforeDinner", map);
				// tv.setOnClickListener(this);
				tv = (TextView) convertView.findViewById(R.id.tv_value_5);
				setTextView(tv, "afterDinner", map);
				// tv.setOnClickListener(this);
				tv = (TextView) convertView.findViewById(R.id.tv_value_6);
				setTextView(tv, "beforeSleep", map);
				// tv.setOnClickListener(this);
			} else if (strBlood.equals(mInfo.label)) {
				tv = (TextView) convertView.findViewById(R.id.tv_ssy);
				setTextView(tv, "bloodpressuresystolic", map);
				tv = (TextView) convertView.findViewById(R.id.tv_szy);
				setTextView(tv, "bloodpressurediastolic", map);
			} else if ("BMI".equalsIgnoreCase(mInfo.label)) {
				tv = (TextView) convertView.findViewById(R.id.tv_value2);
				setTextView(tv, "bmi", map);
				tv = (TextView) convertView.findViewById(R.id.tv_value0);
				setTextView(tv, "height", map, getResources().getColor(R.color.green));
				tv = (TextView) convertView.findViewById(R.id.tv_value1);
				setTextView(tv, "weight", map, getResources().getColor(R.color.green));
			} else if (strHemoglobin.equalsIgnoreCase(mInfo.label)) {
				tv = (TextView) convertView.findViewById(R.id.tv_value0);
				setTextView(tv, "hemoglobin", map);
			}

			return convertView;
		}

		@Override
		public void onClick(View arg0) {
			if (strSugar.equalsIgnoreCase(mInfo.label)) {
				String time = arg0.getTag().toString().split(" ")[0];
				String code = arg0.getTag().toString().split(" ")[1];
				RecordSugarInputNewFrag.toFragment(mainFragment.getActivity(),time,code);
//				mainFragment.toFragment(RecordSugarInputFragment.newInstance("记录修改", arg0.getTag().toString()), true, true);
			} else if (strBlood.equalsIgnoreCase(mInfo.label)) {
				mainFragment.toFragment(RecordPressBloodInputFragment.newInstance(arg0.getTag().toString()), true, true);
			} else if (strHemoglobin.equalsIgnoreCase(mInfo.label)) {
				mainFragment.toFragment(RecordHemoglobinFragment.newInstance(arg0.getTag().toString()), true, true);
			} else if ("bmi".equalsIgnoreCase(mInfo.label)) {
				mainFragment.toFragment(RecordBmiInputFragment.newInstance(arg0.getTag().toString()), true, true);
			}
		}
	}

}
