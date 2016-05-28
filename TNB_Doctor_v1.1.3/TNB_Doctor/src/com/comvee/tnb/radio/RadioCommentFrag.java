package com.comvee.tnb.radio;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comvee.ThreadHandler;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.model.RadioComment;
import com.comvee.tnb.model.RadioCommentModel;
import com.comvee.tnb.network.NetworkCallBack;
import com.comvee.tnb.network.RadioCommentControl;
import com.comvee.tnb.network.RadioCommentLoader;
import com.comvee.tnb.radio.NickNameWindow.NickNameCallback;
import com.comvee.tnb.ui.xlistview.XListView;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.ViewHolder;
import com.comvee.util.UITool;
import com.easemob.easeui.domain.EaseEmojicon;
import com.easemob.easeui.domain.EaseEmojiconGroupEntity;
import com.easemob.easeui.model.EaseDefaultEmojiconDatas;
import com.easemob.easeui.widget.emojicon.EaseEmojiconMenu;
import com.easemob.easeui.widget.emojicon.EaseEmojiconMenuBase.EaseEmojiconMenuListener;

import java.util.ArrayList;
import java.util.Arrays;

public class RadioCommentFrag extends BaseFragment implements NetworkCallBack, OnClickListener, XListView.IXListViewListener {
    private XListView mListView;
    private RadioCommentAdapter mAdapter;
    private RadioCommentLoader mLoader;
    private EditText edtInput;
    private View btnFace, btnSend;
    private EaseEmojiconMenu emojiconMenu;
    private String type;// 评论类型 1 专辑 2 节目
    private String radioId;
    private ArrayList<RadioComment> mList;
    private View footView, headView, floatView;
    private ViewHolder viewHolder;
    private int brilliantCount;// 精彩评论条数
    private NickNameWindow mWindow;

    @Override
    public int getViewLayoutId() {
        return R.layout.radio_comment_frag;
    }

    public static void toFragment(FragmentActivity act, String radioId, String type) {
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        bundle.putString("radioId", radioId);
        FragmentMrg.toFragment(act, RadioCommentFrag.class, bundle, true);
    }

    @Override
    public void onLaunch(Bundle dataBundle) {
        super.onLaunch(dataBundle);
        if (dataBundle != null) {
            type = dataBundle.getString("type");
            radioId = dataBundle.getString("radioId");
        }
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        TitleBarView titleView = (TitleBarView) findViewById(R.id.layout_top);
        titleView.setTitle("内容评论");
        titleView.setVisibility(View.VISIBLE);
        titleView.setLeftButton(R.drawable.top_menu_back, this);
        btnFace = findViewById(R.id.btn_face);
        btnSend = findViewById(R.id.btn_send);
        edtInput = (EditText) findViewById(R.id.edt_input);
        edtInput.addTextChangedListener(mTextWatcher);
        mWindow = new NickNameWindow();
        findViewById(R.id.btn_send).setOnClickListener(this);
        findViewById(R.id.btn_face).setOnClickListener(this);
        initListView();
        initEmoji();
        initViewChageListener();
        mLoader = new RadioCommentLoader(this);
        mLoader.loadMore(radioId, type);

    }

    /**
     * 初始化listview 同时添加“加载更多”控件
     */
    private void initListView() {
        mListView = (XListView) findViewById(R.id.list_view);
        headView = View.inflate(TNBApplication.getInstance(), R.layout.radio_comment_floattag, null);
        floatView = findViewById(R.id.newstComment_float);
        mListView.addHeaderView(headView);
        viewHolder = ViewHolder.getViewHolder(getContext(), footView, null, R.layout.radio_comment_footview);
        footView = viewHolder.mConvertView;
        initFootView();
        mListView.addFooterView(footView);
        if (mList == null) {
            mList = new ArrayList<RadioComment>();
        }
        if (mAdapter == null) {
            mAdapter = new RadioCommentAdapter();
        }
        mAdapter.setList(mList);
        mAdapter.setOnClickListener(this);
        mListView.setAdapter(mAdapter);
        mListView.setXListViewListener(this);
        mListView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    emojiconMenu.setVisibility(View.GONE);
                }
                return false;
            }
        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem > 0) {
                    floatView.setVisibility(View.VISIBLE);
                } else {
                    floatView.setVisibility(View.GONE);
                }
            }

        });
    }

    /**
     * 初始化emoji表情框
     */
    private void initEmoji() {
        emojiconMenu = (EaseEmojiconMenu) findViewById(R.id.emojicon);
        ArrayList<EaseEmojiconGroupEntity> emojiconGroupList = new ArrayList<EaseEmojiconGroupEntity>();
        emojiconGroupList.add(new EaseEmojiconGroupEntity(R.drawable.ee_1, Arrays.asList(EaseDefaultEmojiconDatas.getData())));
        ((EaseEmojiconMenu) emojiconMenu).init(emojiconGroupList);
        emojiconMenu.setEmojiconMenuListener(new EaseEmojiconMenuListener() {

            @Override
            public void onExpressionClicked(EaseEmojicon emojicon) {
                sendMsg(emojicon.getEmojiText(), "");
            }

            @Override
            public void onDeleteImageClicked() {
                emojiconMenu.setVisibility(View.GONE);
            }
        });
    }

    private TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (TextUtils.isEmpty(edtInput.getText())) {
                btnFace.setVisibility(View.VISIBLE);
                btnSend.setVisibility(View.GONE);
            } else {
                btnFace.setVisibility(View.GONE);
                btnSend.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    public void callBack(int what, int status, Object obj) {
        cancelProgressDialog();
        ///swipeRefreshLayout.setRefreshing(false);
        if (obj == null) {
            return;
        }
        RadioCommentModel commentModel = (RadioCommentModel) obj;
        if (commentModel.loadType == RadioCommentLoader.REFRESH) {
            mList.clear();
        } else {
            brilliantCount = commentModel.brilliantCount;
        }
        if (commentModel.currentPage == commentModel.totalPages) {
            footView.setVisibility(View.GONE);
        } else {
            footView.setVisibility(View.VISIBLE);
            initFootView();
        }
        mList.addAll(commentModel.mList);
        mAdapter.setBrilliantCount(commentModel.brilliantCount);
        mAdapter.notifyDataSetChanged();

    }

    public void sendMsg(final String msg, String name) {
        showProgressDialog("请稍后...");
        new RadioCommentControl().addComment(radioId, msg, type, mReplyComment, name, new NetworkCallBack() {
            @Override
            public void callBack(final int what, int status, final Object obj) {
                try {
                    cancelProgressDialog();
                    if (null == obj) {
                        return;
                    }
                    if (status == 0) {
                        mWindow.setShow(true);
                        mWindow.dismiss();
                        mList.add(brilliantCount, (RadioComment) obj);
                        mAdapter.notifyChanged(brilliantCount);
                        mListView.setSelection(what);
                        mReplyComment = null;
                        edtInput.setHint("发表评论");
                        edtInput.setText(null);
                        try {
                            UITool.closeInputMethodManager(edtInput.getWindowToken());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (status == 10002) {
                        mWindow.setShow(false);
                        showSetNameWindow(msg, obj.toString());
                    } else if (status == 10001) {
                        mWindow.setShow(false);
                        showSetNameWindow(msg, obj.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                   // showToast("网络出岔子了！");
                }

            }
        });
    }

    private View getRootView(Activity act) {
        return ((ViewGroup) act.findViewById(android.R.id.content)).getChildAt(0);
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
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TitleBarView.ID_LEFT_BUTTON:
                onBackPress();
                break;
            case R.id.tv_comment_reply:
                toReply((RadioComment) v.getTag());
                break;
            case R.id.tv_comment_count:
                RadioComment comment = (RadioComment) v.getTag();
                if (comment.isParise == 0) {
                    comment.pariseNum += 1;
                    comment.isParise = 1;
                    mAdapter.notifyDataSetChanged();
                } else {
                    comment.isParise = 0;
                    comment.pariseNum -= 1;
                    mAdapter.notifyDataSetChanged();
                }
                RadioCommentControl control = new RadioCommentControl();
                control.requestPraise(comment, null);
                break;
            case R.id.btn_send:
                String msg = edtInput.getText().toString().trim();
                if (TextUtils.isEmpty(msg)) {
                    UITool.showEditError(edtInput, "不能为空");
                    return;
                }
                sendMsg(msg, "");
                break;
            case R.id.btn_face:
                if (emojiconMenu.getVisibility() == View.VISIBLE) {
                    emojiconMenu.setVisibility(View.GONE);
                } else {
                    emojiconMenu.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.loader_more:
                loaderMore();
                break;
            default:
                break;
        }
    }

    /**
     * 加载更多评论，同时设置加载更多时的界面
     */
    private void loaderMore() {
        mLoader.loadMore(radioId, type);
        if (viewHolder != null) {
            TextView tv = viewHolder.get(R.id.tv_load);
            tv.setText(R.string.loading);
            viewHolder.get(R.id.progressbar).setVisibility(View.VISIBLE);
            viewHolder.get(R.id.img).setVisibility(View.GONE);
            viewHolder.get(R.id.loader_more).setOnClickListener(null);
        }
    }

    /**
     * 初始化“加载更多”控件
     */
    private void initFootView() {
        if (viewHolder != null) {
            TextView tv = viewHolder.get(R.id.tv_load);
            tv.setText(R.string.loader_more_comment);
            viewHolder.get(R.id.progressbar).setVisibility(View.GONE);
            viewHolder.get(R.id.img).setVisibility(View.VISIBLE);
            viewHolder.get(R.id.loader_more).setOnClickListener(this);
        }
    }

    private RadioComment mReplyComment;

    private void toReply(RadioComment comment) {
        edtInput.setHint("回复:@" + comment.memberName);
        mReplyComment = comment;
        UITool.autoOpenInputMethod(getApplicationContext(), edtInput, 100);
    }

    private  boolean isPopWindow;

    @Override
    public boolean onBackPress() {
        FragmentMrg.toBack(getActivity());
//		FragmentMrg.popBackToFragment(getActivity(), FragmentMrg.getLastFragment().getClass(), null, false);
        return true;
    }

    private void showSetNameWindow(final String context, String title) {
        mWindow.setTetle(title);
        mWindow.show(getActivity().findViewById(R.id.content), new NickNameCallback() {
            @Override
            public void onCancel() {
            }

            @Override
            public void onCallBack(String name) {
                // ===========================提交昵称==========================//
                sendMsg(context, name);

                // =================================================================//
            }
        });

    }

    @Override
    public void onRefresh() {
        floatView.setVisibility(View.GONE);
        ThreadHandler.postUiThread(new Runnable() {
            @Override
            public void run() {
                mListView.stopRefresh();
                floatView.setVisibility(View.VISIBLE);
            }
        }, 800);
    }

    @Override
    public void onLoadMore() {

    }
}
