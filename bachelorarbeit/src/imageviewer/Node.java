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

import java.util.Collection;
import java.util.LinkedList;

/**
 * @author 7679963 Viefhaus, Ulrich A node for the graph. Includes the edges in
 *         form of lists, wich contain the nodes it is connected to.
 */
public class Node {

    /**
     * A collection of incoming nodes.
     */
    private final Collection<Node> in;

    /**
     * A collection of outgoing nodes.
     */
    private final Collection<Node> out;

    /**
     * The name of this node.
     */
    private Integer name;

    /**
     * The index for the dfs in Tarjan's algorithm
     */
    private Integer index = -1;

    /**
     * The lowlink counter for the dfs in Tarjan's algorithm
     */
    private Integer lowlink;

    /**
     * True if the the node is on the stack in Tarjan's algorithm. False in each
     * other case.
     */
    private boolean isonstack = false;

    /**
     * Default constructor using super(). Instantiates the collections of the
     * node with LinkedList<Node>.
     */
    public Node() {

        super();
        this.in = new LinkedList<Node>();
        this.out = new LinkedList<Node>();
    }

    /**
     * Constructor using this(). Sets the name of the node to the given Integer
     * name.
     * 
     * @param name
     *            The name of the new node as Integer.
     */
    public Node(final Integer name) {

        this();
        this.name = name;
    }

    /**
     * Constructor using this(). Sets the name of the node to the given String
     * name. The String has to be a valid hex represantation of an Integer.
     * 
     * @param string
     *            A hex encoded Integer.
     */
    public Node(final String string) {

        this();
        // trim the String to correct input files with spaces before or after
        // seperators
        this.name = Integer.parseInt(string.trim(), 16);
    }

    /**
     * Adds the given node to the collection of incoming nodes.
     * 
     * @param node
     *            A node that should be added as a incoming node.
     */
    public final void addInNode(final Node node) {

        this.in.add(node);
    }

    /**
     * Adds the given node to the collection of outgoing nodes.
     * 
     * @param node
     *            A node that should be added as a outgoing node.
     */
    public final void addOutNode(final Node node) {

        this.out.add(node);
    }

    /**
     * @param node
     *            A node that should be removed from the collection of incomming
     *            nodes.
     * @throws ElementNotFoundException
     *             Is thrown, if the given node does not exist in the collection
     *             of incoming nodes.
     */
    public final void removeInNode(final Node node)
            throws ElementNotFoundException {

        if (this.in.contains(node)) {
            this.in.remove(node);
        } else {
            throw new ElementNotFoundException("The given node " + node
                    + " is not a incoming node of the node " + this + ".");
        }
    }

    /**
     * @param node
     *            A node that should be removed from the collection of outgoing
     *            nodes.
     * @throws ElementNotFoundException
     *             Is thrown, if the given node does not exist in the collection
     *             of outgoing nodes.
     */
    public final void removeOutNode(final Node node)
            throws ElementNotFoundException {

        if (this.out.contains(node)) {
            this.out.remove(node);
        } else {
            throw new ElementNotFoundException("The given node " + node
                    + " is not a outgoing node of the node " + this + ".");
        }
    }

    /**
     * Sets the name of the node.
     * 
     * @param name
     *            the name to set.
     */
    public void setName(final Integer name) {

        this.name = name;
    }

    /**
     * Returns the name of the node.
     * 
     * @return the name.
     */
    public Integer getName() {

        return name;
    }

    /**
     * Returns the name of the node encoded to hex.
     * 
     * @return the name in Hex.
     */
    public String getHexName() {

        return Integer.toHexString(name);
    }

    /**
     * Returns the collection of incoming nodes.
     * 
     * @return The collection of incomming nodes.
     */
    public Collection<Node> getIn() {

        return this.in;
    }

    /**
     * Returns the collection of outgoing nodes.
     * 
     * @return The collection of outgoing nodes.
     */
    public Collection<Node> getOut() {

        return this.out;
    }

    /**
     * Sets the dfs index for Tarjan's algorithm.
     * 
     * @param ind
     *            the new dfs index.
     */
    public void setIndex(final Integer ind) {

        this.index = ind;
    }

    /**
     * Returns the dfs index of Tarjan's algorithm.
     * 
     * @return The dfs index.
     */
    public Integer getIndex() {

        return this.index;
    }

    /**
     * Sets the lowlink of the node for Tarjan's algorithm.
     * 
     * @param link
     *            The new value of the lowlink.
     */
    public void setLowlink(final Integer link) {

        this.lowlink = link;
    }

    /**
     * Returns the lowlink for Tarjan's algorithm.
     * 
     * @return The lowlink of the node.
     */
    public Integer getLowlink() {

        return this.lowlink;
    }

    /**
     * Set true if the node was put on the stack for Tarjan's algorithm. Set
     * false if it was removed from the stack.
     * 
     * @param stack
     *            the new value for isonstack.
     */
    public void setOnStack(final boolean stack) {

        this.isonstack = stack;
    }

    /**
     * Returns true if the node was put on the stack for Tarjan's algorithm. Set
     * false if it was removed from the stack.
     */
    public Boolean isOnStack() {

        return this.isonstack;
    }
}
