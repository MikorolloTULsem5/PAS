package nbd.gV.model.courts;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nbd.gV.exceptions.ConstructorParameterException;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
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

    ///TODO raczej wywalic
    public Court(double area, int baseCost, int courtNumber) {
        if (area <= 0.0 || baseCost < 0 || courtNumber < 1) {
            throw new ConstructorParameterException("Niepoprawny parametr przy tworzeniu obiektu boiska!");
        }
        this.id = UUID.randomUUID();
        this.area = area;
        this.baseCost = baseCost;
        this.courtNumber = courtNumber;
    }

    public Court(UUID id, double area, int baseCost, int courtNumber) {
        this(area, baseCost, courtNumber);
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Court court = (Court) o;
        return Objects.equals(id, court.id);
    }
}
