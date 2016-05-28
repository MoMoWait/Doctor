package com.comvee.tnb.view;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.comvee.tnb.R;
import com.comvee.tnb.adapter.MenuAdapter;
import com.comvee.tnb.widget.ScrollListView;
import com.comvee.tool.UITool;

public class MenuWindow extends PopupWindow {
	private static MenuWindow menuWindow;
	// PopupWindow popupWindow;
	private Activity activity;
	private ArrayList<String> values;
	private ArrayList<Integer> icons;
	private float itemWidth;
	private ScrollListView listView;

	public static MenuWindow getInstance(Activity activities, ArrayList<String> values, ArrayList<Integer> icons, float ItemMaxWidth) {

		menuWindow = new MenuWindow();
		menuWindow.setActivity(activities);
		menuWindow.setIcons(icons);
		menuWindow.setValues(values);
		menuWindow.setItemWidth(ItemMaxWidth);
		menuWindow.createPopuwindow();

		return menuWindow;
	}

	public void setOnOnitemClickList(OnItemClickListener clickListener) {
		if (listView != null) {
			listView.setOnItemClickListener(clickListener);
		}
	}

	private void setItemWidth(float itemWidth) {
		this.itemWidth = itemWidth;
	}

	private void setValues(ArrayList<String> values) {
		this.values = values;
	}

	private void setIcons(ArrayList<Integer> icons) {
		this.icons = icons;
	}

	private void setActivity(Activity activity) {
		this.activity = activity;
	}

	private MenuAdapter adapter;

	private void createPopuwindow() {
		View v = View.inflate(activity, R.layout.menu_layout, null);
		// popupWindow = new PopupWindow(v, LayoutParams.MATCH_PARENT,
		// LayoutParams.MATCH_PARENT);
		// popupWindow.setFocusable(true);
		// // 设置允许在外点击消失
		// popupWindow.setOutsideTouchable(true);
		// // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
		// popupWindow.setBackgroundDrawable(new BitmapDrawable());
		// popupWindow.setAnimationStyle(R.style.PopupAnimation);
		setContentView(v);
		setWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, itemWidth, activity.getResources().getDisplayMetrics()));
		setHeight(LayoutParams.WRAP_CONTENT);
		setBackgroundDrawable(new BitmapDrawable());
		setAnimationStyle(R.style.PopupAnimation);
		setFocusable(true);
		setOutsideTouchable(true);
		listView = (ScrollListView) v.findViewById(R.id.list_menu);
		if (itemWidth != 0) {
			// float width =
			// TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, itemWidth,
			// activity.getResources().getDisplayMetrics());
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			layoutParams.setMargins(0, UITool.dip2px(activity, 10), 0, 0);
			listView.setLayoutParams(layoutParams);
		}
		adapter = new MenuAdapter();
		adapter.setContext(activity);
		adapter.setItemIcon(icons);
		adapter.setItemValues(values);
		listView.setAdapter(adapter);

		this.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				UITool.backgroundAlpha(activity, 1);
			}
		});
	}
}
