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
import com.comvee.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * 新建食谱
 */
@SuppressLint("ValidFragment")
public class CreateDietFragment extends BaseFragment implements OnHttpListener, OnClickListener, OnItemClickListener, OnDeleteListener {

    private static final String fileDirStr = Environment.getExternalStorageDirectory() + File.separator + "temp";// 拍照存放的路径
    private EditText mealContentTv;
    private int mealTimeType = 1;// 1早餐2午餐3晚餐
    private String from;
    private GridView gridViewSelected;
    private SelectedImageGridAdapter selectedImageGridAdapter;
    private File fileDir;// 拍照保存在本地的图片的文件夹

    private PopupWindow choosePopupWindow;

    private CustomDialog leaveDialog;
    private CustomDialog deletePicDialog;
    private int deletePosition;

    private boolean isRightClick = false;
    private TitleBarView mBarView;

    @Override
    public int getViewLayoutId() {
        return R.layout.input_create_diet_input_fragment;
    }

    @Override
    public void onLaunch(Bundle dataBundle) {
        super.onLaunch(dataBundle);
        fileDir = new File(fileDirStr);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        findViewById();
        mBarView.setLeftDefault(this);
        mBarView.setTitle(getString(R.string.record_food));
        mBarView.setRightButton(getText(R.string.save).toString(), this);
        mealTimeType = dataBundle.getInt("mealTimeType", 1);
        from = dataBundle.getString("from");
        initPopupWindow();

        selectedImageGridAdapter = new SelectedImageGridAdapter(getActivity(), MyBitmapFactory.tempSelectBitmap);
        selectedImageGridAdapter.setOnDeleteListener(this);
        gridViewSelected.setAdapter(selectedImageGridAdapter);
        gridViewSelected.setOnItemClickListener(this);

        AppUtil.registerEditTextListener1(mealContentTv, R.string.diet_nametip, 100, getApplicationContext());

    }

    private void findViewById() {
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mealContentTv = (EditText) findViewById(R.id.name);
        gridViewSelected = (GridView) findViewById(R.id.gridview);
    }

    @SuppressLint("InflateParams")
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

    }

    @Override
    public void onResume() {
        super.onResume();
        selectedImageGridAdapter.notifyDataSetChanged();
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
                toFragment(ShowLocalImageFragment.class, null, true);
                choosePopupWindow.dismiss();
                break;
            case TitleBarView.ID_RIGHT_BUTTON:
                saveData();
                break;
        }
    }

    private void saveData() {
        if (TextUtils.isEmpty(mealContentTv.getText()) && MyBitmapFactory.tempSelectBitmap.size() == 0) {
            showToast("请先填写文字描述或者拍照上传");
            return;
        }
        try {
            if (mealContentTv.getText().toString().getBytes("GBK").length > 200) {
                showToast("食物描述不能超过100个字哦");
                return;
            }
        } catch (UnsupportedEncodingException e1) {
            Log.e("tag", e1.getMessage(), e1);
        }
        ((BaseFragmentActivity) getActivity()).showProgressDialog("正在提交中...");
        selectedImageGridAdapter.notifyDataSetChanged();
        UploadImageHelper1 uploadImageHelper = new UploadImageHelper1(mContext, MyBitmapFactory.tempSelectBitmap);
        uploadImageHelper.setAllfishListener(new ImageUploadFinishListener() {

            @Override
            public void allFinish(List<String> bigUrls) {
                boolean allSuccess = true;
                if (MyBitmapFactory.tempSelectBitmap.size() > 0)
                    if (bigUrls.size() == 0)
                        allSuccess = false;
                for (String item : bigUrls) {
                    if (TextUtils.isEmpty(item)) {
                        allSuccess = false;
                        break;
                    }
                }
                // 图片全部上传阿里成功后上传服务器
                if (allSuccess) {
                    try {
                        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.UPLOAD_DIET);
                        JSONArray jsonArray = new JSONArray();
                        for (int i = 0; i < bigUrls.size(); i++) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("urlBig", bigUrls.get(i));
                            jsonArray.put(jsonObject);
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                        http.setPostValueForKey("dateTime", sdf.format(new Date()));
                        http.setPostValueForKey("folderName", mealContentTv.getText() + "");
                        http.setPostValueForKey("period", mealTimeType + "");
                        http.setPostValueForKey("photoPathJson", jsonArray.toString());
                        http.setPostValueForKey("isVaild", 1 + "");
                        http.setOnHttpListener(1, CreateDietFragment.this);
                        http.startAsynchronous();

                    } catch (Exception e) {
                        cancelProgressDialog();
                        showToast("饮食记录上传失败，请重试~");
                        Log.e("tag", e.getMessage(), e);
                    }
                } else {
                    cancelProgressDialog();
                    showToast("饮食记录上传失败，请重试~");
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
    public void onSussece(int what, byte[] b, boolean fromCache) {
        cancelProgressDialog();
        try {
            ComveePacket packet = ComveePacket.fromJsonString(b);
            JSONObject jsonObject = packet.getJSONObject("body");
            boolean isSuccess = jsonObject.getBoolean("success");
            if (isSuccess) {
                JSONObject objJSonObject = jsonObject.optJSONObject("obj");
                int resultType = objJSonObject.getInt("type");
                String resultContent = objJSonObject.getString("showText");
                String folderId = objJSonObject.optString("folderId");
                Bundle bundle = new Bundle();
                bundle.putString("mealContent", mealContentTv.getText() + "");
                bundle.putInt("mealTimeType", mealTimeType);// 用餐时间
                bundle.putString("resultContent", resultContent);
                bundle.putInt("resultType", resultType);// 返回类型1学习，3去设置，4去交换，5查询
                bundle.putString("from", from);
                bundle.putString("folderId", folderId);
                if (resultType == 1) {
                    JSONArray subJsonArray = jsonObject.getJSONObject("obj").getJSONObject("wikiModel").getJSONArray("subMap");
                    String subTitle = jsonObject.getJSONObject("obj").getJSONObject("wikiModel").getString("title");
                    ArrayList<HashMap<String, String>> subDatas = new ArrayList<HashMap<String, String>>();
                    for (int i = 0; i < subJsonArray.length(); i++) {
                        JSONObject item = subJsonArray.getJSONObject(i);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("title", item.getString("title"));
                        map.put("digest", item.getString("digest"));
                        map.put("url", item.getString("url"));
                        map.put("id", item.getString("id"));
                        subDatas.add(map);
                    }
                    bundle.putSerializable("subDatas", subDatas);
                    bundle.putString("subTitle", subTitle);
                    bundle.putString("folderId", folderId);

                }
                toFragment(EditResultFragment.class, bundle, true);
                return;
            }
        } catch (Exception e) {
            showToast("饮食记录上传失败，请重试~");
            e.printStackTrace();
        }
        showToast("饮食记录上传失败，请重试~");
    }

    @Override
    public void onFialed(int what, int errorCode) {
        cancelProgressDialog();
        showToast("饮食记录上传失败，请重试~");
    }

    @Override
    public void onDelete(int position, ViewGroup parent) {
        deletePosition = position;
        deletePicDialog.show();
    }

    @Override
    public boolean onBackPress() {
        if (isProgressDialogShowing())
            return true;
        cancelProgressDialog();
        if (choosePopupWindow.isShowing()) {
            choosePopupWindow.dismiss();
            return true;
        } else if (leaveDialog.isShowing()) {
            leaveDialog.dismiss();
            return true;
        } else if (!TextUtils.isEmpty(mealContentTv.getText()) || MyBitmapFactory.tempSelectBitmap.size() > 0) {
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

    private void deleteallImageLoc() {
        MyBitmapFactory.clearAll();
        for (int i = 0; i < 9; i++) {
            File file = new File(fileDirStr + File.separator + i + ".png");
            if (file.exists())
                file.delete();
        }
    }

}
