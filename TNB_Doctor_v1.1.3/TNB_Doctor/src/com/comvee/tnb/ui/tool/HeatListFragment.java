package com.comvee.tnb.ui.tool;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.adapter.HeatListAdapter;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.model.FoodInfo;
import com.comvee.tnb.model.HeatInfo;
import com.comvee.tnb.ui.book.BookWebActivity;
import com.comvee.tnb.ui.record.diet.SwapDietFragment;
import com.comvee.tnb.ui.xlistview.XListView;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tnb.widget.pageview.PageViewControl;
import com.comvee.util.BundleHelper;

/**
 * 食物库
 *
 * @author friendlove-pc
 */
public class HeatListFragment extends BaseFragment implements OnClickListener, OnItemClickListener {

    private XListView mListView;
    private HeatListAdapter mAdapter;
    private LinearLayout toSearchLayout;
    private String categoryId;
    private TitleBarView mBarView;
    private String foodId = "";
    private int fromwhere = -1;
    private PageViewControl mControl;

    public HeatListFragment() {
    }

    public static void toFragment(FragmentActivity fragment, String id, String foodId, int fromWhere) {
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("foodid", foodId);
        bundle.putInt("fromwhere", fromWhere);
        FragmentMrg.toFragment(fragment, HeatListFragment.class, bundle, true);
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.fragment_heat;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onLaunch(Bundle bundle) {
        super.onLaunch(bundle);
        if (bundle != null) {
            categoryId = bundle.getString("id");
            foodId = bundle.getString("foodid");
            fromwhere = bundle.getInt("fromwhere", -1);

        }
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        mBarView.setTitle("食物库");

        toSearchLayout = (LinearLayout) findViewById(R.id.tosearch);
        toSearchLayout.setOnClickListener(this);
        mListView = (XListView) findViewById(R.id.lv_heat);
        mListView.setOnItemClickListener(this);
        mAdapter = new HeatListAdapter();
        mListView.setAdapter(mAdapter);

        showProgressDialog("请稍候...");
        mControl = new PageViewControl(mListView, HeatInfo.class, mAdapter, ConfigUrlMrg.HEAT_SECOND, new PageViewControl.onPageViewListenerAdapter() {
            @Override
            public void onStopLoading() {
                cancelProgressDialog();
            }
        });
        mControl.putPostValue("parentId", categoryId);
        mControl.putPostValue("order", "desc");
        if (fromwhere == HeatFragment.FROM_SELECT_EXCHANGE_FOOD) {
            mControl.putPostValue("status", "2");
        }
        mControl.load();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HeatInfo info = mAdapter.getItem(position - 1);
        FoodInfo info2 = new FoodInfo();
        info2.id = info.type;
        info2.heat = info.heat;
        info2.imgUrl = info.picurl;
        info2.name = info.name;
        info2.weight = info.weight;
        info2.eatAdvice = info.foodleveltext;
        info2.foodId = info.id;
        if (fromwhere != -1) {
            if (info.heat == 0 || info.weight == 0) {
                showToast("数据错误，请重新选择");
                return;
            }
            FragmentMrg.popBackToFragment(getActivity(), SwapDietFragment.class, BundleHelper.getBundleBySerializable(info2));
        } else {
            BookWebActivity.toWebActivity(getActivity(), BookWebActivity.FOOD, null, null, info.urlAddress, info.id);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tosearch:
                SearchFragment.toFragment(getActivity(), categoryId, foodId, fromwhere);
                break;
            default:
                break;
        }

    }

}
