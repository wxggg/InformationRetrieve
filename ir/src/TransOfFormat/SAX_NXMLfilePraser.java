package TransOfFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class SAX_NXMLfilePraser {

	public SAX_NXMLfilePraser(String inFilePath, String outFilePath) {

		try {
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = parserFactory.newSAXParser();

			// FileInputStream fileInputStream = new
			// FileInputStream(inFilePath);
			// saxParser.parse(fileInputStream, new
			// SAX_NXMLHandler(outFilePath));
			XMLReader xmlReader = saxParser.getXMLReader();
			xmlReader
					.setFeature(
							"http://apache.org/xml/features/nonvalidating/load-external-dtd",
							false);
			xmlReader.setContentHandler(new SAX_NXMLHandler(outFilePath));
			FileInputStream fileInputStream = new FileInputStream(inFilePath);
			xmlReader.parse(new InputSource(fileInputStream));
			
			//文档遍历
			// // traverDataFile
			// File dataFile = new File(inFilePath);
			// if (dataFile.exists()) {
			// LinkedList<File> list = new LinkedList<File>();
			// File[] files = dataFile.listFiles();
			// for (File file : files) {
			//
			// if (file.isDirectory()) {
			// System.out.println("文件夹:" + file.getAbsolutePath());
			// list.add(file);
			// } else {
			// System.out.println("文件:" + file.getAbsolutePath());
			// FileInputStream fileInputStream = new FileInputStream(
			// file.getAbsolutePath());
			// xmlReader.parse(new InputSource(fileInputStream));
			// }
			// }
			//
			// File temp_fileDir;
			//
			// while (!list.isEmpty()) {
			// temp_fileDir = list.removeFirst();
			// files = temp_fileDir.listFiles();
			// for (File file : files) {
			// if (file.isDirectory()) {
			// System.out.println("文件夹:" + file.getAbsolutePath());
			// list.add(file);
			// } else {
			// System.out.println("文件:" + file.getAbsolutePath());
			// FileInputStream fileInputStream = new FileInputStream(
			// file.getAbsolutePath());
			// xmlReader.parse(new InputSource(fileInputStream));
			// }
			// }
			// }
			// } else {
			// System.out.println("文件不存在!");
			// }

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
