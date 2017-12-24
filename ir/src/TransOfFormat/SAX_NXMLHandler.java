package TransOfFormat;

import java.util.LinkedList;
import java.util.Stack;
import java.util.logging.Handler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


import javax.xml.transform.sax.TransformerHandler;

public class SAX_NXMLHandler extends DefaultHandler {
	Tag rootTag = null;
	Stack<Tag> stackOfTags = new Stack<Tag>();

	TransformerHandler outHandler = null;
	private String outFileName = null;

	private boolean ifOutContent = false;// 内容事件触发时决定是否输出

	private final String whiteSpace = " ";// 遇到标签就输出空格
	private SAX_NXMLfileWirter saxWriter ;
	public  SAX_NXMLHandler(String outFileName) {
		super();
		this.outFileName = outFileName;
	}

	@Override
	public void startDocument() throws SAXException {
		initRootTag();
		saxWriter = new SAX_NXMLfileWirter(outFileName, rootTag.getTagName());
		outHandler = saxWriter.getHandler();
		stackOfTags.push(rootTag);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		Tag topTagOfStack = stackOfTags.peek();

		// 判断该标签的父级标签类型,type1的话直接忽略该tag的名字，而去输出其内容，通过设置ifOutContent为true
		// type2则需要判断该标签是否在标签列表里，如果在则输出标签名字，并将该标签压入栈。同时判断该标签的类型来决定该标签内容是否输出
		if (topTagOfStack.isIgnoreSubTag()) {
			// topTagOfStack是type1
			ifOutContent = true;
			return;
		} else {
			// topTagOfStack是type2
			int indexOfTag = topTagOfStack.indexOfTag(qName);
			if (indexOfTag == -1) {
				ifOutContent = false;
				return;
			} else {
				boolean idFlag=true;
				if(qName.equals("article-id")){
					//获取标签的全部属性
					idFlag=false;
		            for(int i = 0;i < attributes.getLength();i++){
		            	//getLocalName(i)得到是pub-id-type  getValue(i)得到的是后边的值
		            	if(attributes.getLocalName(i).equals("pub-id-type")&&attributes.getValue(i).equals("pmc")){
		            		idFlag=true;
		            	}
		            }
		            if (!idFlag) {
		            	ifOutContent = false;
						return;
					}
		            
				}
				// 解决预定义标签实际文件中缺失的情况
				int lastOutTagId = topTagOfStack.getLastOutTagId();
				if ((indexOfTag - lastOutTagId) != 1) {
					for (int i = 1; i < (indexOfTag - lastOutTagId); i++)
						outEmptyTag(topTagOfStack.getTagByTagId(lastOutTagId + i));
				}

				Tag tagByTagId = topTagOfStack.getTagByTagId(indexOfTag);
				// 输出该标签到xml文件中
				try {
					outHandler.startElement("", "", tagByTagId.getMappedName(), null);

					stackOfTags.push(tagByTagId);
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// 根据其类型决定该标签与下一个标签之间的内容是否要输出
				if (tagByTagId.isIgnoreSubTag()) {
					// type1
					ifOutContent = true;
					return;
				} else {
					// type2
					ifOutContent = false;
					return;
				}
			}
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) {
		if (ifOutContent) {
			try {
				outHandler.characters(ch, start, length);
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) {

		Tag topTagOfStack = stackOfTags.peek();
		// 判断栈顶tag是否和读取的tag一样，一样说明该标签内容输出结束
		if (topTagOfStack.getTagName().equals(qName)) {
			// 相等
			Tag endTag = stackOfTags.pop();
			try {
				outHandler.endElement("", "", endTag.getMappedName());
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(stackOfTags.empty()){
				System.out.println("sd");
			}
			topTagOfStack = stackOfTags.peek();
			topTagOfStack.setLastOutTagId(endTag.getTagId());
		} 
		// 根据新的栈顶Tag对象的类型来决定紧挨着该结束标签的输出与忽略
		if (topTagOfStack.isIgnoreSubTag())
			ifOutContent = true;
		else
			ifOutContent = false;
	}
	
	@Override
	public void endDocument(){
		Tag topTagOfStack = stackOfTags.pop();
		int lastOutTagId = topTagOfStack.getLastOutTagId();
		LinkedList<Tag> subListOfTag = topTagOfStack.getSubListOfTag();
		if(lastOutTagId<(subListOfTag.size()-1)){
			for(int i=1;i<(subListOfTag.size()-lastOutTagId);i++){
				Tag tagByTagId = topTagOfStack.getTagByTagId(lastOutTagId+i);
				outEmptyTag(tagByTagId);
			}
		}
		try {
			outHandler.endElement("", "", topTagOfStack.getMappedName());
			saxWriter.end();
			
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void outEmptyTag(Tag emptyTag) {
		// 迭代向XML文件输出空标签
		try {
			outHandler.startElement("", "", emptyTag.getMappedName(), null);

			outHandler.characters(whiteSpace.toCharArray(), 0, whiteSpace.length());
			if (!emptyTag.isIgnoreSubTag()) {

				LinkedList<Tag> subListOfTag = emptyTag.getSubListOfTag();
				for (Tag tag : subListOfTag) {
					outHandler.startElement("", "", tag.getMappedName(), null);
				}
			}
			outHandler.endElement("", "", emptyTag.getMappedName());
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initRootTag() {
		Tag docno = new Tag("article-id", "DOCNO", true);
		Tag title = new Tag("title-group", "TITLE", true);
		Tag abs = new Tag("abstract", "ABSTRACT", true);
		Tag body = new Tag("body", "BODY", true);

		Tag refList = new Tag("ref-list", "REF-LIST", false);
		Tag ref = new Tag("article-title", "REFERENCE", true);
		refList.addSubTag(ref);

		Tag doc = new Tag("DOC", "DOC", false);
		doc.addSubTag(docno);
		doc.addSubTag(title);
		doc.addSubTag(abs);
		doc.addSubTag(body);
		doc.addSubTag(refList);
		rootTag = doc;
	}

}
