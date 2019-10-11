package mdlg;

import java.io.*;

public class MDLG
{
	public static final String VERSION = "1.0";
	private static final String INFO = "Maze Description Language Generator - v" + VERSION +
										"\nby Akash Nag. This software is open-source and is distributed under Artistic License 2.0." +
										"\nUsage:\n\t java -jar mdlg.jar <config-file> <output-files-in-mdl-format>";
										
	private static int width;
	private static int height;

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

	private static void printUsage()
	{
		System.out.println(INFO);
	}

	private static String readConfiguration(String fileName) throws Exception
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

	private static void writeMazeToFile(boolean maze[][], String config, String outputFile) throws IOException
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		bw.write(config);
		bw.write(mazeToString(maze));
		bw.close();
	}

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

	private static String extract(String s, String prefix, String suffix)
	{
		int pos = s.indexOf(prefix);
		if(pos==-1) return null;
		String res = s.substring(pos+prefix.length());
		if(!res.endsWith(suffix)) return null;
		return res.substring(0, res.length()-suffix.length());
	}

	private static int[] paramsAsInt(String s, String sep)
	{
		String x[] = s.split(sep);
		int p[] = new int[x.length];
		for(int i=0; i<x.length; i++) p[i] = Integer.parseInt(x[i]);
		return p;
	}
}