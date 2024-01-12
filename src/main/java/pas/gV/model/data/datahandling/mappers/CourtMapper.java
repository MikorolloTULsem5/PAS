package pas.gV.model.data.datahandling.mappers;

import pas.gV.model.logic.courts.Court;
import pas.gV.model.data.datahandling.entities.CourtEntity;

import java.util.UUID;

public class CourtMapper {
    public static CourtEntity toMongoCourt(Court court) {
        return new CourtEntity(court.getId().toString(), court.getArea(), court.getBaseCost(),
                court.getCourtNumber(), court.isArchive(), court.isRented() ? 1 : 0);
    }

    public static Court fromMongoCourt(CourtEntity courtMapper) {
        Court courtModel = new Court(UUID.fromString(courtMapper.getId()), courtMapper.getArea(),
                courtMapper.getBaseCost(), courtMapper.getCourtNumber());
        courtModel.setArchive(courtMapper.isArchive());
        courtModel.setRented(courtMapper.isRented() > 0);
        return courtModel;
    }
}
