package pas.gV.restapi.data.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;

import lombok.Getter;
import lombok.experimental.FieldDefaults;

import pas.gV.model.data.datahandling.entities.Entity;

import java.util.Objects;

@Getter
@FieldDefaults(makeFinal = true)
@JsonPropertyOrder({"archive", "area", "baseCost", "courtNumber", "id", "rented"})
public class CourtDTO implements Entity {
    @JsonProperty("id")
    private String id;
    @JsonProperty("area")
    @PositiveOrZero
    private double area;
    @JsonProperty("baseCost")
    @PositiveOrZero
    private int baseCost;
    @JsonProperty("courtNumber")
    @Min(value = 1)
    private int courtNumber;
    @JsonProperty("archive")
    private boolean archive;
    @JsonProperty("rented")
    private boolean rented;

    @JsonCreator
    public CourtDTO(@JsonProperty("id") String id,
                    @JsonProperty("area") double area,
                    @JsonProperty("baseCost") int baseCost,
                    @JsonProperty("courtNumber") int courtNumber,
                    @JsonProperty("archive") boolean archive,
                    @JsonProperty("rented") boolean rented) {
        this.id = id;
        this.area = area;
        this.baseCost = baseCost;
        this.courtNumber = courtNumber;
        this.archive = archive;
        this.rented = rented;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourtDTO that = (CourtDTO) o;
        return Double.compare(area, that.area) == 0 &&
                baseCost == that.baseCost &&
                courtNumber == that.courtNumber &&
                archive == that.archive &&
                rented == that.rented &&
                Objects.equals(id, that.id);
    }
}
