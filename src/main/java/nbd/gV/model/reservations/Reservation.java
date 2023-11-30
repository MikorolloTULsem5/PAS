package nbd.gV.model.reservations;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import nbd.gV.model.users.Client;
import nbd.gV.model.courts.Court;
import nbd.gV.exceptions.MainException;
import nbd.gV.exceptions.ReservationException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Reservation {
    private UUID id;

    @NotNull
    private final Client client;
    @NotNull
    private final Court court;
    private final LocalDateTime beginTime;

    private LocalDateTime endTime = null;
    private double reservationCost;

    public Reservation(Client client, Court court, LocalDateTime beginTime) {
        if (client == null || court == null) {
            throw new MainException("Niepoprawny parametr przy tworzeniu obiektu rezerwacji!");
        }

        this.id = UUID.randomUUID();
        this.client = client;
        this.court = court;
        this.beginTime = (beginTime == null) ? LocalDateTime.now() : beginTime;
    }

    public Reservation(UUID id, Client client, Court court, LocalDateTime beginTime) {
        this(client, court, beginTime);
        if (id == null) {
            throw new MainException("UUID nie moze byc null'em!");
        }
        this.id = id;
    }

    public int getReservationHours() {
        int hours = 0;

        if (endTime != null) {
            long duration = Duration.between(beginTime, endTime).getSeconds();
            int hoursDur = (int) (duration / 3600);
            int minutesDur = (int) ((duration / 60) % 60);

            if (!(hoursDur == 0 && minutesDur == 0)) {
                hours = (minutesDur == 0) ? hoursDur : (hoursDur + 1);
            }
        }

        return hours;
    }

    public void endReservation(LocalDateTime endingDate) {
        if (endTime == null) {
            endTime = (endingDate == null) ? LocalDateTime.now() : endingDate;
            if (Duration.between(beginTime, endTime).isNegative()) {
                endTime = beginTime;
            }

            court.setRented(false);

            if (getReservationHours() <= client.clientMaxHours()) {
                reservationCost = getReservationHours() * court.getBaseCost() -
                        client.applyDiscount();
            } else {
                reservationCost = court.getBaseCost() *
                        (client.clientMaxHours() + (getReservationHours() - client.clientMaxHours()) * 1.5) -
                        client.applyDiscount();
            }
        } else {
            throw new ReservationException("Ta rezerwacja juz sie zakonczyla i nie mozna zmienic jej daty!");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        ///TODO czy to spelnienie 1 wymagania z zadania (o byciu podstawa rownosci obiektow)???
        return Objects.equals(id, that.id);
//        return Double.compare(reservationCost, that.reservationCost) == 0 &&
//                Objects.equals(id, that.id) &&
//                Objects.equals(client.getId().toString(), that.client.getId().toString()) &&
//                Objects.equals(court.getCourtId().toString(), that.court.getCourtId().toString()) &&
//                Duration.between(beginTime, that.beginTime).getSeconds() < 5 &&
//                Objects.equals(endTime, that.endTime);
    }
}
