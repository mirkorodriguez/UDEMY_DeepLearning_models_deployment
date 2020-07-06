//mvn clean compile package install
//mvn exec:java -Dexec.mainClass="model.deployment.JettyModelLoaderTest"
//mvn exec:java -Dexec.mainClass="model.deployment.JettyServerTest" -Dexec.args="5000"

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

public class JettyServerTest extends AbstractHandler {


	public static void main(String[] args) throws Exception {
		int portNumber = Integer.valueOf(args[0]);
		System.out.println("Inicializando Jetty server en el pueto "+portNumber+" ...");
		Server server = new Server(portNumber);
		server.setHandler(new JettyServerTest());
		server.start();
		System.out.println("Jetty server inicializado");
		server.join();
	}

	public JettyServerTest() throws Exception {
		System.out.println("Calling constructor ...");
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		System.out.println("Calling handle service ...");
	}

}
