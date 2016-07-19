/**
 * Copyright (C) 2013 Ulrich Viefhaus
 *
 * This file is part of Hashmaker and/or Hashviewer.
 *
 * Hashmaker and Hashviewer is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Hashmaker and Hashviewer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Hashmaker and Hashviewer. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */
package imageviewer;

import java.util.Comparator;
import java.util.List;

/**
 * Provides a comparator for the type List and compares its length.
 * 
 * @author 7679963 Viefhaus, Ulrich
 * @param <E>
 */
public class ListLengthComparator<E> implements Comparator<List<E>> {

    /**
     * Default constructor for the Comparator.
     */
    public ListLengthComparator() {

	super();
    }

    @Override
    public int compare(final List<E> l1, final List<E> l2) {

	int result = 0;
	if (l1.size() < l2.size()) {
	    result = -1;
	} else if (l1.size() > l2.size()) {
	    result = +1;
	}
	return result;
    }

}
