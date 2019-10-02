
import java.util.*;
import javax.media.opengl.GL2;
import com.jogamp.opengl.util.gl2.GLUT;//for new version of gl

public class Sphere extends RenderObject
{
  private double x, y, z, radius; // center and radius of sphere
  private GLUT glut;

  public Sphere( StringTokenizer stok, int id_ )
  {
    glut = new GLUT();

    id = id_;

    stok.nextToken(); // strip away the token "mat"
    stok.nextToken(); // strip away the token "ID"
    matId = Integer.parseInt( stok.nextToken() );

    x  = Double.parseDouble( stok.nextToken() );
    y  = Double.parseDouble( stok.nextToken() );
    z  = Double.parseDouble( stok.nextToken() );
    radius  = Double.parseDouble( stok.nextToken() );
  }

  public void render( GL2 gl, MaterialCollection materials )
  {
    gl.glPushMatrix();
    gl.glTranslated(x,y,z);
    materials.setMaterial(matId, gl);
    glut.glutSolidSphere(radius,40,40);
	gl.glPopMatrix();
  }

  public void print()
  {
    System.out.print( "Sphere ID:" + id + " mat ID:" + matId );
    System.out.print( " x:" + x + " y:" + y + " z:" + z);
    System.out.print( " radius:" + radius );
    System.out.println();

  }

}

