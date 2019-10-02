/**
 * Point3D.java - a three-dimensional point with double values
 * 
 * History:
 * 
 * 18 February 2011
 * 
 * - made members private and added accessors
 * 
 * - added documentation
 * 
 * - added toString() method
 * 
 * (Jeffrey Finkelstein <jeffrey.finkelstein@gmail.com>)
 * 
 * 30 January 2008
 * 
 * - created
 * 
 * (Tai-Peng Tian <tiantp@gmail.com>)
 */


/**
 * A three-dimensional point with double values.
 * 
 * @author Tai-Peng Tian <tiantp@gmail.com>
 * @author Jeffrey Finkelstein <jeffrey.finkelstein@gmail.com>
 * @since Spring 2008
 */
public class Point3D {
  /** The origin, (0, 0, 0). */
  public static final Point3D ORIGIN = new Point3D(0, 0, 0);
  /** The x component of this point. */
  private final double x;
  /** The y component of this point. */
  private final double y;
  /** The z component of this point. */
  private final double z;

  /**
   * Instantiates this point with the three specified components.
   * 
   * @param x
   *          The x component of this point.
   * @param y
   *          The y component of this point.
   * @param z
   *          The z component of this point.
   */
  public Point3D(final double x, final double y, final double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /**
   * Returns the String representation of this object.
   * 
   * @return The String representation of this object.
   */
  @Override
  public String toString() {
    return "Point[" + this.x + ", " + this.y + ", " + this.z + "]";
  }

  /**
   * Gets the x component of this point.
   * 
   * @return The x component of this point.
   */
  public double x() {
    return this.x;
  }

  /**
   * Gets the y component of this point.
   * 
   * @return The y component of this point.
   */
  public double y() {
    return this.y;
  }

  /**
   * Gets the z component of this point.
   * 
   * @return The z component of this point.
   */
  public double z() {
    return this.z;
  }
}
