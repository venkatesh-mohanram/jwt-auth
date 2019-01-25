package com.vasi.learning.resources.filters.v1;

import java.io.IOException;
import java.util.Set;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
@PreMatching
public class CorsFilter implements ContainerRequestFilter, ContainerResponseFilter {

    /**
     * Method for ContainerRequestFilter.
     */
    @Override
    public void filter(ContainerRequestContext request) throws IOException {
    	System.out.println("CrosFilter:RequestOnly");
        // If it's a preflight request, we abort the request with
        // a 200 status, and the CORS headers are added in the
        // response filter method below.
        if (isPreflightRequest(request)) {
        	System.out.println("It is a preflight");
        	preflight(request);            
            return;
        }
        System.out.println("Origin is " + request.getHeaderString("Origin"));
        System.out.println("Method is " + request.getMethod());
    }

    /**
     * A preflight request is an OPTIONS request
     * with an Origin header.
     */
    private static boolean isPreflightRequest(ContainerRequestContext request) {
        return request.getHeaderString("Origin") != null
                && request.getMethod().equalsIgnoreCase("OPTIONS");
    }
    
    private void preflight(ContainerRequestContext requestContext) throws IOException
    {       

       Response.ResponseBuilder builder = Response.ok();
       builder.header("Access-Control-Allow-Origin", "*");       
       builder.header("Access-Control-Allow-Credentials", "true");
       builder.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
       builder.header("Access-Control-Allow-Headers",
           // Whatever other non-standard/safe headers (see list above) 
           // you want the client to be able to send to the server,
           // put it in this list. And remove the ones you don't want.
           "X-Requested-With, Authorization, " +
           "Accept-Version, Content-MD5, CSRF-Token, Content-Type");
       requestContext.abortWith(builder.build());

    }

    /**
     * Method for ContainerResponseFilter.
     */
    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response)
            throws IOException {
    	System.out.println("CrosFilter: Request and Response expected");
        // if there is no Origin header, then it is not a
        // cross origin request. We don't do anything.
        if (request.getHeaderString("Origin") == null) {
            return;
        }
        Set<String> keySet = response.getHeaders().keySet();
        for (String key : keySet) {        	
        	System.out.println(key + "=" + response.getHeaderString(key));
        }
        // If it is a preflight request, then we add all
        // the CORS headers here.        
        if (isPreflightRequest(request)) {        	
        	System.out.println("Adding all the headers");
        	if (!response.getHeaders().containsKey("Access-Control-Allow-Credentials"))
        		response.getHeaders().add("Access-Control-Allow-Credentials", "true");
        	if (!response.getHeaders().containsKey("Access-Control-Allow-Methods"))
        		response.getHeaders().add("Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        	if (!response.getHeaders().containsKey("Access-Control-Allow-Headers"))
	            response.getHeaders().add("Access-Control-Allow-Headers",
	                // Whatever other non-standard/safe headers (see list above) 
	                // you want the client to be able to send to the server,
	                // put it in this list. And remove the ones you don't want.
	                "X-Requested-With, Authorization, " +
	                "Accept-Version, Content-MD5, CSRF-Token, Content-Type");
        }

        // Cross origin requests can be either simple requests
        // or preflight request. We need to add this header
        // to both type of requests. Only preflight requests
        // need the previously added headers.
    	if (!response.getHeaders().containsKey("Access-Control-Allow-Origin"))
    		response.getHeaders().add("Access-Control-Allow-Origin", "*");
    }
}