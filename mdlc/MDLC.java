/**
 * Maze Description Language Compiler
 * Distributed under Artistic License 2.0
 * 
 * Compiles mazes given in MDL format to beautiful maze images or Java/Python code
 * 
 * @author	Akash Nag
 * @version	1.0
 */

package mdlc;

/**
 * This is the main class handling all command-line arguments and calling other appropriate functions
 * to compile the maze
 */
public class MDLC
{
	/**
	 * The current version of the MDL generator
	 */
	public static final String VERSION = "1.0";

	/**
	 * The usage string that will be displayed if the program is invoked
	 * without parameters or with incorrect parameters
	 */
	private static final String INFO = "Maze Description Language Compiler - v" + VERSION +
										"\nby Akash Nag. This software is open-source and is distributed under Artistic License 2.0" +
										"\nUsage:\n\t java -jar mdlc.jar <maze-description-files>" +
										"\nNote:\n" +
										"Output file name must not include the file extension as it will be added automatically depending on the settings specified in the source";

	/**
     * This is the main method, and it processes the command-line arguments
     * 
     * @param args 			The names of the maze files in MDL format
     * @return 				Nothing
     * @exception Exception On input error or error in the syntax of the configuration file.
     * @see 				Exception
     */
	public static void main(String args[]) throws Exception
	{
		if(args.length==0)
		{
			printUsage();
		} else {
			try {
				for(int i=0; i<args.length; i++)
				{
					String source = args[i];
					String output = source.substring(0, source.lastIndexOf("."));
				
					Description desc = Description.readFile(source);
					Generator.generateMaze(desc, output);
					System.out.println((i+1) + "/" + args.length + ": maze generated successfully");
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
}