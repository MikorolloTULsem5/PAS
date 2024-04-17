package pas.gV.model.data.datahandling.entities;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@FieldDefaults(makeFinal = true)
public class ReservationEntity implements Entity {
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
    public ReservationEntity(@BsonProperty("_id") String id,
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationEntity that = (ReservationEntity) o;
        return Double.compare(reservationCost, that.reservationCost) == 0 &&
                Objects.equals(id, that.id) &&
                Objects.equals(clientId, that.clientId) &&
                Objects.equals(courtId, that.courtId) &&
                Objects.equals(beginTime, that.beginTime) &&
                Objects.equals(endTime, that.endTime);
    }
}
