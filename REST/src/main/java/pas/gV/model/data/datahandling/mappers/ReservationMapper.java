package pas.gV.model.data.datahandling.mappers;

import pas.gV.model.data.datahandling.entities.ClientEntity;
import pas.gV.model.data.datahandling.entities.CourtEntity;
import pas.gV.model.data.datahandling.entities.ReservationEntity;
import pas.gV.model.logic.reservations.Reservation;

import java.util.UUID;

public class ReservationMapper {
    public static ReservationEntity toMongoReservation(Reservation reservation) {
        return new ReservationEntity(reservation.getId().toString(), reservation.getClient().getId().toString(),
                reservation.getCourt().getId().toString(), reservation.getBeginTime(), reservation.getEndTime(),
                reservation.getReservationCost());
    }

    public static Reservation fromMongoReservation(ReservationEntity reservationMapper, ClientEntity clientMapper,
                                                   CourtEntity courtMapper) {
        Reservation reservation = new Reservation(UUID.fromString(reservationMapper.getId()),
                ClientMapper.fromMongoUser(clientMapper), CourtMapper.fromMongoCourt(courtMapper),
                reservationMapper.getBeginTime());
        if (reservationMapper.getEndTime() != null) {
            reservation.endReservation(reservationMapper.getEndTime());
        }
        return reservation;
    }
}
