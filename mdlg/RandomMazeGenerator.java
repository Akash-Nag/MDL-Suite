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

/**
 * This class handles the maze creation process
 */
public class RandomMazeGenerator
{
	/**
	 * Stores the generated maze as a boolean matrix with true representing vacant cells and false otherwise
	 */
	private static boolean maze[][];
	
	/**
	 * A random object used to generate random positions for placing the walls inside a chamber
	 */
	private static java.util.Random random;

	/**
	 * The primary function that starts the maze generation process and calls other maze generation functions
	 * 
	 * @param width			The width of the maze in number of cells
	 * @param height		The height of the maze in number of cells
	 * @return 				The generated maze as a boolean matrix
	 */
	public static boolean[][] generateMaze(int width, int height)
	{
		// check if dimensions are valid
		if(width < 3 || height < 3) return null;
		
		// create new blank maze with borders all around
		random = new java.util.Random();
		maze = new boolean[height][width];
		for(int i=1; i<height-1; i++)
		{
			for(int j=1; j<width-1; j++)
			{
				maze[i][j] = true;
			}
		}

		mazifyChamber(1, 1, height-2, width-2);
		createExits(height, width);

		return maze;
	}

	/**
	 * The recursive procedure to generate the maze through recursive-division
	 * 
	 * @param rowStart	The starting index of the row
	 * @param colStart	The starting index of the column
	 * @param rowEnd	The ending index of the row
	 * @param colEnd	The ending index of the column
	 * @return 			Nothing
	 */
	private static void mazifyChamber(int rowStart, int colStart, int rowEnd, int colEnd)
	{
		int dCol = colEnd - colStart;
		int dRow = rowEnd - rowStart;
		if(dCol <= 1 || dRow <= 1) return;
		
		// randomly select 2 orthogonal walls
		int verticalWall = 0, horizontalWall = 0;

		do {
			verticalWall = colStart + 1 + random.nextInt(dCol-1);
			horizontalWall = rowStart + 1 + random.nextInt(dRow - 1);
		} while(verticalWall - colStart == 2 || horizontalWall - rowStart == 2);

		// draw the walls
		drawWalls(rowStart, colStart, rowEnd, colEnd, verticalWall, horizontalWall);
		
		// draw 3 holes in randomly selected 3 out of 4 wall sections
		drawHoles(rowStart, colStart, rowEnd, colEnd, verticalWall, horizontalWall);

		// call itself recursively
		mazifyChamber(rowStart, colStart, horizontalWall-1, verticalWall-1);
		mazifyChamber(rowStart, verticalWall+1, horizontalWall-1, colEnd);
		mazifyChamber(horizontalWall+1, colStart, rowEnd, verticalWall-1);
		mazifyChamber(horizontalWall+1, verticalWall+1, rowEnd, colEnd);		
	}

	/**
	 * Divides a chamber into four sub-chambers by drawing a vertical and a horizontal wall
	 * 
	 * @param rs	The row-index of the top-left coordinate of the chamber
	 * @param cs	The column-index of the top-left coordinate of the chamber
	 * @param re	The row-index of the bottom-right coordinate of the chamber
	 * @param ce	The column-index of the bottom-right coordinate of the chamber
	 * @param vWall	The distance of the vertical wall from the left of the chamber
	 * @param hWall	The distance of the horizontal wall from the top of the chamber
	 * @return 		Nothing
	 */
	private static void drawWalls(int rs, int cs, int re, int ce, int vWall, int hWall)
	{
		for(int i=rs; i<=re; i++) 
		{
			// ensure that wall does not block a hole
			if(i==rs && maze[i-1][vWall]) continue;
			if(i==re && maze[i+1][vWall]) continue;
			
			maze[i][vWall] = false;
		}

		for(int i=cs; i<=ce; i++) 
		{
			// ensure that wall does not block a hole
			if(i==cs && maze[hWall][i-1]) continue;
			if(i==ce && maze[hWall][i+1]) continue;
			
			maze[hWall][i] = false;
		}
	}

	/**
	 * Draw holes/exits at three out of four wall-sections dividing a chamber
	 * 
	 * @param rs	The row-index of the top-left coordinate of the chamber
	 * @param cs	The column-index of the top-left coordinate of the chamber
	 * @param re	The row-index of the bottom-right coordinate of the chamber
	 * @param ce	The column-index of the bottom-right coordinate of the chamber
	 * @param vWall	The distance of the vertical wall from the left of the chamber
	 * @param hWall	The distance of the horizontal wall from the top of the chamber
	 * @return 		Nothing
	 */
	private static void drawHoles(int rs, int cs, int re, int ce, int v, int h)
	{
		int sections[][] = {
				{ rs, v, h-1, v },	// top-vertical
				{ h, v+1, h, ce },	// right-horizontal
				{ h+1, v, re, v },	// bottom-vertical;
				{ h, cs, h, v-1 }	// left-horizontal
		};

		// select a number between 0 and 3 (both inclusive)
		// and then do not put hole in that wall, therefore 3 will be selected for putting holes
		int drop = random.nextInt(4);

		for(int i=0; i<4; i++)
		{
			if(i==drop) continue;		// do not select this wall

			int dRow = sections[i][2] - sections[i][0];
			int dCol = sections[i][3] - sections[i][1];
			// 3,5 --> 10,5, dr=7, dc=0
			int offset = (dRow==0 ? random.nextInt(dCol+1) : random.nextInt(dRow+1));
			if(dRow==0)
			{
				int row = sections[i][0];
				maze[row][sections[i][1] + offset] = true; 
			} else {
				int col = sections[i][1];
				maze[sections[i][0] + offset][col] = true;
			}
		}
	}

	/**
	 * Creates the entrance and exit at the first possible position in the
	 * first and last rows of the maze
	 * 
	 * @param h	The height of the maze in number of cells
	 * @param w The width of the maze in number of cells
	 * @return 	Nothing
	 */
	private static void createExits(int h, int w)
	{
		for(int i=1; i<w-1; i++)
		{
			if(maze[1][i])
			{
				maze[0][i] = true;
				break;
			}
		}

		for(int i=w-2; i>=1; i--)
		{
			if(maze[h-2][i])
			{
				maze[h-1][i] = true;
				break;
			}
		}
	}
}