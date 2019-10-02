import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.*;
import java.awt.event.*;

//import java.awt.*;


public class PA3_stereo extends JFrame
  implements GLEventListener, KeyListener, MouseListener, MouseMotionListener
{
  /**
	 *
	 */
  private static final long serialVersionUID = 1L;
  private final int DEFAULT_WINDOW_WIDTH =512;
  private final int DEFAULT_WINDOW_HEIGHT=512;

  private GLCapabilities capabilities;
  private GLCanvas canvas;
  private FPSAnimator animator;
  private GLU glu;
  @SuppressWarnings("unused")
  private GLUT glut;
  private Vivarium vivarium;
  private Quaternion viewing_quaternion; // world rotation controlled by mouse actions

  // State variables for the mouse actions
  int last_x, last_y;
  boolean rotate_world;

  // Initialize parameter
  float PI=3.1415926f;
  float Fd=5.0f; //fusion distance  
  float RealScreenToEyeDistance=1.0f;
  float R=Fd/RealScreenToEyeDistance; //ratio R=Fd/RealScreenToEyeDistance  
  float Sd=0.05f; //distance between 2 eyes  
  float aspect=1.0f; // height and width ratio of window
  //float fovy=60.0f; //view angle between 2 eyes  
  //float f=(float)(1/Math.tan((fovy*PI)/(2*180))); //f=ctg(fovy/2);  
  float angle = (float)(Math.atan(Fd/(Sd/2.0f))*180.0f/Math.PI);
    float red_color = 1.0f;
    float blue_color = 1.0f;

  public PA3_stereo()
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

    animator = new FPSAnimator(canvas, 60); // drive the display loop @ 60 FPS

    glu  = new GLU();
    glut = new GLUT();

    setTitle("CS480/CS680 : Vivarium_Stereo");
    setSize( DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
   
    last_x = last_y = 0;
    rotate_world = false;

    // Set initialization code for user created classes that involves OpenGL
    // calls after here. After this line, the opengGl context will be
    // correctly initialized.
    vivarium = new Vivarium(  );
    viewing_quaternion = new Quaternion();
  }

  public void run()
  {
    animator.start();
  }

  public static void main( String[] args )
  {
    PA3_stereo P = new PA3_stereo();
    P.run();
  }

  //***************************************************************************
  //GLEventListener Interfaces
  //***************************************************************************
  //
  // Place all OpenGL related initialization here. Including display list
  // initialization for user created classes
  //
  public void init( GLAutoDrawable drawable) 
  {
    GL2 gl = (GL2)drawable.getGL();

    /* set up for shaded display of the vivarium*/
    float light0_position[] = {0,0,0,1};
    float light0_ambient_color[] = {0.25f,0.25f,0.25f,1};
    float light0_diffuse_color[] = {1,1,1,1};

    gl.glPolygonMode(GL.GL_FRONT,GL2.GL_FILL);
    gl.glEnable(GL2.GL_COLOR_MATERIAL);
    gl.glColorMaterial(GL.GL_FRONT,GL2.GL_AMBIENT_AND_DIFFUSE);

    gl.glClearColor(0.0f,0.0f,0.0f,0.0f);
    gl.glShadeModel(GL2.GL_SMOOTH);
    
    /* set up the light source */
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light0_position, 0);
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, light0_ambient_color, 0);
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, light0_diffuse_color, 0);

    /* turn lighting and depth buffering on */
    gl.glEnable(GL2.GL_LIGHTING);
    gl.glEnable(GL2.GL_LIGHT0);
    gl.glEnable(GL2.GL_DEPTH_TEST);
    gl.glEnable(GL2.GL_NORMALIZE);

    vivarium.init( gl );
  }

  // Redisplaying graphics
  public void display(GLAutoDrawable drawable)
  {

    float matrix[]={0.f};

    GL2 gl = (GL2)drawable.getGL();

    gl.glColorMask(true, true,true,true);
    gl.glClearColor(0.0f,0.0f,0.0f,1.0f);
    gl.glClearDepth(1.0);

    // clear the display 
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    //gl.glColor3f(1.0f, 1.0f, 1.0f);

    // left eye


//---------------------------------------------------------------------------------------------  
//Left Viewport  
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glLoadIdentity();
      gl.glPushMatrix();
      gl.glDrawBuffer(GL2.GL_BACK_LEFT);
    gl.glTranslated(-Sd/2.f,0.0,0);
    gl.glRotatef(-angle, 0.f, 1.f, 0.f);

        //gl.glGetFloatv(GL2.GL_PROJECTION_MATRIX,matrix,0);
        //gl.glMultMatrixf(matrix,0);
        gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX,matrix,0);
        gl.glMultMatrixf(matrix,0);
        gl.glColorMask(true,false,false,true);
        gl.glColor3f(red_color, 1.0f, blue_color);
        gl.glMultMatrixf( viewing_quaternion.to_matrix(), 0 );

      gl.glPushMatrix();
      {
        vivarium.update( gl );
        vivarium.draw( gl );
      }
      gl.glPopMatrix();
      gl.glPopMatrix();

    //---------------------------------------------------------------------------------------------  
    // Right Viewport  
      gl.glPushMatrix();
      gl.glDrawBuffer(GL2.GL_BACK_RIGHT);
      gl.glTranslated(Sd/2.f,0.0,0);
      gl.glRotatef(angle, 0.f, 1.f, 0.f);

      //gl.glGetFloatv(GL2.GL_PROJECTION_MATRIX,matrix,0);
      //gl.glMultMatrixf(matrix,0);
      gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX,matrix,0);
      gl.glMultMatrixf(matrix,0);
      gl.glColorMask(false,false,true,true);

      gl.glMultMatrixf( viewing_quaternion.to_matrix(), 0 );

      gl.glPushMatrix();
      {

          vivarium.update( gl );
          vivarium.draw( gl );

      }
      gl.glPopMatrix();
      gl.glPopMatrix();
    //drawable.swapBuffers();
    //gl.glutSwapBuffers();
  }

  // Window size change
  public void reshape(GLAutoDrawable drawable, int x, int y, 
                            int width, int height)
  {
    // Change viewport dimensions
    GL2 gl = (GL2)drawable.getGL();

    // Prevent a divide by zero, when window is too short (you cant make a
    // window of zero width).
    if(height == 0) height = 1;

    double ratio = 1.0f * width / height;

    // Reset the coordinate system before modifying 
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glLoadIdentity();
    
    // Set the viewport to be the entire window 
    gl.glViewport(0, 0, width, height);
    
    // Set the clipping volume 
    glu.gluPerspective(25,width / height,0.1,100);

    // Camera positioned at (0,0,6), look at point (0,0,0), Up Vector (0,1,0)
    glu.gluLookAt(0,0,12,0,0,0,0,1,0);

    gl.glMatrixMode(GL2.GL_MODELVIEW);
    
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
      switch ( key.getKeyChar() ) {
        case 'Q' :
        case 'q' : new Thread() {
                     public void run()
                     { animator.stop(); }
                   }.start();
                   System.exit(0);
                   break;

        // set the viewing quaternion to 0 rotation 
        case 'R' :
        case 'r' : 
                   viewing_quaternion.reset(); 
                   break;

        // set the viewing quaternion to 0 rotation
        case 'F' :
        case 'f' :
          vivarium.addfood();
          break;

          case 'A' :
          case 'a' : {
              if(Sd > 0)
              Sd -= 0.01f;
             break;}

          case 'D' :
          case 'd' : {
              if(Sd < 0.1)
                  Sd += 0.01f;
              break;}

          case 'W' :
          case 'w' : {
              if(vivarium.tank.red_color < 1.0f)
                  vivarium.tank.red_color += 0.05f;
              if(vivarium.predators.get(0).red_color < 1.0f)
                  vivarium.predators.get(0).red_color += 0.05f;
              if(vivarium.fishes.get(0).fishcolor[2] < 1.0f)
                  vivarium.fishes.get(0).fishcolor[2] += 0.05f;
              break;}

          case 'S' :
          case 's' : {
              if(vivarium.tank.red_color > 0f)
                  vivarium.tank.red_color -= 0.05f;
              if(vivarium.predators.get(0).red_color > 0f)
                  vivarium.predators.get(0).red_color -= 0.05f;
              if(vivarium.fishes.get(0).fishcolor[2] > 0f)
                  vivarium.fishes.get(0).fishcolor[2] -= 0.05f;
              break;}

          case 'T' :
          case 't' : {
              if(vivarium.tank.blue_color < 1.0f)
                  vivarium.tank.blue_color += 0.05f;
              if(vivarium.predators.get(0).blue_color < 1.0f)
                  vivarium.predators.get(0).blue_color += 0.05f;
              if(vivarium.fishes.get(0).fishcolor[0] < 1.0f)
                  vivarium.fishes.get(0).fishcolor[0] += 0.05f;
              break;}

          case 'G' :
          case 'g' : {
              if(vivarium.tank.blue_color > 0f)
                  vivarium.tank.blue_color -= 0.05f;
              if(vivarium.predators.get(0).blue_color > 0f)
                  vivarium.predators.get(0).blue_color -= 0.05f;
              if(vivarium.fishes.get(0).fishcolor[0] > 0f)
                  vivarium.fishes.get(0).fishcolor[0] -= 0.05f;
              break;}
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
  }

  public void mouseDragged( MouseEvent mouse )
  {
    if (rotate_world)
    {
      // vector in the direction of mouse motion
      int x = mouse.getX();
      int y = mouse.getY();
      float dx = x - last_x;
      float dy = y - last_y;
     
      // spin around axis by small delta
      float mag = (float) Math.sqrt( dx*dx + dy*dy );
      if(mag < 0.0001)
    	  return;
     
      float[] axis = new float[3];
      axis[0] = dy/ mag;
      axis[1] = dx/ mag;
      axis[2] = 0.0f;

      // calculate appropriate quaternion
      float viewing_delta = 3.1415927f / 180.0f; // 1 degree
      float s = (float) Math.sin( 0.5f*viewing_delta );
      float c = (float) Math.cos( 0.5f*viewing_delta );

      Quaternion Q = new Quaternion( c, s*axis[0], s*axis[1], s*axis[2]);
      viewing_quaternion = Q.multiply( viewing_quaternion );

      // normalize to counteract acccumulating round-off error
      viewing_quaternion.normalize();

      // Save x, y as last x, y
      last_x = x;
      last_y = y;
    }
  }

  public void mouseEntered( MouseEvent mouse)
  {
  }

  public void mouseExited( MouseEvent mouse)
  {
  }

public void dispose(GLAutoDrawable drawable) {
	// TODO Auto-generated method stub
	
}
}
