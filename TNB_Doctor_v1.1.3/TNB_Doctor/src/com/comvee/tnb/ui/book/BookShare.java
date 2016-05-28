package com.comvee.tnb.ui.book;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.CustomDialog;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.network.ObjectLoader;
import com.comvee.tnb.ui.book.ShareUtil.OnShareItemClickListence;
import com.comvee.tnb.view.MenuWindow;
import com.comvee.tool.UITool;
import com.comvee.tool.UserMrg;

public class BookShare implements OnHttpListener, OnClickListener {
	private Activity activity;
	private PopupWindow popupWindow;
	private boolean isCollected;
	private String title;
	private String shareText;
	private String shareURL;
	private String shareImgurl;
	private int msgType;//常识1 其他2
	private boolean isShowCollse = true;// 更多中是否显示收藏选项
	private String collectID = "";// 取消收藏的id
	private List<TextView> fontlist;// 存放字体弹窗的控件
	private final int LARGEST = 4;// 特大号字体
	private final int LARGER = 3;// 大号字体
	private final int NORMAL = 2;// 中号字体
	private final int SMALLER = 1;// 小号字体
	private int FONT_SIZE = NORMAL;// 字体大小
	private int TEMP_FONT_SIZE = NORMAL;// 临时字体大小
	private WebView mWebView;
	private ArrayList<String> items = new ArrayList<String>();
	private ArrayList<Integer> icons = new ArrayList<Integer>();
	public ShareUtil shareUtil;

	public static BookShare getIntence(Activity activity, WebView view) {
		BookShare bookShare = new BookShare();
		bookShare.setActivity(activity);
		bookShare.setmWebView(view);
		bookShare.shareUtil = ShareUtil.getInstance(activity);
		return bookShare;

	}

	public String getShareURL() {
		return shareURL;
	}

	/**
	 * 设置分享文案
	 * 
	 * 
	 */
	public void setShareText(String shareText) {
		this.shareText = shareText;
	}

	public void setmWebView(WebView mWebView) {
		this.mWebView = mWebView;
	}

	/**
	 * 设置HTML的id
	 * 
	 * 
	 */
	public void setCollectID(String collectID) {
		this.collectID = collectID;
		if (shareUtil != null) {
			shareUtil.setContentId(collectID);
		}
		requestIsCollse();
	}

	private void setActivity(Activity activity) {
		this.activity = activity;
		if (isShowCollse) {
			// requestCollectList();
		}
	}

	public BookShare() {
	}

	public boolean isShowCollse() {
		return isShowCollse;
	}

	/**
	 * 设置分享标题
	 * 
	 * 
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	// public void setText(String text) {
	// this.text = text;
	// }

	public int getMsgType() {
		return msgType;
	}

	/**
	 * 设置web数据类型
	 * 
	 * 
	 */
	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	/**
	 * 设置分享图片链接
	 * 
	 * 
	 */
	public void setShareImgurl(String shareImgurl) {
		this.shareImgurl = shareImgurl;
	}

	/**
	 * 设置分享链接
	 * 
	 * @param shareURL
	 */
	public void setShareURL(String shareURL) {
		this.shareURL = shareURL;
	}

	/**
	 * 设置是否显示收藏选项
	 * 
	 * 
	 */
	public void setShowCollse(boolean isShowCollse) {
		this.isShowCollse = isShowCollse;
	}

	/**
	 * 显示“更多”菜单
	 * 
	 */
	public void showMenu() {
		UITool.backgroundAlpha(activity, 0.85f);
		requestIsCollse();
		items.clear();
		icons.clear();
		items.add(activity.getResources().getString(R.string.menu_list_share).toString());
		items.add(activity.getResources().getString(R.string.menu_list_fontsize_small).toString());
		items.add(activity.getResources().getString(R.string.menu_list_collection).toString());
		icons.add(R.drawable.share_article);
		icons.add(R.drawable.font_size);
		icons.add(R.drawable.book_btn_right);
		if (!isShowCollse) {
			items.remove(activity.getResources().getString(R.string.menu_list_collection).toString());
			icons.remove((Object) R.drawable.book_btn_right);
		}

		if (collectID != null) {
			if (isCollected) {
				items.remove(activity.getResources().getString(R.string.menu_list_collection).toString());
				icons.remove((Object) R.drawable.book_btn_right);
				items.add(activity.getResources().getString(R.string.menu_list_collection_1).toString());
				icons.add(R.drawable.book_btn_rightdeep);
			}
//			if (msgType == BookWebActivity.FOOD || msgType == BookWebActivity.COOKBOOK) {
//				items.remove(activity.getResources().getString(R.string.menu_list_fontsize_small).toString());
//				icons.remove((Object) R.drawable.font_size);
//			}
		}

		final MenuWindow menuWindow = MenuWindow.getInstance(activity, items, icons, 150);

		menuWindow.setOnOnitemClickList(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				menuWindow.dismiss();
				String item = parent.getAdapter().getItem(position).toString();
				if (item.equals(activity.getText(R.string.menu_list_share).toString())) {
					shareUtil.setTitle(title);
					shareUtil.setShareImgUrl(shareImgurl);
					shareUtil.setTitleUrl(shareURL);
					shareUtil.setUrl(shareURL);
					shareUtil.addOnShareItemClickListence(new OnShareItemClickListence() {

						@Override
						public void onItemClick(String platform) {
							shareUtil.setShareText(getShareText(platform));
							shareUtil.ShareArticle(platform);
						}
					});
					shareUtil.show(activity.findViewById(R.id.layout_top), Gravity.BOTTOM);
				} else if (item.equals(activity.getText(R.string.menu_list_fontsize_small))) {
					showFontMenu();
				} else if (item.equals(activity.getText(R.string.menu_list_collection_1))
						|| item.equals(activity.getText(R.string.menu_list_collection))) {
					String path = "文章?";
//					if (msgType == 3) {
//						path = "食谱?";
//					}
					String msg = (isCollected ? "是否取消收藏该" : "是否收藏该") + path;
					showCollectDialog(msg);
				}
			}
		});
		int x = (int) (UITool.getDisplayWidth(activity) - UITool.dip2px(activity, 160));
		menuWindow.showAsDropDown(activity.findViewById(R.id.layout_top), x, 0);
	}

	@Override
	public void onSussece(int what, byte[] b, boolean fromCache) {
		switch (what) {

		case 2:
			try {
				ComveePacket packet = ComveePacket.fromJsonString(b);
				if (packet.getResultCode() == 0) {
					ComveeHttp.clearCache(activity, UserMrg.getCacheKey(ConfigUrlMrg.COLLECT_LIST));
					Toast.makeText(activity, "收藏成功!", Toast.LENGTH_SHORT).show();
					// requestCollectList();
					this.isCollected = true;
				} else {
					ComveeHttpErrorControl.parseError(activity, packet);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 3:
			try {
				ComveePacket packet = ComveePacket.fromJsonString(b);
				if (packet.getResultCode() == 0) {
					isCollected = false;
					Toast.makeText(activity, packet.getResultMsg(), Toast.LENGTH_SHORT).show();
					// requestCollectList();
				} else {
					ComveeHttpErrorControl.parseError(activity, packet);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 5:
			try {
				ComveePacket packet = ComveePacket.fromJsonString(b);
				if (packet.getResultCode() == 0) {
					int iscol = packet.getJSONObject("body").optInt("isCollect");
					this.isCollected = iscol == 1 ? true : false;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		}
	}

	@Override
	public void onFialed(int what, int errorCode) {
	}

	/**
	 * 验证是否被收藏
	 * 
	 * 
	 */
	public void requestIsCollse() {
		// if (messageMap != null && messageMap.size() > 100 && collectID !=
		// null && !messageMap.containsKey(collectID)) {
		if (collectID != null && !collectID.equals("")) {
			ComveeHttp http = new ComveeHttp(activity, ConfigUrlMrg.CHECKCOLLECT);
			http.setOnHttpListener(5, this);
			http.setPostValueForKey("id", collectID);
			http.startAsynchronous();
		}
		// }
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.popu_img:
			if (popupWindow.isShowing()) {
				popupWindow.dismiss();
			}
			break;

		case R.id.btn_ok:
			if (popupWindow.isShowing() && popupWindow != null) {
				popupWindow.dismiss();
			}
			TEMP_FONT_SIZE = FONT_SIZE;
			if (mWebView != null)
				mWebView.loadUrl(String.format("javascript:updateText(" + FONT_SIZE + ")"));// 这里是java端调用webview的JS
			break;
		case R.id.btn_no:
			if (popupWindow.isShowing()) {
				popupWindow.dismiss();
			}
			FONT_SIZE = TEMP_FONT_SIZE;
			break;
		case R.id.tv_max:
			FONT_SIZE = LARGEST;
			fontSelect(LARGEST);
			break;
		case R.id.tv_max_1:
			FONT_SIZE = LARGER;
			fontSelect(LARGER);
			break;
		case R.id.tv_in:
			FONT_SIZE = NORMAL;
			fontSelect(NORMAL);
			break;
		case R.id.tv_min:
			FONT_SIZE = SMALLER;
			fontSelect(SMALLER);
			break;
		default:
			break;
		}
	}

	/**
	 * 显示“字体大小”菜单
	 * 
	 */
	public void showFontMenu() {
		fontlist = new ArrayList<TextView>();
		View v = View.inflate(activity, R.layout.font_layout, null);

		TextView max_max = (TextView) v.findViewById(R.id.tv_max);
		TextView max = (TextView) v.findViewById(R.id.tv_max_1);
		TextView in = (TextView) v.findViewById(R.id.tv_in);
		TextView min = (TextView) v.findViewById(R.id.tv_min);
		Button ok = (Button) v.findViewById(R.id.btn_ok);
		Button no = (Button) v.findViewById(R.id.btn_no);

		fontlist.add(min);
		fontlist.add(in);
		fontlist.add(max);
		fontlist.add(max_max);

		fontSelect(FONT_SIZE);
		max_max.setOnClickListener(this);
		max.setOnClickListener(this);
		in.setOnClickListener(this);
		min.setOnClickListener(this);
		ok.setOnClickListener(this);
		no.setOnClickListener(this);

		popupWindow = new PopupWindow(v, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		popupWindow.setFocusable(true);
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(true);
		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setAnimationStyle(R.style.PopupAnimation);
		popupWindow.showAtLocation(activity.findViewById(R.id.layout_top), Gravity.CENTER, 0, 0);
	}

	/**
	 * 选择字体
	 * 
	 * @param fontsize
	 */
	private void fontSelect(int fontsize) {
		if (fontlist == null) {
			return;
		}
		for (int i = 1; i <= fontlist.size(); i++) {
			if (fontsize == i) {
				fontlist.get(i - 1).setCompoundDrawablesWithIntrinsicBounds(null, null, activity.getResources().getDrawable(R.drawable.right_select),
						null);
			} else {
				fontlist.get(i - 1).setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			}
		}

	}

	/**
	 * 是否收藏的对话框
	 * 
	 */
	private void showCollectDialog(String msg) {
		CustomDialog.Builder dialog = new CustomDialog.Builder(activity);
		dialog.setMessage(msg);
		dialog.setNegativeButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if (isCollected)
					requestCancleCollect();
				else
					requestCollect();
			}
		});
		dialog.setPositiveButton("取消", null);
		dialog.create().show();
	}

	/**
	 * 添加收藏
	 * 
	 */
	private void requestCollect() {
		ComveeHttp http = new ComveeHttp(activity, ConfigUrlMrg.COLLECT_ADD_NEW);
		http.setPostValueForKey("type", msgType + "");
		http.setPostValueForKey("id", collectID);
		http.setOnHttpListener(2, this);
		http.startAsynchronous();
	}

	/**
	 * 取消收藏
	 * 
	 */
	private void requestCancleCollect() {
		ComveeHttp http = new ComveeHttp(activity, ConfigUrlMrg.COLLECT_CANCLE);
		http.setOnHttpListener(3, this);
		http.setPostValueForKey("type", msgType + "");
		http.setPostValueForKey("id", collectID);
		http.startAsynchronous();
	}

	/**
	 * 文案
	 * 
	 * @param platform
	 * @return
	 */
	private String getShareText(String platform) {
		String text = null;
		if (msgType == BookWebActivity.TASK) {
			if (platform.equals(SinaWeibo.NAME)) {
				text = "我正在完成掌控糖尿病每日阅读一篇文章的任务：" + title + "，推荐你也来看看哦～";
			}
			if (platform.equals(Wechat.NAME) || platform.equals(WechatMoments.NAME) || platform.equals(QQ.NAME)) {
				text = "掌控糖尿病每日阅读计划：" + title + "，一天一篇能学到很多知识哦，推荐你也来看看～";
			}
		} else {
			if (!TextUtils.isEmpty(shareText)) {
				text = shareText;
			} else {
				text = title;
			}
		}

		return text;
	}

}
