package com.comvee.tnb.ui.ask;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.comvee.frame.BaseFragmentActivity;
import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.model.NewAskModel;
import com.comvee.tnb.model.ServerListModel;
import com.comvee.tnb.ui.more.MoreFragment;
import com.comvee.tnb.ui.privatedoctor.DoctorServerList;
import com.comvee.tnb.widget.Voice.ParamsConfig;
import com.comvee.tnb.widget.Voice.PlayImageView;
import com.comvee.tnb.widget.Voice.VoiceInfo;
import com.comvee.tool.AppUtil;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.tool.UserMrg;
import com.comvee.tool.ViewHolder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;

/**
 *医生首页
 */
public class AskContentAdapter extends BaseAdapter {

    private final int TYPE_ONE = 0;
    private final int TYPE_TWO = 1;
    private final int TYPE_THREE = 2;
    private final int TYPE_FOUR = 3;
    private final int TYPE_FIVE = 4;
    private Activity context;
    private LayoutInflater inflater;
    // private ArrayList<ItemData> list = new
    // ArrayList<AskContentAdapter.ItemData>();
    private NewAskModel item;
    private ToPatientPersonal personal;
    private anewSendMsgListener listener;
    private ArrayList<NewAskModel> mList;
    public AskContentAdapter(Activity cxt, ToPatientPersonal personal, anewSendMsgListener listener, ArrayList<NewAskModel> mList) {
        this.inflater = LayoutInflater.from(cxt);
        this.context = cxt;
        this.personal = personal;
        this.listener = listener;
        this.mList = mList;
    }

    /**
     * 判断类型加载不同item布局
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        int viewType = mList.get(position).getMsgType();
        if (viewType == 1 || viewType == 2 || viewType == 3 || viewType == 11) {
            return TYPE_ONE;
        } else if (viewType == 5 || viewType == 7 || viewType == 6||viewType==22) {
            return TYPE_TWO;
        } else if (viewType == 19 || viewType == 8
                || (viewType == 12 && (mList.get(position).getList() == null || mList.get(position).getList().size() == 0))) {
            return TYPE_THREE;
        } else if (viewType == 4 || viewType == 10) {
            return TYPE_FOUR;
        } else if (viewType == 12 || viewType == 13) {
            return TYPE_FIVE;
        } else {
            return -1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 6;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        // return list == null ? null : list.get(position).question;
        return mList == null ? null : mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // private void setTiemText(TextView tv, int postion) {
    // if (list == null) {
    // return;
    // }
    // tv.setVisibility(View.VISIBLE);
    // if (postion == 0) {
    // tv.setText(AppUtil.formatTime(item. getInsertDt()));
    // } else {
    // try {
    // long time = Long.parseLong(list.get(postion). getInsertDt());
    // long time1 = Long.parseLong(list.get(postion - 1). getInsertDt());
    // if ((time - time1) > (5 * 60 * 1000)) {
    // tv.setText(AppUtil.formatTime(item. getInsertDt()));
    // } else {
    // tv.setVisibility(View.GONE);
    // }
    // } catch (Exception e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }
    // }

    // 1 血糖异常 2 血压异常 3 BMI异常 4 咨询 5 任务推荐 6 创建随访 7 完成随访 8 创建日程提醒 9 请求确认医患关系 10
    // 通过医患关系 22 化验单解读通知
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        item = mList.get(position);
        int msgType = item.getMsgType();
        int viewTypeNum = getItemViewType(position);
        ViewHolder holder = null;
        if (viewTypeNum == -1) {
            // 不兼容的消息类型展示
            holder = ViewHolder.getViewHolder(context, convertView, parent, R.layout.ask_consult_list_item_two);
            TextView noti_title = holder.get(R.id.ask_noti_title);
            TextView noti_content = holder.get(R.id.ask_noti_content);
            LinearLayout ask_lin_group = holder.get(R.id.ask_lin_group);
            TextView tv_insert_time = holder.get(R.id.tv_insertDt_value);
            ask_lin_group.setVisibility(View.GONE);
            noti_title.setGravity(Gravity.LEFT);
            noti_title.setText("您当前的版本不兼容此消息类型！");
            noti_content.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
            noti_content.setTextColor(Color.parseColor("#0066ff"));
            noti_content.setText("立即升级");
            tv_insert_time.setVisibility(View.VISIBLE);
            tv_insert_time.setText(AppUtil.formatTime(item.getInsertDt()));
            holder.mConvertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    MoreFragment.upData(context, true);
                }
            });
            return holder.mConvertView;
        } else if (viewTypeNum == TYPE_FOUR) {
            holder = ViewHolder.getViewHolder(context, convertView, parent, R.layout.ask_content_item);

        } else if (viewTypeNum == TYPE_FIVE) {
            holder = ViewHolder.getViewHolder(context, convertView, parent, R.layout.ask_consult_list_item_two);
            holder.get(R.id.tv_insertDt_value).setVisibility(View.VISIBLE);
        } else {
            holder = ViewHolder.getViewHolder(context, convertView, parent, R.layout.ask_consult_list_item);
            holder.get(R.id.tv_insertDt_value).setVisibility(View.VISIBLE);
            holder.get(R.id.tv_deal_state).setVisibility(View.GONE);
            if (viewTypeNum == TYPE_TWO) {
                holder.get(R.id.ask_noti_date).setVisibility(View.GONE);
                holder.get(R.id.ask_noti_time).setVisibility(View.GONE);
            } else if (viewTypeNum == TYPE_THREE) {
                holder.get(R.id.ask_noti_seprator).setVisibility(View.GONE);
                holder.get(R.id.ask_detail).setVisibility(View.GONE);
            }
        }
        if (holder.get(R.id.ask_noti_time) != null) {
            TextView textView = holder.get(R.id.ask_noti_time);
            textView.getPaint().setFlags(0);// 下划线
            textView.setTextColor(Color.parseColor("#666666"));
            holder.get(R.id.ask_noti_time).setOnClickListener(null);
        }
        switch (msgType) {
            // 血糖异常
            case 1:
                ((TextView) holder.get(R.id.tv_insertDt_value)).setText(AppUtil.formatTime(item.getInsertDt()));
                ((TextView) holder.get(R.id.ask_noti_title)).setText(item.getTitle());
                ((TextView) holder.get(R.id.ask_noti_content)).setText("记录时间： " + item.getRecordTime());
                ((TextView) holder.get(R.id.ask_noti_date)).setText("血糖值：" + item.getBloodglucoseValue() + item.getUnit());
                ((TextView) holder.get(R.id.ask_noti_time)).setText(Html.fromHtml("血糖水平：" + returnLevel(item.getParamLevel())));
                break;
            // 血压异常
            case 2:
                ((TextView) holder.get(R.id.tv_insertDt_value)).setText(AppUtil.formatTime(item.getInsertDt()));
                ((TextView) holder.get(R.id.ask_noti_content)).setText("记录时间： " + item.getRecordTime());
                ((TextView) holder.get(R.id.ask_noti_title)).setText(item.getTitle());
                ((TextView) holder.get(R.id.ask_noti_date)).setText("血压值: " + item.getBloodpressuresystolic() + "/" + item.getBloodpressurediastolic()
                        + item.getUnit());
                ((TextView) holder.get(R.id.ask_noti_time)).setText(Html.fromHtml("血压水平： " + returnLevel(item.getParamLevel())));
                break;
            // bmi异常
            case 3:
                ((TextView) holder.get(R.id.tv_insertDt_value)).setText(AppUtil.formatTime(item.getInsertDt()));
                ((TextView) holder.get(R.id.ask_noti_title)).setText(item.getTitle());
                ((TextView) holder.get(R.id.ask_noti_content)).setText("记录时间： " + item.getRecordTime());
                DecimalFormat doubleFormat = new DecimalFormat(".#");
                String bmiValue = doubleFormat.format(item.getBmiValue());
                ((TextView) holder.get(R.id.ask_noti_date)).setText("BMI值： " + bmiValue);
                ((TextView) holder.get(R.id.ask_noti_time)).setText(Html.fromHtml("BMI水平： " + returnLevel(item.getParamLevel())));
                break;
            // 日程提醒
            case 8:
                ((TextView) holder.get(R.id.tv_insertDt_value)).setText(AppUtil.formatTime(item.getInsertDt()));
                ((TextView) holder.get(R.id.ask_noti_title)).setText(item.getTitle());
                ((TextView) holder.get(R.id.ask_noti_content)).setText("提醒事项： " + item.getRemindTitle());
                ((TextView) holder.get(R.id.ask_noti_date)).setText("日期： " + item.getDate() + "  " + item.getTime());
                if (item.getRemark() != null && !"".equals(item.getRemark())) {
                    ((TextView) holder.get(R.id.ask_noti_time)).setText("备注： " + item.getRemark());
                } else {
                    ((TextView) holder.get(R.id.ask_noti_time)).setText("备注： 无");
                }
                break;
            //
            case 5:
                ((TextView) holder.get(R.id.tv_insertDt_value)).setText(AppUtil.formatTime(item.getInsertDt()));
                ((TextView) holder.get(R.id.ask_noti_title)).setText(item.getTitle());
                ((TextView) holder.get(R.id.ask_noti_content)).setText(item.getContent());
                break;
            case 6:
                // 新建随访
                ((TextView) holder.get(R.id.tv_insertDt_value)).setText(AppUtil.formatTime(item.getInsertDt()));
                ((TextView) holder.get(R.id.ask_noti_title)).setText(item.getTitle());
                ((TextView) holder.get(R.id.ask_noti_content)).setText(item.getContent());
                ((ImageView) holder.get(R.id.tv_deal_state)).setBackgroundResource(R.drawable.follow_1);
                ((ImageView) holder.get(R.id.tv_deal_state)).setVisibility(View.VISIBLE);
                break;
            // 完成随访
            case 7:
                ((TextView) holder.get(R.id.tv_insertDt_value)).setText(AppUtil.formatTime(item.getInsertDt()));
                ((TextView) holder.get(R.id.ask_noti_title)).setText(item.getTitle());
                ((TextView) holder.get(R.id.ask_noti_content)).setText(item.getContent());
                ((ImageView) holder.get(R.id.tv_deal_state)).setVisibility(View.VISIBLE);
                if (item.getIsDispose() == 0) {
                    ((ImageView) holder.get(R.id.tv_deal_state)).setBackgroundResource(R.drawable.follow_2);
                } else {
                    ((ImageView) holder.get(R.id.tv_deal_state)).setBackgroundResource(R.drawable.follow_3);
                }
                break;
            // 图文咨询
            case 10:
            case 4:
                // setTiemText(viewHolder.tv_insert_time, position);
                ((TextView) holder.get(R.id.tv_insertDt_value)).setText(AppUtil.formatTime(item.getInsertDt()));
                holder.get(R.id.alpha_layer).setVisibility(View.GONE);
                if (item.getOwnerType() == 1) {
                    // viewHolder.tv_username.setVisibility(View.VISIBLE);
                    // iewHolder.tv_username.setText(UserMrg.DEFAULT_MEMBER.name);
                    if (TextUtils.isEmpty(item.getAttachList()) || "[]".equals(item.getAttachList())) {
                        item.setAttachType("2");
                        isPatient(holder, 2, item.getMessageState(), position);
                    } else {
                        setAttachContent(item.getAttachList());
                        isPatient(holder, Integer.valueOf(item.getAttachType()), item.getMessageState(), position);
                    }

                } else {
                    // viewHolder.tv_username.setVisibility(View.VISIBLE);
                    // viewHolder.tv_username.setText(item. getDoctorName());
                    if (TextUtils.isEmpty(item.getAttachList()) || "[]".equals(item.getAttachList())) {
                        item.setAttachType("2");
                        isDoc(holder, 2, position);
                    } else {
                        setAttachContent(item.getAttachList());
                        isDoc(holder, Integer.valueOf(item.getAttachType()), position);
                    }
                }
                break;
            case 9:
                // viewHolder.tv_username.setVisibility(View.GONE);
                // String insertTimeAdd = item. getInsertDt();
                // String formatTimeAdd = Util.getStringDateFormat(insertTimeAdd,
                // ConfigParams.TIME_LONG, ConfigParams.TIME_LONG_NO_SECOND);
                // if (!Util.isTheDayBeforeToday(formatTimeAdd)) {
                // formatTimeAdd = Util.getStringDateFormat(insertTimeAdd,
                // ConfigParams.TIME_LONG, ConfigParams.TIME_SHORT_HM);
                // }
                // viewHolder.tv_insert_time.setText(formatTimeAdd);
                // isDoc(viewHolder, 2);
                // viewHolder.tv_insert_time.setText(item. getInsertDt());
                // viewHolder.tv_content.setText(item. getMsgContent());
                break;
            // 预约详情
            case 11:
                ((TextView) holder.get(R.id.tv_insertDt_value)).setText(AppUtil.formatTime(item.getInsertDt()));
                ((TextView) holder.get(R.id.ask_noti_title)).setText(item.getTitle());
                ((TextView) holder.get(R.id.ask_noti_content)).setText("预约医生: " + item.getDoctorName());
                ((TextView) holder.get(R.id.ask_noti_date)).setText("日期： " + item.getDate());
                ((TextView) holder.get(R.id.ask_noti_time)).setText("时间： " + item.getStartTime() + "-" + item.getEndTime());
                ((ImageView) holder.get(R.id.tv_deal_state)).setVisibility(View.VISIBLE);
                switch (item.getStatus()) {

                    case 0:
                        if (AskTellDetailFragment.isEnabled(item.getDate() + " " + item.getEndTime(), 1)) {
                            // viewHolder.tv_deal_state.setText("已过期");
                            ((ImageView) holder.get(R.id.tv_deal_state)).setBackgroundResource(R.drawable.yuyue_guoqi);

                        } else {
                            // viewHolder.tv_deal_state.setText("待回访");
                            ((ImageView) holder.get(R.id.tv_deal_state)).setBackgroundResource(R.drawable.yuyue_dendai);
                        }
                        break;
                    case 1:
                        // viewHolder.tv_deal_state.setText("已完成");
                        ((ImageView) holder.get(R.id.tv_deal_state)).setBackgroundResource(R.drawable.yuyue_wanchen);
                        break;
                    case 2:
                        // viewHolder.tv_deal_state.setText("已过期");
                        ((ImageView) holder.get(R.id.tv_deal_state)).setBackgroundResource(R.drawable.yuyue_guoqi);
                        break;
                    default:
                        break;
                }
                ;
                // viewHolder.noti_state_icon
                break;
            // 服务过期
            case 12:
                ((TextView) holder.get(R.id.tv_insertDt_value)).setText(AppUtil.formatTime(item.getInsertDt()));
                ((TextView) holder.get(R.id.ask_noti_title)).setText(item.getMsgContent());
                ((TextView) holder.get(R.id.ask_noti_content)).setText(item.getContent());
                if (item.getList() != null && item.getList().size() > 0) {
                    holder.get(R.id.ask_lin_group).setVisibility(View.VISIBLE);
                    if (holder.get(R.id.ask_lin_group).getTag() == null || (Boolean) holder.get(R.id.ask_lin_group).getTag()) {
                        for (int i = 0; i < item.getList().size(); i++) {
                            createView(((LinearLayout) holder.get(R.id.ask_lin_group)), item.getList().get(i), i == item.getList().size() - 1 ? false
                                    : true);
                        }
                        holder.get(R.id.ask_lin_group).setTag(false);
                    }
                } else {
                    ((TextView) holder.get(R.id.ask_noti_title)).setText(item.getTitle());
                    ((TextView) holder.get(R.id.ask_noti_content)).setText(item.getContent());
                    ((TextView) holder.get(R.id.ask_noti_time)).getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
                    ((TextView) holder.get(R.id.ask_noti_time)).setTextColor(Color.parseColor("#0066ff"));
                    ((TextView) holder.get(R.id.ask_noti_time)).setText("立即购买服务");
                    ((TextView) holder.get(R.id.ask_noti_date)).setText("想继续向" + item.getDoctorName() + "医生咨询？");
                    ((TextView) holder.get(R.id.ask_noti_time)).setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            if (personal != null) {
                                personal.toPatientPersonal(null);
                            }
                        }
                    });
                }
                break;
            // 纯消息展示
            case 13:
                ((TextView) holder.get(R.id.tv_insertDt_value)).setText(AppUtil.formatTime(item.getInsertDt()));
                ((TextView) holder.get(R.id.ask_noti_title)).setText(item.getTitle());
                ((TextView) holder.get(R.id.ask_noti_content)).setText(item.getContent());
                holder.get(R.id.ask_lin_group).setVisibility(View.GONE);
                break;
            case 19:
                ((TextView) holder.get(R.id.tv_insertDt_value)).setText(AppUtil.formatTime(item.getInsertDt()));
                ((TextView) holder.get(R.id.ask_noti_title)).setText(item.getTitle());
                ((TextView) holder.get(R.id.ask_noti_date)).setText("有效期： " + item.getStartTime() + "至" + item.getEndTime());
                ((TextView) holder.get(R.id.ask_noti_content)).setVisibility(View.GONE);
                if (item.getRemark() != null && !"".equals(item.getRemark())) {
                    ((TextView) holder.get(R.id.ask_noti_time)).setText("套餐内容： " + item.getRemark());
                } else {
                    ((TextView) holder.get(R.id.ask_noti_time)).setText("套餐内容：有效期内不限咨询次数");
                }
                break;
            case 22:
                ((TextView) holder.get(R.id.tv_insertDt_value)).setText(AppUtil.formatTime(item.getInsertDt()));
                ((TextView) holder.get(R.id.ask_noti_title)).setText(item.getTitle());
                ((TextView) holder.get(R.id.ask_noti_content)).setText("解读时间: "+item.getInsertDt());
                break;
        }
        // viewHolder.noti_time.setText(Html.fromHtml("血压水平：<font color=red>"+item.question.getAnswerUserName()+"</font>"));
        // viewHolder.tv_insertDt_left.setText(item.question.getInsertDt());
        // viewHolder.tv_insertDt_right.setText(item.question.getInsertDt());
        // viewHolder.tv_username.setText(item.question.getAnswerUserName());
        // switch (item.question.getAnswerUserType()) {
        // case 1:
        // isPatient(viewHolder, item.question.getContinueType());
        // break;
        // case 2:
        // isDoc(viewHolder, item.question.getContinueType());
        // break;
        // }
        return holder.mConvertView;
    }

    private void createView(LinearLayout layout, final ServerListModel model, boolean add) {
        View.inflate(context, R.layout.item_index_group_line, layout);
        View view = inflater.inflate(R.layout.ask_listitem_item, null);
        ImageLoaderUtil.getInstance(context).displayImage(model.getImage(), (ImageView) view.findViewById(R.id.iv_icon),
                ImageLoaderUtil.default_options);
        ((TextView) view.findViewById(R.id.tv_content)).setText(model.getContent());
        layout.addView(view);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (personal != null) {
                    personal.toPatientPersonal(model.getUrl());
                }
            }
        });
    }

    // 根据传入的数值来判断遂平是偏高还是偏低
    private String returnLevel(int level) {
        if (level == 1) {
            return "<font color=" + TNBApplication.getInstance().getResources().getColor(R.color.ask_sugar_red) + ">偏高</font>";
        } else {
            return "<font color=" + TNBApplication.getInstance().getResources().getColor(R.color.ask_sugar_blue) + ">偏低</font>";
        }
    }

    // 解析attachlist的JSONArray
    private void setAttachContent(String jsonArr) {
        try {
            JSONArray arr = new JSONArray(jsonArr);
            JSONObject obj = arr.optJSONObject(0);
            item.setAttachType(obj.optString("attachType"));
            if (!TextUtils.isEmpty(obj.optString("attachUrl"))) {
                item.setAttachUrl(obj.optString("attachUrl"));
            }
            item.setVoiceMins(obj.optInt("voiceMins"));// 语音长度
            if (item.getAttachType().equals("1")) {// 类型是语音时
                item.voice = new VoiceInfo();
                item.voice.setAttUrl(item.getLocalUrl());
                if (TextUtils.isEmpty(item.getLocalUrl())) {
                    item.voice.setAttUrl(item.getAttachUrl());
                }
                item.voice.setPlaying(false);
                item.voice.setVoiceLength(item.getVoiceMins() + "");
            }
            // }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void isDoc(ViewHolder viewHolder, int contenttype, int pos) {
        ((LinearLayout) viewHolder.get(R.id.tem_layout)).setGravity(Gravity.LEFT);
        ((ImageView) viewHolder.get(R.id.iv_doc_photo)).setVisibility(View.INVISIBLE);
        ((ImageView) viewHolder.get(R.id.iv_pat_photo)).setVisibility(View.VISIBLE);
        ((ImageView) viewHolder.get(R.id.iv_message_state)).setVisibility(View.GONE);
        viewHolder.get(R.id.rl_content_view).setVisibility(View.VISIBLE);
        viewHolder.get(R.id.rl_content_view).setBackgroundResource(R.drawable.ask_layout_left);
        ((TextView) viewHolder.get(R.id.tv_insertDt_value)).setVisibility(View.VISIBLE);
        ((ProgressBar) viewHolder.get(R.id.progressbar)).setVisibility(View.GONE);
        ((ProgressBar) viewHolder.get(R.id.progressbar)).clearAnimation();
        // viewHolder.tv_username.setVisibility(View.VISIBLE);
        ((TextView) viewHolder.get(R.id.tv_content)).setTextColor(Color.rgb(64, 64, 64));
        ((TextView) viewHolder.get(R.id.tv_voicetime_left)).setTextColor(Color.rgb(64, 64, 64));
        ((TextView) viewHolder.get(R.id.tv_voicetime_right)).setTextColor(Color.rgb(64, 64, 64));
        isContentType(viewHolder, contenttype, 2, pos);
        ImageLoaderUtil.getInstance(context).displayImage(item.getHeadImageUrl(), ((ImageView) viewHolder.get(R.id.iv_pat_photo)),
                ImageLoaderUtil.doc_options);
        ((ImageView) viewHolder.get(R.id.iv_pat_photo)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                DoctorServerList.toFragment((BaseFragmentActivity) context, item.getDoctorId() + "");
            }
        });
    }

    private void isPatient(final ViewHolder viewHolder, int contenttype, int messageState, final int position) {
        ((LinearLayout) viewHolder.get(R.id.tem_layout)).setGravity(Gravity.RIGHT);
        ((ImageView) viewHolder.get(R.id.iv_doc_photo)).setVisibility(View.VISIBLE);
        ((ImageView) viewHolder.get(R.id.iv_pat_photo)).setVisibility(View.INVISIBLE);
        viewHolder.get(R.id.rl_content_view).setVisibility(View.VISIBLE);
        viewHolder.get(R.id.rl_content_view).setBackgroundResource(R.drawable.ask_layout_right);
        ((TextView) viewHolder.get(R.id.tv_insertDt_value)).setVisibility(View.VISIBLE);
        ((TextView) viewHolder.get(R.id.tv_content)).setTextColor(Color.rgb(255, 255, 255));
        ((TextView) viewHolder.get(R.id.tv_voicetime_left)).setTextColor(Color.rgb(255, 255, 255));
        ((TextView) viewHolder.get(R.id.tv_voicetime_right)).setTextColor(Color.rgb(255, 255, 255));
        isContentType(viewHolder, contenttype, 1, position);
        ((ImageView) viewHolder.get(R.id.iv_message_state)).setVisibility(View.GONE);
        ((ImageView) viewHolder.get(R.id.iv_message_state)).setOnClickListener(null);
        ((ProgressBar) viewHolder.get(R.id.progressbar)).clearAnimation();
        ((ProgressBar) viewHolder.get(R.id.progressbar)).setVisibility(View.GONE);
        switch (messageState) {
            case 1:// 发送中
                ((ImageView) viewHolder.get(R.id.iv_message_state)).setVisibility(View.GONE);
                ((ProgressBar) viewHolder.get(R.id.progressbar)).setVisibility(View.VISIBLE);
                break;
            case 3:// 发送失败
                // viewHolder.iv_message_state.setImageResource(R.drawable.message_fail);
                ((ImageView) viewHolder.get(R.id.iv_message_state)).setVisibility(View.VISIBLE);
                ((ImageView) viewHolder.get(R.id.iv_message_state)).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ImageView) viewHolder.get(R.id.iv_message_state)).setVisibility(View.GONE);
                        ((ProgressBar) viewHolder.get(R.id.progressbar)).setVisibility(View.VISIBLE);
                        if (listener != null) {
                            listener.onclick(mList.get(position));
                        }
                    }
                });
                break;
            default:
                ((ImageView) viewHolder.get(R.id.iv_message_state)).setVisibility(View.GONE);
                ((ProgressBar) viewHolder.get(R.id.progressbar)).setVisibility(View.GONE);
                break;
        }
        ImageLoaderUtil.getInstance(context).displayImage(UserMrg.DEFAULT_MEMBER.photo, ((ImageView) viewHolder.get(R.id.iv_doc_photo)),
                ImageLoaderUtil.user_options);

    }

    private void isContentType(ViewHolder viewHolder, int contenttype, int usertype, int pos) {
        switch (contenttype) {
            case 0:// 图片咨询
                isImage(viewHolder, usertype, pos);
                break;
            case 1:// 语音咨询
                isVoice(viewHolder, usertype, pos);
                break;
            case 2:// 文本咨询
                isContent(viewHolder);
                break;
            default:
                break;
            // case 1:
            // isContent(viewHolder);
            // break;
            // case 2:
            // isImage(viewHolder);
            // break;
            // case 3:
            // isVoice(viewHolder, usertype);
            // break;
        }
    }

    private void isContent(ViewHolder viewHolder) {
        ((TextView) viewHolder.get(R.id.tv_content)).setVisibility(View.VISIBLE);
        ((TextView) viewHolder.get(R.id.tv_voicetime_left)).setVisibility(View.GONE);
        ((TextView) viewHolder.get(R.id.tv_voicetime_right)).setVisibility(View.GONE);
        ((ImageView) viewHolder.get(R.id.iv_content)).setVisibility(View.GONE);
        ((PlayImageView) viewHolder.get(R.id.iv_voice_play)).setVisibility(View.GONE);
        // viewHolder.tv_content.setText(item.question.getAnswerCon());
        if (TextUtils.isEmpty(item.getContent())) {
            ((TextView) viewHolder.get(R.id.tv_content)).setText(AppUtil.ToDBC(item.getMsgContent()));
        } else {
            ((TextView) viewHolder.get(R.id.tv_content)).setText(AppUtil.ToDBC(item.getContent()));
        }
    }

    private void isImage(final ViewHolder viewHolder, int usertype, int position) {
        ((TextView) viewHolder.get(R.id.tv_content)).setVisibility(View.GONE);
        ((TextView) viewHolder.get(R.id.tv_voicetime_left)).setVisibility(View.GONE);
        ((TextView) viewHolder.get(R.id.tv_voicetime_right)).setVisibility(View.GONE);
        ((ImageView) viewHolder.get(R.id.iv_content)).setVisibility(View.VISIBLE);
        ((PlayImageView) viewHolder.get(R.id.iv_voice_play)).setVisibility(View.GONE);
        //
        viewHolder.get(R.id.rl_content_view).setBackgroundColor(0xf3f3f3);
        viewHolder.get(R.id.rl_content_view).setPadding(0, 0, 0, 0);
        viewHolder.get(R.id.alpha_layer).setVisibility(View.VISIBLE);
        switch (usertype) {
            case 1:
                viewHolder.get(R.id.alpha_layer).setBackgroundResource(R.drawable.iv_layer_right);
                break;
            case 2:
                viewHolder.get(R.id.alpha_layer).setBackgroundResource(R.drawable.iv_layer_left);
                break;
        }

        NewAskModel askModel = mList.get(position);
        int messageState = askModel.getMessageState();
        if (1 == messageState || 2 == messageState || 3 == messageState) {
            if (!TextUtils.isEmpty(askModel.getLocalUrl()) && new File(askModel.getLocalUrl()).exists()) {
                ImageLoaderUtil.getInstance(context).displayImage("file://" + askModel.getLocalUrl(), ((ImageView) viewHolder.get(R.id.iv_content)),
                        ImageLoaderUtil.default_options);
            } else {
                Toast.makeText(context, "找不到文件", Toast.LENGTH_SHORT).show();
                ImageLoaderUtil.getInstance(context).displayImage("file://" + askModel.getLocalUrl(), ((ImageView) viewHolder.get(R.id.iv_content)),
                        ImageLoaderUtil.default_options);
            }
        } else {
            if (!TextUtils.isEmpty(askModel.getLocalUrl()) && new File(askModel.getLocalUrl()).exists()) {
                ImageLoaderUtil.getInstance(context).displayImage("file://" + askModel.getLocalUrl(), ((ImageView) viewHolder.get(R.id.iv_content)),
                        ImageLoaderUtil.default_options);
            } else {
                ImageLoaderUtil.getInstance(context).displayImage(askModel.getAttachUrl(), ((ImageView) viewHolder.get(R.id.iv_content)),
                        ImageLoaderUtil.default_options);
            }
        }

    }

    private void isVoice(ViewHolder viewHolder, int usertype, int postion) {
        ((TextView) viewHolder.get(R.id.tv_content)).setVisibility(View.GONE);
        switch (usertype) {
            case 1:
                ((PlayImageView) viewHolder.get(R.id.iv_voice_play)).setIsLeft(false);
                ((TextView) viewHolder.get(R.id.tv_voicetime_left)).setVisibility(View.GONE);
                ((TextView) viewHolder.get(R.id.tv_voicetime_right)).setVisibility(View.VISIBLE);
                ((TextView) viewHolder.get(R.id.tv_voicetime_right)).setText(item.getVoiceMins() + "'");
                break;
            case 2:
                ((TextView) viewHolder.get(R.id.tv_voicetime_left)).setVisibility(View.VISIBLE);
                ((TextView) viewHolder.get(R.id.tv_voicetime_right)).setVisibility(View.GONE);
                ((PlayImageView) viewHolder.get(R.id.iv_voice_play)).setIsLeft(true);
                ((TextView) viewHolder.get(R.id.tv_voicetime_left)).setText(item.getVoiceMins() + "'");
                break;

        }
        ((PlayImageView) viewHolder.get(R.id.iv_voice_play)).setVisibility(View.VISIBLE);
        ((ImageView) viewHolder.get(R.id.iv_content)).setVisibility(View.GONE);
        if (item.voice != null) {
            if (item.voice.isPlaying()) {
                ((PlayImageView) viewHolder.get(R.id.iv_voice_play)).play();
            } else {
                ((PlayImageView) viewHolder.get(R.id.iv_voice_play)).stop();
            }
        }

    }

    public interface ToPatientPersonal {
        public void toPatientPersonal(String url);// 跳转类型 1 医生服务详情，2 web
    }

    public interface anewSendMsgListener {
        public void onclick(NewAskModel askModel);
    }

    class NewAskComparator implements Comparator<NewAskModel> {
        @Override
        public int compare(NewAskModel arg0, NewAskModel arg1) {
            return arg0.getInsertDt().compareTo(arg1.getInsertDt());
        }
    }
}
