// AmbientLight class
// define ambient_light
//
public class AmbientLight {
	//public Vector3D direction;
	public ColorType color;

	public AmbientLight(ColorType _c) {
		color = new ColorType(_c);
		//direction = new Vector3D(_direction);

	}

	// apply this light source to the vertex / normal, given material
	// return resulting color value
	public ColorType applyLight(Material mat) {
		ColorType res = new ColorType();

		//ambient component
		res.r = mat.ka.r*color.r;
		res.g = mat.ka.g*color.g;
		res.b = mat.ka.b*color.b;

		// clamp so that allowable maximum illumination level is not exceeded
		res.r = (float) Math.min(1.0, res.r);
		res.g = (float) Math.min(1.0, res.g);
		res.b = (float) Math.min(1.0, res.b);
		return (res);
	}
}

