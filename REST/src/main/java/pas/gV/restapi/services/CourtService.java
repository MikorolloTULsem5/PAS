package pas.gV.restapi.services;

import com.mongodb.client.model.Filters;

import lombok.NoArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pas.gV.restapi.data.mappers.CourtMapper;
import pas.gV.restapi.data.dto.CourtDTO;

import pas.gV.model.exceptions.CourtNumberException;
import pas.gV.model.logic.courts.Court;
import pas.gV.model.exceptions.CourtException;
import pas.gV.model.exceptions.MyMongoException;
import pas.gV.model.data.repositories.CourtMongoRepository;

import java.util.ArrayList;
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

    public CourtDTO registerCourt(double area, int baseCost, int courtNumber) {
        try {
            return CourtMapper.toJsonCourt(courtRepository.create(new Court(null, area, baseCost, courtNumber)));
        } catch (MyMongoException exception) {
            throw new CourtException("Nie udalo sie dodac boiska.");
        }
    }

    public CourtDTO getCourtById(String courtId) {
        Court court = courtRepository.readByUUID(UUID.fromString(courtId));
        return court != null ? CourtMapper.toJsonCourt(court) : null;
    }

    public List<CourtDTO> getAllCourts() {
        List<CourtDTO> list = new ArrayList<>();
        for (var court : courtRepository.readAll()) {
            list.add(CourtMapper.toJsonCourt(court));
        }
        return list;
    }

    public CourtDTO getCourtByCourtNumber(int courtNumber) {
        var list = courtRepository.read(Filters.eq("courtnumber", courtNumber));
        return !list.isEmpty() ? CourtMapper.toJsonCourt(list.get(0)) : null;
    }

    public void modifyCourt(CourtDTO modifiedCourt) {
        var list = courtRepository.read(Filters.and(
                Filters.eq("courtnumber", modifiedCourt.getCourtNumber()),
                Filters.ne("_id", modifiedCourt.getId())));
        if (!list.isEmpty()) {
            throw new CourtNumberException("Nie udalo sie zmodyfikowac podanego boiska - " +
                    "proba zmiany numeru boiska na numer wystepujacy juz u innego boiska");
        }
        if (!courtRepository.updateByReplace(UUID.fromString(modifiedCourt.getId()), CourtMapper.fromJsonCourt(modifiedCourt))) {
            throw new CourtException("Nie udalo się zmodyfikowac podanego boiska");
        }
    }

    public void activateCourt(String courtId) {
        courtRepository.update(UUID.fromString(courtId), "archive", false);
    }

    public void deactivateCourt(String courtId) {
        courtRepository.update(UUID.fromString(courtId), "archive", true);
    }

    public void deleteCourt(String courtId) {
        try {
            courtRepository.delete(UUID.fromString(courtId));
        } catch (IllegalStateException e) {
            throw new CourtException("Nie mozna usunac boiska - istnieja powiazanie z nim rezerwacje");
        } catch (MyMongoException exception) {
            throw new MyMongoException("Nie udalo sie usunac podanego boiska. - " + exception.getMessage());
        }
    }


    /*----------------------------------------------HANDLE UUID----------------------------------------------*/

    public CourtDTO getCourtById(UUID courtId) {
        return getCourtById(courtId.toString());
    }

    public void activateCourt(UUID courtId) {
        activateCourt(courtId.toString());
    }

    public void deactivateCourt(UUID courtId) {
        deactivateCourt(courtId.toString());
    }

    public void deleteCourt(UUID courtId) {
        deleteCourt(courtId.toString());
    }
}
