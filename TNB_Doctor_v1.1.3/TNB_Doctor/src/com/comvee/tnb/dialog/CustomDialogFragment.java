package com.comvee.tnb.dialog;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.comvee.tnb.R;

public class CustomDialogFragment extends DialogFragment {
	private String title;
	private String message;
	private String positiveButtonText;
	private String negativeButtonText;
	private OnCancelListener mCancelListener;
	private DialogInterface.OnClickListener positiveButtonClickListener, negativeButtonClickListener;
	private int mButtonCount;
	private boolean isHashClose;

	public static CustomDialogFragment getInstance() {
		return new CustomDialogFragment();
	}

	public void setCanClose(boolean b) {
		isHashClose = b;

	}

	/**
	 * Set the Dialog message from String
	 * 
	 * @param title
	 * @return
	 */
	public void setMessage(String message) {
		this.message = message;

	}

	/**
	 * Set the Dialog message from resource
	 * 
	 * @param title
	 * @return
	 */
	public void setMessage(int message) {
		this.message = (String) getText(message);

	}

	/**
	 * Set the Dialog title from resource
	 * 
	 * @param title
	 * @return
	 */
	public void setTitle(int title) {
		this.title = (String) getText(title);

	}

	/**
	 * Set the Dialog title from String
	 * 
	 * @param title
	 * @return
	 */
	public void setTitle(String title) {
		this.title = title;

	}

	/**
	 * Set the positive button resource and it's listener
	 * 
	 * @param positiveButtonText
	 * @param listener
	 * @return
	 */
	public void setPositiveButton(int positiveButtonText, DialogInterface.OnClickListener listener) {
		this.positiveButtonText = (String) getText(positiveButtonText);
		this.positiveButtonClickListener = listener;
		mButtonCount++;

	}

	/**
	 * Set the positive button text and it's listener
	 * 
	 * @param positiveButtonText
	 * @param listener
	 * @return
	 */
	public void setPositiveButton(String positiveButtonText, DialogInterface.OnClickListener listener) {
		this.positiveButtonText = positiveButtonText;
		this.positiveButtonClickListener = listener;
		mButtonCount++;

	}

	/**
	 * Set the negative button resource and it's listener
	 * 
	 * @param negativeButtonText
	 * @param listener
	 * @return
	 */
	public void setNegativeButton(int negativeButtonText, DialogInterface.OnClickListener listener) {
		this.negativeButtonText = (String) getText(negativeButtonText);
		this.negativeButtonClickListener = listener;
		mButtonCount++;
	}

	/**
	 * Set the negative button text and it's listener
	 * 
	 * @param negativeButtonText
	 * @param listener
	 * @return
	 */
	public void setNegativeButton(String negativeButtonText, DialogInterface.OnClickListener listener) {
		this.negativeButtonText = negativeButtonText;
		this.negativeButtonClickListener = listener;
		mButtonCount++;
	}

	public void setListener(DialogInterface.OnClickListener listener) {
		this.negativeButtonClickListener = listener;
		this.positiveButtonClickListener = listener;
	}

	public void setOnCancelListener(DialogInterface.OnCancelListener listener) {
		mCancelListener = listener;

	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));;
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		View layout = inflater.inflate(R.layout.dialog_layout, container);

		final TextView tvMsg = (TextView) layout.findViewById(R.id.tv_msg);
		final TextView tvTitle = (TextView) layout.findViewById(R.id.tv_title);
		if (!TextUtils.isEmpty(title)) {
			tvTitle.setVisibility(View.VISIBLE);
			tvTitle.setText(title);
		} else {
			tvTitle.setVisibility(View.GONE);
		}
		tvMsg.setText(message);

		if (mButtonCount < 2) {
			layout.findViewById(R.id.line1).setVisibility(View.GONE);
		}

		if (mCancelListener != null) {
			setOnCancelListener(mCancelListener);
		}

		final View.OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final int id = v.getId();
				switch (id) {
				case R.id.btn_ok:
					if (null != positiveButtonClickListener) {
						positiveButtonClickListener.onClick(null, id);
					}
					break;
				case R.id.btn_no:
					if (null != negativeButtonClickListener) {
						negativeButtonClickListener.onClick(null, id);
					}
					break;
				case R.id.btn_close:
					break;
				default:
					break;
				}

				dismiss();

			}
		};

		layout.findViewById(R.id.btn_close).setOnClickListener(listener);
		layout.findViewById(R.id.btn_close).setVisibility(isHashClose ? View.VISIBLE : View.GONE);
		final Button btnOk = (Button) layout.findViewById(R.id.btn_ok);
		btnOk.setOnClickListener(listener);
		if (!TextUtils.isEmpty(positiveButtonText)) {
			btnOk.setText(positiveButtonText);
			btnOk.setVisibility(View.VISIBLE);
		} else {
			btnOk.setVisibility(View.GONE);
		}

		final Button btnNo = (Button) layout.findViewById(R.id.btn_no);
		btnNo.setOnClickListener(listener);
		if (!TextUtils.isEmpty(negativeButtonText)) {
			btnNo.setText(negativeButtonText);
			btnNo.setVisibility(View.VISIBLE);
		} else {
			btnNo.setVisibility(View.GONE);
		}

		return layout;
	}

}
