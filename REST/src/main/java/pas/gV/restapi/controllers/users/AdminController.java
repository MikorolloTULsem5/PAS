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
import pas.gV.restapi.data.dto.AdminDTO;
import pas.gV.restapi.data.dto.UserDTO;
import pas.gV.restapi.services.userservice.AdminService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admins")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
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

    @PutMapping("/modifyAdmin/{id}")
    public ResponseEntity<String> modifyAdmin(@PathVariable("id") String id,
                                              @Validated(UserDTO.BasicUserValidation.class) @RequestBody AdminDTO modifiedAdmin,
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
            AdminDTO finalModifyAdmin = new AdminDTO(id, modifiedAdmin.getLogin(), null,
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
}
