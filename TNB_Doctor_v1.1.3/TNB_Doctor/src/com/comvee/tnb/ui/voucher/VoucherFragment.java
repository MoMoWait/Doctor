package com.comvee.tnb.ui.voucher;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.comvee.tnb.DrawerMrg;
import com.comvee.frame.FragmentMrg;
import com.comvee.network.BackgroundTasks;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.network.DataHelper;
import com.comvee.tnb.network.DataHelper.PostFinishInterface;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.ui.money.MyWalletFragment;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.ResUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的卡券
 *
 * @author Administrator
 */
public class VoucherFragment extends BaseFragment implements OnItemClickListener, PostFinishInterface {
    private VoucherListlAdapter adapter;
    private ListView listView;

    private SparseArray<List<VoucherModel>> allDatas = new SparseArray<List<VoucherModel>>();

    private boolean isCancleRequest = false;// 切换卡页时需要
    private int currentType = 1;
    private boolean isSliding;
    private int fromWhere;// 1表示从左侧栏跳转

    public static void toFragment(FragmentActivity fragment, boolean isSliding) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isSliding", isSliding);
        bundle.putInt("from_where", 3);
        boolean isAnima = true;
        if (isSliding) {
            isAnima = false;
        }
        FragmentMrg.toFragment(fragment, VoucherFragment.class, bundle, true);
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.voucher_fragment;
    }

    @Override
    public void onLaunch(Bundle dataBundle) {
        // TODO Auto-generated method stub
        super.onLaunch(dataBundle);
        if (dataBundle != null) {
            isSliding = dataBundle.getBoolean("isSliding");
            fromWhere = dataBundle.getInt("from_where");
        }
        if (isSliding) {
            DrawerMrg.getInstance().close();
        }
        TitleBarView titlebar = (TitleBarView) findViewById(R.id.main_titlebar_view);
        titlebar.setTitle(ResUtil.getString(R.string.my_voucher));
        titlebar.setLeftDefault(this);

        // 依次是已使用，已过期 ，未使用
        if (allDatas.size() == 0) {
            for (int i = 1; i < 4; i++) {
                allDatas.append(i, new ArrayList<VoucherModel>());
            }
        }
        listView = (ListView) findViewById(R.id.listview);
        if (allDatas.get(1).size() == 0 && allDatas.get(2).size() == 0 && allDatas.get(3).size() == 0) {
            requestMedicinalList(1);
        } else {
            adapter = new VoucherListlAdapter(getApplicationContext(), allDatas.get(currentType), R.layout.item_voucherlist, currentType);
            listView.setAdapter(adapter);
        }
        listView.setOnItemClickListener(this);
        initStyleTabs();

    }

    private void initStyleTabs() {
        LinearLayout indicators = ((LinearLayout) findViewById(R.id.text_and_indicator));
        final ViewGroup[] indicatorArray = new ViewGroup[indicators.getChildCount()];
        for (int i = 0; i < indicatorArray.length; i++) {
            final int currentindex = i;
            indicatorArray[i] = (ViewGroup) indicators.getChildAt(i);
            indicatorArray[i].setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < indicatorArray.length; i++) {
                        ViewGroup vg = indicatorArray[i];
                        TextView name = (TextView) vg.getChildAt(0);
                        View indicator = vg.getChildAt(1);
                        if (i == currentindex) {
                            name.setTextColor(getResources().getColor(R.color.theme_color_green));
                            indicator.setVisibility(View.VISIBLE);
                            doOnclickSth(currentindex);
                        } else {
                            name.setTextColor(Color.parseColor("#333333"));
                            indicator.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            });
        }
    }

    /**
     * @param currentindex
     */
    protected void doOnclickSth(int currentindex) {
        TextView noData = (TextView) findViewById(R.id.tv_voucher_no_data);
        currentType = 1;
        switch (currentindex) {
            case 0:
                currentType = 1;
                noData.setText(getText(R.string.voucher_no_data).toString());
                break;
            case 1:
                currentType = 3;
                noData.setText(getText(R.string.voucher_no_data_2).toString());
                break;
            case 2:
                currentType = 2;
                noData.setText(getText(R.string.voucher_no_data_1).toString());
                break;
        }
        listView.setAdapter(null);
        if (allDatas.get(currentType).size() > 0) {
            isCancleRequest = true;
            listView.setAdapter(new VoucherListlAdapter(getApplicationContext(), allDatas.get(currentType), R.layout.item_voucherlist, currentType));
        } else {
            isCancleRequest = false;
            requestMedicinalList(currentType);
        }
    }

    /**
     * @param type 1：未使用；3：已使用；2：已过期
     */
    private void requestMedicinalList(int type) {
        DataHelper dataHelper = new DataHelper(ConfigUrlMrg.GET_VOUCHER);
        dataHelper.putPostValue("type", type + "");
        dataHelper.setPostFinishInterface(this, type);
        dataHelper.start();
      //  BackgroundTasks.getInstance().push(BackgroundTasks.TASK_NETWORK, dataHelper);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        VoucherListlAdapter voucherListlAdapter = (VoucherListlAdapter) listView.getAdapter();
        int type = voucherListlAdapter.getType();
        if (type == 1) {
            toFragment(new VoucherDetailFragment(allDatas.get(type).get(position)), true, true);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void postFinish(int status, Object obj, int tag) {
        if (isCancleRequest) {
            return;
        }
        listView.setEmptyView(findViewById(R.id.nodata));
        if (status != 0) {
            Toast.makeText(getApplicationContext(), obj + "", Toast.LENGTH_LONG).show();
        }
        List<VoucherModel> list = (List<VoucherModel>) obj;
        if (list != null) {
            allDatas.put(tag, list);
        }
        adapter = new VoucherListlAdapter(getApplicationContext(), allDatas.get(currentType), R.layout.item_voucherlist, currentType);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onBackPress() {
        if (fromWhere == 1) {
            MyWalletFragment.toFragment(getActivity(), true);
            return true;
        } else
            return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
