package com.comvee.tnb.ui.machine;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comvee.tnb.DrawerMrg;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.CustomDialog;
import com.comvee.tnb.dialog.ShadeActivityDialog;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.MachineInfo;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.ui.more.UseGuideFragment;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.AppUtil;
import com.comvee.tool.UserMrg;
import com.comvee.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 设备管理
 *
 * @author friendlove
 */
public class MachineListFragment extends BaseFragment implements OnClickListener, OnHttpListener, OnItemLongClickListener, OnItemClickListener,
ShadeActivityDialog.ShadeClickListener{

    private MachineAdapter mAdapter;
    private GridView mGridView;
    private ArrayList<MachineInfo> mListItems;
    private TextView textView;
    private ImageView ImageView;
    private RelativeLayout layout;
    private boolean isSliding;
    private TitleBarView mBarView;
    private int fromWhere = 1;// 1表示从左侧栏跳转
    private int status;// 1/编辑模式0/normal模式

    public static MachineListFragment newInstance(boolean isSliding) {
        MachineListFragment fragment = new MachineListFragment();
        fragment.setSliding(isSliding);
        return fragment;
    }

    public static void toFragment(FragmentActivity fragment, boolean isSliding) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isSliding", isSliding);
        bundle.putInt("from_where", 1);
        boolean isAnima = true;
        if (isSliding) {
            isAnima = false;
        }
        FragmentMrg.toFragment(fragment, MachineListFragment.class, bundle, isAnima);
    }

    public void setSliding(boolean isSliding) {
        this.isSliding = isSliding;
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.fragemnt_machinelist;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onLaunch(Bundle bundle) {
        super.onLaunch(bundle);
        if (bundle != null) {
            isSliding = bundle.getBoolean("isSliding");
            fromWhere = bundle.getInt("from_where");
        }

        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);

        init();
        mBarView.setTitle("设备管理");

        requestMachineList();
        if (Util.checkFirst(getApplicationContext(), "machinelist")) {
            AppUtil.initLuanchWindow(getActivity(), R.drawable.machine_guide,this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void init() {
        layout = (RelativeLayout) findViewById(R.id.grid_notic);
        textView = (TextView) findViewById(R.id.guide_text);
        ImageView = (android.widget.ImageView) findViewById(R.id.grid_remove);
        textView.setOnClickListener(this);
        ImageView.setOnClickListener(this);
        layout.setVisibility(View.VISIBLE);
        mGridView = (GridView) findViewById(R.id.grid_machine);
        mGridView.setOnItemLongClickListener(this);
        mGridView.setOnItemClickListener(this);
        mAdapter = new MachineAdapter();
        mGridView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TitleBarView.ID_LEFT_BUTTON:
                getActivity().onBackPressed();
                break;
            case R.id.guide_text:
                toFragment(UseGuideFragment.newInstance(), true, true);
                break;
            case R.id.grid_remove:
                layout.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public boolean onBackPress() {
        if (fromWhere == 1) {
            IndexFrag.toFragment(getActivity(),true); 
            return true;
        }
        return super.onBackPress();
    }

    // 绑定设备
    private void requestMachineList() {
        showProgressDialog("正在加载...");
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.MACHINE_LIST);
        http.setOnHttpListener(1, this);
        http.startAsynchronous();
    }

    private void requestRemoveMachine(MachineInfo info) {
        showProgressDialog("正在移除设备...");
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.MACHINE_REMOVE);
        http.setPostValueForKey("machineId", info.machineId);
        http.setPostValueForKey("machineType", info.machineType);
        http.setOnHttpListener(2, this);
        http.startAsynchronous();
    }

    @Override
    public void onSussece(int what, byte[] b, boolean fromCache) {
        cancelProgressDialog();

        try {
            ComveePacket packet = ComveePacket.fromJsonString(b);
            if (packet.getResultCode() == 0) {
                if (what == 1) {
                    mListItems = new ArrayList<MachineInfo>();
                    JSONArray array = packet.getJSONArray("body");
                    int len = array.length();
                    JSONObject obj = null;
                    for (int i = 0; i < len; i++) {
                        obj = array.optJSONObject(i);
                        MachineInfo info = new MachineInfo();
                        info.insertDt = obj.optString("insertDt");
                        info.machineId = obj.optString("machineId");
                        info.machineType = obj.optString("machineType");
                        info.memberId = obj.optString("memberId");
                        mListItems.add(info);
                    }
                    mAdapter.notifyDataSetChanged();
                } else {

                    mAdapter.removeMachine(mAdapter.getRemoveMachine());
                    if (mAdapter.getCount() == 1) {
                        UserMrg.DEFAULT_MEMBER.hasMachine = 0;
                        DrawerMrg.getInstance().updateLefFtagment();
                    }
                }
                DrawerMrg.getInstance().updateLefFtagment();
            } else {
                ComveeHttpErrorControl.parseError(getActivity(), packet);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFialed(int what, int errorCode) {
        cancelProgressDialog();
        ComveeHttpErrorControl.parseError(getActivity(), errorCode);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if (arg2 == mAdapter.getCount() - 1) {
            toAddMachine();
        } else {

        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if (arg2 == mAdapter.getCount() - 1) {
            toAddMachine();
        } else {
            status = 1 - status;
            mAdapter.notifyDataSetChanged();
        }

        return false;
    }

    private void toAddMachine() {
        BarCodeFragment.toFragment(getActivity(), false, 1);
    }

    class MachineAdapter extends BaseAdapter implements View.OnClickListener, DialogInterface.OnClickListener {

        private MachineInfo mRemoveInfo;

        @Override
        public int getCount() {
            return mListItems == null ? 1 : mListItems.size() + 1;
        }

        @Override
        public Object getItem(int arg0) {
            return mListItems.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {
            ViewHolder holder = null;
            if (arg1 == null) {

                holder = new ViewHolder();
                arg1 = View.inflate(getApplicationContext(), R.layout.item_machine, null);
                holder.tvLabel = (TextView) arg1.findViewById(R.id.tv_label);
                holder.imgLogo = (ImageView) arg1.findViewById(R.id.iv_logo);
                holder.layoutMachine = arg1.findViewById(R.id.layout_machine);
                holder.btnRemove = arg1.findViewById(R.id.btn_remove);
                holder.layoutAdd = (ImageView) arg1.findViewById(R.id.layout_add);

                arg1.setTag(holder);

            } else {
                holder = (ViewHolder) arg1.getTag();
            }

            if (arg0 == getCount() - 1) {
                holder.layoutAdd.setVisibility(View.VISIBLE);
                holder.layoutMachine.setVisibility(View.GONE);
                holder.layoutAdd.setImageResource(R.drawable.machine_add);
                holder.btnRemove.setVisibility(View.GONE);
                holder.btnRemove.setOnClickListener(null);
            } else {
                MachineInfo info = mListItems.get(arg0);
                holder.layoutAdd.setVisibility(View.GONE);
                holder.layoutMachine.setVisibility(View.VISIBLE);
                holder.imgLogo.setImageResource(info.machineType.equals("02") ? R.drawable.machine_sugarblood : R.drawable.machine_highblood);
                holder.tvLabel.setText(info.machineType.equals("02") ? "血糖仪器" : "血压仪器");
                holder.btnRemove.setVisibility(status == 1 ? View.VISIBLE : View.GONE);
                holder.btnRemove.setTag(info);
                holder.btnRemove.setOnClickListener(this);
                // Animation cycleAnim =
                // AnimationUtils.loadAnimation(getApplicationContext(),
                // R.anim.imag_anim);
                // holder.imgLogo.startAnimation(cycleAnim);
            }

            return arg1;
        }

        public MachineInfo getRemoveMachine() {
            return mRemoveInfo;
        }

        public void removeMachine(MachineInfo mRemoveInfo) {
            mListItems.remove(mRemoveInfo);
            status = 0;
            notifyDataSetChanged();
        }

        @Override
        public void onClick(View v) {
            mRemoveInfo = (MachineInfo) v.getTag();
            CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
            builder.setMessage("你是否要解除该血糖仪绑定？");
            builder.setPositiveButton("确定", this);
            builder.setNegativeButton("取消", null);
            builder.create().show();
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            requestRemoveMachine(mRemoveInfo);
        }

        class ViewHolder {
            View layoutMachine;
            TextView tvLabel;
            ImageView imgLogo, layoutAdd;
            View btnRemove;
        }

    }

	@Override
	public void onShadeClick() {
		// TODO Auto-generated method stub
		
	}
}
