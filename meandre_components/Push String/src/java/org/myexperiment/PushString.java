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

/** 
 * This component pushes a native Java string on the output port as
 * opposed to Push Text which uses an org.seasr.BasicDataTypes object
 *
 * @author Gianni O'Neill;
 *
 */
@Component(creator="Gianni O'Neill", description="This component pushes a native Java string on the output port as opposed to Push Text which uses an org.seasr.BasicDataTypes object", 
		name="Push String",
		tags="test print hello")
public class PushString implements ExecutableComponent {
	

	@ComponentOutput(description="Output String", name="string_out")
	final static String DATA_OUTPUT_1= "string_out";
	
	@ComponentProperty(description="String", 
			name = "string",
			defaultValue = "")
	final static String DATA_PROPERTY_STRING = "string";

	
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
		String str = cc.getProperty(DATA_PROPERTY_STRING);
		cc.pushDataComponentToOutput(DATA_OUTPUT_1, str);
	}

	/** This method is called when the Menadre Flow execution is completed.
	 *
	 * @param ccp The properties associated to a component context
	 */
	public void dispose ( ComponentContextProperties ccp ) {

	}
}
