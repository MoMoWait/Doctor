package com.comvee.tnb.ui.money;

import com.comvee.tnb.http.ComveePacket;

public class GlobalEntity {

	private ComveePacket comveePacket;

	private int fromWhere;

	public int getFromWhere() {
		return fromWhere;
	}

	public void setFromWhere(int fromWhere) {
		this.fromWhere = fromWhere;
	}

	public ComveePacket getComveePacket() {
		return comveePacket;
	}

	public void setComveePacket(ComveePacket comveePacket) {
		this.comveePacket = comveePacket;
	}

	private static GlobalEntity instance = new GlobalEntity();

	private GlobalEntity() {
	}

	// 获取单例
	public static GlobalEntity getInstance() {
		if (instance == null) {
			synchronized (GlobalEntity.class) {
				if (instance == null) {
					instance = new GlobalEntity();
				}
			}
		}
		return instance;
	}

//	public static void main(String[] args) {
//		GlobalEntity.getInstance().setFromWhere(9);
//		System.out.println(GlobalEntity.getInstance().getFromWhere());
//	}

}
