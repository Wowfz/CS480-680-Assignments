/*
 * Predator.java
 *
 * Lunhao Liang - lunhaol@bu.edu
 *
 * Draws predator in the tank and predator will chase fishes and eat them.
 */
import com.jogamp.opengl.util.gl2.GLUT;
import javax.media.opengl.GL2;
import java.util.Random;

public class Predator
{
  private int predator_object;
    private int body, fin_up, fin_down_1, fin_down_2, tail, beard, sphere_collision;
  private float scale;
  private float fin_angle, tail_angle;
  private float fin_rotation, tail_rotation;
  public float x,y,z;
    public float last_x,last_y,last_z;
  private float vx,vy,vz;
    private float dirx,diry,dirz;
  private Random rand = new Random();
  public float r_collisionsphere = 0.3f;
  public boolean sphere_collision_show = false;

    private float Prey_Potential_Weight = -0.5f;
    private float Wall_Potential_Weight = 0.1f;
    private Fish prey = null;
    public float red_color,blue_color;
    /**
   * The default number of slices to use when drawing the sphere.
   */
  public static final int DEFAULT_SLICES = 36;
  /**
   * The default number of stacks to use when drawing the sphere.
   */
  public static final int DEFAULT_STACKS = 28;

  public Predator(float scale_, float x_, float y_, float z_)
  {
      red_color = 1f;
      blue_color = 0f;
    scale = scale_;
    fin_angle = 0;
      fin_rotation = 1;
    tail_angle = 0;
      tail_rotation = 1;
    last_x = x = x_;
    last_y = y = y_;
    last_z = z = z_;
    dirx = -1;
    diry = -1;
    dirz = -1;
        vx = 0.001f;
        vy = 0.001f;
        vz = 0.001f;
  }

  public void init( GL2 gl )
  {
      // draw all parts of the predator
      draw_sphere_collision(gl);
     draw_body(gl);
     draw_fin_up(gl);
     draw_fin_down_1(gl);
     draw_fin_down_2(gl);
     draw_tail(gl);
     draw_beard(gl);

    predator_object = gl.glGenLists(1);
    gl.glNewList( predator_object, GL2.GL_COMPILE );
	    GLUT glut = new GLUT();
	    // update the location and orientation
      construct_disp_list( gl );
      gl.glEndList();
  }
    //draw body of predator
    private void draw_body(GL2 gl){
        body = gl.glGenLists(1);
        gl.glNewList(body, GL2.GL_COMPILE );
        GLUT glut = new GLUT();
        gl.glPushMatrix();
        gl.glScalef(4f, 1.5f, 1.5f);
        glut.glutSolidSphere(0.1,DEFAULT_SLICES,DEFAULT_STACKS);
        gl.glPopMatrix();
        gl.glEndList();
    }
        //draw upper fin of predator
    private void draw_fin_up(GL2 gl){
        fin_up = gl.glGenLists(1);
        gl.glNewList(fin_up, GL2.GL_COMPILE );
        GLUT glut = new GLUT();
        gl.glPushMatrix();
        gl.glTranslated(0f, 0.15f, 0f);
        gl.glScaled(1.1,1,0.3);
        glut.glutSolidSphere(0.1,DEFAULT_SLICES,DEFAULT_STACKS);
        gl.glPopMatrix();
        gl.glEndList();
    }
        //draw downer fin1 of predator
    private void draw_fin_down_1(GL2 gl){
        fin_down_1 = gl.glGenLists(1);
        gl.glNewList(fin_down_1, GL2.GL_COMPILE );
        GLUT glut = new GLUT();
        gl.glPushMatrix();
        gl.glTranslated(0f, -0.15f, 0.05f);
        gl.glRotated(-45,1,0,0);
        gl.glScaled(1.1,1,0.3);
        glut.glutSolidSphere(0.1,DEFAULT_SLICES,DEFAULT_STACKS);
        gl.glPopMatrix();
        gl.glEndList();
    }
        //draw downer fin2 of predator
    private void draw_fin_down_2(GL2 gl){
    fin_down_2 = gl.glGenLists(1);
    gl.glNewList(fin_down_2, GL2.GL_COMPILE );
    GLUT glut = new GLUT();
    gl.glPushMatrix();
    gl.glTranslated(0f, -0.15f, -0.05f);
    gl.glRotated(45,1,0,0);
    gl.glScaled(1.1,1,0.3);
    glut.glutSolidSphere(0.1,DEFAULT_SLICES,DEFAULT_STACKS);
    gl.glPopMatrix();
    gl.glEndList();
}
      //draw tail of predator
    private void draw_tail(GL2 gl){
    tail = gl.glGenLists(1);
    gl.glNewList(tail, GL2.GL_COMPILE );
    GLUT glut = new GLUT();
    gl.glPushMatrix();
    gl.glTranslated(.4f, 0f, 0f);
    gl.glScaled(1,1.5,0.3);
    glut.glutSolidSphere(0.1,DEFAULT_SLICES,DEFAULT_STACKS);
    gl.glPopMatrix();
    gl.glEndList();
}
      //draw beard of predator
    private void draw_beard(GL2 gl){
        beard = gl.glGenLists(1);
        gl.glNewList(beard, GL2.GL_COMPILE );
        GLUT glut = new GLUT();
        gl.glPushMatrix();
        gl.glTranslated(-0.3f, -0.07f, 0f);
        gl.glScaled(1.1,1,10);
        glut.glutSolidSphere(0.03,DEFAULT_SLICES,DEFAULT_STACKS);
        gl.glPopMatrix();
        gl.glEndList();
    }
      //draw wire sphererd of predator
    private void draw_sphere_collision(GL2 gl) {
        sphere_collision = gl.glGenLists(1);
        gl.glNewList(sphere_collision, GL2.GL_COMPILE );
        GLUT glut = new GLUT();
        gl.glPushMatrix();
        glut.glutWireSphere(r_collisionsphere, 36, 24);
        gl.glPopMatrix();
        gl.glEndList();
    }

    //draw the model and make the it face to the direction it moves
    private void construct_disp_list( GL2 gl)
    {
        gl.glPushMatrix();
        gl.glScalef(scale, scale, scale);

        // get the transforming matrix that make the model face the direction of movement

        // get the move distance in x y z in last frame, it is the velocity on x y z axis
        float dx = -last_x;
        float dy = -last_y;
        float dz = -last_z;

        // normalized
        float mag = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        // store the velocity vector
        float[] v = new float[3];
        v[0] = dx / mag;
        v[1] = dy / mag;
        v[2] = dz / mag;

        // assume up vector is 0,1,0
        float[] up = { 0.0f, 1.0f, 0.0f };

        // cross product v vector and up vector get right vector
        float[] right = {v[1] * up[2] - up[1] * v[2], v[2] * up[0] - v[0] * up[2], v[0] * up[1] - v[1] * up[0]};

        // use v, right, up vector and x,y,z to make new transforming matrix
        float[] rotationMatrix = { v[0], v[1], v[2], 0.0f, up[0], up[1], up[2], 0.0f, right[0], right[1], right[2], 0.0f, x, y, z, 1.0f };

        // use current matrix to multiply the new transforming matrix to change the model's location and face to direction
        gl.glMultMatrixf(rotationMatrix, 0);

        // set predator's color
        gl.glColor3f(blue_color, 1f, red_color); //blue
        //gl.glRotated(angle, 1, 1, 0);
        //gl.glRotated(tailAngle + 90, 0, 1, 0);

        //body
        gl.glPushMatrix();
        gl.glCallList( body );
        gl.glPopMatrix();
        //fin_up
        gl.glPushMatrix();
        gl.glCallList(fin_up);
        gl.glPopMatrix();
        //fin_down_1
        gl.glPushMatrix();
        fin_angle += fin_rotation;
        if (fin_angle > 15 || fin_angle < -15) {
            fin_rotation *= -1;
        }
        gl.glRotated(fin_angle,1,0,0);
        gl.glCallList(fin_down_1);
        gl.glPopMatrix();
        //fin_down_2
        gl.glPushMatrix();
        gl.glRotated(-fin_angle,1,0,0);
        gl.glCallList(fin_down_2);
        gl.glPopMatrix();
        //tail
        gl.glPushMatrix();
        tail_angle += tail_rotation;
        if (tail_angle > 20 || tail_angle < -20) {
            tail_rotation *= -1;
        }
        gl.glRotated(tail_angle,0,1,0);
        gl.glCallList(tail);
        gl.glPopMatrix();
        //beard
        gl.glPushMatrix();
        gl.glCallList(beard);
        gl.glPopMatrix();

        if(sphere_collision_show) {
            //sphere collision
            gl.glPushMatrix();
            gl.glCallList(sphere_collision);
            gl.glPopMatrix();
        }

        gl.glPopMatrix();
    }

  public void update( GL2 gl )
  {
      gl.glNewList(predator_object, GL2.GL_COMPILE );

      // calculate move distance in last frame
      last_x = dirx;
      last_y = diry;
      last_z = dirz;

      // calculate potential function and output new dirx, diry, dirz
      calcPotential();

      // wall collision if predator hit the wall!
      float nx = rand.nextFloat();
      while (nx < 0.2f) {
          nx = rand.nextFloat(); }
      float ny = rand.nextFloat();
      while (ny < 0.2f) {
          ny = rand.nextFloat(); }
      float nz = rand.nextFloat();
      while (nz < 0.2f) {
          nz = rand.nextFloat(); }
      if (x > 1.8 || x < -1.8) {
          if(x > 1.8){
              x = 1.8f;
              dirx = -nx;
          }
          else {
              x = -1.8f;
              dirx = nx;
          }
      }
      if (y > 1.8 || y < -1.8) {
          if(y > 1.8){
              y = 1.8f;
              diry = -nx;
          }
          else {
              y = -1.8f;
              diry = nx;
          }
      }
      if (z > 1.8 || z < -1.8) {
          if(z > 1.8){
              z = 1.8f;
              dirz = -nx;
          }
          else {
              z = -1.8f;
              dirz = nx;
          }
      }

      // use the new dirx, diry, dirz to calculate new x y z
      x+=vx*dirx;
      y+=vy*diry;
      z+=vz*dirz;

        // draw the new model in new location and direction
      construct_disp_list( gl );
      gl.glEndList();
  }

  public void draw( GL2 gl )
  {
      gl.glPushMatrix();
      gl.glCallList( predator_object );
      gl.glPopMatrix();
  }

    // potential function using Gaussian Potential
    private Coord Gaussian_Potential_Function(Coord p, Coord q1, float weight) {
        float x = (float) (2*weight*(p.x - q1.x)*Math.pow(Math.E,-1*(Math.pow((p.x-q1.x), 2) + Math.pow((p.y-q1.y), 2) + Math.pow((p.z-q1.z), 2)) ));
        float y = (float) (2*weight*(p.y - q1.y)*Math.pow(Math.E,-1*(Math.pow((p.x-q1.x), 2) + Math.pow((p.y-q1.y), 2) + Math.pow((p.z-q1.z), 2)) ));
        float z = (float) (2*weight*(p.z - q1.z)*Math.pow(Math.E,-1*(Math.pow((p.x-q1.x), 2) + Math.pow((p.y-q1.y), 2) + Math.pow((p.z-q1.z), 2)) ));
        Coord potential = new Coord(x, y, z);
        return potential;
    }

    // sum the potential function
    private void calcPotential() {
        // the predator
        Coord p = new Coord(x,y,z);
        // the prey if it exists
        Coord q1;
        if (prey != null) q1 = new Coord(prey.x, prey.y, prey.z);
        else q1 = new Coord(999999,999999,999999);
        // the six walls of the tank
        Coord q2 = new Coord(1.9, y, z);
        Coord q3 = new Coord(-1.9, y, z);
        Coord q4 = new Coord(x, 1.9, z);
        Coord q5 = new Coord(x, -1.9, z);
        Coord q6 = new Coord(x, y, 1.9);
        Coord q7 = new Coord(x, y, -1.9);

        // calculate and sum all the potential function of q1,q2,q3,q4,q5,q6,q7
        Coord[] coords = {Gaussian_Potential_Function(p,q1,Prey_Potential_Weight), Gaussian_Potential_Function(p,q2, Wall_Potential_Weight),
                Gaussian_Potential_Function(p,q3,Wall_Potential_Weight), Gaussian_Potential_Function(p,q4,Wall_Potential_Weight),
                Gaussian_Potential_Function(p,q5,Wall_Potential_Weight), Gaussian_Potential_Function(p,q6,Wall_Potential_Weight),
                Gaussian_Potential_Function(p,q7,Wall_Potential_Weight)};
        Coord potential = new Coord(0,0,0);
        for(int i = 0; i < coords.length; i++)
        {
            potential.x += coords[i].x;
            potential.y += coords[i].y;
            potential.z += coords[i].z;
        }

        // update the new dirx, diry, dirz
        dirx += potential.x;
        diry += potential.y;
        dirz += potential.z;

    }

    // get the prey information for predator
    public void getprey(Fish f) {
        prey = f;
    }

    // delete the prey information for predator
    public void deleteprey() {
        prey = null;
    }
}
