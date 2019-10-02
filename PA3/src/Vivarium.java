
import javax.media.opengl.*;
import java.util.*;

public class Vivarium
{
  public Tank tank;
  private  boolean foodadd = false;
  //private Teapot teapot;
  public List<Fish> fishes = new ArrayList<>();
  public List<Predator> predators = new ArrayList<>();
  public List<Food> foods = new ArrayList<>();

  public Vivarium()
  {
    tank = new Tank( 4f, 4f, 4f );
    fishes.add(new Fish(1f,-0.9f,0.3f,0.3f, this));
    predators.add(new Predator(1f,0f,-0.9f,-0.7f));
    predators.get(0).getprey(fishes.get(0));
    fishes.get(0).getpredator(predators.get(0));

  }

  public void addfood()
  {
      foods.add(new Food());
      foodadd = true;
  }

  public void init( GL2 gl )
  {
    tank.init( gl );
    for(Fish fish: fishes)
    {
        fish.init( gl );
    }
    for(Predator predator: predators)
    {
      predator.init( gl );
    }
      for(Food food: foods)
      {
          food.init( gl );
      }
  }

  public void update( GL2 gl )
  {
      if(fishes.size()!=0) {
          for(int i = 0; i<fishes.size();i++)
              if (fishes.get(i).dead) {
                  Fish item = fishes.get(i);
                  fishes.remove(item);
              }
      }

      if(foods.size()!=0) {
          for(int i = 0; i<foods.size();i++)
          if (foods.get(i).eat) {
              Food item = foods.get(i);
              foods.remove(item);
          }
      }

      tank.update( gl );
	  if(fishes != null)
    for(Fish fish: fishes)
    {
        fish.update( gl );
    }
    for(Predator predator: predators)
    {
      predator.update( gl );
    }


      if (foodadd) {
          for (Food food : foods) {
              food.init( gl );
              if(fishes.size()!=0)
              fishes.get(0).getfood(foods.get(0));
          }
          foodadd = false;
      }

      for(Food food: foods)
      {
          food.update( gl );
      }
  }

  public void draw( GL2 gl )
  {
    tank.draw( gl );
    if(fishes != null)
    for(Fish fish: fishes)
    {
      fish.draw( gl );
    }
    for(Predator predator: predators)
    {
      predator.draw( gl );
    }
      for(Food food: foods)
      {
          food.draw( gl );
      }
  }
}
