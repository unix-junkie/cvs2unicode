/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode;

import static java.util.Arrays.binarySearch;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.ListModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public final class SortedListModel<E extends Comparable<E>> implements ListModel<E> {
	private final SortedSet<E> delegate = new TreeSet<>();

	private Object cache[];

	private final EventListenerList listenerList = new EventListenerList();

	/**
	 * @see ListModel#getSize()
	 */
	@Override
	public int getSize() {
		return this.delegate.size();
	}

	/**
	 * @see ListModel#getElementAt(int)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public E getElementAt(final int index) {
		return (E) (this.cache == null
				? this.cache = this.delegate.toArray()
				: this.cache)[index];
	}

	/**
	 * @param element
	 */
	public void addElement(final E element) {
		this.delegate.add(element);
		this.cache = this.delegate.toArray();
		final int index = binarySearch(this.cache, element);
		this.fireIntervalAdded(this, index, index);
	}

	/**
	 * @see ListModel#addListDataListener(ListDataListener)
	 */
	@Override
	public void addListDataListener(final ListDataListener l) {
		this.listenerList.add(ListDataListener.class, l);
	}

	/**
	 * @see ListModel#removeListDataListener(ListDataListener)
	 */
	@Override
	public void removeListDataListener(final ListDataListener l) {
		this.listenerList.remove(ListDataListener.class, l);
	}

	private void fireIntervalAdded(final Object source, final int index0, final int index1) {
		final Object[] listeners = this.listenerList.getListenerList();
		ListDataEvent e = null;

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ListDataListener.class) {
				if (e == null) {
					e = new ListDataEvent(source, ListDataEvent.INTERVAL_ADDED, index0, index1);
				}
				((ListDataListener) listeners[i + 1]).intervalAdded(e);
			}
		}
	}
}
