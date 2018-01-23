package GraphicalUserInterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * An <code>ArrayList</code> which raises events upon modification as per the <code>ListModel</code> interface.
 *
 * @param <E> the type of the element which this <code>ArrayList</code> stores
 * @author Shazz Amin
 * @version 1.0 2017-02-17
 */
@SuppressWarnings("serial")
public class ArrayListModel<E> extends ArrayList<E> implements ListModel<E>
{
   // instance fields
   private HashSet<ListDataListener> listDataListener;

   /*
      constructors
   */

   /**
    * {@inheritDoc}
    */
   public ArrayListModel()
   {
      super();

      listDataListener = new HashSet<ListDataListener>();
   }

   /**
    * {@inheritDoc}
    */
   public ArrayListModel(Collection<? extends E> collection)
   {
      super(collection);

      listDataListener = new HashSet<ListDataListener>();
   }

   /**
    * {@inheritDoc}
    */
   public ArrayListModel(int initialCapacity)
   {
      super(initialCapacity);

      listDataListener = new HashSet<ListDataListener>();
   }

   /*
      accessors
   */

   /**
    * Fires an event that notifies all attached event listeners that the contents within a certain interval of this <code>ArrayListModel</code> have changed.
    *
    * @param fromIndex the lower index of the interval (inclusive)
    * @param toIndex the upper index of the interval (inclusive)
    */
   public void fireContentsChangedEvent(int fromIndex, int toIndex)
   {
      ListDataEvent event = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, fromIndex, toIndex);
      for (ListDataListener listDataListenerElement : listDataListener) listDataListenerElement.contentsChanged(event);
   }

   /**
    * Fires an event that notifies all attached event listeners that there have been new elements added to a certain interval of this <code>ArrayListModel</code>.
    *
    * @param fromIndex the lower index of the interval (inclusive)
    * @param toIndex the upper index of the interval (inclusive)
    */
   public void fireIntervalAddedEvent(int fromIndex, int toIndex)
   {
      ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, fromIndex, toIndex);
      for (ListDataListener listDataListenerElement : listDataListener) listDataListenerElement.intervalAdded(event);
   }

   /**
    * Fires an event that notifies all attached event listeners that there have been elements deleted from a certain interval of this <code>ArrayListModel</code>.
    *
    * @param fromIndex the lower index of the interval (inclusive)
    * @param toIndex the upper index of the interval (inclusive)
    */
   public void fireIntervalRemovedEvent(int fromIndex, int toIndex)
   {
      ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, fromIndex, toIndex);
      for (ListDataListener listDataListenerElement : listDataListener) listDataListenerElement.intervalRemoved(event);
   }

   /**
    * {@inheritDoc}
    */
   public E getElementAt(int index)
   {
      return get(index);
   }

   /**
    * {@inheritDoc}
    */
   public int getSize()
   {
      return size();
   }

   /*
      mutators
   */

   /**
    * {@inheritDoc}
    */
   public boolean add(E element)
   {
      boolean didChange = super.add(element);

      if (didChange) fireIntervalAddedEvent(size() - 1, size() - 1);

      return didChange;
   }

   /**
    * {@inheritDoc}
    */
   public void add(int index, E element)
   {
      super.add(index, element);

      fireIntervalAddedEvent(index, index);
   }

   /**
    * {@inheritDoc}
    */
   public boolean addAll(Collection<? extends E> collection)
   {
      boolean didChange = super.addAll(collection);

      if (didChange) fireIntervalAddedEvent(size() - collection.size(), size() - 1);

      return didChange;
   }

   /**
    * {@inheritDoc}
    */
   public boolean addAll(int index, Collection<? extends E> collection)
   {
      boolean didChange = super.addAll(index, collection);

      if (didChange) fireIntervalAddedEvent(index, index + collection.size() - 1);

      return didChange;
   }

   /**
    * {@inheritDoc}
    */
   public void addListDataListener(ListDataListener listener)
   {
      listDataListener.add(listener);
   }

   /**
    * {@inheritDoc}
    */
   public void clear()
   {
      int sizeBeforeClearing = size();

      super.clear();

      fireIntervalRemovedEvent(0, sizeBeforeClearing - 1);
   }

   /**
    * {@inheritDoc}
    */
   public E remove(int index)
   {
      E element = super.remove(index);

      fireIntervalRemovedEvent(index, index);

      return element;
   }

   /**
    * {@inheritDoc}
    */
   public boolean remove(Object object)
   {
      int indexOfRemoved = indexOf(object);

      boolean didChange = super.remove(object);

      if (didChange) fireIntervalRemovedEvent(indexOfRemoved, indexOfRemoved);

      return didChange;
   }

   /**
    * {@inheritDoc}
    */
   public boolean removeAll(Collection<?> collection)
   {
      boolean didChange = super.removeAll(collection);

      if (didChange) fireContentsChangedEvent(0, size() - 1);

      return didChange;
   }

   /**
    * {@inheritDoc}
    */
   public boolean removeIf(Predicate<? super E> filter)
   {
      boolean didChange = super.removeIf(filter);

      if (didChange) fireContentsChangedEvent(0, size() - 1);

      return didChange;
   }

   /**
    * {@inheritDoc}
    */
   public void removeListDataListener(ListDataListener listener)
   {
      listDataListener.remove(listener);
   }

   /**
    * {@inheritDoc}
    */
   public void removeRange(int fromIndex, int toIndex)
   {
      super.removeRange(fromIndex, toIndex);

      fireIntervalRemovedEvent(fromIndex, toIndex);
   }

   /**
    * {@inheritDoc}
    */
   public void replaceAll(UnaryOperator<E> operator)
   {
      super.replaceAll(operator);

      fireContentsChangedEvent(0, size() - 1);
   }

   /**
    * {@inheritDoc}
    */
   public boolean retainAll(Collection<?> collection)
   {
      boolean didChange = super.retainAll(collection);

      fireContentsChangedEvent(0, size() - 1);

      return didChange;
   }

   /**
    * {@inheritDoc}
    */
   public E set(int index, E element)
   {
      E oldElement = super.set(index, element);

      fireContentsChangedEvent(index, index);

      return oldElement;
   }

   /**
    * {@inheritDoc}
    */
   public void sort(Comparator<? super E> collection)
   {
      super.sort(collection);

      fireContentsChangedEvent(0, size() - 1);
   }
}
