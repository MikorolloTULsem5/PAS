package nbd.gV.restapi.services;

import com.mongodb.client.model.Filters;

import jakarta.annotation.PostConstruct;
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
        Court court = new Court(UUID.randomUUID(), area, baseCost, courtNumber);
        try {
            if (!courtRepository.read(Filters.eq("courtnumber", courtNumber)).isEmpty()) {
                throw new CourtNumberException("Nie udalo sie zarejestrowac boiska w bazie! - boisko o tym numerze " +
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

    public void modifyCourt(Court modifiedCourt) {
        var list = courtRepository.read(Filters.and(
                Filters.eq("courtnumber", modifiedCourt.getCourtNumber()),
                Filters.ne("_id", modifiedCourt.getId().toString())));
        if (!list.isEmpty()) {
            throw new CourtNumberException("Nie udalo sie zmodyfikowac podanego boiska - " +
                    "proba zmiany numeru boiska na numer wystepujacy juz u innego boiska");
        }
        if (!courtRepository.updateByReplace(modifiedCourt.getId(), CourtMapper.toMongoCourt(modifiedCourt))) {
            throw new CourtException("Nie udalo się zmodyfikowac podanego boiska");
        }
    }

    public void activateCourt(UUID courtId) {
        courtRepository.update(courtId, "archive", false);
    }

    public void archiveCourt(UUID courtId) {
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

    @PostConstruct
    private void init() {
//        courtRepository.create(CourtMapper.toMongoCourt(new Court(UUID.fromString("634d9130-0015-42bb-a70a-543dee846760"), 100, 100, 1)));
//        courtRepository.create(CourtMapper.toMongoCourt(new Court(UUID.fromString("fe6a35bb-7535-4c23-a259-a14ac0ccedba"),100, 200, 2)));
//        courtRepository.create(CourtMapper.toMongoCourt(new Court(UUID.fromString("30ac2027-dcc8-4af7-920f-831b51023bc9"),300, 200, 3)));
//        courtRepository.create(CourtMapper.toMongoCourt(new Court(UUID.fromString("d820d682-0f5d-46b7-9963-66291e5f64b0"),350, 100, 4)));
//        courtRepository.create(CourtMapper.toMongoCourt(new Court(UUID.fromString("2e9258b2-98dd-4f9a-8f73-6f4f56c2e618"),150, 200, 5)));
    }

}
