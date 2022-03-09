package zaz.xml;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 * A class contatining a collection of static methods used to make XML Document nodes.
 * @author Zaz
 *
 */
public class XMLMaker
{
	
	/**Creates a Document node from a XML file
	 * 
	 * @param file  file to make XML Document node from
	 * @return Document  node of XML tree from specified file
	 */
	public static Document makeDocNode(Path file)
	{
		try
		{
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			return db.parse(file.toFile());
		}
		catch (IOException | ParserConfigurationException | SAXException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**Creates a Document node from a URL
	 * 
	 * @param url  url to make XML Document node from
	 * @return Document  node of XML tree from specified webpage
	 */
	public static Document makeDocNode(String url)
	{
		try
		{
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			
			URLConnection urlConnection = new URL(url).openConnection();

			return db.parse(urlConnection.getInputStream());
		}
		catch (IOException | ParserConfigurationException | SAXException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
}
