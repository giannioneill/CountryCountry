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

import org.imirsel.m2k.util.Signal;
import org.imirsel.meandre.m2k.StreamTerminator;
import org.meandre.annotations.Component;
import org.meandre.annotations.ComponentInput;
import org.meandre.annotations.ComponentOutput;
import org.meandre.core.ComponentContext;
import org.meandre.core.ComponentContextException;
import org.meandre.core.ComponentContextProperties;
import org.meandre.core.ComponentExecutionException;
import org.meandre.core.ExecutableComponent;

@Component(creator = "Gianni O'Neill", description = "take a URL String as input and create a signal object.", name = "URL To Signal Object", tags = "file reader")
public class URLToSignal implements ExecutableComponent {
    @ComponentInput(description = "file name", name = "url")
    final static String DATA_INPUT_URL = "url";
    @ComponentOutput(description = "signal object", name = "signal")
    final static String DATA_OUTPUT_SIGNAL = "signal";

    
    public void initialize(ComponentContextProperties ccp) {
    }

    public void dispose(ComponentContextProperties ccp) {
    }

    public void execute(ComponentContext cc) throws ComponentExecutionException, ComponentContextException {
        Object input = cc.getDataComponentFromInput(DATA_INPUT_URL);
        
        if (StreamTerminator.isStreamTerminator(input)) {
            cc.pushDataComponentToOutput(DATA_OUTPUT_SIGNAL, input);
            return;
           
        }
        String url = (String) input;
        
        Signal outputSignal = new Signal(url);
        cc.pushDataComponentToOutput(DATA_OUTPUT_SIGNAL, outputSignal);
    }
}



