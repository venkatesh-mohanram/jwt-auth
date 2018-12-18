package com.vasi.learning.api.resources.v1;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.vasi.learning.ApplicationManager;
import com.vasi.learning.api.resources.logmessages.CategoryMessage;
import com.vasi.learning.model.v1.Category;
import com.vasi.learning.model.v1.ResponseMessage;
import com.vasi.learning.persistence.dao.impl.CategoryDao;
import com.vasi.learning.resources.filters.v1.AccessRole;
import com.vasi.learning.resources.filters.v1.Secured;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api (value = "Categories Resource")
@Secured({AccessRole.USER, AccessRole.ADMIN, AccessRole.CONTENT_PROVIDER})
@Path("v1/categories")
public class CategoriesResource {
	private Logger logger = LoggerFactory.getLogger(CategoriesResource.class);
	
	
	@GET
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "Authorization", value = "Authorization token", 
	                      required = true, dataType = "string", paramType = "header") })
	@ApiOperation(
			value = "Read Category(ies)",
			notes = "List all Category or List based on conditions like id, name",
			response = Category.class,
			responseContainer = "List"
			)
	@ApiResponses(
			@ApiResponse (code = 404, 
						  message = "Category with given id is not found",
						  response = ResponseMessage.class)
			)
	@Produces(MediaType.APPLICATION_JSON)
	public Response read(			
			@QueryParam("id") String id,
			@QueryParam("name") String name
			) {
		Response response = null;
		try {
			System.out.println("Entering read");
			ApplicationContext context = ApplicationManager.getApplicationContext();
			CategoryDao categoryDao = context.getBean(CategoryDao.class);
			List<Category> categoryList = new ArrayList<>();	
			Category category=null;
			if (id != null && !id.equalsIgnoreCase("")) {
				//logger.debug(CourseCategoryMessage.GET_BY_COURSE_ID.toString());
				category = categoryDao.read(Integer.valueOf(id));
				categoryList.add(category);
			} else {			
				categoryList = categoryDao.list();
			}			
			// Check whether we have result for the query
			if (categoryList.size() == 0) {				
				response = createResponseMessage(Response.Status.NOT_FOUND, 
												 CategoryMessage.RES_NOT_FOUND, 
												 null);
				// Log the error
				logger.error(CategoryMessage.RES_NOT_FOUND.toString());
			} else {
				GenericEntity<List<Category>> entity = new GenericEntity<List<Category>>(categoryList) {};
				response = Response.ok(entity).build();
			}
		} catch (Exception ex) {
			response = createResponseMessage(Response.Status.INTERNAL_SERVER_ERROR, 
											 CategoryMessage.SERVER_ERROR, 
											 ex);
			// Log the error
			logger.error(CategoryMessage.SERVER_ERROR.toString(), ex);			
		}
		return response;
	}	
	
	@GET
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "Authorization", value = "Authorization token", 
	                      required = true, dataType = "string", paramType = "header") })
	@ApiOperation(
			value = "Read root Category(ies)",
			notes = "List all root Categories",
			response = Category.class,
			responseContainer = "List"
			)
	@ApiResponses(
			@ApiResponse (code = 404, 
						  message = "No categories found",
						  response = ResponseMessage.class)
			)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/root")
	public Response readRootCategories() {
		Response response = null;
		try {			
			ApplicationContext context = ApplicationManager.getApplicationContext();
			CategoryDao categoryDao = context.getBean(CategoryDao.class);
			List<Category> categoryList = categoryDao.listRootCategories();
						
			// Check whether we have result for the query
			if (categoryList.size() == 0) {				
				response = createResponseMessage(Response.Status.NOT_FOUND, 
												 CategoryMessage.RES_NOT_FOUND, 
												 null);
				// Log the error
				logger.error(CategoryMessage.RES_NOT_FOUND.toString());
			} else {
				GenericEntity<List<Category>> entity = new GenericEntity<List<Category>>(categoryList) {};
				response = Response.ok(entity).build();
			}
		} catch (Exception ex) {
			response = createResponseMessage(Response.Status.INTERNAL_SERVER_ERROR, 
											 CategoryMessage.SERVER_ERROR, 
											 ex);
			// Log the error
			logger.error(CategoryMessage.SERVER_ERROR.toString(), ex);			
		}
		return response;
	}
	
	@GET
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "Authorization", value = "Authorization token", 
	                      required = true, dataType = "string", paramType = "header") })
	@ApiOperation(
			value = "Read root Category(ies)",
			notes = "List all root Categories",
			response = Category.class,
			responseContainer = "List"
			)
	@ApiResponses(
			@ApiResponse (code = 404, 
						  message = "No categories found",
						  response = ResponseMessage.class)
			)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}/children")
	public Response readChildrenCategories(@PathParam("id")int id) {
		Response response = null;
		try {			
			ApplicationContext context = ApplicationManager.getApplicationContext();
			CategoryDao categoryDao = context.getBean(CategoryDao.class);
			List<Category> categoryList = categoryDao.listChildren(id);
						
			// Check whether we have result for the query
			if (categoryList!= null && categoryList.size() == 0) {				
				response = createResponseMessage(Response.Status.NOT_FOUND, 
												 CategoryMessage.RES_NOT_FOUND, 
												 null);
				// Log the error
				logger.error(CategoryMessage.RES_NOT_FOUND.toString());
			} else {
				GenericEntity<List<Category>> entity = new GenericEntity<List<Category>>(categoryList) {};
				response = Response.ok(entity).build();
			}
		} catch (Exception ex) {
			response = createResponseMessage(Response.Status.INTERNAL_SERVER_ERROR, 
											 CategoryMessage.SERVER_ERROR, 
											 ex);
			// Log the error
			logger.error(CategoryMessage.SERVER_ERROR.toString(), ex);			
		}
		return response;
	}
	
	@POST
	@Secured({AccessRole.ADMIN, AccessRole.CONTENT_PROVIDER})
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "Authorization", value = "Authorization token", 
	                      required = true, dataType = "string", paramType = "header") })
	@ApiOperation(
			value = "Create Category",
			notes = "Create a Course",
			response = ResponseMessage.class
			)
	@ApiResponses({
			@ApiResponse (code = 400, 
						  message = "Bad request, if the mandatory fields are missing", 
						  response = ResponseMessage.class),
			@ApiResponse (code = 200, 
						  message = "Successfully Created",
						  response = ResponseMessage.class)
			})
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(Category category) {
		Response response;
		try {
			ApplicationContext context = ApplicationManager.getApplicationContext();
			CategoryDao categoryDao = context.getBean(CategoryDao.class);			
			int create  = categoryDao.create(category);
			if (create == 1) {		
				response = createResponseMessage(Response.Status.OK, 
												 CategoryMessage.CATEGORY_CREATED_SUCCESSFULLY, 
												 null);
			} else {
				response = createResponseMessage(Response.Status.BAD_REQUEST, 
												 CategoryMessage.CATEGORY_CREATION_FAILED, 
												 null);
				// Log the error
				logger.error(CategoryMessage.CATEGORY_CREATION_FAILED.toString());
			}
		} catch (Exception ex) {
			response = createResponseMessage(Response.Status.INTERNAL_SERVER_ERROR, 
											 CategoryMessage.SERVER_ERROR, 
											 ex);
			// Log the error
			logger.error(CategoryMessage.SERVER_ERROR.toString(), ex);
		}
		return response;
	}
	
	@PUT
	@Secured({AccessRole.ADMIN, AccessRole.CONTENT_PROVIDER})
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "Authorization", value = "Authorization token", 
	                      required = true, dataType = "string", paramType = "header") })
	@ApiOperation(
			value = "Update Category",
			notes = "Update a Category. Allowed only to ADMIN and CONTENT_PROVIDER roles",
			response = ResponseMessage.class
			)
	@ApiResponses({
			@ApiResponse (code = 404, 
						  message = "Given Category is not present in the DB",
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
	public Response update(Category category) {
		Response response;
		try {
			ApplicationContext context = ApplicationManager.getApplicationContext();
			CategoryDao categoryDao = context.getBean(CategoryDao.class);			
			if(category.getId()>-1 && category.getName()!=null) {
				int updated = categoryDao.update(category);	
				if (updated == 1) {					
					response = createResponseMessage(Response.Status.OK, 
													 CategoryMessage.CATEGORY_UPDATED_SUCCESSFULLY, 
													 null);		
				} else {
					response = createResponseMessage(Response.Status.BAD_REQUEST, 
													 CategoryMessage.CATEGORY_UPDATION_FAILED, 
													 null);
					// Log the error
					logger.error(CategoryMessage.CATEGORY_UPDATION_FAILED.toString());
				}
			} else {
				response = createResponseMessage(Response.Status.BAD_REQUEST, 
												 CategoryMessage.CATEGORY_UPDATION_FAILED, 
												 null);
				// Log the error
				logger.error(CategoryMessage.CATEGORY_UPDATION_FAILED.toString());
			}
		}catch (Exception ex) {			
			response = createResponseMessage(Response.Status.INTERNAL_SERVER_ERROR, 
											 CategoryMessage.SERVER_ERROR, 
											 ex);
			// Log the error
			logger.error(CategoryMessage.SERVER_ERROR.toString(), ex);
		}
		return response;
	}
	
	@DELETE
	@Secured({AccessRole.ADMIN, AccessRole.CONTENT_PROVIDER})
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "Authorization", value = "Authorization token", 
	                      required = true, dataType = "string", paramType = "header") })
	@Path("/{id}")
	@ApiOperation(
			value = "Delete Category",
			notes = "Delete a Category by Category Id"
			)
	@ApiResponses({
			@ApiResponse (code = 404, 
						  message = "Given category is not present in the DB",
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
			System.out.println(id);
			CategoryDao categoryDao = context.getBean(CategoryDao.class);
			if (id > 0) {
				int deleted = categoryDao.delete(id);
				if(deleted==1) {				
					response = createResponseMessage(Response.Status.OK, 
							 CategoryMessage.CATEGORY_DELETED_SUCCESSFULLY, 
							 null);	
				} else {
					response = createResponseMessage(Response.Status.BAD_REQUEST, 
							 CategoryMessage.CATEGORY_DELETION_FAILED, 
							 null);
					// Log the error
					logger.error(CategoryMessage.CATEGORY_DELETION_FAILED.toString());
				}		
			} else {
				response = createResponseMessage(Response.Status.BAD_REQUEST, 
						 CategoryMessage.CATEGORY_DELETION_FAILED, 
						 null);
				// Log the error
				logger.error(CategoryMessage.CATEGORY_DELETION_FAILED.toString());
			}
		}catch (Exception ex) {			
			response = createResponseMessage(Response.Status.INTERNAL_SERVER_ERROR, 
											 CategoryMessage.SERVER_ERROR, 
											 ex);
			// Log the error
			logger.error(CategoryMessage.SERVER_ERROR.toString(), ex);
		}
		return response;
	}	
	
	private Response createResponseMessage(Response.Status status, CategoryMessage message, Exception ex) {
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
