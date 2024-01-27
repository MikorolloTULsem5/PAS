package pas.gV.restapi.controllers.users;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pas.gV.model.exceptions.UserException;
import pas.gV.model.exceptions.UserLoginException;

import pas.gV.restapi.data.dto.ResourceAdminDTO;
import pas.gV.restapi.data.dto.UserDTO;
import pas.gV.restapi.services.userservice.ResourceAdminService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/resAdmins")
public class ResourceAdminController {
    private final ResourceAdminService resourceAdminService;

    @Autowired
    public ResourceAdminController(ResourceAdminService resourceAdminService) {
        this.resourceAdminService = resourceAdminService;
    }

    @PostMapping("/addResAdmin")
    public ResponseEntity<String> addResAdmin(@Validated({UserDTO.BasicValidation.class, UserDTO.PasswordValidation.class}) @RequestBody ResourceAdminDTO resourceAdmin,
                                              Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errors.getAllErrors()
                            .stream().map(ObjectError::getDefaultMessage)
                            .toList()
                            .toString()
                    );
        }

        try {
            resourceAdminService.registerResourceAdmin(resourceAdmin.getLogin(), resourceAdmin.getPassword());
        } catch (UserLoginException ule) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ule.getMessage());
        } catch (UserException ue) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ue.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public List<ResourceAdminDTO> getAllResAdmins(HttpServletResponse response) {
        List<ResourceAdminDTO> resultList = resourceAdminService.getAllResourceAdmins();
        if (resultList.isEmpty()) {
            resultList = null;
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return resultList;
    }

    @GetMapping("/{id}")
    public ResourceAdminDTO getResAdminById(@PathVariable("id") String id, HttpServletResponse response) {
        ResourceAdminDTO resourceAdminDTO = resourceAdminService.getResourceAdminById(UUID.fromString(id));
        if (resourceAdminDTO == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return resourceAdminDTO;
    }

    @GetMapping("/get")
    public ResourceAdminDTO getResAdminByLogin(@RequestParam("login") String login, HttpServletResponse response) {
        ResourceAdminDTO resourceAdminDTO = resourceAdminService.getResourceAdminByLogin(login);
        if (resourceAdminDTO == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return resourceAdminDTO;
    }

    @GetMapping("/match")
    public List<ResourceAdminDTO> getResAdminByLoginMatching(@RequestParam("login") String login, HttpServletResponse response) {
        List<ResourceAdminDTO> resultList = resourceAdminService.getResourceAdminByLoginMatching(login);
        if (resultList.isEmpty()) {
            resultList = null;
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return resultList;
    }

    @PutMapping("/modifyResAdmin/{id}")
    public ResponseEntity<String> modifyResAdmin(@PathVariable("id") String id,
                                                 @Validated(UserDTO.BasicValidation.class) @RequestBody ResourceAdminDTO modifyResourceAdmin,
                                                 Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errors.getAllErrors()
                            .stream().map(ObjectError::getDefaultMessage)
                            .toList()
                            .toString()
                    );
        }

        try {
            ResourceAdminDTO finalModifyResourceAdmin = new ResourceAdminDTO(id, modifyResourceAdmin.getLogin(),
                    "", modifyResourceAdmin.isArchive());
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
        resourceAdminService.activateResourceAdmin(id);
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

    @PostMapping("/deactivate/{id}")
    public void archiveResAdmin(@PathVariable("id") String id, HttpServletResponse response) {
        resourceAdminService.deactivateResourceAdmin(id);
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }
}
