/**
 * PA2.java - driver for the hand model simulation
 * 
 * Lunhao Liang (lunhaol@bu.edu)
 */

import java.lang.Math;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;//for new version of gl
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import com.jogamp.opengl.util.FPSAnimator;//for new version of gl
import com.jogamp.opengl.util.gl2.GLUT;//for new version of gl

/**
 * The main class which drives the hand model simulation.
 * 
 * @author Lunhao Liang (lunhaol@bu.edu)
 * @since Fall 2018
 */
public class PA2 extends JFrame implements GLEventListener, KeyListener,
    MouseListener, MouseMotionListener {

  /**
   * A foot which has a body joint, a inner middle joint, a outer middle joint and a distal joint.
   * 
   * @author Lunhao Liang (lunhaol@bu.edu)
   * @since Fall 2018
   */
	
  private class Foot {
	    /** The distal joint of this Foot. */
	    private final Component distalJoint;
	    /** The list of all the joints in this Foot. */
	    private final List<Component> joints;
	    /** The outer middle joint of this Foot. */
	    private final Component outmiddleJoint;
	    /** The inner middle joint of this Foot. */
	    private final Component inmiddleJoint;
	    /** The body joint of this Foot. */
	    private final Component bodyJoint;

	    /**
	     * Instantiates this foot with the four specified joints.
	     * 
	     * @param bodyJoint
	     *          The body joint of this foot.
	     * @param inmiddleJoint
	     *          The inner middle joint of this foot.
	     * @param outmiddleJoint
	     *          The outer middle joint of this foot.
	     * @param distalJoint
	     *          The distal joint of this foot.
	     */
	    public Foot(final Component bodyJoint, final Component inmiddleJoint, final Component outmiddleJoint,
	        final Component distalJoint) {
	      this.bodyJoint = bodyJoint;
	      this.inmiddleJoint = inmiddleJoint;
	      this.outmiddleJoint = outmiddleJoint;
	      this.distalJoint = distalJoint;

	      this.joints = Collections.unmodifiableList(Arrays.asList(this.bodyJoint,
	          this.inmiddleJoint, this.outmiddleJoint, this.distalJoint));
	    }

	    /**
	     * Gets the distal joint of this foot.
	     * 
	     * @return The distal joint of this foot.
	     */
	    Component distalJoint() {
	      return this.distalJoint;
	    }

	    /**
	     * Gets an unmodifiable view of the list of the joints of this foot.
	     * 
	     * @return An unmodifiable view of the list of the joints of this foot.
	     */
	    List<Component> joints() {
	      return this.joints;
	    }

	    /**
	     * Gets the inner middle joint of this foot.
	     * 
	     * @return The inner middle joint of this foot.
	     */
	    Component inmiddleJoint() {
	      return this.inmiddleJoint;
	    }
	    
	    /**
	     * Gets the outer middle joint of this foot.
	     * 
	     * @return The outer middle joint of this foot.
	     */
	    Component outmiddleJoint() {
	      return this.outmiddleJoint;
	    }

	    /**
	     * Gets the body joint of this foot.
	     * 
	     * @return The body joint of this foot.
	     */
	    Component bodyJoint() {
	      return this.bodyJoint;
	    }
	  }
  
  private class Eye {
	    /** The eye. */
	    private final ComponentEye eye;
	    /** The list of all the parts in this eye. */
	    private final List<ComponentEye> parts;
	    /** The eyeball. */
	    private final ComponentEye eyeball;

	    public Eye(final ComponentEye eye, final ComponentEye eyeball) {
	      this.eye = eye;
	      this.eyeball = eyeball;
	      this.parts = Collections.unmodifiableList(Arrays.asList(this.eye, this.eyeball));
	    }

	    /**
	     * Gets the eye.
	     * 
	     * @return The eye.
	     */
	    ComponentEye eye() {
	      return this.eye;
	    }

	    /**
	     * Gets an unmodifiable view of the list of the joints of this eye.
	     * 
	     * @return An unmodifiable view of the list of the joints of this eye.
	     */
	    List<ComponentEye> parts() {
	      return this.parts;
	    }

	    /**
	     * Gets the eyeball.
	     * 
	     * @return The eyeball.
	     */
	    ComponentEye eyeball() {
	      return this.eyeball;
	    }
	    
	  }
  
  /** The color for components which are selected for rotation. */
  public static final FloatColor ACTIVE_COLOR = FloatColor.RED;
  /** The default width of the created window. */
  public static final int DEFAULT_WINDOW_HEIGHT = 512;
  /** The default height of the created window. */
  public static final int DEFAULT_WINDOW_WIDTH = 512;
  /** The height of the distal joint on each of the foot. */
  public static final double DISTAL_JOINT_HEIGHT = 0.1;
  /** The color for components which are not selected for rotation. */
  public static final FloatColor INACTIVE_COLOR = FloatColor.ORANGE;
  /** The color for components which are not selected for rotation. */
  public static final FloatColor EYE_INACTIVE_COLOR = FloatColor.WHITE;
  /** The initial position of the top level component in the scene. */
  public static final Point3D INITIAL_POSITION = new Point3D(0, -1, 0);
  /** The angle by which to rotate the joint on user request to rotate. */
  public static final double ROTATION_ANGLE = 2.0;
  /** Randomly generated serial version UID. */
  private static final long serialVersionUID = -7060944143920496524L;
  /** The radius of the components which comprise the foot. */
  public static final double FOOT_RADIUS = 0.02;
  /** The radius of the components which comprise the body. */
  public static final double BODY_RADIUS = 0.4;
  /** The height of the body. */
  public static final double BODY_HEIGHT = 1.8;
  /** The radius of the components which comprise the abdomen. */
  public static final double ABDOMEN_RADIUS = 0.4;
  /** The radius of the components which comprise the eye. */
  public static final double EYE_RADIUS = 0.15;
  /** The height of the eye. */
  public static final double EYE_HEIGHT = 1.3;
  /** The radius of the components which comprise the eyeball. */
  public static final double EYEBALL_RADIUS = 0.05;
  /** The height of the eyeball. */
  public static final double EYEBALL_HEIGHT = 1.3;
  /** The height of the abdomen. */
  public static final double ABDOMEN_HEIGHT = 2.3;
  /** The scale of the ellipsoid of x axis of abdomen. */
  public static final double ABDOMEN_SCALEX = 1;
  /** The scale of the ellipsoid of y axis of abdomen. */
  public static final double ABDOMEN_SCALEY = 1;
  /** The scale of the ellipsoid of z axis of abdomen. */
  public static final double ABDOMEN_SCALEZ = 1.5;
  /** The height of the inner middle joint on each of the foot. */
  public static final double INMIDDLE_JOINT_HEIGHT = 0.5;
  /** The height of the outer middle joint on each of the foot. */
  public static final double OUTMIDDLE_JOINT_HEIGHT = 0.5;
  /** The height of the body joint on each of the foot. */
  public static final double BODY_JOINT_HEIGHT = 0.35;
  
  /**
   * Runs the hand simulation in a single JFrame.
   * 
   * @param args
   *          This parameter is ignored.
   */
  public static void main(final String[] args) {
    new PA2().animator.start();
  }

  /**
   * The animator which controls the framerate at which the canvas is animated.
   */
  final FPSAnimator animator;
  /** The canvas on which we draw the scene. */
  private final GLCanvas canvas;
  /** The capabilities of the canvas. */
  private final GLCapabilities capabilities = new GLCapabilities(null);
  /** The feet on the modeled. */
  private final Foot[] feet;
  /** The eyes on the modeled. */
  private final Eye[] eyes;
  /** The OpenGL utility object. */
  private final GLU glu = new GLU();
  /** The OpenGL utility toolkit object. */
  private final GLUT glut = new GLUT();
  /** The last x and y coordinates of the mouse press. */
  private int last_x = 0, last_y = 0;
  /** Whether the world is being rotated. */
  private boolean rotate_world = false;
  /** The axis around which to rotate the selected joints. */
  private Axis selectedAxis = Axis.X;
  /** The set of components which are currently selected for rotation. */
  private final Set<Component> selectedComponents = new HashSet<Component>(18);
  /** The set of componenteyes which are currently selected for rotation. */
  private final Set<ComponentEye> selectedComponenteyes = new HashSet<ComponentEye>(2);
  /**
   * The set of feet which have been selected for rotation.
   * 
   * Selecting a joint will only affect the joints in this set of selected
   * feet.
   **/
  private final Set<Foot> selectedFeet = new HashSet<Foot>(8);
  /**
   * The set of eyes which have been selected for rotation.
   * 
   * Selecting a joint will only affect the joints in this set of selected
   * eyes.
   **/
  private final Set<Eye> selectedEyes = new HashSet<Eye>(2);
  /** Whether the state of the model has been changed. */
  private boolean stateChanged = true;
  /**
   * The top level component in the scene which controls the positioning and
   * rotation of everything in the scene.
   */
  private final Component topLevelComponent;
  /** The body to be modeled. */
  private final Component body;
  /** The abdomen to be modeled. */
  private final Component abdomen;
  
  /** The quaternion which controls the rotation of the world. */
  private Quaternion viewing_quaternion = new Quaternion();
  /** The set of all components. */
  private final List<Component> components;
  private final List<ComponentEye> componenteyes;
//top level
  public static String TOP_LEVEL_NAME = "top level";

  //right feet
  public static String A_BODY_NAME = "A body";
  public static String A_INMIDDLE_NAME = "A inmiddle";
  public static String A_OUTMIDDLE_NAME = "A outmiddle";
  public static String A_DISTAL_NAME = "A distal";
  public static String B_BODY_NAME = "B body";
  public static String B_INMIDDLE_NAME = "B inmiddle";
  public static String B_OUTMIDDLE_NAME = "B outmiddle";
  public static String B_DISTAL_NAME = "B distal";
  public static String C_BODY_NAME = "C body";
  public static String C_INMIDDLE_NAME = "C inmiddle";
  public static String C_OUTMIDDLE_NAME = "C outmiddle";
  public static String C_DISTAL_NAME = "C distal";
  public static String D_BODY_NAME = "D body";
  public static String D_INMIDDLE_NAME = "D inmiddle";
  public static String D_OUTMIDDLE_NAME = "D outmiddle";
  public static String D_DISTAL_NAME = "D distal";
  //left feet
  public static String a_BODY_NAME = "a body";
  public static String a_INMIDDLE_NAME = "a inmiddle";
  public static String a_OUTMIDDLE_NAME = "a outmiddle";
  public static String a_DISTAL_NAME = "a distal";
  public static String b_BODY_NAME = "b body";
  public static String b_INMIDDLE_NAME = "b inmiddle";
  public static String b_OUTMIDDLE_NAME = "b outmiddle";
  public static String b_DISTAL_NAME = "b distal";
  public static String c_BODY_NAME = "c body";
  public static String c_INMIDDLE_NAME = "c inmiddle";
  public static String c_OUTMIDDLE_NAME = "c outmiddle";
  public static String c_DISTAL_NAME = "c distal";
  public static String d_BODY_NAME = "d body";
  public static String d_INMIDDLE_NAME = "d inmiddle";
  public static String d_OUTMIDDLE_NAME = "d outmiddle";
  public static String d_DISTAL_NAME = "d distal";
  public static String BODY_NAME = "body";
  public static String ABDOMEN_NAME = "abdomen";
  public static String EYE1_NAME = "eye1";
  public static String EYE2_NAME = "eye2";
  public static String EYEBALL1_NAME = "eyeball1";
  public static String EYEBALL2_NAME = "eyeball2";
  /**
   * Initializes the necessary OpenGL objects and adds a canvas to this JFrame.
   */
  public PA2() {
    this.capabilities.setDoubleBuffered(true);

    this.canvas = new GLCanvas(this.capabilities);
    this.canvas.addGLEventListener(this);
    this.canvas.addMouseListener(this);
    this.canvas.addMouseMotionListener(this);
    this.canvas.addKeyListener(this);
    // this is true by default, but we just add this line to be explicit
    this.canvas.setAutoSwapBufferMode(true);
    this.getContentPane().add(this.canvas);

    // refresh the scene at 60 frames per second
    this.animator = new FPSAnimator(this.canvas, 60);

    this.setTitle("CS480/CS680 : Spider Simulator");
    this.setSize(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setVisible(true);

    // all the right distal joints
    final Component distalA = new Component(new Point3D(0, 0,
        OUTMIDDLE_JOINT_HEIGHT), new RoundedCylinder(FOOT_RADIUS,
        DISTAL_JOINT_HEIGHT, this.glut), A_DISTAL_NAME);
    final Component distalB = new Component(new Point3D(0, 0,
        OUTMIDDLE_JOINT_HEIGHT), new RoundedCylinder(FOOT_RADIUS,
        DISTAL_JOINT_HEIGHT, this.glut), B_DISTAL_NAME);
    final Component distalC = new Component(new Point3D(0, 0,
    	OUTMIDDLE_JOINT_HEIGHT), new RoundedCylinder(FOOT_RADIUS,
        DISTAL_JOINT_HEIGHT, this.glut), C_DISTAL_NAME);
    final Component distalD = new Component(new Point3D(0, 0,
    	OUTMIDDLE_JOINT_HEIGHT), new RoundedCylinder(FOOT_RADIUS,
        DISTAL_JOINT_HEIGHT, this.glut), D_DISTAL_NAME);

    // all the left distal joints
    final Component distala = new Component(new Point3D(0, 0,
        OUTMIDDLE_JOINT_HEIGHT), new RoundedCylinder(FOOT_RADIUS,
        DISTAL_JOINT_HEIGHT, this.glut), a_DISTAL_NAME);
    final Component distalb = new Component(new Point3D(0, 0,
        OUTMIDDLE_JOINT_HEIGHT), new RoundedCylinder(FOOT_RADIUS,
        DISTAL_JOINT_HEIGHT, this.glut), b_DISTAL_NAME);
    final Component distalc = new Component(new Point3D(0, 0,
    	OUTMIDDLE_JOINT_HEIGHT), new RoundedCylinder(FOOT_RADIUS,
        DISTAL_JOINT_HEIGHT, this.glut), c_DISTAL_NAME);
    final Component distald = new Component(new Point3D(0, 0,
    	OUTMIDDLE_JOINT_HEIGHT), new RoundedCylinder(FOOT_RADIUS,
        DISTAL_JOINT_HEIGHT, this.glut), d_DISTAL_NAME);

    // all the right outer middle joints
    final Component outmiddleA = new Component(new Point3D(0, 0,
    	INMIDDLE_JOINT_HEIGHT), new RoundedCylinder(FOOT_RADIUS,
        OUTMIDDLE_JOINT_HEIGHT, this.glut), A_OUTMIDDLE_NAME);
    final Component outmiddleB = new Component(new Point3D(0, 0,
    	INMIDDLE_JOINT_HEIGHT), new RoundedCylinder(FOOT_RADIUS,
    	OUTMIDDLE_JOINT_HEIGHT, this.glut), B_OUTMIDDLE_NAME);
    final Component outmiddleC = new Component(new Point3D(0, 0,
    	INMIDDLE_JOINT_HEIGHT), new RoundedCylinder(FOOT_RADIUS,
    	OUTMIDDLE_JOINT_HEIGHT, this.glut), C_OUTMIDDLE_NAME);
    final Component outmiddleD = new Component(new Point3D(0, 0,
    	INMIDDLE_JOINT_HEIGHT), new RoundedCylinder(FOOT_RADIUS,
    	OUTMIDDLE_JOINT_HEIGHT, this.glut), D_OUTMIDDLE_NAME);

    // all the left outer middle joints
    final Component outmiddlea = new Component(new Point3D(0, 0,
    	INMIDDLE_JOINT_HEIGHT), new RoundedCylinder(FOOT_RADIUS,
        OUTMIDDLE_JOINT_HEIGHT, this.glut), a_OUTMIDDLE_NAME);
    final Component outmiddleb = new Component(new Point3D(0, 0,
    	INMIDDLE_JOINT_HEIGHT), new RoundedCylinder(FOOT_RADIUS,
    	OUTMIDDLE_JOINT_HEIGHT, this.glut), b_OUTMIDDLE_NAME);
    final Component outmiddlec = new Component(new Point3D(0, 0,
    	INMIDDLE_JOINT_HEIGHT), new RoundedCylinder(FOOT_RADIUS,
    	OUTMIDDLE_JOINT_HEIGHT, this.glut), c_OUTMIDDLE_NAME);
    final Component outmiddled = new Component(new Point3D(0, 0,
    	INMIDDLE_JOINT_HEIGHT), new RoundedCylinder(FOOT_RADIUS,
    	OUTMIDDLE_JOINT_HEIGHT, this.glut), d_OUTMIDDLE_NAME);
    
    // all the right inner middle joints
    final Component inmiddleA = new Component(new Point3D(0, 0,
    	BODY_JOINT_HEIGHT), new RoundedCylinder(FOOT_RADIUS,
    	INMIDDLE_JOINT_HEIGHT, this.glut), A_INMIDDLE_NAME);
    final Component inmiddleB = new Component(new Point3D(0, 0,
    	BODY_JOINT_HEIGHT), new RoundedCylinder(FOOT_RADIUS,
    	INMIDDLE_JOINT_HEIGHT, this.glut), B_INMIDDLE_NAME);
    final Component inmiddleC = new Component(new Point3D(0, 0,
    	BODY_JOINT_HEIGHT), new RoundedCylinder(FOOT_RADIUS,
    	INMIDDLE_JOINT_HEIGHT, this.glut), C_INMIDDLE_NAME);
    final Component inmiddleD = new Component(new Point3D(0, 0,
    	BODY_JOINT_HEIGHT), new RoundedCylinder(FOOT_RADIUS,
    	INMIDDLE_JOINT_HEIGHT, this.glut), D_INMIDDLE_NAME);
    
    // all the left inner middle joints
    final Component inmiddlea = new Component(new Point3D(0, 0,
    	BODY_JOINT_HEIGHT), new RoundedCylinder(FOOT_RADIUS,
    	INMIDDLE_JOINT_HEIGHT, this.glut), a_INMIDDLE_NAME);
    final Component inmiddleb = new Component(new Point3D(0, 0,
    	BODY_JOINT_HEIGHT), new RoundedCylinder(FOOT_RADIUS,
    	INMIDDLE_JOINT_HEIGHT, this.glut), b_INMIDDLE_NAME);
    final Component inmiddlec = new Component(new Point3D(0, 0,
    	BODY_JOINT_HEIGHT), new RoundedCylinder(FOOT_RADIUS,
    	INMIDDLE_JOINT_HEIGHT, this.glut), c_INMIDDLE_NAME);
    final Component inmiddled = new Component(new Point3D(0, 0,
    	BODY_JOINT_HEIGHT), new RoundedCylinder(FOOT_RADIUS,
    	INMIDDLE_JOINT_HEIGHT, this.glut), d_INMIDDLE_NAME);
   
    // all the right body joints
    final Component bodyA = new Component(new Point3D(-0.25, 0, 1.5),
            new RoundedCylinder(FOOT_RADIUS, BODY_JOINT_HEIGHT, this.glut),
            A_BODY_NAME);
    final Component bodyB = new Component(new Point3D(-0.35, 0, 1.6),
            new RoundedCylinder(FOOT_RADIUS, BODY_JOINT_HEIGHT, this.glut),
            B_BODY_NAME);
    final Component bodyC = new Component(new Point3D(-0.35, 0, 2),
            new RoundedCylinder(FOOT_RADIUS, BODY_JOINT_HEIGHT, this.glut),
            C_BODY_NAME);
    final Component bodyD = new Component(new Point3D(-0.25, 0, 2.1),
            new RoundedCylinder(FOOT_RADIUS, BODY_JOINT_HEIGHT, this.glut),
            D_BODY_NAME);
    
    // all the left body joints
    final Component bodya = new Component(new Point3D(0.25, 0, 1.5),
            new RoundedCylinder(FOOT_RADIUS, BODY_JOINT_HEIGHT, this.glut),
            a_BODY_NAME);
    final Component bodyb = new Component(new Point3D(0.35, 0, 1.6),
            new RoundedCylinder(FOOT_RADIUS, BODY_JOINT_HEIGHT, this.glut),
            b_BODY_NAME);
    final Component bodyc = new Component(new Point3D(0.35, 0, 2),
            new RoundedCylinder(FOOT_RADIUS, BODY_JOINT_HEIGHT, this.glut),
            c_BODY_NAME);
    final Component bodyd = new Component(new Point3D(0.25, 0, 2.1),
            new RoundedCylinder(FOOT_RADIUS, BODY_JOINT_HEIGHT, this.glut),
            d_BODY_NAME);
    
    // put together the feet for easier selection by keyboard input later on
    this.feet = new Foot[] { new Foot(bodyA, inmiddleA, outmiddleA, distalA),
				    		 new Foot(bodyB, inmiddleB, outmiddleB, distalB),
				    		 new Foot(bodyC, inmiddleC, outmiddleC, distalC),
				    		 new Foot(bodyD, inmiddleD, outmiddleD, distalD),
				    		 new Foot(bodya, inmiddlea, outmiddlea, distala),
				    		 new Foot(bodyb, inmiddleb, outmiddleb, distalb),
				    		 new Foot(bodyc, inmiddlec, outmiddlec, distalc),
				    		 new Foot(bodyd, inmiddled, outmiddled, distald)};
    
    // the body which models
    this.body = new Component(new Point3D(0, 0, 0), new Sphere(
        BODY_RADIUS, BODY_HEIGHT, this.glut), BODY_NAME);
    // the abdomen which models
    this.abdomen = new Component(new Point3D(0, 0, BODY_RADIUS), new Ellipsoid(
        ABDOMEN_RADIUS, ABDOMEN_HEIGHT, ABDOMEN_SCALEX, ABDOMEN_SCALEY, ABDOMEN_SCALEZ,this.glut), ABDOMEN_NAME);
    
    // the eye1 which models
    final ComponentEye eye1 = new ComponentEye(new Point3D(0.2, 0, -1.2), new EyeBody(
    		EYE_RADIUS, new Point3D(0.2, 0, -1.2), this.glut), EYE1_NAME);
    // the eye2 which models
    final ComponentEye eye2 = new ComponentEye(new Point3D(-0.2, 0, -1.2), new EyeBody(
        EYE_RADIUS, new Point3D(-0.2, 0, -1.2), this.glut), EYE2_NAME);
   
    // the eyeball1 which models
    final ComponentEye eyeball1 = new ComponentEye(new Point3D(0.2, 0, -1), new EyeBody(
        EYEBALL_RADIUS, new Point3D(0.2, 0, -1), this.glut), EYEBALL1_NAME);
    eyeball1.setColor(FloatColor.BLACK);

    // the eyeball2 which models
    final ComponentEye eyeball2 = new ComponentEye(new Point3D(-0.2, 0, -1), new EyeBody(
        EYEBALL_RADIUS, new Point3D(-0.2, 0, -1), this.glut), EYEBALL2_NAME);
    eyeball2.setColor(FloatColor.BLACK);
    
    // put together the eyes for easier selection by keyboard input later on
    this.eyes = new Eye[] { new Eye(eye1,eyeball1),
			new Eye(eye2,eyeball2)};

    // the top level component which provides an initial position and rotation
    // to the scene (but does not cause anything to be drawn)
    this.topLevelComponent = new Component(INITIAL_POSITION, TOP_LEVEL_NAME);

    this.topLevelComponent.addChild(this.body);
    this.body.addChildren(bodyA, bodyB, bodyC, bodyD, bodya, bodyb, bodyc, bodyd, abdomen);
    bodyA.addChild(inmiddleA);
    bodyB.addChild(inmiddleB);
    bodyC.addChild(inmiddleC);
    bodyD.addChild(inmiddleD);
    bodya.addChild(inmiddlea);
    bodyb.addChild(inmiddleb);
    bodyc.addChild(inmiddlec);
    bodyd.addChild(inmiddled);
    inmiddleA.addChild(outmiddleA);
    inmiddleB.addChild(outmiddleB);
    inmiddleC.addChild(outmiddleC);
    inmiddleD.addChild(outmiddleD);
    inmiddlea.addChild(outmiddlea);
    inmiddleb.addChild(outmiddleb);
    inmiddlec.addChild(outmiddlec);
    inmiddled.addChild(outmiddled);
    outmiddleA.addChild(distalA);
    outmiddleB.addChild(distalB);
    outmiddleC.addChild(distalC);
    outmiddleD.addChild(distalD);
    outmiddlea.addChild(distala);
    outmiddleb.addChild(distalb);
    outmiddlec.addChild(distalc);
    outmiddled.addChild(distald);
    eye1.addChild(eyeball1);
    eye2.addChild(eyeball2);
    
    // turn the whole spider to be at an spider-like angle
    this.topLevelComponent.rotate(Axis.Y, 180);
    this.topLevelComponent.rotate(Axis.X, 30);
    this.topLevelComponent.rotate(Axis.Z, 0);
    
    // rotate the body joint initially
    bodyA.rotate(Axis.Y, -130);
    bodya.rotate(Axis.Y, 130);
    bodyB.rotate(Axis.Y, -85);
    bodyb.rotate(Axis.Y, 85);
    bodyC.rotate(Axis.Y, -85);
    bodyc.rotate(Axis.Y, 85);
    bodyD.rotate(Axis.Y, -50);
    bodyd.rotate(Axis.Y, 50);
    
    // rotate the inner middle joint initially
    inmiddleA.rotate(Axis.Y, -10);
    inmiddlea.rotate(Axis.Y, 10);
    inmiddleB.rotate(Axis.Y, -55);
    inmiddleb.rotate(Axis.Y, 55);
    inmiddleC.rotate(Axis.Y, -15);
    inmiddlec.rotate(Axis.Y, 15);
    inmiddleD.rotate(Axis.Y, 10);
    inmiddled.rotate(Axis.Y, -10);
    
    inmiddleA.rotate(Axis.X, 15);
    inmiddlea.rotate(Axis.X, 15);
    inmiddleB.rotate(Axis.X, 15);
    inmiddleb.rotate(Axis.X, 15);
    inmiddleC.rotate(Axis.X, 15);
    inmiddlec.rotate(Axis.X, 15);
    inmiddleD.rotate(Axis.X, 15);
    inmiddled.rotate(Axis.X, 15);
    
    // rotate the outer middle joint initially
    outmiddleA.rotate(Axis.Y, -10);
    outmiddlea.rotate(Axis.Y, 10);
    outmiddleB.rotate(Axis.Y, -10);
    outmiddleb.rotate(Axis.Y, 10);
    outmiddleC.rotate(Axis.Y, -30);
    outmiddlec.rotate(Axis.Y, 30);
    outmiddleD.rotate(Axis.Y, 10);
    outmiddled.rotate(Axis.Y, -10);
    
    outmiddleA.rotate(Axis.X, 15);
    outmiddlea.rotate(Axis.X, 15);
    outmiddleB.rotate(Axis.X, 15);
    outmiddleb.rotate(Axis.X, 15);
    outmiddleC.rotate(Axis.X, 15);
    outmiddlec.rotate(Axis.X, 15);
    outmiddleD.rotate(Axis.X, 15);
    outmiddled.rotate(Axis.X, 15);
    
    
    // set rotation limits for the body joints of the feet
    for (final Component bodyJoint : Arrays.asList(bodyA, bodyB, bodyC, bodyD, bodya, bodyb, bodyc, bodyd )) {
      bodyJoint.setXPositiveExtent(bodyJoint.xAngle()+30);
      bodyJoint.setXNegativeExtent(bodyJoint.xAngle()-30);
      bodyJoint.setYPositiveExtent(bodyJoint.yAngle()+30);
      bodyJoint.setYNegativeExtent(bodyJoint.yAngle()-30);
      bodyJoint.setZPositiveExtent(0);
      bodyJoint.setZNegativeExtent(0);
    }
    
    // set rotation limits for the inner middle joints of the feet
    for (final Component inmiddleJoint : Arrays.asList(inmiddleA, inmiddleB, inmiddleC, inmiddleD, inmiddlea, inmiddleb, inmiddlec, inmiddled )) {
    	inmiddleJoint.setXPositiveExtent(inmiddleJoint.xAngle()+30);
    	inmiddleJoint.setXNegativeExtent(inmiddleJoint.xAngle()-30);
    	inmiddleJoint.setYPositiveExtent(inmiddleJoint.yAngle()+30);
    	inmiddleJoint.setYNegativeExtent(inmiddleJoint.yAngle()-30);
    	inmiddleJoint.setZPositiveExtent(0);
    	inmiddleJoint.setZNegativeExtent(0);
    }
    
    // set rotation limits for the outer middle joints of the feet
    for (final Component outmiddleJoint : Arrays.asList(outmiddleA, outmiddleB, outmiddleC, outmiddleD, outmiddlea, outmiddleb, outmiddlec, outmiddled )) {
    	outmiddleJoint.setXPositiveExtent(outmiddleJoint.xAngle()+30);
    	outmiddleJoint.setXNegativeExtent(outmiddleJoint.xAngle()-30);
    	outmiddleJoint.setYPositiveExtent(outmiddleJoint.yAngle()+30);
    	outmiddleJoint.setYNegativeExtent(outmiddleJoint.yAngle()-30);
    	outmiddleJoint.setZPositiveExtent(0);
    	outmiddleJoint.setZNegativeExtent(0);
    }
    
    // set rotation limits for the distal joints of the feet
    for (final Component distalJoint : Arrays.asList(distalA, distalB, distalC, distalD, distala, distalb, distalc, distald )) {
    	distalJoint.setXPositiveExtent(distalJoint.xAngle()+30);
    	distalJoint.setXNegativeExtent(distalJoint.xAngle()-30);
    	distalJoint.setYPositiveExtent(distalJoint.yAngle()+30);
    	distalJoint.setYNegativeExtent(distalJoint.yAngle()-30);
    	distalJoint.setZPositiveExtent(0);
    	distalJoint.setZNegativeExtent(0);
    }
    
    // set rotation limits for the distal joints of the feet
    for (final ComponentEye eye : Arrays.asList(eye1, eye2 )) {
    	eye.setXPositiveExtent(eye.xAngle()+80);
    	eye.setXNegativeExtent(eye.xAngle()-80);
    	eye.setYPositiveExtent(eye.yAngle()+80);
    	eye.setYNegativeExtent(eye.yAngle()-80);
    	eye.setZPositiveExtent(0);
    	eye.setZNegativeExtent(0);
    }

    // create the list of all the components for debugging purposes
    this.components = Arrays.asList(bodyA, inmiddleA, outmiddleA, distalA,
    		bodyB, inmiddleB, outmiddleB, distalB,
    		bodyC, inmiddleC, outmiddleC, distalC,
    		bodyD, inmiddleA, outmiddleA, distalD,
    		bodya, inmiddlea, outmiddlea, distala,
    		bodyb, inmiddleb, outmiddleb, distalb,
    		bodyc, inmiddlec, outmiddlec, distalc,
    		bodyd, inmiddled, outmiddled, distald,
    		this.body, this.abdomen);    
    
    // create the list of all the componenteyes for debugging purposes
    this.componenteyes = Arrays.asList(eye1,eyeball1,eye2,eyeball2);    
  }

  /**
   * Redisplays the scene containing the spider model.
   * 
   * @param drawable
   *          The OpenGL drawable object with which to create OpenGL models.
   */

  public void display(final GLAutoDrawable drawable) {
    final GL2 gl = (GL2)drawable.getGL();

    // clear the display
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    // from here on affect the model view
    gl.glMatrixMode(GL2.GL_MODELVIEW);

    // start with the identity matrix initially
    gl.glLoadIdentity();

    // rotate the world by the appropriate rotation quaternion
    gl.glMultMatrixf(this.viewing_quaternion.toMatrix(), 0);
    
    // update the position of the components which need to be updated
    // TODO only need to update the selected and JUST deselected components
    if (this.stateChanged) {
      this.topLevelComponent.update(gl);
      this.eyes[0].eye.update(gl);
      this.eyes[1].eye.update(gl);
      this.stateChanged = false;
    }

    // redraw the components and componenteyes
    this.topLevelComponent.draw(gl);
    this.eyes[0].eye.draw(gl);
    this.eyes[1].eye.draw(gl);
    
  }

  /**
   * Initializes the scene and model.
   * 
   * @param drawable
   *          {@inheritDoc}
   */
  public void init(final GLAutoDrawable drawable) {
    final GL2 gl = (GL2)drawable.getGL();

    // perform any initialization needed by the spider model
    this.topLevelComponent.initialize(gl);
    this.eyes[0].eye.initialize(gl);
    this.eyes[1].eye.initialize(gl);

    // initially draw the scene
    this.topLevelComponent.update(gl);
    this.eyes[0].eye.update(gl);
    this.eyes[1].eye.update(gl);

    // set up for shaded display of the spider
    final float light0_position[] = { 1, 1, 1, 0 };
    final float light0_ambient_color[] = { 0.25f, 0.25f, 0.25f, 1 };
    final float light0_diffuse_color[] = { 1, 1, 1, 1 };

    gl.glPolygonMode(GL.GL_FRONT, GL2.GL_FILL);
    gl.glEnable(GL2.GL_COLOR_MATERIAL);
    gl.glColorMaterial(GL.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE);

    gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    gl.glShadeModel(GL2.GL_SMOOTH);

    // set up the light source
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light0_position, 0);
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, light0_ambient_color, 0);
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, light0_diffuse_color, 0);

    // turn lighting and depth buffering on
    gl.glEnable(GL2.GL_LIGHTING);
    gl.glEnable(GL2.GL_LIGHT0);
    gl.glEnable(GL2.GL_DEPTH_TEST);
    gl.glEnable(GL2.GL_NORMALIZE);
  }

  /**
   * Interprets key presses according to the following scheme:
   * 
   * up-arrow, down-arrow: increase/decrease rotation angle
   * 
   * @param key
   *          The key press event object.
   */
  public void keyPressed(final KeyEvent key) {
    switch (key.getKeyCode()) {
    case KeyEvent.VK_KP_UP:
    case KeyEvent.VK_UP:
      for (final Component component : this.selectedComponents) {
        component.rotate(this.selectedAxis, ROTATION_ANGLE);
      }
      for (final ComponentEye componenteye : this.selectedComponenteyes) {
          componenteye.rotate(this.selectedAxis, ROTATION_ANGLE);
        }
      this.stateChanged = true;
      break;
    case KeyEvent.VK_KP_DOWN:
    case KeyEvent.VK_DOWN:
        for (final ComponentEye componenteye : this.selectedComponenteyes) {
            componenteye.rotate(this.selectedAxis, -ROTATION_ANGLE);
          }
      for (final Component component : this.selectedComponents) {
        component.rotate(this.selectedAxis, -ROTATION_ANGLE);
      }
      this.stateChanged = true;
      break;
    default:
      break;
    }
  }
  
  public void keyReleased(final KeyEvent key) {
	    // intentionally unimplemented
	  }
  
  private final TestCases testCases = new TestCases();

  private void setModelState(final Map<String, Angled> state) {
	    this.abdomen.setAngles(state.get(ABDOMEN_NAME));
	    this.body.setAngles(state.get(BODY_NAME));
	    this.feet[0].bodyJoint().setAngles(state.get(A_BODY_NAME));
	    this.feet[0].inmiddleJoint().setAngles(state.get(A_INMIDDLE_NAME));
	    this.feet[0].outmiddleJoint().setAngles(state.get(A_OUTMIDDLE_NAME));
	    this.feet[0].distalJoint().setAngles(state.get(A_DISTAL_NAME));

	    this.feet[1].bodyJoint().setAngles(state.get(B_BODY_NAME));
	    this.feet[1].inmiddleJoint().setAngles(state.get(B_INMIDDLE_NAME));
	    this.feet[1].outmiddleJoint().setAngles(state.get(B_OUTMIDDLE_NAME));
	    this.feet[1].distalJoint().setAngles(state.get(B_DISTAL_NAME));

	    this.feet[2].bodyJoint().setAngles(state.get(C_BODY_NAME));
	    this.feet[2].inmiddleJoint().setAngles(state.get(C_INMIDDLE_NAME));
	    this.feet[2].outmiddleJoint().setAngles(state.get(C_OUTMIDDLE_NAME));
	    this.feet[2].distalJoint().setAngles(state.get(C_DISTAL_NAME));
	    
	    this.feet[3].bodyJoint().setAngles(state.get(D_BODY_NAME));
	    this.feet[3].inmiddleJoint().setAngles(state.get(D_INMIDDLE_NAME));
	    this.feet[3].outmiddleJoint().setAngles(state.get(D_OUTMIDDLE_NAME));
	    this.feet[3].distalJoint().setAngles(state.get(D_DISTAL_NAME));
	    
	    this.feet[4].bodyJoint().setAngles(state.get(a_BODY_NAME));
	    this.feet[4].inmiddleJoint().setAngles(state.get(a_INMIDDLE_NAME));
	    this.feet[4].outmiddleJoint().setAngles(state.get(a_OUTMIDDLE_NAME));
	    this.feet[4].distalJoint().setAngles(state.get(a_DISTAL_NAME));
	    
	    this.feet[5].bodyJoint().setAngles(state.get(b_BODY_NAME));
	    this.feet[5].inmiddleJoint().setAngles(state.get(b_INMIDDLE_NAME));
	    this.feet[5].outmiddleJoint().setAngles(state.get(b_OUTMIDDLE_NAME));
	    this.feet[5].distalJoint().setAngles(state.get(b_DISTAL_NAME));
	    
	    this.feet[6].bodyJoint().setAngles(state.get(c_BODY_NAME));
	    this.feet[6].inmiddleJoint().setAngles(state.get(c_INMIDDLE_NAME));
	    this.feet[6].outmiddleJoint().setAngles(state.get(c_OUTMIDDLE_NAME));
	    this.feet[6].distalJoint().setAngles(state.get(c_DISTAL_NAME));
	    
	    this.feet[7].bodyJoint().setAngles(state.get(d_BODY_NAME));
	    this.feet[7].inmiddleJoint().setAngles(state.get(d_INMIDDLE_NAME));
	    this.feet[7].outmiddleJoint().setAngles(state.get(d_OUTMIDDLE_NAME));
	    this.feet[7].distalJoint().setAngles(state.get(d_DISTAL_NAME));
	    
	    this.stateChanged = true;
	  }
  
  /**
   * Interprets typed keys according to the following scheme:
   * 
   * 1 : toggle the right A feet active in rotation
   * 
   * 2 : toggle the right B feet active in rotation
   * 
   * 3 : toggle the right C feet active in rotation
   * 
   * 4 : toggle the right D feet active in rotation
   * 
   * 5 : toggle the left A feet active in rotation
   * 
   * 6 : toggle the left B feet active in rotation
   * 
   * 7 : toggle the left C feet active in rotation
   * 
   * 8 : toggle the left D feet active in rotation
   * 
   * X : use the X axis rotation at the active joint(s)
   * 
   * Y : use the Y axis rotation at the active joint(s)
   * 
   * Z : use the Z axis rotation at the active joint(s)
   * 
   * I : resets the spider to the stop sign
   * 
   * A : select body joint
   * 
   * B : select inner middle joint
   * 
   * C : select outer middle joint
   * 
   * D : select distal joint
   * 
   * R : resets the view to the initial rotation
   * 
   * K : prints the angles of the spider for debugging purposes
   * 
   * Q, Esc : exits the program
   * 
   */
  public void keyTyped(final KeyEvent key) {
    switch (key.getKeyChar()) {
    case 'Q':
    case 'q':
    case KeyEvent.VK_ESCAPE:
    //case KeyEvent.VK_UP:
      new Thread() {
        @Override
        public void run() {
          PA2.this.animator.stop();
        }
      }.start();
      System.exit(0);
      break;

    // print the angles of the components
    case 'K':
    case 'k':
      printJoints();
      break;

    // resets to the stop sign
    case 'I':
    case 'i':
      this.setModelState(this.testCases.stop());
      break;
    // set the state of the spider to the next test case
    case 'T':
    case 't':
      this.setModelState(this.testCases.next());
      break;
    // set the viewing quaternion to 0 rotation
    case 'R':
    case 'r':
      this.viewing_quaternion.reset();
      break;
      // Toggle which feet are affected by the current rotation
      case '1':
        toggleSelection(this.feet[0]);
        break;
      case '2':
        toggleSelection(this.feet[1]);
        break;
      case '3':
        toggleSelection(this.feet[2]);
        break;
      case '4':
        toggleSelection(this.feet[3]);
        break;
      case '5':
        toggleSelection(this.feet[4]);
        break;
      case '6':
          toggleSelection(this.feet[5]);
          break;
      case '7':
          toggleSelection(this.feet[6]);
          break;
      case '8':
          toggleSelection(this.feet[7]);
          break;
      // toggle which joints are affected by the current rotation
      case 'A':
      case 'a':
        for (final Foot foot : this.selectedFeet) {
          toggleSelection(foot.bodyJoint());
        }
        break;
      case 'B':
      case 'b':
        for (final Foot foot : this.selectedFeet) {
          toggleSelection(foot.inmiddleJoint());
        }
        break;
      case 'C':
      case 'c':
        for (final Foot foot : this.selectedFeet) {
          toggleSelection(foot.outmiddleJoint());
        }
        break;
      case 'D':
      case 'd':
        for (final Foot foot : this.selectedFeet) {
          toggleSelection(foot.distalJoint());
        }
        break;
        
      case 'E':
      case 'e':
    	  toggleSelection(this.eyes[0]);
    	  toggleSelection(this.eyes[1]);
    	  for (final Eye eye : this.selectedEyes) {
              toggleSelection(eye.eye());
            }
     
          break;
    // change the axis of rotation at current active joint
    case 'X':
    case 'x':
      this.selectedAxis = Axis.X;
      break;
    case 'Y':
    case 'y':
      this.selectedAxis = Axis.Y;
      break;
    case 'Z':
    case 'z':
      this.selectedAxis = Axis.Z;
      break;
    default:
      break;
    }
  }

  /**
   * Prints the joints on the System.out print stream.
   */
  private void printJoints() {
    this.printJoints(System.out);
  }

  /**
   * Prints the joints on the specified PrintStream.
   * 
   * @param printStream
   *          The stream on which to print each of the components.
   */
  private void printJoints(final PrintStream printStream) {
    for (final Component component : this.components) {
      printStream.println(component);
    }
  }
  
  /**
   * Updates the rotation quaternion as the mouse is dragged.
   * 
   * @param mouse
   *          The mouse drag event object.
   */
  public void mouseDragged(final MouseEvent mouse) {
	if (this.rotate_world) {
		// get the current position of the mouse
		final int x = mouse.getX();
		final int y = mouse.getY();
	
		// get the change in position from the previous one
		final int dx = x - this.last_x;
		final int dy = y - this.last_y;
	
		// create a unit vector in the direction of the vector (dy, dx, 0)
		final double magnitude = Math.sqrt(dx * dx + dy * dy);
		final float[] axis = magnitude == 0 ? new float[]{1,0,0}: // avoid dividing by 0
			new float[] { (float) (dy / magnitude),(float) (dx / magnitude), 0 };
	
		// calculate appropriate quaternion
		final float viewing_delta = 3.1415927f / 180.0f;
		final float s = (float) Math.sin(0.5f * viewing_delta);
		final float c = (float) Math.cos(0.5f * viewing_delta);
		final Quaternion Q = new Quaternion(c, s * axis[0], s * axis[1], s
				* axis[2]);
		this.viewing_quaternion = Q.multiply(this.viewing_quaternion);
	
		// normalize to counteract acccumulating round-off error
		this.viewing_quaternion.normalize();
	
		// save x, y as last x, y
		this.last_x = x;
		this.last_y = y;
	}
  }
  
  /**
   * Move eyes with the move of mouse
   * 
   * @param mouse
   *          The mouse drag event object.
   */
  double lastx=0,lasty=0; // last time the x y value of the mouse
  public void mouseMoved(MouseEvent mouse) {
	  double x = mouse.getX(), y = mouse.getY();
	  if(x>lastx) {
	  for (final ComponentEye component : this.selectedComponenteyes) {
	        component.rotate(Axis.Y, ROTATION_ANGLE);
	      }
	      this.stateChanged = true;

	  }
	  if(x<lastx) {
	  for (final ComponentEye component : this.selectedComponenteyes) {
	        component.rotate(Axis.Y, -ROTATION_ANGLE);
	      }
	      this.stateChanged = true;

	  }
	  if(lasty > y) {
	  for (final ComponentEye component : this.selectedComponenteyes) {
	        component.rotate(Axis.X, -ROTATION_ANGLE);
	      }
	      this.stateChanged = true;
	  }
	  if(lasty < y) {
	  for (final ComponentEye component : this.selectedComponenteyes) {
	        component.rotate(Axis.X, ROTATION_ANGLE);
	      }
	      this.stateChanged = true;
	  }
      lastx = x;
      lasty = y;
  }

  /**
   * Starts rotating the world if the left mouse button was released.
   * 
   * @param mouse
   *          The mouse press event object.
   */
  public void mousePressed(final MouseEvent mouse) {
    if (mouse.getButton() == MouseEvent.BUTTON1) {
      this.last_x = mouse.getX();
      this.last_y = mouse.getY();
      this.rotate_world = true;
    }
  }
  
  /**
   * Stops rotating the world if the left mouse button was released.
   * 
   * @param mouse
   *          The mouse release event object.
   */
  public void mouseReleased(final MouseEvent mouse) {
    if (mouse.getButton() == MouseEvent.BUTTON1) {
      this.rotate_world = false;
    }
  }

  public void mouseExited(MouseEvent mouse) {
	    // intentionally unimplemented
	  }
  
  public void mouseEntered(MouseEvent mouse) {
	    // intentionally unimplemented
	  }
  
  public void mouseClicked(MouseEvent mouse) {
	    // intentionally unimplemented
	  }
  
  /**
   * {@inheritDoc}
   * 
   * @param drawable
   *          {@inheritDoc}
   * @param x
   *          {@inheritDoc}
   * @param y
   *          {@inheritDoc}
   * @param width
   *          {@inheritDoc}
   * @param height
   *          {@inheritDoc}
   */
  public void reshape(final GLAutoDrawable drawable, final int x, final int y,
      final int width, final int height) {
    final GL2 gl = (GL2)drawable.getGL();

    // prevent division by zero by ensuring window has height 1 at least
    final int newHeight = Math.max(1, height);

    // compute the aspect ratio
    final double ratio = (double) width / newHeight;

    // reset the projection coordinate system before modifying it
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glLoadIdentity();

    // set the viewport to be the entire window
    gl.glViewport(0, 0, width, newHeight);

    // set the clipping volume
    this.glu.gluPerspective(25, ratio, 0.1, 100);

    // camera positioned at (0,0,6), look at point (0,0,0), up vector (0,1,0)
    this.glu.gluLookAt(0, 0, 12, 0, 0, 0, 0, 1, 0);

    // switch back to model coordinate system
    gl.glMatrixMode(GL2.GL_MODELVIEW);
  }

  private void toggleSelection(final Component component) {
    if (this.selectedComponents.contains(component)) {
      this.selectedComponents.remove(component);
      component.setColor(INACTIVE_COLOR);
    } else {
      this.selectedComponents.add(component);
      component.setColor(ACTIVE_COLOR);
    }
    this.stateChanged = true;
  }
  
  private void toggleSelection(final ComponentEye component) {
	    if (this.selectedComponenteyes.contains(component)) {
	      this.selectedComponenteyes.remove(component);
	      component.setColor(INACTIVE_COLOR);
	    } else {
	      this.selectedComponenteyes.add(component);
	      component.setColor(ACTIVE_COLOR);
	    }
	    this.stateChanged = true;
	  }

  private void toggleSelection(final Foot foot) {
	    if (this.selectedFeet.contains(foot)) {
	      this.selectedFeet.remove(foot);
	      this.selectedComponents.removeAll(foot.joints());
	      for (final Component joint : foot.joints()) {
	        joint.setColor(INACTIVE_COLOR);
	      }
	    } else {
	      this.selectedFeet.add(foot);
	    }
	    this.stateChanged = true;
	  }
  
  private void toggleSelection(final Eye eye) {
	    if (this.selectedEyes.contains(eye)) {
	      this.selectedEyes.remove(eye);
	      this.selectedComponenteyes.removeAll(eye.parts());
	        eye.eye.setColor(EYE_INACTIVE_COLOR);
	        eye.eyeball.setColor(FloatColor.BLACK);
	    } else {
	      this.selectedEyes.add(eye);
	    }
	    this.stateChanged = true;
	  }

@Override
public void dispose(GLAutoDrawable drawable) {
	// TODO Auto-generated method stub
	
}
}
