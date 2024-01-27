package pas.gV.restapi.data.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.base.Objects;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@FieldDefaults(makeFinal = true)
@JsonPropertyOrder({"beginTime", "client", "court", "endTime", "id", "reservationCost"})
public class ReservationDTO implements DTO {

    public interface BasicReservationValidation {}

    @JsonProperty("id")
    private String id;
    @JsonProperty("client")
    @NotNull
    private ClientDTO client;
    @JsonProperty("court")
    @NotNull
    private CourtDTO court;
    @JsonProperty("beginTime")
    private LocalDateTime beginTime;
    @JsonProperty("endTime")
    private LocalDateTime endTime;
    @JsonProperty("reservationCost")
    private double reservationCost;

    @JsonCreator
    public ReservationDTO(@JsonProperty("id") String id,
                          @JsonProperty("client") ClientDTO client,
                          @JsonProperty("court") CourtDTO court,
                          @JsonProperty("beginTime") LocalDateTime beginTime,
                          @JsonProperty("endTime") LocalDateTime endTime,
                          @JsonProperty("reservationCost") double reservationCost) {
        this.id = id;
        this.client = client;
        this.court = court;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.reservationCost = reservationCost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationDTO that = (ReservationDTO) o;
        return Objects.equal(id, that.id) &&
                Double.compare(reservationCost, that.reservationCost) == 0  &&
                Objects.equal(client, that.client) &&
                Objects.equal(court, that.court);
    }
}
