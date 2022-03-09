 package zaz.xml;

import org.w3c.dom.*;


/**
 * A class contatining a collection of methods used to find and return XML nodes in a given XML tree.
 * @author Zaz
 */
public class XMLParser
{
	
	/** Itterates and recurses through a list of nodes and returns the node with a given name.
	 * <p>
	 * If no node is pressent with given name will return null. Uses getChildNodes and itterates
	 * through list. If node is not found in child node, recursion begins.
	 * 
	 * @param node  Node to search 
	 * @param childName  Name of node child to search for
	 * @return Node  Child node of node param with the name of childName param
	 */
	public static Node findChild(Node node, String childName)
	{
		//if node is leaf return null
		if(!node.hasChildNodes())
			return null;
			
		NodeList childNodes = node.getChildNodes();
		
		for(int i = 0; i < childNodes.getLength(); i++)
		{
			Node n = childNodes.item(i);
			if(n.getNodeName().equals(childName))
				return n;
			
			 n = findChild(childNodes.item(i), childName);
				if(n != null && n.getNodeName().equals(childName))
					return n;
		}
		
		return null;
	}
	
	/** Itterates and recurses through a list of nodes and returns the node with a given name and value.
	 * <p>
	 * If no node is pressent with given name will return null. Uses getChildNodes and itterates
	 * through list. If node is not found in child node, recursion begins.
	 * 
	 * @param node  Node to search 
	 * @param childName  Name of node child to search for
	 * @param childValue  String value of the node being searched for
	 * @return Node  Child node of node param with the name of childName param
	 */
	public static Node findChild(Node node, String childName, String childValue)
	{
		//if node is leaf return null
		if(!node.hasChildNodes())
			return null;
		
		NodeList childNodes = node.getChildNodes();
		
		//checks if children are the search node
		for(int i = 0; i < childNodes.getLength(); i++)
		{
			Node n = childNodes.item(i);
			
			if(n.getNodeName().equals(childName))			//if childName is correct
				if(n.getTextContent().equals(childValue))	//see if the value of current node is param
					return n;								//if so return n 
			
			 n = findChild(childNodes.item(i), childName, childValue);
				if(n!=null && n.getNodeName().equals(childName))	//need to check if null as cannot get
					if(n.getTextContent().equals(childValue))	//node name of null node
						return n;
		}
		return null;
	}
	
	/** Searches through a tree and returns a boolean if the searched node is in the tree 
	 * <p>
	 * Calls the {@link findChild} method and returns a boolean based on
	 * if the value is null.
	 * 
	 * @param node Node  node to search through
	 * @param childName String  Name of the node being searched
	 * @return boolean  true if node has the specified node, false if not
	 */
	public static boolean hasChild(Node node, String childName)
	{
		return findChild(node, childName) == null;
	}
	
	
	/** Searches through a tree and returns a boolean if the searched node is in the tree 
	 * <p>
	 * Calls the {@link findChild} method and returns a boolean based on
	 * if the value is null.
	 * 
	 * @param node Node  node to search through
	 * @param childName String  Name of the node being searched
	 * @param childValue String Value of the value of target node
	 * @return boolean  true if node has the specified node, false if not
	 */
	public static boolean hasChild(Node node, String childName, String childValue)
	{
		return findChild(node, childName, childValue) != null;
	}
	
	
	/** Searches through a tree and returns a boolean if the searched node is in the tree 
	 * <p>
	 * Recurses through the tree and returns true if the current node is the search node.
	 * 
	 * @param node Node  node to search through
	 * @param child Node  node to find
	 * @return boolean  true if node has the specified node, false if not
	 */
	public static boolean hasChild(Node node, Node child)
	{
		if(!node.hasChildNodes())
			return false;
		
		NodeList childNodes = node.getChildNodes();
		
		for(int i = 0; i < childNodes.getLength(); i++)
		{
			Node n = childNodes.item(i);
			if(n.equals(child))
				return true;
			
			if(hasChild(n,child))
				return true;
		}
		
		return false;
	}
	
	public static Node getFirstElement(Node n)
	{
		NodeList children = n.getChildNodes();
		
		for(int i = 0; i < children.getLength(); i++)
			if(children.item(i).getNodeType() == Node.ELEMENT_NODE)
				return children.item(i);
		
		return null;
	}
}