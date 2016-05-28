package com.comvee.tnb.view;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.model.AssessQuestion;
import com.comvee.tnb.model.QuestionInfo;
import com.comvee.tnb.ui.assess.AssessQuestionFragment;

public class AssessInputContentView extends AssessBaseContentView implements OnClickListener {
    private TextView tvName;
    private EditText edtValue;

    public AssessInputContentView(Context context, AssessQuestionFragment main) {
        super(context, main);
    }


    public void init() {
        View mRootView = LayoutInflater.from(getContext()).inflate(R.layout.item_quetion_input, this, true);

        tvName = (TextView) mRootView.findViewById(R.id.tv_qestion_name);
        TextView tvDecs = (TextView) mRootView.findViewById(R.id.tv_qestion_decs);
        TextView tvHelp = (TextView) mRootView.findViewById(R.id.tv_qestion_help);
        tvHelp.setText(mInfo.ques.help);
        tvDecs.setText("填空");
        tvName.setText(mInfo.ques.con);
//		tvName.setOnClickListener(!TextUtils.isEmpty(mInfo.help)?this:null);
//		tvName.setCompoundDrawablesWithIntrinsicBounds(mInfo.isNeed ? R.drawable.member_input_sgin : 0, 0,
//				!TextUtils.isEmpty(mInfo.help) ? R.drawable.quetion_help_sign : 0, 0);

        edtValue = (EditText) mRootView.findViewById(R.id.tv_value);
        edtValue.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(edtValue.getText().toString())) {
                    getMain().setCanJump(false);
                } else {
                    getMain().setCanJump(true);
                }
            }
        });

        setDefualtValue();
    }

    private void setDefualtValue() {
        edtValue.setText(mInfo.displayValue);
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
                break;
        }

    }

    @Override
    public int getNextIndex() {
        mInfo.displayValue = edtValue.getText().toString();
        if (!TextUtils.isEmpty(mInfo.displayValue)) {
            return mInfo.ques.goTo;
        } else if (mInfo.ques.isNeed != 1) {
            return mInfo.ques.goTo;
        }

        return -2;
    }

}
