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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;
import java.util.Map.Entry;
import java.util.logging.Logger;

/**
 * @author 7679963 Viefhaus, Ulrich
 * 
 */
public class ImageGraph {

    /**
     * The logger.
     */
    Logger logger;

    /**
     * The file containing the Nodes and Edges.
     */
    File datafile;

    /**
     * The index needed for the search for strongly connected components.
     */
    int index = 0;

    /**
     * The stack needed for the search for strongly connected components.
     */
    final Stack<Node> stack = new Stack<Node>();

    /**
     * The List of components needed for the search for strongly connected
     * components.
     */
    final LinkedList<ArrayList<Node>> components = new LinkedList<ArrayList<Node>>();

    /**
     * Collection of nodes.
     */
    private Collection<Node> nodes;

    /**
     * Default constructor using super(), a given logger and a file containing
     * the nodes and edges of the graph.
     * 
     * @param logger
     *            The logger.
     * @param file
     *            The file.
     */
    public ImageGraph(final Logger logger, final File file) {

        super();
        this.nodes = new LinkedList<Node>();
        this.logger = logger;
        this.datafile = file;
    }

    /**
     * Adds a node to the graph.
     * 
     * @param name
     *            The name of the new node.
     * @throws Exception
     *             Thrown, if a node with the given name already exists.
     */
    public final void addNode(final Integer name) throws Exception {

        if (this.contains(name)) {
            throw new Exception(
                    "A node with the name "
                            + name
                            + ", "
                            + Integer.toHexString(name)
                            + " in Hex, already exists. Try again with a different name.");
        } else {
            Node node = new Node(name);
            this.nodes.add(node);
        }
    }

    /**
     * Adds a node to the graph.
     * 
     * @throws Exception
     *             Thrown, if a node with the given name already exists.
     */
    public final void addNode(final Node node) throws Exception {

        if (this.contains(node)) {
            throw new Exception(
                    "A node with the name "
                            + node.getName()
                            + ", "
                            + node.getHexName()
                            + " in Hex, already exists. Try again with a different name.");
        } else {
            this.nodes.add(node);
        }
    }

    /**
     * Deletes a node from the graph.
     * 
     * @param name
     *            The name of the node you want to delete.
     * @throws Exception
     *             Thrown, if no node with the given name exists.
     */
    public final void deleteNode(final Integer name) throws Exception {

        this.deleteNode(this.get(name));
    }

    /**
     * Deletes a node from the graph.
     * 
     * @param todelete
     *            The node that should be deleted.
     * @throws Exception
     *             Thrown, if no node with the given name exists.
     */
    public final void deleteNode(final Node todelete) throws Exception {

        if (this.contains(todelete)) {
            // Remove from in and out collections
            if (!todelete.getIn().isEmpty()) {
                for (Node node : todelete.getIn()) {
                    node.getOut().remove(todelete);
                }
            }

            if (!todelete.getOut().isEmpty()) {
                for (Node node : todelete.getOut()) {
                    node.getIn().remove(todelete);
                }
            }

            // Remove from the collection
            this.nodes.remove(todelete);

        } else {
            throw new Exception("A node with the name " + todelete.getName()
                    + ", " + todelete.getHexName()
                    + " in Hex, does not exists within this graph.");
        }
    }

    /**
     * Checks if a node with the given name exists.
     * 
     * @param name
     *            The name of the node you want to know, if it is in this graph.
     * @return True if the node with the given name is in this graph.
     */
    public boolean contains(final Integer name) {

        boolean result = false;
        for (Node node : this.nodes) {
            if (name.equals(node.getName())) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Checks if the given node is in the graph.
     * 
     * @param node
     *            The node you want to know, if it is in this graph.
     * @return True if the node with the given name is in this graph.
     */
    public boolean contains(final Node node) {

        boolean result = false;
        for (Node tmpnode : this.nodes) {
            if (node.getHexName().equals(tmpnode.getHexName())) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Returns the node with the given name.
     * 
     * @param name
     *            The name of the node you want to get.
     * @return The node with the given name.
     */
    public Node get(final Integer name) {

        Node result = null;
        for (Node node : this.nodes) {
            if (name.equals(node.getName())) {
                result = node;
                break;
            }
        }
        return result;
    }

    /**
     * Adds an edge between the first and the second node.
     * 
     * @param out
     *            The node from which the edge is coming.
     * @param in
     *            The node to which the edge is going.
     * @throws Exception
     */
    public final void addEdge(final Node out, final Node in) throws Exception {

        // Add connection
        in.getIn().add(out);
        out.getOut().add(in);
    }

    /**
     * Analyses the graph and safes the results to the result file.
     */
    public final void printAnalysis() {

        this.printAlalysis(true);
    }

    /**
     * Analyses the graph and safes the results to the result file.
     * 
     * @param sort
     *            True if the componentes should be sorted by size.
     */
    public final void printAlalysis(final boolean sort) {

        final HashMap<Node, Boolean> leafes = this.searchLeafes();

        // Remove Leafes from the nodes collection
        for (Entry<Node, Boolean> entry : leafes.entrySet()) {
            if (entry.getValue()) {
                try {
                    this.deleteNode(entry.getKey());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        // Start a search for each node, that wasn't visited already
        // Save the strongly connected component in the list
        for (Node node : this.nodes) {

            if (node.getIndex() <= 0) {
                this.searchStrongComponent(node);
            }
        }

        // Sort the lists of scc if needed
        if (sort) {
            java.util.Collections.sort(components, new ListLengthComparator());
        }

        final FileWriter filewriter;
        final BufferedWriter datastream;
        final File file = new File(datafile.getPath() + ".analysis");

        try {
            filewriter = new FileWriter(file, true);
            datastream = new BufferedWriter(filewriter);
            int totalnodes = 0;
            int numberscc = 0;
            // Print the nodes of each scc
            for (ArrayList<Node> lists : components) {
                numberscc++;
                int nodes = 0;
                datastream.write("Strongly connected component nr.: "
                        + numberscc + "\n");
                for (Node tmp : lists) {
                    datastream.write("\t" + Integer.toString(tmp.getName(), 16)
                            + " \n");
                    nodes++;
                    totalnodes++;
                }
                datastream.write("Number of nodes:" + nodes + "\n\n");

            }

            // Print leafes
            int count = 0;
            datastream.write("List of peeled of leafes.\n");
            for (Node tmp : leafes.keySet()) {
                if (leafes.get(tmp)) {
                    datastream.write(" \t"
                            + Integer.toString(tmp.getName(), 16) + " \n");
                    count++;
                }
            }

            datastream.write("Leafes peeled of:" + count + "\n\n");

            datastream.write("Number of strongly connected components:"
                    + components.size() + "\n\n");

            // Size of space
            datastream.write("Total size:" + totalnodes + "\nEOF");

            // Close the result file
            datastream.flush();
            datastream.close();
            filewriter.close();
        } catch (IOException e) {
            logger.warning("Error while trying to save the results to "
                    + file.getAbsolutePath() + ". The Errormessage was:"
                    + e.getLocalizedMessage());
        }

    }

    /**
     * This searches for the tree the given node is connected to.
     * 
     * @param node
     *            The node you want the tree.
     * @return The found tree.
     */
    private LinkedList<ArrayList<Node>> searchStrongComponent(final Node node) {

        node.setIndex(index);
        node.setLowlink(index);
        index++;

        stack.push(node);
        node.setOnStack(true);

        // Add nodes that are reachable from this node
        for (Node tmp : node.getOut()) {
            if (tmp.getIndex() == -1) {
                searchStrongComponent(tmp);
                node.setLowlink(Math.min(node.getLowlink(), tmp.getLowlink()));
            } else if (tmp.isOnStack()) {
                node.setLowlink(Math.min(node.getLowlink(), tmp.getIndex()));
            }
        }
        // Check if node is a root of an SCC
        if (node.getLowlink().equals(node.getIndex())) {

            ArrayList<Node> strong_component = new ArrayList<Node>();
            Node n = new Node();
            do {
                n = stack.pop();
                n.setOnStack(false);
                strong_component.add(n);
            } while (!node.equals(n));
            components.add(strong_component);
        }
        return components;
    }

    /**
     * @return A array with marks for the leafes of the graph.
     */
    private final HashMap<Node, Boolean> searchLeafes() {

        final HashMap<Node, Boolean> map = new HashMap<Node, Boolean>();

        // Test each node
        for (Node node : this.nodes) {
            // The node is a leaf if it has no input
            map.put(node, !hasInput(node));
        }
        return map;
    }

    /**
     * Checks if the given node has any incoming edges.
     * 
     * @param node
     *            A node you want to know if it has incoming edges.
     * @return True if the given node has incoming edges, false if not.
     */
    private final boolean hasInput(final Node node) {

        boolean result = false;
        // Search for a node that has an edge to the given node
        // The given node has input if there is such a node
        if (!node.getIn().isEmpty()) {
            result = true;
        }
        return result;
    }

    /**
     * Creates a new Node with the given name.
     * 
     * @param string
     *            The name of the new Node.
     * @return The newly created Node.
     */
    public Node newNode(String string) {

        Node newNode = new Node(string);
        if (this.contains(newNode)) {
            newNode = this.get(newNode.getName());
        } else {
            try {
                this.addNode(newNode);
            } catch (Exception e) {
                logger
                        .warning("Error while creating new Node " + string
                                + ". The error message was: "
                                + e.getLocalizedMessage());
            }
        }
        return newNode;
    }
}
