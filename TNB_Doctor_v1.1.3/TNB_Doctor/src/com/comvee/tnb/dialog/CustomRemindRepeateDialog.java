//package com.comvee.tnb.dialog;
//
//import java.util.ArrayList;
//
//import org.chenai.util.Util;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.graphics.Color;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup.LayoutParams;
//import android.view.Window;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
//import android.widget.CompoundButton.OnCheckedChangeListener;
//import android.widget.TextView;
//
//import com.comvee.tnb.R;
//import com.comvee.tnb.config.ConfigParams;
//import com.comvee.ui.wheelview.NumericWheelAdapter;
//import com.comvee.ui.wheelview.OnWheelScrollListener;
//import com.comvee.ui.wheelview.StringListWheelAdapter;
//import com.comvee.ui.wheelview.WheelView;
//
//public class CustomRemindRepeateDialog extends Dialog implements View.OnClickListener
//{
//
//	public CustomRemindRepeateDialog(Context context, int theme)
//	{
//		super(context, theme);
//	}
//
//	public CustomRemindRepeateDialog(Context context)
//	{
//		super(context);
//
//	}
//
//	/**
//	 * Helper class for creating a custom dialog
//	 */
//	public static class Builder
//	{
//
//		private Context context;
//		private WheelListener wheelListener;
//		private WheelView wheelNum;
//		private WheelView wheelRule;
//		private int tabNum = 4;
//		private View btnOk;
//		public String strDate = "";
//		private View btnCancel;
//		private DialogInterface.OnClickListener positiveButtonClickListener, negativeButtonClickListener;
//		private int oldTabResId = R.id.tab_4;
//		private View layoutWheel;
//		private View layoutWeek;
//		private CheckBox cW1;
//		private CheckBox cW2;
//		private CheckBox cW3;
//		private CheckBox cW4;
//		private CheckBox cW5;
//		private CheckBox cW6;
//		private CheckBox cW7;
//		private int num;
//		private boolean[] cWeeks =
//		{};
//
//		private ArrayList<String> mRuleList = new ArrayList<String>();
//
//		public Builder(Context context)
//		{
//			this.context = context;
//		}
//
//		public Builder setListener(DialogInterface.OnClickListener listener)
//		{
//			this.negativeButtonClickListener = listener;
//			return this;
//		}
//
//		public Builder setWheelListener(WheelListener l)
//		{
//			this.wheelListener = l;
//			return this;
//		}
//
//		public CustomRemindRepeateDialog create()
//		{
//			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//			final CustomRemindRepeateDialog dialog = new CustomRemindRepeateDialog(context, R.style.Dialog);
//			dialog.getWindow().setWindowAnimations(R.style.DilaogAnimation);
//
//			final View layout = inflater.inflate(R.layout.dialog_repeate, null);
//			dialog.addContentView(layout, new LayoutParams(-1, -2));
//			dialog.setContentView(layout);
//			Window win = dialog.getWindow();
//			win.setGravity(Gravity.BOTTOM);
//			dialog.setCanceledOnTouchOutside(true);// 设置点击Dialog外部任意区域关闭Dialog
//			btnOk = layout.findViewById(R.id.btn_ok);
//			btnCancel = layout.findViewById(R.id.btn_cancel);
//
//			btnCancel.setOnClickListener(new View.OnClickListener()
//			{
//				@Override
//				public void onClick(View v)
//				{
//					if (negativeButtonClickListener != null)
//					{
//						negativeButtonClickListener.onClick(dialog, R.id.btn_cancel);
//					} else
//					{
//						dialog.cancel();
//					}
//				}
//			});
//			btnOk.setOnClickListener(new View.OnClickListener()
//			{
//				public void onClick(View v)
//				{
//
//					if (positiveButtonClickListener != null)
//					{
//						positiveButtonClickListener.onClick(dialog, R.id.btn_ok);
//						return;
//					}
//					try
//					{
//						if (wheelListener != null)
//						{
//
//							if (tabNum == 0)
//							{
//								int num = 1;
//								try
//								{
//									num = Integer.valueOf(wheelNum.getAdapter().getItem(wheelNum.getCurrentItem()));
//									num = num == -1 ? 0 : num;
//								} catch (Exception e)
//								{
//								}
//								scrollChange(dialog, tabNum, num);
//								wheelListener.finish(dialog, tabNum, num, cWeeks);
//							}
//						} else
//						{
//							wheelListener.finish(dialog, 4, 0, null);
//						}
//						dialog.dismiss();
//
//					} catch (Exception e)
//					{
//						dialog.dismiss();
//						e.printStackTrace();
//					}
//
//				}
//			});
//
//			View.OnClickListener tabClick = new View.OnClickListener()
//			{
//
//				@Override
//				public void onClick(View v)
//				{
//					int id = v.getId();
//					switch (id)
//					{
//					case R.id.tab_0:
//						toTab1(dialog, layout);
//						break;
//					case R.id.tab_1:
//						toTab2(dialog, layout);
//						break;
//					case R.id.tab_2:
//						layoutWeek.setVisibility(View.GONE);
//						layoutWheel.setVisibility(View.VISIBLE);
//						tabNum = 2;
//						toTab3(dialog, layout);
//						break;
//					case R.id.tab_4:
//						toTab4(dialog, layout);
//						break;
//					default:
//						break;
//					}
//					oldTabResId = id;
//					scrollChange(dialog, tabNum, num);
//				}
//			};
//			OnCheckedChangeListener cListener = new OnCheckedChangeListener()
//			{
//
//				@Override
//				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//				{
//					scrollChange(dialog, tabNum, 1);
//				}
//			};
//
//			cW1 = (CheckBox) layout.findViewById(R.id.check_week1);
//			cW2 = (CheckBox) layout.findViewById(R.id.check_week2);
//			cW3 = (CheckBox) layout.findViewById(R.id.check_week3);
//			cW4 = (CheckBox) layout.findViewById(R.id.check_week4);
//			cW5 = (CheckBox) layout.findViewById(R.id.check_week5);
//			cW6 = (CheckBox) layout.findViewById(R.id.check_week6);
//			cW7 = (CheckBox) layout.findViewById(R.id.check_week7);
//
//			cW1.setOnCheckedChangeListener(cListener);
//			cW2.setOnCheckedChangeListener(cListener);
//			cW3.setOnCheckedChangeListener(cListener);
//			cW4.setOnCheckedChangeListener(cListener);
//			cW5.setOnCheckedChangeListener(cListener);
//			cW6.setOnCheckedChangeListener(cListener);
//			cW7.setOnCheckedChangeListener(cListener);
//
//			final View btnTab0 = layout.findViewById(R.id.tab_0);
//			final View btnTab1 = layout.findViewById(R.id.tab_1);
//			final View btnTab2 = layout.findViewById(R.id.tab_2);
//			final View btnTab3 = layout.findViewById(R.id.tab_3);
//			final View btnTab4 = layout.findViewById(R.id.tab_4);
//			btnTab0.setOnClickListener(tabClick);
//			btnTab1.setOnClickListener(tabClick);
//			btnTab2.setOnClickListener(tabClick);
//			btnTab3.setOnClickListener(tabClick);
//			btnTab4.setOnClickListener(tabClick);
//
//			layoutWheel = layout.findViewById(R.id.layout_wheel);
//			layoutWeek = layout.findViewById(R.id.layout_week);
//
//			if (tabNum == 4)
//			{
//				toTab4(dialog, layout);
//			} else if (tabNum == 5)
//			{
//				toTab2(dialog, layout);
//			} else
//			{
//				toTab1(dialog, layout);
//			}
//			scrollChange(dialog, tabNum, num);
//			return dialog;
//		}
//
//		public Builder setDefaultRule(int tab, int num, boolean[] weeks)
//		{
//			this.tabNum = tab;
//			this.num = num;
//			this.cWeeks = weeks;
//			return this;
//		}
//
//		// 星期
//		private void toTab2(final Dialog dialog, View layout)
//		{
//			onUnChoiceTab(layout, oldTabResId);
//			onChoiceTab(layout, R.id.tab_1);
//			layoutWeek.setVisibility(View.VISIBLE);
//			layoutWheel.setVisibility(View.GONE);
//			tabNum = 5;
//
//			if (this.cWeeks != null)
//			{
//				CheckBox[] cbs =
//				{ cW1, cW2, cW3, cW4, cW5, cW6, cW7 };
//				for (int i = 0; i < this.cWeeks.length; i++)
//				{
//					cbs[i].setChecked(cWeeks[i]);
//				}
//			}
//
//		}
//
//		// 月
//		private void toTab3(final Dialog dialog, View layout)
//		{
//
//		}
//
//		// 不重复
//		private void toTab4(final Dialog dialog, View layout)
//		{
//
//			onUnChoiceTab(layout, oldTabResId);
//			onChoiceTab(layout, R.id.tab_4);
//
//			layoutWeek.setVisibility(View.GONE);
//			layoutWheel.setVisibility(View.VISIBLE);
//			mRuleList.clear();
//			mRuleList.add("不重复");
//			StringListWheelAdapter adapter = new StringListWheelAdapter(mRuleList);
//			wheelRule = (WheelView) layout.findViewById(R.id.rule);
//			wheelNum = (WheelView) layout.findViewById(R.id.num);
//			int iHeight = Util.dip2px(context, 20);
//			int textSize = Util.dip2px(context, ConfigParams.TEXT_SIZE_WHEEL);
//			wheelNum.TEXT_SIZE = textSize;
//			wheelRule.TEXT_SIZE = textSize;
//
//			wheelRule.ADDITIONAL_ITEM_HEIGHT = iHeight;
//			wheelNum.ADDITIONAL_ITEM_HEIGHT = iHeight;
//
//			wheelRule.setAdapter(adapter);
//			wheelRule.setCurrentItem(0);
//			wheelNum.setVisibility(View.GONE);
//			wheelRule.setVisibility(View.VISIBLE);
//			tabNum = 4;
//
//		}
//
//		private void onChoiceTab(View layout, int resid)
//		{
//			TextView tv = (TextView) layout.findViewById(resid);
//			tv.setBackgroundResource(R.drawable.tab_10);
//			tv.setTextColor(Color.WHITE);
//			this.oldTabResId = resid;
//		}
//
//		private void onUnChoiceTab(View layout, int resid)
//		{
//			TextView tv = (TextView) layout.findViewById(resid);
//			tv.setTextColor(Color.BLACK);
//			tv.setBackgroundResource(R.drawable.tab_10a);
//		}
//
//		private void toTab1(final Dialog dialog, View layout)
//		{
//
//			onUnChoiceTab(layout, oldTabResId);
//			onChoiceTab(layout, R.id.tab_0);
//
//			layoutWeek.setVisibility(View.GONE);
//			layoutWheel.setVisibility(View.VISIBLE);
//			mRuleList.clear();
//			mRuleList.add("日");
//			mRuleList.add("周");
//			mRuleList.add("月");
//			// mRuleList.add("年");
//			StringListWheelAdapter adapter = new StringListWheelAdapter(mRuleList);
//
//			wheelNum = (WheelView) layout.findViewById(R.id.num);
//			wheelRule = (WheelView) layout.findViewById(R.id.rule);
//
//			int iHeight = Util.dip2px(context, 20);
//			int textSize = Util.dip2px(context, ConfigParams.TEXT_SIZE_WHEEL);
//			wheelNum.TEXT_SIZE = textSize;
//			wheelRule.TEXT_SIZE = textSize;
//
//			wheelRule.ADDITIONAL_ITEM_HEIGHT = iHeight;
//			wheelNum.ADDITIONAL_ITEM_HEIGHT = iHeight;
//
//			wheelNum.setVisibility(View.VISIBLE);
//			wheelRule.setVisibility(View.VISIBLE);
//
//			wheelRule.setAdapter(adapter);
//
//			wheelNum.setAdapter(new NumericWheelAdapter(1, 31));
//			wheelNum.setCyclic(true);
//			wheelNum.setVisibleItems(5);
//			final OnWheelScrollListener scrollListener = new OnWheelScrollListener()
//			{
//
//				@Override
//				public void onScrollingStarted(WheelView wheel)
//				{
//				}
//
//				@Override
//				public void onScrollingFinished(WheelView wheel)
//				{
//					int num = 1;
//					int rule = 0;
//					try
//					{
//						num = Integer.valueOf(wheelNum.getAdapter().getItem(wheelNum.getCurrentItem()));
//					} catch (Exception e)
//					{
//					}
//					rule = wheelRule.getCurrentItem();
//					tabNum = rule;
//					if (tabNum != 5 && tabNum != 4)
//					{
//						scrollChange(dialog, rule, num);
//					}
//				}
//			};
//
//			wheelNum.addScrollingListener(scrollListener);
//			wheelRule.addScrollingListener(scrollListener);
//
//			if (tabNum < 0 || tabNum > 3)
//			{
//				tabNum = 0;
//				num = 1;
//			}
//
//			wheelRule.setCurrentItem(tabNum, false);
//			wheelNum.setCurrentItem(num - 1, false);
//		}
//
//		private void scrollChange(Dialog dialog, int tab, int num)
//		{
//			if (wheelListener != null)
//			{
////				String format = "";
////
////				switch (tab)
////				{
////				case 0:
////					format = "每%d日";
////					break;
////				case 1:
////					format = "每%d周";
////					break;
////				case 2:
////					format = "每%d月";
////					break;
////				case 3:
////					format = "每%d年";
////					break;
////				case 4:
////					format = "不重复";
////					break;
////				case 5:
////					format = "";
////					break;
////				default:
////					break;
////				}
//
//				boolean b[] =
//				{ cW1.isChecked(), cW2.isChecked(), cW3.isChecked(), cW4.isChecked(), cW5.isChecked(), cW6.isChecked(),
//						cW7.isChecked() };
//
//				wheelListener.scrollChange(dialog, tab, num, b);
//			}
//
//		}
//	}
//
//	@Override
//	public void onClick(View v)
//	{
//
//	}
//
//	public interface WheelListener
//	{
//		void scrollChange(Dialog dialog, int tab, int num, boolean[] weeks);
//
//		void finish(Dialog dialog, int tab, int num, boolean[] weeks);
//	}
//
//}
