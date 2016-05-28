package com.comvee.tnb.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.model.FollowQuestionDetailed;

/**
 * 随访问卷适配器
 * 
 * @author SZM
 * 
 */
public class FollowQDetailedAdapter extends BaseAdapter {

	private List<FollowQuestionDetailed> list = new ArrayList<FollowQuestionDetailed>();
	public boolean save;
	private int position = -1;

	private LayoutInflater mInflater;

	public FollowQDetailedAdapter(Context ctx) {
		mInflater = LayoutInflater.from(ctx);
	}

	public List<FollowQuestionDetailed> getList() {
		return list;
	}

	public void update(List<FollowQuestionDetailed> temp) {
		if (temp != null) {
			list.clear();
			for (int i = 0; i < temp.size(); i++) {
				if (temp.get(i).getIsShow() == 1) {
					list.add(temp.get(i));
				}
			}
			Collections.sort(list, new FollowQuestionDetailedComparator());
		}
		position = -1;
		editText = null;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		int size = 0;
		if (list != null) {
			size = list.size();
		}
		return size;
	}

	@Override
	public FollowQuestionDetailed getItem(int arg0) {
		FollowQuestionDetailed item = null;
		if (list != null) {
			item = list.get(arg0);
		}
		return item;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.follow_question_item, null);
			holder = new ViewHolder();
			holder.tv_qt_name = (TextView) convertView.findViewById(R.id.tv_qt_name);
			holder.tv_qt_value_below = (TextView) convertView.findViewById(R.id.tv_qt_value_below);
			holder.tv_qt_value_right = (EditText) convertView.findViewById(R.id.tv_qt_value_right);
			holder.iv_question_arrow = (ImageView) convertView.findViewById(R.id.follow_question_arrow);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String name = list.get(arg0).getDictName();
		if (!TextUtils.isEmpty(list.get(arg0).getUnit())) {
			name += "(单位：" + list.get(arg0).getUnit() + ")";
		}
		holder.tv_qt_name.setText(name);
		// 输入的值放右
		if (list.get(arg0).getItemType() == 4 || list.get(arg0).getItemType() == 5) {
			String hint = "";
			if (list.get(arg0).getItemType() == 4) {
				holder.tv_qt_value_right.setInputType(InputType.TYPE_CLASS_TEXT);
				hint = "请输入相关详细！";
			} else {
				holder.tv_qt_value_right.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
				hint = "请输入数值！";
			}
			holder.tv_qt_value_right.setVisibility(View.VISIBLE);
			holder.tv_qt_value_right.setHint(hint);
			holder.tv_qt_value_right.setText(list.get(arg0).getValue() + "");
			holder.tv_qt_value_below.setVisibility(View.GONE);
			holder.iv_question_arrow.setVisibility(View.GONE);
			holder.tv_qt_value_right.setTag(arg0);
			specialAddListener(holder.tv_qt_value_right);
		} else {
			holder.tv_qt_value_right.setVisibility(View.GONE);
			holder.tv_qt_value_below.setVisibility(View.VISIBLE);
			holder.iv_question_arrow.setVisibility(View.VISIBLE);
			holder.tv_qt_value_below.setText(list.get(arg0).getValue() + "");
		}
		return convertView;
	}

	private void specialAddListener(EditText v) {
		v.setOnTouchListener(mOnTouchListener);
		v.addTextChangedListener(mTextWatcher);
	}

	public class ViewHolder {
		public TextView tv_qt_name;
		public TextView tv_qt_value_below;
		public EditText tv_qt_value_right;
		public ImageView iv_question_arrow;
	}

	class FollowQuestionDetailedComparator implements Comparator<FollowQuestionDetailed> {
		@Override
		public int compare(FollowQuestionDetailed object1, FollowQuestionDetailed object2) {
			String[] ms1 = object1.getPath().split("_");
			String[] ms2 = object2.getPath().split("_");
			int m1 = Integer.parseInt(ms1[(ms1.length - 1)]);
			int m2 = Integer.parseInt(ms2[(ms2.length - 1)]);
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

	OnTouchListener mOnTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			position = (Integer) arg0.getTag();
			editText = (EditText) arg0;
			return false;
		}
	};
	private EditText editText;
	TextWatcher mTextWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

		}

		@Override
		public void afterTextChanged(Editable arg0) {
			if (editText != null) {

				String value = editText.getText().toString();
				if (value == null) {
					value = "";
				}
				if (position >= 0 && position < list.size()) {
					list.get(position).setValue(value);
				}
				save = true;

			}
		}
	};
}
