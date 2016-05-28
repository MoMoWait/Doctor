package com.comvee.tnb.guides;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.widget.TitleBarView;

import org.json.JSONObject;

public class GuideHealthBrowseFrag extends BaseFragment implements OnClickListener {
    boolean isShowBottom = true;
    private IndexTaskInfo mIndexInfo;
    private IndexTaskInfo mLinkInfo;
    private GuideBrowseInfo mContentInfo;
    //private boolean isFromWhere;
    private WebView mWebView;
    //private Handler mHandler;
    //private View layoutBottom;
    private TitleBarView mBarView;

    public GuideHealthBrowseFrag() {
    }

	/*public void setFromIndex(boolean isFromIndex) {
        this.isFromWhere = isFromIndex;
	}*/

    public static GuideHealthBrowseFrag newInstance(IndexTaskInfo indexInfo, GuideBrowseInfo mContent, boolean isFromIndex) {
        GuideHealthBrowseFrag frag = new GuideHealthBrowseFrag();
        frag.setIndexInfo(indexInfo);
        frag.setTaskContentInfo(mContent);
        //frag.setFromIndex(isFromIndex);
        return frag;
    }

    public void setTaskContentInfo(GuideBrowseInfo mInfo) {
        this.mContentInfo = mInfo;
    }

    public void setIndexInfo(IndexTaskInfo mIndexInfo) {
        this.mIndexInfo = mIndexInfo;
    }

	/*// 判断type是否为9；
    private boolean ifTypeSportLink() {
		try {
			return mIndexInfo.getType() != IndexTaskInfo.JUMP_SPORTS_VALUE_HTML;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}*/

    @Override
    public int getViewLayoutId() {
        // TODO Auto-generated method stub
        return R.layout.fragment_guides_health_browse;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onLaunch(Bundle bundle) {
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        if (mContentInfo == null) {
            return;
        }
        // getTitleBar().setLeftButton(0, R.drawable.top_menu_back, this);
        TextView pre = (TextView) findViewById(R.id.btn_pre);
        TextView next = (TextView) findViewById(R.id.btn_next);
        next.setOnClickListener(this);
        pre.setOnClickListener(this);
        next.setTag(false);
        // 添加一个type==9时的判断

        if (mContentInfo.getSeq() == 1) {
            pre.setVisibility(View.GONE);
        }
        switch (mIndexInfo.getType()) {
            case IndexTaskInfo.JUMP_NEW_SPORTS_VALUE_1:
            case IndexTaskInfo.JUMP_NEW_SPORTS_VALUE:
            case IndexTaskInfo.JUMP_SPORTS_VALUE_HTML:
                next.setText(R.string.btn_next_day);
                pre.setText(R.string.btn_pre_day);
                break;
            default:
                break;
        }
        if (mContentInfo.getMsgtype() == 1 && mContentInfo.getSeq() == mContentInfo.getMsgseq()) {
            next.setEnabled(false);
        }
        if (mContentInfo.getSeq() == mContentInfo.getTotal()) {
            next.setText(R.string.txt_isee);
            next.setEnabled(true);
        }

        mWebView = (WebView) findViewById(R.id.v_webview);
        WebSettings settings = mWebView.getSettings();
        settings.setAppCacheEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setDefaultTextEncodingName("utf-8");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mWebView.setVisibility(View.VISIBLE);
            }
        });

        if (mContentInfo.getUrl() != null) {
            mWebView.loadUrl(mContentInfo.getUrl());
        } else {
            // Log.e("tnb", "链接为空");
        }

        TextView tvTask = (TextView) findViewById(R.id.tv_task);
        if (mContentInfo.getLinktype() == 1) {
            try {
                mLinkInfo = DataParser.createIndexTaskInfo(new JSONObject(mContentInfo.getLinktask()));
                if (mLinkInfo != null) {
                    tvTask.setText(mLinkInfo.getTitle());
                    tvTask.setOnClickListener(this);
                    tvTask.setVisibility(View.VISIBLE);

                    if (mLinkInfo.getStatus() == 1) {
                        tvTask.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.family_choose, 0);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            tvTask.setVisibility(View.GONE);
        }

        if (mIndexInfo.getTotal() == 1) {
            // findViewById(R.id.layout_bottom).setVisibility(View.GONE);
        } else {
			/*layoutBottom = findViewById(R.id.layout_bottom);
			mHandler = new Handler() {
				public void handleMessage(android.os.Message msg) {
					hideBottomLayout();
				};
			};*/
            // mWebView.setOnTouchListener(new OnTouchListener() {
            // @Override
            // public boolean onTouch(View v, MotionEvent event) {
            // if (!isShowBottom)
            // showBottomLayout();
            // mHandler.removeMessages(0);
            // mHandler.sendEmptyMessageDelayed(0, 1500);
            // return false;
            // }
            // });

        }
        if (mIndexInfo.getTitle() != null && mIndexInfo != null) {
            mBarView.setTitle(mIndexInfo.getTitle());
        }
        next.setTag(true);
    }

	/*private void showBottomLayout() {
		isShowBottom = true;
		layoutBottom.setVisibility(View.VISIBLE);
		layoutBottom.setEnabled(true);
		layoutBottom.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fragment_up_in));
	}

	private void hideBottomLayout() {
		isShowBottom = false;
		Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_down_out);
		anim.setAnimationListener(mAnimListener);
		layoutBottom.startAnimation(anim);
	}*/

/*	private AnimationListener mAnimListener = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			layoutBottom.setVisibility(View.GONE);
			layoutBottom.setEnabled(false);
			animation = null;
		}
	};*/

    @Override
    public boolean onBackPress() {
        if (!mIndexInfo.isCanBackIndex()) {
            mIndexInfo.setStatus(1);
            FragmentMrg.toBack(getActivity());
        } else {
            IndexFrag.toFragment(getActivity(),true);
        }
        return true;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_pre) {
            if (mContentInfo.getSeq() > 0) {
                mIndexInfo.setSeq(mContentInfo.getSeq());
                GuideMrg.getInstance().jumpPreBrowseTask(this, mIndexInfo);
            }
        } else if (v.getId() == R.id.btn_next) {
            boolean b = (Boolean) v.getTag();
            if (b) {

                if (mContentInfo.getSeq() == mContentInfo.getTotal()) {
                    if (mIndexInfo.getType() == IndexTaskInfo.JUMP_NEW_SPORTS_VALUE) {
                        mIndexInfo.setSeq(mContentInfo.getSeq());
                        GuideMrg.getInstance().jumpNextBrowseTask(this, mIndexInfo);
                    } else if (mIndexInfo.getType() == IndexTaskInfo.JUMP_SPORTS_VALUE_HTML) {

                        GuideMrg.getInstance().jumpToIndexForSport(this, mIndexInfo);
                    } else {
                        if (!mIndexInfo.isCanBackIndex()) {
                            mIndexInfo.setStatus(1);
                            FragmentMrg.toBack(getActivity());
                        } else {
                            IndexFrag.toFragment(getActivity(),true);
                        }
                    }
                } else if (mContentInfo.getSeq() < mContentInfo.getTotal()) {
                    mIndexInfo.setSeq(mContentInfo.getSeq());
                    GuideMrg.getInstance().jumpNextBrowseTask(this, mIndexInfo);
                }

            }
        } else if (v.getId() == R.id.tv_task) {
            if (mContentInfo.getLinktype() == 1 && mLinkInfo != null) {
                if (mLinkInfo != null) {
                    String msg = null;
                    msg = mIndexInfo.getType() + "/" + mIndexInfo.getTaskCode() + "/" + mIndexInfo.getSeq() + "/" + mIndexInfo.getStatus() + "/"
                            + mIndexInfo.getTotal();
                    ConfigParams.setGuideReadMsg(getApplicationContext(), msg);
                    GuideMrg.getInstance().jumpGuide(this, mLinkInfo);

                } else {
                    showToast(getResources().getString(R.string.error));
                }// 跳转问题链接
                // TaskJumpMrg.getInstance(getActivity()).setTempInfo(mIndexInfo);
                // TaskJumpMrg.getInstance(getActivity()).jumpIndexTask(mLinkTaskInfo,
                // false);
            }
        }
        // else if (v.getId() == TitleBarView.ID_LEFT_BUTTON) {
        // if (!isFromWhere) {
        // FragmentMrg.toBack(this);
        // }
        // }
    }

}
