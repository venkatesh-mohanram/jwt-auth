package com.vasi.learning;

import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import io.swagger.jaxrs.config.BeanConfig;

import java.io.IOException;
import java.net.URI;

/**
 * Main class.
 *
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://0.0.0.0:8081/api/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.vasi.learning package
    	String resources = "com.vasi.learning";
    	BeanConfig beanConfig = new BeanConfig();
        beanConfig.setTitle("Vasi Learning Server APIs");
        beanConfig.setVersion("1.0.0");
        beanConfig.setDescription("Server REST APIs to be consumed by clients");
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setBasePath("/api");
		beanConfig.setResourcePackage(resources);
        beanConfig.setScan(true);

        final ResourceConfig rc = new ResourceConfig();
        rc.packages(resources);
        rc.register(io.swagger.jaxrs.listing.ApiListingResource.class);
        rc.register(io.swagger.jaxrs.listing.SwaggerSerializers.class);
        rc.register(org.glassfish.jersey.moxy.json.MoxyJsonFeature.class);
        
        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);        
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
    	try {
        	final HttpServer server = startServer();
        	
        	// Add shutdown hook to stop the server
        	Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
        		
        		@Override
        		public void run() {
        			server.shutdown();
        			
        		}
        		
        	}));
        	server.start();
        	
        	// Setting the Swagger UI
        	ClassLoader loader = Main.class.getClassLoader();
            CLStaticHttpHandler docsHandler = new CLStaticHttpHandler(loader, "swagger-ui/");
            docsHandler.setFileCacheEnabled(false);

            ServerConfiguration cfg = server.getServerConfiguration();
            cfg.addHttpHandler(docsHandler, "/docs");
    
        	System.out.println(String.format("Jersey app started with WADL available at "
        			+ "%sapplication.wadl\nPress Ctrl + C to stop the server...", BASE_URI));
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}

