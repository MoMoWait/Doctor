package com.comvee.tnb.radio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.db.DownloadItemDao;
import com.comvee.tnb.model.RadioAlbumItem;
import com.comvee.tnb.model.RadioGroup.RadioAlbum;
import com.comvee.tnb.view.RadioPlayView;
import com.comvee.tnb.view.RadioPlayView.PlayerViewListener;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.util.BundleHelper;

/**
 * 已经下载完成的界面
 *
 * @author friendlove-pc
 */
public class DownLoadCompleteFrag extends BaseFragment implements OnClickListener, OnItemClickListener {
    private TitleBarView mTitlebar;
    private RadioAlbum mAlbum;
    private ListView mListView;
    private Cursor mCursor;
    private MyCursorAdapter mAdapter;
    private Map<String, RadioAlbumItem> selectedRadioAlbumItems = new HashMap<String, RadioAlbumItem>();// 被选中的
    private boolean editState;// 处于编辑状态
    private View option_layout, btn_select, btn_delete;
    private RadioPlayView mPlayView;
    private TextView tvLabel;

    @Override
    public int getViewLayoutId() {
        return R.layout.download_complete_frag;
    }

    @Override
    public void onLaunch(Bundle dataBundle) {
        super.onLaunch(dataBundle);
        editState = false;// 处于编辑状态
        option_layout = findViewById(R.id.option_layout);
        btn_select = findViewById(R.id.btn_select);
        btn_select.setOnClickListener(this);
        btn_delete = findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(this);

        mAlbum = BundleHelper.getObjecByBundle(RadioAlbum.class, dataBundle);

        mTitlebar = (TitleBarView) findViewById(R.id.layout_top);
        mTitlebar.setTitle(mAlbum.radioTitle);
        mTitlebar.setLeftButton(R.drawable.top_menu_back, this);
        mTitlebar.setRightButton("编辑", this);

        mCursor = DownloadItemDao.getInstance().getSQLiteDatabase()
                .query("RadioAlbumItem", null, "downloadState=5 and refId=" + mAlbum.radioId, null, null, null, null);
        mAdapter = new MyCursorAdapter(getApplicationContext(), mCursor, true);

        findViewById(R.id.btn_download_more).setOnClickListener(this);
        tvLabel = (TextView) findViewById(R.id.tv_label);
        mListView = (ListView) findViewById(R.id.listview);
        mListView.setAdapter(mAdapter);
        mListView.addFooterView(new View(getApplicationContext()));
        mListView.setOnItemClickListener(this);
        initPlay();
        tvLabel.setText(String.format("已下载%d个节目", mCursor.getCount()));
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
                mPlayView.setLabel(item.radioTitle);
            }

            @Override
            public void onPause(RadioAlbumItem item) {
            }
        });
    }

    public static void toFragment(FragmentActivity act, RadioAlbum mAlbum) {
        FragmentMrg.toFragment(act, DownLoadCompleteFrag.class, BundleHelper.getBundleByObject(mAlbum), true);
    }

    private class MyCursorAdapter extends CursorAdapter {
        private boolean isAllSelected;// 是否全部选中

        public MyCursorAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        @Override
        public View newView(Context ctx, Cursor arg1, ViewGroup arg2) {
            View view = LayoutInflater.from(ctx).inflate(R.layout.downloaded_complete_item, arg2, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.tv_label = (TextView) view.findViewById(R.id.tv_label);
            viewHolder.tv_time = (TextView) view.findViewById(R.id.tv_time);
            viewHolder.checkBox = (CheckBox) view.findViewById(R.id.cb);
            view.setTag(viewHolder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            final RadioAlbumItem item = DownloadItemDao.getInfoByCursor(cursor);
            viewHolder.tv_label.setText(item.radioTitle);
            viewHolder.tv_time.setText(RadioUtil.getMedaioDurationString(item.timeLong));
            viewHolder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        selectedRadioAlbumItems.put(item._id, item);//添加节目
                    } else {
                        selectedRadioAlbumItems.remove(item._id);
                    }
                   /* if (mTvSelectAll != null && selectedRadioAlbumItems.size() > 0) {
                        mTvSelectAll.setText(selectedRadioAlbumItems.size() == mCursor.getCount() ? getString(R.string.no_select_all) : getString(R.string.select_all));
                    }*/
                }

            });

            if (editState) {
                viewHolder.checkBox.setVisibility(View.VISIBLE);

            } else {
                viewHolder.checkBox.setVisibility(View.GONE);
            }
            if (isAllSelected) {
                viewHolder.checkBox.setChecked(true);
                selectedRadioAlbumItems.put(item._id, item);
            } else {
                selectedRadioAlbumItems.remove(item._id);
                viewHolder.checkBox.setChecked(false);
            }
        }

        private class ViewHolder {
            public TextView tv_label;
            public TextView tv_time;
            public CheckBox checkBox;

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_download_more:
                DownloadNoyeFragment.toFragment(getActivity(), mAlbum);
                break;
            case TitleBarView.ID_LEFT_BUTTON:
                FragmentMrg.toBack(getActivity());
                break;
            case TitleBarView.ID_RIGHT_BUTTON:
                swapEditState();
                break;
            case R.id.btn_select:
                TextView  mTvSelectAll = (TextView) btn_select.findViewById(R.id.tv_select);
                mAdapter.isAllSelected = selectedRadioAlbumItems.size() != mCursor.getCount();
                mTvSelectAll.setText(mAdapter.isAllSelected ? getString(R.string.no_select_all) : getString(R.string.select_all));
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_delete:
                Iterator it = selectedRadioAlbumItems.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();
                    DownloadItemDao.getInstance().delete((RadioAlbumItem) entry.getValue());
                }
                mCursor = DownloadItemDao.getInstance().getSQLiteDatabase()
                        .query("RadioAlbumItem", null, "downloadState=5 and refId=" + mAlbum.radioId, null, null, null, null);
                if (mCursor.getCount() == 0) {
                    getActivity().onBackPressed();
                }
                mAdapter = new MyCursorAdapter(getApplicationContext(), mCursor, true);
                mListView.setAdapter(mAdapter);
                selectedRadioAlbumItems.clear();

                tvLabel.setText(String.format("已下载%d个节目", mCursor.getCount()));
                break;
            default:
                break;
        }
    }

    public void swapEditState() {
        editState = !editState;
        mTitlebar.setRightButton(editState ? "取消" : "编辑", this);
        mAdapter.notifyDataSetChanged();
        option_layout.setVisibility(editState ? View.VISIBLE : View.GONE);
        mPlayView.setVisibility(!editState ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
        RadioPlayerMrg mrg = RadioPlayerMrg.getInstance();
        ArrayList<RadioAlbumItem> mItems = DownloadItemDao.getInstance().getRaidosByWhere("downloadState=5 and refId=" + mAlbum.radioId);
        mrg.setDataSource(mAlbum, mItems,true);
        mrg.play(position);
    }
}
