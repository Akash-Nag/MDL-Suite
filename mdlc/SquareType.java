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
 * Defines the type of a cell in a maze
 */
enum SquareType
{
	PATH, WALL
}

/**
 * Defines the shape of the line drawn to indicate the visited-path
 */
enum VisitDirection
{
	HORIZONTAL_LEFT, HORIZONTAL_RIGHT, VERTICAL_UP, VERTICAL_DOWN, 		// these are for half-lines at the beginning and ending of paths
	HORIZONTAL, VERTICAL,
	TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
}

/**
 * Defines the direction of movement for paths defined as direction-strings
 */
enum Direction
{
	LEFT, UP, RIGHT, DOWN
}