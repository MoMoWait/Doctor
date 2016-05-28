package com.comvee.tnb.ui.member;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.comvee.tnb.DrawerMrg;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.CustomDatePickDialog;
import com.comvee.tnb.dialog.CustomFloatNumPickDialog;
import com.comvee.tnb.dialog.CustomFloatNumPickDialog.OnNumChangeListener;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.MembeArchive;
import com.comvee.tnb.model.MemberArchiveItem;
import com.comvee.tnb.model.MemberRecordModel;
import com.comvee.tnb.network.MemberRecordLoader;
import com.comvee.tnb.network.NetworkCallBack;
import com.comvee.tnb.ui.assess.AssessQuestionFragment;
import com.comvee.tnb.ui.follow.FollowQuestionFragment;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.view.SingleInputItemWindow;
import com.comvee.tnb.widget.ComveeAlertDialog;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.AppUtil;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.tool.UserMrg;
import com.comvee.util.BitmapUtil;
import com.comvee.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * 成员资料填写页面
 *
 * @author Administrator
 */
public class MemberRecordFragment extends BaseFragment implements OnClickListener, OnHttpListener {
    AlertDialog dialog;
    private String memberId;
    private LayoutInflater inflater;
    private LinearLayout roobLayout;
    private List<MembeArchive> archives;
    private List<KindView> kindViews;// 最外层类别：基本信息，家族史，生活方式等等
    private String newValue;
    private int newValuePotion;
    private int oldValuePotion;
    private boolean[] oldSelected;// 复选框原先被选的
    private int manORwomen;// 0为男，1为女
    private ImageView head;
    private String key;
    private String url;
    private View member_head;
    private View[] selectView;
    private int callreason;
    private boolean isCreat = true;
    private boolean isSliding;
    private int type;// 0 普通的基本档案 1评估调用的基本档案 2随访调用的基本档案
    private String followId;
    private TitleBarView mBarView;
    private int fromWhere;
    private MemberRecordModel recordModel;
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 2:
                    requestMemberMsg();
                    break;
                case 1:
                    getAllViews();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    public MemberRecordFragment() {
        Bundle b = new Bundle();
        b.putString("String", memberId);
        this.setArguments(b);
    }

    public static MemberRecordFragment newInstance(int type, int fromWhere, boolean isSliding) {
        MemberRecordFragment fragment = new MemberRecordFragment();
        fragment.setSliding(isSliding);
        fragment.setType(type);
        fragment.setFromWhere(fromWhere);
        return fragment;
    }

    public static MemberRecordFragment newInstance(int type, boolean isSliding, String followId) {
        MemberRecordFragment fragment = new MemberRecordFragment();
        fragment.setSliding(isSliding);
        fragment.setType(type);
        fragment.setFollowId(followId);
        return fragment;
    }

    private void setSliding(boolean isSliding) {
        this.isSliding = isSliding;
    }

    private void setType(int type) {
        this.type = type;
    }

    private void setFollowId(String followId) {
        this.followId = followId;
    }

    public void setFromWhere(int from) {
        this.fromWhere = from;
    }

    private void requestMemberMsg() {
        MemberRecordLoader loader = new MemberRecordLoader();
        loader.loaderUseMsg(new NetworkCallBack() {
            @Override
            public void callBack(int what, int status, Object obj) {
                if (status == 0) {
                    if (type == 1) {
                        AppUtil.showTost("温馨提示", "系统将调用您的档案信息进行评估，这些信息将对评估结果产生影响，请如实填写。", Toast.LENGTH_LONG);
                    }
                    recordModel = (MemberRecordModel) obj;
                    ImageLoaderUtil.getInstance(mContext).displayImage(recordModel.archivePic.getPhone(), head, ImageLoaderUtil.user_options);
                    archives = recordModel.archives;
                    handler.sendEmptyMessage(1);
                }
            }
        }, type);
    }

    private void getAllViews() {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mBarView.setRightButton("保存", MemberRecordFragment.this);
                getKindLayout();
                bindKindView();
                bindindChildren();
                isShow();
                cancelProgressDialog();
            }
        });
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.patient_basicfile_fragment;
    }

    @Override
    public void onLaunch(Bundle bundle) {
        super.onStart();
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        if (isSliding) {
        }
        initView();
        initData();

    }

    // 初始化控件
    private void initView() {
        this.inflater = LayoutInflater.from(getActivity());
        roobLayout = (LinearLayout) findViewById(R.id.basicfile_rootLayout);
        head = (ImageView) findViewById(R.id.head_image);
        head.setOnClickListener(this);
        member_head = findViewById(R.id.member_head);
        Button next = (Button) findViewById(R.id.follow_personal_next);
        next.setOnClickListener(this);
        if (type == 2) {
            member_head.setVisibility(View.GONE);
            next.setVisibility(View.VISIBLE);
        } else {
            member_head.setVisibility(View.VISIBLE);
            next.setVisibility(View.GONE);
        }
        member_head.setOnClickListener(this);

        if (type == 1) {
            mBarView.setTitle("健康评估");
        } else if (type == 0) {
            mBarView.setTitle("基本档案");
        } else {
            mBarView.setTitle("个人信息确认");
            mBarView.hideRightButton();
        }

    }

    // 初始化数据
    private void initData() {
        if (archives == null) {
            showProgressDialog(getString(R.string.msg_loading));
            // requestMemberInfo();
            handler.sendEmptyMessageDelayed(2, 0);
            // }
        } else {
            handler.sendEmptyMessageDelayed(1, 0);

        }
    }


    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {

            case TitleBarView.ID_LEFT_BUTTON:
                DrawerMrg.getInstance().open();
                break;
            case R.id.follow_personal_next:
            case TitleBarView.ID_RIGHT_BUTTON:
                if (type == 1) {
                    if (isFronEdit() > 0) {
                        showToast("请完善个人信息");
                        return;
                    }

                }
                if (url == null) {
                    requestModify(requestSave(), key);
                } else {
                    Submit(url);
                }
                break;
            case R.id.member_head:
            case R.id.head_image:
                toChangeHead();
                break;
            default:
                break;
        }
    }

    private void toChangeHead() {
        final String[] items = {"手机拍照", "从相册上传", "取消"};
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        toCaramer();
                        break;
                    case 1:
                        toPhoto();
                        break;
                    case 2:
                        // dialog.cancel();
                        break;
                    default:
                        break;
                }
            }

        };
        SingleInputItemWindow dialog = new SingleInputItemWindow(getContext(), items, "选择方式", -1, 0, 0);
        dialog.setOnItemClick(listener);
        dialog.setOutTouchCancel(true);
        dialog.showAtLocation(mRoot, Gravity.CENTER, 0, 0);
    }

    /**
     * 照相机
     */
    private void toCaramer() {
        if (!Util.SDCardExists()) {
            showToast("存储卡不可用，暂无法完成拍照!");
            return;
        }

        File vFile = new File(ConfigParams.IMG_CACHE_PATH + "/head.png");

        if (!vFile.exists()) {
            File vDirPath = vFile.getParentFile();
            vDirPath.mkdirs();
        }

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(vFile));
        startActivityForResult(cameraIntent, 0);
    }

    /**
     * 相册
     */
    private void toPhoto() {
        if (!Util.SDCardExists()) {
            showToast("存储卡不可用，暂无法从相册提取相片!");
            return;
        }
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, null);
        galleryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(galleryIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == 0) {
            File tempFile = new File(ConfigParams.IMG_CACHE_PATH + "/head.png");

            startPhotoZoom(Uri.fromFile(tempFile));
        } else if (requestCode == 1) {
            startPhotoZoom(data.getData());
        } else if (requestCode == 2) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap bitmap = extras.getParcelable("data");
                try {
                    BitmapUtil.saveBitmap(bitmap, ConfigParams.IMG_CACHE_PATH, "head.png");
                    url = ConfigParams.IMG_CACHE_PATH + "/head.png";
                } catch (Exception e) {
                    e.printStackTrace();
                }
                onChangeHead(bitmap);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        startActivityForResult(intent, 2);
    }

    private final void onChangeHead(Bitmap bitmap) {
        if (bitmap != null) {
            head.setImageBitmap(bitmap);

        }
    }

    private String requestSave() {

        showProgressDialog("档案信息保存中……");
        StringBuffer paramStr = new StringBuffer();
        paramStr.append("[");
        for (int i = 0; i < archives.size(); i++) {
            if (paramStr.length() > 1) {
                paramStr.append(",");
            }
            MembeArchive detailed = archives.get(i);
            String code = detailed.getItemCode();
            String pcode = detailed.getpCode();
            String value = null;
            if (detailed.getSeq().equals("seq6")) {
                if (detailed.getItemList().get(0).getIsValue().equals("1")) {
                    callreason = 2;
                } else
                    callreason = 1;
            }

            switch (Integer.parseInt(detailed.getItemType())) {
                case 1:
                case 2:
                    List<MemberArchiveItem> itemList = archives.get(i).getItemList();
                    boolean isAdd = false;
                    for (int k = 0; k < itemList.size(); k++) {
                        if (Integer.parseInt(itemList.get(k).getIsValue()) == 1) {
                            value = itemList.get(k).getValueCode();
                            if (isAdd) {
                                paramStr.append(",");
                            }
                            paramStr.append("{\"code\":\"");
                            paramStr.append(code);
                            paramStr.append("\",\"pcode\":\"");
                            paramStr.append(pcode);
                            paramStr.append("\",\"value\":\"");
                            paramStr.append(value);
                            paramStr.append("\"}");
                            isAdd = true;
                        }

                    }
                    if (!isAdd && paramStr.length() > 1) {
                        paramStr.deleteCharAt(paramStr.length() - 1);
                    }
                    break;
                case 3:
                case 4:
                case 5:

                    value = detailed.getValues();
                    if (detailed.getItemCode().equals("memberName")) {
                        UserMrg.DEFAULT_MEMBER.name = value;
                    }
                    paramStr.append("{\"code\":\"");
                    paramStr.append(code);
                    paramStr.append("\",\"pcode\":\"");
                    paramStr.append(pcode);
                    paramStr.append("\",\"value\":\"");
                    paramStr.append(value);
                    paramStr.append("\"}");
                    break;
                default:
                    break;
            }

        }

        paramStr.append("]");
        return paramStr.toString();
    }

    // 提交修改后的基本档案
    private void requestModify(String paramStr, String key) {
        if (archives == null) {
            return;
        }
        final String url = ConfigUrlMrg.MODIFY_MEMBER1;
        ComveeHttp http = new ComveeHttp(getApplicationContext(), url);
        http.setPostValueForKey("paramStr", paramStr);
        http.setPostValueForKey("perRealPhoto", key);
        http.setOnHttpListener(2, this);
        http.startAsynchronous();
    }

    private void Submit(String url) {
        showProgressDialog(getString(R.string.msg_loading));
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.SUBMIT_IMG_YUN);
        http.setUploadFileForKey("file", url);
        http.setPostValueForKey("platCode", "198");
        http.setOnHttpListener(3, this);
        http.startAsynchronous();
    }

    @Override
    public void onSussece(int what, byte[] b, boolean fromCache) {
        cancelProgressDialog();

        switch (what) {
            case 1:
                if (type == 1) {
                    AppUtil.showTost("温馨提示", "系统将调用您的档案信息进行评估，这些信息将对评估结果产生影响，请如实填写。", Toast.LENGTH_LONG);
                }
                // parserMemberInfo(b);
                break;
            case 2:
                try {
                    ComveePacket packet = ComveePacket.fromJsonString(b);
                    if (packet.getResultCode() == 0) {
                        UserMrg.DEFAULT_MEMBER.callreason = callreason - 1;
                        showToast("保存成功");
                        String body = packet.optString("body");
                        if (body != null) {
                            UserMrg.DEFAULT_MEMBER.photo = body;
                        }
                        DrawerMrg.getInstance().updateLefFtagment();
                        if (type == 2) {
                            toFragment(FollowQuestionFragment.newInstance(followId), false, true);
                            return;
                        }
                        if (type == 1) {
                            AssessQuestionFragment frag = AssessQuestionFragment.newInstance();
                            frag.setQuestionType(callreason);
                            toFragment(frag, false, true);
                            return;
                        }
                        IndexFrag.toFragment(getActivity(),true); 

                    } else {
                        showToast(getResources().getString(R.string.error));
                    }
                } catch (Exception e) {
                    showToast(R.string.time_out);
                }

                break;
            case 3:
                parserImage(b);
                break;
            default:
                break;
        }
    }

    @Override
    public void onFialed(int what, int errorCode) {
        cancelProgressDialog();
        ComveeHttpErrorControl.parseError(getActivity(), errorCode);
    }

    // 根据ItemType显示对应对话框处理
    private void createDialogByItemType(View v, int index) {
        MembeArchive archive = archives.get(index);
        // 初始化各个选项
        List<MemberArchiveItem> optionItems = archive.getItemList();
        String[] itemNames = null;
        if (optionItems != null) {
            itemNames = new String[optionItems.size()];
            for (int j = 0; j < optionItems.size(); j++) {
                itemNames[j] = optionItems.get(j).getValueName();
            }
        }
        selectView[index].setFocusable(true);
//        selectView[index].setFocusableInTouchMode(true);

//        selectView[index].requestFocus();
//        selectView[index].requestFocusFromTouch();

        // 1 单选 2 多选 3 日期 4 文本填空 5 数值填空
        switch (Integer.parseInt(archive.getItemType())) {
            case 1:
                if (index == 1)
                    manORwomen = getItemListChooseId(optionItems);
                oldValuePotion = getItemListChooseId(optionItems);
                createSingleDialog(v, archive.getDictName(), itemNames, index);
                break;
            case 2:
                oldSelected = new boolean[archive.getItemList().size()];

                for (int i = 0; i < archive.getItemList().size(); i++) {
                    if (archive.getItemList().get(i).getIsValue().equals("1"))
                        oldSelected[i] = true;
                }
                createMultiDialog(v, archive.getDictName(), itemNames, index);
                break;
            case 3:
                Calendar calendar = null;
                try {
                    calendar = stringToCalendar(archive.getValues(), "yyyy-MM-dd");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (calendar == null) {
                    calendar = Calendar.getInstance();
                }
                showTimeDialogDate(calendar, index, v);
                break;
            case 4:
                if(!"1".equals(archive.getTie())){
                    showEdittext(v, index);
                }
                break;
            case 5:
                int defult = 0;
                if (archive.getValues() != null && !archive.getValues().equals("")) {
                    defult = Integer.parseInt(archive.getValues());
                }
                if (archive.getDictName().equals("身高")) {
                    showNumDialog(defult, "cm", index, v);
                } else if (archive.getDictName().equals("体重")) {
                    showNumDialog(defult, "  kg", index, v);
                } else {
                    showNumDialog(defult, "", index, v);
                }

                break;
            default:
                break;
        }
    }

    private void showNumDialog(int height, final String label, final int index, final View v) {
        CustomFloatNumPickDialog dialog = new CustomFloatNumPickDialog();
        dialog.addOnNumChangeListener(new OnNumChangeListener() {

            @Override
            public void onChange(DialogFragment dialog, float num) {
                // TODO Auto-generated method stub
                archives.get(index).setValues((int) num + "");
                ((TextView) v.findViewById(R.id.basicfile_value)).setText(archives.get(index).getValues() + label);
            }
        });

        dialog.setLimitNum(30, 300);
        dialog.setUnit(label);
        if (height > 30 && height < 300) {
            dialog.setDefult(height);
        } else {
            dialog.setDefult(80);
        }
        dialog.show(getActivity().getSupportFragmentManager(), "dialog");
    }

    // 创建时间对话框
    private void showTimeDialogDate(Calendar calendar, final int index, final View v) {
        CustomDatePickDialog dialog = new CustomDatePickDialog();
        dialog.setOnTimeChangeListener(new CustomDatePickDialog.OnTimeChangeListener() {

            @Override
            public void onChange(DialogFragment dialog, int year, int month, int day) {
                // TODO Auto-generated method stub
                String time = String.format("%d-%02d-%02d", year, month, day);
                archives.get(index).setValues(time);
                ((TextView) v.findViewById(R.id.basicfile_value)).setText(archives.get(index).getValues());
                dealIsRestrain(v, index);
                isShow();
            }
        });

        dialog.setLimitTime(1890, 2190);
        Calendar cal = Calendar.getInstance();
        dialog.setLimitCanSelectOfEndTime(cal);
        try {
            dialog.setDefaultTime(calendar);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.show(getActivity().getSupportFragmentManager(), "dialog");
    }

    // 将String转Calendar
    private Calendar stringToCalendar(String time, String fomate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(fomate);
        Date date = (Date) sdf.parse(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    // 创建多选对话框
    private void createMultiDialog(final View v, String title, final String[] itemNames, final int index) {
        new ComveeAlertDialog.Builder(getActivity()).setTitle(title)
                .setMultiChoiceItems(itemNames, oldSelected, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1, boolean arg2) {
                        oldSelected[arg1] = arg2;
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

                for (int i = 0; i < oldSelected.length; i++) {
                    if (oldSelected[i]) {
                        archives.get(index).getItemList().get(i).setIsValue("1");
                    } else {
                        archives.get(index).getItemList().get(i).setIsValue("0");
                    }
                }
                addStrings(v, index);

            }
        }).show();
    }

    // 创建单选对话框
    private void createSingleDialog(final View v, String title, String[] itemNames, final int index) {
        dialog = new ComveeAlertDialog.Builder(getActivity()).setTitle(title)
                .setSingleChoiceItems(itemNames, oldValuePotion, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (index == 1)

                            manORwomen = arg1;
                        newValuePotion = arg1;
                        newValue = archives.get(index).getItemList().get(arg1).getValueName();
                        if (newValue != null) {
                            // 将旧的值设置未选中
                            if (oldValuePotion != -1)
                                archives.get(index).getItemList().get(oldValuePotion).setIsValue("0");
                            // 将新值设置为选中
                            archives.get(index).getItemList().get(newValuePotion).setIsValue("1");
                            archives.get(index).setValues(newValue);

                            dialog.cancel();
                        }
                        ((TextView) v.findViewById(R.id.basicfile_value)).setText(archives.get(index).getValues());
                        dealIsRestrain(v, index);
                        isShow();
                    }
                }).create();

        dialog.show();

    }

    private void addStrings(View v, int index) {
        MembeArchive archive = archives.get(index);
        String value = "是(";

        for (int i = 0; i < archive.getItemList().size(); i++) {
            if (archive.getItemList().get(i).getIsValue().equals("1")) {
                value = value + archive.getItemList().get(i).getValueName() + ",";
            }
        }
        if (value.length() > 2) {
            value = value.substring(0, value.length() - 1) + ")";
        } else {
            value = "是";

        }
        ((TextView) v.findViewById(R.id.basicfile_value)).setText(value);
    }

    // 处理约束
    private void dealIsRestrain(View v, int index) {
        MembeArchive archive = archives.get(index);
        manORwomen = getItemListChooseId(archive.getItemList());

        int which = getItemListChooseId(archive.getItemList());
        if (which == -1)
            return;
        // 获取当前题目的所有规则
        String[] seqs = getRuleByItemListId(archive);

        // 开始实行规则
        switch (Integer.parseInt(archive.getItemList().get(which).getIsRestrain())) {
            case 0:// 0 没有规则
                break;
            case 1:// 1 显示隐藏项目
                for (int i = 0; i < seqs.length; i++) {
                    archives.get(getArchiveBySeq(seqs[i])).setIsShow("1");
                }

                break;
            case 2:// 2 嵌入弹开
                if (isCreat) {
                    createDialogByItemType(v, getArchiveBySeq(seqs[0]));
                }
                break;
            case 3:// 3 显示隐藏项目同时触发题目约束
                for (int i = 0; i < seqs.length; i++) {
                    archives.get(getArchiveBySeq(seqs[i])).setIsShow("1");
                    archives.get(getArchiveBySeq(seqs[i])).setTie("1");
                }

                break;
            case 4:// 4 隐藏项目
                for (int i = 0; i < seqs.length; i++) {
                    archives.get(getArchiveBySeq(seqs[i])).setIsShow("0");
                }
                break;
            case 5:
                String seq[] = seqs[0].split(",");

                for (int i = 0; i < seq.length; i++) {
                    archives.get(getArchiveBySeq(seq[i])).setIsShow("1");
                }
                String seq1[] = seqs[1].split(",");
                for (int i = 0; i < seq1.length; i++) {
                    archives.get(getArchiveBySeq(seq1[i])).setIsShow("0");
                }
                break;
            default:
                break;
        }
    }

    // 根据档案题目选项自身id去取规则
    private String[] getRuleByItemListId(MembeArchive archive) {
        if (archive.getRule() == null)
            return null;

        int which = getItemListChooseId(archive.getItemList());
        if (which == -1)
            return null;
        String id = archive.getItemList().get(which).getId();
        String str[] = archive.getRule().optString(id).split("@");
        if (str.length > 1) {
            return str;
        }
        String[] seqs = archive.getRule().optString(id).split(",");

        return seqs;
    }

    // 根据题号获取档案题目下标
    private int getArchiveBySeq(String seq) {
        for (int i = 0; i < archives.size(); i++) {
            if (archives.get(i).getSeq().equals(seq)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取外层控件数
     */
    @SuppressWarnings("unchecked")
    private void getKindLayout() {

        kindViews = new ArrayList<KindView>();
        for (int i = 0; i < archives.size(); i++) {
            String kindName = null;
            String kindKey = null;
            KindView kindView = null;
            if (type == 3) {
                if (archives.get(i).getIsNeed().equals("1")) {
                    kindName = archives.get(i).getCategoryName();
                    kindKey = archives.get(i).getCategory();
                    kindView = new KindView(kindKey, kindName, null);
                }
            } else {
                kindName = archives.get(i).getCategoryName();
                kindKey = archives.get(i).getCategory();
                kindView = new KindView(kindKey, kindName, null);
            }
            if (!kindViews.contains(kindView) && kindView != null) {
                kindViews.add(kindView);
            }
        }
        // 按照kindKey从小到大排序
        Collections.sort(kindViews, new MyComparator());
    }

    /**
     * 生成外层控件
     */
    private void bindKindView() {
        KindView kindView = null;
        for (int i = 0; i < kindViews.size(); i++) {
            kindView = kindViews.get(i);
            LinearLayout layout = null;
            if (kindView.kindKey.equals("0"))
                layout = (LinearLayout) inflater.inflate(R.layout.empty, null);
            else {
                layout = (LinearLayout) inflater.inflate(R.layout.basicfile_linerlayout_item, null);
                TextView tv_pcode = (TextView) layout.findViewById(R.id.basicfile_tv_pcode);
                tv_pcode.setText(kindView.kindName);
            }
            kindView.kindLayout = layout;
            roobLayout.addView(layout);
        }
    }

    /**
     * 根据外层控件获取子项 生成子控件
     */
    private void bindindChildren() {
        if (archives == null)
            return;
        selectView = new View[archives.size()];
        for (int i = 0; i < kindViews.size(); i++) {
            int num = 0;
            for (int j = 0; j < archives.size(); j++) {
                MembeArchive archive = archives.get(j);
                if (archive.getCategory().equals(kindViews.get(i).kindKey)) {
                    addChildView(kindViews.get(i).kindLayout, j, num);

                    num++;
                }
            }
        }
    }

    /**
     * 生成子控件
     *
     * @param parent
     * @param index
     */
    private void addChildView(LinearLayout parent, int index, int num) {
        MembeArchive archive = archives.get(index);
        View child = inflater.inflate(R.layout.basicfile_content_item, null);
        TextView basicfile_key = (TextView) child.findViewById(R.id.basicfile_key);
        TextView basicfile_value = (TextView) child.findViewById(R.id.basicfile_value);
        View line = child.findViewById(R.id.sprite_line);
        if (num == 0) {
            line.setVisibility(View.GONE);

        } else {
            line.setVisibility(View.VISIBLE);
        }
        selectView[index] = child;
        basicfile_key.setText(archive.getDictName());
        // 没有选项的情况
        if (archive.getItemList() == null) {
            basicfile_value.setText(archive.getValues());
            if("1".equals(archive.getTie() )){
                basicfile_value.setEnabled(false);
                basicfile_value.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
            }else{
                basicfile_value.setEnabled(true);
            }
            if (archive.getDictName().equals("身高")) {
                basicfile_value.setText(archive.getValues() + "cm");
            }
            if (archive.getDictName().equals("体重")) {
                basicfile_value.setText(archive.getValues() + "kg");

            }
        } else if (archive.getItemType().equals("2")) {
            addStrings(child, index);
        } else {
            int which = getItemListChooseId(archive.getItemList());
            if (which == -1) {
                basicfile_value.setText("");

            } else {
                basicfile_value.setText(archive.getItemList().get(which).getValueName());
                String[] seqs = getRuleByItemListId(archive);
                if (archive.getItemList().get(which).getIsRestrain().equals("2")) {
                    MembeArchive archive1 = archives.get(getArchiveBySeq(seqs[0]));
                    if (archive1.getItemType().equals("2")) {
                        addStrings(child, getArchiveBySeq(seqs[0]));
                    }
                }
            }
        }
        if (parent.getChildCount() == 0) {
            child.findViewById(R.id.sprite_line).setVisibility(View.GONE);
        }

        child.setOnClickListener(new ChidrenViewOnclickListener(index));
        parent.addView(child, parent.getChildCount());

    }

    private void isShow() {
        for (int i = 0; i < archives.size(); i++) {
            dealIsRestrain(selectView[i], i);
            isCreat = false;
            if (type == 1) {

                if (selectView[i] != null) {
                    if (archives.get(i).getIsShow().equals("1")) {
                        selectView[i].setVisibility(View.VISIBLE);
                        if (archives.get(i).getIsNeed().equals("1")) {

                            if (archives.get(i).getItemType().equals("1") || archives.get(i).getItemType().equals("2")) {
                                if (getItemListChooseId(archives.get(i).getItemList()) != -1) {
                                    selectView[i].findViewById(R.id.img_tag).setVisibility(View.INVISIBLE);
                                } else {

                                    selectView[i].findViewById(R.id.img_tag).setVisibility(View.VISIBLE);
                                }
                            }
                            if (archives.get(i).getItemType().equals("4") || archives.get(i).getItemType().equals("5")
                                    || archives.get(i).getItemType().equals("3")) {
                                if (archives.get(i).getValues() == null || archives.get(i).getValues().trim().equals("")) {

                                    selectView[i].findViewById(R.id.img_tag).setVisibility(View.VISIBLE);
                                } else {
                                    selectView[i].findViewById(R.id.img_tag).setVisibility(View.INVISIBLE);

                                }
                            }
                        }
                    } else {

                        selectView[i].setVisibility(View.GONE);
                    }
                }
            } else {
                if (archives.get(i).getIsShow().equals("1")) {
                    selectView[i].setVisibility(View.VISIBLE);
                } else {
                    selectView[i].setVisibility(View.GONE);
                }
            }

        }
        isCreat = true;
    }

    private int isFronEdit() {
        int num = 0;
        if (archives.isEmpty()) {
            return 0;
        }
        for (int i = 0; i < archives.size(); i++) {
            if (archives.get(i).getIsNeed().equals("1") && archives.get(i).getIsShow().equals("1")) {
                switch (Integer.parseInt(archives.get(i).getItemType())) {
                    case 1:
                    case 2:
                        List<MemberArchiveItem> itemList = archives.get(i).getItemList();
                        if (getItemListChooseId(itemList) == -1) {
                            num++;
                        }
                        break;
                    case 3:
                    case 4:
                    case 5:
                        if (archives.get(i).getValues() == null || archives.get(i).getValues().equals("")) {
                            num++;
                        }
                        break;
                    default:
                        break;
                }

            }
        }
        return num;
    }

    // 判断子项里面哪一个被选中,-1表示全部没有选
    private int getItemListChooseId(List<MemberArchiveItem> optionItems) {
        int which = -1;
        // 判断被选中的是哪一项
        if (optionItems != null) {
            for (int j = 0; j < optionItems.size(); j++) {
                if (optionItems.get(j).getIsValue().equals("1")) {
                    which = j;
                }
            }
        }
        return which;
    }

    private void parserImage(byte[] b) {

        try {
            final ComveePacket packet = ComveePacket.fromJsonString(b);
            if (packet.getResultCode() == 0) {
                JSONArray body = packet.getJSONArray("body");

                if (body.length() > 0) {
                    JSONObject obj = body.getJSONObject(0);
                    // String url = obj.optString("url");
                    String key = obj.optString("key");
                    // if (UserMrg.DEFAULT_MEMBER != null)
                    // UserMrg.DEFAULT_MEMBER.photo = url + "/" + key;
                    // this.key = key;
                    requestModify(requestSave(), key);
                }

                // requestModify(requestSave(), key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 创建文本输入框
    private void showEdittext(final View v, final int index) {
        final TextView basicfile_value = (TextView) v.findViewById(R.id.basicfile_value);
        final EditText text = (EditText) v.findViewById(R.id.basicfile_value_edi);
        String hint = null;
        final MembeArchive archive = archives.get(index);
        if (archive.getItemType().equals("4")) {
            text.setVisibility(View.VISIBLE);
            basicfile_value.setVisibility(View.GONE);
            text.setInputType(InputType.TYPE_CLASS_TEXT);
            if (archive.getDictName().equals("姓名") || archive.getDictName().equals("昵称")) {
                hint = "请输入昵称！";
                InputFilter[] filters = {new NameLengthFilter(30)};
                text.setFilters(filters);
            } else {
                hint = "请输入相关详细！";
            }
            text.setHint(hint);
            text.setText(archive.getValues());
        }
        if (archive.getItemType().equals("5")) {
            text.setVisibility(View.VISIBLE);
            basicfile_value.setVisibility(View.GONE);
            text.setHint(hint);
            text.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            if (archive.getDictName().equals("身高")) {
                hint = "单位：cm";
            }
            if (archive.getDictName().equals("体重")) {
                hint = "单位：kg";
            }
            text.setHint(hint);
            text.setText(archive.getValues());

        }
        text.setFocusable(true);
        text.setFocusableInTouchMode(true);
        text.requestFocus();
        // // 打开软键盘
        final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        text.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean arg1) {

                if (text.hasFocus() == false) {
                    text.setVisibility(View.GONE);
                    basicfile_value.setVisibility(View.VISIBLE);
                    if (archive.getDictName().equals("身高")) {
                        basicfile_value.setText(text.getText().toString().trim() + "cm");
                    } else if (archive.getDictName().equals("体重")) {
                        basicfile_value.setText(text.getText().toString().trim() + "kg");
                    }

                    imm.hideSoftInputFromWindow(text.getWindowToken(), 0);
                }

            }
        });
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (arg0 != null) {
                    archive.setValues(arg0.toString().trim());
                    basicfile_value.setText(arg0.toString().trim());
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public boolean onBackPress() {
        if (fromWhere == 1) {
            IndexFrag.toFragment(getActivity(),true); 
            return true;
        }
        if (isSliding) {
            DrawerMrg.getInstance().open();
            return true;
        }
        return false;
    }

    // 子控件的监听
    class ChidrenViewOnclickListener implements OnClickListener {
        int index;

        public ChidrenViewOnclickListener(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            createDialogByItemType(v, index);
        }
    }

    class KindView {
        String kindKey;
        String kindName;
        LinearLayout kindLayout;

        public KindView(String kindKey, String kindName, LinearLayout kindLayout) {
            this.kindKey = kindKey;
            this.kindName = kindName;
            this.kindLayout = kindLayout;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            KindView other = (KindView) obj;
            if (other.kindKey.equals(this.kindKey) && other.kindName.equals(this.kindName))
                return true;
            return false;
        }
    }

    @SuppressWarnings("rawtypes")
    class MyComparator implements Comparator {
        @Override
        public int compare(Object obj1, Object obj2) {
            KindView k1 = (KindView) obj1;
            KindView k2 = (KindView) obj2;
            if (Integer.parseInt(k1.kindKey) > Integer.parseInt(k2.kindKey))
                return 1;
            else if (Integer.parseInt(k1.kindKey) < Integer.parseInt(k2.kindKey))
                return -1;
            else
                return 0;
        }
    }
}
