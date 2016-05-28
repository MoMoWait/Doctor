package com.comvee.tnb.ui.more;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.DrawerMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.adapter.CollectListAdapter;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.CustomDialog;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.CollectInfo;
import com.comvee.tnb.radio.RadioCollectFrag;
import com.comvee.tnb.radio.RadioMainFrag;
import com.comvee.tnb.ui.book.BookWebActivity;
import com.comvee.tnb.ui.heathknowledge.HealthKnowledgeFragment;
import com.comvee.tnb.ui.index.IndexFrag;
import com.comvee.tnb.ui.newsknowledg.NewsKnowledgeFragment;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UserMrg;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的收藏
 *
 * @author SZM
 */
public class CollectionListFragment extends BaseFragment implements OnClickListener, OnItemClickListener, OnHttpListener {

    public static final int WHERE_INDEX = 0;
    public static final int WHERE_RADIO = 2;
    private ListView listView;
    private CollectListAdapter mCollectListAdapter;
    private View layoutNonDefault;
    private List<CollectInfo> infos;
    private boolean editTag;// 进入编辑状态
    private TitleBarView mBarView;
    private int mFromWhere;

    public static void toFragment(FragmentActivity fragment, int fromwhere, boolean anim) {
        Bundle bundle = new Bundle();
        bundle.putInt("from_where", fromwhere);
        FragmentMrg.popSingleFragment(fragment, CollectionListFragment.class, bundle, anim);

        // FragmentMrg.toFragment(fragment, CollectionListFragment.class,
        // bundle, anim);
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.collect_list_fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        requestCollectList();

    }

    @Override
    public void onLaunch(Bundle bundle) {
        super.onLaunch(bundle);

        mFromWhere = bundle.getInt("from_where");

        if (mFromWhere == WHERE_INDEX) {
            DrawerMrg.getInstance().close();
        }
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);

        findViewById(R.id.btn_radio).setOnClickListener(this);
        findViewById(R.id.btn_editting).setOnClickListener(this);
        ConfigParams.setAnswerNew(getApplicationContext(), false);
        mBarView.setTitle("我的收藏");
        init();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            editTag = false;
            mCollectListAdapter = new CollectListAdapter(getApplicationContext(), editTag);
            mCollectListAdapter.setListItems(infos);
            listView.setAdapter(mCollectListAdapter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    private void init() {
        editTag = false;

        layoutNonDefault = findViewById(R.id.layout_no_data);
        Button bt_understand = (Button) findViewById(R.id.bt_understandTNBinfo);
        bt_understand.setOnClickListener(this);
        mCollectListAdapter = new CollectListAdapter(getApplicationContext(), false);
        listView = (ListView) findViewById(R.id.list_view);
        listView.setOnItemClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_radio:
                RadioCollectFrag.toFragment(getActivity(), 2, false);
                FragmentMrg.popSingleFragment(getActivity(), RadioCollectFrag.class, null, 0, 0);
                break;
            case TitleBarView.ID_LEFT_BUTTON:
                DrawerMrg.getInstance().open();
                break;

            case R.id.bt_understandTNBinfo:
                toFragment(new NewsKnowledgeFragment(), true, true);
                break;
            case R.id.btn_editting:
                editTag = !editTag;
                mCollectListAdapter = new CollectListAdapter(getApplicationContext(), editTag);
                mCollectListAdapter.setListItems(infos);
                listView.setAdapter(mCollectListAdapter);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        if (editTag == false)
            // toFragment(BookWebFragment.newInstance(infos.get(position)),
            // true, true);
            BookWebActivity.toWebActivity(getActivity(), BookWebActivity.COLLECT, infos.get(position), null, null, null);
        else
            showCancleCollectDialog(position);
    }

    private void requestCollectList() {
        showProgressDialog(getString(R.string.msg_loading));
        layoutNonDefault.setVisibility(View.GONE);
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.COLLECT_LIST);
        http.setOnHttpListener(1, this);
        http.setPostValueForKey("page", "" + 1);
        http.setPostValueForKey("rows", "" + 100);
        // http.setNeedGetCache(true,
        // UserMrg.getCacheKey(ConfigUrlMrg.COLLECT_LIST));
        http.startAsynchronous();
    }

    private void showCancleCollectDialog(final int position) {
        CustomDialog.Builder dialog = new CustomDialog.Builder(getActivity());
        dialog.setMessage("是否取消收藏该文章？");
        dialog.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                requestCancleCollect(position);
                infos.remove(position);
                mCollectListAdapter.setListItems(infos);
                ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.COLLECT_LIST));
            }
        });
        dialog.setPositiveButton("取消", null);
        dialog.create().show();
    }

    private void requestCancleCollect(int position) {
        showProgressDialog(getString(R.string.msg_loading));
        ComveeHttp http = new ComveeHttp(getActivity(), ConfigUrlMrg.COLLECT_CANCLE);
        http.setOnHttpListener(3, this);
        http.setPostValueForKey("type", infos.get(position).getType());
        http.setPostValueForKey("id", "" + infos.get(position).getObjId());
        http.startAsynchronous();

    }

    @Override
    public void onSussece(int what, byte[] b, boolean fromCache) {
        cancelProgressDialog();
        switch (what) {
            case 1:
                parseCollectList(b, fromCache);
                break;
            case 3:
                try {
                    ComveePacket packet = ComveePacket.fromJsonString(b);
                    showToast(packet.getResultMsg());
                    if (mCollectListAdapter.getCount() == 0) {
                        layoutNonDefault.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private void parseCollectList(byte[] arg1, boolean fromCache) {
        try {
            mRoot.setVisibility(View.VISIBLE);
            ComveePacket packet = ComveePacket.fromJsonString(arg1);
            if (packet.getResultCode() == 0) {
                infos = new ArrayList<CollectInfo>();
                JSONArray array = packet.getJSONObject("body").getJSONArray("rows");
                int len = array.length();
                for (int i = 0; i < len; i++) {
                    JSONObject obj = array.getJSONObject(i);
                    String collectId = obj.getString("collectId");
                    String insertDt = obj.getString("insertDt");
                    String memberId = obj.getString("memberId");
                    String objId = obj.getString("objId");
                    String title = obj.getString("title");
                    String url = obj.getString("url");
                    String imgUrl = obj.getString("imgUrl");
                    String type = obj.getString("type");
                    infos.add(new CollectInfo(collectId, insertDt, memberId, objId, title, url, imgUrl, "1", type));
                }
                mCollectListAdapter.setListItems(infos);
                listView.setAdapter(mCollectListAdapter);
                if (mCollectListAdapter.getCount() == 0) {
                    layoutNonDefault.setVisibility(View.VISIBLE);
                } else {
                    layoutNonDefault.setVisibility(View.GONE);
                }
            } else {
                showToast(packet.getResultMsg());
            }
        } catch (Exception e) {
            showToast(getString(R.string.error));
            e.printStackTrace();
        }
    }

    @Override
    public void onFialed(int what, int errorCode) {
        cancelProgressDialog();
        ComveeHttpErrorControl.parseError(getActivity(), errorCode);
    }

    @Override
    public boolean onBackPress() {
        switch (mFromWhere) {
            case WHERE_INDEX:
                IndexFrag.toFragment(getActivity(), true);
                break;
            case WHERE_RADIO:
                FragmentMrg.popBackToFragment(getActivity(), RadioMainFrag.class, null, true);
                break;
        }


        return true;

    }

}
