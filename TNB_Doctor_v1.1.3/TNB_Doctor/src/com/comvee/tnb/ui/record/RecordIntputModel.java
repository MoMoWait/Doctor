package com.comvee.tnb.ui.record;

import java.util.HashMap;

import com.comvee.tnb.model.TendencyInputModelItem;

public class RecordIntputModel {
	/**
	 * 用二维数组来定义 label 先后为 "BMI","血糖","血压" 数组中按顺序的字段
	 * ：label,isFloat,defDate,type,type_display
	 */
	public static HashMap<String, TendencyInputModelItem> TendencyHashMap;

//	public static  String[][] Tendency_Input = { { "血糖", "1", "", "1", "3" }, { "血压", "0", "", "1", "1" }, { "BMI", "0", "", "1", "2" } ,{ "糖化血红蛋白", "1", "", "1", "4" }};

	/**
	 * 将原数据库中存储的一些字段进行初始化
	 */
	public static final void initInputModel() {
		TendencyHashMap = new HashMap<String, TendencyInputModelItem>();
		TendencyInputModelItem tendency0 = new TendencyInputModelItem();
		tendency0.name = "凌晨血糖";
		tendency0.super_label = "血糖";
		tendency0.maxValue = 11f;
		tendency0.minValue = 4f;
		tendency0.defValue = 4f;
		tendency0.limitMax = 99;
		tendency0.limitMin = 0;
		tendency0.strUnit = "mmol/l";
		tendency0.code = "beforedawn";
		tendency0.color = "#F5B302";
		TendencyHashMap.put(tendency0.name, tendency0);

		TendencyInputModelItem tendency = new TendencyInputModelItem();
		tendency.name = "空腹血糖";
		tendency.super_label = "血糖";
		tendency.maxValue = 6f;
		tendency.minValue = 4f;
		tendency.defValue = 4f;
		tendency.limitMax = 99;
		tendency.limitMin = 0;
		tendency.strUnit = "mmol/l";
		tendency.code = "beforeBreakfast";
		tendency.color = "#F5B302";
		TendencyHashMap.put(tendency.name, tendency);

		TendencyInputModelItem tendency2 = new TendencyInputModelItem();
		tendency2.name = "早餐后2小时血糖";
		tendency2.super_label = "血糖";
		tendency2.maxValue = 11f;
		tendency2.minValue = 4f;
		tendency2.defValue = 4f;
		tendency2.limitMax = 99;
		tendency2.limitMin = 0;
		tendency2.strUnit = "mmol/l";
		tendency2.code = "afterBreakfast";
		tendency2.color = "#F5B302";
		TendencyHashMap.put(tendency2.name, tendency2);

		TendencyInputModelItem tendency3 = new TendencyInputModelItem();
		tendency3.name = "午餐前血糖";
		tendency3.super_label = "血糖";
		tendency3.maxValue = 11f;
		tendency3.minValue = 4f;
		tendency3.defValue = 4f;
		tendency3.limitMax = 99;
		tendency3.limitMin = 0;
		tendency3.strUnit = "mmol/l";
		tendency3.code = "beforeLunch";
		tendency3.color = "#1a9fe0";
		TendencyHashMap.put(tendency3.name, tendency3);

		TendencyInputModelItem tendency4 = new TendencyInputModelItem();
		tendency4.name = "午餐后2小时血糖";
		tendency4.super_label = "血糖";
		tendency4.maxValue = 11f;
		tendency4.minValue = 4f;
		tendency4.defValue = 4f;
		tendency4.limitMax = 99;
		tendency4.limitMin = 0;
		tendency4.strUnit = "mmol/l";
		tendency4.code = "afterLunch";
		tendency4.color = "#1a9fe0";
		TendencyHashMap.put(tendency4.name, tendency4);

		TendencyInputModelItem tendency5 = new TendencyInputModelItem();
		tendency5.name = "晚餐前血糖";
		tendency5.super_label = "血糖";
		tendency5.maxValue = 11f;
		tendency5.minValue = 4f;
		tendency5.defValue = 4f;
		tendency5.limitMax = 99;
		tendency5.limitMin = 0;
		tendency5.strUnit = "mmol/l";
		tendency5.code = "beforeDinner";
		tendency5.color = "#707980";
		TendencyHashMap.put(tendency5.name, tendency5);

		TendencyInputModelItem tendency6 = new TendencyInputModelItem();
		tendency6.name = "晚餐后2小时血糖";
		tendency6.super_label = "血糖";
		tendency6.maxValue = 11f;
		tendency6.minValue = 4f;
		tendency6.defValue = 4f;
		tendency6.limitMax = 99;
		tendency6.limitMin = 0;
		tendency6.strUnit = "mmol/l";
		tendency6.code = "afterDinner";
		tendency6.color = "#707980";
		TendencyHashMap.put(tendency6.name, tendency6);

		TendencyInputModelItem tendency7 = new TendencyInputModelItem();
		tendency7.name = "睡前血糖";
		tendency7.super_label = "血糖";
		tendency7.maxValue = 11f;
		tendency7.minValue = 4f;
		tendency7.defValue = 4f;
		tendency7.limitMax = 99;
		tendency7.limitMin = 0;
		tendency7.strUnit = "mmol/l";
		tendency7.code = "beforeSleep";
		tendency7.color = "#F5B302";
		TendencyHashMap.put(tendency7.name, tendency7);

		TendencyInputModelItem tendency8 = new TendencyInputModelItem();
		tendency8.name = "收缩压";
		tendency8.super_label = "血压";
		tendency8.maxValue = 140f;
		tendency8.minValue = 90f;
		tendency8.defValue = 91f;
		tendency8.limitMax = 300;
		tendency8.limitMin = 70;
		tendency8.strUnit = "mmHg";
		tendency8.code = "bloodpressuresystolic";
		tendency8.color = "#bfbfbf";
		TendencyHashMap.put(tendency8.name, tendency8);

		TendencyInputModelItem tendency9 = new TendencyInputModelItem();
		tendency9.name = "舒张压";
		tendency9.super_label = "血压";
		tendency9.maxValue = 90f;
		tendency9.minValue = 60f;
		tendency9.defValue = 61f;
		tendency9.limitMax = 180;
		tendency9.limitMin = 40;
		tendency9.strUnit = "mmHg";
		tendency9.code = "bloodpressurediastolic";
		tendency9.color = "#F5B302";
		TendencyHashMap.put(tendency9.name, tendency9);

		TendencyInputModelItem tendency10 = new TendencyInputModelItem();
		tendency10.name = "身高";
		tendency10.super_label = "BMI";
		tendency10.maxValue = 0f;
		tendency10.minValue = 0f;
		tendency10.defValue = 170f;
		tendency10.limitMax = 280;
		tendency10.limitMin = 70;
		tendency10.strUnit = "cm";
		tendency10.code = "height";
		TendencyHashMap.put(tendency10.name, tendency10);

		TendencyInputModelItem tendency1 = new TendencyInputModelItem();
		tendency1.name = "体重";
		tendency1.super_label = "BMI";
		tendency1.maxValue = 0f;
		tendency1.minValue = 0f;
		tendency1.defValue = 60f;
		tendency1.limitMax = 200;
		tendency1.limitMin = 20;
		tendency1.strUnit = "kg";
		tendency1.code = "weight";
		TendencyHashMap.put(tendency1.name, tendency1);

		TendencyInputModelItem tendency11 = new TendencyInputModelItem();
		tendency11.name = "糖化血红蛋白";
		tendency11.super_label = "糖化血红蛋白";
		tendency11.maxValue = 7.0f;
		tendency11.minValue = 4.0f;
		tendency11.defValue = 4f;
		tendency11.limitMax = 30;
		tendency11.limitMin = 0;
		tendency11.strUnit = "%";
		tendency11.code = "hemoglobin";
		TendencyHashMap.put(tendency11.name, tendency11);
	}

}
