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
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
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

import uk.co.magus.fourstore.client.Store;

import com.hp.hpl.jena.rdf.model.Model;

/** 
 *
 *This component takes a JENA model as input and writes it to a 4store
 *
 * @author Gianni O'Neill;
 *
 */
@Component(creator="Giani O'Neill", description="Takes a JENA model as input and writes it to a 4store", 
		name="AddTo4Store",
		tags="rdf triple store 4store")
public class AddTo4Store implements ExecutableComponent {


	@ComponentInput(description="JENA model", name="model")
	final static String DATA_INPUT_MODEL= "model";
	
	@ComponentProperty(description="4Store address", 
			name = "url",
			defaultValue = "http://localhost:8000")
	final static String DATA_PROPERTY_URL = "url";

	
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
		Model model = (Model) cc.getDataComponentFromInput(DATA_INPUT_MODEL);
		OutputStream os = new ByteArrayOutputStream();
		model.write(os);
		String rdf = os.toString();
		
		
		try{
			Store store = new Store(cc.getProperty(DATA_PROPERTY_URL));
			store.add(cc.getFlowExecutionInstanceID(), rdf, Store.InputFormat.XML);
		}catch(MalformedURLException e){
			throw new ComponentExecutionException(e.getMessage());
		}catch(IOException e){
			throw new ComponentExecutionException(e.getMessage());
		}
	}

	/** This method is called when the Menadre Flow execution is completed.
	 *
	 * @param ccp The properties associated to a component context
	 */
	public void dispose ( ComponentContextProperties ccp ) {

	}
}
