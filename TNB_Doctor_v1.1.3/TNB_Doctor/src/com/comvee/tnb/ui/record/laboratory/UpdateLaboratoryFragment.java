package com.comvee.tnb.ui.record.laboratory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.comvee.frame.BaseFragmentActivity;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.CustomDatePickDialog;
import com.comvee.tnb.dialog.CustomDatePickDialog.OnTimeChangeListener;
import com.comvee.tnb.dialog.CustomDialog;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.CreateLaboratoryInfo;
import com.comvee.tnb.network.ObjectLoader;
import com.comvee.tnb.ui.record.common.AlbumHelper;
import com.comvee.tnb.ui.record.common.ImageBucket;
import com.comvee.tnb.ui.record.common.ImageItem4LocalImage;
import com.comvee.tnb.ui.record.common.MyBitmapFactory;
import com.comvee.tnb.ui.record.common.NetPic;
import com.comvee.tnb.ui.record.common.SelectedImageGridAdapter;
import com.comvee.tnb.ui.record.common.SelectedImageGridAdapter.OnDeleteListener;
import com.comvee.tnb.ui.record.common.ShowImageViewpagerActivity;
import com.comvee.tnb.ui.record.common.ShowLocalImageFragment;
import com.comvee.tnb.ui.record.common.UploadImageHelper1;
import com.comvee.tnb.ui.record.common.UploadImageHelper1.ImageUploadFinishListener;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.AppUtil;
import com.comvee.tool.UserMrg;
import com.comvee.util.BundleHelper;
import com.comvee.util.CacheUtil;
import com.comvee.util.UITool;
import com.comvee.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * 修改化验单
 *
 * @author PXL
 */
@SuppressLint("ValidFragment")
public class UpdateLaboratoryFragment extends BaseFragment implements OnHttpListener, OnClickListener, OnItemClickListener, OnDeleteListener,
        OnTimeChangeListener {
    public static final String fileDirStr = Environment.getExternalStorageDirectory() + File.separator + "temp";// 拍照存放的路径
    public static String timeStr = "";
    private String initName;
    private String initTime;
    private EditText nameEt;
    private TextView timeTv;
    private Button deleteBt;
    private Laboratory laboratory;
    private GridView gridViewSelected;
    private SelectedImageGridAdapter selectedImageGridAdapter;
    private PopupWindow choosedPop;
    private CustomDialog leaveDialog;
    private CustomDialog deleteDialog;
    private CustomDialog deleteLaboryDialog;
    private boolean saveOrUpdateSave = false;// 用来标记保存后或者删除操作后直接退出界面
    private CustomDatePickDialog dialog;
    private boolean isLeftClick = false;
    private int deletePosition;
    private File fileDir;// 拍照保存在本地的图片的文件夹
    private TitleBarView mBarView;
    private CheckBox mCheck0;
    private boolean isCheckedID;
    private String mServiceId;
    private String mDoctorId;
    private String isUseService;

    public static void toFragment(FragmentActivity act, Laboratory laboratory) {
        FragmentMrg.toFragment(act, UpdateLaboratoryFragment.class, BundleHelper.getBundleByObject(laboratory), true);
    }

    @Override
    public void onLaunch(Bundle dataBundle) {
        super.onLaunch(dataBundle);
        initLoader();
        this.laboratory = BundleHelper.getObjecByBundle(Laboratory.class, dataBundle);
        fileDir = new File(fileDirStr);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        deleteallImageLoc();
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        mBarView.setRightButton(getText(R.string.save).toString(), this);
        findViewById();
        mBarView.setTitle(getString(R.string.record_report_title));
        initName = laboratory.folderName;
        isUseService = laboratory.isUseService;//验化单
        try {
            initTime = laboratory.insertDt.split(" ")[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
        nameEt.setText(initName);
        timeTv.setKeyListener(null);
        if (TextUtils.isEmpty(timeStr)) {
            timeTv.setText(initTime);
        } else {
            timeTv.setText(timeStr);
        }
        mCheck0 = (CheckBox) findViewById(R.id.check0);
        initPopupWindow();

        dialog = new CustomDatePickDialog();
        dialog.setLimitTime(1890, 2190);
        dialog.setDefaultTime(Calendar.getInstance());

        selectedImageGridAdapter = new SelectedImageGridAdapter(getActivity(), MyBitmapFactory.tempSelectBitmap);
        selectedImageGridAdapter.setOnDeleteListener(this);
        gridViewSelected.setAdapter(selectedImageGridAdapter);
        gridViewSelected.setOnItemClickListener(this);

        timeTv.setOnClickListener(this);
        dialog.setOnTimeChangeListener(this);
        deleteBt.setOnClickListener(this);
        AppUtil.registerEditTextListener1(nameEt, R.string.laboratory_nametip, 20, getApplicationContext());
        mCheck0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    isCheckedID = isChecked;
                } else {
                    isCheckedID=isChecked;
                }
            }
        });
        if (!TextUtils.isEmpty(mServiceId)&&isUseService.equals("1")) {
            mCheck0.setEnabled(true);
            mCheck0.setChecked(true);
        } else {
            mCheck0.setEnabled(false);
            mCheck0.setChecked(false);
        }
    }
    private void initLoader() {
        final ObjectLoader<CreateLaboratoryInfo> loader = new ObjectLoader<CreateLaboratoryInfo>();
        loader.loadBodyObject(CreateLaboratoryInfo.class, ConfigUrlMrg.ANSWER_SERVICE_ID, loader.new CallBack() {

            @Override
            public void onBodyObjectSuccess(boolean isFromCache, CreateLaboratoryInfo obj) {
                super.onBodyObjectSuccess(isFromCache, obj);
                if (obj != null) {
                    try {
                        mServiceId = obj.serverId;
                        mDoctorId = obj.doctorId;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (!TextUtils.isEmpty(mServiceId)) {
                    mCheck0.setEnabled(true);
                    mCheck0.setChecked(true);
                } else {
                    mCheck0.setEnabled(false);
                    mCheck0.setChecked(false);
                }if(isUseService.equals("1")){
                    mCheck0.setChecked(true);
                }else {
                    mCheck0.setChecked(false);
                }
            }
        });
    }
    @Override
    public int getViewLayoutId() {
        return R.layout.input_update_laboratory_input_fragment;
    }

    private void deleteallImageLoc() {
        for (int i = 0; i < 9; i++) {
            File file = new File(fileDirStr + File.separator + i + ".png");
            if (file.exists())
                file.delete();
        }
    }

    @SuppressWarnings("deprecation")
    private void initPopupWindow() {
        CustomDialog.Builder leaveBuilder = new CustomDialog.Builder(getActivity());
        leaveBuilder.setMessage("该记录有修改，是否保存？").setPositiveButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialoginterface, int i) {
                leaveDialog.dismiss();
                isLeftClick = true;
                ((BaseFragmentActivity) getActivity()).onBackPressed();
            }
        }).setNegativeButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialoginterface, int i) {
                leaveDialog.dismiss();
                update();
            }
        });
        leaveDialog = leaveBuilder.create();

        CustomDialog.Builder deleteBuilder = new CustomDialog.Builder(getActivity());
        deleteBuilder.setMessage("是否确定删除该照片？").setPositiveButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialoginterface, int i) {
                deleteDialog.dismiss();
            }
        }).setNegativeButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialoginterface, int i) {
                deleteDialog.dismiss();
                // 非本地图片则更新
                if (TextUtils.isEmpty(MyBitmapFactory.tempSelectBitmap.get(deletePosition).imagePath)) {
                    deletePic();
                } else {
                    ImageItem4LocalImage image = MyBitmapFactory.tempSelectBitmap.get(deletePosition);
                    MyBitmapFactory.tempSelectBitmap.remove(image);
                    selectedImageGridAdapter.notifyDataSetChanged();
                }

            }
        });
        deleteDialog = deleteBuilder.create();

        CustomDialog.Builder deleteLaboryBuilder = new CustomDialog.Builder(getActivity());
        deleteLaboryBuilder.setMessage("是否确定删除该记录？").setPositiveButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialoginterface, int i) {
                deleteLaboryDialog.dismiss();
            }
        }).setNegativeButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialoginterface, int i) {
                deleteLaboryDialog.dismiss();
                deleteLaboratory();
            }
        });
        deleteLaboryDialog = deleteLaboryBuilder.create();

        View contentView = LayoutInflater.from(mContext).inflate(R.layout.pop_select_pic, null);
        choosedPop = new PopupWindow(contentView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        choosedPop.setBackgroundDrawable(new BitmapDrawable());
        choosedPop.setAnimationStyle(R.style.PopupAnimation);
        contentView.findViewById(R.id.cancle).setOnClickListener(this);
        contentView.findViewById(R.id.pic).setOnClickListener(this);
        contentView.findViewById(R.id.camera).setOnClickListener(this);

        View contentView1 = LayoutInflater.from(mContext).inflate(R.layout.pop_uploading, null);

    }

    @Override
    public void onResume() {
        super.onResume();
        selectedImageGridAdapter.notifyDataSetChanged();
    }

    private void findViewById() {
        nameEt = (EditText) findViewById(R.id.name);
        timeTv = (TextView) findViewById(R.id.time);
        deleteBt = (Button) findViewById(R.id.delete);

        gridViewSelected = (GridView) findViewById(R.id.gridview);
    }

    /**
     * 删除某张图片
     */
    protected void deletePic() {
        ((BaseFragmentActivity) getActivity()).showProgressDialog("正在提交中...");
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.DELETE_LABORATOR_PIC);
        http.setPostValueForKey("picId", laboratory.uploadPics.get(deletePosition).picId);
        http.setOnHttpListener(3, UpdateLaboratoryFragment.this);
        http.startAsynchronous();
    }

    // 更新
    private void update() {
        if (TextUtils.isEmpty(nameEt.getText())) {
            showToast("请填写化验单名称");
            return;
        }
        if (MyBitmapFactory.tempSelectBitmap.size() == 0) {
            showToast("请选择照片");
            return;
        }
        try {
            if (nameEt.getText().toString().getBytes("GBK").length > 40) {
                showToast("化验单名称不能超过20个字哦");
                return;
            }
        } catch (UnsupportedEncodingException e1) {
            Log.e("tag", e1.getMessage(), e1);
        }
        ((BaseFragmentActivity) getActivity()).showProgressDialog("正在提交中...");
        deleteBt.setBackgroundResource(R.drawable.btn_color_default);
        deleteBt.setClickable(false);
        selectedImageGridAdapter.notifyDataSetChanged();

        UploadImageHelper1 uploadImageHelper = new UploadImageHelper1(mContext, MyBitmapFactory.tempSelectBitmap);
        uploadImageHelper.setAllfishListener(new ImageUploadFinishListener() {

            @Override
            public void allFinish(List<String> bigUrls) {
                boolean allSuccess = true;
                for (String item : bigUrls) {
                    if (TextUtils.isEmpty(item)) {
                        allSuccess = false;
                        return;
                    }
                }
                // 图片全部上传阿里成功后上传服务器
                if (allSuccess) {
                    try {
                        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.UPDATE_LABORATOR);
                        JSONArray jsonArray = new JSONArray();
                        for (int i = 0; i < bigUrls.size(); i++) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("urlBig", bigUrls.get(i));
                            jsonArray.put(jsonObject);
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
                        String time = timeTv.getText() + " " + sdf.format(new Date());
                        http.setPostValueForKey("folderId", laboratory.folderId);
                        http.setPostValueForKey("folderName", nameEt.getText() + "");
                        http.setPostValueForKey("dateTime", time);
                        http.setPostValueForKey("photoPathJson", jsonArray.toString());
                        if (!TextUtils.isEmpty(mServiceId)&& isCheckedID&&!TextUtils.isEmpty(mDoctorId)) {
                            http.setPostValueForKey("answerServiceId", mServiceId);
                            http.setPostValueForKey("doctorId", mDoctorId);
                        }
                        http.setOnHttpListener(1, UpdateLaboratoryFragment.this);
                        http.startAsynchronous();

                    } catch (Exception e) {
                    }
                } else {
                    cancelProgressDialog();
                    showToast("化验单上传失败，请重试~");
                }

            }

        });
        uploadImageHelper.start();
    }

    // 删除
    protected void deleteLaboratory() {
        ((BaseFragmentActivity) getActivity()).showProgressDialog("正在提交中...");
        deleteBt.setBackgroundResource(R.drawable.btn_color_default);
        deleteBt.setClickable(false);
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.DELETE_LABORATOR);
        http.setPostValueForKey("folderId", laboratory.folderId);
        http.setOnHttpListener(2, UpdateLaboratoryFragment.this);
        http.startAsynchronous();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == selectedImageGridAdapter.getCount() - 1 && MyBitmapFactory.tempSelectBitmap.size() != 9) {
            UITool.closeInputMethodManager(nameEt.getWindowToken());

            choosedPop.showAtLocation(mRoot, Gravity.CENTER, 0, 0);
        } else {
            List<NetPic> datas = new ArrayList<NetPic>();
            for (ImageItem4LocalImage image : MyBitmapFactory.tempSelectBitmap) {
                NetPic netPic = new NetPic();
                if (TextUtils.isEmpty(image.sourceImagePath)) {
                    netPic.localPath = image.imagePath;
                } else {
                    netPic.picBig = image.sourceImagePath;
                }
                datas.add(netPic);
            }
            Activity activity = getActivity();
            Intent it = ShowImageViewpagerActivity.getIntent(activity, datas, position, selectedImageGridAdapter.getAllImageView());
            activity.startActivity(it);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.time:
                dialog.show(getActivity().getSupportFragmentManager(), "");
                break;
            case R.id.camera:
                toCaramer();
                break;
            case R.id.cancle:
                choosedPop.dismiss();
                break;
            case TitleBarView.ID_RIGHT_BUTTON:
                update();
                break;
            case R.id.pic:
                choosedPop.dismiss();
                MyBitmapFactory.canback = true;
                MyBitmapFactory.tempAllImage.clear();
                MyBitmapFactory.tempSelectBitmapInAlbum.clear();
                AlbumHelper helper = AlbumHelper.getHelper();
                helper.init(getApplicationContext());
                List<ImageBucket> bucketList = helper.getImagesBucketList(false);
                for (int i = 0; i < bucketList.size(); i++) {
                    MyBitmapFactory.tempAllImage.addAll(bucketList.get(i).imageList);
                }
                List<ImageItem4LocalImage> listWithoutDup = new ArrayList<ImageItem4LocalImage>(new HashSet<ImageItem4LocalImage>(
                        MyBitmapFactory.tempAllImage));
                MyBitmapFactory.tempAllImage = listWithoutDup;
                MyBitmapFactory.albumnName = "最近照片";
                toFragment(new ShowLocalImageFragment(), true, true);
                break;
            case R.id.delete:
                deleteLaboryDialog.show();
                break;
        }
    }

    /**
     * 照相机
     */
    private void toCaramer() {
        choosedPop.dismiss();
        if (!Util.SDCardExists()) {
            showToast("存储卡不可用，暂无法完成拍照!");
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(fileDir, MyBitmapFactory.tempSelectBitmap.size() + ".png")));
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode != Activity.RESULT_OK) {
                return;
            }
            if (requestCode == 0) {
                ImageItem4LocalImage imageItem4LocalImage = new ImageItem4LocalImage();
                imageItem4LocalImage.imagePath = fileDirStr + File.separator + MyBitmapFactory.tempSelectBitmap.size() + ".png";
                MyBitmapFactory.tempSelectBitmap.add(imageItem4LocalImage);

            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast("图片选择出错");
        }
        selectedImageGridAdapter.notifyDataSetChanged();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onChange(DialogFragment dialog, int year, int month, int day) {
        Calendar selectedCalendar = Calendar.getInstance();
        selectedCalendar.set(Calendar.YEAR, year);
        selectedCalendar.set(Calendar.MONTH, month - 1);
        selectedCalendar.set(Calendar.DAY_OF_MONTH, day);
        if (selectedCalendar.after(Calendar.getInstance())) {
            showToast("不能选择未来时间哦");
            return;
        } else {
            String selectedTime = String.format(Locale.CHINA, "%d-%02d-%02d", year, month, day);
            timeStr = selectedTime;
            timeTv.setText(selectedTime);
        }
    }

    @Override
    public void onSussece(int what, byte[] b, boolean fromCache) {
        cancelProgressDialog();
        if (what == 3) {
            MyBitmapFactory.tempSelectBitmap.remove(deletePosition);
            selectedImageGridAdapter.notifyDataSetChanged();
            showToast("图片删除成功");
            CacheUtil.getInstance().putObjectById(UserMrg.getCacheKey(ConfigUrlMrg.LABORATOR_LIST), null);
            return;
        } else {// 删除
            try {
                ComveePacket packet = ComveePacket.fromJsonString(b);
                saveOrUpdateSave = packet.getJSONObject("body").getBoolean("success");
                if (saveOrUpdateSave) {
                    Bundle bundle = null;
                    if (what == 1) {// 修改
                        bundle = BundleHelper.getBundleByObject(laboratory);
                        bundle.putString("opt", "update");
                    } else if (what == 2) {// 删除
                        bundle = BundleHelper.getBundleByObject(laboratory);
                        bundle.putString("opt", "delete");
                    }
                    MyBitmapFactory.clearAll();
                    FragmentMrg.popBackToFragment(getActivity(), RecordLaboratoryFragment.class, bundle, true);
                    return;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        showToast("化验单上传失败，请重试~");

    }

    /**
     * 恢复按钮样式
     */
    void resetButtonStyle() {
        deleteBt.setBackgroundResource(R.drawable.button_red);
        deleteBt.setClickable(true);
    }

    @Override
    public void onFialed(int what, int errorCode) {
        cancelProgressDialog();
        resetButtonStyle();
        showToast("保存失败，请重新提交");
    }

    @Override
    public void onDelete(int position, ViewGroup parent) {
        deleteDialog.show();
        deletePosition = position;
    }

    @Override
    public boolean onBackPress() {

        boolean ischange = false;
        for (ImageItem4LocalImage image : MyBitmapFactory.tempSelectBitmap) {
            if (!TextUtils.isEmpty(image.imagePath)) {
                ischange = true;
                break;
            }
        }
        if (saveOrUpdateSave) {
            return false;
        }
        if (MyBitmapFactory.tempSelectBitmap.size() == 0) {
            showToast("请选择照片");
            return true;
        }
        if (isProgressDialogShowing()) {
            return true;
        }
        if (choosedPop.isShowing()) {
            choosedPop.dismiss();
            return true;
        } else if (leaveDialog.isShowing()) {
            leaveDialog.dismiss();
            return true;
        } else if (deleteLaboryDialog.isShowing()) {
            deleteLaboryDialog.dismiss();
            return true;
        } else if (deleteDialog.isShowing()) {
            deleteDialog.dismiss();
            return true;
        } else if (ischange || !initName.equals(nameEt.getText().toString()) || !initTime.equals(timeTv.getText().toString())) {
            if (isLeftClick) {
                return false;
            } else {
                leaveDialog.show();
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public void onDestroy() {
        timeStr = "";
        super.onDestroy();
    }
}
