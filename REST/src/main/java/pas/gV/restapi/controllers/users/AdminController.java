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

import pas.gV.model.exceptions.UserException;
import pas.gV.model.exceptions.UserLoginException;
import pas.gV.model.logic.users.Admin;
import pas.gV.restapi.services.userservice.AdminService;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/admins")
public class AdminController {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/addAdmin")
    public ResponseEntity<String> addAdmin(@RequestBody Admin admin) {
        Set<ConstraintViolation<Admin>> violations = validator.validate(admin);
        List<String> errors = violations.stream().map(ConstraintViolation::getMessage).toList();
        if (!violations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.toString());
        }

        try {
            adminService.registerAdmin(admin.getLogin(), "password");
        } catch (UserLoginException ule) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ule.getMessage());
        } catch (UserException ue) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ue.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public List<Admin> getAllAdmins(HttpServletResponse response) {
        List<Admin> resultList = adminService.getAllAdmins();
        if (resultList.isEmpty()) {
            resultList = null;
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return resultList;
    }

    @GetMapping("/{id}")
    public Admin getAdminById(@PathVariable("id") String id, HttpServletResponse response) {
        Admin admin = adminService.getAdminById(UUID.fromString(id));
        if (admin == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return admin;
    }

    @GetMapping("/get")
    public Admin getAdminByLogin(@RequestParam("login") String login, HttpServletResponse response) {
        Admin admin = adminService.getAdminByLogin(login);
        if (admin == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return admin;
    }

    @GetMapping("/match")
    public List<Admin> getAdminByLoginMatching(@RequestParam("login") String login, HttpServletResponse response) {
        List<Admin> resultList = adminService.getAdminByLoginMatching(login);
        if (resultList.isEmpty()) {
            resultList = null;
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return resultList;
    }

    @PutMapping("/modifyAdmin/{id}")
    public ResponseEntity<String> modifyAdmin(@PathVariable("id") String id, @RequestBody Admin modifiedAdmin) {
        Set<ConstraintViolation<Admin>> violations = validator.validate(modifiedAdmin);
        List<String> errors = violations.stream().map(ConstraintViolation::getMessage).toList();
        if (!violations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.toString());
        }

        try {
            Admin finalModifyAdmin = new Admin(UUID.fromString(id), modifiedAdmin.getLogin(), "password");
            finalModifyAdmin.setArchive(modifiedAdmin.isArchive());
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
        adminService.activateAdmin(UUID.fromString(id));
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

    @PostMapping("/deactivate/{id}")
    public void archiveAdmin(@PathVariable("id") String id, HttpServletResponse response) {
        adminService.deactivateAdmin(UUID.fromString(id));
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }
}