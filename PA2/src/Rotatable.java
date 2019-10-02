/**
 * Rotatable.java - an object which can rotate about three axes
 */


/**
 * An object which can rotate about the x, y, or z axes.
 * 
 * @author Jeffrey Finkelstein <jeffrey.finkelstein@gmail.com>
 * @since Spring 2011
 */
public interface Rotatable {

  /**
   * Rotates this object around the specified axis by the specified angle.
   * 
   * @param axis
   *          The axis of rotation.
   * @param angleDelta
   *          The angle by which to rotate this joint.
   */
  void rotate(final Axis axis, final double angleDelta);
  
  /**
   * Sets the minimum angle to which this joint can be rotated around the x
   * axis.
   * 
   * @param newXNegativeExtent
   *          The minimum angle to which this joint can be rotated around the x
   *          axis.
   */
  void setXNegativeExtent(final double newXNegativeExtent);

  /**
   * Sets the maximum angle to which this joint can be rotated around the x
   * axis.
   * 
   * @param newXPositiveExtent
   *          The maximum angle to which this joint can be rotated around the x
   *          axis.
   */
  void setXPositiveExtent(final double newXPositiveExtent);

  /**
   * Sets the minimum angle to which this joint can be rotated around the y
   * axis.
   * 
   * @param newYNegativeExtent
   *          The minimum angle to which this joint can be rotated around the y
   *          axis.
   */
  void setYNegativeExtent(final double newYNegativeExtent);
  /**
   * Sets the maximum angle to which this joint can be rotated around the y
   * axis.
   * 
   * @param newYPositiveExtent
   *          The maximum angle to which this joint can be rotated around the y
   *          axis.
   */
  void setYPositiveExtent(final double newYPositiveExtent);

  /**
   * Sets the minimum angle to which this joint can be rotated around the z
   * axis.
   * 
   * @param newZNegativeExtent
   *          The minimum angle to which this joint can be rotated around the z
   *          axis.
   */
  void setZNegativeExtent(final double newZNegativeExtent);

  /**
   * Sets the maximum angle to which this joint can be rotated around the z
   * axis.
   * 
   * @param newZPositiveExtent
   *          The maximum angle to which this joint can be rotated around the z
   *          axis.
   */
  void setZPositiveExtent(final double newZPositiveExtent);

  /**
   * Gets the current angle at which this joint is rotated around the x axis.
   * 
   * @return The current angle at which this joint is rotated around the x axis.
   */
  double xAngle();
  
  /**
   * Gets the current angle at which this joint is rotated around the y axis.
   * 
   * @return The current angle at which this joint is rotated around the y axis.
   */
  double yAngle();

  /**
   * Gets the current angle at which this joint is rotated around the z axis.
   * 
   * @return The current angle at which this joint is rotated around the z axis.
   */
  double zAngle();


}
