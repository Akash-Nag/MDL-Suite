package mdlc;

enum SquareType
{
	PATH, WALL
}

enum VisitDirection
{
	HORIZONTAL_LEFT, HORIZONTAL_RIGHT, VERTICAL_UP, VERTICAL_DOWN, 		// these are for half-lines at the beginning and ending of paths
	HORIZONTAL, VERTICAL,
	TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
}

enum Direction
{
	LEFT, UP, RIGHT, DOWN
}