package com.vasi.learning.api.resources.v1;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.context.ApplicationContext;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jwt.JWTClaimsSet;
import com.vasi.learning.ApplicationManager;
import com.vasi.learning.model.v1.Credentials;
import com.vasi.learning.model.v1.User;
import com.vasi.learning.persistence.dao.impl.UserDao;
import com.vasi.learning.persistence.util.AuthenticationManagerUtils;

import io.swagger.annotations.Api;

@Api (value = "Authentication Resource")
@Path("v1/authentication")
public class AuthenticationEndpoint {
	
	private final long EXPIRATION_TIME = 60 * 60 * 1000; // 1 hr - 3600000 ms

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response authenticateUser(Credentials credentials) {

        try {

            // Authenticate the user using the credentials provided
            User user = authenticate(credentials.getUsername(), credentials.getPassword());

            // Issue a token for the user
            String token = issueToken(user);

            // Return the token on the response
            return Response.ok(token).build();

        } catch (NotFoundException e) {
        	return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e) {
        	e.printStackTrace();
            return Response.status(Response.Status.FORBIDDEN).build();
        }      
    }

    private User authenticate(String username, String password) throws Exception {
    	System.out.println("Checking isAuthnticated");
        // Authenticate against a database, LDAP, file or whatever
    	// Throw an Exception if the credentials are invalid
    	ApplicationContext context = ApplicationManager.getApplicationContext();			
		UserDao userDao = context.getBean(UserDao.class);
		User user = userDao.read(username);
		if (user != null) {
			System.out.println("User is " + user.getFirstName());
			if (!userDao.isAuthentic(username, password)) {
				throw new Exception();
			}
		} else {
			throw new NotFoundException();
		}
		return user;
    }

    private String issueToken(User user) {
    	JWTClaimsSet claims = new JWTClaimsSet.Builder()
    			  .claim("id", user.getId())
    			  .claim("email", user.getEmail())
    			  .claim("name", user.getFirstName())
    			  .claim("roleId", user.getUserType().getId())
    			  .claim("roleName", user.getUserType().getName())
    			  .expirationTime(new Date(new Date().getTime() + EXPIRATION_TIME))
    			  .build();
    	// Prepare Payload
    	Payload payload = new Payload(claims.toJSONObject());
    	// Prepare Header
    	JWEHeader header = new JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256);
    	// Set the Encryption key		
		byte[] secretKey = AuthenticationManagerUtils.getSecret().getBytes();
		DirectEncrypter encrypter = null;
		try {
			encrypter = new DirectEncrypter(secretKey);
		} catch (KeyLengthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Generate the token
		JWEObject jweObject = new JWEObject(header, payload);
		try {
			if (encrypter != null)
			jweObject.encrypt(encrypter);
		} catch (JOSEException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String token = jweObject.serialize();
    	return token;
    }
}