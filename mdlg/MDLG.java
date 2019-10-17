/**
 * Maze Description Language Generator
 * Distributed under Artistic License 2.0
 * 
 * Generates random mazes in MDL format using the recursive division method
 * 
 * @author	Akash Nag
 * @version	1.0
 */

package mdlg;

import java.io.*;

/**
 * This is the main class handling the user-parameters, reading configuration files
 * and calling appropriate maze generation functions.
 */
public class MDLG
{
	/**
	 * The current version of the MDL generator
	 */
	public static final String VERSION = "1.0";

	/**
	 * The usage string that will be displayed if the program is invoked
	 * without parameters or with incorrect parameters
	 */
	private static final String INFO = "Maze Description Language Generator - v" + VERSION +
										"\nby Akash Nag. This software is open-source and is distributed under Artistic License 2.0." +
										"\nUsage:\n\t java -jar mdlg.jar <config-file> <output-files-in-mdl-format>";
										
	/**
	 * The width of the maze (in number of cells) that will be generated
	 */
	private static int width;

	/**
	 * The height of the maze (in number of cells) that will be generated
	 */
	private static int height;

	/**
     * This is the main method, and it processes the command-line arguments
     * 
     * @param args 			The name of the configuration file followed by one or more output file names
     * @return 				Nothing
     * @exception Exception On input error or error in the syntax of the configuration file.
     * @see 				Exception
     */
	public static void main(String args[]) throws Exception
	{
		if(args.length < 2)
		{
			printUsage();
		} else {
			try {
				String config = readConfiguration(args[0]);
				for(int i=1; i<args.length; i++)
				{
					String output = args[i];
					boolean maze[][] = RandomMazeGenerator.generateMaze(width, height);
					writeMazeToFile(maze, config, output);
					System.out.println(i + "/" + (args.length-1) + ": maze generated successfully");
				}
			} catch(Exception e) {
				System.out.println("An error occurred during maze generation:\n" + e.toString());
				e.printStackTrace();
			}
		}
	}

	/**
	 * Displays the version information and the list of parameters 
	 * to be passed to the program at command-line
	 * 
	 * @param 	Nothing
	 * @return 	Nothing
	 */
	private static void printUsage()
	{
		System.out.println(INFO);
	}

	/**
	 * Reads the configuration file passed as the first parameter to the program.
	 * The configuration file contains settings that will be common to all generated mazes.
	 * 
	 * @param fileName			Name of the configuration file
	 * @return 					A string containing a new-line-separated list of configurations
	 * @exception IOException	On input error
	 * @see 					IOException
	 */
	private static String readConfiguration(String fileName) throws IOException
	{
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String s = null;
		while((s=br.readLine())!=null)
		{
			String line = s.toLowerCase().trim();
			if(line.startsWith("[size:") && line.endsWith("]"))
			{
				int x[] = paramsAsInt(extract(line, "[size:", "]"), ",");
				width = x[0];
				height = x[1];
			}
			sb.append(s + "\n");
		}
		br.close();
		return sb.toString();
	}

	/**
	 * Merges and writes the configuration and generated maze into an output file in MDL format
	 * 
	 * @param maze				A boolean matrix representing the generated maze, indicating true for path or false for blocked cells
	 * @param config			The list of configurations about the maze
	 * @param outputFile		The name of the output file
	 * @return 					Nothing
	 * @exception IOException	On output error
	 */
	private static void writeMazeToFile(boolean maze[][], String config, String outputFile) throws IOException
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		bw.write(config);
		bw.write(mazeToString(maze));
		bw.close();
	}

	/**
	 * Prepares a generated maze for writing to a file by converting it from boolean to a string in MDL format
	 * 
	 * @param maze		The generated maze
	 * @return 			A string representation of the maze in MDL format
	 */
	private static String mazeToString(boolean maze[][])
	{
		StringBuffer sb = new StringBuffer(height * width);
		for(int i=0; i<height; i++)
		{
			sb.append("r" + i + ":");
			for(int j=0; j<width; j++)
			{
				if(maze[i][j])
					sb.append("p");
				else
					sb.append("w");
			}
			sb.append(";\n");
		}
		return sb.toString();
	}

	/**
	 * An utility function used to extract a portion of a string with the given prefix and suffix
	 * 
	 * @param s			The string to process
	 * @param prefix	The prefix to match
	 * @param suffix	The suffix to match
	 * @return 			The extracted substring with the given prefix and suffix
	 */
	private static String extract(String s, String prefix, String suffix)
	{
		int pos = s.indexOf(prefix);
		if(pos==-1) return null;
		String res = s.substring(pos+prefix.length());
		if(!res.endsWith(suffix)) return null;
		return res.substring(0, res.length()-suffix.length());
	}

	/**
	 * An utility function separating a delimiter-separated string to separate integer arguments
	 * 
	 * @param s			The given string
	 * @param sep		The delimeter
	 * @return 			An integer array containing each individual element in the given string
	 */
	private static int[] paramsAsInt(String s, String sep)
	{
		String x[] = s.split(sep);
		int p[] = new int[x.length];
		for(int i=0; i<x.length; i++) p[i] = Integer.parseInt(x[i]);
		return p;
	}
}