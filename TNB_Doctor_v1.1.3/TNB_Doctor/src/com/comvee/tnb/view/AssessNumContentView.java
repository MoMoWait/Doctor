package com.comvee.tnb.view;

import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.dialog.CustomNumPickDialog;
import com.comvee.tnb.dialog.CustomNumPickDialog.OnChangeNumListener;
import com.comvee.tnb.model.AssessQuestion;
import com.comvee.tnb.model.QuestionInfo;
import com.comvee.tnb.model.QuestionItemInfo;
import com.comvee.tnb.ui.assess.AssessQuestionFragment;
import com.comvee.tool.UserMrg;

public class AssessNumContentView extends AssessBaseContentView implements OnChangeNumListener, OnClickListener {
    private TextView tvValue;
    private TextView tvError;
    private TextView tvName;
    private TextView tvSign;

    private String strUnit;
    private boolean isFloat = false;
    private int floatNum = 1;
    private float numMax = 0;
    private float numMin = 0;
    private float standard_max = 0;
    private float standard_min = 0;

    private float numDefault = 0;
    private int jump = -2;

    public AssessNumContentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public AssessNumContentView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public AssessNumContentView(Context context, AssessQuestionFragment main) {
        super(context, main);
    }

    public void init() {
        View rootView = LinearLayout.inflate(getContext().getApplicationContext(), R.layout.item_quetion_date, this);
        View btnTime = rootView.findViewById(R.id.btn_set_value);
        btnTime.setOnClickListener(this);
        TextView tvLabel = (TextView) rootView.findViewById(R.id.tv_label);
        TextView tvDecs = (TextView) rootView.findViewById(R.id.tv_qestion_decs);
        TextView tvHelp = (TextView) rootView.findViewById(R.id.tv_qestion_help);
        tvHelp.setText(mInfo.ques.help);
        tvValue = (TextView) rootView.findViewById(R.id.tv_value);
        tvValue.setText(strUnit);
        tvName = (TextView) rootView.findViewById(R.id.tv_qestion_name);
        tvSign = (TextView) rootView.findViewById(R.id.tv_sign);
        tvLabel.setText("请输入数值");
        tvDecs.setVisibility(View.GONE);
        tvName.setText(mInfo.ques.con);

        if ("请填写您的身高".equals(mInfo.ques.con)) {
            tvLabel.setText("身高");
            try {
                onChange(null, Float.valueOf(UserMrg.DEFAULT_MEMBER.memberHeight));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("请填写您的体重".equals(mInfo.ques.con)) {
            tvLabel.setText("体重");
        }

        if (!TextUtils.isEmpty(mInfo.displayValue)) {
            try {
                if (isFloat) {
                    onChange(null,Float.valueOf(mInfo.displayValue));
//                    tvValue.setText( + strUnit);
                } else {
                    float value = Float.valueOf(mInfo.displayValue);
                    onChange(null,(int) value);
                }
                getMain().setCanJump(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    private void showHelpWindow() {
        QuestionHelpWindow window = new QuestionHelpWindow(getContext().getApplicationContext(), mInfo.ques.help);
        window.showAsDropDown(tvName, 0, 0);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.btn_set_value:
                showSetValueDialog();
                break;
            case R.id.btn_label:
                break;
            case R.id.tv_qestion_name:
                showHelpWindow();
                break;
            default:
                break;
        }

    }

    public void showSetValueDialog() {
        CustomNumPickDialog buidler = new CustomNumPickDialog();
        buidler.setMessage(mInfo.ques.help);
        buidler.setDefualtNum(numDefault);
        buidler.setFloat(isFloat);
        buidler.setFloatNum(floatNum);
        buidler.setLimit((int) Math.floor(numMin), (int) Math.ceil(numMax));
        buidler.setOnChangeNumListener(this);
        buidler.show(getMain().getActivity().getSupportFragmentManager(), "dialog");
    }

    @Override
    public void setQuestion(AssessQuestion info) {
        super.setQuestion(info);
        int len = this.mInfo.items.size();
        for (int i = 0; i < len; i++) {

            AssessQuestion.QuestionItem item = this.mInfo.items.get(i);
            jump = item.jump;
            String tag = item.item;
            String value = item.value;

            if ("max".equalsIgnoreCase(tag)) {
                numMax = Float.valueOf(value);
            } else if ("min".equalsIgnoreCase(tag)) {
                numMin = Float.valueOf(value);
            } else if ("unit".equalsIgnoreCase(tag)) {
                strUnit = value;
            } else if ("default".equalsIgnoreCase(tag)) {
                numDefault = Float.valueOf(value);
            } else if ("isFloat".equalsIgnoreCase(tag)) {
                isFloat = Integer.valueOf(value) > 0;
                floatNum = Integer.valueOf(value);
            } else if ("standard_min".equalsIgnoreCase(tag)) {
                standard_min = Float.valueOf(value);
            } else if ("standard_max".equalsIgnoreCase(tag)) {
                standard_max = Float.valueOf(value);
            }
        }
    }

    @Override
    public int getNextIndex() {
        if (!TextUtils.isEmpty(tvValue.getText().toString()) && !strUnit.equals(tvValue.getText().toString())) {
            return jump;
        } else if (mInfo.ques.isNeed != 1) {
            return mInfo.ques.goTo;
        } else {
            return -2;
        }
    }

    @Override
    public void onChange(DialogFragment dialog, float num) {
        // TODO Auto-generated method stub
        numDefault = num;
        if (isFloat) {
            tvValue.setText(num + strUnit);
        } else {
            tvValue.setText((int) num + strUnit);
        }
        if (standard_max != standard_min && (num > standard_max || num < standard_min)) {
            tvValue.setTextColor(Color.RED);
            tvSign.setTextColor(Color.RED);
            tvSign.setVisibility(View.VISIBLE);
            if (num > standard_max) {
                tvSign.setText("↑");
            } else {
                tvSign.setText("↓");
            }
        } else {
            tvSign.setVisibility(View.GONE);
            tvValue.setTextColor(getResources().getColor(R.color.theme_color_green));
        }
        mInfo.displayValue = String.valueOf(num);
        getMain().setCanJump(false);
    }

}
