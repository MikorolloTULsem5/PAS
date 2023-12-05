package nbd.gV.restapi.services;

import com.mongodb.client.model.Filters;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import nbd.gV.exceptions.CourtNumberException;
import nbd.gV.model.courts.Court;
import nbd.gV.exceptions.CourtException;
import nbd.gV.exceptions.MyMongoException;
import nbd.gV.data.datahandling.dto.CourtDTO;
import nbd.gV.data.datahandling.mappers.CourtMapper;
import nbd.gV.data.repositories.CourtMongoRepository;

import java.util.List;
import java.util.UUID;


@ApplicationScoped
@NoArgsConstructor
public class CourtService {

    @Inject
    private CourtMongoRepository courtRepository;

    public CourtService(CourtMongoRepository courtRepository) {
        this.courtRepository = courtRepository;
    }

    public Court registerCourt(double area, int baseCost, int courtNumber) {
//        Court court = new Court(UUID.randomUUID(), area, baseCost, courtNumber);
//        try {
//            if (!courtRepository.read(Filters.eq("courtnumber", courtNumber)).isEmpty()) {
//                throw new CourtNumberException("Nie udalo sie zarejestrowac boiska w bazie! - boisko o tym numerze " +
//                        "znajduje sie juz w bazie");
//            }
//
//            if (!courtRepository.create(CourtMapper.toMongoCourt(court))) {
//                throw new CourtException("Nie udalo sie zarejestrowac boiska w bazie! - brak odpowiedzi");
//            }
//        } catch (MyMongoException exception) {
//            throw new CourtException("Nie udalo sie dodac boiska.");
//        }
//        return court;

        try {
            return courtRepository.create(new Court(area, baseCost, courtNumber));
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
