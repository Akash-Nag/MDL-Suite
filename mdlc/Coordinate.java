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
 * Defines the position of each cell in a maze as a coordinate consisting of a row and a column
 */
class Coordinate
{
	/**
	 * Defines the row of the coordinate
	 */
	protected int row;

	/**
	 * Defines the column of the coordinate
	 */
	protected int col;

	/**
	 * Constructor to initialize the object with a given row and column
	 * 
	 * @param r	The row index of the coordinate
	 * @param c The column index of the coordinate
	 */
	public Coordinate(int r, int c)
	{
		this.row = r;
		this.col = c;
	}

	/**
	 * Copy-constructor to initialize the object with another coordinate object
	 * 
	 * @param c	The object that will be copied
	 */
	public Coordinate(Coordinate c)
	{
		this.row = c.row;
		this.col = c.col;
	}

	/**
	 * Generates a string representation of the coordinate object
	 * 
	 * @return A string representation of the object
	 */
	@Override
	public String toString()
	{
		return "(" + this.row + ", " + this.col + ")";
	}

	/**
	 * Determines the final position after executing a series of moves through the maze
	 * 
	 * @param moves	A string representing the movement instructions consisting of the 4 directional codes: left, up, right, down
	 * @return		An array of coordinates representing all positions that will be traversed in course of performing the manoeveur
	 */
	public Coordinate[] moveTo(String moves)
	{
		final int rOffset[] = { 0, 0, -1, 1 };
		final int cOffset[] = { -1, 1, 0, 0 };
		final String codes = "lrud";

		int len = moves.length();
		Coordinate coords[] = new Coordinate[1+len];
		coords[0] = new Coordinate(this);
		for(int i=0; i<len; i++)
		{
			int p = codes.indexOf(moves.charAt(i));
			if(p > -1)
			{
				this.row += rOffset[p];
				this.col += cOffset[p];
				coords[i+1] = new Coordinate(this.row, this.col);
			}
		}

		return coords;
	}

	/**
	 * Determines whether or not a given coordinate is adjacent to the coordinate representing the current object
	 * 
	 * @param coord	The coordinate to check
	 * @return		A boolean indicating whether the two coordinates are adjacent linearly (horizontally or vertically)
	 */
	public boolean isAdjacent(Coordinate coord)
	{
		int dr = Math.abs(this.row - coord.row);
		int dc = Math.abs(this.col - coord.col);
		return(dr+dc == 1);
	}
}