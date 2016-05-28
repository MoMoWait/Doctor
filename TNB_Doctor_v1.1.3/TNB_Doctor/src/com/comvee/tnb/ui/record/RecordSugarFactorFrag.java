package com.comvee.tnb.ui.record;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.ComveeBaseAdapter;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.SugarMrg;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.model.HealthResultInfo;
import com.comvee.tnb.model.SugarRecord;
import com.comvee.tnb.network.ObjectLoader;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.ui.record.diet.RecordDietIndexFragment;
import com.comvee.tnb.widget.MyGridView;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.AppUtil;
import com.comvee.util.BundleHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择血糖异常因素
 * Created by friendlove-pc on 16/3/22.
 */
public class RecordSugarFactorFrag extends BaseFragment {
    private OptionAdapter mAdapter;
    private HealthResultInfo mInfo;

    private TitleBarView mBarView;

    private MyGridView mGridView;

    private SugarRecord mSugarInfo;


    public static void toFragment(FragmentActivity act, HealthResultInfo mInfo) {
        Bundle bundle = BundleHelper.getBundleBySerializable(mInfo);
        FragmentMrg.toFragment(act, RecordSugarFactorFrag.class, bundle, true);
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.record_sugar_factor_frag;
    }

    @Override
    public void onLaunch(Bundle dataBundle) {
        super.onLaunch(dataBundle);

        mInfo = BundleHelper.getSerializableByBundle(dataBundle);
        mSugarInfo = mInfo.bean;

        if (mSugarInfo == null || mInfo.sugger == null) {
            FragmentMrg.toBack(getActivity());
        }

        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        mBarView.setTitle(getString(R.string.title_record_detail));
        mAdapter = new OptionAdapter();
        mGridView = (MyGridView) findViewById(R.id.gridview_detail);
        mGridView.setAdapter(mAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mAdapter.select(i);
            }
        });

        final TextView tvTitle = (TextView) findViewById(R.id.tv_title);

        findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestSubmit();
            }
        });
        try {
            //当前血糖值  是 低值还是高值
            ArrayList<HealthResultInfo.OptionsValue> options = null;
            if (mSugarInfo.level == 1) {//血糖低值
                options = mInfo.sugger.suggerLow.optionsValue;
            } else {// 血糖高值
                options = mInfo.sugger.suggerHigh.optionsValue;
            }
            if (options != null) {
                for (HealthResultInfo.OptionsValue opt : options) {
                    if (mSugarInfo.paramCode.equals(opt.code)) {
                        mGridView.setItemCount(opt.options.size());
                        mAdapter.setDatas(opt.options);
                        mAdapter.notifyDataSetChanged();
                        tvTitle.setText(Html.fromHtml(opt.textTitle.replace("?", String.format("<font color='%s'>%.1f</>", SugarMrg.getSugarLevelColor(mSugarInfo.level), mSugarInfo.value))));
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 拼接用户选中的选项
     *
     * @return
     */
    private String getCoadString() {
        StringBuffer buffer = new StringBuffer();
        try {
            for (int i = 0; i < mAdapter.getDatas().size(); i++) {
                if (mAdapter.getItem(i).isSelected) {
                    buffer.append(mAdapter.getItem(i).option);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return buffer.toString();
    }

    private void requestSubmit() {
        mBarView.setVisibility(View.VISIBLE);
        showProgressDialog(getString(R.string.msg_loading));

        ObjectLoader<HealthResultInfo> loader = new ObjectLoader<HealthResultInfo>();
        loader.putPostValue("paramLogId", mSugarInfo.paramLogId + "");
        loader.putPostValue("value", mSugarInfo.value + "");
        loader.putPostValue("paramCode", mSugarInfo.paramCode);
        loader.putPostValue("option", getCoadString());
        loader.putPostValue("recordTime", mSugarInfo.recordTime);
        loader.putPostValue("memo", "");

        loader.loadBodyObject(HealthResultInfo.class, ConfigUrlMrg.MODIFY_MEMBER_SUFFER_PARAM, loader.new CallBack() {
            @Override
            public void onBodyObjectSuccess(boolean isFromCache, HealthResultInfo obj) {
                super.onBodyObjectSuccess(isFromCache, obj);
                cancelProgressDialog();
                HealthRecordRusultFragment.toFragment(getActivity(), obj, 1);
            }

            @Override
            public boolean onFail(int code) {
                cancelProgressDialog();
                return super.onFail(code);

            }
        });
    }

    @Override
    public boolean onBackPress() {
        if (FragmentMrg.isContain(RecordDietIndexFragment.class)) {
            FragmentMrg.popBackToFragment(getActivity(), RecordDietIndexFragment.class, new Bundle());
        } else if (FragmentMrg.isContain(RecordMainFragment.class)) {
            FragmentMrg.popBackToFragment(getActivity(), RecordMainFragment.class, new Bundle());
        } else {
            IndexFrag.toFragment(getActivity(), true);
        }
        return true;
    }

    public class OptionAdapter extends ComveeBaseAdapter<HealthResultInfo.Options> {

        private int mCount = 0;

        public OptionAdapter() {
            super(TNBApplication.getInstance(), R.layout.record_detail_item);
        }

        public void select(int mSelectedIndex) {
            if (getDatas().size() > mSelectedIndex) {
                getItem(mSelectedIndex).isSelected = !getItem(mSelectedIndex).isSelected;
                notifyDataSetChanged();
            }

        }

        @Override
        public int getCount() {
            return mCount;
        }

        @Override
        public void setDatas(List<HealthResultInfo.Options> datas) {
            super.setDatas(datas);
            //将Count整成 3的倍数
            if (datas != null) {
                mCount = datas.size();
                mCount = mCount + (3 - mCount % 3);
            }
        }

        @Override
        protected void getView(ViewHolder holder, int position) {
            View layout = holder.get(R.id.layout);
            TextView tv_value = holder.get(R.id.tv_value);
            ImageView select = holder.get(R.id.img_select);
            ImageView img_value = holder.get(R.id.img_value);

            if (position > mListDatas.size() - 1) {
                layout.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.color_gray_bg)));
                select.setVisibility(View.INVISIBLE);
                tv_value.setVisibility(View.INVISIBLE);
                img_value.setVisibility(View.INVISIBLE);
            } else {

                select.setVisibility(View.VISIBLE);
                tv_value.setVisibility(View.VISIBLE);
                img_value.setVisibility(View.VISIBLE);
                layout.setBackgroundDrawable(null);
                HealthResultInfo.Options info = getItem(position);

                AppUtil.loadImageByLocationAndNet(img_value, info.photo);
                tv_value.setText(info.text);
                if (info.isSelected) {
                    select.setBackgroundResource(R.drawable.check_style_1_b);
                    tv_value.setTextColor(Color.parseColor("#333333"));
                } else {
                    select.setBackgroundResource(R.drawable.record_unselect);
                    tv_value.setTextColor(Color.parseColor("#999999"));
                }
            }

        }

    }
}
