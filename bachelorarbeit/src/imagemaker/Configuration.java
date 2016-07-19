package imagemaker;

/**
 *  Copyright (C) 2012  Ulrich Viefhaus
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

/**
 * @author 7679963 Viefhaus, Ulrich
 * 
 */
public class Configuration {

    /**
     * The number of bits from a random number generator added to the hash.
     */
    private int abits;

    /**
     * The length of the hashes in bit.
     */
    private int rbits;

    /**
     * The used hashalgorithm.
     */
    private String hashalg;

    /**
     * The reduction method.
     */
    private String reduction;

    /**
     * Sets the number of additional bits to the given value.
     * 
     * @param bits
     *            The wanted number of additional bits.
     */
    public final void setAbits(final int bits) {

        this.abits = bits;
    }

    /**
     * Sets the length of the hashes to the given value.
     * 
     * @param bits
     *            The wanted length of the hashes.
     */
    public final void setRbits(final int bits) {

        this.rbits = bits;
    }

    /**
     * Sets the hashalgorithm to the given value.
     * 
     * @param alg
     *            A name of a hashalgorithm.
     */
    public final void setHashAlg(final String alg) {

        this.hashalg = alg;
    }

    /**
     * Sets the reduction method to the given value.
     * 
     * @param red
     *            The reduction method.
     */
    public final void setReduction(final String red) {

        this.reduction = red;
    }

    /**
     * Returns the number of additional bits.
     * 
     * @return The number of additional bits.
     */
    public final Integer getAbits() {

        return this.abits;
    }

    /**
     * Returns the length of the hashes.
     * 
     * @return The length of the hashes.
     */
    public final Integer getRbits() {

        return this.rbits;
    }

    /**
     * Returns the name of the hashalgorithm.
     * 
     * @return The name of the hashalgorithm.
     */
    public final String getHashAlg() {

        return this.hashalg;
    }

    /**
     * Returns the reduction method.
     * 
     * @return The reduction method.
     */
    public final String getReduction() {

        return this.reduction;
    }
}
