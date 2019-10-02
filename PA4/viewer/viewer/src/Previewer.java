/*******************************************************************
   Model Previewer 
********************************************************************
History :
1 Apr 08. Created by Tai-peng Tian based on the C++ version created
          by Ashwin Thangali.
5 Nov 14. Modified by Stan Sclaroff for PA4 assignment model viewer.
********************************************************************/

import java.io.*;
import java.util.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*; 

import com.jogamp.opengl.util.gl2.GLUT;//for new version of gl

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.awt.GLCanvas;//for new version of gl
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.*;


@SuppressWarnings("unused")
public class Previewer extends JFrame
  implements GLEventListener, KeyListener, MouseListener, MouseMotionListener
{
  
  private static final long serialVersionUID = 1L;
  private final int DEFAULT_WINDOW_WIDTH=512;
  private final int DEFAULT_WINDOW_HEIGHT=512;
  private final float DEFAULT_LINE_WIDTH=1.0f;

  private GLCapabilities capabilities;
  private GLCanvas canvas;
  private FPSAnimator animator;
  private GLU glu;
  private GLUT glut;

  private RenderObjectCollection objects;
  private MaterialCollection materials;
  private LightCollection lights;
  private Camera camera;

  public Previewer ( String filename )
  {
    capabilities = new GLCapabilities(null);
    capabilities.setDoubleBuffered(true);  // Enable Double buffering

    canvas  = new GLCanvas(capabilities);
    canvas.addGLEventListener(this);
    canvas.addMouseListener(this);
    canvas.addMouseMotionListener(this);
    canvas.addKeyListener(this);
    canvas.setAutoSwapBufferMode(true); // true by default. Just to be explicit
    getContentPane().add(canvas);

    animator = new FPSAnimator(canvas, 1); 

    glu  = new GLU();
    glut = new GLUT();

    objects   = new RenderObjectCollection();
    materials = new MaterialCollection();
    lights    = new LightCollection();
    camera    = new Camera();

    readModelFile( filename );

    setTitle("CS480/CS680 Model Previewer");
    setSize( camera.viewportWidth , camera.viewportHeight );
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);

    // For debugging purposes
    // camera.print();
    // objects.print();
    // lights.print();
    // materials.print();
  }

  public void run()
  {
    animator.start();
  }

  public static void main( String[] args )
  {
    if (args.length < 1) {
      System.out.println( "Usage: previewer <model_file>\n" );
      System.exit(1);
    }

    Previewer P = new Previewer( args[0] );
    P.run();
  }

  //*********************************************** 
  // Parse contents of the file 
  //*********************************************** 
  private void readModelFile( String filename)
  {
    try 
    {
      BufferedReader input = new BufferedReader( new FileReader(filename) );

      try 
      {
        String line = null;
        while ( (line = input.readLine()) != null )
        {
          StringTokenizer stok = new StringTokenizer( line );
          if (stok.hasMoreTokens())
          {
            String tok = stok.nextToken();

            if (tok.equals( "obj" )) objects.newObject( stok );
            else if (tok.equals( "light" )) lights.newLight( stok );
            else if (tok.equals( "mat" )) materials.newMaterial( stok );
            else if (tok.equals( "resolution" )) camera.setResolution( stok );
            else if (tok.equals( "camera" )) camera.setParameters( stok );
            else if (tok.equals( "viewport" )) camera.setViewport( stok );
            else if (tok.equals( "render" )) objects.setRenderObjIds( stok );
          }

        }
      }
      finally
      {
        input.close();
      }
      
    }
    catch (IOException ex) 
    {
      System.err.println( "File input error" );
    }

    objects.reorganizeObjects(); // remove objects that are not specified 
                                 // in render option.
  }


  //*********************************************** 
  //  GLEventListener Interfaces
  //*********************************************** 
  public void init( GLAutoDrawable drawable) 
  {
    GL2 gl = (GL2)drawable.getGL();
    gl.glClearColor( 0.0f, 0.0f, 0.0f, 0.0f);

    gl.glEnable( GL2.GL_DEPTH_TEST );
    gl.glEnable( GL2.GL_NORMALIZE );
    gl.glShadeModel( GL2.GL_SMOOTH );

    camera.setCamera( camera.viewportWidth, camera.viewportHeight, gl );
    
    lights.setLights( gl );
  }

  // Redisplaying graphics
  public void display(GLAutoDrawable drawable)
  {
    GL2 gl = (GL2)drawable.getGL();

    /* clear the display */
    gl.glClear( GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT );

	gl.glPushMatrix();
	gl.glTranslatef(0f,0f,0.001f);
	gl.glPopMatrix();

    objects.render( gl, materials );

  }

  public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h)
  {
  }

  public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
      boolean deviceChanged)
  {
  }


  //*********************************************** 
  //          KeyListener Interfaces
  //*********************************************** 
  public void keyTyped(KeyEvent key)
  {
  //      Q,q: quit

    switch ( key.getKeyChar() ) {
        case 'Q' :
        case 'q' : new Thread() {
                     public void run()
                     { animator.stop(); }
                   }.start();
                   System.exit(0);
                   break;
        default :
          break;
    }
 }

  public void keyPressed(KeyEvent key)
  {
    switch (key.getKeyCode()) {
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
  }

  //************************************************** 
  // MouseListener and MouseMotionListener Interfaces
  //************************************************** 
  public void mouseClicked(MouseEvent mouse)
  {
  }

  public void mousePressed(MouseEvent mouse)
  {
  }

  public void mouseReleased(MouseEvent mouse)
  {
  }

  public void mouseMoved( MouseEvent mouse)
  {
  }

  public void mouseDragged( MouseEvent mouse )
  {
  }

  public void mouseEntered( MouseEvent mouse)
  {
  }

  public void mouseExited( MouseEvent mouse)
  {
  }

@Override
public void dispose(GLAutoDrawable arg0) {
	
} 


}


