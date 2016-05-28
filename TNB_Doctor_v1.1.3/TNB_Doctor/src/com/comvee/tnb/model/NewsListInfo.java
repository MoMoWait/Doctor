package com.comvee.tnb.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yujun on 2016/5/3.
 */
public class NewsListInfo implements Serializable {

        public List<TurnlistBean> turnlist;//轮播图片数组

        public List<RowsBean> rows;

        public static class TurnlistBean implements Serializable {
            public String turnsPlayUrl;//轮播图片地址
            public int turnsPlayStatus;//轮播状态	0不轮播1轮播
            public String hot_spot_id;//主键id
            public String hot_spot_title;//标题
            public String url;//跳转地址

        }
        public static class RowsBean implements Serializable{
            public int hotType;//热点类型
            public String send_time;//发送时间
            public int turnsPlayStatus;

            public int getClickNum() {
                return clickNum;
            }

            public void setClickNum(int clickNum) {
                this.clickNum = clickNum;
            }

            public int clickNum;//阅读数
            public String abstract_info;//摘要
            public String hot_spot_id;//主键id
            public int photo_size;//图片大小
            public String hot_spot_title;//内容标题
            public String url;//跳转页面
            public List<String> photo_url_list;//图片地址数组

        }
}
