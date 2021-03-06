/*******************************************************************************
 * Copyright (c) 2009, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.jpql.tools.utility.iterable;

import java.util.ListIterator;
import org.eclipse.persistence.jpa.jpql.tools.utility.iterator.SingleElementListIterator;
import org.eclipse.persistence.jpa.jpql.utility.iterable.ListIterable;

/**
 * A <code>SingleElementListIterable</code> returns a {@link ListIterator}
 * that holds a single element
 * and returns it with the first call to {@link ListIterator#next()}, at
 * which point it will return <code>false</code> to any subsequent
 * call to {@link ListIterator#hasNext()}. Likewise, it will return <code>false</code>
 * to a call to {@link ListIterator#hasPrevious()} until a call to {@link ListIterator#next()},
 * at which point a call to {@link ListIterator#previous()} will return the
 * single element.
 * <p>
 * A <code>SingleElementListIterable</code> is equivalent to the
 * {@link Iterable} returned by:
 * 	{@link java.util.Collections#singletonList(Object)}.
 *
 * @param <E> the type of elements returned by the list iterable's list iterator
 *
 * @see SingleElementListIterator
 * @see SingleElementIterable
 */
@SuppressWarnings("nls")
public class SingleElementListIterable<E> implements ListIterable<E> {

	private final E element;

	/**
	 * Construct a list iterable that contains only the specified element.
	 */
	public SingleElementListIterable(E element) {
		super();
		this.element = element;
	}

	/**
	 * {@inheritDoc}
	 */
	public ListIterator<E> iterator() {
		return new SingleElementListIterator<E>(this.element);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "[" + this.element + "]";
	}
}