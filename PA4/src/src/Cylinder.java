//****************************************************************************
//      Cylinder class
//****************************************************************************
// History :
//   December 2016 Chanan Suksangium
//

public class Cylinder
{
	private Vector3D center;
	private float r, rx, ry, umax, umin;
	private int m,n;
	public Mesh3D mesh;
	
	private final float vmin = (float)-Math.PI;
	private final float vmax = (float)Math.PI;
	
	public Cylinder(float _x, float _y, float _z, float _rx, float _ry, int _m, int _n, float _r)
	{
		center = new Vector3D(_x,_y,_z);
		rx = _rx;
		ry = _ry;
		m = _m;
		n = _n;
		r = _r;
		umax = r;
		umin = -r;
		initMesh();
	}
	
	public void set_center(float _x, float _y, float _z)
	{
		center.x=_x;
		center.y=_y;
		center.z=_z;
		fillMesh();  // update the triangle mesh
	}
	
	public Vector3D get_center()
	{
		return center;
	}
	
	public void setRadiusX(float _rx) {
		rx = _rx;
		fillMesh();
	}
	
	public void setRadiusY(float _ry) {
		ry = _ry;
		fillMesh();
	}
	
	
	public float getRadiusX() {
		return rx;
	}
	
	public float getRadiusY() {
		return ry;
	}

	public void set_m(int _m)
	{
		m = _m;
		initMesh(); // resized the mesh, must re-initialize
	}
	
	public void set_n(int _n)
	{
		n = _n;
		initMesh(); // resized the mesh, must re-initialize
	}
	
	public int get_n()
	{
		return n;
	}
	
	public int get_m()
	{
		return m;
	}
	
	private void initMesh()
	{
		mesh = new Mesh3D(m,n);
		fillMesh();  // set the mesh vertices and normals
	}
		
	// fill the triangle mesh vertices and normals
	// using the current parameters for the sphere
	private void fillMesh(){
	int i, j;
	float theta, phi;
	float d_phi = (umax-umin)/((float)n-1);
	float d_theta = (vmax-vmin)/((float)m-1);
	
	float c_theta, s_theta, c_phi, s_phi;
	
	Vector3D du = new Vector3D();
	Vector3D dv = new Vector3D();
	
	for (i = 0, theta = vmin; i < m; ++i, theta += d_theta){
		c_theta = (float)Math.cos(theta);
		s_theta = (float)Math.sin(theta);
		
		for (j = 0, phi = umin; j < n; ++j, phi += d_phi) {
			c_phi = (float)Math.cos(phi);
			s_phi = (float)Math.sin(phi);
			
			//vertex
			mesh.v[i][j].x = center.x + rx * c_theta;
			mesh.v[i][j].y = center.y + ry * s_theta;
			mesh.v[i][j].z = center.z + phi;
			
			du.x = -rx * s_theta;
			du.y = ry * c_theta;
			du.z = 0;
			dv.x = 0;
			dv.y = 0;
			dv.z = 1;
			
			du.crossProduct(dv, mesh.n[i][j]);
			mesh.n[i][j].normalize();
		}
	}
	
	// Endcaps *** Need to create normal for this to get the specular 
	for (i = 0, theta = vmin; i < m; ++i, theta += d_theta){
		c_theta=(float)Math.cos(theta);
		s_theta=(float)Math.sin(theta);
		
		//rear endcap
		mesh.n[i][0] = new Vector3D(c_theta,s_theta,-1);
		mesh.n[i][0].normalize();
		mesh.v[i][0] = new Vector3D(center.x,center.y,center.z - r);
		
		//front endcap
		mesh.n[i][n-1] = new Vector3D(c_theta,s_theta,1);
		mesh.n[i][n-1].normalize();
		mesh.v[i][n-1] = new Vector3D(center.x,center.y,center.z + r);
	}
}
}