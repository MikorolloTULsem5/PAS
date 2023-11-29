package nbd.gV.restapi.services;

import com.mongodb.client.model.Filters;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import nbd.gV.model.courts.Court;
import nbd.gV.exceptions.CourtException;
import nbd.gV.exceptions.MyMongoException;
import nbd.gV.data.datahandling.dto.CourtDTO;
import nbd.gV.data.datahandling.mappers.CourtMapper;
import nbd.gV.data.repositories.CourtMongoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@ApplicationScoped
@NoArgsConstructor
public class CourtService {

    @Inject
    private CourtMongoRepository courtRepository;

    ///TODO kompatybilnosc testow potem wywalic
    public CourtService(CourtMongoRepository courtRepository) {
        this.courtRepository = courtRepository;
    }

    public Court registerCourt(double area, int baseCost, int courtNumber) {
        Court court = new Court(area, baseCost, courtNumber);
        try {
            if (!courtRepository.read(Filters.eq("courtnumber", courtNumber)).isEmpty()) {
                throw new CourtException("Nie udalo sie zarejestrowac boiska w bazie! - boisko o tym numerze " +
                        "znajduje sie juz w bazie");
            }

            if (!courtRepository.create(CourtMapper.toMongoCourt(court))) {
                throw new CourtException("Nie udalo sie zarejestrowac boiska w bazie! - brak odpowiedzi");
            }
        } catch (MyMongoException exception) {
            throw new CourtException("Nie udalo sie dodac boiska.");
        }
        return court;
    }

    public Court getCourtById(UUID courtID) {
        CourtDTO courtMapper = courtRepository.readByUUID(courtID);
        return courtMapper != null ? CourtMapper.fromMongoCourt(courtMapper) : null;
    }

    public List<Court> getAllCourts() {
        List<Court> courtsList = new ArrayList<>();
        for (var el : courtRepository.readAll()) {
            courtsList.add(CourtMapper.fromMongoCourt(el));
        }
        return courtsList;
    }

    public Court getCourtByCourtNumber(int courtNumber) {
        var list = courtRepository.read(Filters.eq("courtnumber", courtNumber));
        return !list.isEmpty() ? CourtMapper.fromMongoCourt(list.get(0)) : null;
    }

    /*-----------------------------------------------------------------------------------------------------*/

    public void modifyCourt(Court modifiedCourt) {
        if (courtRepository.updateByReplace(modifiedCourt.getCourtId(), CourtMapper.toMongoCourt(modifiedCourt))) {
            throw new CourtException("Nie udalo się zmodyfikowac podanego boiska");
        }
    }

    public void activateCourt(UUID courtId) {
        courtRepository.update(courtId, "archive", false);
    }

    public void archiveClient(UUID courtId) {
        courtRepository.update(courtId, "archive", true);
    }

    public void deleteCourt(UUID courtId) {
        try {
            if (!courtRepository.delete(courtId)) {
                throw new CourtException("Nie mozna usunac boiska - istnieja powiazanie z nim rezerwacje");
            }
        } catch (Exception exception) {
            throw new CourtException("Nie udalo sie usunac podanego boiska.");
        }
    }
}
