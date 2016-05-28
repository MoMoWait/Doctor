package com.comvee.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import android.content.Context;
import android.content.SharedPreferences;

import com.comvee.tnb.R;
import com.comvee.tnb.model.TendencyInputModel;
import com.comvee.tnb.model.TendencyInputModelItem;
import com.comvee.tnb.ui.record.RecordIntputModel;
import com.comvee.util.SerializUtil;

public class TendencyInputMrg {

	// private ArrayList<TendencyInputModel> arrayMore = new
	// ArrayList<TendencyInputModel>();
	private ArrayList<TendencyInputModel> arrayRecommend = new ArrayList<TendencyInputModel>();
	private ArrayList<TendencyInputModel> arrayDisplay = new ArrayList<TendencyInputModel>();

	private Context mContext;
	private SharedPreferences sp;
	private static TendencyInputMrg instance;

	@SuppressWarnings("unchecked")
	private TendencyInputMrg(Context cxt) {
		this.mContext = cxt;
		sp = cxt.getSharedPreferences("tendency_input", 0);
		// InputDataDB db = new InputDataDB(mContext);
		// arrayRecommend = db.getTendencyInputModels(1);
		// arrayRecommend = db.getTendencyInputModels_new();
		arrayRecommend = getTendencyInputModels();
		// arrayMore = db.getTendencyInputModels(0);
		// db.Close();

		// if (Util.checkFirst(mContext, "first_load_more")) {
		// arrayDisplay.clear();
		// arrayDisplay.addAll(arrayRecommend);
		// commit();
		// return;
		// }
		arrayDisplay.addAll(arrayRecommend);
		// try {
		// String strObj = sp.getString("models", null);
		// arrayDisplay = TextUtils.isEmpty(strObj) ? new
		// ArrayList<TendencyInputModel>() : ((ArrayList<TendencyInputModel>)
		// SerializUtil
		// .fromString(strObj));
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

	}

	public static TendencyInputMrg getInstance(Context cxt) {
		return instance != null ? instance : (instance = new TendencyInputMrg(cxt));
	}

	public void commit() {
		try {
			// Log.v("TendencyInputMrg", "commit");
			sp.edit().putString("models", SerializUtil.toString(arrayDisplay)).commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// public ArrayList<TendencyInputModel> getArrayMore()
	// {
	// return arrayMore;
	// }

	// public void setArrayMore(ArrayList<TendencyInputModel> arrayMore)
	// {
	// this.arrayMore = arrayMore;
	// }

	public ArrayList<TendencyInputModel> getArrayRecommend() {
		return arrayRecommend;
	}

	public void setArrayRecommend(ArrayList<TendencyInputModel> arrayRecommend) {
		this.arrayRecommend = arrayRecommend;
	}

	public ArrayList<TendencyInputModel> getArrayDisplay() {
		return arrayDisplay;
	}

	public void setArrayDisplay(ArrayList<TendencyInputModel> arrayDisplay) {
		this.arrayDisplay = arrayDisplay;
	}

	/**
	 * 根据传入的label字段来拿到符合条件的list
	 * 
	 * @param label
	 * @return
	 */
	public ArrayList<TendencyInputModelItem> getInputModelItems(String label) {
		// InputDataDB db = new InputDataDB(mContext);
		//
		// ArrayList<TendencyInputModelItem> list =
		// db.getTendencyInputModelItems(label);
		// db.Close();
		// InputDataDB db = new InputDataDB(mContext);
		// ArrayList<TendencyInputModelItem> list =
		// db.getTendencyInputModelItems_new(label);
		//
		// return list;

		ArrayList<TendencyInputModelItem> infos = new ArrayList<TendencyInputModelItem>();
		HashMap<String, TendencyInputModelItem> tempMap = RecordIntputModel.TendencyHashMap;
		if (tempMap == null) {
			return infos;
		}
		for (Entry<String, TendencyInputModelItem> entry : tempMap.entrySet()) {
			if (entry.getValue().super_label.equals(label)) {
				infos.add(entry.getValue());
			}
		}
		return infos;

	}

	/**
	 * 根据传入的字符串数组来获取符合的list
	 * 
	 * @param name
	 * @return
	 */
	public ArrayList<TendencyInputModelItem> getInputModelItems(String[] name) {
		// InputDataDB db = new InputDataDB(mContext);
		//
		// // ArrayList<TendencyInputModelItem> list =
		// db.getTendencyInputModelItems(name);
		// ArrayList<TendencyInputModelItem> list =
		// db.getTendencyInputModelItems_new(name);
		// // db.Close();
		// return list;

		ArrayList<TendencyInputModelItem> infos = new ArrayList<TendencyInputModelItem>();
		HashMap<String, TendencyInputModelItem> tempMap = RecordIntputModel.TendencyHashMap;
		if(tempMap!=null){
			for (Entry<String, TendencyInputModelItem> entry : tempMap.entrySet()) {
				if (useLoop(name, entry.getValue().code)) {
					TendencyInputModelItem items = new TendencyInputModelItem();
					items = entry.getValue();
					infos.add(items);
				}
			}
		}


		return infos;

	}

	public TendencyInputModel getTendencyInputModel(String label) {
		TendencyInputModel temp = new TendencyInputModel();
		temp.label = label;
		return arrayDisplay.get(arrayDisplay.indexOf(temp));
	}

	/**
	 * 用来对传入的string数组进行遍历，返回期望的字符串
	 * 
	 * @param array
	 *            要查询的数组
	 * @param targetString
	 *            用来查询的字符串
	 * @return
	 */
	private boolean useLoop(String[] array, String targetString) {
		for (String str : array) {
			if (str.equals(targetString)) {
				return true;
			}
		}
		return false;
	}

	private ArrayList<TendencyInputModel> getTendencyInputModels() {
		ArrayList<TendencyInputModel> infos = new ArrayList<TendencyInputModel>();
		String[][] basic = { { ResUtil.getString(R.string.record_sugar), "1", "", "1", "3" },
				{ ResUtil.getString(R.string.record_bloodpress), "0", "", "1", "1" }, { "BMI", "0", "", "1", "2" },
				{ ResUtil.getString(R.string.tendencyadd1_glycosylated_hemoglobin), "1", "", "1", "4" } };
		for (int i = 0, len = basic.length; i < len; i++) {
			TendencyInputModel item = new TendencyInputModel();
			item.label = basic[i][0];
			item.typeDisplay = Integer.valueOf(basic[i][4]);
			item.defDate = basic[i][2];
			item.isFloat = Integer.valueOf(basic[i][1]) == 1;
			infos.add(item);
		}
		return infos;
	}
}
