package com.tencent.sugardoctor.widget.wheelview;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.comvee.tnb.R;

public class StringWheelAdapter extends BaseAdapter implements TosAdapterView.OnItemSelectedListener {

	private int textSizeLeve1 = 40;
	private int textSizeLeve2 = 29;
	private int textSizeLeve3 = 20;

	private int colorGray = Color.parseColor("#bdbfbe");
	private int colorBlack = Color.parseColor("#737473");
	private int seletedIndex = -1;

	/** The default min value */
	public static final int DEFAULT_MAX_VALUE = 9;

	/** The default max value */
	public static final int DEFAULT_MIN_VALUE = 0;

	public ArrayList<String> list;

	private int textSize;
	private Context mContext;

	public StringWheelAdapter(Context cxt, ArrayList<String> list, int textSize) {
		mContext = cxt;
		this.list = list;
		this.textSize = textSize;
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public String getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView textView = null;

		convertView = View.inflate(mContext, R.layout.fragment_guides_health_num_item, null);

		textView = (TextView) convertView.findViewById(R.id.tv_text);
		textView.setTextSize(textSize);
		textView.setGravity(Gravity.CENTER);
		textView.getPaint().setFakeBoldText(true);

		String text = getItem(position);

		if (position == seletedIndex - 2) {
			textView.setTextSize(textSizeLeve3);
			textView.setTextColor(colorGray);
			// mHandler.sendMessageDelayed(mHandler.obtainMessage(0, 0, 0,
			// textView), 1000);
		} else if (position == seletedIndex - 1) {
			textView.setTextSize(textSizeLeve2);
			textView.setTextColor(colorBlack);
			// mHandler.sendMessageDelayed(mHandler.obtainMessage(0, 100, 0,
			// convertView), 1000);
		} else if (position == seletedIndex) {
			textView.setTextSize(textSizeLeve1);
			textView.setTextColor(mContext.getResources().getColor(R.color.theme_color_green));
			// mHandler.sendMessageDelayed(mHandler.obtainMessage(0, 200, 0,
			// textView), 1000);
		} else if (position == seletedIndex + 1) {
			textView.setTextSize(textSizeLeve2);
			textView.setTextColor(colorBlack);
			// mHandler.sendMessageDelayed(mHandler.obtainMessage(0, 300, 0,
			// textView), 1000);
		} else if (position == seletedIndex + 2) {
			textView.setTextSize(textSizeLeve3);
			textView.setTextColor(colorGray);
			// mHandler.sendMessageDelayed(mHandler.obtainMessage(0, 400, 0,
			// textView), 1000);
		}

		textView.setText(text);
		return convertView;
	}

	// private Handler mHandler = new Handler() {
	// public void handleMessage(android.os.Message msg) {
	// Animation one = AnimationUtils.loadAnimation(mContext,
	// R.anim.fragment_slide_in_top);
	// one.setStartOffset(msg.arg1);
	// ((View) msg.obj).startAnimation(one);
	// };
	// };

	/**
	 * @author valexhuang
	 * @return the seletedIndex
	 */
	public int getSeletedIndex() {
		return seletedIndex;
	}

	/**
	 * @author valexhuang
	 * @param seletedIndex
	 *            the seletedIndex to set
	 */
	public void setSeletedIndex(int seletedIndex) {
		this.seletedIndex = seletedIndex;
	}

	@Override
	public void onItemSelected(TosAdapterView<?> parent, View view, int position, long id) {

		int selectColor = mContext.getResources().getColor(R.color.theme_color_green);
		setSeletedIndex(position);
		TextView text = (TextView) view.findViewById(R.id.tv_text);
		text.setTextSize(textSizeLeve1);
		text.setTextColor(selectColor);
		int index = Integer.parseInt(view.getTag().toString());
		if (parent.getChildAt(index + 1) != null) {
			text = (TextView) parent.getChildAt(index + 1).findViewById(R.id.tv_text);
			text.setBackgroundColor(Color.TRANSPARENT);
			text.setTextColor(colorBlack);
			text.setTextSize(textSizeLeve2);

		}

		if (parent.getChildAt(index + 2) != null) {
			text = (TextView) parent.getChildAt(index + 2).findViewById(R.id.tv_text);
			text.setBackgroundColor(Color.TRANSPARENT);
			text.setTextColor(colorGray);
			text.setTextSize(textSizeLeve3);

		}

		if (parent.getChildAt(index - 1) != null) {
			text = (TextView) parent.getChildAt(index - 1).findViewById(R.id.tv_text);
			text.setBackgroundColor(Color.TRANSPARENT);
			text.setTextColor(colorBlack);
			text.setTextSize(textSizeLeve2);

		}

		if (parent.getChildAt(index - 2) != null) {
			text = (TextView) parent.getChildAt(index - 2).findViewById(R.id.tv_text);
			text.setBackgroundColor(Color.TRANSPARENT);
			text.setTextColor(colorGray);
			text.setTextSize(textSizeLeve3);

		}

	}

	@Override
	public void onNothingSelected(TosAdapterView<?> parent) {

	}
}
