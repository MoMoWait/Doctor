package com.comvee.tnb.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.ComveeTask;
import com.comvee.tnb.model.MyTaskInfo;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.util.TimeUtil;

public class TaskRemindAdaapter extends BaseAdapter implements OnCheckedChangeListener {
	private List<MyTaskInfo> list = null;
	private Context context;

	public TaskRemindAdaapter(ArrayList<MyTaskInfo> listItems, Context context) {
		super();
		this.list = listItems;
		this.context = context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	public void setList(List<MyTaskInfo> list) {
		this.list = list;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		MyTaskInfo myTaskInfo = list.get(arg0);
		if (arg1 == null) {
			arg1 = View.inflate(context, R.layout.item_task_remind, null);

		}
		ImageView photo = (ImageView) arg1.findViewById(R.id.img_photo);
		ImageLoaderUtil.getInstance(context).displayImage(myTaskInfo.getImgUrl(), photo, ImageLoaderUtil.user_options);
		CheckBox box = (CheckBox) arg1.findViewById(R.id.cb_task_remind);
		box.setTag(myTaskInfo);
		box.setOnCheckedChangeListener(this);
		box.setChecked(myTaskInfo.defaultRemind == 1);
		TextView label = (TextView) arg1.findViewById(R.id.tv_task_label);
		TextView time = (TextView) arg1.findViewById(R.id.tv_task_time);
		label.setText(myTaskInfo.getJobTitle());
		try {
			String str = String.format("于%s添加本任务", TimeUtil.fomateTime("yyyy-MM-dd HH:mm:ss", "yyyy年MM月dd日", myTaskInfo.getInsertDt()));
			time.setText(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arg1;
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		MyTaskInfo info = (MyTaskInfo) arg0.getTag();
		requestModifyTask(info, arg0.isChecked() ? "1" : "0", info.getMemberJobId());
	}

	// 设置开关
	private void requestModifyTask(final MyTaskInfo info, final String ischeck, final String id) {
		new ComveeTask<String>() {

			@Override
			protected String doInBackground() {
				ComveeHttp http = new ComveeHttp(context, ConfigUrlMrg.TASK_MODIFY);
				http.setPostValueForKey("memberJobId", id);
				http.setPostValueForKey("defualtRemind", ischeck);
				String result = http.startSyncRequestString();
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				try {
					ComveePacket packet = ComveePacket.fromJsonString(result);
					if (packet.getResultCode() == 0) {
						info.setDefaultRemind(Integer.parseInt(ischeck));
					} else {
						info.setDefaultRemind(Integer.parseInt(ischeck) == 0 ? 1 : 0);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.execute();

	}
}
