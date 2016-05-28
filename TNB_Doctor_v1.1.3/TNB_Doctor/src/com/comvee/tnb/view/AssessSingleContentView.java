package com.comvee.tnb.view;

import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.model.AssessQuestion;
import com.comvee.tnb.model.QuestionInfo;
import com.comvee.tnb.model.QuestionItemInfo;
import com.comvee.tnb.ui.assess.AssessQuestionFragment;
import com.comvee.util.Util;

public class AssessSingleContentView extends AssessBaseContentView implements OnClickListener {
	private LinearLayout layoutCheck;
	private TextView tvName;

	public AssessSingleContentView(Context context, AssessQuestionFragment main) {
		super(context, main);
	}


	public void init() {
		View mRootView = LayoutInflater.from(getContext()).inflate(R.layout.item_quetion_select, this, true);

		tvName = (TextView) mRootView.findViewById(R.id.tv_qestion_name);
		TextView tvHelp = (TextView) mRootView.findViewById(R.id.tv_qestion_help);
		tvHelp.setText(mInfo.ques.help);
		TextView tvDecs = (TextView) mRootView.findViewById(R.id.tv_qestion_decs);
		tvDecs.setText("单选");
		tvName.setText(mInfo.ques.con);
		layoutCheck = (LinearLayout) mRootView.findViewById(R.id.layout_items);
		if(null!=mInfo.items)
		for (int i = 0; i < mInfo.items.size(); i++) {
			createItemAction(layoutCheck, i);
		}
		setDefualtValue();
		checkCanJump();
	}
	private void checkCanJump() {
		if (mInfo.ques.isNeed == 1) {
			getMain().setCanJump(false);
		} else {
			getMain().setCanJump(!TextUtils.isEmpty(mInfo.displayValue)?false:true);
		}
	}

	private void setDefualtValue() {
		if (!TextUtils.isEmpty(mInfo.displayValue)) {

			try {
				for (int i = 0; i < mInfo.items.size(); i++) {
					if (mInfo.displayValue.equals(mInfo.items.get(i).value)) {
						checkItem(i, true);
						getMain().setCanJump(false);
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void createItemAction(LinearLayout root, int index) {
		AssessQuestion.QuestionItem item = mInfo.items.get(index);
		TextView tv = new TextView(getContext());
		tv.setText(item.item);
		tv.setTextColor(Color.BLACK);
		tv.setTag(item);
		tv.setTextSize(16);
		tv.setGravity(Gravity.CENTER_VERTICAL);
		tv.setId(index);
		tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.assess_question_item_select, 0, 0, 0);
		tv.setOnClickListener(this);
		int pading = Util.dipToPx(getContext(), 50);
		tv.setPadding(pading / 3, 0, pading / 3, 0);
		tv.setCompoundDrawablePadding(pading / 4);
		root.addView(tv, -1, pading);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
	}

	private void showHelpWindow() {
		QuestionHelpWindow window = new QuestionHelpWindow(getContext(), mInfo.ques.help);
		window.showAsDropDown(tvName, 0, 0);
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
		case R.id.btn_settime:
			break;
		case R.id.btn_label:
			break;
		case R.id.tv_qestion_name:
			showHelpWindow();
			break;
		default:
			AssessQuestion.QuestionItem item = (AssessQuestion.QuestionItem) v.getTag();
			for (int i = 0; i < layoutCheck.getChildCount(); i++) {
				View view = layoutCheck.getChildAt(i);
				if(v != view){
					view.setSelected(false);
				}
			}
			v.setSelected(!v.isSelected());
			if(mInfo.ques.isNeed!=1){//非必填
				getMain().setCanJump(!v.isSelected());
			}
			break;
		}

	}

	private boolean isCheckItem(int position) {
		return layoutCheck.getChildAt(position).isSelected();
	}

	private void checkItem(int position, boolean b) {
		layoutCheck.getChildAt(position).setSelected(b);
	}



	@Override
	public int getNextIndex() {
		int len = layoutCheck.getChildCount();
		for (int i = 0; i < len; i++) {
			if (isCheckItem(i)) {
				mInfo.displayValue = mInfo.items.get(i).value;
				return mInfo.items.get(i).jump;
			}
		}
		if (mInfo.ques.isNeed != 1) {
			return mInfo.ques.goTo;
		} else {
			return -2;
		}
	}


}
