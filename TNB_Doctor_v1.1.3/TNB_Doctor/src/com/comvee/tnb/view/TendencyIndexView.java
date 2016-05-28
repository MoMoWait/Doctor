package com.comvee.tnb.view;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.model.TendencyDataInfo;
import com.comvee.tnb.widget.Tendency;

public class TendencyIndexView extends RelativeLayout {
	private TextView tvTendencyTitle;
	private TendencyDataInfo mInfo;
	public TendencyIndexView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
	}

	public TendencyIndexView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}

	public TendencyIndexView(Context context) {
		super(context);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		init();
	};
	
	private final void setTendencyTitle(String title) {
		tvTendencyTitle.setText(title);
	}
	
	public void setTendencyDataInfo(TendencyDataInfo info){
		this.mInfo = info;
	}
	
	public void init(){
		
		LayoutInflater.from(getContext()).inflate(R.layout.fragemnt_tendency_index_page, this, true);
		
		tvTendencyTitle = (TextView) findViewById(R.id.tv_tendency_title);

		Tendency tendency = (Tendency) findViewById(R.id.tendency);
		final ArrayList<String> h = new ArrayList<String>();
		for(int i=0;i<7;i++){
			h.add("10/03");
		}
		
		tendency.setPointSpace(h.size()/7);
		
		final ArrayList<Float> v = new ArrayList<Float>();
		v.add(22f);
		v.add(30f);
		v.add(23f);
		v.add(12f);
		v.add(12f);
		v.add(30f);
		v.add(23f);
		
		final ArrayList<Float> v1 = new ArrayList<Float>();
		v1.add(4f);
		v1.add(22f);
		v1.add(40f);
		v1.add(10f);
		v1.add(10f);
		v1.add(45f);
		v1.add(35f);
		
		final ArrayList<Float> c = new ArrayList<Float>();
		c.add(2f);
		c.add(12f);
		c.add(22f);
		c.add(32f);
		c.add(42f);
		c.add(52f);

//		TendencyLine line1 = tendency.createLine();
//		line1.setCoordValues(v);
//		line1.setLineColor(Color.parseColor("#F5B302"));
//
//		TendencyLine line2 = tendency.createLine();
//		line2.setCoordValues(v1);
//		line2.setLineColor(Color.parseColor("#bfbfbf"));
//		line2.setMaxValue(50f);
//		line2.setMinValue(20f);

//		tendency.addCoordValues(line1);
//		tendency.addCoordValues(line2);
		tendency.setVerticalCoord(c);
		tendency.setHorizontalCoord(h);
		tendency.show();
	}
	
}
