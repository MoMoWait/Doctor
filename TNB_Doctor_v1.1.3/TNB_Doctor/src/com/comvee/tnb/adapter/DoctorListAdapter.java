package com.comvee.tnb.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comvee.BaseApplication;
import com.comvee.ComveeBaseAdapter;
import com.comvee.tnb.R;
import com.comvee.tnb.model.DoctorInfo;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.util.Util;

/**
 * 医生列表的适配器（找医生模块）
 * Created by friendlove-pc on 16/3/25.
 */
public class DoctorListAdapter extends ComveeBaseAdapter<DoctorInfo> {
    final int[] tagView = {R.id.tv_label_1, R.id.tv_label_2, R.id.tv_label_3};
    private final LinearLayout.LayoutParams parmas = new LinearLayout.LayoutParams(-2, -2);
    private View.OnClickListener mServerOnclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewHolder holder = (ViewHolder) view.getTag();
            View layout = holder.get(R.id.layout_detail_server);
            if(layout.getVisibility() == View.VISIBLE){
                layout.setVisibility(View.GONE);
            }else{
                layout.setVisibility(View.VISIBLE);
            }
        }
    };

    public DoctorListAdapter() {
        super(BaseApplication.getInstance(), R.layout.doc_list_item);
        parmas.rightMargin = Util.dip2px(context, 5);
    }

    @Override
    protected void getView(ViewHolder holder, int position) {

        DoctorInfo info = getItem(position);
        ImageView img_doc = holder.get(R.id.img_doc);
        TextView doc_name = holder.get(R.id.tv_doc_name);
        TextView doc_desc = holder.get(R.id.tv_doc_desc);
        TextView doc_address = holder.get(R.id.tv_doc_address);
        if (info.if_doctor == 1) {
            holder.get(R.id.tv_my_doctor_lable).setVisibility(View.VISIBLE);
        } else {
            holder.get(R.id.tv_my_doctor_lable).setVisibility(View.GONE);
        }
        ImageLoaderUtil.getInstance(context).displayImage(info.PER_PER_REAL_PHOTO, img_doc, ImageLoaderUtil.doc_options);
        doc_name.setText(info.PER_NAME);
        doc_desc.setText(info.PER_POSITION);
        doc_address.setText(info.HOS_NAME);


        //组装医生标签
        setupTags(holder, info.TAGS);
        //组装服务图标
        setupServerIcons(holder, info);

        holder.get(R.id.btn_server_detail).setTag(holder);
        holder.get(R.id.btn_server_detail).setOnClickListener(mServerOnclick);
        setupServerDetailList(holder, info);
    }

    private void setupServerDetailList(ViewHolder holder, DoctorInfo info) {

        LinearLayout layout = holder.get(R.id.layout_detail_server);
        layout.removeAllViews();
        if (info.doctorPackageInfo != null) {

            for (int i = 0; i < info.doctorPackageInfo.size(); i++) {

                DoctorInfo.PackageInfo obj = info.doctorPackageInfo.get(i);
                if (i == 0) {
                    View view = createDeatilView(obj);
                    ImageView img_up = (ImageView) view.findViewById(R.id.img_server_end);
                    img_up.setVisibility(View.VISIBLE);
                    layout.addView(view,parmas);
                } else {
                    layout.addView(createDeatilView(obj),parmas);
                }
            }
        }
    }

    private View createDeatilView(DoctorInfo.PackageInfo info) {
        View view = View.inflate(context, R.layout.item_doc_server, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.img_server_head);
        TextView server_name = (TextView) view.findViewById(R.id.tv_server_name);
        TextView server_money = (TextView) view.findViewById(R.id.tv_server_money);

        ImageLoaderUtil.getInstance(context).displayImage(info.packageImgThumb, imageView, ImageLoaderUtil.null_defult);
        server_name.setText(info.packageName);
        server_money.setText(info.priceShow);
        if (info.isHaveCoupon) {
            server_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.kaquan_27, 0);
        }
        return view;
    }


    private void setupTags(ViewHolder holder, String tags) {
        if (!TextUtils.isEmpty(tags)) {
            String str[] = tags.replace("^$%", "@").split("@");
            TextView tvTag = null;

            for (int i = 0; i < tagView.length; i++) {
                tvTag = holder.get(tagView[i]);
                if (i < str.length) {
                    tvTag.setVisibility(View.VISIBLE);
                    tvTag.setText(str[i]);
                } else {
                    tvTag.setVisibility(View.GONE);
                }
            }

        } else {
            for (int id : tagView) {
                holder.get(id).setVisibility(View.GONE);
            }
        }
    }

    private void setupServerIcons(ViewHolder holder, DoctorInfo info) {
        LinearLayout iconLayout = holder.get(R.id.layout_server);
        iconLayout.removeAllViews();
        if (info.doctorPackageInfo != null) {
            for (DoctorInfo.PackageInfo obj : info.doctorPackageInfo) {
                setupServerIcon(iconLayout, obj);
            }
        }
    }

    private void setupServerIcon(LinearLayout layout, DoctorInfo.PackageInfo obj) {
        ImageView iv = (ImageView) View.inflate(context, R.layout.item_doc_server_head_grid, null);
        ImageLoaderUtil.getInstance(context).displayImage(obj.packageImgThumb, iv, ImageLoaderUtil.null_defult);
        layout.addView(iv, parmas);
    }


}
