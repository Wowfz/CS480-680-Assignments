//
// generate superellipsoid3D with mesh
// Author: Lunhao Liang
//


public class Superellipsoid3D
{
	private Vector3D center;
	private  float r,t;
	private float a, b,  c;
	private int m,n;
	public Mesh3D mesh;
	
	public Superellipsoid3D(float _x, float _y, float _z, float _r, float _t,float _a, float _b, float _c, int _m, int _n)
	{
		center = new Vector3D(_x,_y,_z);
		r = _r;
		t = _t;
		a = _a;
		b = _b;
		c = _c;
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
	
	public void set_radius(float _rx, float _ry, float _rz)
	{
		a = _rx;
		b = _ry;
		c = _rz;
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
		float theta, phi;
		float d_theta=(float)(2.0*Math.PI)/ ((float)(m-1));
		float d_phi=(float)Math.PI / ((float)n-1);
		float c_theta,s_theta;
		float cos_theta,sin_theta;
		float c_phi, s_phi;
		float cos_phi, sin_phi;
		Vector3D du = new Vector3D(),dv = new Vector3D();
		
		for(i=0,theta=-(float)Math.PI;i<m;++i,theta += d_theta)
	    {
			cos_theta=(float)Math.cos(theta);
			sin_theta=(float)Math.sin(theta);
			c_theta = Math.signum(cos_theta)*((float)Math.pow(Math.abs(cos_theta),2/r));
			s_theta = Math.signum(sin_theta)*((float)Math.pow(Math.abs(sin_theta),2/r));

			for(j=0,phi=(float)(-0.5*Math.PI);j<n;++j,phi += d_phi)
			{
				cos_phi = (float)Math.cos(phi);
				sin_phi = (float)Math.sin(phi);
				c_phi = Math.signum(cos_phi)*((float)Math.pow(Math.abs(cos_phi),2/t));
				s_phi = Math.signum(sin_phi)*((float)Math.pow(Math.abs(sin_phi),2/t));

				mesh.v[i][j].x=center.x+a*c_theta*c_phi;
				mesh.v[i][j].y=center.y+b*c_phi*s_theta;
				mesh.v[i][j].z=center.z+c*s_phi;
				


				// compute partial derivatives
				// then use cross-product to get the normal
				// and normalize to produce a unit vector for the normal
				du.x = a*Math.signum(cos_phi)*(2/t)*(float)Math.pow(Math.abs(cos_phi),2/t-1)*Math.abs(-sin_phi)*c_theta;
				du.y = b*Math.signum(cos_phi)*(2/t)*(float)Math.pow(Math.abs(cos_phi),2/t-1)*Math.abs(-sin_phi)*s_theta;
				du.z = c*Math.signum(sin_phi)*(2/t)*(float)Math.pow(Math.abs(sin_phi),2/t-1)*Math.abs(cos_phi);


				//float dx_du=A*sgn(cos_v_dv)*cos_u_du*pow(std::abs(cos_v_dv),n)*pow(std::abs(sin_u_du),m-1);
				//float dy_du=B*sgn(sin_v_dv)*cos_u_du*pow(std::abs(sin_v_dv),n)*pow(std::abs(sin_u_du),m-1);
				//float dz_du=-C*sin_u_du*pow(std::abs(cos_u_du),m-1);

				// Derivative with respect to v
				//float dx_dv=-A*sgn(sin_u_du)*pow(std::abs(sin_u_du),m)*sin_v_dv*pow(std::abs(cos_v_dv),n-1);
				//float dy_dv=B*sgn(sin_u_du)*pow(std::abs(sin_u_du),m)*cos_v_dv*pow(std::abs(sin_v_dv),n-1);
				// derivative of z with respect to v is 0

				dv.x = a*Math.signum(cos_theta)*(2/r)*(float)Math.pow(Math.abs(cos_theta),2/r-1)*Math.abs(-sin_theta)*c_phi;
				dv.y = b*Math.signum(sin_theta)*(2/r)*(float)Math.pow(Math.abs(sin_theta),2/r-1)*Math.abs(cos_theta)*c_phi;
				dv.z = 0;

				dv.crossProduct(du, mesh.n[i][j]);
				mesh.n[i][j].normalize();
				//mesh.n[i][j].x =

				//mesh.n[i][0] =  new Vector3D(0,0,-1);
				//mesh.n[i][n-1] =  new Vector3D(0,0,1);
				//mesh.v[i][0] = new Vector3D(center.x,center.y,center.z - a);
				//mesh.v[i][n-1] = new Vector3D(center.x,center.y,center.z + a);


				// ellipsoid normal
				//mesh.n[i][j].x = cos_phi*cos_theta;
				//mesh.n[i][j].y = cos_phi*sin_theta;
				//mesh.n[i][j].z= sin_phi;
			}
	    }
	}
}