package com.comvee.tnb.model;

import java.io.Serializable;

public class RadioComment implements Serializable {

	public String commentId;
	public String commentText;
	public String commentType;
	public String id;
	public String insertDt;
	public String memberName;
	public String memberPhoto;
	public String parise;
	public int isParise;
	public int pariseNum;
	public RepetComment repetComment;
	public int type;
	public boolean isHot;

	public static class RepetComment {
		public String commentId;
		public String commentText;
		public String commentType;
		public String id;
		public String insertDt;
		public String memberName;
		public String memberPhoto;
		public String parise;
		public String pariseNum;

	}

}
