package nbd.gV.restapi.controllers.users;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import nbd.gV.exceptions.UserException;
import nbd.gV.exceptions.UserLoginException;
import nbd.gV.model.users.ResourceAdmin;
import nbd.gV.restapi.services.userservice.ResourceAdminService;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/resAdmins")
@ApplicationScoped
public class ResourceAdminController {
    @Inject
    private ResourceAdminService resourceAdminService;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/addResAdmin")
    public Response addResAdmin(ResourceAdmin resourceAdmin) {
        Set<ConstraintViolation<ResourceAdmin>> violations = validator.validate(resourceAdmin);
        List<String> errors = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
        if (!violations.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
        }

        try {
            resourceAdminService.registerResourceAdmin(resourceAdmin.getLogin());
        } catch (UserLoginException ule) {
            return Response.status(Response.Status.CONFLICT).entity(ule.getMessage()).build();
        } catch (UserException ue) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ue.getMessage()).build();
        }

        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ResourceAdmin> getAllResAdmins() {
        List<ResourceAdmin> resultList = resourceAdminService.getAllResourceAdmins();
        if (resultList.isEmpty()) {
            resultList = null;
        }
        return resultList;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("/{id}")
    public ResourceAdmin getResAdminById(@PathParam("id") String id) {
        return resourceAdminService.getResourceAdminById(UUID.fromString(id));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("/get")
    public ResourceAdmin getResAdminByLogin(@QueryParam("login") String login) {
        return resourceAdminService.getResourceAdminByLogin(login);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("/match")
    public List<ResourceAdmin> getResAdminByLoginMatching(@QueryParam("login") String login) {
        List<ResourceAdmin> resultList = resourceAdminService.getResourceAdminByLoginMatching(login);
        if (resultList.isEmpty()) {
            resultList = null;
        }
        return resultList;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/modifyAdmin/{id}")
    public Response modifyResAdmin(@PathParam("id") String id, ResourceAdmin modifyResourceAdmin) {
        Set<ConstraintViolation<ResourceAdmin>> violations = validator.validate(modifyResourceAdmin);
        List<String> errors = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
        if (!violations.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
        }

        try {
            ResourceAdmin finalModifyResourceAdmin = new ResourceAdmin(UUID.fromString(id), modifyResourceAdmin.getLogin());
            finalModifyResourceAdmin.setArchive(modifyResourceAdmin.isArchive());
            resourceAdminService.modifyResourceAdmin(finalModifyResourceAdmin);
        } catch (UserLoginException ule) {
            return Response.status(Response.Status.CONFLICT).entity(ule.getMessage()).build();
        } catch (UserException ue) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ue.getMessage()).build();
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/activate/{id}")
    public void activateResAdmin(@PathParam("id") String id) {
        resourceAdminService.activateResourceAdmin(UUID.fromString(id));
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/deactivate/{id}")
    public void archiveResAdmin(@PathParam("id") String id) {
        resourceAdminService.deactivateResourceAdmin(UUID.fromString(id));
    }
}
