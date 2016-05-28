package com.comvee.tnb.ui.ask;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.FinalDb;
import com.comvee.frame.BaseFragmentActivity;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.adapter.AskListAdapter;
import com.comvee.tnb.adapter.DocListAdapter;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.model.AskIndexInfo;
import com.comvee.tnb.model.AskLoopModel;
import com.comvee.tnb.model.AskServerInfo;
import com.comvee.tnb.network.ObjectLoader;
import com.comvee.tnb.ui.book.BookIndexRootFragment;
import com.comvee.tnb.ui.book.BookWebActivity;
import com.comvee.tnb.ui.more.WebFragment;
import com.comvee.tnb.ui.privatedoctor.DoctorServerList;
import com.comvee.tnb.ui.record.diet.AutoLoopViewPager;
import com.comvee.tnb.ui.record.diet.AutoLoopViewPager.AutoLoopViewPagerAdapter;
import com.comvee.tnb.view.IndexBottomView;
import com.comvee.tnb.widget.MyListView;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.ImageLoaderUtil;

import java.util.ArrayList;

/**
 * 医生模块首页
 * 流程 先取本地数据，同时后台去请求接口，返回后刷新界面
 *
 * @author Administrator
 */
public class AskIndexFragment extends BaseFragment implements OnClickListener, OnItemClickListener {

    public static String ASK_INDEX_ACTION = "com.comvee.tnb.ASK_INDEX_ACTION";
    private AutoLoopViewPager ask_viewpage;
    private ArrayList<AskLoopModel> arrayList = new ArrayList<AskLoopModel>();
    private AutoLoopViewPagerAdapter<AskLoopModel> autoLoopViewPagerAdapter;
    private View group_my_doc, group_recommend;
    private MyListView lv_my_doc, lv_recommend;
    // private RecommendDocReceiver docReceiver;
    private FinalDb finalDb;
    // public static String RECOMMEND_DOC = "com.comvee.tnb.RECOMMEND_DOC";
    private AskListAdapter myDocAdapter;

    private DocListAdapter docListAdapter;
    private TitleBarView mBarView;
    // private AskIndexLoader indexLoader;
    private View loader_more_doc;
    private View progress;
    private String mWebUrl;
    private IndexBottomView bottomView;

    public static void toFragment(FragmentActivity act, boolean anim) {
        FragmentMrg.popSingleFragment(act, AskIndexFragment.class, null, anim);
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.ask_index_fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onLaunch(Bundle dataBundle) {
        super.onLaunch(dataBundle);

        bottomView = (IndexBottomView) findViewById(R.id.layout_frame_bottom);
        bottomView.bindFragment(this);
        bottomView.selectDoc();

        ConfigParams.ASKLIST_IS_REFRESH = true;
        init();
        // initLoader();
        initLoopViewPage();
//		mBarView.setTitle(getString(R.string.ask_index_title));
//		if (askServerInfos.size() == 0) {
//			getLocationMsg(false);
//		}
//		notifyListView();

    }

    public void updateBottomView() {
        if (bottomView != null)
            bottomView.update();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (TextUtils.isEmpty(mWebUrl))
            showProgressDialog(getString(R.string.msg_loading));
        ObjectLoader<AskIndexInfo> loader = new ObjectLoader<AskIndexInfo>();
        loader.loadBodyObject(AskIndexInfo.class, ConfigUrlMrg.ASK_NEW_SERVER_LIST, loader.new CallBack() {
            @Override
            public void onBodyObjectSuccess(boolean isFromCache, AskIndexInfo obj) {
                super.onBodyObjectSuccess(isFromCache, obj);
                cancelProgressDialog();
                if (obj == null) {
                    return;
                }
                mWebUrl = obj.qusrepository;
                //我的医生
                group_my_doc.setVisibility((obj.memMsg != null && !obj.memMsg.isEmpty()) ? View.VISIBLE : View.GONE);
                //为您建议的医生
                group_recommend.setVisibility((obj.recommendDoctors != null && !obj.recommendDoctors.isEmpty()) ? View.VISIBLE : View.GONE);

                ask_viewpage.setVisibility((obj.banner != null && !obj.banner.isEmpty()) ? View.VISIBLE : View.GONE);

                myDocAdapter.setDatas(obj.memMsg);
                docListAdapter.setDatas(obj.recommendDoctors);

                docListAdapter.notifyDataSetChanged();
                myDocAdapter.notifyDataSetChanged();
//                if (model.askLoopModels != null && model.askLoopModels.size() > 0) {
//                    arrayList.clear();
//                    arrayList.addAll(model.askLoopModels);
//                    notifyAutoLoop();
//                }
//                progress.setVisibility(View.GONE);
//                loader_more_doc.setOnClickListener(AskIndexFragment.this);
//                if (model.curPage == model.totalPage) {
//                    loader_more_doc.setVisibility(View.GONE);
//                } else {
//                    loader_more_doc.setVisibility(View.VISIBLE);
//
//                }

            }

            @Override
            public boolean onFail(int code) {
                cancelProgressDialog();
                return super.onFail(code);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ConfigParams.ASKLIST_IS_REFRESH = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void init() {
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setTitle("医生");
        findViewById(R.id.select_doc).setOnClickListener(this);
        findViewById(R.id.problem_db).setOnClickListener(this);
        if (finalDb == null) {
            finalDb = FinalDb.create(getApplicationContext(), ConfigParams.DB_NAME);
        }
        group_my_doc = findViewById(R.id.group_my_doc);
        group_recommend = findViewById(R.id.group_recommend);
        lv_my_doc = (MyListView) findViewById(R.id.lv_my_doc);
        lv_recommend = (MyListView) findViewById(R.id.lv_recommend);
        loader_more_doc = findViewById(R.id.loader_more_doc);
        progress = findViewById(R.id.progressbar);
        lv_my_doc.setOnItemClickListener(this);
        lv_recommend.setOnItemClickListener(this);
        loader_more_doc.setOnClickListener(this);
        myDocAdapter = new AskListAdapter();
        docListAdapter = new DocListAdapter();
        lv_my_doc.setAdapter(myDocAdapter);
        lv_recommend.setAdapter(docListAdapter);

        // showProgressDialog(getText(R.string.loading).toString());

    }

    @Override
    public boolean onBackPress() {
        ((BaseFragmentActivity) getActivity()).tryExit();
        return true;
    }

    private void notifyAutoLoop() {
        if (arrayList.size() == 1) {
            ask_viewpage.enableLoop(false);
            ask_viewpage.setScroll(false);
        } else {
            ask_viewpage.setScroll(true);
        }
        autoLoopViewPagerAdapter.notifyDataSetChanged();
    }

    private void initLoopViewPage() {
        ask_viewpage = (AutoLoopViewPager) findViewById(R.id.ask_viewpage);
        autoLoopViewPagerAdapter = new AutoLoopViewPagerAdapter<AskLoopModel>(ask_viewpage, getApplicationContext(),
                R.layout.diet_loop_image_viewpager, arrayList) {

            @Override
            public void doSth(View view, final AskLoopModel t) {
                ImageView img = (ImageView) view.findViewById(R.id.iv);
                TextView tv = (TextView) view.findViewById(R.id.tv);
                img.setOnClickListener(new OnClickListener() {

                    @SuppressWarnings("deprecation")
                    @Override
                    public void onClick(View arg0) {
                        if (t.bannerType == 0) {
                            toFragment(WebFragment.newInstance(t.title, t.turnnurl, true), false);
                        } else if (t.bannerType == 22) {
                            toFragment(WebFragment.newInstance(t.title, t.turnnurl, true), true);
                        } else {
                            toFragment(BookIndexRootFragment.newInstance(false, t.turnnurl, t.title), true, true);
                        }
                    }
                });
                ImageLoaderUtil.getInstance(getApplicationContext()).displayImage(t.imgUrl, img, ImageLoaderUtil.default_options);
                tv.setText("");
            }
        };
        ask_viewpage.setAdapter(autoLoopViewPagerAdapter);
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.select_doc:
                DocListFragment.toFragment(getActivity(), DocListFragment.WHERE_PRIVATE_DOCTOR, null);
                break;
            case R.id.problem_db:
                String url = mWebUrl;
                if (!TextUtils.isEmpty(url)) {
                    BookWebActivity.toWebActivity(getActivity(), -1, null, "问题库", url, null);
                }
                break;
            case R.id.loader_more_doc:
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {

        Object obj = arg0.getAdapter().getItem(position);

        if (obj instanceof AskIndexInfo.MemMsg) {//我的医生
            AskIndexInfo.MemMsg temp = (AskIndexInfo.MemMsg) obj;
            AskServerInfo info = new AskServerInfo();
            info.setDoctorId(temp.doctorId);
            info.setDoctorName(temp.doctorName);
            info.setInsertDt(temp.insertDt);
            info.setDataStr(temp.userMsg);
            temp.count = 0;
            AskQuestionFragment.toFragment(getActivity(), info);
            ConfigParams.setMsgDocCount(getContext(), 0);
            docListAdapter.notifyDataSetChanged();
            myDocAdapter.notifyDataSetChanged();

        } else if (obj instanceof AskIndexInfo.RecommendDoctor) {//为您建议的医生
            AskIndexInfo.RecommendDoctor info = (AskIndexInfo.RecommendDoctor) obj;
            DoctorServerList.toFragment(getActivity(), info.businessDoctorId, false);
        }
    }


}
