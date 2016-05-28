package com.comvee.tnb.ui.task;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.comvee.FinalDb;
import com.comvee.tnb.R;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.model.TendencyPointInfo;
import com.comvee.util.TimeUtil;

public class CalendarView implements OnItemClickListener, OnClickListener, OnGestureListener {
	private View calendarView;
	private TextView titleTime;
	private ImageView btnTopRight;
	private ImageView drawableTitleLeft, drawableTitleRight;
	private Calendar calToday = Calendar.getInstance();
	private Calendar calSelect = Calendar.getInstance();
	private Calendar calCurrent = Calendar.getInstance();
	private Calendar calStartDay = Calendar.getInstance();
	private List<Boolean> tagList = new ArrayList<Boolean>();
	private ArrayList<Calendar> mCalendars = new ArrayList<Calendar>();
	private CalendarThread mCurThread;
	private Context context;
	private ViewFlipper mViewFilpper;
	private FinalDb db;
	private OnChoiceCalendarListener mListener;
	private int dirType;// 1、左边2、右边
	private List<TendencyPointInfo> sugarHistoryThreeMouth;

	private GestureDetector gestureDetector;
	private GestureGridView mGrid;
	private CalendarAdapter mAdapter;

	public View inflate(Context context_) {
		this.context = context_;
		gestureDetector = new GestureDetector(context, this);
		db = FinalDb.create(context, ConfigParams.DB_NAME);
		calendarView = View.inflate(context, R.layout.layout_calendar1, null);
		titleTime = (TextView) calendarView.findViewById(R.id.title_time);
		btnTopRight = (ImageView) calendarView.findViewById(R.id.btn_top_right);
		btnTopRight.setOnClickListener(this);
		drawableTitleLeft = (ImageView) calendarView.findViewById(R.id.title_drawer_left);
		drawableTitleRight = (ImageView) calendarView.findViewById(R.id.title_drawer_right);
		drawableTitleLeft.setOnClickListener(this);
		drawableTitleRight.setOnClickListener(this);
		mViewFilpper = (ViewFlipper) calendarView.findViewById(R.id.view_filpper);
		readHistoryData();
		toCalendar();
		return calendarView;
	}

	/**
	 * 获取本地血糖记录
	 */
	private void readHistoryData() {
		String sql = null;
		StringBuffer sb = new StringBuffer();
		for (String s : ConfigParams.SUGAR_TIME_CODE) {
			sb.append("code='").append(s).append("'").append(" or ");
		}
		String code = sb.toString().substring(0, sb.lastIndexOf(" or "));
		Calendar calen = Calendar.getInstance();
		calen.add(Calendar.MONTH, -3);
		String day = String.format("date(time)>date('%s')", TimeUtil.fomateTime(calen.getTimeInMillis(), "yyyy-MM-dd HH:mm:ss"));
		sql = String.format("(%s) and %s order by date(time) desc,time desc", code, day);
		sugarHistoryThreeMouth = db.findAllByWhere(TendencyPointInfo.class, sql);
		toCalendar();
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			setShowDate(calCurrent);
			initGridView();
			if (dirType == 2) {
				mViewFilpper.setOutAnimation(context, R.anim.push_left_out);
				mViewFilpper.setInAnimation(context, R.anim.push_left_in);
			} else if (dirType == 1) {
				mViewFilpper.setOutAnimation(context, R.anim.push_right_out);
				mViewFilpper.setInAnimation(context, R.anim.push_right_in);
			} else {
				mViewFilpper.setOutAnimation(null);
				mViewFilpper.setInAnimation(null);
			}

			SoftReference<View> temp = new SoftReference<View>(mGrid);
			mViewFilpper.addView(temp.get());
			mViewFilpper.showNext();
		};
	};

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
				convertView = View.inflate(context, R.layout.list_item_calendar1, null);
				holder = new ViewHolder();
				holder.tag = (ImageView) convertView.findViewById(R.id.list_item_task);
				holder.selectedItemImage = (ImageView) convertView.findViewById(R.id.selected_item);
				holder.todayItemImage = (ImageView) convertView.findViewById(R.id.today_item);
				holder.tv = (TextView) convertView.findViewById(R.id.list_item_textview);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final Calendar calTag = mCalendars.get(position);

			String sDay = calTag.get(Calendar.DAY_OF_MONTH) + "";
			holder.tv.setText(sDay);
			if (!tagList.get(position)) {
				holder.tag.setVisibility(View.GONE);
			} else {
				holder.tag.setVisibility(View.VISIBLE);
			}
			if (TimeUtil.isSameMouth(calTag, calCurrent)) {
				if (TimeUtil.isSameDay(calTag, calToday)) {
					holder.selectedItemImage.setVisibility(View.GONE);
					holder.todayItemImage.setVisibility(View.VISIBLE);
					holder.tv.setTextColor(Color.WHITE);
				} else if (TimeUtil.isSameDay(calTag, calSelect)) {
					holder.todayItemImage.setVisibility(View.GONE);
					holder.selectedItemImage.setVisibility(View.VISIBLE);
					holder.tv.setTextColor(Color.parseColor("#6c7174"));

				} else {
					holder.selectedItemImage.setVisibility(View.GONE);
					holder.todayItemImage.setVisibility(View.GONE);
					holder.tv.setTextColor(Color.parseColor("#6c7174"));
				}
			} else {
				holder.tv.setTextColor(Color.parseColor("#c8cccf"));
			}

			return convertView;
		}

		class ViewHolder {
			ImageView tag;
			ImageView selectedItemImage, todayItemImage;
			TextView tv;
		}

	}

	class CalendarThread extends Thread implements Runnable {
		boolean beContinue = true;

		public void prepare() {
			if (!beContinue) {
				mCurThread = new CalendarThread();
				mCurThread.start();
				try {
					mCurThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
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
				mViewFilpper.setOutAnimation(context, R.anim.push_left_in);
				mViewFilpper.setInAnimation(context, R.anim.push_left_out);
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(calCurrent.getTimeInMillis());
				mCalendars.clear();
				tagList.clear();
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
						tagList.add(isRecord(calTemp));
						calStartDay.add(Calendar.DAY_OF_MONTH, 1);
					}
				}
			} catch (Exception e) {
			}
			mHandler.sendEmptyMessage(1);
			beContinue = false;
		}
	}

	public void setOnChoiceCalendarListener(OnChoiceCalendarListener mListener) {
		this.mListener = mListener;
	}

	private void initGridView() {
		mGrid = new GestureGridView(context, gestureDetector);
		mAdapter = new CalendarAdapter();
		mGrid.setOnItemClickListener(CalendarView.this);
		mGrid.setNumColumns(7);
		mGrid.setSelector(new ColorDrawable(0x000000));
		mGrid.setVerticalSpacing(0);
		mGrid.setAdapter(mAdapter);

	}

	public synchronized void toCalendar() {
		if (mCurThread != null) {
			mCurThread.prepare();
		} else {
			mCurThread = new CalendarThread();
			mCurThread.start();
			try {
				mCurThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public void toSpecifyMonth(Calendar calendar) {
		if (calendar.get(Calendar.MONTH) == calCurrent.get(Calendar.MONTH)) {
			return;
		}
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(calendar.getTimeInMillis());
		calCurrent = c;
		dirType = 0;
		toCalendar();
	}

	/**
	 * @param cal
	 * @return
	 */
	private boolean isRecord(Calendar cal) {
		for (TendencyPointInfo tendencyPointInfo : sugarHistoryThreeMouth) {
			if (tendencyPointInfo.time.contains(TimeUtil.fomateTime(cal.getTimeInMillis(), "yyyy-MM-dd"))) {
				return true;
			}
		}
		return false;
	}

	private Calendar getStartDayOfMouth(Calendar calendar) {
		Calendar calStartDay = Calendar.getInstance();
		calStartDay.setTimeInMillis(calendar.getTimeInMillis());
		calStartDay.set(Calendar.DAY_OF_MONTH, 1);
		int iDayOfWeek = calStartDay.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;// 一周的第一天为周末
		calStartDay.add(Calendar.DAY_OF_MONTH, -iDayOfWeek);
		return calStartDay;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		int curMonth = calSelect.get(Calendar.MONTH);
		if (TimeUtil.isBefore(System.currentTimeMillis(), mCalendars.get(position).getTimeInMillis())) {
			Toast.makeText(context, context.getResources().getString(R.string.txt_date_choose_limit), Toast.LENGTH_SHORT).show();
			return;
		}

		calSelect = mCalendars.get(position);
		if (TimeUtil.isSameMouth(calSelect, calCurrent)) {
			mAdapter.notifyDataSetChanged();
		} else {
			calCurrent.set(Calendar.MONTH, calSelect.get(Calendar.MONTH));
			dirType = curMonth > calSelect.get(Calendar.MONTH) ? 2 : 1;
			toCalendar();
		}
		if (null != mListener) {
			mListener.onItemChoice(calSelect, position, tagList.get(position));
		}
	}

	public void topreCalendar() {
		dirType = 1;
		calCurrent.add(Calendar.MONTH, -1);
		toCalendar();
	}

	public void toNetxtCalendar() {
		dirType = 2;
		calCurrent.add(Calendar.MONTH, 1);
		toCalendar();
	}

	public interface OnChoiceCalendarListener {
		public void onItemChoice(Calendar calendarSelected, int position, boolean isRecord);

		public void onRightClick(Calendar calendar, boolean isRecord);
	}

	public void setShowDate(Calendar calendar) {
		int y = calendar.get(Calendar.YEAR);
		int m = calendar.get(Calendar.MONTH) + 1;
		titleTime.setText(y + "年" + m + "月");

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_drawer_left:
			topreCalendar();
			break;
		case R.id.title_drawer_right:
			toNetxtCalendar();
			break;
		case R.id.btn_top_right:
			if (mListener != null) {
				mListener.onRightClick(calToday, isRecord(calToday));
			}
			calSelect = Calendar.getInstance();
			toSpecifyMonth(calToday);

			break;
		default:
			break;
		}
	}

	public class GestureGridView extends GridView {
		private GestureDetector gestureDetector;

		public GestureGridView(Context context, GestureDetector gestureDetector_) {
			super(context);
			this.gestureDetector = gestureDetector_;
		}

		public GestureGridView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		public boolean dispatchTouchEvent(MotionEvent ev) {
			if (gestureDetector.onTouchEvent(ev))
				return false;
			return super.dispatchTouchEvent(ev);
		}
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if (e2.getX() - e1.getX() > 150) {
			topreCalendar();
		} else if (e2.getX() - e1.getX() < -150) {
			toNetxtCalendar();
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

}
