package com.comvee.tnb.ui.record;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.HealthResultInfo;
import com.comvee.tnb.model.RecordDetailItem;
import com.comvee.tnb.model.RecordDetailModels;
import com.comvee.tnb.network.ObjectLoader;
import com.comvee.tnb.ui.record.diet.RecordDietIndexFragment;
import com.comvee.tnb.view.SingleInputItemWindow;
import com.comvee.tnb.widget.MyGridView;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.AppUtil;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.tool.UITool;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class RecordDetailFragment extends BaseFragment implements OnClickListener, OnItemClickListener, OnHttpListener {
    private TextView tv_suggest;
    private MyGridView grid1;
    private RecordDetailGridAdaapter adapter;
    private ComveePacket packet;
    private int suggerType;// 血糖类型 1 低 3 正常 5 高
    private View lin_normal, lin_suggest_group, lin_select_time, rel_remark;
    private ImageView img_normal, im_cancle, img_top_2, img_top_1, img_top_3;
    private TextView tv_title, tv_normal, tv_label, tv_select_time;
    private Button btn_submit;
    private EditText tv_remark;
    private HashMap<String, RecordDetailModels> suggerHigh;// 高血糖
    private HashMap<String, RecordDetailModels> suggerLow;// 低血糖
    private int fromWhere;// 1 记录 2 血糖仪 3饮食记录
    private ArrayList<RecordDetailItem> models = new ArrayList<RecordDetailItem>();// 要显示的选择列表
    private RecordDetailBean bean;
    private String[] times, codes;
    private int ViewWidth;// 每个选项的宽度
    private ComveePacket detatilList;// 每个时间段的选项列表
    private TitleBarView mBarView;
    private TextView tv_remind;

    public RecordDetailFragment(){

    }

    public static RecordDetailFragment newInstance(ComveePacket packet, ComveePacket detatilList, int fromWhere){
        RecordDetailFragment frag = new RecordDetailFragment();
        frag.packet = packet;
        frag.fromWhere = fromWhere;
        frag.detatilList = detatilList;
        return frag;
    }

    @Override
    public int getViewLayoutId() {
        // TODO Auto-generated method stub
        return R.layout.record_detail_fragment;
    }

    /**
     * 初始化界面
     */
    private void init() {
        lin_normal = findViewById(R.id.lin_normal);
        rel_remark = findViewById(R.id.rel_remark);
        lin_select_time = findViewById(R.id.lin_select_time);
        lin_suggest_group = findViewById(R.id.lin_suggest_group);
        im_cancle = (ImageView) findViewById(R.id.im_cancle);
        img_top_2 = (ImageView) findViewById(R.id.img_top_2);
        img_top_1 = (ImageView) findViewById(R.id.img_top_1);
        img_top_3 = (ImageView) findViewById(R.id.img_top_3);
        tv_label = (TextView) findViewById(R.id.tv_label);
        tv_remark = (EditText) findViewById(R.id.tv_remark);
        tv_suggest = (TextView) findViewById(R.id.tv_suggest);
        tv_select_time = (TextView) findViewById(R.id.tv_select_time);
        grid1 = (MyGridView) findViewById(R.id.gridview_detail);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_normal = (TextView) findViewById(R.id.tv_normal);
        img_normal = (ImageView) findViewById(R.id.img_normal);
        tv_remind = (TextView) findViewById(R.id.tv_remind);
        lin_select_time.setOnClickListener(this);
        grid1.setOnItemClickListener(this);
        im_cancle.setOnClickListener(this);
        btn_submit.setOnClickListener(this);

        if (fromWhere == 2) {
            lin_select_time.setVisibility(View.VISIBLE);
            im_cancle.setVisibility(View.VISIBLE);
            mBarView.setVisibility(View.GONE);
        } else {
            lin_suggest_group.setBackgroundColor(getResources().getColor(R.color.white));
            im_cancle.setVisibility(View.GONE);
            lin_select_time.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLaunch(Bundle bundle) {
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        mBarView.setTitle(getString(R.string.title_record_detail));
        init();
        getViewWidth();
        if (suggerHigh == null || suggerLow == null) {
            try {
                parseDate();
            } catch (Exception e) {
                FragmentMrg.toBack(getActivity());
            }

        }
        adapter = new RecordDetailGridAdaapter(getApplicationContext());
        grid1.setAdapter(adapter);
        upView();
        // MyGridView grid2 = (MyGridView) findViewById(R.id.grid_view_2);
        // grid2.setAdapter(adapter);
    }

    private void parseDate() {
        if (packet == null || detatilList == null) {
            return;
        }

        if (packet.getResultCode() == 0) {
            RecordDetailBean detailBean = new RecordDetailBean();
            JSONObject body = packet.optJSONObject("body");
            JSONObject range = body.optJSONObject("range");
            suggerHigh = new HashMap<String, RecordDetailModels>();
            suggerLow = new HashMap<String, RecordDetailModels>();
            detailBean.setHighEmpty((float) range.optDouble("highEmpty"));
            detailBean.setLowEmpty((float) range.optDouble("lowEmpty"));
            detailBean.setHighFull((float) range.optDouble("highFull"));
            detailBean.setLowFull((float) range.optDouble("lowFull"));
            detailBean.setSugerValue((float) body.optJSONObject("bean").optDouble("value"));
            detailBean.setParamCode(body.optJSONObject("bean").optString("paramCode"));
            detailBean.setParamLogId(body.optJSONObject("bean").optLong("paramLogId"));
            detailBean.setRecordTime(body.optJSONObject("bean").optString("recordTime"));
            String remindStr = body.optString("remind");
            if (!TextUtils.isEmpty(remindStr)) {
                tv_remind.setText(remindStr);
                tv_remind.setVisibility(View.VISIBLE);
            } else {
                tv_remind.setVisibility(View.GONE);
            }
            bean = detailBean;

        } else {
            ComveeHttpErrorControl.parseError(getActivity(), packet);
        }
        if (detatilList.getResultCode() == 0) {
            JSONObject body = detatilList.optJSONObject("body");
            bean.setSuggerImage(body.optJSONObject("suggerRegular").optString("suggerImage"));
            bean.setSuggerText(body.optJSONObject("suggerRegular").optString("suggerText"));
            bean.setSuggerTitle(body.optJSONObject("suggerRegular").optString("suggerTitle"));
            JSONArray high = body.optJSONObject("suggerHigh").optJSONArray("optionsValue");
            JSONArray low = body.optJSONObject("suggerLow").optJSONArray("optionsValue");
            parseArray(high, suggerHigh, true);
            parseArray(low, suggerLow, false);
        } else {
            ComveeHttpErrorControl.parseError(getActivity(), packet);
        }
    }

    private void parseArray(JSONArray aJsonArray, HashMap<String, RecordDetailModels> sugger, boolean addTimeCode) {

        if (addTimeCode) {
            times = new String[aJsonArray.length()];
            codes = new String[aJsonArray.length()];
        }
        for (int i = 0; i < aJsonArray.length(); i++) {
            RecordDetailModels models = new RecordDetailModels();
            JSONObject value = aJsonArray.optJSONObject(i);
            String code = value.optString("code");

            models.setCode(code);
            models.setMeaning(value.optString("meaning"));
            models.setText(value.optString("textTitle"));
            models.setHint(value.optString("hint"));
            models.setB(value.optString("b"));
            JSONArray array = value.optJSONArray("options");
            if (addTimeCode) {
                times[i] = models.getMeaning();
                codes[i] = models.getCode();
            }
            ArrayList<RecordDetailItem> arrayList = new ArrayList<RecordDetailItem>();
            for (int j = 0; j < array.length(); j++) {
                JSONObject jsonObject = array.optJSONObject(j);
                RecordDetailItem item = new RecordDetailItem();
                item.setOption(jsonObject.optString("option"));
                item.setPhoto(jsonObject.optString("photo"));
                item.setText(jsonObject.optString("text"));
                item.setViewWidth(ViewWidth);
                arrayList.add(item);
            }
            models.setOptions(arrayList);
            sugger.put(code, models);
        }
    }

    /**
     * 更新界面显示
     */
    private void upView() {
        if (bean != null) {
            suggerType = checkValue(bean.getSugerValue());
            setSuggest(suggerType);
            setHeanValueAndSelectList(suggerType);

        }
    }

    private void setHeanValueAndSelectList(int type) {
        String font = null;
        String value = null;
        int num = -1;
        switch (type) {
            case 1:
                num = suggerLow.get(bean.getParamCode()).getOptions().size();
                font = "<font color=#005ebe>" + Float.valueOf(String.format("%.1f", bean.getSugerValue())) + "</font>";
                value = suggerLow.get(bean.getParamCode()).getText().replace("?", font);
                models = suggerLow.get(bean.getParamCode()).getOptions();
                tv_title.setVisibility(View.GONE);
                tv_label.setVisibility(View.VISIBLE);
                grid1.setVisibility(View.VISIBLE);
                lin_normal.setVisibility(View.GONE);
                break;
            case 3:
                num = -1;
                font = "<font color=#4eb800>" + Float.valueOf(String.format("%.1f", bean.getSugerValue())) + "</font>";
                value = bean.getSuggerTitle().replace("?", font);
                ImageLoaderUtil.getInstance(mContext).displayImage(bean.getSuggerImage(), img_normal, ImageLoaderUtil.default_options);
                tv_normal.setText(bean.getSuggerText());
                tv_title.setText(Html.fromHtml(value));
                tv_title.setVisibility(View.VISIBLE);
                tv_label.setVisibility(View.GONE);
                grid1.setVisibility(View.GONE);
                lin_normal.setVisibility(View.VISIBLE);
                break;
            case 5:
                num = suggerHigh.get(bean.getParamCode()).getOptions().size();
                font = "<font color=#ff3b30>" + Float.valueOf(String.format("%.1f", bean.getSugerValue())) + "</font>";
                value = suggerHigh.get(bean.getParamCode()).getText().replace("?", font);
                models = suggerHigh.get(bean.getParamCode()).getOptions();
                tv_title.setVisibility(View.GONE);
                tv_label.setVisibility(View.VISIBLE);
                grid1.setVisibility(View.VISIBLE);
                lin_normal.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        if (num != -1) {
            switch (num % 3) {
                case 0:
                    img_top_3.setVisibility(View.VISIBLE);
                    img_top_2.setVisibility(View.INVISIBLE);
                    img_top_1.setVisibility(View.INVISIBLE);
                    break;
                case 1:
                    img_top_1.setVisibility(View.VISIBLE);
                    img_top_2.setVisibility(View.INVISIBLE);
                    img_top_3.setVisibility(View.INVISIBLE);
                    break;
                case 2:
                    img_top_2.setVisibility(View.VISIBLE);
                    img_top_1.setVisibility(View.INVISIBLE);
                    img_top_3.setVisibility(View.INVISIBLE);
                    break;
                default:
                    break;
            }
        }
        adapter.setArrayList(models);
        adapter.notifyDataSetChanged();
        setGoneLine();
        tv_select_time.setText(suggerHigh.get(bean.getParamCode()).getMeaning());
        tv_label.setText(Html.fromHtml(value));
    }

    // 隐藏gridview多余的线
    private void setGoneLine() {
        TextView tv_remove_bg = (TextView) findViewById(R.id.tv_remove_bg);
        tv_remove_bg.setVisibility(View.VISIBLE);
        int bg_width = 0;
        int bg_height = 0;
        switch (models.size() % 3) {
            case 0:
                tv_remove_bg.setVisibility(View.GONE);
                break;
            case 1:
                bg_width = ViewWidth * 2 - 1;
                bg_height = ViewWidth * 4 / 3 - 2;
                break;
            case 2:
                bg_width = ViewWidth - 1;
                bg_height = ViewWidth * 4 / 3 - 2;
                break;
            default:
                break;
        }
        tv_remove_bg.setWidth(bg_width);
        tv_remove_bg.setHeight(bg_height);
    }

    /**
     * 设置其他原因的输入框是否显示
     *
     * @param type
     * @param isShow
     */
    private void setRemarkView(int type, boolean isShow) {
        if (type != 3) {
            // String remark =
            // ConfigParams.getRecordRemark(getApplicationContext());
            if (isShow) {
                rel_remark.setVisibility(View.VISIBLE);
                tv_remark.requestFocus();
            } else {
                rel_remark.setVisibility(View.GONE);
            }
        } else {
            rel_remark.setVisibility(View.GONE);
        }
    }

    /**
     * 设置温馨提醒的文案
     *
     * @param type
     */
    private void setSuggest(int type) {
        tv_suggest.setText("");
        ImageGetter imageGetter = new ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                int id = Integer.parseInt(source);

                // 根据id从资源文件中获取图片对象
                Drawable d = getResources().getDrawable(id);
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                return d;
            }
        };
        // 在textview中插入图片
        // tv_suggest.append(Html.fromHtml("<img src='" +
        // R.drawable.suggest_icon + "'/>", imageGetter, null));
        // tv_suggest.append(Html.fromHtml("<b> " +
        // suggerLow.get(bean.getParamCode()).getB() + "</b>" + ","
        // + suggerLow.get(bean.getParamCode()).getHint()));
        tv_suggest.append(Html.fromHtml(AppUtil.ToDBC(suggerLow.get(bean.getParamCode()).getHint())));
        if (type == 1) {
            lin_suggest_group.setVisibility(View.VISIBLE);
        } else {
            lin_suggest_group.setVisibility(View.GONE);
        }
    }

    /**
     * 根据时间段判断血糖的状态 1 偏低 3 正常 5 偏高
     *
     * @param num
     * @return
     */
    private int checkValue(float num) {
        float minLimit = -1.0f;
        float maxLimit = -1.0f;
        // int index =
        // ConfigParams.getCurBloodTimeIndex(tvTime.getText().toString());
        if (bean.getParamCode().contains("beforeBreakfast")) {
            // strLimit = lowEmpty + "-" + highEmpty;
            minLimit = bean.getLowEmpty();
            maxLimit = bean.getHighEmpty();
        } else {
            // strLimit = lowFull + "-" + highFull;
            minLimit = bean.getLowFull();
            maxLimit = bean.getHighFull();
        }
        if (minLimit == -1.0f || maxLimit == -1.0f) {
            return -1;
        }
        if (num >= minLimit && num <= maxLimit) {
            return 3;
        }
        if (num < minLimit) {
            return 1;
        }
        if (num > maxLimit) {
            return 5;
        }
        return -1;
    }

    /**
     * 根据时间段显示不同的选项
     */
    private void showChooseSugarTimeDialog() {
        if (times != null && codes != null && times.length == codes.length) {
            SingleInputItemWindow dialog = new SingleInputItemWindow(getApplicationContext(), times, "选择时间段", -1, 0, 0);
            dialog.setOnItemClick(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    bean.setParamCode(codes[which]);
                    upView();
                }
            });
            dialog.setOutTouchCancel(true);
            dialog.showAtLocation(mBarView, Gravity.CENTER, 0, 0);
        } else {
            showToast("系统异常");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        RecordDetailItem model = (RecordDetailItem) arg0.getAdapter().getItem(arg2);
        model.setSelect(!model.isSelect());
        adapter.notifyDataSetChanged();
        // if (model.getOption().equals("O")) {
        // setRemarkView(suggerType, model.isSelect());
        // }
        upView();
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.lin_select_time:
                showChooseSugarTimeDialog();
                break;
            case R.id.im_cancle:
                FragmentMrg.toBack(getActivity());
                break;
            case R.id.btn_submit:
                requestSubmit();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    /**
     * 数据入库
     */
    private void requestSubmit() {
        mBarView.setVisibility(View.VISIBLE);
        showProgressDialog(getString(R.string.msg_loading));
//		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.MODIFY_MEMBER_SUFFER_PARAM);
//		http.setPostValueForKey("paramLogId", bean.getParamLogId() + "");
//		http.setPostValueForKey("value", bean.getSugerValue() + "");
//		http.setPostValueForKey("paramCode", bean.getParamCode());
//		http.setOnHttpListener(1, this);
//		http.setPostValueForKey("option", getCoadString());
//		http.setPostValueForKey("recordTime", bean.getRecordTime());
//		http.setPostValueForKey("memo", tv_remark.getText().toString().trim());
//		http.startAsynchronous();

        ObjectLoader<HealthResultInfo> loader = new ObjectLoader<HealthResultInfo>();
        loader.putPostValue("paramLogId", bean.getParamLogId() + "");
        loader.putPostValue("paramLogId", bean.getParamLogId() + "");
        loader.putPostValue("value", bean.getSugerValue() + "");
        loader.putPostValue("paramCode", bean.getParamCode());
        loader.putPostValue("option", getCoadString());
        loader.putPostValue("recordTime", bean.getRecordTime());
        loader.putPostValue("memo", tv_remark.getText().toString().trim());

        loader.loadBodyObject(HealthResultInfo.class, ConfigUrlMrg.MODIFY_MEMBER_SUFFER_PARAM, loader.new CallBack() {
            @Override
            public void onBodyObjectSuccess(boolean isFromCache, HealthResultInfo obj) {
                super.onBodyObjectSuccess(isFromCache, obj);
                cancelProgressDialog();
                if (fromWhere == 2) {
                    mBarView.setVisibility(View.GONE);
                }
                HealthRecordRusultFragment.toFragment(getActivity(), obj, fromWhere);

            }

            @Override
            public boolean onFail(int code) {
                cancelProgressDialog();
                return super.onFail(code);

            }
        });

    }

    @Override
    public void onSussece(int what, byte[] b, boolean fromCache) {
//
//        cancelProgressDialog();
//        switch (what) {
//            case 1:
//                try {
//                    ComveePacket packet = ComveePacket.fromJsonString(b);
//                    if (packet.getResultCode() == 0) {
//                        toFragment(HealthRecordRusultFragment.newInstance(packet, fromWhere), true, true);
//                    } else {
//                        ComveeHttpErrorControl.parseError(getActivity(), packet);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                break;
//
//            default:
//                break;
//        }
    }

    /**
     * 拼接用户选中的选项
     *
     * @return
     */
    private String getCoadString() {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < models.size(); i++) {
            if (models.get(i).isSelect()) {
                buffer.append(models.get(i).getOption());
            }
        }
        return buffer.toString();
    }

    @Override
    public void onFialed(int what, int errorCode) {

    }

    /**
     * 获取用户手机的的宽度，动态适配每个选项的宽度
     */
    private void getViewWidth() {
        // DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        // getActivity().getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        // final int W = mDisplayMetrics.widthPixels;
        float W = UITool.getDisplayWidth(getActivity());
        ViewWidth = (int) ((W - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getActivity().getResources().getDisplayMetrics())) / 3);

    }

    @Override
    public boolean onBackPress() {
        if (fromWhere == 3) {
            FragmentMrg.popBackToFragment(getActivity(), RecordDietIndexFragment.class, null);
        }
        return super.onBackPress();
    }
}
