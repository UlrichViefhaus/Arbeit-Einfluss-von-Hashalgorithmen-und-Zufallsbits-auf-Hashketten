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

/**
 * @author 7679963 Viefhaus, Ulrich
 * 
 */
public class ElementNotFoundException extends Exception {

    /**
     * A Random serialVersionUID
     */
    private static final long serialVersionUID = -2870434083301098366L;

    /**
     * Default constructor for the exception.
     */
    public ElementNotFoundException() {

        super();
    }

    /**
     * Constructor with a message.
     * 
     * @param message
     *            The message for the exception.
     */
    public ElementNotFoundException(String message) {

        super(message);
    }

    /**
     * Constructor with a cause of the exception
     * 
     * @param cause
     *            The cause of the exception.
     */
    public ElementNotFoundException(Throwable cause) {

        super(cause);
    }

    /**
     * Constructor with a message and a cause of the exception.
     * 
     * @param message
     *            The message for the exception.
     * @param cause
     *            The cause of the exception.
     */
    public ElementNotFoundException(String message, Throwable cause) {

        super(message, cause);
    }

}
