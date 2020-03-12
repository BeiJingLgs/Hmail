package com.fsck.k9.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;
import android.util.Xml;



public class configRead {
 
	private boolean isMp3 = false;
	private String FILE_PATH = File.separator + "data" + File.separator + "hanvon_resources" + File.separator + "hvsetting.conf";
	private boolean isInit = false;
	
	
	public boolean IsSurportMp3(){
		if(!isInit){
			if(ReadConfig()){
				isInit = true;
			}else{
				return true;
			}
		}
		return isMp3;
	}
	
	
	public boolean setMp3Config(boolean IsSurportMp3){
		isMp3 = IsSurportMp3;
		return WriteConfig();	
	}
	
	private boolean ReadConfig(){
		try{ 
			File file = new File(FILE_PATH);
			if (!file.exists())
				return false;
			FileInputStream fin = new FileInputStream(FILE_PATH);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(fin);
			Element element = document.getDocumentElement();
			NodeList nodeList1 = element.getElementsByTagName("Setting");

			NodeList nodeList = nodeList1.item(0).getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++) {
				if ("IsSurportMp3".equals(nodeList.item(i).getNodeName())) {
					if (nodeList.item(i).getChildNodes().getLength() != 0) {
						int tem = new Short(nodeList.item(i).getFirstChild()
								.getNodeValue());
						if(tem == 1){
							isMp3 = true;
						}
						else{
							isMp3 = false;
						}
					} else {
						isMp3 = false;
					}
				}
			}
		    fin.close();  
		    return true;
		    
		}catch(Exception e){ 
		    e.printStackTrace(); 
		    return false;
		} 

	}
	
	
	private boolean WriteConfig(){
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		String strFile;
		try {
			serializer.setOutput(writer);
			// <?xml version=��1.0�� encoding=��UTF-8�� standalone=��yes��?>
			serializer.startDocument("UTF-8", true);

			// <Setting>
			serializer.startTag("", "Setting");

			// <IsSurportMp3>1</IsSurportMp3>
			serializer.startTag("", "IsSurportMp3");
			if(isMp3){
			    serializer.text("1");
			}
			else{
				serializer.text("0");
			}
			serializer.endTag("", "IsSurportMp3");

			// </Setting>
			serializer.endTag("", "Setting");
			serializer.endDocument();
			strFile = writer.toString();
			FileOutputStream fout = new FileOutputStream(FILE_PATH);
	        byte [] bytes = strFile.getBytes(); 
	        fout.write(bytes); 
	        fout.close(); 
	        return true;
		} catch (Exception e) {
			return false;
		}				
	}
	
}
