package com.comvee.tnb.ui.heathknowledge;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alipay.mobilesecuritysdk.deviceID.LOG;
import com.comvee.ThreadHandler;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.adapter.NewsListAdapter;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.ComveeTask;
import com.comvee.tnb.model.NewsListInfo;
import com.comvee.tnb.network.ObjectLoader;
import com.comvee.tnb.ui.ask.AskIndexFragment;
import com.comvee.tnb.ui.book.BookWebActivity;
import com.comvee.tnb.ui.newsknowledg.NewsKnowledgeFragment;
import com.comvee.tnb.ui.xlistview.XListView;
import com.comvee.tnb.ui.xlistview.XListView.IXListViewListener;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UITool;

public class SearchFragment extends BaseFragment implements OnClickListener, OnItemClickListener {
    private EditText titleEditTextView;
    private ImageView imageView;
    private TextView textView;
    private LinearLayout lodinggroup;
    private XListView mListView;
    private NewsListAdapter mAdapter;
    private int page;
    private String loadStr;
    ArrayList<NewsListInfo.RowsBean> arrayList;
    private TitleBarView mBarView;

    @Override
    public int getViewLayoutId() {
        return R.layout.healthknowledge_search_frag;
    }

    public static void toFragment(FragmentActivity fragment) {

        FragmentMrg.toFragment(fragment, SearchFragment.class, null, false);
    }

    @Override
    public void onLaunch(Bundle bundle) {
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        setupView();

    }

    @Override
    public void onResume() {
        init();
        super.onResume();
    }

    private void init() {
        mListView = (XListView) findViewById(R.id.list_view_of_search);
        imageView = (ImageView) findViewById(R.id.loadingImageView_of_search);
        textView = (TextView) findViewById(R.id.tv_of_search);
        lodinggroup = (LinearLayout) findViewById(R.id.lin_group_of_search);
        mListView.setPullRefreshEnable(false);
        mListView.setPullLoadEnable(false);
        mAdapter = new NewsListAdapter(getApplicationContext(), mNewsList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setXListViewListener(new IXListViewListener() {

            @Override
            public void onRefresh() {
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
                        mListView.stopLoadMore();
                        mListView.stopRefresh();
                    }
                }, 2000);

                requestHotSpot(loadStr, false);
            }
        });
        if (arrayList != null) {
            notifySearchList(arrayList);
        } else {
            arrayList = new ArrayList<NewsListInfo.RowsBean>();
        }
        if (loadStr != null && titleEditTextView != null) {
            titleEditTextView.setText(loadStr);
        }


    }

    private void starSearch() {
        mListView.setVisibility(View.GONE);
        lodinggroup.setVisibility(View.VISIBLE);
        imageView.setImageResource(R.drawable.loading_anim);
        AnimationDrawable drawable = (AnimationDrawable) imageView.getDrawable();
        textView.setText("正在努力搜索中...");
        textView.setTextColor(getResources().getColor(R.color.text_color_3));
        drawable.start();
    }

    private void notFindResoult() {
        mListView.setVisibility(View.GONE);
        lodinggroup.setVisibility(View.VISIBLE);
        imageView.setImageResource(R.drawable.task_no_data);
        textView.setTextColor(getResources().getColor(R.color.text_color_1));
        textView.setText("没有找到相关内容哦~");
    }

    private void setupView() {
        View newTitleView = mBarView.resetLayout(R.layout.health_seach_titleview);
        newTitleView.findViewById(R.id.tv_back).setOnClickListener(this);
        titleEditTextView = (EditText) newTitleView.findViewById(R.id.search_et);
        UITool.setEditWithClearButton(titleEditTextView, R.drawable.seach_clear);
        UITool.autoOpenInputMethod(mContext, titleEditTextView);
        titleEditTextView.setFocusable(true);
        titleEditTextView.setFocusableInTouchMode(true);
        titleEditTextView.requestFocus();
        titleEditTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {//搜索功能
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
// 先隐藏键盘
                    ((InputMethodManager) titleEditTextView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(
                                    getActivity()
                                            .getCurrentFocus()
                                            .getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);

                    page = 0;
                    UITool.closeInputMethodManager(getActivity());
                    checkSearchStr();
                    return true;
                }
                return false;
            }
        });


    }

    private void notifySearchList(ArrayList<NewsListInfo.RowsBean> arrayList) {
        mListView.setVisibility(View.VISIBLE);
        lodinggroup.setVisibility(View.GONE);
        mAdapter.addData(arrayList,false);
        mAdapter.notifyDataSetChanged();
    }

    public static BaseFragment newInstance() {
        SearchFragment frag = new SearchFragment();
        return frag;
    }

    private void checkSearchStr() {
        loadStr = titleEditTextView.getText().toString().trim();
        if ("".equals(loadStr) || loadStr == null) {
            showToast("请输入您要搜索的关键字");
            return;
        }
        requestHotSpot(loadStr, true);
    }

    @Override
    public void onClick(View v) {
        try {
            FragmentMrg.toBack(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBarView.setTitle("");
    }

    private List<NewsListInfo.RowsBean> mNewsList;//列表内容

    private void requestHotSpot(final String str, boolean isShow) {
        if (isShow) {
            starSearch();
        }
        ObjectLoader<NewsListInfo> loader = new ObjectLoader<NewsListInfo>();
        loader.putPostValue("title", str);
        loader.putPostValue("page", ++page + "");
        loader.putPostValue("rows", 20 + "");
        loader.loadBodyObject(NewsListInfo.class, ConfigUrlMrg.LOAD_HOT_SPOT, loader.new CallBack() {
            @Override
            public void onBodyObjectSuccess(boolean isFromCache, NewsListInfo obj) {
                super.onBodyObjectSuccess(isFromCache, obj);
                if (obj != null) {
                    if (obj != null) {
                        mNewsList = obj.rows;
                    }
                    if (mNewsList.size() <= 0) {
                        notFindResoult();
                    }else {
                        init();
                    }
                }
            }

            @Override
            public boolean onFail(int code) {
                notFindResoult();
                return super.onFail(code);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        final NewsListInfo.RowsBean item = (NewsListInfo.RowsBean) mAdapter.getItem(arg2 - 1);
        String url = item.url;
        //跳转WEB页面
        ThreadHandler.postUiThread(new Runnable() {
            @Override
            public void run() {
                item.setClickNum(item.clickNum + 1);
                mAdapter.notifyDataSetChanged();
            }
        },500);
        BookWebActivity.toWebActivity(getActivity(), item.hotType, null, item.hot_spot_title, url, item.hot_spot_id);

    }
}
