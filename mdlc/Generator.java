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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import javax.imageio.ImageIO;
import java.util.ArrayList;

/**
 * This class is responsible for compiling the maze and generating the output
 */
class Generator
{
	/**
	 * This is the main function that will call the appropriate functions to compile
	 * the maze and write the output to a file
	 * 
	 * @param desc			A description object describing the maze
	 * @param outputFile	The path to the output file
	 * @exception Exception	On output/processing error
	 * @return 				Nothing
	 */
	public static void generateMaze(Description desc, String outputFile) throws Exception
	{
		switch(desc.outputMode)
		{
			case JAVA_INT:
			case JAVA_CHAR:
			case JAVA_BOOLEAN:
			case PYTHON_INT:
			case PYTHON_CHAR:
			case PYTHON_BOOLEAN:
				processText(desc, outputFile);
				break;

			case PNG:
			case JPG:
			case TIF:
			case BMP:
				processGraphics(desc, outputFile);
				break;
		}
	}

	/**
	 * This function handles all output modes related to generating text: Java/Python code
	 * 
	 * @param desc			A description object describing the maze
	 * @param outputFile	The path to the output file
	 * @exception Exception	On output/processing error
	 * @return 				Nothing
	 */
	private static void processText(Description desc, String outputFile) throws Exception
	{
		StringBuffer s = new StringBuffer();

		if(desc.outputMode == Mode.JAVA_INT || desc.outputMode == Mode.JAVA_CHAR || desc.outputMode == Mode.JAVA_BOOLEAN)
		{
			s.append("int maze[][] = {\n");
		} else if(desc.outputMode == Mode.PYTHON_INT || desc.outputMode == Mode.PYTHON_CHAR || desc.outputMode == Mode.PYTHON_BOOLEAN) {
			s.append("maze = [\n");
		}

		for(int r=0; r<desc.height; r++)
		{
			if(desc.outputMode == Mode.JAVA_INT || desc.outputMode == Mode.JAVA_CHAR || desc.outputMode == Mode.JAVA_BOOLEAN)
			{
				s.append("\t{ ");
			}  else if(desc.outputMode == Mode.PYTHON_INT || desc.outputMode == Mode.PYTHON_CHAR || desc.outputMode == Mode.PYTHON_BOOLEAN) {
				s.append("\t[ ");
			}

			for(int c=0; c<desc.width; c++)
			{
				if(desc.outputMode == Mode.JAVA_INT || desc.outputMode == Mode.PYTHON_INT)
				{
					if(desc.maze[r][c] == SquareType.PATH)
						s.append(desc.pathInt + ", ");
					else if(desc.maze[r][c] == SquareType.WALL)
						s.append(desc.wallInt + ", ");
					else if(desc.visitStatus[r][c] != null)
						s.append(desc.visitedInt + ", ");
					else if(desc.currentPosition.row == r && desc.currentPosition.col == c)
						s.append(desc.positionInt + ", ");
				} else if(desc.outputMode == Mode.JAVA_BOOLEAN) {
					if(desc.maze[r][c] == SquareType.WALL)
						s.append("false, ");
					else
						s.append("true, ");
				} else if(desc.outputMode == Mode.PYTHON_BOOLEAN) {
					if(desc.maze[r][c] == SquareType.WALL)
						s.append("False, ");
					else
						s.append("True, ");
				} else if(desc.outputMode == Mode.JAVA_CHAR) {
					if(desc.maze[r][c] == SquareType.PATH)
						s.append("'" + desc.pathChar + "', ");
					else if(desc.maze[r][c] == SquareType.WALL)
						s.append("'" + desc.wallChar + "', ");
					else if(desc.visitStatus[r][c] != null)
						s.append("'" + desc.visitedChar + "', ");
					else if(desc.currentPosition.row == r && desc.currentPosition.col == c)
						s.append("'" + desc.positionChar + "', ");
				} else if(desc.outputMode == Mode.PYTHON_CHAR) {
					if(desc.maze[r][c] == SquareType.PATH)
						s.append("\"" + desc.pathChar + "\", ");
					else if(desc.maze[r][c] == SquareType.WALL)
						s.append("\"" + desc.wallChar + "\", ");
					else if(desc.visitStatus[r][c] != null)
						s.append("\"" + desc.visitedChar + "\", ");
					else if(desc.currentPosition.row == r && desc.currentPosition.col == c)
						s.append("\"" + desc.positionChar + "\", ");
				}
			}

			s.deleteCharAt(s.length()-2);
			if(desc.outputMode == Mode.JAVA_INT || desc.outputMode == Mode.JAVA_CHAR || desc.outputMode == Mode.JAVA_BOOLEAN)
			{
				s.append("},\n");
			} else if(desc.outputMode == Mode.PYTHON_INT || desc.outputMode == Mode.PYTHON_CHAR || desc.outputMode == Mode.PYTHON_BOOLEAN) {
				s.append("\t],\n");
			}
		}
		
		s.deleteCharAt(s.length()-2);
		if(desc.outputMode == Mode.JAVA_INT || desc.outputMode == Mode.JAVA_CHAR || desc.outputMode == Mode.JAVA_BOOLEAN)
		{
			s.append("};");
		} else if(desc.outputMode == Mode.PYTHON_INT || desc.outputMode == Mode.PYTHON_CHAR || desc.outputMode == Mode.PYTHON_BOOLEAN) {
			s.append("];");
		}

		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile + ".txt"));
		bw.write(s.toString());
		bw.close();
	}

	/**
	 * This function handles all output modes related to generating images in JPG/TIFF/PNG/BMP format
	 * 
	 * @param desc			A description object describing the maze
	 * @param outputFile	The path to the output image file
	 * @exception Exception	On output/processing error
	 * @return 				Nothing
	 */
	private static void processGraphics(Description desc, String outputFile) throws Exception
	{
		int s = desc.squareSize;
		int w = (desc.width + 2) * s;
		int h = (desc.height + 2) * s;
		
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = image.getGraphics();

		graphics.setColor(Color.WHITE);
		graphics.fillRect(0,0,w,h);

		graphics.setColor(Color.BLACK);
		graphics.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 10));
		
		java.awt.FontMetrics fontMetrics = graphics.getFontMetrics();
		int fontWidth = fontMetrics.charWidth('8');
		int fontDescent = fontMetrics.getDescent();
		int fontLeading = fontMetrics.getLeading();
		int fontHeight = fontMetrics.getHeight() - fontMetrics.getAscent() - fontDescent - fontLeading;

		if(desc.showIndices)
		{
			for(int c=0; c<desc.width; c++) 
			{
				String text = String.valueOf(c);
				int textWidth = fontWidth * text.length();
				int x = ((c+1)*s)+((s-textWidth)/2);
				int y = fontDescent + fontLeading + (s-fontHeight)/2;
				graphics.drawString(text, x, y);
			}
		}

		for(int r=0; r<desc.height; r++)
		{
			int y = (r+1)*s;
			
			if(desc.showIndices)
			{
				String text = String.valueOf(r);
				int tw = fontWidth * text.length();
				int tx = (s - tw)/2;
				int ty = fontDescent + fontLeading + y + ((s-fontHeight)/2);

				graphics.setColor(Color.BLACK);
				graphics.drawString(text, tx, ty);
			}

			for(int c=0; c<desc.width; c++)
			{
				int x = (c+1)*s;

				BufferedImage sprite = null;
				if(desc.maze[r][c] == SquareType.PATH)
					graphics.setColor(desc.pathColor);
				else if(desc.maze[r][c] == SquareType.WALL)
					graphics.setColor(desc.wallColor);
				else if(desc.visitStatus[r][c] != null) {
					graphics.setColor(desc.pathColor);
					sprite = desc.visitedSprite;
				} else if(desc.currentPosition.row == r && desc.currentPosition.col == c) {
					graphics.setColor(desc.pathColor);
					sprite = desc.positionSprite;
				}

				if(sprite == null) {
					graphics.fillRect(x,y,s,s);
				} else {
					Color bgColor = graphics.getColor();
					graphics.drawImage(sprite, x, y, s, s, bgColor, null);
				}
			}
		}

		// draw grid
		if(desc.showGrid)
		{
			graphics.setColor(desc.gridColor);
			for(int r=0; r<desc.height; r++)
			{
				int y = (r+1)*s;
				for(int c=0; c<desc.width; c++)
				{
					int x = (c+1)*s;
					graphics.drawRect(x,y,s,s);
				}
			}
		}

		// draw visited paths
		for(int r=0; r<desc.height; r++)
		{
			int y = (r+1)*s;
			for(int c=0; c<desc.width; c++)
			{
				int x = (c+1)*s;
				if(desc.maze[r][c] == SquareType.PATH && desc.visitStatus[r][c] != null && desc.visitedSprite==null) 
				{
					drawVisitedPath(graphics, desc, r, c, x, y, s);
				}
			}
		}

		if(desc.positionSprite == null && desc.currentPosition != null)
		{
			int y = (desc.currentPosition.row +1)*s;
			int x = (desc.currentPosition.col +1)*s;
			graphics.setColor(desc.positionColor);
			int rad = s/2;
			graphics.fillOval(x+((s-rad)/2), y+((s-rad)/2), rad, rad);
		}

		String format = desc.outputMode.toString().toLowerCase();		
		ImageIO.write(image, format, new java.io.File(outputFile + "." + format));
	}

	/**
	 * This function draws a portion of the visited path for a given cell in the maze
	 * 
	 * @param graphics	A graphics-object reference that will be used to draw the visited path
	 * @param desc		A description object describing the maze
	 * @param r			The row-index of the cell in which to draw
	 * @param c			The column-index of the cell in which to draw
	 * @param x			The pixel x-coordinate of the top-left of the cell
	 * @param y			The pixel y-coordinate of the top-left of the cell
	 * @param s			The size of the square-cell in pixels
	 * @return 			Nothing
	 */
	private static void drawVisitedPath(Graphics graphics, Description desc, int r, int c, int x, int y, int s)
	{
		if(desc.visitStatus[r][c]==null) return;

		ArrayList<VisitDirection> dirList = (ArrayList<VisitDirection>)desc.visitStatus[r][c];
		ArrayList<Color> colorList = (ArrayList<Color>)desc.visitedPathColor[r][c];

		int n = dirList.size();
		for(int i=0; i<n; i++)
		{
			VisitDirection dir = dirList.get(i);
			Color color = colorList.get(i);
			
			graphics.setColor(color);
			if(dir==VisitDirection.HORIZONTAL_LEFT || dir==VisitDirection.HORIZONTAL_RIGHT || dir==VisitDirection.VERTICAL_UP || dir==VisitDirection.VERTICAL_DOWN) {
				drawVisitedPathPortion(graphics, x, y, s, dir);
			} else if(dir==VisitDirection.HORIZONTAL) {
				drawVisitedPathPortion(graphics, x, y, s, VisitDirection.HORIZONTAL_LEFT);
				drawVisitedPathPortion(graphics, x, y, s, VisitDirection.HORIZONTAL_RIGHT);
			} else if(dir==VisitDirection.VERTICAL) {
				drawVisitedPathPortion(graphics, x, y, s, VisitDirection.VERTICAL_UP);
				drawVisitedPathPortion(graphics, x, y, s, VisitDirection.VERTICAL_DOWN);
			} else if(dir==VisitDirection.TOP_LEFT) {
				drawVisitedPathPortion(graphics, x, y, s, VisitDirection.HORIZONTAL_RIGHT);
				drawVisitedPathPortion(graphics, x, y, s, VisitDirection.VERTICAL_DOWN);
			} else if(dir==VisitDirection.TOP_RIGHT) {
				drawVisitedPathPortion(graphics, x, y, s, VisitDirection.HORIZONTAL_LEFT);
				drawVisitedPathPortion(graphics, x, y, s, VisitDirection.VERTICAL_DOWN);
			} else if(dir==VisitDirection.BOTTOM_LEFT) {
				drawVisitedPathPortion(graphics, x, y, s, VisitDirection.HORIZONTAL_RIGHT);
				drawVisitedPathPortion(graphics, x, y, s, VisitDirection.VERTICAL_UP);
			} else if(dir==VisitDirection.BOTTOM_RIGHT) {
				drawVisitedPathPortion(graphics, x, y, s, VisitDirection.HORIZONTAL_LEFT);
				drawVisitedPathPortion(graphics, x, y, s, VisitDirection.VERTICAL_UP);
			}
		}
	}

	/**
	 * Draws half of the line in a given cell representing a visited-path.
	 * Each cell contains two parts of a line which can be combined to get 6 different shapes.
	 * This function draws one such part.
	 * 
	 * @param graphics	A graphics-object reference that will be used to draw the visited path
	 * @param x			The pixel x-coordinate of the top-left of the cell
	 * @param y			The pixel y-coordinate of the top-left of the cell
	 * @param s			The size of the square-cell in pixels
	 * @param dir		The shape of the line
	 * @return 			Nothing	 
	 */
	private static void drawVisitedPathPortion(Graphics graphics, int x, int y, int s, VisitDirection dir)
	{
		int x1 = 0, y1 = 0, w = 0, h = 0;
		int thickness=(int)Math.ceil(s*0.2);
		int half = (int)Math.ceil(s/2);

		if(dir == VisitDirection.HORIZONTAL_LEFT)
		{
			x1 = x;
			y1 = y+(s-thickness)/2;
			w = 3+half;
			h = thickness;
		} else if(dir == VisitDirection.HORIZONTAL_RIGHT) {
			x1 = x + half;
			y1 = y+(s-thickness)/2;
			w = 3+half;
			h = thickness;
		} else if(dir == VisitDirection.VERTICAL_UP) {
			x1 = x + half;
			y1 = y;
			w = thickness;
			h = 3+half;
		} else if(dir == VisitDirection.VERTICAL_DOWN) {
			x1 = x + half;
			y1 = y + half;
			w = thickness;
			h = 3+half;
		} else {
			return;
		}

		graphics.fillRect(x1,y1,w,h);
	}
}