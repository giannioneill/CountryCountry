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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

/** This executable component writes to a filename 
 * based on a hash of the flow's execution ID.
 *
 * @author Gianni O'Neill;
 *
 */
@Component(creator="Gianni O'Neill", description="Writes to a file based on the hash of the flow's unique execution ID", 
		name="WriteToUniqeFile",
		tags="write print file hash")
public class WriteToUniqeFile implements ExecutableComponent {


	@ComponentInput(description="Input sring", name="str")
	final static String DATA_INPUT_STR= "str";
	
	@ComponentProperty(description="filename prefix", 
			name = "prefix",
			defaultValue = "")
	final static String DATA_PROPERTY_PREFIX = "prefix";
	
	
	// log messages are here
	private Logger _logger;
	private BufferedWriter writer;
	

	public void initialize ( ComponentContextProperties ccp ) {
		try{
			MessageDigest md = MessageDigest.getInstance("SHA");
			String hash = HexBin.encode(md.digest(ccp.getFlowExecutionInstanceID().getBytes()));
			String filename = ccp.getProperty(DATA_PROPERTY_PREFIX) + hash;
			File file = new File(filename);
			this.writer = new BufferedWriter(new FileWriter(file));
		}catch(Exception e){
			e.printStackTrace();
			return;
		}
	}


	public void execute(ComponentContext cc) throws ComponentExecutionException, ComponentContextException {
		 try {
			writer.write((String)cc.getDataComponentFromInput(DATA_INPUT_STR));
			writer.flush();
		} catch (Exception e) {
			throw new ComponentExecutionException(e.getMessage());
		}
	}

	public void dispose ( ComponentContextProperties ccp ) {
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
