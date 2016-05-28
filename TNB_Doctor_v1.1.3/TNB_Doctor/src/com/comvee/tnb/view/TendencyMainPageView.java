package com.comvee.tnb.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comvee.FinalDb;
import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.model.TendencyInputModel;
import com.comvee.tnb.model.TendencyInputModelItem;
import com.comvee.tnb.model.TendencyPointInfo;
import com.comvee.tnb.widget.Tendency;
import com.comvee.tool.TendencyLineFactory;
import com.comvee.tool.TendencyUtil;
import com.comvee.tool.UserMrg;
import com.comvee.util.TimeUtil;

public class TendencyMainPageView extends RelativeLayout implements Runnable {

	private TextView tvTendencyTitle;
	private TendencyInputModel mInfo;
	private ArrayList<TendencyInputModelItem> items;
	private Tendency tendency;
	private int layoutId = R.layout.fragemnt_tendency_main_page;
	private int mDayType;// 0、天1、周2、月3、三月

	private Handler mHnalder = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case 4:
				tendency.show();
				break;
			default:
				break;
			}

		};
	};

	public TendencyMainPageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public TendencyMainPageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TendencyMainPageView(Context context) {
		super(context);
	}

	public void setLayoutId(int layoutId) {
		this.layoutId = layoutId;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		init();
	};

	public final void setTendencyTitle(String title) {
		tvTendencyTitle.setText(title);
	}

	public void setTendencyInputModel(TendencyInputModel info) {
		this.mInfo = info;
	}

	public void setTendencyInputModelItem(ArrayList<TendencyInputModelItem> items) {
		this.items = items;
	}

	public void init() {

		LayoutInflater.from(getContext()).inflate(layoutId, this, true);

		tvTendencyTitle = (TextView) findViewById(R.id.tv_tendency_title);

		setTendencyTitle(mInfo.label);
		tendency = (Tendency) findViewById(R.id.tendency);

	}

	public void updateData(int date, int lineCount) {
		mDayType = date;
		tendency.clear();
		new Thread(this).start();
	}

	public static List<TendencyPointInfo> getInfos(int dateType, String code) {
		FinalDb db = FinalDb.create(TNBApplication.getInstance(), ConfigParams.DB_NAME);

		String day = null;

		if (dateType == 0) {// 当天（精确到小时）
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
		if (dateType == 0) {// 天
			sql = String.format("code='%s' and %s order by time desc,insertDt desc", code, day);
		} else {
			sql = String.format("code='%s' and %s order by date(time) desc,insertDt desc", code, day);
		}

		List<TendencyPointInfo> infos1 = db.findAllByWhere(TendencyPointInfo.class, sql);

		for (TendencyPointInfo info : infos1) {
		}
		return infos1;
	}

	private TendencyLineFactory<TendencyPointInfo> mPointlineFactory = new TendencyLineFactory<TendencyPointInfo>() {

		@Override
		public String getTime(TendencyPointInfo obj) {
			try {
				if (mDayType == 0) {
					return TimeUtil.fomateTime("yyyy-MM-dd HH:mm", "HH:00", obj.getTime());
				} else {
					return TimeUtil.fomateTime("yyyy-MM-dd HH:mm", "MM/dd", obj.getTime());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public float getValue(TendencyPointInfo obj) {
			return obj.getValue();
		}

	};

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		boolean isTnb = UserMrg.isTnb();
		List<TendencyPointInfo> infos = null;
		float max = 0;
		float min = 0;
		if (mInfo.label.equals("糖化血红蛋白")) {
			infos = getInfos(mDayType, "hemoglobin");
			min = 4.0f;
			max = 7.0f;
			TendencyUtil.createLines(mDayType, tendency, infos, mPointlineFactory, max, min);
		} else if (mInfo.label.equalsIgnoreCase("BMI")) {
			infos = getInfos(mDayType, "bmi");
			min = 18.5f;
			max = 24f;
			TendencyUtil.createLines(mDayType, tendency, infos, mPointlineFactory, max, min);

		} else if (mInfo.label.equals("血压")) {
			TendencyInputModelItem item1 = items.get(0);
			TendencyInputModelItem item2 = items.get(1);
			List<TendencyPointInfo> infos1 = getInfos(mDayType, item1.code);
			List<TendencyPointInfo> infos2 = getInfos(mDayType, item2.code);
			TendencyUtil.createLines(mDayType, tendency, mPointlineFactory, new float[] { item1.maxValue, item2.maxValue }, new float[] {
					item1.minValue, item2.minValue }, infos1, infos2);
		} else {
			TendencyInputModelItem item = items.get(0);
			String code = item.code;
			infos = getInfos(mDayType, code);
			if (code.equalsIgnoreCase("beforeBreakfast")) {
				item.minValue = isTnb ? ConfigParams.MIN_beforeBreakfast_ISTNB : ConfigParams.MIN_beforeBreakfast_NOTNB;
				item.maxValue = isTnb ? ConfigParams.MAX_beforeBreakfast_ISTNB : ConfigParams.MAX_beforeBreakfast_NOTNB;
			} else {
				item.minValue = isTnb ? ConfigParams.MIN_NObeforeBreakfast_ISTNB : ConfigParams.MIN_NObeforeBreakfast_NOTNB;
				item.maxValue = isTnb ? ConfigParams.MAX_NObeforeBreakfast_ISTNB : ConfigParams.MAX_NObeforeBreakfast_NOTNB;
			}
			min = item.minValue;
			max = item.maxValue;
			TendencyUtil.createLines(mDayType, tendency, infos, mPointlineFactory, max, min);

		}
		mHnalder.sendEmptyMessage(4);

	}
}
