/**
 *  Copyright (C) 2012  Ulrich Viefhaus
 *
 *  This file is part of Hashmaker and/or Hashview free software: you can redistribute it and/or modify
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
package imagemaker;

import iaik.sha3.IAIKSHA3Provider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import utils.ByteUtils;
import au.com.bytecode.opencsv.CSVReader;

/**
 * @author 7679963 Viefhaus, Ulrich
 * 
 */
public class BitImageMaker extends Thread {

    /**
     * Number of leading zeros for the hex values of the hashes.
     */
    private static final int LEADINGZEROS = 3;

    /**
     * The logger.
     */
    public static final Logger logger = Logger
            .getLogger("imagemaker.BitImageMaker");

    /**
     * True if old results should be deleted.
     */
    private boolean delete = true;

    /**
     * Stores values and their corrosponding hashes in a hashmap
     */
    ConcurrentHashMap<BitSet, BitSet> hashmap;

    /**
     * The name of the hashalgorithm.
     */
    private String hashalg;

    /**
     * The length of the hashes.
     */
    final private int reduceto;

    /**
     * The number of additional bits from the random number generator.
     */
    final private int abits;

    /**
     * The result of the hashing.
     */
    private BitSet result;

    /**
     * The result of the hashing from the last round.
     */
    private BitSet oldresult;

    /**
     * The file that will contain the generated data.
     */
    private File data = null;

    /**
     * A {@link MessageDigest}.
     */
    private MessageDigest mdigest;

    /**
     * A HashMap for the names of the hash algorithms and a corresponding
     * number. Neccesary to use case instead of if.
     */
    final private HashMap<String, Integer> hashalorithms = new HashMap<String, Integer>();

    private static final String START = "start";
    private static final String MID = "mid";
    private static final String END = "end";
    private String reduction = START;

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

        // Start with round 1
        boolean run = true;
        Integer round = 1;
        CSVReader reader = null;
        String[] nextLine;

        // Generate needed components for writing the result file
        FileWriter filewriter = null;
        BufferedWriter datastream = null;
        File file = null;

        // Create the folder structure and delete earlier results if needed
        if (data.exists()) {
            logger.info("Delete old results from the data directory: "
                    + data.getAbsolutePath());
            utils.FileUtils.deleteFiles(data);
            delete = false;
        } else if (delete) {
            logger
                    .info("Create folder for the data: "
                            + data.getAbsolutePath());
            data.mkdirs();
        }

        // Open result file
        // Name: /datapath/round_#round.csv
        file = new File(data.getPath().concat(
                "/round_".concat(addLeadingZeros(round.toString())).concat(
                        ".csv")));
        logger.info("Creating file for round " + round.toString() + ": "
                + file.getAbsolutePath());

        try {
            filewriter = new FileWriter(file, true);
            datastream = new BufferedWriter(filewriter);
        } catch (IOException e1) {
            logger.warning("Error while opening " + file.getAbsolutePath()
                    + ".\nThe errormessage was: " + e1.getLocalizedMessage());
        }

        // Save informations about algorithm and settings to the stream
        try {
            if (datastream != null) {
                datastream.write("algorithm:" + this.hashalg + ", bits:"
                        + this.reduceto + ", random:" + this.abits + ", round:"
                        + round + ", reduction:" + this.reduction + "\n");
            }
        } catch (IOException e2) {
            logger
                    .warning("Could not save the settings. The errormessage was: "
                            + e2.getLocalizedMessage());
        }

        // Initialise array for results
        boolean[] hashes = new boolean[(int) Math.pow(2, reduceto)];

        // Calculate the hashes of the counter i
        for (Integer i = 0; i < Math.pow(2, reduceto); i++) {

            // Seed the hashfile with the hexadezimal version of the values and
            // the hashes of
            // them
            oldresult = ByteUtils.convert(i);

            // Add random data
            // One entry for each of the 2^abits possible
            // combinations
            if (abits == 0) {
                result = hash(oldresult);
                save(datastream);
                hashes[(int) ByteUtils.convert(result)] = true;
            } else {
                for (int rand = 0; rand < Math.pow(2, abits); rand++) {

                    final BitSet tmpresult = ByteUtils.expand(oldresult,
                            ByteUtils.convert(rand));

                    // Hash the concatenated hexstring
                    result = hash(tmpresult);
                    save(datastream);
                    hashes[(int) ByteUtils.convert(result)] = true;
                }
            }
        }
        try { // Close the result file
            if (datastream != null) {
                datastream.close();
            }
            if (filewriter != null) {
                filewriter.close();
            }
        } catch (IOException e) {
            logger.warning(e.getLocalizedMessage());
        }
        round++;
        while (run) {
            // Create a new result file for this round
            file = new File(data.getPath().concat(
                    "/round_".concat(addLeadingZeros(round.toString())).concat(
                            ".csv")));
            logger.info("Creating file for round " + round.toString() + ": "
                    + file.getAbsolutePath());

            try {
                filewriter = new FileWriter(file, true);
            } catch (IOException e1) {
                logger.warning("Error while opening " + file.getAbsolutePath()
                        + ".\nThe errormessage was: "
                        + e1.getLocalizedMessage());
            }
            datastream = new BufferedWriter(filewriter);

            // Save informations about algorithm and settings to the stream
            try {
                datastream.write("algorithm:" + this.hashalg + ", bits:"
                        + this.reduceto + ", random:" + this.abits + ", round:"
                        + round + ", reduction:" + this.reduction + "\n");
            } catch (IOException e2) {
                logger
                        .warning("Could not save the settings. The errormessage was: "
                                + e2.getLocalizedMessage());
            }

            boolean[] tmphashes = new boolean[(int) Math.pow(2, reduceto)];
            boolean[] oldhashes = new boolean[(int) Math.pow(2, reduceto)];

            // Read the last hashes and add leading zeros if needed
            try {
                reader = new CSVReader(new FileReader(data.getPath().concat(
                        "/round_").concat(
                        addLeadingZeros(Integer.toString(round - 1)).concat(
                                ".csv"))));
            } catch (FileNotFoundException e) {
                logger
                        .warning("File " + file.getAbsolutePath()
                                + " not found.");
            }

            // Calculate hashes of the last round hashes one after the other
            try {
                if (reader != null) {
                    // Skip info part of the file
                    reader.readNext();

                    while ((nextLine = reader.readNext()) != null) {
                        // Load the last hash and remove leading or tailing
                        // spaces
                        oldresult = ByteUtils.convert(Integer.valueOf(
                                nextLine[1].trim(), 16));

                        // Check if the hash of the old hash was already
                        // calculated
                        if (!oldhashes[(int) ByteUtils.convert(oldresult)]) {
                            // Add random data
                            // One entry for each of the 2^abits possible
                            // combinations
                            if (abits == 0) {
                                result = hash(oldresult);
                                save(datastream);

                                // Mark the nodes as existant
                                tmphashes[(int) ByteUtils.convert(result)] = true;
                                oldhashes[(int) ByteUtils.convert(oldresult)] = true;
                            } else {
                                for (int rand = 0; rand < Math.pow(2, abits); rand++) {

                                    final BitSet tmpresult = ByteUtils.expand(
                                            oldresult, ByteUtils.convert(rand));

                                    // Hash the concatenated hexstring
                                    result = hash(tmpresult);
                                    save(datastream);

                                    tmphashes[(int) ByteUtils.convert(result)] = true;
                                    oldhashes[(int) ByteUtils
                                            .convert(oldresult)] = true;
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                logger
                        .warning("Error while reading "
                                + file.getAbsolutePath()
                                + ".\nThe errormessage was: "
                                + e.getLocalizedMessage());
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    logger.warning("Error while reading "
                            + file.getAbsolutePath()
                            + ".\nThe errormessage was: "
                            + e.getLocalizedMessage());
                }
            }

            // Try to close the datastream and filewriter
            try {
                datastream.close();
                filewriter.close();
            } catch (IOException e) {
                logger.warning("An error accured while closing "
                        + file.getAbsolutePath() + ". The errormessage was: "
                        + e.getLocalizedMessage());
            }

            // Check if there are no leafes left and the range will not change
            // anymore.
            if (Arrays.equals(hashes, tmphashes)) {
                logger.info("Finished round " + round.toString()
                        + ". This was the last round for this configuration.\n"
                        + "Hashalgorithm: " + this.hashalg + ", hashlength: "
                        + this.reduceto + ", additional bits: " + this.abits);
                run = false;
            } else {
                logger.info("Finished round " + round.toString() + ".");
                round++;
                System.arraycopy(tmphashes, 0, hashes, 0, tmphashes.length);
            }
        }
    }

    /**
     * Saves the current value of oldresult and result to the given datastream.
     * 
     * @param datastream
     *            The datastream to save the values to.
     */
    private void save(final BufferedWriter datastream) {

        try {

            datastream.write(Integer.toHexString((int) ByteUtils
                    .convert(oldresult))
                    + ","
                    + Integer.toHexString((int) ByteUtils.convert(result))
                    + "\n");
            datastream.flush();
        } catch (IOException e) {
            logger.warning(e.getLocalizedMessage());
        }
    }

    /**
     * Hashes the given String with the current hashalgorithm and returns the
     * hexadecimal hash after it was parsed with reduce().
     * 
     * @param bits
     *            The String you want to hash.
     */
    private BitSet hash(final BitSet bits) {

        BitSet tmp = (BitSet) bits.clone();

        // Chose the right hashcommand
        // calculate the hash
        switch (hashalorithms.get(hashalg)) {
            case 0:
                // tmp = hasher.randomHex(tmp);
                break;
            case 1:
                mdigest.update(tmp.toByteArray());
                tmp = BitSet.valueOf(mdigest.digest());
                break;
            case 2:
                mdigest.update(tmp.toByteArray());
                tmp = BitSet.valueOf(mdigest.digest());
                break;
            case 3:
                mdigest.update(tmp.toByteArray());
                tmp = BitSet.valueOf(mdigest.digest());
                break;
            case 4:
                mdigest.update(tmp.toByteArray());
                tmp = BitSet.valueOf(mdigest.digest());
                break;
            case 5:
                mdigest.update(tmp.toByteArray());
                tmp = BitSet.valueOf(mdigest.digest());
                break;
            case 6:
                mdigest.update(tmp.toByteArray());
                tmp = BitSet.valueOf(mdigest.digest());
                break;
            case 7:
                mdigest.update(tmp.toByteArray());
                tmp = BitSet.valueOf(mdigest.digest());
                break;
            case 8:
                mdigest.update(tmp.toByteArray());
                tmp = BitSet.valueOf(mdigest.digest());
                break;
            case 9:
                mdigest.update(tmp.toByteArray());
                tmp = BitSet.valueOf(mdigest.digest());
                break;
            default:
                logger.warning("Error: No such hashalgorithm: " + hashalg);
        }

        // Reduce the hash
        tmp = reduce(tmp);
        return tmp;
    }

    /**
     * Reduces the given BitSet to a BitSet of the value of reduceto. The way it
     * is reduced depends on the reduction method.
     * 
     * @param bits
     *            The BitSet you want to shrink
     * @return The reduced BitSet
     **/
    private BitSet reduce(final BitSet bits) {

        // Return the first #hexdigit chars of the hash
        // This is the simplest version of reducing the hashvalues, but
        // in good hashfunction a change of 1 bit in the input should cause a
        // change
        // of at least 50% of the hexdigit of the output. Therefore it shouldn't
        // matter which hexdigit
        // are taken for the reduction.
        if (bits != null) {
            int start = 0;
            int end = bits.length() - 1;

            if (bits.length() >= reduceto) {

                if (this.reduction.equals(START)) {

                    end = reduceto;

                } else if (this.reduction.equals(MID)) {

                    start = (bits.length() - 1) / 2 - reduceto / 2;
                    end = start + reduceto;

                } else if (this.reduction.equals(END)) {

                    start = bits.length() - reduceto - 1;

                }
            }

            result = bits.get(start, end);

        } else {
            logger
                    .warning("No hashobject given. Please check the correctness of the hashfunction.");
        }
        return result;
    }

    /**
     * Initialises a new BitImageMaker object with the given configuration.
     * 
     * @param config
     *            The Configuration the new BitImageMaker should use.
     * 
     */
    public BitImageMaker(final Configuration config) {

        // Create a new object
        super();

        // Read the needed values from the configuration file
        this.hashalg = config.getHashAlg();
        this.reduceto = config.getRbits();
        this.abits = config.getAbits();
        this.reduction = config.getReduction();

        // Create a new folder structure for the result file
        String foldername = hashalg.concat("/");
        foldername = foldername.concat(Integer.toString(reduceto)).concat(
                "bits/");
        foldername = foldername.concat(Integer.toString(abits)).concat(
                "bits_prg/");

        if (this.reduction.equals(START)) {

            foldername = "data/from_start/".concat(foldername);

        } else if (this.reduction.equals(MID)) {

            foldername = "data/from_mid/".concat(foldername);

        } else if (this.reduction.equals(END)) {

            foldername = "data/from_end/".concat(foldername);

        }

        data = new File(foldername);

        // Load the needed algorithms
        loadHashCommands();
    }

    /**
     * Loads the needed algorithms for hashing.
     */
    private final void loadHashCommands() {

        // A random hash function
        this.hashalorithms.put("rh", 0);
        this.hashalorithms.put("Rh", 0);
        this.hashalorithms.put("rH", 0);
        this.hashalorithms.put("RH", 0);
        this.hashalorithms.put("randomhash", 0);
        this.hashalorithms.put("randomHash", 0);
        this.hashalorithms.put("Randomhash", 0);
        this.hashalorithms.put("RandomHash", 0);

        // MD5
        this.hashalorithms.put("md5", 1);
        this.hashalorithms.put("Md5", 1);
        this.hashalorithms.put("mD5", 1);
        this.hashalorithms.put("MD5", 1);
        this.hashalorithms.put("md-5", 1);
        this.hashalorithms.put("Md-5", 1);
        this.hashalorithms.put("mD-5", 1);
        this.hashalorithms.put("MD-5", 1);

        // SHA-1
        this.hashalorithms.put("Sha", 2);
        this.hashalorithms.put("sha", 2);
        this.hashalorithms.put("SHA", 2);
        this.hashalorithms.put("Sha1", 2);
        this.hashalorithms.put("sha1", 2);
        this.hashalorithms.put("SHA1", 2);
        this.hashalorithms.put("Sha-1", 2);
        this.hashalorithms.put("sha-1", 2);
        this.hashalorithms.put("SHA-1", 2);

        // SHA-2
        this.hashalorithms.put("Sha256", 3);
        this.hashalorithms.put("sha256", 3);
        this.hashalorithms.put("SHA256", 3);
        this.hashalorithms.put("Sha-256", 3);
        this.hashalorithms.put("SHA-256", 3);
        this.hashalorithms.put("sha-256", 3);
        this.hashalorithms.put("sha2", 3);
        this.hashalorithms.put("Sha2", 3);
        this.hashalorithms.put("SHA2", 3);
        this.hashalorithms.put("sha-2", 3);
        this.hashalorithms.put("Sha-2", 3);
        this.hashalorithms.put("SHA-2", 3);

        // BLAKE
        this.hashalorithms.put("BLAKE", 4);
        this.hashalorithms.put("Blake", 4);
        this.hashalorithms.put("blake", 4);
        this.hashalorithms.put("BLAKE256", 4);
        this.hashalorithms.put("Blkake256", 4);
        this.hashalorithms.put("blkake256", 4);

        // Groestl
        this.hashalorithms.put("GROESTL", 5);
        this.hashalorithms.put("Groestl", 5);
        this.hashalorithms.put("groestl", 5);
        this.hashalorithms.put("GROESTL256", 5);
        this.hashalorithms.put("Groestl256", 5);
        this.hashalorithms.put("groestl256", 5);

        // JH
        this.hashalorithms.put("JH", 6);
        this.hashalorithms.put("jh", 6);
        this.hashalorithms.put("JH256", 6);
        this.hashalorithms.put("jh256", 6);

        // KECCAK
        this.hashalorithms.put("KECCAK", 7);
        this.hashalorithms.put("Keccak", 7);
        this.hashalorithms.put("keccak", 7);
        this.hashalorithms.put("KECCAK256", 7);
        this.hashalorithms.put("Keccak256", 7);
        this.hashalorithms.put("keccak256", 7);

        // Skein
        this.hashalorithms.put("SKEIN", 8);
        this.hashalorithms.put("Skein", 8);
        this.hashalorithms.put("skein", 8);
        this.hashalorithms.put("SKEIN256", 8);
        this.hashalorithms.put("Skein256", 8);
        this.hashalorithms.put("skein256", 8);

        // RIPEMD160
        this.hashalorithms.put("RIPEMD160", 9);
        this.hashalorithms.put("RIPEMD-160", 9);
        this.hashalorithms.put("Ripemd160", 9);
        this.hashalorithms.put("Ripemd-160", 9);
        this.hashalorithms.put("ripemd160", 9);
        this.hashalorithms.put("ripemd-160", 9);

        Security.addProvider(new IAIKSHA3Provider());
        Security.addProvider(new BouncyCastleProvider());
        switch (hashalorithms.get(hashalg)) {
            case 0:
                break;
            case 1:
                this.hashalg = "MD5";
                try {
                    mdigest = MessageDigest.getInstance("MD5");
                } catch (final NoSuchAlgorithmException e) {
                    logger.warning("Could not create an Instance of "
                            + this.hashalg + ".\nThe errormessage was: "
                            + e.getLocalizedMessage());
                }
                break;
            case 2:
                this.hashalg = "SHA-1";
                try {
                    mdigest = MessageDigest.getInstance("SHA1");
                } catch (final NoSuchAlgorithmException e) {
                    logger.warning("Could not create an Instance of "
                            + this.hashalg + ".\nThe errormessage was: "
                            + e.getLocalizedMessage());
                }
                break;
            case 3:
                this.hashalg = "SHA256";
                try {
                    mdigest = MessageDigest.getInstance("SHA256");
                } catch (final NoSuchAlgorithmException e) {
                    logger.warning("Could not create an Instance of "
                            + this.hashalg + ".\nThe errormessage was: "
                            + e.getLocalizedMessage());
                }
                break;
            case 4:

                this.hashalg = "BLAKE256";
                try {
                    mdigest = MessageDigest.getInstance("BLAKE256");
                } catch (final NoSuchAlgorithmException e) {
                    logger.warning("Could not create an Instance of "
                            + this.hashalg + ".\nThe errormessage was: "
                            + e.getLocalizedMessage());
                }
                break;
            case 5:
                this.hashalg = "Groestl256";
                try {
                    mdigest = MessageDigest.getInstance("Groestl256");
                } catch (final NoSuchAlgorithmException e) {
                    logger.warning("Could not create an Instance of "
                            + this.hashalg + ".\nThe errormessage was: "
                            + e.getLocalizedMessage());
                }
                break;
            case 6:
                this.hashalg = "JH256";
                try {
                    mdigest = MessageDigest.getInstance("JH256");
                } catch (final NoSuchAlgorithmException e) {
                    logger.warning("Could not create an Instance of "
                            + this.hashalg + ".\nThe errormessage was: "
                            + e.getLocalizedMessage());
                }
                break;
            case 7:
                this.hashalg = "KECCAK256";
                try {
                    mdigest = MessageDigest.getInstance("KECCAK256");
                } catch (final NoSuchAlgorithmException e) {
                    logger.warning("Could not create an Instance of "
                            + this.hashalg + ".\nThe errormessage was: "
                            + e.getLocalizedMessage());
                }
                break;
            case 8:
                this.hashalg = "Skein256";
                try {
                    mdigest = MessageDigest.getInstance("Skein256");
                } catch (final NoSuchAlgorithmException e) {
                    logger.warning("Could not create an Instance of "
                            + this.hashalg + ".\nThe errormessage was: "
                            + e.getLocalizedMessage());
                }
                break;
            case 9:
                this.hashalg = "RIPEMD160";
                try {
                    mdigest = MessageDigest.getInstance("RIPEMD160");
                } catch (final NoSuchAlgorithmException e) {
                    logger.warning("Could not create an Instance of "
                            + this.hashalg + ".\nThe errormessage was: "
                            + e.getLocalizedMessage());
                }
                break;
            default:
                logger
                        .warning("Error: No hash algorithm with the following name: "
                                + hashalg);
        }
    }

    /**
     * Adds leading zeros to a given string.
     * 
     * @param tmp
     *            The String you want to add leading zeros to.
     * @return The given String with leading zeros.
     */
    private String addLeadingZeros(final String tmp) {

        String result = tmp;
        // Leading zeros
        while (BitImageMaker.LEADINGZEROS > result.length()) {
            result = "0".concat(result);
        }
        return result;
    }
}
