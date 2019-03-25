/**
 *  Copyright (C) 2012 Ulrich Viefhaus
 *    This file (FileUtils.java) is from the package utils which is part of Hashchecker and/or Hashviewer.
 *    Hashchecker and/or Hashviewer are free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Hashmaker and Hashviewer are distributed in the hope that it will be useful,
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author 7679963 Viefhaus, Ulrich
 * 
 */
public abstract class FileUtils {

    /**
     * Deletes all all files in the given folder with the given extension.
     * 
     * @param file
     *            The folder in wich the files are deleted.
     * @param extension
     *            The file extension.
     * @param folders
     *            True if folders should be deleted as well.
     */
    private final static void delete(final File file, final String extension,
            final boolean folders) {

        if (file.exists()) {
            final Collection<File> files = getFiles(file, extension);
            for (File f : files) {
                // Only use files not folders, if the boolean folders is false
                if (folders || f.isFile()) {
                    if (f.exists()) {
                        f.delete();
                    }
                }
            }
        } else {

            // Throw exception, if file does not exist.
            throw new IllegalArgumentException("The given file "
                    + file.getAbsolutePath() + " does not exist.");
        }

    }

    /**
     * Deletes files in the given folder with the given extension.
     * 
     * @param file
     *            The folder.
     * @param extension
     *            The file extension.
     */
    public final static void deleteFiles(final File file, final String extension) {

        delete(file, extension, false);
    }

    /**
     * Deletes all files in the given folder.
     * 
     * @param file
     *            The folder.
     */
    public final static void deleteFiles(final File file) {

        delete(file, "", false);
    }

    /**
     * Deletes all files with the given file extension and all folders in a
     * given folder.
     * 
     * @param file
     *            The folder.
     * @param extension
     *            The file extension.
     */
    public final static void deleteFilesAndFolders(final File file,
            final String extension) {

        delete(file, extension, true);
    }

    /**
     * Deletes all files and folders in a given folder.
     * 
     * @param file
     *            The given folder.
     */
    public final static void deleteFilesAndFolders(final File file) {

        delete(file, "", true);
    }

    /**
     * Returns all files in the given folder with the given file extension.
     * 
     * @param file
     *            The folder.
     * @param extension
     *            The file extension.
     */
    public static List<File> getFiles(final File file, final String extension) {

        // Create the List for the result
        List<File> result = new LinkedList<File>();

        // Check if file exists
        if (file.exists()) {

            // List all children of the file
            File[] children = file.listFiles();

            if (children != null) {
                // Check all the children
                for (File child : children) {
                    if (child.isFile()
                            && child.getName().toLowerCase()
                                    .endsWith(extension)) {
                        // Add it to the list, if its a file and ends with the
                        // given file extension.
                        result.add(child);

                    } else if (child.isDirectory()) {
                        // Recursivly add all children to the list, if its a
                        // folder
                        result.addAll(getFiles(child, extension));
                    }
                }
            }
        } else {

            // Throw exception, if file does not exist.
            throw new IllegalArgumentException("The given file "
                    + file.getAbsolutePath() + " does not exist.");
        }
        return result;
    }

    /**
     * Get all files with the given file extension and all folders within the
     * given folder.
     * 
     * @param file
     *            The folder.
     * @param extension
     *            The file extension
     */
    public static List<File> getFilesAndFolders(File file,
            final String extension) {

        // Create the List for the result
        List<File> result = new LinkedList<File>();

        // Check if file exists
        if (file.exists()) {

            // List all children of the file
            File[] children = file.listFiles();

            if (children != null) {

                // Check all the children
                for (File child : children) {
                    if (child.isFile()
                            && child.getName().toLowerCase()
                                    .endsWith(extension)) {
                        // Add it to the list, if its a file and ends with the
                        // given file extension.
                        result.add(child);
                    } else if (child.isDirectory()) {
                        // Recursivly add all children to the list, if its a
                        // folder. Then add the folder itself to the list.
                        result.addAll(getFiles(child, extension));
                        result.add(child);
                    }
                }
            }
        } else {

            // Throw exception, if file does not exist.
            throw new IllegalArgumentException("The given file "
                    + file.getAbsolutePath() + " does not exist.");
        }
        return result;
    }

    /**
     * Returns only the lexicorgaphicaly last file with the given file extension
     * of the given folder and each of its subfolders.
     * 
     * @param file
     *            The folder.
     * @param extension
     *            The file extension.
     */
    public static List<File> getLastFile(final File file, final String extension) {

        // Create the List for the result
        List<File> all = new LinkedList<File>();

        // Check if file exists
        if (file.exists()) {
            // List all children of the file
            final File[] children = file.listFiles();

            // Create temporary list for the unsorted results
            final ArrayList<File> list = new ArrayList<File>();

            if (children != null) {
                // Check all the children
                for (File child : children) {
                    if (child.isFile()
                            && child.getName().toLowerCase()
                                    .endsWith(extension)) {
                        // Add it to the temporary list, if its a file and ends
                        // with the given file extension.
                        list.add(child);
                    }
                    if (child.isDirectory()) {
                        // Recursivly add the lexicographic last children to the
                        // final list, if its a
                        // folder.
                        all.addAll(getLastFile(child, extension));
                    }
                }

                if (!list.isEmpty()) {
                    // Sort the temporary list and add only the lexicographic
                    // last children to the final result list.
                    Collections.sort(list);
                    all.add(list.get(list.size() - 1));
                }
            }
        } else {

            // Throw exception, if file does not exist.
            throw new IllegalArgumentException("The given file "
                    + file.getAbsolutePath() + " does not exist.");
        }
        return all;
    }

    /**
     * Opens the given file for write acces. Don't forget to close it after use.
     * 
     * @param file
     *            The file you want to open.
     * @return The BufferedWriter.
     */
    public static BufferedWriter openFileForWrite(final File file) {

        // Check if file exists
        if (file.exists()) {
            FileWriter filewriter = null;
            BufferedWriter datastream = null;

            // Try to open the writer
            try {
                filewriter = new FileWriter(file, true);
                datastream = new BufferedWriter(filewriter);

                datastream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return datastream;
        } else {

            // Throw exception, if file does not exist.
            throw new IllegalArgumentException("The given file "
                    + file.getAbsolutePath() + " does not exist.");
        }
    }
}
