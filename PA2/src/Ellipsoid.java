/**
 * Ellipsoid.java - a solid Ellipsoid with a rounded top
 */


import javax.media.opengl.GL2;

import com.jogamp.opengl.util.gl2.GLUT;//for new version of gl

/**
 * A solid Ellipsoid with a rounded top.
 * 
 * @author Lunhao Liang <lunhaol@bu.edu>
 * @since Fall 2018
 */
public class Ellipsoid extends Circular implements Displayable {
  /**
   * The default number of slices to use when drawing the Ellipsoid and the
   * sphere.
   */
  public static final int DEFAULT_SLICES = 36;
  /**
   * The default number of stacks to use when drawing the Ellipsoid and the
   * sphere.
   */
  public static final int DEFAULT_STACKS = 28;

  /**
   * The OpenGL handle to the display list which contains all the components
   * which comprise this Ellipsoid.
   */
  private int callListHandle;
  /** The height of this Ellipsoid. */
  private final double height;

  /** The scale of Ellipsoid*/
  private final double scalex;
  private final double scaley;
  private final double scalez;
  
  /**
   * Instantiates this object with the specified radius and height of the
   * Ellipsoid, and the GLUT object to use for drawing the Ellipsoid and the
   * sphere at the top.
   * 
   * @param radius
   *          The radius of this Ellipsoid.
   * @param height
   *          The height of this Ellipsoid.
   * @param glut
   *          The OpenGL utility toolkit object to use to draw the Ellipsoid and
   *          the sphere at the top.
   */
  public Ellipsoid(final double radius, final double height, final double scalex, final double scaley, final double scalez,
      final GLUT glut) {
    super(radius, glut);
    this.height = height;
    this.scalex = scalex;
    this.scaley = scaley;
    this.scalez = scalez;
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
    gl.glTranslated(0, 0, this.height);
    gl.glScaled(scalex, scaley, scalez);
    this.glut().glutSolidSphere(this.radius(), DEFAULT_SLICES, DEFAULT_STACKS);

    gl.glPopMatrix();

    gl.glEndList();
  }
}
