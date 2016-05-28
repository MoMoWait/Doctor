package com.comvee.tnb.radio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comvee.ThreadHandler;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.db.DownloadIChannelDao;
import com.comvee.tnb.db.DownloadItemDao;
import com.comvee.tnb.dialog.CustomDialog;
import com.comvee.tnb.model.RadioAlbumItem;
import com.comvee.tnb.model.RadioGroup.RadioAlbum;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.view.RadioPlayView;
import com.comvee.tnb.view.RadioPlayView.PlayerViewListener;
import com.comvee.tnb.widget.TitleBarView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownLoadLocalFragment extends BaseFragment implements OnClickListener, AdapterView.OnItemClickListener {
    public final static String DOWNLOAD_FINISH = "DOWNLOAD_FINISH";
    private CustomDialog deletePicDialog;
    private ListView listView;
    private Cursor mCursor;//专辑指针，用于adpter
    private MyCursorAdapter cursorAdapter;
    private BroadcastReceiver broadcastReceiver;
    private boolean isSlingLeft;
    private RadioPlayView mPlayView;
    private TitleBarView mBarView;
    private TextView tv_select;
    private boolean isEditting;// 是否编辑状态
    private View btnSelect, btnDelete, playBar, headBtn, groupEditting;
    private List<RadioAlbum> mDeleteList = new ArrayList<RadioAlbum>();
    private boolean isSelectAllDeleted;
    private ArrayList<RadioAlbumItem> mRadioAlbumItems = new ArrayList<RadioAlbumItem>();//以下载的所有节目
    private Map<String, RadioAlbumItem> radioMap = new HashMap<String, RadioAlbumItem>();//节目对应的专辑
    private ImageView mSelectImg;

    @Override
    public int getViewLayoutId() {
        return R.layout.download_local_fragment;
    }

    @Override
    public void onLaunch(Bundle dataBundle) {
        if (dataBundle != null) {
            isSlingLeft = dataBundle.getBoolean("isSlingLeft");
        }
        initTitleBarView();
        initPlay();
        listView = (ListView) findViewById(R.id.listview);
        findViewById(R.id.btn_editting).setOnClickListener(this);
        btnDelete = findViewById(R.id.btn_delete);
        mSelectImg = (ImageView) findViewById(R.id.btn_select_img);
        btnSelect = findViewById(R.id.btn_select);
        playBar = findViewById(R.id.layout_play_bar);
        headBtn = findViewById(R.id.group_btn);
        tv_select = (TextView) findViewById(R.id.tv_select);
        groupEditting = findViewById(R.id.group_editting);
        btnDelete.setOnClickListener(this);
        btnSelect.setOnClickListener(this);
        mCursor = DownloadIChannelDao.getInstance().getCursor();
        cursorAdapter = new MyCursorAdapter(getApplicationContext(), mCursor, true);
        listView.setAdapter(cursorAdapter);
        listView.setOnItemClickListener(this);
        getRadioAlbumItems();//获取所有的下载节目
        listView.addFooterView(new View(getApplicationContext()));
    }

    private void initPlay() {
        mPlayView = (RadioPlayView) findViewById(R.id.layout_play_bar);
        mPlayView.setListener(new PlayerViewListener() {
            @Override
            public void onToCurAlbum() {
                RadioPlayFrag.toFragment(getActivity(), RadioPlayerMrg.getInstance().getAlbum(), RadioPlayerMrg.getInstance().getCurrent());

            }

            @Override
            public void onStart(RadioAlbumItem item) {
                if (cursorAdapter != null) {
                    mPlayView.setLabel(item.radioTitle);
                    datachange();
                }
            }

            @Override
            public void onPause(RadioAlbumItem item) {
                if (cursorAdapter != null)
                    datachange();
            }
        });
    }

    private void registerBrocast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DOWNLOAD_FINISH);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                datachange();
            }
        };
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    private void datachange() {
        mCursor = DownloadIChannelDao.getInstance().getCursor();
        cursorAdapter = new MyCursorAdapter(getApplicationContext(), mCursor, true);
        listView.setAdapter(cursorAdapter);
        cursorAdapter.notifyDataSetInvalidated();
    }

    private void initTitleBarView() {
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.titlebar_follow_record, null);
        TextView left = (TextView) view.findViewById(R.id.tab_left);
        TextView right = (TextView) view.findViewById(R.id.tab_right);
        left.setBackgroundResource(R.drawable.jiankangzixun_03);
        right.setBackgroundResource(R.drawable.jiankangzixun_04);
        left.setText("已下载");
        right.setText("下载中");
        left.setTextColor(Color.WHITE);
        right.setTextColor(getResources().getColor(R.color.theme_color_green));
        right.setOnClickListener(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mBarView.addView(view, params);
        mBarView.setTitle("");
    }

    /**
     * 显示删除提示窗口
     */
    private void showDeleteDialog() {
        if (mDeleteList.isEmpty()) {
            showToast("请选择要删除的节目");
            return;
        }

        if (deletePicDialog != null && deletePicDialog.isShowing()) {
            return;
        }
        CustomDialog.Builder deleteBuilder = new CustomDialog.Builder(getActivity());
        deleteBuilder.setMessage("删除所有下载的节目？").setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialoginterface, int i) {
                deletePicDialog.cancel();
            }
        }).setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialoginterface, int i) {
                RadioPlayerMrg mrg = RadioPlayerMrg.getInstance();
                mrg.stop();
                for (int j = 0; j < mDeleteList.size(); j++) {
                    DownloadMrg.deleteAlbum(mDeleteList.get(j));
                    mRadioAlbumItems.remove(radioMap.get(mDeleteList.get(j)._id));
                    radioMap.remove(mDeleteList.get(j)._id);
                }
                deletePicDialog.cancel();
                datachange();
                showToast("删除成功");
                cancleEditting();
                mrg.setDataSource(null, mRadioAlbumItems, true);
            }
        });
        deletePicDialog = deleteBuilder.create();
        deletePicDialog.show();
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.tab_right:
                Bundle bundle = new Bundle();
                bundle.putBoolean("isSlingLeft", true);
                toFragment(DownLoadindListFragment.class, bundle, false);
                break;
            case TitleBarView.ID_LEFT_BUTTON:
                if (isSlingLeft) {
                    getActivity().onBackPressed();
                } else {
                    FragmentMrg.toBack(getActivity());
                }
                break;
            case R.id.btn_editting:
                //如果列表无数据，不进入编辑模式
                if (cursorAdapter.isEmpty()) {
                } else {
                    if (!isEditting){
                        selectEditting();
                    }else{
                        cancleEditting();
                    }
                }
                break;
            case TitleBarView.ID_RIGHT_BUTTON://取消编辑状态
                if (mDeleteList != null) {
                    mDeleteList.removeAll(mDeleteList);
                }
                cancleEditting();
                break;
            case R.id.btn_select://全选 删除
                selectAllToDelete();
                break;
            case R.id.btn_delete:
                //弹出删除提示框
                showDeleteDialog();
                break;
            default:
                break;
        }
    }

    /**
     * 全选 准备删除
     */
    private void selectAllToDelete() {
        isSelectAllDeleted = !isSelectAllDeleted;
        if (isSelectAllDeleted) {
            int len = cursorAdapter.getCount();
            for (int i = 0; i < len; i++) {
                RadioAlbum mItem = DownloadIChannelDao.getInfoByCursor((Cursor) cursorAdapter.getItem(i));//获取专辑
                mDeleteList.add(mItem);
            }
        } else {
            if (mDeleteList != null) {
                mDeleteList.removeAll(mDeleteList);
            }
        }
        cursorAdapter.notifyDataSetChanged();
        mSelectImg.setImageResource(isSelectAllDeleted?R.drawable.cb_check_1:R.drawable.cb_uncheck_1);
        tv_select.setText(isSelectAllDeleted ? getString(R.string.select_all) : getString(R.string.select_all));
    }

    /**
     * 进入编辑模式
     */
    private void selectEditting() {
        isEditting = true;
        cursorAdapter.setShowSelect(true);
        cursorAdapter.notifyDataSetChanged();
       // mBarView.setRightButton(getString(R.string.cancel), this);
        playBar.setVisibility(View.GONE);
        btnDelete.setVisibility(View.VISIBLE);
        btnSelect.setVisibility(View.VISIBLE);
      //  groupEditting.setVisibility(View.GONE);
    }

    /**
     * 取消编辑模式
     */
    private void cancleEditting() {
        isEditting = false;
        if (mDeleteList != null) {
            mDeleteList.removeAll(mDeleteList);
        }
        cursorAdapter.setShowSelect(false);
        cursorAdapter.notifyDataSetChanged();
        mBarView.hideRightButton();
        btnDelete.setVisibility(View.GONE);
        btnSelect.setVisibility(View.GONE);
        playBar.setVisibility(View.VISIBLE);
        groupEditting.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        registerBrocast();// 注册广播，用于即时更新下载完毕后节目的数量
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            cancleEditting();
            getActivity().unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onFragmentResult(Bundle data) {
        super.onFragmentResult(data);
        mCursor = DownloadIChannelDao.getInstance().getCursor();
        cursorAdapter = new MyCursorAdapter(getApplicationContext(), mCursor, true);
        listView.setAdapter(cursorAdapter);
        getRadioAlbumItems();//获取所有的下载节目
    }

    @Override
    public boolean onBackPress() {
        IndexFrag.toFragment(getActivity(), true);
        return true;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            Cursor cur = (Cursor) cursorAdapter.getItem(position);
            final RadioAlbum mItem = DownloadIChannelDao.getInfoByCursor(cur);//获取专辑
            if (isEditting) {//是否是编辑模式
                if (mDeleteList.contains(mItem)) {//是否已经被选中删除
                    ((CheckBox) view.findViewById(R.id.checkbox)).setChecked(false);
                    mDeleteList.remove(mItem);
                } else {
                    ((CheckBox) view.findViewById(R.id.checkbox)).setChecked(true);
                    mDeleteList.add(mItem);
                }
            } else {
                final int count = DownloadItemDao.getInstance().getDownloadComplecteCount(mItem._id);//获取当前专辑中的节目个数
                if (mItem.programType == 1 || count > 1) {// 专辑则跳转
                    DownLoadCompleteFrag.toFragment(getActivity(), mItem);
                } else if (radioMap != null) {// 节目则播放
                    //获取专辑中的节目
                    RadioPlayerMrg mrg = RadioPlayerMrg.getInstance();
                    mrg.setDataSource(mItem, mRadioAlbumItems, true);
                    if (mRadioAlbumItems.size() == listView.getCount()-1) {
                        //listView如果只有节目
                        mrg.play(position);
                    } else {
                        //listView 用专辑过滤专辑取出相应的节目
                        ArrayList<RadioAlbumItem> raidosByWhere = DownloadItemDao.getInstance().getRaidosByWhere("refId=" + mItem.radioId);
                        RadioAlbumItem radioAlbumItem = radioMap.get(mItem._id);
                        if (raidosByWhere.get(0)._id.equals(radioAlbumItem._id)) {
                            for (int i = 0; i < mRadioAlbumItems.size(); i++) {
                                if (mRadioAlbumItems.get(i)._id.equals(radioAlbumItem._id)) {
                                    mrg.play(i);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void getRadioAlbumItems() {
        cursorAdapter.notifyDataSetChanged();
        mRadioAlbumItems.clear();
        if (listView != null && cursorAdapter != null) {
            for (int i = 0; i < listView.getCount(); i++) {
                Cursor cur = (Cursor) cursorAdapter.getItem(i);
                final RadioAlbum mItem = DownloadIChannelDao.getInfoByCursor(cur);//获取专辑
                final int count = DownloadItemDao.getInstance().getDownloadComplecteCount(mItem._id);//获取当前专辑中的节目个数
                if (count == 1) {
                    mRadioAlbumItems.add(DownloadItemDao.getInstance().getRaidosByWhere("refId=" + mItem.radioId).get(0));
                    radioMap.put(mItem._id, DownloadItemDao.getInstance().getRaidosByWhere("refId=" + mItem.radioId).get(0));
                }else{
                    ArrayList<RadioAlbumItem> raidosByWhere = DownloadItemDao.getInstance().getRaidosByWhere("refId=" + mItem.radioId);
                    for (int j=0;j<raidosByWhere.size();j++){
                        mRadioAlbumItems.add(DownloadItemDao.getInstance().getRaidosByWhere("refId=" + mItem.radioId).get(j));
                        radioMap.put(mItem._id,raidosByWhere.get(j));
                    }
                }
            }
        }

    }

    private class MyCursorAdapter extends CursorAdapter {
        private boolean isShowSelect;//true为编辑模式，false为非编辑模式

        public MyCursorAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        @Override
        public View newView(Context ctx, Cursor arg1, ViewGroup arg2) {
            View view = View.inflate(ctx, R.layout.downloaded_item, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.contentView = view;
            viewHolder.checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            viewHolder.chanelTitleTv = (TextView) view.findViewById(R.id.title);
            viewHolder.chanelSubTv = (TextView) view.findViewById(R.id.sub);
            viewHolder.itemCountTv = (TextView) view.findViewById(R.id.count);
            viewHolder.deleteButton = (TextView) view.findViewById(R.id.delete);
            view.setTag(viewHolder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, final Cursor cursor) {
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            final RadioAlbum mItem = DownloadIChannelDao.getInfoByCursor(cursor);//获取专辑
            String title = mItem.radioTitle;
            String abstractInfo = mItem.radioSubhead;
            final String id = mItem.radioId;
            final int count = DownloadItemDao.getInstance().getDownloadComplecteCount(id);
            viewHolder.chanelTitleTv.setText(title);
            viewHolder.chanelSubTv.setText(abstractInfo);
            if (count > 1) {// 专辑
                viewHolder.itemCountTv.setText(count + "节目");
            } else {// 节目
                viewHolder.itemCountTv.setText("");
            }
            if (isShowSelect) {
                viewHolder.checkBox.setVisibility(View.VISIBLE);
                viewHolder.checkBox.setChecked(mDeleteList.contains(mItem));
                viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton arg0, boolean checked) {
                        if (checked) {
                            mDeleteList.add(mItem);
                        } else {
                            mDeleteList.remove(mItem);
                        }
                    }
                });
            } else {
                viewHolder.checkBox.setVisibility(View.GONE);
                viewHolder.checkBox.setOnCheckedChangeListener(null);
            }
        }

        private class ViewHolder {
            public View contentView;
            public TextView chanelTitleTv;
            public TextView chanelSubTv;
            public TextView itemCountTv;
            public TextView deleteButton;
            public CheckBox checkBox;
        }

        public void setShowSelect(boolean isShowSelect) {
            this.isShowSelect = isShowSelect;
        }
    }
}
