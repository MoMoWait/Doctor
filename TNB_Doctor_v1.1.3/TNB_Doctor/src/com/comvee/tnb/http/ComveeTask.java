package com.comvee.tnb.http;

import android.os.AsyncTask;
import android.widget.Toast;

import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;

public abstract class ComveeTask<T> extends AsyncTask<Void, Object, T> {

	@Override
	protected T doInBackground(Void... params) {
		return doInBackground();
	}

	protected abstract T doInBackground();

	public void postError(ComveePacket pack) {
		publishProgress(pack);
	}

	public void postError() {
		publishProgress(0);
	}

	@Override
	protected void onProgressUpdate(Object... values) {
		super.onProgressUpdate(values);
		if (values[0] instanceof ComveePacket) {
			ComveePacket packet = (ComveePacket) values[0];
			Toast.makeText(TNBApplication.getInstance(), packet.getResultMsg(), Toast.LENGTH_SHORT).show();
			onError(packet.getResultCode());
		} else if (values[0] instanceof Integer) {
			Toast.makeText(TNBApplication.getInstance(), R.string.error, Toast.LENGTH_SHORT).show();
			onError((Integer)values[0]);
		}
	}
	
	public void onError(int code){
		
	}

}
