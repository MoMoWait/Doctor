package com.comvee.tnb.ui.index;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.comvee.frame.BaseFragmentActivity;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.adapter.ComveePageAdapter;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.ComveeTask;
import com.comvee.tnb.model.DiscoverModel;
import com.comvee.tnb.ui.more.WebFragment;
import com.comvee.tnb.ui.task.TaskCenterFragment;
import com.comvee.tnb.ui.tool.BloodSugarFragment;
import com.comvee.tnb.ui.tool.DailyCaloriesFragment;
import com.comvee.tnb.ui.tool.HeatFragment;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.AppUtil;
import com.comvee.tool.UserMrg;
import com.comvee.util.JsonHelper;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * 发现模块 5.0.2版本后以废弃
 *
 * @author Administrator
 */
public class DiscoverFragment extends BaseFragment implements OnClickListener {
    private ViewPager mViewPage;
    private MyPageAdapter mAdapter;

    private View adView = null;// 广告控件
    private TitleBarView mBarView;
    private ArrayList<DiscoverModel> arrayList = new ArrayList<DiscoverModel>();
    private int curIndex;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    try {
                        mHandler.removeMessages(0);
                        mViewPage.setCurrentItem(curIndex++ % mAdapter.getCount(), true);
                        mHandler.sendEmptyMessageDelayed(0, 5000);
                        // mAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Log.e("handler error");
                    }
                    break;
            }
        }

        ;
    };

    public DiscoverFragment() {
    }

    public static DiscoverFragment newInstance() {
        DiscoverFragment frag = new DiscoverFragment();
        return frag;
    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public boolean onBackPress() {
        ((BaseFragmentActivity) getActivity()).tryExit();
        return true;
    }

    @Override
    public void onLaunch(Bundle dataBundle) {
        // TODO Auto-generated method stub
        super.onLaunch(dataBundle);
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setTitle("发现");
        init();
        mBarView.hideLeftButton();
    }

    private void init() {
        // TODO Auto-generated method stub
        loadAdBan();

        // findViewById(R.id.ad).setOnClickListener(this);
        // 食物宜忌
        findViewById(R.id.btn_discovery_food_benefit).setOnClickListener(this);
        // 健康任务
        findViewById(R.id.btn_discovery_health_task).setOnClickListener(this);
        // 热量计算
        findViewById(R.id.btn_discovery_hot_cal).setOnClickListener(this);
        // 糖糖资讯
        findViewById(R.id.btn_discovery_wait).setOnClickListener(this);
        // 升糖指数
        findViewById(R.id.btn_discovery_sugar_rise).setOnClickListener(this);

        // mAdapter.notifyDataSetChanged();

    }

    private void loadAdBan() {
        // TODO Auto-generated method stub
        new ComveeTask<String>() {
            @SuppressWarnings("unchecked")
            @Override
            protected String doInBackground() {
                try {
                    String result = ComveeHttp.getCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.INDEX_DISCOVERY_BANNAR));
                    if (result == null) {
                        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.INDEX_DISCOVERY_BANNAR);
                        result = http.startSyncRequestString();
                        if (result != null) {
                            ComveeHttp.setCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.INDEX_DISCOVERY_BANNAR),
                                    ConfigParams.DAY_TIME_LONG, result);
                        }
                    }
                    // Log.i("发现模块轮播图-->" + result);
                    final ComveePacket packet = ComveePacket.fromJsonString(result);

                    if (packet.getResultCode() != 0) {
                        postError(packet);
                        return null;
                    }
                    JSONArray array = packet.optJSONArray("body");
                    if (array == null) {
                        return null;
                    }
                    arrayList = (ArrayList<DiscoverModel>) JsonHelper.getListByJsonArray(DiscoverModel.class, array);

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                return "1000";
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                cancelProgressDialog();
                if (result == null) {
                    showToast(R.string.time_out);
                } else {
                    initAd(result);
                }

            }

        }.execute();

    }

    // 初始化广告条
    private void initAd(String json) {
        mViewPage = (ViewPager) findViewById(R.id.view_pager);
        mAdapter = new MyPageAdapter();
        mViewPage.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mViewPage.setCurrentItem(0);
        mHandler.sendEmptyMessage(0);
    }

    // 广告点击
    private void clickAd(View view) {
        DiscoverModel model = (DiscoverModel) view.getTag();
        if (model == null) {
            return;
        }
        if (model.type == 22) {
            toFragment(WebFragment.newInstance(model.proclamationTitle, model.proclamationUrl, true), true);
        } else {
            toFragment(WebFragment.newInstance(model.proclamationTitle, model.proclamationUrl), true);
        }

    }

    @Override
    public int getViewLayoutId() {
        // TODO Auto-generated method stub
        return R.layout.index_discovery_frag;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_discovery_food_benefit:
                toFragment(HeatFragment.class, null, true);
                break;
            case R.id.btn_discovery_health_task:
                toFragment(TaskCenterFragment.class, null, true);
                break;
            case R.id.btn_discovery_hot_cal:
                toFragment(DailyCaloriesFragment.class, null, true);
                break;
            case R.id.btn_discovery_wait:
                break;
            case R.id.btn_discovery_sugar_rise:
                toFragment(BloodSugarFragment.class, null, true);
                break;
            case R.id.ad:
                clickAd(v);
                break;
        }

    }

    class MyPageAdapter extends ComveePageAdapter {

        @Override
        public View getView(int position) {
            if (arrayList.size() == 0) {
                return null;
            }
            DiscoverModel model = arrayList.get(position);
            try {
                adView = View.inflate(getApplicationContext(), R.layout.discovery_ad_bar, null);
                ImageView iv = (ImageView) adView.findViewById(R.id.ad_img);
                AppUtil.loadImageByLocationAndNet(iv, model.proclamationBanner);
                adView.setTag(model);
                adView.setOnClickListener(DiscoverFragment.this);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return adView;
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

    }

}
