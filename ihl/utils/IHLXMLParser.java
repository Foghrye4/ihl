package ihl.utils;

import ihl.IHLMod;
import ihl.guidebook.IHLGuidebookGui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import com.google.common.io.Files;


public class IHLXMLParser {

	public DocumentBuilderFactory dbf;
	public DocumentBuilder db;
	
	public IHLXMLParser() throws ParserConfigurationException
	{
		dbf = DocumentBuilderFactory.newInstance();
	    db = dbf.newDocumentBuilder();
	}
	
	public void visit(Node node, int level, int sectionNumber1, IHLGuidebookGui ihlGuidebookGui) 
	{ 
	  IHLMod.log.debug("Visiting node.");
	  IHLMod.log.debug("Current section="+sectionNumber1);
	  IHLMod.log.debug("Node name="+node.getNodeName());
	  int sectionNumber = sectionNumber1;
	  NodeList list = node.getChildNodes(); 
	  IHLMod.log.debug("child size="+list.getLength());
      if (node instanceof Element)
	  {
    	  IHLMod.log.debug("node instance of Element.");
    	  Element e = (Element) node; 
    	  IHLMod.log.debug("Node tagname="+e.getTagName());
    	  IHLMod.log.debug("Node text content="+e.getTextContent());
	      if(e.getTagName().equals("title"))
	      {
	        	ihlGuidebookGui.setTitle(IHLUtils.trim(e.getTextContent()));
	      }
	      else if(e.getTagName().equals("itemstack"))
	      {
	        	String[] innername = IHLUtils.trim(e.getTextContent()).split(":");
	        	ihlGuidebookGui.addItemStack(IHLUtils.getOtherModItemStackWithDamage(innername[0], innername[1], Integer.parseInt(e.getAttribute("damage"))));
	      }
	      else if(e.getTagName().equals("text"))
	      {
	    	  ihlGuidebookGui.addTextBlock(IHLUtils.trim(e.getTextContent()));
	      }
	      else if(e.getTagName().equals("image"))
	      {
	        	ihlGuidebookGui.setPicture(IHLUtils.trim(e.getTextContent()).replace(" ", ""), Integer.parseInt(e.getAttribute("width")),Integer.parseInt(e.getAttribute("height")));
	      }
	  }
	  for (int i = 0; i < list.getLength(); i++) 
	  {
	      Node childNode = list.item(i); 
	      if(childNode instanceof Element && ((Element) childNode).getTagName().equals("section"))
	      {
	    	  	Element e = (Element) childNode;
	    	  	int id = Integer.parseInt(e.getAttribute("id"));
	    	  	ihlGuidebookGui.setMaxSectionNumber(id);
		    	if(sectionNumber==id)
		    	{
				    visit(childNode, level + 1, sectionNumber, ihlGuidebookGui); 
		    	}
		    	else
		    	{
		    		if(sectionNumber > ihlGuidebookGui.getMaxSectionNumber())
		    		{
		    		  sectionNumber=0;
		    		  ihlGuidebookGui.setSectionNumber(0);
				      visit(childNode, level + 1, sectionNumber, ihlGuidebookGui); 
		    		}
		    	}
	      }
	      else
	      {
		      visit(childNode, level + 1, sectionNumber, ihlGuidebookGui); 
	      }
	  } 
	} 
	 
	public void setupGuidebookGUI(IHLGuidebookGui ihlGuidebookGui, int sectionNumber) throws SAXException, IOException
	{
		Document doc = db.parse(IHLMod.class.getResourceAsStream("/assets/ihl/config/ihl-guidebook.xml"));
	    visit(doc, 0, sectionNumber, ihlGuidebookGui); 
	} 
	
    private File getGuidebookFile() throws IOException
    {
        File folder = new File(IHLMod.proxy.getMinecraftDir(), "config");
        folder.mkdirs();
        File file = new File(folder, "ihl-guidebook.xml");
        if(!file.exists())
        {
        	InputStream in = IHLMod.class.getResourceAsStream("/assets/ihl/config/ihl-guidebook.xml");
	        InputStreamReader isReader = new InputStreamReader(in, "UTF-8");
	        LineNumberReader reader = new LineNumberReader(isReader);
        	OutputStreamWriter osWriter = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
			BufferedWriter writer = new BufferedWriter(osWriter);
	        String line;
			while ((line = reader.readLine()) != null)
			{
				writer.append(line);
				writer.newLine();
			}
			writer.close();
			osWriter.close();
			in = IHLMod.class.getResourceAsStream("/assets/ihl/config/adress.xsd");
	        isReader = new InputStreamReader(in, "UTF-8");
	        reader = new LineNumberReader(isReader);
	        file = new File(folder, "adress.xsd");
        	osWriter = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
			writer = new BufferedWriter(osWriter);
			while ((line = reader.readLine()) != null)
			{
				writer.append(line);
				writer.newLine();
			}
			writer.close();
			osWriter.close();
        }
        return file;
    }
	
}