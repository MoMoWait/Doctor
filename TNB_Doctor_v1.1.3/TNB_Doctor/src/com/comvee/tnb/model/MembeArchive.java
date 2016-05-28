package com.comvee.tnb.model;

import java.util.List;

import org.json.JSONObject;

public class MembeArchive {
	private String itemCode;
    private String dictName;
    private String pCode;
    private String itemType;
    private String isShow;
    private String help;
    private String seq;
    private String tie;
    private String isNeed;
    private String category;
    private String categoryName;
    private List<MemberArchiveItem> itemList;
    private String  values;
    private JSONObject rule;
   
    
    public MembeArchive(){}
    
  

	public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    public String getpCode() {
        return pCode;
    }

    public void setpCode(String pCode) {
        this.pCode = pCode;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getIsShow() {
        return isShow;
    }

    public void setIsShow(String isShow) {
        this.isShow = isShow;
    }

    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getTie() {
        return tie;
    }

    public void setTie(String tie) {
        this.tie = tie;
    }

    public String getIsNeed() {
        return isNeed;
    }

    public void setIsNeed(String isNeed) {
        this.isNeed = isNeed;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<MemberArchiveItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<MemberArchiveItem> itemList) {
        this.itemList = itemList;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }

    public JSONObject getRule() {
        return rule;
    }

    public void setRule(JSONObject rule) {
        this.rule = rule;
    }
}
