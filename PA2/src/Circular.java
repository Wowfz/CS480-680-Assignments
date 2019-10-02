/**
 * Circular.java - a circular object
 */


import com.jogamp.opengl.util.gl2.GLUT;//for new version of gl

/**
 * A circular object.
 * 
 * @author Jeffrey Finkelstein <jeffrey.finkelstein@gmail.com>
 * @since Spring 2011
 */
public class Circular {
  /** The OpenGL utility toolkit object to use to draw this object. */
  private final GLUT glut;
  /** The radius of this object. */
  private final double radius;

  /**
   * Instantiates this object with the specified radius.
   * 
   * @param radius
   *          The radius of this object.
   */
  public Circular(final double radius, final GLUT glut) {
    this.radius = radius;
    this.glut = glut;
  }

  /**
   * Gets the OpenGL utility toolkit object.
   * 
   * @return The OpenGL utility toolkit object.
   */
  protected GLUT glut() {
    return this.glut;
  }

  /**
   * Gets the radius of this object.
   * 
   * @return The radius of this object.
   */
  protected double radius() {
    return this.radius;
  }
}
