package com.comvee.tnb.view;

import java.util.Calendar;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.comvee.tnb.R;
import com.comvee.tnb.dialog.CustomDatePickDialog;
import com.comvee.tnb.model.AssessQuestion;
import com.comvee.tnb.model.QuestionInfo;
import com.comvee.tnb.model.QuestionItemInfo;
import com.comvee.tnb.ui.assess.AssessQuestionFragment;
import com.comvee.util.TimeUtil;

public class AssessDateContentView extends AssessBaseContentView implements
        com.comvee.tnb.dialog.CustomDatePickDialog.OnTimeChangeListener, OnClickListener {
    private TextView tvTime;
    private TextView tvError;
    private TextView tvName;

    public AssessDateContentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public AssessDateContentView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public AssessDateContentView(Context context, AssessQuestionFragment main) {
        super(context, main);
    }

    public void init() {
        View rootView = LinearLayout.inflate(getContext().getApplicationContext(), R.layout.item_quetion_date, this);
        View btnTime = rootView.findViewById(R.id.btn_set_value);
        btnTime.setOnClickListener(this);
        tvTime = (TextView) rootView.findViewById(R.id.tv_value);
        tvName = (TextView) rootView.findViewById(R.id.tv_qestion_name);
        TextView tvHelp = (TextView) rootView.findViewById(R.id.tv_qestion_help);
        tvHelp.setText(mInfo.ques.help);

        if (mInfo.ques.isNeed == 1) {
            tvName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.member_input_sgin, 0, 0, 0);
        }

        TextView tvDecs = (TextView) rootView.findViewById(R.id.tv_qestion_decs);
        tvDecs.setVisibility(View.GONE);
        tvName.setText(mInfo.ques.con);

        if (!TextUtils.isEmpty(mInfo.displayValue)) {
            try {
                tvTime.setText(TimeUtil.fomateTime("yyyy-MM-dd", "yyyy年MM月dd日", mInfo.displayValue));
                // getMain().setNextButtonText("下一题");
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
        QuestionHelpWindow window = new QuestionHelpWindow(getContext(), mInfo.ques.help);
        window.showAsDropDown(tvName, 0, 0);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.btn_set_value:
                showSetTimeDialog();
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

    public void showSetTimeDialog() {
        CustomDatePickDialog builder = new CustomDatePickDialog();
        builder.setDefaultTime(Calendar.getInstance());
        builder.setOnTimeChangeListener(this);
        Calendar cal = Calendar.getInstance();
        builder.setDefaultTime(cal);
        builder.setLimitTime(1800, cal.get(Calendar.YEAR));
        builder.show(getMain().getActivity().getSupportFragmentManager(), "dialog");
    }

    @Override
    public int getNextIndex() {
        if (!TextUtils.isEmpty(tvTime.getText().toString())) {
            return mInfo.items.get(0).jump;
        } else if (mInfo.ques.isNeed != 1) {
            return mInfo.ques.goTo;
        } else {
            return -2;
        }
    }


    @Override
    public void onChange(DialogFragment dialog, int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);
        if (Calendar.getInstance().getTimeInMillis() < cal.getTimeInMillis()) {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.txt_date_choose_limit), Toast.LENGTH_SHORT).show();
            return;
        }

        tvTime.setText(year + "年" + month + "月" + day + "日");
        mInfo.displayValue = String.format("%d-%02d-%02d", year, month, day);
        getMain().setCanJump(false);
    }

}
