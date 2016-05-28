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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
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
import com.comvee.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * 新建化验单
 *
 * @author PXL
 */
@SuppressLint("ValidFragment")
public class CreateLaboratoryFragment extends BaseFragment implements OnHttpListener, OnClickListener, OnItemClickListener, OnDeleteListener,
        OnTimeChangeListener {
    public static final String fileDirStr = Environment.getExternalStorageDirectory() + File.separator + "temp";// 拍照存放的路径
    ;
    public static String timeStr = "";
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private EditText nameEt;
    private TextView timeTv;
    private GridView gridViewSelected;
    private SelectedImageGridAdapter selectedImageGridAdapter;
    private CustomDatePickDialog dialog;
    private File fileDir;// 拍照保存在本地的图片的文件夹

    private PopupWindow choosePopupWindow;

    private CustomDialog deletePicDialog;
    private CustomDialog leaveDialog;
    private ProgressBar loadingPb;
    private int deletePosition;
    private boolean saveSuceess = false;

    private boolean isRightClick = false;
    private TitleBarView mBarView;
    private String mServiceId;
    private CheckBox mCheck0;
    private boolean isCheckedID;
    private String mDoctorId;

    public CreateLaboratoryFragment() {
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.input_create_laboratory_input_fragment;
    }

    @Override
    public void onLaunch(Bundle bundle) {
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mCheck0 = (CheckBox) findViewById(R.id.check0);
        mBarView.setLeftDefault(this);
        mBarView.setRightButton(getText(R.string.save).toString(), this);
        findViewById();
        mBarView.setTitle(getString(R.string.record_report_title));
        initPopupWindow();
        fileDir = new File(fileDirStr);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        if (TextUtils.isEmpty(timeStr)) {
            timeTv.setText(sdf.format(new Date()));
        } else {
            timeTv.setText(timeStr);
        }
        dialog = new CustomDatePickDialog();
        dialog.setLimitTime(1890, 2190);
        dialog.setDefaultTime(Calendar.getInstance());

        selectedImageGridAdapter = new SelectedImageGridAdapter(getActivity(), MyBitmapFactory.tempSelectBitmap);
        selectedImageGridAdapter.setOnDeleteListener(this);
        gridViewSelected.setAdapter(selectedImageGridAdapter);
        gridViewSelected.setOnItemClickListener(this);

        AppUtil.registerEditTextListener1(nameEt, R.string.laboratory_nametip, 20, getApplicationContext());
        timeTv.setOnClickListener(this);
        dialog.setOnTimeChangeListener(this);

        initLoader();
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
        if (!TextUtils.isEmpty(mServiceId)) {
            mCheck0.setEnabled(true);
            mCheck0.setChecked(true);
        } else {
            mCheck0.setEnabled(false);
            mCheck0.setChecked(false);
        }
    }

    /**
     * 化验单解析ID
     */
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
                }
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void initPopupWindow() {
        CustomDialog.Builder leaveBuilder = new CustomDialog.Builder(getActivity());
        leaveBuilder.setMessage("是否放弃此次编辑？").setPositiveButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialoginterface, int i) {
                leaveDialog.dismiss();
            }
        }).setNegativeButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialoginterface, int i) {
                leaveDialog.dismiss();
                isRightClick = true;
                FragmentMrg.toBack(getActivity());
            }
        });
        leaveDialog = leaveBuilder.create();

        CustomDialog.Builder deletePicBuilder = new CustomDialog.Builder(getActivity());
        deletePicBuilder.setMessage("是否确定删除该照片？").setPositiveButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialoginterface, int i) {
                deletePicDialog.dismiss();
            }
        }).setNegativeButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialoginterface, int i) {
                deletePicDialog.dismiss();
                ImageItem4LocalImage image = MyBitmapFactory.tempSelectBitmap.get(deletePosition);
                MyBitmapFactory.tempSelectBitmap.remove(image);
                selectedImageGridAdapter.notifyDataSetChanged();
            }
        });
        deletePicDialog = deletePicBuilder.create();

        View contentView = LayoutInflater.from(mContext).inflate(R.layout.pop_select_pic, null);
        choosePopupWindow = new PopupWindow(contentView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        choosePopupWindow.setBackgroundDrawable(new BitmapDrawable());
        choosePopupWindow.setAnimationStyle(R.style.PopupAnimation);
        contentView.findViewById(R.id.cancle).setOnClickListener(this);
        contentView.findViewById(R.id.pic).setOnClickListener(this);
        contentView.findViewById(R.id.camera).setOnClickListener(this);

        View contentView1 = LayoutInflater.from(mContext).inflate(R.layout.pop_uploading, null);
        loadingPb = (ProgressBar) contentView1.findViewById(R.id.pb_loading);
    }

    @Override
    public void onResume() {
        super.onResume();
        selectedImageGridAdapter.notifyDataSetChanged();
    }

    private void findViewById() {
        nameEt = (EditText) findViewById(R.id.name);
        timeTv = (TextView) findViewById(R.id.time);
        gridViewSelected = (GridView) findViewById(R.id.gridview);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == selectedImageGridAdapter.getCount() - 1 && MyBitmapFactory.tempSelectBitmap.size() != 9) {
            choosePopupWindow.showAtLocation(mRoot, Gravity.CENTER, 0, 0);
        } else {
            List<NetPic> datas = new ArrayList<NetPic>();
            for (ImageItem4LocalImage image : MyBitmapFactory.tempSelectBitmap) {
                NetPic netPic = new NetPic();
                netPic.localPath = image.imagePath;
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
                choosePopupWindow.dismiss();
                break;
            case R.id.pic:
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
                choosePopupWindow.dismiss();
                break;
            case TitleBarView.ID_RIGHT_BUTTON:
                saveData();
                break;
        }
    }

    private void saveData() {
        if (TextUtils.isEmpty(nameEt.getText())) {
            showToast("请填写化验单名称");
            return;
        }
        if (MyBitmapFactory.tempSelectBitmap.size() == 0) {
            showToast("请选择照片");
            return;
        }
        ((BaseFragmentActivity) getActivity()).showProgressDialog("正在提交中...");
        selectedImageGridAdapter.notifyDataSetChanged();
        UploadImageHelper1 uploadImageHelper = new UploadImageHelper1(mContext, MyBitmapFactory.tempSelectBitmap);
        uploadImageHelper.setAllfishListener(new ImageUploadFinishListener() {

            @Override
            public void allFinish(List<String> bigUrls) {
                boolean allSuccess = true;
                for (String item : bigUrls) {
                    if (TextUtils.isEmpty(item)) {
                        allSuccess = false;
                        break;
                    }
                }
                // 图片全部上传阿里成功后上传服务器
                if (allSuccess) {
                    try {
                        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.UPLOAD_LABORATOR);
                        JSONArray jsonArray = new JSONArray();
                        for (int i = 0; i < bigUrls.size(); i++) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("urlBig", bigUrls.get(i));
                            jsonArray.put(jsonObject);
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
                        String time = timeTv.getText() + " " + sdf.format(new Date());
                        http.setPostValueForKey("folderName", nameEt.getText() + "");
                        http.setPostValueForKey("dateTime", time);
                        http.setPostValueForKey("photoPathJson", jsonArray.toString());
                        if (!TextUtils.isEmpty(mServiceId)&& isCheckedID&&!TextUtils.isEmpty(mDoctorId)) {
                            http.setPostValueForKey("answerServiceId", mServiceId);
                            http.setPostValueForKey("doctorId", mDoctorId);
                        }
                        http.setOnHttpListener(1, CreateLaboratoryFragment.this);
                        http.startAsynchronous();

                    } catch (Exception e) {
                        showToast("化验单上传失败，请重试~");
                        Log.e("tag", e.getMessage(), e);
                    }
                } else {
                    cancelProgressDialog();
                    loadingPb.clearAnimation();
                    showToast("化验单上传失败，请重试~");
                }

            }
        });
        uploadImageHelper.start();
    }

    /**
     * 照相机
     */
    private void toCaramer() {
        choosePopupWindow.dismiss();
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
        try {
            ComveePacket packet = ComveePacket.fromJsonString(b);
            boolean isSuccess = packet.getJSONObject("body").getBoolean("success");
            if (isSuccess) {
                cancelProgressDialog();
                loadingPb.clearAnimation();
                saveSuceess = true;
                FragmentMrg.toBack(getActivity(), new Bundle());
                return;
            }
        } catch (Exception e) {
            showToast("化验单上传失败，请重试~");
            e.printStackTrace();
        }
        cancelProgressDialog();
        loadingPb.clearAnimation();
        loadingPb.clearAnimation();
        showToast("化验单上传失败，请重试~");
    }

    @Override
    public void onFialed(int what, int errorCode) {
        cancelProgressDialog();
        loadingPb.clearAnimation();
        showToast("化验单上传失败，请重试~");
    }

    @Override
    public void onDelete(int position, ViewGroup parent) {
        deletePosition = position;
        deletePicDialog.show();
    }

    @Override
    public boolean onBackPress() {
        if (isProgressDialogShowing()) {
            return true;
        }
        if (saveSuceess) {
            deleteallImageLoc();
            return false;
        }

        if (choosePopupWindow.isShowing()) {
            choosePopupWindow.dismiss();
            return true;
        } else if (leaveDialog.isShowing()) {
            leaveDialog.dismiss();
            return true;
        } else if (!TextUtils.isEmpty(nameEt.getText()) || MyBitmapFactory.tempSelectBitmap.size() > 0) {
            if (isRightClick) {
                deleteallImageLoc();
                return false;
            } else {
                leaveDialog.show();
                return true;
            }
        } else {
            deleteallImageLoc();
            return false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("date", timeTv.getText() + "");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        timeStr = "";
        super.onDestroy();
    }

    private void deleteallImageLoc() {
        MyBitmapFactory.clearAll();
        for (int i = 0; i < 9; i++) {
            File file = new File(fileDirStr + File.separator + i + ".png");
            if (file.exists())
                file.delete();
        }
    }

}
