package nbd.gV.courts;

import com.mongodb.client.model.Filters;

import nbd.gV.exceptions.CourtException;
import nbd.gV.exceptions.MainException;
import nbd.gV.exceptions.MyMongoException;
import nbd.gV.mappers.CourtMapper;
import nbd.gV.repositories.CourtMongoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CourtManager {

    private final CourtMongoRepository courtRepository;

    public CourtManager() {
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
                throw new CourtException("Nie udalo sie wyrejestrowac podanego klienta.");
            }
        } catch (Exception exception) {
            court.setArchive(false);
            throw new CourtException("Nie udalo sie wyrejestrowac podanego boiska.");
        }
    }

    public Court getCourt(UUID courtID) {
        try {
            CourtMapper courtMapper = courtRepository.readByUUID(courtID);
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
