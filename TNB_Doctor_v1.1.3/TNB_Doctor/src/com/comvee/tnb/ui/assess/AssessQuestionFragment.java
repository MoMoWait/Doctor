package com.comvee.tnb.ui.assess;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.comvee.ThreadHandler;
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
import com.comvee.tnb.model.AssessQuestion;
import com.comvee.tnb.network.ObjectLoader;
import com.comvee.tnb.view.AssessBaseContentView;
import com.comvee.tnb.view.AssessDateContentView;
import com.comvee.tnb.view.AssessInputContentView;
import com.comvee.tnb.view.AssessMultiContentView;
import com.comvee.tnb.view.AssessNumContentView;
import com.comvee.tnb.view.AssessSingleContentView;
import com.comvee.tnb.widget.MySeekBar;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UserMrg;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 健康评估（做题目页面）
 *
 * @author friendlove
 */
public class AssessQuestionFragment extends BaseFragment implements DialogInterface.OnClickListener, OnHttpListener, OnClickListener {
    private ArrayList<Integer> mQuestIndexs = new ArrayList<Integer>();
    private ArrayList<AssessQuestion> mAnswerList = new ArrayList<AssessQuestion>();
    private ArrayList<AssessQuestion> mQuestionList;
    private HashMap<Integer, AssessQuestion> mQestionMap = new HashMap<Integer, AssessQuestion>();
    private ViewGroup layoutContent;
    private int curIndex;
    private MySeekBar mSeekbar;
    private Button btnSubmit;// 提交按钮
    private Button btnJump;// 跳转评估列表 按钮
    private Button btnPre;
    private int nFirstIndex = -1;
    public int questionType;
    private ScrollView mScrollView;
    private TextView mTvSeek;
    private TitleBarView mBarView;

    public static AssessQuestionFragment newInstance() {
        return new AssessQuestionFragment();
    }

    public void setQuestionType(int type) {
        questionType = type;
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.fragment_assess_question;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    public AssessQuestionFragment() {
    }

    @Override
    public void onLaunch(Bundle bundle) {
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        mRoot.setVisibility(View.GONE);
        init();
        mBarView.setTitle(getString(R.string.title_assess));
    }

    private void init() {
        mTvSeek = (TextView) findViewById(R.id.tv_seekbar);
        mScrollView = (ScrollView) findViewById(R.id.scrollview);
        mSeekbar = (MySeekBar) findViewById(R.id.seekbar);
        layoutContent = (ViewGroup) findViewById(R.id.layout_content);
        btnSubmit = (Button) findViewById(R.id.btn_next);
        btnPre = (Button) findViewById(R.id.btn_pre);
        btnJump = (Button) findViewById(R.id.btn_jump);
        btnJump.setOnClickListener(this);
        btnPre.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        requestQuestionList();
    }

    /**
     * 获取可用的套餐列表
     */
    private void requestQuestionList() {
        showProgressDialog(getString(R.string.msg_loading));
        ObjectLoader<AssessQuestion> loader = new ObjectLoader<AssessQuestion>();
        loader.putPostValue("rows", "1000");
        loader.putPostValue("quesType", String.valueOf(questionType));
        loader.loadBodyArray(AssessQuestion.class, ConfigUrlMrg.ASSESS_QUESTION_LIST, loader.new CallBack() {

            @Override
            public void onBodyArraySuccess(boolean isFromCache, ArrayList<AssessQuestion> obj) {
                cancelProgressDialog();
                if(obj!=null){
                    mQuestionList = obj;
                    for (AssessQuestion item : obj) {
                        mQestionMap.put(item.ques.showSeq, item);
                    }
                    mSeekbar.setMax(mQuestionList.size());
                    mRoot.setVisibility(View.VISIBLE);
                    jumpSeq(1);
                    btnPre.setEnabled(false);
                }

            }

            @Override
            public boolean onFail(int code) {
                cancelProgressDialog();
                return super.onFail(code);
            }
        });
    }

    private void requestSubmit() {
        showProgressDialog("正在提交中...");
        ComveeHttp http = null;
        http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.ASSESS_QUESTION_SUBMIT);
        http.setOnHttpListener(2, this);
        http.setPostValueForKey("paramStr", getUploadJson().toString());
        http.startAsynchronous();
    }

    private JSONArray getUploadJson() {
        JSONArray json = new JSONArray();
        for (AssessQuestion item : mAnswerList) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("code", item.ques.vhQid);
                obj.put("value", item.displayValue);
                obj.put("itemType", String.valueOf(item.ques.itemType));
                json.put(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return json;
    }


    private void requestCheckSubmit() {
        showProgressDialog("正在提交中...");
        ComveeHttp http = null;
        http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.ASSESS_QUESTION_CHECK_SUBMIT);
        http.setOnHttpListener(3, this);
        http.setPostValueForKey("paramStr", getUploadJson().toString());
        http.startAsynchronous();
    }

    private void parseQuestionSubmit(byte[] b) {
        try {
            ComveePacket packet = ComveePacket.fromJsonString(b);
            if (packet.getResultCode() == 0) {

                ConfigParams.setAssessNum(getApplicationContext(), ConfigParams.getAssessNum(getApplicationContext()) + 1);
                // showToast(packet.getResultMsg());
                showWaitDialog();
                // ((MainActivity) getActivity()).toIndexFragment();
                // AssessReportFragment frag =
                // AssessReportFragment.newInstance();
                // // frag.setReportId(reportId);
                // ((MainActivity) getActivity()).toFragmentAfterIndex(frag,
                // true);
                // 清楚 提醒列表 的缓存
                ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.REMIND_LIST));
                ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.INDEX));
            } else {
                ComveeHttpErrorControl.parseError(getActivity(), packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast(R.string.error);
        }
    }


    // 低风险评估 评分
    private void parseQestionSubmitCheck(byte[] b) {
        try {
            ComveePacket packet = ComveePacket.fromJsonString(b);
            if (packet.getResultCode() == 0) {

                // 是否要弹出提示 转 高风险评估
                int isDialog = packet.getJSONObject("body").optInt("dialog");
                if (isDialog == 1) {
                    CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
                    builder.setMessage(packet.getResultMsg());
                    builder.setPositiveButton("是", this);
                    builder.setNegativeButton("否", this);
                    builder.create().show();
                } else {
                    requestSubmit();
                }

                ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.REMIND_LIST));
            } else {
                ComveeHttpErrorControl.parseError(getActivity(), packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast(R.string.error);
        }
    }

    private AssessBaseContentView setupView(AssessQuestion info) {
        AssessBaseContentView curQuetionView = null;
        switch (info.ques.itemType) {
            case 1:
                curQuetionView = new AssessDateContentView(getApplicationContext(), this);
                break;
            case 2:// 单选
                curQuetionView = new AssessSingleContentView(getApplicationContext(), this);
                break;
            case 3:// 多选
            case 6:
                curQuetionView = new AssessMultiContentView(getApplicationContext(), this);
                break;
            case 4:
                curQuetionView = new AssessNumContentView(getApplicationContext(), this);
                break;
            case 5:
                curQuetionView = new AssessInputContentView(getApplicationContext(), this);
            default:
                break;
        }
        curQuetionView.setQuestion(info);
        curQuetionView.init();
        layoutContent.removeAllViews();
        layoutContent.addView(curQuetionView, -1, -1);
        return curQuetionView;
    }

    AssessBaseContentView curQuestionView;

    private void jumpSeq(int showSeq) {
        AssessQuestion item = mQestionMap.get(showSeq);
        if(TextUtils.isEmpty(item.displayValue) && !TextUtils.isEmpty(item.ques.realValue)){
            try {
                item.displayValue = new JSONObject(item.ques.realValue).optString("value");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        curQuestionView = setupView(item);
        mSeekbar.setProgress(mQuestionList.indexOf(item));
    }

    public void setCanJump(boolean b) {
        btnSubmit.setVisibility(b ? View.GONE : View.VISIBLE);
        btnJump.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    private void toNext() {
        try {
            AssessQuestion item = curQuestionView.getQuestion();
            if (curQuestionView.getNextIndex() > 0) {
                mAnswerList.add(item);
                mQuestIndexs.add(item.ques.showSeq);
                jumpSeq(curQuestionView.getNextIndex());
                mScrollView.scrollTo(0,0);
            } else {

                if (mQuestionList.get(mQuestionList.size() - 1) == item || curQuestionView.getNextIndex() == -1) {
                    mAnswerList.add(item);
                    mQuestIndexs.add(item.ques.showSeq);
                    if (questionType == 1) {
                        requestCheckSubmit();
                    } else {
                        requestSubmit();
                    }
                } else {
                    showToast("您还未选择答案");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSussece(int what, byte[] b, boolean fromCache) {
        cancelProgressDialog();
        switch (what) {
            case 2:
                parseQuestionSubmit(b);
                break;
            case 3:
                parseQestionSubmitCheck(b);
                break;

            default:
                break;
        }
    }

    @Override
    public void onFialed(int what, int errorCode) {
        ComveeHttpErrorControl.parseError(getActivity(), errorCode);
        cancelProgressDialog();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_jump:
                try {
                    curQuestionView.getQuestion().displayValue = "";
                    if (mQestionMap.get(curQuestionView.getNextIndex()).ques.goTo == -1) {
                        if (questionType == 1) {
                            requestCheckSubmit();
                        } else {
                            requestSubmit();
                        }
                    } else {
                        jumpSeq(curQuestionView.getNextIndex());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_next:
                if (curQuestionView.getNextIndex()==mQuestionList.size()) {
                    btnSubmit.setText("提交");
                }
                toNext();//点击下一题调用
                btnPre.setEnabled(true);

                break;
            case R.id.btn_pre:
                if (mQuestIndexs.size() > 0) {
                    jumpSeq(mQuestIndexs.get(mQuestIndexs.size() - 1));
                    mQuestIndexs.remove(mQuestIndexs.size() - 1);
                    mAnswerList.remove(mAnswerList.size() - 1);
                    if(mQuestIndexs.isEmpty()){
                        btnPre.setEnabled(false);
                    }else{
                        btnPre.setEnabled(true);
                    }
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(DialogInterface arg0, int arg1) {
        switch (arg1) {
            case CustomDialog.ID_NO:
                requestSubmit();
                break;
            case CustomDialog.ID_OK:
                questionType = 2;
                requestQuestionList();
                break;

            default:
                break;
        }

    }

    @Override
    public boolean onBackPress() {
        showExitAlert();
        return true;
    }

    private void showWaitDialog() {

        // showProDialog("正在生成报告，请稍后....");

        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setIndeterminate(false);
        dialog.setMax(100);
        dialog.setCancelable(false);
        dialog.setMessage("正在生成报告，请稍后....");
        dialog.show();
        new Thread() {
            @Override
            public void run() {
                try {
                    int xh_count = dialog.getProgress();
                    while (xh_count <= 100) {
                        // 由线程来控制进度
                        dialog.setProgress(xh_count++);
                        Thread.sleep(100);
                    }
                    dialog.cancel();
                } catch (Exception e) {
                    dialog.cancel();
                }
            }
        }.start();

        ThreadHandler.postUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.cancel();
                // MemberCenterFragment frag =
                // MemberCenterFragment.newInstance();
                // ParamsConfig.setReportNew(getApplicationContext(), true);
                ConfigParams.setTaskNew(getApplicationContext(), true);
                AssessListFragment frag = AssessListFragment.newInstance();
                toFragment(frag, true);
                // showToast("请查看评估报告或健康任务");
            }
        }, 10 * 1000);

    }

    /**
     * 退出 警告
     */
    private void showExitAlert() {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == CustomDialog.ID_OK) {
                    FragmentMrg.toBack(getActivity());
                }
            }
        };
        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setTitle("提示");
        builder.setMessage("您确定要放弃当前评估吗？");
        builder.setPositiveButton("确定", listener);
        builder.setNegativeButton("取消", listener);
        builder.create().show();
    }


//    @Override
//    public String getValue() {
//        try {
//
//            StringBuffer sb = new StringBuffer();
//            for (int i = 0; i < layoutCheck.getChildCount(); i++) {
//                if (isCheckItem(i)) {
//                    sb.append(mInfo.items.get(i).value);
//                    sb.append("|");
//                }
//            }
//
//            if (sb.length() > 1) {
//                String str = sb.substring(0, sb.length() - 1);
//                JSONObject obj = new JSONObject();
//                obj.put("code", mInfo.vhQid);
//                obj.put("value", str);
//                obj.put("itemType", mInfo.itemType);
//                value = obj.toString();
//            } else {
//                value = "";
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return value;
//    }
}
