/*******************************************************************
   Single Light
*******************************************************************/

import java.util.*;

import javax.media.opengl.*;



public class Light  
{

  public int id;

  private enum LightType { POINT, INFINITE, AMBIENT }
  private LightType type;
  private float pos[] = { 0, 0, 0, 0}; // Location
  private float dir[] = { 0, 0, 0, 0}; // light direction
  private float rgb[] = { 0, 0, 0, 0}; // color of light

  @SuppressWarnings("unused")
private boolean shadow_on = false; // Turns on or off shadows

  private float radialAttenuation[] = { 0, 0, 0}; // Pg 560 eq 10-1 of H&B text
  private float angularAttenuation;   // Pg 561 eq 10-4 of H&B text
  private int glLightId; // ID of this light for opengl purposes

  public void print()
  {
    System.out.print( "Light ID:" + id );
    System.out.print( " type:" );
    switch (type)
    {
      case POINT    : System.out.print( "POINT" ); break; 
      case INFINITE : System.out.print( "INFINITE" ); break; 
      case AMBIENT  : System.out.print( "AMBIENT" ); break; 
    }
    System.out.print( " pos:" + pos[0]+","+pos[1]+","+pos[2]+","+pos[3] );
    System.out.print( " dir:" + dir[0]+","+dir[1]+","+dir[2]+","+dir[3] );
    System.out.print( " rgb:" + rgb[0]+","+rgb[1]+","+rgb[2]+","+rgb[3] );
    System.out.print( " radialAttenuation:" + radialAttenuation[0]
                       +","+radialAttenuation[1]+","+radialAttenuation[2]);
    System.out.print( " angularAttenuation:" + angularAttenuation );
    System.out.print( " glLightId:" + glLightId );
    System.out.println();

  }

  public Light( StringTokenizer stok, int glLightId_ )
  {
    glLightId = glLightId_;

    stok.nextToken(); // Strip away the token "ID"
    id = Integer.parseInt( stok.nextToken() );

    String type_str = stok.nextToken();
    if ( type_str.equals( "pnt" )) type = LightType.POINT;
    else if ( type_str.equals( "inf" )) type = LightType.INFINITE;
    else if ( type_str.equals( "amb" )) type = LightType.AMBIENT;
    else 
      System.err.println( "Light Type : " + type_str + " not recognized. ");
   
    for (int i=0; i<3; ++i)
      pos[i] = Float.parseFloat( stok.nextToken() );

    if ( type == LightType.INFINITE )
      pos[3] = 0;
    else
      pos[3] = 1;

    for (int i=0; i<3; ++i)
      dir[i] = Float.parseFloat( stok.nextToken() );
    dir[3] = 1;
    
    for (int i=0; i<3; ++i)
      rgb[i] = Float.parseFloat( stok.nextToken() );
    rgb[3] = 1;

    String shadow_str = stok.nextToken();
    if ( shadow_str.equals( "shadow_on" ))
      shadow_on = true;
    else
      shadow_on = false;

  }


  public void setLight( GL2 gl )
  {
   // int i;
    float zeroLight[]={0,0,0,1};
    float spotCutoff = 90;

    gl.glDisable( glLightId );
    gl.glLightModeli( GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, GL.GL_TRUE);
   
    if( type == LightType.POINT || type == LightType.INFINITE )
    {
      // Light has no ambient component.
      gl.glLightfv( glLightId, GL2.GL_AMBIENT, zeroLight, 0);
      gl.glLightfv( glLightId, GL2.GL_DIFFUSE, rgb, 0);
      gl.glLightfv( glLightId, GL2.GL_SPECULAR, rgb, 0);
      gl.glLightfv( glLightId, GL2.GL_POSITION, pos, 0);
      gl.glLightfv( glLightId, GL2.GL_SPOT_DIRECTION, dir, 0);
      gl.glLightf ( glLightId, GL2.GL_SPOT_EXPONENT, angularAttenuation);
      gl.glLightf ( glLightId, GL2.GL_SPOT_CUTOFF, spotCutoff);
      // gl.glLightfv( glLightId, GL.GL_CONSTANT_ATTENUATION, 
      //                                           radialAttenuation, 0);
      // gl.glLightfv( glLightId, GL.GL_LINEAR_ATTENUATION, 
      //                                           radialAttenuation, 1);
      // gl.glLightfv( glLightId, GL.GL_QUADRATIC_ATTENUATION, 
      //                                           radialAttenuation, 2);
    } 
    else 
    {
      // Light is of type ambient, hence does not have diffuse and 
      // specular values. 
      gl.glLightfv( glLightId, GL2.GL_AMBIENT, rgb, 0);
      gl.glLightfv( glLightId, GL2.GL_DIFFUSE, zeroLight, 0);
      gl.glLightfv( glLightId, GL2.GL_SPECULAR, zeroLight, 0);
      // Location is not relevant, but set anyway.
      gl.glLightfv( glLightId, GL2.GL_POSITION, pos, 0);
    }

    gl.glEnable( glLightId );

  }

}


