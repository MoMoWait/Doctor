package com.comvee.tnb.ui.task;

import android.os.Bundle;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.AppUtil;

/**
 * Created by yujun on 2016/4/25.
 */
public class TaskMessageFragment extends BaseFragment {

    private String insertDt;
    private String detailInfo;

    @Override
    public int getViewLayoutId() {
        return R.layout.task_message_fragment;
    }

    @Override
    public void onLaunch(Bundle dataBundle) {
        super.onLaunch(dataBundle);
        if (dataBundle!=null){
            insertDt =
                    dataBundle.getString("insertDt");
            detailInfo =
                    dataBundle.getString("detailInfo");
        }
        TextView lable = (TextView) findViewById(R.id.message_item_label);
        TextView message = (TextView) findViewById(R.id.message_item_message);
        lable.setText(detailInfo);
        message.setText(AppUtil.dateFormatForm("yyyy-MM-dd HH:mm:ss", insertDt , "yyyy-MM-dd HH:mm"));
        TitleBarView title = (TitleBarView) findViewById(R.id.main_titlebar_view);
        title.setLeftDefault(this);
        title.setTitle("任务回访");

    }
}
