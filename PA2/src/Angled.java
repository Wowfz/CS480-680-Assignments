/**
 * Angled.java - an object which has angles around three axes
 */


/**
 * An object which has angles around three axes.
 * 
 * @author Jeffrey Finkelstein <jeffrey.finkelstein@gmail.com>
 * @since Spring 2011
 */
public interface Angled {

  /**
   * Gets the current angle at which this joint is rotated around the x axis.
   * 
   * @return The current angle at which this joint is rotated around the x
   *         axis.
   */
  double xAngle();

  /**
   * Gets the current angle at which this joint is rotated around the y axis.
   * 
   * @return The current angle at which this joint is rotated around the y
   *         axis.
   */
  double yAngle();

  /**
   * Gets the current angle at which this joint is rotated around the z axis.
   * 
   * @return The current angle at which this joint is rotated around the z
   *         axis.
   */
  double zAngle();

  void setXAngle(final double xAngle);
  void setYAngle(final double yAngle);
  void setZAngle(final double zAngle);
}
