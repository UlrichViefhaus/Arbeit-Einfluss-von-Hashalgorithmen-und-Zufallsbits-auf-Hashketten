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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import utils.FileUtils;
import au.com.bytecode.opencsv.CSVReader;

/**
 * @author 7679963 Viefhaus, Ulrich
 * 
 */
public class ImageViewer {

    /**
     * The keyword that marks the end of an analysis.
     */
    private static final String EOF = "EOF";

    /**
     * The extension of the analysis files.
     */
    private static final String ANALYSIS_EXT = ".analysis";

    /**
     * The extension of the data files.
     */
    private static final String DATA_EXT = ".csv";

    /**
     * The directory containing the data files.
     */
    private static final File DATA_DIRECTORY = new File("data");

    /**
     * The logger of the imageviewer.
     */
    static final Logger logger = Logger.getLogger("imageviewer");;

    /**
     * Analyses the given files or all files in a folder.
     * 
     * @param args
     *            Files and/or folders.
     */
    public static void main(String[] args) {

        // Configure the logger
        try {
            final File logfolder = new File("log");
            if (!logfolder.exists()) {
                logfolder.mkdir();
            }
            // Create an appending file handler
            final boolean append = true;
            FileHandler handler = new FileHandler("log/imageviewer.log", append);

            // Create text Formatter
            final SimpleFormatter formatter = new SimpleFormatter();
            handler.setFormatter(formatter);

            // Add to the desired logger
            logger.addHandler(handler);

        } catch (IOException e) {
            logger.warning(e.getLocalizedMessage());
        }

        if (0 == args.length) {

            // List all files in DATA_DIRECTORY
            // if there are no commandline arguments
            final List<File> files = FileUtils.getLastFile(
                    ImageViewer.DATA_DIRECTORY, ImageViewer.DATA_EXT);
            Collections.sort(files);

            // Create a Loader for each config file
            for (File file : files) {

                // Only use files not folders and not the summary.csv
                if (file.isFile() && !file.getName().contains("summary")) {

                    // Only analyse files without an finished analysis file
                    final File analysis = new File(file.getAbsolutePath()
                            + ImageViewer.ANALYSIS_EXT);

                    if (analysis.exists()) {

                        // Do nothing, if there is a finished analysis
                        if (isFinished(analysis)) {
                            logger.fine("There is already an analysis of "
                                    + file);

                        } else {
                            // Parse the file, if the analysis is
                            // unfinished.
                            logger
                                    .info("There is only an unfinished analysis of "
                                            + file);
                            parse(file);
                        }

                    } else {
                        // Parse the file, if there is no analysis.
                        parse(file);
                    }
                }
            }
        } else {

            // Read all files from the commandline, if there are commandline
            // arguments.
            // Create a Loader for each config file.
            for (int i = 0; i < args.length; i++) {

                final File file = new File(args[i]);
                if (file.isFile()) {
                    parse(file);

                } else if (file.isDirectory()) {
                    // List all files in data
                    final List<File> files = FileUtils.getLastFile(file,
                            ImageViewer.DATA_EXT);
                    Collections.sort(files);

                    // Create a Loader for each config file
                    for (File f : files) {

                        // Only use files not folders
                        if (f.isFile() && !file.getName().contains("summary")) {
                            // Only analyse files without an finished
                            // analysis
                            // file
                            final File analysis = new File(f.getAbsolutePath()
                                    + ImageViewer.ANALYSIS_EXT);

                            if (analysis.exists()) {

                                // Do nothing, if there is a finished
                                // analysis
                                if (isFinished(analysis)) {
                                    logger
                                            .fine("There is already an analysis of "
                                                    + f);

                                } else {
                                    // Parse the file, if the analysis is
                                    // unfinished.
                                    logger
                                            .info("There is only an unfinished analysis of "
                                                    + f);
                                    parse(f);
                                }

                            } else {
                                // Parse the file, if there is no analysis.
                                parse(f);
                            }
                        }
                    }
                }
            }
        }

        writeSummary();
    }

    /**
     * 
     */
    private static void writeSummary() {

        LinkedList<String[]> superlist = new LinkedList<String[]>();
        // Put the results together

        // Create a summary file and delete the old version, if existant
        final File summary = new File(DATA_DIRECTORY.getAbsolutePath()
                + "/summary.csv");
        if (summary.exists()) {
            summary.delete();
        }
        FileWriter filewriter = null;
        BufferedWriter datastream = null;
        try {
            filewriter = new FileWriter(summary, true);
            datastream = new BufferedWriter(filewriter);
        } catch (IOException e) {
            logger.warning("Could not open " + summary.getAbsolutePath()
                    + ". The errormessage was: " + e.getLocalizedMessage());
        }
        FileReader filereader = null;
        BufferedReader datareader = null;
        String info = "";
        logger.info("Merging the result files to " + summary.getAbsolutePath());

        // Write header
        try {
            datastream
                    .write("reduction, algorithm, length, rbits, round, size, number of strongly connected componentes, scc nr., scc size");
            datastream.flush();
        } catch (IOException e) {
            logger.warning(e.getLocalizedMessage());
        }

        // List all files in the data directory
        final List<File> files = FileUtils.getFiles(ImageViewer.DATA_DIRECTORY,
                ImageViewer.ANALYSIS_EXT);
        Collections.sort(files);

        logger.info("Load the result files...");
        for (final File file : files) {
            // Open file
            try {
                filereader = new FileReader(file);
                datareader = new BufferedReader(filereader);
            } catch (FileNotFoundException e) {
                logger.warning(e.getLocalizedMessage());
            }

            // Read settings
            try {
                info = datareader.readLine();
            } catch (IOException e) {
                logger.warning(e.getLocalizedMessage());
            }

            if (info != null) {
                String reduction = "";
                String algorithm = "";
                int bits = 0;
                int random = 0;
                int round = 0;
                int tree = 0;
                int numtrees = 0;
                int size = 0;

                final Collection<Integer[]> trees = new LinkedList<Integer[]>();
                int index = 0;
                String property;
                String value;

                while (info.length() != 0) {
                    // calculate setting and value
                    index = info.indexOf(":");
                    if (index != -1) {
                        property = info.substring(0, index);
                        info = info.substring(index + 1, info.length());
                        index = info.indexOf(",");
                        if (index == -1) {
                            value = info.substring(0, info.length());
                            info = "";
                        } else {
                            value = info.substring(0, index);

                            // Cut of the used part.
                            // DANGER only works with ", " not ","
                            info = info.substring(index + 2, info.length());
                        }

                        if ("reduction".equals(property)) {
                            reduction = value;
                        } else if ("algorithm".equals(property)) {
                            algorithm = value;
                        } else if ("bits".equals(property)) {
                            bits = Integer.valueOf(value);
                        } else if ("random".equals(property)) {
                            random = Integer.valueOf(value);
                        } else if ("round".equals(property)) {
                            round = Integer.valueOf(value);
                        }
                    }
                }

                String line;
                try {
                    line = datareader.readLine();
                    while (line != null) {
                        // Search for a line with the structure
                        // propertie:value
                        index = line.indexOf(":");
                        if (index != -1) {
                            property = line.substring(0, index);
                            line = line.substring(index + 1, line.length());
                            value = line.substring(0, line.length()).trim();

                            if ("Strongly connected component nr."
                                    .equals(property)) {
                                tree = Integer.valueOf(value);
                            } else if ("Number of nodes".equals(property)) {
                                trees.add(new Integer[] { tree,
                                        Integer.valueOf(value) });
                            } else if ("Number of strongly connected components"
                                    .equals(property)) {
                                numtrees = Integer.valueOf(value);
                            } else if ("Total size".equals(property)) {
                                size = Integer.valueOf(value);
                            }
                        }
                        line = datareader.readLine();
                    }
                } catch (IOException e1) {
                    logger.warning(e1.getLocalizedMessage());
                }
                logger.info("Finished " + file.getAbsolutePath()
                        + ".\nWriting results to " + summary.getAbsolutePath());

                // Formatting csv
                String resultline = "";

                // First the fixed data for the file
                resultline = resultline.concat(reduction + ", " + algorithm
                        + ", " + bits + ", " + random + ", " + round + ", "
                        + size + ", " + numtrees);

                // Second a line for each tree
                for (Integer[] t : trees) {
                    try {
                        String[] stringarray = { reduction,
                                algorithm,
                                Integer.toString(bits),
                                Integer.toString(random),
                                Integer.toString(round),
                                Integer.toString(size),
                                Integer.toString(numtrees),
                                Integer.toString(t[0]),
                                Integer.toString(t[1]) };
                        superlist.add(stringarray);
                        datastream.write("\n" + resultline + ", " + t[0] + ", "
                                + t[1]);
                    } catch (IOException e) {
                        logger.warning(e.getLocalizedMessage());
                    }
                }

                // Flush the stream.
                try {
                    datastream.flush();
                } catch (IOException e) {
                    logger.warning(e.getLocalizedMessage());
                }
            }
        }

        // Close the streams
        if (filewriter != null && datastream != null) {
            try {
                datastream.close();
                filewriter.close();
            } catch (IOException e) {
                logger.warning(e.getLocalizedMessage());
            }
        }
    }

    /**
     * Parses a given file under the assumption that it has a one line header
     * and any number of following lines with pairs of nodes, separated with
     * Commas.
     * 
     * @param file
     *            A file with the properties described above.
     */
    private final static void parse(final File file) {

        if (file.exists()) {
            logger.fine("Begin parsing of: " + file);

            // initialise variables
            CSVReader reader = null;
            ImageParser parser = null;
            parser = new ImageParser(logger, file);
            String[] nextLine;

            // Try to open file in the reader
            try {
                reader = new CSVReader(new FileReader(file));
            } catch (FileNotFoundException e) {
                logger.warning(e.getLocalizedMessage());
            }

            if (reader != null) {
                // Try to read the file
                try {
                    // First line should be the header
                    if ((nextLine = reader.readNext()) != null) {
                        parser.info(nextLine);
                    }

                    // All other lines should contain nodes
                    while ((nextLine = reader.readNext()) != null) {
                        parser.parse(nextLine);
                    }

                } catch (IOException e) {
                    logger.warning(e.getLocalizedMessage());
                }

                // Try to close the reader
                try {
                    reader.close();
                } catch (IOException e) {
                    logger
                            .warning("Error while closing the reader of the file: "
                                    + file.getAbsolutePath()
                                    + ". The errormessage was: "
                                    + e.getLocalizedMessage());
                }
            }

            // Start the analysis
            parser.startAnalysis();

        } else {

            // Throw exception, if file does not exist.
            throw new IllegalArgumentException("The given file "
                    + file.getAbsolutePath() + " does not exist.");
        }
    }

    /**
     * Checks if the analysis of the given file is finished. the result file of
     * a finished analysis ends with a new line containing only the word EOF.
     * 
     * @param file
     *            The result file of a previous analysis.
     * @return True if the analysis was finished. Wrong in any other case.
     */
    private static final boolean isFinished(final File file) {

        if (file.exists()) {
            logger.finest("Check if analysis was finished: "
                    + file.getAbsolutePath());

            // Initialise variables
            boolean result = false;
            CSVReader reader = null;
            String[] nextLine;

            // Try to open the file in the reader
            try {
                reader = new CSVReader(new FileReader(file));
            } catch (FileNotFoundException e) {
                logger.warning(e.getLocalizedMessage());
            }

            if (reader != null) {
                // Try to read the file
                try {
                    // Search for an occurance of imageviewer.EOF in the file
                    while ((nextLine = reader.readNext()) != null) {
                        if (ImageViewer.EOF.equals(nextLine[0])) {
                            result = true;
                            break;
                        }
                    }
                } catch (IOException e) {
                    logger.warning(e.getLocalizedMessage());
                }

                // Try to close the reader
                try {
                    reader.close();
                } catch (IOException e) {
                    logger
                            .warning("Error while closing the reader of the file: "
                                    + file.getAbsolutePath()
                                    + ". The errormessage was: "
                                    + e.getLocalizedMessage());
                }
            }

            logger.finest("Check completed. The analysis was finished: "
                    + result);

            // Return the result
            return result;

        } else {

            // Throw exception, if file does not exist.
            throw new IllegalArgumentException("The given file "
                    + file.getAbsolutePath() + " does not exist.");
        }
    }
}
