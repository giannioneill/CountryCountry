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

import java.io.ByteArrayOutputStream;
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

/** This executable component just concatenates the two input strings received
 * pushing it to the output.
 *
 * @author John Doe;
 *
 */
@Component(creator="Gianni O'Neill", description="takes an RDF model as input and converts to a java string", 
		name="ModelToString",
		tags="rdf jena model string out")
public class ModelToString implements ExecutableComponent {


	@ComponentInput(description="RDF Model", name="rdf")
	final static String DATA_INPUT_RDF = "rdf";
	
	@ComponentOutput(description="Output String", name="string_out")
	final static String DATA_OUTPUT_STR= "string_out";

	
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
		Model m = (Model) cc.getDataComponentFromInput(DATA_INPUT_RDF);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		m.write(stream);
		cc.pushDataComponentToOutput(DATA_OUTPUT_STR, stream.toString());
	}

	/** This method is called when the Menadre Flow execution is completed.
	 *
	 * @param ccp The properties associated to a component context
	 */
	public void dispose ( ComponentContextProperties ccp ) {

	}
}
