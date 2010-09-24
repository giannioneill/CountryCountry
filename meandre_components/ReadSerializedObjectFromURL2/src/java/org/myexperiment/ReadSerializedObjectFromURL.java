/*
 * @(#) NonWebUIFragmentCallback.java @VERSION@
 * 
 * Copyright (c) 2008+ Amit Kumar
 * 
 * The software is released under ASL 2.0, Please
 * read License.txt
 *
 */

package org.myexperiment;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Vector;
import java.util.logging.Logger;

import org.meandre.annotations.Component;
import org.meandre.annotations.ComponentInput;
import org.meandre.annotations.ComponentOutput;
import org.meandre.annotations.ComponentProperty;
import org.meandre.core.ComponentContext;
import org.meandre.core.ComponentContextException;
import org.meandre.core.ComponentContextProperties;
import org.meandre.core.ComponentExecutionException;
import org.meandre.core.ExecutableComponent;

/**
 * This module reads a Serialized object in, and outputs it.
 *
 * @author Kris West @ Sun Labs
 * Modified by Lily Dong on Feb. 27, 2008
 */

@Component(creator="Kris West",
           description="read in a serialized object and output it",
           name="ReadSerializedObjectFromURL",
           tags="serialization")
public class ReadSerializedObjectFromURL implements ExecutableComponent {
    @ComponentInput(description="model names",
                    name="modelNames")
    final static String DATA_INPUT = "modelNames";

    @ComponentOutput(description="the object read-in from the file(object)",
                     name="outputObjects")
    final static String DATA_OUTPUT = "outputObjects";

    /** Indicates whether the module has run.*/
    private boolean hasRun = false;
    /** Path and name of the file to read from */
    private Vector modelNames = new Vector();

    /**
     * Performs operations at the beginning of itinerary execution.
     *
     * @see #dispose
     */

    public void initialize() {
        hasRun = false;
    }
    public void initialize(ComponentContextProperties ccp) {
        hasRun = false;
    }

    /**
     * Performs operations at the end of itinerary execution.
     *
     * @see #initialize
     */
    public void dispose() {
        hasRun = false;
    }
    public void dispose(ComponentContextProperties ccp) {
        hasRun = false;
    }

    /**
     * This module takes an object in, serializes it and writes it to a file
     * (specified as a parameter). Subsequent objects will overwrite the same file.
     */
    public void execute(ComponentContext cc)
            throws ComponentExecutionException, ComponentContextException {
        try{
    	System.out.println("\nReadSerializedObject");

        //for seasr only
        modelNames = (Vector)cc.getDataComponentFromInput(DATA_INPUT);
        Vector theObs = new Vector();
        for(int i=0; i<modelNames.size(); i++) {
            String modelName = (String)modelNames.elementAt(i);
            System.out.println("ReadSerializedObject:modelName="+modelName);
            InputStream fin;
            try{
            	URL url = new URL(modelName);
            	URLClassLoader cl = (URLClassLoader)ClassLoader.getSystemClassLoader();
            	for(URL u : cl.getURLs()){
            		cc.getOutputConsole().println("Tried -- "+u.toString());
            	}
            	fin = url.openStream();
            }catch(IOException e){
            	throw new ComponentExecutionException("Cannot connect to URL");
            }
            ObjectInputStream in;
            try {
                in = new ObjectInputStream(fin);
            } catch(IOException ioe) {
                throw new ComponentExecutionException("I/O error occured while reading stream header",ioe);
            }
            Object theOb;
            try {
                theOb = in.readObject();
                theObs.add(theOb);
            } catch(ClassNotFoundException cnf) {
                throw new ComponentExecutionException("The class of the Serialized Object could not be found!",cnf);
            } catch(InvalidClassException ice) {
                throw new ComponentExecutionException("Something is wrong with a class used by Serialization (wrong JRE version?)!",ice);
            } catch(StreamCorruptedException sce) {
                throw new ComponentExecutionException("Control information in the stream is inconsistent!", sce);
            } catch(IOException ioe) {
                throw new ComponentExecutionException("I/O error occured while reading in Object",ioe);
            }

        }
        cc.pushDataComponentToOutput(DATA_OUTPUT, theObs);
        }catch(NullPointerException n){
        	n.printStackTrace(cc.getOutputConsole());
        	throw new ComponentExecutionException("NULL POINTER!!!!");
        }
    }
}