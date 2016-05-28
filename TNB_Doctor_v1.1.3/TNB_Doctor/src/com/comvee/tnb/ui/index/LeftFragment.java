package com.comvee.tnb.ui.index;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.ThreadHandler;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.DrawerMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.dialog.CustomDialog;
import com.comvee.tnb.radio.DownLoadLocalFragment;
import com.comvee.tnb.radio.RadioCollectFrag;
import com.comvee.tnb.ui.machine.BarCodeFragment;
import com.comvee.tnb.ui.machine.MachineListFragment;
import com.comvee.tnb.ui.member.MemberRecordFragment;
import com.comvee.tnb.ui.money.MyWalletFragment;
import com.comvee.tnb.ui.more.CollectionListFragment;
import com.comvee.tnb.ui.more.MoreFragment;
import com.comvee.tnb.ui.more.MouthSummarize;
import com.comvee.tnb.ui.more.WebFragment;
import com.comvee.tnb.ui.more.WebNewFrag;
import com.comvee.tnb.ui.pay.PayMineOrderFragment;
import com.comvee.tnb.ui.privatedoctor.MemberDoctorListFrag;
import com.comvee.tnb.ui.task.MyTaskCenterFragment;
import com.comvee.tnb.ui.user.LoginFragment;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.tool.UserMrg;

/**
 * 左侧边栏
 *
 * @author friendlove
 */
public class LeftFragment extends BaseFragment implements OnClickListener {

    TextView tv_msg_count;
    private ImageView imgHead;
    private TextView tvEquipment;
    private boolean safeJump = true;
    private TextView tv_member_name;

    public LeftFragment() {
    }

    public static LeftFragment newInstance() {
        LeftFragment fragment = new LeftFragment();
        return fragment;
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.left_fragment;
    }

    @Override
    public void onLaunch(Bundle bundle) {
        super.onLaunch(bundle);
        init();
    }

    private void showMachineOptionDialog() {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case CustomDialog.ID_OK:
                        BarCodeFragment.toFragment(getActivity(), true, 1);
                        DrawerMrg.getInstance().close();
                        break;
                    case CustomDialog.ID_NO:
                        WebFragment fragment = WebFragment.newInstance(
                                "购买设备",
                                ConfigParams.getConfig(getApplicationContext(), ConfigParams.TEXT_MECHINE_INFO) + "?origin=android&sessionID="
                                        + UserMrg.getSessionId(getApplicationContext()) + "&sessionMemberID="
                                        + UserMrg.getMemberSessionId(getApplicationContext()), 1);
                        fragment.setSliding(true);
                        toFragment(fragment, true);
                        DrawerMrg.getInstance().close();

                        break;
                    case 2:
                        // dialog.cancel();
                        break;
                    default:
                        break;
                }
            }

        };

        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setMessage("您尚未绑定血糖仪设备，是否绑定？");
        builder.setPositiveButton("立即绑定", listener);
        builder.setNegativeButton("购买设备", listener);
        builder.setVisible(View.VISIBLE);
        builder.create().show();
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.btn_index:
                IndexFrag.toFragment(getActivity(), false);
                break;
            case R.id.btn_my_download:
                toFragment(DownLoadLocalFragment.class, null, false);
                break;
            case R.id.btn_server:
                MemberDoctorListFrag.toFragment(getActivity(), true, 1);
                break;
            case R.id.btn_wallet:
                MyWalletFragment.toFragment(getActivity(), true);
                break;
            case R.id.btn_msg:
                if (!ConfigParams.IS_TEST_DATA) {
                    MessageCentreFragment.toFragment(getActivity(), true);
                } else {
                    LoginFragment.toFragment(getActivity(), false);
                }
                break;
            case R.id.btn_my_collect:
                RadioCollectFrag.toFragment(getActivity(), CollectionListFragment.WHERE_INDEX, false);

                break;
            case R.id.btn_paylist_mrg:
                String mTurnUrl = ConfigParams.getConfig(getApplicationContext(), ConfigParams.MYORDER_URL);
                mTurnUrl += String.format("?sessionID=%s&sessionMemberID=%s", UserMrg.getSessionId(getContext()), UserMrg.getMemberSessionId(getContext()));
                WebNewFrag.toFragment(getActivity(), "订单管理", mTurnUrl);
                break;

            case R.id.btn_month_info:
                MouthSummarize.toFragment(getActivity(), true);
                break;
            case R.id.tv_member_name:
            case R.id.img_photo:
                if (safeJump) {
                    safeJump = false;
                    ThreadHandler.postUiThread(new Runnable() {

                        @Override
                        public void run() {
                            toFragment(MemberRecordFragment.newInstance(0, 1, true), true);
                            safeJump = true;
                        }
                    }, 450);
                }
                break;
            case R.id.btn_set:
                toMore();
                break;
            case R.id.btn_folk:
                MemberListFrag.toFragment(getActivity(), true);
                break;
            case R.id.btn_my_task:
                Bundle bundle1 = new Bundle();
                bundle1.putBoolean("isSliding", true);
                toFragment(MyTaskCenterFragment.class, bundle1, false);
                break;
            case R.id.btn_equipment:
                if (UserMrg.DEFAULT_MEMBER.hasMachine == 1) {
                    MachineListFragment.toFragment(getActivity(), true);
                } else {
                    showMachineOptionDialog();
                    return;
                }
            default:
                break;
        }

        DrawerMrg.getInstance().close();

    }

    @Override
    public boolean onBackPress() {
        return false;
    }

    private void toMore() {
        MoreFragment txt = MoreFragment.newInstance(true);
        toFragment(txt, true);
    }

    private void init() {

        imgHead = (ImageView) findViewById(R.id.img_photo);
        tvEquipment = (TextView) findViewById(R.id.tv_equipment);
        tv_msg_count = (TextView) getView().findViewById(R.id.tv_msg_count);
        tv_member_name = (TextView) findViewById(R.id.tv_member_name);
        imgHead.setOnClickListener(this);

        findViewById(R.id.btn_index).setOnClickListener(this);
        findViewById(R.id.btn_wallet).setOnClickListener(this);
        findViewById(R.id.btn_server).setOnClickListener(this);
        View msgView = findViewById(R.id.btn_msg);
        // 先设置gone
        msgView.setVisibility(View.VISIBLE);
        msgView.setOnClickListener(this);
        findViewById(R.id.img_photo).setOnClickListener(this);
        findViewById(R.id.btn_my_task).setOnClickListener(this);
        findViewById(R.id.btn_my_collect).setOnClickListener(this);
        findViewById(R.id.btn_paylist_mrg).setOnClickListener(this);
        findViewById(R.id.btn_month_info).setOnClickListener(this);
        findViewById(R.id.btn_set).setOnClickListener(this);
        findViewById(R.id.btn_folk).setOnClickListener(this);
        findViewById(R.id.btn_equipment).setOnClickListener(this);
        findViewById(R.id.tv_member_name).setOnClickListener(this);
        findViewById(R.id.btn_my_download).setOnClickListener(this);
        initView();
    }

    public void initView() {
        if (tv_msg_count != null) {
            int msgSysCount = ConfigParams.getMsgSysCount(TNBApplication.getInstance());
            if (msgSysCount > 0 && msgSysCount < 100) {
                tv_msg_count.setVisibility(View.VISIBLE);
                tv_msg_count.setText(msgSysCount + "");
            } else if (msgSysCount >= 100) {
                tv_msg_count.setVisibility(View.VISIBLE);
                tv_msg_count.setText("..");
            } else {
                tv_msg_count.setVisibility(View.GONE);
            }
        }

        if (tv_member_name != null && tvEquipment != null) {
            // UITool.setTextView(getView(), R.id.tv_member_name,
            // UserMrg.DEFAULT_MEMBER.name);
            // tvEquipment = (TextView) findViewById(R.id.tv_equipment);
            try {
                tv_member_name.setText(UserMrg.DEFAULT_MEMBER.name == null ? "" : UserMrg.DEFAULT_MEMBER.name);
                if (UserMrg.DEFAULT_MEMBER.hasMachine == 1) {
                    tvEquipment.setText("(已绑定)");
                } else {
                    tvEquipment.setText("(未绑定)");
                }
            } catch (Exception e) {
                e.printStackTrace();
                // com.comvee.tool.Log.e("侧边栏错误");
            }
            if (!TextUtils.isEmpty(UserMrg.DEFAULT_MEMBER.photo) && !UserMrg.DEFAULT_MEMBER.photo.equalsIgnoreCase("null") && imgHead != null) {
                ImageLoaderUtil.getInstance(mContext).displayImage(UserMrg.DEFAULT_MEMBER.photo, imgHead, ImageLoaderUtil.user_options);
            }
        }
    }

    public void updateView() {
        try {
            initView();
//            if (!TextUtils.isEmpty(UserMrg.DEFAULT_MEMBER.photo) && !UserMrg.DEFAULT_MEMBER.photo.equalsIgnoreCase("null") && imgHead != null) {
//                ImageLoaderUtil.getInstance(mContext).displayImage(UserMrg.DEFAULT_MEMBER.photo, imgHead, ImageLoaderUtil.user_options);
//            }
//            tv_member_name.setText(UserMrg.DEFAULT_MEMBER.name == null ? "" : UserMrg.DEFAULT_MEMBER.name);
//            try {
//                if(UserMrg.DEFAULT_MEMBER!=null){
//                    if (UserMrg.DEFAULT_MEMBER.hasMachine == 1) {
//                        tvEquipment.setText("(已绑定)");
//                    } else {
//                        tvEquipment.setText("(未绑定)");
//                    }
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
