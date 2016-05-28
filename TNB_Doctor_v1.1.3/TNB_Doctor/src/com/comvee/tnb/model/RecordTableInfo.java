package com.comvee.tnb.model;

import com.comvee.annotation.sqlite.Id;

public class RecordTableInfo
{
	public int score;// 当前分数
	public int goal;// 目标

	public int dinner;// 正餐食量
	public int vagetable;// 蔬菜
	public int fat;// 脂肪
	public int salt;// 盐
	public int water;// 水
	public int sport;// 运动
	public int smoke;// 吸烟
	public int drink;// 喝酒
	public int pill;// 用药
	public int sugarMonitor;// 血糖监测
	public int mood;// 情绪
	public int assistFood;// 饮食配餐
	@Id(column = "insertDt")
	public String insertDt = "";
	public int status;// -1不合格1良好0合格
	
	public int totalFood;//饮食
	public int totalLive;//生活习惯
	

	public int getTotalFood()
	{
		return totalFood;
	}

	public void setTotalFood(int totalFood)
	{
		this.totalFood = totalFood;
	}

	public int getTotalLive()
	{
		return totalLive;
	}

	public void setTotalLive(int totalLive)
	{
		this.totalLive = totalLive;
	}

	public int getStatus()
	{
		return status;
	}

	public void setStatus(int status)
	{
		this.status = status;
	}

	public int getScore()
	{
		return score;
	}

	public void setScore(int score)
	{
		this.score = score;
	}

	public int getGoal()
	{
		return goal;
	}

	public void setGoal(int goal)
	{
		this.goal = goal;
	}

	public int getDinner()
	{
		return dinner;
	}

	public void setDinner(int dinner)
	{
		this.dinner = dinner;
	}

	public int getVagetable()
	{
		return vagetable;
	}

	public void setVagetable(int vagetable)
	{
		this.vagetable = vagetable;
	}

	public int getFat()
	{
		return fat;
	}

	public void setFat(int fat)
	{
		this.fat = fat;
	}

	public int getSalt()
	{
		return salt;
	}

	public void setSalt(int salt)
	{
		this.salt = salt;
	}

	public int getWater()
	{
		return water;
	}

	public void setWater(int water)
	{
		this.water = water;
	}

	public int getSport()
	{
		return sport;
	}

	public void setSport(int sport)
	{
		this.sport = sport;
	}

	public int getSmoke()
	{
		return smoke;
	}

	public void setSmoke(int smoke)
	{
		this.smoke = smoke;
	}

	public int getDrink()
	{
		return drink;
	}

	public void setDrink(int drink)
	{
		this.drink = drink;
	}

	public int getPill()
	{
		return pill;
	}

	public void setPill(int pill)
	{
		this.pill = pill;
	}

	public int getSugarMonitor()
	{
		return sugarMonitor;
	}

	public void setSugarMonitor(int sugarMonitor)
	{
		this.sugarMonitor = sugarMonitor;
	}

	public int getMood()
	{
		return mood;
	}

	public void setMood(int mood)
	{
		this.mood = mood;
	}

	public int getAssistFood()
	{
		return assistFood;
	}

	public void setAssistFood(int assistFood)
	{
		this.assistFood = assistFood;
	}

	public String getInsertDt()
	{
		return insertDt;
	}

	public void setInsertDt(String insertDt)
	{
		this.insertDt = insertDt;
	}

}
