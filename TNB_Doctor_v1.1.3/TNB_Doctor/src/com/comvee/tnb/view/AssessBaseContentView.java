package com.comvee.tnb.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.comvee.tnb.model.AssessQuestion;
import com.comvee.tnb.ui.assess.AssessQuestionFragment;

public abstract class AssessBaseContentView extends LinearLayout {

	private AssessQuestionFragment main;
	public AssessQuestion mInfo;

	public AssessBaseContentView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	public AssessBaseContentView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public AssessBaseContentView(Context context, AssessQuestionFragment main) {
		super(context);
		this.main = main;
	}

	public AssessQuestionFragment getMain() {
		return main;
	}

	public abstract int getNextIndex();

	public void setQuestion(AssessQuestion info){
		this.mInfo = info;
	};

	public final AssessQuestion getQuestion(){
		return mInfo;
	}


	public abstract void init();
}
