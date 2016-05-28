package com.comvee.tnb.ui.ask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.comvee.BaseApplication;
import com.comvee.FinalDb;
import com.comvee.ThreadHandler;
import com.comvee.db.sqlite.DbModel;
import com.comvee.frame.FragmentMrg;
import com.comvee.network.NetStatusManager;
import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.activity.ImageDialogActivity;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.CustomProgressDialog;
import com.comvee.tnb.exception.ExceptionFragment;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.ComveeTask;
import com.comvee.tnb.model.AskServerInfo;
import com.comvee.tnb.model.NewAskModel;
import com.comvee.tnb.model.PageModel;
import com.comvee.tnb.model.ServerListModel;
import com.comvee.tnb.model.TaskItem;
import com.comvee.tnb.network.AskQuestionRefreshLoader;
import com.comvee.tnb.network.AskQuestionRequestListLoader;
import com.comvee.tnb.network.NetworkCallBack;
import com.comvee.tnb.ui.ask.AskContentAdapter.ToPatientPersonal;
import com.comvee.tnb.ui.ask.AskContentAdapter.anewSendMsgListener;
import com.comvee.tnb.ui.follow.FollowRecordFragment;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.ui.member.MemberRecordFragment;
import com.comvee.tnb.ui.more.WebFragment;
import com.comvee.tnb.ui.privatedoctor.DoctorServerList;
import com.comvee.tnb.ui.privatedoctor.MemberDoctorListFrag;
import com.comvee.tnb.ui.record.common.AlbumHelper;
import com.comvee.tnb.ui.record.common.ImageBucket;
import com.comvee.tnb.ui.record.common.ImageItem4LocalImage;
import com.comvee.tnb.ui.record.common.MyBitmapFactory;
import com.comvee.tnb.ui.record.common.ShowLocalImageFragment;
import com.comvee.tnb.ui.record.laboratory.RecordLaboratoryFragment;
import com.comvee.tnb.ui.task.TaskCenterFragment;
import com.comvee.tnb.ui.task.TaskIntroduceFragment;
import com.comvee.tnb.ui.xlistview.XListView;
import com.comvee.tnb.ui.xlistview.XListView.IXListViewListener;
import com.comvee.tnb.view.MenuPopwindow;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tnb.widget.Voice.MediaPlayerUtils;
import com.comvee.tnb.widget.Voice.PlayImageView;
import com.comvee.tnb.widget.Voice.RecordButton;
import com.comvee.tnb.widget.Voice.RecordButton.OnFinishedRecordListener;
import com.comvee.tool.UITool;
import com.comvee.tool.UserMrg;
import com.comvee.util.Log;
import com.comvee.util.TimeUtil;
import com.comvee.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;


/**
 * 患者与医生对话界面 流程 先获取本地数据库数据，同时调用刷新接口数据 数据返回后修改本地数据库，通知页面重新去取本地数据库数据 加载更多流程一样
 * 发送消息流程 本地数据库添加一条临时数据，同时刷新界面 调用接口发送消息，接口返回后更改本地数据，同时更新界面
 *
 * @author Administrator
 */
public class AskQuestionFragment extends BaseFragment implements OnClickListener, OnItemClickListener, ToPatientPersonal, OnTouchListener,
        anewSendMsgListener {
    /**
     * 从预约界面返回后是否删除某个会话
     */
    public static boolean isDeleat;//
    public static boolean isUpdata;// 是否刷新界面
    /**
     * 获取本地数据库数据的模式 LORDER_MORE 加载更多，用于获取界面第一条数据之前的20条
     */
    private final int LORDER_MORE = 1;
    /**
     * 获取本地数据库数据的模式 REFRESH 刷新数据 加载界面第一条之后的所有数据
     */
    private final int REFRESH = 2;
    /**
     * 获取本地数据库数据的模式 DEFAULT默认模式，获取本地最新的20条数据
     */
    private final int DEFAULT = 3;
    private SimpleDateFormat currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private CustomProgressDialog mProDialog;
    private AskServerInfo mInfo;
    private ImageView edit_way_icon;
    private EditText edit_way_txt;
    private TextView btn_send_msg;
    private RecordButton btn_edit_way_voice;
    private Button iv_send_img;
    private XListView mListView;
    /**
     * 输入方式 true:文字 false:语音
     */
    private boolean inputmode = true;
    /**
     * EditText是否有输入内容
     */
    private boolean state = false;
    TextWatcher mTextWatcher = new TextWatcher() {
        private CharSequence temp;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            temp = s;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            temp = s;
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (temp.length() > 0) {
                state = true;

            } else {
                state = false;
                // ConfigParams.setDraftValue(getApplicationContext(),
                // UserMrg.getMemberSessionId(mContext) + mInfo.getDoctorId(),
                // edit_way_txt
                // .getText().toString());
            }
            setBtnSendMsg(state);

        }

    };
    /**
     * 录音长度
     */
    private int mRecordTime = 0;
    /**
     * 是否显示加号按钮对应的界面
     */
    private boolean isShowMore;
    /**
     * 加号按钮对应的控件
     */
    private LinearLayout linMore;
    private ArrayList<NewAskModel> models = new ArrayList<NewAskModel>();
    private AskContentAdapter mAdapter;
    /**
     * 广播，用于刷新对话，伪实时对话
     */
    private AskQustionReceiver receiver;//
    private boolean isChoosedPic = false;
    /**
     * 用于判断是否隐藏输入框和电话咨询按钮 该参数由接口返回
     */
    private int is_phone;//
    private boolean isHaveBookService, isBookServiceExpired;
    private FinalDb finalDb;
    private LocPageMsg locPageMsg;
    /**
     * 临时存放的数据，用于离开本页面时，本地动作改变了数据状态，返回时修改这个临时数据存入数据库
     */
    private NewAskModel tempModel;//
    private TitleBarView mBarView;
    private File uploadtemFile;
    // 获取列表接口
    private AskQuestionRequestListLoader listLoader;
    // 刷新列表接口
    private AskQuestionRefreshLoader refreshLoader;
    private String firstTime;// 第一条消息的时间
    private String lastTime;// 最后一条消息的时间
    private int modelSize;
    private View no_server, buy_server;
    private TextView tv_no_server_title, tv_no_server_label;
    Handler handler2 = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            modelSize = models.size();
            initEditext();
            switch (msg.what) {
                case 0:
                    listLoader.starLoader(firstTime);
                    notifyListView(false);
                    break;
                case LORDER_MORE:
                    mListView.stopRefresh();
                    models.addAll(0, (ArrayList<NewAskModel>) msg.obj);
                    notifyListView(true);
                    break;
                case REFRESH:
                    ArrayList<NewAskModel> temp = (ArrayList<NewAskModel>) msg.obj;
                    if (temp.size() != models.size()) {
                        models.clear();
                        models.addAll(temp);
                        notifyListView(true);
                        scrollLast();
                    }
                    break;
                case DEFAULT:
                    models.clear();
                    models.addAll((ArrayList<NewAskModel>) msg.obj);
                    notifyListView(true);
                    refreshLoader.starLoader(lastTime);
                    scrollLast();
                    break;
                default:
                    break;
            }

        }
    };
    /**
     * 临时存放数据，用来避免音频播放可以多次点击
     */
    private NewAskModel oldModel;
    private String compressPath = TNBApplication.getInstance().getFilesDir().getAbsolutePath() + "/upImg.png";
    private RelativeLayout mEdit;

    public static void toFragment(FragmentActivity activity, AskServerInfo info) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("model", info);
        FragmentMrg.toFragment(activity, AskQuestionFragment.class, bundle, true);
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.ask_content_fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onLaunch(Bundle bundle) {
        super.onLaunch(bundle);
        if (bundle != null) {
            mInfo = (AskServerInfo) bundle.getSerializable("model");
        }
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        if (finalDb == null) {
            finalDb = FinalDb.create(getApplicationContext(), ConfigParams.DB_NAME);
        }
        init();
        initInputView();
        initListLoader();
        initRefreshLoader();
        selectLocPageMsg(finalDb);
    }

    /**
     * 获取本地数据库数据 避免卡主线单开线程去取
     *
     * @param type
     * @param isRequest
     */
    private synchronized void getLoctionModels(final int type, final boolean isRequest) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getLocListView(type, isRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 获取本地数据库数据 如果本地数据库没有数据，则访问接口获取
     *
     * @param type      获取数据类型
     * @param isRequest
     */
    private synchronized void getLocListView(int type, boolean isRequest) {
        ArrayList<NewAskModel> tempmodels = null;
        switch (type) {
            // 加载更多，获取界面中第一条数据之前的20条数据
            case LORDER_MORE:
                if (firstTime == null) {
                    tempmodels = (ArrayList<NewAskModel>) finalDb.findAllByWhere(NewAskModel.class, "memberId=" + UserMrg.DEFAULT_MEMBER.mId
                            + " and doctorId=" + mInfo.getDoctorId() + " order by insertDt desc limit 20 offset 0");

                    Collections.reverse(tempmodels);
                } else {
                    tempmodels = (ArrayList<NewAskModel>) finalDb.findAllByWhere(NewAskModel.class, "memberId=" + UserMrg.DEFAULT_MEMBER.mId
                            + " and doctorId=" + mInfo.getDoctorId() + " and insertDt <datetime('" + firstTime
                            + "') order by  insertDt asc limit 20 offset 0");
                }
                break;
            // 刷新数据，获取界面中第一条数据之后的所有数据
            case REFRESH:
                if (lastTime == null) {
                    tempmodels = (ArrayList<NewAskModel>) finalDb.findAllByWhere(NewAskModel.class, "memberId=" + UserMrg.DEFAULT_MEMBER.mId
                            + " and doctorId=" + mInfo.getDoctorId() + " order by insertDt desc limit 20 offset 0");
                    Collections.reverse(tempmodels);
                } else {
                    tempmodels = (ArrayList<NewAskModel>) finalDb.findAllByWhere(NewAskModel.class, "memberId=" + UserMrg.DEFAULT_MEMBER.mId
                            + " and doctorId=" + mInfo.getDoctorId() + " and insertDt>=datetime('" + firstTime + "') order by  insertDt asc");

                }
                if (tempmodels == null || tempmodels.size() == 0) {
                    return;
                }
                break;
            // 默认数据 获取本地数据库最新的20条数据
            case DEFAULT:
                tempmodels = (ArrayList<NewAskModel>) finalDb.findAllByWhere(NewAskModel.class, "memberId=" + UserMrg.DEFAULT_MEMBER.mId
                        + " and doctorId=" + mInfo.getDoctorId() + " order by insertDt desc limit 20 offset 0");
                Collections.reverse(tempmodels);
                break;
            default:
                break;
        }
        getLocServerList(finalDb, tempmodels);
        Message message = new Message();
        if (tempmodels != null && tempmodels.size() > 0) {
            message.what = type;
            message.obj = tempmodels;
        } else if (!isRequest) {
            return;
        } else {
            message.what = 0;
        }
        handler2.sendMessage(message);
    }

    /**
     * 刷新listview
     *
     * @param isSelect 是否置顶 因为在加载数据时如果置顶 会看不到加载动画
     */
    private void notifyListView(boolean isSelect) {
        int selection = models.size() - modelSize + 1 >= 0 ? models.size() - modelSize + 1 : 0;
        mAdapter.notifyDataSetChanged();
        if (models.size() > 0) {
            if (isSelect) {
                mListView.setSelection(selection);
            }
            firstTime = models.get(0).getInsertDt();
            lastTime = models.get(models.size() - 1).getInsertDt();
        }
    }

    /**
     * 本地临时更新数据
     */
    private void updataModels() {
        if (isDeleat && tempModel != null) {
            finalDb.delete(tempModel);
            models.remove(tempModel);
            tempModel = null;
            isDeleat = false;
        } else {
            isDeleat = false;
        }
        if (isUpdata && tempModel != null && tempModel.getMsgType() == 6) {
            tempModel.setMsgType(7);
            finalDb.update(tempModel);
            isUpdata = false;
        } else {
            isUpdata = false;
        }

        initEditext();
    }

    /**
     * 获取特殊消息类型12在另一张表中的数据
     *
     * @param finalDb
     * @param newAskModels
     */
    private void getLocServerList(FinalDb finalDb, ArrayList<NewAskModel> newAskModels) {
        for (int i = 0; i < newAskModels.size(); i++) {
            NewAskModel askModel = newAskModels.get(i);
            if (askModel.getMsgType() == 12) {
                ArrayList<ServerListModel> serverListModels = (ArrayList<ServerListModel>) finalDb.findAllByWhere(ServerListModel.class, "sid="
                        + askModel.getSid());
                askModel.setList(serverListModels);
            } else {
                continue;
            }
        }

    }

    /**
     * 初始化输入框以及电话咨询按钮
     */
    private void initEditext() {
        is_phone = ConfigParams.getAskKey(getApplicationContext(), UserMrg.DEFAULT_MEMBER.mId + mInfo.getDoctorId() + "is_phone");
        isHaveBookService = ConfigParams.getAskServerKey(getApplicationContext(), UserMrg.DEFAULT_MEMBER.mId + mInfo.getDoctorId()
                + "isHaveBookService");
        isBookServiceExpired = ConfigParams.getAskServerKey(getApplicationContext(), UserMrg.DEFAULT_MEMBER.mId + mInfo.getDoctorId()
                + "isBookServiceExpired");
        // if (is_showdoc == 1) {
        // mBarView.setRightButton(R.drawable.yisheng_05, this);
        // } else {
        // mBarView.hideRightButton();
        // }
        if (is_phone == 1) {
            mBarView.setRightButton(R.drawable.yisheng_05, this);
            // findViewById(R.id.tv_phone).setVisibility(View.VISIBLE);
        } else {
            // findViewById(R.id.tv_phone).setVisibility(View.GONE);
            mBarView.hideRightButton();
        }
        // if (is_question == 0) {
        // findViewById(R.id.edit_message_view).setVisibility(View.GONE);
        // } else {
        // findViewById(R.id.edit_message_view).setVisibility(View.VISIBLE);
        // }

        if (!isHaveBookService) {
            // 无咨询套餐
            linMore.setVisibility(View.GONE);
            tv_no_server_title.setText(getString(R.string.no_server_title));
            tv_no_server_label.setText(getString(R.string.no_server_label));
            no_server.setVisibility(View.VISIBLE);
            findViewById(R.id.edit_message_view).setVisibility(View.GONE);
        } else {
            if (isHaveBookService) {
                // 有咨询套餐
                if (isBookServiceExpired) {
                    // 咨询套餐过期
                    linMore.setVisibility(View.GONE);
                    tv_no_server_title.setText(getString(R.string.server_expired_title));
                    tv_no_server_label.setText(getString(R.string.server_expired_label));
                    no_server.setVisibility(View.VISIBLE);
                    findViewById(R.id.edit_message_view).setVisibility(View.GONE);
                } else if (no_server != null&&mEdit!=null) {
                    no_server.setVisibility(View.GONE);

                    mEdit.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        if (models.size() > 0) {
            refreshLoader.starLoader(lastTime);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ConfigParams.ASK_DOCID_MEMID = null;
        // 设置草稿
        ConfigParams.setDraftValue(getApplicationContext(), UserMrg.DEFAULT_MEMBER.mId + mInfo.getDoctorId(), edit_way_txt.getText().toString()
                .trim());

    }

    @Override
    public void onResume() {
        if (!TextUtils.isEmpty(mInfo.getDoctorName())) {
            mBarView.setTitle(mInfo.getDoctorName());
        }
        if (models.size() > 0) {
            updataModels();
            refreshLoader.starLoader(lastTime);
        } else {
            getLoctionModels(DEFAULT, true);
        }
        if (MyBitmapFactory.tempSelectBitmap.size() > 0 && isChoosedPic) {
            String picPath = MyBitmapFactory.tempSelectBitmap.get(0).imagePath;
            NewAskModel askModel = NewLocAskModelMsg("", picPath, 0);
            fileUpload(finalDb, askModel);
            MyBitmapFactory.tempSelectBitmap.clear();
            isChoosedPic = false;
        }
        try {
            // 广播注册
            receiver = new AskQustionReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("requestlist");
            getApplicationContext().registerReceiver(receiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onPause() {
        UITool.closeInputMethodManager(getActivity(), edit_way_txt.getApplicationWindowToken());
        try {
            uploadtemFile.delete();
            // 注销广播

            if (receiver != null) {
                getApplicationContext().unregisterReceiver(receiver);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TitleBarView.ID_RIGHT_BUTTON:
                toFragment(AskTellListFragment.newInstance(mInfo.getDoctorId()), true, true);
                break;
            case R.id.btn_server:
                toFragment(MemberDoctorListFrag.class, null, true);
                break;
            case R.id.edit_way_icon:// 切换输入方式： 文字\语音
                UITool.closeInputMethodManager(getActivity(), edit_way_txt.getApplicationWindowToken());
                inputModeChange(!inputmode);
                break;
            case R.id.btn_send_msg:// 文字内容发送按钮
                sendContent();
                break;
            case R.id.iv_send_img:// 发送图片
                UITool.closeInputMethodManager(getActivity(), edit_way_txt.getApplicationWindowToken());
                isShowMore = !isShowMore;
                linMore.setVisibility(linMore.getVisibility() != View.VISIBLE ? View.VISIBLE : View.GONE);
                if (isShowMore = (linMore.getVisibility() == View.VISIBLE)) {
                    ThreadHandler.postUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mListView.setSelection(mAdapter.getCount());
                        }
                    }, 30);
                }

                inputModeChange(true);

                break;
            case R.id.tv_phone:
                toFragment(AskTellListFragment.newInstance(mInfo.getDocTellId() + ""), true, true);
                break;
            case R.id.tv_camera:
                toCaramer();
                break;
            case R.id.tv_photo:
                toPhoto();
                break;
            case R.id.btn_buy_doc_server:
                DoctorServerList.toFragment(getActivity(), mInfo.getDoctorId());
                break;
            default:
                break;
        }

    }

    @Override
    public void onRceycle() {
        super.onRceycle();
        btn_edit_way_voice.setActivity(null);
    }

    /**
     * 初始化界面
     */
    private void init() {
        if (mInfo == null) {
            FragmentMrg.toBack(getActivity());
            return;
        }
        ConfigParams.ASK_DOCID_MEMID = mInfo.getDoctorId() + UserMrg.DEFAULT_MEMBER.mId;
        edit_way_icon = (ImageView) findViewById(R.id.edit_way_icon);
        edit_way_txt = (EditText) findViewById(R.id.edit_way_txt);
        btn_send_msg = (TextView) findViewById(R.id.btn_send_msg);
        btn_edit_way_voice = (RecordButton) findViewById(R.id.btn_edit_way_voice);
        btn_edit_way_voice.setActivity(getActivity());
        iv_send_img = (Button) findViewById(R.id.iv_send_img);
        linMore = (LinearLayout) findViewById(R.id.lin_more_btn);
        no_server = findViewById(R.id.no_ask_server);
        buy_server = findViewById(R.id.btn_buy_doc_server);
        mEdit = (RelativeLayout) findViewById(R.id.edit_message_view);
        tv_no_server_title = (TextView) findViewById(R.id.tv_title);
        tv_no_server_label = (TextView) findViewById(R.id.tv_label);
        buy_server.setOnClickListener(this);
        edit_way_icon.setOnClickListener(this);
        findViewById(R.id.tv_photo).setOnClickListener(this);
        findViewById(R.id.tv_camera).setOnClickListener(this);
        findViewById(R.id.tv_phone).setOnClickListener(this);
        btn_edit_way_voice.setOnFinishedRecordListener(new OnFinishedRecordListener() {

            @Override
            public void onFinishedRecord(String audioPath, int recordTime) {
                mRecordTime = recordTime;
                NewAskModel askModel = NewLocAskModelMsg("", audioPath, 1);
                scrollLast();
                fileUpload(finalDb, askModel);
            }
        });

        // EditText带清除按钮
        // UITool.setEditWithClearButton(edit_way_txt,
        // R.drawable.btn_txt_clear);
        edit_way_txt.addTextChangedListener(mTextWatcher);
        String draft = ConfigParams.getDraftValue(mContext, UserMrg.DEFAULT_MEMBER.mId + mInfo.getDoctorId());
        if (draft != null) {
            edit_way_txt.setText(draft);
        }
        initListView();
        initViewChageListener();
    }

    /**
     * 设置软键盘监听
     */
    private void initViewChageListener() {
        final View rootView = getRootView(getActivity());
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                int offset = rootView.getRootView().getHeight() - rootView.getHeight();
                if (offset > 300 && getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            mListView.setSelection(mAdapter.getCount());
                            isShowMore = false;
                            linMore.setVisibility(isShowMore ? View.VISIBLE : View.GONE);
                        }
                    });
                }
            }
        });
    }

    /**
     * 初始化列表
     */
    private void initListView() {
        mListView = (XListView) findViewById(R.id.ask_content_list_view);
        mListView.setOnItemClickListener(this);
        mListView.setOnTouchListener(this);
        mAdapter = new AskContentAdapter(getActivity(), this, this, models);
        mListView.setAdapter(mAdapter);
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(false);
        // 下拉加载更多消息
        mListView.setXListViewListener(new IXListViewListener() {
            @Override
            public void onRefresh() {
                getLoctionModels(LORDER_MORE, true);
                ThreadHandler.postUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mListView.stopRefresh();
                        mListView.stopLoadMore();
                    }
                }, 2000);
            }
            @Override
            public void onLoadMore() {
                ThreadHandler.postUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mListView.stopRefresh();
                        mListView.stopLoadMore();
                    }
                }, 2000);

            }
        });
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                NewAskModel info = (NewAskModel) arg0.getAdapter().getItem(arg2);
                tempModel = info;
                showPopwindow(info);
                return true;
            }
        });
    }

    private View getRootView(Activity act) {
        return ((ViewGroup) act.findViewById(android.R.id.content)).getChildAt(0);
    }

    /**
     * 删除，复制功能
     *
     * @param model
     */
    private void showPopwindow(final NewAskModel model) {
        String[] items = null;
        if (model.getMsgType() == 4) {
            String[] i1 = {"复制"};
            items = i1;
        } else {
            // String[] i1 = { "删除" };
            // items = i1;
            return;
        }
        MenuPopwindow window = new MenuPopwindow(getApplicationContext(), items);
        window.showAtLocation(mBarView, Gravity.CENTER, 0, 0);
        window.setOnItemClick(new DialogInterface.OnClickListener() {
            @SuppressLint("NewApi")
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        if (model.getMsgType() == 4) {
                            // 将复制的信息放入剪切版
                            ClipboardManager cmb = (ClipboardManager) getActivity().getSystemService(getApplicationContext().CLIPBOARD_SERVICE);
                            cmb.setText(model.getMsgContent());
                            showToast("复制成功");
                        } else {
                            deleatMsg(finalDb, model);
                        }
                        break;
                    // case 1:
                    // requestDeleatMsg(model.getSid() + "");
                    // break;

                    default:
                        break;
                }
            }
        });
    }

    /**
     * 初始化输入框状态
     */
    private void initInputView() {
        inputModeChange(inputmode);
        btn_edit_way_voice.setSavePath(mInfo != null ? mInfo.getDoctorId() : null);

    }

    /**
     * 发送文字内容
     */
    private void sendContent() {
        String answerCon = edit_way_txt.getText().toString();
        if (TextUtils.isEmpty(answerCon)) {
            showToast("请输入内容");
        } else if (answerCon.length() < 1) {
            showToast("请输入1-500字符，包含汉字、字母数字、符号");
        } else if (answerCon.length() > 500) {
            showToast("发送消息内容过长，请分条发送");
        } else {
            NewAskModel askModel = NewLocAskModelMsg(answerCon, "", 0);
            scrollLast();
            requestSendMsg(finalDb, askModel);
            edit_way_txt.setText("");
        }
    }

    /**
     * 新建一条消息
     *
     * @param content
     * @param localUrl
     * @param attachType
     * @return
     */
    private NewAskModel NewLocAskModelMsg(String content, String localUrl, int attachType) {
        String reqnum = TimeUtil.fomateTime(System.currentTimeMillis(), "yyyyMMddHHmmss") + "" + new Random().nextInt(100000);
        NewAskModel askModel = new NewAskModel();
        askModel.setMsgContent(content);
        askModel.setContent(content);
        askModel.setAttachType(attachType + "");
        askModel.setReqNum(reqnum);
        askModel.setMsgType(4);
        askModel.setDoctorId(Long.parseLong(mInfo.getDoctorId()));
        askModel.setVoiceMins(mRecordTime);
        askModel.setLocalUrl(localUrl);
        askModel.setInsertDt(currentTime.format(new Date()));
        askModel.setOwnerType(1);
        askModel.setMessageState(1);
        askModel.setMemberId(Long.parseLong(UserMrg.DEFAULT_MEMBER.mId));
        if (!TextUtils.isEmpty(localUrl)) {
            try {
                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("voiceMins", mRecordTime);
                jsonObject.put("attachType", attachType);
                jsonObject.put("attachUrl", "");
                jsonArray.put(jsonObject);
                askModel.setAttachList(jsonArray.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        finalDb.save(askModel);
        ArrayList<NewAskModel> returns = (ArrayList<NewAskModel>) finalDb.findAllByWhere(NewAskModel.class, "reqNum='" + askModel.getReqNum() + "'");
        if (returns.size() == 1) {
            askModel = returns.get(0);
        }
        models.add(askModel);
        notifyListView(true);
        return askModel;
    }

    /**
     * 相册
     */
    private void toPhoto() {
        if (!Util.SDCardExists()) {
            Toast.makeText(getApplicationContext(), "存储卡不可用，暂无法从相册提取?", Toast.LENGTH_SHORT).show();
            return;
        }
        isChoosedPic = true;
        MyBitmapFactory.clearAll();
        AlbumHelper helper = AlbumHelper.getHelper();
        helper.init(getApplicationContext());
        List<ImageBucket> bucketList = helper.getImagesBucketList(false);
        for (int i = 0; i < bucketList.size(); i++) {
            MyBitmapFactory.tempAllImage.addAll(bucketList.get(i).imageList);
        }
        List<ImageItem4LocalImage> listWithoutDup = new ArrayList<ImageItem4LocalImage>(new HashSet<ImageItem4LocalImage>(
                MyBitmapFactory.tempAllImage));
        MyBitmapFactory.tempAllImage = listWithoutDup;
        MyBitmapFactory.albumnName = "最近照片";
        MyBitmapFactory.tempSelectBitmap.clear();
        toFragment(new ShowLocalImageFragment(1), true, true);
    }

    /**
     * 照相机
     */
    private void toCaramer() {
        if (!Util.SDCardExists()) {
            showToast("存储卡不可用，暂无法完成拍照!");
            return;
        }
        File fileDir = new File(ConfigParams.IMG_CACHE_PATH);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        String uid = UUID.randomUUID().toString();
        uploadtemFile = new File(ConfigParams.IMG_CACHE_PATH, uid + ".png");

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(uploadtemFile));
        startActivityForResult(cameraIntent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode != Activity.RESULT_OK) {
                return;
            }
            /*
             * if (requestCode == IMG) { String picPath =
			 * BitmapUtil.getAbsoluteImageFromUri(getActivity(),
			 * data.getData()); if (TextUtils.isEmpty(picPath)) {
			 * showToast("图片选择出错"); } else { NewAskModel askModel =
			 * NewLocAskModelMsg("", picPath, 0); setListSelect(false);
			 * fileUpload(finalDb, askModel); } }
			 */
            if (requestCode == 0) {
                String picPath = uploadtemFile.getAbsolutePath();

                NewAskModel askModel = NewLocAskModelMsg("", picPath, 0);
                fileUpload(finalDb, askModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast("图片选择出错");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 设置发送按钮的变化
     *
     * @param state
     */
    private void setBtnSendMsg(boolean state) {
        RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) edit_way_txt.getLayoutParams();
        if (state) {
            layoutParams.addRule(RelativeLayout.LEFT_OF, R.id.btn_send_msg);
            iv_send_img.setVisibility(View.GONE);
            btn_send_msg.setVisibility(View.VISIBLE);
            iv_send_img.setOnClickListener(null);
            btn_send_msg.setOnClickListener(this);
        } else {
            layoutParams.addRule(RelativeLayout.LEFT_OF, R.id.iv_send_img);
            iv_send_img.setVisibility(View.VISIBLE);
            btn_send_msg.setVisibility(View.INVISIBLE);
            iv_send_img.setOnClickListener(this);
            btn_send_msg.setOnClickListener(null);
        }
    }

    /**
     * 设置输入UI
     *
     * @param mode true:文字 false:语音
     */
    private void inputModeChange(boolean mode) {
        if (mode) {// 文字输入
            edit_way_icon.setBackgroundResource(R.drawable.seletor_ask_voice);
            setTextInputView(View.VISIBLE);
            setVoiceInputView(View.GONE);
            setBtnSendMsg(state);
        } else {// 语音输入
            linMore.setVisibility(View.GONE);
            edit_way_icon.setBackgroundResource(R.drawable.seletor_ask_edit_way_text);

            setTextInputView(View.GONE);
            setVoiceInputView(View.VISIBLE);
            setBtnSendMsg(false);
        }
        UITool.closeInputMethodManager(getActivity(), edit_way_txt.getApplicationWindowToken());
        inputmode = mode;
    }

    /**
     * 设置输入框显示/隐藏
     *
     * @param visibility
     */
    private void setTextInputView(int visibility) {
        edit_way_txt.setVisibility(visibility);
        btn_send_msg.setVisibility(visibility);
    }

    /**
     * 设置录音按钮显示/隐藏
     *
     * @param visibility
     */
    private void setVoiceInputView(int visibility) {
        btn_edit_way_voice.setVisibility(visibility);
    }

    /**
     * 解析发送消息返回的数据
     *
     * @param finalDb
     * @param result
     * @param askModel
     */
    private void parseAddMsg(final FinalDb finalDb, String result, final NewAskModel askModel) {

        ConfigParams.setMsgDocCount(getApplicationContext(),0);
        try {
            FragmentMrg.getSingleFragment(IndexFrag.class).requestMemUnReadMsgLoader();
        }catch (Exception e){

        }
        if (result == null) {
            // 如果无网络 延迟1.5秒设置消息发送状态为失败
            if (!NetStatusManager.isNetWorkStatus(BaseApplication.getInstance())) {// 针对网络故障做特殊处理
                mListView.setSelection(mAdapter.getCount());

                ThreadHandler.postUiThread(new Runnable() {
                    @Override
                    public void run() {
                        askModel.setMessageState(3);
                        finalDb.update(askModel);
                        notifyListView(true);
                        scrollLast();
                    }
                }, 1500);
            } else {
                askModel.setMessageState(3);
                finalDb.update(askModel);
                notifyListView(true);
                scrollLast();
            }
            return;
        }
        try {
            ComveePacket packet = ComveePacket.fromJsonString(result);
            if (packet.getResultCode() == 0) {
                JSONObject body = packet.optJSONObject("body");
                String returnTime = body.optString("returnDate");
                long sid = body.optLong("sid");
                askModel.setSid(sid);
                askModel.setInsertDt(returnTime);
                askModel.setMessageState(2);
                finalDb.update(askModel);
            } else {
                askModel.setMessageState(3);
                finalDb.update(askModel);
            }
            notifyListView(true);
            scrollLast();
        } catch (Exception e) {
        }
    }

    /**
     * 解析阿里云返回的数据
     */
    private void parseFileMsg(final NewAskModel model, String result) {
        if (result == null) {
            if (!NetStatusManager.isNetWorkStatus(BaseApplication.getInstance())) {// 针对网络故障做特殊处理
                mListView.setSelection(mAdapter.getCount());

                ThreadHandler.postUiThread(new Runnable() {
                    @Override
                    public void run() {
                        model.setMessageState(3);
                        finalDb.update(model);
                        notifyListView(true);
                        scrollLast();
                    }
                }, 1500);
            } else {

                model.setMessageState(3);
                finalDb.update(model);
                notifyListView(true);
                scrollLast();
            }
            return;

        }
        ComveePacket packet;
        try {
            packet = ComveePacket.fromJsonString(result);
            if (packet.getResultCode() == 0) {
                JSONArray body = packet.optJSONArray("body");
                if (body.length() > 0) {
                    JSONObject obj = body.optJSONObject(0);
                    String url = obj.optString("url") + "/" + obj.optString("key");
                    model.setAttachUrl(url);
                    requestSendMsg(finalDb, model);
                }
            } else {
                model.setMessageState(3);
                finalDb.update(model);
            }
            notifyListView(true);
            scrollLast();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 点击图片 全屏显示
     */
    private void showImageDialog(NewAskModel item) {
        if (!TextUtils.isEmpty(item.getLocalUrl())) {
            ImageDialogActivity.showImg(getContext(), "file://" + item.getLocalUrl());
        } else {
            ImageDialogActivity.showImg(getContext(), item.getAttachUrl());
        }
    }

    private void playVoice(View view, NewAskModel item) {

        if (item != null) {
            PlayImageView playImageView = null;
            try {
                playImageView = (PlayImageView) view.findViewById(R.id.iv_voice_play);
            } catch (Exception e) {
                e.printStackTrace();
            }
            MediaPlayerUtils.getInstance(getContext()).playNetVoice(item.voice.getAttUrl(), item.voice, new Handler(), playImageView);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        NewAskModel model = (NewAskModel) arg0.getAdapter().getItem(arg2);
        tempModel = model;
        switch (model.getMsgType()) {
            case 1:
                toFragment(ExceptionFragment.newInstance(model.getForeignId() + "", 10), true, true);
                break;
            case 2:
                toFragment(ExceptionFragment.newInstance(model.getForeignId() + "", 12), true, true);
                break;
            case 3:
                toFragment(ExceptionFragment.newInstance(model.getForeignId() + "", 11), true, true);
                break;

            case 4:
                if (model.getAttachType().equals("0")) {
                    showImageDialog(model);
                } else if (model.getAttachType().equals("1")) {
                    playVoice(arg1, model);
                } else if (model.getAttachType().equals("2")) {

                }
                break;
            case 5:
                String str[] = model.getJobList().split(",");
                if (str.length == 1) {
                    TaskItem item = new TaskItem();
                    item.setJobCfgId(model.getJobList() + "");
                    TaskIntroduceFragment action = TaskIntroduceFragment.newInstance();
                    action.setTaskInfo(item);
                    action.setDoctorId(model.getDoctorId());
                    toFragment(action, true, true);
                }
                if (str.length > 1) {
                    TaskCenterFragment fragment2 = new TaskCenterFragment();
                    fragment2.setDoctorId(model.getDoctorId() + "");
                    fragment2.setIsTaskcent(2);
                    fragment2.setType(model.getJobList());
                    toFragment(fragment2, true, true);
                }
                break;

            case 6:
                toFragment(MemberRecordFragment.newInstance(2, false, model.getForeignId() + ""), true, true);
                break;
            case 7:
                toFragment(FollowRecordFragment.newInstance(1, model.getForeignId()), true, true);
                break;

            case 8:

                break;
            case 9:

                break;
            case 10:

                break;
            case 11:
                toFragment(AskTellDetailFragment.newInstance(model.getForeignId() + ""), true, true);
                break;
            case 22:
                FragmentMrg.toFragment(getActivity(), RecordLaboratoryFragment.class, null, true);
                break;
            default:
                break;
        }
    }

    public void showQuestProDialog(String str) {
        if (mProDialog == null) {
            mProDialog = CustomProgressDialog.createDialog(getActivity());
            mProDialog.setCanceledOnTouchOutside(false);
        }
        mProDialog.setMessage(str);
        if (!mProDialog.isShowing()) {
            mProDialog.show();
        }
    }

    public void cancelQuestProDialog() {
        try {
            if (mProDialog != null) {
                mProDialog.dismiss();
                mProDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toPatientPersonal(String url) {
        if (url != null && !url.equals("")) {
            if (UserMrg.getMemberSessionId(getApplicationContext()) != null && !UserMrg.getMemberSessionId(getApplicationContext()).equals("")
                    && UserMrg.getSessionId(getApplicationContext()) != null && !UserMrg.getSessionId(getApplicationContext()).equals("")) {
                toFragment(
                        WebFragment.newInstance("绿星计划", url + "?origin=android&sessionID=" + UserMrg.getSessionId(getApplicationContext())
                                + "&sessionMemberID=" + UserMrg.getMemberSessionId(getApplicationContext())), true, true);
            }
        } else {
            DoctorServerList.toFragment(getActivity(), mInfo.getDoctorId());
        }
    }

    @Override
    public boolean onTouch(View arg0, MotionEvent arg1) {
        if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
            isShowMore = false;
            linMore.setVisibility(View.GONE);
            UITool.closeInputMethodManager(getActivity(), edit_way_txt.getApplicationWindowToken());
        } else if (arg1.getAction() == MotionEvent.ACTION_MOVE) {
            UITool.closeInputMethodManager(getActivity(), edit_way_txt.getApplicationWindowToken());
            isShowMore = false;
            linMore.setVisibility(View.GONE);
        }
        return false;
    }

    /**
     * 文件上传到阿里云 为了避免图片太大，本地先压缩图片后再上传
     */
    private void fileUpload(FinalDb finalDb, final NewAskModel model) {
        isShowMore = false;
        linMore.setVisibility(isShowMore ? View.VISIBLE : View.GONE);
        scrollLast();
        if (TextUtils.isEmpty(model.getLocalUrl())) {
            showToast("找不到文件");
            models.remove(model);
            finalDb.delete(model);
            return;
        }
        File file = new File(model.getLocalUrl());
        if (!file.exists()) {
            showToast("找不到文件");
            models.remove(model);
            finalDb.delete(model);
            return;
        }
        new ComveeTask<String>() {

            @Override
            protected String doInBackground() {
                ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.UPLOAD_FILE);

                if ("0".equals(model.getAttachType())) {
                    BitmapFactory.Options newOpts = new BitmapFactory.Options();
                    newOpts.inJustDecodeBounds = true;
                    Bitmap bitmap = BitmapFactory.decodeFile(model.getLocalUrl(), newOpts);// 此时返回bm为空
                    newOpts.inJustDecodeBounds = false;
                    int w = newOpts.outWidth;
                    int h = newOpts.outHeight;
                    float be = 1;// be=1表示不缩放
                    if (w > h && w > 1024) {// 如果宽度大的话根据宽度固定大小缩放
                        be = (w * 1.0f / 1024);
                    } else if (w < h && h > 768) {// 如果高度高的话根据宽度固定大小缩放
                        be = (h * 1.0f / 768);
                    }
                    if (be <= 0)
                        be = 1;
                    newOpts.inSampleSize = (int) Math.ceil(be);// 设置缩放比例
                    bitmap = BitmapFactory.decodeFile(model.getLocalUrl(), newOpts);
                    // ** 质量压缩 *//*

                    try {
                        File file = new File(compressPath);
                        FileOutputStream outputStream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
                        outputStream.flush();
                        outputStream.close();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    http.setUploadFileForKey("file", compressPath);
                } else {
                    http.setUploadFileForKey("file", model.getLocalUrl());
                }
                http.setPostValueForKey("platCode", "198");// 104 糖尿病 198 临时
                String result = http.startSyncRequestString();
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                parseFileMsg(model, result);
            }
        }.execute();

    }

    /**
     * 患者咨询
     *
     * @param finalDb
     * @param model
     */
    private void requestSendMsg(final FinalDb finalDb, final NewAskModel model) {

        new ComveeTask<String>() {

            @Override
            protected String doInBackground() {
                ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.ASK_SUBMIT_NEW_CONTENT);
                http.setPostValueForKey("answerContent", model.getMsgContent());
                http.setPostValueForKey("doctorId", model.getDoctorId() + "");
                http.setPostValueForKey("attachType", model.getAttachType());
                http.setPostValueForKey("attachUrl", model.getAttachUrl());
                http.setPostValueForKey("voiceMins", model.getVoiceMins() + "");
                http.setPostValueForKey("req_num", model.getReqNum());
                String result = http.startSyncRequestString();
                return result;
            }

            @Override
            protected void onPostExecute(String result) {

                super.onPostExecute(result);
                parseAddMsg(finalDb, result, model);
            }
        }.execute();
    }

    /**
     * 删除会话
     *
     * @param finalDb
     * @param model
     */
    private void deleatMsg(FinalDb finalDb, final NewAskModel model) {
        new ComveeTask<String>() {
            @Override
            protected String doInBackground() {
                ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.ASK_DELEAT_MSG);
                http.setPostValueForKey("sid", model.getSid() + "");
                String result = http.startSyncRequestString();
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
            }
        }.execute();
        ;
    }

    /**
     * 获取本地的数据库信息 包括分页信息
     *
     * @param finalDb
     */
    private void selectLocPageMsg(final FinalDb finalDb) {

        locPageMsg = new LocPageMsg();
        NewAskModel askModel = new NewAskModel();
        askModel.setId(-1);
        finalDb.update(askModel);
        finalDb.deleteByWhere(NewAskModel.class, "isValid='0'");
        ServerListModel serverListModel = new ServerListModel();
        serverListModel.setId(-1);
        finalDb.update(serverListModel);
        DbModel dbModel = finalDb.findDbModelBySQL("select count(*) from NewAskModel where memberId=" + UserMrg.DEFAULT_MEMBER.mId + " and doctorId="
                + mInfo.getDoctorId());
        locPageMsg.setTotalRows(Integer.parseInt((String) dbModel.getDataMap().get("count(*)")));
        locPageMsg.setPageSize(20);
        locPageMsg.setTotalPages(locPageMsg.getTotalRows() / locPageMsg.getPageSize()
                + (locPageMsg.getTotalRows() % locPageMsg.getPageSize() > 0 ? 1 : 0));

    }

    /**
     * 特殊的消息类型 12 的点击事件监听
     */
    @Override
    public void onclick(NewAskModel askModel) {
        if (TextUtils.isEmpty(askModel.getAttachList()) || "[]".equals(askModel.getAttachList())) {
            askModel.setMessageState(1);
            finalDb.update(askModel);
            requestSendMsg(finalDb, askModel);
        } else {

            if (!TextUtils.isEmpty(askModel.getAttachUrl())) {
                askModel.setMessageState(1);
                finalDb.update(askModel);
                requestSendMsg(finalDb, askModel);
            } else {
                askModel.setMessageState(1);
                // finalDb.delete(askModel);
                finalDb.update(askModel);
                fileUpload(finalDb, askModel);
            }

        }
    }

    /**
     * 滑动到列表底部
     */
    private void scrollLast() {
        if (models.size() > 0) {
            mListView.setSelection(models.size() - 1);
        }
    }

    @Override
    public boolean onBackPress() {
        // TODO Auto-generated method stub
        if (ConfigParams.TO_BACK_TYPE == 2) {
            ConfigParams.TO_BACK_TYPE = 1;
            if (FragmentMrg.indexOfFragment(AskIndexFragment.class) != -1) {
                FragmentMrg.popBackToFragment(getActivity(), AskIndexFragment.class, null);
            } else {
                toFragment(AskIndexFragment.class, null, true);
            }
            return true;
        } else if (ConfigParams.TO_BACK_TYPE == 3) {
            ConfigParams.TO_BACK_TYPE = 1;
            IndexFrag.toFragment(getActivity(), true);
            return true;
        } else {
            ConfigParams.TO_BACK_TYPE = 1;
            return false;
        }

    }

    /**
     * 初始化列表加载接口
     */
    private void initListLoader() {
        listLoader = new AskQuestionRequestListLoader(getApplicationContext(), new NetworkCallBack() {

            @Override
            public void callBack(int what, int status, Object obj) {
                // mListView.stopRefresh();
                mListView.setVisibility(View.VISIBLE);
                if (obj instanceof PageModel) {
                    PageModel model = (PageModel) obj;
                    if (model.currentPage == model.totalPages) {
                        mListView.setPullRefreshEnable(false);
                    }
                }
                initEditext();
                getLoctionModels(LORDER_MORE, false);
                // getLocNewAskModel(models, finalDb, starTime, false);
            }
        }, finalDb, mInfo.getDoctorId());
    }

    /**
     * 初始化列表刷新接口
     */
    private void initRefreshLoader() {
        refreshLoader = new AskQuestionRefreshLoader(getApplicationContext(), new NetworkCallBack() {

            @Override
            public void callBack(int what, int status, Object obj) {
                mListView.stopRefresh();
                mListView.setVisibility(View.VISIBLE);
                initEditext();
                getLoctionModels(REFRESH, false);
            }
        }, finalDb, mInfo.getDoctorId());
    }

    /**
     * 用于接收推送后刷新数据
     *
     * @author Administrator
     */
    class AskQustionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            refreshLoader.starLoader(lastTime);
        }

    }
}
