package pas.gV.model.logic.courts;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder({"archive", "area", "baseCost", "courtNumber", "id", "rented"})
public class Court {
    @Setter(AccessLevel.NONE)
    private UUID id;

    @PositiveOrZero
    private double area;
    @PositiveOrZero
    private int baseCost;
    @Min(value = 1)
    private int courtNumber;

    private boolean archive = false;
    private boolean rented = false;

    public Court(UUID id, double area, int baseCost, int courtNumber) {
        this.id = id;
        this.area = area;
        this.baseCost = baseCost;
        this.courtNumber = courtNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Court court = (Court) o;
        return Objects.equals(id, court.id);
    }
}
