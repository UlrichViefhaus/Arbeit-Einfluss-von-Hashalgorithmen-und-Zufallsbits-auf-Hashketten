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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author 7679963 Viefhaus, Ulrich
 * 
 */
public class ImageParser {

    /**
     * The file with the results that should be analysed.
     */
    File file;

    /**
     * The graph of the hashfunction.
     */
    ImageGraph graph;

    /**
     * The logger of the parser.
     */
    Logger logger;

    /**
     * Constructor for a parser with a given logger and a file with nodes and
     * edges. A new graph will be created for the analysis
     * 
     * @param logger
     *            The logger.
     * @param file
     *            The file.
     * 
     */
    public ImageParser(final Logger logger, final File file) {

        super();
        this.file = file;
        graph = new ImageGraph(logger, file);
        this.logger = logger;
    }

    /**
     * Parses a StringArray by creating nodes for the first and second part of
     * the array and a edge from the first to the second node.
     * 
     * @param string
     *            A StringArray containing two nodes.
     */
    public final void parse(final String[] string) {

        // Create the new nodes
        Node out = graph.newNode(string[0]);
        Node in = graph.newNode(string[1]);

        // Try to create a new edge
        try {
            graph.addEdge(out, in);
        } catch (Exception e) {
            logger.info("Error parsing: " + file.getAbsolutePath() + ": \n"
                    + e.getMessage());
        }
    }

    /**
     * Calls the appropriate function of the graph.
     */
    public final void startAnalysis() {

        logger.info("Start analysis of the graph " + file.getAbsolutePath());
        this.graph.printAnalysis();
    }

    /**
     * Parses the given StringArray and print the properties to the analysis
     * file.
     * 
     * @param line
     *            The StringArray.
     */
    public void info(final String[] line) {

        // Delete an existing analysis file
        final File analysis = new File(file.getPath() + ".analysis");
        analysis.delete();

        boolean comma = false;

        // Try to create a filewriter
        FileWriter filewriter = null;
        BufferedWriter datastream = null;

        try {
            filewriter = new FileWriter(analysis, true);
            datastream = new BufferedWriter(filewriter);
        } catch (IOException e) {
            logger.warning(e.getLocalizedMessage());
        }

        if (datastream != null) {

            // Foreach part of the line
            for (String setting : line) {
                // write a comma to the analysis file but not in the first loop.
                // Then were will be a comma between each part of the line
                try {
                    if (comma) {
                        datastream.write(",");
                    } else {
                        comma = true;
                    }
                    // write the setting
                    datastream.write(setting);
                } catch (IOException e) {
                    logger.warning(e.getLocalizedMessage());
                }
            }
            try {
                datastream.write("\n");
            } catch (IOException e) {
                logger.warning(e.getLocalizedMessage());
            }

            try {
                datastream.flush();
                datastream.close();

            } catch (IOException e) {
                logger.warning(e.getLocalizedMessage());
            }
        }
        try {
            if (filewriter != null) {
                filewriter.close();
            }
        } catch (IOException e) {
            logger.warning(e.getLocalizedMessage());
        }
    }

}
