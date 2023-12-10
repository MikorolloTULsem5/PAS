package pas.gV.data.datahandling.mappers;

import pas.gV.model.courts.Court;
import pas.gV.data.datahandling.dto.CourtDTO;

import java.util.UUID;

public class CourtMapper {
    public static CourtDTO toMongoCourt(Court court) {
        return new CourtDTO(court.getId().toString(), court.getArea(), court.getBaseCost(),
                court.getCourtNumber(), court.isArchive(), court.isRented() ? 1 : 0);
    }

    public static Court fromMongoCourt(CourtDTO courtMapper) {
        Court courtModel = new Court(UUID.fromString(courtMapper.getId()), courtMapper.getArea(),
                courtMapper.getBaseCost(), courtMapper.getCourtNumber());
        courtModel.setArchive(courtMapper.isArchive());
        courtModel.setRented(courtMapper.isRented() > 0);
        return courtModel;
    }
}
