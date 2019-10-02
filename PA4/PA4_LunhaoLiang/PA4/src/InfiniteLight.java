//****************************************************************************
//       Infinite light source class
//****************************************************************************
// History :
//   Nov 6, 2014 Created by Stan Sclaroff
//
public class InfiniteLight 
{
	public Vector3D direction;
	public ColorType color;
	
	public InfiniteLight(ColorType _c, Vector3D _direction)
	{
		color = new ColorType(_c);
		direction = new Vector3D(_direction);
		
	}
	
	// apply this light source to the vertex / normal, given material
	// return resulting color value
	public ColorType applyLight(Material mat, Vector3D v, Vector3D n){
		ColorType res = new ColorType();
		
		// dot product between light direction and normal
		// light must be facing in the positive direction
		// dot <= 0.0 implies this light is facing away (not toward) this point
		// therefore, light only contributes if dot > 0.0
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
		return(res);
	}
}
