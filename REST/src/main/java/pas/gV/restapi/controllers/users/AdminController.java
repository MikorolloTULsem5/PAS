package pas.gV.restapi.controllers.users;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pas.gV.model.exceptions.UserException;
import pas.gV.model.exceptions.UserLoginException;
import pas.gV.restapi.data.dto.AdminDTO;
import pas.gV.restapi.data.dto.ClientDTO;
import pas.gV.restapi.data.dto.ResourceAdminDTO;
import pas.gV.restapi.data.dto.UserDTO;
import pas.gV.restapi.data.dto.UserDTO.PasswordValidation;
import pas.gV.restapi.security.dto.ChangePasswordDTORequest;
import pas.gV.restapi.security.services.JwsService;
import pas.gV.restapi.services.userservice.AdminService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admins")
public class AdminController {

    private final AdminService adminService;
    private final JwsService jwsService;

    @Autowired
    public AdminController(AdminService adminService, JwsService jwsService) {
        this.adminService = adminService;
        this.jwsService = jwsService;
    }

    @PostMapping("/addAdmin")
    public ResponseEntity<String> addAdmin(@Validated({UserDTO.BasicUserValidation.class, UserDTO.PasswordValidation.class}) @RequestBody AdminDTO admin,
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
            adminService.registerAdmin(admin.getLogin(), admin.getPassword());
        } catch (UserLoginException ule) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ule.getMessage());
        } catch (UserException ue) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ue.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public List<AdminDTO> getAllAdmins(HttpServletResponse response) {
        List<AdminDTO> resultList = adminService.getAllAdmins();
        if (resultList.isEmpty()) {
            resultList = null;
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return resultList;
    }

    @GetMapping("/{id}")
    public AdminDTO getAdminById(@PathVariable("id") String id, HttpServletResponse response) {
        AdminDTO adminDTO = adminService.getAdminById(UUID.fromString(id));
        if (adminDTO == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return adminDTO;
    }

    @GetMapping("/get")
    public AdminDTO getAdminByLogin(@RequestParam("login") String login, HttpServletResponse response) {
        AdminDTO admin = adminService.getAdminByLogin(login);
        if (admin == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return admin;
    }

    @GetMapping("/match")
    public List<AdminDTO> getAdminByLoginMatching(@RequestParam("login") String login, HttpServletResponse response) {
        List<AdminDTO> resultList = adminService.getAdminByLoginMatching(login);
        if (resultList.isEmpty()) {
            resultList = null;
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return resultList;
    }

    ///TODO modify i podpisy dla adminow
    @PutMapping("/modifyAdmin")
    public ResponseEntity<String> modifyAdmin(@Validated(UserDTO.BasicUserValidation.class) @RequestBody AdminDTO modifiedAdmin,
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
            AdminDTO finalModifyAdmin = new AdminDTO(modifiedAdmin.getId(), modifiedAdmin.getLogin(), null,
                    modifiedAdmin.isArchive());
            adminService.modifyAdmin(finalModifyAdmin);
        } catch (UserLoginException ule) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ule.getMessage());
        } catch (UserException ue) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ue.getMessage());
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/activate/{id}")
    public void activateAdmin(@PathVariable("id") String id, HttpServletResponse response) {
        adminService.activateAdmin(id);
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

    @PostMapping("/deactivate/{id}")
    public void archiveAdmin(@PathVariable("id") String id, HttpServletResponse response) {
        adminService.deactivateAdmin(id);
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

    @PatchMapping("/changePassword/{id}")
    public ResponseEntity<String> changeAdminPassword(@PathVariable("id") String id,
                                                      @Validated(PasswordValidation.class) @RequestBody ChangePasswordDTORequest body,
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
            adminService.changeAdminPassword(id, body);
        } catch (IllegalStateException ise) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ise.getMessage());
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /* me */
    @GetMapping("/get/me")
    public AdminDTO getClientByLogin(HttpServletResponse response) {
        AdminDTO admin = adminService.getAdminByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
        if (admin == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return null;
        }
        String etag = "";
        response.setHeader(HttpHeaders.ETAG, etag);
        return admin;
    }

    @PatchMapping("/changePassword/me")
    public ResponseEntity<String> changeResAdminPassword(@Validated(PasswordValidation.class) @RequestBody ChangePasswordDTORequest body,
                                                         Errors errors) {
        AdminDTO adminDTO = adminService.getAdminByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errors.getAllErrors()
                            .stream().map(ObjectError::getDefaultMessage)
                            .toList()
                            .toString()
                    );
        }

        try {
            adminService.changeAdminPassword(adminDTO.getId(), body);
        } catch (IllegalStateException ise) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ise.getMessage());
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
