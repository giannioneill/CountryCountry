/*
 * @(#) NonWebUIFragmentCallback.java @VERSION@
 * 
 * Copyright (c) 2008+ Amit Kumar
 * 
 * The software is released under ASL 2.0, Please
 * read License.txt
 *
 */

package org.imirsel.meandre.m2k.math;

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
import org.meandre.core.system.components.ext.StreamTerminator;
import org.imirsel.m2k.math.Mathematics;


/**
 * Calculates Log values of an input array of numbers. A D2K/M2K module that reads in
 * a double array of real numbers, and returns the Log values of them.
 *
 * @author David Tcheng
 * Modified by Lily Dong
 */

@Component(creator="David Tcheng",
           description="calculate Log values of an input array of numbers",
           name="LogN",
           tags="LogN")

public class LogN implements ExecutableComponent {//extends ComputeModule {
    @ComponentInput(description="input values(1-D double array)",
                    name= "input")
    final static String DATA_INPUT = "input";

    @ComponentOutput(description="Log(Input)(1-D double array)",
                     name="output")
    final static String DATA_OUTPUT = "output";

    @ComponentProperty(defaultValue="10.0",
                       description="set the logarithmic base",
                       name="baseN")
    final static String DATA_PROPERTY = "baseN";


        private double baseN = 10.0;

          /**
           * Sets the Log base
           *
           * @param value Log base
           */
        public void setBaseN(double value) {
                this.baseN = value;
        }
        /**
         * Returns the Log base
         *
         * @return the Log base
         */
        public double getBaseN() {
                return this.baseN;
        }

        /**
     * Returns an array of description objects for each property of
     * the Module.
     *
     * @return an array of description objects for each property of
     * the Module.
     */
/*    public PropertyDescription[] getPropertiesDescriptions() {
        PropertyDescription[] pds = new PropertyDescription[1];

        pds[0] = new PropertyDescription("baseN", "N, the base of the log",
                "Sets the logarithmic base. Setting the base to 0.0 produces " +
                "the natural log, base e.");

        return pds;
    }*/

          /**
           * Returns the name of the module
           *
           * @return the module name
           */
        public String getModuleInfo() {
                return "<p>Overview: Calulates Log (base N) of each element of an array of doubles." +
                                "</p><p>Data Handling: The input data is not modified.";
        }
          /**
           * Returns information about the module
           *
           * @return Module information
           */
        public String getModuleName() {
                return "LogN";
        }

         /**
           * Returns an array of strings containing the Java data types of
           * the input.
           *
           * @return the fully qualified java types for each of the inputs
           */
        public String[] getInputTypes() {
                String[] types = { "[D" };
                return types;
        }

         /**
           * Returns an array of strings containing the Java data types of
           * the outputs.
           *
           * @return the fully qualified java types for each of the outputs.
           */
        public String[] getOutputTypes() {
                String[] types = { "[D" };
                return types;
        }

         /**
           * Returns a text description for the indicated input
           *
           * @param i the index of the input
           *
           * @return a text description of the indexed input
           */
        public String getInputInfo(int i) {
                switch (i) {
                case 0:
                        return "Input values (1D double array)";
                default:
                        return "No such input";
                }
        }

         /**
           * Returns a text name for the indicated input
           *
           * @param i the index of the input
           *
           * @return the name of the indexed input
           */
        public String getInputName(int i) {
                switch (i) {
                case 0:
                        return "Input";
                default:
                        return "NO SUCH INPUT!";
                }
        }

         /**
           * Returns a text description for the indicated output
           *
           * @param i the index of the output
           *
           * @return the text description for the indicated output
           */
        public String getOutputInfo(int i) {
                switch (i) {
                case 0:
                        return "Log(Input) (1-D double array)";
                default:
                        return "No such output";
                }
        }

          /**
           * Returns a text name for the indicated output
           *
           * @param i the index of the output
           *
           * @return the name of the indexed output
           */
        public String getOutputName(int i) {
                switch (i) {
                case 0:
                        return "Log(Input)";
                default:
                        return "NO SUCH OUTPUT!";
                }
        }

        /**
         * Calculates Log values of an input array of numbers. If an input value is zero,
         * use the preset LogZeroValue as its correspondent output.
         *
         */
        public void execute(ComponentContext cc)
                throws ComponentExecutionException, ComponentContextException {//doit() {
           
            //for seasr only
            baseN = Double.valueOf(cc.getProperty(DATA_PROPERTY));
            Object input = cc.getDataComponentFromInput(DATA_INPUT);
            if (input instanceof StreamTerminator){
                cc.pushDataComponentToOutput(DATA_OUTPUT, input);
            }else{
                double[] data = (double[])input;
                //double[] data = (double[]) this.pullInput(0);


                /*for(int i=0; i<data.length; i++)
                    System.out.println("data[" + i + "] = " + data[i]);*/

                double[] outputData = Mathematics.logN(data, baseN);

                /*for(int i=0; i<outputData.length; i++)
                    System.out.println("outputData["+ i + "] = " + outputData[i]);*/

                cc.pushDataComponentToOutput(DATA_OUTPUT, outputData);
                //this.pushOutput(outputData, 0);
            }
        }

        public void initialize(ComponentContextProperties ccp) {}
        public void dispose(ComponentContextProperties ccp) {}
}

