package org.infodavid.commons.util.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.collections4.collection.AbstractCollectionDecorator;
import org.apache.commons.collections4.iterators.AbstractListIteratorDecorator;

/**
 * The Class ObservedList.
 * @param <E> the element type
 */
public abstract class ObservedList<E> extends AbstractCollectionDecorator<E> implements List<E> {

    /**
     * The Class ObservedListIterator.
     */
    protected class ObservedListIterator extends AbstractListIteratorDecorator<E> {

        /** The current. */
        private E current = null;

        /**
         * Instantiates a new observed list iterator.
         * @param iterator the iterator
         */
        protected ObservedListIterator(final ListIterator<E> iterator) {
            super(iterator);
        }

        /*
         * (non-javadoc)
         * @see org.apache.commons.collections4.iterators.AbstractListIteratorDecorator#add(java.lang.Object)
         */
        @Override
        public void add(final E object) {
            added(object);
            super.add(object);
        }

        /*
         * (non-javadoc)
         * @see org.apache.commons.collections4.iterators.AbstractListIteratorDecorator#next()
         */
        @Override
        public E next() {
            current = super.next();

            return current;
        }

        /*
         * (non-javadoc)
         * @see org.apache.commons.collections4.iterators.AbstractListIteratorDecorator#previous()
         */
        @Override
        public E previous() {
            current = super.previous();

            return current;
        }

        /*
         * (non-javadoc)
         * @see org.apache.commons.collections4.iterators.AbstractListIteratorDecorator#remove()
         */
        @Override
        public void remove() {
            removed(current);

            super.remove();
        }

        /*
         * (non-javadoc)
         * @see org.apache.commons.collections4.iterators.AbstractListIteratorDecorator#set(java.lang.Object)
         */
        @Override
        public void set(final E object) {
            added(object);

            super.set(object);
        }
    }

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8999168651292863484L;

    /**
     * Instantiates a new observed list.
     * @param list the list
     */
    protected ObservedList(final List<E> list) {
        super(list);
    }

    /*
     * (non-javadoc)
     * @see org.apache.commons.collections4.collection.AbstractCollectionDecorator#add(java.lang.Object)
     */
    @Override
    public boolean add(final E object) {
        final boolean result = decorated().add(object);

        if (result) {
            added(object);
        }

        return result;
    }

    /*
     * (non-javadoc)
     * @see java.util.List#add(int, java.lang.Object)
     */
    @Override
    public void add(final int index, final E object) {
        decorated().add(index, object);
        added(object);
    }

    /*
     * (non-javadoc)
     * @see org.apache.commons.collections4.collection.AbstractCollectionDecorator#addAll(java.util.Collection)
     */
    @Override
    public boolean addAll(final Collection<? extends E> coll) {
        final boolean result = decorated().addAll(coll);

        if (result) {
            coll.forEach(this::added);
        }

        return result;
    }

    /*
     * (non-javadoc)
     * @see java.util.List#addAll(int, java.util.Collection)
     */
    @Override
    public boolean addAll(final int index, final Collection<? extends E> coll) {
        final boolean result = decorated().addAll(index, coll);

        if (result) {
            coll.forEach(this::added);
        }

        return result;
    }

    /*
     * (non-javadoc)
     * @see java.util.List#get(int)
     */
    @Override
    public E get(final int index) {
        return decorated().get(index);
    }

    /*
     * (non-javadoc)
     * @see java.util.List#indexOf(java.lang.Object)
     */
    @Override
    public int indexOf(final Object object) {
        return decorated().indexOf(object);
    }

    /*
     * (non-javadoc)
     * @see java.util.List#lastIndexOf(java.lang.Object)
     */
    @Override
    public int lastIndexOf(final Object object) {
        return decorated().lastIndexOf(object);
    }

    /*
     * (non-javadoc)
     * @see java.util.List#listIterator()
     */
    @Override
    public ListIterator<E> listIterator() {
        return new ObservedListIterator(decorated().listIterator(0));
    }

    /*
     * (non-javadoc)
     * @see java.util.List#listIterator(int)
     */
    @Override
    public ListIterator<E> listIterator(final int i) {
        return new ObservedListIterator(decorated().listIterator(i));
    }

    /*
     * (non-javadoc)
     * @see java.util.List#remove(int)
     */
    @Override
    public E remove(final int index) {
        final E object = decorated().remove(index);
        removed(object);

        return object;
    }

    /*
     * (non-javadoc)
     * @see org.apache.commons.collections4.collection.AbstractCollectionDecorator#remove(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(final Object object) {
        final boolean result = decorated().remove(object);

        if (result) {
            removed((E) object);
        }

        return result;
    }

    /*
     * (non-javadoc)
     * @see org.apache.commons.collections4.collection.AbstractCollectionDecorator#removeAll(java.util.Collection)
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean removeAll(final Collection<?> coll) {
        final boolean result = decorated().removeAll(coll);

        if (result && coll != null) {
            coll.forEach(e -> removed((E) e));
        }

        return result;
    }

    /*
     * (non-javadoc)
     * @see org.apache.commons.collections4.collection.AbstractCollectionDecorator#retainAll(java.util.Collection)
     */
    @Override
    public boolean retainAll(final Collection<?> coll) {
        final List<E> old = new ArrayList<>(decorated());
        final boolean result = decorated().retainAll(coll);

        if (result && coll != null) {
            old.forEach(e -> {
                if (!decorated().contains(e)) {
                    removed(e);
                }
            });
        }

        return result;
    }

    /*
     * (non-javadoc)
     * @see java.util.List#set(int, java.lang.Object)
     */
    @Override
    public E set(final int index, final E object) {
        final E removed = decorated().set(index, object);
        removed(removed);
        add(object);

        return removed;
    }

    /*
     * (non-javadoc)
     * @see java.util.List#subList(int, int)
     */
    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        return decorated().subList(fromIndex, toIndex);
    }

    /**
     * Added.
     * @param object the object
     */
    protected abstract void added(final E object);

    /*
     * (non-javadoc)
     * @see org.apache.commons.collections4.collection.AbstractCollectionDecorator#decorated()
     */
    @Override
    protected List<E> decorated() {
        return (List<E>) super.decorated();
    }

    /**
     * Removed.
     * @param object the object
     */
    protected abstract void removed(final E object);
}
