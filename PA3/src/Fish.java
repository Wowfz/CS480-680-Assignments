/*
 * Fish.java
 *
 * Lunhao Liang - lunhaol@bu.edu
 *
 * Draws fishes in the tank and predator will chase fishes and eat them.
 */
import com.jogamp.opengl.util.gl2.GLUT;

import javax.media.opengl.GL2;
import java.util.Random;

public class Fish
{
    private Vivarium vivarium;
  private int prey_object;
    private int body, tail, sphere_collision;
  private float scale;
  private float tail_angle,tail_rotation;
  public float x,y,z;
    public float last_x,last_y,last_z;
  private float vx,vy,vz;
    private float dirx,diry,dirz;
  private Random rand = new Random();
  public float[] fishcolor = new float[3];
  public boolean dead;
  private  float r_collisionsphere = 0.2f;
    public boolean sphere_collision_show = false;

    private float Predator_Potential_Weight = 0.5f;
    private float Wall_Potential_Weight = 0.1f;
    private float Food_Potential_Weight = -0.6f;
    private Predator predator = null;
    private Food food = null;

    /**
   * The default number of slices to use when drawing the sphere.
   */
  public static final int DEFAULT_SLICES = 36;
  /**
   * The default number of stacks to use when drawing the sphere.
   */
  public static final int DEFAULT_STACKS = 28;

  public Fish(float scale_, float x_, float y_, float z_, Vivarium vivarium_)
  {
    scale = scale_;
      tail_angle = 0;
      tail_rotation = 1;
    x = x_;
    y = y_;
    z = z_;
      vx = 0.003f;
      vy = 0.003f;
      vz = 0.003f;
      dirx = 1;
      diry = 1;
      dirz = 1;
      dead = false;
      vivarium = vivarium_;
  }

  public void init( GL2 gl )
  {
    // set color when fishes alive
      fishcolor[0] = 0.85f;
      fishcolor[1] = 0.55f;
      fishcolor[2] = 0.20f;

      // draw all parts of the predator
      draw_body(gl);
      draw_tail(gl);
      draw_sphere_collision(gl);

    prey_object = gl.glGenLists(1);
    gl.glNewList( prey_object, GL2.GL_COMPILE );
        GLUT glut = new GLUT();
      // update the location and orientation

      construct_disp_list( gl );
    gl.glEndList();
  }

    //draw body of fishes
    private void draw_body(GL2 gl){
        body = gl.glGenLists(1);
        gl.glNewList(body, GL2.GL_COMPILE );
        GLUT glut = new GLUT();
        gl.glPushMatrix();
        gl.glScalef(2.0f, 1.0f, 1.0f);
        glut.glutSolidSphere(0.1,DEFAULT_SLICES,DEFAULT_STACKS);
        gl.glPopMatrix();
        gl.glEndList();
    }
    //draw tail of fishes
    private void draw_tail(GL2 gl){
        tail = gl.glGenLists(1);
        gl.glNewList(tail, GL2.GL_COMPILE );
        GLUT glut = new GLUT();
        gl.glPushMatrix();
        gl.glTranslated(.3f, 0f, 0f);
        gl.glRotated(-90,0,1,0);
        glut.glutSolidCone(0.1,0.1,DEFAULT_SLICES,DEFAULT_STACKS);
        gl.glPopMatrix();
        gl.glEndList();
    }
    //draw wire sphere of fishes
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
    private void construct_disp_list( GL2 gl) {
        gl.glPushMatrix();
        //gl.glTranslatef(x, y, z);
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

        mag = (float)Math.sqrt(right[0] * right[0] + right[1] * right[1] + right[2] * right[2]);
        right[0] = right[0]/mag;
        right[1] = right[1]/mag;
        right[2] = right[2]/mag;
        // use v, right, up vector and x,y,z to make new transforming matrix
        float[] rotationMatrix = { v[0], v[1], v[2], 0.0f, up[0], up[1], up[2], 0.0f, right[0], right[1], right[2], 0.0f, x, y, z, 1.0f };

        // use current matrix to multiply the new transforming matrix to change the model's location and face to direction
        gl.glMultMatrixf(rotationMatrix, 0);

        // set fish's color
        gl.glColor3f(fishcolor[0], fishcolor[1], fishcolor[2]); //orange
        //gl.glRotated(angle, 1, 1, 0);
        //gl.glRotated(tailAngle + 90, 0, 1, 0);

        //body
        gl.glPushMatrix();
        gl.glCallList( body );
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
      gl.glNewList(prey_object, GL2.GL_COMPILE );
      // calculate move distance in last frame
      last_x = dirx;
      last_y = diry;
      last_z = dirz;
      // calculate potential function and output new dirx, diry, dirz
      calcPotential();

      // do wall collision and reflect after touch the walls

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

      // detect if predator eat the fish and the fish die
      if (Math.sqrt(Math.pow((x-predator.x),2)+Math.pow((y-predator.y),2)+Math.pow((z-predator.z),2)) < predator.r_collisionsphere) {
          //set dead color
          fishcolor[0] = 1;
          fishcolor[1] = 0;
          fishcolor[2] = 0;
          // if dead set the dead flag and delete the prey object for predator
          predator.deleteprey();
          dead = true;
      }
      // detect if fish eat the food and the food is eaten
      for (Food f : vivarium.foods) {
          if (Math.sqrt(Math.pow((x-f.x),2)+Math.pow((y-f.y),2)+Math.pow((z-f.z),2)) < 0.3) {
              f.eat = true;
          }
      }

      //calculate new x,y,z
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
      gl.glCallList( prey_object );
      gl.glPopMatrix();
  }

    // potential function using Gaussian Potential
    private Coord potentialFunction(Coord p, Coord q1, float weight) {
        float x = (float) (2*weight*(p.x - q1.x)*Math.pow(Math.E,-1*(Math.pow((p.x-q1.x), 2) + Math.pow((p.y-q1.y), 2) + Math.pow((p.z-q1.z), 2)) ));
        float y = (float) (2*weight*(p.y - q1.y)*Math.pow(Math.E,-1*(Math.pow((p.x-q1.x), 2) + Math.pow((p.y-q1.y), 2) + Math.pow((p.z-q1.z), 2)) ));
        float z = (float) (2*weight*(p.z - q1.z)*Math.pow(Math.E,-1*(Math.pow((p.x-q1.x), 2) + Math.pow((p.y-q1.y), 2) + Math.pow((p.z-q1.z), 2)) ));
        Coord potential = new Coord(x, y, z);
        return potential;
    }

    // sum the potential function
    private void calcPotential() {
        // the fish
        Coord p = new Coord(x,y,z);
        // the predator if it exists
        Coord q1;
        if (predator != null) q1 = new Coord(predator.x, predator.y, predator.z);
        else q1 = new Coord(999999,9999999,999999);
        // the six walls of the tank
        Coord q2 = new Coord(1.8, y, z);
        Coord q3 = new Coord(-1.8, y, z);
        Coord q4 = new Coord(x, 1.8, z);
        Coord q5 = new Coord(x, -1.8, z);
        Coord q6 = new Coord(x, y, 1.8);
        Coord q7 = new Coord(x, y, -1.8);

        // calculate and sum all the potential function of q1,q2,q3,q4,q5,q6,q7
        Coord[] coords = {potentialFunction(p,q1,Predator_Potential_Weight), potentialFunction(p,q2, Wall_Potential_Weight),
                potentialFunction(p,q3,Wall_Potential_Weight), potentialFunction(p,q4,Wall_Potential_Weight),
                potentialFunction(p,q5,Wall_Potential_Weight), potentialFunction(p,q6,Wall_Potential_Weight),
                potentialFunction(p,q7,Wall_Potential_Weight)};
        Coord potential = new Coord(0,0,0);
        for(int i = 0; i < coords.length; i++)
        {
            potential.x += coords[i].x;
            potential.y += coords[i].y;
            potential.z += coords[i].z;
        }

        // calculate and sum all the potential function of foods
        for (Food f : vivarium.foods) {
            Coord qi = new Coord(f.x, f.y, f.z);
            qi = potentialFunction(p, qi, Food_Potential_Weight);
            potential.x += qi.x;
            potential.y += qi.y;
            potential.z += qi.z;
        }

        // update the new dirx, diry, dirz
        dirx += potential.x;
        diry += potential.y;
        dirz += potential.z;
    }

    // get the predator information for fishes
    public void getpredator(Predator p) {
        predator = p;
    }

    // get the food information for fishes
    public void getfood(Food f) {
        food = f;
    }
}
