package nbd.gV.data.datahandling.mappers;

import nbd.gV.data.datahandling.dto.ClientDTO;
import nbd.gV.data.datahandling.dto.CourtDTO;
import nbd.gV.data.datahandling.dto.ReservationDTO;
import nbd.gV.model.reservations.Reservation;

import java.util.UUID;

public class ReservationMapper {
    public static ReservationDTO toMongoReservation(Reservation reservation) {
        return new ReservationDTO(reservation.getId().toString(), reservation.getClient().getId().toString(),
                reservation.getCourt().getId().toString(), reservation.getBeginTime(), reservation.getEndTime(),
                reservation.getReservationCost());
    }

    public static Reservation fromMongoReservation(ReservationDTO reservationMapper, ClientDTO clientMapper,
                                                   CourtDTO courtMapper) {
        Reservation reservation = new Reservation(UUID.fromString(reservationMapper.getId()),
                ClientMapper.fromMongoUser(clientMapper), CourtMapper.fromMongoCourt(courtMapper),
                reservationMapper.getBeginTime());
        if (reservationMapper.getEndTime() != null) {
            reservation.endReservation(reservationMapper.getEndTime());
        }
        return reservation;
    }
}
