package com.comvee.tnb.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.dialog.RecordFragment;
import com.comvee.tnb.radio.RadioMainFrag;
import com.comvee.tnb.ui.ask.AskIndexFragment;
import com.comvee.tnb.ui.assess.AssessFragment;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.ui.user.LoginFragment;
import com.comvee.tool.UserMrg;
import com.tencent.stat.common.User;

/**
 * 首页导航条
 * Created by friendlove-pc on 16/3/20.
 */
public class IndexBottomView extends RelativeLayout {


    private View btnAsk, btnIndex, btnRadio, btnAssess;
    private TextView tvUnread;
    public IndexBottomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public IndexBottomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IndexBottomView(Context context) {
        super(context);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        inflate(getContext(), R.layout.navigation_bar, this);

        btnAsk = findViewById(R.id.btn_ask);
        btnIndex = findViewById(R.id.btn_index);
        btnRadio = findViewById(R.id.btn_discover);
        btnAssess = findViewById(R.id.btn_assess);
        tvUnread = (TextView) findViewById(R.id.tv_unread);
    }

    public void selectAessess() {
        btnAssess.setSelected(true);
    }

    public void selectDoc() {
        btnAsk.setSelected(true);
    }

    public void selectIndex() {
        btnIndex.setSelected(true);
    }

    public void selectRadio() {
        btnRadio.setSelected(true);
    }

    public void bindFragment(final BaseFragment frag) {
        OnClickListener list = new OnClickListener() {

            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btn_index:
                        if (frag instanceof IndexFrag) {
                        } else {
                            IndexFrag.toFragment(frag.getActivity(),false);
                        }
                        break;
                    case R.id.btn_discover:
                        if (frag instanceof RadioMainFrag) {
                        } else {
                            if(UserMrg.IS_VISITOR) {
                                LoginFragment.toFragment(frag.getActivity(),false);
                            }else{
                                frag.toFragment(RadioMainFrag.class, null, false);
                            }
                        }
                        break;
                    case R.id.btn_ask:
                        if (frag instanceof AskIndexFragment) {
                        } else {
                            if(UserMrg.IS_VISITOR){
                                LoginFragment.toFragment(frag.getActivity(),false);
                            }else{
                                AskIndexFragment.toFragment(frag.getActivity(),false);
                            }
                        }
                        break;
                    case R.id.btn_assess:
                        if (frag instanceof AssessFragment) {
                        } else {
                            AssessFragment.toFragment(frag.getActivity(),false);
                        }
                        break;
                    case R.id.btn_record:
                        RecordFragment dialog = new RecordFragment();
                        dialog.show(frag.getFragmentManager(), "record");
                        break;
                }
            }
        };

        btnAsk.setOnClickListener(list);
        btnAssess.setOnClickListener(list);
        btnRadio.setOnClickListener(list);
        btnIndex.setOnClickListener(list);
        findViewById(R.id.btn_record).setOnClickListener(list);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        update();
    }

    public void update(){
        int docCount = ConfigParams.getMsgDocCount(getContext());
        if(docCount>0){
            tvUnread.setVisibility(View.VISIBLE);
            tvUnread.setText(String.valueOf(docCount));
        }else{
            tvUnread.setVisibility(View.GONE);
        }
    }
}
