import java.util.Vector;

//****************************************************************************
//       Infinite light source class
//****************************************************************************
// History :
//   Nov 6, 2014 Created by Stan Sclaroff
//
public class PointLight
{
	public Vector3D position;
	public Vector3D direction;
	public Vector3D max_intensity_vl;
	public ColorType color;
	public boolean radial,angular;
	public float a0, a1, a2;  //radial
	public float theta_l;
	public float al;

	public PointLight(ColorType _c, Vector3D _position, Vector3D _max_intensity_vl, float _a0, float _a1, float _a2, float _al, float _theta_l, boolean _radial, boolean _angular)
	{
		color = new ColorType(_c);
		position = new Vector3D(_position);
		max_intensity_vl = new Vector3D(_max_intensity_vl);
		a0 = _a0;
		a1 = _a1;
		a2 = _a2;
		al = _al;
		theta_l = _theta_l;
		radial = _radial;
		angular = _angular;
	}
	
	// apply this light source to the vertex / normal, given material
	// return resulting color value
	public ColorType applyLight(Material mat, Vector3D v, Vector3D n, Vector3D mp){
		ColorType res = new ColorType();
		
		// dot product between light direction and normal
		// light must be facing in the positive direction
		// dot <= 0.0 implies this light is facing away (not toward) this point
		// therefore, light only contributes if dot > 0.0
		direction = position.minus(mp);
		//direction.y = position.y - mp.y;
		//direction.z = position.z - mp.z;

		direction.normalize();

		double dot = direction.dotProduct(n);
		if(dot>0.0)
		{
			// diffuse component
			if(mat.diffuse)
			{
				res.r = (float)(dot*mat.kd.r*color.r);
				res.g = (float)(dot*mat.kd.g*color.g);
				res.b = (float)(dot*mat.kd.b*color.b);
			}
			// specular component
			if(mat.specular)
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
			
			// clamp so that allowable maximum illumination level is not exceeded
			res.r = (float) Math.min(1.0, res.r);
			res.g = (float) Math.min(1.0, res.g);
			res.b = (float) Math.min(1.0, res.b);
		}


		if(radial)
		{
			float dl = (float) Math.sqrt(Math.pow(position.x, 2) + Math.pow(position.y, 2) + Math.pow(position.z, 2));
			float fradatten = 1 / (a0 + a1 * dl + (float)(a2 * Math.pow(dl, 2)));
			res.r *= fradatten;
			res.g *= fradatten;
			res.b *= fradatten;
		}


		if(angular)
		{
			float cos_alpha;
			cos_alpha = direction.dotProduct(max_intensity_vl);
			//check if the point in the cone
			if (cos_alpha < Math.cos(theta_l)) {
				res.r *= Math.pow(cos_alpha, al);
				res.g *= Math.pow(cos_alpha, al);
				res.b *= Math.pow(cos_alpha, al);
			}
		}

		// clamp so that allowable maximum illumination level is not exceeded
		res.r = (float) Math.min(1.0, res.r);
		res.g = (float) Math.min(1.0, res.g);
		res.b = (float) Math.min(1.0, res.b);
		return(res);
	}
}
