//Chanan Suksangium Point3D to handle z-buffer

public class Point3D
{
	public int x, y, z;
	public ColorType c;
	public Vector3D n;
	public Point3D(int _x, int _y, int _z, ColorType _c)
	{
		x = _x;
		y = _y;
		z = _z;
		c = _c;
	}
	public Point3D()
	{
		c = new ColorType(1.0f, 1.0f, 1.0f);
	}
	public Point3D( Point3D p)
	{
		x = p.x;
		y = p.y;
		z = p.z;
		c = new ColorType(p.c.r, p.c.g, p.c.b);
	}
}