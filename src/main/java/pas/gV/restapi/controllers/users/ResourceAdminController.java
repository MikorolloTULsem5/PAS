package pas.gV.restapi.controllers.users;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pas.gV.exceptions.UserException;
import pas.gV.exceptions.UserLoginException;
import pas.gV.model.users.ResourceAdmin;
import pas.gV.restapi.services.userservice.ResourceAdminService;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/resAdmins")
public class ResourceAdminController {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final ResourceAdminService resourceAdminService;

    @Autowired
    public ResourceAdminController(ResourceAdminService resourceAdminService) {
        this.resourceAdminService = resourceAdminService;
    }

    @PostMapping("/addResAdmin")
    public ResponseEntity<String> addResAdmin(@RequestBody ResourceAdmin resourceAdmin) {
        Set<ConstraintViolation<ResourceAdmin>> violations = validator.validate(resourceAdmin);
        List<String> errors = violations.stream().map(ConstraintViolation::getMessage).toList();
        if (!violations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.toString());
        }

        try {
            resourceAdminService.registerResourceAdmin(resourceAdmin.getLogin());
        } catch (UserLoginException ule) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ule.getMessage());
        } catch (UserException ue) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ue.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public List<ResourceAdmin> getAllResAdmins(HttpServletResponse response) {
        List<ResourceAdmin> resultList = resourceAdminService.getAllResourceAdmins();
        if (resultList.isEmpty()) {
            resultList = null;
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return resultList;
    }

    @GetMapping("/{id}")
    public ResourceAdmin getResAdminById(@PathVariable("id") String id, HttpServletResponse response) {
        ResourceAdmin resourceAdmin = resourceAdminService.getResourceAdminById(UUID.fromString(id));
        if (resourceAdmin == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return resourceAdmin;
    }

    @GetMapping("/get")
    public ResourceAdmin getResAdminByLogin(@RequestParam("login") String login, HttpServletResponse response) {
        ResourceAdmin resourceAdmin = resourceAdminService.getResourceAdminByLogin(login);
        if (resourceAdmin == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return resourceAdmin;
    }

    @GetMapping("/match")
    public List<ResourceAdmin> getResAdminByLoginMatching(@RequestParam("login") String login, HttpServletResponse response) {
        List<ResourceAdmin> resultList = resourceAdminService.getResourceAdminByLoginMatching(login);
        if (resultList.isEmpty()) {
            resultList = null;
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return resultList;
    }

    @PutMapping("/modifyResAdmin/{id}")
    public ResponseEntity<String> modifyResAdmin(@PathVariable("id") String id, @RequestBody ResourceAdmin modifyResourceAdmin) {
        Set<ConstraintViolation<ResourceAdmin>> violations = validator.validate(modifyResourceAdmin);
        List<String> errors = violations.stream().map(ConstraintViolation::getMessage).toList();
        if (!violations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.toString());
        }

        try {
            ResourceAdmin finalModifyResourceAdmin = new ResourceAdmin(UUID.fromString(id), modifyResourceAdmin.getLogin());
            finalModifyResourceAdmin.setArchive(modifyResourceAdmin.isArchive());
            resourceAdminService.modifyResourceAdmin(finalModifyResourceAdmin);
        } catch (UserLoginException ule) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ule.getMessage());
        } catch (UserException ue) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ue.getMessage());
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/activate/{id}")
    public void activateResAdmin(@PathVariable("id") String id, HttpServletResponse response) {
        resourceAdminService.activateResourceAdmin(UUID.fromString(id));
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

    @PostMapping("/deactivate/{id}")
    public void archiveResAdmin(@PathVariable("id") String id, HttpServletResponse response) {
        resourceAdminService.deactivateResourceAdmin(UUID.fromString(id));
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }
}
