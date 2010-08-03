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

/** This executable component Splits a string on the first instance of a
 * specified char
 *
 * @author Gianni O'Neill;
 *
 */
@Component(creator="Gianni O'Neill", description="Splits a string on the first instance of a specified char", 
		name="SplitString",
		tags="split strin")
public class SplitString implements ExecutableComponent {


	@ComponentInput(description="Input String", name="string_in")
	final static String DATA_INPUT_1= "string_in";

	@ComponentOutput(description="Output String 1", name="string_out1")
	final static String DATA_OUTPUT_1= "string_out1";
	
	@ComponentOutput(description="Output String 2", name="string_out2")
	final static String DATA_OUTPUT_2= "string_out2";
	
	@ComponentProperty(description="Char (or regex) to split on.", 
			name = "split",
			defaultValue = "")
	final static String DATA_PROPERTY_SPLIT = "split";

	
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

	public void execute(ComponentContext cc) throws ComponentExecutionException, ComponentContextException {
		String in = (String) cc.getDataComponentFromInput(DATA_INPUT_1);
		String splitter = cc.getProperty(DATA_PROPERTY_SPLIT);
		String[] parts = in.split(splitter, 2);
		if(parts.length < 2){
			throw new ComponentExecutionException("The seperator was not found in the input string.");
		}
		cc.pushDataComponentToOutput(DATA_OUTPUT_1, parts[0]);
		cc.pushDataComponentToOutput(DATA_OUTPUT_2, parts[1]);
	}

	/** This method is called when the Menadre Flow execution is completed.
	 *
	 * @param ccp The properties associated to a component context
	 */
	public void dispose ( ComponentContextProperties ccp ) {

	}
}