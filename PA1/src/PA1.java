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
//


import javax.swing.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.*; 
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;//for new version of gl
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;

import com.jogamp.opengl.util.FPSAnimator;//for new version of gl


public class PA1 extends JFrame
	implements GLEventListener, KeyListener, MouseListener, MouseMotionListener
{
	private final int DEFAULT_WINDOW_WIDTH=512;
	private final int DEFAULT_WINDOW_HEIGHT=512;
	private final float DEFAULT_LINE_WIDTH=1.0f;

	private GLCapabilities capabilities;
	private GLCanvas canvas;
	private FPSAnimator animator;

	final private int numTestCase;
	private int testCase;
	private BufferedImage buff;
	private BufferedImage texture;
	private ColorType color;
	private Random rng;
	
	private ArrayList<Point2D> lineSegs;
	private ArrayList<Point2D> triangles;
	private boolean doSmoothShading;
	
	private boolean doTextureMapping;
	
	private int Nsteps;

	public PA1()
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
	    
	    numTestCase = 6;
	    testCase = -1;
	    Nsteps = 12;

	    setTitle("CS480/680 PA1");
	    setSize( DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setVisible(true);
	    setResizable(false);
	    
	    rng = new Random();
	    color = new ColorType(1.0f,0.0f,0.0f);
	    lineSegs = new ArrayList<Point2D>();
	    triangles = new ArrayList<Point2D>();
	    doSmoothShading = false;
	    doTextureMapping = false;
	    
	    try
	    {
	    	texture = ImageIO.read(new File("pattern.jpg"));
	    } catch (IOException e)
	    {
	    	System.out.println("Error: reading texture image.");
	    	e.printStackTrace();
	    }
	}

	public void run()
	{
		animator.start();
	}

	public static void main( String[] args )
	{
	    PA1 P = new PA1();
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
	  
		System.out.printf("Test case = %d\n",testCase);

		switch (testCase){
		case 0:
			lineTestPattern1(); 
			break;
		case 1:
			lineTestPattern2(); 
			break;
		case 2:
			lineTestPattern3(); 
			break;
		case 3:
			triTestPattern1(false); /*single color*/
			break;
		case 4:
			triTestPattern1(true); /*smooth shaded*/
			break;
		case 5:
			
			triTestPattern2(); /*texture mapping*/
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
	//		T,t: show testing examples
	//		>:	 increase the step number for examples
	//		<:   decrease the step number for examples

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
	    case 'S' :
	    case 's' :
	    	doSmoothShading = !doSmoothShading;
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
	    case 'm':
	    case 'M':
	    	doTextureMapping = !doTextureMapping;
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

	    // Add a new vertex using left mouse click
	    if ( button  == MouseEvent.BUTTON1 )
	    {
	    	int x = mouse.getX();
	    	int y = mouse.getY();
	    	lineSegs.add(new Point2D(x,y,color));
	    	
	    	if (lineSegs.size()%2 == 1)
	    		SketchBase.drawPoint(buff, lineSegs.get(lineSegs.size()-1));
	    	else if (lineSegs.size() > 0)
	    		SketchBase.drawLine(buff, lineSegs.get(lineSegs.size()-2),
	    				lineSegs.get(lineSegs.size()-1));
	    }
	    // Pick the vertex closest to mouse down event using right button
	    else if ( button == MouseEvent.BUTTON3 )
	    {
	    	int x = mouse.getX();
	    	int y = mouse.getY();
	    	triangles.add(new Point2D(x,y,color));
	    	
	    	if (triangles.size()%3 == 1)
	    		SketchBase.drawPoint(buff, triangles.get(triangles.size()-1));
	    	else if (triangles.size()%3 == 2)
	    		SketchBase.drawLine(buff, triangles.get(triangles.size()-2),
	    				triangles.get(triangles.size()-1));
	    	else if (triangles.size() > 0 && !doTextureMapping)
	    		SketchBase.drawTriangle(buff, triangles.get(triangles.size()-3),
	    				triangles.get(triangles.size()-2), triangles.get(triangles.size()-1), doSmoothShading);
	    	
	    	else if(triangles.size() > 0 && doTextureMapping)
	    		SketchBase.triangleTextureMap(buff,texture,triangles.get(triangles.size()-3),
	    				triangles.get(triangles.size()-2), triangles.get(triangles.size()-1)); 
     
	    }

	}

	public void mouseReleased(MouseEvent mouse)
	{
	    // Deliberately left blank
	}

	public void mouseMoved( MouseEvent mouse)
	{
		// Deliberately left blank
	}

	public void mouseDragged( MouseEvent mouse )
	{
		// Deliberately left blank
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
	// Testing Cases
	//************************************************** 
	
	void lineTestPattern1()
	{
		float theta,delta;
		float radius;
		int cx,cy,x,y;
		int i,n_steps = ( Nsteps == 18 )?9:Nsteps;
		Point2D v0 = new Point2D(),v1 = new Point2D(), v2= new Point2D();

		delta = (float)-Math.PI/n_steps;
		radius = buff.getWidth()*0.45f;

		/* center of pattern */
		v0.x = cx = buff.getWidth()/2;
		v0.y = cy = buff.getHeight()/2;
		v0.c.r = v0.c.g = 1.0f;
		v0.c.b = 0;

		for(i=0,theta=0;i<n_steps;++i,theta += delta)
		{
			x = (int)Math.round(Math.sin(theta)*radius);
			y = (int)Math.round(Math.cos(theta)*radius);

			v1.x = cx+x;
			v1.y = cy+y;

			v2.x = cx-x;
			v2.y = cy-y;

			v1.c.r = v2.c.r = 0; 
			v1.c.g = v2.c.b = ((float)(n_steps-i)/(float)n_steps);
			v1.c.b = v2.c.g = 0; 

			SketchBase.drawLine(buff,v0,v1);
			SketchBase.drawLine(buff,v0,v2);
	    }
	}
	
	// draw test line pattern 2: a flower with five petals 
	void lineTestPattern2()
	{
	  float theta,d_theta,d_petal,petal,radius,p;
	  int cx,cy;
	  int i;
	  int n_steps =( Nsteps == 18 )?64:4*Nsteps;
	  Point2D v0 = new Point2D(),v1 = new Point2D();

	  d_theta = (float)(2.0*Math.PI / n_steps);
	  d_petal = (float)(12.0*Math.PI / n_steps);

	  cx = buff.getWidth()/2;
	  cy = buff.getHeight()/2;

	  radius = (float) (0.75 * ((cx < cy) ? cx : cy));
	  p = (float) (radius*0.25);

	  /* draw the outer petals first */

	  v0.x = cx;
	  v0.y = (int)Math.floor(0.5 + radius + p) + cy;
	  v0.c.r = 1;
	  v0.c.g = (float) (128+Math.sin(2*Math.PI)*127.0)/255.0f;
	  v0.c.b = (float) (128+Math.cos(2*Math.PI)*127.0)/255.0f;

	  for(petal=d_petal,theta=d_theta,i=0;i<=n_steps;++i,theta+=d_theta,petal+=d_petal)
	    {
	      v1.x = (int)Math.floor(0.5 + radius*Math.sin(theta) + p*Math.sin(petal)) + cx;
	      v1.y = (int)Math.floor(0.5 + radius*Math.cos(theta) + p*Math.cos(petal)) + cy;

	      v1.c.r = 1;
	      v1.c.g = (float) ((128+Math.sin(theta*5)*127.0)/255.0f);
	      v1.c.b = (float) ((128+Math.cos(theta*5)*127.0)/255.0f);
	      SketchBase.drawLine(buff,v0,v1);

	      v0.x = v1.x;
	      v0.y = v1.y;
	      v0.c = v1.c;
	    }

	  /* draw circle at center */
	  radius *= 0.5;
	  v0.x = cx;
	  v0.y = (int)Math.floor(0.5 + radius) + cy;

	  /* color = orange */
	  v1.c.r = v0.c.r = 1;
	  v1.c.g = v0.c.g = 97.0f/255.0f;
	  v1.c.b = v0.c.b = 0; 

	  for(theta=d_theta,i=0;i<=n_steps;++i,theta+=d_theta)
	    {
	      v1.x = (int)Math.floor(0.5 + radius*Math.sin(theta)) + cx;
	      v1.y = (int)Math.floor(0.5 + radius*Math.cos(theta)) + cy;

	      SketchBase.drawLine(buff,v0,v1);

	      v0.x = v1.x;
	      v0.y = v1.y;
	    }
	}
	
	void lineTestPattern3()
	{
		float theta,delta;
		int i,cx,cy;
		float radius;
		int n_steps = Nsteps;
		Point2D[] tri = {new Point2D(), new Point2D(), new Point2D()};

		delta = (float) (2*Math.PI/n_steps);
		radius = (float) (buff.getWidth()*0.45);
		cx = (int) (buff.getWidth()/2.0);
		cy = (int) (buff.getHeight()/2.0);
	  
		for(i=0,theta=0;i<n_steps;++i,theta += delta)
		{
			tri[0].x = (int) (cx+Math.sin(theta)*radius);
			tri[0].y = (int) (cy+Math.cos(theta)*radius);
			tri[0].c.r = (float) (127+127*Math.sin(theta))/255.0f;
			tri[0].c.g = (float) (127+127*Math.sin(theta+(2*Math.PI/3)))/255.0f;
			tri[0].c.b = (float) (127+127*Math.sin(theta+(4*Math.PI/3)))/255.0f;
	      
			tri[1].x = (int) (cx+Math.sin(theta+delta)*radius);
			tri[1].y = (int) (cy+Math.cos(theta+delta)*radius);
			tri[1].c.r = (float) (127+127*Math.sin(theta+delta))/255.0f;
			tri[1].c.g = (float) (127+127*Math.sin(theta+delta+(2*Math.PI/3)))/255.0f;
			tri[1].c.b = (float) (127+127*Math.sin(theta+delta+(4*Math.PI/3)))/255.0f;
	      
			tri[2].x = cx;	
			tri[2].y = cy;	
			tri[2].c.r = 1.0f;
			tri[2].c.g = 1.0f;
			tri[2].c.b = 1.0f;
	      
			SketchBase.drawLine(buff,tri[0],tri[1]);
			SketchBase.drawLine(buff,tri[1],tri[2]);
			SketchBase.drawLine(buff,tri[0],tri[2]);
	    }
	}
	void triTestPattern1(boolean doSmooth)
	{
		float theta,delta;
		int i,cx,cy;
		float radius;
		int n_steps = Nsteps;
		Point2D[] tri = {new Point2D(), new Point2D(), new Point2D()};

		delta = (float) (2*Math.PI/n_steps);
		radius = (float) (buff.getWidth()*0.45);
		cx = (int) (buff.getWidth()/2.0);
		cy = (int) (buff.getHeight()/2.0);
	  
		for(i=0,theta=0;i<n_steps;++i,theta += delta)
	    {
			tri[0].x = (int) (cx+Math.sin(theta)*radius);
			tri[0].y = (int) (cy+Math.cos(theta)*radius);
			tri[0].c.r = (float) (127+127*Math.sin(theta))/255.0f;
			tri[0].c.g = (float) (127+127*Math.sin(theta+(2*Math.PI/3)))/255.0f;
			tri[0].c.b = (float) (127+127*Math.sin(theta+(4*Math.PI/3)))/255.0f;
	      
			tri[1].x = (int) (cx+Math.sin(theta+delta)*radius);
			tri[1].y = (int) (cy+Math.cos(theta+delta)*radius);
			tri[1].c.r = (float) (127+127*Math.sin(theta+delta))/255.0f;
			tri[1].c.g = (float) (127+127*Math.sin(theta+delta+(2*Math.PI/3)))/255.0f;
			tri[1].c.b = (float) (127+127*Math.sin(theta+delta+(4*Math.PI/3)))/255.0f;
	      
			tri[2].x = cx;	
			tri[2].y = cy;	
			tri[2].c.r = 1.0f;
			tri[2].c.g = 1.0f;
			tri[2].c.b = 1.0f;
	      
			SketchBase.drawTriangle(buff,tri[0],tri[1],tri[2],doSmooth);      
	    }
	}
	
	void triTestPattern2()
	{
		float theta,delta;
		int i,cx,cy;
		float radius;
		int n_steps = Nsteps;
		Point2D[] tri = {new Point2D(), new Point2D(), new Point2D()};

		delta = (float) (2*Math.PI/n_steps);
		radius = (float) (buff.getWidth()*0.45);
		cx = (int) (buff.getWidth()/2.0);
		cy = (int) (buff.getHeight()/2.0);
	  
		for(i=0,theta=0;i<n_steps;++i,theta += delta)
	    {
			tri[0].x = (int) (cx+Math.sin(theta)*radius);
			tri[0].y = (int) (cy+Math.cos(theta)*radius);
			tri[0].c.r = (float) (127+127*Math.sin(theta))/255.0f;
			tri[0].c.g = (float) (127+127*Math.sin(theta+(2*Math.PI/3)))/255.0f;
			tri[0].c.b = (float) (127+127*Math.sin(theta+(4*Math.PI/3)))/255.0f;
	      
			tri[1].x = (int) (cx+Math.sin(theta+delta)*radius);
			tri[1].y = (int) (cy+Math.cos(theta+delta)*radius);
			tri[1].c.r = (float) (127+127*Math.sin(theta+delta))/255.0f;
			tri[1].c.g = (float) (127+127*Math.sin(theta+delta+(2*Math.PI/3)))/255.0f;
			tri[1].c.b = (float) (127+127*Math.sin(theta+delta+(4*Math.PI/3)))/255.0f;
	      
			tri[2].x = cx;	
			tri[2].y = cy;	
			tri[2].c.r = 1.0f;
			tri[2].c.g = 1.0f;
			tri[2].c.b = 1.0f;
	      
			SketchBase.triangleTextureMap(buff,texture,tri[0],tri[1],tri[2]);      
	    }
	}
}