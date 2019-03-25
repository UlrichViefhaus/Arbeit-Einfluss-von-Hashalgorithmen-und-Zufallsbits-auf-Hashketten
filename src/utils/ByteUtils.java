/**
 *  Copyright (C) 2012 Ulrich Viefhaus
 *    This file (ByteUtils.java) is from the package utils which is part of Hashchecker.
 *    Hashchecker is free software: you can redistribute it and/or modify
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
package utils;

import java.util.BitSet;

/**
 * @author 7679963 Viefhaus, Ulrich
 * 
 */
public abstract class ByteUtils {

    /**
     * String needed for the conversion from ByteArray to HexString.
     */
    private static final String HEX = "0123456789ABCDEF";

    /**
     * @param value
     *            The Integer you want to convert.
     * @return The Byte Array representation of the given Integer.
     */
    public static final byte[] intToByteArray(final int value) {

        return new byte[] { (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value };
    }

    /**
     * Converts a ByteArray to an Integer.
     * 
     * @param input
     *            The ByteArray
     * @return The corresponding Integer.
     */
    public static final Integer byteArrayToInt(final byte[] input) {

        return (input[0] << 24) + ((input[1] & 0xFF) << 16)
                + ((input[2] & 0xFF) << 8) + (input[3] & 0xFF);
    }

    /**
     * Normalizes a ByteArray and fits it in the given range.
     * 
     * @param input
     *            The ByteArray.
     * @param bit
     *            The range is 0 to Math.pow(2, bit).
     * @return The normalized value of the ByteArray.
     */
    public static final byte[] normalize(final byte[] input, final int bit) {

        byte[] tmp = input;
        final double max = Math.pow(2, bit);
        int out = ((int) (byteArrayToInt(input) % max));
        if (out < 0) {
            out += max;
        }
        tmp = intToByteArray((out));

        return tmp;
    }

    /**
     * Converts the given ByteArray to a HexString.
     * 
     * @param array
     *            The ByteArray.
     * @return The hexadecimal version of the value of the ByteArray.
     */
    public static String getHexString(final byte[] array) {

        if (array == null) {
            return null;
        }
        final StringBuilder hex = new StringBuilder(2 * array.length);
        for (final byte b : array) {
            hex.append(HEX.charAt((b & 0xF0) >> 4)).append(
                    HEX.charAt((b & 0x0F)));
        }
        return hex.toString();
    }

    /**
     * Converts a given Long to a BitSet.
     * 
     * @param value
     *            The given value.
     * @return The BitSet representation of the given value.
     */
    public static BitSet convert(final long value) {

        long tmp = value;
        BitSet bits = new BitSet();
        int index = 0;
        while (tmp != 0L) {
            if (tmp % 2L != 0) {
                bits.set(index);
            }
            ++index;
            tmp = tmp >>> 1;
        }
        return bits;
    }

    /**
     * Converts a BitSet to a Long.
     * 
     * @param bits
     *            The given BitSet.
     * @return The long representation of the BitSet.
     */
    public static long convert(final BitSet bits) {

        long value = 0L;
        for (int i = 0; i < bits.length(); ++i) {
            value += bits.get(i) ? (1L << i) : 0L;
        }
        return value;
    }

    /**
     * Fuses two given BitSets to one by adding the bits of the second at the
     * end of the first BitSet. The length of the result is the added length off
     * both given BitSets.
     * 
     * @param bits
     *            The first BitSet.
     * @param add
     *            The second BitSet.
     */
    public static BitSet expand(final BitSet bits, final BitSet add) {

        final BitSet result = (BitSet) bits.clone();
        int index = result.length();
        for (int i = 0; i < add.length(); i++) {
            if (add.get(i)) {
                result.set(index);
            }
            index++;
        }
        return result;
    }
}
