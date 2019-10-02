
import javax.media.opengl.GL2;

public abstract class RenderObject
{
	public int id, matId;  // The object's unique ID and its material ID.

	// Each class that derives object should implement render()
	public abstract void render( GL2 gl, MaterialCollection materials ); 

    public abstract void print();
}


