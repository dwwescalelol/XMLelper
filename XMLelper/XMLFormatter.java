package zaz.xml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class XMLFormatter
{
	/**Rewrites the XML in the given file to be all on one line
	 * 
	 * @param input  file of XML to be minified  
	 */
	public static void minify(Path input)
	{
		String minified = readAndMinify(input);
		
		writeMinified(input, minified);
	}

	/**Reads a given XML file and writes the minified XML to a given file
	 * 
	 * @param input file to read XML from
	 * @param output file to output minified XML to
	 */
	public static void minify(Path input, Path output)
	{
		String minified = readAndMinify(input);
		
		writeMinified(output, minified);
	}

	//helper method called in minify methods
	private static String readAndMinify(Path input)
	{
		//using stringbuilder as more efficient than concatinating strings.
		String line = "";
		StringBuilder sr = new StringBuilder();

		try(BufferedReader br = Files.newBufferedReader(input))
		{
			//reads readers next line, trims it and appends to stringbuilder
			while((line=br.readLine())!= null)
				sr.append(line.trim());

			return sr.toString();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;		
	}
	
	//helper method called in minify methods
	private static void writeMinified(Path input, String minified)
	{
		try(BufferedWriter bw = Files.newBufferedWriter(input))
		{
			bw.write(minified.toString());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}