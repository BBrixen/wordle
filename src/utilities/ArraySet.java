package utilities;

import java.util.*;

/**
 * @author Bennett Brixen
 * This class represents a set using an array of elements
 *
 * It is a generic implementation (i feel like there is nothing else to say)
 *
 * @param <E> - the type of object for the set
 */
public class ArraySet<E> extends AbstractSet<E> {

    private static final int DEFAULT_CAPACITY = 4;
    private int size;
    private E[] elements;

    /**
     * This is the constructor, it creates the set
     *
     * the default values are an empty array with DEFAULT_CAPACITY capacity
     * and a size of 0
     */
    public ArraySet() {
        this.size = 0;
        elements = (E[]) new Object[DEFAULT_CAPACITY];
    }

    /**
     * This gets the element at a specific index in the set
     *
     * Since this is an array we can index it. I want to index it to get a random wordle word
     * @param index - the index to get
     * @return the element at that index
     */
    public E get(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Out of set bounds");
        return elements[index];
    }

    /**
     * resizing the array to either add more elements later or save space
     * @param increase to distinguish between increasing and decreasing
     */
    public void resize(boolean increase) {
        int newCapacity = 2 * elements.length; // double capacity for increase
        if (!increase) newCapacity /=4; // half capacity if we want to decrease
        E[] newArr = (E[]) new Object[newCapacity];

        for (int i = 0; i < this.elements.length && i < newCapacity; i++) {
            newArr[i] = elements[i];
        }
        this.elements = newArr;
    }

    /**
     * Adds an element to the utilities.ArraySet
     *
     * @param value - the value to be added
     * @return true if the set changed as a result, false if the value already existed
     */
    @Override
    public boolean add(E value) {
        if (value == null || this.contains(value)) return false; // we dont add duplicates

        if (size >= elements.length) resize (true);
        elements[size] = value;
        this.size ++;
        return true; // new item added
    }

    /**
     * Returns the number of elements in this set
     *
     * @return number of elements
     */
    @Override
    public int size() {
        return this.size;
    }

    /**
     * Checks if the arrayset is empty
     * @return true if there are no items in the set
     */
    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    /**
     * Adds multiple items to the set
     * @param c - the collection of items to add
     * @return true if the set changed from adding these elements
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean changed = false; // the default is that it did not change
        for (E elem : c)
            if (this.add(elem)) changed = true;
        // if it did change, mark it (cannot return because we need to add all elements)

        return changed;
    }

    /**
     * Empties the set
     * Essentially removes all items and sets size to 0
     */
    @Override
    public void clear() {
        this.size = 0;
        elements = (E[]) new Object[DEFAULT_CAPACITY];
    }

    /**
     * Checks if the set contains a specific object
     *
     * @param o - the object to search for
     * @return true if o is present in this set
     */
    @Override
    public boolean contains(Object o) {
        for (E element : elements)
            if (o != null && o.equals(element)) return true;

        return false; // went through all elements and did not find o
    }

    /**
     * Checks if this set contains all elements in a collection
     *
     * If some are present but not all it returns false
     *
     * @param c the collection to check
     * @return true if all elements are present in this set
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object elem: c)
            if (!this.contains(elem)) return false;

        return true;
    }

    /**
     * Compares two sets to see if they are equal
     *
     * Remember for sets, equality means they contain the same items. They do NOT
     * have to be the same exact types. For instance a HashSet with the same items as another
     * TreeSet is equal to that TreeSet. So your equality method should allow equality with any
     * type of Set.
     *
     * @param o - the other set to compare against
     * @return true if they are equal
     */
    @Override
    public boolean equals(Object o) {
        if (! (o instanceof Set otherSet)) return false; // we get to use a cool pattern variable

        for (Object otherSetElement : otherSet)
            if (! this.contains(otherSetElement)) {
                return false; // does not contain so not equal
            }

        for (E elem : elements)
            if (elem != null && ! otherSet.contains(elem)) {
                return false; // does not contain so not equal
            }

        return true;
    }

    /**
     * Removes an element from this set
     *
     * You are not required to throw any exceptions like those specified in the docs.
     *
     * @param o - the object to be removed
     * @return true if the set changed from removing the element
     */
    @Override
    public boolean remove(Object o) {
        if (isEmpty() || o == null) return false;

        for (int i = 0; i < elements.length; i++) {
            E target = elements[i];
            if (! o.equals(target)) continue; // skip if not equal
            // now we remove
            // shift everything back one
            for (int j = i; j < elements.length - 1; j++)
                elements[j] = elements[j+1];

            this.size --;
            if ((this.size * 2) < this.elements.length && this.elements.length > DEFAULT_CAPACITY)
                resize(false); // it is too large so we shrink back down
            return true; // successfully removed, data changed
        }

        return false;
    }

    /**
     * This removes all elements in a collection from the set
     *
     * You are not required to throw any exceptions like those specified in the docs.
     * If the set removes even a single item, then this method returns true
     *
     * @param c - the collection of items to be removed
     * @return true if the set changed as a result of removing the items
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        for (Object o: c) {
            if (this.remove(o)) changed = true; // if it did change, mark it (cannot return because we need to remove all elements)
        }
        return changed;
    }

    /**
     * Retains the specified elements, and removes all others fromt the set
     *
     * If a single element is removed (that means the set contained
     * an element not specified within the collection) then this returns true
     *
     * @param c - the collection of items to retain
     * @return true if the set changed as a retult of retaining these items
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        boolean changed = false;

        for (int i = 0; i < elements.length; i++) {
            E element = elements[i];
            if (element == null) continue;
            boolean isRetained = false;

            // check if we want to keep it
            for (Object o: c) {
                if (o.equals(element)) {
                    isRetained = true;
                    break;
                }
            }

            if (isRetained) continue; // skip if we are keeping it
            this.remove(element);
            changed = true;
            i--; // so we dont skip anything
        }

        return changed;
    }

    /**
     * Creates an array of the elements and returns it
     *
     * @return an array of the set elements
     */
    @Override
    public Object[] toArray() {
        Object[] newArr = new Object[this.size];
        System.arraycopy(this.elements, 0, newArr, 0, newArr.length);

        return newArr;
    }

    /**
     * Generates an iterator of this set
     *
     * @return an iterator over the elements in this set
     */
    @Override
    public Iterator<E> iterator() {
        return new ArraySetIterator<>(this);
    }

    /**
     * This class represents the iterator for an arrayset
     * @param <T> - the type of object in the iterator
     */
    private static class ArraySetIterator<T> implements Iterator<T> {

        private T[] items;
        private int index;

        /**
         * This is the constructor, it creates a new iterator object based on the utilities.ArraySet
         * @param set - the set to create an iterator for
         */
        public ArraySetIterator(ArraySet<T> set) {
            items = (T[]) set.toArray();
            index = 0;
        }

        /**
         * This checks if the iterator has another element
         *
         * @return true if there is another element within the iterator
         */
        @Override
        public boolean hasNext() {
            return index < items.length;
        }

        /**
         * Gets the next element in the iterator, and moves onto the one after in preparation for the next call
         *
         * @return next element
         */
        @Override
        public T next() {
            if (! this.hasNext()) return null;

            this.index++;
            return items[index - 1]; // we have to do -1 because we just did +1
        }

        /**
         * Removes from the underlying collection the last element returned by this iterator (optional operation)
         *
         * This method can be called only once per call to next()
         * The behavior of an iterator is unspecified if the underlying collection is
         * modified while the iteration is in progress in any way other than by calling this method
         *
         * @throws IllegalStateException - if the remove operation is not supported by this iterator
         * @throws UnsupportedOperationException - if the next method has not yet been called, or the remove method has already been called after the last call to the next method
         */
        @Override
        public void remove() throws IllegalStateException, UnsupportedOperationException {
            if (index == 0) throw new IllegalStateException("Cannot remove before calling next");

            T[] newItems = (T[]) new Object[items.length - index];
            for (int i = 0; i < newItems.length; i++) { // copy older elements over
                newItems[i] = this.items[i+index];
            }

            this.items = newItems;
            index = 0;
        }
    }

}
