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
import java.util.Vector;

/** This executable component turns a String into a Vector<String>
 * @author Gianni O'Neill;
 *
 */
@Component(creator="Gianni O'Neill", description="Vectorize String", 
		name="VectorizeString",
		tags="string adaptor vector")
public class VectorizeString implements ExecutableComponent {


	@ComponentInput(description="String", name="string")
	final static String DATA_INPUT_1= "string";
	

	@ComponentOutput(description="Vector", name="vector")
	final static String DATA_OUTPUT_1= "vector";

	
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
		String str = (String) cc.getDataComponentFromInput(DATA_INPUT_1);
		Vector<String> v = new Vector<String>();
		v.add(str);
		cc.pushDataComponentToOutput(DATA_OUTPUT_1, v);
	}

	/** This method is called when the Menadre Flow execution is completed.
	 *
	 * @param ccp The properties associated to a component context
	 */
	public void dispose ( ComponentContextProperties ccp ) {

	}
}