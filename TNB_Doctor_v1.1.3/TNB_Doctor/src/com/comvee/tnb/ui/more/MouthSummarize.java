package com.comvee.tnb.ui.more;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comvee.tnb.DrawerMrg;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.SummarizeList;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.tool.UserMrg;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 月度报告列表页
 *
 * @author Administrator
 */
public class MouthSummarize extends BaseFragment implements OnHttpListener, OnClickListener {
    private List<SummarizeList> list;
    private List<String> year;
    private LinearLayout root;
    private boolean isSliding;
    private View view;
    private TitleBarView mBarView;

    public static void toFragment(FragmentActivity fragment, boolean isSliding) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isSliding", isSliding);
        boolean isAnima = true;
        if (isSliding) {
            isAnima = false;
        }
        FragmentMrg.toFragment(fragment, MouthSummarize.class, bundle, isAnima);
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.fragment_summarizelist;
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

    }

    @Override
    public void onLaunch(Bundle bundle) {
        super.onLaunch(bundle);
        if (bundle != null) {
            isSliding = bundle.getBoolean("isSliding");
        }
        if (isSliding) {
            DrawerMrg.getInstance().close();
        }
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        mBarView.setTitle("月度总结");
        init();
    }

    private void init() {
        root = (LinearLayout) findViewById(R.id.sum_body_lin);
        view = findViewById(R.id.layout_no_data);
        ImageView head = (ImageView) findViewById(R.id.img_photo);
        ImageLoaderUtil.getInstance(mContext).displayImage(UserMrg.DEFAULT_MEMBER.photo, head, ImageLoaderUtil.user_options);
        requestSummeryList();

    }

    private void requestSummeryList() {
        showProgressDialog(getString(R.string.msg_loading));
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.SUM_LIST);
        http.setOnHttpListener(1, this);
        http.startAsynchronous();
    }

    @Override
    public void onSussece(int what, byte[] b, boolean fromCache) {
        cancelProgressDialog();
        try {
            final ComveePacket packet = ComveePacket.fromJsonString(b);
            if (packet.getResultCode() == 0) {
                list = new ArrayList<SummarizeList>();
                JSONArray body = packet.optJSONArray("body");
                for (int i = 0; i < body.length(); i++) {
                    JSONObject info = body.getJSONObject(i);
                    SummarizeList sum = new SummarizeList();
                    sum.setMemberId(info.optString("memberId"));
                    sum.setMouth(info.optString("month"));
                    sum.setMonthId(info.optString("monthId"));
                    sum.setStatus(info.optString("status"));
                    list.add(sum);
                }
                if (list.size() > 0) {
                    root.setVisibility(View.VISIBLE);
                    view.setVisibility(View.GONE);
                } else {
                    root.setVisibility(View.GONE);
                    view.setVisibility(View.VISIBLE);
                }
                getSumYear();
                getChild();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 获取月的总结的年份
    public void getSumYear() {
        year = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            String mouth = list.get(i).getMouth();
            String str[] = mouth.split("-");
            if (!year.contains(str[0])) {
                year.add(str[0]);
            }
        }
    }

    @Override
    public boolean onBackPress() {
        if (isSliding) {
            return false;/*
                         * ((MainActivity) getActivity()).showLeftView(); return
						 * true;
						 */
        } else {
            return false;
        }
    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
    }

    // 获取每年中的月度报告 按年度分类
    private void getChild() {
        for (int i = 0; i < year.size(); i++) {
            for (int j = 0; j < list.size(); j++) {
                String str[] = list.get(j).getMouth().split("-");
                if (year.get(i).equals(str[0])) {
                    createChile(j, 1);
                }
            }
            createChile(i, 2);
        }
    }

    /*
     * 创建子控件 type 1显示月份 2 显示年份
     */
    private void createChile(int index, int type) {
        View view = View.inflate(getContext(), R.layout.item_summerize, null);

        View rel_center = view.findViewById(R.id.sum_rel_center);
        TextView tvCneter = (TextView) view.findViewById(R.id.sum_text_cent);

        View rel = view.findViewById(R.id.sum_rel);
        View left = view.findViewById(R.id.sum_lin_left);
        View right = view.findViewById(R.id.sum_lin_right);
        TextView text = null;
        root.addView(view);
        if (type == 1) {
            rel_center.setVisibility(View.GONE);
            rel.setVisibility(View.VISIBLE);
        } else {
            rel.setVisibility(View.GONE);
            rel_center.setVisibility(View.VISIBLE);

            tvCneter.setText(year.get(index));
            if (index == year.size() - 1) {
                view.findViewById(R.id.sum_end_img).setVisibility(View.GONE);
                view.findViewById(R.id.sum_end_view).setVisibility(View.GONE);
            }
            return;
        }

        if (index % 2 == 0) {
            left.setVisibility(View.INVISIBLE);
            right.setVisibility(View.VISIBLE);
            text = (TextView) view.findViewById(R.id.sum_text_rignt);
        } else {
            right.setVisibility(View.INVISIBLE);
            left.setVisibility(View.VISIBLE);
            text = (TextView) view.findViewById(R.id.sum_text_left);
        }
        text.setText(list.get(index).getMouth().split("-")[1] + "月份 月度总结");
        text.setTag(list.get(index));
        text.setOnClickListener(this);
    }

    @Override
    public void onFialed(int what, int errorCode) {
        cancelProgressDialog();
        ComveeHttpErrorControl.parseError(getActivity(), errorCode);
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case TitleBarView.ID_LEFT_BUTTON:
                DrawerMrg.getInstance().open();
                break;

            default:
                SummarizeList sum = (SummarizeList) arg0.getTag();
                String url = ConfigUrlMrg.URL_HEAD + "yuedubaogao/index.html?sessionID=" + UserMrg.getSessionId(mContext) + "&sessionMemberID="
                        + UserMrg.getMemberSessionId(mContext) + "&date=" + sum.getMouth() + "&monthId=" + sum.getMonthId() + "&type=android";
                String str[] = sum.getMouth().split("-");
                String year = str[0] + "年" + str[1] + "月";
                WebMouthlySummarizeFragment.toFragment(getActivity(), url, year);
                break;
        }

    }
}
