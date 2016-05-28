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

public class AssessMultiContentView extends AssessBaseContentView implements OnClickListener {
    private LinearLayout layoutCheck;
    private TextView tvName;

    public AssessMultiContentView(Context context, AssessQuestionFragment main) {
        super(context, main);
    }

    public void init() {
        View mRootView = LayoutInflater.from(getContext()).inflate(R.layout.item_quetion_select, this, true);

        tvName = (TextView) mRootView.findViewById(R.id.tv_qestion_name);
        TextView tvHelp = (TextView) mRootView.findViewById(R.id.tv_qestion_help);
        tvHelp.setText(mInfo.ques.help);
        TextView tvDecs = (TextView) mRootView.findViewById(R.id.tv_qestion_decs);
        tvDecs.setText("根据症状可多选");
        tvName.setText(mInfo.ques.con);

        layoutCheck = (LinearLayout) mRootView.findViewById(R.id.layout_items);
        if (mInfo.items != null) {
            for (int i = 0; i < mInfo.items.size(); i++) {
                createItemAction(layoutCheck, i);
            }
        }

        setDefualtValue();
        checkCanJump();
    }

    private void setDefualtValue() {
        if (!TextUtils.isEmpty(mInfo.displayValue)) {

            try {
                HashMap<String, AssessQuestion.QuestionItem> map = new HashMap<String, AssessQuestion.QuestionItem>();
                String[] values = mInfo.displayValue.contains("|") ? mInfo.displayValue.split("|") : new String[]{mInfo.displayValue};
                for (AssessQuestion.QuestionItem item : mInfo.items) {
                    map.put(item.value, item);
                }
                for (String v : values) {
                    AssessQuestion.QuestionItem selectedItem = map.get(v);
                    if (selectedItem != null) {
                        int index = mInfo.items.indexOf(selectedItem);
                        checkItem(index, true);
                        getMain().setCanJump(false);
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
            case R.id.tv_qestion_name:
                showHelpWindow();
                break;
            case R.id.btn_settime:
                break;
            case R.id.btn_label:
                break;
            default:
                AssessQuestion.QuestionItem item = (AssessQuestion.QuestionItem) v.getTag();
                if (item.tie == 1) {// 是否 是 “无” 如果 是选择了 “无” 其他的选项都取消
                    for (int i = 0; i < layoutCheck.getChildCount(); i++) {
                        View view = layoutCheck.getChildAt(i);
                        if (v != view) {
                            view.setSelected(false);
                        }
                    }
                }else{
                    for (int i = 0; i < layoutCheck.getChildCount(); i++) {
                        View view = layoutCheck.getChildAt(i);
                        if (((AssessQuestion.QuestionItem)view.getTag()).tie == 1) {
                            view.setSelected(false);
                        }
                    }
                }
                v.setSelected(!v.isSelected());
                checkCanJump();
                break;
        }

    }

    public void fillValue() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < layoutCheck.getChildCount(); i++) {
            if (layoutCheck.getChildAt(i).isSelected())
                sb.append(mInfo.items.get(i).value).append("|");
        }
        mInfo.displayValue = sb.toString();

        if (!TextUtils.isEmpty(mInfo.displayValue)) {
            mInfo.displayValue =  mInfo.displayValue.substring(0, mInfo.displayValue.length() - 1);
        }


    }

    private void checkCanJump() {
        if (mInfo.ques.isNeed == 1) {
            getMain().setCanJump(false);
        } else {
            getMain().setCanJump(!TextUtils.isEmpty(mInfo.displayValue)?false:true);
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
        fillValue();
        int len = layoutCheck.getChildCount();
        for (int i = 0; i < len; i++) {
            if (isCheckItem(i)) {
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
