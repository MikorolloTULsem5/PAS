package nbd.gV.restapi.services;

import com.mongodb.client.model.Filters;

import nbd.gV.model.courts.Court;
import nbd.gV.exceptions.CourtException;
import nbd.gV.exceptions.MainException;
import nbd.gV.exceptions.MyMongoException;
import nbd.gV.data.datahandling.dto.CourtDTO;
import nbd.gV.data.datahandling.mappers.CourtMapper;
import nbd.gV.data.repositories.CourtMongoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CourtService {

    private final CourtMongoRepository courtRepository;

    public CourtService() {
        courtRepository = new CourtMongoRepository();
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

    public void unregisterCourt(Court court) {
        if (court == null) {
            throw new MainException("Nie mozna wyrejestrowac nieistniejacego boiska!");
        }
        try {
            court.setArchive(true);
            if (!courtRepository.update(court.getCourtId(), "archive", true)) {
                court.setArchive(false);
                throw new CourtException("Nie udalo sie wyrejestrowac podanego boiska.");
            }
        } catch (Exception exception) {
            court.setArchive(false);
            throw new CourtException("Nie udalo sie wyrejestrowac podanego boiska.");
        }
    }

    public void deleteCourt(Court court) {
        if (court == null) {
            throw new MainException("Nie mozna usunac nieistniejacego boiska!");
        }
        try {
            if (!courtRepository.delete(court.getCourtId())) {
                throw new CourtException("Nie mozna usunac boiska - istnieja powiazanie z nim rezerwacje");
            }
        } catch (Exception exception) {
            court.setArchive(false);
            throw new CourtException("Nie udalo sie wyrejestrowac podanego boiska.");
        }
    }

    public Court getCourt(UUID courtID) {
        try {
            CourtDTO courtMapper = courtRepository.readByUUID(courtID);
            return courtMapper != null ? CourtMapper.fromMongoCourt(courtMapper) : null;
        } catch (Exception exception) {
            throw new CourtException("Blad transakcji.");
        }
    }

    public List<Court> getAllCourts() {
        try {
            List<Court> courtsList = new ArrayList<>();
            for (var el : courtRepository.readAll()) {
                courtsList.add(CourtMapper.fromMongoCourt(el));
            }
            return courtsList;
        } catch (Exception exception) {
            throw new CourtException("Nie udalo sie uzyskac boisk.");
        }
    }

    public Court findCourtByCourtNumber(int courtNumber) {
        var list = courtRepository.read(Filters.eq("courtnumber", courtNumber));
        return !list.isEmpty() ? CourtMapper.fromMongoCourt(list.get(0)) : null;
    }
}
