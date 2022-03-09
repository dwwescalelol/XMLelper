package zaz.xml;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** 
 * A class contatining a collection of static methods used to get information on XML nodes in a given XML tree.
 *
 * @author Zaz
 */
public class XMLInfo
{

	/**Prints a given nodes name and type to console 
	 * 
	 * @param n  Node to print name and type to console
	 */
	public static String printNodeType(Node n)
	{
		String nodeType = n.getNodeName() + ": " + n.getNodeType();
		System.out.println(nodeType);
		return nodeType;
	}
	
	/**Prints a given nodes childrens names and types to console
	 * 
	 * @param n  Nodes children to print name and type to console
	 */
	public static String printChildrenTypes(Node n)
	{
		String nodeType = "";
		for(int i = 0; i < n.getChildNodes().getLength(); i++)
			nodeType += printNodeType(n.getChildNodes().item(i));
		return nodeType;
	}
	
	/** Itterates and recurses through a nodes children to return a string format of all values
	 * <p>
	 * Start node must not have only text nodes for children, if this is the case use the 
	 * getTextContent() method insted.
	 * 
	 * @param node  Node to print content of
	 * @return String  A String representation of the content of a node.
	 */
	public static String printChildren(Node node)
	{
		//node is a leaf - return empty string
		if(!node.hasChildNodes())
			return "";
		
		String content = "";
		NodeList childNodes = node.getChildNodes();
		
		for(int i = 0; i < childNodes.getLength(); i++)
		{
			Node n = childNodes.item(i);
			
			//if all nodes are children add the node name and content to variable else recurse
			if(areChildrenTextNodes(n))
				content += n.getNodeName() + ": " + n.getTextContent() + "\n";
			else
				content += printChildren(n);
		}
		
		return content;
	}
	
	/**Itterates through a nodes children and determins if all child nodes are text nodes
	 * 
	 * @param node  node to see if children are all text
	 * @return boolean true if all children nodes are text nodes, false if not.
	 */
	public static boolean areChildrenTextNodes(Node node)
	{
		//if node itself is a leaf return false
		if(!node.hasChildNodes())
			return false;
		
		NodeList childNodes = node.getChildNodes();
		
		for(int i = 0; i < childNodes.getLength(); i++)
			if(childNodes.item(i).getNodeType() != 3)
				return false;
		
		return true;
	}
	
	/**Returns height of a given node in a tree
	 * 
	 * @param node  node to return height of
	 * @return int  depth of a node from its root
	 */
	public static int nodeHeight(Node node)
	{
		return nodeHeight(node.getOwnerDocument(), node);
	}

	private static int nodeHeight(Node node, Node child)
	{
		//if node is leaf return null
		if(!node.hasChildNodes())
			return 0;
		
		NodeList childNodes = node.getChildNodes();
		
		for(int i = 0; i < childNodes.getLength(); i++)
		{
			Node n = childNodes.item(i);
			if(n.equals(child))
				return 1;
			
			int height = nodeHeight(n,child);
			
			if(height > 0)
				return ++height;
		}
		
		return 0;
	}
	
	
	/**Checks if all non text nodes node names are equal
	 * 
	 * @param n Node to check if childrens node names are equal
	 * @return boolean true if all node names are the same, false if not
	 */
	public static boolean areChildrenNamesEqual(Node n)
	{
		NodeList children = n.getChildNodes();
		//all children are compared to the first element childs name
		if(children.getLength() == 0)
			return true;
		
		String firstChildName = XMLParser.getFirstElement(n).getNodeName();
		for(int i= 0; i < children.getLength(); i++)
		{
			Node child = children.item(i);
			if(child.getNodeType() != Node.TEXT_NODE)
				if(!child.getNodeName().equals(firstChildName))
					return false;
		}
		//if all node names are equal program will get to this point 
		return true;
	}
	
	/** Returns the height of the tree of a given node
	 * 
	 * @param node  node of tree to search
	 * @return int  height of the nodes tree
	 */
	public static int treeHeight(Node node)
	{
		return treeHeightRecursive(node.getOwnerDocument());
	}
	
	private static int treeHeightRecursive(Node node)
	{
		if(!node.hasChildNodes())
			return 1;
		
		NodeList childNodes = node.getChildNodes();
		int[] childSize = new int[childNodes.getLength()];
		
		for(int i = 0; i < childNodes.getLength(); i++)
			childSize[i] = treeHeightRecursive(childNodes.item(i));
		
		return 1 + maxArray(childSize);
	}
	
	private static int maxArray(int[] arr)
	{
		int max = arr[0];
		for(int i : arr)
			if(max < i)
				max = i;
		
		return max;
	}
	
	
}
