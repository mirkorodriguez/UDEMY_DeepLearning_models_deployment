//mvn clean compile package install
//mvn exec:java -Dexec.mainClass="model.deployment.JettyModelLoader" -Dexec.args="5005"

package model.deployment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class JettyModelLoader extends AbstractHandler {

	private Properties prop;
	
	private String propFileName; 
	
	private InputStream inputStream;
	
	private MultiLayerNetwork model;

	private static int numberInputs = 10;

	// Jetty server initializer
	public static void main(String[] args) throws Exception {
		int portNumber = Integer.valueOf(args[0]);
		System.out.println("\n\nInicializando Jetty server en el pueto "+portNumber+" ...");
		Server server = new Server(portNumber);
		server.setHandler(new JettyModelLoader());
		server.start();
		System.out.println("Jetty server inicializado");
		server.join();
	}


	public JettyModelLoader() throws Exception {
		
		prop = new Properties();
		propFileName = "model.properties";

		inputStream = getClass().getClassLoader().getResourceAsStream("model.properties");

		if (inputStream != null) {
			prop.load(inputStream);
		} else {
			throw new FileNotFoundException("Property file '" + propFileName + "' not found in the classpath.");
		}
		
		// get the property value
		String modelKerasH5 = prop.getProperty("modelh5");
					
		File modelFile = new File(modelKerasH5);
		if(!modelFile.isFile()){
			throw new FileNotFoundException("Model file '" + modelFile.getAbsolutePath() + "' not found.");
		}
		
		model = KerasModelImport.importKerasSequentialModelAndWeights(modelFile.getAbsolutePath());
		System.out.println(" >>Model file loaded: " + modelFile.getAbsolutePath());
		System.out.println(" >>Model instance loaded: " + model);
	}


	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		// create a dataset from the input parameters
		INDArray features = Nd4j.zeros(numberInputs);
		for (int i = 0; i < numberInputs; i++) {
			features.putScalar(new int[] { i }, Double.parseDouble(baseRequest.getParameter("G" + (i + 1))));
		}

		INDArray input = features.reshape(1, numberInputs);
		System.out.println("Request features: " + input);

		// output the estimate
		double prediction = model.output(input).getDouble(0);
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println("Prediction: " + prediction);
		baseRequest.setHandled(true);

	}

}