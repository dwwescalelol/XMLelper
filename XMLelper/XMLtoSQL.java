package zaz.xml;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

import zaz.sql.SQLInfo;

import org.w3c.dom.*;

//make function to find node with most amout of subnodes
//this will make sure all fields are covered

public class XMLtoSQL
{
	//allows us to easily change the database used
	private static final String JDBC_CONNECTION_STRING = "jdbc:sqlite:dummy.db";
	private static final String DATABASENAME = "dummy";
	
	//allows easy acess to table and primary keys or current database
	private static ArrayList<String> TABLENAMES;
	private static HashMap<String, String> PRIMARYKEYS;
	
	private static Statement SQLSTATMENT;
	
	//batch statment to execute SQL and amount of statments in batch
	private static StringBuilder SQL;
	private static int STATMENTCOUNT = 0;
	
	/**Creates a database from a given XML node, assigns the primary key given. If 
	 * one is not given the primary key is assumed to be the first attribute of a given node.
	 * 
	 * @param n {@link Node} of XML to be turned into SQLite database
	 * @param pk Primary key to be assinged
	 */
	public static void makeDatabase(Node n, String pk)
	{
		SQL = new StringBuilder();
		
		makeTables(n, pk);
		fillData(n.getOwnerDocument());
		
//		WriterUtil.WriteToFile(Paths.get("E:\\elcipse\\Advanced Programing\\data\\dummy.sql"), pk);
	}
	
	/**Takes a XML node and fills a SQLite database with the values of given node and its children.
	 * 
	 * @param n {@link Node} to start filling data into SQlite dataabase
	 */
	public static void fillData(Node n)
	{
		try(Connection c = DriverManager.getConnection(JDBC_CONNECTION_STRING))
		{
			//asign static properties
			SQLSTATMENT = c.createStatement();
			TABLENAMES = SQLInfo.getTableNames(c);	
			PRIMARYKEYS = SQLInfo.getPrivateKeys(c);
			
			//pass in node, connection and null as first instance will nto have forign key
			insertNodeData(n, c, null);
			
			//execute SQL
			System.out.println("filled data");
		}
		catch (SQLException se) 
		{
			System.err.println(se.getMessage());
			se.printStackTrace();
		}
	}
	
	private static void insertNodeData(Node n, Connection c, Node fkNode) throws SQLException
	{
		//if node should not be a table
		if(n == null)
			return;
		//if current node is not a table, recurse through children and insert their fields
		if(!TABLENAMES.contains(n.getNodeName()))
		{
			NodeList children = n.getChildNodes();
			for(int i = 0; i < children.getLength(); i++)
				insertNodeData(children.item(i),c,fkNode);
			return;
		}
		ArrayList<String> fields = new ArrayList<>();
		ArrayList<String> values = new ArrayList<>();
		if(fkNode != null)
		{
			fields.add(fkNode.getNodeName());
			values.add(fkNode.getTextContent());
		}
			
		if(PRIMARYKEYS.containsKey(n.getNodeName()))
		{
			String pkNodeName  = PRIMARYKEYS.get(n.getNodeName());
			fkNode = XMLParser.findChild(n, pkNodeName);
			if(fkNode == null)
				fkNode = n.getAttributes().getNamedItem(pkNodeName); 
		}
		ArrayList<Node> subTables = insertFieldsIntoTable(n, c, fields, values);
		
		//recurse through nodes eligible to be more tables
		for(Node t : subTables )
			insertNodeData(t,c,fkNode);
	}

	private static ArrayList<Node> insertFieldsIntoTable(Node n, Connection c, ArrayList<String> fields, ArrayList<String> values) throws SQLException
	{
		//datastructures to increment through where fields are node names and values are textcontent
		NodeList children = n.getChildNodes();
		NamedNodeMap attributes = n.getAttributes();
		
		//list to keep track of enteties

		ArrayList<Node> subTables = new ArrayList<>();
		
		for(int i = 0; i < children.getLength(); i++)
		{
			Node child = children.item(i);
			if(XMLInfo.areChildrenTextNodes(child))
			{
				fields.add(child.getNodeName());
				values.add(child.getTextContent());
			}
			else if(Node.TEXT_NODE != child.getNodeType())
				subTables.add(child);
		}
		
		for(int i = 0; i < attributes.getLength(); i++)
		{
			Node child = attributes.item(i);
			fields.add(child.getNodeName());
			values.add(child.getTextContent());
		}
		
		//if nothing in statment do not execute statment
		if(fields.size() == 0)
			return subTables;
		
		StringBuilder SQLStatment = new StringBuilder();
		
		SQLStatment.append("INSERT INTO " + n.getNodeName() + "(");
		
		for(String f : fields)
			SQLStatment.append(f + ",");
		
		SQLStatment.setLength(SQLStatment.length() -1);
		SQLStatment.append(")\nValues(");
		
		for(String v : values)
			SQLStatment.append("\"" + v + "\",");
		
		SQLStatment.setLength(SQLStatment.length() -1);
		SQLStatment.append(");");
		executeSQL(c, SQLStatment);
		
		return subTables;
	}
	
	/**Creates SQLite tables from a XML node with a specified public key. 
	 * 
	 * @param n {@link Node} to create SQLite tables from
	 * @param pk primary key of outermost table. 
	 */
	public static void makeTables(Node n, String pk)
	{
		try(Connection c = DriverManager.getConnection(JDBC_CONNECTION_STRING))
		{
			//drops all tables of current table
			SQLInfo.dropAllTables(c);
			System.out.println("tables dropped");
			
			//asigns static variable to statment based on connection
			SQLSTATMENT = c.createStatement();
			
			createTable(n, c, pk, "", n);

			//executes all SQLstatments to create tables
//			SQLSTATMENT.executeBatch();
//			STATMENTCOUNT = 0;
			
			//assigns static variables to have required data
			TABLENAMES = SQLInfo.getTableNames(c);
			PRIMARYKEYS = SQLInfo.getPrivateKeys(c);
			
		}
		catch (SQLException se) 
		{
			System.err.println(se.getMessage());
			se.printStackTrace();
		}
	}

	/**Creates a SQLite table from a specified database, 
	 * from a node with specified private and forign keys.
	 * 
	 * @param n  {@link Node} to crete SQLite table from
	 * @param c  {@link Connection} to SQLite database
	 * @param pk String primary key of table 
	 * @param fk String forign key of table
	 * @param previousTable  String The name of the most outer table, used for forign keys
	 * @throws SQLException if SQLite is invalid
	 */
	private static void createTable(Node n, Connection c, String pk, String fk, Node previousTable) throws SQLException
	{
		//if node should not be a table
		if(n == null)
			return;
		
		//if all children have same name, should not be table. Example library node contains many and only book nodes.
		//book should be table. library should not be included in database.
		System.out.println(n.getNodeName());
		if(XMLInfo.areChildrenNamesEqual(n))
		{
			createTable(XMLParser.getFirstElement(n), c, pk, fk,previousTable);
			return;
		}
		
		//name of this tables fields
		ArrayList<String> fieldNames = new ArrayList<>();
		//tables to create that will use a fk of pk of this table
		ArrayList<Node> subTables = new ArrayList<>();
		
		//adds the required node names and nodes to the fieldNmaes and subTables array lists
		addFieldsAndSubtables(n, fieldNames, subTables);
		
		//generates the SQL code to create the tables and queries
		StringBuilder SQLStatment = createTableFieldsQuery(n.getNodeName(), fieldNames);
		
		//makes the primary key and forign key SQL, would be its own function
		//however no pass by reference in java, thus cannot change the value of pk easily
		boolean hasPk = false;
		boolean hasFk = !fk.equals("");
		
		//checks if pk is in the field names of this table, if so set bool 
		for(String f : fieldNames)
			if(f.equals(pk))
				hasPk = true;
		
		if(n.hasAttributes())
		{
			pk = n.getAttributes().item(0).getNodeName();
			SQLStatment.append("\n\"" + pk + "\"TEXT,");
			hasPk = true;
		}
		
		//if pk is in fields of this table, add the primary key
		if(hasPk)
			SQLStatment.append("\nPRIMARY KEY(\"" + pk +"\"),");
		
		//if fk param is not null, make field and set to forign key
		if(hasFk)
		{
			SQLStatment.append("\n\"" + fk + "\"TEXT,");
			SQLStatment.append("\nFOREIGN KEY(\"" + fk + "\") REFERENCES \"" + previousTable.getNodeName() +"\"(\""+ fk +"\"),");
		}
		
		//gets rid of last "," before closing the statment
		SQLStatment.setLength(SQLStatment.length() -1);
		SQLStatment.append(");\n");
		
		executeSQL(c, SQLStatment);
		
		//recurse through nodes eligible to be more tables
		for(Node t : subTables)
			createTable(t,c,"",pk,n);
	}

	/**
	 * @param n {@link Node} node to iterate through children and assign field and subtables
	 * @param fieldNames {@link ArrayList} of field names to add to.
	 * @param subTables {@link ArrayList} of subTables to add valid nodes to.
	 */
	private static void addFieldsAndSubtables(Node n, ArrayList<String> fieldNames, ArrayList<Node> subTables)
	{
		NodeList children = n.getChildNodes();
		
		for(int i = 0; i < children.getLength(); i++)
		{
			Node child = children.item(i);
			//if all children are text nodes, node is a field
			if(XMLInfo.areChildrenTextNodes(child))
				fieldNames.add(child.getNodeName());
			//if the node is not a text node and contains elements as children, will be a table
			else if(child.getNodeType() != Node.TEXT_NODE)
				subTables.add(child);
		}
	}

	/**Constructs a {@link StringBuilder} that contains the SQLite statment to create
	 * the fields and table for a given node
	 * <p>
	 * A {@link StringBuilder} insted of concatinating strings for efficiency and
	 * to reduce the amount stored on the heap compared with concatinating {@link String}
	 * 
	 * @param tableName String the name of the table to be created
	 * @param fieldNames {@link ArrayList} of field names of table to be created
	 * @return {@link StringBuilder} of SQLite statment to be executed
	 */
	private static StringBuilder createTableFieldsQuery(String tableName , ArrayList<String> fieldNames)
	{
		StringBuilder SQLStatment = new StringBuilder();

		SQLStatment.append("CREATE TABLE \"" + tableName + "\" ( ");
		
		for(String fn : fieldNames)
			SQLStatment.append("\n\"" + fn + "\"TEXT,");
		
		return SQLStatment;
	}

	/**Executes a {@link String} representation of a SQLite statment to a specified database
	 * 
	 * @param c Connection to database for SQLite statments to be executed
	 * @param SQLStatment String representation of SQLite statment to be executed
	 * @throws SQLException if SQLite is invalid
	 */
	private static void executeSQL(Connection c, StringBuilder SQLStatment) throws SQLException
	{
//		STATMENTCOUNT++;

		System.out.println(SQLStatment.toString());

		SQLSTATMENT.executeUpdate(SQLStatment.toString());
	}
	
	/**Executes a {@link String} representation of a SQLite statment to a specified database
	 * 
	 * @param c Connection to database for SQLite statments to be executed
	 * @param SQLStatment String representation of SQLite statment to be executed
	 * @throws SQLException if SQLite is invalid
	 */
	private static void saveQuery(Connection c, StringBuilder SQLStatment) throws SQLException
	{
		STATMENTCOUNT++;

		System.out.println(SQLStatment.toString());

		SQL.append(SQLStatment.toString() + "\n");
	}
}
