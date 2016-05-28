package com.comvee.tnb.ui.book;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.Toast;

import cn.sharesdk.framework.ShareSDK;

import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.CustomProgressNewDialog;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.model.CollectInfo;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UserMrg;
import com.comvee.util.UITool;

/**
 * 资讯详细页
 *
 * @author friendlove
 */
@SuppressLint("ValidFragment")
public class BookWebActivity extends Activity implements OnClickListener, OnItemClickListener, OnHttpListener {
	private CollectInfo collectinfo;
	private ObservableWebView mWebView;
	private String url;
	private String title;
	private String mTaskid;
	private ProgressBar mBar;
	private int mFromWhere;
	public static final int TASK = 12;// 任务
	public static final int COLLECT = 11;// 收藏
	public static final int BOOKS = 1;// 知识
	public static final int MESSAGE = 2;// 资讯
	public static final int COOKBOOK = 3;// 食谱
	public static final int FOOD = 4;// 食物库
	public static final int TYPE_COMMEN = 1;  //常识Tab
	public static final int TYPE_OTHER = 2; //其他Tab
	private TitleBarView titleBarView;
	private String bookID;
	/**
	 * 网页点击跳转 网页调用软件代码
	 *
	 * @param position
	 */
	private Handler h;
	private String contentId;
	private WebFinishReceiver finishReceiver;
	public static String INFINSH_ACTION = "com.comvee.finish.bookweb";
	private final int hideBarViewDistance = 200;
	private void getPutExtraMsg() {
		Bundle extras = getIntent().getExtras();
		int type = extras.getInt("fromwhere");
		this.mFromWhere = type;
		switch (type) {
			case COLLECT:
				this.collectinfo = (CollectInfo) extras.getSerializable("collectinfo");
				break;
			case MESSAGE:
			case BOOKS:
			case TASK:
			case COOKBOOK:
			case FOOD:
			default:
				this.bookID = extras.getString("id");
				this.url = extras.getString("url");
				this.title = extras.getString("title");
				break;
		}
	}

	public void setTaskId(String id) {
		mTaskid = id;
	}
	class WebFinishReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			BookWebActivity.this.finish();
		}
	}
	private void register() {
		finishReceiver = new WebFinishReceiver();
		IntentFilter filter = new IntentFilter(INFINSH_ACTION);
		registerReceiver(finishReceiver, filter);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		UITool.setImmersive(getWindow(), true);
//		UITool.setMiuiStatusBarDarkMode(getWindow(),true);
//		UITool.setMeizuStatusBarDarkIcon(getWindow(),true);
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		setContentView(R.layout.fragment_book_web);
		register();
		titleBarView = (TitleBarView) findViewById(R.id.layout_top);
		h = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case 1:
						Bundle b = msg.getData();
						String value = b.getString("key");
						String title = b.getString("title");
						String url = b.getString("url");
						String imgurl = b.getString("imgurl");
						bookShare.setCollectID(value);
						BookWebActivity.this.title = title;
						bookShare.setTitle(title);
						bookShare.setShareImgurl(imgurl);
						bookShare.requestIsCollse();
						break;
					case 2:
						Bundle b1 = msg.getData();
						String value1 = b1.getString("key");
						String title1 = b1.getString("title");
						String url1 = b1.getString("url");
						String imgurl1 = b1.getString("imgurl");
						String type = b1.getString("type");
						bookShare.setCollectID(value1);
						BookWebActivity.this.title = title1;
						bookShare.setTitle(title1);
						bookShare.setShareImgurl(imgurl1);
						bookShare.requestIsCollse();
						// if (appTitle != null && !"".equals(appTitle)) {
						// setTitle(appTitle);
						// }
//						bookShare.setMsgType(type == null ? COOKBOOK : Integer.parseInt(type));
						if ("3".equals(type)) {
							bookShare.setTitle("糖友食谱：" + title1);
						}
						break;
					case 3:
						String titles = (String) msg.obj;
						if (titles != null && !"".equals(titles)) {
							titleBarView.setTitle(titles);
						}
						break;
					default:
						break;
				}

			}
		};
		getPutExtraMsg();
		ShareSDK.initSDK(getApplicationContext());
		init();
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	@Override
	public void onStart() {
		super.onStart();
	}
	@SuppressLint("SetJavaScriptEnabled")
	private void init() {
		if (TextUtils.isEmpty(mTaskid)) {
			findViewById(R.id.layout_bottom).setVisibility(View.GONE);
		} else {
			findViewById(R.id.layout_bottom).setVisibility(View.VISIBLE);
			final View btnComplete = findViewById(R.id.btn_complete);
			btnComplete.setVisibility(View.VISIBLE);
			btnComplete.setOnClickListener(this);
		}
		mBar = (ProgressBar) findViewById(R.id.pro_loading);
		mBar.setProgress(0);
		mWebView = (ObservableWebView) findViewById(R.id.web);
		mWebView.setVisibility(View.VISIBLE);
		WebSettings setings = mWebView.getSettings();
		setings.setAppCacheEnabled(true);
		setings.setLoadWithOverviewMode(true);
		setings.setUseWideViewPort(true);
		setings.setCacheMode(WebSettings.LOAD_DEFAULT); // 设置 缓存模式
		setings.setJavaScriptEnabled(true);// 设置可使用js
		mWebView.addJavascriptInterface(new JSClient(), "js");
		mWebView.setDrawingCacheEnabled(false);// 允许进行可视区域的截图
		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, final int newProgress) {
				super.onProgressChanged(view, newProgress);
				if (newProgress < 100) {
					mBar.setVisibility(View.VISIBLE);
				} else if (newProgress == 100) {
					mBar.setVisibility(View.GONE);
				}
				mBar.setProgress(newProgress);
			}
		});
		mWebView.getSettings().setAppCacheEnabled(true);
		mWebView.addJavascriptInterface(new HtmlHandler(), "handler");
		mWebView.setWebViewClient(new WebViewClient() {
									  @Override
									  public boolean shouldOverrideUrlLoading(WebView view, String url) {
										  mWebView.loadUrl(url);
										  return true;
									  }
								  }

		);
		inflateView();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		titleBarView.hideRightButton();
		if (finishReceiver != null) {
			unregisterReceiver(finishReceiver);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_complete:
				requestTaskComplete(mTaskid);
				break;
			case TitleBarView.ID_LEFT_BUTTON:
				onBackPressed();
				break;
			case TitleBarView.ID_RIGHT_BUTTON:
				mWebView.loadUrl("javascript:window.handler.show(document.getElementById('shareContent').innerText.substr(0,50));");
				if (mFromWhere == BOOKS) {
					mWebView.loadUrl("javascript:window.handler.getWebShareMsg(document.getElementsByTagName('h1')[0].innerHTML,document.getElementsByTagName('p')[1].innerHTML,document.getElementsByTagName('img')[0].src);");
				}
				if (bookShare != null) {
					if (!TextUtils.isEmpty(bookShare.getShareURL())) {
						bookShare.showMenu();
					}
				}
				break;
			default:
				break;
		}
	}

	/**
	 * 完成任务
	 */
	private void requestTaskComplete(String id) {
		showProDialog(getString(R.string.msg_loading));
		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.TASK_COMPLETE);
		http.setPostValueForKey("memberJobDetailId", id);
		http.setOnHttpListener(4, this);
		http.startAsynchronous();
	}

	@Override
	public void onSussece(int what, byte[] b, boolean fromCache) {
		cancelProDialog();
		switch (what) {
			case 4:
				try {
					cancelProDialog();
					ComveePacket packet = ComveePacket.fromJsonString(b);
					if (packet.getResultCode() == 0) {
						ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.TASK_MINE));
						ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.INDEX));
						ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.TASK_DETAIL));
						finish();
					} else {
						ComveeHttpErrorControl.parseError(this, packet);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
		}
	}
	/**
	 * 网页回调软件接口
	 */
	final class JSClient {
		/**
		 * web回调android
		 *
		 * @param messageID
		 * @param title
		 * @param url
		 * @param imgurl
		 */
		@JavascriptInterface
		public void showMessage(String messageID, String title, String url, String imgurl) {
			Message msg = new Message();
			msg.what = 1;
			Bundle b = new Bundle();
			b.putString("key", messageID);
			b.putString("title", title);
			b.putString("url", url);
			b.putString("imgurl", imgurl);
			msg.setData(b);
			h.sendMessage(msg);

		}
		@JavascriptInterface
		public void showMessageNew(String messageID, String title, String url, String imgurl, String type) {
			Message msg = new Message();
			msg.what = 2;
			Bundle b = new Bundle();
			b.putString("key", messageID);
			b.putString("title", title);
			b.putString("url", url);
			b.putString("imgurl", imgurl);
			b.putString("type", type);
			msg.setData(b);
			h.sendMessage(msg);

		}
		@JavascriptInterface
		public void setCommenUrl(String url){
			if (url!=null){
				String articleUrl = url.split(",")[0];
				String articleId = url.split(",")[1];
				String imageUrl = url.split(",")[2];
				String title = url.split(",")[3];
				bookShare.setShareURL(articleUrl);
				bookShare.setCollectID(articleId);
				bookShare.setShareImgurl(imageUrl);
				bookShare.setTitle(title);
			}
		}
		@JavascriptInterface
		public void setClickNum(String i){
			TNBApplication.getInstance().getSharedPreferences("clickNums",0).edit().putString("clickNum",i).commit();
		}
		public void setTitle(String str) {
			h.obtainMessage(3, str);
		}
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		inflateView();
	}
	private BookShare bookShare;  //分享，收藏，字体大小类
	/**
	 * 初始化界面
	 */
	private void inflateView() {
		titleBarView.setRightButton("更多", this);
		titleBarView.setLeftButton(R.drawable.top_menu_back, this);
		bookShare = BookShare.getIntence(this, mWebView);
		switch (mFromWhere) {
			case MESSAGE:
				bookShare.setCollectID(contentId);
				titleBarView.setTitle(title);
				break;
			case COLLECT:
				url = collectinfo.getUrl();
				bookShare.setCollectID(collectinfo.getObjId());
				int msgType = collectinfo.getType() == null ? MESSAGE : Integer.parseInt(collectinfo.getType());
				switch (msgType) {
					case 3:
					case 4:
					default:
						titleBarView.setTitle(title);
						break;
				}
				break;
			case TASK:
				titleBarView.setTitle(title);
				bookShare.setShowCollse(false);
				bookShare.setTitle(title);
				break;
			case BOOKS:
				titleBarView.setTitle(title);
				bookShare.setTitle(title);
				bookShare.setCollectID(bookID);
				break;
			case COOKBOOK:
				titleBarView.setTitle(title);
				break;
			case FOOD:
				titleBarView.setTitle(title);
				bookShare.setCollectID(bookID);
				break;
			default:
				// titleBarView.hideRightButton();
				titleBarView.setTitle(title);
				break;
		}
		String path = url.contains("?") ? "&param=1" : "?param=1";
		bookShare.setShareURL(url + path);
		mWebView.loadUrl(url);  //加载页面

		if (mFromWhere == 7) {  //常识页面
			bookShare.setMsgType(TYPE_COMMEN);

		} else {
			bookShare.setMsgType(TYPE_OTHER);
		}
	}
	@Override
	public void onFialed(int what, int errorCode) {
		cancelProDialog();
		ComveeHttpErrorControl.parseError(this, errorCode);
	}
	private CustomProgressNewDialog mProDialog;
	public boolean isProShowing() {
		return mProDialog != null && mProDialog.isShowing();
	}
	public void showProDialog(String str) {
		if (mProDialog == null) {
			mProDialog = CustomProgressNewDialog.createDialog(this);
		}
		if (!mProDialog.isShowing()) {
			mProDialog.show(str, CustomProgressNewDialog.WHITE_BACKGROUND);
		}
	}
	public void cancelProDialog() {
		try {
			getWindow().getDecorView().post(new Runnable() {

				@Override
				public void run() {
					try {
						if (mProDialog != null) {
							mProDialog.dismiss();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		// if (Integer.valueOf(android.os.Build.VERSION.SDK) >= 5) {
		// this.overridePendingTransition(R.anim.push_left_in,
		// R.anim.push_left_out); // 此为自定义的动画效果，下面两个为系统的动画效果
		// }
	}

	private class HtmlHandler {
		@JavascriptInterface
		public void show(String data) {
			if (!"".equals(data) && data != null) {
				bookShare.setShareText(data);
			}
		}
		@JavascriptInterface
		public void getWebShareMsg(String title, String shareContent, String imgUrl) {
			if (!TextUtils.isEmpty(title)) {
				bookShare.setTitle(title);
			}
			if (!TextUtils.isEmpty(shareContent)) {
				bookShare.setShareText(shareContent);
			}
			if (!TextUtils.isEmpty(imgUrl)) {
				bookShare.setShareImgurl(imgUrl);
			}
		}
	}

	/**
	 * 用于跳转web activity界面
	 * @param activity
	 * @param type
	 * @param info
	 * @param title
	 * @param url
	 * @param id
	 */
	public static final void toWebActivity(Activity activity, int type, CollectInfo info, String title, String url, String id) {
		Intent intent = new Intent(activity, BookWebActivity.class);
		intent.putExtra("fromwhere", type);
		switch (type) {
			case BookWebActivity.COLLECT:
				Bundle bundle = new Bundle();
				bundle.putSerializable("collectinfo", info);
				intent.putExtras(bundle);
				break;
			case BookWebActivity.MESSAGE:
			case BookWebActivity.BOOKS:
			case BookWebActivity.TASK:
			case BookWebActivity.COOKBOOK:
			case BookWebActivity.FOOD:
			default:
				intent.putExtra("id", id);
				intent.putExtra("url", url);
				intent.putExtra("title", title);
				break;
		}
		activity.startActivity(intent);
	}
}
