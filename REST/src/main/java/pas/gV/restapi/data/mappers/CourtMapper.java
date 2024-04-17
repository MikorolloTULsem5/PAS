package pas.gV.restapi.data.mappers;

import pas.gV.model.logic.courts.Court;
import pas.gV.restapi.data.dto.CourtDTO;

import java.util.UUID;

public class CourtMapper {
    public static CourtDTO toJsonCourt(Court court) {
        return new CourtDTO(court.getId().toString(),
                court.getArea(),
                court.getBaseCost(),
                court.getCourtNumber(),
                court.isArchive(),
                court.isRented());
    }

    public static Court fromJsonCourt(CourtDTO courtMapper) {
        Court courtModel = new Court(courtMapper.getId() != null ? UUID.fromString(courtMapper.getId()) : null,
                courtMapper.getArea(),
                courtMapper.getBaseCost(),
                courtMapper.getCourtNumber());
        courtModel.setArchive(courtMapper.isArchive());
        courtModel.setRented(courtMapper.isRented());
        return courtModel;
    }
}
