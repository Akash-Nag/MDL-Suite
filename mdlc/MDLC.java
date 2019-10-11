package mdlc;

public class MDLC
{
	public static final String VERSION = "1.0";
	private static final String INFO = "Maze Description Language Compiler - v" + VERSION +
										"\nby Akash Nag. This software is open-source and is distributed under Artistic License 2.0" +
										"\nUsage:\n\t java -jar mdlc.jar <maze-description-files>" +
										"\nNote:\n" +
										"Output file name must not include the file extension as it will be added automatically depending on the settings specified in the source";

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

	private static void printUsage()
	{
		System.out.println(INFO);
	}
}