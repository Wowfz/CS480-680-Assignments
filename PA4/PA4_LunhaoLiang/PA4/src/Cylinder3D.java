// Cylinder class generate cylinder with endcaps

public class Cylinder3D
{
	private Vector3D center;
	private float r;
	private float h; //height
	private int m,n;
	public Mesh3D mesh;

	public Cylinder3D(float _x, float _y, float _z, float _r, float _h, int _m, int _n)
	{
		center = new Vector3D(_x,_y,_z);
		r = _r;
		h = _h;
		m = _m;
		n = _n;
		initMesh();
	}
	
	public void set_center(float _x, float _y, float _z)
	{
		center.x=_x;
		center.y=_y;
		center.z=_z;
		fillMesh();  // update the triangle mesh
	}
	
	public void set_radius(float _r)
	{
		r = _r;
		fillMesh(); // update the triangle mesh
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
	private void fillMesh()
	{
		int i,j;
		float umin = 0;
		float umax = h;
		float theta, phi;
		float d_theta=(float)(2.0*Math.PI)/ ((float)(m-1));
		float d_u=(umax-umin) / ((float)n-1);
		float c_theta,s_theta;
		float u;
		float r_small;
		float d_r= r / ((float)n-1);

		float c_phi, s_phi;
		
		for(i=0,theta=-(float)Math.PI;i<m;++i,theta += d_theta)
	    {
			c_theta=(float)Math.cos(theta);
			s_theta=(float)Math.sin(theta);
			
			for(j=0,u=0;j<n;++j,u += d_u)
			{
				// vertex location
				mesh.v[i][j].x=center.x+r*c_theta;
				mesh.v[i][j].y=center.y+r*s_theta;
				mesh.v[i][j].z=center.z+u;

				mesh.n[i][j].x = c_theta;
				mesh.n[i][j].y = s_theta;
				mesh.n[i][j].z = 0;
			}

			//endcaps
			mesh.v[i][0].x = center.x;
			mesh.v[i][0].y = center.y;
			mesh.v[i][0].z = center.z + umin;

			mesh.n[i][0].x = 0;
			mesh.n[i][0].y = 0;
			mesh.n[i][0].z = -1;

			mesh.v[i][n - 1].x = center.x;
			mesh.v[i][n - 1].y = center.y;
			mesh.v[i][n - 1].z = center.z + umax;

			mesh.n[i][n - 1].x = 0;
			mesh.n[i][n - 1].y = 0;
			mesh.n[i][n - 1].z = 1;

		}


	}
}