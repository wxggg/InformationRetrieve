package TransOfFormat;

import java.io.File;
import java.util.LinkedList;



public class TransOfFormat_TREC {
	LinkedList<File> filesList;
	String inFilePath;
	String outFilePath;
	
	public TransOfFormat_TREC(String inFilePath,String outFilePath){
		this.inFilePath=inFilePath;
		this.outFilePath=outFilePath;
		filesList=new LinkedList<File>();
		putDirInList(new File(inFilePath), filesList);
	}
	public void putDirInList(File dirFile,LinkedList<File> f_List){
		System.out.println(dirFile);
		File[] files=dirFile.listFiles();
		if(files==null||files.length==0){
			return;
		}
		for(File f:files){
			if(f.isDirectory()){
				putDirInList(f, filesList);
			}
			else{
				//这里读取nxml文件然后写入TREC格式的的文件中
				SAX_NXMLfilePraser sax_NXMLfilePraser = new SAX_NXMLfilePraser(f.getPath(),
				f.getPath().replace("nxml", "xml"));
				System.err.println("parsing " + f.getPath());
				f.delete();
			}
		}
	}
}
