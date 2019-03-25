/**
 *  Copyright (C) 2013  Ulrich Viefhaus
 *
 *  This file is part of Hashmaker and/or Hashviewer.
 *
 *    Hashmaker and Hashviewer is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Hashmaker and Hashviewer is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with Hashmaker and Hashviewer.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */
package imageviewer;

import java.util.Comparator;

/**
 * Provides a comparator for the type Node and compares its name.
 * 
 * @author 7679963 Viefhaus, Ulrich
 * 
 */
public class NodeComparator implements Comparator<Node> {

    /**
     * The default constructor for the comparator.
     */
    public NodeComparator() {

        super();
    }

    @Override
    public int compare(Node arg0, Node arg1) {

        int result = 0;
        if (arg0.getName() < arg1.getName()) {
            result = -1;
        } else if (arg0.getName() > arg1.getName()) {
            result = 1;
        }
        return result;

    }
}
