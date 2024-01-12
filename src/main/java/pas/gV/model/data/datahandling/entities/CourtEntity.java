package pas.gV.model.data.datahandling.entities;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Objects;

@Getter
@FieldDefaults(makeFinal = true)
public class CourtEntity implements Entity {
    @BsonProperty("_id")
    private String id;
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
    public CourtEntity(@BsonProperty("_id") String id,
                       @BsonProperty("area") double area,
                       @BsonProperty("basecost") int baseCost,
                       @BsonProperty("courtnumber") int courtNumber,
                       @BsonProperty("archive") boolean archive,
                       @BsonProperty("rented") int rented) {
        this.id = id;
        this.area = area;
        this.baseCost = baseCost;
        this.courtNumber = courtNumber;
        this.archive = archive;
        this.rented = rented;
    }

    public int isRented() {
        return rented;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourtEntity that = (CourtEntity) o;
        return Double.compare(area, that.area) == 0 &&
                baseCost == that.baseCost &&
                courtNumber == that.courtNumber &&
                archive == that.archive &&
                rented == that.rented &&
                Objects.equals(id, that.id);
    }
}
