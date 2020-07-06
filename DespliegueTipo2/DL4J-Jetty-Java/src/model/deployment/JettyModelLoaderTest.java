package model.deployment;

import java.io.File;
import java.io.IOException;

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

public class JettyModelLoaderTest extends AbstractHandler {

	/** the model loaded from Keras **/
	private MultiLayerNetwork model;

	/** the number of input parameters in the Keras model **/
	private static int numberInputs = 10;

	/** launch a web server on port 5000 */
	public static void main(String[] args) throws Exception {
		Server server = new Server(5003);
		server.setHandler(new JettyModelLoaderTest());
		server.start();
		server.join();
	}

	/** Loads the Keras Model **/
	public JettyModelLoaderTest() throws Exception {
		String modelKerasH5Full = "/home/mirko/Desktop/ModelH5/games.h5";
		File modelFile = new File(modelKerasH5Full);
		model = KerasModelImport.importKerasSequentialModelAndWeights(modelFile.getPath());
		System.out.println("model: " + model);
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
