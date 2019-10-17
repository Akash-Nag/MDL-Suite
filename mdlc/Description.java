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

import java.io.BufferedReader;
import java.io.FileReader;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * This class encapsulates the maze description, read from the MDL file
 */
class Description
{
	/**
	 * Defines the output mode of the compiler: to generate code or image
	 */
	protected Mode outputMode;

	/**
	 * Defines whether a thin grid will be visible to separate individual cells in the maze
	 */
	protected boolean showGrid;
	
	/**
	 * Defines whether the coordinates of the cells will be shown as row and column headers
	 */
	protected boolean showIndices;

	/**
	 * Defines the character that will be used as a place-holder in Java/Python code
	 * for representing vacant cells
	 */
	protected char pathChar;
	
	/**
	 * Defines the character that will be used as a place-holder in Java/Python code
	 * for representing blocked cells
	 */
	protected char wallChar;

	/**
	 * Defines the character that will be used as a place-holder in Java/Python code
	 * for representing the current-cell (in case of a solver)
	 */
	protected char positionChar;

	/**
	 * Defines the character that will be used as a place-holder in Java/Python code
	 * for representing all cells which have been visited (in case of a solver)
	 */
	protected char visitedChar;

	/**
	 * Defines the integer that will be used as a place-holder in Java/Python code
	 * for representing vacant cells
	 */
	protected int pathInt;

	/**
	 * Defines the integer that will be used as a place-holder in Java/Python code
	 * for representing blocked cells
	 */
	protected int wallInt;

	/**
	 * Defines the integer that will be used as a place-holder in Java/Python code
	 * for representing the current cell (in case of a solver)
	 */
	protected int positionInt;

	/**
	 * Defines the integer that will be used as a place-holder in Java/Python code
	 * for representing all cells that have been visited (by a solver)
	 */
	protected int visitedInt;

	/**
	 * Defines the color of vacant cells
	 */
	protected Color pathColor;

	/**
	 * Defines the color of the blocked cells
	 */
	protected Color wallColor;

	/**
	 * Defines the color of the grid (if visible)
	 */
	protected Color gridColor;

	/**
	 * Defines the color of the filled-circle drawn to indicate the current position in absence of a sprite-image
	 */
	protected Color positionColor;

	/**
	 * Defines the sprite image that will be drawn to indicate the current position (of a solver)
	 */
	protected BufferedImage positionSprite;

	/**
	 * Defines the sprite image that will be drawn to indicate the positions that have been visited (by a solver)
	 */
	protected BufferedImage visitedSprite;

	/**
	 * Defines the width of the maze (in number of cells)
	 */
	protected int width;

	/**
	 * Defines the height of the maze (in number of cells)
	 */
	protected int height;

	/**
	 * Defines the coordinate of the entrance to the maze
	 */
	protected Coordinate entrance;

	/**
	 * Defines the coordinates of the exit of the maze
	 */
	protected Coordinate exit;

	/**
	 * Defines the coordinates of the current position (of a solver)
	 */
	protected Coordinate currentPosition;

	/**
	 * Defines the size of each cell (in pixels)
	 */
	protected int squareSize;

	/**
	 * Stores the actual maze
	 */
	protected SquareType maze[][];

	/**
	 * Stores the status of each cell as 
	 * an ArrayList of directions representing the visited-path line shape
	 */
	protected Object visitStatus[][];

	/**
	 * Stores the status of each cell as an ArrayList of colors 
	 * representing the visited path in case of multiple visited 
	 * paths of different colors
	 */
	protected Object visitedPathColor[][];

	/**
	 * Stores the strings representing the specified paths indexed by a path ID
	 */
	private static HashMap<Integer, String> allLines;

	/**
	 * Stores the initial coordinates of each path indexed by a path ID
	 */
	private static HashMap<Integer, Coordinate> lineCoords;

	/**
	 * Default constructor to initialize all maze settings to their default values
	 */
	public Description()
	{
		this.pathColor = Color.WHITE;
		this.wallColor = Color.BLACK;
		this.gridColor = Color.WHITE;
		this.positionColor = Color.RED;

		this.showGrid = true;
		this.showIndices = true;

		this.maze = null;
		this.visitStatus = null;
		this.visitedPathColor = null;
		
		this.entrance = null;
		this.exit = null;
		this.currentPosition = null;

		this.squareSize = 25;
		this.positionSprite = null;
		this.visitedSprite = null;

		this.pathChar = ' ';
		this.wallChar = '#';
		this.visitedChar = '~';
		this.positionChar = '*';

		this.pathInt = 1;
		this.wallInt = 0;
		this.visitedInt = 2;
		this.positionInt = 3;

		this.outputMode = Mode.PNG;
	}

	/**
	 * Reads a maze-description from a MDL file
	 * 
	 * @param filename		The MDL-file to read from
	 * @return 				A description-object describing the maze
	 * @exception Exception	On input error or on encountering invalid settings
	 */
	protected static Description readFile(String filename) throws Exception
	{
		allLines = new HashMap<Integer, String>();
		lineCoords = new HashMap<Integer, Coordinate>();

		Description desc = new Description();
		BufferedReader br = new BufferedReader(new FileReader(filename));

		String line = null;
		int lc = 1;

		line = br.readLine();
		if(!line.startsWith("[version:")) 
		{
			br.close();
			throw(new Exception("Line " + lc + ": expected version information"));
		} else {
			String ver = extract(line.trim(), "[version:", "]").trim();
			if(!ver.equalsIgnoreCase(MDLC.VERSION))
			{
				br.close();
				throw(new Exception("Line " + lc + ": cannot read files of version '" + ver + "', expected: '" + MDLC.VERSION + "'"));
			}
		}

		while((line = br.readLine()) != null)
		{
			lc++;
			line = line.trim().replace("\n","").replace(" ","").replace("\r","").toLowerCase();
			if(line.length()==0) continue;

			if(line.startsWith("["))							// settings definition
			{
				if(!processSettings(desc, line))
				{
					br.close();
					throw(new Exception("Line " + lc + ": invalid syntax"));
				}
			} else if(line.startsWith("p") && Character.isDigit(line.charAt(1))) {		// path definition
				if(desc.maze == null) {
					br.close();
					throw(new Exception("Line " + lc + ": maze size must be defined prior to defining paths"));
				} else {
					if(!processPath(desc, line.substring(1)))
					{
						br.close();
						throw(new Exception("Line " + lc + ": invalid syntax"));
					}
				}
			} else if(line.startsWith("r") && Character.isDigit(line.charAt(1))) {		// row definition
				if(desc.maze == null) {
					br.close();
					throw(new Exception("Line " + lc + ": maze size must be defined prior to defining rows"));
				} else {
					if(!processRow(desc, line.substring(1)))
					{
						br.close();
						throw(new Exception("Line " + lc + ": invalid syntax"));
					}
				}
			} else if(line.startsWith("v:") || line.startsWith("vc:")) {
				// mark path as visited
				if(desc.maze == null) {
					br.close();
					throw(new Exception("Line " + lc + ": maze size must be defined prior to defining visited paths"));
				} else {
					if(line.startsWith("v:"))
					{
						if(!processVisitedPath(desc, line))
						{
							br.close();
							throw(new Exception("Line " + lc + ": invalid syntax"));
						}
					} else {
						if(!processVisitedPathInCoordinates(desc, line))
						{
							br.close();
							throw(new Exception("Line " + lc + ": invalid syntax"));
						}
					}
				}
			} else {
				br.close();
				throw(new Exception("Line " + lc + ": unexpected statement"));
			}
		}

		br.close();

		if(desc.maze != null)
		{
			if(desc.entrance!=null) desc.maze[desc.entrance.row][desc.entrance.col] = SquareType.PATH;
			if(desc.exit!=null) desc.maze[desc.exit.row][desc.exit.col] = SquareType.PATH;
		}

		return desc;
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
	 * Processes configuration settings for the maze
	 * 
	 * @param desc	A description-object to store the settings into
	 * @param line	The setting read from file
	 * @return 		A boolean value indicating whether or not the setting was a valid setting with proper syntax
	 */
	private static boolean processSettings(Description desc, String line)
	{
		// Syntax:		[setting:value]
		if(!line.startsWith("[")) return false;
		if(!line.endsWith("]")) return false;
		
		int p = line.indexOf(":");
		if(p < 0) return false;

		String key = line.substring(1,p).trim();
		String value = line.substring(p+1,line.length()-1).trim();
		int x[] = null;
		String a[] = null;

		switch(key)
		{
			case "size":
				x = paramsAsInt(value, ",");
				desc.height = x[0];
				desc.width = x[1];

				desc.maze = new SquareType[desc.height][desc.width];
				desc.visitStatus = new Object[desc.height][desc.width];
				desc.visitedPathColor = new Object[desc.height][desc.width];
				
				for(int r=0; r<desc.height; r++)
				{
					for(int c=0; c<desc.width; c++)
					{
						desc.maze[r][c] = SquareType.WALL;
						desc.visitStatus[r][c] = null;
						desc.visitedPathColor[r][c] = null;
					}
				}
				break;

			case "output-mode":
				desc.outputMode = Mode.valueOf(value.toUpperCase());
				break;

			case "unit":
				desc.squareSize = Integer.parseInt(value);
				break;

			case "entrance":
				desc.entrance = paramAsCoordinate(value);
				break;

			case "exit":
				desc.exit = paramAsCoordinate(value);
				break;

			case "current-position":
				desc.currentPosition = paramAsCoordinate(value);
				break;

			case "placeholder-char":
				value = value.trim().replace("\"","");
				desc.pathChar = value.charAt(0);
				desc.wallChar = value.charAt(1);
				if(value.length() > 2)
				{
					desc.visitedChar = value.charAt(2);
					desc.positionChar = value.charAt(3);
				}
				break;

			case "placeholder-int":
				x = paramsAsInt(value, ",");
				desc.pathInt = x[0];
				desc.wallInt = x[1];
				if(x.length > 2)
				{
					desc.visitedInt = x[2];
					desc.positionInt = x[3];
				}
				break;

			case "path-color":
				a = new String[] { value };
				desc.pathColor = processColors(a)[0];
				break;

			case "wall-color":
				a = new String[] { value };
				desc.wallColor = processColors(a)[0];
				break;

			case "position-color":
				a = new String[] { value };
				desc.positionColor = processColors(a)[0];
				break;

			case "grid-color":
				a = new String[] { value };
				desc.gridColor = processColors(a)[0];
				break;

			case "visited-sprite":
				desc.visitedSprite = loadImage(value);
				break;
			
			case "position-sprite":
				desc.positionSprite = loadImage(value);
				break;
			
			case "show-grid":
				desc.showGrid = Boolean.parseBoolean(value);
				break;

			case "show-indices":
				desc.showIndices = Boolean.parseBoolean(value);
				break;

			default:
				return false;
		}

		return true;
	}

	/**
	 * Reads an image from a file
	 * 
	 * @param path	Path to the image file to be read
	 * @return 		A BufferedImage object reference to the image
	 */
	private static BufferedImage loadImage(String path)
	{
		if(!path.startsWith("\"") || !path.endsWith("\"")) return null;
		path = path.substring(1, path.length()-1).trim();
		try {
			return javax.imageio.ImageIO.read(new java.io.File(path));
		} catch(Exception e) {
			System.out.println("Error reading image: " + path);
			return null;
		}
	}

	/**
	 * Processes multiple colors in RGB-string-format and translates them to Color objects
	 * 
	 * @param colors	An array of strings containing color-strings in the format rgb(red:green:blue)
	 * @return 			A Color array of objects representing the colors
	 * @see Color
	 */
	private static Color[] processColors(String colors[])
	{
		Color c[] = new Color[colors.length];
		for(int i=0; i<colors.length; i++)
		{
			String x = colors[i].trim();
			if(!x.startsWith("rgb(") || !x.endsWith(")")) continue;
			int rgb[] = paramsAsInt(extract(x, "rgb(", ")"), ":");
			c[i] = new Color(rgb[0], rgb[1], rgb[2]);
		}
		return c;
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

	/**
	 * An utility function translating a string to a coordinate object
	 * 
	 * @param s			The given string in the format c(row,col)
	 * @return 			A Coordinate object representing the coordinate
	 * @see Coordinate
	 */
	private static Coordinate paramAsCoordinate(String s)
	{
		int x[] = paramsAsInt(extract(s,"c(",")"), ",");
		return(new Coordinate(x[0], x[1]));
	}

	/**
	 * Translates a visited-path description as a path-string to actual coordinates and stores them into a description object
	 * 
	 * @param desc	A description object to store the path information into
	 * @param line	A path description
	 * @return 		A boolean indicating whether the path was successfully processed
	 */
	private static boolean processVisitedPath(Description desc, String line) throws Exception
	{
		// Syntax: v:p(line,colStart,colEnd,color);
		if(!line.endsWith(");")) return false;
		if(!line.startsWith("v:p(")) return false;
		
		String indices[] = extract(line, "v:p(", ");").trim().split(",");

		int lineIndex = Integer.parseInt(indices[0].trim());
		int colStart = Integer.parseInt(indices[1].trim());
		int colEnd = Integer.parseInt(indices[2].trim());
		Color visitedColor = (indices.length > 3 ? processColors(new String[] { indices[3].trim() })[0] : Color.LIGHT_GRAY);
		
		if(!lineCoords.containsKey(lineIndex)) return false;
		if(colStart < 0 || colStart > colEnd) return false;

		Coordinate start = lineCoords.get(lineIndex);

		String lineMoves = allLines.get(lineIndex);
		if(colEnd > lineMoves.length()) return false;
		lineMoves = lineMoves.substring(0, colEnd);
		
		if(colStart > 1) start.moveTo(lineMoves.substring(0,colStart-1));
		Coordinate coords[] = start.moveTo(lineMoves.substring(colStart > 0 ? colStart-1 : 0));
		
		for(int i=0; i<coords.length; i++)
		{
			int r = coords[i].row, c = coords[i].col;
			
			Object obj1 = desc.visitStatus[r][c];
			ArrayList<VisitDirection> list1 = (obj1 == null ? new ArrayList<VisitDirection>() : (ArrayList<VisitDirection>)obj1);
			list1.add(getVisitDirection(i, coords));
			desc.visitStatus[r][c] = list1;

			Object obj2 = desc.visitedPathColor[r][c];
			ArrayList<Color> list2 = (obj2 == null ? new ArrayList<Color>() : (ArrayList<Color>)obj2);
			list2.add(visitedColor);
			desc.visitedPathColor[r][c] = list2;
		}

		return true;
	}
	
	/** 
	 * Translates a visited-path description as a coordinate-string to actual coordinates and stores them into a description object
	 * 
	 * @param desc			A description object to store the path information into
	 * @param line			A path description
	 * @exception Exception On processing error
	 * @return 				A boolean indicating whether the path was successfully processed 
	 */
	private static boolean processVisitedPathInCoordinates(Description desc, String line) throws Exception
	{
		// Syntax: vc:color,c(row,col) > c(row,col) > .... > c(row,col);
		if(!line.endsWith(");")) return false;
		if(!line.startsWith("vc:rgb(")) return false;
		
		String indices[] = extract(line, "vc:", ";").trim().split(">");
		
		int p = indices[0].indexOf(",");
		String colorString = indices[0].substring(0,p).trim();
		indices[0] = indices[0].substring(p+1);
		Color visitedColor = processColors(new String[] { colorString })[0];
		
		Coordinate coords[] = new Coordinate[indices.length];
		for(int i=0; i<indices.length; i++)
		{
			String cx[] = extract(indices[i].trim(), "c(", ")").trim().split(",");
			int r = Integer.parseInt(cx[0]), c = Integer.parseInt(cx[1]);
			coords[i] = new Coordinate(r,c);

			if(i > 0 && !coords[i].isAdjacent(coords[i-1])) return false;
		}

		for(int i=0; i<coords.length; i++)
		{
			int r = coords[i].row, c = coords[i].col;

			Object obj1 = desc.visitStatus[r][c];
			ArrayList<VisitDirection> list1 = (obj1 == null ? new ArrayList<VisitDirection>() : (ArrayList<VisitDirection>)obj1);
			list1.add(getVisitDirection(i, coords));
			desc.visitStatus[r][c] = list1;

			Object obj2 = desc.visitedPathColor[r][c];
			ArrayList<Color> list2 = (obj2 == null ? new ArrayList<Color>() : (ArrayList<Color>)obj2);
			list2.add(visitedColor);
			desc.visitedPathColor[r][c] = list2;
		}

		return true;
	}

	/** 
	 * Translates a visited-path description as a movement-string to actual coordinates and stores them into a description object
	 * 
	 * @param desc			A description object to store the path information into
	 * @param line			A path description
	 * @exception Exception On processing error
	 * @return 				A boolean indicating whether the path was successfully processed 
	 */
	private static boolean processPath(Description desc, String line) throws Exception
	{
		int pos = line.indexOf(":");
		if(pos < 0) return false;
		int pathNumber = Integer.parseInt(line.substring(0,pos).trim());
		if(lineCoords.containsKey(pathNumber)) return false;

		char x = line.charAt(pos+1);
		Coordinate start = null;
		int k = -1, mode = 0;

		if(x=='e')
		{
			if(desc.entrance == null) return false;
			lineCoords.put(pathNumber, desc.entrance);
			start = new Coordinate(desc.entrance);
			mode = 1;
		} else if(x == 'x') {
			if(desc.exit == null) return false;
			lineCoords.put(pathNumber, desc.exit);
			start = new Coordinate(desc.exit);
			mode = 2;
		} else if(x == 'p') {
			// Syntax:	p(line,pos)
			k = line.indexOf(")");
			if(k<0 || line.charAt(pos+2) != '(') return false;

			mode = 3;
			String coords[] = line.substring(pos+3, k).split(",");
			int lineIndex = Integer.parseInt(coords[0]);
			int colIndex = Integer.parseInt(coords[1]);

			if(lineIndex==pathNumber || !lineCoords.containsKey(lineIndex)) return false;		// self/forward reference not allowed
			start = determineCoordinateFromLine(lineIndex, colIndex);
			lineCoords.put(pathNumber, start);
		} else if(x == 'c') {
			// Syntax:	c(row,col)
			k = line.indexOf(")");
			if(k<0 || line.charAt(pos+2) != '(') return false;

			mode = 4;
			String coords[] = line.substring(pos+3, k).split(",");
			int rowIndex = Integer.parseInt(coords[0]);
			int colIndex = Integer.parseInt(coords[1]);

			start = new Coordinate(rowIndex, colIndex);
			lineCoords.put(pathNumber, start);
		}

		if(mode<=2)
			line = line.substring(pos+2).trim();
		else if(mode==3 || mode==4)
			line = line.substring(k+1).trim();
		
		if(!line.endsWith(";")) return false;

		String lineMoves = enumerateMoves(line);
		if(lineMoves == null) return false;
		allLines.put(pathNumber, lineMoves);
		
		Coordinate coords[] = start.moveTo(lineMoves);
		for(int i=0; i<coords.length; i++)
		{
			desc.maze[coords[i].row][coords[i].col] = SquareType.PATH;
		}
		
		return true;
	}

	/** 
	 * Translates a maze description in row-format to actual coordinates and stores them into a description object
	 * 
	 * @param desc			A description object to store the maze information into
	 * @param line			A string describing one row of the maze
	 * @exception Exception On processing error
	 * @return 				A boolean indicating whether the row was successfully processed 
	 */
	private static boolean processRow(Description desc, String line) throws Exception
	{
		int pos = line.indexOf(":");
		if(pos < 0) return false;
		int rowIndex = Integer.parseInt(line.substring(0,pos).trim());
		if(rowIndex < 0 || rowIndex >= desc.height) return false;

		String data = line.substring(pos+1).trim().toLowerCase();
		if(!data.endsWith(";")) return false;
		int len = data.length()-1;
		for(int i=0; i<len; i++)
		{
			char x = data.charAt(i);
			if(x=='p')
				desc.maze[rowIndex][i] = SquareType.PATH;
			else if(x == 'w')
				desc.maze[rowIndex][i] = SquareType.WALL;
		}
		
		return true;
	}

	/**
	 * Determines the coordinate on a position in a given path
	 * 
	 * @param lineIndex		The path ID
	 * @param colIndex		The position-index on that path
	 * @return Coordinate	The coordinate of that position on that path
	 */
	private static Coordinate determineCoordinateFromLine(int lineIndex, int colIndex)
	{
		Coordinate start = new Coordinate(lineCoords.get(lineIndex));
		String lineMoves = allLines.get(lineIndex);
		start.moveTo(lineMoves.substring(0,colIndex));
		return start;
	}

	/**
	 * Expands a condensed movement string
	 * 
	 * @param line	A string containing a movement string in condensed form
	 * @return		The expanded movement string
	 */
	private static String enumerateMoves(String line)
	{
		// this function expands a coded path into its full form
		// e.g. L(3)U(2)RRU(3) ==> LLLUURRUUU

		line = line.trim();
		StringBuffer moves = new StringBuffer();
		int len = line.length()-1;		// bcoz it is terminated by a semicolon
		
		for(int i=0; i<len; i++)
		{
			char x = line.charAt(i);
			char y = line.charAt(i+1);
			
			if(x==' ') continue;
			if("udlr".indexOf(x) > -1)
			{
				if(y == '(')
				{
					int p = line.indexOf(")", i+1);
					if(p < 0) return null;
					int repeat = Integer.parseInt(line.substring(i+2,p));
					for(int j=0; j<repeat; j++) moves.append(x);
					i = p;		// to skip to next code
				} else if(y!=';' && "udlr".indexOf(y) == -1) {
					return null;
				} else {
					moves.append(x);
				}
			} else {
				return null;
			}
		}

		String s = validateMoveString(moves.toString());
		return s;
	}

	/**
	 * Checks if a movement string is valid, i.e. there is no re-traversal over the same path in the opposite direction
	 * 
	 * @param s	The movement string
	 * @return	null if the movement string is invalid, or the movement string itself unchanged if it is valid
	 */
	private static String validateMoveString(String s)
	{
		// check if direction is reversed: left-right, or up-down pairs
		String x = s.toLowerCase(), a = "lrud";
		int b[] = { -1, 1, -2, 2 };
		int n = x.length();
		for(int i=0; i<n-1; i++)
		{
			char c1 = x.charAt(i);
			char c2 = x.charAt(i+1);
			int sum = b[a.indexOf(c1)] + b[a.indexOf(c2)];
			if(sum==0) return null;
		}		
		return s;
	}

	/**
	 * Determines the direction of a visited-path at a particular point on the path
	 * 
	 * @param index		A position-index on a visited path
	 * @param coords	A list of coordinates representing the visited path
	 * @return 			The direction to face at the given position on the path
	 */
	private static VisitDirection getVisitDirection(int index, Coordinate coords[])
	{
		Coordinate prev = null, next = null, current = coords[index];
		Direction dirNext = null, dirPrev = null;

		if(index==0 || index==coords.length-1)
		{
			if(index==0)
			{
				next = coords[index+1];
				dirNext = getDirectionRelativeTo(current, next);
			} else {
				prev = coords[index-1];
				dirNext = getDirectionRelativeTo(current, prev);			
			}

			if(dirNext==Direction.LEFT)
				return VisitDirection.HORIZONTAL_LEFT;
			else if(dirNext==Direction.RIGHT)
				return VisitDirection.HORIZONTAL_RIGHT;
			else if(dirNext==Direction.UP)
				return VisitDirection.VERTICAL_UP;
			else if(dirNext==Direction.DOWN)
				return VisitDirection.VERTICAL_DOWN;
			else
				return null;
		} else {
			prev = coords[index-1];
			next = coords[index+1];
			dirPrev = getDirectionRelativeTo(prev, current);
			dirNext = getDirectionRelativeTo(current, next);

			int pos = (dirPrev.ordinal() * 4) + dirNext.ordinal();
			VisitDirection dirs[] = {
				// left-left, left-up, left-right, left-down:
				VisitDirection.HORIZONTAL, VisitDirection.BOTTOM_LEFT, null, VisitDirection.TOP_LEFT,

				// up-left, up-up, up-right, up-down:
				VisitDirection.TOP_RIGHT, VisitDirection.VERTICAL, VisitDirection.TOP_LEFT, null,

				// right-left, right-up, right-right, right-down:
				null, VisitDirection.BOTTOM_RIGHT, VisitDirection.HORIZONTAL, VisitDirection.TOP_RIGHT,

				// down-left, down-up, down-right, down-down:
				VisitDirection.BOTTOM_RIGHT, null, VisitDirection.BOTTOM_LEFT, VisitDirection.VERTICAL
			};

			return dirs[pos];
		}
	}

	/**
	 * Determines the direction of a position with respect to another position
	 * 
	 * @param from	The first position
	 * @param to	The second position
	 * @return		The direction of the second position with respect to the first position
	 */
	private static Direction getDirectionRelativeTo(Coordinate from, Coordinate to)
	{
		// returns the direction of 'to' relative to 'from'
		if(to.row == from.row)
		{
			if(to.col > from.col)
				return Direction.RIGHT;
			else if(to.col < from.col)
				return Direction.LEFT;
			else
				return null;
		} else if(to.col == from.col) {
			if(to.row > from.row)
				return Direction.DOWN;
			else if(to.row < from.row)
				return Direction.UP;
			else
				return null;
		}

		return null;
	}
}