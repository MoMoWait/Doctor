package com.comvee.tnb.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.model.TaskFilterInfo;

public class TaskFilterWindow extends PopupWindow implements View.OnClickListener
{

	private Context mContext;
	private View mRootView;
	private ArrayList<TaskFilterInfo> mTaskFilterList;
	private OnItemClick mListener;

	public void setOnListener(OnItemClick mListener)
	{
		this.mListener = mListener;
	}

	public TaskFilterWindow(Context cxt, ArrayList<TaskFilterInfo> list, String type)
	{
		super(cxt);
		this.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
		this.mContext = cxt;
		mRootView = createRootView();

		this.mTaskFilterList = list;
		if (null == type || type.equals(list.get(0).id))
		{
			list.get(0).isSelected = true;
		} 
		else
		{
			List<String> types = Arrays.asList(type.split(","));

			for (TaskFilterInfo item : TaskFilterWindow.this.mTaskFilterList)
			{
				if (types.indexOf(item.id) != -1)
				{
					item.isSelected = true;
				}
			}
			list.get(0).isSelected = false;

		}
		GridView mGridView = (GridView) mRootView.findViewById(R.id.gridview);
		mRootView.findViewById(R.id.btn_ok).setOnClickListener(this);
		final MyAdapter mAdapter = new MyAdapter();
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				if (arg2 == 0)
				{
					if (!mAdapter.getItem(0).isSelected)
					{
						for (TaskFilterInfo item : TaskFilterWindow.this.mTaskFilterList)
						{
							item.isSelected = false;
						}
						mAdapter.getItem(0).isSelected = true;
						mAdapter.notifyDataSetChanged();

					}
				} else
				{
					TaskFilterInfo info = mAdapter.getItem(arg2);
					info.isSelected = !info.isSelected;

					mAdapter.getItem(0).isSelected = false;

					if (!info.isSelected)
					{
						boolean b = false;
						for (int i = 1; i < mTaskFilterList.size(); i++)
						{
							b = mTaskFilterList.get(i).isSelected;
							if (b)
							{
								break;
							}
						}
						if (!b)
						{
							mAdapter.getItem(0).isSelected = true;
						}
					}

					mAdapter.notifyDataSetChanged();
				}
			}
		});
		setContentView(mRootView);
		setWidth(-1);
		setHeight(-1);
		setBackgroundDrawable(new BitmapDrawable());
		setAnimationStyle(R.style.PopupAnimation);
		setFocusable(false);
	}

	public void setOutTouchCancel(boolean b)
	{
		mRootView.setOnClickListener(b ? this : null);
	}

	private View createRootView()
	{
		View layout = View.inflate(mContext, R.layout.window_taskfilter, null);
		// LinearLayout layout = new LinearLayout(mContext);
		// layout.setOrientation(LinearLayout.VERTICAL);
		return layout;
	}

	public interface OnItemClick
	{
		void onClick(String type);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.btn_ok:
			StringBuffer sb = new StringBuffer();
			for (TaskFilterInfo item : mTaskFilterList)
			{
				if (item.isSelected)
				{
					sb.append(item.id).append(",");
				}
			}
			String id = sb.substring(0, sb.length() - 1);
			if (mListener != null)
			{
				mListener.onClick(id);
			}
			dismiss();
			break;

		default:
			break;
		}
	}

	class MyAdapter extends BaseAdapter
	{
		@Override
		public int getCount()
		{
			return mTaskFilterList == null ? 0 : mTaskFilterList.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			if (convertView == null)
			{
				convertView = View.inflate(mContext, R.layout.item_task_filter, null);
			}
			TextView tv = (TextView) convertView.findViewById(R.id.tv_name);
			tv.setText(getItem(position).name);
			if (getItem(position).isSelected)
			{
				tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check_style_1_b, 0, 0, 0);
			} else
			{
				tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check_style_1_a, 0, 0, 0);
			}
			return convertView;
		}

		@Override
		public TaskFilterInfo getItem(int position)
		{
			return mTaskFilterList.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return 0;
		}
	}

}
