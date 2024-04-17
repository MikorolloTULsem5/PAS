package pas.gV.restapi.data.mappers;

import pas.gV.model.logic.reservations.Reservation;
import pas.gV.restapi.data.dto.ReservationDTO;

import java.util.UUID;

public class ReservationMapper {

    public static ReservationDTO toJsonReservation(Reservation reservation) {
        return new ReservationDTO(reservation.getId().toString(),
                ClientMapper.toJsonUser(reservation.getClient()),
                CourtMapper.toJsonCourt(reservation.getCourt()),
                reservation.getBeginTime(),
                reservation.getEndTime(),
                reservation.getReservationCost()
        );
    }

    public static Reservation fromJsonUser(ReservationDTO reservationDTO) {
        Reservation newReservation = new Reservation(reservationDTO.getId() != null ? UUID.fromString(reservationDTO.getId()) : null,
                ClientMapper.fromJsonUser(reservationDTO.getClient()),
                CourtMapper.fromJsonCourt(reservationDTO.getCourt()),
                reservationDTO.getBeginTime()
        );
        if (reservationDTO.getEndTime() != null) {
            newReservation.endReservation(reservationDTO.getEndTime());
        }
        return newReservation;
    }
}
