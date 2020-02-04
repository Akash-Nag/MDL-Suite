package testmdlsuite;

import java.util.HashSet;
import java.util.Stack;
import org.junit.Test;
import static org.junit.Assert.*;
import mdlg.RandomMazeGenerator;

public class TestMDLG
{
    private class Position
    {
		int row;
		int col;
		
		Position(int r, int c)
		{
			this.row = r;
			this.col = c;
		}

		boolean isInBounds(int width, int height)
		{
			return(row >=0 && row < height && col >=0 && col < width);
		}
		
		@Override
		public int hashCode()
		{
			int hash = 5;
			hash = 73 * hash + this.row;
			hash = 73 * hash + this.col;
			return hash;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
			{
			return true;
			}
			if (obj == null)
			{
			return false;
			}
			if (getClass() != obj.getClass())
			{
			return false;
			}
			final Position other = (Position) obj;
			if (this.row != other.row)
			{
			return false;
			}
			if (this.col != other.col)
			{
			return false;
			}
			return true;
		}	
    }
    
    @Test
    public void IfParametersAreZero()
    {
		int width = 0, height = 0;
		boolean m[][] = RandomMazeGenerator.generateMaze(width, height);
		assertEquals(null, m);	    // because width or height must be atleast 2, else null is returned
    }
    
    @Test
    public void IfMazeDimensionsAreInvalid()
    {
		int width = 1, height = 100;
		boolean m[][] = RandomMazeGenerator.generateMaze(width, height);
		assertEquals(null, m);	    // because width or height must be atleast 2, else null is returned
    }
    
    @Test
    public void ThinMazeSolvability()
    {
		int width = 3, height = 100;
		boolean m[][] = RandomMazeGenerator.generateMaze(width, height);
		int entrance = findEntrance(m, width, height);
		int exit = findExit(m, width, height);
		assertEquals((byte)1, (isSolvable(m, width, height, entrance, exit) ? (byte)1 : (byte)0));
    }
    
    @Test
    public void IfMazesAreSolvable()
    {
		int height = 20, width = 20, n = 10000;
		byte s[] = new byte[n];
		byte a[] = new byte[n];
		
		for(int i=0; i<n; i++)
		{
			boolean m[][] = RandomMazeGenerator.generateMaze(width, height);
			int entrance = findEntrance(m, width, height);
			int exit = findExit(m, width, height);
			s[i] = (isSolvable(m, width, height, entrance, exit) ? (byte)1 : (byte)0);
			a[i] = (byte)1;	// always true, so that assertArrayEquals may be used
		}
		
		assertArrayEquals(a, s);
    }
    
    /**
     * To find the column for the maze entrance
     * @param maze	The generated maze
     * @param width	The width of the generated maze
     * @param height	The height of the generated maze
     * @return		The column (0-based) in the first (0) where the entrance lies
     */
    private int findEntrance(boolean maze[][], int width, int height)
    {
		for(int i=1; i<width-1; i++)
		{
			if(maze[0][i]) return i;
		}
		return -1;
    }
    
    /**
     * To find the column for the maze exit
     * @param maze	The generated maze
     * @param width	The width of the generated maze
     * @param height	The height of the generated maze
     * @return		The column (0-based) in the last row (height-1) where the exit lies
     */
    private int findExit(boolean maze[][], int width, int height)
    {
		for(int i=1; i<width-1; i++)
		{
			if(maze[height-1][i]) return i;
		}
		return -1;
    }
    
    /**
     * To find the column for the maze exit
     * @param maze	The generated maze
     * @param width	The width of the generated maze
     * @param height	The height of the generated maze
     * @param entrance	The column where the entrance lies
     * @param exit	The column where the exit lies
     * @return		True if there is a path from the entrance to the exit, false otherwise
     */
    private boolean isSolvable(boolean maze[][], int width, int height, int entrance, int exit)
    {
		Stack<Position> stack = new Stack<Position>();
		HashSet<Position> visited = new HashSet<Position>();
		
		Position destPos = new Position(height-1, exit);	// determine exit coordinate
		stack.push(new Position(0, entrance));			// push entrance onto stack
		
		while(!stack.isEmpty())
		{
			Position current = stack.pop();
			visited.add(current);				// mark current position as visited
			
			if(current.equals(destPos)) return true;		// if exit found, return
			
			// enumerate the 4 neighbours
			Position neighbours[] = {
			new Position(current.row - 1, current.col),
			new Position(current.row + 1, current.col),
			new Position(current.row, current.col - 1),
			new Position(current.row, current.col + 1)
			};
			
			// push all valid & unvisited neighbours onto stack
			for(int i=0; i<4; i++)
			{
			if(neighbours[i].isInBounds(width, height) && !visited.contains(neighbours[i]))
			{
				stack.push(neighbours[i]);
			}
			}
		}
		
		return false;
    }
}
