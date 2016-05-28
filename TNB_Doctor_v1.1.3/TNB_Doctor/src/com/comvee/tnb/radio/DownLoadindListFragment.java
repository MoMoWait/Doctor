package com.comvee.tnb.radio;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.db.DatabaseHelper;
import com.comvee.tnb.db.DownloadItemDao;
import com.comvee.tnb.dialog.CustomDialog;
import com.comvee.tnb.model.RadioAlbumItem;
import com.comvee.tnb.radio.DownLoadindAdapter.NotifyDatasetChangeInt;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.view.RadioPlayView;
import com.comvee.tnb.widget.TitleBarView;

import java.util.ArrayList;
import java.util.List;

/**
 * 前后台数据都通过本地库来中转。 所以不管是前台还是后台的下载记录变动 ，都第一时间更新到本地库中。
 *
 * @author Administrator
 */
public class DownLoadindListFragment extends BaseFragment implements OnClickListener, OnItemClickListener, NotifyDatasetChangeInt {
    private final int DELAYUPDATE = 1000;
    List<RadioAlbumItem> data;
    private DownLoadindAdapter mAdapter;
    private DownloadItemDao downloadItemDao;
    private SQLiteDatabase sqLiteDatabase;
    private TimerHandler timerHandler;// 用于定时更新下载任务状态
    private boolean isAllstart = true;
    private String dir = Environment.getExternalStorageDirectory() + "/comvee/radio";
    private TextView opTextView;
    private CustomDialog conitueDownLoadDialog, clearAllDialog;
    @Override
    public int getViewLayoutId() {
        return R.layout.downloading_local_fragment;
    }
    @Override
    public void onLaunch(Bundle dataBundle) {
        Intent intent = new Intent(getApplicationContext(), DownLoadService.class);
        getActivity().startService(intent);
        downloadItemDao = DownloadItemDao.getInstance();
        sqLiteDatabase = new DatabaseHelper(getApplicationContext()).getWritableDatabase();
        initTitleBarView();
        buildStartDialog();
        buildClearDialg();
        timerHandler = new TimerHandler();
        Message msg = timerHandler.obtainMessage();
        msg.what = 0;
        timerHandler.sendMessageDelayed(timerHandler.obtainMessage(), DELAYUPDATE);
        ListView listView = (ListView) findViewById(R.id.listview);
        // 首先查询本地库中所有非下载成功的记录
        data = downloadItemDao.getRaidosByWhere("downloadState not in ('5')");
        mAdapter = new DownLoadindAdapter(data, getApplicationContext());
        mAdapter.setNotifyDatasetChangeInt(this);
        listView.setAdapter(mAdapter);
        listView.addFooterView(new View(getApplicationContext()));
        listView.setOnItemClickListener(this);
        opTextView = (TextView) findViewById(R.id.optext);
        findViewById(R.id.all_start).setOnClickListener(this);
        findViewById(R.id.all_clear).setOnClickListener(this);
        RadioPlayView player = (RadioPlayView) findViewById(R.id.layout_play_bar);
        player.setListener(new RadioPlayView.PlayerViewListener() {
            @Override
            public void onToCurAlbum() {
                RadioPlayFrag.toFragment(getActivity(), RadioPlayerMrg.getInstance().getAlbum(), RadioPlayerMrg.getInstance().getCurrent());
            }

            @Override
            public void onStart(RadioAlbumItem item) {
            }

            @Override
            public void onPause(RadioAlbumItem item) {
            }
        });
    }

    private void initTitleBarView() {
        TitleBarView mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        View view = View.inflate(getApplicationContext(), R.layout.titlebar_follow_record, null);
        TextView left = (TextView) view.findViewById(R.id.tab_left);
        TextView right = (TextView) view.findViewById(R.id.tab_right);
        left.setText("已下载");
        right.setText("下载中");
        left.setOnClickListener(this);
        left.setBackgroundResource(R.drawable.jiankangzixun_07);
        right.setBackgroundResource(R.drawable.jiankangzixun_08);
        right.setTextColor(Color.WHITE);
        left.setTextColor(getResources().getColor(R.color.theme_color_green));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mBarView.addView(view, params);
    }

    public void buildStartDialog() {
        CustomDialog.Builder cotinueBuilder = new CustomDialog.Builder(getActivity());
        cotinueBuilder.setMessage("当前网络环境为wifi，是否继续下载？").setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialoginterface, int i) {
                conitueDownLoadDialog.dismiss();
            }
        }).setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(DialogInterface dialoginterface, int i) {
                int tagwhat = (Integer) conitueDownLoadDialog.getTag1();
                if (tagwhat == 1) {
                    RadioAlbumItem radioAlbumItem = (RadioAlbumItem) conitueDownLoadDialog.getTag();
                    DownLoadModel.addToDownLoad(radioAlbumItem, getApplicationContext(), sqLiteDatabase);
                } else if (tagwhat == 2) {
                    DownLoadModel.batchOp((ArrayList<RadioAlbumItem>) conitueDownLoadDialog.getTag(), false, getApplicationContext());
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
        conitueDownLoadDialog = cotinueBuilder.create();
    }

    private void buildClearDialg() {
        CustomDialog.Builder clearBuilder = new CustomDialog.Builder(getActivity());
        clearBuilder.setMessage("是否删除所有下载任务？").setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialoginterface, int i) {
                clearAllDialog.dismiss();
            }
        }).setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialoginterface, int i) {
                ArrayList<RadioAlbumItem> adapterdata = (ArrayList<RadioAlbumItem>) mAdapter.getData();
                DownLoadModel.deleteAllDownLoadingFile(adapterdata, downloadItemDao, dir);
                adapterdata.clear();
                timerHandler.removeMessages(0);
                mAdapter.notifyDataSetChanged();
            }
        });
        clearAllDialog = clearBuilder.create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_left:
                Bundle bundle = new Bundle();
                bundle.putBoolean("isSlingLeft", true);
                FragmentMrg.popBackToFragment(getActivity(), DownLoadLocalFragment.class, bundle, false);
                break;
            case R.id.all_start:
                ArrayList<RadioAlbumItem> list = (ArrayList<RadioAlbumItem>) mAdapter.getData();
                Message message = timerHandler.obtainMessage();
                message.what = 0;
                if (isAllstart) {// 点击全部暂停的逻辑
                    timerHandler.removeMessages(0);
                    DownLoadModel.batchOp(list, true, getApplicationContext());
                    mAdapter.notifyDataSetChanged();
                } else if (!DownLoadModel.isWifi(getApplicationContext())) {// 点击全部开始的逻辑
                    timerHandler.sendMessageDelayed(message, DELAYUPDATE);
                    conitueDownLoadDialog.setTag(list);
                    conitueDownLoadDialog.setTag1(2);
                    conitueDownLoadDialog.show();
                } else {
                    timerHandler.sendMessageDelayed(message, DELAYUPDATE);
                    DownLoadModel.batchOp(list, false, getApplicationContext());
                    mAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.all_clear:
                if (mAdapter.getCount() > 0) {
                    clearAllDialog.show();
                }
                break;
            case TitleBarView.ID_LEFT_BUTTON:
                getActivity().onBackPressed();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        RadioAlbumItem radioAlbumItem = mAdapter.getData().get(position);
        updateDownLoadState(radioAlbumItem, sqLiteDatabase);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 状态是等待下载时暂时忽略动作 状态是暂停和下载失败时转为下载中,暂停和下载中可能存在服务未开启，所以统一使用startService
     * 状态为下载中时转为暂停下载。
     *
     * @param radioAlbumItem
     * @param sqLiteDatabase
     */
    public void updateDownLoadState(RadioAlbumItem radioAlbumItem, SQLiteDatabase sqLiteDatabase) {
        try {
            String raidoId = radioAlbumItem.radioId;
            int currentState = radioAlbumItem.downloadState;
            switch (currentState) {
                case 2:
                    DownLoadService.shutdownByID(raidoId);
                    sqLiteDatabase.execSQL("update RadioAlbumItem set downloadState=3  where radioId='" + radioAlbumItem.radioId + "'");
                    radioAlbumItem.downloadState = 3;
                    break;
                case 3:
                case 4:
                    if (!DownLoadModel.isWifi(getApplicationContext())) {
                        conitueDownLoadDialog.setTag(radioAlbumItem);
                        conitueDownLoadDialog.setTag1(1);
                        conitueDownLoadDialog.show();
                        return;
                    }
                    DownLoadModel.addToDownLoad(radioAlbumItem, getApplicationContext(), sqLiteDatabase);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onBackPress() {
        IndexFrag.toFragment(getActivity(), true);
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        timerHandler.removeMessages(0);
        downloadItemDao.close();
        sqLiteDatabase.close();
    }

    @Override
    public void isAllPause(boolean allPause) {
        if (allPause) {
            opTextView.setText("全部开始");
        } else {
            opTextView.setText("全部暂停");
        }
        isAllstart = !allPause;
    }

    @SuppressLint("HandlerLeak")
    private class TimerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            List<RadioAlbumItem> radioAlbumItems = downloadItemDao.getDownloadUnComplectes();
            if (radioAlbumItems != null) {
                mAdapter.setdata(radioAlbumItems);
                mAdapter.notifyDataSetChanged();
                Message message = new Message();
                message.what = 0;
                timerHandler.sendMessageDelayed(message, DELAYUPDATE);
            }
        }
    }
}
