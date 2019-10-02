//
// generate box with mesh
//

public class Box
{
	private Vector3D center;
	private float l, w, h;
	private int m,n;
	public Mesh3D mesh;
	
	public Box(float _x, float _y, float _z, float _l, float _w, float _h, int _m, int _n)
	{
		center = new Vector3D(_x,_y,_z);
		l = _l;
		w = _l;
		h = _l;
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
	
	public Vector3D get_center()
	{
		return center;
	}
	
	public void set_radius(float _l, float _w, float _h)
	{
		l = _l;
		w = _w;
		h = _h;
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
	// using the current parameters for the box
	private void fillMesh()
	{
		int i,j;		
		float theta, phi;
		float d_theta=(float)(2.0*Math.PI)/ ((float)(m-1));
		float d_phi=(float)Math.PI / ((float)n-1);
		float c_theta,s_theta;
		float c_phi, s_phi;
		
		for(i=0,theta=-(float)Math.PI;i<m;++i,theta += d_theta)
	    {
			c_theta=(float)Math.cos(theta);
			s_theta=(float)Math.sin(theta);
			
			for(j=0,phi=(float)(-0.5*Math.PI);j<n;++j,phi += d_phi)
			{
				// vertex location
				c_phi = (float)Math.cos(phi);
				s_phi = (float)Math.sin(phi);
				float absU = Math.abs(c_phi);
				float absV = Math.abs(c_theta);
				float absSV = Math.abs(s_theta);
				float absSU = Math.abs(s_phi);
				mesh.v[i][j].x =center.x +l* Math.signum(c_phi) * ((float) Math.pow(absU, 0.1))*Math.signum(c_theta)*((float) Math.pow(absV, 2));
				mesh.v[i][j].y =center.y +w* Math.signum(c_phi) * ((float) Math.pow(absU, 0.1))*Math.signum(s_theta)*((float) Math.pow(absSV, 2));
				mesh.v[i][j].z =center.z +h* Math.signum(s_phi) * ((float) Math.pow(absSU, 0.1));
				
				// used same normal as sphere as suggested in lecture
				
				mesh.n[i][j].x = c_phi*c_theta;
				mesh.n[i][j].y = c_phi*s_theta;
				mesh.n[i][j].z= s_phi;
				
				mesh.n[i][0] =  new Vector3D(0,0,-1);
				mesh.n[i][n-1] =  new Vector3D(0,0,1);
				mesh.v[i][0] = new Vector3D(center.x,center.y,center.z - l);
				mesh.v[i][n-1] = new Vector3D(center.x,center.y,center.z + l);
				
				//mesh.n[i][j].normalize();
			}
	    }
	}
}