package com.comvee.tnb.guides;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.TextPaint;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.widget.TitleBarView;

import org.json.JSONArray;

public class GuideHealthFoodRecipe extends BaseFragment implements OnClickListener {
    private GuideFoodRecipeInfo info;
    private IndexTaskInfo indexTaskInfo;
    private LinearLayout layout_text, layout_task;
    private TextView tvLeft, tvCentre, tvRight;
    private TitleBarView mBarView;

    public GuideHealthFoodRecipe() {
    }

    public static GuideHealthFoodRecipe newInstance(IndexTaskInfo indexTaskInfo, GuideFoodRecipeInfo info) {
        GuideHealthFoodRecipe frag = new GuideHealthFoodRecipe();
        frag.setInfo(info);
        frag.setIndexTaskInfo(indexTaskInfo);
        return frag;
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.guide_food_tecipe_frag;
    }

    @Override
    public void onLaunch(Bundle bundle) {
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        init();
    }

    public void setInfo(GuideFoodRecipeInfo info) {
        this.info = info;
    }

    public void setIndexTaskInfo(IndexTaskInfo indexTaskInfo) {
        this.indexTaskInfo = indexTaskInfo;
    }

    private void init() {

        if (info == null) {
            showToast(getResources().getString(R.string.error));
            return;
        }

        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        TextView tvDesc = (TextView) findViewById(R.id.tv_desc);
        tvLeft = (TextView) findViewById(R.id.tv_left);
        tvCentre = (TextView) findViewById(R.id.tv_centre);
        tvRight = (TextView) findViewById(R.id.tv_right);
        Button btnLast = (Button) findViewById(R.id.btn_last);
        Button btnNext = (Button) findViewById(R.id.btn_next);
        Button btnKnow = (Button) findViewById(R.id.btn_iknow);

        layout_text = (LinearLayout) findViewById(R.id.layout_text);
        layout_task = (LinearLayout) findViewById(R.id.layout_task);
        btnKnow.setOnClickListener(this);
        tvLeft.setOnClickListener(this);
        tvCentre.setOnClickListener(this);
        tvRight.setOnClickListener(this);
        btnLast.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        if (Integer.parseInt(info.getSeq()) <= 1) {
            btnLast.setEnabled(false);
            // btnLast.setPressed(false);
        }
        if (info.getSeq().equals(info.getTotal())) {
            btnNext.setText(getString(R.string.txt_isee));

        } else if (Integer.parseInt(info.getSeq()) >= Integer.parseInt(info.getMsgseq())) {

            btnNext.setEnabled(false);
            // btnNext.setPressed(false);
        }

        if (info.getPagetype().equals("3")) {
            findViewById(R.id.lin_button).setVisibility(View.GONE);
            findViewById(R.id.lin_recipe).setVisibility(View.GONE);
            findViewById(R.id.btn_iknow).setVisibility(View.VISIBLE);
            findViewById(R.id.line).setVisibility(View.VISIBLE);
            final String title = String.format("您每日需要消耗<font color=" + TNBApplication.getInstance().getResources().getColor(R.color.theme_color_green)
                    + "><strong><big>%s</big></strong></font>卡路里", info.getCalorie());
            tvTitle.setText(Html.fromHtml(title));
            tvDesc.setText(info.getDesc());
            if (info.getDesc1() != null && !info.getDesc1().equals("")) {

                try {
                    JSONArray array = new JSONArray(info.getDesc1());
                    for (int i = 0; i < array.length(); i++) {
                        // addText(array.getString(i));
                        // TextView desc_1 = (TextView)
                        // findViewById(R.id.tv_food_recipe_desc_1);
                        // desc_1.setText(array.getString(i));
                        addText(array.getString(i));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (info.getNexttasks() != null && !info.getNexttasks().equals("")) {
                try {
                    JSONArray array = new JSONArray(info.getNexttasks());
                    for (int i = 0; i < array.length(); i++) {
                        // addText(array.getString(i));

                        IndexTaskInfo mLinkInfo = DataParser.createIndexTaskInfo(array.optJSONObject(i));
                        addTaskItem(mLinkInfo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            findViewById(R.id.lin_button).setVisibility(View.VISIBLE);
            final String title = String.format("<font color=" + TNBApplication.getInstance().getResources().getColor(R.color.theme_color_green)
                    + "><strong><big>%s</big></strong></font>卡路里食谱", info.getCalorie());
            tvTitle.setText(Html.fromHtml(title));
            tvDesc.setText(info.getDate());
            tvLeft.setBackgroundColor(getResources().getColor(R.color.theme_color_green));
            tvLeft.setTextColor(Color.parseColor("#ffffff"));
            addText(info.getBreakfast());
            if (info.getBreakfastadd() != null || info.getBreakfastadd().equals("")) {
                addText(info.getBreakfastadd());
            }
        }

        if (indexTaskInfo.getTitle() != null && indexTaskInfo != null) {
            mBarView.setTitle(indexTaskInfo.getTitle());
        }
    }

    private void addTaskItem(IndexTaskInfo info) {
        View view = View.inflate(getApplicationContext(), R.layout.guide_five_food_recipe_item, null);
        ((TextView) view.findViewById(R.id.tv_value)).setText(info.getSubtitle());
        view.setOnClickListener(this);
        view.setTag(info);
        layout_task.addView(view);
    }

    private void addText(String str) {
        // JSONArray array;
        // try {
        // array = new JSONArray(str);
        // for (int i = 0; i < array.length(); i++) {
        if (str != null && !str.equals("")) {
            View view = View.inflate(getApplicationContext(), R.layout.guides_result_item, null);
            TextView tv = (TextView) view.findViewById(R.id.tv_text);
            TextPaint tp = tv.getPaint();
            tp.setFakeBoldText(false);
            tv.setText(str);
            layout_text.addView(view);
        }
        // }
        // } catch (JSONException e) {
        // e.printStackTrace();
        // }

    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.tv_left:
                tvLeft.setBackgroundColor(getResources().getColor(R.color.theme_color_green));
                tvLeft.setTextColor(Color.parseColor("#ffffff"));
                tvCentre.setTextColor(getResources().getColor(R.color.theme_color_green));
                tvRight.setTextColor(getResources().getColor(R.color.theme_color_green));
                tvCentre.setBackgroundResource(R.drawable.recipe_centre);

                tvRight.setBackgroundResource(R.drawable.recipt_right);
                layout_text.removeAllViews();
                addText(info.getBreakfast());
                addText(info.getBreakfastadd());
                break;
            case R.id.tv_centre:
                tvCentre.setBackgroundColor(getResources().getColor(R.color.theme_color_green));
                tvLeft.setBackgroundResource(R.drawable.recipe_left);
                tvRight.setBackgroundResource(R.drawable.recipt_right);
                tvCentre.setTextColor(Color.parseColor("#ffffff"));
                tvLeft.setTextColor(getResources().getColor(R.color.theme_color_green));
                tvRight.setTextColor(getResources().getColor(R.color.theme_color_green));
                layout_text.removeAllViews();
                addText(info.getLunch());
                addText(info.getLunchadd());
                break;
            case R.id.tv_right:
                tvRight.setBackgroundColor(getResources().getColor(R.color.theme_color_green));
                tvLeft.setBackgroundResource(R.drawable.recipe_left);
                tvCentre.setBackgroundResource(R.drawable.recipe_centre);
                tvRight.setTextColor(Color.parseColor("#ffffff"));
                tvCentre.setTextColor(getResources().getColor(R.color.theme_color_green));
                tvLeft.setTextColor(getResources().getColor(R.color.theme_color_green));
                layout_text.removeAllViews();
                addText(info.getDinner());
                addText(info.getDinneradd());
                break;
            case R.id.btn_last:
                if (Integer.parseInt(info.getSeq()) > 1) {
                    GuideMrg guideMrg = GuideMrg.getInstance();
                    indexTaskInfo.setSeq(Integer.parseInt(info.getSeq()) - 1);
                    if (indexTaskInfo != null) {
                        guideMrg.jumpGuide(this, indexTaskInfo);
                    } else {
                        showToast(getResources().getString(R.string.error));
                    }
                }

                break;
            case R.id.btn_next:
                if (Integer.parseInt(info.getSeq()) < Integer.parseInt(info.getMsgseq())
                        || Integer.parseInt(info.getSeq()) == Integer.parseInt(info.getTotal())) {
                    GuideMrg guideMrg = GuideMrg.getInstance();
                    if (indexTaskInfo.getType() == IndexTaskInfo.JUMP_NEW_FOOD_RECIOE && indexTaskInfo.getStatus() == 1) {
                        IndexFrag.toFragment(getActivity(),true); 
                    } else {
                        indexTaskInfo.setSeq(Integer.parseInt(info.getSeq()) + 1);
                        if (indexTaskInfo != null) {
                            guideMrg.jumpGuide(this, indexTaskInfo);
                        } else {
                            showToast(getResources().getString(R.string.error));
                        }
                    }
                }

                break;
            case R.id.btn_iknow:
                //	getActivity().getSupportFragmentManager().beginTransaction().replace(arg0, arg1)
                IndexFrag.toFragment(getActivity(),true); 
                break;
            case R.id.guide_five_no_recipe_item:
                IndexTaskInfo info = (IndexTaskInfo) arg0.getTag();
                if (info != null) {
                    GuideMrg.getInstance().jumpGuide(this, info);
                } else {
                    showToast(getResources().getString(R.string.error));
                }
                break;
            default:
                break;
        }
    }

    // 直接返回首页
    @Override
    public boolean onBackPress() {
        IndexFrag.toFragment(getActivity(),true); 
        return true;
    }
}
