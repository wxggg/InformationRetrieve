package TransOfFormat;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.SAXException;


public class SAX_NXMLfileWirter {
	SAXTransformerFactory factory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
	private TransformerHandler handler=null;
	private OutputStream outputStream=null;
	//private String outFileName;
	
	
	
	public SAX_NXMLfileWirter(String outFileName,String rootElement){
		try {
			handler=factory.newTransformerHandler();
			Transformer transformer=handler.getTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"yes");
			
			outputStream=new FileOutputStream(outFileName);
			StreamResult resultXml=new StreamResult(outputStream);
			
			handler.setResult(resultXml);
			
			handler.startDocument();
			handler.startElement("", "", rootElement, null);
			
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public TransformerHandler getHandler() {
		return handler;
	}
	public void end(){
		
		try {
			handler.endDocument();
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
