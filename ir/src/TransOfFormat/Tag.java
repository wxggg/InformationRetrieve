package TransOfFormat;

import java.util.LinkedList;

public class Tag {
	private String tagName;
	private String mappedName;
	
	// true: 忽略子标签名字的输出，false:输出指定子标签
	private boolean isIgnoreSubTag;
	// 只有当输入指定子标签时，子标签列表才不为空。也就是当忽略子标签时，子标签列表为空
	private LinkedList<Tag> subListOfTag;
	
	// 代表该tag在其父标签的子标签列表中的位置
	//当该标签对象被当做一个另外一个标签的子标签而添加到其子标签列表中时会被修改
	private int tagId;// 从0开始计数，-1代表未赋值，
	//当其子标签结束事件触发时，对其进行修改。这样有助于判断读取的上下两个标签是否是预设的相邻，如果不相邻则填补中间标签（内容为空）
	private int lastOutTagId;// 初始值为-1
	
	

	public Tag(String tagName,String mappedName,boolean tagType){
		this.tagName=tagName;
		this.mappedName=mappedName;
		this.isIgnoreSubTag=tagType;
		if(tagType){
			subListOfTag=null;
		}else{
			subListOfTag=new LinkedList<Tag>();
		}
		tagId=-1;
		lastOutTagId=-1;
	}
	
	public void addSubTag(Tag subTag){
		int curLength= subListOfTag.size();
		subTag.setTagId(curLength);
		subListOfTag.add(subTag);
		
	}
	public int indexOfTag(String tagName){
		
		for (Tag tag : subListOfTag) {
			if(tag.getTagName().equals(tagName))
				return tag.getTagId();
		}
		return -1;//不存在
	}
	public Tag getTagByTagId(int tagId){
		return subListOfTag.get(tagId);
	}
	public String getTagName() {
		return tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	public String getMappedName() {
		return mappedName;
	}
	public void setMappedName(String mappedName) {
		this.mappedName = mappedName;
	}
	public boolean isIgnoreSubTag() {
		return isIgnoreSubTag;
	}
	public void setIgnoreSubTag(boolean isIgnoreSubTag) {
		this.isIgnoreSubTag = isIgnoreSubTag;
	}
	public LinkedList<Tag> getSubListOfTag() {
		return subListOfTag;
	}
	public void setSubListOfTag(LinkedList<Tag> subListOfTag) {
		this.subListOfTag = subListOfTag;
	}
	public int getTagId() {
		return tagId;
	}
	public void setTagId(int tagId) {
		this.tagId = tagId;
	}
	public int getLastOutTagId() {
		return lastOutTagId;
	}
	public void setLastOutTagId(int lastOutTagId) {
		this.lastOutTagId = lastOutTagId;
	}
}
