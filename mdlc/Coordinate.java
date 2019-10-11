package mdlc;

class Coordinate
{
	protected int row;
	protected int col;

	public Coordinate(int r, int c)
	{
		this.row = r;
		this.col = c;
	}

	public Coordinate(Coordinate c)
	{
		this.row = c.row;
		this.col = c.col;
	}

	@Override
	public String toString()
	{
		return "(" + this.row + ", " + this.col + ")";
	}

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

	public boolean isAdjacent(Coordinate coord)
	{
		int dr = Math.abs(this.row - coord.row);
		int dc = Math.abs(this.col - coord.col);
		return(dr+dc == 1);
	}
}