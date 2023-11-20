package nbd.gV.data.mappers;

import nbd.gV.data.dto.ClientDTO;
import nbd.gV.data.dto.CourtDTO;
import nbd.gV.data.dto.ReservationDTO;
import nbd.gV.reservations.Reservation;

import java.util.UUID;

public class ReservationMapper {
    public static ReservationDTO toMongoReservation(Reservation reservation) {
        return new ReservationDTO(reservation.getId().toString(), reservation.getClient().getClientId().toString(),
                reservation.getCourt().getCourtId().toString(), reservation.getBeginTime(), reservation.getEndTime(),
                reservation.getReservationCost());
    }

    public static Reservation fromMongoReservation(ReservationDTO reservationMapper, ClientDTO clientMapper,
                                                   CourtDTO courtMapper) {
        Reservation reservation = new Reservation(UUID.fromString(reservationMapper.getId()),
                ClientMapper.fromMongoClient(clientMapper), CourtMapper.fromMongoCourt(courtMapper),
                reservationMapper.getBeginTime());
        if (reservationMapper.getEndTime() != null) {
            reservation.endReservation(reservationMapper.getEndTime());
        }
        return reservation;
    }
}
