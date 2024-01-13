package pas.gV.restapi.services;

import com.mongodb.client.model.Filters;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pas.gV.model.exceptions.CourtNumberException;
import pas.gV.model.logic.courts.Court;
import pas.gV.model.exceptions.CourtException;
import pas.gV.model.exceptions.MyMongoException;
import pas.gV.model.data.repositories.CourtMongoRepository;

import java.util.List;
import java.util.UUID;


@Service
@NoArgsConstructor
public class CourtService {

    private CourtMongoRepository courtRepository;

    @Autowired
    public CourtService(CourtMongoRepository courtRepository) {
        this.courtRepository = courtRepository;
    }

    public Court registerCourt(double area, int baseCost, int courtNumber) {
        try {
            return courtRepository.create(new Court(null, area, baseCost, courtNumber));
        } catch (MyMongoException exception) {
            throw new CourtException("Nie udalo sie dodac boiska.");
        }
    }

    public Court getCourtById(UUID courtID) {
        return courtRepository.readByUUID(courtID);
    }

    public List<Court> getAllCourts() {
        return courtRepository.readAll();
    }

    public Court getCourtByCourtNumber(int courtNumber) {
        var list = courtRepository.read(Filters.eq("courtnumber", courtNumber));
        return !list.isEmpty() ? list.get(0) : null;
    }

    public void modifyCourt(Court modifiedCourt) {
        var list = courtRepository.read(Filters.and(
                Filters.eq("courtnumber", modifiedCourt.getCourtNumber()),
                Filters.ne("_id", modifiedCourt.getId().toString())));
        if (!list.isEmpty()) {
            throw new CourtNumberException("Nie udalo sie zmodyfikowac podanego boiska - " +
                    "proba zmiany numeru boiska na numer wystepujacy juz u innego boiska");
        }
        if (!courtRepository.updateByReplace(modifiedCourt.getId(), modifiedCourt)) {
            throw new CourtException("Nie udalo się zmodyfikowac podanego boiska");
        }
    }

    public void activateCourt(UUID courtId) {
        courtRepository.update(courtId, "archive", false);
    }

    public void deactivateCourt(UUID courtId) {
        courtRepository.update(courtId, "archive", true);
    }

    public void deleteCourt(UUID courtId) {
        try {
            courtRepository.delete(courtId);
        } catch (IllegalStateException e) {
            throw new CourtException("Nie mozna usunac boiska - istnieja powiazanie z nim rezerwacje");
        } catch (MyMongoException exception) {
            throw new MyMongoException("Nie udalo sie usunac podanego boiska. - " + exception.getMessage());
        }
    }

}