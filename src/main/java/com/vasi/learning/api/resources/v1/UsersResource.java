package com.vasi.learning.api.resources.v1;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.vasi.learning.ApplicationManager;
import com.vasi.learning.api.resources.logmessages.CategoryMessage;
import com.vasi.learning.api.resources.logmessages.UserMessage;
import com.vasi.learning.model.v1.ResponseMessage;
import com.vasi.learning.model.v1.User;
import com.vasi.learning.persistence.dao.impl.CategoryDao;
import com.vasi.learning.persistence.dao.impl.UserDao;
import com.vasi.learning.persistence.dao.impl.UserTypeDao;
import com.vasi.learning.resources.filters.v1.AccessRole;
import com.vasi.learning.resources.filters.v1.Secured;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api (value = "Users Resource")
@Path("v1/users")
public class UsersResource {
	
	private Logger logger = LoggerFactory.getLogger(UsersResource.class);
	
	@POST
	@Path("/signup")	
	@ApiOperation(
			value = "Create a User",
			notes = "Create a User",
			response = ResponseMessage.class
			)
	@ApiResponses({
			@ApiResponse (code = 400, 
						  message = "Bad request, if the mandatory fields are missing", 
						  response = ResponseMessage.class),
			@ApiResponse (code = 200, 
						  message = "Successfully Created",
						  response = ResponseMessage.class),
			@ApiResponse (code = 500, 
						  message = "Internal Server Error",
						  response = ResponseMessage.class)
			})
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response signUpUser(User user) {
		Response response;
		try {
			UserTypeDao userTypeDao = ApplicationManager.getApplicationContext().getBean(UserTypeDao.class);
			UserDao userDao = ApplicationManager.getApplicationContext().getBean(UserDao.class);
			user.setUserType(userTypeDao.read("USER"));
			int created = userDao.create(user);
			if (created == 1) {
				response = createResponseMessage(Response.Status.OK, 
						 UserMessage.USER_CREATED_SUCCESSFULLY, 
						 null);
			} else {
				response = createResponseMessage(Response.Status.BAD_REQUEST, 
						 UserMessage.USER_CREATION_FAILED, 
						 null);
			}
		} catch (Exception e) {
			response = createResponseMessage(Response.Status.INTERNAL_SERVER_ERROR, 
					 UserMessage.USER_CREATION_FAILED, 
					 e);
			// Log the error
			logger.error(UserMessage.SERVER_ERROR.toString(), e);
		}
		return response;
	}
	
	@POST
	@Path("/signup/contentprovider")
	@Secured({AccessRole.ADMIN, AccessRole.CONTENT_PROVIDER})
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "Authorization", value = "Authorization token", 
	                      required = true, dataType = "string", paramType = "header") })
	@ApiOperation(
			value = "Create a Content Provider",
			notes = "Create a Content Provider",
			response = ResponseMessage.class
			)
	@ApiResponses({
			@ApiResponse (code = 400, 
						  message = "Bad request, if the mandatory fields are missing", 
						  response = ResponseMessage.class),
			@ApiResponse (code = 200, 
						  message = "Successfully Created",
						  response = ResponseMessage.class),
			@ApiResponse (code = 500, 
						  message = "Internal Server Error",
						  response = ResponseMessage.class)
			})
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)	
	public Response signUpContentProvider(User user) {
		Response response;
		try {
			UserTypeDao userTypeDao = ApplicationManager.getApplicationContext().getBean(UserTypeDao.class);
			UserDao userDao = ApplicationManager.getApplicationContext().getBean(UserDao.class);
			user.setUserType(userTypeDao.read("CONTENT_PROVIDER"));
			int created = userDao.create(user);
			if (created == 1) {
				response = createResponseMessage(Response.Status.OK, 
						 UserMessage.USER_CREATED_SUCCESSFULLY, 
						 null);
			} else {
				response = createResponseMessage(Response.Status.BAD_REQUEST, 
						 UserMessage.USER_CREATION_FAILED, 
						 null);
			}
		} catch (Exception e) {
			response = createResponseMessage(Response.Status.INTERNAL_SERVER_ERROR, 
					 UserMessage.USER_CREATION_FAILED, 
					 e);
			// Log the error
			logger.error(UserMessage.SERVER_ERROR.toString(), e);
		}
		return response;
	}
	
	@GET
	@Path("/email/{email}")
	@Secured({AccessRole.USER, AccessRole.ADMIN, AccessRole.CONTENT_PROVIDER})
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "Authorization", value = "Authorization token", 
	                      required = true, dataType = "string", paramType = "header") })
	@ApiOperation(
			value = "Get the User/Admin/ContentProvider details",
			notes = "Get the User/Admin/ContentProvider details.Only ADMIN can read any user details other can see only their details",
			response = ResponseMessage.class
			)
	@ApiResponses({
			@ApiResponse (code = 400, 
						  message = "Bad request, if the mandatory fields are missing", 
						  response = ResponseMessage.class),
			@ApiResponse (code = 200, 
						  message = "Found the user",
						  response = ResponseMessage.class),
			@ApiResponse (code = 404, 
						  message = "No user found",
						  response = ResponseMessage.class),
			@ApiResponse (code = 500, 
						  message = "Internal Server Error",
						  response = ResponseMessage.class)
			})
	@Produces(MediaType.APPLICATION_JSON)	
	public Response readByEmail(@PathParam("email") String email,
								@Context ContainerRequestContext requestContext) {
		Response response;
		try {
			User currentUser = (User)requestContext.getProperty("user");
			if (currentUser.getUserType().getName().equals("ADMIN") || currentUser.getEmail().equals(email)) {
				UserDao userDao = ApplicationManager.getApplicationContext().getBean(UserDao.class);
				if (email != "") {
					User user = userDao.read(email);
					if (user != null) {
						GenericEntity<User> entity = new GenericEntity<User>(user) {};
						response = Response.ok(entity).build();
					} else {
						response = createResponseMessage(Response.Status.NOT_FOUND, 
								 UserMessage.RES_NOT_FOUND, 
								 null);
						// Log the error
						logger.error(CategoryMessage.RES_NOT_FOUND.toString());
					}
				} else {
					response = createResponseMessage(Response.Status.BAD_REQUEST, 
							 UserMessage.INCORRECT_INPUT, 
							 null);
				}
			} else {
				response = createResponseMessage(Response.Status.FORBIDDEN, UserMessage.INCORRECT_INPUT, null);
			}
		} catch (Exception e) {
			response = createResponseMessage(Response.Status.INTERNAL_SERVER_ERROR, 
					 UserMessage.USER_CREATION_FAILED, 
					 e);
			// Log the error
			logger.error(UserMessage.SERVER_ERROR.toString(), e);
		}
		return response;
		
	}
	
	@GET
	@Path("/id/{id}")
	@Secured({AccessRole.USER, AccessRole.ADMIN, AccessRole.CONTENT_PROVIDER})
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "Authorization", value = "Authorization token", 
	                      required = true, dataType = "string", paramType = "header") })
	@ApiOperation(
			value = "Get the User/Admin/ContentProvider details",
			notes = "Get the User/Admin/ContentProvider details. Only ADMIN can read any user details other can see only their details",
			response = ResponseMessage.class
			)
	@ApiResponses({
			@ApiResponse (code = 400, 
						  message = "Bad request, if the mandatory fields are missing", 
						  response = ResponseMessage.class),
			@ApiResponse (code = 200, 
						  message = "Found the user",
						  response = ResponseMessage.class),
			@ApiResponse (code = 404, 
						  message = "No user found",
						  response = ResponseMessage.class),
			@ApiResponse (code = 500, 
						  message = "Internal Server Error",
						  response = ResponseMessage.class)
			})
	@Produces(MediaType.APPLICATION_JSON)	
	public Response readById(@PathParam("id") int id,
							 @Context ContainerRequestContext requestContext) {
		Response response;
		try {
			User currentUser = (User)requestContext.getProperty("user");
			if (currentUser.getUserType().getName().equals("ADMIN") || currentUser.getId() == id) {
				UserDao userDao = ApplicationManager.getApplicationContext().getBean(UserDao.class);
				if (id != 0) {
					User user = userDao.read(id);
					if (user != null) {
						GenericEntity<User> entity = new GenericEntity<User>(user) {};
						response = Response.ok(entity).build();
					} else {
						response = createResponseMessage(Response.Status.NOT_FOUND, 
								 UserMessage.RES_NOT_FOUND, 
								 null);
						// Log the error
						logger.error(CategoryMessage.RES_NOT_FOUND.toString());
					}
				} else {
					response = createResponseMessage(Response.Status.BAD_REQUEST, 
							 UserMessage.INCORRECT_INPUT, 
							 null);
				}
			} else {
				response = createResponseMessage(Response.Status.FORBIDDEN, UserMessage.INCORRECT_INPUT, null);
			}
		} catch (Exception e) {
			response = createResponseMessage(Response.Status.INTERNAL_SERVER_ERROR, 
					 UserMessage.USER_CREATION_FAILED, 
					 e);
			// Log the error
			logger.error(UserMessage.SERVER_ERROR.toString(), e);
		}
		return response;
		
	}
	
	@PUT
	@Secured({AccessRole.USER, AccessRole.ADMIN, AccessRole.CONTENT_PROVIDER})
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "Authorization", value = "Authorization token", 
	                      required = true, dataType = "string", paramType = "header") })
	@ApiOperation(
			value = "Update User"
					+ "",
			notes = "Update a User. Allowed to all user however an user can edit only his details.",
			response = ResponseMessage.class
			)
	@ApiResponses({
			@ApiResponse (code = 404, 
						  message = "Given user is not present in the DB",
						  response = ResponseMessage.class),
			@ApiResponse (code = 400, 
						  message = "Bad request, if the mandatory fields are missing",
						  response = ResponseMessage.class),
			@ApiResponse (code = 200, 
						  message = "Successfully updated",
						  response = ResponseMessage.class)
			})
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(User user,
						   @Context ContainerRequestContext requestContext) {
		Response response;
		try {
			User currentUser = (User)requestContext.getProperty("user");
			ApplicationContext context = ApplicationManager.getApplicationContext();
			UserDao userDao = context.getBean(UserDao.class);
			if (currentUser.getUserType().getName().equals("ADMIN") || currentUser.getId() == user.getId()) {
				if(user.getId()>-1 && user.getEmail()!=null) {
					int updated = userDao.update(user);	
					if (updated == 1) {					
						response = createResponseMessage(Response.Status.OK, 
														 UserMessage.USER_UPDATED_SUCCESSFULLY, 
														 null);		
					} else {
						response = createResponseMessage(Response.Status.BAD_REQUEST, 
														 UserMessage.USER_UPDATION_FAILED, 
														 null);
						// Log the error
						logger.error(UserMessage.USER_UPDATION_FAILED.toString());
					}
				} else {
					response = createResponseMessage(Response.Status.BAD_REQUEST, 
													 UserMessage.USER_UPDATION_FAILED, 
													 null);
					// Log the error
					logger.error(UserMessage.USER_UPDATION_FAILED.toString());
				}
			} else {
				response = createResponseMessage(Response.Status.FORBIDDEN, UserMessage.INCORRECT_INPUT, null);
			}
		}catch (Exception ex) {			
			response = createResponseMessage(Response.Status.INTERNAL_SERVER_ERROR, 
											 UserMessage.SERVER_ERROR, 
											 ex);
			// Log the error
			logger.error(UserMessage.SERVER_ERROR.toString(), ex);
		}
		return response;
	}
	
	@DELETE
	@Secured(AccessRole.ADMIN)
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "Authorization", value = "Authorization token", 
	                      required = true, dataType = "string", paramType = "header") })
	@Path("/{id}")
	@ApiOperation(
			value = "Delete User",
			notes = "Delete a User by user Id"
			)
	@ApiResponses({
			@ApiResponse (code = 404, 
						  message = "Given user id is not present in the DB",
						  response = ResponseMessage.class),			
			@ApiResponse (code = 200, 
						  message = "Successfully deleted",
						  response = ResponseMessage.class)
			})
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("id") Integer id) {
		Response response;
		try {
			ApplicationContext context = ApplicationManager.getApplicationContext();			
			UserDao userDao = context.getBean(UserDao.class);
			if (id > 0) {
				int deleted = userDao.delete(id);
				if(deleted==1) {				
					response = createResponseMessage(Response.Status.OK, 
							 UserMessage.USER_DELETED_SUCCESSFULLY, 
							 null);	
				} else {
					response = createResponseMessage(Response.Status.BAD_REQUEST, 
							 UserMessage.USER_DELETION_FAILED, 
							 null);
					// Log the error
					logger.error(UserMessage.USER_DELETION_FAILED.toString());
				}		
			} else {
				response = createResponseMessage(Response.Status.BAD_REQUEST, 
						 UserMessage.USER_DELETION_FAILED, 
						 null);
				// Log the error
				logger.error(UserMessage.USER_DELETION_FAILED.toString());
			}
		}catch (Exception ex) {			
			response = createResponseMessage(Response.Status.INTERNAL_SERVER_ERROR, 
											 UserMessage.SERVER_ERROR, 
											 ex);
			// Log the error
			logger.error(UserMessage.SERVER_ERROR.toString(), ex);
		}
		return response;
	}	
	
	private Response createResponseMessage(Response.Status status, UserMessage message, Exception ex) {
		ResponseMessage resObj = new ResponseMessage();
		resObj.setStatus(status.getStatusCode());
		resObj.setCode(message.getCode());
		resObj.setMessage(message.getPhrase());			
		if (ex != null) {
			resObj.setDescription(ex.getMessage());
		}
		GenericEntity<ResponseMessage> entity = new GenericEntity<ResponseMessage>(resObj) {};
		Response response = Response.status(status).entity(entity).build();
		return response;
	}
}
