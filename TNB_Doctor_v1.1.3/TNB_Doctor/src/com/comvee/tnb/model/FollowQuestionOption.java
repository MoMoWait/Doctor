package com.comvee.tnb.model;
import com.comvee.annotation.sqlite.Id;
import com.comvee.annotation.sqlite.Table;
/**
 * @author SZM
 * valueCode	itemList	string	V24	ѡ��code	
valueName	itemList	string	V48	ѡ���ı�	
isRestrain	itemList	string	C1	ѡ��Լ��	0 û�й��� 1 ��ʾ������Ŀ 2 Ƕ�뵯�� ������ԭ�������н��׶�ѡ�� 3 ��ʾ������Ŀͬʱ������ĿԼ��
isValue	itemList	string	C1	�Ƿ�ѡ��	 1 ѡ�� 0 δѡ��
id	itemList	string	V10	ѡ��id	
 * */
@Table(name="FollowQuestionOptionTNB")//���ñ���
public class FollowQuestionOption {
	@Id(column="position")
	private int position;
	private String valueCode;
	private String valueName;
	private int isRestrain;
	private int isValue;
	private String id;
	private int show_seq;
	
	private String itemCode;
	private String followId;
	
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public String getValueCode() {
		return valueCode;
	}
	public void setValueCode(String valueCode) {
		this.valueCode = valueCode;
	}
	public String getValueName() {
		return valueName;
	}
	public void setValueName(String valueName) {
		this.valueName = valueName;
	}
	public int getIsRestrain() {
		return isRestrain;
	}
	public void setIsRestrain(int isRestrain) {
		this.isRestrain = isRestrain;
	}
	public int getIsValue() {
		return isValue;
	}
	public void setIsValue(int isValue) {
		this.isValue = isValue;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getShow_seq() {
		return show_seq;
	}
	public void setShow_seq(int show_seq) {
		this.show_seq = show_seq;
	}
	public String getItemCode() {
		return itemCode;
	}
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	public String getFollowId() {
		return followId;
	}
	public void setFollowId(String followId) {
		this.followId = followId;
	}

}
