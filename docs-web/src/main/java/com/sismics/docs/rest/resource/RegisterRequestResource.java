package com.sismics.docs.rest.resource;

import com.sismics.docs.core.constant.Constants;
import com.sismics.docs.core.constant.RegisterRequestStatusType;
import com.sismics.docs.core.dao.RegisterRequestDao;
import com.sismics.docs.core.dao.UserDao;
import com.sismics.docs.core.dao.dto.RegisterRequestDto;
import com.sismics.docs.core.model.jpa.RegisterRequest;
import com.sismics.docs.core.model.jpa.User;
import com.sismics.docs.rest.constant.BaseFunction;
import com.sismics.rest.exception.ClientException;
import com.sismics.rest.exception.ForbiddenClientException;
import com.sismics.rest.exception.ServerException;
import com.sismics.rest.util.ValidationUtil;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * Register request REST resources
 *
 * @author IceLocke
 */

@Path("/register-request")
public class RegisterRequestResource extends BaseResource {
    /**
     * Send a register request to admin
     *
     * @api {post} /register-request Send a register request to admin.
     * @apiName PostRegisterRequest
     * @apiGroup RegisterRequest
     * @apiParam {String{3..50}} username Username
     * @apiParam {String{8..50}} password Password
     * @apiParam {String{1..100}} email E-mail
     * @apiSuccess status Status OK
     * @apiError (client) ValidationError Validation error
     * @apiError (client) AlreadyExistingUsername Login already used
     * @apiError (client) AlreadyExistingRegisterRequest Already sent a request
     * @apiError (server) UnknownError Unknown server error
     *
     * @param username User's username
     * @param password Password
     * @param email E-mail
     * @return Response
     */
    @POST
    public Response createRegisterRequest(
            @FormParam("username") String username,
            @FormParam("password") String password,
            @FormParam("email") String email
            ) {
        username = ValidationUtil.validateLength(username, "username", 3, 50);
        ValidationUtil.validateUsername(username, "username");
        password = ValidationUtil.validateLength(password, "password", 8, 50);
        email = ValidationUtil.validateLength(email, "email", 1, 100);

        RegisterRequest request = new RegisterRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setEmail(email);
        request.setStorageQuota(10000000000L);

        RegisterRequestDao dao = new RegisterRequestDao();
        try {
            dao.create(request);
        } catch (Exception e) {
            if ("AlreadyExistingUsername".equals(e.getMessage())) {
                throw new ClientException("AlreadyExistingUsername", "Login already used", e);
            } else if ("AlreadyExistingRegisterRequest".equals(e.getMessage())) {
                throw new ClientException("AlreadyExistingRegisterRequest", "Already sent a request", e);
            } else {
                throw new ServerException("UnknownError", "Unknown server error", e);
            }
        }

        JsonObjectBuilder response = Json.createObjectBuilder()
        .add("status", "ok");
        return Response.ok().entity(response.build()).build();
    }

    /**
     * Returns all active register requests
     *
     * @api {get} /register-request/list Get register requests
     * @apiName GetRegisterRequestList
     * @apiGroup RegisterRequest
     * @apiSuccess {Object[]} requests List of requests
     * @apiSuccess requests.id ID
     * @apiSuccess requests.username Username
     * @apiSuccess requests.email E-mail
     * @apiSuccess requests.storage_quota Storage quota
     * @apiSuccess requests.create_timestamp Create timestamp
     * @apiSuccess requests.status request status
     * @apiError (client) ForbiddenError Access denied
     */
    @GET
    @Path("list")
    public Response getActiveRegisterRequests() {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        checkBaseFunction(BaseFunction.ADMIN);

        JsonArrayBuilder requests = Json.createArrayBuilder();
        RegisterRequestDao dao = new RegisterRequestDao();

        List<RegisterRequestDto> list = dao.getActiveRegisterRequests();
        list.sort((a, b) -> Math.toIntExact(a.getCreateTimestamp() - b.getCreateTimestamp()));
        for (RegisterRequestDto dto : list) {
            requests.add(Json.createObjectBuilder()
                    .add("id", dto.getId())
                    .add("username", dto.getUsername())
                    .add("email", dto.getEmail())
                    .add("storage_quota", dto.getStorageQuota())
                    .add("create_timestamp", dto.getCreateTimestamp())
                    .add("status", dto.getStatus().toString()));

        }

        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("requests", requests);
        return Response.ok().entity(response.build()).build();
    }

    /**
     * Accept a register request
     * @api {post} /register-request/accept Accept register request
     * @apiName AcceptRegisterRequest
     * @apiGroup RegisterRequest
     * @apiParam {String} id Request id
     * @apiSuccess status Status OK
     * @apiError (client) NoRequest No such request
     * @param id Register request's id
     * @return Response
     */
    @POST
    @Path("accept")
    public Response acceptRegisterRequest(
            @FormParam("id") String id) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        checkBaseFunction(BaseFunction.ADMIN);

        RegisterRequestDao dao = new RegisterRequestDao();
        RegisterRequest request = dao.getById(id);

        if (request == null) {
            throw new ClientException("NoRequest", "Register request not found");
        }

        // Create the user
        User user = new User();
        user.setRoleId(Constants.DEFAULT_USER_ROLE);
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setStorageQuota(request.getStorageQuota());
        user.setOnboarding(true);

        UserDao userDao = new UserDao();
        try {
            userDao.create(user, principal.getId());
        } catch (Exception e) {
            if ("AlreadyExistingUsername".equals(e.getMessage())) {
                throw new ClientException("AlreadyExistingUsername", "Login already used", e);
            } else {
                throw new ServerException("UnknownError", "Unknown server error", e);
            }
        }

        dao.processRequest(id, RegisterRequestStatusType.ACCEPT);

        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("status", "ok");
        return Response.ok().entity(response.build()).build();
    }

    /**
     * Accept a register request
     * @api {post} /register-request/reject Accept register request
     * @apiName AcceptRegisterRequest
     * @apiGroup RegisterRequest
     * @apiParam {String} id Request id
     * @apiSuccess status Status OK
     * @apiError (client) NoRequest No such request
     * @param id Register request's id
     * @return Response
     */
    @POST
    @Path("reject")
    public Response rejectRegisterRequest(
            @FormParam("id") String id) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        checkBaseFunction(BaseFunction.ADMIN);

        RegisterRequestDao dao = new RegisterRequestDao();
        RegisterRequest request = dao.getById(id);
        if (request == null) {
            throw new ClientException("NoRequest", "Register request not found");
        }
        dao.processRequest(id, RegisterRequestStatusType.REJECT);

        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("status", "ok");
        return Response.ok().entity(response.build()).build();
    }
}
