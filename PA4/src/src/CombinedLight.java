import java.util.*;

//Combined light module to calculate attenuations and other light sources

public class CombinedLight {
	//List of lights
	public List<CombinedLight> lights;
	
	
	public CombinedLight() {
		lights = new ArrayList<CombinedLight>();
	}
	
	public void addLight(InfiniteLight newLight) {
		lights.add(newLight);
	}
	
	public void removeLight(CombinedLight l) {
		lights.remove(l);
	}
	
	public int sizee(){
		return lights.size();
	}
	
	public ColorType applyLight(Material mat, Vector3D v, Vector3D n, Vector3D p) {
		ColorType res = new ColorType();
		ColorType summ = new ColorType();
		ColorType amb = new ColorType();
		for (CombinedLight l : lights) {
			
			summ = l.applyLight(mat, v, n, p);
			
			res.r += summ.r;
			res.g += summ.g;
			res.b += summ.b;
		}
		
		//Combined lighting module
		res.r = amb.r + res.r;
		res.g = amb.g + res.g;
		res.b = amb.b + res.b;
		
		// Clamp
		res.r = (float) Math.min(1.0, res.r);
		res.g = (float) Math.min(1.0, res.g);
		res.b = (float) Math.min(1.0, res.b);
		
		return res;
	}
	
}