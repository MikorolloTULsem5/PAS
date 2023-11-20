package nbd.gV.mappers;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import nbd.gV.courts.Court;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Objects;
import java.util.UUID;

@Getter
@FieldDefaults(makeFinal = true)
public class CourtMapper {
    @BsonProperty("_id")
    private String courtId;
    @BsonProperty("area")
    private double area;
    @BsonProperty("basecost")
    private int baseCost;
    @BsonProperty("courtnumber")
    private int courtNumber;
    @BsonProperty("archive")
    private boolean archive;
    @BsonProperty("rented")
    private int rented;

    @BsonCreator
    public CourtMapper(@BsonProperty("_id") String courtId,
                       @BsonProperty("area") double area,
                       @BsonProperty("basecost") int baseCost,
                       @BsonProperty("courtnumber") int courtNumber,
                       @BsonProperty("archive") boolean archive,
                       @BsonProperty("rented") int rented) {
        this.courtId = courtId;
        this.area = area;
        this.baseCost = baseCost;
        this.courtNumber = courtNumber;
        this.archive = archive;
        this.rented = rented;
    }

    public int isRented() {
        return rented;
    }

    public static CourtMapper toMongoCourt(Court court) {
        return new CourtMapper(court.getCourtId().toString(), court.getArea(), court.getBaseCost(),
                court.getCourtNumber(), court.isArchive(), court.isRented() ? 1 : 0);
    }

    public static Court fromMongoCourt(CourtMapper courtMapper) {
        Court courtModel = new Court(UUID.fromString(courtMapper.getCourtId()), courtMapper.getArea(),
                courtMapper.getBaseCost(), courtMapper.getCourtNumber());
        courtModel.setArchive(courtMapper.isArchive());
        courtModel.setRented(courtMapper.isRented() > 0);
        return courtModel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourtMapper that = (CourtMapper) o;
        return Double.compare(area, that.area) == 0 &&
                baseCost == that.baseCost &&
                courtNumber == that.courtNumber &&
                archive == that.archive &&
                rented == that.rented &&
                Objects.equals(courtId, that.courtId);
    }
}
