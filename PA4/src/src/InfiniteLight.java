//****************************************************************************
//       Infinite light source class
//****************************************************************************
// History :
//   Nov 6, 2014 Created by Stan Sclaroff
//
public class InfiniteLight extends CombinedLight
{
	public Vector3D direction, location;
	public ColorType color;
	public boolean radial, angular, amb;
	
	//Constants for radial
	private float a0, a1, a2;
	
	//Constants for angular
	private float al, theta;
	public InfiniteLight(ColorType _c, Vector3D _direction, Vector3D _location, boolean _amb, boolean _radial, boolean _angular)
	{
		color = new ColorType(_c);
		direction = new Vector3D(_direction);
		radial = _radial;
		angular = _angular;
		location = _location;
		amb = _amb;
		a0=a1=a2=0.00001f;
		
		al = 70f;
		theta = 0.1f;
		
	}
	
	// apply this light source to the vertex / normal, given material
	// return resulting color value
	public ColorType applyLight(Material mat, Vector3D v, Vector3D n, Vector3D s){
		ColorType res = new ColorType();
		
		//For angular
		float xx = location.x - s.x;
		float yy = location.y - s.y;
		float zz = location.z - s.z;
		Vector3D lightV = new Vector3D(xx, yy, zz);
		lightV.normalize();
		
		// dot product between light direction and normal
		// light must be facing in the positive direction
		// dot <= 0.0 implies this light is facing away (not toward) this point
		// therefore, light only contributes if dot > 0.0
		double dot = direction.dotProduct(n);
		
		if(radial || angular){
			dot = lightV.dotProduct(n);
		}
		if(dot>0.0)
		{
			//ambient component
			if(mat.ambient && amb){
				res.r = (float)(mat.ka.r*color.r);
				res.g = (float)(mat.ka.g*color.g);
				res.b = (float)(mat.ka.b*color.b);
				
			}
			
			// diffuse component
			if(mat.diffuse && (radial == false) && (angular == false))
			{
				res.r += (float)(dot*mat.kd.r*color.r);
				res.g += (float)(dot*mat.kd.g*color.g);
				res.b += (float)(dot*mat.kd.b*color.b);
			}
			// specular component
			if(mat.specular && (radial == false) && (angular == false))
			{
				Vector3D r = direction.reflect(n);
				dot = r.dotProduct(v);
				if(dot>0.0)
				{
					res.r += (float)Math.pow((dot*mat.ks.r*color.r),mat.ns);
					res.g += (float)Math.pow((dot*mat.ks.g*color.g),mat.ns);
					res.b += (float)Math.pow((dot*mat.ks.b*color.b),mat.ns);
				}
			}
			
			if(mat.diffuse && (radial || angular))
			{	
				res.r += (float)(dot*mat.kd.r*color.r);
				res.g += (float)(dot*mat.kd.g*color.g);
				res.b += (float)(dot*mat.kd.b*color.b);
			}
			// specular component
			if(mat.specular && (radial || angular))
			{
				Vector3D r = direction.reflect(n);
				dot = r.dotProduct(v);
				if(dot>0.0)
				{
					res.r += (float)Math.pow((dot*mat.ks.r*color.r),mat.ns);
					res.g += (float)Math.pow((dot*mat.ks.g*color.g),mat.ns);
					res.b += (float)Math.pow((dot*mat.ks.b*color.b),mat.ns);
				}
			}
			
			
			if(radial)
			{
				float d = (float) Math.sqrt(Math.pow(xx, 2) + Math.pow(yy, 2) + Math.pow(zz, 2));
				float radialAttenuation = 1 / (a0 + a1 * d + a2 * (float)Math.pow(d, 2));
				res.r *= radialAttenuation;
				res.g *= radialAttenuation;
				res.b *= radialAttenuation;
			}
			
			
			if(angular)
			{
				dot = lightV.dotProduct(direction);
				//see if cos alpha is less than cos theta
				if (dot < Math.cos(theta)) {
					res.r *= Math.pow(dot, al);
					res.g *= Math.pow(dot, al);
					res.b *= Math.pow(dot, al);
			}
			}
			
		
		}
		// clamp so that allowable maximum illumination level is not exceeded
		res.r = (float) Math.min(1.0, res.r);
		res.g = (float) Math.min(1.0, res.g);
		res.b = (float) Math.min(1.0, res.b);
		return(res);
	}
	}
