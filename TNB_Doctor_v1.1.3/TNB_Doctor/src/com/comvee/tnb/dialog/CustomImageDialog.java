package com.comvee.tnb.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.comvee.tnb.R;

public class CustomImageDialog extends Dialog {

	public static final int ID_OK = R.id.btn_ok;
	public static final int ID_NO = R.id.btn_no;
	private static View layout;

	public CustomImageDialog(Context context, int theme) {
		super(context, theme);
	}

	public CustomImageDialog(Context context) {
		super(context);
	}

	/**
	 * Helper class for creating a custom dialog
	 */
	public static class Builder {

		private Context context;

		private String title;
		private String message;
		private String positiveButtonText;
		private String negativeButtonText;
		private OnCancelListener mCancelListener;
		private DialogInterface.OnClickListener positiveButtonClickListener, negativeButtonClickListener;
		private int mButtonCount;

		public Builder(Context context) {
			this.context = context;
		}

		/**
		 * Set the Dialog message from String
		 * 
		 * @param title
		 * @return
		 */
		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		/**
		 * Set the Dialog message from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}

		/**
		 * Set the Dialog title from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		/**
		 * Set the Dialog title from String
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		/**
		 * Set the positive button resource and it's listener
		 * 
		 * @param positiveButtonText
		 * @param listener
		 * @return
		 */
		public Builder setPositiveButton(int positiveButtonText, DialogInterface.OnClickListener listener) {
			this.positiveButtonText = (String) context.getText(positiveButtonText);
			this.positiveButtonClickListener = listener;
			mButtonCount++;
			return this;
		}

		/**
		 * Set the positive button text and it's listener
		 * 
		 * @param positiveButtonText
		 * @param listener
		 * @return
		 */
		public Builder setPositiveButton(String positiveButtonText, DialogInterface.OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			mButtonCount++;
			return this;
		}

		/**
		 * Set the negative button resource and it's listener
		 * 
		 * @param negativeButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNegativeButton(int negativeButtonText, DialogInterface.OnClickListener listener) {
			this.negativeButtonText = (String) context.getText(negativeButtonText);
			this.negativeButtonClickListener = listener;
			mButtonCount++;
			return this;
		}

		/**
		 * Set the negative button text and it's listener
		 * 
		 * @param negativeButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNegativeButton(String negativeButtonText, DialogInterface.OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			mButtonCount++;
			return this;
		}

		public Builder setListener(DialogInterface.OnClickListener listener) {
			this.negativeButtonClickListener = listener;
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setOnCancelListener(DialogInterface.OnCancelListener listener) {
			mCancelListener = listener;
			return this;
		};
		/**
		 * Create the custom dialog
		 */
		public CustomDialog create() {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final CustomDialog dialog = new CustomDialog(context, R.style.CustomDialog);
			layout = inflater.inflate(R.layout.dialog_image_layout, null);
			final TextView tvMsg = (TextView) layout.findViewById(R.id.tv_msg);
			final TextView tvTitle = (TextView) layout.findViewById(R.id.tv_title);
			if (!TextUtils.isEmpty(title)) {
				tvTitle.setVisibility(View.GONE);
				tvTitle.setText(title);
			}
			tvMsg.setText(message);

			dialog.addContentView(layout, new LayoutParams(-1, -2));
			dialog.setContentView(layout);

			// if (mButtonCount < 2) {
			// layout.findViewById(R.id.line1).setVisibility(View.GONE);
			// }

			if (mCancelListener != null) {
				dialog.setOnCancelListener(mCancelListener);
			}

			final View.OnClickListener listener = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					final int id = v.getId();
					switch (id) {
					case R.id.btn_ok:
						if (null != positiveButtonClickListener) {
							positiveButtonClickListener.onClick(dialog, id);
						}
						break;
					case R.id.btn_no:
						if (null != negativeButtonClickListener) {
							negativeButtonClickListener.onClick(dialog, id);
						}
						break;
					default:
						break;
					}

					dialog.dismiss();
				}
			};

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
			return dialog;
		}

	}

	
}
