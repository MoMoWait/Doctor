package com.comvee.tnb.ui.record.diet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.PopupWindow;

import com.comvee.frame.BaseFragmentActivity;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.CustomDialog;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
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
import com.comvee.ui.remind.TimeRemindUtil;
import com.comvee.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 修改食谱
 *
 * @author PXL
 */
@SuppressLint("ValidFragment")
public class UpdateDietFragment extends BaseFragment implements OnHttpListener, OnClickListener, OnItemClickListener, OnDeleteListener {
    public static final String fileDirStr = Environment.getExternalStorageDirectory() + File.separator + "temp";// 拍照存放的路径
    private Diet diet;
    private String from;
    private EditText mealContentEt;
    private Button deleteBt;
    private GridView gridViewSelected;
    private SelectedImageGridAdapter selectedImageGridAdapter;
    private PopupWindow choosePopupWindow;
    private CustomDialog leaveDialog;
    private CustomDialog deletePicDialog;
    private CustomDialog deleteDietDialog;
    private boolean saveOrUpdateSave = false;// 用来标记保存后或者删除操作后直接退出界面
    private boolean isLeftClick = false;
    private int deletePosition;
    private File fileDir;// 拍照保存在本地的图片的文件夹

    private TitleBarView mBarView;

    @Override
    public int getViewLayoutId() {
        return R.layout.input_update_diet_input_fragment;
    }

    @Override
    public void onLaunch(Bundle dataBundle) {
        super.onLaunch(dataBundle);
        this.diet = (Diet) dataBundle.getSerializable("currentDiet");
        this.from = dataBundle.getString("from");
        fileDir = new File(fileDirStr);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        findViewById();
        mBarView.setTitle(getString(R.string.record_food));
        mBarView.setRightButton(getText(R.string.save).toString(), this);
        if (TextUtils.isEmpty(diet.name))
            mealContentEt.setHint("暂无描述");
        else
            mealContentEt.setText(diet.name);

        initPopupWindow();

        selectedImageGridAdapter = new SelectedImageGridAdapter(getActivity(), MyBitmapFactory.tempSelectBitmap);
        selectedImageGridAdapter.setOnDeleteListener(this);
        gridViewSelected.setAdapter(selectedImageGridAdapter);
        gridViewSelected.setOnItemClickListener(this);
        deleteBt.setOnClickListener(this);
        AppUtil.registerEditTextListener1(mealContentEt, R.string.diet_nametip, 100, getApplicationContext());

    }

    @SuppressLint("InflateParams")
    @SuppressWarnings("deprecation")
    private void initPopupWindow() {
        CustomDialog.Builder leaveBuilder = new CustomDialog.Builder(getActivity());
        leaveBuilder.setMessage("该记录有修改，是否保存？").setPositiveButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialoginterface, int i) {
                leaveDialog.dismiss();
                isLeftClick = true;
                FragmentMrg.toBack(getActivity());
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
                deletePicDialog.dismiss();
            }
        }).setNegativeButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialoginterface, int i) {
                deletePicDialog.dismiss();
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
        deletePicDialog = deleteBuilder.create();

        CustomDialog.Builder deleteDietBuilder = new CustomDialog.Builder(getActivity());
        deleteDietBuilder.setMessage("是否确定删除该记录？").setPositiveButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialoginterface, int i) {
                deleteDietDialog.dismiss();
            }
        }).setNegativeButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialoginterface, int i) {
                deleteDietDialog.dismiss();
                deleteDiet();
            }
        });
        deleteDietDialog = deleteDietBuilder.create();

        View contentView = LayoutInflater.from(mContext).inflate(R.layout.pop_select_pic, null);
        choosePopupWindow = new PopupWindow(contentView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        choosePopupWindow.setBackgroundDrawable(new BitmapDrawable());
        choosePopupWindow.setAnimationStyle(R.style.PopupAnimation);
        contentView.findViewById(R.id.cancle).setOnClickListener(this);
        contentView.findViewById(R.id.pic).setOnClickListener(this);
        contentView.findViewById(R.id.camera).setOnClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        selectedImageGridAdapter.notifyDataSetChanged();
    }

    private void findViewById() {
        mealContentEt = (EditText) findViewById(R.id.name);
        deleteBt = (Button) findViewById(R.id.delete);
        gridViewSelected = (GridView) findViewById(R.id.gridview);
    }

    /**
     * 删除某张图片
     */
    protected void deletePic() {
        ((BaseFragmentActivity) getActivity()).showProgressDialog("正在提交中...");
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.DELETE_DIET_PIC);
        http.setPostValueForKey("picId", diet.netpics.get(deletePosition).picId);
        http.setOnHttpListener(3, UpdateDietFragment.this);
        http.startAsynchronous();
    }

    // 更新
    private void update() {
        if (TextUtils.isEmpty(mealContentEt.getText()) && MyBitmapFactory.tempSelectBitmap.size() == 0) {
            showToast("请先填写文字描述或者拍照上传");
            return;
        }
        try {
            if (mealContentEt.getText().toString().getBytes("GBK").length > 200) {
                showToast("食物描述不能超过100个字哦");
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
                        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.UPDATE_DIET);
                        JSONArray jsonArray = new JSONArray();
                        for (int i = 0; i < bigUrls.size(); i++) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("urlBig", bigUrls.get(i));
                            jsonArray.put(jsonObject);
                        }
                        http.setPostValueForKey("folderId", diet.id);
                        http.setPostValueForKey("folderName", mealContentEt.getText() + "");
                        http.setPostValueForKey("period", diet.period);
                        http.setPostValueForKey("photoPathJson", jsonArray.toString());

                        http.setOnHttpListener(1, UpdateDietFragment.this);
                        http.startAsynchronous();

                    } catch (Exception e) {
                        cancelProgressDialog();
                    }
                } else {
                    cancelProgressDialog();
                    showToast("饮食记录上传失败，请重试~");
                }

            }

        });
        uploadImageHelper.start();
    }

    // 删除
    protected void deleteDiet() {
        ((BaseFragmentActivity) getActivity()).showProgressDialog("正在提交中...");
        deleteBt.setBackgroundResource(R.drawable.btn_color_default);
        deleteBt.setClickable(false);
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.DELETE_DIET);
        http.setPostValueForKey("folderId", diet.id);
        http.setOnHttpListener(2, UpdateDietFragment.this);
        http.startAsynchronous();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == selectedImageGridAdapter.getCount() - 1 && MyBitmapFactory.tempSelectBitmap.size() != 9) {
            choosePopupWindow.showAtLocation(mRoot, Gravity.CENTER, 0, 0);
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
            case R.id.camera:
                toCaramer();
                break;
            case R.id.cancle:
                choosePopupWindow.dismiss();
                break;
            case R.id.pic:

                choosePopupWindow.dismiss();
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
                toFragment(ShowLocalImageFragment.class, null, true);
                break;
            case R.id.delete:
                deleteDietDialog.show();
                break;
            case TitleBarView.ID_RIGHT_BUTTON:
                update();
                break;
        }
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
    public void onSussece(int what, byte[] b, boolean fromCache) {
        cancelProgressDialog();
        try {
            ComveePacket packet = ComveePacket.fromJsonString(b);
            if (what == 3) {
                MyBitmapFactory.tempSelectBitmap.remove(deletePosition);
                selectedImageGridAdapter.notifyDataSetChanged();
                showToast("图片删除成功");

                return;
            } else if (what == 1) {// 更新
                saveOrUpdateSave = true;
                if ("index".equals(from)) {
                    FragmentMrg.toBack(getActivity(),new Bundle());
                } else {
                    FragmentMrg.popBackToFragment(getActivity(), HistoryDietFragment.class, new Bundle());
                }
                deleteallImageLoc();
            } else {// 删除
                saveOrUpdateSave = packet.getJSONObject("body").getBoolean("success");
                if (saveOrUpdateSave) {
                    if (!TextUtils.isEmpty(diet.id)) {
                        TimeRemindUtil util = TimeRemindUtil.getInstance(getApplicationContext());
                        for (int i = 1; i < 4; i++) {
                            String folderId = diet.id;
                            util.cancleDisposableAlarm((int) Long.parseLong(i
                                    + (folderId.length() > 8 ? folderId.substring(folderId.length() - 8, folderId.length()) : folderId)));
                        }
                    }
                    deleteallImageLoc();
                    FragmentMrg.toBack(getActivity(),new Bundle());
                    return;
                }
            }

        } catch (Exception e) {
            showToast("饮食记录上传失败，请重试~");
        }
    }

    /**
     * 恢复按钮样式
     */
    void resetButtonStyle() {
        deleteBt.setBackgroundResource(R.drawable.button_red1);
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
        deletePicDialog.show();
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
            deleteallImageLoc();
            return false;
        }

        if (choosePopupWindow.isShowing()) {
            choosePopupWindow.dismiss();
            return true;
        } else if (leaveDialog.isShowing()) {
            leaveDialog.dismiss();
            return true;
        } else if (deleteDietDialog.isShowing()) {
            deleteDietDialog.dismiss();
            return true;
        } else if (deletePicDialog.isShowing()) {
            deletePicDialog.dismiss();
            return true;
        } else if (ischange || !diet.name.equals(mealContentEt.getText().toString())) {
            if (isLeftClick) {
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

    private void deleteallImageLoc() {
        MyBitmapFactory.tempSelectBitmap.clear();
        for (int i = 0; i < 9; i++) {
            File file = new File(fileDirStr + File.separator + i + ".png");
            if (file.exists())
                file.delete();
        }
    }
}
