package com.comvee.tnb.model;

import android.text.TextUtils;

import com.comvee.annotation.sqlite.Id;

public class RecordInfo
{

	public String dinner = "";// 正餐食量
	public String vagetable = "";// 蔬菜
	public String fat = "";// 脂肪
	public String salt = "";// 盐
	public String water = "";// 水
	public String sport = "";// 运动
	public String smoke = "";// 吸烟
	public String drink = "";// 喝酒
	public String pill = "";// 用药
	public String sugarMonitor = "";// 血糖监测
	public String mood = "";// 情绪
	public String assistFood = "";// 饮食配餐
	@Id(column = "insertDt")
	public String insertDt = "";

	public String getDinner()
	{
		return dinner;
	}

	public void setDinner(String dinner)
	{
		if (!TextUtils.isEmpty(dinner))
			this.dinner = dinner;
	}

	public String getVagetable()
	{
		return vagetable;
	}

	public void setVagetable(String vagetable)
	{
		if (!TextUtils.isEmpty(vagetable))
			this.vagetable = vagetable;
	}

	public String getFat()
	{
		return fat;
	}

	public void setFat(String fat)
	{
		if (!TextUtils.isEmpty(fat))
			this.fat = fat;
	}

	public String getSalt()
	{
		return salt;
	}

	public void setSalt(String salt)
	{
		if (!TextUtils.isEmpty(salt))
			this.salt = salt;
	}

	public String getWater()
	{
		return water;
	}

	public void setWater(String water)
	{
		if (!TextUtils.isEmpty(water))
			this.water = water;
	}

	public String getSport()
	{
		return sport;
	}

	public void setSport(String sport)
	{
		if (!TextUtils.isEmpty(sport))
			this.sport = sport;
	}

	public String getSmoke()
	{
		return smoke;
	}

	public void setSmoke(String smoke)
	{
		if (!TextUtils.isEmpty(smoke))

			this.smoke = smoke;
	}

	public String getDrink()
	{

		return drink;
	}

	public void setDrink(String drink)
	{
		if (!TextUtils.isEmpty(drink))

			this.drink = drink;
	}

	public String getPill()
	{
		return pill;
	}

	public void setPill(String pill)
	{
		if (!TextUtils.isEmpty(pill))

			this.pill = pill;
	}

	public String getSugarMonitor()
	{
		return sugarMonitor;
	}

	public void setSugarMonitor(String sugarMonitor)
	{
		if (!TextUtils.isEmpty(sugarMonitor))
			this.sugarMonitor = sugarMonitor;
	}

	public String getMood()
	{
		return mood;
	}

	public void setMood(String mood)
	{
		if (!TextUtils.isEmpty(mood))
			this.mood = mood;
	}

	public String getAssistFood()
	{
		return assistFood;
	}

	public void setAssistFood(String assistFood)
	{
		if (!TextUtils.isEmpty(assistFood))
			this.assistFood = assistFood;
	}

	public String getInsertDt()
	{
		return insertDt;
	}

	public void setInsertDt(String insertDt)
	{
		if (!TextUtils.isEmpty(insertDt))
			this.insertDt = insertDt;
	}

}
