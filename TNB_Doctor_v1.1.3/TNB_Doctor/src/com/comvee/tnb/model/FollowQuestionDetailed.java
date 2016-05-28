package com.comvee.tnb.model;
import com.comvee.annotation.sqlite.Id;
import com.comvee.annotation.sqlite.Table;

/**
 * @author SZM
 * 锟斤拷目锟斤拷细锟斤拷息
 *itemCode	ques	string	V24	锟斤拷前锟斤拷code	
dictName	ques	string	V48	锟斤拷前锟斤拷锟侥憋拷	
pCode	ques	string	V24	锟斤拷前锟筋父code	
itemType	ques	string	C1	锟斤拷目锟斤拷锟斤拷	1 锟斤拷选锟斤拷  2锟斤拷选锟斤拷  3锟斤拷锟斤拷 4 锟侥憋拷锟斤拷锟� 5 锟斤拷值锟斤拷锟�
isShow	ques	string	C1	锟角凤拷展示	1 展示 0 锟斤拷展示
help	ques	string	V128	锟斤拷锟斤拷锟斤拷息	
seq	ques	string	V10	锟斤拷锟�	
isNeed	ques	string	C1	锟角凤拷锟斤拷锟�	1 锟斤拷锟斤拷 0 锟角憋拷锟斤拷
category	ques	string	V2	锟斤拷锟�	
categoryName	ques	string	V48	锟斤拷锟斤拷谋锟�	
rules	ques	struct		锟斤拷锟斤拷	"锟斤拷选锟斤拷弑锟皆硷拷锟絠sRestrain锟斤拷时锟斤拷锟斤拷要锟斤拷锟斤拷锟斤拷锟斤拷寻锟揭讹拷应锟斤拷锟斤拷目锟斤拷锟斤拷约锟斤拷锟斤拷娑拷牟锟斤拷锟斤拷锟斤拷锟绞轿�
{
  锟斤拷id1锟斤拷:锟斤拷seq1,se2锟斤拷,
  锟斤拷id2锟斤拷:锟斤拷seq5锟斤拷
} 
key为选锟斤拷唯一锟斤拷识锟斤拷value为锟斤拷锟�
锟斤拷锟斤拷锟接︼拷锟斤拷锟侥匡拷锟斤拷艽锟斤拷诙锟斤拷锟斤拷锟斤拷茫锟斤拷锟斤拷欧指锟斤拷锟斤拷锟斤拷锟揭伙拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷要锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷虼シ锟斤拷锟叫э拷锟�"
tie	ques	string	C1	锟斤拷目约锟斤拷	0 没锟斤拷约锟斤拷 1 锟斤拷值锟斤拷锟斤拷锟睫革拷
path	ques	string	V10	锟斤拷锟斤拷	"锟斤拷锟节斤拷锟斤拷锟斤拷目锟结构
锟斤拷锟界：
锟斤拷锟斤拷 path:11
血锟角硷拷锟� path:11_1

锟斤拷锟斤拷锟斤拷为血锟角硷拷锟侥革拷锟节碉拷"
defualtCheck	ques	string	C1	默锟较癸拷选	1 锟斤拷选 0 未锟斤拷选
 * */
@Table(name="FollowQuestionDetailedTNB")//锟斤拷锟矫憋拷锟斤拷
public class FollowQuestionDetailed {
	
	private String followUpId;
	private String itemCode;
	private String dictName;
	private String parent;
	private int itemType;
	private int isShow;
	private String help;
	private String seq;
	private int isNeed;
	private int category;
	private String categoryName;
	private String rules;
	private int tie;
	@Id(column = "path")
	private String path;
	private int defualtCheck;
	private String unit;

	private String parentPath;//锟斤拷锟斤拷path值
	private String value;//锟斤拷锟节★拷锟斤拷值锟斤拷锟街凤拷直锟接达拷值锟斤拷锟斤拷选锟斤拷选锟斤拷Code锟斤拷锟斤拷选锟斤拷@锟街凤拷指锟窖★拷锟紺ode值锟斤拷锟斤拷值锟街成讹拷锟絠tem锟较达拷
	private int mhasParent ; //锟角凤拷锟叫革拷锟节碉拷
	private int mhasChild ;//锟角凤拷锟叫猴拷锟接节碉拷
	private int level;//锟斤拷前锟节碉拷锟斤拷锟节的诧拷锟�

	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getFollowUpId() {
		return followUpId;
	}
	public void setFollowUpId(String followUpId) {
		this.followUpId = followUpId;
	}
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
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public int getItemType() {
		return itemType;
	}
	public void setItemType(int itemType) {
		this.itemType = itemType;
	}
	public int getIsShow() {
		return isShow;
	}
	public void setIsShow(int isShow) {
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
	public int getIsNeed() {
		return isNeed;
	}
	public void setIsNeed(int isNeed) {
		this.isNeed = isNeed;
	}
	public int getCategory() {
		return category;
	}
	public void setCategory(int category) {
		this.category = category;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getRules() {
		return rules;
	}
	public void setRules(String rules) {
		this.rules = rules;
	}
	public int getTie() {
		return tie;
	}
	public void setTie(int tie) {
		this.tie = tie;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public int getDefualtCheck() {
		return defualtCheck;
	}
	public void setDefualtCheck(int defualtCheck) {
		this.defualtCheck = defualtCheck;
	}
	public String getParentPath() {
		return parentPath;
	}
	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getMhasParent() {
		return mhasParent;
	}
	public void setMhasParent(int mhasParent) {
		this.mhasParent = mhasParent;
	}
	public int getMhasChild() {
		return mhasChild;
	}
	public void setMhasChild(int mhasChild) {
		this.mhasChild = mhasChild;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	
}
