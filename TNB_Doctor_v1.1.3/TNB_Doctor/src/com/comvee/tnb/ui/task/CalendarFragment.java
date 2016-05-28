package com.comvee.tnb.ui.task;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.comvee.FinalDb;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.model.RecordInfo;
import com.comvee.util.TimeUtil;

@SuppressLint("ValidFragment")
public class CalendarFragment extends BaseFragment implements View.OnClickListener, OnItemClickListener {

	private Calendar calToday;
	private Calendar calStartDay;
	private Calendar calCurrent;
	private Calendar calSelect;
	private ArrayList<Calendar> mCalendars = new ArrayList<Calendar>();
	private ArrayList<Boolean> mHashTasks = new ArrayList<Boolean>();
	private GestureDetector gesture;
	private CalendarAdapter mAdapter;
	private GridView mGrid;
	private OnChoiceCalendarListener mListener;
	private TextView tvTime;
	private ViewFlipper mViewFilpper;
	private int dirType;// 1、左边2、右边
	private int mType;// 0、任务1、日志
	private FinalDb db;
	private CalendarThread mCurThread;
	private boolean isNeedInitCalendar = true;

	public CalendarFragment() {
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mRoot.setVisibility(View.VISIBLE);
			setShowDate(calCurrent);

			mAdapter = new CalendarAdapter();
			mGrid = new GridView(getApplicationContext());
			mGrid.setAdapter(mAdapter);
			mGrid.setOnItemClickListener(CalendarFragment.this);
			mGrid.setNumColumns(7);
			mGrid.setVerticalSpacing(0);

			if (dirType == 1) {
				mViewFilpper.setOutAnimation(getApplicationContext(), R.anim.push_left_out);
				mViewFilpper.setInAnimation(getApplicationContext(), R.anim.push_left_in);
			} else if (dirType == 2) {
				mViewFilpper.setOutAnimation(getApplicationContext(), R.anim.push_right_out);
				mViewFilpper.setInAnimation(getApplicationContext(), R.anim.push_right_in);
			} else {
				mViewFilpper.setOutAnimation(null);
				mViewFilpper.setInAnimation(null);
			}

			SoftReference<View> temp = new SoftReference<View>(mGrid);
			mViewFilpper.addView(temp.get());
			mViewFilpper.showNext();
		};
	};

	public void setNeedInitCalendar(boolean isNeedInitCalendar) {
		this.isNeedInitCalendar = isNeedInitCalendar;
	}

	private GestureDetector.SimpleOnGestureListener listener = new SimpleOnGestureListener() {

		public boolean onDown(MotionEvent e) {
			return true;
		};
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

			if (e2.getX() - e1.getX() > 150) {
				// calCurrent.add(Calendar.MONTH, -1);
				// toCalendar(calCurrent);
				toPreCalendar();
				return true;
			} else if (e2.getX() - e1.getX() < -150) {
				// calCurrent.add(Calendar.MONTH, 1);
				// toCalendar(calCurrent);
				toNextCalendar();
				return true;
			}
			return false;
		};

	};

	private CalendarFragment(int type) {
		this.mType = type;
	}

	public static CalendarFragment newInstance(int type) {
		CalendarFragment fragment = new CalendarFragment(type);
		return fragment;
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.layout_calendar;
	}

	@Override
	public void onStart() {
		super.onStart();
		db = FinalDb.create(getApplicationContext(), ConfigParams.DB_NAME);
		initCalendarUI();
	}

	public void setOnChoiceCalendarListener(OnChoiceCalendarListener mListener) {
		this.mListener = mListener;
	}

	@SuppressWarnings("deprecation")
	private void initCalendarUI() {

		mViewFilpper = (ViewFlipper) findViewById(R.id.view_filpper);
		mRoot.setVisibility(View.GONE);
		final View btnLeft = findViewById(R.id.btn_calendar_left);
		final View btnRight = findViewById(R.id.btn_calendar_right);

		btnRight.setOnClickListener(this);
		btnLeft.setOnClickListener(this);

		tvTime = (TextView) findViewById(R.id.tv_value);
		mAdapter = new CalendarAdapter();

		initCalendar();

		gesture = new GestureDetector(listener);

		mViewFilpper.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (gesture.onTouchEvent(event)) {
					return false;
				} else {
					return false;
				}
			}
		});

		// mGrid.setOnItemClickListener(this);

	}

	private void initCalendar() {

		calToday = Calendar.getInstance();

		calCurrent = Calendar.getInstance();

		calSelect = Calendar.getInstance();

		if (isNeedInitCalendar)
			toCalendar(calCurrent);

	}

	private boolean isHashRecord(Calendar cal) {
		String time = TimeUtil.fomateTime(cal.getTimeInMillis(), ConfigParams.DATE_FORMAT);
		List<RecordInfo> infos = db.findAllByWhere(RecordInfo.class, String.format("insertDt='%s'", time));
		if (infos == null || infos.isEmpty()) {
			return false;
		}
		return true;
	}

	/**
	 * 切换到cal所在的月的日历页面
	 * 
	 * @param cal
	 */
	public void toCalendar(Calendar cal) {
		calCurrent = cal;

		if (mCurThread != null) {
			mCurThread.prepare();
		} else {
			mCurThread = new CalendarThread();
			mCurThread.start();
		}

	}

	private void setShowDate(Calendar calendar) {

		if (null != mListener) {
			mListener.onMonthChange(this, calendar);
		}

		int y = calendar.get(Calendar.YEAR);
		int m = calendar.get(Calendar.MONTH) + 1;
		//
		// TextView tv = (TextView) findViewById(R.id.txt_curdate);
		tvTime.setText(y + "年" + m + "月");

	}

	private Calendar getStartDayOfMouth(Calendar calendar) {

		Calendar calStartDay = Calendar.getInstance();
		calStartDay.setTimeInMillis(calendar.getTimeInMillis());

		calStartDay.set(Calendar.DAY_OF_MONTH, 1);

		int iDayOfWeek = calStartDay.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;// 一周的第一天为周末

		calStartDay.add(Calendar.DAY_OF_MONTH, -iDayOfWeek);

		return calStartDay;
	}

	public Calendar getCalendar() {
		return calCurrent;
	}

	private void toNextCalendar() {
		dirType = 1;
		calCurrent.add(Calendar.MONTH, 1);
		toCalendar(calCurrent);
	}

	private void toPreCalendar() {
		dirType = 2;
		calCurrent.add(Calendar.MONTH, -1);
		toCalendar(calCurrent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_calendar_left:
			toPreCalendar();
			break;
		case R.id.btn_calendar_right:
			toNextCalendar();
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

		int curMonth = calSelect.get(Calendar.MONTH);

		if (mType == 1 && TimeUtil.isBefore(System.currentTimeMillis(), mCalendars.get(position).getTimeInMillis())) {
			showToast(getContext().getResources().getString(R.string.txt_date_choose_limit));
			return;
		}

		calSelect = mCalendars.get(position);
		if (TimeUtil.isSameMouth(calSelect, calCurrent)) {
			mAdapter.notifyDataSetChanged();
		} else {
			calCurrent.set(Calendar.MONTH, calSelect.get(Calendar.MONTH));
			dirType = curMonth > calSelect.get(Calendar.MONTH) ? 2 : 1;
			toCalendar(calCurrent);
		}
		if (null != mListener) {
			mListener.onItemChoice(CalendarFragment.this, position, calSelect);
		}
	}

	public void update() {
		if (null != calCurrent) {
			toCalendar(calCurrent);
		}
	}

	@Override
	public boolean onBackPress() {
		return false;
	}

	class CalendarAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mCalendars.size();
		}

		@Override
		public Object getItem(int position) {
			return mCalendars.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;

			if (null == convertView) {
				convertView = View.inflate(getApplicationContext(), R.layout.list_item_calendar, null);
				holder = new ViewHolder();
				holder.tag = (ImageView) convertView.findViewById(R.id.list_item_task);
				holder.tv = (TextView) convertView.findViewById(R.id.list_item_textview);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final Calendar calTag = mCalendars.get(position);

			String sDay = calTag.get(Calendar.DAY_OF_MONTH) + "";
			holder.tv.setText(sDay);
			if (!mHashTasks.get(position)) {
				holder.tag.setVisibility(View.GONE);
			} else {
				holder.tag.setVisibility(View.VISIBLE);
			}
			if (TimeUtil.isSameMouth(calTag, calCurrent)) {

				if (TimeUtil.isSameDay(calTag, calSelect)) {
					convertView.setBackgroundResource(R.drawable.bg_calendar_item_selected);
					holder.tv.setTextColor(Color.WHITE);
				} else if (TimeUtil.isSameDay(calTag, calToday)) {
					convertView.setBackgroundResource(R.drawable.bg_calendar_item_today);
					holder.tv.setTextColor(Color.parseColor("#6c7174"));
				} else {
					convertView.setBackgroundResource(R.drawable.bg_calendar_item);
					holder.tv.setTextColor(Color.parseColor("#6c7174"));
				}

			} else {
				holder.tv.setTextColor(Color.parseColor("#c8cccf"));
				convertView.setBackgroundResource(R.drawable.bg_calendar_item);
			}

			return convertView;
		}

		class ViewHolder {
			ImageView tag;
			TextView tv;
		}

	}

	public interface OnChoiceCalendarListener {
		public void onItemChoice(CalendarFragment frag, int position, Calendar cal);

		public void onMonthChange(CalendarFragment frag, Calendar cal);

		public boolean isHashTask(CalendarFragment frag, Calendar cal);
	}

	class CalendarThread extends Thread implements Runnable {

		boolean beContinue = true;

		public void prepare() {
			if (!beContinue) {
				mCurThread = new CalendarThread();
				mCurThread.start();
			}
			interrupt();
			beContinue = false;
		}

		@Override
		public void run() {
			if (mViewFilpper == null) {
				return;
			}
			try {
				mViewFilpper.setOutAnimation(getApplicationContext(), R.anim.push_left_in);
				mViewFilpper.setInAnimation(getApplicationContext(), R.anim.push_left_out);

				Calendar c = Calendar.getInstance();

				c.setTimeInMillis(calCurrent.getTimeInMillis());

				mCalendars.clear();
				mHashTasks.clear();

				calStartDay = getStartDayOfMouth(c);// 得到当月的第一天

				for (int i = 0; i < 6; i++) {
					for (int j = 0; j < 7; j++) {
						if (!beContinue) {
							mCurThread = new CalendarThread();
							mCurThread.start();
							return;
						}
						Calendar calTemp = Calendar.getInstance();
						calTemp.setTimeInMillis(calStartDay.getTimeInMillis());
						mCalendars.add(calTemp);
						if (mHashTasks != null) {
							if (mType == 0) {
								mHashTasks.add(mListener.isHashTask(CalendarFragment.this, calTemp));
								// mHashTasks.add(!RemindMrg.getInstance(getApplicationContext()).getAlarmsByCalendar(calTemp).isEmpty());
							} else if (mType == 1) {
								mHashTasks.add(isHashRecord(calTemp));
							}
						}
						calStartDay.add(Calendar.DAY_OF_MONTH, 1);
					}
				}
			} catch (Exception e) {
			}
			mHandler.sendEmptyMessage(1);
			beContinue = false;
		}

	}

}
