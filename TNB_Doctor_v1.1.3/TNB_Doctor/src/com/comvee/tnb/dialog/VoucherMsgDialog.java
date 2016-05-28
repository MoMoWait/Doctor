package com.comvee.tnb.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.comvee.frame.BaseFragmentActivity;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.adapter.DiscountAdapter;
import com.comvee.tnb.model.IndexVoucherModel;
import com.comvee.tnb.ui.user.LoginFragment;
import com.comvee.tnb.widget.MyListView;
import com.comvee.tool.UITool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VoucherMsgDialog extends Dialog implements android.view.View.OnClickListener {

    private Activity activity;
    private IndexVoucherModel model;

    private VoucherMsgDialog(Context context) {
        super(context, R.style.CustomDialog2);
        this.setOwnerActivity((Activity) context);
    }

    public static VoucherMsgDialog getInstance(Activity activity, IndexVoucherModel model) {
        VoucherMsgDialog dialog = new VoucherMsgDialog(activity);
        dialog.setActivity(activity);
        dialog.setModel(model);
        dialog.init();
        return dialog;
    }

    public void setModel(IndexVoucherModel model) {
        this.model = model;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    private int getListItemHeight(MyListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return 0;
        }
        View listItem = listAdapter.getView(0, null, listView);
        listItem.measure(0, 0);
        return listItem.getMeasuredHeight();
    }

    public void init() {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.discount_dialog_frag, null);
        GridView gridView = (GridView) view.findViewById(R.id.graidview_pay_result);
        MyListView listView = (MyListView) view.findViewById(R.id.discount_list);
        ImageView img = (ImageView) view.findViewById(R.id.discount_head_img);
        TextView tv_prompt = (TextView) view.findViewById(R.id.tv_prompt);
        Button button = (Button) view.findViewById(R.id.btn_regist);
        View dismess = view.findViewById(R.id.img_dismess);
        ScrollView scrollView = (ScrollView) view.findViewById(R.id.scr_voucher_list);
        dismess.setOnClickListener(this);
        // 底部锯齿 star
        ArrayList<Map<String, Integer>> arrayList = getData();
        gridView.setLayoutParams(new LinearLayout.LayoutParams(UITool.dip2px(getContext(), 12) * arrayList.size(), LayoutParams.WRAP_CONTENT));
        gridView.setAdapter(new SimpleAdapter(getContext(), arrayList, R.layout.item_discount_of_grid, new String[0], new int[0]));
        // 底部锯齿 end
        DiscountAdapter adapter = DiscountAdapter.getInstance(getContext());
        adapter.setList(model.getList());

        if (model.getType() == 1) {
            img.setImageResource(R.drawable.kaquan_04);
            tv_prompt.setText(activity.getText(R.string.discount_text).toString());
            button.setVisibility(View.GONE);
        } else {
            img.setImageResource(R.drawable.kaquan_14);
            tv_prompt.setText(activity.getText(R.string.discount_text_1).toString());
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(this);
        }
        listView.setAdapter(adapter);
        if (model.getList().size() > 4) {
            int height = UITool.dip2px(activity, 300);
            int itemHeight = getListItemHeight(listView);
            if (itemHeight != 0) {
                height = (itemHeight + UITool.dip2px(activity, 12)) * 4 - UITool.dip2px(activity, 12);
            }
            scrollView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, height));
        }
        addContentView(view, new LayoutParams(-1, -2));
        setContentView(view);

    }

    private ArrayList<Map<String, Integer>> getData() {
        ArrayList<Map<String, Integer>> arrayList = new ArrayList<Map<String, Integer>>();
        final int W = (int) UITool.getDisplayWidth(activity);
        // int mag = (int)
        // TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30,
        // getContext().getResources().getDisplayMetrics());
        // int viewWidth = (int)
        // TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12,
        // getContext().getResources().getDisplayMetrics());
        // int len = (W - mag) / viewWidth;
        int len = UITool.dip2px(getContext(), 290) / UITool.dip2px(getContext(), 12);
        for (int i = 0; i < len - 1; i++) {
            arrayList.add(new HashMap<String, Integer>());
        }
        return arrayList;

    }

    @Override
    public void onClick(View arg0) {
        dismiss();
        switch (arg0.getId()) {
            case R.id.btn_regist:
                FragmentMrg.toFragment((BaseFragmentActivity) activity, LoginFragment.newInstance(false), false, true);
                break;

            default:
                break;
        }

    }
}
