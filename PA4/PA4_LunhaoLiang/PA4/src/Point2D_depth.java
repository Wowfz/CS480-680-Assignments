//****************************************************************************
//       2D Point Class from PA1
//****************************************************************************
// History :
//   Nov 6, 2014 Created by Stan Sclaroff
//	Modify on Dec 9, 2018 by Lunhao Liang
//

public class Point2D_depth
{
	public int x, y,z;
	public float u, v; // uv coordinates for texture mapping
	public Vector3D n;
	public ColorType c;
	public Point2D_depth(int _x, int _y, int _z, ColorType _c)
	{
		u = 0;
		v = 0;
		x = _x;
		y = _y;
		z = _z;
		c = _c;
	}

	public Point2D_depth(int _x, int _y, int _z, Vector3D _n)
	{
		u = 0;
		v = 0;
		x = _x;
		y = _y;
		z = _z;
		n = _n;
		c = new ColorType(0,0,0);
	}

	public Point2D_depth(int _x, int _y, int _z, ColorType _c, float _u, float _v)
	{
		u = _u;
		v = _v;
		x = _x;
		y = _y;
		z = _z;
		c = _c;
	}
	public Point2D_depth()
	{
		c = new ColorType(1.0f, 1.0f, 1.0f);
	}
	public Point2D_depth(Point2D_depth p)
	{
		u = p.u;
		v = p.v;
		x = p.x;
		y = p.y;
		z = p.z;
		c = new ColorType(p.c.r, p.c.g, p.c.b);
	}
}