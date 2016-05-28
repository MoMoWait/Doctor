package com.comvee.tnb.model;

import java.io.Serializable;
import java.util.List;

/**
 * 知识页面标题栏标题
 * Created by yujun on 2016/5/2.
 */
public class NewsTabColumnInfo implements Serializable {

    public int hotType;//热点类型
    public String hotTypeDesc;//热点描述
    public String hotTypeName;//热点名称
    public String hotTypeTitleUrl;//类型标题图片
    public String hotTypeUrl;//类型背景图片
    public String insertDt;//插入时间
    public int isValid;//有效标志
    public String modifyDt;//修改时间
    public String orderNo;//排序号
    public long sid;//主键id
    public String url;
}
