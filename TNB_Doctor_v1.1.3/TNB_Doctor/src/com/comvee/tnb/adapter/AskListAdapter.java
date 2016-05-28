package com.comvee.tnb.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.BaseApplication;
import com.comvee.ComveeBaseAdapter;
import com.comvee.tnb.R;
import com.comvee.tnb.model.AskIndexInfo;
import com.comvee.tool.AppUtil;
import com.comvee.tool.ImageLoaderUtil;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AskListAdapter extends ComveeBaseAdapter<AskIndexInfo.MemMsg> {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
    Calendar calendar = Calendar.getInstance();

    public AskListAdapter() {
        super(BaseApplication.getInstance(), R.layout.ask_new_list_item);
    }

    @Override
    protected void getView(ViewHolder holder, int position) {
        try {
            AskIndexInfo.MemMsg info = getItem(position);
            ImageView ivPhoto = holder.get(R.id.ask_list_item_image);
            TextView tvAskName = holder.get(R.id.tv_server_name);
            TextView tvAskTime = holder.get(R.id.tv_server_time);
            TextView tvValue = holder.get(R.id.tv_value);
            TextView unRead = holder.get(R.id.tv_unread);
            ImageLoaderUtil.getInstance(context).displayImage(info.headImageUrl, ivPhoto, ImageLoaderUtil.doc_options);
            if (info.isAdvisors) {
                tvAskName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.yisheng_101, 0);
            } else {
                tvAskName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
            tvAskName.setText(info.doctorName);

            calendar.setTimeInMillis(sdf.parse(info.insertDt).getTime());

            tvAskTime.setText(AppUtil.formatTime1(calendar));
            if (info.userMsg!=null ){
                tvValue.setText(info.userMsg.toString());
            }
            if (info.count > 0) {
                if (info.count > 99) {
                    unRead.setText("..");
                } else {
                    unRead.setText(info.count + "");
                }
                unRead.setVisibility(View.VISIBLE);
            } else {
                unRead.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
