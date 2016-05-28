package com.comvee.tnb.radio;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.adapter.ComveePageAdapter;
import com.comvee.tnb.adapter.RadioMainListAdapter;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.ShadeActivityDialog;
import com.comvee.tnb.model.RadioAlbumItem;
import com.comvee.tnb.model.RadioGroup;
import com.comvee.tnb.model.RadioMainInfo;
import com.comvee.tnb.model.RadioTurns;
import com.comvee.tnb.network.ObjectLoader;
import com.comvee.tnb.network.RadioMainLoader;
import com.comvee.tnb.network.RadioMainLoader.RadioMainCallBack;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.ui.more.CollectionListFragment;
import com.comvee.tnb.ui.more.WebFragment;
import com.comvee.tnb.ui.more.WebNewFrag;
import com.comvee.tnb.view.RadioPlayView;
import com.comvee.tnb.view.RadioPlayView.PlayerViewListener;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tnb.widget.ViewPagerIndicator;
import com.comvee.tool.AppUtil;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.tool.Log;
import com.comvee.tool.UITool;
import com.comvee.tool.UserMrg;
import com.comvee.util.BundleHelper;
import com.comvee.util.CacheUtil;
import com.comvee.util.Util;

/**
 * 电台
 */
public class RadioMainFrag extends BaseFragment implements OnClickListener, OnRefreshListener,
        ShadeActivityDialog.ShadeClickListener {
    private ViewPagerIndicator mIndicator;
    private ViewPager viewPager;
    private ListView mListView;
    private RadioMainListAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MyPageAdapter adapter;
    private int iIndicator;
    private View view;
    private String mTurnMarketUrl;//商城地址URL
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            try {
                if (viewPager != null) {
                    iIndicator++;
                    viewPager.setCurrentItem(iIndicator % adapter.getCount(), true);
                }
                mHandler.sendEmptyMessageDelayed(0, 3000);
            } catch (Exception e) {
                Log.e("MyPageAdapter is null");
            }
        }

        ;
    };

    @Override
    public int getViewLayoutId() {
        return R.layout.radio_main_frag;
    }

    public static void toFragment(FragmentActivity fragment, boolean isSliding) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isSliding", isSliding);
        bundle.putInt("from_where", 3);
        FragmentMrg.toFragment(fragment, RadioMainFrag.class, bundle, true);
    }

    private void initTitleBarView() {
        TitleBarView mBarView = (TitleBarView) findViewById(R.id.layout_top);
        mBarView.setLeftDefault(this);
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.titlebar_follow_record, null);
        TextView left = (TextView) view.findViewById(R.id.tab_left);
        TextView right = (TextView) view.findViewById(R.id.tab_right);
        left.setBackgroundResource(R.drawable.jiankangzixun_03);
        right.setBackgroundResource(R.drawable.jiankangzixun_04);
        left.setText("糖豆电台");
        mBarView.setVisibility(View.VISIBLE);
        //mBarView.setRightButton(R.drawable.radio_main_more, this);
        mBarView.setLeftDefault(this);
        right.setText("糖豆商城");
        left.setTextColor(Color.WHITE);
        right.setTextColor(getResources().getColor(R.color.theme_color_green));
        right.setOnClickListener(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mBarView.addView(view, params);
        mBarView.setTitle("");
    }

    @Override
    public void onLaunch(Bundle dataBundle) {
        super.onLaunch(dataBundle);
        setLyoutImg();
        initTitleBarView();
        findViewById(R.id.btn_download).setOnClickListener(this);
        findViewById(R.id.btn_collect).setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.list_group);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.layout_swipe);
        swipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(this);
        view = View.inflate(getApplicationContext(), R.layout.radio_main_top, null);
        mIndicator = (ViewPagerIndicator) view.findViewById(R.id.layout_indicator);
        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(getApplicationContext(), new AccelerateInterpolator());
            field.set(viewPager, scroller);
            scroller.setmDuration(600);
        } catch (Exception e) {
        }
        viewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                mIndicator.selectTo(arg0);
                iIndicator = arg0;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        // =====================解决手势冲突=====================//
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        stopBanner();

                        swipeRefreshLayout.setEnabled(false);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        startBanner();
                        swipeRefreshLayout.setEnabled(true);
                        break;
                }
                return false;
            }
        });
        // ====================================================//

        if (adapter != null) {
            if (mListView.getHeaderViewsCount() == 0) {
                mListView.addHeaderView(view);
            }
            viewPager.setAdapter(adapter);
            mIndicator.updateIndicator(adapter.getCount());
            mIndicator.selectTo(0);
        }

        if (mAdapter == null) {
            mAdapter = new RadioMainListAdapter();
            mAdapter.setOnClickListener(mListener);
            mListView.setAdapter(mAdapter);
            // 判断是否有缓存，如果有缓存不需要loading
            if (!CacheUtil.getInstance().hashCache("RadioGroup"))
                showProgressDialog("请稍等...");
            requestData(true);
        } else {
            mListView.setAdapter(mAdapter);
        }
        RadioPlayView player = (RadioPlayView) findViewById(R.id.layout_play_bar);
        player.setListener(new PlayerViewListener() {
            @Override
            public void onToCurAlbum() {
                RadioPlayFrag.toFragment(getActivity(), RadioPlayerMrg.getInstance().getAlbum(), RadioPlayerMrg.getInstance().getCurrent());
            }

            @Override
            public void onStart(RadioAlbumItem item) {
            }

            @Override
            public void onPause(RadioAlbumItem item) {
            }
        });

    }

    /**
     * 遮罩页
     */
    private void setLyoutImg() {
        if (Util.checkFirst(getApplicationContext(), "radio_v5.0.4")) {
            if ((UITool.getDisplayHeight(getActivity()) / UITool.getDisplayWidth(getActivity())) == (15f / 9f)) {
                AppUtil.initLuanchWindow(getActivity(), R.drawable.radio_main_img, this);
            } else {
                AppUtil.initLuanchWindow(getActivity(), R.drawable.radio_main_img_2, this);
            }
        }
    }

    private void startBanner() {
        mHandler.removeMessages(0);
        mHandler.sendEmptyMessageDelayed(0, 3000);
    }

    private void stopBanner() {
        mHandler.removeMessages(0);
    }

    private void requestData(boolean needCache) {
        ObjectLoader<RadioMainInfo> loader = new ObjectLoader<RadioMainInfo>();
        loader.loadObjectByBodyobj(RadioMainInfo.class, ConfigUrlMrg.RAIOD_MAIN_LIST, loader.new CallBack() {
            @Override
            public void onBodyObjectSuccess(boolean isFromCache, RadioMainInfo obj) {
                super.onBodyObjectSuccess(isFromCache, obj);
                cancelProgressDialog();

                if (!isFromCache) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                try {
                    if (obj == null) {
                        return;
                    }
                    RadioUtil.setReg(obj.isReg);
                    mTurnMarketUrl = obj.turn_market_url;
                    mAdapter.setListItems(obj.newProgramList);
                    mAdapter.notifyDataSetChanged();
                    if (obj.turnsList != null && !obj.turnsList.isEmpty()) {
                        if (mListView.getHeaderViewsCount() == 0) {
                            mListView.addHeaderView(view);
                            mListView.setAdapter(mAdapter);
                        }
                        adapter = new MyPageAdapter(obj.turnsList);
                        mIndicator.updateIndicator(adapter.getCount());
                        viewPager.setAdapter(adapter);
                        mIndicator.selectTo(0);
                    } else {
                        // 隐藏headview
                        mListView.removeHeaderView(view);
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public boolean onFail(int code) {
                cancelProgressDialog();
                return super.onFail(code);
            }
        });
//		RadioMainLoader loader = new RadioMainLoader();
//		loader.load(needCache, new RadioMainCallBack() {
//			@Override
//			public void onCallBack(boolean cache, ArrayList<RadioGroup> album, ArrayList<RadioTurns> turns) {
//				cancelProgressDialog();
//				if (!cache) {
//					swipeRefreshLayout.setRefreshing(false);
//				}
//				try {
//					mAdapter.setListItems(album);
//					mAdapter.notifyDataSetChanged();
//					if (turns!=null&&!turns.isEmpty() ) {
//						if (mListView.getHeaderViewsCount() == 0) {
//							mListView.addHeaderView(view);
//							mListView.setAdapter(mAdapter);
//						}
//						adapter = new MyPageAdapter(turns);
//						mIndicator.updateIndicator(adapter.getCount());
//						viewPager.setAdapter(adapter);
//						mIndicator.selectTo(0);
//						// 显示headview
//						// view.setVisibility(View.VISIBLE);
//						// view.setPadding(0, 0, 0, 0);
//					} else {
//						// 隐藏headview
//						mListView.removeHeaderView(view);
//						mAdapter.notifyDataSetChanged();
//						// view.setVisibility(View.GONE);
//						// if (-view.getHeight() != 0) {
//						// view.setPadding(0, -view.getHeight(), 0, 0);
//						// }
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
    }

    class MyPageAdapter extends ComveePageAdapter {
        public ArrayList<RadioTurns> list;

        public MyPageAdapter(ArrayList<RadioTurns> list) {
            this.list = list;
        }

        @Override
        public View getView(int position) {
            ImageView view = new ImageView(getApplicationContext());
            view.setScaleType(ScaleType.CENTER_CROP);
            view.setOnClickListener(clickListener);
            view.setTag(list.get(position));
            ImageLoaderUtil.getInstance(getApplicationContext()).displayImage(list.get(position).proclamationLogo, view, ImageLoaderUtil.null_defult);
            return view;
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        private OnClickListener clickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioTurns turn = (RadioTurns) v.getTag();
                // 1音频节目
                // 2视频节目
                // 3直播
                // 4html
                // 5专辑
                switch (turn.proclamationType) {
                    case 1:// 节目
                        RadioGroup.RadioAlbum items = RadioUtil.getRadioAlbumByTurns(turn);
                        RadioAlbumItem item = RadioUtil.getProgramByRadioAlbum(items);
                        ArrayList<RadioAlbumItem> list = new ArrayList<RadioAlbumItem>();
                        list.add(item);
                        RadioPlayerMrg.getInstance().setDataSource(items, list,false);
                        RadioPlayerMrg.getInstance().play(0);
                        RadioPlayFrag.toFragment(getActivity(), items, item);
                        break;
                    case 3:// 直播
                        RadioUtil.jumpFrag(getActivity(), RadioUtil.getRadioAlbumByTurns(turn));
                        break;
                    case 4:// web
                        WebFragment.toFragment(getActivity(), turn.proclamationTitle, turn.proclamationUrl);
                        break;
                    case 5:// 专辑
                        RadioGroup.RadioAlbum album = RadioUtil.getRadioAlbumByTurns(turn);
                        toFragment(ProgrammeListFrag.class, BundleHelper.getBundleByObject(album), true);
                        break;

                    default:
                        break;
                }
            }
        };
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.iv_photo1:
                toFragment(ProgrammeListFrag.class, null, true);
                break;
            case TitleBarView.ID_RIGHT_BUTTON:
                showMoreMenu();
                break;
            case R.id.layout_cover:
                hideMoreMenu();
                break;
            case R.id.btn_download:
                toFragment(DownLoadLocalFragment.class, null, true);
                break;
            case R.id.btn_collect:
                RadioCollectFrag.toFragment(getActivity(), 1, true);
                break;
            case R.id.tab_right:
                if (!TextUtils.isEmpty(mTurnMarketUrl.toString())) {
                    mTurnMarketUrl += String.format("?sessionID=%s&sessionMemberID=%s", UserMrg.getSessionId(getContext()), UserMrg.getMemberSessionId(getContext()));
                    WebNewFrag.toFragment(getActivity(), "糖豆商城", mTurnMarketUrl);
                }
                break;
            default:
                break;
        }
    }

    private RadioGroup.RadioAlbum tempAlbum;
    private OnClickListener mListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_more:
                    RadioGroup group = (RadioGroup) v.getTag();
                    toFragment(RadioAblumListFrag.class, BundleHelper.getBundleByObject(group), true);
                    break;
                case R.id.iv_photo1:
                case R.id.iv_photo2:
                case R.id.iv_photo3:
                case R.id.tv_label1:
                case R.id.tv_label2:
                case R.id.tv_label3:
                case R.id.tv_title1:
                case R.id.tv_title2:
                case R.id.tv_title3:
                    RadioGroup.RadioAlbum items = (RadioGroup.RadioAlbum) v.getTag();
                    tempAlbum = items;
                    RadioUtil.jumpFrag(getActivity(), items);
                    break;
                default:
                    break;
            }

        }
    };

    public void showMoreMenu() {
        findViewById(R.id.layout_more).setVisibility(View.VISIBLE);
        findViewById(R.id.layout_cover).setVisibility(View.VISIBLE);
        findViewById(R.id.layout_cover).setOnClickListener(this);
        findViewById(R.id.layout_cover).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
    }

    public void hideMoreMenu() {
        findViewById(R.id.layout_more).setVisibility(View.GONE);
        findViewById(R.id.layout_cover).setVisibility(View.GONE);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        requestData(false);
    }

    @Override
    public void onFragmentResult(Bundle data) {
        super.onFragmentResult(data);
        if (data != null && tempAlbum != null) {
            try {
                tempAlbum.isCollect = data.getInt("collect");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRoot = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        stopBanner();
    }

    @Override
    public void onResume() {
        super.onResume();
        startBanner();
    }

    @Override
    public void onShadeClick() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onBackPress() {
        FragmentMrg.popBackToFragment(getActivity(), IndexFrag.class, null, false);
        return true;
    }
}
