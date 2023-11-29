package nbd.gV.model.courts;

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
    private UUID courtId;

    private double area;
    private int baseCost;
    ///TODO setter or not setter
    private int courtNumber;

    private boolean archive = false;
    private boolean rented = false;

    ///TODO raczej wywalic
    public Court(double area, int baseCost, int courtNumber) {
        if (area <= 0.0 || baseCost < 0 || courtNumber < 1) {
            throw new ConstructorParameterException("Niepoprawny parametr przy tworzeniu obiektu boiska!");
        }
        this.courtId = UUID.randomUUID();
        this.area = area;
        this.baseCost = baseCost;
        this.courtNumber = courtNumber;
    }

    public Court(UUID courtId, double area, int baseCost, int courtNumber) {
        this(area, baseCost, courtNumber);
        this.courtId = courtId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Court court = (Court) o;
        ///TODO czy to spelnienie 1 wymagania z zadania (o byciu podstawa rownosci obiektow)???
        return Objects.equals(courtId, court.courtId);
//        return Double.compare(area, court.area) == 0 &&
//                baseCost == court.baseCost &&
//                courtNumber == court.courtNumber &&
//                archive == court.archive &&
//                rented == court.rented &&
//
    }
}
