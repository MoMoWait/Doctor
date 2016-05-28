package com.comvee.tnb.ui.record.common;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.AppUtil;

/**
 * 查看相册
 *
 * @author PXL
 */
@SuppressLint("ValidFragment")
public class ShowLocalImageFragment extends BaseFragment implements OnClickListener, OnItemClickListener {

    private final int gridviewColumnNum = 3;
    private GridView gridView;
    private AlbumGridViewAdapter gridImageAdapter;
    private TextView previewTv;
    private TextView finishTv;

    private PopupWindow popupWindow;
    private TitleBarView mBarView;
    private int limitSelectCount = 9;// 选择照片限制数量,默认9张

    public ShowLocalImageFragment() {
    }

    public ShowLocalImageFragment(int countlimit) {
        this.limitSelectCount = countlimit;
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.show_local_image;
    }

    @Override
    public void onLaunch(Bundle bundle) {
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        findViewById();
        mBarView.setLeftButton("图库", this);
        mBarView.setRightButton("取消", this);

        initPopupWindow();

        previewTv.setOnClickListener(this);
        finishTv.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        if (!MyBitmapFactory.canback) {
            getActivity().onBackPressed();
        }
        float density = AppUtil.getDeviceDensity(getActivity());
        float spacing = density * (4 * 2);
        int itemGridViewHeight = (int) ((AppUtil.getScreenWidth(getActivity()) - spacing) / gridviewColumnNum);
        gridImageAdapter = new AlbumGridViewAdapter(itemGridViewHeight, getApplicationContext(), MyBitmapFactory.tempAllImage);
        gridView.setAdapter(gridImageAdapter);
        gridView.setOnItemClickListener(this);
        mBarView.setTitle(MyBitmapFactory.albumnName);

        String beFinish = MyBitmapFactory.tempSelectBitmapInAlbum.size() + "/" + (limitSelectCount - MyBitmapFactory.tempSelectBitmap.size());
        finishTv.setText("完成(" + beFinish + ")");

        super.onResume();
    }

    private void initPopupWindow() {
        String beFinish = limitSelectCount - MyBitmapFactory.tempSelectBitmap.size() + "张";
        popupWindow = CustomerPopupWindow.getPopupWindowOneButton(mContext, "确定", "#0b8ef4", "最多只能添加" + beFinish, this);
    }

    private void findViewById() {
        gridView = (GridView) findViewById(R.id.gridView);
        previewTv = (TextView) findViewById(R.id.preview);
        finishTv = (TextView) findViewById(R.id.finish);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        ImageItem4LocalImage imageItem = MyBitmapFactory.tempAllImage.get(position);
        imageItem.uploadState = 1;
        if (MyBitmapFactory.tempSelectBitmapInAlbum.contains(imageItem)) {
            MyBitmapFactory.tempSelectBitmapInAlbum.remove(imageItem);
        } else {
            if (MyBitmapFactory.tempSelectBitmapInAlbum.size() + MyBitmapFactory.tempSelectBitmap.size() == limitSelectCount) {
                popupWindow.showAtLocation(mRoot, Gravity.CENTER, 0, 0);
                return;
            }
            ImageView imageView = (ImageView) view.findViewById(R.id.image);
            imageItem.drawableThumb = imageView.getDrawable();
            MyBitmapFactory.tempSelectBitmapInAlbum.add(imageItem);
        }

        String beFinish = MyBitmapFactory.tempSelectBitmapInAlbum.size() + "/" + (limitSelectCount - MyBitmapFactory.tempSelectBitmap.size());
        finishTv.setText("完成(" + beFinish + ")");
        gridImageAdapter.notifyDataSetChanged();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.preview:
                if (MyBitmapFactory.tempSelectBitmapInAlbum.size() == 0) {
                    showToast("请选择需要预览的照片");
                } else {
                    Intent it = new Intent(getActivity(), ShowSelectedViewPagerActivity.class);
                    it.putExtra("current", 0);
                    it.putExtra("type", 1);
                    startActivity(it);
                }
                break;
            case R.id.finish:
                MyBitmapFactory.tempSelectBitmap.addAll(MyBitmapFactory.tempSelectBitmapInAlbum);
                getActivity().onBackPressed();
                break;
            case TitleBarView.ID_LEFT_BUTTON:
                toFragment(new ShowAlbumFragment(), true, true);
                break;
            case TitleBarView.ID_RIGHT_BUTTON:
                MyBitmapFactory.tempSelectBitmapInAlbum.clear();
                getActivity().onBackPressed();
                break;
            case CustomerPopupWindow.TEXT_ID:
                popupWindow.dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
    }

    @Override
    public boolean onBackPress() {
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
            return true;
        } else {
            if (MyBitmapFactory.canback) {
                MyBitmapFactory.tempSelectBitmapInAlbum.clear();
            }
            return false;
        }
    }

}
