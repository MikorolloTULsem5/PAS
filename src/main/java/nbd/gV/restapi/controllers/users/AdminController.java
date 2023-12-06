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
import nbd.gV.model.users.Admin;
import nbd.gV.restapi.services.userservice.AdminService;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/admins")
@ApplicationScoped
public class AdminController {
    @Inject
    private AdminService adminService;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/addAdmin")
    public Response addAdmin(Admin admin) {
        Set<ConstraintViolation<Admin>> violations = validator.validate(admin);
        List<String> errors = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
        if (!violations.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
        }

        try {
            adminService.registerAdmin(admin.getLogin());
        } catch (UserLoginException ule) {
            return Response.status(Response.Status.CONFLICT).entity(ule.getMessage()).build();
        } catch (UserException ue) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ue.getMessage()).build();
        }

        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Admin> getAllAdmins() {
        List<Admin> resultList = adminService.getAllAdmins();
        if (resultList.isEmpty()) {
            resultList = null;
        }
        return resultList;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("/{id}")
    public Admin getAdminById(@PathParam("id") String id) {
        return adminService.getAdminById(UUID.fromString(id));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("/get")
    public Admin getAdminByLogin(@QueryParam("login") String login) {
        return adminService.getAdminByLogin(login);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("/match")
    public List<Admin> getAdminByLoginMatching(@QueryParam("login") String login) {
        List<Admin> resultList = adminService.getAdminByLoginMatching(login);
        if (resultList.isEmpty()) {
            resultList = null;
        }
        return resultList;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/modifyAdmin/{id}")
    public Response modifyAdmin(@PathParam("id") String id, Admin modifiedAdmin) {
        Set<ConstraintViolation<Admin>> violations = validator.validate(modifiedAdmin);
        List<String> errors = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
        if (!violations.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
        }

        try {
            Admin finalModifyAdmin = new Admin(UUID.fromString(id), modifiedAdmin.getLogin());
            finalModifyAdmin.setArchive(modifiedAdmin.isArchive());
            adminService.modifyAdmin(finalModifyAdmin);
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
    public void activateAdmin(@PathParam("id") String id) {
        adminService.activateAdmin(UUID.fromString(id));
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/deactivate/{id}")
    public void archiveAdmin(@PathParam("id") String id) {
        adminService.deactivateAdmin(UUID.fromString(id));
    }
}
