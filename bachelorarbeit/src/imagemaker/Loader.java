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
package imagemaker;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author 7679963 Viefhaus, Ulrich
 * 
 */
public class Loader extends Thread {

    /**
     * The logger.
     */
    public static final Logger logger = Logger.getLogger("imagemaker.Loader");

    /**
     * The properties read from the configuration file.
     */
    Properties properties;

    /**
     * Creates a Loader to read a configuration file and start the
     * {@link BitImageMaker} in an appropriate way.
     * 
     * @param file
     *            The configurationfile.
     */
    public Loader(final File file) {

        super();
        this.properties = loadProperties(file);
    }

    /**
     * Load the configuration file through the Java properties API.
     * 
     * @return The Properties object of the given file
     */
    private Properties loadProperties(final File file) {

        // Create the new Properties
        Properties properties = new Properties();

        // Create stream for the fileinput
        BufferedInputStream stream = null;

        // Try to read the file into the stream
        try {
            stream = new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e1) {
            logger.warning(e1.getLocalizedMessage());
        }

        // Try to parse the stream
        try {
            properties.load(stream);
        } catch (IOException e1) {
            logger.warning(e1.getLocalizedMessage());
        }

        // Try to close the stream
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e1) {
            logger.warning(e1.getLocalizedMessage());
        }

        return properties;
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

        // Read the properties
        // Additional bits of information for each chain link
        final int abits = Integer.valueOf(properties.getProperty("addbits"));

        // Reduce the output of each hash to rbits
        final Integer rbits = Integer.valueOf(properties
                .getProperty("reduceto"));

        // Used hashing algorithm
        final String hashalgorithm = properties.getProperty("hashalgorithm");

        // Used hashing algorithm
        final String reduction = properties.getProperty("reduction");

        // Create a Configuration from the properties
        Configuration config = new Configuration();

        config = new Configuration();
        config.setAbits(abits);
        config.setRbits(rbits);
        config.setHashAlg(hashalgorithm);
        config.setReduction(reduction);

        logger.info("Creating new HashImageMaker...");
        BitImageMaker imagemaker = new BitImageMaker(config);

        new Thread(imagemaker).start();
        try {
            imagemaker.join();
        } catch (InterruptedException e) {
            logger.warning("Error while waiting for the Thread to finish: "
                    + e.getLocalizedMessage());
        }
    }
}
