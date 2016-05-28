package com.comvee.tnb.ui.record.laboratory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.comvee.tnb.ui.record.common.NetPic;
import com.comvee.tnb.ui.record.common.NetPics;

public class Laboratory implements Serializable {
    /**
     *
     */
    public String folderId;// 化验单ID
    public String folderName;// 化验单名称
    public String insertDt;// 化验单创建时间
    public String isUseService;
    List<NetPic> uploadPics = new ArrayList<NetPic>();
    public AnswerUploadFolder answerUploadFolder;

    public static class AnswerUploadFolder extends Laboratory implements Serializable {
        public String answer;
        public String folderId;// 化验单ID
        public String folderName;// 化验单名称
        public String insertDt;// 化验单创建时间
        List<NetPic> uploadPics = new ArrayList<NetPic>();
    }

    @Override
    public boolean equals(Object o) {
        return this.folderId != null && this.folderId.equals(((Laboratory) o).folderId);
    }
}
