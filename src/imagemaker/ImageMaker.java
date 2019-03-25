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

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import utils.FileUtils;

/**
 * @author 7679963 Viefhaus, Ulrich
 * 
 */
public final class ImageMaker {

    /**
     * The name of the directory containing the configuration files.
     */
    private static final String CONF_DIR = "conf.d";

    /**
     * The extension of the configuration files
     */
    private static final String CONF_EXT = ".properties";

    /**
     * True if the logger should APPEND the messages to existing logs.
     */
    private static final boolean APPEND = true;

    /**
     * The logger for this class
     */
    public static final Logger logger = Logger.getLogger("imagemaker");

    /**
     * Starts the {@link Loader} for the given configuration files. You may use
     * single files and/or directorys separated with spaces to specify
     * configuration files or you may leave it blank to run all configuration
     * files in the configuration directory.
     * 
     * @param args
     *            A list of configurations files or directories containing such
     *            files. All configuration files in the configuration folder
     *            will be used if empty.
     */
    public static void main(final String[] args) {

        // Configure the logger
        try {
            final File logfolder = new File("log");
            if (!logfolder.exists()) {
                logfolder.mkdir();
            }
            final FileHandler handler = new FileHandler("log/hashchecker.log",
                    APPEND);

            // Create text Formatter
            final SimpleFormatter formatter = new SimpleFormatter();
            handler.setFormatter(formatter);

            // Add to the desired logger
            logger.addHandler(handler);

        } catch (IOException e) {
            logger
                    .warning("Error while creating FileHandler and Formatter for the logger. The errormessage was: "
                            + e.getLocalizedMessage());
        }

        // Check if a given set of config files/folders from the commandline
        // should be used, or all config files
        // from the folder conf.d
        if (0 == args.length) {

            logger
                    .info("No commandline arguments found. Searching for configuration files in "
                            + CONF_DIR);

            // List all files in conf.d and sort them
            final List<File> configs = FileUtils.getFiles(new File(
                    ImageMaker.CONF_DIR), ImageMaker.CONF_EXT);
            Collections.sort(configs);

            // Create a Loader for each config file in the list
            loadEach(configs);

        } else {

            logger.info("Commandline arguments found.");
            // Read all files/directorys from the commandline.
            // Create a Loader for each config file.
            for (int i = 0; i < args.length; i++) {
                final File file = new File(args[i]);

                if (file.isFile()) {
                    createLoader(file);

                } else if (file.isDirectory()) {
                    logger.info("Scanning " + file.getAbsolutePath()
                            + " for configurations...");

                    // List all files in data
                    final List<File> files = FileUtils.getFiles(file,
                            ImageMaker.CONF_EXT);
                    Collections.sort(files);

                    // Create a Loader for each config file in the list
                    loadEach(files);
                }
            }
        }
    }

    /**
     * Creates a new {@link Loader} for the given file.
     * 
     * @param file
     *            A configuration file.
     */
    private static void createLoader(final File file) {

        logger.info("Load configuration: " + file.getAbsolutePath());
        final Loader loader = new Loader(file);
        loader.start();
        try {
            loader.join();
        } catch (InterruptedException e) {
            logger.warning(e.getLocalizedMessage());
        }
    }

    /**
     * Scans the given list of files for configuration files and creates a
     * Loader for each of them
     * 
     * @param configs
     *            The list of files which should be scanned for configuration
     *            files.
     */
    private static void loadEach(final List<File> configs) {

        for (File file : configs) {
            // Check if given file is a file
            if (file.isFile()) {
                createLoader(file);
            }
        }
    }
}
