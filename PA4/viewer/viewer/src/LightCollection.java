/*******************************************************************
  Container class to hold all the Lights 
*******************************************************************/

import java.util.*;
import javax.media.opengl.*;

public class LightCollection 
{
  private ArrayList<Light> lightArray;

  public LightCollection()
  {
    lightArray = new ArrayList<Light>();
  }
  

  public void newLight( StringTokenizer stok )
  {
    int glLightId = 0;

    // glLightId is only relevant for OpenGL
    switch( lightArray.size() )
    {
      case 0: glLightId = GL2.GL_LIGHT0; break;
      case 1: glLightId = GL2.GL_LIGHT1; break;
      case 2: glLightId = GL2.GL_LIGHT2; break;
      case 3: glLightId = GL2.GL_LIGHT3; break;
      case 4: glLightId = GL2.GL_LIGHT4; break;
      case 5: glLightId = GL2.GL_LIGHT5; break;
      case 6: glLightId = GL2.GL_LIGHT6; break;
      case 7: glLightId = GL2.GL_LIGHT7; break;
      default:
	    System.err.println("Exceed max number of lights"); 
	    System.exit(1);
    }

    Light light = new Light( stok, glLightId );

    // Check for duplicate light IDs
    for ( Light l : lightArray )
      if ( l.id == light.id )
      {
        System.err.println( "Duplicate ID: " + l.id 
                            + " detected. Object not inserted " );
        return;
      }

    lightArray.add( light );  
  }

  public void setLights( GL2 gl )
  {
    gl.glEnable( GL2.GL_LIGHTING );

    for ( Light l : lightArray)
      l.setLight( gl );
  }

  public void print()
  {
    System.out.println( "---------- LightCollection ----------" );
    for ( Light l : lightArray )
      l.print();
  }
}


