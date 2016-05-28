package com.comvee.tnb.radio;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.BaseApplication;
import com.comvee.ComveeBaseAdapter;
import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.model.CollectItem;
import com.comvee.tool.ImageLoaderUtil;

public class RadioCollectAdapter extends ComveeBaseAdapter<CollectItem> {
    private boolean isShowSelect;

    public RadioCollectAdapter() {
        super(BaseApplication.getInstance(), R.layout.radio_collect_list_item);
    }

    @Override
    protected void getView(ComveeBaseAdapter.ViewHolder holder, int position) {

        final CollectItem item = getItem(position);
        CheckBox checkbox = holder.get(R.id.checkbox);

        if (isShowSelect) {
            checkbox.setVisibility(View.VISIBLE);
            checkbox.setChecked(item.isCheck);
            checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton arg0, boolean checked) {
                    item.isCheck = checked;
                }
            });
        } else {
            checkbox.setVisibility(View.GONE);
        }

        ((TextView) holder.get(R.id.collect_tv_title)).setText(item.radioTitle);
        ((TextView) holder.get(R.id.collect_tv_desc)).setText(item.radioSubhead);
        ((TextView) holder.get(R.id.collect_tv_time)).setText(item.updateTime);
        ImageLoaderUtil.getInstance(TNBApplication.getInstance()).displayImage(item.photoUrl, (ImageView) holder.get(R.id.iv_booklogo), ImageLoaderUtil.null_defult);
    }

    public void setShowSelect(boolean isShowSelect) {
        this.isShowSelect = isShowSelect;
    }
}


