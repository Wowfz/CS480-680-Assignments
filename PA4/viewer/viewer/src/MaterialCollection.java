/*******************************************************************
  Container class to hold all the Materials 
*******************************************************************/

import java.util.* ;
import javax.media.opengl.GL2;

public class MaterialCollection 
{
  private ArrayList<Material> materialArray;

  public MaterialCollection()
  {
    materialArray = new ArrayList<Material>();
  }

  public void newMaterial( StringTokenizer stok ) 
  {
    Material mat = new Material( stok );

    // check for duplicate id
    for ( Material m : materialArray )
      if ( m.id == mat.id )
      {
        System.err.println( "Duplicate ID: " + m.id 
                            + " detected. Material not inserted.");
        return;
      }

    materialArray.add( mat );
  }

  public void setMaterial( int matId, GL2 gl )
  {
    for ( Material m : materialArray )
      if ( m.id == matId )
        m.setMaterial( gl );
  }

  public void print()
  {
    System.out.println( "--------- Materials ----------" );
    for ( Material m : materialArray )
      m.print();
  }
}


