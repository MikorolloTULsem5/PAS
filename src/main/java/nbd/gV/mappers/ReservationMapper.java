package nbd.gV.mappers;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import nbd.gV.reservations.Reservation;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@FieldDefaults(makeFinal = true)
public class ReservationMapper {
    @BsonProperty("_id")
    private String id;
    @BsonProperty("clientid")
    private String clientId;
    @BsonProperty("courtid")
    private String courtId;
    @BsonProperty("begintime")
    private LocalDateTime beginTime;
    @BsonProperty("endtime")
    private LocalDateTime endTime;
    @BsonProperty("reservationcost")
    private double reservationCost;

    @BsonCreator
    public ReservationMapper(@BsonProperty("_id") String id,
                             @BsonProperty("clientid") String clientId,
                             @BsonProperty("courtid") String courtId,
                             @BsonProperty("begintime") LocalDateTime beginTime,
                             @BsonProperty("endtime") LocalDateTime endTime,
                             @BsonProperty("reservationcost") double reservationCost) {
        this.id = id;
        this.clientId = clientId;
        this.courtId = courtId;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.reservationCost = reservationCost;
    }

    public static ReservationMapper toMongoReservation(Reservation reservation) {
        return new ReservationMapper(reservation.getId().toString(), reservation.getClient().getClientId().toString(),
                reservation.getCourt().getCourtId().toString(), reservation.getBeginTime(), reservation.getEndTime(),
                reservation.getReservationCost());
    }

    public static Reservation fromMongoReservation(ReservationMapper reservationMapper, ClientMapper clientMapper,
                                                   CourtMapper courtMapper) {
        Reservation reservation = new Reservation(UUID.fromString(reservationMapper.getId()),
                ClientMapper.fromMongoClient(clientMapper), CourtMapper.fromMongoCourt(courtMapper),
                reservationMapper.getBeginTime());
        if (reservationMapper.getEndTime() != null) {
            reservation.endReservation(reservationMapper.getEndTime());
        }
        return reservation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationMapper that = (ReservationMapper) o;
        return Double.compare(reservationCost, that.reservationCost) == 0 &&
                Objects.equals(id, that.id) &&
                Objects.equals(clientId, that.clientId) &&
                Objects.equals(courtId, that.courtId) &&
                Objects.equals(beginTime, that.beginTime) &&
                Objects.equals(endTime, that.endTime);
    }
}
