package com.comvee.tnb.model;

/**
 * 首页弹窗实体类
 * 
 * @author linbin
 * 
 */
public class Alert {
	private String pic;// 图片
	private String turnUrl;// 跳转到H5地址
	private String turn_type;// 跳转类型，0表示跳转到H5

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getTurnUrl() {
		return turnUrl;
	}

	public void setTurnUrl(String turnUrl) {
		this.turnUrl = turnUrl;
	}

	public String getTurn_type() {
		return turn_type;
	}

	public void setTurn_type(String turn_type) {
		this.turn_type = turn_type;
	}

}
