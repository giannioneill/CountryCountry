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

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.imirsel.m2k.io.file.MP3ThreadedAudioPlayer;
import org.imirsel.m2k.util.Signal;
import org.imirsel.m2k.util.noMetadataException;
import org.imirsel.meandre.m2k.StreamTerminator;
import org.meandre.annotations.Component;
import org.meandre.annotations.ComponentInput;
import org.meandre.annotations.ComponentOutput;
import org.meandre.annotations.ComponentProperty;
import org.meandre.core.ComponentContext;
import org.meandre.core.ComponentContextException;
import org.meandre.core.ComponentContextProperties;
import org.meandre.core.ComponentExecutionException;
import org.meandre.core.ExecutableComponent;
import org.meandre.core.logger.KernelLoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;
import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

@Component(creator = "Gianni O'Neill", description = "This component adds RDF assertions about the audio to a RDF model.", name = "OutputRDF", tags = "xml", firingPolicy = Component.FiringPolicy.any)
public class OutputRDF implements ExecutableComponent {

    public final static String INPUT_1_classNames = "classNames";
    @ComponentInput(description = "class name for each column of likelihoods(vector of storing String[])", name = INPUT_1_classNames)
    public final static String INPUT_2_signal = "signal";
    @ComponentInput(description = "signal object conatining the metadata for the audio files(signal)", name = INPUT_2_signal)
    public final static String INPUT_3_likelihoodFrame = "likelihoodFrame";
    @ComponentInput(description = "classificaiton likelihood frames for the signal object(vector of storing vector)", name = INPUT_3_likelihoodFrame)
    public final static String INPUT_4_modelnames = "RDF_Modelnames";
    @ComponentInput(description = "JENA RDF Model", name = INPUT_4_modelnames)
    public final static String OUTPUT_rdf = "RDF_Model_Out";
    @ComponentOutput(description = "rdf model", name = OUTPUT_rdf)
    /** The instance ID */
    private String sInstanceID = null; 
    
    private final static String MO = "http://purl.org/ontology/mo/";
    private final static String MUSIM = "http://purl.org/ontology/similarity/";
    private final static String PV = "http://purl.org/net/provenance/ns#";
    private final static String ORE = "http://www.openarchives.org/ore/terms/";
    
    //store data
    private Signal signal;
    private Vector<String[]> classNames;
    private Vector<String> modelNames;
    private Vector<Vector<double[]>> historyVectorOfModelVectorOfClassLikelihoods;
    private Resource track;
    private Resource aggregate;
    private Map<String, Resource> classMap;
    private Model model;
    private String flowURI;
    private int count; // for counting the URIs
//    private double duration;
    File destAudio; //destination file for copy to public/resources directory

    //true if DATA_INPUT_1 is available, otherwise false
    boolean input1;
    //true if DATA_INPUT_2 is available, otherwise false
    boolean input2;
    //true if DATA_INPUT_3 is available, otherwise false
    boolean input3;
    //true if DATA_INPUT_4 is available, otherwise false
    boolean input4;
    
    
    /** A simple message.
     *
     * @return The HTML containing the page
     */
    private void addGenreAssertion() throws Exception {
    	 
    	Property genre = model.createProperty(MO, "genre");
        String fileName = null;
        String location = null;
        try {
            location = (String) signal.getStringMetadata(Signal.PROP_FILE_LOCATION);
        } catch (noMetadataException ex) {
            Logger.getLogger(OutputRDF.class.getName()).log(Level.SEVERE, null, ex);
        }
        int posOfLastSeperator = location.lastIndexOf(File.separator);
        fileName = location.substring(posOfLastSeperator + 1);

        int numberOfFrames = historyVectorOfModelVectorOfClassLikelihoods.size();
        int numberOfModels = historyVectorOfModelVectorOfClassLikelihoods.elementAt(0).size();


        String songid = fileName;

        for (int modelIndex = 0; modelIndex < numberOfModels; modelIndex++) {

            String[] modelClassNames = (String[]) classNames.elementAt(modelIndex);

            for (int historyIndex = 0; historyIndex < numberOfFrames; historyIndex++) {

                for (int classIndex = 0; classIndex < modelClassNames.length; classIndex++) {

                    double[] theFrame = historyVectorOfModelVectorOfClassLikelihoods.elementAt(historyIndex).elementAt(modelIndex);

                    if (theFrame[classIndex] < 0.0) {

                        theFrame[classIndex] = 0.0;

//                        System.out.println("Error in normalization!  theFrame[classIndex] < 0.0");
//                        throw new Exception();
                    }

                }


                boolean makePDF = true;


                if (makePDF) {

                    double sum = 0.0;
                    for (int classIndex = 0; classIndex < modelClassNames.length; classIndex++) {

                        double[] theFrame = historyVectorOfModelVectorOfClassLikelihoods.elementAt(historyIndex).elementAt(modelIndex);

                        sum += theFrame[classIndex];
                    }

                    for (int classIndex = 0; classIndex < modelClassNames.length; classIndex++) {

                        double[] theFrame = historyVectorOfModelVectorOfClassLikelihoods.elementAt(historyIndex).elementAt(modelIndex);

                        if (sum > 0) {
                            theFrame[classIndex] /= sum;
                        }
                    }

                }

            }



			for (int classIndex = 0; classIndex < modelClassNames.length; classIndex++) {
            	String className = modelClassNames[classIndex];
            	
            	int valueSum = 0;
            	
                for (int historyIndex = 0; historyIndex < numberOfFrames; historyIndex++) {

                    double[] theFrame = historyVectorOfModelVectorOfClassLikelihoods.elementAt(historyIndex).elementAt(modelIndex);
                    double value = theFrame[classIndex];

                    int intValue = (int) (value * 100);

                    int frameOffset = 5;
					
					valueSum += intValue;
                    
                }
                
                double avgScore = valueSum/numberOfFrames;
                
                //add data to model here
                Resource assoc = model.createResource(this.flowURI+"#assoc"+count);
                Resource method = model.createResource(this.flowURI+"#method"+count);
                count++;
                
                assoc.addProperty(RDF.type, model.createResource(MUSIM+"Association"));
                assoc.addProperty(model.createProperty(MUSIM, "subject"), track);
                assoc.addProperty(model.createProperty(MUSIM, "weight"), 
                		model.createTypedLiteral(avgScore));
                
                assoc.addProperty(model.createProperty(MUSIM, "method"), method);
                aggregate.addProperty(model.createProperty(ORE, "aggregates"), assoc);
                
                Resource classResource;
                if((classResource = classMap.get(className)) != null){
                	assoc.addProperty(model.createProperty(MUSIM,"object"), classResource);
                }else{
                	assoc.addProperty(model.createProperty(MUSIM,"object"), className);
                }
                
                method.addProperty(RDF.type, model.createResource(MUSIM+"AssociationMethod"));
                method.addProperty(model.createProperty(PV, "usedGuideline"), 
                		model.createResource(modelNames.get(modelIndex)));
                
            
			}
            
        }

    }


    /** Call at the end of an execution flow.
     *
     *
     */
    public void dispose(ComponentContextProperties ccp) {
        if (destAudio != null) {
            destAudio.delete();
        }
    }

    /** When ready for execution.
     *
     * @param cc The component context
     * @throws ComponentExecutionException An exeception occurred during execution
     * @throws ComponentContextException Illigal access to context
     */
    public void execute(ComponentContext cc) throws ComponentExecutionException,
            ComponentContextException {


//        log.info("################################ executing CreateBlinkieXML");
        try {
            if (cc.isInputAvailable(INPUT_1_classNames)) {
//                log.info("################################ executing CreateBlinkieXML: got input #1");
                classNames = (Vector<String[]>) cc.getDataComponentFromInput(INPUT_1_classNames);
                input1 = true;
            }

            if (cc.isInputAvailable(INPUT_2_signal)) {
//                log.info("################################ executing CreateBlinkieXML: got input #2");
                signal = (Signal) (cc.getDataComponentFromInput(INPUT_2_signal));
                input2 = true;
                MP3ThreadedAudioPlayer player = null;
                try {
                    player = new MP3ThreadedAudioPlayer(signal.getFile());
                //player.init();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
//                duration = (int)(player.getDuration()*1000);
            }

            if (cc.isInputAvailable(INPUT_3_likelihoodFrame)) {
//                log.info("################################ executing CreateBlinkieXML: got input #3");

                Object input = cc.getDataComponentFromInput(INPUT_3_likelihoodFrame);

                if (StreamTerminator.isStreamTerminator(input)) {
                    input3 = true;
                } else {
                    historyVectorOfModelVectorOfClassLikelihoods.add((Vector) cc.getDataComponentFromInput(INPUT_3_likelihoodFrame));
                }
            }

            if (cc.isInputAvailable(INPUT_4_modelnames)) {
                Object input = cc.getDataComponentFromInput(INPUT_4_modelnames);
                modelNames = (Vector<String>) input;
                input4 = true;
            }

            sInstanceID = cc.getExecutionInstanceID();


            if (input1 && input2 && input3 && input4) {
//                log.info("################################ executing CreateBlinkieXML: got all inputs");
            	String fileUri = signal.getStringMetadata(Signal.PROP_FILE_LOCATION);
            	this.model = model.read(fileUri);
            	
            	Resource audioFile = model.getResource(fileUri);
            	Statement encodesProp = audioFile.getProperty(model.createProperty(MO, "encodes"));
            	RDFNode signal;
            	if(encodesProp != null){
            		signal = encodesProp.getObject();
            		//dereference signal URI
                	model.read(signal.toString());
            	}else{
            		//mint URI here
            		signal = model.createResource(this.flowURI+"#signal");
            		model.add((Resource)signal, RDF.type, model.createResource(MO+"Signal"));
            	}
            	
            	
            	Resource signalResource = model.getResource(signal.toString());
            	Statement releaseProp = signalResource.getProperty(model.createProperty(MO,"published_as")); 
            	if(releaseProp != null){
            		RDFNode tracknode = releaseProp.getObject();
            		track = model.getResource(tracknode.toString());
            	}else{
            		//mint URI here
            		track = model.createResource(this.flowURI+"#track");
            		model.add(track, model.createProperty(MO,"available_as"), audioFile);
            	}
            	addGenreAssertion();
                

//                log.info("################################ executing CreateBlinkieXML xml:" + xml);

                cc.pushDataComponentToOutput(OUTPUT_rdf, model);
            }


        } catch (Exception e) {
            throw new ComponentExecutionException(e);
        }
    }
    /** Called when a flow is started.
     *
     */
    int frameDurationInMS = 1000;
    protected transient static Logger log = KernelLoggerFactory.getCoreLogger();

    /*public void initialize() {
        signal = null;
        classNames = null;
        historyVectorOfModelVectorOfClassLikelihoods = new Vector();
        input1 = false;
        input2 = false;
        input3 = false;
    }*/

    public void initialize(ComponentContextProperties ccp) {
        try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			String hash = HexBin.encode(md.digest(ccp.getFlowExecutionInstanceID().getBytes()));
			this.flowURI = "http://results.nema.ecs.soton.ac.uk/"+hash;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return;
		}
    	signal = null;
        classNames = null;
        historyVectorOfModelVectorOfClassLikelihoods = new Vector();
        input1 = false;
        input2 = false;
        input3 = false;
        this.model = ModelFactory.createDefaultModel();
        
        aggregate = model.createResource(this.flowURI+"#aggregate");
        aggregate.addProperty(RDF.type, model.createResource(ORE+"Aggregation"));
        
        Resource rm = model.createResource(this.flowURI);
        rm.addProperty(RDF.type, model.createResource(ORE+"ResourceMap"));
        rm.addProperty(model.createProperty(ORE,"describes"), aggregate);
        rm.addProperty(DC.creator, "http://enactor.nema.ecs.soton.ac.uk/about.rdf#enactor");
        
        classMap = new HashMap<String, Resource>();
        classMap.put("Baroque", model.createResource("http://dbpedia.org/page/Baroque_music"));
        classMap.put("Romantic", model.createResource("http://dbpedia.org/page/Romantic_music"));
        classMap.put("Classical", model.createResource("http://dbpedia.org/page/Classical_music"));
        classMap.put("Country", model.createResource("http://dbpedia.org/resource/Country_music"));
        classMap.put("Blues", model.createResource("http://dbpedia.org/resource/Blues"));
        classMap.put("Jazz", model.createResource("http://dbpedia.org/resource/Jazz"));
        classMap.put("Electronica & Dance", model.createResource("http://dbpedia.org/resource/Electronica"));
        classMap.put("HipHop & Rap", model.createResource("http://dbpedia.org/resource/Hip_hop_music"));
        classMap.put("HardRock & Metal", model.createResource("http://dbpedia.org/resource/Hard_rock"));
        classMap.put("Rock", model.createResource("http://dbpedia.org/resource/Rock_music"));
        classMap.put("Rock & Roll", model.createResource("http://dbpedia.org/resource/Rock_and_roll"));
    }
}