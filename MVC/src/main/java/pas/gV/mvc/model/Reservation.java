package pas.gV.mvc.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder({"beginTime", "client", "court", "endTime", "id", "reservationCost", "reservationHours"})
public class Reservation {
    @JsonProperty("id")
    private String id;
    @JsonProperty("client")
    private Client client;
    @JsonProperty("court")
    private Court court;
    @JsonProperty("beginTime")
    private LocalDateTime beginTime;
    @JsonProperty("endTime")
    private LocalDateTime endTime;
    @JsonProperty("reservationCost")
    private double reservationCost;
    @JsonProperty("reservationHours")
    private double reservationHours;

    @JsonCreator
    public Reservation(@JsonProperty("id") String id,
                       @JsonProperty("client") Client client,
                       @JsonProperty("court") Court court,
                       @JsonProperty("beginTime") LocalDateTime beginTime,
                       @JsonProperty("endTime") LocalDateTime endTime,
                       @JsonProperty("reservationCost") double reservationCost,
                       @JsonProperty("reservationHours") double reservationHours) {
        this.id = id;
        this.client = client;
        this.court = court;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.reservationCost = reservationCost;
        this.reservationHours = reservationHours;
    }
}
