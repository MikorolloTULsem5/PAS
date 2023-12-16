package pas.gV.model.users;

import org.codehaus.jackson.annotate.JsonPropertyOrder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor
@JsonPropertyOrder({"archive", "id", "login", "clientTypeName", "firstName", "lastName"})
public class Client extends User {

    private enum ClientType {
        NORMAL(0, 3), ATHLETE(10, 6), COACH(20, 12);

        private final double discount;
        @Getter
        private final int maxHours;
        ClientType(double discount, int maxHours) {
            this.discount = discount;
            this.maxHours = maxHours;
        }

        public double applyDiscount() {
            return discount;
        }

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    @Getter
    @Setter
    @NotBlank
    private String firstName;
    @Getter
    @Setter
    @NotBlank
    private String lastName;
    private ClientType clientType;

    @Getter
    private String clientTypeName;

    public Client(UUID id, String firstName, String lastName, String clientType, String login, String password) {
        super(id, login, password);

        this.firstName = firstName;
        this.lastName = lastName;
        if (clientType != null) {
            this.clientType = switch (clientType.toLowerCase()) {
                case "athlete" -> ClientType.ATHLETE;
                case "coach" -> ClientType.COACH;
                default -> ClientType.NORMAL;
            };
        } else {
            this.clientType = ClientType.NORMAL;
        }
        this.clientTypeName = this.clientType.toString();
    }

    public double applyDiscount() {
        return clientType.applyDiscount();
    }

    public int clientMaxHours() {
        return clientType.getMaxHours();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(getId(), client.getId());
    }

    public void setClientTypeName(@NotNull String clientType) {
        if (clientType != null) {
            this.clientType = switch (clientType.toLowerCase()) {
                case "athlete" -> ClientType.ATHLETE;
                case "coach" -> ClientType.COACH;
                default -> ClientType.NORMAL;
            };
        } else {
            this.clientType = ClientType.NORMAL;
        }
        this.clientTypeName = this.clientType.toString();
    }
}
