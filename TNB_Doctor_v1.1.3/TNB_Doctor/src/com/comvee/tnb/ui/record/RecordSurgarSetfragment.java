package com.comvee.tnb.ui.record;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.comvee.tnb.DrawerMrg;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.dialog.CustomFloatNumPickDialog;
import com.comvee.tnb.dialog.CustomFloatNumPickDialog.OnNumChangeListener;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tool.UserMrg;

import org.json.JSONObject;

/**
 * 设置控制目标 标准
 *
 * @author friendlove-pc
 */
@SuppressLint("ValidFragment")
public class RecordSurgarSetfragment extends BaseFragment implements OnHttpListener, OnClickListener, OnNumChangeListener {

    private TextView highEmpty;
    private TextView lowEmpty;
    private TextView highFull;
    private TextView lowFull;
    private float sHighEmpty, sLowEmpty, sHighFull, sLowFull;
    private int fromWhere;
    private int mInputWhat;// 1、空腹血糖最小值2、空腹血糖最大值3、非空腹血糖最小值4、非空腹血糖最大值
    private TitleBarView mBarView;

    @Override
    public int getViewLayoutId() {
        return R.layout.fragment_surgar_set;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onLaunch(Bundle bundle) {
        mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
        mBarView.setLeftDefault(this);
        init();
        mBarView.setTitle("血糖目标设置");
        mBarView.setRightButton(getText(R.string.save).toString(), this);

        requestData();
    }

    private void init()

    {

        highEmpty = (TextView) findViewById(R.id.tv_limosis_max);
        lowEmpty = (TextView) findViewById(R.id.tv_limosis_min);

        highFull = (TextView) findViewById(R.id.tv_nolimosis_max);
        lowFull = (TextView) findViewById(R.id.tv_nolimosis_min);

        findViewById(R.id.btn_limosis_min).setOnClickListener(this);
        findViewById(R.id.btn_limosis_max).setOnClickListener(this);
        findViewById(R.id.btn_nolimosis_min).setOnClickListener(this);
        findViewById(R.id.btn_nolimosis_max).setOnClickListener(this);
        findViewById(R.id.btn_restore).setOnClickListener(this);

    }

    public void showSetValueDialog(View v) {
        TextView tv;
        switch (mInputWhat) {
            case 1:
                tv = lowEmpty;
                break;
            case 2:
                tv = highEmpty;
                break;
            case 3:
                tv = lowFull;
                break;
            case 4:
                tv = highFull;
                break;
            default:
                tv = highFull;
                break;
        }
        float def = TextUtils.isEmpty(tv.getText().toString()) ? 4.4f : Float.valueOf(tv.getText().toString()
                .replace(getString(R.string.unit).toString(), ""));
        showSetSingleValueDialog(def);
    }

    /**
     * 一个值 多列的选择空间
     */
    public void showSetSingleValueDialog(float defaultValue) {
        CustomFloatNumPickDialog builder = new CustomFloatNumPickDialog();
        builder.setUnit("mmol/L");
        builder.setLimitNum(1, 20);
        builder.setDefult(defaultValue);
        builder.setFloat(true);
        builder.addOnNumChangeListener(this);
        builder.show(getActivity().getSupportFragmentManager(), "dialog");
    }

    private void parseData(byte[] b) throws Exception {
        ComveePacket packet = ComveePacket.fromJsonString(b);

        if (packet.getResultCode() == 0) {

            JSONObject body = packet.optJSONObject("body");

            String highEmpty = body.optString("highEmpty");
            String lowEmpty = body.optString("lowEmpty");
            String highFull = body.optString("highFull");
            String lowFull = body.optString("lowFull");

            sHighEmpty = Float.valueOf(highEmpty);
            sLowEmpty = Float.valueOf(lowEmpty);
            sHighFull = Float.valueOf(highFull);
            sLowFull = Float.valueOf(lowFull);

            this.highEmpty.setText(highEmpty + getString(R.string.unit));
            this.lowEmpty.setText(lowEmpty + getString(R.string.unit));
            this.highFull.setText(highFull + getString(R.string.unit));
            this.lowFull.setText(lowFull + getString(R.string.unit));
        } else {
            ComveeHttpErrorControl.parseError(getActivity(), packet);
        }

    }

    private void parseDataModify(byte[] b) throws Exception {
        ComveePacket packet = ComveePacket.fromJsonString(b);
        if (packet.getResultCode() == 0) {
            showToast("保存完成");
            FragmentMrg.toBack(getActivity());
        } else {
            // highEmpty.setText(sHighEmpty);
            // highFull.setText(sHighFull);
            // lowEmpty.setText(sLowEmpty);
            // lowFull.setText(sLowFull);
            showToast(packet.getResultMsg());
        }
    }

    @Override
    public void onSussece(int what, byte[] b, boolean fromCache) {
        cancelProgressDialog();
        ComveeHttp.clearCache(getApplicationContext(), UserMrg.getCacheKey(ConfigUrlMrg.INDEX));
        try {
            switch (what) {
                case 3:
                    showToast("恢复成功");
                    parseData(b);
                    break;
                case 2:
                    parseDataModify(b);
                    break;
                case 1:
                    parseData(b);
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast(R.string.time_out);
        }

    }

    @Override
    public void onFialed(int what, int errorCode) {
        cancelProgressDialog();

        ComveeHttpErrorControl.parseError(getActivity(), errorCode);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }

    private void requestData() {
        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.RECORD_SUGAR_SET);
        http.setOnHttpListener(1, this);
        http.startAsynchronous();
    }

    /**
     * @param isRestore 是否重置
     */
    public void requsetModify(boolean isRestore) {
        showProgressDialog(getString(R.string.msg_loading));
        String sHighEmpty = highEmpty.getText().toString().replace(getString(R.string.unit).toString(), "");
        String sLowEmpty = lowEmpty.getText().toString().replace(getString(R.string.unit).toString(), "");
        String sHighFull = highFull.getText().toString().replace(getString(R.string.unit).toString(), "");
        String sLowFull = lowFull.getText().toString().replace(getString(R.string.unit).toString(), "");

        ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.RECORD_SUGAR_SET_MODIFY);
        http.setPostValueForKey("highFull", sHighFull);
        http.setPostValueForKey("lowFull", sLowFull);
        http.setPostValueForKey("highEmpty", sHighEmpty);
        http.setPostValueForKey("lowEmpty", sLowEmpty);
        http.setPostValueForKey("type", isRestore ? "1" : "2");
        http.setOnHttpListener(isRestore ? 3 : 2, this);
        http.startAsynchronous();
    }

    @Override
    public void onClick(final View v) {
        final int id = v.getId();
        switch (id) {

            case R.id.btn_limosis_min:
                mInputWhat = 1;
                showSetValueDialog(v);
                break;
            case R.id.btn_limosis_max:
                mInputWhat = 2;
                showSetValueDialog(v);
                break;
            case R.id.btn_nolimosis_min:
                mInputWhat = 3;
                showSetValueDialog(v);
                break;
            case R.id.btn_nolimosis_max:
                mInputWhat = 4;
                showSetValueDialog(v);
                break;

            case R.id.btn_restore:
                requsetModify(true);
                break;
            case TitleBarView.ID_RIGHT_BUTTON:
                requsetModify(false);
                break;
            default:
                break;
        }

    }

    @Override
    public void onChange(DialogFragment dialog, float num) {
        // TODO Auto-generated method stub
        switch (mInputWhat) {
            case 1:
                if (num >= sHighEmpty) {
                    lowEmpty.setText(sLowEmpty + getString(R.string.unit).toString());
                    showToast("最低值必须小于最高值哦！");
                    return;
                }
                lowEmpty.setText(num + getString(R.string.unit).toString());
                sLowEmpty = num;
                break;
            case 2:

                if (num <= sLowEmpty) {
                    lowFull.setText(sLowFull + getString(R.string.unit).toString());
                    showToast("最高值必须大于最低值哦！");
                    return;
                }

                sHighEmpty = num;
                highEmpty.setText(num + getString(R.string.unit).toString());
                break;
            case 3:
                if (num >= sHighFull) {
                    lowFull.setText(sLowFull + getString(R.string.unit).toString());
                    showToast("最低值必须小于最高值哦！");
                    return;
                }
                lowFull.setText(num + getString(R.string.unit).toString());
                sLowFull = num;
                break;
            case 4:

                if (num <= sLowFull) {
                    lowFull.setText(sLowFull + getString(R.string.unit).toString());
                    showToast("最高值必须大于最低值哦！");
                    return;
                }
                highFull.setText(num + getString(R.string.unit).toString());
                sHighFull = num;

                break;
            default:
                break;
        }
    }

}
