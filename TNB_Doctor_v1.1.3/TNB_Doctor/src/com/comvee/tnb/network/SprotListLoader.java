package com.comvee.tnb.network;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.ui.exercise.Exercise;

/**
 * Created by friendlove-pc on 16/3/17.
 */
public class SprotListLoader extends TnbBaseNetwork {
    @Override
    protected void onDoInMainThread(int status, Object obj) {

    }

    @Override
    protected Object parseResponseJsonData(JSONObject resData) {
        List<List<Exercise>> data = new ArrayList<List<Exercise>>();// 请求回来的数据
        try {
            for (int i = 0; i < 4; i++) {// 轻度运动,轻中强度运动 中强度运动 高强度运动
                data.add(new ArrayList<Exercise>());// 轻度运动
            }
            JSONArray jsonArray = resData.getJSONObject("body").getJSONArray("obj");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Exercise exercise = new Exercise();
                exercise.caloriesOneMinutes = jsonObject.getString("caloriesOneMinutes");
                exercise.caloriesThirtyMinutes = jsonObject.getString("caloriesThirtyMinutes");
                exercise.id = jsonObject.getString("id");
                exercise.level = jsonObject.getString("level");
                exercise.name = jsonObject.getString("name");
                exercise.imgUrl = jsonObject.getString("imgUrl");
                data.get(Integer.parseInt(exercise.level) - 1).add(exercise);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public String getUrl() {
        return ConfigUrlMrg.SPORT_TYPE;
    }

    public void load(){

    }
}
