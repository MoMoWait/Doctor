package com.comvee.tnb.ui.newsknowledg;

/**
 * Created by xiaobao on 2016/5/9.
 */

import java.io.Serializable;

/**
 * ITEM的对应可序化队列属性
 *  */
public class ChannelItem implements Serializable {
    /**
     * 栏目对应ID
     *  */
    public Integer id;
    /**
     * 栏目对应TYPE
     *  */
    public int type;
    /**
     * 栏目对应NAME
     *  */
    public String name;
    /**
     * 栏目在整体中的排序顺序  rank
     *  */
    public Integer orderId;
    /**
     * 栏目是否选中
    *  */
    public Integer selected;
}
