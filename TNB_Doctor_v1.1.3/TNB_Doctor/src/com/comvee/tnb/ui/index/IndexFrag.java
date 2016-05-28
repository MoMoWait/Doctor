package com.comvee.tnb.ui.index;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comvee.ThreadHandler;
import com.comvee.frame.BaseFragmentActivity;
import com.comvee.frame.FragmentMrg;
import com.comvee.network.BackgroundTasks;
import com.comvee.tnb.DrawerMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.LunchActivityDialog;
import com.comvee.tnb.dialog.MoneyGetTaskDialog;
import com.comvee.tnb.dialog.RobRedPacketDialog;
import com.comvee.tnb.dialog.ShadeActivityDialog;
import com.comvee.tnb.dialog.VoucherMsgDialog;
import com.comvee.tnb.model.HomeIndex;
import com.comvee.tnb.model.HomeIndex.Tunrs;
import com.comvee.tnb.model.IndexSugarInfo;
import com.comvee.tnb.model.IndexVoucherModel;
import com.comvee.tnb.network.IndexNewGuideUtil;
import com.comvee.tnb.network.IndexRequestUtils;
import com.comvee.tnb.network.MemUnReadMsgLoader;
import com.comvee.tnb.network.NetworkCallBack;
import com.comvee.tnb.network.ObjectLoader;
import com.comvee.tnb.pedometer.PedometerFragment;
import com.comvee.tnb.radio.RadioUtil;
import com.comvee.tnb.ui.ask.AskIndexFragment;
import com.comvee.tnb.ui.assess.AssessFragment;
import com.comvee.tnb.ui.exercise.RecordExerciseFragment;
import com.comvee.tnb.ui.heathknowledge.HealthKnowledgeFragment;
import com.comvee.tnb.ui.newsknowledg.NewsKnowledgeFragment;
import com.comvee.tnb.ui.pharmacy.PharmacyListFragment;
import com.comvee.tnb.ui.record.RecordMainFragment;
import com.comvee.tnb.ui.record.RecordSugarInputNewFrag;
import com.comvee.tnb.ui.record.RecordSurgarSetfragment;
import com.comvee.tnb.ui.record.diet.RecordDietIndexFragment;
import com.comvee.tnb.ui.user.LoginFragment;
import com.comvee.tnb.view.IndexBannerView;
import com.comvee.tnb.view.IndexBannerView.BannerClickListener;
import com.comvee.tnb.view.IndexBottomView;
import com.comvee.tnb.view.IndexWaveView;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.AppUtil;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.tool.UITool;
import com.comvee.tool.UmenPushUtil;
import com.comvee.tool.UserMrg;
import com.comvee.util.Util;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * 首页
 *
 * @author Administrator
 */
public class IndexFrag extends BaseFragment implements OnClickListener, OnTouchListener, IndexClockMrg.CallBack,
        ShadeActivityDialog.ShadeClickListener {
    public static final int ALARM_TIME = 7200;// 闹钟倒计时时间

    public static int PENDING_CODE = 100111;
    TitleBarView mTitleBarView;
    private String url;
    private String title;
    private View view;
    private TextView tv_label, tv_unit, tv_value, tv_limit_value, tv_limit, tv_history, tv_history_label, countdown_clock;
    private ImageView image_record;
    private View food, spor, pharmacy, know;
    private TitleBarView mBarView;
    private View whitecircle;
    private TextView tv_remain_bonus;
    private String bonusTitle;// 点击奖金时跳转H5的title;
    private ImageView ivHead;
    private LinearLayout mChangrMoney;
    private HomeIndex.SugarPay surgarPay;
    private IndexBannerView bannerView;
    private int flag;
    private IndexBottomView bottomView;
    private RelativeLayout layoutBanner;
    private ObjectLoader<HomeIndex> redLoader;//红包加载
    private DialogFragment mGetTaskDialog;
    private DialogFragment mRedPacketDialog;
    private TextView tv_pedometer;
    public static IndexFrag newInstance() {
        return new IndexFrag();
    }

    public static void toFragment(FragmentActivity act, boolean anim) {
        FragmentMrg.popSingleFragment(act, IndexFrag.class, null, anim);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (flag == 0) {// 遮罩页后出现点击判断
            ThreadHandler.postUiThread(new Runnable() {
                @Override
                public void run() {
                    getHomePopup();
                }
            }, LunchActivityDialog.isShow ? 3000 : 0);// 闪屏页之后延3秒出现
        }

        if (!UserMrg.IS_VISITOR) {
            DrawerMrg.getInstance().openTouch();
        }

        loadSugarRecord();

        IndexRequestUtils utils = new IndexRequestUtils();
        utils.requestVoucher(new NetworkCallBack() {

            @Override
            public void callBack(int what, int status, Object obj) {
                if (obj != null && obj instanceof IndexVoucherModel) {
                    IndexVoucherModel model = (IndexVoucherModel) obj;
                    if (model.getType() != 0) {
                        if (model.getType() == 2 && !ConfigParams.isShowDiscount) {
                            return;
                        }
                        VoucherMsgDialog dialog = VoucherMsgDialog.getInstance(getActivity(), model);
                        dialog.show();
                        ConfigParams.isShowDiscount = false;
                    }
                }
            }
        });
        if (countdown_clock != null)
            countdown_clock.setText("我开饭啦");
        ((IndexWaveView) findViewById(R.id.wave_view)).startwaveAnim();

        mBarView.setTitle(UserMrg.DEFAULT_MEMBER.name);
        if (!TextUtils.isEmpty(UserMrg.DEFAULT_MEMBER.photo) && !UserMrg.DEFAULT_MEMBER.photo.equalsIgnoreCase("null") && null != ivHead) {
            ImageLoaderUtil.getInstance(mContext).displayImage(UserMrg.DEFAULT_MEMBER.photo, ivHead, ImageLoaderUtil.user_options);
        }

        requestMemUnReadMsgLoader();
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.index_new_frag;
    }

    @Override
    public void onLaunch(Bundle bundle) {
        super.onLaunch(bundle);
        if (Util.checkFirst(getApplicationContext(), "index_new_5.0.0")) {
            flag = 1;
            if ((UITool.getDisplayHeight(getActivity()) / UITool.getDisplayWidth(getActivity())) == (15f / 9f)) {
                AppUtil.initLuanchWindow(getActivity(), R.drawable.index_1, this);
            } else {
                AppUtil.initLuanchWindow(getActivity(), R.drawable.index_2, this);
            }
        }
        redLoader = new ObjectLoader<HomeIndex>();
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        redLoader.setNeedCache(true);
        redLoader.putPostValue("height", String.valueOf(displayMetrics.heightPixels));
        redLoader.putPostValue("width", String.valueOf(displayMetrics.widthPixels));

        layoutBanner = (RelativeLayout) findViewById(R.id.layout_banner);
        bottomView = (IndexBottomView) findViewById(R.id.layout_frame_bottom);
        /**
         * 计步器 by zhang qc
         */
        tv_pedometer= (TextView) findViewById(R.id.tv_pedometer);
        tv_pedometer.setOnClickListener(this);
        bottomView.bindFragment(this);
        bottomView.selectIndex();

        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        init();

        initTitleBarPhoto();
        initSugarView();
        RadioUtil.checkJumpComment(getActivity());

        // 到首页时，预加载其他页面的数据
        UserMrg.preLoad();

    }

    @Override
    public void onStart() {
        super.onStart();
        UmenPushUtil.initPushInActivity();
    }

    private String formatTime(long countingSecond) {
        DecimalFormat decimalFormat = new DecimalFormat("00");
        String hourStr = decimalFormat.format(countingSecond / 3600);
        String minuteStr = decimalFormat.format((countingSecond / 60) % 60);
        String secondStr = decimalFormat.format(countingSecond % 60);
        return hourStr + ":" + minuteStr + ":" + secondStr;
    }

    public void updatePhoneView() {

        if (view == null) {
            return;
        }
        int msgCount = ConfigParams.getMsgSysCount(TNBApplication.getInstance());
        if (msgCount > 0) {
            view.findViewById(R.id.index_red_dot).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.index_red_dot).setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onBackPress() {
        try {
            ((BaseFragmentActivity) getActivity()).tryExit();
        } catch (Exception e) {

        }

        return true;
    }

    /**
     * 初始化控件
     */
    private void init() {
        whitecircle = findViewById(R.id.whitecircle);
        mTitleBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        countdown_clock = (TextView) findViewById(R.id.countdown_clock);
        countdown_clock.setOnClickListener(this);
        tv_value = (TextView) findViewById(R.id.tv_value);
        tv_label = (TextView) findViewById(R.id.tv_label);
        tv_unit = (TextView) findViewById(R.id.tv_unit);
        tv_history = (TextView) findViewById(R.id.tv_history);
        tv_history_label = (TextView) findViewById(R.id.tv_history_label);
        tv_limit = (TextView) findViewById(R.id.tv_limit);
        tv_limit_value = (TextView) findViewById(R.id.tv_limit_value);
        image_record = (ImageView) findViewById(R.id.image_record);
        food = findViewById(R.id.group_food);
        spor = findViewById(R.id.group_spor);
        pharmacy = findViewById(R.id.group_pharmacy);
        know = findViewById(R.id.group_know);
        food.setOnClickListener(this);
        spor.setOnClickListener(this);
        pharmacy.setOnClickListener(this);

        know.setOnClickListener(this);
        tv_value.setOnClickListener(this);
        tv_remain_bonus = (TextView) findViewById(R.id.tv_remain_bonus);
        findViewById(R.id.whitecircle).setOnTouchListener(this);
        findViewById(R.id.limit_group).setOnClickListener(this);
        findViewById(R.id.record_group).setOnClickListener(this);
        findViewById(R.id.whitecircle).setOnClickListener(this);

    }

    /**
     * 初始化首页左上角头像
     */
    @SuppressLint("ResourceAsColor")
    private void initTitleBarPhoto() {
        // setRightButton(ResUtil.getString(R.string.index_history), this);
        mTitleBarView.setLeftDefault(this);
        mBarView.hideLeftButton();
        mBarView.setRightButton("历史", this);
        mBarView.setTitleBarBackgroundColor(getResources().getColor(android.R.color.transparent));
        mBarView.setTextColor(getResources().getColor(R.color.white), getResources().getColor(R.color.white), getResources().getColor(R.color.white));
        mBarView.findViewById(R.id.titlebar_line).setVisibility(View.GONE);
        view = View.inflate(getApplicationContext(), R.layout.titlebar_photo, null);
        ivHead = (ImageView) view.findViewById(R.id.img_photo);
        view.setId(10010101);
        view.setOnClickListener(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        params.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getActivity().getResources().getDisplayMetrics()), 0, 0, 0);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        mBarView.addView(view, params);
        if (!TextUtils.isEmpty(UserMrg.DEFAULT_MEMBER.photo) && !UserMrg.DEFAULT_MEMBER.photo.equalsIgnoreCase("null")) {
            ImageLoaderUtil.getInstance(mContext).displayImage(UserMrg.DEFAULT_MEMBER.photo, ivHead, ImageLoaderUtil.user_options);
        }
        updatePhoneView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case TitleBarView.ID_LEFT_BUTTON:
            case 10010101:
                if (!UserMrg.IS_VISITOR) {
                    DrawerMrg.getInstance().open();
                } else {
                    toFragment(LoginFragment.class, null, false);
                }
                break;
            case TitleBarView.ID_RIGHT_BUTTON:
                toFragment(RecordMainFragment.newInstance(true, 0), true, true);
                break;
            case R.id.limit_group:
                if (UserMrg.isTnb()) {
                    toFragment(RecordSurgarSetfragment.class, null, true);
                } else {
                }

                break;
            case R.id.tv_value:
            case R.id.whitecircle:
                toFragment(RecordSugarInputNewFrag.class, null, false);
                break;
            case R.id.group_food:
                toFragment(RecordDietIndexFragment.class, null, false);
                break;
            case R.id.group_spor:
                toFragment(RecordExerciseFragment.class, null, false);
                break;
            case R.id.group_pharmacy:
                toFragment(PharmacyListFragment.class, null, false);
                break;
            case R.id.group_know:
                //toFragment(HealthKnowledgeFragment.class, null, false);
                toFragment(NewsKnowledgeFragment.class, null, false);
                break;
            case R.id.record_group:
                toFragment(RecordMainFragment.newInstance(true, 0), true, true);
                break;
            case R.id.countdown_clock:
                if (!UserMrg.IS_VISITOR) {// 是否是游客
                    if (!IndexClockMrg.getInstance().isRuning()) {
                        IndexClockMrg.getInstance().reStart();
                    }
                    IndexClockWindow window = new IndexClockWindow();
                    window.showAtLocation(mRoot, Gravity.CENTER, 0, 0);
                } else {
                    // 如果是游客，就去登陆页面
                    FragmentMrg.toFragment(getActivity(), LoginFragment.class, null, true);
                }

                break;
            case R.id.tv_remain_bonus:// 剩余奖金跳转h5;
                if (!TextUtils.isEmpty(bonusTitle) && !TextUtils.isEmpty(url)) {
                    IndexNewGuideUtil.newGuideJump(getActivity(), 0, bonusTitle, surgarPay.turn_to);
                }
                break;
            case R.id.tv_pedometer:
                toFragment(PedometerFragment.class,null,false);
                break;
            default:
                break;
        }
    }

    /**
     * 初始化首页顶部血糖界面值
     */
    private void initSugarView() {
        tv_value.setText("");
        image_record.setVisibility(View.VISIBLE);
        tv_label.setText(ConfigParams.getCurBloodTimeString(ConfigParams.SUGAR_TIME_STR2));
        tv_limit.setVisibility(View.GONE);
        tv_history.setText("--");
        tv_limit_value.setText("--");
        tv_history_label.setText("上次测量值");
    }

    private void setupSugarView(IndexSugarInfo info) {

        if (info == null || TextUtils.isEmpty(info.title)) {
            initSugarView();
            return;
        }

        image_record.setVisibility(View.GONE);
        tv_limit.setVisibility(View.VISIBLE);

        switch (info.bloodGlucoseStatus) {
            case 1:
            case 2:
                whitecircle.setBackgroundResource(R.drawable.index_head_circle_low);
                tv_label.setTextColor(getResources().getColor(R.color.index_record_blue));
                tv_value.setTextColor(getResources().getColor(R.color.index_record_blue));
                tv_unit.setTextColor(getResources().getColor(R.color.index_record_blue));
                tv_limit.setCompoundDrawablesWithIntrinsicBounds(R.drawable.index_dir_down, 0, 0, 0);
                break;

            case 3:
                whitecircle.setBackgroundResource(R.drawable.index_head_circle_normal);
                tv_label.setTextColor(getResources().getColor(R.color.index_record_green));
                tv_unit.setTextColor(getResources().getColor(R.color.index_record_green));
                tv_value.setTextColor(getResources().getColor(R.color.index_record_green));
                tv_limit.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                break;
            case 4:
            case 5:
                whitecircle.setBackgroundResource(R.drawable.index_head_circle_hight);
                tv_unit.setTextColor(getResources().getColor(R.color.index_record_red));
                tv_label.setTextColor(getResources().getColor(R.color.index_record_red));
                tv_value.setTextColor(getResources().getColor(R.color.index_record_red));
                tv_limit.setCompoundDrawablesWithIntrinsicBounds(R.drawable.index_dir_up, 0, 0, 0);
                break;

            default:
                whitecircle.setBackgroundResource(R.drawable.index_head_circle_none);
                tv_label.setTextColor(getResources().getColor(R.color.index_record_gray));
                tv_unit.setTextColor(getResources().getColor(R.color.index_record_gray));
                initSugarView();
                break;
        }
        tv_label.setText(info.title);
        tv_limit_value.setText(info.downValue + "-" + info.highValue);
        ConfigParams.setSugarHightValue(getApplicationContext(), info.highValue);
        ConfigParams.setSugarLowValue(getApplicationContext(), info.downValue);
        tv_history_label.setText("上次" + info.title);
        tv_value.setText(info.value);
        if (!TextUtils.isEmpty(info.lastValue)) {
            tv_history.setText(info.lastValue);
        }
        if (TextUtils.isEmpty(info.value) || TextUtils.isEmpty(info.downValue) || TextUtils.isEmpty(info.highValue)
                || TextUtils.isEmpty(info.maxValue)) {
            return;
        }
        int progress = AppUtil.optionSugar(Double.parseDouble(info.value), Double.parseDouble(info.downValue), Double.parseDouble(info.highValue),
                Double.parseDouble(info.maxValue));

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        IndexClockMrg.getInstance().addCallBack(this);
        IndexClockMrg.getInstance().resume();
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        IndexClockMrg.getInstance().pause();
        if (bannerView != null)
            bannerView.stop();

    }

    /**
     * 加载首页血糖数据
     */
    @SuppressLint("NewApi")
    private void loadSugarRecord() {
        ObjectLoader<IndexSugarInfo> loader = new ObjectLoader<IndexSugarInfo>();
        loader.loadObjByPath(IndexSugarInfo.class, ConfigUrlMrg.INDEX_SUGAR_MSG, loader.new CallBack() {
            @Override
            public void onBodyObjectSuccess(boolean isFromCache, IndexSugarInfo obj) {
                setupSugarView(obj);
            }
        }, "body", "log");
        //
        //
        // IndexLoader loader = new IndexLoader();
        // loader.startLoadSugarRecord(new NetworkCallBack() {
        // @Override
        // public void callBack(int what, int status, Object obj) {
        // setupSugarView((IndexSugarInfo) obj);
        // }
        // });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int viewId = v.getId();
        if (viewId == R.id.whitecircle) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    whitecircle.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.index_buton_scale));
                    ((IndexWaveView) findViewById(R.id.wave_view)).startwaveAnim();
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    break;
            }
            return false;

        }
        return false;
    }

    @Override
    public void onTick(int tick) {
        if (countdown_clock != null)
            countdown_clock.setText(formatTime(tick));

    }

    @Override
    public void onCancel() {
        if (countdown_clock != null)
            countdown_clock.setText("我开饭啦");
    }

    public void clearBannerCache(){
        try {
            redLoader.clearCache();
            layoutBanner.removeAllViews();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 请求首页轮播，剩余金额等数据
     */
    private void getHomePopup() {
        if (isAdded()) {
            redLoader.setNeedCache(false);
            redLoader.loadBodyObject(HomeIndex.class, ConfigUrlMrg.LOAD_INDEX_TURN, redLoader.new CallBack() {
                public void onBodyObjectSuccess(boolean isFromCache, HomeIndex obj) {

                    try {
                        if (obj != null) {
                            if (!isFromCache) {
                                surgarPay = obj.sugar_pay;
                                if (surgarPay != null) {
                                    bonusTitle = surgarPay.title;
                                    url = surgarPay.turn_to;
                                    HomeIndex.Alert alert = surgarPay.alert;
                                    if (alert != null) {// 有首页活动的时候显示弹出框
                                        showActivityDialog(alert);
                                    }
                                    if (surgarPay.money.equals("")) {// 取消活动时，不显示活动余额
                                        tv_remain_bonus.setVisibility(View.GONE);
                                    } else {
                                        tv_remain_bonus.setVisibility(View.VISIBLE);
                                        // 奖金余额
                                        tv_remain_bonus.setText(surgarPay.money);
                                        tv_remain_bonus.setOnClickListener(IndexFrag.this);
                                    }

                                    if (surgarPay.money_package == 1)// 为1时弹出抢红包
                                    {
                                        showRedPacketDialog(surgarPay.user_flag);
                                    }
                                }
                            }

                            if (obj.turns != null) {
                                initBanner(obj.turns);
                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                }

                @Override
                public boolean onFail(int status) {
                    return super.onFail(status);
                }
            });
        }
    }

    /**
     * 显示活动窗口
     *
     * @param alert
     */


    private void showActivityDialog(HomeIndex.Alert alert) {
        if (mGetTaskDialog != null && mGetTaskDialog.isVisible()) {
            return;
        }
        MoneyGetTaskDialog dialog = new MoneyGetTaskDialog();
        dialog.setImageUrl(alert.pic);
        dialog.setH5Url(alert.turnUrl);
        dialog.setTitle(alert.title);
        mGetTaskDialog = dialog;
        dialog.show(getFragmentManager(), "");

    }

    private void showRedPacketDialog(int user_flag) {
        if (mRedPacketDialog != null && mRedPacketDialog.isVisible()) {
            return;
        }
        RobRedPacketDialog dialog = new RobRedPacketDialog();
        dialog.setUserFlag(user_flag);
        mRedPacketDialog = dialog;
        dialog.show(getChildFragmentManager(), "");

    }

    /**
     * 初始化中间的banner条
     *
     * @param turns
     */
    private void initBanner(ArrayList<Tunrs> turns) {
        bannerView = new IndexBannerView(getApplicationContext());
        layoutBanner.removeAllViews();
        layoutBanner.addView(bannerView);
//		bannerView = (IndexBannerView) findViewById(R.id.banner_view);
        bannerView.setDatas(turns);
      //  bannerView.skipCacheItem();// 跳过VIewPager缓存页
        if (turns.size() > 1) {// 大于1条时才轮播
            bannerView.startAutoFlowTimer();
        }
        bannerView.addBannerClickListener(new BannerClickListener() {
            public void onBannerClick(Tunrs info) {
                try {
                    if (info.turn_type == 0) {
                        IndexNewGuideUtil.newGuideJump(getActivity(), info.turn_type, info.title, info.turn_to);
                    } else {
                        IndexNewGuideUtil.newGuideJump(getActivity(), info.turn_type);
                    }
                } catch (Exception e) {
                }

            }
        });
    }

    /**
     * 请求 ，有多少条新的医生回复和有多少条新的系统消息
     */
    public void requestMemUnReadMsgLoader() {
        MemUnReadMsgLoader loader = new MemUnReadMsgLoader(new NetworkCallBack() {

            @Override
            public void callBack(int what, int status, Object obj) {
                if (null != obj && obj instanceof Integer) {
                    DrawerMrg.getInstance().updateLefFtagment();
                    updatePhoneView();
                } else if (status != 0) {
                    ConfigParams.setMsgSysCount(getApplicationContext(), 0);
                }
                // 刷新评估页面的小红点
                AssessFragment frag = FragmentMrg.getSingleFragment(AssessFragment.class);
                if (frag != null) {
                    frag.updateBottomView();
                }

                // 刷新医生首页的小红点
                AskIndexFragment frag2 = FragmentMrg.getSingleFragment(AskIndexFragment.class);
                if (frag2 != null) {
                    frag2.updateBottomView();
                }
                bottomView.update();

            }
        }, getApplicationContext());
        loader.setWhat(BackgroundTasks.TASK_BACKGROUND);
        loader.starLoader();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag = 0;

    }

    @Override
    public void onShadeClick() {
        getHomePopup();
    }

}