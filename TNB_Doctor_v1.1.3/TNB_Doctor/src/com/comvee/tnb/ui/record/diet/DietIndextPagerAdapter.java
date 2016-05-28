package com.comvee.tnb.ui.record.diet;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.frame.BaseFragment;
import com.comvee.tnb.R;
import com.comvee.tnb.common.MyBaseAdapter;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.ui.record.RecordSugarInputNewFrag;
import com.comvee.tnb.ui.record.common.ImageItem4LocalImage;
import com.comvee.tnb.ui.record.common.MyBitmapFactory;
import com.comvee.tnb.ui.record.common.NetPic;
import com.comvee.tnb.ui.record.common.ShowImageViewpagerActivity;
import com.comvee.tool.AppUtil;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.tool.ViewHolder;

public class DietIndextPagerAdapter extends PagerAdapter implements InnerViewpagerAdapterInter {
    private final int VIEWCOUNT = 3;
    private final String[] mealTimes = {"早餐", "午餐", "晚餐"};
    private View[] views;
    private List<TodayDiet> diets;
    private int[] mealImgRes = {R.drawable.yinshi_10, R.drawable.yinshi_12, R.drawable.yinshi_14};

    private BaseFragment baf;
    // 查看图片需要存储的数据
    private final SparseArray<List<ImageView>> allSmallImageViewMap = new SparseArray<List<ImageView>>();
    private ViewPager mViewpager;
    private View mCurrentView;

    /**
     * 今日饮食的adapter
     *
     * @param baf
     * @param diets
     * @param vg
     */
    public DietIndextPagerAdapter(BaseFragment baf, List<TodayDiet> diets, ViewPager vg) {
        this.baf = baf;
        this.mViewpager = vg;
        views = new View[VIEWCOUNT];
        if (diets == null) {
            this.diets = new ArrayList<TodayDiet>();
            for (int i = 0; i < VIEWCOUNT; i++) {
                this.diets.add(new TodayDiet());
            }
        } else if (diets.size() != 3) {
            Log.e("log", "diets数量必须为3");
        } else
            this.diets = diets;
    }

    @Override
    public int getCount() {
        return VIEWCOUNT;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {

        return arg0 == arg1;
    }

    public View[] getChildsView() {
        return views;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final TodayDiet currentTodayDiet = diets.get(position);
        final DecimalFormat decimalFormat = new DecimalFormat("0.0");

        TextView beforeMealName, afterMealName;
        TextView beforeTextView, afterTextView, difTextView;
        ImageView beforeImageView, afterImageView;
        float beforeValue, afterValue;

        View converView;
        if (TextUtils.isEmpty(currentTodayDiet.id)) {
            converView = LayoutInflater.from(baf.getActivity().getApplicationContext()).inflate(R.layout.empty_diet_index_pager, container, false);
            beforeImageView = (ImageView) converView.findViewById(R.id.before_imageview);
            afterImageView = (ImageView) converView.findViewById(R.id.after_imageview);
            beforeTextView = (TextView) converView.findViewById(R.id.before_textview);
            afterTextView = (TextView) converView.findViewById(R.id.after_textview);
            difTextView = (TextView) converView.findViewById(R.id.dif_textview);
            beforeMealName = (TextView) converView.findViewById(R.id.before_text);
            afterMealName = (TextView) converView.findViewById(R.id.after_text);

            beforeMealName.setText(currentTodayDiet.beforeLabel);
            afterMealName.setText(currentTodayDiet.afterLabel);

            beforeValue = currentTodayDiet.beforeSugarValue;
            afterValue = currentTodayDiet.afterSugarValue;
            String difValue = "";
            difValue = decimalFormat.format(afterValue - beforeValue);
            if (afterValue == 0 || beforeValue == 0) {
                difTextView.setText("- - ");
            } else {
                difTextView.setText(difValue + "");
            }
            swapShowView(beforeTextView, beforeImageView, beforeValue);
            swapShowView(afterTextView, afterImageView, afterValue);

            beforeImageView.setOnClickListener(new UpdateSugarOnclickListener(ConfigParams.SUGAR_TIME_CODE[2 * position + 1]));
            beforeTextView.setOnClickListener(new UpdateSugarOnclickListener(ConfigParams.SUGAR_TIME_CODE[2 * position + 1]));
            afterImageView.setOnClickListener(new UpdateSugarOnclickListener(ConfigParams.SUGAR_TIME_CODE[2 * position + 2]));
            afterTextView.setOnClickListener(new UpdateSugarOnclickListener(ConfigParams.SUGAR_TIME_CODE[2 * position + 2]));
            converView.findViewById(R.id.toadd).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentIndex = mViewpager.getCurrentItem() + 1;
                    Bundle bundle = new Bundle();
                    bundle.putInt("mealTimeType", currentIndex);
                    MyBitmapFactory.clearAll();
                    baf.toFragment(CreateDietFragment.class, bundle, true);
                }
            });
        } else {
            converView = LayoutInflater.from(baf.getActivity().getApplicationContext()).inflate(R.layout.item_diet_index_pager, container, false);

            ImageView mealImg = (ImageView) converView.findViewById(R.id.meal_img);
            mealImg.setImageResource(mealImgRes[position]);
            TextView mealTimeTv = (TextView) converView.findViewById(R.id.meal_time);
            TextView mealContentTv = (TextView) converView.findViewById(R.id.meal_content);

            beforeImageView = (ImageView) converView.findViewById(R.id.before_imageview);
            afterImageView = (ImageView) converView.findViewById(R.id.after_imageview);
            beforeTextView = (TextView) converView.findViewById(R.id.before_textview);
            afterTextView = (TextView) converView.findViewById(R.id.after_textview);
            difTextView = (TextView) converView.findViewById(R.id.dif_textview);
            beforeMealName = (TextView) converView.findViewById(R.id.before_text);
            afterMealName = (TextView) converView.findViewById(R.id.after_text);

            beforeMealName.setText(currentTodayDiet.beforeLabel);
            afterMealName.setText(currentTodayDiet.afterLabel);

            beforeValue = currentTodayDiet.beforeSugarValue;
            afterValue = currentTodayDiet.afterSugarValue;
            String difValue = "";
            difValue = decimalFormat.format(afterValue - beforeValue);
            if (beforeValue == 0 || afterValue == 0) {
                difTextView.setText("- - ");
            } else {
                difTextView.setText(difValue + "");
            }
            swapShowView(beforeTextView, beforeImageView, beforeValue);
            swapShowView(afterTextView, afterImageView, afterValue);

            beforeImageView.setOnClickListener(new UpdateSugarOnclickListener(ConfigParams.SUGAR_TIME_CODE[2 * position + 1]));
            beforeTextView.setOnClickListener(new UpdateSugarOnclickListener(ConfigParams.SUGAR_TIME_CODE[2 * position + 1]));
            afterImageView.setOnClickListener(new UpdateSugarOnclickListener(ConfigParams.SUGAR_TIME_CODE[2 * position + 2]));
            afterTextView.setOnClickListener(new UpdateSugarOnclickListener(ConfigParams.SUGAR_TIME_CODE[2 * position + 2]));

            if (TextUtils.isEmpty(currentTodayDiet.name))
                mealContentTv.setText("暂无描述");
            else {
                mealContentTv.setText(AppUtil.ToDBC(currentTodayDiet.name));
            }
            mealTimeTv.setText(mealTimes[position]);

            GridView gridView = (GridView) converView.findViewById(R.id.gridview);
            converView.findViewById(R.id.to_update_layout).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyBitmapFactory.tempSelectBitmap.clear();
                    for (int i = 0; i < currentTodayDiet.netpics.size(); i++) {
                        ImageItem4LocalImage imageItem = new ImageItem4LocalImage();
                        imageItem.sourceImagePath = currentTodayDiet.netpics.get(i).picBig;
                        imageItem.smallImagePath = currentTodayDiet.netpics.get(i).picSmall;
                        MyBitmapFactory.tempSelectBitmap.add(imageItem);
                    }
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("currentDiet", currentTodayDiet);
                    bundle.putString("from", "index");
                    baf.toFragment(UpdateDietFragment.class, bundle, true);

                }
            });
            // 最多取三张图片
            int subLength = Math.min(3, diets.get(position).netpics.size());
            final ArrayList<NetPic> showData = new ArrayList<NetPic>();
            for (int i = 0; i < subLength; i++) {
                showData.add(diets.get(position).netpics.get(i));
            }
            gridView.setAdapter(new GridViewAdapter(baf.getActivity().getApplicationContext(), showData, position, R.layout.item_record_diet_gridview));
            gridView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position1, long id) {
                    Intent it = ShowImageViewpagerActivity.getIntent(baf.getActivity(), showData, position1, allSmallImageViewMap.get(position));
                    baf.getActivity().startActivity(it);
                }
            });
        }
        views[position] = converView;
        ((ViewPager) container).addView(converView);

        return converView;
    }

    private void swapShowView(TextView textView, View view, float value) {
        final float hightValue = Float.parseFloat(ConfigParams.getSugarHightValue(baf.getContext()));
        final float lowValue = Float.parseFloat(ConfigParams.getSugarLowValue(baf.getContext()));
        if (value != 0) {
            if (value < lowValue) {
                textView.setTextColor(baf.getResources().getColor(R.color.index_record_blue));
            } else if (value > hightValue) {
                textView.setTextColor(baf.getResources().getColor(R.color.index_record_red));
            } else {
                textView.setTextColor(baf.getResources().getColor(R.color.index_record_green));
            }
            textView.setText(value + "");
            textView.setVisibility(View.VISIBLE);
            view.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views[position]);
    }

    private class GridViewAdapter extends MyBaseAdapter<NetPic> {
        private List<NetPic> datas;
        private List<ImageView> imageViews = new ArrayList<ImageView>();
        private int pos;

        public GridViewAdapter(Context ctx, List<NetPic> datas, int pos, int mayoutid) {
            super(ctx, datas, mayoutid);
            this.datas = datas;
            this.pos = pos;
        }

        @Override
        protected void doyouself(ViewHolder holder, final int position) {
            NetPic netPic = datas.get(position);
            final ImageView imageView = holder.get(R.id.image);

            String smallImage = netPic.picSmall;
            if (imageViews.size() <= position) {
                imageViews.add(imageView);
            }

            allSmallImageViewMap.put(pos, imageViews);
            ImageLoaderUtil.getInstance(context).displayImage(smallImage, imageView, ImageLoaderUtil.default_options);
        }
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        mCurrentView = (View) object;
    }

    public View getPrimaryItem() {
        return mCurrentView;
    }

    private class UpdateSugarOnclickListener implements OnClickListener {
        String timeCode;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putString("time", sdf.format(new Date()));
            bundle.putString("code", timeCode);
            bundle.putInt("from_where", 3);
            bundle.putString("data",sdf.toString());
            baf.toFragment(RecordSugarInputNewFrag.class, bundle, true);
        }

        public UpdateSugarOnclickListener(String timeCode) {
            this.timeCode = timeCode;
        }
    }
}
