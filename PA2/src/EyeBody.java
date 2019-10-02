/**
 * EyeBody.java - a solid cylinder with a rounded top
 */


import javax.media.opengl.GL2;

import com.jogamp.opengl.util.gl2.GLUT;//for new version of gl

/**
 * A solid EyeBody with a rounded top.
 * 
 * @author Lunhao Liang <lunhaol@bu.edu>
 * @since Fall 2018
 */
public class EyeBody extends Circular implements Displayable {
  /**
   * The default number of slices to use when drawing the EyeBody and the
   * sphere.
   */
  public static final int DEFAULT_SLICES = 36;
  /**
   * The default number of stacks to use when drawing the EyeBody and the
   * sphere.
   */
  public static final int DEFAULT_STACKS = 28;

  /**
   * The OpenGL handle to the display list which contains all the components
   * which comprise this EyeBody.
   */
  private int callListHandle;
  /** The height of this cylinder. */
  //private final double height;
  Point3D point;
  
  /**
   * Instantiates this object with the specified radius and height of the
   * EyeBody, and the GLUT object to use for drawing the EyeBody and the
   * sphere at the top.
   * 
   * @param radius
   *          The radius of this EyeBody.
   * @param height
   *          The height of this EyeBody.
   * @param glut
   *          The OpenGL utility toolkit object to use to draw the EyeBody and
   *          the sphere at the top.
   */
  public EyeBody(final double radius, final Point3D point,
      final GLUT glut) {
    super(radius, glut);
    this.point = point;
  }

  /**
   * {@inheritDoc}
   * 
   * @param gl
   *          {@inheritDoc}
   * @see edu.bu.cs.cs480.Displayable#draw(javax.media.opengl.GL)
   */
  @Override
  public void draw(final GL2 gl) {
    gl.glCallList(this.callListHandle);
  }

  /**
   * {@inheritDoc}
   * 
   * @param gl
   *          {@inheritDoc}
   * @see edu.bu.cs.cs480.Displayable#initialize(javax.media.opengl.GL)
   */
  @Override
  public void initialize(final GL2 gl) {
    this.callListHandle = gl.glGenLists(1);

    gl.glNewList(this.callListHandle, GL2.GL_COMPILE);

    gl.glPushMatrix();
    gl.glTranslated(point.x(), point.y(), point.z());
    this.glut().glutSolidSphere(this.radius(), DEFAULT_SLICES, DEFAULT_STACKS);
    gl.glPopMatrix();

    gl.glEndList();
  }
  
}
