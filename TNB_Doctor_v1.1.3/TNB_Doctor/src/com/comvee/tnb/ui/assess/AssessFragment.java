package com.comvee.tnb.ui.assess;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import com.comvee.ThreadHandler;
import com.comvee.frame.BaseFragmentActivity;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.CustomDialog;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.AssessIndexInfo;
import com.comvee.tnb.model.Package;
import com.comvee.tnb.network.ObjectLoader;
import com.comvee.tnb.ui.member.MemberRecordFragment;
import com.comvee.tnb.ui.more.WebFragment;
import com.comvee.tnb.ui.user.LoginFragment;
import com.comvee.tnb.view.IndexBottomView;
import com.comvee.tnb.view.QuestionHelpWindow;
import com.comvee.tnb.view.SingleInputItemWindow;
import com.comvee.tnb.widget.TitleBarView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 评估页面
 *
 * @author comv048
 */

public class AssessFragment extends BaseFragment implements OnClickListener, ViewFactory, DialogInterface.OnClickListener, OnHttpListener {
    private ArrayList<Package> listItems;
    private View btnAssessDoc;
    private Button btnAssessSelf;

    private int indexPackage;
    private TextView tvLimit;
    private boolean isLimit;

    private boolean safeJudge = true;

    private int[] mRes = {R.id.tv_num0, R.id.tv_num1, R.id.tv_num2, R.id.tv_num3, R.id.tv_num4, R.id.tv_num5,};
    private TitleBarView mBarView;
    private int[] time = new int[]{210, 200, 190, 180, 170, 160, 150};
    private IndexBottomView bottomView;

    public AssessFragment() {
    }

    public static AssessFragment newInstance() {
        return new AssessFragment();
    }

    public static void toFragment(FragmentActivity act, boolean anim) {
        FragmentMrg.popSingleFragment(act, AssessFragment.class, null, anim);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.fragment_assess_before_info1;
    }

    @Override
    public void onLaunch(Bundle bundle) {
        bottomView = (IndexBottomView) findViewById(R.id.layout_frame_bottom);
        bottomView.bindFragment(this);
        bottomView.selectAessess();

        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        init();
        mBarView.setTitle(getString(R.string.title_assess));
        mBarView.setRightButton(getString(R.string.assess_right_text), this);
        mBarView.setTitleBarBackgroundColor(getResources().getColor(android.R.color.transparent));
        mBarView.setTextColor(getResources().getColor(R.color.white), getResources().getColor(R.color.white), getResources().getColor(R.color.white));
        mBarView.findViewById(R.id.titlebar_line).setVisibility(View.GONE);


        showProgressDialog(getString(R.string.msg_loading));
    }

    @Override
    public void onResume() {
        super.onResume();
        requestAssessCheck();
    }

    public void updateBottomView() {
        if (bottomView != null)
            bottomView.update();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void init() {
        tvLimit = (TextView) findViewById(R.id.tv_limit);
        final View btnToAbout = findViewById(R.id.btn_to_assess_abount);
        btnToAbout.setOnClickListener(this);
        final View btnToList = findViewById(R.id.btn_assess_list);
        btnToList.setOnClickListener(this);
        final View btnToDemo = findViewById(R.id.btn_assess_demo);
        btnToDemo.setOnClickListener(this);

        btnAssessDoc = findViewById(R.id.btn_assess_doc);
        btnAssessSelf = (Button) findViewById(R.id.btn_assess_self);

        btnAssessSelf.setOnClickListener(this);
        if (ConfigParams.IS_TEST_DATA) {
            btnToList.setVisibility(View.GONE);
            tvLimit.setVisibility(View.VISIBLE);
            tvLimit.setText("您还未登录，请登录后再进行评估");
        }

    }

    /**
     * 确定医生评估对话框
     *
     * @param memberPackageId
     */
    private void showToAssessDocDialog(final String memberPackageId) {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case CustomDialog.ID_OK:
                        requestToAssessDocDialog(memberPackageId);
                        break;
                    default:
                        break;
                }
            }
        };

        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setTitle("提示");
        builder.setMessage("您即将预约医生电话评估！\n医生评估时间：\n8：00～22：00\n客服热线：968696(按当地市话标准计费）");
        // builder.setMessage("");
        builder.setPositiveButton("确定", listener);
        builder.setNegativeButton("取消", listener);
        builder.create().show();
    }

    private void showNoServerDialog() {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == R.id.btn_ok) {
                    // showToast("申请服务");
                    // toApplyServer();
                }
            }
        };

        new CustomDialog.Builder(getActivity()).setMessage("您没有服务或医生评估次数已用完,赶快申请服务吧！").setTitle("提示").setPositiveButton("申请", listener)
                .setNegativeButton("取消", listener).create().show();
    }

    private void showTestDataDialog() {

        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setMessage("您目前是游客，无法进行健康评估，建议您注册/登录掌控糖尿病，获得权限。（掌控糖尿病活动期间，注册用户每天都将获得一次免费自助评估服务）");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                toFragment(LoginFragment.class, null, true);
            }
        });
        builder.create().show();

    }

    /**
     * 求情医生评估
     */
    private void requestToAssessDocDialog(final String memberPackageId) {

        showProgressDialog(getString(R.string.msg_loading));
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.ASSESS_APPLY_DOC);
        http.setPostValueForKey("selectPackage", memberPackageId);
        http.setOnHttpListener(2, this);
        http.startAsynchronous();
        // showToast("请求医生评估");
    }

    /**
     * 获取可用的套餐列表
     */
    private void requestPackageList() {
        showProgressDialog(getString(R.string.msg_loading));
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.SERVER_LIST_BY_SERVER);
        http.setPostValueForKey("serverCode", ConfigParams.PACKAGE_ASSESS);
        http.setOnHttpListener(1, this);
        http.startAsynchronous();
    }

    private void parsePackageList(byte[] arg1) {
        try {
            ComveePacket packet = ComveePacket.fromJsonString(arg1);
            if (packet.getResultCode() == 0) {

                boolean hasServer = packet.getJSONObject("body").optInt("hasServer") == 1;
                if (hasServer) {// 有服务

                    ArrayList<Package> list = new ArrayList<Package>();

                    JSONArray array = packet.getJSONObject("body").getJSONArray("list");
                    final int len = array.length();
                    for (int i = 0; i < len; i++) {
                        JSONObject obj = array.getJSONObject(i).getJSONObject("doctor");
                        final String perName = obj.optString("perName");
                        JSONArray itemArray = array.getJSONObject(i).getJSONArray("packages");
                        final int count = itemArray.length();
                        for (int j = 0; j < count; j++) {
                            JSONObject itemObj = itemArray.getJSONObject(j);

                            Package p = new Package();
                            p.perName = perName;
                            p.memberPackageId = itemObj.optString("memberPackageId");
                            p.leaveNum = itemObj.optInt("leaveNum");
                            p.packageName = itemObj.optString("packageName");
                            p.ownerId = itemObj.optString("ownerId");
                            list.add(p);
                        }
                    }
                    this.listItems = list;
                    final int length = list.size();
                    indexPackage = indexPackage >= length ? 0 : indexPackage;
                    if (length == 1) {
                        showToAssessDocDialog(listItems.get(indexPackage).memberPackageId);
                    } else {
                        showChosePackageDialog();
                    }
                } else {// 没有服务
                    showNoServerDialog();
                }

            } else {
                // showToast(packet.getResultMsg());

                ComveeHttpErrorControl.parseError(getActivity(), packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 申请服务
     */
    private void toApplyServer() {
        // ServerApplyFragment frag = ServerApplyFragment.newInstance();
        // toFragment(frag, true, true);
    }

    private void showChosePackageDialog() {

        if (listItems == null || listItems.isEmpty()) {
            showToast("套餐列表为空");
            return;
        }

        int len = listItems.size();
        String[] mPackageItems = new String[len];
        for (int i = 0; i < len; i++) {
            mPackageItems[i] = listItems.get(i).packageName;
        }

        SingleInputItemWindow dialog = new SingleInputItemWindow(getContext(), mPackageItems, "选择套餐", indexPackage);
        dialog.setOnItemClick(this);
        dialog.setOutTouchCancel(true);
        dialog.showAtLocation(mRoot, Gravity.CENTER, 0, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_assess_demo:
                String url = ConfigParams.getConfig(getApplicationContext(), ConfigParams.TEXT_ASSESS_DEMO);
                WebFragment.toFragment(getActivity(), "评估报告样例", url);
                break;
            case R.id.btn_assess_list:
                toFragment(AssessListFragment.newInstance(), true, true);
                break;
            case R.id.btn_to_assess_abount:
                WebFragment frag1 = WebFragment.newInstance("了解评估", ConfigParams.getConfig(getApplicationContext(), ConfigParams.TEXT_ASSESS_INTRODUCE));
                toFragment(frag1, true, true);
                break;
            case TitleBarView.ID_RIGHT_BUTTON:

                // toFragment(AssessListFragment.newInstance(), true, true);
                WebFragment frag = WebFragment.newInstance("了解评估", ConfigParams.getConfig(getApplicationContext(), ConfigParams.TEXT_ASSESS_INTRODUCE));
                toFragment(frag, true, true);
                break;

            case R.id.btn_assess_doc:
                if (ConfigParams.IS_TEST_DATA) {
                    showTestDataDialog();
                } else {
                    requestPackageList();
                }
                break;
            case R.id.btn_assess_self:
                // if (btnAssessSelf.isPressed()) {
                // btnAssessSelf.setTextColor(Color.parseColor("#046c6d"));
                // } else {
                //
                // }
                if (ConfigParams.IS_TEST_DATA) {
                    if (safeJudge) {
                        safeJudge = false;
                        ThreadHandler.postUiThread(new Runnable() {

                            @Override
                            public void run() {
                                toFragment(LoginFragment.class, null, true);
                                safeJudge = true;
                            }
                        }, 350);
                    }
                } else {
                    if (isLimit) {
                        showToast("您的评估次数已用完。购买私人医生/绿星计划服务，可获得更多评估机会哦~");
                    } else {
                        if (safeJudge) {
                            safeJudge = false;
                            ThreadHandler.postUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    MemberRecordFragment fragment = MemberRecordFragment.newInstance(1, 0, false);
                                    // MemberCreateFragment frag =
                                    // MemberCreateFragment.newInstance(UserMrg.DEFAULT_MEMBER.mId);
                                    // frag.setFromEdit(true);
                                    toFragment(fragment, true, true);
                                    safeJudge = true;
                                }
                            }, 350);
                        }
                    }
                }
                break;
            case R.id.btn_assess_doc_help:
                int[] loc = new int[2];
                btnAssessDoc.getLocationInWindow(loc);
                QuestionHelpWindow window = new QuestionHelpWindow(getApplicationContext(), "医生评估：在自助评估的基础上，医生将综合用户最新的身体参数变化情况，提出专业的建议，制定更具针对性的健康评估报告。");
                window.showAtLocation(mRoot, Gravity.TOP | Gravity.CENTER_VERTICAL, 0, loc[1] + btnAssessDoc.getMeasuredHeight());
                // showToast("医生评估：在自助评估的基础上，医生将综合用户最新的身体参数变化情况，提出专业的建议，制定更具针对性的健康评估报告。");
                break;
            case R.id.btn_assess_self_help:
                // showToast("自助评估：通过填写体征数据及身体情况，系统将根据糖尿病预警模型智能分析您的健康情况，并生成评估报告。");

                int[] loc1 = new int[2];
                btnAssessSelf.getLocationInWindow(loc1);
                QuestionHelpWindow window1 = new QuestionHelpWindow(getApplicationContext(), "自助评估：通过填写体征数据及身体情况，系统将根据糖尿病预警模型智能分析您的健康情况，并生成评估报告。");
                window1.showAtLocation(mRoot, Gravity.TOP | Gravity.CENTER_VERTICAL, 0, loc1[1] + btnAssessSelf.getMeasuredHeight());

                break;
            default:
                break;
        }
    }

    private void parseAssessDoc(byte[] b) {
        try {
            ComveePacket packet = ComveePacket.fromJsonString(b);
            if (packet.getResultCode() == 0) {
                Toast.makeText(getApplicationContext(), "预约成功，医生将在5～10分钟后联系您，请您耐心等待。", Toast.LENGTH_LONG).show();
                // showToast("预约成功，医生将在5～10分钟后联系您，请您耐心等待。");
                // showToast(packet.getResultMsg());

            } else {
                showToast(packet.getResultMsg());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSussece(int what, byte[] b, boolean fromCache) {
        cancelProgressDialog();
        switch (what) {
            case 1:
                parsePackageList(b);
                break;
            case 2:
                parseAssessDoc(b);
                break;
            default:
                break;
        }
    }

    @Override
    public void onFialed(int what, int errorCode) {
        cancelProgressDialog();
        showToast(R.string.time_out);
        ComveeHttpErrorControl.parseError(getActivity(), errorCode);

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        indexPackage = which;
        showToAssessDocDialog(listItems.get(indexPackage).memberPackageId);
    }

    @Override
    public boolean onBackPress() {
        ((BaseFragmentActivity) getActivity()).tryExit();
        return true;
    }

    private void setAssessNum(int count) {

        String str = String.format("%06d", count);
        if (str.length() < 6) {
            for (int i = 0; i < 6 - str.length(); i++) {
                str = 0 + str;
            }
        }

        int len = str.length();
        System.gc();
        try {
            for (int i = 0; i < len; i++) {
                int num = Integer.valueOf(str.charAt(i) + "");
                TextView tv = (TextView) findViewById(mRes[i]);
                // tv.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                // R.anim.push_up_in));
                // tv.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                // R.anim.push_up_out));
                // tv.setFactory(this);
                // tv.setImageResource(mImgRes[num]);
                tv.setText(num + "");
                // mHandler.sendMessageDelayed(mHandler.obtainMessage(i, i,
                // num), 100);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void requestAssessCheck() {

        ObjectLoader<AssessIndexInfo> loader = new ObjectLoader<AssessIndexInfo>();
        loader.loadBodyObject(AssessIndexInfo.class, ConfigUrlMrg.ASSESS_CHECK, loader.new CallBack() {

            @Override
            public void onBodyObjectSuccess(boolean isFromCache, AssessIndexInfo obj) {
                if (obj.assessmentNum > 0 || obj.assessmentNum == -1) {
                    isLimit = false;
                    tvLimit.setVisibility(View.VISIBLE);
                    if (obj.assessmentNum == -1) {
                        tvLimit.setText(String.format("您还剩%d次评估机会", 999));
                    } else {
                        tvLimit.setText(String.format("您还剩%d次评估机会", obj.assessmentNum));
                    }
                } else {
                    if (!ConfigParams.IS_TEST_DATA) {
                        tvLimit.setVisibility(View.VISIBLE);
                        tvLimit.setText("您的评估次数已用完。购买私人医生/绿星计划服务，可获得更多评估机会哦~");
                        isLimit = true;
                    }
                }
                setAssessNum(obj.allAssessNum);

                cancelProgressDialog();
            }

            @Override
            public boolean onFail(int code) {
                cancelProgressDialog();
                return super.onFail(code);
            }
        });

    }

    @Override
    public View makeView() {
        ImageView i = new ImageView(getApplicationContext());
        i.setLayoutParams(new ImageSwitcher.LayoutParams(-2, -2));
        return i;
    }

}
