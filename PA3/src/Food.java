/*
 * Food.java
 * 
 * Lunhao Liang - lunhaol@bu.edu
 * 
 * Draws food in the tank randomly and fishes will move to the food eat the food.
 */

import com.jogamp.opengl.util.gl2.GLUT;

import javax.media.opengl.GL2;
import java.util.Random;

public class Food {
	private Random rand;
	private float v; // drop speed
	public float r; // food radius
	public int food;
	public boolean eat; // if it is been eaten
	public float x, y, z;

	//create Fodder
	public Food() {
		rand=new Random();
		//drop velocity
		v=0.01f;
		r=0.05f;
		//initial xyz of fodder
		x=rand.nextFloat()*3.5f-2;
		y=2;
		z=rand.nextFloat()*3.5f-2;
		//boolean to check if eaten
		eat=false;
	}
	
	public void init(GL2 gl) {
		food=gl.glGenLists(1);
		gl.glNewList(food, GL2.GL_COMPILE);
		GLUT glut = new GLUT();
		glut.glutSolidSphere(r, 25, 18);
		gl.glEndList();
	}
	
	public void update(GL2 gl) {
		//control the food stop dropping once they touch the floor
		if (y>-1.9f){
			y=y-v;
		}
		else
			y = -1.9f;
	}
	
	public void draw(GL2 gl) {
		gl.glPushMatrix();
	    gl.glPushAttrib( GL2.GL_CURRENT_BIT );
	    gl.glTranslatef(x, y, z);
	    //light green color healthy food
	    gl.glColor3f( 0f, 1.0f, 0f);
	    gl.glCallList(food);
	    gl.glPopAttrib();
	    gl.glPopMatrix();
	}

}
