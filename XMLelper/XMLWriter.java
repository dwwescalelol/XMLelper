package zaz.xml;

import java.io.StringWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;

/**
 * A class contatining a collection of static methods used to write XML to.
 * @author Zaz
 */
public class XMLWriter
{
	/**Prints to the console the raw XML of a given node and all its ancestors.
	 * 
	 * @param node  starting Node of XML to print. 
	 * @throws TransformerConfigurationException
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 */
	public static void printXML(Node node)
	{
		try
		{
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
	
			StringWriter output = new StringWriter();
			
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(new DOMSource(node), new StreamResult(output));
			
			System.out.println(output);
		}
		catch (TransformerFactoryConfigurationError | TransformerException e)
		{
			e.printStackTrace();
		}
	}
	
	/**Returns a string of the XML of a given node and all its ancestors.
	 * 
	 * @param node  starting Node of XML to print. 
	 * @throws TransformerConfigurationException
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 * @return String  returns the raw XML as a string.
	 */
	public static String XMLtoString(Node node)
	{
		try
		{
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
	
			StringWriter output = new StringWriter();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(new DOMSource(node), new StreamResult(output));
			
			return output.toString();
		}
		catch (TransformerFactoryConfigurationError | TransformerException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	/**Writes the raw XML of a given node and all its ancestors 
	 * 
	 * @param node  starting tag of XML.
	 * @param filePath  file to write XML to.
	 * @throws TransformerConfigurationException
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 */
	public static void writeXML(Node node, String filePath)
	{
		try
		{
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			//transforms input XML from node into a StreamResult that outputs to a file path
			transformer.transform(new DOMSource(node), new StreamResult(filePath));
		}
		catch (TransformerFactoryConfigurationError | TransformerException e)
		{
			e.printStackTrace();
		}
		
	}
}
