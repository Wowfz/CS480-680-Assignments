

import java.util.*;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.opengl.util.gl2.GLUT;//for new version of gl

@SuppressWarnings("unused")
public class Box extends RenderObject
{
  private GLUT glut;
  private double x, y, z; // Center of box
  private double basis[][]; // Orthogonal unit vectors for the 3 axis of box
  private double len[]; // lengths of the box along directions corresponding 
                        // to the basis vectors
  private double mat[]; // Coordinate transformation matrix for OpenGL

  public Box( StringTokenizer stok, int id_ )
  {
    basis = new double[3][3];
    len   = new double[3];
    mat   = new double[16];

    id = id_;

    stok.nextToken(); // strip away the token "mat"
    stok.nextToken(); // strip away the token "ID"
    matId = Integer.parseInt( stok.nextToken() );

    x  = Double.parseDouble( stok.nextToken() );
    y  = Double.parseDouble( stok.nextToken() );
    z  = Double.parseDouble( stok.nextToken() );
    
    for (int i=0; i<3; ++i)
      for (int j=0; j<3; ++j)
      {
        basis[i][j] = Double.parseDouble( stok.nextToken() );
        mat[4*i+j] = basis[i][j];
      }
    mat[15] = 1;

    for (int i=0; i<3; ++i)
      len[i] = Double.parseDouble( stok.nextToken() );

  }

  public void render( GL2 gl, MaterialCollection materials )
  {
	double alpha, beta;

    glut = new GLUT();

    gl.glPushMatrix();
	gl.glTranslated( x, y, z );
	gl.glMultMatrixd( mat, 0 );
	materials.setMaterial( matId, gl);
	gl.glScaled( len[0], len[1], len[2] );
	glut.glutSolidCube( 1 );
	gl.glPopMatrix();
  }

  public void print()
  {
    System.out.print( "Box ID:" + id + " mat ID:" + matId );
    System.out.print( " x:" + x + " y:" + y + " z:" + z );
    System.out.print( " basis (" );
    for (int i=0; i<3; ++i)
      for (int j=0; j<3; ++j)
        System.out.print( basis[i][j]+ " ");

    System.out.print( " len:" + len[0] + " " + len[1] + " " + len[2]);
    System.out.print( " mat:" );
    for (int i=0; i<16; ++i)
      System.out.print( mat[i] + " " );
    System.out.println();

  }

}

