/**
 * 
 */


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Jeffrey Finkelstein
 * 
 */
public class CyclicIterator<E> implements Iterator<E> {

  private List<E> elements = new ArrayList<E>();

  public boolean add(final E element) {
    return this.elements.add(element);
  }
  
  public void add(final E... elements) {
    for (final E element : elements) {
      this.add(element);
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.util.Iterator#hasNext()
   */
  @Override
  public boolean hasNext() {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Iterator#next()
   */
  @Override
  public E next() {
    this.current = (this.current + 1) % this.elements.size();
    return this.elements.get(this.current);
  }

  private int current = 0;

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Iterator#remove()
   */
  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }

}
