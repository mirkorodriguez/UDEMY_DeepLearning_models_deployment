//mvn clean compile package install
//mvn exec:java -Dexec.mainClass="model.deployment.JettyModelLoader" -Dexec.args="5005"

package model.deployment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.StringUtil;

public class JettyModelLoader extends AbstractHandler {

	private Properties prop;
	
	private String propFileName; 
	
	private InputStream inputStream;
	
	private MultiLayerNetwork model;

	private static int numberInputs = 10;

	// Jetty server initializer
	public static void main(String[] args) throws Exception {
//		int portNumber = Integer.valueOf(args[0]);
		int portNumber = Integer.valueOf("5000");
		System.out.println("\n\nInicializando Jetty server en el pueto "+portNumber+" ...");
		Server server = new Server(portNumber);
		server.setHandler(new JettyModelLoader());
		server.start();
		System.out.println("Jetty server inicializado");
		server.join();
	}

	// Model Loader
	public JettyModelLoader() throws Exception {
//		super();
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
		
//		https://jrmerwin.github.io/deeplearning4j-docs/model-import-keras.html
//		enforceTrainingConfig=false
//		Cloud DataFlow: Provides autoscaling for batch predictions on GCP
//		https://towardsdatascience.com/deploying-keras-deep-learning-models-with-java-62d80464f34a
		model = KerasModelImport.importKerasSequentialModelAndWeights(modelFile.getAbsolutePath());
		System.out.println(" >>Model file loaded: " + modelFile.getAbsolutePath());
		System.out.println(" >>Model instance loaded: " + model);
	}


	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		
//		https://github.com/jetty-project/embedded-jetty-cookbook/blob/master/src/main/java/org/eclipse/jetty/cookbook/MultipartMimeUploadExample.java
//		https://stackoverflow.com/questions/57201328/embedded-jetty-upload-fails-with-exception-content-type-multipart-form-data
//		https://nikgrozev.com/2014/10/16/rest-with-embedded-jetty-and-jersey-in-a-single-jar-step-by-step/
//		https://stackoverflow.com/questions/17652530/how-to-implement-fileupload-in-embedded-jetty
        if (!target.startsWith("/model/predict/"))
        {
            System.out.println(target + " CONTEXT no permitido ...");
            return;
        }
        
        if (!request.getMethod().equalsIgnoreCase("POST"))
        {
            System.out.println(request.getMethod() + " METHOD no permitido ...");
        	response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

//        // Ensure request knows about MultiPartConfigElement setup.
//        // MultiPartConfig setup - to allow for ServletRequest.getParts() usage
//        Path multipartTmpDir = Paths.get("target/multipart-tmp");
//        multipartTmpDir = ensureDirExists(multipartTmpDir);
//        String location = multipartTmpDir.toString();
//        long maxFileSize = 10 * 1024 * 1024; // 10 MB
//        long maxRequestSize = 10 * 1024 * 1024; // 10 MB
//        int fileSizeThreshold = 64 * 1024; // 64 KB
//        MultipartConfigElement multipartConfig = new MultipartConfigElement(location, maxFileSize, maxRequestSize, fileSizeThreshold);
//        request.setAttribute(Request.MULTIPART_CONFIG_ELEMENT, multipartConfig);
        
        // Process the request
        Path fileReceived = processParts(request, response);
        if(new File(fileReceived.toString()).exists()){
        	System.out.println("File "+fileReceived.getFileName()+" recibido");
        }
        baseRequest.setHandled(true);
        
		
//		// create a dataset from the input parameters
//		INDArray features = Nd4j.zeros(numberInputs);
//		for (int i = 0; i < numberInputs; i++) {
//			features.putScalar(new int[] { i }, Double.parseDouble(baseRequest.getParameter("G" + (i + 1))));
//		}
//
//		INDArray input = features.reshape(1, numberInputs);
//		System.out.println("Request features: " + input);
//
//		// output the estimate
//		double prediction = model.output(input).getDouble(0);
//		response.setStatus(HttpServletResponse.SC_OK);
//		response.getWriter().println("Prediction: " + prediction);
//		baseRequest.setHandled(true);

	}

	public static Path processParts(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
		System.out.println("Uploading file ...");
		
		Path fileProcessed = null;
        // Ensure request knows about MultiPartConfigElement setup.
        // MultiPartConfig setup - to allow for ServletRequest.getParts() usage
        Path multipartTmpDir = Paths.get("target/multipart-tmp");
        multipartTmpDir = ensureDirExists(multipartTmpDir);
        String location = multipartTmpDir.toString();
        long maxFileSize = 10 * 1024 * 1024; // 10 MB
        long maxRequestSize = 10 * 1024 * 1024; // 10 MB
        int fileSizeThreshold = 64 * 1024; // 64 KB
        MultipartConfigElement multipartConfig = new MultipartConfigElement(location, maxFileSize, maxRequestSize, fileSizeThreshold);

        request.setAttribute(Request.MULTIPART_CONFIG_ELEMENT, multipartConfig);

		
		
		// Establish output directory
        Path outputDir = Paths.get("target/upload-dir");
        outputDir = ensureDirExists(outputDir);
        
        response.setContentType("text/plain");
        response.setCharacterEncoding("utf-8");

        PrintWriter out = response.getWriter();

        for (Part part : request.getParts())
        {
            out.printf("Got Part[%s].size=%s%n", part.getName(), part.getSize());
            out.printf("Got Part[%s].contentType=%s%n", part.getName(), part.getContentType());
            out.printf("Got Part[%s].submittedFileName=%s%n", part.getName(), part.getSubmittedFileName());
            String filename = part.getSubmittedFileName();
            if (StringUtil.isNotBlank(filename))
            {
                // ensure we don't have "/" and ".." in the raw form.
                filename = URLEncoder.encode(filename, "utf-8");

                Path outputFile = outputDir.resolve(filename);
               
                InputStream inputStream = part.getInputStream();
                OutputStream outputStream = Files.newOutputStream(outputFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                IO.copy(inputStream, outputStream);
                out.printf("Saved Part[%s] to %s%n", part.getName(), outputFile.toString());                	
                
                System.out.println("File " +outputFile.getFileName()+ " procesado");              
                fileProcessed = outputFile;
            }
        }
        return fileProcessed;
    }
	
    private static Path ensureDirExists(Path path) throws IOException
    {
        Path dir = path.toAbsolutePath();

        if (!Files.exists(dir))
        {
            Files.createDirectories(dir);
        }

        return dir;
    }
}