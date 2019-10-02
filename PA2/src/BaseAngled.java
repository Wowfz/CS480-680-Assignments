/**
 * BaseAngled.java - base class for an object with angles around three axes
 */


/**
 * A base class for an object which has angles around three axes.
 * 
 * @author Jeffrey Finkelstein <jeffrey.finkelstein@gmail.com>
 * @since Spring 2011
 */
public class BaseAngled implements Angled {
  /** The current angle at which this joint is rotated around the x axis. */
  private double xAngle = 0.0;
  /** The current angle at which this joint is rotated around the y axis. */
  private double yAngle = 0.0;
  /** The current angle at which this joint is rotated around the z axis. */
  private double zAngle = 0.0;

  BaseAngled(final double xAngle, final double yAngle, final double zAngle) {
    this.xAngle = xAngle;
    this.yAngle = yAngle;
    this.zAngle = zAngle;
  }

  /**
   * {@inheritDoc}
   * 
   * @return {@inheritDoc}
   */
  public double xAngle() {
    return this.xAngle;
  }

  /**
   * {@inheritDoc}
   * 
   * @return {@inheritDoc}
   */
  public double yAngle() {
    return this.yAngle;
  }

  /**
   * {@inheritDoc}
   * 
   * @return {@inheritDoc}
   */
  public double zAngle() {
    return this.zAngle;
  }

  public void setXAngle(final double xAngle) {
    this.xAngle = xAngle;
  }

  public void setYAngle(final double yAngle) {
    this.yAngle = yAngle;
  }

  public void setZAngle(final double zAngle) {
    this.zAngle = zAngle;
  }

}
