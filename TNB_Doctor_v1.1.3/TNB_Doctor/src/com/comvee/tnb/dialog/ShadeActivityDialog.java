package com.comvee.tnb.dialog;

import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.comvee.tnb.R;
import com.comvee.tnb.model.HomeIndex;
import com.comvee.tnb.view.IndexBannerView.BannerClickListener;

/**
 * 遮罩页面
 * 
 * @author Administrator
 * 
 */
public class ShadeActivityDialog extends DialogFragment implements OnClickListener {
	private ImageView imageView;
	private int imgResources;

	// 回调监听
	private ShadeClickListener listence;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialogs);
	}

	
	public ShadeActivityDialog(ShadeClickListener shadeClickListener) {
		this.listence = shadeClickListener;
	}

	
	public void setImgResources(int imgResources) {
		this.imgResources = imgResources;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.shade_layout, null);
		imageView = (ImageView) layout.findViewById(R.id.img_shade);
		if (imgResources != 0) {
			imageView.setBackgroundResource(imgResources);
		}
		imageView.setOnClickListener(this);
		getDialog().getWindow().setLayout(-1, -1);
		getDialog().getWindow().setGravity(Gravity.CENTER);
		getDialog().setCanceledOnTouchOutside(false);
		getDialog().getWindow().setWindowAnimations(R.style.event_amin);
		getDialog().setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				return true;
			}
		});
		return layout;
		
		
		
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.img_shade://点击图片消失
			dismiss();
			if (listence != null) {
				listence.onShadeClick();
			}
			break;
		default:
			break;
		}

	}

	public void setShadeClickListener(ShadeClickListener clickListence) {
		this.listence = clickListence;
	}

	public interface ShadeClickListener {
		void onShadeClick();
	}

}
