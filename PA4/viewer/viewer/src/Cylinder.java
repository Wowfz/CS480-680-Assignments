
import java.util.*;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLUquadric;
import javax.media.opengl.glu.GLU;


@SuppressWarnings("unused")
public class Cylinder extends RenderObject
{
  private double x, y, z; // Center of base of cylinder
  private double nx, ny, nz; // A (non necessarily unit) vector along axis of
                             // cylinder
  private double radius, length; 

  public Cylinder( StringTokenizer stok, int id_ )
  {
    
    id = id_;

    stok.nextToken(); // strip away the token "mat"
    stok.nextToken(); // strip away the token "ID"
    matId = Integer.parseInt( stok.nextToken() );

    x  = Double.parseDouble( stok.nextToken() );
    y  = Double.parseDouble( stok.nextToken() );
    z  = Double.parseDouble( stok.nextToken() );
    nx = Double.parseDouble( stok.nextToken() );
    ny = Double.parseDouble( stok.nextToken() );
    nz = Double.parseDouble( stok.nextToken() );
    radius = Double.parseDouble( stok.nextToken() );
    length = Double.parseDouble( stok.nextToken() );

  }

  public void render( GL2 gl, MaterialCollection materials )
  {
     double p[]={1, 1, 1}; 
     double q[]={0,0,0};
     double r[]={0,0,0};
     double n[]={0,0,0};
     double mat[];
     double l1,l2,l3,s;
     int i,j;
    
     mat = new double[16];

     n[0] = nx;
     n[1] = ny;
     n[2] = nz;
    
     l3 = n[0]*n[0] + n[1]*n[1] + n[2]*n[2];
     l2 = n[0]*p[0] + n[1]*p[1] + n[2]*p[2];
     s = l2 / l3;
    
     q[0] = p[0] - s * n[0]; q[1] = p[1] - s * n[1]; q[2] = p[2] - s * n[2];
     r[0] = n[2] * q[1] - n[1] * q[2];
     r[1] = n[0] * q[2] - n[2] * q[0];
     r[2] = n[1] * q[0] - n[0] * q[1];
    
     l1 = 0; l2 =0;
    
     for( i =0; i<3; i++){
     	l1 += q[i] * q[i];
     	l2 += r[i] * r[i];
     }
    
     l1 = Math.sqrt(l1);
     l2 = Math.sqrt(l2);
     l3 = Math.sqrt(l3);
    
     for( i =0; i<3; i++){
     	q[i] /= l1;
     	r[i] /= l2;
     	n[i] /= l3;
     }
    
     for( i=0;i<16;i++) mat[i] =0;
     mat[15] = 1;
    
     for( i =0; i<3; i++){
     	mat[i+0] = q[i]; 
     	mat[i+4] = r[i]; 
     	mat[i+8] = n[i]; 
     }

     GLU glu = new GLU();
     GLUquadric quadric = glu.gluNewQuadric();
    
     gl.glPushMatrix();
     gl.glTranslated(x,y,z);
     gl.glMultMatrixd(mat, 0);
     materials.setMaterial( matId, gl );
     gl.glTranslatef(0f,0f,((float)-length)/2.0f);
     glu.gluCylinder(quadric, radius, radius, length, 20, 1);
     gl.glPushMatrix();
     gl.glRotatef(180,0,1,0);
     glu.gluDisk(quadric, 0.0 , radius, 20, 1);
     gl.glPopMatrix();
     gl.glTranslatef(0,0, ((float)length));
     glu.gluDisk(quadric, 0.0 , radius, 20, 1);
     gl.glPopMatrix();
     glu.gluDeleteQuadric( quadric );
  }

  public void print()
  {
    System.out.print( "Cylinder ID:" + id + " mat ID:" + matId);
    System.out.print( " x:" + x + " y:" + y + " z:" + z );
    System.out.print( " nx:" + nx + " ny:" + ny + " nz:" + nz );
    System.out.print( " radius:" + radius + " length:" + length);

    System.out.println();
  }


}

