package pas.gV.mvc.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;

import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder({"archive", "area", "baseCost", "courtNumber", "id", "rented"})
public class Court {
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
    public Court(@JsonProperty("id") String id,
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
}
