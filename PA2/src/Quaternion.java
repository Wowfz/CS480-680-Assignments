/**
 * Quaternion.java - a quaternion with methods for common operations
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
 * - translated from C code by Stan Sclaroff from CS480 2006 assignment 2,
 * "quaternion.c"
 * 
 * (Tai-Peng Tian <tiantp@gmail.com>)
 */


/**
 * A quaternion with float values.
 * 
 * @author Tai-Peng Tian <tiantp@gmail.com>
 * @since Spring 2008
 */
public class Quaternion {
  /** The threshold below which to renormalize this quaternion, if necessary. */
  public static final float ROUND_OFF_THRESHOLD = 0.0001f;
  /** The scalar component of this quaternion. */
  private float s;

  /** The vector components of this quaternion. */
  private final float[] v = new float[3];

  /**
   * Instantiates this quaternion with an initial value of (1, 0, 0, 0).
   */
  public Quaternion() {
    this(1, 0, 0, 0);
  }

  /**
   * Instantiates this quaternion with the specified component values.
   * 
   * 
   * @param s
   *          The scalar component of this quaternion.
   * @param v0
   *          The first vector component of this quaternion.
   * @param v1
   *          The second vector component of this quaternion.
   * @param v2
   *          The third vector component of this quaternion.
   */
  public Quaternion(float s, float v0, float v1, float v2) {
    this.set(s, v0, v1, v2);
  }

  /**
   * Returns a new quaternion representing the product of this and the specified
   * other quaternion.
   * 
   * @param that
   *          The other quaternion with which to multiply this one.
   * @return The product of this and the specified other quaternion.
   */
  public Quaternion multiply(final Quaternion that) {

    // s = s1*s2 - v1.v2
    final float newS = this.s * that.s - this.v[0] * that.v[0] - this.v[1]
        * that.v[1] - this.v[2] * that.v[2];

    // v = s1 v2 + s2 v1 + v1 x v2
    float i = (this.s * that.v[0]) + (that.s * this.v[0])
        + (this.v[1] * that.v[2] - this.v[2] * that.v[1]);
    float j = (this.s * that.v[1]) + (that.s * this.v[1])
        + (this.v[2] * that.v[0] - this.v[0] * that.v[2]);
    float k = (this.s * that.v[2]) + (that.s * this.v[2])
        + (this.v[0] * that.v[1] - this.v[1] * that.v[0]);

    return new Quaternion(newS, i, j, k);
  }

  /**
   * Returns the norm (magnitude) of this quaternion.
   * 
   * @return The norm (magnitude) of this quaternion.
   */
  private float norm() {
    return (float) Math.sqrt(this.s * this.s + this.v[0] * this.v[0]
        + this.v[1] * this.v[1] + this.v[2] * this.v[2]);
  }

  /**
   * Normalizes this quaternion so that it is truly unit.
   * 
   * It may be necessary to call this method after accumulating round-off error.
   */
  public void normalize() {
    final float mag = this.norm();

    if (mag > ROUND_OFF_THRESHOLD) {
      this.s /= mag;
      this.v[0] /= mag;
      this.v[1] /= mag;
      this.v[2] /= mag;
    }
  }

  /**
   * Resets this quaternion to (1, 0, 0, 0).
   */
  public void reset() {
    this.set(1f, 0f, 0f, 0f);
  }

  /**
   * Sets the components of this quaternion to the specified values.
   * 
   * @param s
   *          The scalar component of this quaternion.
   * @param v0
   *          The first vector component of this quaternion.
   * @param v1
   *          The second vector component of this quaternion.
   * @param v2
   *          The third vector component of this quaternion.
   */
  private void set(float s, float v0, float v1, float v2) {
    this.s = s;
    this.v[0] = v0;
    this.v[1] = v1;
    this.v[2] = v2;
  }

  /**
   * Returns a 4 by 4 matrix which represents a transformation equivalent to
   * that of this quaternion.
   * 
   * Note: OpenGL uses column major order when specifying a matrix.
   * 
   * Algorithm: follows equation 5-107 on page 273 of Hearn and Baker.
   * 
   * @return
   */
  public float[] toMatrix() {
    final float[] M = new float[16];

    final float a = this.v[0];
    final float b = this.v[1];
    final float c = this.v[2];

    // Specify the matrix in column major
    M[0] = 1 - 2 * b * b - 2 * c * c; // M[0][0]
    M[1] = 2 * a * b + 2 * this.s * c; // M[1][0]
    M[2] = 2 * a * c - 2 * this.s * b; // M[2][0]
    M[3] = 0.0f; // M[3][0]

    M[4] = 2 * a * b - 2 * this.s * c; // M[0][1]
    M[5] = 1 - 2 * a * a - 2 * c * c; // M[1][1]
    M[6] = 2 * b * c + 2 * this.s * a; // M[2][1]
    M[7] = 0.0f; // M[3][1]

    M[8] = 2 * a * c + 2 * this.s * b; // M[0][2]
    M[9] = 2 * b * c - 2 * this.s * a; // M[1][2]
    M[10] = 1 - 2 * a * a - 2 * b * b; // M[2][2]
    M[11] = 0.0f; // M[3][2]

    M[12] = 0.0f; // M[0][3]
    M[13] = 0.0f; // M[1][3]
    M[14] = 0.0f; // M[2][3]
    M[15] = 1.0f; // M[3][3]

    return M;
  }

}
