package com.comvee.tnb.model;

import java.io.Serializable;
import java.util.List;

public class RadioGroup implements Serializable {


    public String radioTypeName;
    public String radioTypeDesc;
    public String sid;
    public String relationProgramSort;
    public List<RadioAlbum> subList;

    public static class RadioAlbum implements Serializable {
        public int programType;//item类型1、专辑2、节目
        public String radioSubhead;
        public String radioInfo;
        public long timeLong;
        public String insDate;
        public String radioId;
        public String collectNum;
        public String updateTime;
        public String playUrl;
        public String photoUrl;
        public String pariseNum;
        public String clickNum;
        public String radioTitle;
        public int radioType;
        public String bannerUrl;//banner的图片url
        public String _id;
        public int isCollect;//是否收藏
        public String shareHtml;
        public String startTime;
        public int isSet;

        @Override
        public boolean equals(Object o) {
            return this._id != null && o != null && this._id.equals(((RadioAlbum) o)._id);
        }
    }

}
