package pas.gV.model.logic.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor
public class Client extends User {

    private enum ClientType {
        NORMAL(0, 3), ATHLETE(0.1, 6), COACH(0.2, 12);

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

    public Client(UUID id, String firstName, String lastName, String login, String password, String clientType) {
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
        this.role = Role.CLIENT;
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
