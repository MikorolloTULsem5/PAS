package pas.gV.restapi.controllers;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pas.gV.exceptions.CourtException;
import pas.gV.exceptions.CourtNumberException;
import pas.gV.exceptions.MyMongoException;
import pas.gV.model.courts.Court;
import pas.gV.restapi.services.CourtService;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/courts")
public class CourtController {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final CourtService courtService;

    @Autowired
    public CourtController(CourtService courtService) {
        this.courtService = courtService;
    }

    @PostMapping(path = "/addCourt")
    public ResponseEntity<String> addCourt(@RequestBody Court court) {
        Set<ConstraintViolation<Court>> violations = validator.validate(court);
        List<String> errors = violations.stream().map(ConstraintViolation::getMessage).toList();
        if (!violations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.toString());
        }

        try {
            courtService.registerCourt(court.getArea(), court.getBaseCost(), court.getCourtNumber());
        } catch (CourtNumberException cne) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(cne.getMessage());
        } catch (CourtException ce) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ce.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public List<Court> getAllCourts(HttpServletResponse response) {
        List<Court> resultList = courtService.getAllCourts();
        if (resultList.isEmpty()) {
            resultList = null;
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return resultList;
    }

    @GetMapping(path = "/{id}")
    public Court getCourtById(@PathVariable("id") String id, HttpServletResponse response) {
        Court court = courtService.getCourtById(UUID.fromString(id));
        if (court == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return court;
    }

    @GetMapping(path = "/get")
    public Court getCourtByCourtNumber(@RequestParam("number") String number, HttpServletResponse response) {
        Court court = courtService.getCourtByCourtNumber(Integer.parseInt(number));
        if (court == null) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return court;
    }

    @PutMapping(path = "/modifyCourt/{id}")
    public ResponseEntity<String> modifyCourt(@PathVariable("id") String id, @RequestBody Court modifiedCourt) {
        Set<ConstraintViolation<Court>> violations = validator.validate(modifiedCourt);
        List<String> errors = violations.stream().map(ConstraintViolation::getMessage).toList();
        if (!violations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.toString());
        }

        try {
            Court finalModifyCourt = new Court(UUID.fromString(id), modifiedCourt.getArea(), modifiedCourt.getBaseCost(),
                    modifiedCourt.getCourtNumber());
            finalModifyCourt.setArchive(modifiedCourt.isArchive());
            finalModifyCourt.setRented(modifiedCourt.isRented());
            courtService.modifyCourt(finalModifyCourt);
        } catch (CourtNumberException cne) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(cne.getMessage());
        } catch (CourtException ce) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ce.getMessage());
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping(path = "/activate/{id}")
    public void activateCourt(@PathVariable("id") String id, HttpServletResponse response) {
        courtService.activateCourt(UUID.fromString(id));
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

    @PostMapping(path = "/deactivate/{id}")
    public void archiveCourt(@PathVariable("id") String id, HttpServletResponse response) {
        courtService.deactivateCourt(UUID.fromString(id));
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<String> deleteCourt(@PathVariable("id") String id) {
        try {
            courtService.deleteCourt(UUID.fromString(id));
        } catch (CourtException ce) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ce.getMessage());
        } catch (MyMongoException mme) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mme.getMessage());
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
