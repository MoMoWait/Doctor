package com.comvee.tnb.ui.record;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.comvee.ThreadHandler;
import com.comvee.frame.BaseFragmentActivity;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.ui.log.RecordCalendarFragment;
import com.comvee.tnb.ui.log.RecordChooseFragment;
import com.comvee.tnb.ui.record.laboratory.RecordLaboratoryFragment;
import com.comvee.tnb.ui.tool.blood.BluetoothUtil;
import com.comvee.tnb.view.MenuWindow;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UITool;

import java.util.ArrayList;

/**
 * 添加 健康记录
 *
 * @author friendlove
 */
public class RecordChooseAddFragment extends BaseFragment implements OnClickListener, OnHttpListener {

    private volatile boolean safeJump = true;
    private float height1, height2, height3, height4;
    private TitleBarView mBarView;
    private ArrayList<String> items = new ArrayList<String>();
    private ArrayList<Integer> icons = new ArrayList<Integer>();

    public RecordChooseAddFragment() {
    }

    public static RecordChooseAddFragment newInstance() {
        RecordChooseAddFragment fragment = new RecordChooseAddFragment();
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.fragment_tendencyadd1;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setViewHeight() {
        float disW = UITool.getDisplayWidth(getActivity());
        float viewWidth = (disW - UITool.dip2px(getApplicationContext(), 60)) / 2;
        height1 = viewWidth * 336 / 290;
        height2 = viewWidth * 324 / 290;
        height3 = viewWidth * 228 / 290;
        height4 = viewWidth * 216 / 290;
    }

    @Override
    public void onLaunch(Bundle bundle) {
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        BluetoothUtil.getInstance().close();
        mBarView.setRightButton(getString(R.string.history), this);
        setViewHeight();
        init();
        mBarView.setTitle(getString(R.string.title_record));

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    private void init() {
        int pad = (int) UITool.dip2px(getApplicationContext(), 10);
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) height1);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) height2);
        LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) height3);
        LinearLayout.LayoutParams lp4 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) height4);
        lp1.setMargins(0, pad, 0, 0);
        lp3.setMargins(0, pad, 0, 0);
        View btn = null;
        btn = findViewById(R.id.btn_1);

        btn.setLayoutParams(lp2);
        // btn.setOnTouchListener(TouchedAnimation.TouchLight);去掉点击变灰的效果
        btn.setOnClickListener(this);

        btn = findViewById(R.id.btn_2);
        btn.setLayoutParams(lp4);
        // btn.setOnTouchListener(TouchedAnimation.TouchLight);
        btn.setOnClickListener(this);

        btn = findViewById(R.id.btn_3);
        btn.setLayoutParams(lp3);
        // btn.setOnTouchListener(TouchedAnimation.TouchLight);
        btn.setOnClickListener(this);

        btn = findViewById(R.id.btn_4);
        btn.setLayoutParams(lp1);
        // btn.setOnTouchListener(TouchedAnimation.TouchLight);
        btn.setOnClickListener(this);

        btn = findViewById(R.id.btn_5);
        btn.setLayoutParams(lp3);
        // btn.setOnTouchListener(TouchedAnimation.TouchLight);
        btn.setOnClickListener(this);

        btn = findViewById(R.id.btn_6);
        btn.setLayoutParams(lp3);
        // btn.setOnTouchListener(TouchedAnimation.TouchLight);
        btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case TitleBarView.ID_RIGHT_BUTTON:
                UITool.backgroundAlpha(getActivity(), 0.85f);
                showMenu();
                break;
            case R.id.btn_1:
                if (safeJump) {
                    safeJump = false;
                    ThreadHandler.postUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            toFragment(RecordSugarInputNewFrag.class, null, true);
                            safeJump = true;
                        }
                    }, 250);
                }
                break;
            case R.id.btn_2:
                if (safeJump) {
                    safeJump = false;
                    ThreadHandler.postUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            toFragment(RecordPressBloodInputFragment.newInstance(null), true, true);
                            safeJump = true;
                        }
                    }, 250);
                }
                break;
            case R.id.btn_3:
                if (safeJump) {
                    safeJump = false;
                    ThreadHandler.postUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            toFragment(RecordBmiInputFragment.newInstance(null), true, true);

                            safeJump = true;
                        }
                    }, 250);
                }
                break;
            case R.id.btn_4:
                if (safeJump) {
                    safeJump = false;
                    ThreadHandler.postUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            toFragment(RecordCalendarFragment.newInstance(), true, true);
                            safeJump = true;
                        }
                    }, 250);
                }
                break;
            case R.id.btn_5:
                toFragment(new RecordLaboratoryFragment(), true, true);
                break;
            case R.id.btn_6:
                toFragment(RecordHemoglobinFragment.newInstance(null), true, true);
                break;

            default:
                break;
        }

    }

    @Override
    public boolean onBackPress() {
        ((BaseFragmentActivity) getActivity()).tryExit();
        return true;
    }

    @Override
    public void onSussece(int what, byte[] b, boolean fromCache) {

    }

    @Override
    public void onFialed(int what, int errorCode) {
        cancelProgressDialog();
        ComveeHttpErrorControl.parseError(getActivity(), errorCode);
    }

    public void showMenu() {
        if (items.size() == 0) {
            items.add(getString(R.string.record_menu_suggar).toString());
            items.add(getString(R.string.record_menu_blood).toString());
            items.add(getString(R.string.record_menu_laboratory).toString());
            items.add(getString(R.string.record_menu_bmi).toString());
            items.add(getString(R.string.record_menu_hemog).toString());
            items.add(getString(R.string.record_menu_log).toString());
            icons.add(R.drawable.tanchuang_001);
            icons.add(R.drawable.tanchuang_003);
            icons.add(R.drawable.tanchuang_005);
            icons.add(R.drawable.tanchuang_007);
            icons.add(R.drawable.tanchuang_009);
            icons.add(R.drawable.tanchuang_011);
        }

        final MenuWindow menuWindow = MenuWindow.getInstance(getActivity(), items, icons, 150);

        menuWindow.setOnOnitemClickList(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                menuWindow.dismiss();
                switch (position) {
                    case 0:
                        toFragment(RecordMainFragment.newInstance(true, 0), true, true);
                        break;
                    case 1:
                        toFragment(RecordMainFragment.newInstance(true, 1), true, true);
                        break;
                    case 2:
                        toFragment(new RecordLaboratoryFragment(), true, true);
                        break;

                    case 3:
                        toFragment(RecordMainFragment.newInstance(true, 2), true, true);
                        break;
                    case 4:
                        toFragment(RecordMainFragment.newInstance(true, 3), true, true);
                        break;
                    case 5:
                        toFragment(RecordChooseFragment.newInstance(false), true, true);
                        break;
                    default:
                        break;
                }
            }
        });
        int x = (int) (UITool.getDisplayWidth(getActivity()) - UITool.dip2px(getApplicationContext(), 160));
        menuWindow.showAsDropDown(findViewById(R.id.main_titlebar_view), x, 0);
    }
}
