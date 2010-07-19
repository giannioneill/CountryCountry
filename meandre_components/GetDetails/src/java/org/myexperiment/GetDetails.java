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

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;

/** This executable component queries the RDF data and 
 *  extracts the relevent details
 *
 * @author Gianni O'Neill;
 *
 */
@Component(creator="Gianni O'Neill", description="Extracts relevent details from an RDF model", 
		name="GetDetails",
		tags="extract query rdf")
public class GetDetails implements ExecutableComponent {


	@ComponentInput(description="JENA RDF Model", name="rdf")
	final static String DATA_INPUT_RDF= "rdf";	

	@ComponentOutput(description="File location", name="location")
	final static String DATA_OUTPUT_LOC= "location";
	
	@ComponentOutput(description="URI", name="uri")
	final static String DATA_OUTPUT_URI= "uri";

	
	// log messages are here
	private Logger _logger;
	
	/** This method is invoked when the Meandre Flow is being prepared for 
	 * getting run.
	 *
	 * @param ccp The properties associated to a component context
	 */
	public void initialize ( ComponentContextProperties ccp ) {
		this._logger = ccp.getLogger();
	}

	/** This method just pushes a concatenated version of the entry to the
	 * output.
	 *
	 * @throws ComponentExecutionException If a fatal condition arises during
	 *         the execution of a component, a ComponentExecutionException
	 *         should be thrown to signal termination of execution required.
	 * @throws ComponentContextException A violation of the component context
	 *         access was detected

	 */
	public void execute(ComponentContext cc) throws ComponentExecutionException, ComponentContextException {
		Model model = (Model) cc.getDataComponentFromInput(DATA_INPUT_RDF);
		Property availableAs = model.createProperty("http://purl.org/ontology/mo/","available_as");
		ResIterator iter = model.listSubjectsWithProperty(availableAs); 
		if(!iter.hasNext()){
			throw new ComponentExecutionException("There are no 'available_as' predicates!");
		}
		Resource r = iter.next();
		String loc = r.getProperty(availableAs).getObject().toString();
		cc.pushDataComponentToOutput(DATA_OUTPUT_LOC, loc);
		cc.pushDataComponentToOutput(DATA_OUTPUT_URI, r);
	}

	/** This method is called when the Menadre Flow execution is completed.
	 *
	 * @param ccp The properties associated to a component context
	 */
	public void dispose ( ComponentContextProperties ccp ) {

	}
}
