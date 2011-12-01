package com.jenkins.nativeDroid;

import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RSSReader {
	private static RSSReader instance = null;
	
	private RSSReader() {
		
	}
	
	public static RSSReader getInstance() {
		if(instance == null)
			instance = new RSSReader();
		return instance;
	}
	
	public String writeNews(String url) {
		String string = "";
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			URL u = new URL(url); // your feed url
			Document doc = builder.parse(u.openStream());
			NodeList nodes = doc.getElementsByTagName("entry");
			for(int i=0; i<nodes.getLength(); i++) {
				Element element = (Element)nodes.item(i);
				string += (
						getElementValue(element,"title") + "#t#" + 
						getElementValue(element, "updated") + "#l#"
						);
			}//for
		}//try
		catch(Exception ex) {
			ex.printStackTrace();
		}
		return string;
	}
	
	private String getCharacterDataFromElement(Element e) {
		try {
			Node child = e.getFirstChild();
			if(child instanceof CharacterData) {
				CharacterData cd = (CharacterData) child;
				return cd.getData();
			}
		}catch(Exception ex) {
			
		}
		return "";
	}
	
	protected float getFloat(String value) {
		if(value != null && !value.equals(""))
			return Float.parseFloat(value);
		else
			return 0;
	}
	
	protected String getElementValue(Element parent,String label) {
		return getCharacterDataFromElement((Element)parent.getElementsByTagName(label).item(0));
	}
}
