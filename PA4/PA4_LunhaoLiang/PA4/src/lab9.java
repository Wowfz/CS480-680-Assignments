//****************************************************************************
//       Example Main Program for CS680 PA4
// Author: Lunhao Liang
//****************************************************************************
// Description: 
//   
//   This is a template program for the sketching tool.  
//
//     LEFTMOUSE: draw line segments 
//     RIGHTMOUSE: draw triangles 
//
//     The following keys control the program:
//
//case'1': amb
//		case'2': point
//		case'3': infinit
//
//		case'L':
//		case'l': light
//
//		case 'O' :
//		case 'o' : System.exit(0);
//
//		case 'R' :
//		case 'r' : color = new ColorType(rng.nextFloat(),rng.nextFloat(),rng.nextFloat());
//
//		case 'H' :
//		case 'h' :clearPixelBuffer();
//
//		case 'S' :
//		case 's' :toggle_ks
//
//		case 'A' :
//		case 'a' :toggle_ka
//
//		case 'D' :
//		case 'd' :toggle_kd
//
//		case 'P' :
//		case 'p' : phong
//
//		case 'G' :
//		case 'g' : gouraud
//
//		case 'F' :
//		case 'f' : flat
//
//		case 'Z':object_translation_x+=5;
//		case 'z':object_translation_x-=5;
//
//		case 'X':object_translation_y+=5;
//		case 'x':object_translation_y-=5;
//
//		case 'C':object_translation_z+=5;
//		case 'c':object_translation_z-=5;
//
//
//		case 'V':camera_translation_x+=5;
//		case 'v':camera_translation_x-=5;
//
//		case 'B':camera_translation_y+=5;
//		case 'b':camera_translation_y-=5;
//
//		case 'N':camera_translation_z+=5;
//		case 'n':camera_translation_z-=5;
//
//		case ',' :radius -= 1f;
//		case '.' :radius += 1f;
//
//		case 'Q' :
//		case 'q' :rotation_angle = 0;
//		axis[0] = 1f;
//		axis[1] = 0f;
//		axis[2] = 0f;
//
//		case 'W' :
//		case 'w' :rotation_angle = 0;
//		axis[0] = 0f;
//		axis[1] = 1f;
//		axis[2] = 0f;
//
//		case 'E' :
//		case 'e' :rotation_angle = 0;
//		axis[0] = 0f;
//		axis[1] = 0f;
//		axis[2] = 1f;
//
//		case 'M' :rotation_angle+=1;
//		case 'm' :rotation_angle-=1;
//
//		case 'T' :
//		case 't' : testCase = (testCase+1)%numTestCase;
//
//		case '<':  Nsteps = Nsteps < 4 ? Nsteps: Nsteps / 2;
//		System.out.printf( "Nsteps = %d \n", Nsteps);
//		drawTestCase();
//		break;
//		case '>':Nsteps = Nsteps > 190 ? Nsteps: Nsteps * 2;
//		System.out.printf( "Nsteps = %d \n", Nsteps);
//		drawTestCase();
//		break;
//		case '+':
//		ns++;
//		case '_':
//		if(ns>0)
//		ns--;
//		case ')':
//		oppset_ks+=0.05;
//
//		case '0':
//		oppset_ks-=0.05;
//
//		case '(':
//		oppset_kd+=0.05;
//
//		case '9':
//		oppset_kd-=0.05;
//
//		case '*':
//		oppset_ka+=0.05;
//
//		case '8':
//		oppset_ka-=0.05;
//
//****************************************************************************
// History :
//   Aug 2004 Created by Jianming Zhang based on the C
//   code by Stan Sclaroff
//  Nov 2014 modified to include test cases for shading example for PA4
//


import javax.swing.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.*; 
import java.awt.image.*;
//import java.io.File;
//import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

//import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;//for new version of gl
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;

import com.jogamp.opengl.util.FPSAnimator;//for new version of gl


public class lab9 extends JFrame
	implements GLEventListener, KeyListener, MouseListener, MouseMotionListener
{
	
	private static final long serialVersionUID = 1L;
	private final int DEFAULT_WINDOW_WIDTH=512;
	private final int DEFAULT_WINDOW_HEIGHT=512;
	public float[][] depthbuffer= new float[DEFAULT_WINDOW_WIDTH][DEFAULT_WINDOW_HEIGHT];
	private final float DEFAULT_LINE_WIDTH=1.0f;

	private GLCapabilities capabilities;
	private GLCanvas canvas;
	private FPSAnimator animator;

	final private int numTestCase;
	private int testCase;
	private BufferedImage buff;
	@SuppressWarnings("unused")
	private ColorType color;
	private Random rng;
	
	 // specular exponent for materials
	private int ns=5; 
	
	private ArrayList<Point2D_depth> lineSegs;
	private ArrayList<Point2D_depth> triangles;
	private boolean doSmoothShading;
	private int Nsteps;

	/** The quaternion which controls the rotation of the world. */
    private Quaternion viewing_quaternion = new Quaternion();
    private Vector3D viewing_center = new Vector3D((float)(DEFAULT_WINDOW_WIDTH/2),(float)(DEFAULT_WINDOW_HEIGHT/2),(float)0.0);
    /** The last x and y coordinates of the mouse press. */
    private int last_x = 0, last_y = 0;
    /** Whether the world is being rotated. */
    private boolean rotate_world = false;
    
	public lab9()
	{

	    capabilities = new GLCapabilities(null);
	    capabilities.setDoubleBuffered(true);  // Enable Double buffering

	    canvas  = new GLCanvas(capabilities);
	    canvas.addGLEventListener(this);
	    canvas.addMouseListener(this);
	    canvas.addMouseMotionListener(this);
	    canvas.addKeyListener(this);
	    canvas.setAutoSwapBufferMode(true); // true by default. Just to be explicit
	    canvas.setFocusable(true);
	    getContentPane().add(canvas);

	    animator = new FPSAnimator(canvas, 60); // drive the display loop @ 60 FPS

	    numTestCase = 3;
	    testCase = 1;
	    Nsteps = 12;

	    setTitle("CS480/680 Lab 11");
	    setSize( DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setVisible(true);
	    setResizable(false);
	    
	    rng = new Random();
	    color = new ColorType(1.0f,0.0f,0.0f);
	    lineSegs = new ArrayList<Point2D_depth>();
	    triangles = new ArrayList<Point2D_depth>();
	    doSmoothShading = false;
	}

	public void run()
	{
		animator.start();
	}

	public static void main( String[] args )
	{
	    lab9 P = new lab9();
	    P.run();
	}

	//*********************************************** 
	//  GLEventListener Interfaces
	//*********************************************** 
	public void init( GLAutoDrawable drawable) 
	{
	    GL gl = drawable.getGL();
	    gl.glClearColor( 0.0f, 0.0f, 0.0f, 0.0f);
	    gl.glLineWidth( DEFAULT_LINE_WIDTH );
	    Dimension sz = this.getContentPane().getSize();
	    buff = new BufferedImage(sz.width,sz.height,BufferedImage.TYPE_3BYTE_BGR);
	    clearPixelBuffer();
	}

	float a = 0;

	// Redisplaying graphics
	public void display(GLAutoDrawable drawable)
	{
		GL2 gl = drawable.getGL().getGL2();
	    WritableRaster wr = buff.getRaster();
	    DataBufferByte dbb = (DataBufferByte) wr.getDataBuffer();
	    byte[] data = dbb.getData();

	    gl.glPixelStorei(GL2.GL_UNPACK_ALIGNMENT, 1);
	    gl.glDrawPixels (buff.getWidth(), buff.getHeight(),
                GL2.GL_BGR, GL2.GL_UNSIGNED_BYTE,
                ByteBuffer.wrap(data));

		gl.glTranslated(a, 0, 0);
        drawTestCase();

	}

	// Window size change
	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h)
	{
		// deliberately left blank
	}
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
	      boolean deviceChanged)
	{
		// deliberately left blank
	}
	
	void clearPixelBuffer()
	{
		lineSegs.clear();
    	triangles.clear();
		Graphics2D g = buff.createGraphics();
	    g.setColor(Color.BLACK);
	    g.fillRect(0, 0, buff.getWidth(), buff.getHeight());
	    g.dispose();
	}
	
	// drawTest
	void drawTestCase()
	{  
		/* clear the window and vertex state */
		clearPixelBuffer();
	  
		//System.out.printf("Test case = %d\n",testCase);

		switch (testCase){
		case 0:
			shadeTest(true,false); /* smooth shaded, sphere and torus */
			break;
		case 1:
			shadeTest(false,false); /* flat shaded, sphere and torus */
			break;

			case 2:
				shadeTest(false,true);
		}	
	}

	public boolean light = true, amb = true, point = true, infinit = true;
	public boolean toggle_ka = true, toggle_kd = true, toggle_ks = true;
	public boolean phong;
	public float object_translation_x = 0,object_translation_y = 0,object_translation_z = 0;
	public float camera_translation_x = 0,camera_translation_y = 0,camera_translation_z = 0;
	public float rotation_angle = 0;
	public float oppset_ka = 0;
	public float oppset_kd = 0;
	public float oppset_ks = 0;
	public float oppset_ka_r=0, oppset_ka_g=0, oppset_ka_b=0;
	public float oppset_kd_r=0, oppset_kd_g=0, oppset_kd_b=0;
	public float oppset_ks_r=0, oppset_ks_g=0, oppset_ks_b=0;
	public float[] axis = new float[]{1,0,0};

	float radius = (float)50.0;
	public Boolean radial = true, angular = true;

	//*********************************************** 
	//          KeyListener Interfaces
	//*********************************************** 
	public void keyTyped(KeyEvent key)
	{
	//      Q,q: quit 
	//      C,c: clear polygon (set vertex count=0)
	//		R,r: randomly change the color
	//		S,s: toggle the smooth shading
	//		T,t: show testing examples (toggles between smooth shading and flat shading test cases)
	//		>:	 increase the step number for examples
	//		<:   decrease the step number for examples
	//     +,-:  increase or decrease spectral exponent

	    switch ( key.getKeyChar() ) 
	    {

			case'1':
			{
				if(amb == true)
					amb = false;
				else
					amb = true;
			}
			break;

			case'2':
			{
				if(point == true)
					point = false;
				else
					point = true;
			}
			break;

			case'3':
			{
				if(infinit == true)
					infinit = false;
				else
					infinit = true;
			}
			break;

			case'L':
			case'l':
			{
				if(light == true)
					light = false;
				else
					light = true;
			}
			break;

	    case 'O' :
	    case 'o' :
	    	new Thread()
	    	{
	          	public void run() { animator.stop(); }
	        }.start();
	        System.exit(0);
	        break;
	    case 'R' :
	    case 'r' :
	    	color = new ColorType(rng.nextFloat(),rng.nextFloat(),
	    			rng.nextFloat());
	    	break;
	    case 'H' :
	    case 'h' :
	    	clearPixelBuffer();
	    	break;

	    case 'Y' :
		case 'y' :

			//final Quaternion Q = new Quaternion(0, 1 * viewing_quaternion.get_v().x + 1, 1 * viewing_quaternion.get_v().y, 1 * viewing_quaternion.get_v().z);
			//this.viewing_quaternion = Q.multiply(this.viewing_quaternion);
			//viewing_quaternion.
			// normalize to counteract acccumulating round-off error
			//this.viewing_quaternion.normalize();
			//drawTestCase();
			break;

	    case 'S' :
	    case 's' :
	    	toggle_ks = !toggle_ks; // toggle_ks
	    	break;

			case 'A' :
			case 'a' :
				toggle_ka = !toggle_ka; // toggle_ks
				break;

			case 'D' :
			case 'd' :
				toggle_kd = !toggle_kd; // toggle_ks
				break;

	    	case 'P' :
			case 'p' :
				testCase = (2)%numTestCase;
				drawTestCase();
				break;

			case 'G' :
			case 'g' :
				testCase = (0)%numTestCase;
				drawTestCase();
				break;

			case 'F' :
			case 'f' :
				testCase = (1)%numTestCase;
				drawTestCase();
				break;

			case 'Z':
				object_translation_x+=5;
				drawTestCase();
				break;
			case 'z':
				object_translation_x-=5;
				drawTestCase();
				break;
			case 'X':
				object_translation_y+=5;
				drawTestCase();
				break;
			case 'x':
				object_translation_y-=5;
				drawTestCase();
				break;
			case 'C':
				object_translation_z+=5;
				drawTestCase();
				break;
			case 'c':
				object_translation_z-=5;
				drawTestCase();
				break;

			case 'V':
				camera_translation_x+=5;
				drawTestCase();
				break;
			case 'v':
				camera_translation_x-=5;
				drawTestCase();
				break;
			case 'B':
				camera_translation_y+=5;
				drawTestCase();
				break;
			case 'b':
				camera_translation_y-=5;
				drawTestCase();
				break;
			case 'N':
				camera_translation_z+=5;
				drawTestCase();
				break;
			case 'n':
				camera_translation_z-=5;
				drawTestCase();
				break;

			case ',' :
				radius -= 1f;
				drawTestCase();
				break;

			case '.' :
				radius += 1f;
				drawTestCase();
				break;

			case 'Q' :
			case 'q' :
				rotation_angle = 0;
				axis[0] = 1f;
				axis[1] = 0f;
				axis[2] = 0f;
				drawTestCase();
				break;

			case 'W' :
			case 'w' :
				rotation_angle = 0;
				axis[0] = 0f;
				axis[1] = 1f;
				axis[2] = 0f;
				drawTestCase();
				break;

			case 'E' :
			case 'e' :
				rotation_angle = 0;
				axis[0] = 0f;
				axis[1] = 0f;
				axis[2] = 1f;
				drawTestCase();
				break;

			case 'M' :
				rotation_angle+=1;
				drawTestCase();
				break;

			case 'm' :
				rotation_angle-=1;
				drawTestCase();
				break;

			case 'T' :
	    case 't' : 
	    	testCase = (testCase+1)%numTestCase;
	    	drawTestCase();
	        break; 
	    case '<':  
	        Nsteps = Nsteps < 4 ? Nsteps: Nsteps / 2;
	        System.out.printf( "Nsteps = %d \n", Nsteps);
	        drawTestCase();
	        break;
	    case '>':
	        Nsteps = Nsteps > 190 ? Nsteps: Nsteps * 2;
	        System.out.printf( "Nsteps = %d \n", Nsteps);
	        drawTestCase();
	        break;
	    case '+':
	    	ns++;
	        drawTestCase();
	    	break;
	    case '_':
	    	if(ns>0)
	    		ns--;
	        drawTestCase();
	    	break;

			case ')':
				oppset_ks+=0.05;
				drawTestCase();
				break;

			case '0':
				oppset_ks-=0.05;
				drawTestCase();
				break;

			case '(':
			oppset_kd+=0.05;
			drawTestCase();
			break;

			case '9':
				oppset_kd-=0.05;
				drawTestCase();
				break;

			case '*':
				oppset_ka+=0.05;
				drawTestCase();
				break;

			case '8':
				oppset_ka-=0.05;
				drawTestCase();
				break;

			case 'u':
				radial = !radial;
				drawTestCase();
				break;

			case 'i':
				angular = !angular;
				drawTestCase();
				break;

	    default :
	        break;
	    }
	}

	public void keyPressed(KeyEvent key)
	{
	    switch (key.getKeyCode()) 
	    {
	    case KeyEvent.VK_ESCAPE:
	    	new Thread()
	        {
	    		public void run()
	    		{
	    			animator.stop();
	    		}
	        }.start();
	        System.exit(0);
	        break;
	      default:
	        break;
	    }
	}

	public void keyReleased(KeyEvent key)
	{
		// deliberately left blank
	}

	//************************************************** 
	// MouseListener and MouseMotionListener Interfaces
	//************************************************** 
	public void mouseClicked(MouseEvent mouse)
	{
		// deliberately left blank
	}
	  public void mousePressed(MouseEvent mouse)
	  {
	    int button = mouse.getButton();
	    if ( button == MouseEvent.BUTTON1 )
	    {
	      last_x = mouse.getX();
	      last_y = mouse.getY();
	      rotate_world = true;
	    }
	  }

	  public void mouseReleased(MouseEvent mouse)
	  {
	    int button = mouse.getButton();
	    if ( button == MouseEvent.BUTTON1 )
	    {
	      rotate_world = false;
	    }
	  }

	public void mouseMoved( MouseEvent mouse)
	{
		// Deliberately left blank
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
	      final float magnitude = (float)Math.sqrt(dx * dx + dy * dy);
	      if(magnitude > 0.0001)
	      {
	    	  // define axis perpendicular to (dx,-dy,0)
	    	  // use -y because origin is in upper lefthand corner of the window
	    	  final float[] axis = new float[] { -(float) (dy / magnitude),
	    			  (float) (dx / magnitude), 0 };

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
	          drawTestCase();
	      }
	    }

	  }
	  
	public void mouseEntered( MouseEvent mouse)
	{
		// Deliberately left blank
	}

	public void mouseExited( MouseEvent mouse)
	{
		// Deliberately left blank
	} 


	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		
	}
	
	//************************************************** 
	// Test Cases
	// Nov 9, 2014 Stan Sclaroff -- removed line and triangle test cases
	//************************************************** 

	void shadeTest(boolean doSmooth, boolean phong){
		for(int i = 0; i<DEFAULT_WINDOW_HEIGHT; i++)
			for(int j = 0; j<DEFAULT_WINDOW_WIDTH; j++) {
				depthbuffer[i][j] = -10000;
			}

		// the simple example scene includes one sphere and one torus
		float sphere_x = (float)128.0 + object_translation_x - camera_translation_x;
		float sphere_y = (float)128.0 + object_translation_y - camera_translation_y;
		float sphere_z = (float)128.0 + object_translation_z - camera_translation_z;
		float torus_x = (float)256.0 + object_translation_x - camera_translation_x;
		float torus_y = (float)384.0 + object_translation_y - camera_translation_y;
		float torus_z = (float)128.0 + object_translation_z - camera_translation_z;
		float elliposoid_x = (float)375.0 + object_translation_x - camera_translation_x;
		float elliposoid_y = (float)200.0 + object_translation_y - camera_translation_y;
		float elliposoid_z = (float)128.0 + object_translation_z - camera_translation_z;
		float superelliposoid_x = (float)450.0 + object_translation_x - camera_translation_x;
		float superelliposoid_y = (float)300.0 + object_translation_y - camera_translation_y;
		float superelliposoid_z = (float)128.0 + object_translation_z - camera_translation_z;
		float box_x = (float)450.0 + object_translation_x - camera_translation_x;
		float box_y = (float)100.0 + object_translation_y - camera_translation_y;
		float box_z = (float)128.0 + object_translation_z - camera_translation_z;
		float cylinder_x = (float)200.0 + object_translation_x - camera_translation_x;
		float cylinder_y = (float)250.0 + object_translation_y - camera_translation_y;
		float cylinder_z = (float)128.0 + object_translation_z - camera_translation_z;

        Sphere3D sphere = new Sphere3D(sphere_x, sphere_y, sphere_z, (float)1.5*radius, Nsteps, Nsteps);
        Torus3D torus = new Torus3D(torus_x, torus_y, torus_z, (float)0.8*radius, (float)1.25*radius, Nsteps, Nsteps);
        Ellipsoid3D ellipsoid = new Ellipsoid3D(elliposoid_x, elliposoid_y, elliposoid_z, (float)1.5*radius, (float)1*radius, (float)0.5*radius, Nsteps, Nsteps);
       	Superellipsoid3D superellipsoid = new Superellipsoid3D(superelliposoid_x, superelliposoid_y, superelliposoid_z, 0.7f, 0.7f, (float)0.8*radius,(float)0.8*radius,(float)0.8*radius, Nsteps, Nsteps);
		Box3D box = new Box3D(box_x, box_y, box_z, (float)1*radius,(float)1*radius,(float)1*radius, Nsteps, Nsteps);
		Cylinder3D cylinder = new Cylinder3D(cylinder_x, cylinder_y, cylinder_z, (float)1.5*radius, 1*radius, Nsteps, Nsteps);

        // view vector is defined along z axis
        // this example assumes simple othorgraphic projection
        // view vector is used in 
        //   (a) calculating specular lighting contribution
        //   (b) backface culling / backface rejection
        Vector3D view_vector = new Vector3D((float)0.0,(float)0.0,(float)1.0);
      
        // material properties for the sphere and torus
        // ambient, diffuse, and specular coefficients
        // specular exponent is a global variable

		ColorType torus_ka = new ColorType(0.5f + oppset_ka, 0.4f + oppset_ka, 0.3f + oppset_ka);
		ColorType sphere_ka = new ColorType(0.0f + oppset_ka, 0.0f + oppset_ka, 0.0f + oppset_ka);
		ColorType ellipsoid_ka = new ColorType(0.0f + oppset_ka, 0.0f + oppset_ka, 0.0f + oppset_ka);
		ColorType superellipsoid_ka = new ColorType(0.8f + oppset_ka, 0.0f + oppset_ka, 0.0f + oppset_ka);
		ColorType box_ka = new ColorType(0.0f + oppset_ka, 0.0f + oppset_ka, 0.0f + oppset_ka);
		ColorType cylinder_ka = new ColorType(0.0f + oppset_ka, 0.0f + oppset_ka, 0.0f + oppset_ka);

		if(!toggle_ka) {
		torus_ka.setR_int(0);torus_ka.setG_int(0);torus_ka.setB_int(0);
			sphere_ka.setR_int(0);sphere_ka.setG_int(0);sphere_ka.setB_int(0);
			ellipsoid_ka.setR_int(0);ellipsoid_ka.setG_int(0);ellipsoid_ka.setB_int(0);
			superellipsoid_ka.setR_int(0);superellipsoid_ka.setG_int(0);superellipsoid_ka.setB_int(0);
			box_ka.setR_int(0);box_ka.setG_int(0);box_ka.setB_int(0);
			cylinder_ka.setR_int(0);cylinder_ka.setG_int(0);cylinder_ka.setB_int(0);
		}

		ColorType torus_kd = new ColorType(0.0f + oppset_kd, 0.5f + oppset_kd, 0.9f + oppset_kd);
		ColorType sphere_kd = new ColorType(0.9f + oppset_kd, 0.3f + oppset_kd, 0.1f + oppset_kd);
		ColorType ellipsoid_kd = new ColorType(0.9f + oppset_kd, 0.3f + oppset_kd, 0.1f + oppset_kd);
		ColorType superellipsoid_kd = new ColorType(0.9f + oppset_kd, 0.3f + oppset_kd, 0.1f + oppset_kd);
		ColorType cylinder_kd = new ColorType(0.9f + oppset_kd, 0.3f + oppset_kd, 0.1f + oppset_kd);
		ColorType box_kd = new ColorType(0.9f + oppset_kd, 0.3f + oppset_kd, 0.1f + oppset_kd);

		if(!toggle_kd) {
			torus_kd.setR_int(0);torus_kd.setG_int(0);torus_kd.setB_int(0);
			sphere_kd.setR_int(0);sphere_kd.setG_int(0);sphere_kd.setB_int(0);
			ellipsoid_kd.setR_int(0);ellipsoid_kd.setG_int(0);ellipsoid_kd.setB_int(0);
			superellipsoid_kd.setR_int(0);superellipsoid_kd.setG_int(0);superellipsoid_kd.setB_int(0);
			box_kd.setR_int(0);box_kd.setG_int(0);box_kd.setB_int(0);
			cylinder_kd.setR_int(0);cylinder_kd.setG_int(0);cylinder_kd.setB_int(0);
		}

		ColorType torus_ks = new ColorType(1.0f + oppset_ks,1.0f + oppset_ks,1.0f + oppset_ks);
		ColorType sphere_ks = new ColorType(1.0f + oppset_ks,1.0f + oppset_ks,1.0f + oppset_ks);
		ColorType ellipsoid_ks = new ColorType(1.0f + oppset_ks,1.0f + oppset_ks,1.0f + oppset_ks);
		ColorType superellipsoid_ks = new ColorType(1.0f + oppset_ks,1.0f + oppset_ks,1.0f + oppset_ks);
		ColorType box_ks = new ColorType(1.0f + oppset_ks,1.0f + oppset_ks,1.0f + oppset_ks);
		ColorType cylinder_ks = new ColorType(1.0f + oppset_ks,1.0f + oppset_ks,1.0f + oppset_ks);

		if(!toggle_ks) {
			torus_ks.setR_int(0);torus_ks.setG_int(0);torus_ks.setB_int(0);
			sphere_ks.setR_int(0);sphere_ks.setG_int(0);sphere_ks.setB_int(0);
			ellipsoid_ks.setR_int(0);ellipsoid_ks.setG_int(0);ellipsoid_ks.setB_int(0);
			superellipsoid_ks.setR_int(0);superellipsoid_ks.setG_int(0);superellipsoid_ks.setB_int(0);
			box_ks.setR_int(0);box_ks.setG_int(0);box_ks.setB_int(0);
			cylinder_ks.setR_int(0);cylinder_ks.setG_int(0);cylinder_ks.setB_int(0);
		}

        Material[] mats = {new Material(sphere_ka, sphere_kd, sphere_ks, ns), new Material(torus_ka, torus_kd, torus_ks, ns), new Material(ellipsoid_ka,ellipsoid_kd,ellipsoid_ks,ns)
				, new Material(superellipsoid_ka,superellipsoid_kd,superellipsoid_ks,ns)
        		, new Material(box_ka,box_kd,box_ks,ns)
        		, new Material(cylinder_ka,cylinder_kd,cylinder_ks,ns)};

        // define one infinite light source, with color = white
        ColorType Infinite_light_color = new ColorType(1.0f,1.0f,1.0f);
        Vector3D Infinite_light_direction = new Vector3D((float)0.0,(float)(-1.0/Math.sqrt(2.0)),(float)(1.0/Math.sqrt(2.0)));
		InfiniteLight infinitelight = new InfiniteLight(Infinite_light_color,Infinite_light_direction);

		ColorType Point_light_color = new ColorType(1.0f,1.0f,1.0f);
        Vector3D point_light_position = new Vector3D((float)1.0,(float)1.0,(float)1.0);
        Vector3D point_light_vl = new Vector3D(0f,-200f, 200f);

        PointLight pointlight = new PointLight(Point_light_color,point_light_position,point_light_vl,0.1f,0.1f,0.1f,70,10, radial, angular);

		ColorType Ambient_Light_Color = new ColorType(0.0f,1.0f,0.0f);
        AmbientLight ambientlight = new AmbientLight(Ambient_Light_Color);

        // normal to the plane of a triangle
        // to be used in backface culling / backface rejection
        Vector3D triangle_normal = new Vector3D();
        
        // a triangle mesh
        Mesh3D mesh;
            
		int i, j, n, m;
		
		// temporary variables for triangle 3D vertices and 3D normals
		Vector3D v0,v1, v2, n0, n1, n2;
		
		// projected triangle, with vertex colors
		Point2D_depth[] tri = {new Point2D_depth(), new Point2D_depth(), new Point2D_depth()};

		for(int k=0;k<6;++k) // loop: shade sphere, then torus, ellipsoid
		{
			if(k==0)
			{
				mesh=sphere.mesh;

				// calculate appropriate quaternion
				final float viewing_delta = 3.1415927f / 180.0f;
				final float s = (float) Math.sin(rotation_angle * viewing_delta);
				final float c = (float) Math.cos(rotation_angle * viewing_delta);
				final Quaternion Q = new Quaternion(c, s * axis[0], s * axis[1], s * axis[2]);
				mesh.rotateMesh(Q, new Vector3D(sphere_x, sphere_y, sphere_z));

				n=sphere.get_n();
				m=sphere.get_m();
			}
			else if(k == 1)
			{
				mesh=torus.mesh;

				// calculate appropriate quaternion
				final float viewing_delta = 3.1415927f / 180.0f;
				final float s = (float) Math.sin(rotation_angle * viewing_delta);
				final float c = (float) Math.cos(rotation_angle * viewing_delta);
				final Quaternion Q = new Quaternion(c, s * axis[0], s * axis[1], s * axis[2]);
				mesh.rotateMesh(Q, new Vector3D(torus_x, torus_y, torus_z));

				n=torus.get_n();
				m=torus.get_m();
			}

			else if(k == 2)
			{
				mesh=ellipsoid.mesh;

				// calculate appropriate quaternion
				final float viewing_delta = 3.1415927f / 180.0f;
				final float s = (float) Math.sin(rotation_angle * viewing_delta);
				final float c = (float) Math.cos(rotation_angle * viewing_delta);
				final Quaternion Q = new Quaternion(c, s * axis[0], s * axis[1], s * axis[2]);
				mesh.rotateMesh(Q, new Vector3D(elliposoid_x, elliposoid_y, elliposoid_z));

				n=ellipsoid.get_n();
				m=ellipsoid.get_m();
			}

			else if (k == 3)
			{
				mesh=superellipsoid.mesh;

				// calculate appropriate quaternion
				final float viewing_delta = 3.1415927f / 180.0f;
				final float s = (float) Math.sin(rotation_angle * viewing_delta);
				final float c = (float) Math.cos(rotation_angle * viewing_delta);
				final Quaternion Q = new Quaternion(c, s * axis[0], s * axis[1], s * axis[2]);
				mesh.rotateMesh(Q, new Vector3D(superelliposoid_x, superelliposoid_y, superelliposoid_z));

				n=superellipsoid.get_n();
				m=superellipsoid.get_m();
			}

			else if( k == 4)
			{
				mesh=box.mesh;

				// calculate appropriate quaternion
				final float viewing_delta = 3.1415927f / 180.0f;
				final float s = (float) Math.sin(rotation_angle * viewing_delta);
				final float c = (float) Math.cos(rotation_angle * viewing_delta);
				final Quaternion Q = new Quaternion(c, s * axis[0], s * axis[1], s * axis[2]);
				mesh.rotateMesh(Q, new Vector3D(box_x, box_y, box_z));

				n=box.get_n();
				m=box.get_m();
			}

			else
			{
				mesh=cylinder.mesh;

				// calculate appropriate quaternion
				final float viewing_delta = 3.1415927f / 180.0f;
				final float s = (float) Math.sin(rotation_angle * viewing_delta);
				final float c = (float) Math.cos(rotation_angle * viewing_delta);
				final Quaternion Q = new Quaternion(c, s * axis[0], s * axis[1], s * axis[2]);
				mesh.rotateMesh(Q, new Vector3D(cylinder_x, cylinder_y, cylinder_z));

				n=cylinder.get_n();
				m=cylinder.get_m();
			}
			
			// rotate the surface's 3D mesh using quaternion
			mesh.rotateMesh(viewing_quaternion, viewing_center);
					
			// draw triangles for the current surface, using vertex colors
			// this works for Gouraud and flat shading only (not Phong)
			for(i=0; i < m-1; ++i)
		    {
				for(j=0; j < n-1; ++j)
				{
					v0 = mesh.v[i][j];
					v1 = mesh.v[i][j+1];
					v2 = mesh.v[i+1][j+1];
					triangle_normal = computeTriangleNormal(v0,v1,v2);
					
					if(view_vector.dotProduct(triangle_normal) > 0.0)  // front-facing triangle?   // Draw the triangle the
					{	
						if(doSmooth && !phong)
						{
							// vertex colors for Gouraud shading
							n0 = mesh.n[i][j];
							n1 = mesh.n[i][j+1];
							n2 = mesh.n[i+1][j+1];

							tri[0].c.setR_int(0);tri[0].c.setG_int(0);tri[0].c.setB_int(0);
							tri[1].c.setR_int(0);tri[1].c.setG_int(0);tri[1].c.setB_int(0);
							tri[2].c.setR_int(0);tri[2].c.setG_int(0);tri[2].c.setB_int(0);

							if(amb && light){
								tri[0].c.r += ambientlight.applyLight(mats[k]).r;
								tri[0].c.g += ambientlight.applyLight(mats[k]).g;
								tri[0].c.b += ambientlight.applyLight(mats[k]).b;
								tri[1].c.r += ambientlight.applyLight(mats[k]).r;
								tri[1].c.g += ambientlight.applyLight(mats[k]).g;
								tri[1].c.b += ambientlight.applyLight(mats[k]).b;
								tri[2].c.r += ambientlight.applyLight(mats[k]).r;
								tri[2].c.g += ambientlight.applyLight(mats[k]).g;
								tri[2].c.b += ambientlight.applyLight(mats[k]).b;
							}

							if(point && light) {
								tri[0].c.r += pointlight.applyLight(mats[k], view_vector, n0, v0).r;
								tri[0].c.g += pointlight.applyLight(mats[k], view_vector, n0, v0).g;
								tri[0].c.b += pointlight.applyLight(mats[k], view_vector, n0, v0).b;
								tri[1].c.r += pointlight.applyLight(mats[k], view_vector, n1, v1).r;
								tri[1].c.g += pointlight.applyLight(mats[k], view_vector, n1, v1).g;
								tri[1].c.b += pointlight.applyLight(mats[k], view_vector, n1, v1).b;
								tri[2].c.r += pointlight.applyLight(mats[k], view_vector, n2, v2).r;
								tri[2].c.g += pointlight.applyLight(mats[k], view_vector, n2, v2).g;
								tri[2].c.b += pointlight.applyLight(mats[k], view_vector, n2, v2).b;
								//materials!!!!
							}

							if(infinit && light) {
								tri[0].c.r += infinitelight.applyLight(mats[k], view_vector, n0).r;
								tri[0].c.g += infinitelight.applyLight(mats[k], view_vector, n0).g;
								tri[0].c.b += infinitelight.applyLight(mats[k], view_vector, n0).b;
								tri[1].c.r += infinitelight.applyLight(mats[k], view_vector, n1).r;
								tri[1].c.g += infinitelight.applyLight(mats[k], view_vector, n1).g;
								tri[1].c.b += infinitelight.applyLight(mats[k], view_vector, n1).b;
								tri[2].c.r += infinitelight.applyLight(mats[k], view_vector, n2).r;
								tri[2].c.g += infinitelight.applyLight(mats[k], view_vector, n2).g;
								tri[2].c.b += infinitelight.applyLight(mats[k], view_vector, n2).b;
							}

							// clamp so that allowable maximum illumination level is not exceeded
							tri[0].c.r = (float) Math.min(1.0, tri[0].c.r);
							tri[0].c.g = (float) Math.min(1.0, tri[0].c.g);
							tri[0].c.b = (float) Math.min(1.0, tri[0].c.b);
							tri[1].c.r = (float) Math.min(1.0, tri[1].c.r);
							tri[1].c.g = (float) Math.min(1.0, tri[1].c.g);
							tri[1].c.b = (float) Math.min(1.0, tri[1].c.b);
							tri[2].c.r = (float) Math.min(1.0, tri[2].c.r);
							tri[2].c.g = (float) Math.min(1.0, tri[2].c.g);
							tri[2].c.b = (float) Math.min(1.0, tri[2].c.b);
							//materials!!!!

							tri[0].x = (int)v0.x;
							tri[0].y = (int)v0.y;
							tri[0].z = (int)v0.z;
							tri[1].x = (int)v1.x;
							tri[1].y = (int)v1.y;
							tri[1].z = (int)v1.z;
							tri[2].x = (int)v2.x;
							tri[2].y = (int)v2.y;
							tri[2].z = (int)v2.z;

							SketchBase.drawTriangle(buff,tri[0],tri[1],tri[2],doSmooth,depthbuffer);
						}

						else if(!doSmooth && !phong)
						{
							// flat shading: use the normal to the triangle itself
							n2 = n1 = n0 =  triangle_normal;
							//tri[2].c = tri[1].c = tri[0].c = light.applyLight(mats[k], view_vector, triangle_normal);

							tri[0].c.setR_int(0);tri[0].c.setG_int(0);tri[0].c.setB_int(0);
							tri[1].c.setR_int(0);tri[1].c.setG_int(0);tri[1].c.setB_int(0);
							tri[2].c.setR_int(0);tri[2].c.setG_int(0);tri[2].c.setB_int(0);

							if(amb && light) {
								tri[0].c.r += ambientlight.applyLight(mats[k]).r;
								tri[0].c.g += ambientlight.applyLight(mats[k]).g;
								tri[0].c.b += ambientlight.applyLight(mats[k]).b;
								tri[1].c.r += ambientlight.applyLight(mats[k]).r;
								tri[1].c.g += ambientlight.applyLight(mats[k]).g;
								tri[1].c.b += ambientlight.applyLight(mats[k]).b;
								tri[2].c.r += ambientlight.applyLight(mats[k]).r;
								tri[2].c.g += ambientlight.applyLight(mats[k]).g;
								tri[2].c.b += ambientlight.applyLight(mats[k]).b;
							}

							if(point && light) {
								tri[0].c.r += pointlight.applyLight(mats[k], view_vector, n0, v0).r;
								tri[0].c.g += pointlight.applyLight(mats[k], view_vector, n0, v0).g;
								tri[0].c.b += pointlight.applyLight(mats[k], view_vector, n0, v0).b;
								tri[1].c.r += pointlight.applyLight(mats[k], view_vector, n1, v1).r;
								tri[1].c.g += pointlight.applyLight(mats[k], view_vector, n1, v1).g;
								tri[1].c.b += pointlight.applyLight(mats[k], view_vector, n1, v1).b;
								tri[2].c.r += pointlight.applyLight(mats[k], view_vector, n2, v2).r;
								tri[2].c.g += pointlight.applyLight(mats[k], view_vector, n2, v2).g;
								tri[2].c.b += pointlight.applyLight(mats[k], view_vector, n2, v2).b;
								//materials!!!!
							}

							if(infinit && light) {
								tri[0].c.r += infinitelight.applyLight(mats[k], view_vector, n0).r;
								tri[0].c.g += infinitelight.applyLight(mats[k], view_vector, n0).g;
								tri[0].c.b += infinitelight.applyLight(mats[k], view_vector, n0).b;
								tri[1].c.r += infinitelight.applyLight(mats[k], view_vector, n1).r;
								tri[1].c.g += infinitelight.applyLight(mats[k], view_vector, n1).g;
								tri[1].c.b += infinitelight.applyLight(mats[k], view_vector, n1).b;
								tri[2].c.r += infinitelight.applyLight(mats[k], view_vector, n2).r;
								tri[2].c.g += infinitelight.applyLight(mats[k], view_vector, n2).g;
								tri[2].c.b += infinitelight.applyLight(mats[k], view_vector, n2).b;
								//materials!!!!
							}

							// clamp so that allowable maximum illumination level is not exceeded
							tri[0].c.r = (float) Math.min(1.0, tri[0].c.r);
							tri[0].c.g = (float) Math.min(1.0, tri[0].c.g);
							tri[0].c.b = (float) Math.min(1.0, tri[0].c.b);
							tri[1].c.r = (float) Math.min(1.0, tri[1].c.r);
							tri[1].c.g = (float) Math.min(1.0, tri[1].c.g);
							tri[1].c.b = (float) Math.min(1.0, tri[1].c.b);
							tri[2].c.r = (float) Math.min(1.0, tri[2].c.r);
							tri[2].c.g = (float) Math.min(1.0, tri[2].c.g);
							tri[2].c.b = (float) Math.min(1.0, tri[2].c.b);

							tri[0].x = (int)v0.x;
							tri[0].y = (int)v0.y;
							tri[0].z = (int)v0.z;
							tri[1].x = (int)v1.x;
							tri[1].y = (int)v1.y;
							tri[1].z = (int)v1.z;
							tri[2].x = (int)v2.x;
							tri[2].y = (int)v2.y;
							tri[2].z = (int)v2.z;

							SketchBase.drawTriangle(buff,tri[0],tri[1],tri[2],doSmooth,depthbuffer);
						}

						else
						{
							if(light) {
								n0 = mesh.n[i][j];
								n1 = mesh.n[i][j + 1];
								n2 = mesh.n[i + 1][j + 1];

								tri[0].x = (int) v0.x;
								tri[0].y = (int) v0.y;
								tri[0].z = (int) v0.z;
								tri[1].x = (int) v1.x;
								tri[1].y = (int) v1.y;
								tri[1].z = (int) v1.z;
								tri[2].x = (int) v2.x;
								tri[2].y = (int) v2.y;
								tri[2].z = (int) v2.z;

								SketchBase.drawTriangle_Phong_Shading(buff, tri[0], tri[1], tri[2], n0, n1, n2, mats[k], view_vector, doSmooth, depthbuffer, infinitelight, pointlight, ambientlight, light, infinit, point, amb);
							}
						}

					}
					
					v0 = mesh.v[i][j];
					v1 = mesh.v[i+1][j+1];
					v2 = mesh.v[i+1][j];
					triangle_normal = computeTriangleNormal(v0,v1,v2);
					
					if(view_vector.dotProduct(triangle_normal) > 0.0)  // front-facing triangle?
					{	
						if(doSmooth && !phong)
						{
							// vertex colors for Gouraud shading
							n0 = mesh.n[i][j];
							n1 = mesh.n[i+1][j+1];
							n2 = mesh.n[i+1][j];
							//tri[0].c = light.applyLight(mats[k], view_vector, n0);
							//tri[1].c = light.applyLight(mats[k], view_vector, n1);
							//tri[2].c = light.applyLight(mats[k], view_vector, n2);

							tri[0].c.setR_int(0);tri[0].c.setG_int(0);tri[0].c.setB_int(0);
							tri[1].c.setR_int(0);tri[1].c.setG_int(0);tri[1].c.setB_int(0);
							tri[2].c.setR_int(0);tri[2].c.setG_int(0);tri[2].c.setB_int(0);

							if(amb && light) {
								tri[0].c.r += ambientlight.applyLight(mats[k]).r;
								tri[0].c.g += ambientlight.applyLight(mats[k]).g;
								tri[0].c.b += ambientlight.applyLight(mats[k]).b;
								tri[1].c.r += ambientlight.applyLight(mats[k]).r;
								tri[1].c.g += ambientlight.applyLight(mats[k]).g;
								tri[1].c.b += ambientlight.applyLight(mats[k]).b;
								tri[2].c.r += ambientlight.applyLight(mats[k]).r;
								tri[2].c.g += ambientlight.applyLight(mats[k]).g;
								tri[2].c.b += ambientlight.applyLight(mats[k]).b;
							}

							if(point && light) {
								tri[0].c.r += pointlight.applyLight(mats[k], view_vector, n0, v0).r;
								tri[0].c.g += pointlight.applyLight(mats[k], view_vector, n0, v0).g;
								tri[0].c.b += pointlight.applyLight(mats[k], view_vector, n0, v0).b;
								tri[1].c.r += pointlight.applyLight(mats[k], view_vector, n1, v1).r;
								tri[1].c.g += pointlight.applyLight(mats[k], view_vector, n1, v1).g;
								tri[1].c.b += pointlight.applyLight(mats[k], view_vector, n1, v1).b;
								tri[2].c.r += pointlight.applyLight(mats[k], view_vector, n2, v2).r;
								tri[2].c.g += pointlight.applyLight(mats[k], view_vector, n2, v2).g;
								tri[2].c.b += pointlight.applyLight(mats[k], view_vector, n2, v2).b;
								//materials!!!!
							}

							if(infinit && light) {
								tri[0].c.r += infinitelight.applyLight(mats[k], view_vector, n0).r;
								tri[0].c.g += infinitelight.applyLight(mats[k], view_vector, n0).g;
								tri[0].c.b += infinitelight.applyLight(mats[k], view_vector, n0).b;
								tri[1].c.r += infinitelight.applyLight(mats[k], view_vector, n1).r;
								tri[1].c.g += infinitelight.applyLight(mats[k], view_vector, n1).g;
								tri[1].c.b += infinitelight.applyLight(mats[k], view_vector, n1).b;
								tri[2].c.r += infinitelight.applyLight(mats[k], view_vector, n2).r;
								tri[2].c.g += infinitelight.applyLight(mats[k], view_vector, n2).g;
								tri[2].c.b += infinitelight.applyLight(mats[k], view_vector, n2).b;
								//materials!!!!
							}

							// clamp so that allowable maximum illumination level is not exceeded
							tri[0].c.r = (float) Math.min(1.0, tri[0].c.r);
							tri[0].c.g = (float) Math.min(1.0, tri[0].c.g);
							tri[0].c.b = (float) Math.min(1.0, tri[0].c.b);
							tri[1].c.r = (float) Math.min(1.0, tri[1].c.r);
							tri[1].c.g = (float) Math.min(1.0, tri[1].c.g);
							tri[1].c.b = (float) Math.min(1.0, tri[1].c.b);
							tri[2].c.r = (float) Math.min(1.0, tri[2].c.r);
							tri[2].c.g = (float) Math.min(1.0, tri[2].c.g);
							tri[2].c.b = (float) Math.min(1.0, tri[2].c.b);

							tri[0].x = (int)v0.x;
							tri[0].y = (int)v0.y;
							tri[0].z = (int)v0.z;
							tri[1].x = (int)v1.x;
							tri[1].y = (int)v1.y;
							tri[1].z = (int)v1.z;
							tri[2].x = (int)v2.x;
							tri[2].y = (int)v2.y;
							tri[2].z = (int)v2.z;

							SketchBase.drawTriangle(buff,tri[0],tri[1],tri[2],doSmooth,depthbuffer);
						}
						else if(!phong && !doSmooth)
						{
							// flat shading: use the normal to the triangle itself
							n2 = n1 = n0 =  triangle_normal;
							//tri[2].c = tri[1].c = tri[0].c = light.applyLight(mats[k], view_vector, triangle_normal);

							tri[0].c.setR_int(0);tri[0].c.setG_int(0);tri[0].c.setB_int(0);
							tri[1].c.setR_int(0);tri[1].c.setG_int(0);tri[1].c.setB_int(0);
							tri[2].c.setR_int(0);tri[2].c.setG_int(0);tri[2].c.setB_int(0);

							if(amb && light) {
								tri[0].c.r += ambientlight.applyLight(mats[k]).r;
								tri[0].c.g += ambientlight.applyLight(mats[k]).g;
								tri[0].c.b += ambientlight.applyLight(mats[k]).b;
								tri[1].c.r += ambientlight.applyLight(mats[k]).r;
								tri[1].c.g += ambientlight.applyLight(mats[k]).g;
								tri[1].c.b += ambientlight.applyLight(mats[k]).b;
								tri[2].c.r += ambientlight.applyLight(mats[k]).r;
								tri[2].c.g += ambientlight.applyLight(mats[k]).g;
								tri[2].c.b += ambientlight.applyLight(mats[k]).b;
							}

							if(point && light) {
								tri[0].c.r += pointlight.applyLight(mats[k], view_vector, n0, v0).r;
								tri[0].c.g += pointlight.applyLight(mats[k], view_vector, n0, v0).g;
								tri[0].c.b += pointlight.applyLight(mats[k], view_vector, n0, v0).b;
								tri[1].c.r += pointlight.applyLight(mats[k], view_vector, n1, v1).r;
								tri[1].c.g += pointlight.applyLight(mats[k], view_vector, n1, v1).g;
								tri[1].c.b += pointlight.applyLight(mats[k], view_vector, n1, v1).b;
								tri[2].c.r += pointlight.applyLight(mats[k], view_vector, n2, v2).r;
								tri[2].c.g += pointlight.applyLight(mats[k], view_vector, n2, v2).g;
								tri[2].c.b += pointlight.applyLight(mats[k], view_vector, n2, v2).b;
								//materials!!!!
							}

							if(infinit && light) {
								tri[0].c.r += infinitelight.applyLight(mats[k], view_vector, n0).r;
								tri[0].c.g += infinitelight.applyLight(mats[k], view_vector, n0).g;
								tri[0].c.b += infinitelight.applyLight(mats[k], view_vector, n0).b;
								tri[1].c.r += infinitelight.applyLight(mats[k], view_vector, n1).r;
								tri[1].c.g += infinitelight.applyLight(mats[k], view_vector, n1).g;
								tri[1].c.b += infinitelight.applyLight(mats[k], view_vector, n1).b;
								tri[2].c.r += infinitelight.applyLight(mats[k], view_vector, n2).r;
								tri[2].c.g += infinitelight.applyLight(mats[k], view_vector, n2).g;
								tri[2].c.b += infinitelight.applyLight(mats[k], view_vector, n2).b;
								//materials!!!!
							}

							// clamp so that allowable maximum illumination level is not exceeded
							tri[0].c.r = (float) Math.min(1.0, tri[0].c.r);
							tri[0].c.g = (float) Math.min(1.0, tri[0].c.g);
							tri[0].c.b = (float) Math.min(1.0, tri[0].c.b);
							tri[1].c.r = (float) Math.min(1.0, tri[1].c.r);
							tri[1].c.g = (float) Math.min(1.0, tri[1].c.g);
							tri[1].c.b = (float) Math.min(1.0, tri[1].c.b);
							tri[2].c.r = (float) Math.min(1.0, tri[2].c.r);
							tri[2].c.g = (float) Math.min(1.0, tri[2].c.g);
							tri[2].c.b = (float) Math.min(1.0, tri[2].c.b);

							tri[0].x = (int)v0.x;
							tri[0].y = (int)v0.y;
							tri[0].z = (int)v0.z;
							tri[1].x = (int)v1.x;
							tri[1].y = (int)v1.y;
							tri[1].z = (int)v1.z;
							tri[2].x = (int)v2.x;
							tri[2].y = (int)v2.y;
							tri[2].z = (int)v2.z;

							SketchBase.drawTriangle(buff,tri[0],tri[1],tri[2],doSmooth,depthbuffer);
						}

						else
						{
							if(light) {
								n0 = mesh.n[i][j];
								n1 = mesh.n[i][j + 1];
								n2 = mesh.n[i + 1][j + 1];

								tri[0].x = (int) v0.x;
								tri[0].y = (int) v0.y;
								tri[0].z = (int) v0.z;
								tri[1].x = (int) v1.x;
								tri[1].y = (int) v1.y;
								tri[1].z = (int) v1.z;
								tri[2].x = (int) v2.x;
								tri[2].y = (int) v2.y;
								tri[2].z = (int) v2.z;

								SketchBase.drawTriangle_Phong_Shading(buff, tri[0], tri[1], tri[2], n0, n1, n2, mats[k], view_vector, doSmooth, depthbuffer, infinitelight, pointlight, ambientlight, light, infinit, point, amb);
							}
						}
			

					}
				}	
		    }
		}
	}

	// helper method that computes the unit normal to the plane of the triangle
	// degenerate triangles yield normal that is numerically zero
	private Vector3D computeTriangleNormal(Vector3D v0, Vector3D v1, Vector3D v2)
	{
		Vector3D e0 = v1.minus(v2);
		Vector3D e1 = v0.minus(v2);
		Vector3D norm = e0.crossProduct(e1);
		
		if(norm.magnitude()>0.000001)
			norm.normalize();
		else 	// detect degenerate triangle and set its normal to zero
			norm.set((float)0.0,(float)0.0,(float)0.0);

		return norm;
	}

}