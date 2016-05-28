package com.comvee.tnb.ui.pharmacy;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.common.MyBaseAdapter;
import com.comvee.tool.ViewHolder;

public class MedicinalAdapter extends MyBaseAdapter<MedicinalModel> {
	public MedicinalAdapter(Context ctx, List<MedicinalModel> datas, int mayoutid) {
		super(ctx, datas, mayoutid);
	}

	@Override
	protected void doyouself(ViewHolder holder, int position) {
		MedicinalModel medicinalModel = datas.get(position);
		TextView nameTv = holder.get(R.id.name);
		TextView doesTv = holder.get(R.id.does);
		nameTv.setText(medicinalModel.name);
		doesTv.setText(medicinalModel.dose);
		if (position == datas.size() - 1) {
			holder.get(R.id.line_short).setVisibility(View.GONE);
			holder.get(R.id.line_long).setVisibility(View.VISIBLE);
		} else {
			holder.get(R.id.line_short).setVisibility(View.VISIBLE);
			holder.get(R.id.line_long).setVisibility(View.GONE);
		}
	}
}