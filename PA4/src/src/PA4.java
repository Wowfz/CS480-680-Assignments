//****************************************************************************
//       Example Main Program for CS480 PA1
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
//		Q,q: quit 
//		C,c: clear polygon (set vertex count=0)
//		R,r: randomly change the color
//		S,s: toggle the smooth shading for triangle 
//			 (no smooth shading by default)
//		T,t: show testing examples
//		>:	 increase the step number for examples
//		<:   decrease the step number for examples
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


public class PA4 extends JFrame
	implements GLEventListener, KeyListener, MouseListener, MouseMotionListener
{
	
	private static final long serialVersionUID = 1L;
	private final int DEFAULT_WINDOW_WIDTH=512;
	private final int DEFAULT_WINDOW_HEIGHT=512;
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
	private int ns=7; 
	
	//Depth buffer 2d matrix
	public float[][] depthBuffer1;
	
	private ArrayList<Point2D> lineSegs;
	private ArrayList<Point2D> triangles;
	private boolean doSmoothShading;
	private int Nsteps;
	
	private int cases = 0;
	
	GL2 gl;
	
	private boolean phong, gouraud, flat;
	
	//For translating shapes
	private int tRx, tRy;
	private int tSx, tSy, tSz;
	//Scaling
	private int sc;
	private int rot;
	private boolean tor;
	private InfiniteLight[] combined;
	
	private float globalKa;
	private float globalKs;
	private float globalKd;
	
	//ambient boolean to stay off for point light source
	private boolean amb;
	private boolean src1, src2, src3, src4, src5;

	/** The quaternion which controls the rotation of the world. */
    private Quaternion viewing_quaternion = new Quaternion();
    private Vector3D viewing_center = new Vector3D((float)(DEFAULT_WINDOW_WIDTH/2),(float)(DEFAULT_WINDOW_HEIGHT/2),(float)0.0);
    /** The last x and y coordinates of the mouse press. */
    private int last_x = 0, last_y = 0;
    /** Whether the world is being rotated. */
    private boolean rotate_world = false;
    //Boolean list to switch on/off the diffuse, ambient, and specular lights
    private boolean[] doff = new boolean[3];
	public PA4()
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
	    testCase = 0;
	    Nsteps = 24;
	    
	    tor = false;
	    tRx = 0;
	    tRy = 0;
	    sc = 1;
	    rot = 1;
	    
	    tSx = tSy = tSz = 0;
	    
	    globalKa = globalKs = globalKd = 0;
	    
	    src1 = src2 = src3 = src4 = src5 = false;
	    
	    setTitle("CS480 PA4");
	    setSize( DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setVisible(true);
	    setResizable(false);
	    
	    rng = new Random();
	    color = new ColorType(1.0f,0.0f,0.0f);
	    lineSegs = new ArrayList<Point2D>();
	    triangles = new ArrayList<Point2D>();
	    doSmoothShading = false;
	    
	    doff[0] = true;
	    doff[1] = true;
	    doff[2] = true;
	    
	    gouraud = true;
	    phong = flat = false;
	    
	}

	public void run()
	{
		animator.start();
	}

	public static void main( String[] args )
	{
	    PA4 P = new PA4();
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
			shadeTest(true, 0); /* smooth shaded, sphere and torus */
			break;
		case 1:
			shadeTest(true, 1); /* flat shaded, sphere and torus */
			break;
		case 2:
			shadeTest(true, 2); /* flat shaded, sphere and torus */
			break;
		}	
	}


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
	    case 'Q' :
	    case 'q' : 
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
	    case 'C' :
	    case 'c' :
	    	clearPixelBuffer();
	    	break;
	    case 'D':
	    case 'd':
	    	doff[1] = !doff[1];
	    	break;
	    case 'A':
	    case 'a':
	    	doff[0] = !doff[0];
	    	break;
	    case 'S':
	    case 's':
	    	doff[2] = !doff[2];
	    	break;
	    case ']':
	    	tRx += 5;
	    	break;
	    case '[':
	    	tRx -= 5;
	    	break;
	    case '}':
	    	tRy += 5;
	    	break;
	    case '{':
	    	tRy -= 5;
	    	break;
	    case '.':
	    	sc += 1;
	    	break;
	    case ',':
	    	sc -= 1;
	    	break;
	    case 'z':
	    	rot += 1;
	    	break;
	    case 'Z':
	    	rot -= 1;
	    	break;
	    case '1':
	    	src1 = !src1;
	    	break;
	    case '2':
	    	src2 = !src2;
	    	break;
	    case '3':
	    	src3 = !src3;
	    	break;
	    case '4':
	    	src4 = !src4;
	    	break;
	    case '5':
	    	src5 = !src5;
	    	break;
	    case 'T' :
	    case 't' : 
	    	testCase = (testCase+1)%numTestCase;
	    	drawTestCase();
	        break;
	    case 'G':
	    case 'g':
	    	gouraud = true;
	    	flat = false;
	    	phong = false;
	    	break;
	    case 'f':
	    case 'F':
	    	gouraud = false;
	    	flat = true;
	    	phong = false;
	    	break;
	    case 'P':
	    case 'p':
	    	gouraud = false;
	    	flat = false;
	    	phong = true;
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
	    case '-':
	    	if(ns>0)
	    		ns--;
	        drawTestCase();
	    	break;
	    case 'w':
	    	globalKa += 0.1;
	    	break;
	    case 'W':
	    	globalKa -= 0.1;
	    	break;
	    case 'e':
	    	globalKs += 0.1;
	    	break;
	    case 'E':
	    	globalKs -= 0.1;
	    	break;
	    case 'y':
	    	globalKd += 0.1;
	    	break;
	    case 'Y':
	    	globalKd -= 0.1;
	    	break;
	    //translate shape xyz
	    case '9':
	    	tSx += 5;
	    	break;
	    case '8':
	    	tSy += 5;
	    	break;
	    case '7':
	    	tSz += 5;
	    	break;
	    case '(':
	    	tSx -= 5;
	    	break;
	    case '*':
	    	tSy -= 5;
	    	break;
	    case '&':
	    	tSz -= 5;
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

	
	//DO: Depth buffer, Superquadric/ellipsoid (maybe), Phong Flat Gourand, Box, Cyl, Light Sources
	void shadeTest(boolean doSmooth, int cases){
		
		// the simple example scene includes one sphere and one torus
		float radius = (float)50.0;
		radius = radius + sc;
        Sphere3D sphere = new Sphere3D((float)128.0 + tSx, (float)400.0 + tSy, (float)128.0 + tSz, (float)1.5*radius, Nsteps, Nsteps);
        
        Torus3D torus = new Torus3D((float)(400.0 + tSx), (float)(400.0 + tSy), (float)128.0 + tSz, (float)0.8*radius, (float)1.25*radius, Nsteps, Nsteps);
        
        Ellipsoid e = new Ellipsoid((float)128.0 + tSx, (float)400.0 + tSy, (float)128.0 + tSz, (float)2*radius, (float) 1 * radius, (float) 1.2 * radius, Nsteps, Nsteps);
        
        Cylinder c = new Cylinder(400f + tSx, 100f + tSy, 50.0f + tSz, (float)1.5*radius, (float)1.5*radius, Nsteps, Nsteps, radius);
        
        Box b = new Box((float)100.0 + tSx, (float)100.0 + tSy, (float)50.0 + tSz, (float)1*radius, (float) 1 * radius, (float) 1 * radius, Nsteps, Nsteps);
        
        Superellipsoid sp = new Superellipsoid((float)100.0 + tSx, (float)100.0 + tSy, (float)50.0 + tSz, (float)1*radius, (float) 1 * radius, (float) 1 * radius, Nsteps, Nsteps);
        
        Superellipsoid sp2 = new Superellipsoid((float)400.0 + tSx, (float)100.0 + tSy, (float)50.0 + tSz, (float)1*radius, (float) 1 * radius, (float) 1 * radius, Nsteps, Nsteps);
        // view vector is defined along z axis
        // this example assumes simple othorgraphic projection
        // view vector is used in 
        //   (a) calculating specular lighting contribution
        //   (b) backface culling / backface rejection
        Vector3D view_vector = new Vector3D((float)0.0,(float)0.0,(float)1.0);
        
        Vector3D ll = sphere.get_center();
      
        // material properties for the sphere and torus
        // ambient, diffuse, and specular coefficients
        // specular exponent is a global variable
        ColorType torus_ka = new ColorType(0.1f + globalKa,0.1f+ globalKa,0.1f+ globalKa);
        ColorType sphere_ka = new ColorType(0.0f + globalKa,0.0f+ globalKa,0.0f+ globalKa);
        ColorType torus_kd = new ColorType(0.0f+ globalKd,0.5f+ globalKd,0.9f+ globalKd);
        ColorType sphere_kd = new ColorType(0.9f+ globalKd,0.3f+ globalKd,0.1f+ globalKd);
        ColorType torus_ks = new ColorType(1.0f+ globalKs,1.0f+ globalKs,1.0f+ globalKs);
        ColorType sphere_ks = new ColorType(1.0f+ globalKs,1.0f+ globalKs,1.0f+ globalKs);
        ColorType cyl_ka = new ColorType(0f,0f,1f);
        ColorType cyl_kd = new ColorType(0.0f+ globalKd,0.75f,1f);
        ColorType cyl_ks = new ColorType(1.0f+ globalKs,1.0f+ globalKs,1.0f+ globalKs);
        ColorType el_ka = new ColorType(0.0f+ globalKa,0.0f+ globalKa,0.0f+ globalKa);
        ColorType el_kd = new ColorType(0.5f+ globalKd,1f+ globalKd,0f+ globalKd);
        ColorType el_ks = new ColorType(1.0f+ globalKs,1.0f+ globalKs,1.0f+ globalKs);
        
        Material[] mats = {new Material(el_ka, el_kd, el_ks, doff, ns), new Material(torus_ka, torus_kd, torus_ks, doff,  ns),  new Material(sphere_ka, sphere_kd, sphere_ks, doff, ns), new Material(torus_ka, torus_kd, torus_ks, doff, ns)};

        // define one infinite light source, with color = white
        ColorType light_color = new ColorType(1f,1f,1f);
        Vector3D light_direction = new Vector3D((float)0.0,(float)(-1.0/Math.sqrt(2.0)),(float)(1.0/Math.sqrt(2.0)));
        Vector3D nul = new Vector3D(0.0f, 0f, 0f);
        InfiniteLight light = new InfiniteLight(light_color,light_direction, nul, true, false, false);
       
        //src1 radial
        Vector3D light_position = new Vector3D(0.0f, 700.0f, 500f);
        Vector3D light_ddirection = new Vector3D((float) 0,(float) -1.0f, (float) 1f);
        ColorType pink = new ColorType(1f, 0.07f, 0.58f);
        InfiniteLight rLight1 = new InfiniteLight(pink,light_ddirection, light_position, false, src1, false);
        
        //src2 angular
        ColorType lC = new ColorType(1f,0f,0f);
        Vector3D location = new Vector3D(0.0f, 0f, 1000f);
        Vector3D direction2 = new Vector3D(1f, -1f, 1f);
        InfiniteLight aLight1 = new InfiniteLight(lC,direction2, location, false, false, src2);
        
        //src3 angular from right
        ColorType light_color2 = new ColorType(0f,1f,0f);
    	Vector3D light_direction2 = new Vector3D(0f,-1f, 1f);
    	Vector3D loc3 = new Vector3D((float)100, (float)25, (float)50);
    	Vector3D loc4 = new Vector3D((float)1000, (float)250, (float)500);
    	
    	ColorType light_color3 = new ColorType(0f,0.5f,0f);
    	
    	InfiniteLight aLight3 = new InfiniteLight(light_color3,light_direction2, loc3, false, false, src3);
    	
    	//src4 radial
    	InfiniteLight aLight4 = new InfiniteLight(light_color2,light_direction2, loc4, false, src4, false);
        
        CombinedLight combined = new CombinedLight();
        
       combined.addLight(light);
       if(cases == 0)
       {
    	   combined.addLight(rLight1);
    	   if(src1)
    	   {
    		   combined.removeLight(rLight1);
    	   }
       }
       if(cases == 1)
       {
    	   combined.addLight(aLight1);
    	   combined.removeLight(rLight1);
    	   if(src2)
    	   {
    		   combined.removeLight(aLight1);
    	   }
       }
       if(cases == 2)
       {
    	   combined.addLight(aLight3);
    	   combined.removeLight(aLight1);
    	   combined.removeLight(rLight1);
    	   if(src3)
    	   {
    		   combined.removeLight(aLight3);
    	   }
       }
       
       if(src1 && cases != 0)
       {
    	   combined.addLight(rLight1);
       }
       if(src2 && cases != 1)
       {
    	   combined.addLight(aLight1);
       }
       if(src3 && cases != 2)
       {
    	   combined.addLight(aLight3);
       }
       if(src4)
       {
    	   combined.addLight(aLight4);
       }
       
       if(!src1 && cases == 0)
       {
    	   combined.addLight(rLight1);
       }
       if(!src2 && cases == 1)
       {
    	   combined.addLight(aLight1);
       }
       if(!src3 && cases == 2)
       {
    	   combined.addLight(aLight3);
       }
       if(!src4)
       {
    	   combined.removeLight(aLight4);
       }

        // normal to the plane of a triangle
        // to be used in backface culling / backface rejection
        Vector3D triangle_normal = new Vector3D();
        
        // a triangle mesh
        Mesh3D mesh;
            
		int i, j, n, m;
		
		// temporary variables for triangle 3D vertices and 3D normals
		Vector3D v0,v1, v2, n0, n1, n2;
		
		// projected triangle, with vertex colors
		Point3D[] tri = {new Point3D(), new Point3D(), new Point3D()};
		Vector3D ctr = new Vector3D();
		
		//Initialize depth buffer
		depthBuffer1 = new float[512][512];
		for (float[] row : depthBuffer1){
		    Arrays.fill(row, -999f);
		}
			for(int k=0;k<4;++k) // loop
			{
				//case 0: box, torus, sphere, cylinder

					if(k==0)
					{
						mesh=b.mesh;
						n=b.get_n();
						m=b.get_m();

					}
					else if(k == 1)
					{
						mesh=torus.mesh;
						n=torus.get_n();
						m=torus.get_m();

					}
					else if(k == 2)
					{
						mesh=sphere.mesh;
						n=sphere.get_n();
						m=sphere.get_m();
				
					}
					else{
						
						mesh = sp2.mesh;
						n = sp2.get_n();
						m = sp2.get_m();
					
					}
				if(cases == 1)
					{
					if(k==0)
					{
						mesh=b.mesh;
						n=b.get_n();
						m=b.get_m();

					}
					else if(k == 1)
					{
						mesh=torus.mesh;
						n=torus.get_n();
						m=torus.get_m();
		
					}
					else if(k == 2)
					{
						mesh=sphere.mesh;
						n=sphere.get_n();
						m=sphere.get_m();
	
					}
					else{
						
						mesh = c.mesh;
						n = c.get_n();
						m = c.get_m();
						
					}
					}
					
				if(cases == 2)
					{
					if(k==0)
					{
						mesh=sp.mesh;
						n=sp.get_n();
						m=sp.get_m();
					}
					else if(k == 1)
					{
						mesh=torus.mesh;
						n=torus.get_n();
						m=torus.get_m();
					}
					else if(k == 2)
					{
						mesh=e.mesh;
						n=e.get_n();
						m=e.get_m();
						ctr = sphere.get_center();
					}
					else{
						mesh = c.mesh;
						n = c.get_n();
						m = c.get_m();
					}
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
					
					if(view_vector.dotProduct(triangle_normal) > 0.0)  // front-facing triangle?
					{	
						if(gouraud == true && flat == false)  
						{
							// vertex colors for Gouraud shading
							n0 = mesh.n[i][j];
							n1 = mesh.n[i][j+1];
							n2 = mesh.n[i+1][j+1];
							
							tri[0].c = combined.applyLight(mats[k], view_vector, n0, v0);
							tri[1].c = combined.applyLight(mats[k], view_vector,
									n1, v1);
							tri[2].c = combined.applyLight(mats[k], view_vector,
									n2, v2);
						}
						else if(phong)
						{
							n0 = mesh.n[i][j];
							n1 = mesh.n[i][j+1];
							n2 = mesh.n[i+1][j+1];
						}
						else 
						{
							// flat shading: use the normal to the triangle itself
							n2 = n1 = n0 =  triangle_normal;
							Vector3D point = new Vector3D((v0.x + v1.x + v2.x)/3, 
									(v0.y + v1.y + v2.y)/3, 
									(v0.z + v1.z + v2.z)/3);
							tri[2].c = tri[1].c = tri[0].c = combined.applyLight(mats[k], view_vector, triangle_normal, point);
						
						}
						tri[0].x = (int)v0.x + tRx;
						tri[0].y = (int)v0.y - tRy;
						tri[0].z = (int)v0.z;
						tri[1].x = (int)v1.x + tRx;
						tri[1].y = (int)v1.y - tRy;
						tri[1].z = (int)v1.z;
						tri[2].x = (int)v2.x + tRx;
						tri[2].y = (int)v2.y - tRy;
						tri[2].z = (int)v2.z;
						
						
						//Construct a rotation matrix
						/*
						float[] rotationMatrix = { 1, 0, 0,
						 		0, (float)Math.cos(rot), (float)-Math.sin(rot),
						 		0, (float)Math.sin(rot), (float)Math.cos(rot)};
						float[] trI = {tri[0].x, tri[0].y, tri[0].z,
								tri[1].x, tri[1].y, tri[1].z,
								tri[2].x, tri[2].y, tri[2].z,
						};
						*/

						if(phong){
							
							SketchBase.drawTrianglePhong(mats[k],buff,tri[0],tri[1],tri[2], n0, n1, n2, depthBuffer1, doSmooth, phong, combined, view_vector); 
						}
						else{
						
							SketchBase.drawTriangle(buff,tri[0],tri[1],tri[2], depthBuffer1, doSmooth, phong);      
						}    
					}
					
					v0 = mesh.v[i][j];
					v1 = mesh.v[i+1][j+1];
					v2 = mesh.v[i+1][j];
					triangle_normal = computeTriangleNormal(v0,v1,v2);
					
					if(view_vector.dotProduct(triangle_normal) > 0.0)  // front-facing triangle?
					{	
						if(gouraud == true && flat == false)
						{
							// vertex colors for Gouraud shading
							n0 = mesh.n[i][j];
							n1 = mesh.n[i+1][j+1];
							n2 = mesh.n[i+1][j];
							tri[0].c = combined.applyLight(mats[k], view_vector, n0, v0);
							tri[1].c = combined.applyLight(mats[k], view_vector, n1, v1);
							tri[2].c = combined.applyLight(mats[k], view_vector, n2, v2);
						}
						else if(phong == true && gouraud == false && flat == false)
						{
							n0 = mesh.n[i][j];
							n1 = mesh.n[i+1][j + 1];
							n2 = mesh.n[i + 1][j];
						}
						else 
						{
							// flat shading: use the normal to the triangle itself
							n2 = n1 = n0 =  triangle_normal;
							Vector3D point = new Vector3D((v0.x + v1.x + v2.x)/3, 
									(v0.y + v1.y + v2.y)/3, 
									(v0.z + v1.z + v2.z)/3);
								tri[2].c = tri[1].c = tri[0].c = combined.applyLight(mats[k], view_vector, triangle_normal, point);
							
						}	
			
						tri[0].x = (int)v0.x + tRx;
						tri[0].y = (int)v0.y - tRy;
						tri[0].z = (int)v0.z;
						tri[1].x = (int)v1.x + tRx;
						tri[1].y = (int)v1.y - tRy;
						tri[1].z = (int)v1.z;
						tri[2].x = (int)v2.x + tRx;
						tri[2].y = (int)v2.y - tRy;
						tri[2].z = (int)v2.z;
						
						if(phong){
							
							SketchBase.drawTrianglePhong(mats[k],buff,tri[0],tri[1],tri[2], n0, n1, n2, depthBuffer1, doSmooth, phong, combined, view_vector); 
						}
						else{
						
						SketchBase.drawTriangle(buff,tri[0],tri[1],tri[2], depthBuffer1, doSmooth, phong);      
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