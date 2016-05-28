package com.comvee.tnb.ui.more;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.adapter.ViewPageAdapter;
import com.comvee.tnb.widget.TitleBarView;

import java.util.ArrayList;
import java.util.List;

/**
 * 血糖仪 使用指南
 */
public class UseGuideFragment extends BaseFragment implements OnPageChangeListener, OnClickListener {
    private List<ImageView> dian;// 存放移动时显示的点
    private List<View> views;// 存放使用指南的图片控件集合
    private int[] image_id;// 存放图片id;
    private ViewPager pager;
    private boolean isInit;
    private ImageView last, next;
    private String[] values = new String[]{"用温水洗净双手，或用酒精棉片清洗采血部位，测量前确保采血处充分干燥", "拧开采血笔保护盖", "将采血针插入支架，拧下采血针保护头", "将采血笔笔帽盖好，调整采血笔的深度（刻度越大，扎的越深）",
            "将笔杆往后拉（记得要拉到底），会有咔嚓声，“触按钮”会突出", "将试纸电极端朝上插入血糖仪插槽中，血糖仪将于「哔」声后自动开机", "将采血笔紧贴指尖，按下采血键，稍后挤压指尖即可取得一滴血液检体",
            "将指尖上的血液检体轻触测纸反应区侧边吸入点，试纸将自动吸入反应区", "得到血糖值结果，血糖数据自动上传到掌控糖尿病客户端", "拔除使用完的试纸，将采血针保护头插回采血针上，退下针头，一同丢弃"};
    private TextView textView;
    private TitleBarView mBarView;

    public UseGuideFragment() {
    }

    public static UseGuideFragment newInstance() {
        UseGuideFragment fragment = new UseGuideFragment();
        return fragment;
    }

    @Override
    public int getViewLayoutId() {

        return R.layout.layou_usage_fragment;
    }

    @Override
    public void onStart() {

        super.onStart();

    }

    @Override
    public void onLaunch(Bundle bundle) {
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        mBarView.setTitle("使用指南");
        if (!isInit) {
            init();
            isInit = true;
        }
    }

    public void init() {
        ViewGroup group = (ViewGroup) findViewById(R.id.usage_dian_layout);
        pager = (ViewPager) findViewById(R.id.usage_vpage);
        last = (ImageView) findViewById(R.id.usage_last);
        next = (ImageView) findViewById(R.id.usage_next);
        textView = (TextView) findViewById(R.id.usage_text);
        last.setOnClickListener(this);
        next.setOnClickListener(this);

        views = new ArrayList<View>();
        dian = new ArrayList<ImageView>();

        image_id = new int[]{R.drawable.guide1, R.drawable.guide2, R.drawable.guide3, R.drawable.guide4, R.drawable.guide5, R.drawable.guide6,
                R.drawable.guide7, R.drawable.guide8, R.drawable.guide9, R.drawable.guide10};

        for (int i = 0; i < image_id.length; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setLayoutParams(new LayoutParams(10, 10));
            dian.add(imageView);
            if (i == 0) {
                dian.get(i).setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                dian.get(i).setBackgroundResource(R.drawable.tendencypoit3);
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 10;
            layoutParams.rightMargin = 10;
            group.addView(imageView, layoutParams);

            View view = View.inflate(getContext(), R.layout.guide_item, null);
            ImageView imageView2 = (ImageView) view.findViewById(R.id.guide_item_image);
            imageView2.setBackgroundResource(image_id[i]);
            views.add(view);
        }
        pager.setOnPageChangeListener(this);
        ViewPageAdapter adapter = new ViewPageAdapter(views);
        pager.setAdapter(adapter);
        pager.setCurrentItem(0);
        textView.setText(values[0]);

    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int arg0) {
        // TODO Auto-generated method stub
        textView.setText(values[arg0]);
        for (int i = 0; i < image_id.length; i++) {
            if (i == arg0 % image_id.length) {
                dian.get(i).setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                dian.get(i).setBackgroundResource(R.drawable.tendencypoit3);
            }
        }

    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.usage_next:
                pager.setCurrentItem(pager.getCurrentItem() + 1);
                break;
            case R.id.usage_last:
                pager.setCurrentItem(pager.getCurrentItem() - 1);
                break;

        }
    }

}
